package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.optim.InitialGuess;
import org.hipparchus.optim.MaxEval;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunction;
import org.hipparchus.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.hipparchus.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.numerics.functions.GammaJS;

/**
 * Maximum likelihood estimates of the symbolic parameters of a distribution for
 * <code>FindDistributionParameters(data, dist)</code> and
 * <code>EstimatedDistribution(data, dist)</code>.
 *
 * <p>
 * The normal, log-normal, exponential, Poisson, Laplace and inverse Gaussian distributions have
 * closed forms, the gamma and Weibull distributions reduce to one equation for the shape parameter
 * which is solved by Newton's method; every other distribution maximizes its log-likelihood
 * numerically, starting at the given start values (default <code>1</code>).
 *
 * <p>
 * The logarithms are taken with {@link FastMath#log(double)}: <code>Math.log(3.0)</code> for
 * example is one unit in the last place below the correctly rounded value.
 */
public final class MaximumLikelihood {

  private MaximumLikelihood() {}

  /**
   * The estimates <code>{p1 -&gt; v1, p2 -&gt; v2, ...}</code> for
   * <code>FindDistributionParameters(data, dist, {{p1, start1}, ...}, options)</code>.
   *
   * @return {@link F#NIL} if the call is not supported or no estimate was found
   */
  public static IExpr estimate(IAST ast, EvalEngine engine) {
    int argSize = ast.argSize();
    while (argSize > 2 && ast.get(argSize).isRuleAST()) {
      // only the maximum likelihood method is available; ParameterEstimator is no built-in symbol,
      // in relaxed syntax its name is lower case
      IExpr option = ast.get(argSize);
      if (!option.first().isSymbol()
          || !option.first().toString().equalsIgnoreCase("ParameterEstimator")
          || !(option.second().isString("MaximumLikelihood")
              || option.second() == S.Automatic)) {
        return F.NIL;
      }
      argSize--;
    }
    if (argSize > 3 || !ast.arg2().isAST() || !ast.arg2().isDistribution()) {
      return F.NIL;
    }
    double[] data = ast.arg1().toDoubleVector();
    if (data == null || data.length == 0) {
      return F.NIL;
    }
    IAST dist = (IAST) ast.arg2();
    // the parameters to estimate are the arguments of dist which are variables
    IASTAppendable parameters = F.ListAlloc(dist.argSize());
    for (IExpr arg : dist) {
      if (arg.isVariable()) {
        parameters.append(arg);
      }
    }
    if (parameters.isEmpty()) {
      return F.NIL;
    }
    double[] start = new double[parameters.argSize()];
    Arrays.fill(start, 1.0);
    if (argSize == 3 && !startValues(ast.arg3(), parameters, start)) {
      return F.NIL;
    }
    double[] values;
    try {
      values = closedForm(dist, data);
      if (values == null) {
        values = numeric(dist, parameters, data, start, engine);
      }
    } catch (MathRuntimeException mrex) {
      Errors.rethrowsInterruptException(mrex);
      return F.NIL;
    }
    if (values == null) {
      return F.NIL;
    }
    final double[] estimates = values;
    return F.mapRange(1, parameters.size(), i -> F.Rule(parameters.get(i), F.num(estimates[i - 1])));
  }

  /** Read <code>{{p1, start1}, ...}</code> or <code>{p, start}</code> into <code>start</code>. */
  private static boolean startValues(IExpr spec, IAST parameters, double[] start) {
    if (!spec.isList()) {
      return false;
    }
    IAST list = (IAST) spec;
    if (list.isList2() && !list.arg1().isList()) {
      list = F.list(list);
    }
    for (IExpr pair : list) {
      if (!pair.isList2()) {
        return false;
      }
      int index = parameters.indexOf(pair.first());
      double value = pair.second().evalfNaN();
      if (index < 1 || Double.isNaN(value)) {
        return false;
      }
      start[index - 1] = value;
    }
    return true;
  }

  /**
   * The estimates of the distributions with a closed form or a single equation, if all their
   * parameters are free; for a normal distribution with a fixed parameter the other one.
   *
   * @return <code>null</code> if there is no such form
   */
  private static double[] closedForm(IAST dist, double[] data) {
    int n = data.length;
    double mean = mean(data);
    boolean allFree = dist.forAll(x -> x.isVariable());
    if (dist.isAST(S.NormalDistribution, 3)) {
      IExpr m = dist.arg1();
      IExpr s = dist.arg2();
      if (allFree) {
        return new double[] {mean, Math.sqrt(squares(data, mean) / n)};
      }
      if (m.isVariable() && s.isNumber()) {
        return new double[] {mean};
      }
      double fixedMean = m.evalfNaN();
      if (s.isVariable() && !Double.isNaN(fixedMean)) {
        return new double[] {Math.sqrt(squares(data, fixedMean) / n)};
      }
      return null;
    }
    if (!allFree) {
      return null;
    }
    if (dist.isAST(S.LogNormalDistribution, 3)) {
      double[] logs = new double[n];
      for (int i = 0; i < n; i++) {
        if (!(data[i] > 0.0)) {
          return null;
        }
        logs[i] = FastMath.log(data[i]);
      }
      double logMean = mean(logs);
      return new double[] {logMean, Math.sqrt(squares(logs, logMean) / n)};
    }
    if (dist.isAST(S.ExponentialDistribution, 2)) {
      return mean > 0.0 ? new double[] {1.0 / mean} : null;
    }
    if (dist.isAST(S.PoissonDistribution, 2)) {
      return mean >= 0.0 ? new double[] {mean} : null;
    }
    if (dist.isAST(S.LaplaceDistribution, 3)) {
      double[] sorted = data.clone();
      Arrays.sort(sorted);
      double median = (n % 2 == 1) ? sorted[n / 2] //
          : (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
      double meanAbsoluteDeviation = 0.0;
      for (int i = 0; i < n; i++) {
        meanAbsoluteDeviation += Math.abs(sorted[i] - median);
      }
      return new double[] {median, meanAbsoluteDeviation / n};
    }
    if (dist.isAST(S.GammaDistribution, 3)) {
      return gamma(data, mean);
    }
    if (dist.isAST(S.WeibullDistribution, 3)) {
      return weibull(data);
    }
    if (dist.isAST(S.InverseGaussianDistribution, 3)) {
      // the mean, and 1/l == mean(1/x - 1/mean)
      double sum = 0.0;
      for (double x : data) {
        if (!(x > 0.0)) {
          return null;
        }
        sum += 1.0 / x - 1.0 / mean;
      }
      return sum > 0.0 ? new double[] {mean, n / sum} : null;
    }
    return null;
  }

  /**
   * The shape <code>a</code> solves <code>Log(a) - PolyGamma(a) == Log(mean) - mean(Log(x))</code>,
   * the scale is <code>mean/a</code>.
   */
  private static double[] gamma(double[] data, double mean) {
    double logMean = 0.0;
    for (double x : data) {
      if (!(x > 0.0)) {
        return null;
      }
      logMean += FastMath.log(x);
    }
    logMean /= data.length;
    double s = FastMath.log(mean) - logMean;
    if (!(s > 0.0)) {
      return null;
    }
    // the approximation of Minka (2002) as start value
    double a = (3.0 - s + Math.sqrt((s - 3.0) * (s - 3.0) + 24.0 * s)) / (12.0 * s);
    for (int i = 0; i < 100; i++) {
      double f = FastMath.log(a) - GammaJS.digamma(a) - s;
      double df = 1.0 / a - trigamma(a);
      double next = a - f / df;
      if (!(next > 0.0)) {
        next = a / 2.0;
      }
      if (Math.abs(next - a) <= 1e-15 * a) {
        a = next;
        break;
      }
      a = next;
    }
    return new double[] {a, mean / a};
  }

  /**
   * <code>PolyGamma(1, x)</code> for <code>x &gt; 0</code>, shifted up to 12 with the recurrence
   * and summed there with its asymptotic series.
   */
  private static double trigamma(double x) {
    double result = 0.0;
    while (x < 12.0) {
      result += 1.0 / (x * x);
      x += 1.0;
    }
    double t = 1.0 / (x * x);
    return result + 1.0 / x
        + t * (0.5 + (1.0 / 6.0 - (1.0 / 30.0 - (1.0 / 42.0 - t / 30.0) * t) * t) / x);
  }

  /**
   * The shape <code>k</code> solves
   * <code>1/k + mean(Log(x)) - Sum(x^k*Log(x))/Sum(x^k) == 0</code>, the scale is
   * <code>mean(x^k)^(1/k)</code>.
   */
  private static double[] weibull(double[] data) {
    int n = data.length;
    double[] logs = new double[n];
    double logMean = 0.0;
    double logMax = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < n; i++) {
      if (!(data[i] > 0.0)) {
        return null;
      }
      logs[i] = FastMath.log(data[i]);
      logMean += logs[i];
      logMax = Math.max(logMax, logs[i]);
    }
    logMean /= n;
    double k = 1.0;
    for (int iteration = 0; iteration < 200; iteration++) {
      // x^k scaled by max(x)^k against overflow
      double s0 = 0.0;
      double s1 = 0.0;
      double s2 = 0.0;
      for (int i = 0; i < n; i++) {
        double w = FastMath.exp(k * (logs[i] - logMax));
        s0 += w;
        s1 += w * logs[i];
        s2 += w * logs[i] * logs[i];
      }
      double f = 1.0 / k + logMean - s1 / s0;
      double df = -1.0 / (k * k) - (s2 * s0 - s1 * s1) / (s0 * s0);
      double next = k - f / df;
      if (!(next > 0.0)) {
        next = k / 2.0;
      }
      if (Math.abs(next - k) <= 1e-15 * k) {
        k = next;
        break;
      }
      k = next;
    }
    double s0 = 0.0;
    for (int i = 0; i < n; i++) {
      s0 += FastMath.exp(k * (logs[i] - logMax));
    }
    return new double[] {k, FastMath.exp(logMax + FastMath.log(s0 / n) / k)};
  }

  /** Maximize the log-likelihood with the simplex method of Nelder and Mead. */
  private static double[] numeric(IAST dist, IAST parameters, double[] data, double[] start,
      EvalEngine engine) {
    IAST dataList = F.mapRange(0, data.length, i -> F.num(data[i]));
    ObjectiveFunction negativeLogLikelihood = new ObjectiveFunction(point -> {
      IASTAppendable rules = F.ListAlloc(point.length);
      for (int i = 0; i < point.length; i++) {
        rules.append(F.Rule(parameters.get(i + 1), F.num(point[i])));
      }
      IExpr numericDist = F.subst(dist, rules);
      double value = engine.evalN(F.binaryAST2(S.LogLikelihood, numericDist, dataList)).evalfNaN();
      return Double.isFinite(value) ? -value : Double.MAX_VALUE;
    });
    double[] steps = new double[start.length];
    for (int i = 0; i < start.length; i++) {
      steps[i] = Math.max(0.1, 0.5 * Math.abs(start[i]));
    }
    // the simplex leaves the region of valid parameters now and then: no messages for that
    final boolean quiet = engine.isQuietMode();
    engine.setQuietMode(true);
    try {
      if (negativeLogLikelihood.getObjectiveFunction().value(start) == Double.MAX_VALUE) {
        return null;
      }
      double[] point = start;
      // restart from the optimum until it doesn't move any more
      for (int restart = 0; restart < 5; restart++) {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-15, 1e-15);
        PointValuePair optimum = optimizer.optimize(new MaxEval(20000), negativeLogLikelihood,
            GoalType.MINIMIZE, new InitialGuess(point), new NelderMeadSimplex(steps));
        double[] next = optimum.getPoint();
        double change = 0.0;
        for (int i = 0; i < next.length; i++) {
          change =
              Math.max(change, Math.abs(next[i] - point[i]) / Math.max(1.0, Math.abs(next[i])));
          steps[i] = Math.max(1e-6, 0.01 * Math.abs(next[i]));
        }
        point = next;
        if (change < 1e-12) {
          break;
        }
      }
      if (negativeLogLikelihood.getObjectiveFunction().value(point) == Double.MAX_VALUE) {
        return null;
      }
      return point;
    } finally {
      engine.setQuietMode(quiet);
    }
  }

  /** The mean as a left to right sum. */
  private static double mean(double[] data) {
    double sum = 0.0;
    for (double x : data) {
      sum += x;
    }
    return sum / data.length;
  }

  private static double squares(double[] data, double mean) {
    double sum = 0.0;
    for (double x : data) {
      sum += (x - mean) * (x - mean);
    }
    return sum;
  }
}
