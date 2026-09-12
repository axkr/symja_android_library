package org.matheclipse.core.numerics.functions;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApcomplexHelper;

/**
 * The Anger function for an order far larger than its argument.
 *
 * <p>
 * <code>AngerJ(nu, z) = 1/Pi * Integral(Cos(nu*t - z*Sin(t)), {t, 0, Pi})</code>. The library
 * routine reaches it through a regularized hypergeometric function whose cost grows with
 * <code>|nu|</code>: 11 ms at an order of 1000, 1.4 s at 1000000, and
 * <code>AngerJ(-9223372036854775808/11, -0.8)</code> never returned at all - for a value
 * Mathematica gives at once.
 *
 * <p>
 * Where <code>|nu| > |z|</code> the phase <code>nu*t - z*Sin(t)</code> has no stationary point in
 * <code>(0, Pi)</code>, so repeated integration by parts moves the whole integral to its two
 * endpoints:
 *
 * <pre>
 * Integral(E^(I*(nu*t - z*Sin(t))), {t, 0, Pi}) ~ Sum(c_m*(E^(I*Pi*nu) - (-1)^m)/(I*nu)^(m+1), m)
 * </pre>
 *
 * with <code>c_m</code> the Taylor derivatives of <code>E^(-I*z*Sin(t))</code> at
 * <code>t == 0</code>. Its cost is the same at any order. The series is asymptotic in
 * <code>1/nu</code>, so it is summed to its smallest term and that term is the error estimate,
 * which is what decides whether the answer is returned at all.
 */
public final class AngerWeber {

  /** Enough for the series to reach its smallest term while |nu| is only a little above |z|. */
  private static final int MAX_TERMS = 24;

  /**
   * Largest error estimate accepted, relative to the largest term summed. Measured against the
   * library routine at <code>nu == 100.25</code>: an estimate of 3.7E-16 came with a true error of
   * 2.3E-16, and one of 1.9E-5 with a true error of 7.6E-6 - the estimate bounds the error rather
   * than tracking it, which is the way round this needs to be.
   */
  private static final double TOLERANCE = 1.0e-14;

  private AngerWeber() {}

  /**
   * <code>AngerJ(nu, z)</code>, or <code>null</code> when the expansion cannot reach the working
   * precision here and the caller should use the library routine instead.
   *
   * @param nu the order, whose magnitude has to be well above <code>|z|</code>
   * @param nuModTwo <code>nu</code> reduced modulo 2, which is all <code>E^(I*Pi*nu)</code> needs
   *        and all that a large order has left to say
   * @param z the argument
   * @param h the working precision
   */
  public static Apcomplex angerJ(Apfloat nu, Apfloat nuModTwo, Apcomplex z,
      FixedPrecisionApcomplexHelper h) {
    // Cos(nu*t - z*Sin(t)) is the half sum of the two exponentials, and the second is the first
    // with both arguments negated: for real arguments that is the real part, and for complex ones
    // it is what continues it analytically
    double[] error = new double[2];
    Apcomplex forward = endpointSum(nu, nuModTwo, z, h, error, 0);
    Apcomplex backward = endpointSum(nu.negate(), nuModTwo.negate(), z.negate(), h, error, 1);
    if (Math.max(error[0], error[1]) > TOLERANCE) {
      return null;
    }
    Apfloat twoPi = ApfloatMath.pi(h.precision()).multiply(new Apfloat(2, h.precision()));
    return h.divide(h.add(forward, backward), new Apcomplex(twoPi));
  }

  /**
   * <code>Sum(c_m*(E^(I*Pi*nu) - (-1)^m)/(I*nu)^(m+1), m)</code>, summed to its smallest term, with
   * the size of that term left in <code>error[slot]</code> relative to the largest term summed.
   */
  private static Apcomplex endpointSum(Apfloat nu, Apfloat nuModTwo, Apcomplex z,
      FixedPrecisionApcomplexHelper h, double[] error, int slot) {
    Apcomplex[] taylor = expOfMinusISin(z, h);
    Apcomplex iPiNu = h.multiply(h.multiply(imaginaryUnit(h),
        new Apcomplex(ApfloatMath.pi(h.precision()))), new Apcomplex(nuModTwo));
    Apcomplex expIPiNu = h.exp(iPiNu);
    final Apcomplex iNu = h.multiply(imaginaryUnit(h), new Apcomplex(nu));
    final Apcomplex one = new Apcomplex(new Apfloat(1, h.precision()));

    Apcomplex total = Apcomplex.ZERO;
    Apcomplex power = one;
    Apfloat factorial = new Apfloat(1, h.precision());
    double previous = Double.MAX_VALUE;
    double largest = 0.0;
    double smallest = Double.MAX_VALUE;
    for (int m = 0; m <= MAX_TERMS; m++) {
      if (m > 0) {
        factorial = factorial.multiply(new Apfloat(m, h.precision()));
      }
      // c_m = m! * [t^m] E^(-I*z*Sin(t))
      Apcomplex term = h.multiply(taylor[m], new Apcomplex(factorial));
      term = h.multiply(term, (m & 1) == 0 ? h.subtract(expIPiNu, one) : h.add(expIPiNu, one));
      power = h.multiply(power, iNu);
      term = h.divide(term, power);
      double size = size(term);
      if (size > previous) {
        // an asymptotic series says nothing past its smallest term
        break;
      }
      total = h.add(total, term);
      largest = Math.max(largest, size);
      smallest = size;
      previous = size;
    }
    error[slot] = largest == 0.0 ? 0.0 : smallest / largest;
    return total;
  }

  /**
   * The Taylor coefficients of <code>E^(-I*z*Sin(t))</code> at <code>t == 0</code>, by
   * <code>E_n = 1/n*Sum(k*S_k*E_(n-k), {k, 1, n})</code> for <code>E == E^S</code>.
   */
  private static Apcomplex[] expOfMinusISin(Apcomplex z, FixedPrecisionApcomplexHelper h) {
    Apcomplex w = h.multiply(imaginaryUnit(h).negate(), z);
    Apcomplex[] sine = new Apcomplex[MAX_TERMS + 1];
    java.util.Arrays.fill(sine, Apcomplex.ZERO);
    Apfloat factorial = new Apfloat(1, h.precision());
    for (int k = 1, sign = 1; k <= MAX_TERMS; k += 2, sign = -sign) {
      for (int i = (k == 1) ? 1 : k - 1; i <= k; i++) {
        factorial = factorial.multiply(new Apfloat(i, h.precision()));
      }
      Apcomplex coefficient = h.divide(w, new Apcomplex(factorial));
      sine[k] = sign > 0 ? coefficient : coefficient.negate();
    }
    Apcomplex[] result = new Apcomplex[MAX_TERMS + 1];
    result[0] = new Apcomplex(new Apfloat(1, h.precision()));
    for (int n = 1; n <= MAX_TERMS; n++) {
      Apcomplex sum = Apcomplex.ZERO;
      for (int k = 1; k <= n; k++) {
        sum = h.add(sum, h.multiply(h.multiply(sine[k], new Apcomplex(new Apfloat(k))),
            result[n - k]));
      }
      result[n] = h.divide(sum, new Apcomplex(new Apfloat(n, h.precision())));
    }
    return result;
  }

  private static Apcomplex imaginaryUnit(FixedPrecisionApcomplexHelper h) {
    return new Apcomplex(new Apfloat(0, h.precision()), new Apfloat(1, h.precision()));
  }

  /** The size of a term, for comparing one against the next - a double is precision enough. */
  private static double size(Apcomplex value) {
    double real = value.real().doubleValue();
    double imaginary = value.imag().doubleValue();
    return Math.hypot(real, imaginary);
  }
}
