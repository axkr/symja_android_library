package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Charts of data built directly from graphics primitives, or from the plot functions:
 * <code>PairedBarChart</code>, <code>PairedHistogram</code>, <code>Histogram3D</code>,
 * <code>SmoothHistogram</code>, <code>SmoothHistogram3D</code> and <code>ClockGauge</code>.
 */
public final class DataCharts {

  /** The half width of a bar of a paired bar chart. */
  private static final double PAIRED_BAR_HALF_WIDTH = 0.457;

  /** The central gap of a paired chart on each side, relative to the largest bar. */
  private static final double PAIRED_GAP = 0.075;

  /** The bandwidths a smooth histogram extends beyond the data. */
  private static final double SMOOTH_MARGIN = 3.35;

  /** The bandwidths a two-dimensional smooth histogram extends beyond the data. */
  private static final double SMOOTH_MARGIN_2D = 3.0;

  /** The samples per axis of a two-dimensional smooth histogram. */
  private static final int SMOOTH_SAMPLES = 40;

  private DataCharts() {}

  /** The option rules at the end of <code>ast</code>. */
  private static List<IExpr> options(IAST ast) {
    List<IExpr> options = new ArrayList<>();
    for (IExpr arg : ast) {
      if (arg.isRuleAST()) {
        options.add(arg);
      }
    }
    return options;
  }

  /** The number of arguments of <code>ast</code> before its option rules. */
  private static int positional(IAST ast) {
    int n = 0;
    while (n < ast.argSize() && !ast.get(n + 1).isRuleAST()) {
      n++;
    }
    return n;
  }

  private static IASTAppendable withOptions(IExpr head, IExpr first, IAST ast) {
    IASTAppendable result = F.ast(head);
    result.append(first);
    for (IExpr option : options(ast)) {
      result.append(option);
    }
    return result;
  }

  /** The Silverman bandwidth of the data, 1 if the data has no spread. */
  private static double silverman(double[] data) {
    double[] sorted = data.clone();
    java.util.Arrays.sort(sorted);
    final int n = sorted.length;
    double mean = 0.0;
    for (double x : sorted) {
      mean += x;
    }
    mean /= n;
    double sum = 0.0;
    for (double x : sorted) {
      sum += (x - mean) * (x - mean);
    }
    double sigma = Math.sqrt(sum / (n - 1));
    double iqr = sorted[Math.min(n - 1, (3 * n) / 4)] - sorted[n / 4];
    double spread = iqr > 0.0 ? Math.min(sigma, iqr / 1.34) : sigma;
    double h = 0.9 * spread * Math.pow(n, -0.2);
    return h > 0.0 && Double.isFinite(h) ? h : 1.0;
  }

  /** The numbers of a list of at least two numbers, or <code>null</code>. */
  private static double[] numbers(IExpr list) {
    if (!list.isList() || list.argSize() < 2) {
      return null;
    }
    double[] values = list.toDoubleVector();
    if (values == null) {
      return null;
    }
    for (double value : values) {
      if (!Double.isFinite(value)) {
        return null;
      }
    }
    return values;
  }

  /**
   * <code>PairedBarChart(left, right)</code> and <code>PairedHistogram(left, right, bspec)</code> -
   * two series drawn back to back as horizontal bars: the first extends left of a central gap, the
   * second right. A histogram bins both data sets with the bins of the joined data.
   */
  public static final class PairedChart extends AbstractFunctionEvaluator {
    private final boolean histogram;

    public PairedChart(boolean histogram) {
      this.histogram = histogram;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      if (positional < 2 || !ast.arg1().isList() || !ast.arg2().isList()) {
        return F.NIL;
      }
      List<double[]> bars = histogram ? histogramBars(ast, positional, engine) : barChartBars(ast);
      if (bars == null || bars.isEmpty()) {
        return F.NIL;
      }
      double largest = 0.0;
      for (double[] bar : bars) {
        largest = Math.max(largest, Math.max(Math.abs(bar[2]), Math.abs(bar[3])));
      }
      double gap = PAIRED_GAP * Math.max(largest, Double.MIN_VALUE);
      IASTAppendable left = F.ListAlloc(bars.size() + 1);
      IASTAppendable right = F.ListAlloc(bars.size() + 1);
      left.append(RectangleChart.color(S.Automatic, 0));
      right.append(RectangleChart.color(S.Automatic, 1));
      for (double[] bar : bars) {
        if (bar[2] != 0.0) {
          left.append(F.Rectangle(F.List(F.num(-gap - bar[2]), F.num(bar[0])),
              F.List(F.num(-gap), F.num(bar[1]))));
        }
        if (bar[3] != 0.0) {
          right.append(F.Rectangle(F.List(F.num(gap), F.num(bar[0])),
              F.List(F.num(gap + bar[3]), F.num(bar[1]))));
        }
      }
      return withOptions(S.Graphics,
          F.List(F.EdgeForm(F.GrayLevel(F.num(0.5))), left, right), ast);
    }

    /** The bars <code>{bottom, top, left length, right length}</code> of a bar chart. */
    private static List<double[]> barChartBars(IAST ast) {
      double[] left = ast.arg1().toDoubleVector();
      double[] right = ast.arg2().toDoubleVector();
      if (left == null || right == null) {
        return null;
      }
      List<double[]> bars = new ArrayList<>();
      for (int k = 0; k < Math.max(left.length, right.length); k++) {
        double y = k + 1.0;
        bars.add(new double[] {y - PAIRED_BAR_HALF_WIDTH, y + PAIRED_BAR_HALF_WIDTH,
            k < left.length ? left[k] : 0.0, k < right.length ? right[k] : 0.0});
      }
      return bars;
    }

    /** The bars with the bins of the joined data. */
    private static List<double[]> histogramBars(IAST ast, int positional, EvalEngine engine) {
      IExpr joined = F.Join(ast.arg1(), ast.arg2());
      IExpr list = engine.evaluate(
          positional >= 3 ? F.binaryAST2(S.HistogramList, joined, ast.arg3())
              : F.unaryAST1(S.HistogramList, joined));
      if (!list.isList2() || !list.first().isList()) {
        return null;
      }
      IExpr edgeList = list.first();
      double[] edges = edgeList.toDoubleVector();
      IExpr left = engine.evaluate(F.binaryAST2(S.BinCounts, ast.arg1(), F.List(edgeList)));
      IExpr right = engine.evaluate(F.binaryAST2(S.BinCounts, ast.arg2(), F.List(edgeList)));
      double[] leftCounts = left.isList() ? left.toDoubleVector() : null;
      double[] rightCounts = right.isList() ? right.toDoubleVector() : null;
      if (edges == null || leftCounts == null || rightCounts == null
          || leftCounts.length != edges.length - 1 || rightCounts.length != edges.length - 1) {
        return null;
      }
      List<double[]> bars = new ArrayList<>();
      for (int k = 0; k < leftCounts.length; k++) {
        bars.add(new double[] {edges[k], edges[k + 1], leftCounts[k], rightCounts[k]});
      }
      return bars;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** The bin edges of one coordinate of the data of a <code>Histogram3D</code>. */
  private static double[] binEdges(IExpr values, IAST ast, int positional, EvalEngine engine) {
    IExpr list = engine.evaluate(
        positional >= 2 ? F.binaryAST2(S.HistogramList, values, ast.arg2())
            : F.unaryAST1(S.HistogramList, values));
    if (!list.isList2() || !list.first().isList() || list.first().argSize() < 2) {
      return null;
    }
    return list.first().toDoubleVector();
  }

  /** The index of the bin of <code>value</code>, or <code>-1</code>. */
  private static int binOf(double[] edges, double value) {
    int n = edges.length - 1;
    for (int i = 0; i < n; i++) {
      if (value >= edges[i] && (i == n - 1 ? value <= edges[i + 1] : value < edges[i + 1])) {
        return i;
      }
    }
    return -1;
  }

  /** The first and the second coordinates of a list of pairs of numbers, or <code>null</code>. */
  private static double[][] pairs(IExpr data) {
    if (!data.isList() || data.argSize() == 0) {
      return null;
    }
    IAST points = (IAST) data;
    double[][] xy = new double[2][points.argSize()];
    for (int i = 0; i < points.argSize(); i++) {
      IExpr point = points.get(i + 1);
      xy[0][i] = point.isList2() ? point.first().evalfNaN() : Double.NaN;
      xy[1][i] = point.isList2() ? point.second().evalfNaN() : Double.NaN;
      if (!Double.isFinite(xy[0][i]) || !Double.isFinite(xy[1][i])) {
        return null;
      }
    }
    return xy;
  }

  /**
   * <code>Histogram3D({{x1, y1}, {x2, y2}, ...})</code> and <code>Histogram3D(data, bspec)</code> -
   * one cuboid for each non-empty bin of the points, as high as the bin has points.
   */
  public static final class Histogram3D extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      double[][] xy = pairs(ast.arg1());
      if (xy == null || positional > 2) {
        return F.NIL;
      }
      double[] xEdges = binEdges(F.List(xy[0]), ast, positional, engine);
      double[] yEdges = binEdges(F.List(xy[1]), ast, positional, engine);
      if (xEdges == null || yEdges == null) {
        return F.NIL;
      }
      int[][] counts = new int[xEdges.length - 1][yEdges.length - 1];
      for (int k = 0; k < xy[0].length; k++) {
        int i = binOf(xEdges, xy[0][k]);
        int j = binOf(yEdges, xy[1][k]);
        if (i >= 0 && j >= 0) {
          counts[i][j]++;
        }
      }
      IASTAppendable cuboids = F.ListAlloc();
      cuboids.append(RectangleChart.color(S.Automatic, 0));
      for (int i = 0; i < counts.length; i++) {
        for (int j = 0; j < counts[i].length; j++) {
          if (counts[i][j] > 0) {
            cuboids.append(F.Cuboid(F.List(F.num(xEdges[i]), F.num(yEdges[j]), F.CD0),
                F.List(F.num(xEdges[i + 1]), F.num(yEdges[j + 1]), F.num(counts[i][j]))));
          }
        }
      }
      return withOptions(S.Graphics3D, cuboids, ast);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>SmoothHistogram(data)</code> and <code>SmoothHistogram({data1, ...}, bw, dfun)</code> - the
   * curves of the <code>SmoothKernelDistribution</code> of each data set: its <code>"PDF"</code>,
   * <code>"CDF"</code>, <code>"SF"</code> or <code>"HF"</code>, over the data widened by a few
   * bandwidths.
   */
  public static final class SmoothHistogram extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      if (positional > 3 || !ast.arg1().isList()) {
        return F.NIL;
      }
      IAST data = (IAST) ast.arg1();
      IAST datasets = data.forAll(x -> x.isList()) ? data : F.List(data);
      if (datasets.isEmpty()) {
        return F.NIL;
      }
      IExpr bandwidth = positional >= 2 ? ast.arg2() : S.Automatic;
      double fixed = bandwidth.isAutomatic() ? Double.NaN : bandwidth.evalfNaN();
      if (!bandwidth.isAutomatic() && !(fixed > 0.0)) {
        return F.NIL;
      }
      IExpr distributionFunction = positional >= 3 ? distributionFunction(ast.arg3()) : S.PDF;
      if (distributionFunction.isNIL()) {
        return F.NIL;
      }
      ISymbol x = F.Dummy("$x");
      double low = Double.POSITIVE_INFINITY;
      double high = Double.NEGATIVE_INFINITY;
      IASTAppendable curves = F.ListAlloc(datasets.argSize());
      for (IExpr dataset : datasets) {
        double[] values = numbers(dataset);
        if (values == null) {
          return F.NIL;
        }
        double h = bandwidth.isAutomatic() ? silverman(values) : fixed;
        for (double value : values) {
          low = Math.min(low, value - SMOOTH_MARGIN * h);
          high = Math.max(high, value + SMOOTH_MARGIN * h);
        }
        IExpr distribution = F.binaryAST2(S.SmoothKernelDistribution, dataset, F.num(h));
        curves.append(F.binaryAST2(distributionFunction, distribution, x));
      }
      IExpr body = curves.argSize() == 1 ? curves.arg1() : curves;
      IASTAppendable plot = F.ast(S.Plot);
      plot.append(body);
      plot.append(F.List(x, F.num(low), F.num(high)));
      boolean plotRange = false;
      for (IExpr option : options(ast)) {
        plot.append(option);
        plotRange |= option.first() == S.PlotRange;
      }
      if (!plotRange) {
        plot.append(F.Rule(S.PlotRange, S.All));
      }
      return plot;
    }

    private static IExpr distributionFunction(IExpr name) {
      if (name.isString("PDF")) {
        return S.PDF;
      }
      if (name.isString("CDF")) {
        return S.CDF;
      }
      if (name.isString("SF")) {
        return S.SurvivalFunction;
      }
      return name.isString("HF") ? S.HazardFunction : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>SmoothHistogram3D({{x1, y1}, ...}, bspec)</code> - the Gaussian kernel density estimate of
   * points in the plane as a surface, with the bandwidth <code>bspec</code> or <code>{hx, hy}</code>
   * and by default the Silverman rule in each coordinate.
   */
  public static final class SmoothHistogram3D extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      double[][] xy = pairs(ast.arg1());
      if (xy == null || xy[0].length < 2 || positional > 3
          || (positional == 3 && !ast.arg3().isString("PDF"))) {
        return F.NIL;
      }
      double hx = silverman(xy[0]);
      double hy = silverman(xy[1]);
      if (positional >= 2 && !ast.arg2().isAutomatic() && !ast.arg2().isString("Silverman")) {
        IExpr bandwidth = ast.arg2();
        hx = bandwidth.isList2() ? bandwidth.first().evalfNaN() : bandwidth.evalfNaN();
        hy = bandwidth.isList2() ? bandwidth.second().evalfNaN() : hx;
        if (!(hx > 0.0) || !(hy > 0.0)) {
          return F.NIL;
        }
      }
      double[] xRange = range(xy[0], hx);
      double[] yRange = range(xy[1], hy);
      final int n = xy[0].length;
      double norm = 1.0 / (n * 2.0 * Math.PI * hx * hy);
      IASTAppendable rows = F.ListAlloc(SMOOTH_SAMPLES);
      for (int i = 0; i < SMOOTH_SAMPLES; i++) {
        engine.checkTimeLimit();
        double y = yRange[0] + (yRange[1] - yRange[0]) * i / (SMOOTH_SAMPLES - 1);
        double[] row = new double[SMOOTH_SAMPLES];
        for (int j = 0; j < SMOOTH_SAMPLES; j++) {
          double x = xRange[0] + (xRange[1] - xRange[0]) * j / (SMOOTH_SAMPLES - 1);
          double density = 0.0;
          for (int k = 0; k < n; k++) {
            double u = (x - xy[0][k]) / hx;
            double v = (y - xy[1][k]) / hy;
            density += Math.exp(-0.5 * (u * u + v * v));
          }
          row[j] = density * norm;
        }
        rows.append(F.List(row));
      }
      IASTAppendable plot = withOptions(S.ListPlot3D, rows, ast);
      plot.append(F.Rule(S.DataRange, F.List(F.List(F.num(xRange[0]), F.num(xRange[1])),
          F.List(F.num(yRange[0]), F.num(yRange[1])))));
      return plot;
    }

    private static double[] range(double[] values, double h) {
      double low = Double.POSITIVE_INFINITY;
      double high = Double.NEGATIVE_INFINITY;
      for (double value : values) {
        low = Math.min(low, value - SMOOTH_MARGIN_2D * h);
        high = Math.max(high, value + SMOOTH_MARGIN_2D * h);
      }
      return new double[] {low, high};
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>ClockGauge()</code>, <code>ClockGauge({h, m, s})</code>,
   * <code>ClockGauge(DateObject(...))</code> and <code>ClockGauge(seconds)</code> - an analog clock
   * face with the dial, a tick and a number for each hour and the hour, minute and second hands of
   * that time of day.
   */
  public static final class ClockGauge extends AbstractFunctionEvaluator {

    private static final int HOURS = 12;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      double[] time = time(ast, engine);
      if (time == null) {
        return F.NIL;
      }
      IASTAppendable face = F.ListAlloc();
      face.append(F.Circle(F.List(F.CD0, F.CD0), F.CD1));
      for (int k = 0; k < HOURS; k++) {
        double turns = k / (double) HOURS;
        face.append(F.Line(F.List(at(turns, 0.88), at(turns, 0.98))));
        face.append(F.Text(F.ZZ(k == 0 ? HOURS : k), at(turns, 0.75)));
      }
      double seconds = time[2] + 60.0 * time[1] + 3600.0 * time[0];
      face.append(hand(seconds / 43200.0, 0.5, 4.0));
      face.append(hand(seconds / 3600.0, 0.8, 3.0));
      face.append(F.List(F.RGBColor(F.C1, F.C0, F.C0), hand(seconds / 60.0, 0.85, 1.0)));
      IASTAppendable graphics = F.ast(S.Graphics);
      graphics.append(face);
      for (IExpr option : options(ast)) {
        // the options of the gauge scale don't apply to the graphics
        if (option.first() != S.ScaleRanges) {
          graphics.append(option);
        }
      }
      return graphics;
    }

    /** <code>{hours, minutes, seconds}</code> of the arguments, or <code>null</code>. */
    private static double[] time(IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      if (positional > 1) {
        return null;
      }
      if (positional == 0) {
        return dateTime(engine.evaluate(F.ast(S.DateList)));
      }
      IExpr arg = ast.arg1();
      if (arg.isList() && arg.argSize() >= 1 && arg.argSize() <= 3) {
        double[] time = new double[3];
        for (int i = 0; i < arg.argSize(); i++) {
          time[i] = arg.getAt(i + 1).evalfNaN();
          if (!Double.isFinite(time[i])) {
            return null;
          }
        }
        return time;
      }
      if (arg.isAST(S.DateObject)) {
        return dateTime(engine.evaluate(F.unaryAST1(S.DateList, arg)));
      }
      double seconds = arg.evalfNaN();
      if (!Double.isFinite(seconds)) {
        return null;
      }
      seconds = seconds - 86400.0 * Math.floor(seconds / 86400.0);
      return new double[] {Math.floor(seconds / 3600.0), Math.floor((seconds % 3600.0) / 60.0),
          seconds % 60.0};
    }

    private static double[] dateTime(IExpr dateList) {
      if (!dateList.isList() || dateList.argSize() < 6) {
        return null;
      }
      return new double[] {dateList.getAt(4).evalfNaN(), dateList.getAt(5).evalfNaN(),
          dateList.getAt(6).evalfNaN()};
    }

    /** The point at <code>radius</code> after <code>turns</code> clockwise from 12 o'clock. */
    private static IAST at(double turns, double radius) {
      double angle = Math.PI / 2.0 - turns * 2.0 * Math.PI;
      return F.List(F.num(radius * Math.cos(angle)), F.num(radius * Math.sin(angle)));
    }

    private static IAST hand(double turns, double radius, double thickness) {
      return F.List(F.AbsoluteThickness(F.num(thickness)),
          F.Line(F.List(F.List(F.CD0, F.CD0), at(turns - Math.floor(turns), radius))));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }
}
