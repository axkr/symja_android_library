package org.matheclipse.core.numerics.functions;

/**
 * The confluent hypergeometric function of the second kind <code>U(a, b, z)</code> (Tricomi's
 * function) in double precision, for real <code>a</code>, <code>b</code> and a positive real
 * <code>z</code>.
 *
 * <p>
 * The evaluation follows the strategy of mpmath's <code>hyperu</code>: first the asymptotic series
 * <code>U(a, b, z) = z^(-a) * 2F0(a, 1+a-b; ; -1/z)</code>, which is used when it converges to
 * working precision (and is exact when it terminates); otherwise Kummer's connection formula
 * through two <code>1F1</code> series, and for an integer <code>b</code>, where that formula is a
 * limit, the logarithmic series DLMF 13.2.9. mpmath raises the working precision where these sums
 * cancel; double arithmetic cannot, so every sum carries a running bound of its rounding error, and
 * a value whose bound exceeds {@link #TOLERANCE} is not returned: the caller computes it in
 * arbitrary precision instead.
 */
public final class TricomiU {

  private static final double EPS = 0x1.0p-53;

  /** The relative error a returned value is guaranteed to stay below. */
  public static final double TOLERANCE = 1.0e-13;

  /** The relative error of {@link GammaJS#gamma(double)} and {@link GammaJS#digamma(double)}. */
  private static final double GAMMA_ERROR = 1.0e-15;

  private static final int MAX_ASYMPTOTIC_TERMS = 200;

  private static final int MAX_SERIES_TERMS = 1000;

  /** The largest argument of a Gamma function in the formulas. */
  private static final double MAX_GAMMA_ARGUMENT = 170.0;

  private static final double EULER_GAMMA = 0.5772156649015329;

  private TricomiU() {}

  /**
   * <code>U(a, b, z)</code> for real <code>a</code>, <code>b</code> and <code>z &gt; 0</code>.
   *
   * @return <code>NaN</code> if the value can't be computed in double precision to a relative error
   *         below {@link #TOLERANCE}, or <code>z</code> is not positive
   */
  public static double hypergeometricU(double a, double b, double z) {
    if (!(z > 0.0) || !Double.isFinite(a) || !Double.isFinite(b) || !Double.isFinite(z)) {
      return Double.NaN;
    }
    if (a == 0.0) {
      return 1.0;
    }
    double value = asymptotic(a, b, z);
    if (!Double.isNaN(value)) {
      return value;
    }
    if (z <= SERIES_MAX_Z) {
      if (b == Math.rint(b)) {
        if (b <= 0.0) {
          // U(a, b, z) == z^(1-b)*U(a-b+1, 2-b, z) with 2-b >= 2
          double u = logarithmic(a - b + 1.0, 2.0 - b, z);
          value = Double.isNaN(u) ? Double.NaN : Math.pow(z, 1.0 - b) * u;
        } else {
          value = logarithmic(a, b, z);
        }
      } else {
        value = kummer(a, b, z);
      }
      if (!Double.isNaN(value)) {
        return value;
      }
    }
    // U(a, b, z) == z^(1-b)*U(a-b+1, 2-b, z): the larger first parameter needs fewer steps of the
    // recurrence down to it
    final double c = a - b + 1.0;
    if (c > a) {
      double u = byIntegral(c, 2.0 - b, z);
      return Double.isNaN(u) ? Double.NaN : withPowerFactor(u, 1.0 - b, z);
    }
    return byIntegral(a, b, z);
  }

  /** The series are only tried up to this argument; beyond it they cancel too much. */
  private static final double SERIES_MAX_Z = 2.0;

  /** <code>z^p*u</code>, <code>NaN</code> if the power factor costs too much accuracy. */
  private static double withPowerFactor(double u, double p, double z) {
    double factor = Math.pow(z, p);
    double result = factor * u;
    if (!Double.isFinite(result) || result == 0.0
        || (Math.abs(p * Math.log(z)) + 1.0) * EPS > TOLERANCE / 4.0) {
      return Double.NaN;
    }
    return result;
  }

  /**
   * A value with its relative error bound.
   */
  private static final class Estimate {
    final double value;
    final double relativeError;

    Estimate(double value, double relativeError) {
      this.value = value;
      this.relativeError = relativeError;
    }
  }

  /** The largest number of recurrence steps from the integral down to <code>a</code>. */
  private static final int MAX_RECURRENCE_STEPS = 200;

  /**
   * <code>U(a, b, z)</code> from the integral representation for <code>a &gt;= 1</code>; a smaller
   * <code>a</code> from the two values at <code>a+m</code> and <code>a+m+1</code> by the recurrence
   * <code>U(a-1) = (2*a-b+z)*U(a) - a*(a-b+1)*U(a+1)</code> (DLMF 13.3.7). <code>U</code> is the
   * minimal solution as <code>a</code> grows, so the recurrence is stable downward; the error of
   * each step is still bounded from the terms it combines.
   */
  private static double byIntegral(double a, double b, double z) {
    int steps = a >= 1.0 ? 0 : (int) Math.ceil(1.0 - a);
    if (steps > MAX_RECURRENCE_STEPS) {
      return Double.NaN;
    }
    double start = a + steps;
    Estimate u0 = integral(start, b, z);
    if (u0 == null) {
      return Double.NaN;
    }
    if (steps == 0) {
      return u0.relativeError <= TOLERANCE ? u0.value : Double.NaN;
    }
    Estimate u1 = integral(start + 1.0, b, z);
    if (u1 == null) {
      return Double.NaN;
    }
    // uHigh = U(s+1), uLow = U(s), going down to U(a)
    double uHigh = u1.value;
    double eHigh = u1.relativeError;
    double uLow = u0.value;
    double eLow = u0.relativeError;
    for (double s = start; s > a + 0.5; s -= 1.0) {
      double t1 = (2.0 * s - b + z) * uLow;
      double t2 = s * (s - b + 1.0) * uHigh;
      double next = t1 - t2;
      if (next == 0.0 || !Double.isFinite(next)) {
        return Double.NaN;
      }
      double eNext = (Math.abs(t1) * (eLow + 2.0 * EPS) + Math.abs(t2) * (eHigh + 3.0 * EPS))
          / Math.abs(next) + EPS;
      uHigh = uLow;
      eHigh = eLow;
      uLow = next;
      eLow = eNext;
    }
    return eLow <= TOLERANCE ? uLow : Double.NaN;
  }

  /** The step of the first level of the quadrature. */
  private static final double FIRST_STEP = 0.5;

  private static final int MAX_LEVELS = 8;

  /**
   * <code>U(a, b, z) = z^(-a)/Gamma(a) * Integrate(E^(-u)*u^(a-1)*(1+u/z)^(b-a-1), {u, 0, Infinity})</code>
   * (DLMF 13.4.4 with <code>u = z*t</code>) for <code>1 &lt;= a &lt;= 170</code>, by the
   * exp-sinh quadrature <code>u = E^(Pi/2*Sinh(t))</code>. The integrand is positive, so the sum
   * does not cancel; the logarithm of the integrand is summed relative to its largest value.
   *
   * @return <code>null</code> if the quadrature does not converge
   */
  private static Estimate integral(double a, double b, double z) {
    if (a < 1.0 || a > MAX_GAMMA_ARGUMENT) {
      return null;
    }
    final double p = b - a - 1.0;
    final double am1 = a - 1.0;
    // the logarithm of the integrand times the Jacobian, and the size of its terms
    java.util.function.DoubleUnaryOperator logG = t -> {
      double lnU = Math.PI / 2.0 * Math.sinh(t);
      double u = Math.exp(lnU);
      return am1 * lnU - u + p * Math.log1p(u / z) + lnU + Math.log(Math.PI / 2.0 * Math.cosh(t));
    };
    // the maximum of the integrand over the nodes of the first level fixes the scale
    double maxLog = Double.NEGATIVE_INFINITY;
    double maxScale = 0.0;
    final double scanStep = FIRST_STEP / 8.0;
    final int scanNodes = (int) Math.round(2.0 * T_MAX / scanStep);
    for (int i = 0; i <= scanNodes; i++) {
      double t = -T_MAX + i * scanStep;
      double v = logG.applyAsDouble(t);
      if (v > maxLog) {
        maxLog = v;
      }
    }
    if (!Double.isFinite(maxLog)) {
      return null;
    }
    double h = FIRST_STEP;
    double sum = 0.0;
    final int firstNodes = (int) Math.round(2.0 * T_MAX / h);
    for (int i = 0; i <= firstNodes; i++) {
      sum += Math.exp(logG.applyAsDouble(-T_MAX + i * h) - maxLog);
    }
    double previous = sum * h;
    double estimate = previous;
    double difference = Double.POSITIVE_INFINITY;
    for (int level = 1; level <= MAX_LEVELS; level++) {
      h /= 2.0;
      // the new nodes lie between the old ones
      final int newNodes = (int) Math.round(T_MAX / h);
      for (int i = 0; i < newNodes; i++) {
        double t = -T_MAX + (2 * i + 1) * h;
        double v = logG.applyAsDouble(t);
        double w = Math.exp(v - maxLog);
        sum += w;
        if (w > 1.0e-17) {
          double lnU = Math.PI / 2.0 * Math.sinh(t);
          double u = Math.exp(lnU);
          double scale = Math.abs(am1 * lnU) + u + Math.abs(p * Math.log1p(u / z)) + Math.abs(lnU)
              + Math.abs(v - maxLog);
          if (scale > maxScale) {
            maxScale = scale;
          }
        }
      }
      estimate = sum * h;
      difference = Math.abs(estimate - previous);
      if (level >= 3 && difference <= 1.0e-15 * estimate) {
        break;
      }
      previous = estimate;
    }
    if (!(difference <= 1.0e-14 * estimate) || !(estimate > 0.0)) {
      return null;
    }
    double logPrefactor = maxLog - a * Math.log(z) - GammaJS.logGamma(a);
    double value = Math.exp(logPrefactor) * estimate;
    if (!Double.isFinite(value) || value == 0.0) {
      return null;
    }
    double relativeError = difference / estimate
        + EPS * (2.0 * maxScale + Math.abs(logPrefactor) + Math.abs(a * Math.log(z))
            + 2.0 * Math.abs(GammaJS.logGamma(a)) + 8.0)
        + GAMMA_ERROR;
    return new Estimate(value, relativeError);
  }

  /**
   * The range of the variable of the exp-sinh substitution: <code>Pi/2*Sinh(6.5)</code> is about
   * 522, so the nodes reach from <code>E^(-522)</code> to beyond any decay of <code>E^(-u)</code>
   * which matters.
   */
  private static final double T_MAX = 6.5;

  /**
   * <code>z^(-a) * Sum((a)_k*(1+a-b)_k/k!*(-1/z)^k)</code>, summed while the terms decrease.
   *
   * @return <code>NaN</code> if the series does not reach working precision before its terms grow
   */
  private static double asymptotic(double a, double b, double z) {
    final double c = 1.0 + a - b;
    final double w = -1.0 / z;
    // a or 1+a-b a non-positive integer: the series terminates, its sum is U exactly
    final boolean terminates = isNonPositiveInteger(a, MAX_ASYMPTOTIC_TERMS)
        || isNonPositiveInteger(c, MAX_ASYMPTOTIC_TERMS);
    double term = 1.0;
    double sum = 1.0;
    double errorSum = 1.0;
    boolean converged = false;
    for (int k = 1; k <= MAX_ASYMPTOTIC_TERMS; k++) {
      double next = term * (a + k - 1) * (c + k - 1) / k * w;
      if (next == 0.0) {
        converged = true;
        break;
      }
      if (!terminates && Math.abs(next) > Math.abs(term)) {
        // the terms grow: the smallest one was reached before the sum was accurate
        return Double.NaN;
      }
      term = next;
      sum += term;
      errorSum += Math.abs(term) * (3.0 * k + 1.0);
      if (Math.abs(term) < EPS * Math.abs(sum)) {
        converged = true;
        break;
      }
    }
    if (!converged || sum == 0.0) {
      return Double.NaN;
    }
    double powError = Math.abs(a * Math.log(z)) + 1.0;
    double relativeError = (errorSum / Math.abs(sum) + powError) * EPS;
    if (!(relativeError <= TOLERANCE)) {
      return Double.NaN;
    }
    double result = sum * Math.pow(z, -a);
    return Double.isFinite(result) && result != 0.0 ? result : Double.NaN;
  }

  /** <code>1/Gamma(x)</code>, <code>0</code> at the poles of Gamma. */
  private static double reciprocalGamma(double x) {
    if (x <= 0.0 && x == Math.rint(x)) {
      return 0.0;
    }
    return 1.0 / GammaJS.gamma(x);
  }

  /**
   * Kummer's connection formula for a non-integer <code>b</code>:
   * <code>U = Gamma(1-b)/Gamma(a-b+1)*M(a,b,z) + Gamma(b-1)/Gamma(a)*z^(1-b)*M(a-b+1,2-b,z)</code>.
   */
  private static double kummer(double a, double b, double z) {
    if (Math.abs(b) > MAX_GAMMA_ARGUMENT || Math.abs(a - b + 1.0) > MAX_GAMMA_ARGUMENT
        || Math.abs(a) > MAX_GAMMA_ARGUMENT) {
      return Double.NaN;
    }
    KummerM.Sum m1 = KummerM.series(a, b, z);
    KummerM.Sum m2 = KummerM.series(a - b + 1.0, 2.0 - b, z);
    if (m1 == null || m2 == null) {
      return Double.NaN;
    }
    double f1 = GammaJS.gamma(1.0 - b) * reciprocalGamma(a - b + 1.0);
    double f2 = GammaJS.gamma(b - 1.0) * reciprocalGamma(a) * Math.pow(z, 1.0 - b);
    double t1 = f1 * m1.value;
    double t2 = f2 * m2.value;
    double result = t1 + t2;
    if (!Double.isFinite(result) || result == 0.0) {
      return Double.NaN;
    }
    double powError = Math.abs((1.0 - b) * Math.log(z)) + 1.0;
    double error = Math.abs(f1) * m1.error + Math.abs(t1) * 3.0 * GAMMA_ERROR
        + Math.abs(f2) * m2.error + Math.abs(t2) * (3.0 * GAMMA_ERROR + powError * EPS)
        + Math.abs(result) * EPS;
    return error <= TOLERANCE * Math.abs(result) ? result : Double.NaN;
  }

  /**
   * DLMF 13.2.9 for <code>b = n+1</code>, <code>n = 0, 1, 2, ...</code>:
   * <code>U(a,n+1,z) = (-1)^(n+1)/(n!*Gamma(a-n)) * Sum((a)_k/((n+1)_k*k!)*z^k*(Log(z)+Psi(a+k)-Psi(1+k)-Psi(n+k+1)))
   * + 1/Gamma(a) * Sum((k-1)!*(1-a+k)_(n-k)/(n-k)!*z^(-k), {k,1,n})</code>.
   *
   * @return <code>NaN</code> for a non-positive integer <code>a</code>, where the formula does not
   *         apply (the asymptotic series terminates there)
   */
  private static double logarithmic(double a, double b, double z) {
    final int n = (int) b - 1;
    if (n < 0 || n > MAX_GAMMA_ARGUMENT || (a <= 0.0 && a == Math.rint(a))
        || Math.abs(a) > MAX_GAMMA_ARGUMENT || Math.abs(a - n) > MAX_GAMMA_ARGUMENT) {
      return Double.NaN;
    }
    double result = 0.0;
    double error = 0.0;

    // the finite sum: 1/Gamma(a) * Sum((k-1)!*(1-a+k)_(n-k)/(n-k)!*z^(-k), {k,1,n})
    if (n > 0) {
      double rgammaA = reciprocalGamma(a);
      double finite = 0.0;
      double finiteError = 0.0;
      double zInverse = 1.0 / z;
      for (int k = 1; k <= n; k++) {
        double term = factorial(k - 1) / factorial(n - k) * Math.pow(zInverse, k);
        for (int j = 0; j < n - k; j++) {
          term *= 1.0 - a + k + j;
        }
        finite += term;
        finiteError += Math.abs(term) * (2.0 * n + 4.0);
      }
      result += rgammaA * finite;
      error += Math.abs(rgammaA) * (finiteError * EPS + Math.abs(finite) * GAMMA_ERROR);
    }

    // the logarithmic sum, zero if a is a positive integer <= n
    double prefactor = ((n & 1) == 0 ? -1.0 : 1.0) * reciprocalGamma(a - n) / factorial(n);
    if (prefactor != 0.0) {
      double logZ = Math.log(z);
      double psiA = GammaJS.digamma(a);
      double psi1 = -EULER_GAMMA;
      double psiN = GammaJS.digamma(n + 1.0);
      if (Double.isNaN(psiA) || Double.isNaN(psiN)) {
        return Double.NaN;
      }
      double coefficient = 1.0;
      double sum = 0.0;
      double sumError = 0.0;
      boolean converged = false;
      for (int k = 0; k <= MAX_SERIES_TERMS; k++) {
        if (k > 0) {
          coefficient *= (a + k - 1) / ((n + k) * (double) k) * z;
          psiA += 1.0 / (a + k - 1);
          psi1 += 1.0 / k;
          psiN += 1.0 / (n + k);
        }
        double bracket = logZ + psiA - psi1 - psiN;
        double term = coefficient * bracket;
        sum += term;
        double bracketError = (Math.abs(logZ) + Math.abs(psiA) + Math.abs(psi1) + Math.abs(psiN))
            * (k + 2.0) * EPS + Math.abs(psiA) * GAMMA_ERROR;
        sumError += Math.abs(term) * (3.0 * k + 2.0) * EPS + Math.abs(coefficient) * bracketError;
        if (k > 2 && Math.abs(z * (a + k) / ((n + k + 1.0) * (k + 1.0))) < 0.5
            && Math.abs(term) < EPS * Math.abs(sum)) {
          converged = true;
          break;
        }
      }
      if (!converged) {
        return Double.NaN;
      }
      result += prefactor * sum;
      error += Math.abs(prefactor) * (sumError + Math.abs(sum) * GAMMA_ERROR);
    }
    if (!Double.isFinite(result) || result == 0.0) {
      return Double.NaN;
    }
    error += Math.abs(result) * EPS;
    return error <= TOLERANCE * Math.abs(result) ? result : Double.NaN;
  }

  /** Test for an integer <code>x</code> with <code>-max &lt; x &lt;= 0</code>. */
  private static boolean isNonPositiveInteger(double x, int max) {
    return x <= 0.0 && x > -max && x == Math.rint(x);
  }

  private static double factorial(int n) {
    double result = 1.0;
    for (int i = 2; i <= n; i++) {
      result *= i;
    }
    return result;
  }
}
