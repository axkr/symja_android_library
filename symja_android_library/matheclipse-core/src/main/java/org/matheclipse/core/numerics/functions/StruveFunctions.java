package org.matheclipse.core.numerics.functions;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApcomplexHelper;

/**
 * The Struve functions <code>StruveH</code> and <code>StruveL</code> for a large argument.
 *
 * <p>
 * The library routine reaches both through a regularized hypergeometric series in
 * <code>z^2</code>, whose terms grow like <code>E^|z|</code> before they cancel, so the working
 * precision it needs grows with the argument: <code>StruveH(-0.8+1.2*I, 10007)</code> took 396
 * seconds at 25 digits, for a value Mathematica gives at once.
 *
 * <p>
 * For a large argument each is a Bessel function plus an algebraic correction (DLMF 11.6.1 and
 * 11.6.2):
 *
 * <pre>
 * StruveH(nu, z) ~ BesselY(nu, z) + 1/Pi*Sum(Gamma(k+1/2)/Gamma(nu+1/2-k)*(z/2)^(nu-2*k-1), k)
 * StruveL(nu, z) ~ BesselI(-nu, z) - 1/Pi*Sum((-1)^k*Gamma(k+1/2)/Gamma(nu+1/2-k)*(z/2)^(nu-2*k-1), k)
 * </pre>
 *
 * The Bessel functions themselves have large argument methods in the library and stay quick -
 * <code>BesselY(-0.8+1.2*I, 10007)</code> takes 123 ms. At <code>nu == 1/2</code> the correction
 * has one term and both lines are exact, which is the check that the signs are these. The series
 * is asymptotic, so it is summed to its smallest term and that term decides whether the answer is
 * returned at all.
 */
public final class StruveFunctions {

  /** Enough to reach the smallest term while |z| is only a little above the order. */
  private static final int MAX_TERMS = 80;

  /**
   * Largest error estimate accepted, relative to the largest term summed: three digits short of the
   * working precision, which at machine precision is 1E-14. A fixed tolerance would let a 30 digit
   * question be answered with 17 good digits wherever the series happens to stop there.
   */
  private static double tolerance(FixedPrecisionApcomplexHelper h) {
    long digits = Math.min(h.precision(), 300L);
    return Math.pow(10.0, -(digits - 3));
  }

  private StruveFunctions() {}

  /**
   * <code>StruveH(nu, z)</code>, or <code>null</code> when the expansion cannot reach the working
   * precision here and the caller should use the library routine instead.
   *
   * @param z the argument, with a positive real part: the expansion holds for
   *        <code>|arg z| < Pi</code>, and the half plane is the part of that nothing here has to
   *        argue about
   */
  public static Apcomplex struveH(Apcomplex nu, Apcomplex z, FixedPrecisionApcomplexHelper h) {
    if (z.real().signum() <= 0 || isInteger(nu)) {
      // The library's BesselY divides by Sin(nu*Pi) on the way to an integer order. Exactly at one
      // it reports a division by zero; at 1.0 and z == 30 it did not, and the rounding noise it
      // divided by instead cost ten of the 25 digits. The integer orders stay with the library
      // routine, which answers them as it did before.
      return null;
    }
    Apcomplex correction = correction(nu, z, false, h);
    if (correction == null) {
      return null;
    }
    Apcomplex besselY;
    try {
      besselY = h.besselY(nu, z);
    } catch (ApfloatArithmeticException aae) {
      return null;
    }
    return h.add(besselY, correction);
  }

  /**
   * <code>StruveL(nu, z)</code>, or <code>null</code> when the expansion cannot reach the working
   * precision here and the caller should use the library routine instead.
   *
   * @param z the argument, with a positive real part: the expansion holds for
   *        <code>|arg z| < Pi/2</code>
   */
  public static Apcomplex struveL(Apcomplex nu, Apcomplex z, FixedPrecisionApcomplexHelper h) {
    if (z.real().signum() <= 0) {
      return null;
    }
    Apcomplex correction = correction(nu, z, true, h);
    if (correction == null) {
      return null;
    }
    Apcomplex besselI;
    try {
      besselI = h.besselI(nu.negate(), z);
    } catch (ApfloatArithmeticException aae) {
      return null;
    }
    return h.subtract(besselI, correction);
  }

  /**
   * <code>1/Pi*Sum(s_k*Gamma(k+1/2)/Gamma(nu+1/2-k)*(z/2)^(nu-2*k-1), k)</code> with
   * <code>s_k == 1</code>, or <code>(-1)^k</code> when <code>alternating</code>, summed to its
   * smallest term - or <code>null</code> if that term is too large to answer for.
   */
  private static Apcomplex correction(Apcomplex nu, Apcomplex z, boolean alternating,
      FixedPrecisionApcomplexHelper h) {
    final long precision = h.precision();
    final Apcomplex half = new Apcomplex(new Apfloat("0.5", precision));
    final Apcomplex one = new Apcomplex(new Apfloat(1, precision));
    Apcomplex nuPlusHalf = h.add(nu, half);
    if (isNonPositiveInteger(nuPlusHalf)) {
      // Gamma(nu+1/2) has a pole, the leading terms vanish, and the step below would divide by
      // the zero it produces
      return null;
    }
    Apcomplex halfZ = h.multiply(z, half);
    Apcomplex inverseHalfZSquared = h.divide(one, h.multiply(halfZ, halfZ));
    if (alternating) {
      inverseHalfZSquared = inverseHalfZSquared.negate();
    }
    // k == 0: Gamma(1/2)/Gamma(nu+1/2)*(z/2)^(nu-1)
    Apcomplex sqrtPi = new Apcomplex(ApfloatMath.sqrt(ApfloatMath.pi(precision)));
    Apcomplex term = h.divide(sqrtPi, h.gamma(nuPlusHalf));
    term = h.multiply(term, h.exp(h.multiply(h.subtract(nu, one), h.log(halfZ))));

    Apcomplex total = Apcomplex.ZERO;
    double previous = Double.MAX_VALUE;
    double largest = 0.0;
    double smallest = Double.MAX_VALUE;
    for (int k = 0; k < MAX_TERMS; k++) {
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
        break;
      }
      // term_(k+1)/term_k = (k+1/2)*(nu-1/2-k)*(z/2)^-2, the gammas stepping by one each
      Apcomplex kPlusHalf = h.add(new Apcomplex(new Apfloat(k, precision)), half);
      Apcomplex nuMinusHalfMinusK =
          h.subtract(h.subtract(nu, half), new Apcomplex(new Apfloat(k, precision)));
      term = h.multiply(h.multiply(term, kPlusHalf), nuMinusHalfMinusK);
      term = h.multiply(term, inverseHalfZSquared);
    }
    if (largest != 0.0 && smallest / largest > tolerance(h)) {
      return null;
    }
    return h.divide(total, new Apcomplex(ApfloatMath.pi(precision)));
  }

  private static boolean isInteger(Apcomplex value) {
    if (value.imag().signum() != 0) {
      return false;
    }
    Apfloat real = value.real();
    return real.equals(real.truncate());
  }

  private static boolean isNonPositiveInteger(Apcomplex value) {
    if (value.imag().signum() != 0) {
      return false;
    }
    Apfloat real = value.real();
    return real.signum() <= 0 && real.equals(real.truncate());
  }

  /** The size of a term, for comparing one against the next - a double is precision enough. */
  private static double size(Apcomplex value) {
    return Math.hypot(value.real().doubleValue(), value.imag().doubleValue());
  }
}
