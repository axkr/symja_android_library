package org.matheclipse.core.numerics.functions;

import org.hipparchus.complex.Complex;

/**
 * The Nielsen generalized polylogarithm
 * <code>S(n, p, z) = 1/(Gamma(n)*Gamma(p+1)) * Integrate((-Log(t))^(n-1)*(-Log(1-z*t))^p/t,
 * {t, 0, 1})</code> in <code>double</code> arithmetic, for real <code>n, p &gt; 0</code> and a
 * complex <code>z</code>. For integer orders this is the form with <code>(-1)^(n+p-1)</code> in
 * front; for others the signs inside the powers give the real values on <code>0 &lt; z &lt; 1</code>.
 *
 * <p>
 * The powers and logarithms are the principal ones. For a real <code>z &gt; 1</code> the integrand
 * has a branch point at <code>t = 1/z</code>, where the integral is split; on the cut beyond it
 * <code>Log(1-z*t)</code> has the imaginary part <code>+Pi</code>, which gives the values of
 * <code>PolyLog(n, z)</code> below the cut. Both pieces are summed by the tanh-sinh rule, whose
 * nodes crowd to the end points and take the integrable singularities there.
 */
public final class NielsenPolyLog {

  /** The relative accuracy at which the step halving stops. */
  private static final double TOLERANCE = 1.0e-14;

  private NielsenPolyLog() {}

  /**
   * <code>S(n, p, z)</code>, or <code>null</code> if the quadrature does not settle.
   *
   * @param n a real <code>n &gt; 0</code>
   * @param p a real <code>p &gt; 0</code>
   */
  public static Complex value(double n, double p, Complex z) {
    if (!(n > 0.0) || !(p > 0.0) || z.isNaN() || z.isInfinite()) {
      return null;
    }
    if (z.getReal() == 0.0 && z.getImaginary() == 0.0) {
      return Complex.ZERO;
    }
    Complex integral;
    if (z.getImaginary() == 0.0 && z.getReal() > 1.0) {
      double c = 1.0 / z.getReal();
      Complex left = integrate(n, p, z, 0.0, c, c);
      Complex right = integrate(n, p, z, c, 1.0, c);
      if (left == null || right == null) {
        return null;
      }
      integral = left.add(right);
    } else {
      integral = integrate(n, p, z, 0.0, 1.0, Double.NaN);
      if (integral == null) {
        return null;
      }
    }
    return integral.divide(GammaJS.gamma(n) * GammaJS.gamma(p + 1.0));
  }

  /**
   * The integrand <code>(-Log(t))^(n-1)*(-Log(1-z*t))^p/t</code> at <code>t</code>, where
   * <code>oneMinusT</code> is <code>1-t</code> and <code>oneMinusZt</code> is <code>1-z*t</code>
   * to full precision.
   */
  private static Complex integrand(double n, double p, Complex z, double t, double oneMinusT,
      Complex oneMinusZt) {
    if (t <= 0.0) {
      return Complex.ZERO;
    }
    // near t == 1 the logarithm comes from 1-t, near 0 Log(1-z*t) from z*t
    Complex logT = Complex.valueOf(t > 0.5 ? Math.log1p(-oneMinusT) : Math.log(t));
    Complex zt = z.multiply(t);
    Complex logOneMinusZt;
    if (zt.norm() < 1.0e-3) {
      // Log(1-w) == -w - w^2/2 - w^3/3 - ...
      Complex sum = Complex.ZERO;
      Complex power = Complex.ONE;
      for (int k = 1; k <= 8; k++) {
        power = power.multiply(zt);
        sum = sum.subtract(power.divide(k));
      }
      logOneMinusZt = sum;
    } else {
      if (oneMinusZt.getImaginary() == 0.0 && oneMinusZt.getReal() < 0.0) {
        // on the cut: the side with the imaginary part +Pi
        oneMinusZt = Complex.valueOf(oneMinusZt.getReal(), 0.0);
      }
      logOneMinusZt = oneMinusZt.log();
    }
    // the signs inside the powers: both logarithms are negative for 0 < z < 1
    Complex first = n == 1.0 ? Complex.ONE : powerOf(logT.negate(), n - 1.0);
    Complex second = powerOf(logOneMinusZt.negate(), p);
    return first.multiply(second).divide(t);
  }

  /** The principal power, exact for small integer exponents. */
  private static Complex powerOf(Complex base, double exponent) {
    if (exponent == Math.rint(exponent) && exponent >= 0 && exponent <= 16) {
      Complex result = Complex.ONE;
      for (int i = 0; i < (int) exponent; i++) {
        result = result.multiply(base);
      }
      return result;
    }
    if (base.getReal() == 0.0 && base.getImaginary() == 0.0) {
      return Complex.ZERO;
    }
    return base.pow(exponent);
  }

  /**
   * The tanh-sinh sum of the integrand over <code>[a, b]</code>, halving the step. A node is
   * given by its distances to both ends, which keep their precision where <code>t</code> itself
   * rounds to an end point; <code>branch</code> is the zero <code>1/z</code> of
   * <code>1-z*t</code> if it is an end point, otherwise <code>NaN</code>.
   */
  private static Complex integrate(double n, double p, Complex z, double a, double b,
      double branch) {
    final double half = 0.5 * (b - a);
    Complex previous = null;
    for (int level = 3; level <= 12; level++) {
      double h = 1.0 / (1 << level);
      Complex sum = Complex.ZERO;
      for (int k = -(int) (4.0 / h); k <= (int) (4.0 / h); k++) {
        double u = k * h;
        double s = Math.PI * 0.5 * Math.sinh(u);
        double weight = Math.PI * 0.5 * Math.cosh(u) / (Math.cosh(s) * Math.cosh(s));
        if (weight < 1.0e-300) {
          continue;
        }
        // t - a = half*(1 + tanh(s)) and b - t = half*(1 - tanh(s)) without cancellation
        double e = Math.exp(-2.0 * s);
        double fromA = half * 2.0 / (1.0 + e);
        double fromB = half * 2.0 * e / (1.0 + e);
        if (!(fromA > 0.0) || !(fromB > 0.0)) {
          continue;
        }
        double t = fromA <= fromB ? a + fromA : b - fromB;
        double oneMinusT = b == 1.0 ? fromB : 1.0 - t;
        Complex oneMinusZt;
        if (a == branch) {
          oneMinusZt = z.multiply(-fromA);
        } else if (b == branch) {
          oneMinusZt = z.multiply(fromB);
        } else {
          oneMinusZt = Complex.ONE.subtract(z.multiply(t));
        }
        Complex f = integrand(n, p, z, t, oneMinusT, oneMinusZt);
        if (f.isNaN() || f.isInfinite()) {
          continue;
        }
        sum = sum.add(f.multiply(weight));
      }
      Complex value = sum.multiply(h * half);
      if (previous != null) {
        double difference = value.subtract(previous).norm();
        if (difference <= TOLERANCE * Math.max(1.0, value.norm())) {
          return value;
        }
      }
      previous = value;
    }
    return null;
  }
}
