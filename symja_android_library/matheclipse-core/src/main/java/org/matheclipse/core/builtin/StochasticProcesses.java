package org.matheclipse.core.builtin;

import java.util.Random;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.CholeskyDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.RandomFunction;

/**
 * The random processes <code>WienerProcess</code>, <code>OrnsteinUhlenbeckProcess</code> and
 * <code>ARMAProcess</code>: <code>proc(t)</code> is the distribution of the process at time
 * <code>t</code>, and {@link RandomFunction} simulates their paths.
 */
public final class StochasticProcesses {

  /** The number of moving average weights summed for an autocovariance at most. */
  private static final int MAX_WEIGHTS = 100_000;

  private StochasticProcesses() {}

  /**
   * <code>WienerProcess(mu, sigma)</code> - Brownian motion with drift <code>mu</code> and
   * volatility <code>sigma</code>, starting at <code>0</code>; <code>WienerProcess()</code> is the
   * standard one.
   */
  public static final class WienerProcess extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // proc(t)
      if (ast.head().isAST(S.WienerProcess) && ast.isAST1()) {
        IAST process = (IAST) ast.head();
        IExpr t = ast.arg1();
        if (process.isAST0()) {
          return F.NormalDistribution(F.C0, F.Sqrt(t));
        }
        if (process.isAST2()) {
          return F.NormalDistribution(F.Times(process.arg1(), t),
              F.Times(process.arg2(), F.Sqrt(t)));
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>OrnsteinUhlenbeckProcess(mu, sigma, theta)</code> - the stationary mean reverting process
   * with long term mean <code>mu</code>, volatility <code>sigma</code> and reversion rate
   * <code>theta</code>; <code>OrnsteinUhlenbeckProcess(mu, sigma, theta, x0)</code> starts at
   * <code>x0</code>.
   */
  public static final class OrnsteinUhlenbeckProcess extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // proc(t)
      if (ast.head().isAST(S.OrnsteinUhlenbeckProcess) && ast.isAST1()) {
        IAST process = (IAST) ast.head();
        IExpr t = ast.arg1();
        if (process.isAST3()) {
          return F.NormalDistribution(process.arg1(),
              F.Times(process.arg2(), F.Power(F.Sqrt(F.Times(F.C2, process.arg3())), F.CN1)));
        }
        if (process.argSize() == 4) {
          IExpr mu = process.arg1();
          IExpr sigma = process.arg2();
          IExpr theta = process.arg3();
          IExpr x0 = process.arg4();
          IExpr decay = F.Exp(F.Times(F.CN1, theta, t));
          return F.NormalDistribution(F.Plus(mu, F.Times(F.Subtract(x0, mu), decay)),
              F.Times(sigma, F.Sqrt(F.Subtract(F.C1, F.Exp(F.Times(F.CN2, theta, t)))),
                  F.Power(F.Sqrt(F.Times(F.C2, theta)), F.CN1)));
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>ARMAProcess({a1, ..., ap}, {b1, ..., bq}, v)</code> - the autoregressive moving average
   * process <code>x(t) = a1 x(t-1) + ... + ap x(t-p) + e(t) + b1 e(t-1) + ... + bq e(t-q)</code>
   * with white noise <code>e</code> of variance <code>v</code>;
   * <code>ARMAProcess(c, {a1, ...}, {b1, ...}, v)</code> adds the constant <code>c</code>.
   * <code>proc(t)</code> of a numeric weakly stationary process is its normal distribution.
   */
  public static final class ARMAProcess extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // proc(t)
      if (ast.head().isAST(S.ARMAProcess) && ast.isAST1()) {
        Arma arma = Arma.of((IAST) ast.head());
        if (arma != null) {
          double[] gamma = arma.autocovariances(1);
          if (gamma != null) {
            return F.NormalDistribution(F.num(arma.mean()), F.num(Math.sqrt(gamma[0])));
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** The numeric parameters of an <code>ARMAProcess</code>. */
  static final class Arma {
    final double c;
    final double[] a;
    final double[] b;
    final double v;

    private Arma(double c, double[] a, double[] b, double v) {
      this.c = c;
      this.a = a;
      this.b = b;
      this.v = v;
    }

    /** The parameters, or <code>null</code> if they are not numbers. */
    static Arma of(IAST process) {
      int offset = process.argSize() == 4 ? 1 : 0;
      if (process.argSize() != 3 + offset) {
        return null;
      }
      double c = offset == 1 ? process.arg1().evalfNaN() : 0.0;
      double[] a = vector(process.get(1 + offset));
      double[] b = vector(process.get(2 + offset));
      double v = process.get(3 + offset).evalfNaN();
      if (a == null || b == null || !Double.isFinite(c) || !(v > 0.0)) {
        return null;
      }
      return new Arma(c, a, b, v);
    }

    private static double[] vector(IExpr list) {
      if (!list.isList()) {
        return null;
      }
      double[] result = new double[list.argSize()];
      for (int i = 0; i < result.length; i++) {
        result[i] = list.getAt(i + 1).evalfNaN();
        if (!Double.isFinite(result[i])) {
          return null;
        }
      }
      return result;
    }

    double mean() {
      double sum = 0.0;
      for (double ai : a) {
        sum += ai;
      }
      return c / (1.0 - sum);
    }

    /**
     * The autocovariances <code>gamma(0), ..., gamma(n-1)</code> from the moving average weights
     * <code>psi</code> of the process: <code>gamma(h) = v Sum(psi(j) psi(j+h))</code>.
     * <code>null</code> if the weights do not die out, that is if the process is not stationary.
     */
    double[] autocovariances(int n) {
      double[] psi = new double[MAX_WEIGHTS + n];
      psi[0] = 1.0;
      int length = psi.length;
      int quiet = 0;
      for (int j = 1; j < psi.length; j++) {
        double value = j <= b.length ? b[j - 1] : 0.0;
        for (int i = 1; i <= a.length && i <= j; i++) {
          value += a[i - 1] * psi[j - i];
        }
        psi[j] = value;
        if (!Double.isFinite(value) || Math.abs(value) > 1.0e12) {
          return null;
        }
        // the weights have died out once the last few are negligible
        quiet = Math.abs(value) < 1.0e-18 ? quiet + 1 : 0;
        if (quiet > Math.max(a.length, b.length) + 1 && j >= n + b.length) {
          length = j + 1;
          break;
        }
        if (j == psi.length - 1) {
          return null;
        }
      }
      double[] gamma = new double[n];
      for (int h = 0; h < n; h++) {
        double sum = 0.0;
        for (int j = 0; j + h < length; j++) {
          sum += psi[j] * psi[j + h];
        }
        gamma[h] = v * sum;
      }
      return gamma;
    }
  }

  /**
   * The Gaussian log likelihood of one path of an <code>ARMAProcess</code>, with the covariance
   * matrix of its autocovariances, or <code>NaN</code> if the process or the data are not numeric
   * or the process is not stationary.
   */
  public static double armaLogLikelihood(IAST process, IAST data) {
    Arma arma = Arma.of(process);
    if (arma == null) {
      return Double.NaN;
    }
    int n = data.argSize();
    double mean = arma.mean();
    double[] x = new double[n];
    for (int i = 0; i < n; i++) {
      x[i] = data.getAt(i + 1).evalfNaN() - mean;
      if (!Double.isFinite(x[i])) {
        return Double.NaN;
      }
    }
    double[] gamma = arma.autocovariances(n);
    if (gamma == null) {
      return Double.NaN;
    }
    RealMatrix covariance = new Array2DRowRealMatrix(n, n);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        covariance.setEntry(i, j, gamma[Math.abs(i - j)]);
      }
    }
    try {
      RealMatrix l = new CholeskyDecomposition(covariance).getL();
      // solve L y = x: the quadratic form is |y|^2
      double[] y = new double[n];
      double logDeterminant = 0.0;
      double quadratic = 0.0;
      for (int i = 0; i < n; i++) {
        double sum = x[i];
        for (int k = 0; k < i; k++) {
          sum -= l.getEntry(i, k) * y[k];
        }
        y[i] = sum / l.getEntry(i, i);
        quadratic += y[i] * y[i];
        logDeterminant += 2.0 * Math.log(l.getEntry(i, i));
      }
      return -0.5 * (n * Math.log(2.0 * Math.PI) + logDeterminant + quadratic);
    } catch (MathIllegalArgumentException e) {
      return Double.NaN;
    }
  }

  /**
   * Whether <code>RandomFunction</code> can simulate the process, and whether its time is discrete.
   *
   * @return {@link IExpr.COMPARE_TERNARY#TRUE} for discrete time,
   *         {@link IExpr.COMPARE_TERNARY#FALSE} for continuous time and
   *         {@link IExpr.COMPARE_TERNARY#UNDECIDABLE} for a process that cannot be simulated
   */
  public static IExpr.COMPARE_TERNARY isDiscrete(IExpr process) {
    if (process.isAST(S.ARMAProcess)) {
      return Arma.of((IAST) process) != null ? IExpr.COMPARE_TERNARY.TRUE
          : IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }
    if (process.isAST(S.WienerProcess, 1) || process.isAST(S.WienerProcess, 3)
        || process.isAST(S.OrnsteinUhlenbeckProcess, 4)
        || process.isAST(S.OrnsteinUhlenbeckProcess, 5)) {
      for (IExpr parameter : (IAST) process) {
        if (!Double.isFinite(parameter.evalfNaN())) {
          return IExpr.COMPARE_TERNARY.UNDECIDABLE;
        }
      }
      return IExpr.COMPARE_TERNARY.FALSE;
    }
    if (process.isAST(S.PoissonProcess, 2)) {
      double rate = process.first().evalfNaN();
      return rate > 0.0 && Double.isFinite(rate) ? IExpr.COMPARE_TERNARY.FALSE
          : IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }
    return IExpr.COMPARE_TERNARY.UNDECIDABLE;
  }

  /** Whether the values of the process are integers: a counting process. */
  public static boolean isIntegerValued(IExpr process) {
    return process.isAST(S.PoissonProcess, 2);
  }

  /**
   * One path of the process at the equally spaced <code>times</code>, by its exact transitions from
   * one time to the next.
   */
  public static double[] simulate(IAST process, double[] times, Random random) {
    int n = times.length;
    double[] x = new double[n];
    if (process.isAST(S.PoissonProcess, 2)) {
      // a counting process: 0 at time 0, independent Poisson(rate*dt) increments
      double rate = process.first().evalf();
      double previous = 0.0;
      double count = 0.0;
      for (int i = 0; i < n; i++) {
        double t = Math.max(times[i], 0.0);
        double mean = rate * (t - previous);
        if (mean > 0.0) {
          count += new org.hipparchus.distribution.discrete.PoissonDistribution(mean)
              .inverseCumulativeProbability(random.nextDouble());
        }
        x[i] = count;
        previous = t;
      }
      return x;
    }
    if (process.isAST(S.ARMAProcess)) {
      Arma arma = Arma.of(process);
      int p = arma.a.length;
      int q = arma.b.length;
      // start from the mean and run in, so the path is drawn from the stationary process
      int burnIn = 1000;
      double sigma = Math.sqrt(arma.v);
      double mean = arma.mean();
      double[] history = new double[burnIn + n];
      double[] noise = new double[burnIn + n];
      for (int t = 0; t < history.length; t++) {
        noise[t] = sigma * random.nextGaussian();
        double value = arma.c + noise[t];
        for (int i = 1; i <= p; i++) {
          value += arma.a[i - 1] * (t - i >= 0 ? history[t - i] : mean);
        }
        for (int j = 1; j <= q; j++) {
          value += arma.b[j - 1] * (t - j >= 0 ? noise[t - j] : 0.0);
        }
        history[t] = value;
      }
      System.arraycopy(history, burnIn, x, 0, n);
      return x;
    }
    double[] parameters = new double[process.argSize()];
    for (int i = 0; i < parameters.length; i++) {
      parameters[i] = process.getAt(i + 1).evalf();
    }
    if (process.isAST(S.WienerProcess)) {
      double mu = parameters.length == 2 ? parameters[0] : 0.0;
      double sigma = parameters.length == 2 ? parameters[1] : 1.0;
      // the process is 0 at time 0
      double t0 = Math.max(times[0], 0.0);
      x[0] = mu * t0 + sigma * Math.sqrt(t0) * (t0 > 0.0 ? random.nextGaussian() : 0.0);
      for (int i = 1; i < n; i++) {
        double dt = times[i] - times[i - 1];
        x[i] = x[i - 1] + mu * dt + sigma * Math.sqrt(dt) * random.nextGaussian();
      }
      return x;
    }
    // OrnsteinUhlenbeckProcess
    double mu = parameters[0];
    double sigma = parameters[1];
    double theta = parameters[2];
    double stationary = sigma / Math.sqrt(2.0 * theta);
    if (parameters.length == 4) {
      // started at x0 at time 0
      double t0 = Math.max(times[0], 0.0);
      double decay = Math.exp(-theta * t0);
      x[0] = mu + (parameters[3] - mu) * decay
          + stationary * Math.sqrt(1.0 - decay * decay) * random.nextGaussian();
    } else {
      x[0] = mu + stationary * random.nextGaussian();
    }
    for (int i = 1; i < n; i++) {
      double decay = Math.exp(-theta * (times[i] - times[i - 1]));
      x[i] = mu + (x[i - 1] - mu) * decay
          + stationary * Math.sqrt(1.0 - decay * decay) * random.nextGaussian();
    }
    return x;
  }
}
