package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.SvgGraphics2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The plots which are drawn by other plots, put together:
 *
 * <ul>
 * <li><code>StreamDensityPlot({vx, vy}, {x, ...}, {y, ...})</code>: the speed as a
 * <code>DensityPlot</code> under the <code>StreamPlot</code> of the field;
 * <code>StreamDensityPlot({{vx, vy}, s}, ...)</code> shows the scalar <code>s</code> instead;</li>
 * <li><code>ListStreamPlot(data)</code>: the streamlines of a field given at points, interpolated
 * between them as <code>ListVectorPlot</code> interpolates it;</li>
 * <li><code>ComplexRegionPlot(pred, {z, zmin, zmax})</code>: the <code>RegionPlot</code> of
 * <code>pred</code> with <code>z = x + I y</code>;</li>
 * <li><code>DateListLogPlot(data)</code>: a <code>DateListPlot</code> on a logarithmic vertical
 * axis;</li>
 * <li><code>StackedListPlot(data)</code>, <code>StackedDateListPlot(data)</code>: the datasets
 * stacked on each other, each filled down to the one below;</li>
 * <li><code>ListFitPlot(data)</code>: the data with the least squares line through them;</li>
 * <li><code>PlotGrid({{g11, g12, ...}, ...})</code>: plots in a grid with shared ranges along each
 * row and column, framed, a narrow gap apart, and with tick labels on the outer edges only.</li>
 * </ul>
 */
public final class ComposedPlots {

  private ComposedPlots() {}

  /** The options of a call from position <code>from</code> on. */
  private static List<IExpr> options(IAST ast, int from) {
    List<IExpr> options = new ArrayList<>();
    for (int i = from; i < ast.size(); i++) {
      if (ast.get(i).isRuleAST()) {
        options.add(ast.get(i));
      }
    }
    return options;
  }

  /** The number of arguments before the first option. */
  private static int positional(IAST ast) {
    int n = 0;
    while (n < ast.argSize() && !ast.get(n + 1).isRuleAST()) {
      n++;
    }
    return n;
  }

  private static boolean has(List<IExpr> options, IExpr key) {
    for (IExpr option : options) {
      if (option.first() == key) {
        return true;
      }
    }
    return false;
  }

  /** A plot evaluator which holds its arguments, so that its variables stay symbols. */
  private abstract static class HeldPlot extends AbstractFunctionEvaluator {
    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.addAttributes(ISymbol.HOLDALL | ISymbol.READPROTECTED);
    }
  }

  // ------------------------------------------------------------ StreamDensityPlot

  /** The options which go to the density underneath. */
  private static boolean isDensityOption(IExpr key) {
    return key == S.ColorFunction || key == S.ColorFunctionScaling || key == S.PlotPoints
        || key == S.MaxRecursion || key == S.PlotLegends;
  }

  /** <code>StreamDensityPlot(field, {x, x0, x1}, {y, y0, y1}, options)</code>. */
  public static final class StreamDensityPlot extends HeldPlot {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (positional(ast) != 3 || !ast.arg2().isList3() || !ast.arg3().isList3()) {
        return F.NIL;
      }
      IExpr field = ast.arg1();
      if (!field.isList()) {
        // a symbol or a call standing for the field: evaluated with the variables free
        field = engine.evaluate(
            F.Block(F.List(ast.arg2().first(), ast.arg3().first()), field));
      }
      IExpr vector;
      IExpr scalar;
      if (field.isList2() && field.first().isList2()) {
        vector = field.first();
        scalar = field.second();
      } else if (field.isList2()) {
        vector = field;
        scalar = F.Sqrt(F.Plus(F.Sqr(field.first()), F.Sqr(field.second())));
      } else {
        // no field to draw: the empty frame of the plot
        double[] x = range(ast.arg2(), engine);
        double[] y = range(ast.arg3(), engine);
        if (x == null || y == null) {
          return F.NIL;
        }
        return VectorPlot.fieldGraphics(F.List(F.CEmptyList), F.CEmptyList, x[0], x[1], y[0],
            y[1]);
      }
      List<IExpr> options = options(ast, 4);
      IASTAppendable density = F.ast(S.DensityPlot, 8);
      density.append(scalar);
      density.append(ast.arg2());
      density.append(ast.arg3());
      IASTAppendable stream = F.ast(S.StreamPlot, 8);
      stream.append(vector);
      stream.append(ast.arg2());
      stream.append(ast.arg3());
      IASTAppendable show = F.ast(S.Show, 8);
      for (IExpr option : options) {
        IExpr key = option.first();
        if (key == S.RegionFunction) {
          density.append(option);
          stream.append(option);
        } else if (isDensityOption(key)) {
          density.append(option);
        } else if (StreamPlot.isStreamOption(key)) {
          stream.append(option);
        } else {
          show.append(option);
        }
      }
      // the speed runs from blue where it is low to red where it is high
      if (!has(options, S.ColorFunction)) {
        density.append(F.Rule(S.ColorFunction, F.stringx("ThermometerColors")));
      }
      // over the colours of the density the lines are drawn in one dark colour
      if (!has(options, S.StreamStyle) && !has(options, S.StreamColorFunction)) {
        stream.append(F.Rule(S.StreamColorFunction, S.None));
        stream.append(F.Rule(S.StreamStyle, F.GrayLevel(F.num(0.15))));
      }
      IExpr densityGraphics = engine.evaluate(density);
      IExpr streamGraphics = engine.evaluate(stream);
      if (!streamGraphics.isAST(S.Graphics)) {
        return F.NIL;
      }
      show.append(1, streamGraphics);
      // without a density (one it cannot sample, say) the streamlines are drawn alone
      if (densityGraphics.isAST(S.Graphics)) {
        show.append(1, densityGraphics);
      }
      return engine.evaluate(show);
    }
  }

  // ------------------------------------------------------------ ListStreamPlot

  /**
   * <code>ListStreamPlot({{{x1, y1}, {vx1, vy1}}, ...})</code> and
   * <code>ListStreamPlot({{v11, v12, ...}, ...})</code>, an array of vectors whose entry
   * <code>(j, i)</code> sits at <code>{i, j}</code>, or in the box <code>DataRange -&gt; {{xmin,
   * xmax}, {ymin, ymax}}</code> gives.
   */
  public static final class ListStreamPlot extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (positional(ast) != 1 || !ast.arg1().isList()) {
        return F.NIL;
      }
      IExpr evaluated = engine.evalN(ast.arg1());
      if (!evaluated.isList() || evaluated.argSize() == 0) {
        return F.NIL;
      }
      IAST data = (IAST) evaluated;
      List<IExpr> options = options(ast, 2);
      List<double[]> points = new ArrayList<>();
      List<double[]> vectors = new ArrayList<>();
      double[] min = new double[2];
      double[] max = new double[2];
      java.util.function.Function<double[], double[]> field = null;
      // {{point, vector}, ...}; an array of vectors with two columns has the same shape, so the
      // array is read when the points of the pairs give no field (they lie on a line, say)
      boolean pairs = readPairs(data, points, vectors);
      if (pairs && points.size() >= 3) {
        field = ListVectorPlot.interpolatedField(points, vectors, min, max);
      }
      if (field == null) {
        points.clear();
        vectors.clear();
        if (readArray(data, options, points, vectors, engine)) {
          if (points.size() >= 3) {
            field = ListVectorPlot.interpolatedField(points, vectors, min, max);
          }
        } else if (!pairs) {
          return Errors.printMessage(S.ListStreamPlot, "arrayerr", F.list(ast.arg1()), engine);
        }
      }
      if (field == null) {
        return F.NIL;
      }
      IExpr spec = S.Automatic;
      IASTAppendable streamOptions = F.ListAlloc();
      IASTAppendable graphicsOptions = F.ListAlloc();
      for (IExpr option : options) {
        IExpr key = option.first();
        if (key == S.StreamPoints) {
          spec = engine.evaluate(option.second());
        } else if (StreamPlot.isStreamOption(key)) {
          streamOptions.append(option);
        } else if (key != S.DataRange) {
          graphicsOptions.append(option);
        }
      }
      StreamLines streams = new StreamLines(field, min[0], max[0], min[1], max[1], null, engine);
      IAST primitives = StreamPlot.streams(streams, spec, streamOptions, false, engine);
      return VectorPlot.fieldGraphics(primitives.isPresent() ? primitives : F.CEmptyList,
          graphicsOptions, min[0], max[0], min[1], max[1]);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * Read <code>{{{x, y}, {vx, vy}}, ...}</code>.
   *
   * @return false unless every entry is a pair of two pairs of numbers
   */
  private static boolean readPairs(IAST data, List<double[]> points, List<double[]> vectors) {
    for (IExpr pair : data) {
      double[] point = pair.isList2() ? pair2(pair.first()) : null;
      double[] vector = pair.isList2() ? pair2(pair.second()) : null;
      if (point == null || vector == null) {
        return false;
      }
      points.add(point);
      vectors.add(vector);
    }
    return true;
  }

  /**
   * Read an array <code>{{v11, v12, ...}, ...}</code> of vectors whose entry <code>(j, i)</code>
   * sits at <code>{i, j}</code>, or in the box of <code>DataRange -&gt; {{xmin, xmax}, {ymin,
   * ymax}}</code>.
   *
   * @return false unless the rows are of one length and hold pairs of numbers
   */
  private static boolean readArray(IAST data, List<IExpr> options, List<double[]> points,
      List<double[]> vectors, EvalEngine engine) {
    int rows = data.argSize();
    int cols = data.arg1().isList() ? data.arg1().argSize() : 0;
    if (cols == 0 || !data.forAll(row -> row.isList() && row.argSize() == cols
        && ((IAST) row).forAll(v -> pair2(v) != null))) {
      return false;
    }
    double[][] range = {{1, cols}, {1, rows}};
    for (IExpr option : options) {
      if (option.first() == S.DataRange) {
        IExpr r = engine.evalN(option.second());
        double[] xRange = r.isList2() ? pair2(r.first()) : null;
        double[] yRange = r.isList2() ? pair2(r.second()) : null;
        if (xRange != null && yRange != null) {
          range[0] = xRange;
          range[1] = yRange;
        }
      }
    }
    for (int j = 0; j < rows; j++) {
      IAST row = (IAST) data.get(j + 1);
      for (int i = 0; i < cols; i++) {
        double x = cols == 1 ? range[0][0]
            : range[0][0] + (range[0][1] - range[0][0]) * i / (cols - 1);
        double y = rows == 1 ? range[1][0]
            : range[1][0] + (range[1][1] - range[1][0]) * j / (rows - 1);
        points.add(new double[] {x, y});
        vectors.add(pair2(row.get(i + 1)));
      }
    }
    return true;
  }

  /** The ends of an iterator <code>{x, a, b}</code>, or {@code null}. */
  private static double[] range(IExpr iterator, EvalEngine engine) {
    if (!iterator.isList3()) {
      return null;
    }
    double a = engine.evalN(iterator.second()).evalfNaN();
    double b = engine.evalN(iterator.getAt(3)).evalfNaN();
    return Double.isFinite(a) && Double.isFinite(b) && a < b ? new double[] {a, b} : null;
  }

  /** <code>{a, b}</code> as two finite numbers, or {@code null}. */
  private static double[] pair2(IExpr expr) {
    if (!expr.isList2()) {
      return null;
    }
    double a = expr.first().evalfNaN();
    double b = expr.second().evalfNaN();
    return Double.isFinite(a) && Double.isFinite(b) ? new double[] {a, b} : null;
  }

  // ------------------------------------------------------------ ComplexRegionPlot

  /**
   * <code>ComplexRegionPlot(pred, {z, zmin, zmax})</code> over the rectangle with the corners
   * <code>zmin</code> and <code>zmax</code>, and <code>ComplexRegionPlot(pred, {z, r})</code> over
   * the one from <code>-r - r I</code> to <code>r + r I</code>.
   */
  public static final class ComplexRegionPlot extends HeldPlot {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (positional(ast) != 2 || !ast.arg2().isList() || !ast.arg2().first().isSymbol()) {
        return F.NIL;
      }
      IAST range = (IAST) ast.arg2();
      ISymbol z = (ISymbol) range.arg1();
      IExpr low;
      IExpr high;
      if (range.argSize() == 3) {
        low = engine.evaluate(range.arg2());
        high = engine.evaluate(range.arg3());
      } else if (range.argSize() == 2) {
        IExpr r = engine.evaluate(range.arg2());
        low = engine.evaluate(F.Times(F.CN1, F.Plus(r, F.Times(F.CI, r))));
        high = engine.evaluate(F.Plus(r, F.Times(F.CI, r)));
      } else {
        return F.NIL;
      }
      double[] a = complex(low, engine);
      double[] b = complex(high, engine);
      if (a == null || b == null || a[0] == b[0] || a[1] == b[1]) {
        // Corners for `1` in `2` must have distinct machine-precision real and imaginary parts.
        return Errors.printMessage(S.ComplexRegionPlot, "plldc", F.list(z, range), engine);
      }
      ISymbol x = F.Dummy("x");
      ISymbol y = F.Dummy("y");
      IExpr predicate = F.subst(ast.arg1(), z, F.Plus(x, F.Times(F.CI, y)));
      IASTAppendable plot = F.ast(S.RegionPlot, ast.size() + 2);
      plot.append(predicate);
      plot.append(F.List(x, F.num(Math.min(a[0], b[0])), F.num(Math.max(a[0], b[0]))));
      plot.append(F.List(y, F.num(Math.min(a[1], b[1])), F.num(Math.max(a[1], b[1]))));
      List<IExpr> options = options(ast, 3);
      plot.appendAll(options);
      if (!has(options, S.PlotRange)) {
        // the whole rectangle, not only the part where the predicate holds
        plot.append(F.Rule(S.PlotRange,
            F.List(F.List(F.num(Math.min(a[0], b[0])), F.num(Math.max(a[0], b[0]))),
                F.List(F.num(Math.min(a[1], b[1])), F.num(Math.max(a[1], b[1]))))));
      }
      return engine.evaluate(plot);
    }
  }

  /** The real and imaginary part of a number, or {@code null}. */
  private static double[] complex(IExpr number, EvalEngine engine) {
    double re = engine.evaluate(F.Re(number)).evalfNaN();
    double im = engine.evaluate(F.Im(number)).evalfNaN();
    return Double.isFinite(re) && Double.isFinite(im) ? new double[] {re, im} : null;
  }

  // ------------------------------------------------------------ DateListLogPlot

  /** <code>DateListLogPlot(data, options)</code>. */
  public static final class DateListLogPlot extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (positional(ast) < 1) {
        return F.NIL;
      }
      IExpr plot = engine.evaluate(ast.setAtCopy(0, S.DateListPlot));
      return logScaled(plot);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * The graphic with a logarithmic vertical axis: the scaling the log plots declare, and the
   * vertical range taken from the data, since a range from <code>0</code> has no logarithm.
   */
  private static @AllowNIL IExpr logScaled(IExpr plot) {
    if (plot.isAST(S.Legended) && plot.argSize() >= 1) {
      IExpr inner = logScaled(plot.first());
      return inner.isPresent() ? ((IAST) plot).setAtCopy(1, inner) : F.NIL;
    }
    if (!plot.isAST(S.Graphics)) {
      return F.NIL;
    }
    IASTAppendable result = ((IAST) plot).copyAppendable();
    boolean scaled = false;
    for (int i = 2; i < result.size(); i++) {
      IExpr option = result.get(i);
      if (!option.isRuleAST()) {
        continue;
      }
      if (option.first() == S.Method) {
        result.set(i, F.Rule(S.Method,
            F.List(F.Rule(F.stringx("Scaling"), F.List(F.stringx("None"), F.stringx("Log"))))));
        scaled = true;
      } else if (option.first() == S.PlotRange && option.second().isList2()) {
        result.set(i, F.Rule(S.PlotRange, F.List(option.second().first(), S.All)));
      }
    }
    if (!scaled) {
      result.append(F.Rule(S.Method,
          F.List(F.Rule(F.stringx("Scaling"), F.List(F.stringx("None"), F.stringx("Log"))))));
    }
    return result;
  }

  // ------------------------------------------------------------ stacked plots

  /**
   * <code>Filling -&gt; {1 -&gt; Axis, 2 -&gt; {1}, 3 -&gt; {2}, ...}</code>: each of
   * <code>n</code> curves filled down to the one below.
   */
  private static IAST stackedFilling(int n) {
    IASTAppendable rules = F.ListAlloc(n);
    rules.append(F.Rule(F.C1, S.Axis));
    for (int k = 2; k <= n; k++) {
      rules.append(F.Rule(F.ZZ(k), F.List(F.ZZ(k - 1))));
    }
    return F.Rule(S.Filling, rules);
  }

  /**
   * <code>StackedListPlot(data, options)</code>, drawn by <code>ListLinePlot</code>, and
   * <code>StackedDateListPlot(data, options)</code> of series of <code>{date, value}</code>,
   * drawn by <code>DateListPlot</code>: several datasets are summed at equal positions.
   */
  public static final class StackedPlot extends AbstractFunctionEvaluator {

    /** <code>ListLinePlot</code> or <code>DateListPlot</code>. */
    private final ISymbol plot;

    public StackedPlot(ISymbol plot) {
      this.plot = plot;
    }

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (positional(ast) != 1) {
        return F.NIL;
      }
      IExpr data = engine.evaluate(ast.arg1());
      if (!data.isList() || data.argSize() == 0) {
        return F.NIL;
      }
      List<IExpr> options = options(ast, 2);
      boolean several;
      IExpr stacked = F.NIL;
      if (plot == S.DateListPlot) {
        several = data.argSize() >= 2 && ((IAST) data).forAll(
            series -> series.isList() && ((IAST) series).forAll(p -> p.isList2()));
        stacked = several ? PlotLayouts.stacked(F.stringx("Stacked"), data) : F.NIL;
      } else {
        several = ListPlot.isListOfDatasets(data);
      }
      IASTAppendable call = F.ast(plot, ast.size() + 2);
      call.append(stacked.isPresent() ? stacked : data);
      call.appendAll(options);
      if (several && plot != S.DateListPlot) {
        call.append(F.Rule(S.PlotLayout, F.stringx("Stacked")));
      }
      if (!has(options, S.Filling)) {
        call.append(stackedFilling(several ? data.argSize() : 1));
      }
      return engine.evaluate(call);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  // ------------------------------------------------------------ ListFitPlot

  /**
   * <code>ListFitPlot(data, options)</code>: the points with the least squares line through them,
   * over the range of the data, both in the colour of the first dataset. The data are
   * <code>{x, y}</code> pairs or values at the positions <code>1, 2, ...</code>.
   */
  public static final class ListFitPlot extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() < 1 || ast.arg1().isRuleAST()) {
        return F.NIL;
      }
      if (ast.argSize() >= 2 && !ast.arg2().isRuleAST()) {
        // Options expected (instead of `1`) beyond position `2` in `3`. An option must be a rule
        // or a list of rules.
        return Errors.printMessage(S.ListFitPlot, "nonopt", F.list(ast.arg2(), F.C1, ast),
            engine);
      }
      IExpr data = engine.evaluate(ast.arg1());
      IAST points = fitPoints(data);
      if (points.isNIL()) {
        return F.NIL;
      }
      ISymbol t = F.Dummy("t");
      IExpr fit = engine.evaluate(F.Fit(points, F.List(F.C1, t), t));
      double low = Double.POSITIVE_INFINITY;
      double high = Double.NEGATIVE_INFINITY;
      for (IExpr p : points) {
        double x = p.first().evalfNaN();
        low = Math.min(low, x);
        high = Math.max(high, x);
      }
      if (!(high > low) || fit.isAST(S.Fit)) {
        return F.NIL;
      }
      IASTAppendable listPlot = F.ast(S.ListPlot, ast.size());
      listPlot.append(data);
      listPlot.appendAll(options(ast, 2));
      IExpr dots = engine.evaluate(listPlot);
      IExpr curve = engine.evaluate(F.Plot(fit, F.List(t, F.num(low), F.num(high))));
      return engine.evaluate(F.binaryAST2(S.Show, dots, curve));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** The data as <code>{x, y}</code> pairs: values get the positions 1, 2, ... */
  private static @AllowNIL IAST fitPoints(IExpr data) {
    if (data.isRuleAST()) {
      data = data.first();
    }
    if (!data.isList() || data.argSize() < 2) {
      return F.NIL;
    }
    IAST list = (IAST) data;
    IASTAppendable points = F.ListAlloc(list.argSize());
    for (int i = 1; i <= list.argSize(); i++) {
      IExpr p = list.get(i);
      if (p.isList2()) {
        points.append(p);
      } else if (p.isReal()) {
        points.append(F.List(F.ZZ(i), p));
      } else {
        return F.NIL;
      }
    }
    return points;
  }

  // ------------------------------------------------------------ PlotGrid

  /**
   * <code>PlotGrid({{g11, g12, ...}, ...})</code>: the plots in a grid, a narrow gap apart.
   * The plots of a column share the horizontal range, those of a row the vertical one; each is
   * framed, and only the left column and the bottom row write their tick labels.
   */
  public static final class PlotGrid extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (positional(ast) != 1) {
        return F.NIL;
      }
      IExpr arg = engine.evaluate(ast.arg1());
      if (!arg.isList() || arg.argSize() == 0) {
        return F.NIL;
      }
      IAST grid = (IAST) arg;
      if (!grid.arg1().isList()) {
        // one row
        grid = F.List(grid);
      }
      int rows = grid.argSize();
      int cols = 0;
      for (IExpr row : grid) {
        if (!row.isList()) {
          return F.NIL;
        }
        cols = Math.max(cols, row.argSize());
      }
      double[][] xRange = new double[cols][] ;
      double[][] yRange = new double[rows][];
      IExpr[][] cells = new IExpr[rows][cols];
      for (int r = 0; r < rows; r++) {
        IAST row = (IAST) grid.get(r + 1);
        for (int c = 0; c < cols; c++) {
          IExpr cell = c < row.argSize() ? row.get(c + 1) : F.NIL;
          IExpr graphics = cell.isAST(S.Legended) ? cell.first() : cell;
          // the cell as given: a Legended plot keeps its legend
          cells[r][c] = cell;
          if (!graphics.isAST(S.Graphics)) {
            continue;
          }
          double[][] range = SvgGraphics2D.plotRange((IAST) graphics);
          if (range == null) {
            continue;
          }
          xRange[c] = union(xRange[c], range[0]);
          yRange[r] = union(yRange[r], range[1]);
        }
      }
      IASTAppendable out = F.ListAlloc(rows);
      for (int r = 0; r < rows; r++) {
        IASTAppendable row = F.ListAlloc(cols);
        for (int c = 0; c < cols; c++) {
          IExpr cell = cells[r][c];
          if (cell == null || cell.isNIL()) {
            row.append(F.CEmptyString);
            continue;
          }
          boolean legended = cell.isAST(S.Legended) && cell.first().isAST(S.Graphics);
          IExpr graphics = legended ? cell.first() : cell;
          if (!graphics.isAST(S.Graphics)) {
            row.append(cell);
            continue;
          }
          IAST framed = framedCell((IAST) graphics, xRange[c], yRange[r], c == 0, r == rows - 1);
          row.append(legended ? ((IAST) cell).setAtCopy(1, framed) : framed);
        }
        out.append(row);
      }
      IASTAppendable result = F.ast(S.GraphicsGrid, 4);
      result.append(out);
      // a narrow gap between the frames
      result.append(F.Rule(S.Spacings, F.C5));
      result.appendAll(options(ast, 2));
      return result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** Ticks <code>{{value, ""}, ...}</code> at the round values of a range, or None. */
  private static IExpr unlabelledTicks(double[] range) {
    if (range == null) {
      return S.None;
    }
    IASTAppendable ticks = F.ListAlloc();
    for (org.matheclipse.core.graphics.svg.TickGenerator.Tick tick
        : org.matheclipse.core.graphics.svg.TickGenerator.linear(range[0], range[1])) {
      ticks.append(F.List(F.num(tick.value), F.CEmptyString));
    }
    return ticks;
  }

  private static double[] union(double[] range, double[] more) {
    if (range == null) {
      return more.clone();
    }
    return new double[] {Math.min(range[0], more[0]), Math.max(range[1], more[1])};
  }

  /**
   * A cell of a plot grid: framed, in the shared ranges, with tick labels only on the left and the
   * bottom edge of the grid.
   */
  private static IAST framedCell(IAST graphics, double[] xRange, double[] yRange, boolean left,
      boolean bottom) {
    IASTAppendable cell = F.ast(S.Graphics, graphics.size() + 4);
    cell.append(graphics.arg1());
    for (int i = 2; i < graphics.size(); i++) {
      IExpr option = graphics.get(i);
      if (option.isRuleAST() && (option.first() == S.Frame || option.first() == S.FrameTicks
          || option.first() == S.PlotRange || option.first() == S.AxesLabel
          || option.first() == S.ImagePadding)) {
        continue;
      }
      cell.append(option);
    }
    cell.append(F.Rule(S.Frame, S.True));
    // the inner edges carry the ticks without their labels
    IExpr leftTicks = left ? S.Automatic : unlabelledTicks(yRange);
    IExpr bottomTicks = bottom ? S.Automatic : unlabelledTicks(xRange);
    cell.append(F.Rule(S.FrameTicks,
        F.List(F.List(leftTicks, S.None), F.List(bottomTicks, S.None))));
    // room for tick labels on the outer edges only, so the frames of neighbours meet
    cell.append(F.Rule(S.ImagePadding, F.List(F.List(F.ZZ(left ? 40 : 1), F.C1),
        F.List(F.ZZ(bottom ? 25 : 1), F.C1))));
    if (xRange != null && yRange != null) {
      cell.append(F.Rule(S.PlotRange, F.List(F.List(F.num(xRange[0]), F.num(xRange[1])),
          F.List(F.num(yRange[0]), F.num(yRange[1])))));
    }
    return cell;
  }
}
