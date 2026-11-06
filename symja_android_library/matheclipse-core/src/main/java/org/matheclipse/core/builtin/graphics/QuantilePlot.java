package org.matheclipse.core.builtin.graphics;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.hipparchus.distribution.continuous.NormalDistribution;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.svg.ScaledTicks;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>QuantilePlot(data)</code>, <code>QuantilePlot({data1, data2, ...})</code>,
 * <code>QuantilePlot(data, dist)</code> or <code>QuantilePlot(data, ref)</code> - the sorted data
 * against the quantiles of a normal distribution with the mean and standard deviation of the data,
 * of the distribution <code>dist</code> or of the data <code>ref</code>, each with a dashed line
 * through the points of the two quartiles of the data.
 *
 * <p>
 * The <code>i</code>-th of <code>n</code> sorted values is set against the quantile at the
 * probability <code>(i - 0.3)/(n + 0.4)</code>. Two invisible points on each reference line widen
 * the plot range so that the line runs beyond the data.
 *
 * <p>
 * {@link ProbabilityPlot} and {@link ProbabilityScalePlot} share this layout.
 */
public class QuantilePlot extends AbstractFunctionEvaluator {

  /** What the sorted data is set against. */
  protected enum Kind {
    /** The quantiles of the reference against the data. */
    QUANTILE,
    /** The cumulative probabilities of the reference against those of the data. */
    PROBABILITY,
    /** The data against the normal quantiles of its plotting positions. */
    PROBABILITY_SCALE
  }

  /** How far the diagonal of a probability plot stays inside the unit square. */
  private static final double DIAGONAL_INSET = 1.0 / 49_000_000.0;

  private final Kind kind;

  public QuantilePlot() {
    this(Kind.QUANTILE);
  }

  protected QuantilePlot(Kind kind) {
    this.kind = kind;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (!arg1.isList() || arg1.argSize() == 0) {
      return F.NIL;
    }
    int firstOption = 2;
    IExpr reference = F.NIL;
    if (ast.argSize() >= 2 && !ast.arg2().isRuleAST()) {
      reference = ast.arg2();
      firstOption = 3;
    }
    for (int i = firstOption; i <= ast.argSize(); i++) {
      if (!ast.get(i).isRuleAST()) {
        return F.NIL;
      }
    }
    boolean weibull = false;
    if (kind == Kind.PROBABILITY_SCALE && reference.isPresent()) {
      // the normal and the Weibull scale
      weibull = reference.isString("Weibull");
      if (!weibull && !reference.isString("Normal")) {
        return F.NIL;
      }
      reference = F.NIL;
    }
    // a single list of numbers, or a list of such lists
    IAST datasets = arg1.first().isList() ? (IAST) arg1 : F.list(arg1);
    double[][][] series = new double[datasets.argSize()][][];
    for (int k = 1; k <= datasets.argSize(); k++) {
      double[] data = numbers(datasets.get(k));
      if (data == null || data.length == 0) {
        return F.NIL;
      }
      Arrays.sort(data);
      series[k - 1] = weibull ? weibullPoints(data) : points(data, reference, engine);
      if (series[k - 1] == null) {
        if (weibull) {
          // Unable to estimate a reference distribution from the data.
          Errors.printMessage(ast.topHead(), "estmd", F.CEmptyList, engine);
        }
        return F.NIL;
      }
    }

    IExpr plotStyle = S.Automatic;
    IExpr epilog = F.NIL;
    boolean frame = false;
    boolean axes = false;
    IASTAppendable listPlot = F.ast(S.ListPlot, ast.argSize() + 5);
    IASTAppendable seriesList = F.ListAlloc(series.length);
    for (double[][] points : series) {
      seriesList.append(pointList(points));
    }
    listPlot.append(series.length == 1 ? seriesList.arg1() : seriesList);
    for (int i = firstOption; i <= ast.argSize(); i++) {
      IExpr option = ast.get(i);
      if (option.first() == S.Epilog) {
        // the reference lines are drawn along with the epilog of the call
        epilog = option.second();
        continue;
      }
      if (option.first() == S.PlotStyle) {
        plotStyle = option.second();
        if (kind == Kind.PROBABILITY_SCALE) {
          // replaced by the styles with the larger points below
          continue;
        }
      }
      frame |= option.first() == S.Frame;
      axes |= option.first() == S.Axes;
      listPlot.append(option);
    }
    if (!frame) {
      listPlot.append(F.Rule(S.Frame, S.True));
    }
    if (!axes) {
      listPlot.append(F.Rule(S.Axes, S.False));
    }

    IASTAppendable lines = F.ListAlloc(series.length);
    IASTAppendable hidden = F.ListAlloc(series.length + 1);
    hidden.append(F.Opacity(F.C0));
    referenceLines(series, plotStyle, lines, hidden);
    listPlot.append(F.Rule(S.Epilog, epilog.isPresent() ? F.list(lines, epilog) : lines));
    if (kind == Kind.PROBABILITY_SCALE) {
      appendScaleOptions(listPlot, series, plotStyle, weibull);
    }
    IExpr plot = engine.evaluate(listPlot);
    if (hidden.argSize() == 1 || !plot.isAST(S.Graphics)) {
      return plot;
    }
    IAST invisible = F.unaryAST1(S.Graphics, hidden);
    return engine.evaluate(F.binaryAST2(S.Show, plot, invisible));
  }

  /** The real numbers of a list, or {@code null} if it has anything else. */
  private static double[] numbers(IExpr list) {
    if (!list.isList()) {
      return null;
    }
    double[] result = new double[list.argSize()];
    for (int i = 1; i <= list.argSize(); i++) {
      result[i - 1] = list.getAt(i).evalfNaN();
      if (!Double.isFinite(result[i - 1])) {
        return null;
      }
    }
    return result;
  }

  private static IAST pointList(double[][] points) {
    IASTAppendable pairs = F.ListAlloc(points.length);
    for (double[] point : points) {
      pairs.append(F.list(F.num(point[0]), F.num(point[1])));
    }
    return pairs;
  }

  /**
   * The points of the sorted data.
   *
   * @param reference {@link F#NIL} for a normal distribution fitted to the data, a distribution,
   *        or a list of numbers
   * @return {@code null} if a coordinate isn't a real number
   */
  private double[][] points(double[] sorted, IExpr reference, EvalEngine engine) {
    final int n = sorted.length;
    double mean = 0.0;
    NormalDistribution normal = null;
    if (reference.isNIL() && kind != Kind.PROBABILITY_SCALE) {
      for (double value : sorted) {
        mean += value;
      }
      mean /= n;
      double variance = 0.0;
      for (double value : sorted) {
        variance += (value - mean) * (value - mean);
      }
      // the standard deviation of the data as a population, which is that of the normal
      // distribution of the largest likelihood
      double sd = Math.sqrt(variance / n);
      if (sd > 0.0) {
        normal = new NormalDistribution(mean, sd);
      }
    }
    double[] referenceData = null;
    if (reference.isList()) {
      referenceData = numbers(reference);
      if (referenceData == null || referenceData.length == 0) {
        return null;
      }
      Arrays.sort(referenceData);
    }
    double[][] points = new double[n][];
    // the number of reference values up to the current value
    int below = 0;
    for (int i = 1; i <= n; i++) {
      double y = sorted[i - 1];
      double p = (i - 0.3) / (n + 0.4);
      double x;
      switch (kind) {
        case PROBABILITY:
          if (referenceData != null) {
            // the values are sorted, so the count only grows
            while (below < referenceData.length && referenceData[below] <= y) {
              below++;
            }
            x = below / (double) referenceData.length;
          } else if (reference.isNIL()) {
            // equal values: the fitted distribution jumps from 0 to 1 at their mean, where it is
            // 1/2
            x = normal != null ? normal.cumulativeProbability(y)
                : (y < mean ? 0.0 : (y > mean ? 1.0 : 0.5));
          } else {
            x = engine.evalN(F.CDF(reference, F.num(y))).evalfNaN();
          }
          points[i - 1] = new double[] {x, i / (double) n};
          break;
        case PROBABILITY_SCALE:
          points[i - 1] = new double[] {y, ScaledTicks.normalY(p)};
          break;
        default:
          if (referenceData != null) {
            // the element at Ceiling(m*p) of the sorted reference, as Quantile gives it
            final int m = referenceData.length;
            x = referenceData[Math.max(0, Math.min(m - 1, (int) Math.ceil(m * p) - 1))];
          } else if (reference.isNIL()) {
            // equal values: every quantile of the fitted distribution is their mean
            x = normal != null ? normal.inverseCumulativeProbability(p) : mean;
          } else {
            x = engine.evalN(F.InverseCDF(reference, F.num(p))).evalfNaN();
          }
          points[i - 1] = new double[] {x, y};
          break;
      }
      if (!Double.isFinite(points[i - 1][0]) || !Double.isFinite(points[i - 1][1])) {
        return null;
      }
    }
    return kind == Kind.PROBABILITY ? lastOfEqualValues(points, sorted) : points;
  }

  /**
   * The points of the sorted data on a Weibull probability scale: the <code>i</code>-th of
   * <code>n</code> values <code>x</code> at <code>{Log(x), Log(-Log(1 - p))}</code> with the
   * plotting position <code>p = (i - 0.3)/(n + 0.4)</code>.
   *
   * @return {@code null} if a value is not positive, which no Weibull distribution can produce
   */
  private static double[][] weibullPoints(double[] sorted) {
    final int n = sorted.length;
    if (sorted[0] <= 0.0) {
      return null;
    }
    double[][] points = new double[n][];
    for (int i = 1; i <= n; i++) {
      double p = (i - 0.3) / (n + 0.4);
      points[i - 1] = new double[] {Math.log(sorted[i - 1]), ScaledTicks.weibullY(p)};
    }
    return points;
  }

  /**
   * The points of a probability plot with a single point for equal values, the one of the largest
   * cumulative probability. A single value is drawn as two equal points.
   */
  private static double[][] lastOfEqualValues(double[][] points, double[] sorted) {
    final int n = points.length;
    int count = 0;
    double[][] result = new double[n][];
    for (int i = 0; i < n; i++) {
      if (i + 1 == n || sorted[i + 1] != sorted[i]) {
        result[count++] = points[i];
      }
    }
    if (count == 1) {
      return new double[][] {result[0], result[0]};
    }
    return Arrays.copyOf(result, count);
  }

  /**
   * The dashed reference line of every series, in the colour of the series, and a
   * <code>Point</code> of two invisible points per series which stretch the plot range along it.
   *
   * <p>
   * A probability plot gets the diagonal of the unit square. Otherwise the line runs through the
   * points of the first and the third quartile of the coordinates; it ends at the smallest and the
   * largest value of a probability scale plot and runs far beyond the data of a quantile plot, so
   * that the picture cuts it off.
   */
  private void referenceLines(double[][][] series, IExpr plotStyle, IASTAppendable lines,
      IASTAppendable hidden) {
    double xLo = Double.POSITIVE_INFINITY;
    double xHi = Double.NEGATIVE_INFINITY;
    for (double[][] points : series) {
      for (double[] point : points) {
        xLo = Math.min(xLo, point[0]);
        xHi = Math.max(xHi, point[0]);
      }
    }
    for (int k = 0; k < series.length; k++) {
      double[][] points = series[k];
      final int n = points.length;
      double[] from;
      double[] to;
      if (kind == Kind.PROBABILITY) {
        from = new double[] {DIAGONAL_INSET, DIAGONAL_INSET};
        to = new double[] {1.0 - DIAGONAL_INSET, 1.0 - DIAGONAL_INSET};
        hidden.append(F.Point(F.list(pair(from[0], from[1]), pair(to[0], to[1]))));
      } else {
        double[] xs = new double[n];
        double[] ys = new double[n];
        for (int i = 0; i < n; i++) {
          xs[i] = points[i][0];
          ys[i] = points[i][1];
        }
        Arrays.sort(xs);
        Arrays.sort(ys);
        double qx1 = quartile(xs, 0.25);
        double qx3 = quartile(xs, 0.75);
        double qy1 = quartile(ys, 0.25);
        double qy3 = quartile(ys, 0.75);
        if (qx3 == qx1 && qy3 != qy1) {
          continue;
        }
        // equal values: a horizontal line at the value
        double slope = qx3 == qx1 ? 0.0 : (qy3 - qy1) / (qx3 - qx1);
        // the data range, reaching 0 when its lower end is near 0, lowered by 1/48 of its width
        // and padded by 2% on both sides
        double lo = xLo > 0.0 && xLo <= xHi / 6.0 ? 0.0 : xLo;
        double drop = (xHi - lo) / 48.0;
        double width = (xHi - lo + drop) / 0.96;
        double left = lo - drop - 0.02 * width;
        double right = xHi + 0.02 * width;
        hidden.append(F.Point(F.list(pair(left, qy1 + slope * (left - qx1)),
            pair(right, qy1 + slope * (right - qx1)))));
        if (kind == Kind.PROBABILITY_SCALE && slope != 0.0) {
          double y0 = points[0][1];
          double y1 = points[n - 1][1];
          from = new double[] {qx1 + (y0 - qy1) / slope, y0};
          to = new double[] {qx1 + (y1 - qy1) / slope, y1};
        } else {
          double a = xLo - 2.0 * Math.abs(xLo);
          double b = xHi + 2.0 * Math.abs(xHi);
          from = new double[] {a, qy1 + slope * (a - qx1)};
          to = new double[] {b, qy1 + slope * (b - qx1)};
        }
      }
      lines.append(F.List(seriesDirective(k, plotStyle), F.Dashing(F.list(F.C0, S.Small)),
          F.Line(F.list(pair(from[0], from[1]), pair(to[0], to[1])))));
    }
  }

  private static IAST pair(double x, double y) {
    return F.list(F.num(x), F.num(y));
  }

  private static IExpr seriesDirective(int index, IExpr plotStyle) {
    return plotStyle.isAutomatic() ? GraphicsOptions.plotStyleDirective(index, F.NIL, 2.0)
        : GraphicsOptions.getPlotStyle(plotStyle, index);
  }

  /**
   * The quantile <code>q</code> of sorted values interpolated linearly between the values around
   * the position <code>n*q + 1/2</code>.
   */
  private static double quartile(double[] sorted, double q) {
    final int n = sorted.length;
    double position = Math.max(1.0, Math.min(n, n * q + 0.5));
    int lower = (int) Math.floor(position);
    double fraction = position - lower;
    double a = sorted[lower - 1];
    if (fraction == 0.0 || lower >= n) {
      return a;
    }
    return a + fraction * (sorted[lower] - a);
  }

  /**
   * The larger points and the percentage ticks on the vertical frame of a probability scale plot;
   * on a Weibull scale also the ticks of the logarithmic horizontal frame.
   */
  private static void appendScaleOptions(IASTAppendable listPlot, double[][][] series,
      IExpr plotStyle, boolean weibull) {
    double size = pointSizeSteps(series) / 360.0;
    IASTAppendable styles = F.ListAlloc(series.length);
    for (int k = 0; k < series.length; k++) {
      IExpr color = plotStyle.isAutomatic() ? GraphicsOptions.plotStyleColorExpr(k, F.NIL)
          : GraphicsOptions.getPlotStyle(plotStyle, k);
      styles.append(F.Directive(color, F.PointSize(size)));
    }
    listPlot.append(F.Rule(S.PlotStyle, styles));
    // the ticks are symbolic: the renderer places them on the scale of the axis
    EvalEngine engine = EvalEngine.get();
    IAST vertical = ScaledTicks.edges(
        weibull ? ScaledTicks.weibullPair() : ScaledTicks.normalPair(), engine);
    IAST horizontal = weibull ? ScaledTicks.edges(ScaledTicks.logPair(), engine)
        : F.list(S.Automatic, S.Automatic);
    listPlot.append(F.Rule(S.FrameTicks, F.list(vertical, horizontal)));
  }

  /**
   * The size of the dots in printer's points of a 360 points wide picture, from the number of the
   * distinct points: 7 for less than 50 points, 6, 5 and 4 from 50, 100 and 250 points on and 1 for
   * more than 15000 points. In between it depends on how clustered the points are in a 10 x 10
   * grid over their range: the fullest cell over the mean of the non-empty cells.
   */
  private static double pointSizeSteps(double[][][] series) {
    Set<List<Double>> distinct = new LinkedHashSet<>();
    for (double[][] points : series) {
      for (double[] point : points) {
        distinct.add(Arrays.asList(point[0], point[1]));
      }
    }
    final int count = distinct.size();
    if (count < 50) {
      return 7.0;
    } else if (count < 100) {
      return 6.0;
    } else if (count < 250) {
      return 5.0;
    } else if (count < 500) {
      return 4.0;
    } else if (count > 15000) {
      return 1.0;
    }
    double x1 = Double.POSITIVE_INFINITY;
    double x2 = Double.NEGATIVE_INFINITY;
    double y1 = Double.POSITIVE_INFINITY;
    double y2 = Double.NEGATIVE_INFINITY;
    for (List<Double> point : distinct) {
      x1 = Math.min(x1, point.get(0));
      x2 = Math.max(x2, point.get(0));
      y1 = Math.min(y1, point.get(1));
      y2 = Math.max(y2, point.get(1));
    }
    if (x1 == x2) {
      x1 -= 1.0;
      x2 += 1.0;
    }
    if (y1 == y2) {
      y1 -= 1.0;
      y2 += 1.0;
    }
    double wx = (x2 - x1 + 0.0001 * (x2 - x1)) / 10.0;
    double wy = (y2 - y1 + 0.0001 * (y2 - y1)) / 10.0;
    int[][] bins = new int[10][10];
    for (List<Double> point : distinct) {
      double i = Math.floor((point.get(0) - x1) / wx);
      double j = Math.floor((point.get(1) - y1) / wy);
      if (i >= 0.0 && i < 10.0 && j >= 0.0 && j < 10.0) {
        bins[(int) i][(int) j]++;
      }
    }
    int max = 0;
    int sum = 0;
    int filled = 0;
    for (int[] row : bins) {
      for (int c : row) {
        if (c > 0) {
          max = Math.max(max, c);
          sum += c;
          filled++;
        }
      }
    }
    double density = max / (sum / (double) Math.max(1, filled));
    int densityClass = density < 2.0 ? 0 : (density > 10.0 ? 1 : 2);
    double[] table;
    if (count < 1000) {
      table = new double[] {4.0, 4.0, 3.0};
    } else if (count < 2500) {
      table = new double[] {3.0, 3.0, 2.5};
    } else if (count < 5000) {
      table = new double[] {2.5, 2.5, 2.0};
    } else if (count < 10000) {
      table = new double[] {2.0, 2.0, 1.0};
    } else {
      table = new double[] {2.0, 1.0, 1.0};
    }
    return table[densityClass];
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
