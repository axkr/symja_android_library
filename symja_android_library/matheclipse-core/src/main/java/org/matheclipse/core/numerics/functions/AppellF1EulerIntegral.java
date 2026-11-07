package org.matheclipse.core.numerics.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.numerics.integral.ApfloatDoubleExponential;

/**
 * Appell's <i>F<sub>1</sub></i> from its Euler integral, for the arguments which the series and
 * their transformations don't reach:
 *
 * <pre>
 * F1(a; b1, b2; c; x, y) = Gamma(c) / (Gamma(a) Gamma(c - a))
 *     * Integrate(t^(a-1) (1-t)^(c-a-1) (1-x t)^(-b1) (1-y t)^(-b2), {t, 0, 1})
 * </pre>
 *
 * which holds for <code>Re(c) &gt; Re(a) &gt; 0</code>. The powers are principal values, so for a real
 * argument greater than one the integral is the limit from below the branch cut, the same value as
 * <code>Hypergeometric2F1</code> takes on its cut.
 *
 * <p>
 * The integrand is singular at <code>t = 0</code>, <code>t = 1</code> and, for a real argument
 * <code>z &gt;= 1</code>, at <code>t = 1/z</code>. The interval is cut at these points and every
 * piece is cut once more in its middle, so that each part has a singular point at one end only.
 * Each part is integrated in the distance <code>u</code> from that end, and the factors which
 * vanish there are computed from <code>u</code>: <code>1 - z t = -z u</code> just above
 * <code>1/z</code>, rather than as a difference which loses the digits the singularity depends on.
 * The integral exists only if every exponent at a singular point is greater than <code>-1</code>.
 */
public final class AppellF1EulerIntegral {

  /** Digits beyond the requested precision for the parameters. */
  private static final long GUARD_DIGITS = 10;

  /** The step of the machine precision quadrature is halved at most this many times. */
  private static final int MACHINE_LEVELS = 8;

  /** Relative difference of two machine precision estimates which ends the halving. */
  private static final double MACHINE_TOLERANCE = 1.0e-13;

  /** A point where the integrand may be singular, with the arguments whose factor vanishes there. */
  private static final class Point {
    final Apfloat t;
    /** The sum of the exponents of the factors which vanish at <code>t</code>. */
    Apcomplex exponent;
    boolean x;
    boolean y;

    Point(Apfloat t, Apcomplex exponent) {
      this.t = t;
      this.exponent = exponent;
    }
  }

  private final Apcomplex aMinus1;
  private final Apcomplex cMinusAMinus1;
  private final Apcomplex minusB1;
  private final Apcomplex minusB2;
  private final Apcomplex x;
  private final Apcomplex y;
  private final Apfloat zero;
  private final Apfloat one;

  private AppellF1EulerIntegral(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x,
      Apcomplex y, long working) {
    this.one = new Apfloat(1, working);
    this.zero = new Apfloat(0, working);
    this.aMinus1 = a.subtract(one);
    this.cMinusAMinus1 = c.subtract(a).subtract(one);
    this.minusB1 = b1.negate();
    this.minusB2 = b2.negate();
    this.x = x;
    this.y = y;
  }

  /**
   * <i>F<sub>1</sub>(a; b<sub>1</sub>, b<sub>2</sub>; c; x, y)</i> with <code>precision</code>
   * digits, or <code>null</code> if the integral doesn't exist for these parameters or doesn't
   * converge.
   *
   * @param machine if <code>true</code> the integral is summed in <code>double</code>, and only
   *        if that doesn't converge in apfloat with <code>precision</code> digits
   */
  public static Apcomplex value(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x,
      Apcomplex y, long precision, boolean machine) {
    // a machine number has fewer digits than the quadrature compares its estimates to; its binary
    // value is exact, so the missing digits are zeros
    final long working = precision + GUARD_DIGITS;
    a = widen(a, working);
    b1 = widen(b1, working);
    b2 = widen(b2, working);
    c = widen(c, working);
    x = widen(x, working);
    y = widen(y, working);
    final Apcomplex cMinusA = c.subtract(a);
    if (a.real().signum() <= 0 || cMinusA.real().signum() <= 0) {
      return null;
    }
    AppellF1EulerIntegral integral = new AppellF1EulerIntegral(a, b1, b2, c, x, y, working);
    List<Point> points = integral.singularPoints();
    if (points == null) {
      return null;
    }
    try {
      Apcomplex sum = machine ? integral.machineSum(points) : null;
      if (sum == null) {
        sum = Apcomplex.ZERO;
        for (int i = 1; i < points.size(); i++) {
          Point lower = points.get(i - 1);
          Point upper = points.get(i);
          Apfloat half = upper.t.subtract(lower.t).divide(new Apfloat(2, working));
          sum = sum.add(integral.part(lower, 1, half, precision));
          sum = sum.add(integral.part(upper, -1, half, precision));
        }
      }
      Apcomplex gammas = ApcomplexMath.gamma(c)
          .divide(ApcomplexMath.gamma(a).multiply(ApcomplexMath.gamma(cMinusA)));
      return gammas.multiply(sum);
    } catch (ApfloatDoubleExponential.Failure | ArithmeticException | ApfloatRuntimeException ex) {
      return null;
    }
  }

  /**
   * The points <code>0</code>, <code>1/z</code> for a real argument <code>z &gt; 1</code>, and
   * <code>1</code> in increasing order, or <code>null</code> if the integral doesn't exist.
   */
  private List<Point> singularPoints() {
    List<Point> points = new ArrayList<Point>();
    points.add(new Point(zero, aMinus1));
    points.add(new Point(one, cMinusAMinus1));
    for (int k = 0; k < 2; k++) {
      Apcomplex z = k == 0 ? x : y;
      Apcomplex exponent = k == 0 ? minusB1 : minusB2;
      if (z.imag().signum() != 0 || z.real().compareTo(one) < 0 || isZero(exponent)) {
        continue;
      }
      Apfloat t = one.divide(z.real());
      Point point = null;
      for (Point p : points) {
        if (p.t.compareTo(t) == 0) {
          point = p;
        }
      }
      if (point == null) {
        point = new Point(t, Apcomplex.ZERO);
        points.add(point);
      }
      point.exponent = point.exponent.add(exponent);
      if (k == 0) {
        point.x = true;
      } else {
        point.y = true;
      }
    }
    for (Point p : points) {
      if (p.exponent.real().compareTo(one.negate()) <= 0) {
        return null;
      }
    }
    points.sort((p, q) -> p.t.compareTo(q.t));
    return points;
  }

  /**
   * The integral from the point <code>near</code> over the distance <code>length</code> in the
   * direction <code>side</code>, which is <code>1</code> or <code>-1</code>.
   */
  private Apcomplex part(Point near, int side, Apfloat length, long precision)
      throws ApfloatDoubleExponential.Failure {
    final boolean atZero = near.t.signum() == 0;
    final boolean atOne = near.t.compareTo(one) == 0;
    final Apfloat sign = side > 0 ? one : one.negate();
    ApfloatDoubleExponential.Sampler integrand = u -> {
      try {
        Apfloat signedU = u.multiply(sign);
        Apfloat t = near.t.add(signedU);
        Apfloat tBase = atZero ? u : t;
        Apfloat oneMinusT = atOne ? u : one.subtract(t);
        // 1 - z t = -z (t - 1/z) next to t = 1/z
        Apcomplex xBase = near.x ? x.multiply(signedU).negate() : one.subtract(x.multiply(t));
        Apcomplex yBase = near.y ? y.multiply(signedU).negate() : one.subtract(y.multiply(t));
        return pow(tBase, aMinus1).multiply(pow(oneMinusT, cMinusAMinus1))
            .multiply(pow(xBase, minusB1)).multiply(pow(yBase, minusB2));
      } catch (ArithmeticException | ApfloatRuntimeException ex) {
        return null;
      }
    };
    // three more digits for the sum of the parts
    final long digits = precision + 3;
    final Apfloat ePlus1 = near.exponent.real().add(one);
    if (ePlus1.compareTo(one) >= 0) {
      return ApfloatDoubleExponential.integrate(integrand, zero, length, digits);
    }
    // u = s^k with k = 1/(e+1) turns u^e du into k s^0 ds: the quadrature can't sample close
    // enough to the end to integrate a strong singularity u^e, -1 < e < 0
    final Apfloat k = one.divide(ePlus1);
    ApfloatDoubleExponential.Sampler smooth = s -> {
      Apfloat u = ApfloatMath.pow(s, k);
      Apcomplex value = integrand.value(u);
      // du = k s^(k-1) ds = k u/s ds
      return value == null ? null : value.multiply(k.multiply(u).divide(s));
    };
    return ApfloatDoubleExponential.integrate(smooth, zero, ApfloatMath.pow(length, ePlus1),
        digits);
  }

  /**
   * The sum of all parts in <code>double</code>, with the same cuts and substitutions as
   * {@link #part}, or <code>null</code> if a part doesn't converge to machine precision.
   */
  private Apcomplex machineSum(List<Point> points) {
    final Complex dA1 = complex(aMinus1);
    final Complex dCA1 = complex(cMinusAMinus1);
    final Complex dB1 = complex(minusB1);
    final Complex dB2 = complex(minusB2);
    final Complex dx = complex(x);
    final Complex dy = complex(y);
    Complex sum = Complex.ZERO;
    for (int i = 1; i < points.size(); i++) {
      Point lower = points.get(i - 1);
      Point upper = points.get(i);
      double half = (upper.t.doubleValue() - lower.t.doubleValue()) / 2.0;
      for (int side = 1; side >= -1; side -= 2) {
        final Point near = side > 0 ? lower : upper;
        final double nearT = near.t.doubleValue();
        final double sign = side;
        final boolean atZero = near.t.signum() == 0;
        final boolean atOne = near.t.compareTo(one) == 0;
        final double ePlus1 = near.exponent.real().doubleValue() + 1.0;
        final double k = ePlus1 < 1.0 ? 1.0 / ePlus1 : 1.0;
        DoubleFunction<Complex> integrand = s -> {
          double u = k == 1.0 ? s : Math.pow(s, k);
          if (u == 0.0) {
            // underflow next to the end
            return null;
          }
          double t = nearT + sign * u;
          Complex value = power(atZero ? u : t, dA1).multiply(power(atOne ? u : 1.0 - t, dCA1))
              .multiply(power(near.x ? dx.multiply(-sign * u) : oneMinus(dx, t), dB1))
              .multiply(power(near.y ? dy.multiply(-sign * u) : oneMinus(dy, t), dB2));
          return k == 1.0 ? value : value.multiply(k * u / s);
        };
        Complex part = tanhSinh(integrand, ePlus1 < 1.0 ? Math.pow(half, ePlus1) : half);
        if (part == null) {
          return null;
        }
        sum = sum.add(part);
      }
    }
    return new Apcomplex(new Apfloat(sum.getReal()), new Apfloat(sum.getImaginary()));
  }

  /**
   * Tanh-sinh quadrature over <code>[0, length]</code> in <code>double</code>; an abscissa next to
   * zero is computed from its distance to zero. <code>null</code> if the estimates don't agree to
   * machine precision or a sample isn't finite.
   */
  private static Complex tanhSinh(DoubleFunction<Complex> f, double length) {
    final double halfPi = Math.PI / 2.0;
    Complex sum = Complex.ZERO;
    Complex previous = null;
    double h = 1.0;
    for (int level = 0; level <= MACHINE_LEVELS; level++) {
      EvalEngine.get().checkTimeLimit();
      final int step = level == 0 ? 1 : 2;
      for (int sign = 1; sign >= -1; sign -= 2) {
        int start = level == 0 ? (sign > 0 ? 0 : 1) : 1;
        double largest = 0.0;
        int negligible = 0;
        for (int j = start;; j += step) {
          double t = sign * j * h;
          if (Math.abs(t) > 4.0) {
            break;
          }
          double v = halfPi * Math.sinh(t);
          double s = length / (1.0 + Math.exp(-2.0 * v));
          if (s <= 0.0 || s >= length) {
            // the abscissa merged with an end
            break;
          }
          double coshV = Math.cosh(v);
          double weight = length * halfPi * Math.cosh(t) / (2.0 * coshV * coshV);
          Complex value = f.apply(s);
          if (value == null) {
            break;
          }
          if (!Double.isFinite(value.getReal()) || !Double.isFinite(value.getImaginary())) {
            return null;
          }
          Complex term = value.multiply(weight);
          sum = sum.add(term);
          double magnitude = term.norm();
          largest = Math.max(largest, magnitude);
          if (magnitude <= 1.0e-18 * largest) {
            if (++negligible >= 3) {
              break;
            }
          } else {
            negligible = 0;
          }
        }
      }
      Complex estimate = sum.multiply(h);
      if (previous != null
          && estimate.subtract(previous).norm() <= MACHINE_TOLERANCE * estimate.norm()) {
        return estimate;
      }
      previous = estimate;
      h /= 2.0;
    }
    return null;
  }

  private static Complex complex(Apcomplex z) {
    return new Complex(z.real().doubleValue(), z.imag().doubleValue());
  }

  private static Complex oneMinus(Complex z, double t) {
    return new Complex(1.0 - z.getReal() * t, -z.getImaginary() * t);
  }

  /**
   * The principal power of a real base, with the argument <code>Pi</code> for a negative one; a
   * <code>-0.0</code> imaginary part can't turn it into <code>-Pi</code> here.
   */
  private static Complex power(double base, Complex w) {
    if (w.getReal() == 0.0 && w.getImaginary() == 0.0) {
      return Complex.ONE;
    }
    Complex log = base < 0.0 ? new Complex(Math.log(-base), Math.PI) : new Complex(Math.log(base));
    return w.multiply(log).exp();
  }

  private static Complex power(Complex base, Complex w) {
    if (base.getImaginary() == 0.0) {
      return power(base.getReal(), w);
    }
    return base.pow(w);
  }

  private static Apcomplex widen(Apcomplex z, long precision) {
    return z.precision() < precision ? z.precision(precision) : z;
  }

  private static boolean isZero(Apcomplex z) {
    return z.real().signum() == 0 && z.imag().signum() == 0;
  }

  private static Apcomplex pow(Apcomplex z, Apcomplex w) {
    if (isZero(w)) {
      return Apcomplex.ONE;
    }
    return ApcomplexMath.pow(z, w);
  }
}
