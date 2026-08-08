package org.matheclipse.core.numerics.functions;

import org.hipparchus.complex.Complex;
import org.hipparchus.special.Gamma;

/**
 * The Barnes G-function <code>G(z)</code> and its logarithm for real <code>double</code> arguments.
 *
 * <p>
 * <code>G</code> is the "double gamma": <code>G(1) = 1</code> and
 * <code>G(z+1) = Gamma(z) * G(z)</code>, so <code>G(n) = 1! * 2! * ... * (n-2)!</code> at a
 * positive integer. The Apfloat route costs about 50 ms for each of <code>barnesG</code> and
 * <code>logBarnesG</code> - the last two calls in {@code Num} above a millisecond.
 *
 * <p>
 * <code>logBarnesG</code> is computed from the Alexeiewsky/Barnes asymptotic expansion, which is
 * only usable for a large argument, so the recurrence above first walks the argument up into that
 * region and the accumulated <code>log Gamma</code> terms are subtracted off again.
 *
 * <p>
 * <code>barnesG</code> is then <code>exp(logBarnesG)</code> on the positive side. On the negative
 * side it is instead carried by the recurrence in the other direction, <code>G(z) =
 * G(z+1)/Gamma(z)</code>: <code>G</code> stays real there while its logarithm does not - the
 * function alternates sign between the zeros it has at every non-positive integer - so the two
 * cannot share a path.
 *
 * <p>
 * <b>Validated domain.</b> See {@link #isSupportedG(double)} and {@link #isSupportedLogG(double)}.
 * Measured against Apfloat at 40: <code>logBarnesG</code> holds to 5e-14 on an absolute scale - the
 * right one, since it passes through zero at 1, 2 and 3 - and <code>barnesG</code> to 1.9e-13
 * relative. That last figure is the floor rather than slack in the implementation: <code>G</code>
 * is recovered as <code>exp(log G)</code>, so a logarithm of around 500 near the overflow limit
 * carries its own rounding into the exponential and multiplies the relative error by that 500.
 * Callers wanting the full precision at a large argument should ask for the logarithm.
 */
public final class BarnesG {

  /** <code>zeta'(-1) = 1/12 - log(A)</code> with <code>A</code> the Glaisher-Kinkelin constant. */
  private static final double ZETA_PRIME_MINUS_ONE = -0.16542114370045092921;

  private static final double LOG_TWO_PI = 1.8378770664093454836;

  /**
   * The asymptotic expansion is applied at or above this argument. At 12 the last term carried
   * below is already down at 1e-20, so the truncation contributes nothing measurable.
   */
  private static final double ASYMPTOTIC_FROM = 12.0;

  /**
   * <code>B(2k+2) / (4k(k+1))</code> for <code>k = 1..9</code> - the coefficients of
   * <code>z^-2k</code> in the expansion. The Bernoulli numbers grow quickly, so the series is
   * asymptotic rather than convergent; it is truncated where the terms are smallest for the
   * argument range it is used on.
   */
  private static final double[] SERIES_COEFFICIENTS = { //
      -1.0 / 240.0, // B4 / 8
      1.0 / 1008.0, // B6 / 24
      -1.0 / 1440.0, // B8 / 48
      1.0 / 1056.0, // B10 / 80
      -691.0 / 327600.0, // B12 / 120
      7.0 / 1008.0, // B14 / 168
      -3617.0 / 114240.0, // B16 / 224
      43867.0 / 229824.0, // B18 / 288
      -174611.0 / 118800.0 // B20 / 360
  };

  /**
   * Beyond this the shift loop would run too many times to stay cheap, and <code>logBarnesG</code>
   * grows like <code>z^2 log z</code> so the answer is dominated by that anyway.
   */
  private static final double X_MAX = 1.0e6;

  /** Largest argument whose <code>G</code> still fits a double; past it G overflows. */
  private static final double G_OVERFLOW_LIMIT = 26.0;

  /** How far below zero the downward recurrence is worth running. */
  private static final double G_NEGATIVE_LIMIT = -50.0;

  private BarnesG() {}

  /**
   * Whether {@link #logBarnesG(double)} is trusted here. Only a positive argument qualifies:
   * <code>G</code> is negative on part of the negative axis and zero at every non-positive integer,
   * so its logarithm is not real there.
   */
  public static boolean isSupportedLogG(double x) {
    return !Double.isNaN(x) && x > 0.0 && x <= X_MAX;
  }

  /**
   * Whether {@link #barnesG(double)} is trusted here. <code>G</code> overflows a double past about
   * 26, and the downward recurrence used on the negative side is capped.
   */
  public static boolean isSupportedG(double x) {
    if (Double.isNaN(x)) {
      return false;
    }
    return x <= G_OVERFLOW_LIMIT && x >= G_NEGATIVE_LIMIT;
  }

  /**
   * <code>log G(x)</code> for <code>x &gt; 0</code>, or NaN outside that.
   */
  public static double logBarnesG(final double x) {
    if (!(x > 0.0)) {
      return Double.NaN;
    }
    // G(1) = G(2) = G(3) = 1 exactly - worth taking directly so the identities hold to the bit
    if (x == 1.0 || x == 2.0 || x == 3.0) {
      return 0.0;
    }
    // Walk up to where the expansion applies, accumulating the log Gamma terms that
    // G(z+1) = Gamma(z) G(z) introduces, then subtract them again.
    double z = x;
    double shift = 0.0;
    while (z < ASYMPTOTIC_FROM) {
      shift += Gamma.logGamma(z);
      z += 1.0;
    }
    return asymptoticLogG(z) - shift;
  }

  /**
   * The Alexeiewsky expansion, in the form
   * <code>log G(w+1) = w^2/2 log w - 3w^2/4 + w/2 log(2 Pi) - 1/12 log w + zeta'(-1) + sum</code>,
   * evaluated at <code>w = z - 1</code> so that it returns <code>log G(z)</code>.
   */
  private static double asymptoticLogG(final double z) {
    final double w = z - 1.0;
    final double logW = Math.log(w);
    double result = 0.5 * w * w * logW //
        - 0.75 * w * w //
        + 0.5 * w * LOG_TWO_PI //
        - logW / 12.0 //
        + ZETA_PRIME_MINUS_ONE;
    final double wSquared = w * w;
    double power = wSquared;
    for (int k = 0; k < SERIES_COEFFICIENTS.length; k++) {
      result += SERIES_COEFFICIENTS[k] / power;
      power *= wSquared;
    }
    return result;
  }

  /**
   * <code>G(x)</code> for real <code>x</code>, or NaN where a double cannot carry it.
   *
   * <p>
   * Zero at every non-positive integer, exactly - those are genuine zeros of the function, not
   * underflow.
   */
  public static double barnesG(final double x) {
    if (Double.isNaN(x)) {
      return Double.NaN;
    }
    if (x <= 0.0 && Math.rint(x) == x) {
      return 0.0; // G vanishes at 0, -1, -2, ...
    }
    if (x > 0.0) {
      if (x > G_OVERFLOW_LIMIT) {
        return Double.POSITIVE_INFINITY;
      }
      final double logG = logBarnesG(x);
      return Double.isNaN(logG) ? Double.NaN : Math.exp(logG);
    }
    // x < 0 and not an integer. G(z) = G(z+1)/Gamma(z), applied until the argument is positive.
    // Gamma alternates sign across the negative axis, which is exactly how G picks up the sign
    // changes between its zeros - so this must not go through a logarithm.
    if (x < G_NEGATIVE_LIMIT) {
      return Double.NaN;
    }
    double z = x;
    double denominator = 1.0;
    while (z < 0.0) {
      denominator *= Gamma.gamma(z);
      if (denominator == 0.0 || !Double.isFinite(denominator)) {
        return Double.NaN;
      }
      z += 1.0;
    }
    final double logG = logBarnesG(z);
    if (Double.isNaN(logG)) {
      return Double.NaN;
    }
    return Math.exp(logG) / denominator;
  }

  // ----------------------------------------------------------------------------------------
  // Complex arguments
  // ----------------------------------------------------------------------------------------

  /**
   * Beyond this the shift loop below runs too many times to stay cheap. <code>logBarnesG</code>
   * grows like <code>z^2 log z</code>, so the answer is dominated by that term long before here.
   */
  private static final double COMPLEX_MAX_NORM = 1.0e4;

  /**
   * Whether {@link #logBarnesG(Complex)} is trusted here.
   *
   * <p>
   * Unlike the real case there is no positivity requirement: <code>logBarnesG</code> is defined
   * across the plane, picking up an imaginary part on the negative real axis exactly as
   * <code>logGamma</code> does. The non-positive integers are still excluded - those are the zeros
   * of <code>G</code>, where its logarithm has a branch point.
   */
  public static boolean isSupportedLogG(Complex z) {
    if (z == null || Double.isNaN(z.getReal()) || Double.isNaN(z.getImaginary())) {
      return false;
    }
    if (z.getImaginary() == 0.0 && z.getReal() <= 0.0 && Math.rint(z.getReal()) == z.getReal()) {
      return false;
    }
    return z.norm() <= COMPLEX_MAX_NORM;
  }

  /** Whether {@link #barnesG(Complex)} is trusted here. */
  public static boolean isSupportedG(Complex z) {
    if (z == null || Double.isNaN(z.getReal()) || Double.isNaN(z.getImaginary())) {
      return false;
    }
    return z.norm() <= COMPLEX_MAX_NORM;
  }

  /**
   * <code>log G(z)</code> for complex <code>z</code>, or null where it is not trusted.
   *
   * <p>
   * Same shape as the real version: walk the argument right with
   * <code>G(z+1) = Gamma(z) G(z)</code> until the asymptotic expansion applies, then subtract the
   * <code>log Gamma</code> terms collected on the way.
   */
  public static Complex logBarnesG(final Complex z) {
    if (!isSupportedLogG(z)) {
      return null;
    }
    if (z.getImaginary() == 0.0
        && (z.getReal() == 1.0 || z.getReal() == 2.0 || z.getReal() == 3.0)) {
      return Complex.ZERO; // G(1) = G(2) = G(3) = 1 exactly
    }
    Complex w = z;
    Complex shift = Complex.ZERO;
    int guard = 0;
    while (w.getReal() < ASYMPTOTIC_FROM) {
      final Complex logGamma = ComplexGamma.logGamma(w);
      if (logGamma == null) {
        return null;
      }
      shift = shift.add(logGamma);
      w = w.add(1.0);
      if (++guard > 100000) {
        return null;
      }
    }
    final Complex asymptotic = asymptoticLogG(w);
    return asymptotic == null ? null : asymptotic.subtract(shift);
  }

  /**
   * The Alexeiewsky expansion in complex arithmetic; see the real {@link #asymptoticLogG(double)}.
   */
  private static Complex asymptoticLogG(final Complex z) {
    final Complex w = z.subtract(1.0);
    if (w.getReal() == 0.0 && w.getImaginary() == 0.0) {
      return null;
    }
    final Complex logW = w.log();
    final Complex wSquared = w.multiply(w);
    Complex result = wSquared.multiply(logW).multiply(0.5) //
        .subtract(wSquared.multiply(0.75)) //
        .add(w.multiply(0.5 * LOG_TWO_PI)) //
        .subtract(logW.divide(12.0)) //
        .add(ZETA_PRIME_MINUS_ONE);
    Complex power = wSquared;
    for (int k = 0; k < SERIES_COEFFICIENTS.length; k++) {
      result = result.add(power.reciprocal().multiply(SERIES_COEFFICIENTS[k]));
      power = power.multiply(wSquared);
    }
    return result;
  }

  /**
   * <code>G(z)</code> for complex <code>z</code>, or null where it is not trusted.
   *
   * <p>
   * <code>G</code> is entire, so unlike its logarithm it has no cut - and exponentiating
   * <code>logBarnesG</code> recovers it correctly even on the negative real axis, where the
   * imaginary <code>Pi</code> in the logarithm is exactly what supplies <code>G</code>'s sign.
   */
  public static Complex barnesG(final Complex z) {
    if (!isSupportedG(z)) {
      return null;
    }
    if (z.getImaginary() == 0.0 && z.getReal() <= 0.0 && Math.rint(z.getReal()) == z.getReal()) {
      return Complex.ZERO; // the zeros of G, exactly
    }
    final Complex logG = logBarnesG(z);
    if (logG == null || logG.getReal() > 709.0) {
      return null;
    }
    final Complex result = logG.exp();
    return Double.isFinite(result.getReal()) && Double.isFinite(result.getImaginary()) ? result
        : null;
  }
}
