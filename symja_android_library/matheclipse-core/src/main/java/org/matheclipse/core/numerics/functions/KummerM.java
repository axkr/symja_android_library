package org.matheclipse.core.numerics.functions;

/**
 * Kummer's confluent hypergeometric function <code>M(a, b, z) = 1F1(a; b; z)</code> in double
 * precision for real arguments.
 *
 * <p>
 * The series <code>Sum((a)_n/(b)_n*z^n/n!)</code> is summed until a term is below the rounding
 * level of the sum, with a running bound of its rounding error. Where the series cancels (its
 * terms alternate in sign), Kummer's transformation <code>M(a, b, z) = E^z*M(b-a, b, -z)</code>
 * may not; the representation with the smaller error bound is taken. A value whose bound exceeds
 * {@link TricomiU#TOLERANCE} is not returned: the caller computes it in arbitrary precision.
 */
public final class KummerM {

  private static final double EPS = 0x1.0p-53;

  private static final int MAX_SERIES_TERMS = 1000;

  /** Beyond this argument the exponential of the transformation overflows. */
  private static final double MAX_ARGUMENT = 700.0;

  private KummerM() {}

  /**
   * <code>1F1(a; b; z)</code> for real arguments.
   *
   * @return <code>NaN</code> if the value can't be computed in double precision to a relative
   *         error below {@link TricomiU#TOLERANCE}, at a pole (<code>b</code> a non-positive
   *         integer) or for a non-finite argument
   */
  public static double hypergeometric1F1(double a, double b, double z) {
    if (!Double.isFinite(a) || !Double.isFinite(b) || !Double.isFinite(z)
        || Math.abs(z) > MAX_ARGUMENT) {
      return Double.NaN;
    }
    if (a == 0.0 || z == 0.0) {
      return 1.0;
    }
    if (b <= 0.0 && b == Math.rint(b)) {
      if (a <= 0.0 && a == Math.rint(a) && a > b) {
        // the series ends before its pole: a polynomial
        Sum polynomial = series(a, b, z);
        return polynomial != null && polynomial.value != 0.0
            && polynomial.error <= TricomiU.TOLERANCE * Math.abs(polynomial.value)
                ? polynomial.value
                : Double.NaN;
      }
      return Double.NaN;
    }
    double best = Double.NaN;
    double bestError = Double.POSITIVE_INFINITY;
    Sum direct = series(a, b, z);
    if (direct != null && direct.value != 0.0) {
      best = direct.value;
      bestError = direct.error / Math.abs(direct.value);
    }
    if (!(bestError <= TricomiU.TOLERANCE / 16.0)) {
      Sum transformed = series(b - a, b, -z);
      if (transformed != null && transformed.value != 0.0) {
        double factor = Math.exp(z);
        double value = factor * transformed.value;
        double error = transformed.error / Math.abs(transformed.value) + (Math.abs(z) + 1.0) * EPS;
        if (error < bestError && Double.isFinite(value) && value != 0.0) {
          best = value;
          bestError = error;
        }
      }
    }
    return bestError <= TricomiU.TOLERANCE ? best : Double.NaN;
  }

  /**
   * <code>0F1(; a; z)</code> for real arguments by its series, with the same error bound.
   *
   * @return <code>NaN</code> if the value can't be computed in double precision to a relative
   *         error below {@link TricomiU#TOLERANCE}, at a pole (<code>a</code> a non-positive
   *         integer) or for a non-finite argument
   */
  public static double hypergeometric0F1(double a, double z) {
    if (!Double.isFinite(a) || !Double.isFinite(z) || Math.abs(z) > MAX_ARGUMENT * MAX_ARGUMENT
        || (a <= 0.0 && a == Math.rint(a))) {
      return Double.NaN;
    }
    if (z == 0.0) {
      return 1.0;
    }
    double term = 1.0;
    double sum = 1.0;
    double error = 1.0;
    for (int k = 1; k <= MAX_SERIES_TERMS; k++) {
      double ratio = z / ((a + k - 1) * k);
      term *= ratio;
      sum += term;
      error += Math.abs(term) * (3.0 * k + 1.0);
      if (Math.abs(ratio) < 0.5 && Math.abs(term) < EPS * Math.abs(sum)) {
        error = (error + Math.abs(sum)) * EPS;
        return sum != 0.0 && error <= TricomiU.TOLERANCE * Math.abs(sum) ? sum : Double.NaN;
      }
    }
    return Double.NaN;
  }

  /**
   * <code>1F1(a; b; z)</code> for complex arguments, by the series or by Kummer's transformation
   * <code>E^z*M(b-a, b, -z)</code>, whichever has the smaller error bound.
   *
   * @return <code>null</code> if the value can't be computed in double precision to a relative
   *         error below {@link TricomiU#TOLERANCE}, at a pole or for a non-finite argument
   */
  public static org.hipparchus.complex.Complex hypergeometric1F1(org.hipparchus.complex.Complex a,
      org.hipparchus.complex.Complex b, org.hipparchus.complex.Complex z) {
    if (!a.isFinite() || !b.isFinite() || !z.isFinite() || z.norm() > MAX_ARGUMENT) {
      return null;
    }
    if (b.getImaginary() == 0.0 && b.getReal() <= 0.0 && b.getReal() == Math.rint(b.getReal())) {
      return null;
    }
    if (a.isZero() || z.isZero()) {
      return org.hipparchus.complex.Complex.ONE;
    }
    org.hipparchus.complex.Complex best = null;
    double bestError = Double.POSITIVE_INFINITY;
    ComplexSum direct = complexSeries(a, b, z);
    if (direct != null && !direct.value.isZero()) {
      best = direct.value;
      bestError = direct.error / direct.value.norm();
    }
    if (!(bestError <= TricomiU.TOLERANCE / 16.0)) {
      ComplexSum transformed = complexSeries(b.subtract(a), b, z.negate());
      if (transformed != null && !transformed.value.isZero()) {
        org.hipparchus.complex.Complex value = z.exp().multiply(transformed.value);
        double error = transformed.error / transformed.value.norm() + (z.norm() + 2.0) * 4.0 * EPS;
        if (error < bestError && value.isFinite() && !value.isZero()) {
          best = value;
          bestError = error;
        }
      }
    }
    return bestError <= TricomiU.TOLERANCE ? best : null;
  }

  /** A complex sum with the running bound of its absolute rounding error. */
  private static final class ComplexSum {
    final org.hipparchus.complex.Complex value;
    final double error;

    ComplexSum(org.hipparchus.complex.Complex value, double error) {
      this.value = value;
      this.error = error;
    }
  }

  // the series of 1F1 with complex numbers; a complex product or quotient costs a few units of
  // rounding, hence the larger factor per term
  private static ComplexSum complexSeries(org.hipparchus.complex.Complex a,
      org.hipparchus.complex.Complex b, org.hipparchus.complex.Complex z) {
    org.hipparchus.complex.Complex term = org.hipparchus.complex.Complex.ONE;
    org.hipparchus.complex.Complex sum = org.hipparchus.complex.Complex.ONE;
    double error = 1.0;
    for (int k = 1; k <= MAX_SERIES_TERMS; k++) {
      org.hipparchus.complex.Complex ratio =
          a.add(k - 1).divide(b.add(k - 1)).multiply(z).divide(k);
      term = term.multiply(ratio);
      if (term.isZero()) {
        return new ComplexSum(sum, (error + sum.norm()) * 4.0 * EPS);
      }
      sum = sum.add(term);
      error += term.norm() * (8.0 * k + 2.0);
      if (ratio.norm() < 0.5 && term.norm() < EPS * sum.norm()) {
        return new ComplexSum(sum, (error + sum.norm()) * 4.0 * EPS);
      }
    }
    return null;
  }

  /** A sum with the running bound of its absolute rounding error. */
  static final class Sum {
    final double value;
    final double error;

    Sum(double value, double error) {
      this.value = value;
      this.error = error;
    }
  }

  /**
   * The series <code>Sum((a)_n/(b)_n*z^n/n!)</code>, <code>b</code> not a non-positive integer,
   * with the bound of its rounding error.
   *
   * @return <code>null</code> if the series does not converge in {@link #MAX_SERIES_TERMS} terms
   */
  static Sum series(double a, double b, double z) {
    double term = 1.0;
    double sum = 1.0;
    double error = 1.0;
    for (int k = 1; k <= MAX_SERIES_TERMS; k++) {
      double ratio = (a + k - 1) / (b + k - 1) * z / k;
      term *= ratio;
      if (term == 0.0) {
        // a is a non-positive integer: a polynomial
        return new Sum(sum, (error + Math.abs(sum)) * EPS);
      }
      sum += term;
      error += Math.abs(term) * (3.0 * k + 1.0);
      if (Math.abs(ratio) < 0.5 && Math.abs(term) < EPS * Math.abs(sum)) {
        return new Sum(sum, (error + Math.abs(sum)) * EPS);
      }
    }
    return null;
  }
}
