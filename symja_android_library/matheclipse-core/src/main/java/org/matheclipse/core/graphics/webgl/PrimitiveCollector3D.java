package org.matheclipse.core.graphics.webgl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Walks a {@code Graphics3D} primitive tree and writes one JSON element per primitive.
 *
 * <p>
 * The walk carries three things that a primitive needs but does not itself contain: the directives
 * in force ({@link Style3D}), the vertex pool of an enclosing {@code GraphicsComplex}, and the
 * transformation an enclosing {@code Rotate}/{@code Translate}/{@code Scale} imposes. Directives
 * are scoped by cloning the style on entry to a list, so a colour set inside a sublist does not
 * leak out of it.
 *
 * <p>
 * Coordinates are written in data space. The renderer is responsible for framing, so anything that
 * depends on the size of the scene ({@code PointSize}, dash lengths, arrowheads) is written as a
 * fraction and resolved there.
 */
public final class PrimitiveCollector3D {

  /** The vertex pool of an enclosing {@code GraphicsComplex}. */
  static final class ComplexContext {
    final IAST points;
    final IAST vertexColors;
    final IAST vertexNormals;

    ComplexContext(IAST points, IAST vertexColors, IAST vertexNormals) {
      this.points = points;
      this.vertexColors = vertexColors;
      this.vertexNormals = vertexNormals;
    }

    /** The coordinate an index refers to, or {@code null} when it is out of range. */
    double[] point(int index) {
      if (points == null || index < 1 || index >= points.size()) {
        return null;
      }
      return GraphicsOptions3D.vector(points.get(index));
    }
  }

  private final ArrayNode elements;
  private final String[] scaling;
  /** Total number of vertices written, used to keep a pathological expression from exploding. */
  private int vertexBudget = 4_000_000;

  /** The extent of the data, which the renderer frames the scene and the box against. */
  public final Bounds3D bounds = new Bounds3D();

  public PrimitiveCollector3D(ArrayNode elements, String[] scaling) {
    this.elements = elements;
    this.scaling = scaling;
  }

  public void collect(IExpr expr, Style3D style) {
    process(expr, style, null, Transform3D.IDENTITY);
  }

  /** Record a point in the data extent, after the transformation in force has been applied. */
  private void track(double[] point, Transform3D transform) {
    bounds.add(transform.isIdentity() ? point : transform.apply(point));
  }

  private void trackBall(double[] center, double radius, Transform3D transform) {
    double[] c = transform.isIdentity() ? center : transform.apply(center);
    bounds.addBall(c, radius * transform.maxScale());
  }

  // ---------------------------------------------------------------- tree walk

  private void process(IExpr expr, Style3D style, ComplexContext context, Transform3D transform) {
    if (expr.isBuiltInSymbol()) {
      applySymbolDirective((IBuiltInSymbol) expr, style);
      return;
    }
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    IExpr headExpr = ast.head();
    if (!headExpr.isBuiltInSymbol()) {
      return;
    }
    int id = ((IBuiltInSymbol) headExpr).ordinal();

    switch (id) {
      case ID.List:
      case ID.GraphicsGroup: {
        // a sublist scopes its directives
        Style3D scoped = style.clone();
        for (int i = 1; i <= ast.argSize(); i++) {
          process(ast.get(i), scoped, context, transform);
        }
        return;
      }
      case ID.Style: {
        Style3D scoped = style.clone();
        for (int i = 2; i <= ast.argSize(); i++) {
          applyDirective(ast.get(i), scoped);
        }
        if (ast.argSize() >= 1) {
          process(ast.arg1(), scoped, context, transform);
        }
        return;
      }
      case ID.Directive: {
        for (int i = 1; i <= ast.argSize(); i++) {
          applyDirective(ast.get(i), style);
        }
        return;
      }
      case ID.Tooltip:
      case ID.Annotation:
      case ID.Labeled:
      case ID.StatusArea:
      case ID.Mouseover: {
        // wrappers that carry no 3D appearance of their own
        if (ast.argSize() >= 1) {
          process(ast.arg1(), style, context, transform);
        }
        return;
      }
      case ID.GraphicsComplex: {
        if (ast.argSize() >= 2) {
          IAST pts = ast.arg1().isList() ? (IAST) ast.arg1() : null;
          IAST colors = optionList(ast, S.VertexColors);
          IAST normals = optionList(ast, S.VertexNormals);
          process(ast.arg2(), style.clone(), new ComplexContext(pts, colors, normals), transform);
        }
        return;
      }
      case ID.Translate: {
        double[] v = ast.argSize() >= 2 ? GraphicsOptions3D.vector(ast.arg2()) : null;
        Transform3D t = v == null ? transform : transform.times(Transform3D.translation(v));
        process(ast.arg1(), style.clone(), context, t);
        return;
      }
      case ID.Rotate: {
        process(ast.arg1(), style.clone(), context, rotateTransform(ast, transform));
        return;
      }
      case ID.Scale: {
        process(ast.arg1(), style.clone(), context, scaleTransform(ast, transform));
        return;
      }
      case ID.GeometricTransformation: {
        Transform3D t = transform;
        if (ast.argSize() >= 2) {
          Transform3D parsed = Transform3D.fromExpr(ast.arg2());
          if (parsed != null) {
            t = transform.times(parsed);
          }
        }
        process(ast.arg1(), style.clone(), context, t);
        return;
      }
      default:
        break;
    }

    if (applyDirective(ast, style)) {
      return;
    }
    emitPrimitive(id, ast, style, context, transform);
  }

  // -------------------------------------------------------------- directives

  /** Apply {@code expr} as a directive; return true when it was one. */
  private boolean applyDirective(IExpr expr, Style3D style) {
    if (expr.isBuiltInSymbol()) {
      return applySymbolDirective((IBuiltInSymbol) expr, style);
    }
    if (!expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    if (!ast.head().isBuiltInSymbol()) {
      return false;
    }
    int id = ((IBuiltInSymbol) ast.head()).ordinal();
    switch (id) {
      case ID.RGBColor:
      case ID.Hue:
      case ID.GrayLevel:
      case ID.CMYKColor:
      case ID.Lighter:
      case ID.Darker:
      case ID.Blend: {
        Color c = ColorUtil.parse(ast);
        if (c != null) {
          style.color = c;
          style.colorSet = true;
          style.faceColor = null;
        }
        return true;
      }
      case ID.ColorDataFunction: {
        Color c = ColorUtil.parse(ast);
        if (c != null) {
          style.color = c;
          style.colorSet = true;
        }
        return true;
      }
      case ID.Opacity: {
        if (ast.argSize() >= 2) {
          // Opacity[o, colour] denotes a colour rather than a directive
          Color c = ColorUtil.parse(ast);
          if (c != null) {
            style.color = c;
            style.colorSet = true;
            return true;
          }
        }
        style.opacity = clamp01(ColorUtil.dbl(ast.arg1(), 1.0));
        return true;
      }
      case ID.Thickness:
        style.thickness = Style3D.Size.ofScaled(ColorUtil.dbl(ast.arg1(), 0.002));
        return true;
      case ID.AbsoluteThickness:
        style.thickness = Style3D.Size.ofAbsolute(ColorUtil.dbl(ast.arg1(), 1.0));
        return true;
      case ID.PointSize:
        style.pointSize = Style3D.Size.ofScaled(ColorUtil.dbl(ast.arg1(), 0.01));
        return true;
      case ID.AbsolutePointSize:
        style.pointSize = Style3D.Size.ofAbsolute(ColorUtil.dbl(ast.arg1(), 3.0));
        return true;
      case ID.Dashing:
        style.dashing = dashPattern(ast.arg1(), 0.02);
        style.dashingScaled = true;
        return true;
      case ID.AbsoluteDashing:
        style.dashing = dashPattern(ast.arg1(), 3.0);
        style.dashingScaled = false;
        return true;
      case ID.EdgeForm:
        applyEdgeForm(ast, style);
        return true;
      case ID.FaceForm:
        if (ast.argSize() >= 1) {
          if (ast.arg1().isNone()) {
            style.faceColor = ColorUtil.TRANSPARENT;
          } else {
            Color c = GraphicsOptions3D.firstColor(ast.arg1());
            if (c != null) {
              style.faceColor = c;
            }
          }
        }
        return true;
      case ID.Specularity: {
        IExpr arg = ast.argSize() >= 1 ? ast.arg1() : S.Automatic;
        Color c = ColorUtil.parse(arg);
        if (c != null) {
          // a colour specularity contributes its brightness as the highlight strength
          style.specularity = (c.getRed() + c.getGreen() + c.getBlue()) / (3.0 * 255.0);
        } else {
          style.specularity = clamp01(ColorUtil.dbl(arg, 0.5));
        }
        if (ast.argSize() >= 2) {
          style.specularExponent = Math.max(1.0, ColorUtil.dbl(ast.arg2(), 30.0));
        }
        return true;
      }
      case ID.Glow: {
        if (ast.argSize() == 0 || ast.arg1().isNone()) {
          style.glow = null;
        } else {
          Color c = ColorUtil.parse(ast.arg1());
          style.glow = c;
        }
        return true;
      }
      case ID.Arrowheads: {
        double size = arrowheadSize(ast.arg1());
        if (!Double.isNaN(size)) {
          style.arrowheadSize = size;
        }
        return true;
      }
      default:
        return false;
    }
  }

  private boolean applySymbolDirective(IBuiltInSymbol symbol, Style3D style) {
    Color named = ColorUtil.named(symbol);
    if (named != null) {
      style.color = named;
      style.colorSet = true;
      style.faceColor = null;
      return true;
    }
    switch (symbol.ordinal()) {
      case ID.Thin:
        style.thickness = Style3D.Size.ofAbsolute(1.0);
        return true;
      case ID.Thick:
        style.thickness = Style3D.Size.ofAbsolute(2.0);
        return true;
      case ID.Dashed:
        style.dashing = new double[] {0.02, 0.02};
        style.dashingScaled = true;
        return true;
      case ID.Dotted:
        style.dashing = new double[] {0.002, 0.012};
        style.dashingScaled = true;
        return true;
      case ID.DotDashed:
        style.dashing = new double[] {0.002, 0.012, 0.02, 0.012};
        style.dashingScaled = true;
        return true;
      case ID.Tiny:
        style.pointSize = Style3D.Size.ofScaled(0.005);
        return true;
      case ID.Small:
        style.pointSize = Style3D.Size.ofScaled(0.008);
        return true;
      case ID.Medium:
        style.pointSize = Style3D.Size.ofScaled(0.012);
        return true;
      case ID.Large:
        style.pointSize = Style3D.Size.ofScaled(0.018);
        return true;
      default:
        return false;
    }
  }

  private static void applyEdgeForm(IAST ast, Style3D style) {
    if (ast.argSize() == 0) {
      style.showEdges = true;
      return;
    }
    IExpr arg = ast.arg1();
    if (arg.isNone() || arg.isFalse() || (arg.isList() && ((IAST) arg).argSize() == 0)) {
      style.showEdges = false;
      return;
    }
    style.showEdges = true;
    Color c = GraphicsOptions3D.firstColor(arg);
    if (c != null) {
      style.edgeColor = c;
      style.edgeOpacity = c.getAlpha() / 255.0;
    }
    double t = GraphicsOptions3D.firstThickness(arg);
    if (!Double.isNaN(t)) {
      style.edgeThickness = t;
    }
  }

  private static double[] dashPattern(IExpr expr, double fallback) {
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (list.argSize() == 0) {
        return null;
      }
      double[] pattern = new double[list.argSize()];
      for (int i = 0; i < pattern.length; i++) {
        pattern[i] = Math.max(0.0, ColorUtil.dbl(list.get(i + 1), fallback));
      }
      return pattern;
    }
    if (expr.isNone()) {
      return null;
    }
    double d = ColorUtil.dbl(expr, fallback);
    return new double[] {d, d};
  }

  private static double arrowheadSize(IExpr expr) {
    if (expr.isNone()) {
      return 0.0;
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (list.argSize() >= 1) {
        IExpr first = list.arg1();
        if (first.isList() && ((IAST) first).argSize() >= 1) {
          return Math.abs(ColorUtil.dbl(((IAST) first).arg1(), Double.NaN));
        }
        return Math.abs(ColorUtil.dbl(first, Double.NaN));
      }
      return Double.NaN;
    }
    return Math.abs(ColorUtil.dbl(expr, Double.NaN));
  }

  // -------------------------------------------------------------- primitives

  private void emitPrimitive(int id, IAST ast, Style3D style, ComplexContext context,
      Transform3D transform) {
    switch (id) {
      case ID.Polygon:
      case ID.Triangle:
        emitPolygon(ast, style, context, transform);
        break;
      case ID.Line:
        emitLine(ast, style, context, transform, false);
        break;
      case ID.Arrow:
        emitArrow(ast, style, context, transform);
        break;
      case ID.Point:
        emitPoint(ast, style, context, transform);
        break;
      case ID.Sphere:
      case ID.Ball:
        emitSphere(ast, style, context, transform);
        break;
      case ID.Cylinder:
        emitTwoPointSolid(ast, "Cylinder", style, context, transform, 1.0);
        break;
      case ID.Cone:
        emitTwoPointSolid(ast, "Cone", style, context, transform, 1.0);
        break;
      case ID.Cuboid:
        emitCuboid(ast, style, context, transform);
        break;
      case ID.Cube:
        emitPolyhedron(ast, "Cube", style, context, transform);
        break;
      case ID.Tetrahedron:
        emitPolyhedron(ast, "Tetrahedron", style, context, transform);
        break;
      case ID.Octahedron:
        emitPolyhedron(ast, "Octahedron", style, context, transform);
        break;
      case ID.Dodecahedron:
        emitPolyhedron(ast, "Dodecahedron", style, context, transform);
        break;
      case ID.Icosahedron:
        emitPolyhedron(ast, "Icosahedron", style, context, transform);
        break;
      case ID.Tube:
        emitTube(ast, style, context, transform);
        break;
      case ID.BSplineCurve:
        emitBSpline(ast, style, context, transform);
        break;
      case ID.BezierCurve:
        emitBezier(ast, style, context, transform);
        break;
      case ID.Text:
        emitText(ast, style, context, transform);
        break;
      default:
        break;
    }
  }

  /**
   * A polygon list, emitted as an indexed triangle mesh.
   *
   * <p>
   * When the polygons come from a {@code GraphicsComplex} the original vertex indices are kept, so
   * the {@code VertexNormals} and {@code VertexColors} that go with them stay aligned and shared
   * vertices stay shared. Raw coordinate polygons are welded on their coordinates instead, because
   * a surface written as independent quads would otherwise be shaded facet by facet.
   */
  private void emitPolygon(IAST ast, Style3D style, ComplexContext context, Transform3D transform) {
    if (ast.argSize() < 1) {
      return;
    }
    IAST vertexColors = optionList(ast, S.VertexColors);
    IAST vertexNormals = optionList(ast, S.VertexNormals);

    MeshBuilder mesh = new MeshBuilder(context, vertexColors, vertexNormals);
    IExpr data = ast.arg1();
    for (IExpr face : faces(data, context)) {
      mesh.addFace(face);
    }
    if (mesh.indices.isEmpty()) {
      return;
    }
    vertexBudget -= mesh.indices.size();
    if (vertexBudget < 0) {
      return;
    }

    ObjectNode node = newElement("Polygon", style, transform);
    node.put("color", rgb(style.effectiveFace()));
    node.put("opacity", style.alphaOf(style.effectiveFace()));
    ArrayNode points = node.putArray("points");
    for (double[] p : mesh.points) {
      points.add(p[0]).add(p[1]).add(p[2]);
      track(p, transform);
    }
    ArrayNode indices = node.putArray("indices");
    for (int index : mesh.indices) {
      indices.add(index);
    }
    if (mesh.hasColors()) {
      node.put("color", 0xFFFFFF);
      ArrayNode colors = node.putArray("vertexColors");
      for (Color c : mesh.colors) {
        Color use = c == null ? style.effectiveFace() : c;
        colors.add(use.getRed() / 255.0).add(use.getGreen() / 255.0).add(use.getBlue() / 255.0);
      }
    }
    if (mesh.hasNormals()) {
      ArrayNode normals = node.putArray("vertexNormals");
      for (double[] n : mesh.normals) {
        if (n == null) {
          normals.add(0.0).add(0.0).add(0.0);
        } else {
          normals.add(n[0]).add(n[1]).add(n[2]);
        }
      }
    }
    writeSurfaceStyle(node, style);
  }

  private void emitLine(IAST ast, Style3D style, ComplexContext context, Transform3D transform,
      boolean asArrow) {
    if (ast.argSize() < 1) {
      return;
    }
    List<List<double[]>> polylines = polylines(ast.arg1(), context);
    if (polylines.isEmpty()) {
      return;
    }
    ObjectNode node = newElement(asArrow ? "Arrow" : "Line", style, transform);
    node.put("color", rgb(style.effectiveLine()));
    node.put("opacity", style.alphaOf(style.effectiveLine()));
    writePolylines(node, polylines, transform);
    writeLineStyle(node, style);
    IAST vertexColors = optionList(ast, S.VertexColors);
    if (vertexColors != null) {
      ArrayNode colors = node.putArray("vertexColors");
      int written = 0;
      for (List<double[]> line : polylines) {
        for (int i = 0; i < line.size(); i++) {
          Color c =
              written + 1 < vertexColors.size() ? ColorUtil.parse(vertexColors.get(written + 1))
                  : null;
          Color use = c == null ? style.color : c;
          colors.add(use.getRed() / 255.0).add(use.getGreen() / 255.0).add(use.getBlue() / 255.0);
          written++;
        }
      }
      node.put("color", 0xFFFFFF);
    }
  }

  private void emitArrow(IAST ast, Style3D style, ComplexContext context, Transform3D transform) {
    if (ast.argSize() < 1) {
      return;
    }
    IExpr data = ast.arg1();
    // Arrow[Line[...]] and Arrow[Tube[...]] wrap the path they follow
    if (data.isAST(S.Line, 2) || data.isAST(S.Tube, 2) || data.isAST(S.Tube, 3)) {
      data = ((IAST) data).arg1();
    }
    List<List<double[]>> polylines = polylines(data, context);
    if (polylines.isEmpty()) {
      return;
    }
    ObjectNode node = newElement("Arrow", style, transform);
    node.put("color", rgb(style.effectiveLine()));
    node.put("opacity", style.alphaOf(style.effectiveLine()));
    writePolylines(node, polylines, transform);
    writeLineStyle(node, style);
    node.put("arrowheadSize", style.arrowheadSize);
    if (ast.argSize() >= 2) {
      double setback = ColorUtil.dbl(ast.arg2(), 0.0);
      if (Double.isFinite(setback) && setback != 0.0) {
        node.put("setback", setback);
      }
    }
  }

  private void emitPoint(IAST ast, Style3D style, ComplexContext context, Transform3D transform) {
    if (ast.argSize() < 1) {
      return;
    }
    List<double[]> points = new ArrayList<>();
    collectPoints(ast.arg1(), context, points);
    if (points.isEmpty()) {
      return;
    }
    ObjectNode node = newElement("Point", style, transform);
    node.put("color", rgb(style.effectiveLine()));
    node.put("opacity", style.alphaOf(style.effectiveLine()));
    ArrayNode array = node.putArray("points");
    for (double[] p : points) {
      array.add(p[0]).add(p[1]).add(p[2]);
      track(p, transform);
    }
    writeSize(node, "pointSize", style.pointSize);
    IAST vertexColors = optionList(ast, S.VertexColors);
    if (vertexColors != null) {
      ArrayNode colors = node.putArray("vertexColors");
      for (int i = 0; i < points.size(); i++) {
        Color c = i + 1 < vertexColors.size() ? ColorUtil.parse(vertexColors.get(i + 1)) : null;
        Color use = c == null ? style.color : c;
        colors.add(use.getRed() / 255.0).add(use.getGreen() / 255.0).add(use.getBlue() / 255.0);
      }
      node.put("color", 0xFFFFFF);
    }
  }

  private void emitSphere(IAST ast, Style3D style, ComplexContext context, Transform3D transform) {
    double radius = ast.argSize() >= 2 ? ColorUtil.dbl(ast.arg2(), 1.0) : 1.0;
    List<double[]> centers = new ArrayList<>();
    if (ast.argSize() == 0) {
      centers.add(new double[] {0, 0, 0});
    } else {
      collectPoints(ast.arg1(), context, centers);
    }
    if (centers.isEmpty()) {
      return;
    }
    ObjectNode node = newElement("Sphere", style, transform);
    node.put("color", rgb(style.effectiveFace()));
    node.put("opacity", style.alphaOf(style.effectiveFace()));
    node.put("radius", radius);
    ArrayNode array = node.putArray("centers");
    for (double[] c : centers) {
      array.add(c[0]).add(c[1]).add(c[2]);
      trackBall(c, radius, transform);
    }
    writeSurfaceStyle(node, style);
  }

  private void emitTwoPointSolid(IAST ast, String type, Style3D style, ComplexContext context,
      Transform3D transform, double defaultRadius) {
    double radius = ast.argSize() >= 2 ? ColorUtil.dbl(ast.arg2(), defaultRadius) : defaultRadius;
    List<double[]> axis = new ArrayList<>();
    if (ast.argSize() == 0) {
      axis.add(new double[] {0, 0, -1});
      axis.add(new double[] {0, 0, 1});
    } else {
      collectPoints(ast.arg1(), context, axis);
    }
    if (axis.size() < 2) {
      return;
    }
    // a list of more than two points describes a chain of segments
    for (int i = 0; i + 1 < axis.size(); i += 2) {
      ObjectNode node = newElement(type, style, transform);
      node.put("color", rgb(style.effectiveFace()));
      node.put("opacity", style.alphaOf(style.effectiveFace()));
      node.put("radius", radius);
      node.set("start", vector(axis.get(i)));
      node.set("end", vector(axis.get(i + 1)));
      trackBall(axis.get(i), radius, transform);
      trackBall(axis.get(i + 1), radius, transform);
      writeSurfaceStyle(node, style);
    }
  }

  private void emitCuboid(IAST ast, Style3D style, ComplexContext context, Transform3D transform) {
    double[] min;
    double[] max;
    if (ast.argSize() == 0) {
      min = new double[] {0, 0, 0};
      max = new double[] {1, 1, 1};
    } else {
      min = resolvePoint(ast.arg1(), context);
      if (min == null) {
        return;
      }
      if (ast.argSize() >= 2) {
        max = resolvePoint(ast.arg2(), context);
        if (max == null) {
          return;
        }
      } else {
        max = new double[] {min[0] + 1, min[1] + 1, min[2] + 1};
      }
    }
    ObjectNode node = newElement("Cuboid", style, transform);
    node.put("color", rgb(style.effectiveFace()));
    node.put("opacity", style.alphaOf(style.effectiveFace()));
    double[] lo = {Math.min(min[0], max[0]), Math.min(min[1], max[1]), Math.min(min[2], max[2])};
    double[] hi = {Math.max(min[0], max[0]), Math.max(min[1], max[1]), Math.max(min[2], max[2])};
    node.set("min", vector(lo));
    node.set("max", vector(hi));
    track(lo, transform);
    track(hi, transform);
    writeSurfaceStyle(node, style);
  }

  private void emitPolyhedron(IAST ast, String kind, Style3D style, ComplexContext context,
      Transform3D transform) {
    double[] center = {0, 0, 0};
    double scale = 1.0;
    if (ast.argSize() >= 1) {
      double[] c = resolvePoint(ast.arg1(), context);
      if (c != null) {
        center = c;
      } else {
        double s = ColorUtil.dbl(ast.arg1(), Double.NaN);
        if (!Double.isNaN(s)) {
          scale = s;
        }
      }
    }
    if (ast.argSize() >= 2) {
      double s = ColorUtil.dbl(ast.arg2(), Double.NaN);
      if (!Double.isNaN(s)) {
        scale = s;
      }
    }
    ObjectNode node = newElement("Polyhedron", style, transform);
    node.put("kind", kind);
    node.put("color", rgb(style.effectiveFace()));
    node.put("opacity", style.alphaOf(style.effectiveFace()));
    node.set("center", vector(center));
    node.put("scale", scale);
    trackBall(center, scale, transform);
    writeSurfaceStyle(node, style);
  }

  private void emitTube(IAST ast, Style3D style, ComplexContext context, Transform3D transform) {
    if (ast.argSize() < 1) {
      return;
    }
    double radius = ast.argSize() >= 2 ? ColorUtil.dbl(ast.arg2(), 0.02) : 0.02;
    IExpr geometry = ast.arg1();
    ObjectNode node = newElement("Tube", style, transform);
    node.put("color", rgb(style.effectiveFace()));
    node.put("opacity", style.alphaOf(style.effectiveFace()));
    node.put("radius", radius);
    writeSurfaceStyle(node, style);

    if (geometry.isAST(S.BSplineCurve)) {
      node.put("pathType", "BSpline");
      writeBSplineData(node, (IAST) geometry, context, transform, radius);
      return;
    }
    node.put("pathType", "CatmullRom");
    IExpr pointData = geometry.isAST(S.Line, 2) ? ((IAST) geometry).arg1() : geometry;
    List<List<double[]>> lines = polylines(pointData, context);
    if (lines.isEmpty()) {
      return;
    }
    writePolylines(node, lines, transform);
  }

  private void emitBSpline(IAST ast, Style3D style, ComplexContext context, Transform3D transform) {
    if (ast.argSize() < 1) {
      return;
    }
    ObjectNode node = newElement("BSplineCurve", style, transform);
    node.put("color", rgb(style.effectiveLine()));
    node.put("opacity", style.alphaOf(style.effectiveLine()));
    writeLineStyle(node, style);
    writeBSplineData(node, ast, context, transform, 0.0);
  }

  private void emitBezier(IAST ast, Style3D style, ComplexContext context, Transform3D transform) {
    if (ast.argSize() < 1) {
      return;
    }
    List<double[]> points = new ArrayList<>();
    collectPoints(ast.arg1(), context, points);
    if (points.size() < 2) {
      return;
    }
    ObjectNode node = newElement("BezierCurve", style, transform);
    node.put("color", rgb(style.effectiveLine()));
    node.put("opacity", style.alphaOf(style.effectiveLine()));
    writeLineStyle(node, style);
    ArrayNode array = node.putArray("points");
    for (double[] p : points) {
      array.add(p[0]).add(p[1]).add(p[2]);
      track(p, transform);
    }
  }

  private void emitText(IAST ast, Style3D style, ComplexContext context, Transform3D transform) {
    if (ast.argSize() < 2) {
      return;
    }
    double[] position = resolvePoint(ast.arg2(), context);
    if (position == null) {
      return;
    }
    ObjectNode node = newElement("Text", style, transform);
    node.put("text", GraphicsOptions3D.text(ast.arg1()));
    node.set("position", vector(position));
    track(position, transform);
    Color c = style.effectiveText();
    node.put("color", rgb(c));
    node.put("opacity", style.alphaOf(c));
    node.put("fontSize", style.fontSize);
    node.put("fontFamily", style.fontFamily);
    node.put("fontWeight", style.fontWeight);
    node.put("fontStyle", style.fontStyle);
    if (ast.argSize() >= 3 && ast.arg3().isList() && ((IAST) ast.arg3()).argSize() >= 2) {
      IAST offset = (IAST) ast.arg3();
      ArrayNode array = node.putArray("offset");
      array.add(ColorUtil.dbl(offset.arg1(), 0.0)).add(ColorUtil.dbl(offset.arg2(), 0.0));
    }
  }

  // ------------------------------------------------------------ shared parts

  private ObjectNode newElement(String type, Style3D style, Transform3D transform) {
    ObjectNode node = elements.addObject();
    node.put("type", type);
    if (!transform.isIdentity()) {
      ArrayNode matrix = node.putArray("matrix");
      for (double v : transform.columnMajor()) {
        matrix.add(v);
      }
    }
    return node;
  }

  private static void writeSurfaceStyle(ObjectNode node, Style3D style) {
    node.put("showMesh", style.showEdges);
    if (style.showEdges) {
      if (style.edgeColor != null) {
        node.put("edgeColor", rgb(style.edgeColor));
      }
      node.put("edgeOpacity", style.edgeOpacity);
      node.put("edgeThickness", style.edgeThickness);
    }
    if (!Double.isNaN(style.specularity)) {
      node.put("specularity", style.specularity);
      node.put("specularExponent", style.specularExponent);
    }
    if (style.glow != null) {
      node.put("glow", rgb(style.glow));
    }
  }

  private static void writeLineStyle(ObjectNode node, Style3D style) {
    writeSize(node, "thickness", style.thickness);
    if (style.dashing != null && style.dashing.length > 0) {
      ArrayNode dash = node.putArray("dashing");
      for (double d : style.dashing) {
        dash.add(d);
      }
      node.put("dashingScaled", style.dashingScaled);
    }
    if (style.glow != null) {
      node.put("glow", rgb(style.glow));
    }
  }

  private static void writeSize(ObjectNode node, String name, Style3D.Size size) {
    if (size == null) {
      return;
    }
    if (size.isScaled()) {
      node.put(name + "Scaled", size.scaled);
    } else {
      node.put(name, size.absolute);
    }
  }

  private void writePolylines(ObjectNode node, List<List<double[]>> polylines,
      Transform3D transform) {
    ArrayNode array = node.putArray("polylines");
    for (List<double[]> line : polylines) {
      ArrayNode flat = array.addArray();
      for (double[] p : line) {
        flat.add(p[0]).add(p[1]).add(p[2]);
        track(p, transform);
      }
      vertexBudget -= line.size();
    }
  }

  private void writeBSplineData(ObjectNode node, IAST bspline, ComplexContext context,
      Transform3D transform, double radius) {
    List<double[]> points = new ArrayList<>();
    collectPoints(bspline.arg1(), context, points);
    for (double[] p : points) {
      // the curve stays inside the convex hull of its control points, so they bound it
      trackBall(p, radius, transform);
    }
    int degree = 3;
    IExpr optDegree = option(bspline, S.SplineDegree);
    if (optDegree != null) {
      degree = Math.max(1, optDegree.toIntDefault(3));
    }
    boolean closed = false;
    IExpr optClosed = option(bspline, S.SplineClosed);
    if (optClosed != null && optClosed.isTrue()) {
      closed = true;
    }
    if (closed && !points.isEmpty()) {
      for (int i = 0; i < degree && i < points.size(); i++) {
        points.add(points.get(i));
      }
    }
    degree = Math.min(degree, Math.max(1, points.size() - 1));

    ArrayNode array = node.putArray("points");
    for (double[] p : points) {
      array.add(p[0]).add(p[1]).add(p[2]);
    }
    node.put("degree", degree);
    node.put("closed", closed);

    double[] knots = null;
    IExpr optKnots = option(bspline, S.SplineKnots);
    if (optKnots != null && optKnots.isList()) {
      IAST list = (IAST) optKnots;
      knots = new double[list.argSize()];
      for (int i = 0; i < knots.length; i++) {
        knots[i] = ColorUtil.dbl(list.get(i + 1), i);
      }
    }
    if (knots == null) {
      knots = clampedKnots(points.size(), degree, closed);
    }
    ArrayNode knotArray = node.putArray("knots");
    for (double k : knots) {
      knotArray.add(k);
    }
    IExpr optWeights = option(bspline, S.SplineWeights);
    if (optWeights != null && optWeights.isList()) {
      IAST list = (IAST) optWeights;
      ArrayNode weights = node.putArray("weights");
      for (int i = 0; i < points.size(); i++) {
        weights.add(i + 1 < list.size() ? ColorUtil.dbl(list.get(i + 1), 1.0) : 1.0);
      }
    }
  }

  /** A uniform knot vector, clamped at both ends so the curve meets its first and last point. */
  private static double[] clampedKnots(int pointCount, int degree, boolean closed) {
    int n = Math.max(pointCount, degree + 1);
    double[] knots = new double[n + degree + 1];
    if (closed) {
      for (int i = 0; i < knots.length; i++) {
        knots[i] = i;
      }
      return knots;
    }
    int interior = n - degree;
    for (int i = 0; i <= degree; i++) {
      knots[i] = 0.0;
    }
    for (int i = 1; i < interior; i++) {
      knots[degree + i] = (double) i / interior;
    }
    for (int i = n; i < knots.length; i++) {
      knots[i] = 1.0;
    }
    return knots;
  }

  // --------------------------------------------------------- data extraction

  /**
   * The faces of a polygon specification. A single face is a flat list of coordinates or indices; a
   * multi face specification is a list of those.
   */
  private List<IExpr> faces(IExpr data, ComplexContext context) {
    List<IExpr> result = new ArrayList<>();
    if (!data.isList()) {
      return result;
    }
    IAST list = (IAST) data;
    if (list.argSize() == 0) {
      return result;
    }
    IExpr first = list.arg1();
    boolean multi;
    if (context != null) {
      // inside a GraphicsComplex a face is a list of integers, so a list of lists is multi face
      multi = first.isList();
    } else {
      multi = first.isList() && ((IAST) first).argSize() > 0 && ((IAST) first).arg1().isList();
    }
    if (multi) {
      for (int i = 1; i <= list.argSize(); i++) {
        result.add(list.get(i));
      }
    } else {
      result.add(list);
    }
    return result;
  }

  /** Split a line specification into its polylines. */
  private List<List<double[]>> polylines(IExpr data, ComplexContext context) {
    List<List<double[]>> result = new ArrayList<>();
    for (IExpr face : faces(data, context)) {
      List<double[]> line = new ArrayList<>();
      collectPoints(face, context, line);
      if (line.size() >= 2) {
        result.add(line);
      }
    }
    return result;
  }

  /** Append every coordinate in {@code data}, resolving indices against the enclosing complex. */
  private void collectPoints(IExpr data, ComplexContext context, List<double[]> out) {
    if (!data.isList()) {
      double[] single = resolvePoint(data, context);
      if (single != null) {
        out.add(single);
      }
      return;
    }
    IAST list = (IAST) data;
    // A bare coordinate triple rather than a list of them. Inside a GraphicsComplex a list of
    // three integers is three vertex indices, not a point: Line[{1, 2, 3}] traces three vertices
    // of the pool, and reading it as the coordinate (1, 2, 3) collapsed every such line to a
    // single point, which then drew nothing at all.
    boolean ambiguous = context != null && isIndexList(list);
    double[] direct = ambiguous ? null : resolvePoint(list, context);
    if (direct != null) {
      out.add(direct);
      return;
    }
    for (int i = 1; i <= list.argSize(); i++) {
      IExpr entry = list.get(i);
      double[] point = resolvePoint(entry, context);
      if (point != null) {
        out.add(point);
      } else if (entry.isList()) {
        collectPoints(entry, context, out);
      }
    }
  }

  /** Whether every entry is an integer, which inside a complex means they are vertex indices. */
  private static boolean isIndexList(IAST list) {
    if (list.argSize() == 0) {
      return false;
    }
    for (int i = 1; i <= list.argSize(); i++) {
      if (!list.get(i).isInteger()) {
        return false;
      }
    }
    return true;
  }

  /** A single coordinate: a numeric triple, or an index into the enclosing complex. */
  private double[] resolvePoint(IExpr expr, ComplexContext context) {
    if (context != null && expr.isInteger()) {
      double[] point = context.point(expr.toIntDefault(0));
      return point == null ? null : applyScaling(point);
    }
    double[] v = GraphicsOptions3D.vector(expr);
    return v == null ? null : applyScaling(v);
  }

  private double[] applyScaling(double[] v) {
    return new double[] {scale(v[0], scaling[0]), scale(v[1], scaling[1]), scale(v[2], scaling[2])};
  }

  private static double scale(double value, String type) {
    if ("Log".equals(type)) {
      return value > 0 ? Math.log10(value) : Double.NaN;
    }
    if ("Reverse".equals(type)) {
      return -value;
    }
    return value;
  }

  /** Assembles an indexed triangle mesh out of polygon faces. */
  private final class MeshBuilder {
    final List<double[]> points = new ArrayList<>();
    final List<Color> colors = new ArrayList<>();
    final List<double[]> normals = new ArrayList<>();
    final List<Integer> indices = new ArrayList<>();

    private final ComplexContext context;
    private final IAST localColors;
    private final IAST localNormals;
    /** Complex vertex index to mesh vertex index, so shared vertices stay shared. */
    private final Map<Integer, Integer> byComplexIndex = new HashMap<>();
    /** Rounded coordinate key to mesh vertex index, used when there is no complex to index into. */
    private final Map<Long, Integer> byCoordinate = new HashMap<>();
    private boolean anyColor = false;
    private boolean anyNormal = false;
    private int localVertex = 0;

    MeshBuilder(ComplexContext context, IAST localColors, IAST localNormals) {
      this.context = context;
      this.localColors = localColors;
      this.localNormals = localNormals;
    }

    boolean hasColors() {
      return anyColor;
    }

    boolean hasNormals() {
      return anyNormal;
    }

    void addFace(IExpr face) {
      if (!face.isList()) {
        return;
      }
      IAST list = (IAST) face;
      List<Integer> corners = new ArrayList<>();
      for (int i = 1; i <= list.argSize(); i++) {
        int index = vertexOf(list.get(i));
        if (index >= 0) {
          corners.add(index);
        }
      }
      // fan triangulation, which is exact for the convex faces every plot builtin emits
      for (int i = 1; i + 1 < corners.size(); i++) {
        indices.add(corners.get(0));
        indices.add(corners.get(i));
        indices.add(corners.get(i + 1));
      }
    }

    private int vertexOf(IExpr entry) {
      int position = localVertex++;
      if (context != null && entry.isInteger()) {
        int complexIndex = entry.toIntDefault(0);
        Integer existing = byComplexIndex.get(complexIndex);
        if (existing != null) {
          return existing;
        }
        double[] point = context.point(complexIndex);
        if (point == null) {
          return -1;
        }
        int index = add(applyScaling(point));
        byComplexIndex.put(complexIndex, index);
        attach(index, position, complexIndex);
        return index;
      }
      double[] point = GraphicsOptions3D.vector(entry);
      if (point == null) {
        return -1;
      }
      double[] scaled = applyScaling(point);
      long key = coordinateKey(scaled);
      Integer existing = byCoordinate.get(key);
      if (existing != null) {
        return existing;
      }
      int index = add(scaled);
      byCoordinate.put(key, index);
      attach(index, position, -1);
      return index;
    }

    private int add(double[] point) {
      points.add(point);
      colors.add(null);
      normals.add(null);
      return points.size() - 1;
    }

    /** Attach the colour and normal that belong to a vertex, wherever they were specified. */
    private void attach(int index, int position, int complexIndex) {
      Color color = null;
      if (localColors != null && position + 1 < localColors.size()) {
        color = ColorUtil.parse(localColors.get(position + 1));
      } else if (context != null && context.vertexColors != null && complexIndex > 0
          && complexIndex < context.vertexColors.size()) {
        color = ColorUtil.parse(context.vertexColors.get(complexIndex));
      }
      if (color != null) {
        colors.set(index, color);
        anyColor = true;
      }
      double[] normal = null;
      if (localNormals != null && position + 1 < localNormals.size()) {
        normal = GraphicsOptions3D.vector(localNormals.get(position + 1));
      } else if (context != null && context.vertexNormals != null && complexIndex > 0
          && complexIndex < context.vertexNormals.size()) {
        normal = GraphicsOptions3D.vector(context.vertexNormals.get(complexIndex));
      }
      if (normal != null) {
        normals.set(index, normal);
        anyNormal = true;
      }
    }

    private long coordinateKey(double[] p) {
      long x = Math.round(p[0] * 1e5);
      long y = Math.round(p[1] * 1e5);
      long z = Math.round(p[2] * 1e5);
      return (x * 73856093L) ^ (y * 19349663L) ^ (z * 83492791L);
    }
  }

  // ------------------------------------------------------------------ helpers

  private Transform3D rotateTransform(IAST ast, Transform3D current) {
    if (ast.argSize() < 2) {
      return current;
    }
    double angle = ColorUtil.dbl(ast.arg2(), Double.NaN);
    if (Double.isNaN(angle)) {
      return current;
    }
    double[] axis = ast.argSize() >= 3 ? GraphicsOptions3D.vector(ast.arg3()) : null;
    if (axis == null) {
      axis = new double[] {0, 0, 1};
    }
    double[] center = ast.argSize() >= 4 ? GraphicsOptions3D.vector(ast.arg4()) : null;
    Transform3D rotation = Transform3D.rotation(angle, axis);
    if (center != null) {
      rotation = Transform3D.translation(center).times(rotation)
          .times(Transform3D.translation(new double[] {-center[0], -center[1], -center[2]}));
    }
    return current.times(rotation);
  }

  private Transform3D scaleTransform(IAST ast, Transform3D current) {
    if (ast.argSize() < 2) {
      return current;
    }
    double[] factors = GraphicsOptions3D.vector(ast.arg2());
    if (factors == null) {
      double s = ColorUtil.dbl(ast.arg2(), Double.NaN);
      if (Double.isNaN(s)) {
        return current;
      }
      factors = new double[] {s, s, s};
    }
    double[] center = ast.argSize() >= 3 ? GraphicsOptions3D.vector(ast.arg3()) : null;
    Transform3D scale = Transform3D.scaling(factors);
    if (center != null) {
      scale = Transform3D.translation(center).times(scale)
          .times(Transform3D.translation(new double[] {-center[0], -center[1], -center[2]}));
    }
    return current.times(scale);
  }

  private ArrayNode vector(double[] v) {
    ArrayNode array = elements.arrayNode();
    array.add(v[0]).add(v[1]).add(v[2]);
    return array;
  }

  private static int rgb(Color c) {
    return c == null ? 0 : (c.getRGB() & 0x00FFFFFF);
  }

  private static double clamp01(double v) {
    if (Double.isNaN(v)) {
      return 1.0;
    }
    return Math.max(0.0, Math.min(1.0, v));
  }

  /** The value of an option rule among the arguments of {@code ast}, or {@code null}. */
  static IExpr option(IAST ast, org.matheclipse.core.interfaces.ISymbol name) {
    for (int i = 2; i <= ast.argSize(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && ((IAST) arg).arg1() == name) {
        return ((IAST) arg).arg2();
      }
    }
    return null;
  }

  static IAST optionList(IAST ast, org.matheclipse.core.interfaces.ISymbol name) {
    IExpr value = option(ast, name);
    return value != null && value.isList() ? (IAST) value : null;
  }
}
