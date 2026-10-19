package org.matheclipse.core.builtin.graphics;

import java.util.Arrays;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
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
 * probability <code>(i - 0.3)/(n + 0.4)</code>.
 */
public class QuantilePlot extends AbstractFunctionEvaluator {

  public QuantilePlot() {}

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
    // a single list of numbers, or a list of such lists
    IAST datasets = arg1.first().isList() ? (IAST) arg1 : F.list(arg1);
    IASTAppendable series = F.ListAlloc(datasets.argSize());
    IASTAppendable lines = F.ListAlloc(datasets.argSize());
    for (int k = 1; k <= datasets.argSize(); k++) {
      double[] data = numbers(datasets.get(k));
      if (data == null || data.length == 0) {
        return F.NIL;
      }
      double[][] points = points(data, reference, engine);
      if (points == null) {
        return F.NIL;
      }
      IASTAppendable pairs = F.ListAlloc(points.length);
      for (double[] point : points) {
        pairs.append(F.list(F.num(point[0]), F.num(point[1])));
      }
      series.append(pairs);
      IExpr line = referenceLine(points, k - 1, ast, firstOption);
      if (line.isPresent()) {
        lines.append(line);
      }
    }
    IASTAppendable listPlot = F.ast(S.ListPlot, ast.argSize() + 3);
    listPlot.append(datasets.argSize() == 1 ? series.arg1() : series);
    boolean frame = false;
    boolean axes = false;
    IExpr epilog = F.NIL;
    for (int i = firstOption; i <= ast.argSize(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST()) {
        continue;
      }
      if (option.first() == S.Epilog) {
        // the reference lines are drawn along with the epilog of the call
        epilog = option.second();
        continue;
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
    listPlot.append(F.Rule(S.Epilog, epilog.isPresent() ? F.list(lines, epilog) : lines));
    return engine.evaluate(listPlot);
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

  /**
   * The points <code>{quantile, value}</code> of the sorted data.
   *
   * @param reference {@link F#NIL} for a normal distribution fitted to the data, a distribution,
   *        or a list of numbers
   * @return {@code null} if a quantile isn't a real number
   */
  private static double[][] points(double[] data, IExpr reference, EvalEngine engine) {
    double[] sorted = data.clone();
    Arrays.sort(sorted);
    final int n = sorted.length;
    org.hipparchus.distribution.continuous.NormalDistribution normal = null;
    if (reference.isNIL()) {
      double mean = 0.0;
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
      if (!(sd > 0.0)) {
        // equal values: every quantile of the fitted distribution is their mean
        double[][] points = new double[n][];
        for (int i = 0; i < n; i++) {
          points[i] = new double[] {mean, sorted[i]};
        }
        return points;
      }
      normal = new org.hipparchus.distribution.continuous.NormalDistribution(mean, sd);
    }
    double[] referenceData = reference.isList() ? numbers(reference) : null;
    if (reference.isList()) {
      if (referenceData == null || referenceData.length == 0) {
        return null;
      }
      Arrays.sort(referenceData);
    }
    double[][] points = new double[n][];
    for (int i = 1; i <= n; i++) {
      double p = (i - 0.3) / (n + 0.4);
      double x;
      if (normal != null) {
        x = normal.inverseCumulativeProbability(p);
      } else if (referenceData != null) {
        // the element at Ceiling(m*p) of the sorted reference, as Quantile gives it
        final int m = referenceData.length;
        x = referenceData[Math.max(0, Math.min(m - 1, (int) Math.ceil(m * p) - 1))];
      } else {
        x = engine.evalN(F.InverseCDF(reference, F.num(p))).evalfNaN();
      }
      if (!Double.isFinite(x)) {
        return null;
      }
      points[i - 1] = new double[] {x, sorted[i - 1]};
    }
    return points;
  }

  /**
   * The dashed line through the points at the first and the third quartile of the data, drawn far
   * beyond the data so that the picture cuts it off, in the colour of the series.
   */
  private static IExpr referenceLine(double[][] points, int index, IAST ast, int firstOption) {
    final int n = points.length;
    int lower = (int) Math.ceil(n / 4.0) - 1;
    int upper = (int) Math.ceil(3.0 * n / 4.0) - 1;
    if (lower < 0 || upper <= lower || points[upper][0] == points[lower][0]) {
      return F.NIL;
    }
    double slope = (points[upper][1] - points[lower][1]) / (points[upper][0] - points[lower][0]);
    double span = Math.max(1.0, points[n - 1][0] - points[0][0]);
    double x1 = points[0][0] - 10.0 * span;
    double x2 = points[n - 1][0] + 10.0 * span;
    double y1 = points[lower][1] + slope * (x1 - points[lower][0]);
    double y2 = points[lower][1] + slope * (x2 - points[lower][0]);
    IExpr plotStyle = S.Automatic;
    for (int i = firstOption; i <= ast.argSize(); i++) {
      if (ast.get(i).isRuleAST() && ast.get(i).first() == S.PlotStyle) {
        plotStyle = ast.get(i).second();
      }
    }
    IExpr color = plotStyle.isAutomatic()
        ? GraphicsOptions.plotStyleDirective(index, F.NIL, 2.0)
        : GraphicsOptions.getPlotStyle(plotStyle, index);
    return F.List(color, F.Dashing(F.list(F.C0, S.Small)),
        F.Line(F.list(F.list(F.num(x1), F.num(y1)), F.list(F.num(x2), F.num(y2)))));
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
