package org.matheclipse.core.graphics.svg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Walks a graphics expression and collects it into a list of {@link Prim2D}.
 *
 * <p>
 * A malformed primitive is skipped and recorded in {@link #errors()} rather than propagated: one
 * bad argument must cost that one shape, never the whole picture.
 */
public final class PrimitiveCollector {

  /** How finely curves and ring segments are flattened when an exact form is unavailable. */
  private static final int FLATTEN_STEPS = 64;

  private final List<Prim2D> primitives = new ArrayList<>();
  private final List<String> errors = new ArrayList<>();
  private final double imageWidth;

  /** Vertex table of the enclosing {@code GraphicsComplex}, or {@code null}. */
  private List<double[]> vertices;

  public PrimitiveCollector(double imageWidth) {
    this.imageWidth = imageWidth > 0 ? imageWidth : 360.0;
  }

  public List<Prim2D> primitives() {
    return primitives;
  }

  public List<String> errors() {
    return errors;
  }

  /** Collect {@code expr} using {@code style} as the starting state. */
  public void collect(IExpr expr, Style2D style) {
    if (expr == null) {
      return;
    }
    if (expr.isList()) {
      // a nested list scopes any directive it contains
      Style2D scoped = style.clone();
      IAST list = (IAST) expr;
      for (int i = 1; i <= list.argSize(); i++) {
        collect(list.get(i), scoped);
      }
      return;
    }
    if (expr.isBuiltInSymbol()) {
      applySymbolDirective((IBuiltInSymbol) expr, style);
      return;
    }
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol()) {
      return;
    }
    try {
      dispatch((IBuiltInSymbol) head, ast, style);
    } catch (RuntimeException rex) {
      errors.add(head + ": " + rex.getClass().getSimpleName() + " " + rex.getMessage());
    }
  }

  private void dispatch(IBuiltInSymbol head, IAST ast, Style2D style) {
    switch (head.ordinal()) {
      // ---- directives ----
      case ID.RGBColor:
      case ID.Hue:
      case ID.GrayLevel:
      case ID.CMYKColor:
      case ID.Blend:
      case ID.Lighter:
      case ID.Darker: {
        Color c = ColorUtil.parse(ast);
        if (c != null) {
          style.setColor(c);
        }
        break;
      }
      case ID.Opacity:
        if (ast.argSize() >= 2) {
          Color c = ColorUtil.parse(ast);
          if (c != null) {
            style.setColor(c);
          }
        } else if (ast.argSize() == 1) {
          double o = ColorUtil.dbl(ast.arg1(), 1.0);
          style.opacity = Math.max(0.0, Math.min(1.0, o));
        }
        break;
      case ID.Thickness:
        if (ast.argSize() >= 1) {
          style.strokeWidth = namedThickness(ast.arg1(), 0.001) * imageWidth;
        }
        break;
      case ID.AbsoluteThickness:
        if (ast.argSize() >= 1) {
          style.strokeWidth = ColorUtil.dbl(ast.arg1(), 1.0);
        }
        break;
      case ID.PointSize:
        if (ast.argSize() >= 1) {
          style.pointRadius = namedPointSize(ast.arg1()) * imageWidth / 2.0;
        }
        break;
      case ID.AbsolutePointSize:
        if (ast.argSize() >= 1) {
          style.pointRadius = ColorUtil.dbl(ast.arg1(), 3.0) / 2.0;
        }
        break;
      case ID.Dashing:
        applyDashing(ast, style, imageWidth);
        break;
      case ID.AbsoluteDashing:
        applyDashing(ast, style, 1.0);
        break;
      case ID.EdgeForm:
        applyEdgeForm(ast, style);
        break;
      case ID.FaceForm:
        if (ast.argSize() >= 1) {
          style.faceColor =
              ast.arg1().isNone() ? ColorUtil.TRANSPARENT : ColorUtil.parse(ast.arg1());
        }
        break;
      case ID.CapForm:
        style.lineCap = capOrJoin(ast, "butt", "round", "square");
        break;
      case ID.JoinForm:
        style.lineJoin = capOrJoin(ast, "miter", "round", "bevel");
        break;
      case ID.Arrowheads:
        applyArrowheads(ast, style);
        break;
      case ID.Directive:
        for (int i = 1; i <= ast.argSize(); i++) {
          collect(ast.get(i), style);
        }
        break;
      case ID.Rule:
      case ID.RuleDelayed:
        applyStyleRule(ast, style);
        break;
      case ID.Style: {
        if (ast.argSize() >= 1) {
          Style2D scoped = style.clone();
          for (int i = 2; i <= ast.argSize(); i++) {
            collectDirectiveOnly(ast.get(i), scoped);
          }
          collect(ast.arg1(), scoped);
        }
        break;
      }

      // ---- containers ----
      case ID.GraphicsComplex:
        collectGraphicsComplex(ast, style);
        break;
      case ID.GraphicsGroup:
      case ID.Annotation:
      case ID.Mouseover:
      case ID.StatusArea:
      case ID.Legended:
        if (ast.argSize() >= 1) {
          collect(ast.arg1(), style.clone());
        }
        break;
      case ID.Tooltip: {
        if (ast.argSize() >= 1) {
          Style2D scoped = style.clone();
          // Tooltip(expr) shows the expression itself, which is what makes wrapping a table of
          // bare values worth doing at all
          scoped.tooltip =
              PlotWrapper.tooltipLabel(ast.argSize() >= 2 ? ast.arg2() : ast.arg1());
          collect(ast.arg1(), scoped);
        }
        break;
      }

      // ---- transformations ----
      case ID.Rotate:
        collectRotate(ast, style);
        break;
      case ID.Translate:
        collectTranslate(ast, style);
        break;
      case ID.Scale:
        collectScale(ast, style);
        break;
      case ID.GeometricTransformation:
        collectGeometricTransformation(ast, style);
        break;

      // ---- primitives ----
      case ID.Point:
        collectPoint(ast, style);
        break;
      case ID.Line:
        collectLine(ast, style);
        break;
      case ID.Arrow:
        collectArrow(ast, style);
        break;
      case ID.Rectangle:
        collectRectangle(ast, style);
        break;
      case ID.Polygon:
      case ID.Triangle:
        collectPolygon(ast, style);
        break;
      case ID.RegularPolygon:
        collectRegularPolygon(ast, style);
        break;
      case ID.Circle:
        collectEllipse(ast, style, false, false);
        break;
      case ID.Disk:
        collectEllipse(ast, style, true, false);
        break;
      case ID.Annulus:
        collectEllipse(ast, style, true, true);
        break;
      case ID.StadiumShape:
        collectStadiumShape(ast, style);
        break;
      case ID.Parallelogram:
        collectParallelogram(ast, style);
        break;
      case ID.SSSTriangle:
      case ID.SASTriangle:
      case ID.ASATriangle:
      case ID.AASTriangle:
        collectSpecTriangle(head.ordinal(), ast, style);
        break;
      case ID.Text:
        collectText(ast, style);
        break;
      case ID.Inset:
        collectInset(ast, style);
        break;
      case ID.BezierCurve:
        collectBezier(ast, style, false);
        break;
      case ID.BSplineCurve:
        collectBSpline(ast, style, false);
        break;
      case ID.JoinedCurve:
        if (ast.argSize() >= 1) {
          collect(ast.arg1(), style.clone());
        }
        break;
      case ID.FilledCurve:
        collectFilledCurve(ast, style);
        break;
      case ID.HalfPlane:
        collectHalfPlane(ast, style, false, false);
        break;
      case ID.InfiniteLine:
        collectHalfPlane(ast, style, false, true);
        break;
      case ID.InfinitePlane:
        collectHalfPlane(ast, style, true, false);
        break;
      case ID.Raster:
        collectRaster(ast, style);
        break;
      default:
        // not a primitive we know; ignore rather than guess
        break;
    }
  }

  /**
   * Apply a style specification to {@code target} without emitting anything, so that option values
   * such as {@code AxesStyle -> Directive[Red, Dashed]} are interpreted exactly like inline
   * directives.
   */
  public void applyStyleTo(IExpr expr, Style2D target) {
    if (expr == null) {
      return;
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      for (int i = 1; i <= list.argSize(); i++) {
        collectDirectiveOnly(list.get(i), target);
      }
      return;
    }
    collectDirectiveOnly(expr, target);
  }

  /** Apply {@code expr} as a directive only, never emitting a primitive. */
  private void collectDirectiveOnly(IExpr expr, Style2D style) {
    int before = primitives.size();
    collect(expr, style);
    while (primitives.size() > before) {
      primitives.remove(primitives.size() - 1);
    }
  }

  // ------------------------------------------------------------- directives

  private void applySymbolDirective(IBuiltInSymbol symbol, Style2D style) {
    Color named = ColorUtil.named(symbol);
    if (named != null) {
      style.setColor(named);
      return;
    }
    switch (symbol.ordinal()) {
      case ID.Thick:
        style.strokeWidth = imageWidth * 0.006;
        break;
      case ID.Thin:
        style.strokeWidth = imageWidth * 0.001;
        break;
      case ID.Dashed:
        style.dashArray = fmtDash(new double[] {imageWidth * 0.01, imageWidth * 0.01});
        break;
      case ID.Dotted:
        style.dashArray = fmtDash(new double[] {0.0, imageWidth * 0.01});
        style.lineCap = "round";
        break;
      case ID.DotDashed:
        style.dashArray =
            fmtDash(new double[] {0.0, imageWidth * 0.01, imageWidth * 0.01, imageWidth * 0.01});
        break;
      case ID.Bold:
        style.fontWeight = "bold";
        break;
      case ID.Italic:
        style.fontStyle = "italic";
        break;
      case ID.Plain:
        style.fontWeight = "normal";
        style.fontStyle = "normal";
        break;
      case ID.Underlined:
        style.textDecoration = "underline";
        break;
      default:
        break;
    }
  }

  /** Rules that appear inside {@code Style} or a directive list. */
  private void applyStyleRule(IAST rule, Style2D style) {
    if (rule.argSize() < 2 || !rule.arg1().isBuiltInSymbol()) {
      return;
    }
    IExpr value = rule.arg2();
    switch (((IBuiltInSymbol) rule.arg1()).ordinal()) {
      case ID.FontColor: {
        Color c = ColorUtil.parse(value);
        if (c != null) {
          style.setColor(c);
        }
        break;
      }
      case ID.FontSize:
        style.fontSize = fontSizeOf(value, style.fontSize);
        break;
      case ID.FontFamily:
        style.fontFamily = unquote(value.toString());
        break;
      case ID.FontWeight: {
        String w = unquote(value.toString()).toLowerCase(Locale.US);
        style.fontWeight = w.contains("bold") ? "bold" : "normal";
        break;
      }
      case ID.FontSlant: {
        String s = unquote(value.toString()).toLowerCase(Locale.US);
        style.fontStyle = (s.contains("italic") || s.contains("oblique")) ? "italic" : "normal";
        break;
      }
      case ID.Opacity:
        style.opacity = Math.max(0.0, Math.min(1.0, ColorUtil.dbl(value, 1.0)));
        break;
      case ID.Thickness:
        style.strokeWidth = namedThickness(value, 0.001) * imageWidth;
        break;
      case ID.AbsoluteThickness:
        style.strokeWidth = ColorUtil.dbl(value, 1.0);
        break;
      case ID.PointSize:
        style.pointRadius = namedPointSize(value) * imageWidth / 2.0;
        break;
      default:
        break;
    }
  }

  private double fontSizeOf(IExpr value, double current) {
    if (value.isAST(S.Scaled, 2)) {
      return ColorUtil.dbl(((IAST) value).arg1(), 0.02) * imageWidth;
    }
    if (value.isAST(S.Offset, 2)) {
      return ColorUtil.dbl(((IAST) value).arg1(), current);
    }
    if (value.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) value).ordinal()) {
        case ID.Tiny:
          return 6.0;
        case ID.Small:
          return 9.0;
        case ID.Medium:
          return 12.0;
        case ID.Large:
          return 18.0;
        default:
          return current;
      }
    }
    return ColorUtil.dbl(value, current);
  }

  private void applyDashing(IAST ast, Style2D style, double factor) {
    if (ast.argSize() < 1) {
      return;
    }
    IExpr arg = ast.arg1();
    if (arg.isNone() || arg.isFalse()) {
      style.dashArray = "none";
      return;
    }
    List<Double> values = new ArrayList<>();
    if (arg.isList()) {
      IAST list = (IAST) arg;
      for (int i = 1; i <= list.argSize(); i++) {
        values.add(namedDash(list.get(i)) * factor);
      }
    } else {
      double d = namedDash(arg) * factor;
      values.add(d);
      values.add(d);
    }
    if (values.isEmpty()) {
      style.dashArray = "none";
      return;
    }
    double[] arr = new double[values.size()];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = values.get(i);
    }
    // an all-zero pattern would make the line vanish
    boolean allZero = true;
    for (double d : arr) {
      if (d > 0) {
        allZero = false;
        break;
      }
    }
    style.dashArray = allZero ? "none" : fmtDash(arr);
    if (arr.length >= 2 && arr[0] == 0.0) {
      style.lineCap = "round";
    }
  }

  private double namedDash(IExpr expr) {
    if (expr.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) expr).ordinal()) {
        case ID.Tiny:
          return 0.005;
        case ID.Small:
          return 0.01;
        case ID.Medium:
          return 0.02;
        case ID.Large:
          return 0.04;
        default:
          return 0.02;
      }
    }
    return ColorUtil.dbl(expr, 0.02);
  }

  private double namedThickness(IExpr expr, double def) {
    if (expr.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) expr).ordinal()) {
        case ID.Tiny:
          return 0.0005;
        case ID.Small:
          return 0.001;
        case ID.Medium:
          return 0.002;
        case ID.Large:
          return 0.006;
        default:
          return def;
      }
    }
    return ColorUtil.dbl(expr, def);
  }

  private double namedPointSize(IExpr expr) {
    if (expr.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) expr).ordinal()) {
        case ID.Tiny:
          return 0.005;
        case ID.Small:
          return 0.01;
        case ID.Medium:
          return 0.015;
        case ID.Large:
          return 0.025;
        default:
          return 0.015;
      }
    }
    return ColorUtil.dbl(expr, 0.015);
  }

  private void applyEdgeForm(IAST ast, Style2D style) {
    style.edgeFormSet = true;
    if (ast.argSize() < 1) {
      // EdgeForm[] means "no edge"
      style.edgeColor = null;
      return;
    }
    IExpr arg = ast.arg1();
    if (arg.isNone()) {
      style.edgeColor = null;
      return;
    }
    // an edge form carries its own little style; start from black rather than the fill colour,
    // and opaque rather than at the face's transparency — an Opacity outside the EdgeForm tints
    // the face alone, and only one written inside it fades the outline
    Style2D edge = style.clone();
    edge.setColor(Color.BLACK);
    edge.opacity = 1.0;
    List<IExpr> items = new ArrayList<>();
    if (arg.isList()) {
      IAST list = (IAST) arg;
      for (int i = 1; i <= list.argSize(); i++) {
        items.add(list.get(i));
      }
    } else {
      for (int i = 1; i <= ast.argSize(); i++) {
        items.add(ast.get(i));
      }
    }
    for (IExpr item : items) {
      collectDirectiveOnly(item, edge);
    }
    style.edgeColor = edge.strokeColor;
    style.edgeOpacity = edge.opacity;
    style.strokeWidth = edge.strokeWidth;
    style.dashArray = edge.dashArray;
  }

  private String capOrJoin(IAST ast, String def, String first, String second) {
    if (ast.argSize() < 1) {
      return def;
    }
    String v = unquote(ast.arg1().toString()).toLowerCase(Locale.US);
    if (v.equals("round")) {
      return "round";
    }
    if (v.equals("square") || v.equals("bevel")) {
      return second;
    }
    if (v.equals("butt") || v.equals("miter")) {
      return def;
    }
    return def;
  }

  /**
   * {@code Arrowheads[spec]}. A bare list gives one head per entry, spread evenly from the tail to
   * the tip, so {@code Arrowheads[{-s, s}]} is the usual double headed arrow: a backward head at
   * the start and a forward one at the end. An entry may instead name its own
   * {@code {size, position}}.
   */
  private void applyArrowheads(IAST ast, Style2D style) {
    if (ast.argSize() < 1) {
      return;
    }
    IExpr arg = ast.arg1();
    if (arg.isNone()) {
      style.arrowHeads = new ArrayList<>();
      return;
    }
    if (arg.isBuiltInSymbol()) {
      double size = namedArrowheadSize(arg, Double.NaN);
      if (!Double.isNaN(size)) {
        style.arrowHeadScale = size;
        style.arrowHeads = null;
      }
      return;
    }
    if (arg.isList()) {
      IAST list = (IAST) arg;
      int n = list.argSize();
      List<Style2D.ArrowHead> heads = new ArrayList<>(n);
      for (int i = 1; i <= n; i++) {
        IExpr item = list.get(i);
        double defaultPosition = n <= 1 ? 1.0 : (double) (i - 1) / (n - 1);
        double size;
        double position = defaultPosition;
        if (item.isList() && ((IAST) item).argSize() >= 1) {
          IAST pair = (IAST) item;
          size = namedArrowheadSize(pair.arg1(), ColorUtil.dbl(pair.arg1(), Double.NaN));
          if (pair.argSize() >= 2) {
            position = ColorUtil.dbl(pair.arg2(), defaultPosition);
          }
        } else {
          size = namedArrowheadSize(item, ColorUtil.dbl(item, Double.NaN));
        }
        if (Double.isNaN(size)) {
          continue;
        }
        heads.add(new Style2D.ArrowHead(Math.abs(size), Math.max(0.0, Math.min(1.0, position)),
            size < 0));
      }
      style.arrowHeads = heads;
      return;
    }
    double v = ColorUtil.dbl(arg, Double.NaN);
    if (!Double.isNaN(v)) {
      style.arrowHeadScale = Math.abs(v);
      style.arrowHeads = null;
    }
  }

  private double namedArrowheadSize(IExpr expr, double def) {
    if (expr.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) expr).ordinal()) {
        case ID.Tiny:
          return 0.02;
        case ID.Small:
          return 0.03;
        case ID.Medium:
          return 0.05;
        case ID.Large:
          return 0.08;
        default:
          return def;
      }
    }
    return def;
  }

  // ------------------------------------------------------------ containers

  private void collectGraphicsComplex(IAST ast, Style2D style) {
    if (ast.argSize() < 1 || !ast.arg1().isList()) {
      return;
    }
    List<double[]> saved = vertices;
    try {
      IAST pts = (IAST) ast.arg1();
      List<double[]> table = new ArrayList<>(pts.argSize());
      for (int i = 1; i <= pts.argSize(); i++) {
        table.add(pointOf(pts.get(i)));
      }
      vertices = table;
      if (ast.argSize() >= 2) {
        collect(ast.arg2(), style.clone());
      }
    } finally {
      vertices = saved;
    }
  }

  // -------------------------------------------------------- transformations

  /** Collect {@code expr} in isolation, then push every resulting primitive through {@code map}. */
  private void collectTransformed(IExpr expr, Style2D style, AffineMap2D map) {
    int start = primitives.size();
    collect(expr, style.clone());
    for (int i = start; i < primitives.size(); i++) {
      primitives.set(i, primitives.get(i).mapped(map));
    }
  }

  /** Bounding box of the primitives added since {@code start}, used as a transform centre. */
  private Bounds2D boundsSince(int start) {
    Bounds2D b = new Bounds2D();
    for (int i = start; i < primitives.size(); i++) {
      primitives.get(i).accumulate(b);
    }
    return b;
  }

  private void collectRotate(IAST ast, Style2D style) {
    if (ast.argSize() < 2) {
      return;
    }
    double angle = ColorUtil.dbl(ast.arg2(), Double.NaN);
    if (Double.isNaN(angle)) {
      collect(ast.arg1(), style.clone());
      return;
    }
    int start = primitives.size();
    collect(ast.arg1(), style.clone());
    double[] centre;
    if (ast.argSize() >= 3) {
      centre = pointOf(ast.arg3());
    } else {
      Bounds2D b = boundsSince(start);
      centre = new double[] {b.centerX(), b.centerY()};
    }
    AffineMap2D map = AffineMap2D.rotation(angle, centre[0], centre[1]);
    for (int i = start; i < primitives.size(); i++) {
      primitives.set(i, primitives.get(i).mapped(map));
    }
  }

  private void collectTranslate(IAST ast, Style2D style) {
    if (ast.argSize() < 2) {
      return;
    }
    IExpr offsets = ast.arg2();
    List<double[]> vectors = new ArrayList<>();
    if (listDepth(offsets) >= 2) {
      vectors.addAll(pointsOf(offsets));
    } else {
      vectors.add(pointOf(offsets));
    }
    if (vectors.isEmpty()) {
      collect(ast.arg1(), style.clone());
      return;
    }
    for (double[] v : vectors) {
      collectTransformed(ast.arg1(), style, AffineMap2D.translation(v[0], v[1]));
    }
  }

  private void collectScale(IAST ast, Style2D style) {
    if (ast.argSize() < 2) {
      return;
    }
    double sx;
    double sy;
    if (ast.arg2().isList()) {
      double[] s = pointOf(ast.arg2());
      sx = s[0];
      sy = s[1];
    } else {
      sx = ColorUtil.dbl(ast.arg2(), Double.NaN);
      sy = sx;
    }
    if (Double.isNaN(sx) || Double.isNaN(sy)) {
      collect(ast.arg1(), style.clone());
      return;
    }
    int start = primitives.size();
    collect(ast.arg1(), style.clone());
    double[] centre;
    if (ast.argSize() >= 3) {
      centre = pointOf(ast.arg3());
    } else {
      Bounds2D b = boundsSince(start);
      centre = new double[] {b.centerX(), b.centerY()};
    }
    AffineMap2D map = AffineMap2D.scaling(sx, sy, centre[0], centre[1]);
    for (int i = start; i < primitives.size(); i++) {
      primitives.set(i, primitives.get(i).mapped(map));
    }
  }

  private void collectGeometricTransformation(IAST ast, Style2D style) {
    if (ast.argSize() < 2) {
      return;
    }
    List<AffineMap2D> maps = affineMapsOf(ast.arg2());
    if (maps.isEmpty()) {
      collect(ast.arg1(), style.clone());
      return;
    }
    for (AffineMap2D map : maps) {
      collectTransformed(ast.arg1(), style, map);
    }
  }

  /**
   * Parse a transformation specification: a matrix, a {@code {matrix, vector}} pair, a
   * {@code TransformationFunction} holding a homogeneous matrix, or a list of any of those.
   */
  private List<AffineMap2D> affineMapsOf(IExpr expr) {
    List<AffineMap2D> out = new ArrayList<>();
    if (expr.isAST(S.TransformationFunction, 2)) {
      AffineMap2D m = homogeneousMap(((IAST) expr).arg1());
      if (m != null) {
        out.add(m);
      }
      return out;
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      // {matrix, vector}
      if (list.argSize() == 2 && listDepth(list.arg1()) == 2 && listDepth(list.arg2()) == 1) {
        AffineMap2D m = matrixVectorMap(list.arg1(), list.arg2());
        if (m != null) {
          out.add(m);
          return out;
        }
      }
      // a bare matrix
      if (listDepth(list) == 2) {
        AffineMap2D m = homogeneousMap(list);
        if (m == null) {
          m = matrixVectorMap(list, null);
        }
        if (m != null) {
          out.add(m);
          return out;
        }
      }
      // otherwise a list of transformations
      for (int i = 1; i <= list.argSize(); i++) {
        out.addAll(affineMapsOf(list.get(i)));
      }
    }
    return out;
  }

  private AffineMap2D matrixVectorMap(IExpr matrix, IExpr vector) {
    if (!matrix.isList() || ((IAST) matrix).argSize() < 2) {
      return null;
    }
    IAST m = (IAST) matrix;
    double[] r0 = pointOf(m.arg1());
    double[] r1 = pointOf(m.arg2());
    double vx = 0;
    double vy = 0;
    if (vector != null) {
      double[] v = pointOf(vector);
      vx = v[0];
      vy = v[1];
    }
    return new AffineMap2D(r0[0], r0[1], r1[0], r1[1], vx, vy);
  }

  /** A 3x3 homogeneous matrix, as {@code TransformationFunction} carries. */
  private AffineMap2D homogeneousMap(IExpr expr) {
    if (!expr.isList() || ((IAST) expr).argSize() != 3) {
      return null;
    }
    IAST m = (IAST) expr;
    if (!m.arg1().isList() || ((IAST) m.arg1()).argSize() != 3) {
      return null;
    }
    IAST r0 = (IAST) m.arg1();
    IAST r1 = (IAST) m.arg2();
    return new AffineMap2D(ColorUtil.dbl(r0.arg1(), 1), ColorUtil.dbl(r0.arg2(), 0),
        ColorUtil.dbl(r1.arg1(), 0), ColorUtil.dbl(r1.arg2(), 1), ColorUtil.dbl(r0.arg3(), 0),
        ColorUtil.dbl(r1.arg3(), 0));
  }

  // ------------------------------------------------------------ primitives

  private void collectPoint(IAST ast, Style2D style) {
    if (ast.argSize() < 1) {
      return;
    }
    List<double[]> pts = pointsOf(ast.arg1());
    if (pts.isEmpty()) {
      return;
    }
    primitives.add(new Prim2D.PointsPrim(pts, style.clone()));
  }

  private void collectLine(IAST ast, Style2D style) {
    if (ast.argSize() < 1) {
      return;
    }
    List<List<double[]>> segments = segmentsOf(ast.arg1());
    if (segments.isEmpty()) {
      return;
    }
    primitives.add(new Prim2D.LinePrim(segments, false, style.clone()));
  }

  private void collectArrow(IAST ast, Style2D style) {
    if (ast.argSize() < 1) {
      return;
    }
    double setbackStart = 0;
    double setbackEnd = 0;
    if (ast.argSize() >= 2) {
      IExpr sb = ast.arg2();
      if (sb.isList() && ((IAST) sb).argSize() >= 2) {
        setbackStart = ColorUtil.dbl(((IAST) sb).arg1(), 0);
        setbackEnd = ColorUtil.dbl(((IAST) sb).arg2(), 0);
      } else {
        setbackStart = setbackEnd = ColorUtil.dbl(sb, 0);
      }
    }
    IExpr spec = ast.arg1();
    // Arrow[Line[...]] and Arrow[BezierCurve[...]] wrap the path
    if (spec.isAST() && spec.head().isBuiltInSymbol()
        && ((IBuiltInSymbol) spec.head()).ordinal() == ID.Line) {
      spec = ((IAST) spec).argSize() >= 1 ? ((IAST) spec).arg1() : spec;
    }
    for (List<double[]> seg : segmentsOf(spec)) {
      if (seg.size() >= 2) {
        primitives.add(new Prim2D.ArrowPrim(seg, setbackStart, setbackEnd, style.clone()));
      }
    }
  }

  private void collectRectangle(IAST ast, Style2D style) {
    double[] p1 = ast.argSize() >= 1 ? pointOf(ast.arg1()) : new double[] {0, 0};
    double[] p2 = ast.argSize() >= 2 && !ast.arg2().isRuleAST() ? pointOf(ast.arg2())
        : new double[] {p1[0] + 1, p1[1] + 1};
    double rounding = 0;
    IExpr r = optionValue(ast, S.RoundingRadius);
    if (r != null) {
      rounding = ColorUtil.dbl(r, 0);
    }
    primitives.add(new Prim2D.RectPrim(p1[0], p1[1], p2[0], p2[1], rounding, style.clone()));
  }

  private void collectPolygon(IAST ast, Style2D style) {
    if (ast.argSize() < 1) {
      return;
    }
    IExpr spec = ast.arg1();
    // Polygon[outer -> {hole, ...}]
    if (spec.isRuleAST()) {
      IAST rule = (IAST) spec;
      List<double[]> outer = pointsOf(rule.arg1());
      List<List<double[]>> holes = segmentsOf(rule.arg2());
      if (!outer.isEmpty()) {
        primitives.add(new Prim2D.PolygonPrim(outer, holes, style.clone()));
      }
      return;
    }
    if (listDepth(spec) >= 3) {
      for (List<double[]> poly : segmentsOf(spec)) {
        if (!poly.isEmpty()) {
          primitives.add(new Prim2D.PolygonPrim(poly, style.clone()));
        }
      }
      return;
    }
    List<double[]> pts = pointsOf(spec);
    if (!pts.isEmpty()) {
      primitives.add(new Prim2D.PolygonPrim(pts, style.clone()));
    }
  }

  private void collectRegularPolygon(IAST ast, Style2D style) {
    double[] centre = {0, 0};
    double radius = 1;
    int n;
    double startAngle = Double.NaN;
    if (ast.argSize() == 1) {
      n = ast.arg1().toIntDefault(0);
    } else if (ast.argSize() == 2) {
      // RegularPolygon[r, n]
      radius = ColorUtil.dbl(ast.arg1(), 1);
      n = ast.arg2().toIntDefault(0);
    } else {
      centre = pointOf(ast.arg1());
      if (ast.arg2().isList()) {
        // RegularPolygon[centre, {r, angle}, n]
        IAST ra = (IAST) ast.arg2();
        radius = ColorUtil.dbl(ra.arg1(), 1);
        startAngle = ColorUtil.dbl(ra.arg2(), Double.NaN);
      } else {
        radius = ColorUtil.dbl(ast.arg2(), 1);
      }
      n = ast.arg3().toIntDefault(0);
    }
    if (n < 3) {
      errors.add("RegularPolygon: needs at least 3 sides");
      return;
    }
    if (Double.isNaN(startAngle)) {
      // put the first vertex at the top for an odd count, and level for an even one
      startAngle = Math.PI / 2;
    }
    List<double[]> pts = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      double t = startAngle + 2 * Math.PI * i / n;
      pts.add(new double[] {centre[0] + radius * Math.cos(t), centre[1] + radius * Math.sin(t)});
    }
    primitives.add(new Prim2D.PolygonPrim(pts, style.clone()));
  }

  private void collectEllipse(IAST ast, Style2D style, boolean filled, boolean annulus) {
    double[] centre = ast.argSize() >= 1 ? pointOf(ast.arg1()) : new double[] {0, 0};
    double rx = 1;
    double ry = 1;
    double innerRx = 0;
    double innerRy = 0;
    int angleArg = 3;
    if (annulus) {
      // Annulus[centre, {inner, outer}] or Annulus[centre, {inner, outer}, {t1, t2}]
      if (ast.argSize() >= 2 && ast.arg2().isList() && ((IAST) ast.arg2()).argSize() >= 2) {
        IAST radii = (IAST) ast.arg2();
        innerRx = innerRy = ColorUtil.dbl(radii.arg1(), 0);
        rx = ry = ColorUtil.dbl(radii.arg2(), 1);
      }
    } else if (ast.argSize() >= 2) {
      IExpr r = ast.arg2();
      if (r.isList() && ((IAST) r).argSize() >= 2) {
        rx = ColorUtil.dbl(((IAST) r).arg1(), 1);
        ry = ColorUtil.dbl(((IAST) r).arg2(), 1);
      } else {
        rx = ry = ColorUtil.dbl(r, 1);
      }
    }
    double[] angles = null;
    if (ast.argSize() >= angleArg) {
      IExpr a = ast.get(angleArg);
      if (a.isList() && ((IAST) a).argSize() >= 2) {
        double t1 = ColorUtil.dbl(((IAST) a).arg1(), 0);
        double t2 = ColorUtil.dbl(((IAST) a).arg2(), 2 * Math.PI);
        if (Double.isFinite(t1) && Double.isFinite(t2) && t1 != t2) {
          angles = new double[] {t1, t2};
        }
      }
    }
    // a negative radius still describes the same shape; a zero one draws nothing at all
    rx = Math.abs(rx);
    ry = Math.abs(ry);
    if (rx <= 0 && ry <= 0) {
      return;
    }
    primitives.add(new Prim2D.EllipsePrim(centre[0], centre[1], rx, ry, innerRx, innerRy, 0, angles,
        filled, style.clone()));
  }

  private void collectStadiumShape(IAST ast, Style2D style) {
    if (ast.argSize() < 2) {
      return;
    }
    List<double[]> ends = pointsOf(ast.arg1());
    if (ends.size() < 2) {
      return;
    }
    double r = ColorUtil.dbl(ast.arg2(), 1);
    double[] a = ends.get(0);
    double[] b = ends.get(1);
    double dx = b[0] - a[0];
    double dy = b[1] - a[1];
    double len = Math.hypot(dx, dy);
    if (len < 1e-12) {
      primitives.add(new Prim2D.EllipsePrim(a[0], a[1], r, r, 0, 0, 0, null, true, style.clone()));
      return;
    }
    double nx = -dy / len;
    double ny = dx / len;
    double base = Math.atan2(ny, nx);
    List<double[]> pts = new ArrayList<>();
    // the half circle around b, then the half circle around a
    for (int i = 0; i <= 32; i++) {
      double t = base - Math.PI * i / 32;
      pts.add(new double[] {b[0] + r * Math.cos(t), b[1] + r * Math.sin(t)});
    }
    for (int i = 0; i <= 32; i++) {
      double t = base + Math.PI - Math.PI * i / 32;
      pts.add(new double[] {a[0] + r * Math.cos(t), a[1] + r * Math.sin(t)});
    }
    primitives.add(new Prim2D.PolygonPrim(pts, style.clone()));
  }

  private void collectParallelogram(IAST ast, Style2D style) {
    double[] origin = ast.argSize() >= 1 ? pointOf(ast.arg1()) : new double[] {0, 0};
    double[] v1 = {1, 0};
    double[] v2 = {0, 1};
    if (ast.argSize() >= 2 && ast.arg2().isList() && ((IAST) ast.arg2()).argSize() >= 2) {
      IAST vs = (IAST) ast.arg2();
      v1 = pointOf(vs.arg1());
      v2 = pointOf(vs.arg2());
    }
    List<double[]> pts = new ArrayList<>(4);
    pts.add(new double[] {origin[0], origin[1]});
    pts.add(new double[] {origin[0] + v1[0], origin[1] + v1[1]});
    pts.add(new double[] {origin[0] + v1[0] + v2[0], origin[1] + v1[1] + v2[1]});
    pts.add(new double[] {origin[0] + v2[0], origin[1] + v2[1]});
    primitives.add(new Prim2D.PolygonPrim(pts, style.clone()));
  }

  private void collectSpecTriangle(int kind, IAST ast, Style2D style) {
    if (ast.argSize() < 3) {
      return;
    }
    double a = ColorUtil.dbl(ast.arg1(), Double.NaN);
    double b = ColorUtil.dbl(ast.arg2(), Double.NaN);
    double c = ColorUtil.dbl(ast.arg3(), Double.NaN);
    if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c)) {
      return;
    }
    double baseLen;
    double apexX;
    double apexY;
    switch (kind) {
      case ID.SSSTriangle: {
        // sides a, b, c with a along the x axis
        if (a + b <= c || b + c <= a || c + a <= b) {
          errors.add("SSSTriangle: the side lengths do not satisfy the triangle inequality");
          return;
        }
        baseLen = a;
        apexX = (a * a + c * c - b * b) / (2 * a);
        apexY = Math.sqrt(Math.max(0, c * c - apexX * apexX));
        break;
      }
      case ID.SASTriangle: {
        // sides a and c enclosing the angle b
        baseLen = a;
        apexX = c * Math.cos(b);
        apexY = c * Math.sin(b);
        break;
      }
      case ID.ASATriangle: {
        // angles a and c with the side b between them
        double tanA = Math.tan(a);
        double tanC = Math.tan(c);
        if (Math.abs(tanA + tanC) < 1e-12) {
          return;
        }
        baseLen = b;
        apexX = b * tanC / (tanA + tanC);
        apexY = apexX * tanA;
        break;
      }
      case ID.AASTriangle:
      default: {
        // angles a and b, then the side c opposite the first
        double gamma = Math.PI - a - b;
        if (gamma <= 0 || Math.abs(Math.sin(a)) < 1e-12) {
          return;
        }
        double side = c * Math.sin(gamma) / Math.sin(a);
        double tanA = Math.tan(a);
        double tanB = Math.tan(b);
        if (Math.abs(tanA + tanB) < 1e-12) {
          return;
        }
        baseLen = side;
        apexX = side * tanB / (tanA + tanB);
        apexY = apexX * tanA;
        break;
      }
    }
    List<double[]> pts = new ArrayList<>(3);
    pts.add(new double[] {0, 0});
    pts.add(new double[] {baseLen, 0});
    pts.add(new double[] {apexX, apexY});
    primitives.add(new Prim2D.PolygonPrim(pts, style.clone()));
  }

  private void collectText(IAST ast, Style2D style) {
    if (ast.argSize() < 1) {
      return;
    }
    Style2D textStyle = style.clone();
    IExpr content = ast.arg1();
    Color frame = null;
    Color background = null;

    // unwrap the display wrappers a label can carry
    while (content.isAST()) {
      IAST wrapper = (IAST) content;
      if (wrapper.argSize() < 1) {
        break;
      }
      IExpr head = wrapper.head();
      if (head == S.Framed) {
        frame = Color.BLACK;
        IExpr bg = optionValue(wrapper, S.Background);
        if (bg != null) {
          background = ColorUtil.parse(bg);
        }
        IExpr fs = optionValue(wrapper, S.FrameStyle);
        if (fs != null) {
          Color c = ColorUtil.parse(fs);
          if (c != null) {
            frame = c;
          } else if (fs.isNone()) {
            frame = null;
          }
        }
        content = wrapper.arg1();
      } else if (head == S.Style) {
        for (int i = 2; i <= wrapper.argSize(); i++) {
          collectDirectiveOnly(wrapper.get(i), textStyle);
        }
        content = wrapper.arg1();
      } else {
        break;
      }
    }

    double[] pos = ast.argSize() >= 2 ? pointOf(ast.arg2()) : new double[] {0, 0};
    double offsetX = 0;
    double offsetY = 0;
    if (ast.argSize() >= 3 && ast.arg3().isList()) {
      double[] off = pointOf(ast.arg3());
      offsetX = off[0];
      offsetY = off[1];
    }
    double dirX = 1;
    double dirY = 0;
    if (ast.argSize() >= 4 && ast.arg4().isList()) {
      double[] dir = pointOf(ast.arg4());
      if (dir[0] != 0 || dir[1] != 0) {
        dirX = dir[0];
        dirY = dir[1];
      }
    }
    primitives.add(new Prim2D.TextPrim(textOf(content), pos[0], pos[1], offsetX, offsetY, dirX,
        dirY, frame, background, textStyle));
  }

  /** The displayed form of a label. Strings lose their quotes; anything else prints as is. */
  private String textOf(IExpr expr) {
    if (expr.isString()) {
      return expr.toString();
    }
    return unquote(expr.toString());
  }

  private void collectInset(IAST ast, Style2D style) {
    if (ast.argSize() < 1) {
      return;
    }
    IExpr object = ast.arg1();
    if (!(object.isAST(S.Graphics) || object.isAST(S.Graphics3D))) {
      collectText(ast, style);
      return;
    }
    double[] pos = ast.argSize() >= 2 ? pointOf(ast.arg2()) : new double[] {0, 0};
    double w = imageWidth / 3.0;
    double h = imageWidth / 3.0;
    if (ast.argSize() >= 4 && ast.arg4().isList()) {
      double[] size = pointOf(ast.arg4());
      if (size[0] > 0) {
        w = size[0];
      }
      if (size[1] > 0) {
        h = size[1];
      }
    }
    // the inset is placed at the size worked out above, so it must not resize itself
    SvgGraphics2D sub = new SvgGraphics2D(w, h, true);
    String svg = sub.toSVG((IAST) object, true);
    if (svg == null) {
      return;
    }
    double alignX = w / 2.0;
    double alignY = h / 2.0;
    if (ast.argSize() >= 3 && ast.arg3().isAST(S.ImageScaled)) {
      IExpr inner = ((IAST) ast.arg3()).arg1();
      if (inner.isList()) {
        double[] rel = pointOf(inner);
        alignX = rel[0] * w;
        alignY = (1.0 - rel[1]) * h;
      }
    }
    primitives.add(new Prim2D.InsetPrim(svg, pos[0], pos[1], w, h, alignX, alignY, style.clone()));
  }

  private void collectBezier(IAST ast, Style2D style, boolean filled) {
    if (ast.argSize() < 1) {
      return;
    }
    int degree = 3;
    IExpr d = optionValue(ast, S.SplineDegree);
    if (d != null) {
      degree = d.toIntDefault(3);
    } else if (ast.argSize() >= 2 && ast.arg2().isInteger()) {
      degree = ast.arg2().toIntDefault(3);
    }
    for (List<double[]> seg : segmentsOf(ast.arg1())) {
      if (!seg.isEmpty()) {
        primitives.add(new Prim2D.BezierPrim(seg, degree, filled, style.clone()));
      }
    }
  }

  private void collectBSpline(IAST ast, Style2D style, boolean filled) {
    if (ast.argSize() < 1) {
      return;
    }
    List<double[]> control = pointsOf(ast.arg1());
    if (control.size() < 2) {
      return;
    }
    int degree = 3;
    IExpr d = optionValue(ast, S.SplineDegree);
    if (d != null) {
      degree = Math.max(1, d.toIntDefault(3));
    }
    boolean closed = false;
    IExpr c = optionValue(ast, S.SplineClosed);
    if (c != null && c.isTrue()) {
      closed = true;
    }
    double[] weights = null;
    IExpr w = optionValue(ast, S.SplineWeights);
    if (w != null && w.isList()) {
      IAST wl = (IAST) w;
      weights = new double[control.size()];
      Arrays.fill(weights, 1.0);
      for (int i = 0; i < Math.min(weights.length, wl.argSize()); i++) {
        weights[i] = ColorUtil.dbl(wl.get(i + 1), 1.0);
      }
    }
    List<double[]> curve = BSpline.evaluate(control, degree, closed, weights);
    if (curve.size() >= 2) {
      primitives.add(new Prim2D.BSplinePrim(curve, closed, filled, style.clone()));
    }
  }

  private void collectFilledCurve(IAST ast, Style2D style) {
    if (ast.argSize() < 1) {
      return;
    }
    // the components of a filled curve join into one closed region
    List<double[]> merged = new ArrayList<>();
    collectCurveComponents(ast.arg1(), merged);
    if (merged.size() >= 3) {
      primitives.add(new Prim2D.PolygonPrim(merged, style.clone()));
    }
  }

  private void collectCurveComponents(IExpr expr, List<double[]> out) {
    if (expr.isList()) {
      IAST list = (IAST) expr;
      for (int i = 1; i <= list.argSize(); i++) {
        collectCurveComponents(list.get(i), out);
      }
      return;
    }
    if (!expr.isAST() || !expr.head().isBuiltInSymbol()) {
      return;
    }
    IAST ast = (IAST) expr;
    switch (((IBuiltInSymbol) ast.head()).ordinal()) {
      case ID.Line:
      case ID.BezierCurve:
      case ID.BSplineCurve:
        if (ast.argSize() >= 1) {
          for (List<double[]> seg : segmentsOf(ast.arg1())) {
            out.addAll(seg);
          }
        }
        break;
      default:
        break;
    }
  }

  private void collectHalfPlane(IAST ast, Style2D style, boolean full, boolean lineOnly) {
    if (ast.argSize() < 1) {
      return;
    }
    double[] p;
    double[] v;
    IExpr first = ast.arg1();
    if (listDepth(first) >= 2) {
      List<double[]> pts = pointsOf(first);
      if (pts.size() < 2) {
        return;
      }
      p = pts.get(0);
      v = new double[] {pts.get(1)[0] - p[0], pts.get(1)[1] - p[1]};
    } else {
      p = pointOf(first);
      v = ast.argSize() >= 2 ? pointOf(ast.arg2()) : new double[] {1, 0};
    }
    double[] w = {0, 0};
    if (!lineOnly && !full && ast.argSize() >= 2) {
      w = pointOf(ast.arg2());
    }
    primitives.add(new Prim2D.HalfPlanePrim(p[0], p[1], v[0], v[1], w[0], w[1], full, lineOnly,
        style.clone()));
  }

  private void collectRaster(IAST ast, Style2D style) {
    if (ast.argSize() < 1 || !ast.arg1().isList()) {
      return;
    }
    IAST rows = (IAST) ast.arg1();
    int rowCount = rows.argSize();
    if (rowCount == 0) {
      return;
    }
    // an explicit colour range rescales the values; the default is 0..1
    double vMin = 0;
    double vMax = 1;
    if (ast.argSize() >= 3 && ast.arg3().isList() && ((IAST) ast.arg3()).argSize() >= 2) {
      vMin = ColorUtil.dbl(((IAST) ast.arg3()).arg1(), 0);
      vMax = ColorUtil.dbl(((IAST) ast.arg3()).arg2(), 1);
    }
    double span = vMax - vMin;
    if (Math.abs(span) < 1e-15) {
      span = 1;
    }
    List<Color[]> grid = new ArrayList<>(rowCount);
    int width = 0;
    for (int r = 1; r <= rowCount; r++) {
      IExpr rowExpr = rows.get(r);
      if (!rowExpr.isList()) {
        continue;
      }
      IAST row = (IAST) rowExpr;
      Color[] cells = new Color[row.argSize()];
      for (int c = 1; c <= row.argSize(); c++) {
        IExpr cell = row.get(c);
        Color color = ColorUtil.parse(cell);
        if (color == null) {
          if (cell.isList()) {
            // {r, g, b} or {r, g, b, a} without an RGBColor head
            List<Double> comps = new ArrayList<>();
            IAST cl = (IAST) cell;
            for (int i = 1; i <= cl.argSize(); i++) {
              comps.add(ColorUtil.dbl(cl.get(i), 0));
            }
            if (comps.size() >= 3) {
              color = new Color(clampF(comps.get(0)), clampF(comps.get(1)), clampF(comps.get(2)),
                  comps.size() >= 4 ? clampF(comps.get(3)) : 1.0f);
            }
          } else {
            double g = (ColorUtil.dbl(cell, 0) - vMin) / span;
            color = new Color(clampF(g), clampF(g), clampF(g), 1.0f);
          }
        }
        cells[c - 1] = color == null ? Color.BLACK : color;
      }
      width = Math.max(width, cells.length);
      grid.add(cells);
    }
    if (grid.isEmpty() || width == 0) {
      return;
    }
    Color[][] cells = grid.toArray(new Color[0][]);
    double x1 = 0;
    double y1 = 0;
    double x2 = width;
    double y2 = cells.length;
    if (ast.argSize() >= 2 && ast.arg2().isList() && ((IAST) ast.arg2()).argSize() >= 2) {
      IAST rect = (IAST) ast.arg2();
      double[] a = pointOf(rect.arg1());
      double[] b = pointOf(rect.arg2());
      x1 = a[0];
      y1 = a[1];
      x2 = b[0];
      y2 = b[1];
    }
    // InterpolationOrder -> 0 is a grid of values and keeps its cell edges; anything higher says
    // the cells are samples of something continuous, and is drawn smoothly between them
    boolean smooth = false;
    for (int i = 2; i <= ast.argSize(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && ((IAST) arg).arg1() == S.InterpolationOrder) {
        smooth = ((IAST) arg).arg2().toIntDefault(0) > 0;
      }
    }
    primitives.add(new Prim2D.RasterPrim(cells, x1, y1, x2, y2, smooth, style.clone()));
  }

  private static float clampF(double v) {
    return (float) Math.max(0.0, Math.min(1.0, v));
  }

  // -------------------------------------------------------- point plumbing

  /** Nesting depth of a list expression: 0 for an atom, 1 for {@code {1,2}}, 2 for a point list. */
  private int listDepth(IExpr expr) {
    if (expr == null || !expr.isList()) {
      return 0;
    }
    IAST list = (IAST) expr;
    if (list.argSize() == 0) {
      return 1;
    }
    int max = 0;
    for (int i = 1; i <= list.argSize(); i++) {
      max = Math.max(max, listDepth(list.get(i)));
    }
    return 1 + max;
  }

  /** A single coordinate pair, resolving a {@code GraphicsComplex} index if one is in force. */
  private double[] pointOf(IExpr expr) {
    if (expr == null) {
      return new double[] {0, 0};
    }
    if (vertices != null && expr.isInteger()) {
      int idx = expr.toIntDefault(0);
      if (idx > 0 && idx <= vertices.size()) {
        double[] v = vertices.get(idx - 1);
        return new double[] {v[0], v[1]};
      }
      return new double[] {Double.NaN, Double.NaN};
    }
    if (expr.isList() && ((IAST) expr).argSize() >= 2) {
      IAST list = (IAST) expr;
      return new double[] {coordinate(list.arg1(), true), coordinate(list.arg2(), false)};
    }
    return new double[] {Double.NaN, Double.NaN};
  }

  /**
   * One coordinate of a position, which may be named rather than given as a number.
   *
   * <p>
   * A corner of the drawing area can be written with the words for it: {@code {Right, Bottom}}
   * means the same place as {@code {1, 0}} does in scaled coordinates. Read as a number those
   * words are nothing, and anything positioned with them was quietly dropped.
   *
   * @param horizontal whether this is the first coordinate, since {@code Center} is the middle of
   *        whichever direction it is used in and the other words only belong to one of them
   */
  private static double coordinate(IExpr expr, boolean horizontal) {
    if (expr.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) expr).ordinal()) {
        case ID.Left:
        case ID.Bottom:
          return 0.0;
        case ID.Right:
        case ID.Top:
          return 1.0;
        case ID.Center:
          return 0.5;
        case ID.Axis:
          return horizontal ? 0.5 : 0.0;
        default:
          break;
      }
    }
    return ColorUtil.dbl(expr, Double.NaN);
  }

  /** A flat list of coordinate pairs. */
  private List<double[]> pointsOf(IExpr expr) {
    List<double[]> out = new ArrayList<>();
    if (expr == null) {
      return out;
    }
    if (vertices != null && expr.isInteger()) {
      out.add(pointOf(expr));
      return out;
    }
    if (!expr.isList()) {
      return out;
    }
    IAST list = (IAST) expr;
    if (list.argSize() == 0) {
      return out;
    }
    if (vertices != null) {
      // inside a GraphicsComplex every leaf is an index
      for (int i = 1; i <= list.argSize(); i++) {
        IExpr item = list.get(i);
        if (item.isList()) {
          out.addAll(pointsOf(item));
        } else {
          out.add(pointOf(item));
        }
      }
      return out;
    }
    if (listDepth(list) <= 1) {
      // a single coordinate pair
      double[] p = pointOf(list);
      if (isFinite(p)) {
        out.add(p);
      }
      return out;
    }
    for (int i = 1; i <= list.argSize(); i++) {
      IExpr item = list.get(i);
      if (listDepth(item) >= 2) {
        out.addAll(pointsOf(item));
      } else {
        double[] p = pointOf(item);
        if (isFinite(p)) {
          out.add(p);
        }
      }
    }
    return out;
  }

  /**
   * One or more point sequences. {@code {{0,0},{1,1}}} is a single segment, while
   * {@code {{{0,0},{1,1}}, {{2,2},{3,3}}}} is two.
   */
  private List<List<double[]>> segmentsOf(IExpr expr) {
    List<List<double[]>> out = new ArrayList<>();
    if (expr == null || !expr.isList()) {
      return out;
    }
    IAST list = (IAST) expr;
    if (list.argSize() == 0) {
      return out;
    }
    boolean multi;
    if (vertices != null) {
      // {1, 2, 3} is one segment of indices; {{1,2},{3,4}} is two
      multi = list.arg1().isList();
    } else {
      multi = listDepth(list) >= 3;
    }
    if (multi) {
      for (int i = 1; i <= list.argSize(); i++) {
        List<double[]> seg = pointsOf(list.get(i));
        if (!seg.isEmpty()) {
          out.add(seg);
        }
      }
    } else {
      List<double[]> seg = pointsOf(list);
      if (!seg.isEmpty()) {
        out.add(seg);
      }
    }
    return out;
  }

  private static boolean isFinite(double[] p) {
    return p != null && p.length >= 2 && Double.isFinite(p[0]) && Double.isFinite(p[1]);
  }

  /** The value of an option rule among the arguments of {@code ast}, or {@code null}. */
  static IExpr optionValue(IAST ast, ISymbol name) {
    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && arg.first().equals(name)) {
        return ((IAST) arg).second();
      }
    }
    return null;
  }

  static String unquote(String s) {
    return s.replace("\"", "");
  }

  private static String fmtDash(double[] values) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < values.length; i++) {
      if (i > 0) {
        sb.append(",");
      }
      sb.append(String.format(Locale.US, "%.2f", Math.max(0.0, values[i])));
    }
    return sb.toString();
  }
}
