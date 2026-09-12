package org.matheclipse.core.numerics.functions;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApcomplexHelper;

/**
 * The Hermite function of a non-integer order, for an argument large enough that its series is not
 * the way to reach it.
 *
 * <p>
 * <code>HermiteH(nu, z)</code> at a non-integer <code>nu</code> is a confluent hypergeometric
 * function of <code>z^2</code>, and that is how the library routine computes it - so its working
 * precision has to cover <code>E^(z^2)</code> before the terms cancel back down.
 * <code>HermiteH(1.5707963267948966, 1009)</code> never returned, for a value Mathematica gives at
 * once.
 *
 * <p>
 * For large <code>z</code> the function is instead
 *
 * <pre>
 * HermiteH(nu, z) ~ (2*z)^nu * Sum(Pochhammer(-nu/2, k)*Pochhammer((1-nu)/2, k)/k!*(-1/z^2)^k, k)
 * </pre>
 *
 * which is exact and terminating at a non-negative integer <code>nu</code> - <code>(2*z)^2*(1 -
 * 1/(2*z^2))</code> is <code>4*z^2 - 2</code>, which is <code>HermiteH(2, z)</code> - and
 * asymptotic otherwise. Being asymptotic, it is summed to its smallest term, and that term is the
 * error estimate which decides whether the answer is returned at all.
 */
public final class HermiteFunction {

  /** Enough to reach the smallest term while |z| is only a little above 1. */
  private static final int MAX_TERMS = 60;

  /**
   * Largest error estimate accepted, relative to the largest term summed: three digits short of the
   * working precision, which at machine precision is 1E-14. A fixed tolerance would let a 30 digit
   * question be answered with 17 good digits wherever the series happens to stop there.
   */
  private static double tolerance(FixedPrecisionApcomplexHelper h) {
    long digits = Math.min(h.precision(), 300L);
    return Math.pow(10.0, -(digits - 3));
  }

  private HermiteFunction() {}

  /**
   * <code>HermiteH(nu, z)</code>, or <code>null</code> when the expansion cannot reach the working
   * precision here and the caller should use the library routine instead.
   *
   * @param nu the order
   * @param z the argument, whose magnitude has to be well above 1 and whose real part has to be
   *        positive - the expansion is the one for <code>|arg z| < 3*Pi/4</code>, and the reflection
   *        that would carry it to the other half plane is left to the caller
   * @param h the working precision
   */
  public static Apcomplex hermiteH(Apcomplex nu, Apcomplex z, FixedPrecisionApcomplexHelper h) {
    if (z.real().signum() <= 0) {
      return null;
    }
    final Apcomplex one = new Apcomplex(new Apfloat(1, h.precision()));
    final Apcomplex two = new Apcomplex(new Apfloat(2, h.precision()));
    // -1/z^2, the variable the series runs in
    Apcomplex argument = h.divide(one, h.multiply(z, z)).negate();

    Apcomplex a = h.divide(nu, two).negate();
    Apcomplex b = h.divide(h.subtract(one, nu), two);

    Apcomplex total = one;
    Apcomplex term = one;
    double previous = Double.MAX_VALUE;
    double largest = 1.0;
    double smallest = Double.MAX_VALUE;
    for (int k = 0; k < MAX_TERMS; k++) {
      Apfloat index = new Apfloat(k, h.precision());
      // Pochhammer steps: (a)_(k+1) = (a)_k * (a + k)
      term = h.multiply(term, h.add(a, new Apcomplex(index)));
      term = h.multiply(term, h.add(b, new Apcomplex(index)));
      term = h.divide(term, new Apcomplex(new Apfloat(k + 1L, h.precision())));
      term = h.multiply(term, argument);
      double size = size(term);
      if (size > previous) {
        // an asymptotic series says nothing past its smallest term
        break;
      }
      total = h.add(total, term);
      largest = Math.max(largest, size);
      smallest = size;
      previous = size;
      if (size == 0.0) {
        // a non-negative integer order terminates the series exactly
        break;
      }
    }
    if (smallest / largest > tolerance(h)) {
      return null;
    }
    // (2*z)^nu
    Apcomplex power = h.exp(h.multiply(nu, h.log(h.multiply(two, z))));
    return h.multiply(power, total);
  }

  /** The size of a term, for comparing one against the next - a double is precision enough. */
  private static double size(Apcomplex value) {
    return Math.hypot(value.real().doubleValue(), value.imag().doubleValue());
  }
}
