package org.matheclipse.jsgraphics.mathcell;

import static org.matheclipse.jsgraphics.JSWriter.bezier;
import static org.matheclipse.jsgraphics.JSWriter.color;
import static org.matheclipse.jsgraphics.JSWriter.finiteRuns;
import static org.matheclipse.jsgraphics.JSWriter.num;
import static org.matheclipse.jsgraphics.JSWriter.opacity;
import static org.matheclipse.jsgraphics.JSWriter.pairs;
import static org.matheclipse.jsgraphics.JSWriter.str;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.builtin.OutputFunctions;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JavaScriptFormFactory;
import org.matheclipse.core.form.output.OutputFormats;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.svg.GraphicsOptions2D;
import org.matheclipse.core.graphics.svg.Prim2D;
import org.matheclipse.core.graphics.svg.Scene2D;
import org.matheclipse.core.graphics.svg.Style2D;
import org.matheclipse.core.graphics.svg.Viewport2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.jsgraphics.JSRenderer;

/**
 * Draws a graphic with <a href="https://github.com/paulmasson/mathcell">MathCell</a>, which
 * evaluates functions in the browser with the <a href="https://github.com/paulmasson/math">math</a>
 * library.
 *
 * <p>
 * Two ways in. A plot whose function translates to JavaScript is handed to MathCell as that
 * function - {@link #functionPlot} - so MathCell samples it itself and a <code>Plot3D</code> becomes
 * a surface it can turn. Everything else is evaluated first and its primitives are drawn as MathCell
 * lines, points and text.
 */
public final class MathCellRenderer implements JSRenderer, Prim2D.Visitor<Void> {

  /** Segments a circle is flattened into; MathCell has no circle of its own. */
  private static final int ELLIPSE_STEPS = 72;

  private List<String> data;

  @Override
  public String type() {
    return OutputFormats.MATHCELL_STR;
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
    data = new ArrayList<>();
    for (List<Prim2D> list : List.of(scene.prolog, scene.primitives, scene.epilog)) {
      for (Prim2D p : list) {
        p.accept(this);
      }
    }
    Viewport2D vp = scene.viewport;
    GraphicsOptions2D options = scene.options;
    StringBuilder config = new StringBuilder("{ type: 'svg', xMin: ").append(num(vp.minX))
        .append(", xMax: ").append(num(vp.maxX)).append(", yMin: ").append(num(vp.minY))
        .append(", yMax: ").append(num(vp.maxY)).append(", axes: ")
        .append(options.axesX || options.axesY);
    String[] labels = labelPair(options.axesLabel);
    if (labels[0] != null || labels[1] != null) {
      config.append(", axesLabels: [").append(str(labels[0] == null ? "" : labels[0]))
          .append(", ").append(str(labels[1] == null ? "" : labels[1])).append("]");
    }
    if (options.aspectRatioAutomatic || Double.isNaN(options.aspectRatio)) {
      config.append(", equalAspect: true");
    }
    config.append(" }");
    return program("", data, config.toString());
  }

  /**
   * The MathCell program for a plot the browser can evaluate itself, or <code>null</code> when the
   * plot is not one of those: <code>Plot</code> and <code>ParametricPlot</code> of functions of one
   * variable, and <code>Plot3D</code>, each with numeric bounds and functions the JavaScript
   * translator knows.
   *
   * @param plot the unevaluated plot expression
   */
  public static String functionPlot(IAST plot, EvalEngine engine) {
    try {
      if (plot.isAST(S.Plot) && plot.argSize() >= 2) {
        return plot2D(plot, engine);
      }
      if (plot.isAST(S.ParametricPlot) && plot.argSize() >= 2) {
        return parametricPlot(plot, engine);
      }
      if (plot.isAST(S.Plot3D) && plot.argSize() >= 3) {
        return plot3D(plot, engine);
      }
    } catch (RuntimeException rex) {
      // not translatable after all; the caller draws the evaluated plot instead
    }
    return null;
  }

  private static String plot2D(IAST plot, EvalEngine engine) {
    double[] range = new double[2];
    ISymbol x = iterator(plot.arg2(), range, engine);
    if (x == null) {
      return null;
    }
    IExpr f = plot.arg1();
    IAST functions = f.isList() ? (IAST) f : null;
    StringBuilder defs = new StringBuilder();
    List<String> data = new ArrayList<>();
    int n = functions == null ? 1 : functions.argSize();
    for (int i = 1; i <= n; i++) {
      IExpr fi = functions == null ? f : functions.get(i);
      String name = "f" + i;
      if (!define(defs, name, fi, x.toString())) {
        return null;
      }
      data.add("plot( " + x + " => " + name + "(" + x + "), [" + num(range[0]) + ", "
          + num(range[1]) + ", 400], { color: " + color(plotColor(i - 1)) + " } )");
    }
    return program(defs.toString(), data, "{ type: 'svg' }");
  }

  private static String parametricPlot(IAST plot, EvalEngine engine) {
    double[] range = new double[2];
    ISymbol t = iterator(plot.arg2(), range, engine);
    if (t == null || !plot.arg1().isList() || plot.arg1().argSize() != 2) {
      return null;
    }
    IAST xy = (IAST) plot.arg1();
    StringBuilder defs = new StringBuilder();
    if (!define(defs, "fx", xy.arg1(), t.toString()) || !define(defs, "fy", xy.arg2(), t.toString())) {
      return null;
    }
    List<String> data = new ArrayList<>();
    data.add("parametric( " + t + " => [ fx(" + t + "), fy(" + t + ") ], [" + num(range[0]) + ", "
        + num(range[1]) + ", 400], { color: " + color(plotColor(0)) + " } )");
    return program(defs.toString(), data, "{ type: 'svg', equalAspect: true }");
  }

  private static String plot3D(IAST plot, EvalEngine engine) {
    double[] xRange = new double[2];
    double[] yRange = new double[2];
    ISymbol x = iterator(plot.arg2(), xRange, engine);
    ISymbol y = iterator(plot.arg3(), yRange, engine);
    if (x == null || y == null || plot.arg1().isList()) {
      return null;
    }
    StringBuilder defs = new StringBuilder();
    if (!define(defs, "f", plot.arg1(), x + ", " + y)) {
      return null;
    }
    List<String> data = new ArrayList<>();
    data.add("parametric( (" + x + ", " + y + ") => [ " + x + ", " + y + ", f(" + x + ", " + y
        + ") ], [" + num(xRange[0]) + ", " + num(xRange[1]) + ", 60], [" + num(yRange[0]) + ", "
        + num(yRange[1]) + ", 60], { colormap: 'rainbow' } )");
    return program(defs.toString(), data, "{ type: 'threejs' }");
  }

  /** The variable of an iterator <code>{x, a, b}</code> with numeric bounds, stored in range. */
  private static ISymbol iterator(IExpr spec, double[] range, EvalEngine engine) {
    if (!spec.isList3() || !spec.first().isSymbol()) {
      return null;
    }
    IExpr a = engine.evalN(spec.second());
    IExpr b = engine.evalN(spec.argSize() >= 3 ? ((IAST) spec).arg3() : spec.second());
    if (!a.isReal() || !b.isReal()) {
      return null;
    }
    range[0] = a.evalf();
    range[1] = b.evalf();
    return Double.isFinite(range[0]) && Double.isFinite(range[1]) && range[0] < range[1]
        ? (ISymbol) spec.first()
        : null;
  }

  /**
   * Append a JavaScript function <code>name(params)</code> computing {@code f}, which returns
   * <code>NaN</code> where {@code f} cannot be evaluated so MathCell leaves a gap there.
   *
   * @return false when {@code f} does not translate
   */
  private static boolean define(StringBuilder defs, String name, IExpr f, String params) {
    String body = OutputFunctions.toJavaScript(f, JavaScriptFormFactory.USE_MATHCELL);
    if (body == null || body.isEmpty()) {
      return false;
    }
    defs.append("function ").append(name).append("(").append(params)
        .append(") { try { return ").append(body)
        .append("; } catch (e) { return Number.NaN; } }\n");
    return true;
  }

  private static Color plotColor(int i) {
    RGBColor c = GraphicsOptions.PLOT_COLORS[Math.floorMod(i, GraphicsOptions.PLOT_COLORS.length)];
    return new Color(c.getRed(), c.getGreen(), c.getBlue());
  }

  /** MathCell's own set up: a cell with no controls, drawn once by its update function. */
  private static String program(String definitions, List<String> data, String config) {
    StringBuilder js = new StringBuilder(1024);
    js.append("var parent = document.currentScript.parentNode;\n") //
        .append("var id = generateId();\n") //
        .append("parent.id = id;\n") //
        .append("MathCell( id, [] );\n") //
        .append(definitions) //
        .append("parent.update = function( id ) {\n") //
        .append("var data = [\n");
    for (int i = 0; i < data.size(); i++) {
      js.append(data.get(i)).append(i < data.size() - 1 ? ",\n" : "\n");
    }
    js.append("];\n") //
        .append("var config = ").append(config).append(";\n") //
        .append("evaluate( id, data, config );\n") //
        .append("};\n") //
        .append("parent.update( id );\n");
    return js.toString();
  }

  // ------------------------------------------------------------ primitives

  @Override
  public Void visitPoints(Prim2D.PointsPrim p) {
    Style2D s = p.style;
    // MathCell draws a point with a radius of three times its size
    String options = "{ color: " + color(s.strokeColor) + ", opacity: "
        + num(opacity(s.strokeColor, s)) + ", size: " + num(Math.max(0.3, s.pointRadius / 3))
        + " }";
    for (double[] q : p.points) {
      if (Double.isFinite(q[0]) && Double.isFinite(q[1])) {
        data.add("point( [" + num(q[0]) + ", " + num(q[1]) + "], " + options + " )");
      }
    }
    return null;
  }

  @Override
  public Void visitLine(Prim2D.LinePrim p) {
    for (List<double[]> segment : p.segments) {
      for (List<double[]> run : finiteRuns(segment)) {
        List<double[]> points = p.closed && run.size() == segment.size() ? closed(run) : run;
        line(points, p.style, p.style.strokeColor, false);
      }
    }
    return null;
  }

  @Override
  public Void visitPolygon(Prim2D.PolygonPrim p) {
    for (List<double[]> run : finiteRuns(p.outer)) {
      filled(closed(run), p.style);
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
    filled(corners, p.style);
    return null;
  }

  @Override
  public Void visitEllipse(Prim2D.EllipsePrim p) {
    List<double[]> outline = p.flatten(ELLIPSE_STEPS);
    if (p.filled) {
      if (!p.isFullTurn()) {
        outline.add(0, new double[] {p.cx, p.cy});
        outline.add(new double[] {p.cx, p.cy});
      }
      filled(outline, p.style);
    } else {
      line(outline, p.style, p.style.strokeColor, false);
    }
    return null;
  }

  @Override
  public Void visitText(Prim2D.TextPrim p) {
    if (!Double.isFinite(p.x) || !Double.isFinite(p.y)) {
      return null;
    }
    Style2D s = p.style;
    data.add("text( " + str(p.text) + ", [" + num(p.x) + ", " + num(p.y) + "], { color: "
        + color(s.strokeColor) + ", fontSize: " + num(s.fontSize) + " } )");
    return null;
  }

  @Override
  public Void visitArrow(Prim2D.ArrowPrim p) {
    List<double[]> points = new ArrayList<>();
    for (double[] q : p.points) {
      if (Double.isFinite(q[0]) && Double.isFinite(q[1])) {
        points.add(q);
      }
    }
    if (points.size() < 2) {
      return null;
    }
    // the shaft up to the last segment, and MathCell's arrow on that segment
    if (points.size() > 2) {
      line(points.subList(0, points.size() - 1), p.style, p.style.strokeColor, false);
    }
    double[] a = points.get(points.size() - 2);
    double[] b = points.get(points.size() - 1);
    data.add("arrow( [" + num(a[0]) + ", " + num(a[1]) + "], [" + num(b[0]) + ", " + num(b[1])
        + "], { color: " + color(p.style.strokeColor) + " } )");
    return null;
  }

  @Override
  public Void visitBezier(Prim2D.BezierPrim p) {
    List<double[]> points = bezier(p.points, p.degree);
    if (p.filled) {
      filled(points, p.style);
    } else {
      line(points, p.style, p.style.strokeColor, false);
    }
    return null;
  }

  @Override
  public Void visitBSpline(Prim2D.BSplinePrim p) {
    List<double[]> points = p.closed ? closed(p.curve) : p.curve;
    if (p.filled) {
      filled(points, p.style);
    } else {
      line(points, p.style, p.style.strokeColor, false);
    }
    return null;
  }

  @Override
  public Void visitRaster(Prim2D.RasterPrim p) {
    return null;
  }

  @Override
  public Void visitInset(Prim2D.InsetPrim p) {
    return null;
  }

  @Override
  public Void visitHalfPlane(Prim2D.HalfPlanePrim p) {
    // the line through the anchor, long enough to cross any plot range the picture could have
    double len = Math.hypot(p.vx, p.vy);
    if (!(len > 0)) {
      return null;
    }
    double k = 1e6 / len;
    List<double[]> points = new ArrayList<>(2);
    points.add(new double[] {p.px - k * p.vx, p.py - k * p.vy});
    points.add(new double[] {p.px + k * p.vx, p.py + k * p.vy});
    line(points, p.style, p.style.strokeColor, false);
    return null;
  }

  // --------------------------------------------------------------- helpers

  private void line(List<double[]> points, Style2D s, Color c, boolean fill) {
    if (points.size() < 2 || c == null || c.getAlpha() == 0) {
      return;
    }
    data.add("line( " + pairs(points) + ", { color: " + color(c) + ", opacity: "
        + num(opacity(c, s)) + ", thickness: " + num(Math.max(0.5, s.strokeWidth))
        + (fill ? ", fill: true" : "") + " } )");
  }

  /** A filled outline; MathCell fills and strokes a line in one colour, as EdgeForm[] does. */
  private void filled(List<double[]> points, Style2D s) {
    line(points, s, s.effectiveFill(), true);
    if (s.edgeFormSet && s.edgeColor != null) {
      line(points, s, s.edgeColor, false);
    }
  }

  private static List<double[]> closed(List<double[]> points) {
    List<double[]> out = new ArrayList<>(points);
    if (!points.isEmpty()) {
      double[] first = points.get(0);
      double[] last = points.get(points.size() - 1);
      if (first[0] != last[0] || first[1] != last[1]) {
        out.add(first);
      }
    }
    return out;
  }

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
    if (expr.isNone() || expr.isAutomatic()) {
      return null;
    }
    String s = expr.toString();
    return s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")
        ? s.substring(1, s.length() - 1)
        : s;
  }
}
