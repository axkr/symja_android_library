package org.matheclipse.core.builtin.graphics;

import java.awt.Color;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The plots drawn as one <code>Raster</code>:
 *
 * <ul>
 * <li><code>ReliefPlot(array)</code>: an array of heights coloured by height (low dark purple,
 * high light yellow) and shaded as a relief lit from the north west;</li>
 * <li><code>JuliaSetPlot(c)</code> and <code>JuliaSetPlot(f, z)</code>: the filled Julia set of
 * <code>z -&gt; z^2 + c</code> or of the polynomial <code>f</code>, in black, with the points
 * outside coloured by how fast their orbits escape.</li>
 * </ul>
 */
public final class RasterPlots {

  private RasterPlots() {}

  /** The options which are not passed on to the <code>Graphics</code>. */
  private static boolean isPlotOption(IExpr key) {
    return key == S.ColorFunction || key == S.ColorFunctionScaling || key == S.LightingAngle
        || key == S.PlotPoints || key == S.MaxIterations || key == S.DataRange;
  }

  private static @AllowNIL IExpr option(IAST ast, int from, ISymbol name) {
    for (int i = from; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && arg.first() == name) {
        return arg.second();
      }
    }
    return F.NIL;
  }

  private static void appendGraphicsOptions(IASTAppendable graphics, IAST ast, int from) {
    for (int i = from; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && !isPlotOption(arg.first())) {
        graphics.append(arg);
      }
    }
  }

  // ---------------------------------------------------------------- ReliefPlot

  /**
   * <code>ReliefPlot(array, options)</code>: row <code>j</code> of the array is drawn
   * <code>j</code> cells up from the bottom, as <code>ListDensityPlot</code> draws it.
   * <code>ColorFunction</code> colours the height (the field gradient by default),
   * <code>LightingAngle -&gt; {azimuth, elevation}</code> places the light (by default from the
   * north west, 45 degrees up), <code>LightingAngle -&gt; None</code> leaves out the shading.
   */
  public static final class ReliefPlot extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() < 1 || ast.arg1().isRuleAST()) {
        return F.NIL;
      }
      IExpr evaluated = engine.evalN(ast.arg1());
      double[][] h = matrix(evaluated);
      if (h == null || h.length < 2 || h[0].length < 2) {
        // Argument `1` at position `2` is not a 2 x 2 or larger numerical matrix of real values.
        return Errors.printMessage(S.ReliefPlot, "input", F.list(ast.arg1(), F.C1), engine);
      }
      int rows = h.length;
      int cols = h[0].length;
      double low = Double.POSITIVE_INFINITY;
      double high = Double.NEGATIVE_INFINITY;
      for (double[] row : h) {
        for (double v : row) {
          if (Double.isFinite(v)) {
            low = Math.min(low, v);
            high = Math.max(high, v);
          }
        }
      }
      if (!(low <= high)) {
        return F.NIL;
      }
      PlotColorFunction colors = PlotColorFunction
          .of(PlotColorFunction.Family.FIELD_2D, option(ast, 2, S.ColorFunction),
              option(ast, 2, S.ColorFunctionScaling), S.ReliefPlot, engine)
          .range(1, low, high).sink(PlotColorFunction.Sink.FLAT).build();
      double[] light = lightDirection(option(ast, 2, S.LightingAngle));
      // the heights are stretched so that the whole relief rises a twentieth of the array's size
      double exaggeration = high > low ? 0.05 * (rows + cols) / (high - low) : 0.0;
      IASTAppendable raster = F.ListAlloc(rows);
      for (int j = 0; j < rows; j++) {
        IASTAppendable row = F.ListAlloc(cols);
        for (int i = 0; i < cols; i++) {
          double v = h[j][i];
          if (!Double.isFinite(v)) {
            row.append(F.List(F.C1, F.C1, F.C1, F.C0));
            continue;
          }
          double t = high > low ? (v - low) / (high - low) : 0.5;
          Color base = null;
          if (colors != null) {
            base = ColorUtil.parse(colors.color(v));
          }
          if (base == null) {
            base = ColorUtil.parse(GraphicsOptions.getFieldColor(t));
          }
          double shade = 1.0;
          if (light != null) {
            double dx = (at(h, j, i + 1) - at(h, j, i - 1)) / 2.0 * exaggeration;
            double dy = (at(h, j + 1, i) - at(h, j - 1, i)) / 2.0 * exaggeration;
            double length = Math.sqrt(dx * dx + dy * dy + 1.0);
            double lit = (-dx * light[0] - dy * light[1] + light[2]) / length;
            // a flat cell keeps its colour; a slope towards the light brightens it a little, one
            // away from it darkens it a little, so the colours of the heights still lead
            shade = Math.max(0.6, Math.min(1.15, 0.6 + 0.4 * lit / light[2]));
          }
          row.append(F.List(F.num(channel(base.getRed(), shade)),
              F.num(channel(base.getGreen(), shade)), F.num(channel(base.getBlue(), shade))));
        }
        raster.append(row);
        engine.checkTimeLimit();
      }
      IASTAppendable graphics = F.ast(S.Graphics, ast.size() + 3);
      graphics.append(F.List(F.binaryAST2(S.Raster, raster,
          F.List(F.List(F.C0, F.C0), F.List(F.ZZ(cols), F.ZZ(rows))))));
      appendGraphicsOptions(graphics, ast, 2);
      if (option(ast, 2, S.Frame).isNIL()) {
        graphics.append(F.Rule(S.Frame, S.True));
      }
      if (option(ast, 2, S.AspectRatio).isNIL()) {
        graphics.append(F.Rule(S.AspectRatio, S.Automatic));
      }
      if (option(ast, 2, S.PlotRange).isNIL()) {
        graphics.append(F.Rule(S.PlotRange, F.List(S.All, S.All)));
      }
      return graphics;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** The value at a cell, clamped into the array at its edges. */
  private static double at(double[][] h, int j, int i) {
    int jj = Math.max(0, Math.min(h.length - 1, j));
    int ii = Math.max(0, Math.min(h[0].length - 1, i));
    double v = h[jj][ii];
    return Double.isFinite(v) ? v : h[Math.max(0, Math.min(h.length - 1, j))][ii];
  }

  private static double channel(int value, double shade) {
    return Math.max(0.0, Math.min(1.0, value / 255.0 * shade));
  }

  /**
   * The unit vector towards the light of <code>LightingAngle</code>, or {@code null} for
   * <code>None</code>: the default comes from the north west, 45 degrees above the horizon.
   */
  private static double[] lightDirection(IExpr spec) {
    double azimuth = 3.0 * Math.PI / 4.0;
    double elevation = Math.PI / 4.0;
    if (spec.isPresent()) {
      if (spec.isNone() || spec.isFalse()) {
        return null;
      }
      if (spec.isList2()) {
        double a = spec.first().evalfNaN();
        double e = spec.second().evalfNaN();
        if (Double.isFinite(a)) {
          azimuth = a;
        }
        if (Double.isFinite(e) && e > 0) {
          elevation = e;
        }
      } else if (spec.isReal()) {
        azimuth = spec.evalfNaN();
      }
    }
    return new double[] {Math.cos(elevation) * Math.cos(azimuth),
        Math.cos(elevation) * Math.sin(azimuth), Math.sin(elevation)};
  }

  /** A rectangular array of reals, or {@code null}. */
  private static double[][] matrix(IExpr expr) {
    if (!expr.isList() || expr.argSize() == 0 || !expr.first().isList()) {
      return null;
    }
    int rows = expr.argSize();
    int cols = expr.first().argSize();
    if (cols == 0) {
      return null;
    }
    double[][] h = new double[rows][cols];
    for (int j = 0; j < rows; j++) {
      IExpr row = expr.getAt(j + 1);
      if (!row.isList() || row.argSize() != cols) {
        return null;
      }
      for (int i = 0; i < cols; i++) {
        IExpr v = row.getAt(i + 1);
        if (!v.isReal()) {
          return null;
        }
        h[j][i] = v.evalfNaN();
      }
    }
    return h;
  }

  // ---------------------------------------------------------------- JuliaSetPlot

  /** The samples along each side of a <code>JuliaSetPlot</code> without <code>PlotPoints</code>. */
  static final int JULIA_POINTS = 140;

  /** The iterations of a <code>JuliaSetPlot</code> without <code>MaxIterations</code>. */
  static final int JULIA_ITERATIONS = 100;

  /**
   * <code>JuliaSetPlot(c)</code> for <code>z -&gt; z^2 + c</code>, <code>JuliaSetPlot(f, z)</code>
   * for a polynomial <code>f</code> of degree 1 or more. The framed picture spans the filled set
   * with a small margin (the square around the escape radius when the set has no area);
   * <code>PlotPoints</code> sets the samples along the longer side, <code>MaxIterations</code> how long an orbit is
   * followed, and <code>ColorFunction</code> colours the escape time scaled into <code>0 ... 1</code>
   * (by default light blue for a fast escape through purple and orange to yellow).
   */
  public static final class JuliaSetPlot extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      int positional = 0;
      while (positional < ast.argSize() && !ast.get(positional + 1).isRuleAST()) {
        positional++;
      }
      double[][] coefficients;
      if (positional == 1) {
        double[] c = complex(ast.arg1(), engine);
        if (c == null) {
          return F.NIL;
        }
        // z^2 + c, lowest power first
        coefficients = new double[][] {c, {0, 0}, {1, 0}};
      } else if (positional == 2 && ast.arg2().isSymbol()) {
        coefficients = polynomial(ast.arg1(), (ISymbol) ast.arg2(), engine);
        if (coefficients == null) {
          return F.NIL;
        }
      } else {
        return F.NIL;
      }
      int degree = coefficients.length - 1;
      double lead = Math.hypot(coefficients[degree][0], coefficients[degree][1]);
      double sum = 0.0;
      for (int k = 0; k < degree; k++) {
        sum += Math.hypot(coefficients[k][0], coefficients[k][1]);
      }
      // beyond this radius |f(z)| > |z|, and the orbit escapes
      double radius = Math.max(2.0, 1.0 + sum / lead);
      int n = option(ast, positional + 1, S.PlotPoints).toIntDefault(JULIA_POINTS);
      n = Math.max(10, Math.min(1000, n));
      int iterations =
          option(ast, positional + 1, S.MaxIterations).toIntDefault(JULIA_ITERATIONS);
      iterations = Math.max(2, Math.min(100000, iterations));
      Escape escape = new Escape(coefficients, 2.0 * radius, iterations);
      // the picture spans the filled set, found on a coarse grid, with a margin; a set without
      // area gets the square around the escape radius
      double[] box = filledBox(escape, radius, engine);
      double width = box[1] - box[0];
      double height = box[3] - box[2];
      // n samples along the longer side, at least 2 along the shorter one
      int columns = width >= height ? n : Math.max(2, (int) Math.round(n * width / height));
      int rowCount = width >= height ? Math.max(2, (int) Math.round(n * height / width)) : n;
      IASTAppendable rows = F.ListAlloc(rowCount);
      for (int j = 0; j < rowCount; j++) {
        IASTAppendable row = F.ListAlloc(columns);
        double y = box[2] + height * (j + 0.5) / rowCount;
        for (int i = 0; i < columns; i++) {
          double t = escape.time(box[0] + width * (i + 0.5) / columns, y);
          // in the filled Julia set: black
          row.append(t < 0 ? F.List(F.C0, F.C0, F.C0) : F.num(t));
        }
        rows.append(row);
        engine.checkTimeLimit();
      }
      IExpr colorFunction = option(ast, positional + 1, S.ColorFunction);
      if (colorFunction.isNIL() || colorFunction.isAutomatic()) {
        colorFunction = F.Function(F.binaryAST2(S.Blend, ESCAPE_COLORS, F.Slot1));
      }
      IASTAppendable graphics = F.ast(S.Graphics, ast.size() + 4);
      IASTAppendable raster = F.ast(S.Raster, 4);
      raster.append(rows);
      raster.append(F.List(F.List(F.num(box[0]), F.num(box[2])),
          F.List(F.num(box[1]), F.num(box[3]))));
      raster.append(F.List(F.C0, F.C1));
      raster.append(F.Rule(S.ColorFunction, colorFunction));
      graphics.append(F.List(raster));
      appendGraphicsOptions(graphics, ast, positional + 1);
      if (option(ast, positional + 1, S.Frame).isNIL()) {
        graphics.append(F.Rule(S.Frame, S.True));
      }
      if (option(ast, positional + 1, S.AspectRatio).isNIL()) {
        graphics.append(F.Rule(S.AspectRatio, S.Automatic));
      }
      return graphics;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * The colours of the escape time, from fast (light blue) through purple and orange to slow
   * (yellow).
   */
  private static final IAST ESCAPE_COLORS = F.List(F.RGBColor(0.42, 0.56, 0.8),
      F.RGBColor(0.53, 0.38, 0.6), F.RGBColor(0.62, 0.3, 0.38), F.RGBColor(0.95, 0.55, 0.1),
      F.RGBColor(1.0, 0.95, 0.55));

  /** The escape time of the orbits of a polynomial. */
  private static final class Escape {
    private final double[][] coefficients;
    private final double bound;
    private final int iterations;
    final int degree;

    Escape(double[][] coefficients, double bound, int iterations) {
      this.coefficients = coefficients;
      this.bound = bound;
      this.iterations = iterations;
      this.degree = coefficients.length - 1;
    }

    /**
     * How soon the orbit of <code>re + I im</code> leaves the escape circle, smoothed and scaled
     * into <code>0 ... 1</code>; <code>-1</code> if it stays.
     */
    double time(double re, double im) {
      int k = 0;
      double modulus = Math.hypot(re, im);
      while (k < iterations && modulus <= bound) {
        // Horner, highest power first
        double pr = coefficients[degree][0];
        double pi = coefficients[degree][1];
        for (int d = degree - 1; d >= 0; d--) {
          double tr = pr * re - pi * im + coefficients[d][0];
          pi = pr * im + pi * re + coefficients[d][1];
          pr = tr;
        }
        re = pr;
        im = pi;
        modulus = Math.hypot(re, im);
        k++;
      }
      if (k >= iterations) {
        return -1.0;
      }
      double smooth = degree > 1
          ? k + 1 - Math.log(Math.max(1.0, Math.log(modulus))) / Math.log(degree)
          : k;
      return Math.max(0.0, Math.min(1.0, smooth / Math.min(iterations, 40)));
    }
  }

  /**
   * <code>{xmin, xmax, ymin, ymax}</code> of the points whose orbits stay, with a margin of 3% on
   * each side. Where none stays (a set without area), the points which escape slowest frame the
   * picture: those taking at least half as long as the slowest; the square of
   * <code>radius + 1</code> when all escape alike.
   */
  private static double[] filledBox(Escape escape, double radius, EvalEngine engine) {
    int n = 160;
    double[][] times = new double[n][n];
    double slowest = 0.0;
    boolean stays = false;
    for (int j = 0; j < n; j++) {
      double y = -radius + 2.0 * radius * (j + 0.5) / n;
      for (int i = 0; i < n; i++) {
        double x = -radius + 2.0 * radius * (i + 0.5) / n;
        times[j][i] = escape.time(x, y);
        stays |= times[j][i] < 0;
        slowest = Math.max(slowest, times[j][i]);
      }
      engine.checkTimeLimit();
    }
    double cell = 2.0 * radius / n;
    double[] box = stays ? box(times, radius, t -> t < 0) : null;
    if ((box == null || tooSmall(box, cell)) && escape.degree > 1 && slowest > 0.1) {
      // no area that stays: the slowest escaping points
      double threshold = 0.5 * slowest;
      box = box(times, radius, t -> t >= threshold);
    }
    if (box == null || tooSmall(box, cell)) {
      double r = radius + 1.0;
      return new double[] {-r, r, -r, r};
    }
    // the cells around the ones found, then the margin
    double mx = 0.03 * (box[1] - box[0]) + cell;
    double my = 0.03 * (box[3] - box[2]) + cell;
    return new double[] {box[0] - mx, box[1] + mx, box[2] - my, box[3] + my};
  }

  /** The bounding box of the cells of the coarse grid whose escape time passes a test. */
  private static double[] box(double[][] times, double radius,
      java.util.function.DoublePredicate test) {
    int n = times.length;
    double[] box = {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE};
    boolean any = false;
    for (int j = 0; j < n; j++) {
      double y = -radius + 2.0 * radius * (j + 0.5) / n;
      for (int i = 0; i < n; i++) {
        if (test.test(times[j][i])) {
          double x = -radius + 2.0 * radius * (i + 0.5) / n;
          box[0] = Math.min(box[0], x);
          box[1] = Math.max(box[1], x);
          box[2] = Math.min(box[2], y);
          box[3] = Math.max(box[3], y);
          any = true;
        }
      }
    }
    return any ? box : null;
  }

  private static boolean tooSmall(double[] box, double cell) {
    return !(box[1] - box[0] > 2 * cell) || !(box[3] - box[2] > 2 * cell);
  }

  /** A number as its real and imaginary part, or {@code null}. */
  private static double[] complex(IExpr number, EvalEngine engine) {
    IExpr value = engine.evalN(number);
    if (!value.isNumber()) {
      return null;
    }
    double re = engine.evaluate(F.Re(value)).evalfNaN();
    double im = engine.evaluate(F.Im(value)).evalfNaN();
    return Double.isFinite(re) && Double.isFinite(im) ? new double[] {re, im} : null;
  }

  /**
   * The complex coefficients of a polynomial of degree 1 or more, lowest power first, or
   * {@code null}.
   */
  private static double[][] polynomial(IExpr f, ISymbol z, EvalEngine engine) {
    IExpr expanded = engine.evaluate(F.Expand(f));
    if (!expanded.isPolynomial(z)) {
      return null;
    }
    IExpr list = engine.evaluate(F.CoefficientList(expanded, z));
    if (!list.isList() || list.argSize() < 2) {
      return null;
    }
    double[][] coefficients = new double[list.argSize()][];
    for (int k = 0; k < coefficients.length; k++) {
      coefficients[k] = complex(list.getAt(k + 1), engine);
      if (coefficients[k] == null) {
        return null;
      }
    }
    double[] lead = coefficients[coefficients.length - 1];
    if (lead[0] == 0 && lead[1] == 0) {
      return null;
    }
    return coefficients;
  }
}
