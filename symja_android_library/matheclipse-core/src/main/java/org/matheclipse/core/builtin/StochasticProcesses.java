package org.matheclipse.core.builtin;

import java.util.Random;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.CholeskyDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.RandomFunction;

/**
 * The random processes <code>WienerProcess</code>, <code>OrnsteinUhlenbeckProcess</code>,
 * <code>WhiteNoiseProcess</code> and the ARMA family, <code>ARProcess</code>, <code>MAProcess</code>,
 * <code>ARMAProcess</code>, <code>ARIMAProcess</code>, <code>SARMAProcess</code> and
 * <code>SARIMAProcess</code>: <code>proc(t)</code> is the distribution of the process at time
 * <code>t</code>, and {@link RandomFunction} simulates their paths.
 */
public final class StochasticProcesses {

  /** The number of moving average weights summed for an autocovariance at most. */
  private static final int MAX_WEIGHTS = 100_000;

  /** The largest order of differencing of a process. */
  private static final int MAX_DIFFERENCES = 64;

  /** The largest seasonal period of a process. */
  private static final int MAX_SEASON = 100_000;

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

  /**
   * <code>CovarianceFunction(proc, h)</code> - the autocovariance at the lag <code>h</code> of a
   * weakly stationary <code>ARProcess</code>, <code>MAProcess</code> or <code>ARMAProcess</code>
   * with numeric parameters; <code>CovarianceFunction(proc, s, t)</code> is the covariance of the
   * values at the times <code>s</code> and <code>t</code>. Exact parameters give exact values.
   */
  public static final class CovarianceFunction extends AbstractFunctionEvaluator {
    /** The largest lag whose autocovariance is computed by the recursion. */
    private static final int MAX_LAG = 100_000;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isAST()) {
        return F.NIL;
      }
      IExpr lag;
      if (ast.isAST2()) {
        lag = ast.arg2();
      } else if (ast.isAST3()) {
        lag = engine.evaluate(F.Abs(F.Subtract(ast.arg3(), ast.arg2())));
      } else {
        return F.NIL;
      }
      if (lag.isList()) {
        return ast.isAST2() ? ((IAST) lag).mapThread(ast, 2) : F.NIL;
      }
      IAST process = (IAST) ast.arg1();
      IExpr[] parameters = armaParameters(process);
      if (parameters == null) {
        return F.NIL;
      }
      Arma arma = Arma.of(process);
      if (arma == null || arma.d > 0 || !arma.isStationary()) {
        return F.NIL;
      }
      int h = lag.isInteger() ? Math.abs(lag.toIntDefault()) : -1;
      if (h < 0 || h > MAX_LAG) {
        return F.NIL;
      }
      IAST a = (IAST) parameters[0];
      IAST b = (IAST) parameters[1];
      IExpr[] gamma = autocovariances(a, b, parameters[2], h, engine);
      return gamma == null ? F.NIL : gamma[h];
    }

    /**
     * The autoregressive and the moving average coefficients and the noise variance of an
     * <code>ARProcess</code>, <code>MAProcess</code> or <code>ARMAProcess</code>, with or without
     * a constant.
     *
     * @return <code>null</code> for another expression or non-numeric parameters
     */
    private static IExpr[] armaParameters(IAST process) {
      int offset = process.argSize() > 0 && !process.arg1().isList() ? 1 : 0;
      IExpr a;
      IExpr b;
      if (process.isAST(S.ARProcess) && process.argSize() == 2 + offset) {
        a = process.get(1 + offset);
        b = F.CEmptyList;
      } else if (process.isAST(S.MAProcess) && process.argSize() == 2 + offset) {
        a = F.CEmptyList;
        b = process.get(1 + offset);
      } else if (process.isAST(S.ARMAProcess) && process.argSize() == 3 + offset) {
        a = process.get(1 + offset);
        b = process.get(2 + offset);
      } else {
        return null;
      }
      IExpr v = process.last();
      if (!a.isList() || !b.isList() || !((IAST) a).forAll(IExpr::isNumber)
          || !((IAST) b).forAll(IExpr::isNumber) || !v.isNumber()) {
        return null;
      }
      return new IExpr[] {a, b, v};
    }

    /**
     * The autocovariances <code>gamma(0), ..., gamma(h)</code>. The first <code>m+1 =
     * Max(p, q)+1</code> of them solve the linear equations
     * <code>gamma(k) - Sum(a(i)*gamma(k-i), i=1..p) = v*Sum(b(j)*psi(j-k), j=k..q)</code> with the
     * moving average weights <code>psi</code> and <code>b(0) = 1</code>; the others follow from the
     * recursion <code>gamma(k) = Sum(a(i)*gamma(k-i), i=1..p)</code>.
     */
    private static IExpr[] autocovariances(IAST a, IAST b, IExpr v, int h, EvalEngine engine) {
      int p = a.argSize();
      int q = b.argSize();
      int m = Math.max(p, q);
      // the moving average weights psi(0), ..., psi(q)
      IExpr[] psi = new IExpr[q + 1];
      psi[0] = F.C1;
      for (int j = 1; j <= q; j++) {
        IASTAppendable sum = F.PlusAlloc(p + 1);
        sum.append(b.get(j));
        for (int i = 1; i <= Math.min(j, p); i++) {
          sum.append(F.Times(a.get(i), psi[j - i]));
        }
        psi[j] = engine.evaluate(sum);
      }
      IASTAppendable matrix = F.ListAlloc(m + 1);
      IASTAppendable rhs = F.ListAlloc(m + 1);
      for (int k = 0; k <= m; k++) {
        IExpr[] row = new IExpr[m + 1];
        java.util.Arrays.fill(row, F.C0);
        row[k] = F.C1;
        for (int i = 1; i <= p; i++) {
          int index = Math.abs(k - i);
          row[index] = F.Subtract(row[index], a.get(i));
        }
        matrix.append(F.List(row));
        IASTAppendable sum = F.PlusAlloc(q + 1);
        for (int j = k; j <= q; j++) {
          sum.append(F.Times(j == 0 ? F.C1 : b.get(j), psi[j - k]));
        }
        rhs.append(F.Times(v, sum.oneIdentity0()));
      }
      IExpr solution = engine.evaluate(F.LinearSolve(engine.evaluate(matrix), engine.evaluate(rhs)));
      if (!solution.isList() || solution.argSize() != m + 1
          || !((IAST) solution).forAll(IExpr::isNumber)) {
        return null;
      }
      IExpr[] gamma = new IExpr[Math.max(h, m) + 1];
      for (int k = 0; k <= m; k++) {
        gamma[k] = solution.getAt(k + 1);
      }
      for (int k = m + 1; k <= h; k++) {
        if ((k & 0xFF) == 0) {
          engine.checkTimeLimit();
        }
        IASTAppendable sum = F.PlusAlloc(p);
        for (int i = 1; i <= p; i++) {
          sum.append(F.Times(a.get(i), gamma[k - i]));
        }
        gamma[k] = engine.evaluate(sum.oneIdentity0());
      }
      return gamma;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }
  }

  /**
   * <code>WeakStationarity(proc)</code> - whether the process with numeric parameters is weakly
   * stationary: its autoregressive part has all roots outside the unit circle and it isn't
   * integrated. A process which isn't reports the message <code>nonwkst</code>.
   */
  public static final class WeakStationarity extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr process = ast.arg1();
      if (process.isAST(S.WhiteNoiseProcess)) {
        return isWhiteNoise(process) ? S.True : F.NIL;
      }
      if (!process.isAST()) {
        return F.NIL;
      }
      Arma arma = Arma.of((IAST) process);
      if (arma == null) {
        return F.NIL;
      }
      if (arma.d > 0 || arma.seasonalD > 0) {
        return S.False;
      }
      if (arma.isStationary()) {
        return S.True;
      }
      Errors.printMessage((ISymbol) process.head(), "nonwkst", F.CEmptyList, engine);
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }
  }

  /**
   * The numeric parameters of a process of the ARMA family, in the form
   * <code>x(t) = c + a1 x(t-1) + ... + ap x(t-p) + e(t) + b1 e(t-1) + ... + bq e(t-q)</code> of the
   * series differenced <code>d</code> times and <code>seasonalD</code> times at lag
   * <code>season</code>, with the seasonal factors multiplied out.
   */
  static final class Arma {
    final double c;
    final double[] a;
    final double[] b;
    final double v;
    final int d;
    final int season;
    final int seasonalD;

    private Arma(double c, double[] a, double[] b, double v, int d, int season, int seasonalD) {
      this.c = c;
      this.a = a;
      this.b = b;
      this.v = v;
      this.d = d;
      this.season = season;
      this.seasonalD = seasonalD;
    }

    /** The number of arguments of the process without its constant, 0 for another expression. */
    private static int arity(IExpr head) {
      if (head == S.ARProcess || head == S.MAProcess) {
        return 2;
      }
      if (head == S.ARMAProcess) {
        return 3;
      }
      if (head == S.ARIMAProcess || head == S.SARMAProcess) {
        return 4;
      }
      return head == S.SARIMAProcess ? 5 : 0;
    }

    /** The parameters, or <code>null</code> if they are not numbers. */
    static Arma of(IAST process) {
      final IExpr head = process.head();
      int arity = arity(head);
      if (arity == 0) {
        return null;
      }
      int offset = process.argSize() == arity + 1 && !process.arg1().isList() ? 1 : 0;
      if (process.argSize() != arity + offset) {
        return null;
      }
      double c = offset == 1 ? process.arg1().evalfNaN() : 0.0;
      double v = process.last().evalfNaN();
      if (!Double.isFinite(c) || !(v > 0.0)) {
        return null;
      }
      if (head == S.ARProcess || head == S.MAProcess) {
        double[] weights = vector(process.get(1 + offset));
        if (weights == null) {
          return null;
        }
        return head == S.ARProcess ? new Arma(c, weights, new double[0], v, 0, 1, 0)
            : new Arma(c, new double[0], weights, v, 0, 1, 0);
      }
      double[] a = vector(process.get(1 + offset));
      if (a == null) {
        return null;
      }
      final boolean integrated = head == S.ARIMAProcess || head == S.SARIMAProcess;
      int d = integrated ? process.get(2 + offset).toIntDefault() : 0;
      double[] b = vector(process.get((integrated ? 3 : 2) + offset));
      if (b == null || d < 0 || d > MAX_DIFFERENCES) {
        return null;
      }
      if (head == S.ARMAProcess || head == S.ARIMAProcess) {
        return new Arma(c, a, b, v, d, 1, 0);
      }
      // {s, {sar}, {sma}} or {s, {sar}, sd, {sma}}
      IExpr seasonal = process.get((integrated ? 4 : 3) + offset);
      if (!seasonal.isList() || seasonal.argSize() != (integrated ? 4 : 3)) {
        return null;
      }
      int season = seasonal.first().toIntDefault();
      int seasonalD = integrated ? seasonal.getAt(3).toIntDefault() : 0;
      double[] seasonalA = vector(seasonal.second());
      double[] seasonalB = vector(seasonal.getAt(integrated ? 4 : 3));
      if (season < 1 || season > MAX_SEASON || seasonalD < 0 || seasonalD > MAX_DIFFERENCES
          || seasonalA == null || seasonalB == null) {
        return null;
      }
      return new Arma(c, seasonalProduct(a, seasonalA, season, -1.0),
          seasonalProduct(b, seasonalB, season, 1.0), v, d, season, seasonalD);
    }

    /**
     * The coefficients of <code>L, L^2, ...</code> in
     * <code>(1 + sign Sum(a(i) L^i)) (1 + sign Sum(b(j) L^(s j)))</code>, multiplied by
     * <code>sign</code>. The sign <code>-1</code> is for autoregressive polynomials.
     */
    private static double[] seasonalProduct(double[] a, double[] b, int s, double sign) {
      double[] left = new double[a.length + 1];
      left[0] = 1.0;
      for (int i = 0; i < a.length; i++) {
        left[i + 1] = sign * a[i];
      }
      double[] product = new double[a.length + s * b.length + 1];
      for (int i = 0; i < left.length; i++) {
        product[i] += left[i];
        for (int j = 0; j < b.length; j++) {
          product[i + s * (j + 1)] += left[i] * sign * b[j];
        }
      }
      double[] result = new double[product.length - 1];
      for (int i = 0; i < result.length; i++) {
        result[i] = sign * product[i + 1];
      }
      return result;
    }

    /**
     * Whether all roots of <code>1 - a1 z - ... - ap z^p</code> lie outside the unit circle: the
     * reflection coefficients of the step-down recursion are all smaller than 1 in magnitude.
     */
    boolean isStationary() {
      double[] w = a.clone();
      for (int m = w.length; m >= 1; m--) {
        double k = w[m - 1];
        if (Math.abs(k) >= 1.0) {
          return false;
        }
        double[] next = new double[m - 1];
        for (int i = 0; i < m - 1; i++) {
          next[i] = (w[i] + k * w[m - 2 - i]) / (1.0 - k * k);
        }
        w = next;
      }
      return true;
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

    /**
     * One path of <code>n</code> values. A stationary recursion is run in from its mean, so the
     * path is drawn from the stationary process; otherwise it starts from a zero history, and so
     * does the integration of a differenced series.
     */
    double[] simulate(int n, Random random) {
      int p = a.length;
      int q = b.length;
      boolean stationary = isStationary();
      int burnIn = stationary ? 1000 + 50 * (p + q) : 0;
      double sigma = Math.sqrt(v);
      double start = stationary ? mean() : 0.0;
      double[] history = new double[burnIn + n];
      double[] noise = new double[burnIn + n];
      for (int t = 0; t < history.length; t++) {
        noise[t] = sigma * random.nextGaussian();
        double value = c + noise[t];
        for (int i = 1; i <= p; i++) {
          value += a[i - 1] * (t - i >= 0 ? history[t - i] : start);
        }
        for (int j = 1; j <= q; j++) {
          value += b[j - 1] * (t - j >= 0 ? noise[t - j] : 0.0);
        }
        history[t] = value;
      }
      double[] x = java.util.Arrays.copyOfRange(history, burnIn, burnIn + n);
      for (int k = 0; k < d; k++) {
        for (int t = 1; t < n; t++) {
          x[t] += x[t - 1];
        }
      }
      for (int k = 0; k < seasonalD; k++) {
        for (int t = season; t < n; t++) {
          x[t] += x[t - season];
        }
      }
      return x;
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
    if (process.isAST() && Arma.arity(process.head()) > 0) {
      return Arma.of((IAST) process) != null ? IExpr.COMPARE_TERNARY.TRUE
          : IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }
    if (process.isAST(S.WhiteNoiseProcess)) {
      return isWhiteNoise(process) ? IExpr.COMPARE_TERNARY.TRUE
          : IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }
    if (process.isAST(S.DiscreteMarkovProcess)) {
      return markovMatrix(process) != null ? IExpr.COMPARE_TERNARY.TRUE
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

  /**
   * Whether <code>process</code> is <code>WhiteNoiseProcess()</code>,
   * <code>WhiteNoiseProcess(sigma)</code>, <code>WhiteNoiseProcess(mu, sigma)</code> or
   * <code>WhiteNoiseProcess(dist)</code> with numeric parameters.
   */
  private static boolean isWhiteNoise(IExpr process) {
    if (process.argSize() == 0) {
      return true;
    }
    if (whiteNoiseDistribution(process).isPresent()) {
      return true;
    }
    if (process.argSize() > 2) {
      return false;
    }
    double mu = process.argSize() == 2 ? process.first().evalfNaN() : 0.0;
    double sigma = ((IAST) process).last().evalfNaN();
    return Double.isFinite(mu) && sigma >= 0.0 && Double.isFinite(sigma);
  }

  /**
   * The distribution <code>dist</code> of <code>WhiteNoiseProcess(dist)</code>, otherwise
   * {@link F#NIL}. Its values are drawn with <code>RandomVariate</code>.
   */
  public static IExpr whiteNoiseDistribution(IExpr process) {
    return process.isAST(S.WhiteNoiseProcess, 2) && process.first().isDistribution()
        ? process.first()
        : F.NIL;
  }

  /** Whether the values of the process are integers: a counting process. */
  public static boolean isIntegerValued(IExpr process) {
    return process.isAST(S.PoissonProcess, 2) || markovMatrix(process) != null;
  }

  /**
   * The transition matrix of <code>DiscreteMarkovProcess(i0, m)</code> with a numeric stochastic
   * matrix <code>m</code> and a start state <code>i0</code> or a start distribution vector.
   *
   * @return <code>null</code> for another expression
   */
  private static double[][] markovMatrix(IExpr process) {
    if (!process.isAST(S.DiscreteMarkovProcess, 3)) {
      return null;
    }
    int[] dimensions = process.second().isMatrix();
    if (dimensions == null || dimensions[0] != dimensions[1] || dimensions[0] == 0) {
      return null;
    }
    double[][] m = process.second().toDoubleMatrix();
    if (m == null) {
      return null;
    }
    for (double[] row : m) {
      double sum = 0.0;
      for (double value : row) {
        if (!(value >= 0.0)) {
          return null;
        }
        sum += value;
      }
      if (Math.abs(sum - 1.0) > 1.0e-10) {
        return null;
      }
    }
    IExpr start = process.first();
    if (start.isInteger()) {
      int i0 = start.toIntDefault();
      return i0 >= 1 && i0 <= m.length ? m : null;
    }
    double[] p = start.toDoubleVector();
    return p != null && p.length == m.length ? m : null;
  }

  /** A state drawn from the probabilities <code>p</code>, counted from <code>1</code>. */
  private static int drawState(double[] p, Random random) {
    double u = random.nextDouble();
    double sum = 0.0;
    for (int i = 0; i < p.length; i++) {
      sum += p[i];
      if (u < sum) {
        return i + 1;
      }
    }
    return p.length;
  }

  /**
   * One path of a <code>DiscreteMarkovProcess</code> at the integer <code>times</code>: the
   * process starts at time <code>0</code> and moves at each integer time.
   */
  private static double[] simulateMarkov(IAST process, double[][] m, double[] times,
      Random random) {
    int state = process.first().isInteger() ? process.first().toIntDefault()
        : drawState(process.first().toDoubleVector(), random);
    long time = 0;
    double[] x = new double[times.length];
    EvalEngine engine = EvalEngine.get();
    for (int i = 0; i < times.length; i++) {
      long target = (long) Math.floor(Math.max(times[i], 0.0) + 1.0e-9);
      while (time < target) {
        if ((time & 0xFFFF) == 0) {
          engine.checkTimeLimit();
        }
        state = drawState(m[state - 1], random);
        time++;
      }
      x[i] = state;
    }
    return x;
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
    double[][] markov = markovMatrix(process);
    if (markov != null) {
      return simulateMarkov(process, markov, times, random);
    }
    if (process.isAST(S.WhiteNoiseProcess)) {
      double mu = process.argSize() == 2 ? process.first().evalf() : 0.0;
      double sigma = process.argSize() == 0 ? 1.0 : process.last().evalf();
      for (int i = 0; i < n; i++) {
        x[i] = mu + sigma * random.nextGaussian();
      }
      return x;
    }
    Arma arma = Arma.of(process);
    if (arma != null) {
      return arma.simulate(n, random);
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
