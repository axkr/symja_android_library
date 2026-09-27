package org.matheclipse.jsgraphics.jsxgraph;

import static org.matheclipse.jsgraphics.JSWriter.bezier;
import static org.matheclipse.jsgraphics.JSWriter.color;
import static org.matheclipse.jsgraphics.JSWriter.coordinate;
import static org.matheclipse.jsgraphics.JSWriter.finiteRuns;
import static org.matheclipse.jsgraphics.JSWriter.num;
import static org.matheclipse.jsgraphics.JSWriter.opacity;
import static org.matheclipse.jsgraphics.JSWriter.str;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.form.output.OutputFormats;
import org.matheclipse.core.graphics.svg.GraphicsOptions2D;
import org.matheclipse.core.graphics.svg.Prim2D;
import org.matheclipse.core.graphics.svg.Scene2D;
import org.matheclipse.core.graphics.svg.Style2D;
import org.matheclipse.core.graphics.svg.Viewport2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.jsgraphics.JSRenderer;

/**
 * Draws a 2D graphic with <a href="https://jsxgraph.org">JSXGraph</a>.
 *
 * <p>
 * The board spans the plot range the SVG renderer would have used, and every shape becomes a JSXGraph
 * element in data coordinates, so the board can be panned and zoomed. Open and closed outlines are
 * both <code>curve</code> elements - a curve takes a fill colour and arrowheads, and a polygon
 * would draw a draggable point at each vertex.
 */
public final class JSXGraphRenderer implements JSRenderer, Prim2D.Visitor<Void> {

  /** Segments a circle is flattened into when it cannot be drawn as a JSXGraph circle. */
  private static final int ELLIPSE_STEPS = 72;

  /** What every element shares: drawn only, never dragged or highlighted. */
  private static final String FIXED = "fixed: true, highlight: false";

  private StringBuilder js;

  @Override
  public String type() {
    return OutputFormats.JSXGRAPH_STR;
  }

  @Override
  public String unsupported(Scene2D scene) {
    if (scene.viewport.isLogX() || scene.viewport.isLogY()) {
      return "a logarithmic axis";
    }
    for (List<Prim2D> list : List.of(scene.primitives, scene.prolog, scene.epilog)) {
      for (Prim2D p : list) {
        if (p instanceof Prim2D.RasterPrim) {
          return "Raster";
        }
        if (p instanceof Prim2D.InsetPrim) {
          return "Inset";
        }
      }
    }
    return null;
  }

  @Override
  public String toJavaScript(Scene2D scene) {
    js = new StringBuilder(4096);
    Viewport2D vp = scene.viewport;
    GraphicsOptions2D options = scene.options;
    // the box takes the shape of the drawing area, so one unit is as long on the screen as it is
    // in the SVG picture
    double w = Math.max(1, vp.plotX2 - vp.plotX1);
    double h = Math.max(1, vp.plotY2 - vp.plotY1);
    js.append("document.getElementById('jxgbox').style.aspectRatio = '").append(num(w))
        .append(" / ").append(num(h)).append("';\n");
    js.append("var board = JXG.JSXGraph.initBoard('jxgbox', {boundingbox: [")
        .append(num(vp.minX)).append(", ").append(num(vp.maxY)).append(", ")
        .append(num(vp.maxX)).append(", ").append(num(vp.minY))
        .append("], axis: false, keepAspectRatio: false, showCopyright: false,"
            + " showNavigation: true, pan: {enabled: true}, zoom: {enabled: true, wheel: true}});\n");
    if (options.background != null) {
      js.append("board.containerObj.style.backgroundColor = ").append(color(options.background))
          .append(";\n");
    }
    js.append("board.suspendUpdate();\n");
    axes(scene);
    draw(scene.prolog);
    draw(scene.primitives);
    draw(scene.epilog);
    plotLabel(scene);
    js.append("board.unsuspendUpdate();\n");
    return js.toString();
  }

  private void draw(List<Prim2D> primitives) {
    for (Prim2D p : primitives) {
      p.accept(this);
    }
  }

  // ------------------------------------------------------------------ axes

  private void axes(Scene2D scene) {
    GraphicsOptions2D options = scene.options;
    Viewport2D vp = scene.viewport;
    if (!options.axesX && !options.axesY) {
      return;
    }
    // Axes cross at the origin when it is in view and at the nearest edge of the range otherwise,
    // where they would else be drawn outside the board
    double x0 = options.axesOrigin != null ? options.axesOrigin[0] : clamp(0, vp.minX, vp.maxX);
    double y0 = options.axesOrigin != null ? options.axesOrigin[1] : clamp(0, vp.minY, vp.maxY);
    String[] labels = labelPair(options.axesLabel);
    String tickStyle = "ticks: {strokeColor: '#666666', label: {fontSize: 11, strokeColor: '#333333'}}";
    if (options.axesX) {
      js.append("board.create('axis', [[").append(num(x0)).append(", ").append(num(y0))
          .append("], [").append(num(x0 + 1)).append(", ").append(num(y0))
          .append("]], {strokeColor: '#666666', ").append(tickStyle)
          .append(axisName(labels[0], "'rt'", "[-10, 12]")).append("});\n");
    }
    if (options.axesY) {
      js.append("board.create('axis', [[").append(num(x0)).append(", ").append(num(y0))
          .append("], [").append(num(x0)).append(", ").append(num(y0 + 1))
          .append("]], {strokeColor: '#666666', ").append(tickStyle)
          .append(axisName(labels[1], "'rt'", "[8, -8]")).append("});\n");
    }
  }

  private static String axisName(String label, String position, String offset) {
    if (label == null) {
      return "";
    }
    return ", name: " + str(label) + ", withLabel: true, label: {position: " + position
        + ", offset: " + offset + ", fontSize: 12}";
  }

  private void plotLabel(Scene2D scene) {
    IExpr label = scene.options.plotLabel;
    if (label == null || label.isNone()) {
      return;
    }
    Viewport2D vp = scene.viewport;
    js.append("board.create('text', [").append(num((vp.minX + vp.maxX) / 2)).append(", ")
        .append(num(vp.maxY)).append(", ").append(str(unquote(label.toString())))
        .append("], {anchorX: 'middle', anchorY: 'top', fontSize: 14, cssStyle: 'font-weight: bold', ")
        .append(FIXED).append("});\n");
  }

  /** The two halves of an <code>AxesLabel</code>; either may be <code>null</code>. */
  private static String[] labelPair(IExpr expr) {
    String[] out = new String[2];
    if (expr == null) {
      return out;
    }
    if (expr.isList() && expr.argSize() >= 2) {
      out[0] = labelText(((IAST) expr).arg1());
      out[1] = labelText(((IAST) expr).arg2());
    } else {
      out[0] = labelText(expr);
    }
    return out;
  }

  private static String labelText(IExpr expr) {
    return expr.isNone() || expr.isAutomatic() ? null : unquote(expr.toString());
  }

  private static String unquote(String s) {
    return s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")
        ? s.substring(1, s.length() - 1)
        : s;
  }

  private static double clamp(double v, double lo, double hi) {
    return Math.max(lo, Math.min(hi, v));
  }

  // ------------------------------------------------------------ primitives

  @Override
  public Void visitPoints(Prim2D.PointsPrim p) {
    Style2D s = p.style;
    // a JSXGraph curve draws no marks at its vertices, so each point is an element of its own
    List<double[]> points = new ArrayList<>(p.points.size());
    for (double[] q : p.points) {
      if (Double.isFinite(q[0]) && Double.isFinite(q[1])) {
        points.add(q);
      }
    }
    for (double[] q : points) {
      js.append("board.create('point', [").append(num(q[0])).append(", ").append(num(q[1]))
          .append("], {name: '', withLabel: false, showInfobox: false, face: 'o', size: ")
          .append(num(Math.max(1, s.pointRadius))).append(", strokeColor: ")
          .append(color(s.strokeColor)).append(", fillColor: ").append(color(s.strokeColor))
          .append(", strokeOpacity: ").append(num(opacity(s.strokeColor, s)))
          .append(", fillOpacity: ").append(num(opacity(s.strokeColor, s))).append(", ")
          .append(FIXED).append("});\n");
    }
    return null;
  }

  @Override
  public Void visitLine(Prim2D.LinePrim p) {
    for (List<double[]> segment : p.segments) {
      for (List<double[]> run : finiteRuns(segment)) {
        List<double[]> points = run;
        if (p.closed && run.size() == segment.size() && run.size() > 2) {
          points = closed(run);
        }
        curve(points, stroke(p.style, p.style.strokeColor, false), "");
      }
    }
    return null;
  }

  @Override
  public Void visitPolygon(Prim2D.PolygonPrim p) {
    // holes are not drawn: a JSXGraph curve has no even-odd fill
    for (List<double[]> run : finiteRuns(p.outer)) {
      if (run.size() > 2) {
        curve(closed(run), fill(p.style), "");
      }
    }
    return null;
  }

  @Override
  public Void visitRect(Prim2D.RectPrim p) {
    List<double[]> corners = new ArrayList<>(5);
    corners.add(new double[] {p.x1, p.y1});
    corners.add(new double[] {p.x2, p.y1});
    corners.add(new double[] {p.x2, p.y2});
    corners.add(new double[] {p.x1, p.y2});
    corners.add(new double[] {p.x1, p.y1});
    curve(corners, fill(p.style), "");
    return null;
  }

  @Override
  public Void visitEllipse(Prim2D.EllipsePrim p) {
    String paint = p.filled ? fill(p.style) : stroke(p.style, p.style.strokeColor, false);
    if (p.isFullTurn() && !p.isAnnulus() && Math.abs(p.rx - p.ry) < 1e-12 * Math.max(1, p.rx)) {
      js.append("board.create('circle', [[").append(num(p.cx)).append(", ").append(num(p.cy))
          .append("], ").append(num(p.rx)).append("], {").append(paint).append(", ")
          .append(FIXED).append("});\n");
      return null;
    }
    List<double[]> outline = p.flatten(ELLIPSE_STEPS);
    if (p.filled && !p.isFullTurn()) {
      // a filled sector is closed through the centre
      outline.add(0, new double[] {p.cx, p.cy});
      outline.add(new double[] {p.cx, p.cy});
    }
    curve(outline, paint, "");
    return null;
  }

  @Override
  public Void visitText(Prim2D.TextPrim p) {
    if (!Double.isFinite(p.x) || !Double.isFinite(p.y)) {
      return null;
    }
    Style2D s = p.style;
    String anchorX = p.offsetX <= -0.5 ? "left" : p.offsetX >= 0.5 ? "right" : "middle";
    String anchorY = p.offsetY <= -0.5 ? "bottom" : p.offsetY >= 0.5 ? "top" : "middle";
    StringBuilder css = new StringBuilder();
    if (!"normal".equals(s.fontWeight)) {
      css.append("font-weight: ").append(s.fontWeight).append("; ");
    }
    if (!"normal".equals(s.fontStyle)) {
      css.append("font-style: ").append(s.fontStyle).append("; ");
    }
    if (p.background != null) {
      css.append("background-color: rgba(").append(p.background.getRed()).append(',')
          .append(p.background.getGreen()).append(',').append(p.background.getBlue()).append(',')
          .append(num(p.background.getAlpha() / 255.0)).append("); ");
    }
    js.append("board.create('text', [").append(num(p.x)).append(", ").append(num(p.y))
        .append(", ").append(str(p.text)).append("], {anchorX: '").append(anchorX)
        .append("', anchorY: '").append(anchorY).append("', fontSize: ").append(num(s.fontSize))
        .append(", strokeColor: ").append(color(s.strokeColor)).append(", strokeOpacity: ")
        .append(num(opacity(s.strokeColor, s)));
    if (css.length() > 0) {
      js.append(", cssStyle: ").append(str(css.toString().trim()));
    }
    if (p.dirX != 1 || p.dirY != 0) {
      js.append(", rotate: ").append(num(Math.toDegrees(Math.atan2(p.dirY, p.dirX))))
          .append(", display: 'internal'");
    }
    js.append(", ").append(FIXED).append("});\n");
    return null;
  }

  @Override
  public Void visitArrow(Prim2D.ArrowPrim p) {
    List<List<double[]>> runs = finiteRuns(p.points);
    for (List<double[]> run : runs) {
      if (run.size() > 1) {
        curve(run, stroke(p.style, p.style.strokeColor, false),
            ", lastArrow: {type: 2, size: 8}");
      }
    }
    return null;
  }

  @Override
  public Void visitBezier(Prim2D.BezierPrim p) {
    List<double[]> points = bezier(p.points, p.degree);
    curve(points, p.filled ? fill(p.style) : stroke(p.style, p.style.strokeColor, false), "");
    return null;
  }

  @Override
  public Void visitBSpline(Prim2D.BSplinePrim p) {
    List<double[]> points = p.closed ? closed(p.curve) : p.curve;
    curve(points, p.filled ? fill(p.style) : stroke(p.style, p.style.strokeColor, false), "");
    return null;
  }

  @Override
  public Void visitRaster(Prim2D.RasterPrim p) {
    // refused by unsupported()
    return null;
  }

  @Override
  public Void visitInset(Prim2D.InsetPrim p) {
    // refused by unsupported()
    return null;
  }

  @Override
  public Void visitHalfPlane(Prim2D.HalfPlanePrim p) {
    double[] a = {p.px, p.py};
    double[] b = {p.px + p.vx, p.py + p.vy};
    String paint = stroke(p.style, p.style.strokeColor, false);
    js.append("board.create('line', [[").append(num(a[0])).append(", ").append(num(a[1]))
        .append("], [").append(num(b[0])).append(", ").append(num(b[1])).append("]], {")
        .append(paint).append(", ").append(FIXED).append("});\n");
    return null;
  }

  // --------------------------------------------------------------- helpers

  private void curve(List<double[]> points, String paint, String extra) {
    if (points.size() < 2) {
      return;
    }
    js.append("board.create('curve', [").append(coordinate(points, 0)).append(", ")
        .append(coordinate(points, 1)).append("], {").append(paint).append(extra).append(", ")
        .append(FIXED).append("});\n");
  }

  /** The attributes of an outline stroked in {@code c}. */
  private static String stroke(Style2D s, Color c, boolean edge) {
    StringBuilder a = new StringBuilder();
    a.append("strokeColor: ").append(color(c)).append(", strokeWidth: ")
        .append(num(Math.max(0, s.strokeWidth))).append(", strokeOpacity: ")
        .append(num(edge ? edgeOpacity(c, s) : opacity(c, s)));
    if (!"none".equals(s.dashArray)) {
      a.append(", dash: 2");
    }
    return a.toString();
  }

  /** The attributes of a filled shape: its fill, and an outline only where EdgeForm asks. */
  private static String fill(Style2D s) {
    Color fill = s.effectiveFill();
    StringBuilder a = new StringBuilder();
    a.append("fillColor: ").append(color(fill)).append(", fillOpacity: ")
        .append(num(opacity(fill, s))).append(", ");
    if (s.edgeFormSet && s.edgeColor != null) {
      a.append(stroke(s, s.edgeColor, true));
    } else {
      a.append("strokeColor: 'none', strokeWidth: 0");
    }
    return a.toString();
  }

  private static double edgeOpacity(Color c, Style2D s) {
    double a = c.getAlpha() / 255.0 * s.edgeOpacity;
    return Double.isNaN(a) ? 1.0 : Math.max(0.0, Math.min(1.0, a));
  }

  private static List<double[]> closed(List<double[]> points) {
    List<double[]> out = new ArrayList<>(points);
    double[] first = points.get(0);
    double[] last = points.get(points.size() - 1);
    if (first[0] != last[0] || first[1] != last[1]) {
      out.add(first);
    }
    return out;
  }
}
