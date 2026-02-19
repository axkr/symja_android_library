package org.matheclipse.core.graphics;

import static j2html.TagCreator.tag;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

import j2html.tags.DomContent;
import j2html.tags.UnescapedText;

public class SVGGraphics3D {

  private static class Vector3 {
    double x, y, z;

    Vector3(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    Vector3 add(Vector3 v) {
      return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    Vector3 sub(Vector3 v) {
      return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    Vector3 scale(double s) {
      return new Vector3(x * s, y * s, z * s);
    }

    double dot(Vector3 v) {
      return x * v.x + y * v.y + z * v.z;
    }

    Vector3 cross(Vector3 v) {
      return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    Vector3 normalize() {
      double len = Math.sqrt(x * x + y * y + z * z);
      if (len == 0)
        return new Vector3(0, 0, 0);
      return new Vector3(x / len, y / len, z / len);
    }
  }

  private static class Matrix4 {
    double[] val = new double[16];

    Matrix4() {
      identity();
    }

    void identity() {
      for (int i = 0; i < 16; i++)
        val[i] = 0;
      val[0] = val[5] = val[10] = val[15] = 1;
    }

    static Matrix4 lookAt(Vector3 eye, Vector3 center, Vector3 up) {
      Vector3 f = center.sub(eye).normalize();
      Vector3 s = f.cross(up).normalize();
      Vector3 u = s.cross(f);

      Matrix4 m = new Matrix4();
      m.val[0] = s.x;
      m.val[4] = s.y;
      m.val[8] = s.z;
      m.val[1] = u.x;
      m.val[5] = u.y;
      m.val[9] = u.z;
      m.val[2] = -f.x;
      m.val[6] = -f.y;
      m.val[10] = -f.z;
      m.val[12] = -s.dot(eye);
      m.val[13] = -u.dot(eye);
      m.val[14] = f.dot(eye);
      return m;
    }

    Vector3 project(Vector3 v) {
      double x = v.x * val[0] + v.y * val[4] + v.z * val[8] + val[12];
      double y = v.x * val[1] + v.y * val[5] + v.z * val[9] + val[13];
      double z = v.x * val[2] + v.y * val[6] + v.z * val[10] + val[14];
      // We skip w and perspective division for simple orthographic/isometric like
      // projection if ViewPoint is far?
      // Actually Mathematica defaults to a perspective like view.
      // Let's assume w=1 for now unless we implement full perspective.
      // For full perspective we need full 4D multiplication.
      return new Vector3(x, y, z);
    }
  }

  private static class RenderState implements Cloneable {
    Color color = Color.BLACK;
    double opacity = 1.0;
    double thickness = 1.0;
    boolean dashed = false;

    @Override
    public RenderState clone() {
      try {
        return (RenderState) super.clone();
      } catch (CloneNotSupportedException e) {
        return new RenderState();
      }
    }
  }

  private static abstract class Renderable implements Comparable<Renderable> {
    double zDepth; // Average Z for sorting

    abstract DomContent toSVG();

    @Override
    public int compareTo(Renderable o) {
      // Sort so that furthest (smallest Z in typical Opengl, but here we likely want
      // Painter's algo)
      // Let's assume standard LookAt, Camera is at +Z relative to LookAt. Points with
      // higher Z are closer?
      // In LookAt matrix: +Z is out of screen (Right Handed).
      // So larger Z is closer. We want to draw smaller Z first.
      return Double.compare(this.zDepth, o.zDepth);
    }
  }

  private static class RenderableLine extends Renderable {
    List<Vector3> points;
    RenderState state;

    RenderableLine(List<Vector3> points, RenderState state) {
      this.points = points;
      this.state = state;
      double sumZ = 0;
      for (Vector3 p : points)
        sumZ += p.z;
      this.zDepth = points.isEmpty() ? 0 : sumZ / points.size();
    }

    @Override
    DomContent toSVG() {
      if (points.size() < 2)
        return new UnescapedText("");
      StringBuilder sb = new StringBuilder();
      for (Vector3 p : points) {
        sb.append(String.format(Locale.US, "%.2f,%.2f ", p.x, p.y));
      }
      return tag("polyline").attr("points", sb.toString())
          .attr("fill", "none")
          .attr("stroke", colorToHex(state.color))
          .attr("stroke-width", Math.max(1, state.thickness))
          .condAttr(state.opacity < 1.0, "stroke-opacity", String.valueOf(state.opacity))
          .condAttr(state.dashed, "stroke-dasharray", "4,4");
    }
  }

  private static class RenderablePolygon extends Renderable {
    List<Vector3> points;
    RenderState state;

    RenderablePolygon(List<Vector3> points, RenderState state) {
      this.points = points;
      this.state = state;
      double sumZ = 0;
      for (Vector3 p : points)
        sumZ += p.z;
      this.zDepth = points.isEmpty() ? 0 : sumZ / points.size();
    }

    @Override
    DomContent toSVG() {
      if (points.size() < 3)
        return new UnescapedText("");
      StringBuilder sb = new StringBuilder();
      for (Vector3 p : points) {
        sb.append(String.format(Locale.US, "%.2f,%.2f ", p.x, p.y));
      }
      return tag("polygon").attr("points", sb.toString())
          .attr("fill", colorToHex(state.color))
          .condAttr(state.opacity < 1.0, "fill-opacity", String.valueOf(state.opacity))
          .attr("stroke", "none");
    }
  }

  private static class RenderablePoint extends Renderable {
    Vector3 point;
    RenderState state;

    RenderablePoint(Vector3 point, RenderState state) {
      this.point = point;
      this.state = state;
      this.zDepth = point.z;
    }

    @Override
    DomContent toSVG() {
      double r = Math.max(2.0, state.thickness * 2); // heuristic radius
      return tag("circle")
          .attr("cx", String.format(Locale.US, "%.2f", point.x))
          .attr("cy", String.format(Locale.US, "%.2f", point.y))
          .attr("r", String.format(Locale.US, "%.2f", r))
          .attr("fill", colorToHex(state.color))
          .attr("fill-opacity", String.format(Locale.US, "%.2f", state.opacity));
    }
  }

  private static class RenderableText extends Renderable {
    Vector3 point;
    String text;
    RenderState state;
    String anchor = "middle"; // start, middle, end

    RenderableText(Vector3 point, String text, RenderState state, String anchor) {
      this.point = point;
      this.text = text;
      this.state = state;
      this.anchor = anchor;
      this.zDepth = point.z; // - 100? Text usually should be on top?
      // Ideally text doesn't depth sort with geometry strictly if it's axis label.
      // Keep zDepth as point.z.
    }

    @Override
    DomContent toSVG() {
      return tag("text")
          .attr("x", String.format(Locale.US, "%.2f", point.x))
          .attr("y", String.format(Locale.US, "%.2f", point.y + 4))
          .attr("fill", colorToHex(state.color))
          .attr("fill-opacity", String.format(Locale.US, "%.2f", state.opacity))
          .attr("font-family", "sans-serif")
          .attr("font-size", "12")
          .attr("text-anchor", anchor)
          .withText(text);
    }
  }

  private static class ComplexContext {
    List<Vector3> transformedPoints;

    ComplexContext(List<Vector3> pts) {
      this.transformedPoints = pts;
    }
  }

  public static String toSVG(IAST graphics3D) {

    // 1. Parse Options
    Vector3 viewPoint = new Vector3(1.3, -2.4, 2);
    Vector3 viewVertical = new Vector3(0, 0, 1);
    double[] boxRatios = {1.0, 1.0, 1.0};

    IExpr vpOpt = extractOption(graphics3D, "ViewPoint");
    if (vpOpt != null && vpOpt.isList())
      viewPoint = parseVector((IAST) vpOpt, viewPoint);

    Vector3 viewCenterRel = new Vector3(0.5, 0.5, 0.5); 
    IExpr vcOpt = extractOption(graphics3D, "ViewCenter");
    if (vcOpt != null && vcOpt.isList()) {
    	viewCenterRel = parseVector((IAST) vcOpt, viewCenterRel);
    }
    
    IExpr vvOpt = extractOption(graphics3D, "ViewVertical");
    if (vvOpt != null && vvOpt.isList())
      viewVertical = parseVector((IAST) vvOpt, viewVertical);

    IExpr brOpt = extractOption(graphics3D, "BoxRatios");
    if (brOpt != null && brOpt.isList()) {
      boxRatios[0] = getDouble(((IAST) brOpt).arg1(), 1.0);
      boxRatios[1] = getDouble(((IAST) brOpt).arg2(), 1.0);
      boxRatios[2] = getDouble(((IAST) brOpt).arg3(), 1.0);
    }
    
    // Axes Option
    boolean[] showAxes = {false, false, false};
    IExpr axesOpt = extractOption(graphics3D, "Axes");
    if (axesOpt != null) {
    	if (axesOpt.isTrue()) {
    		showAxes[0] = showAxes[1] = showAxes[2] = true;
    	} else if (axesOpt.isList() && ((IAST)axesOpt).size() >= 4) {
    		// Axes -> {x, y, z}
    		showAxes[0] = !((IAST)axesOpt).get(1).isFalse();
    		showAxes[1] = !((IAST)axesOpt).get(2).isFalse();
    		showAxes[2] = !((IAST)axesOpt).get(3).isFalse();
    	}
    }

    // 2. Pre-scan for Bounding Box (needed for normalization)
    // Assuming GraphicsComplex contains all points.
    List<Vector3> allPointsRaw = new ArrayList<>();
    collectPoints(graphics3D, allPointsRaw);

    Vector3 min = new Vector3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    Vector3 max = new Vector3(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
    for (Vector3 p : allPointsRaw) {
      min.x = Math.min(min.x, p.x);
      min.y = Math.min(min.y, p.y);
      min.z = Math.min(min.z, p.z);
      max.x = Math.max(max.x, p.x);
      max.y = Math.max(max.y, p.y);
      max.z = Math.max(max.z, p.z);
    }

    if (allPointsRaw.isEmpty()) {
      min = new Vector3(-1, -1, -1);
      max = new Vector3(1, 1, 1);
    }

    // Calculate center based on ViewCenter option (relative to bounding box)
    double bboxX = max.x - min.x; if (bboxX == 0) bboxX = 1.0;
    double bboxY = max.y - min.y; if (bboxY == 0) bboxY = 1.0;
    double bboxZ = max.z - min.z; if (bboxZ == 0) bboxZ = 1.0;
    
    Vector3 center = new Vector3(
    		min.x + viewCenterRel.x * bboxX, 
    		min.y + viewCenterRel.y * bboxY, 
    		min.z + viewCenterRel.z * bboxZ);
    
    double maxDim = Math.max(bboxX, Math.max(bboxY, bboxZ));

    // 3. Compute Transform Matrix
    // Standardize points to [-1, 1] box based on boxRatios
    // Or simpler: Transform World -> Camera.

    // ViewPoint is usually defined in "View Coordinates" where the bounding box is Scale dependent.
    // Let's construct a View Matrix that looks at the center of the bounding box.

    // "Camera" Position in World Space:
    // Mathematica ViewPoint {1.3, -2.4, 2} means relative to the bounding box.
    // Formula: C = Center + ViewPoint * (Size/2)? Not exactly.

    // Let's implement a simple LookAt from ViewPoint (scaled) to Center.
    Vector3 eye = new Vector3(center.x + viewPoint.x * maxDim, // simplistic approximation
        center.y + viewPoint.y * maxDim, center.z + viewPoint.z * maxDim);
    // If ViewPoint coordinates are relative to "Bounding Box center is 0,0,0 and radius 1"?
    // Just using center + viewPoint * maxDim/2 might work for reasonably centered plots.

    Matrix4 viewMatrix = Matrix4.lookAt(eye, center, viewVertical);

    // 4. Collect Renderables
    List<Renderable> renderables = new ArrayList<>();
    RenderState state = new RenderState();

    // Process Graphics Data
    // Use a recursive function that transforms points on the fly or looks them up from Context
    processExpr(graphics3D.arg1(), state, null, renderables, viewMatrix, boxRatios);
    
    // Add Axes
    RenderState axesState = new RenderState();
    axesState.color = Color.BLACK;
    axesState.thickness = 1.0;
    
    // Boxed Option
    boolean boxed = true;
    IExpr boxedOpt = extractOption(graphics3D, "Boxed");
    if (boxedOpt != null && boxedOpt.isFalse()) boxed = false;

    if (boxed) {
    	createBox(min, max, viewMatrix, renderables);
    }
    
    IExpr axesEdgeOpt = extractOption(graphics3D, "AxesEdge");
    createAxes(min, max, viewMatrix, renderables, showAxes, eye, axesEdgeOpt);

    // 5. Sort
    Collections.sort(renderables);

    // 6. Generate SVG in Pixel Coordinates
    int width = 400;
    int height = 400;
    // padding in pixels
    double padding = 20.0;

    // Collect unique points to compute bounds and transform
    java.util.Set<Vector3> uniquePoints =
        Collections.newSetFromMap(new java.util.IdentityHashMap<>());
    for (Renderable r : renderables) {
      if (r instanceof RenderablePolygon) {
        uniquePoints.addAll(((RenderablePolygon) r).points);
      } else if (r instanceof RenderableLine) {
        uniquePoints.addAll(((RenderableLine) r).points);
      } else if (r instanceof RenderablePoint) {
        uniquePoints.add(((RenderablePoint) r).point);
      } else if (r instanceof RenderableText) {
        uniquePoints.add(((RenderableText) r).point);
      }
    }

    double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
    double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

    if (uniquePoints.isEmpty()) {
      minX = -1;
      maxX = 1;
      minY = -1;
      maxY = 1;
    } else {
      for (Vector3 p : uniquePoints) {
        minX = Math.min(minX, p.x);
        maxX = Math.max(maxX, p.x);
        minY = Math.min(minY, p.y);
        maxY = Math.max(maxY, p.y);
      }
    }

    double rangeX = maxX - minX;
    double rangeY = maxY - minY;
    if (rangeX == 0)
      rangeX = 1.0;
    if (rangeY == 0)
      rangeY = 1.0;

    // Scale to fit width/height with padding
    double scaleX = (width - 2 * padding) / rangeX;
    double scaleY = (height - 2 * padding) / rangeY;
    double scale = Math.min(scaleX, scaleY);

    // Center alignment
    double contentWidth = rangeX * scale;
    double contentHeight = rangeY * scale;
    double shiftX = padding + (width - 2 * padding - contentWidth) / 2.0;
    double shiftY = padding + (height - 2 * padding - contentHeight) / 2.0;

    // Transform all points to Pixel Coordinates
    // Math Y is Up. SVG Y is Down.
    // y_svg = height - ( (y_math - minY) * scale + shiftY )
    for (Vector3 p : uniquePoints) {
      p.x = (p.x - minX) * scale + shiftX;
      p.y = height - ((p.y - minY) * scale + shiftY);
    }

    final int w = width;
    final int h = height;
    List<DomContent> content = new ArrayList<>();
    for (Renderable r : renderables) {
      content.add(r.toSVG());
    }

    return tag("svg")
        .with(content)
        .attr("xmlns", "http://www.w3.org/2000/svg")
        .attr("width", w)
        .attr("height", h)
        .attr("viewBox", "0 0 " + w + " " + h)
        .render();
  }

  private static void processExpr(IExpr expr, RenderState state, ComplexContext context,
      List<Renderable> renderables, Matrix4 viewMatrix, double[] boxRatios) {
    if (expr.isList()) {
      RenderState scopedState = state.clone();
      IAST list = (IAST) expr;
      for (int i = 1; i < list.size(); i++) {
        processExpr(list.get(i), scopedState, context, renderables, viewMatrix, boxRatios);
      }
      return;
    }

    if (expr.isBuiltInSymbol()) {
      processSymbol((IBuiltInSymbol) expr, state);
      return;
    }

    if (!expr.isAST())
      return;

    IAST ast = (IAST) expr;
    IExpr head = ast.head();

    if (head.equals(S.GraphicsComplex)) {
      // GraphicsComplex[points, primitives]
      if (ast.argSize() >= 2) {
        IExpr ptsArgs = ast.arg1();
        if (ptsArgs.isList()) {
          IAST ptsList = (IAST) ptsArgs;
          List<Vector3> tfPts = new ArrayList<>(ptsList.size());
          tfPts.add(null); // 1-based indexing
          for (int i = 1; i < ptsList.size(); i++) {
            Vector3 p = parseVector((IAST) ptsList.get(i), new Vector3(0, 0, 0));
            // Apply BoxRatios?
            // p.x *= boxRatios[0]; ... // BoxRatios are usually applied to the unit cube mapping

            Vector3 proj = viewMatrix.project(p);
            tfPts.add(proj);
          }
          ComplexContext newContext = new ComplexContext(tfPts);
          processExpr(ast.arg2(), state, newContext, renderables, viewMatrix, boxRatios);
        }
      }
    } else if (head.equals(S.Line)) {
      IExpr arg = ast.arg1();
      if (arg.isList()) {
        IAST list = (IAST) arg;
        // Check if it is Line[{p1, p2...}] or Line[{{p1, p2}, {p3, p4}}] (multi-line)
        if (list.size() > 1 && list.arg1().isList() && !isVector3(list.arg1())) {
          // Multi-line
          for (int i = 1; i < list.size(); i++) {
            createLine(list.get(i), state, context, renderables, viewMatrix);
          }
        } else {
          createLine(list, state, context, renderables, viewMatrix);
        }
      }
		} else if (head.equals(S.Polygon)) {
			IExpr arg = ast.arg1();
			if (arg.isList()) {
				IAST list = (IAST) arg;
				boolean isMulti = false;
				if (list.size() > 1 && list.arg1().isList()) {
					IAST firstPolyOrPoint = (IAST) list.arg1();
					if (firstPolyOrPoint.size() > 1) {
						IExpr firstElem = firstPolyOrPoint.arg1();
						if (context != null) {
							// In GraphicsComplex, List of Integers = Polygon of Indices -> MultiPolygon
							// List of Reals/Lists = Point -> Single Polygon
							if (firstElem.isInteger()) {
								isMulti = true;
							}
						} else {
							// In Raw graphics
							// List of Lists = Polygon -> MultiPolygon
							// List of Scalars = Point -> Single Polygon
							if (firstElem.isList()) {
								isMulti = true;
							}
						}
					}
				}
				
				if (isMulti) {
					// Multi-polygon (list of lists of indices or points)
					for(int i=1; i<list.size(); i++) {
						createPolygon(list.get(i), state, context, renderables, viewMatrix);
					}
				} else {
					createPolygon(list, state, context, renderables, viewMatrix);
				}
			}
		} else if (head.equals(S.Point)) {
      IExpr arg = ast.arg1();
      if (arg.isList()) {
        IAST list = (IAST) arg;
        if (list.size() > 1 && list.arg1().isList() && !isVector3(list.arg1())) {
          for (int i = 1; i < list.size(); i++) {
            createPoint(list.get(i), state, context, renderables, viewMatrix);
          }
        } else {
          createPoint(list, state, context, renderables, viewMatrix);
        }
      }
    } else if (head.equals(S.GraphicsGroup)) {
      processExpr(ast.arg1(), state, context, renderables, viewMatrix, boxRatios);
    }
    // Handle Styles
    else if (head.equals(S.RGBColor) || head.equals(S.Hue) || head.equals(S.GrayLevel)
        || head.equals(S.CMYKColor)) {
      state.color = parseColor(ast);
    } else if (head.equals(S.Opacity)) {
      state.opacity = getDouble(ast.arg1(), 1.0);
    } else if (head.equals(S.Thickness) || head.equals(S.AbsoluteThickness)) {
      state.thickness =
          getDouble(ast.arg1(), 1.0) * ((head.equals(S.AbsoluteThickness)) ? 1.0 : 500.0); // Rough
                                                                                           // scaling
    } else if (head.equals(S.Dashed)) {
      state.dashed = true;
    }
  }

  private static boolean isElementIndex(IExpr expr) {
    return expr.isInteger();
  }

  private static boolean isVector3(IExpr expr) {
    if (expr.isList()) {
      return ((IAST) expr).size() >= 4; // {x,y,z} size 4 (head + 3 args)
    }
    return false;
  }

  private static void createLine(IExpr data, RenderState state, ComplexContext context,
      List<Renderable> renderables, Matrix4 mat) {
    List<Vector3> pts = resolvePoints(data, context, mat);
    if (!pts.isEmpty()) {
      renderables.add(new RenderableLine(pts, state.clone()));
    }
  }

  private static void createPolygon(IExpr data, RenderState state, ComplexContext context,
      List<Renderable> renderables, Matrix4 mat) {
    List<Vector3> pts = resolvePoints(data, context, mat);
    if (!pts.isEmpty()) {
      RenderState lightedState = state.clone();
      lightedState.color = calculateLighting(state.color, pts);
      renderables.add(new RenderablePolygon(pts, lightedState));
    }
  }

  private static void createPoint(IExpr data, RenderState state, ComplexContext context,
      List<Renderable> renderables, Matrix4 mat) {
    // Point can be single coord or list of coords.
    // Using resolvePoints handles List of Indices or List of Vectors.
    // Wait, Point[{1,2}] means Indices 1,2 -> 2 points.
    // Point[{x,y,z}] means 1 point.
    // My resolvePoints logic creates a list of points from the input.

    if (data.isList() && isVector3(data)) {
      // Single point {x,y,z}
      Vector3 p = parseVector((IAST) data, new Vector3(0, 0, 0));
      renderables.add(new RenderablePoint(mat.project(p), state.clone()));
    } else {
      List<Vector3> pts = resolvePoints(data, context, mat);
      for (Vector3 p : pts) {
        renderables.add(new RenderablePoint(p, state.clone()));
      }
    }
  }

  private static List<Vector3> resolvePoints(IExpr data, ComplexContext context, Matrix4 mat) {
    List<Vector3> result = new ArrayList<>();
    if (data.isList()) {
      IAST list = (IAST) data;
      for (int i = 1; i < list.size(); i++) {
        IExpr el = list.get(i);
        if (el.isInteger() && context != null && context.transformedPoints != null) {
          int idx = el.toIntDefault(0);
          if (idx > 0 && idx < context.transformedPoints.size()) {
            result.add(context.transformedPoints.get(idx));
          }
        } else if (el.isList() && ((IAST) el).size() >= 4) {
          Vector3 p = parseVector((IAST) el, new Vector3(0, 0, 0));
          result.add(mat.project(p));
        }
      }
    }
    return result;
  }

  private static void collectPoints(IExpr expr, List<Vector3> points) {
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.head().equals(S.GraphicsComplex)) {
        if (ast.argSize() >= 1 && ast.arg1().isList()) {
          IAST pts = (IAST) ast.arg1();
          for (int i = 1; i < pts.size(); i++) {
            points.add(parseVector((IAST) pts.get(i), new Vector3(0, 0, 0)));
          }
        }
      } else {
        for (int i = 1; i < ast.size(); i++) {
          collectPoints(ast.get(i), points);
        }
      }
    }
  }

  private static Vector3 parseVector(IAST list, Vector3 def) {
    if (list.size() >= 4) {
      return new Vector3(getDouble(list.get(1), def.x), getDouble(list.get(2), def.y),
          getDouble(list.get(3), def.z));
    }
    return def;
  }

  private static double getDouble(IExpr expr, double def) {
    try {
      if (expr instanceof INumber)
        return ((INumber) expr).reDoubleValue();
      return expr.evalf();
    } catch (RuntimeException e) {
      return def;
    }
  }

  private static IExpr extractOption(IAST ast, String optionName) {
    // iterate from end or check all args for Rule/RuleDelayed
    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && ((IAST) arg).arg1().toString().equals(optionName)) {
        return ((IAST) arg).arg2();
      } else if (arg.isList()) {
        // Options can be in a list? Usually options are flat sequence at end.
        // But sometimes {Opt->Val, ...}
        // simplify - only check top level or flattening.
        IExpr res = extractOption((IAST) arg, optionName);
        if (res != null)
          return res;
      }
    }
    return null;
  }

  private static String colorToHex(Color c) {
    return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
  }

  private static void processSymbol(IBuiltInSymbol sym, RenderState state) {
    // Copy from WebGLGraphics3D...
    int id = sym.ordinal();
    if (id == ID.Red)
      state.color = Color.RED;
    else if (id == ID.Green)
      state.color = Color.GREEN;
    else if (id == ID.Blue)
      state.color = Color.BLUE;
    else if (id == ID.Black)
      state.color = Color.BLACK;
    else if (id == ID.White)
      state.color = Color.WHITE;
    // ... etc
  }

  private static Color parseColor(IAST ast) {
    // Simply reuse code logic or call WebGLGraphics3D logic if visible?
    // Use local simplified version.
    IBuiltInSymbol head = (IBuiltInSymbol) ast.head();
    if (head.equals(S.RGBColor))
      return new Color(clamp(getDouble(ast.arg1(), 0)), clamp(getDouble(ast.arg2(), 0)),
          clamp(getDouble(ast.arg3(), 0)));
    return Color.BLACK;
  }


	private static void createBox(Vector3 min, Vector3 max, Matrix4 mat, List<Renderable> renderables) {
		RenderState boxState = new RenderState();
		boxState.color = Color.GRAY;
		boxState.thickness = 0.5;
		// 12 edges
		Vector3[] p = new Vector3[8];
		for(int i=0; i<8; i++) {
			double x = ((i & 1) == 0) ? min.x : max.x;
			double y = ((i & 2) == 0) ? min.y : max.y;
			double z = ((i & 4) == 0) ? min.z : max.z;
			p[i] = mat.project(new Vector3(x, y, z));
		}
		int[][] edges = {{0,1}, {0,2}, {0,4}, {1,3}, {1,5}, {2,3}, {2,6}, {4,5}, {4,6}, {3,7}, {5,7}, {6,7}};
		for(int[] e : edges) {
			List<Vector3> pts = new ArrayList<>();
			pts.add(p[e[0]]);
			pts.add(p[e[1]]);
			renderables.add(new RenderableLine(pts, boxState));
		}
	}

	private static void createAxes(Vector3 min, Vector3 max, Matrix4 mat, List<Renderable> renderables,
			boolean[] showAxes, Vector3 eye, IExpr axesEdgeOpt) {
		RenderState axesState = new RenderState();
		axesState.color = Color.BLACK;
		axesState.thickness = 1.0;
		RenderState textState = new RenderState();
		textState.color = Color.BLACK;
		textState.opacity = 1.0;

		// 1. Determine Origin (furthest corner)
		Vector3[] corners = { new Vector3(min.x, min.y, min.z), new Vector3(max.x, min.y, min.z),
				new Vector3(min.x, max.y, min.z), new Vector3(min.x, min.y, max.z), new Vector3(max.x, max.y, min.z),
				new Vector3(max.x, min.y, max.z), new Vector3(min.x, max.y, max.z),
				new Vector3(max.x, max.y, max.z) };

		Vector3 defaultOrigin = min;
		double maxDistSq = -1.0;

		for (Vector3 c : corners) {
			double d2 = (c.x - eye.x) * (c.x - eye.x) + (c.y - eye.y) * (c.y - eye.y) + (c.z - eye.z) * (c.z - eye.z);
			if (d2 > maxDistSq) {
				maxDistSq = d2;
				defaultOrigin = c;
			}
		}
		
		Vector3 xOrigin = defaultOrigin;
		Vector3 yOrigin = defaultOrigin;
		Vector3 zOrigin = defaultOrigin;

		// Preserve previous fix: Default Y axis at max Z if using automatic/default
		boolean customY = false;

		if (axesEdgeOpt != null && axesEdgeOpt.isList()) {
			IAST list = (IAST) axesEdgeOpt;
			if (list.size() >= 4) {
				// X Axis Spec {dirY, dirZ}
				IExpr xSpec = list.get(1);
				if (xSpec.toString().equals("None")) {
					showAxes[0] = false;
				} else if (xSpec.isList()) {
					xOrigin = resolveOrigin(xOrigin, (IAST) xSpec, 1, 2, min, max);
				}

				// Y Axis Spec {dirX, dirZ}
				IExpr ySpec = list.get(2);
				if (ySpec.toString().equals("None")) {
					showAxes[1] = false;
				} else if (ySpec.isList()) {
					customY = true;
					yOrigin = resolveOrigin(yOrigin, (IAST) ySpec, 0, 2, min, max);
				}

				// Z Axis Spec {dirX, dirY}
				IExpr zSpec = list.get(3);
				if (zSpec.toString().equals("None")) {
					showAxes[2] = false;
				} else if (zSpec.isList()) {
					zOrigin = resolveOrigin(zOrigin, (IAST) zSpec, 0, 1, min, max);
				}
			}
		}

		if (!customY) {
			// Apply the fix requested previously: Y ticks on upper edge (max.z)
			yOrigin = new Vector3(yOrigin.x, yOrigin.y, max.z);
		}

		// Helper to generate linear ticks
		int numTicks = 5;

		if (showAxes[0]) {
			// X Axis
			double start = min.x;
			double end = max.x;
			Vector3 pStart = mat.project(new Vector3(start, xOrigin.y, xOrigin.z));
			Vector3 pEnd = mat.project(new Vector3(end, xOrigin.y, xOrigin.z));
			List<Vector3> pts = new ArrayList<>();
			pts.add(pStart);
			pts.add(pEnd);
			renderables.add(new RenderableLine(pts, axesState));
			
			// Ticks
			double minVal = min.x;
			double maxVal = max.x;
			double step = (maxVal - minVal) / (numTicks - 1);
			for (int i = 0; i < numTicks; i++) {
				double val = minVal + i * step;
				Vector3 pos = new Vector3(val, xOrigin.y, xOrigin.z);
				Vector3 pPos = mat.project(pos);
				renderables.add(new RenderableText(pPos, String.format(Locale.US, "%.1f", val), textState, "middle"));
			}
		}
		
		if (showAxes[1]) {
			// Y Axis
			double start = min.y;
			double end = max.y;
			Vector3 pStart = mat.project(new Vector3(yOrigin.x, start, yOrigin.z));
			Vector3 pEnd = mat.project(new Vector3(yOrigin.x, end, yOrigin.z));
			List<Vector3> pts = new ArrayList<>();
			pts.add(pStart);
			pts.add(pEnd);
			renderables.add(new RenderableLine(pts, axesState));
			
			double minVal = min.y;
			double maxVal = max.y;
			double step = (maxVal - minVal) / (numTicks - 1);
			for (int i = 0; i < numTicks; i++) {
				double val = minVal + i * step;
				Vector3 pos = new Vector3(yOrigin.x, val, yOrigin.z);
				Vector3 pPos = mat.project(pos);
				renderables.add(new RenderableText(pPos, String.format(Locale.US, "%.1f", val), textState, "middle"));
			}
		}
		
		if (showAxes[2]) {
			// Z Axis
			double start = min.z;
			double end = max.z;
			Vector3 pStart = mat.project(new Vector3(zOrigin.x, zOrigin.y, start));
			Vector3 pEnd = mat.project(new Vector3(zOrigin.x, zOrigin.y, end));
			List<Vector3> pts = new ArrayList<>();
			pts.add(pStart);
			pts.add(pEnd);
			renderables.add(new RenderableLine(pts, axesState));
			
			double minVal = min.z;
			double maxVal = max.z;
			double step = (maxVal - minVal) / (numTicks - 1);
			for (int i = 0; i < numTicks; i++) {
				double val = minVal + i * step;
				Vector3 pos = new Vector3(zOrigin.x, zOrigin.y, val);
				Vector3 pPos = mat.project(pos);
				renderables.add(new RenderableText(pPos, String.format(Locale.US, "%.1f", val), textState, "end"));
			}
		}
	}

	private static Vector3 resolveOrigin(Vector3 def, IAST spec, int dim1, int dim2, Vector3 min, Vector3 max) {
		double[] coords = { def.x, def.y, def.z };
		// spec is {dir1, dir2}
		if (spec.size() > 1) {
			IExpr d1 = spec.get(1);
			if (d1.isNumber()) { // +1 or -1
				double val = getDouble(d1, 0);
				if (val > 0)
					coords[dim1] = (dim1 == 0) ? max.x : (dim1 == 1) ? max.y : max.z;
				else if (val < 0)
					coords[dim1] = (dim1 == 0) ? min.x : (dim1 == 1) ? min.y : min.z;
			}
		}
		if (spec.size() > 2) {
			IExpr d2 = spec.get(2);
			if (d2.isNumber()) { // +1 or -1
				double val = getDouble(d2, 0);
				if (val > 0)
					coords[dim2] = (dim2 == 0) ? max.x : (dim2 == 1) ? max.y : max.z;
				else if (val < 0)
					coords[dim2] = (dim2 == 0) ? min.x : (dim2 == 1) ? min.y : min.z;
			}
		}
		return new Vector3(coords[0], coords[1], coords[2]);
	}

	private static Color calculateLighting(Color baseColor, List<Vector3> points) {
		if (points.size() < 3) return baseColor;
		
		// 1. Calculate Face Normal
		Vector3 v0 = points.get(0);
		Vector3 v1 = points.get(1);
		Vector3 v2 = points.get(2);
		
		Vector3 edge1 = v1.sub(v0);
		Vector3 edge2 = v2.sub(v0);
		Vector3 normal = edge1.cross(edge2).normalize();
		
		// 2. Define Light Source (Top-Right-Front)
		Vector3 lightDir = new Vector3(0.5, -0.5, 1.0).normalize();
		
		// 3. Diffuse Component
		double diffuse = Math.max(0.0, normal.dot(lightDir));
		// Two-sided lighting (if we see backfaces)
		diffuse = Math.abs(normal.dot(lightDir));
		
		// 4. Combine
		double ambientParams = 0.5;
		double diffuseParams = 0.5;
		
		double intensity = ambientParams + diffuseParams * diffuse;
		intensity = Math.min(1.0, Math.max(0.0, intensity));
		
		int r = (int)(baseColor.getRed() * intensity);
		int g = (int)(baseColor.getGreen() * intensity);
		int b = (int)(baseColor.getBlue() * intensity);
		
		return new Color(r, g, b);
	}
	
	private static float clamp(double val) {

    return (float) Math.max(0.0, Math.min(1.0, val));
  }
}
