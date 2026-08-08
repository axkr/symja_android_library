package org.matheclipse.core.numerics.functions;

import org.hipparchus.complex.Complex;

/**
 * The Bessel functions <code>J</code>, <code>Y</code>, <code>I</code> and <code>K</code> for
 * complex order and complex <code>double</code> argument.
 *
 * <p>
 * The Apfloat route costs about 25 ms for <code>K</code> and 4 to 8 ms for the others.
 *
 * <p>
 * <b>What is covered.</b> <code>J</code> and <code>I</code> come from their ascending series, which
 * converge for every argument and every order - the coefficients need a complex <code>Gamma</code>,
 * taken from {@link ComplexGamma}. <code>Y</code> and <code>K</code> are then reached by
 * reflection,
 * 
 * <pre>
 *   Y_v = (J_v cos(v Pi) - J_-v) / sin(v Pi)
 *   K_v = (Pi/2) (I_-v - I_v) / sin(v Pi)
 * </pre>
 * 
 * which divides by <code>sin(v Pi)</code> and therefore <b>fails at an integer order</b>. Those are
 * not approximated here: an integer order is reported unsupported and left to the caller's
 * arbitrary-precision fall-back, because the honest alternative is the limit form with its
 * logarithmic series, which is a separate piece of work. <code>J</code> and <code>I</code> are
 * unaffected and cover every order.
 *
 * <p>
 * The series terms grow like <code>exp(|z|)</code> before they decay, so for a large argument the
 * result is a small difference of large sums; the cancellation guard measures that and withdraws
 * the point rather than returning noise.
 */
public final class ComplexBessel {

  private static final double EPS = 1.0e-16;

  private static final int MAX_TERMS = 500;

  /**
   * Largest <code>|z|</code> offered. The ascending series loses roughly <code>|z|</code> nepers to
   * cancellation, so this is where a double still has digits left; past it the guard rejects and
   * Apfloat takes over.
   */
  private static final double MAX_NORM = 20.0;

  /** Largest <code>|v|</code> offered; beyond this the leading factor underflows anyway. */
  private static final double MAX_ORDER = 100.0;

  /**
   * How close to an integer an order may be before the reflection is refused. At a distance
   * <code>d</code> the division by <code>sin(v Pi)</code> costs about <code>-log10(d)</code>
   * digits, so 1e-8 caps the loss at eight and keeps four in hand.
   */
  private static final double MIN_DISTANCE_TO_INTEGER = 1.0e-8;

  /**
   * How much cancellation the reflection may suffer before the point is withdrawn.
   *
   * <p>
   * Tighter than the guard inside the series, and measured rather than chosen: <code>K</code>
   * decays like <code>exp(-|z|)</code> while the two <code>I</code> it is built from both grow like
   * <code>exp(|z|)</code>, so the difference is where the digits go. At <code>z = 3.54+3.54i</code>
   * a ratio of about 600 already produced 1.2e-12 - more than the naive <code>ratio * eps</code>
   * would suggest, because the two series carry their own error into the subtraction - so the bound
   * sits an order of magnitude below that.
   */
  private static final double REFLECTION_MAX_CANCELLATION = 50.0;

  private ComplexBessel() {}

  /** Whether {@link #besselJ} and {@link #besselI} are trusted here. */
  public static boolean isSupported(Complex v, Complex z) {
    if (v == null || z == null) {
      return false;
    }
    if (Double.isNaN(v.getReal()) || Double.isNaN(v.getImaginary()) || Double.isNaN(z.getReal())
        || Double.isNaN(z.getImaginary())) {
      return false;
    }
    if (z.getReal() == 0.0 && z.getImaginary() == 0.0) {
      return false; // J and I are fine at 0 but Y and K are singular; leave all four to the caller
    }
    return z.norm() <= MAX_NORM && v.norm() <= MAX_ORDER;
  }

  /**
   * Whether {@link #besselY} and {@link #besselK} are trusted here - additionally requiring the
   * order to stay clear of the integers, where the reflection they use breaks down.
   */
  public static boolean isSupportedSecondKind(Complex v, Complex z) {
    if (!isSupported(v, z)) {
      return false;
    }
    if (v.getImaginary() != 0.0) {
      return true; // only a real integer order is a problem
    }
    final double distance = Math.abs(v.getReal() - Math.rint(v.getReal()));
    return distance >= MIN_DISTANCE_TO_INTEGER;
  }

  /** <code>J_v(z)</code>, or null where the value is not trusted. */
  public static Complex besselJ(Complex v, Complex z) {
    return isSupported(v, z) ? ascendingSeries(v, z, true) : null;
  }

  /** <code>I_v(z)</code>, or null where the value is not trusted. */
  public static Complex besselI(Complex v, Complex z) {
    return isSupported(v, z) ? ascendingSeries(v, z, false) : null;
  }

  /** <code>Y_v(z)</code>, or null - including at an integer order, which is not covered. */
  public static Complex besselY(Complex v, Complex z) {
    if (!isSupportedSecondKind(v, z)) {
      return null;
    }
    final Complex jPositive = ascendingSeries(v, z, true);
    final Complex jNegative = ascendingSeries(v.negate(), z, true);
    if (jPositive == null || jNegative == null) {
      return null;
    }
    final Complex vPi = v.multiply(Math.PI);
    final Complex numerator = jPositive.multiply(vPi.cos()).subtract(jNegative);
    return reflectionChecked(numerator.divide(vPi.sin()), jPositive, jNegative);
  }

  /** <code>K_v(z)</code>, or null - including at an integer order, which is not covered. */
  public static Complex besselK(Complex v, Complex z) {
    if (!isSupportedSecondKind(v, z)) {
      return null;
    }
    final Complex iPositive = ascendingSeries(v, z, false);
    final Complex iNegative = ascendingSeries(v.negate(), z, false);
    if (iPositive == null || iNegative == null) {
      return null;
    }
    final Complex vPi = v.multiply(Math.PI);
    final Complex numerator = iNegative.subtract(iPositive).multiply(Math.PI / 2.0);
    return reflectionChecked(numerator.divide(vPi.sin()), iPositive, iNegative);
  }

  /**
   * The ascending series <code>(z/2)^v sum (-1)^k (z/2)^(2k) / (k! Gamma(v+k+1))</code> for
   * <code>J</code>, and the same without the alternating sign for <code>I</code>.
   *
   * <p>
   * The leading <code>(z/2)^v</code> is a complex power, so it is taken as
   * <code>exp(v log(z/2))</code> on the principal branch - which is where <code>J</code> and
   * <code>I</code> have their cut for a non-integer order, so this places it correctly rather than
   * by accident.
   */
  private static Complex ascendingSeries(final Complex v, final Complex z,
      final boolean alternate) {
    final Complex half = z.divide(2.0);
    final Complex leading = half.log().multiply(v).exp();
    if (!Double.isFinite(leading.getReal()) || !Double.isFinite(leading.getImaginary())) {
      return null;
    }
    final Complex quarter = half.multiply(half);
    final Complex step = alternate ? quarter.negate() : quarter;

    // term_k = (z/2)^(2k) / (k! Gamma(v+k+1)); start from 1/Gamma(v+1)
    final Complex gammaV1 = ComplexGamma.gamma(v.add(1.0));
    if (gammaV1 == null || (gammaV1.getReal() == 0.0 && gammaV1.getImaginary() == 0.0)) {
      return null;
    }
    Complex term = gammaV1.reciprocal();
    Complex sum = term;
    double largest = term.norm();
    int k;
    for (k = 1; k <= MAX_TERMS; k++) {
      // term_k = term_(k-1) * step / (k (v+k))
      term = term.multiply(step).divide(v.add(k).multiply(k));
      sum = sum.add(term);
      largest = Math.max(largest, term.norm());
      if (term.norm() < EPS * sum.norm()) {
        break;
      }
    }
    if (k > MAX_TERMS) {
      return null;
    }
    if (largest > 1.0e3 * Math.max(sum.norm(), 1.0e-300)) {
      return null; // the series cancelled away its significant digits
    }
    final Complex result = leading.multiply(sum);
    return Double.isFinite(result.getReal()) && Double.isFinite(result.getImaginary()) ? result
        : null;
  }

  /**
   * Withdraw a reflected value that cancelled. Near an integer order the two series above approach
   * one another and their difference is what survives, so the loss has to be measured on the same
   * scale the guard inside the series uses.
   */
  private static Complex reflectionChecked(final Complex result, final Complex first,
      final Complex second) {
    if (!Double.isFinite(result.getReal()) || !Double.isFinite(result.getImaginary())) {
      return null;
    }
    final double largest = Math.max(first.norm(), second.norm());
    return largest > REFLECTION_MAX_CANCELLATION * Math.max(result.norm(), 1.0e-300) ? null
        : result;
  }
}
