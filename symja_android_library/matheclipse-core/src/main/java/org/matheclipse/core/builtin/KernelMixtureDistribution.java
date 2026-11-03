package org.matheclipse.core.builtin;

import java.util.Arrays;
import java.util.Random;
import org.hipparchus.special.Erf;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>KernelMixtureDistribution(data)</code>, <code>KernelMixtureDistribution(data, bw)</code> and
 * <code>KernelMixtureDistribution(data, bw, "Gaussian")</code> - the mixture with equal weights of
 * normal kernels of the width <code>bw</code> centered at the data. It evaluates to the container
 * <code>DataDistribution("KernelMixture", {1/n, "Gaussian", sorted data, bw}, 1, n)</code>.
 *
 * <p>
 * The automatic bandwidth is the rule <code>0.9 Min(sigma, iqr/1.34) n^(-1/5)</code> of the sample
 * standard deviation and the interquartile range, which is ignored if it vanishes; a vanishing
 * standard deviation counts as 1. The bandwidth is at least half of the smallest gap between
 * distinct data values. It is exact for exact data.
 */
public final class KernelMixtureDistribution extends AbstractFunctionEvaluator {

  private static final String KERNEL_MIXTURE = "KernelMixture";

  private static final String GAUSSIAN = "Gaussian";

  /** The steps of the bisection of a quantile, enough for the last bit of a machine number. */
  private static final int QUANTILE_STEPS = 200;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!ast.arg1().isList() || ast.arg1().argSize() < 2) {
      return F.NIL;
    }
    if (ast.isAST3() && !ast.arg3().isString(GAUSSIAN)) {
      return F.NIL;
    }
    IAST data = (IAST) ast.arg1();
    final int n = data.argSize();
    double[] keys = new double[n];
    Integer[] order = new Integer[n];
    for (int i = 0; i < n; i++) {
      keys[i] = data.get(i + 1).evalfNaN();
      order[i] = i;
      if (!Double.isFinite(keys[i])) {
        return F.NIL;
      }
    }
    Arrays.sort(order, (a, b) -> Double.compare(keys[a], keys[b]));
    IAST sorted = F.mapRange(0, n, i -> data.get(order[i] + 1));
    IExpr bandwidth = ast.argSize() >= 2 ? ast.arg2() : S.Automatic;
    if (bandwidth == S.Automatic || bandwidth.isString("Silverman")) {
      bandwidth = automaticBandwidth(sorted, engine);
    } else if (!(bandwidth.evalfNaN() > 0.0)) {
      return F.NIL;
    }
    if (bandwidth.isNIL()) {
      return F.NIL;
    }
    IExpr weight = isInexact(sorted) ? F.num(1.0 / n) : F.QQ(1, n);
    return F.quaternary(S.DataDistribution, F.stringx(KERNEL_MIXTURE),
        F.List(weight, F.stringx(GAUSSIAN), sorted, bandwidth), F.C1, F.ZZ(n));
  }

  private static boolean isInexact(IAST data) {
    return data.exists(x -> x.isInexactNumber());
  }

  /** The bandwidth of the sorted data, {@link F#NIL} if the statistics aren't numbers. */
  private static IExpr automaticBandwidth(IAST sorted, EvalEngine engine) {
    final int n = sorted.argSize();
    IExpr iqr = engine.evaluate(F.Subtract(quartile(sorted, 3), quartile(sorted, 1)));
    IExpr variance = engine.evaluate(F.Variance(sorted));
    double iqrValue = iqr.evalfNaN();
    double varianceValue = variance.evalfNaN();
    if (!Double.isFinite(iqrValue) || !Double.isFinite(varianceValue)) {
      return F.NIL;
    }
    // iqr/1.34 is the spread if it doesn't exceed the standard deviation
    boolean useIqr = iqrValue > 0.0 && Math.pow(iqrValue * 50.0 / 67.0, 2) < varianceValue;
    double minimalGap = Double.POSITIVE_INFINITY;
    int gapIndex = 0;
    for (int i = 1; i < n; i++) {
      double gap = sorted.get(i + 1).evalfNaN() - sorted.get(i).evalfNaN();
      if (gap > 0.0 && gap < minimalGap) {
        minimalGap = gap;
        gapIndex = i;
      }
    }
    if (isInexact(sorted)) {
      double spread = useIqr ? iqrValue * (50.0 / 67.0)
          : varianceValue > 0.0 ? Math.sqrt(varianceValue) : 1.0;
      double h = spread * Math.pow(n, -0.2) * 0.9;
      return F.num(minimalGap < Double.POSITIVE_INFINITY ? Math.max(h, minimalGap / 2.0) : h);
    }
    IExpr spreadSquared = useIqr ? F.Sqr(F.Times(iqr, F.QQ(50, 67)))
        : varianceValue > 0.0 ? variance : F.C1;
    spreadSquared = engine.evaluate(spreadSquared);
    IExpr h = engine.evaluate(F.Times(F.QQ(9, 10), F.Sqrt(engine.evaluate(F.Numerator(spreadSquared))),
        F.Power(F.Sqrt(engine.evaluate(F.Denominator(spreadSquared))), F.CN1),
        F.Power(F.ZZ(n), F.QQ(-1, 5))));
    if (h.evalfNaN() < minimalGap / 2.0) {
      return engine
          .evaluate(F.Times(F.C1D2, F.Subtract(sorted.get(gapIndex + 1), sorted.get(gapIndex))));
    }
    return h;
  }

  /** The data value at the position <code>Ceiling(n k/4)</code>. */
  private static IExpr quartile(IAST sorted, int k) {
    final int n = sorted.argSize();
    int position = (n * k + 3) / 4;
    return sorted.get(Math.max(1, Math.min(position, n)));
  }

  /** Test if <code>dist</code> is the container of a Gaussian kernel mixture. */
  static boolean isKernelMixture(IAST dist) {
    return dist.argSize() == 4 && dist.arg1().isString(KERNEL_MIXTURE) && dist.arg2().isList()
        && dist.arg2().argSize() == 4 && dist.arg2().second().isString(GAUSSIAN)
        && dist.arg2().getAt(3).isList();
  }

  private static IExpr weight(IAST dist) {
    return dist.arg2().first();
  }

  private static IAST data(IAST dist) {
    return (IAST) dist.arg2().getAt(3);
  }

  private static IExpr bandwidth(IAST dist) {
    return dist.arg2().getAt(4);
  }

  /** The sum over the distinct data values of their multiplicity times the term of the value. */
  private static IAST distinctSum(IAST data, java.util.function.Function<IExpr, IExpr> term) {
    IASTAppendable sum = F.PlusAlloc(data.argSize());
    int i = 1;
    while (i < data.size()) {
      IExpr value = data.get(i);
      int count = 1;
      while (i + count < data.size() && data.get(i + count).equals(value)) {
        count++;
      }
      sum.append(F.Times(F.ZZ(count), term.apply(value)));
      i += count;
    }
    return sum;
  }

  /** The density <code>w/h Sum(c(i) Exp(-(x - v(i))^2/(2 h^2))/Sqrt(2 Pi))</code>. */
  static IExpr pdf(IAST dist, IExpr x, EvalEngine engine) {
    if (x.isNIL()) {
      return F.NIL;
    }
    IExpr h = bandwidth(dist);
    IExpr sum = distinctSum(data(dist), value -> F.Times(F.Power(F.Sqrt(F.Times(F.C2, S.Pi)), F.CN1),
        F.Exp(F.Times(F.CN1, F.Power(F.Times(F.C2, F.Sqr(h)), F.CN1), F.Sqr(F.Subtract(x, value))))));
    return engine.evaluate(F.Times(weight(dist), F.Power(h, F.CN1), sum));
  }

  /** The cumulative distribution <code>w Sum(c(i) Erfc(-(x - v(i))/(Sqrt(2) h))/2)</code>. */
  static IExpr cdf(IAST dist, IExpr x, EvalEngine engine) {
    if (x.isNIL()) {
      return F.NIL;
    }
    IExpr h = bandwidth(dist);
    IExpr sum = distinctSum(data(dist), value -> F.Times(F.C1D2, F.Erfc(F.Times(F.CN1,
        F.Power(F.Times(F.Sqrt(F.C2), h), F.CN1), F.Subtract(x, value)))));
    return engine.evaluate(F.Times(weight(dist), sum));
  }

  static IExpr mean(IAST dist, EvalEngine engine) {
    return engine.evaluate(F.Mean(data(dist)));
  }

  /** The population variance of the data plus the squared bandwidth. */
  static IExpr variance(IAST dist, EvalEngine engine) {
    IAST data = data(dist);
    final int n = data.argSize();
    IExpr population = F.Times(F.QQ(n - 1, n), F.Variance(data));
    return engine.evaluate(F.Together(F.Plus(population, F.Sqr(bandwidth(dist)))));
  }

  private static double[] values(IAST dist) {
    IAST data = data(dist);
    double[] values = new double[data.argSize()];
    for (int i = 0; i < values.length; i++) {
      values[i] = data.get(i + 1).evalfNaN();
    }
    return values;
  }

  private static double machineCdf(double[] values, double h, double x) {
    double sum = 0.0;
    for (double value : values) {
      sum += 0.5 * Erf.erfc(-(x - value) / (Math.sqrt(2.0) * h));
    }
    return sum / values.length;
  }

  /** The point where the increasing cumulative distribution reaches <code>q</code>. */
  static IExpr quantile(IAST dist, IExpr q, EvalEngine engine) {
    double p = q.evalfNaN();
    double h = bandwidth(dist).evalfNaN();
    if (Double.isNaN(p) || Double.isNaN(h)) {
      return F.NIL;
    }
    if (p < 0.0 || p > 1.0) {
      return S.Undefined;
    }
    if (p == 0.0) {
      return F.CNInfinity;
    }
    if (p == 1.0) {
      return F.CInfinity;
    }
    double[] values = values(dist);
    double lo = values[0] - h;
    double hi = values[values.length - 1] + h;
    while (machineCdf(values, h, lo) > p) {
      engine.checkTimeLimit();
      lo -= 2.0 * (hi - lo);
    }
    while (machineCdf(values, h, hi) < p) {
      engine.checkTimeLimit();
      hi += 2.0 * (hi - lo);
    }
    for (int i = 0; i < QUANTILE_STEPS; i++) {
      double middle = lo + 0.5 * (hi - lo);
      if (middle <= lo || middle >= hi) {
        break;
      }
      if (machineCdf(values, h, middle) < p) {
        lo = middle;
      } else {
        hi = middle;
      }
    }
    return F.num(lo + 0.5 * (hi - lo));
  }

  /** A random data value plus a normal deviate of the width of the bandwidth. */
  static IExpr randomVariate(Random random, IAST dist, int size) {
    double[] values = values(dist);
    double h = bandwidth(dist).evalfNaN();
    if (Double.isNaN(h)) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(size);
    for (int i = 0; i < size; i++) {
      result.append(F.num(values[random.nextInt(values.length)] + h * random.nextGaussian()));
    }
    return size == 1 ? result.arg1() : result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
