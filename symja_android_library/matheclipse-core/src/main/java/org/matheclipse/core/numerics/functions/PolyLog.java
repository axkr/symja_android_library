package org.matheclipse.core.numerics.functions;

import org.hipparchus.complex.Complex;

/**
 * The polylogarithm <code>Li_n(z) = sum over k &gt;= 1 of z^k / k^n</code> for real order and real
 * argument, in <code>double</code> arithmetic.
 *
 * <p>
 * This is the most expensive call in {@code Num}: the Apfloat route costs about 74 ms. Note that
 * {@code ZetaJS.polyLog} is not an alternative despite its name and its home in this package - its
 * double implementation is commented out and the method simply forwards to Apfloat.
 *
 * <p>
 * Three regimes:
 * <ul>
 * <li><b>order 1 and 0, and every non-positive integer order</b>: closed forms.
 * <code>Li_1(z) = -log(1-z)</code>, <code>Li_0(z) = z/(1-z)</code>, and
 * <code>Li_-n(z) = (sum of Eulerian numbers A(n,k) z^(n-k)) / (1-z)^(n+1)</code>. These are exact
 * and instant, and they matter: at <code>n = -2, z = 0.99</code> the closed form gives 1970100
 * where the defining series would have to sum thousands of terms of size 10^4 to get there.</li>
 * <li><b>everything else with a small argument</b>: the defining series, summed with compensation.
 * It converges geometrically, so the term count is set by <code>|z|</code> alone - about 350 terms
 * at <code>|z| = 0.9</code> and 3700 at 0.99.</li>
 * <li><b>argument at or beyond the unit circle</b>: not handled - <code>Li</code> has a branch cut
 * along <code>[1, Infinity)</code> and is not real beyond it for a general order, so the caller
 * falls back.</li>
 * </ul>
 *
 * <p>
 * <b>Validated domain.</b> See {@link #isSupported(double, double)}.
 */
public final class PolyLog {

  private static final double EPS = 1.0e-16;

  /**
   * Largest <code>|z|</code> handled by the series.
   *
   * <p>
   * The series needs about <code>log(eps)/log(|z|)</code> terms, which is 3700 at 0.99 and 37000 at
   * 0.999 - still far cheaper than the 74 ms Apfloat call, but the count grows without bound as
   * <code>|z|</code> approaches 1 while the sum itself approaches a singularity for
   * <code>n &lt;= 1</code>. Cutting at 0.999 keeps the worst case near 40 microseconds.
   */
  private static final double Z_MAX = 0.999;

  /** Enough for <code>|z| = 0.999</code> with room to spare; beyond it the point is withdrawn. */
  private static final int MAX_TERMS = 200000;

  /** Largest <code>|n|</code> for the Eulerian closed form; A(n,k) overflows a double past this. */
  private static final int MAX_NEGATIVE_ORDER = 15;

  private PolyLog() {}

  /**
   * Whether {@link #polyLog(double, double)} is trusted for this order and argument.
   *
   * <p>
   * The non-positive integer orders are rational functions with a pole only at <code>z = 1</code>,
   * so they are accepted for any <code>z</code> away from it; everything else needs
   * <code>|z| &lt;= 0.999</code>.
   */
  public static boolean isSupported(double n, double z) {
    if (Double.isNaN(n) || Double.isNaN(z)) {
      return false;
    }
    if (isNonPositiveInteger(n) && -n <= MAX_NEGATIVE_ORDER) {
      return z != 1.0 && Math.abs(z) < 1.0e150;
    }
    return Math.abs(z) <= Z_MAX;
  }

  private static boolean isNonPositiveInteger(double n) {
    return n <= 0.0 && Math.rint(n) == n;
  }

  /**
   * <code>Li_n(z)</code>, or NaN when the series did not settle within {@link #MAX_TERMS}.
   *
   * @param n the order
   * @param z the argument
   */
  public static double polyLog(final double n, final double z) {
    if (z == 0.0) {
      return 0.0;
    }
    // Li_1(z) = -log(1-z). log1p keeps the accuracy for a small z, where 1-z rounds to 1.
    if (n == 1.0) {
      return z < 1.0 ? -Math.log1p(-z) : Double.NaN;
    }
    if (isNonPositiveInteger(n)) {
      final int order = (int) -n;
      if (order <= MAX_NEGATIVE_ORDER) {
        return negativeIntegerOrder(order, z);
      }
    }
    if (Math.abs(z) > Z_MAX) {
      return Double.NaN;
    }
    return series(n, z);
  }

  /**
   * <code>Li_-order(z)</code> as the rational function it is.
   *
   * <p>
   * <code>Li_0(z) = z/(1-z)</code> and, for <code>order &gt;= 1</code>,
   * <code>Li_-order(z) = (sum over k of A(order,k) z^(order-k)) / (1-z)^(order+1)</code> with
   * <code>A</code> the Eulerian numbers, built here by their recurrence
   * <code>A(n,k) = (k+1)*A(n-1,k) + (n-k)*A(n-1,k-1)</code>.
   */
  private static double negativeIntegerOrder(final int order, final double z) {
    final double oneMinusZ = 1.0 - z;
    if (order == 0) {
      return z / oneMinusZ;
    }
    final double[] eulerian = eulerianRow(order);
    // Horner in z over the numerator sum(A(order,k) * z^(order-k)), k = 0..order-1
    double numerator = 0.0;
    for (int k = 0; k < order; k++) {
      numerator = numerator * z + eulerian[k];
    }
    numerator *= z; // the lowest power present is z^1, not z^0
    return numerator / Math.pow(oneMinusZ, order + 1.0);
  }

  /** Row <code>n</code> of the Eulerian triangle, <code>A(n,0) .. A(n,n-1)</code>. */
  private static double[] eulerianRow(final int n) {
    double[] row = new double[] {1.0};
    for (int i = 2; i <= n; i++) {
      final double[] next = new double[i];
      for (int k = 0; k < i; k++) {
        final double left = (k < row.length) ? row[k] : 0.0;
        final double right = (k > 0 && k - 1 < row.length) ? row[k - 1] : 0.0;
        next[k] = (k + 1) * left + (i - k) * right;
      }
      row = next;
    }
    return row;
  }

  /**
   * <code>sum over k &gt;= 1 of z^k / k^n</code>, by Kahan-compensated summation.
   *
   * <p>
   * The compensation is not decorative. For a positive argument every term has the same sign, so a
   * plain running total accumulates rounding in proportion to the term count - which reaches
   * thousands near the edge of the domain and would put the error at 1e-13, close enough to the
   * 1e-12 callers rely on to matter. Compensated, the error stays at a couple of ulps whatever the
   * count.
   */
  private static double series(final double n, final double z) {
    double sum = 0.0;
    double compensation = 0.0;
    double power = 1.0;
    double absoluteSum = 0.0;
    for (int k = 1; k <= MAX_TERMS; k++) {
      power *= z;
      // k^-n; for an integer order this stays exact enough, and pow handles the rest
      final double term = power * Math.pow(k, -n);
      final double y = term - compensation;
      final double t = sum + y;
      compensation = (t - sum) - y;
      sum = t;
      absoluteSum += Math.abs(term);
      if (Math.abs(term) <= EPS * Math.abs(sum)) {
        return cancellationChecked(sum, absoluteSum);
      }
      if (power == 0.0) {
        return cancellationChecked(sum, absoluteSum); // z^k underflowed - nothing left to add
      }
    }
    return Double.NaN;
  }

  /**
   * Withdraw the result when the series cancelled away its significant digits.
   *
   * <p>
   * A negative argument makes the series alternate, and a negative order makes the terms grow like
   * <code>k^|n|</code> before the geometric factor finally wins - so the partial sums can dwarf the
   * answer. At <code>n = -7.25, z = -0.999</code> the terms peak near <code>10^28</code> while the
   * sum is 1.23, and the result came out as <code>3*10^10</code>: every digit was rounding noise.
   * Comparing against the sum of magnitudes measures exactly that loss, so the caller can fall back
   * to arbitrary precision instead of being handed a fabricated number.
   */
  private static double cancellationChecked(final double sum, final double absoluteSum) {
    // the error is about EPS * absoluteSum; requiring it to stay a thousandth of the result keeps
    // the relative error near 1e-13, comfortably inside the 1e-12 callers rely on
    if (absoluteSum > 1.0e3 * Math.abs(sum)) {
      return Double.NaN;
    }
    return sum;
  }

  // ----------------------------------------------------------------------------------------
  // Complex arguments
  // ----------------------------------------------------------------------------------------

  /**
   * Largest <code>|z|</code> for the complex series. Kept below the real limit because the complex
   * series has no monotone-terms regime to lean on: the terms rotate as well as decay, so the
   * cancellation guard rather than the term count is what decides, and 0.95 is where the guard
   * still passes across the whole circle.
   */
  private static final double COMPLEX_Z_MAX = 0.95;

  /**
   * Whether {@link #polyLog(Complex, Complex)} is trusted here.
   *
   * <p>
   * As on the real side the non-positive integer orders are rational and accepted anywhere away
   * from the pole at <code>z = 1</code>; everything else needs the series to converge.
   */
  public static boolean isSupported(Complex n, Complex z) {
    if (n == null || z == null) {
      return false;
    }
    if (Double.isNaN(n.getReal()) || Double.isNaN(n.getImaginary()) || Double.isNaN(z.getReal())
        || Double.isNaN(z.getImaginary())) {
      return false;
    }
    if (isNonPositiveInteger(n) && -n.getReal() <= MAX_NEGATIVE_ORDER) {
      return !(z.getReal() == 1.0 && z.getImaginary() == 0.0) && z.norm() < 1.0e150;
    }
    return z.norm() <= COMPLEX_Z_MAX;
  }

  private static boolean isNonPositiveInteger(Complex n) {
    return n.getImaginary() == 0.0 && isNonPositiveInteger(n.getReal());
  }

  /**
   * <code>Li_n(z)</code> for complex order and argument, or null where the value cannot be trusted.
   *
   * <p>
   * <code>Li_1(z) = -log(1-z)</code> uses the principal logarithm, which places the branch cut
   * along <code>[1, Infinity)</code> where <code>Li</code> has it.
   */
  public static Complex polyLog(final Complex n, final Complex z) {
    if (!isSupported(n, z)) {
      return null;
    }
    if (z.getReal() == 0.0 && z.getImaginary() == 0.0) {
      return Complex.ZERO;
    }
    if (n.getImaginary() == 0.0 && n.getReal() == 1.0) {
      return Complex.ONE.subtract(z).log().negate();
    }
    if (isNonPositiveInteger(n)) {
      final int order = (int) -n.getReal();
      if (order <= MAX_NEGATIVE_ORDER) {
        return negativeIntegerOrder(order, z);
      }
    }
    return series(n, z);
  }

  /** The rational closed form at a non-positive integer order, in complex arithmetic. */
  private static Complex negativeIntegerOrder(final int order, final Complex z) {
    final Complex oneMinusZ = Complex.ONE.subtract(z);
    if (order == 0) {
      return z.divide(oneMinusZ);
    }
    final double[] eulerian = eulerianRow(order);
    Complex numerator = Complex.ZERO;
    for (int k = 0; k < order; k++) {
      numerator = numerator.multiply(z).add(eulerian[k]);
    }
    numerator = numerator.multiply(z);
    return numerator.divide(oneMinusZ.pow(order + 1.0));
  }

  /**
   * <code>sum over k &gt;= 1 of z^k / k^n</code> for complex order and argument.
   *
   * <p>
   * <code>k^-n</code> is <code>exp(-n log k)</code>, which for a complex order is a rotation as
   * well as a scaling - so the terms need not decay monotonically and the same cancellation guard
   * as the real series decides whether the answer survived.
   */
  private static Complex series(final Complex n, final Complex z) {
    Complex sum = Complex.ZERO;
    Complex power = Complex.ONE;
    double absoluteSum = 0.0;
    for (int k = 1; k <= MAX_TERMS; k++) {
      power = power.multiply(z);
      final Complex term = power.multiply(n.negate().multiply(Math.log(k)).exp());
      sum = sum.add(term);
      final double magnitude = term.norm();
      absoluteSum += magnitude;
      if (magnitude <= EPS * sum.norm()) {
        return absoluteSum > 1.0e3 * sum.norm() ? null : sum;
      }
      if (power.norm() == 0.0) {
        return absoluteSum > 1.0e3 * sum.norm() ? null : sum;
      }
    }
    return null;
  }
}
