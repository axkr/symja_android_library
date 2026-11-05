package org.matheclipse.core.numerics.integral;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.EvalEngine;

/**
 * Double exponential quadrature in arbitrary precision: tanh-sinh on a finite interval, exp-sinh
 * on a half-infinite one and sinh-sinh on the whole real line. The step is halved until two
 * estimates agree to the requested number of digits. An integrable singularity at a finite end is
 * never sampled: the abscissas next to it are computed from their distance to the end.
 */
public final class ApfloatDoubleExponential {

  /** The integrand. */
  @FunctionalInterface
  public interface Sampler {
    /**
     * @return the value at <code>x</code>, or <code>null</code> if there is no numerical value
     */
    Apcomplex value(Apfloat x);
  }

  /** The integral did not converge or the integrand had no numerical value. */
  public static final class Failure extends Exception {
    private static final long serialVersionUID = 1L;

    Failure(String message) {
      super(message, null, false, false);
    }
  }

  private static final int MAX_LEVELS = 10;

  private final Sampler f;
  private final long precision;
  private final long workPrecision;
  private final Apfloat halfPi;
  private final Apfloat epsilon;

  private ApfloatDoubleExponential(Sampler f, long precision) {
    this.f = f;
    this.precision = precision;
    this.workPrecision = precision + 15;
    this.halfPi = ApfloatMath.pi(workPrecision).divide(new Apfloat(2, workPrecision));
    this.epsilon = ApfloatMath.pow(new Apfloat(10, workPrecision), -(precision + 3));
  }

  /**
   * The integral of <code>f</code> from <code>a</code> to <code>b</code>.
   *
   * @param a the lower end, <code>null</code> for minus infinity
   * @param b the upper end, <code>null</code> for infinity
   * @param precision the number of correct digits wanted
   * @throws Failure if the estimates don't converge or a sample has no numerical value
   */
  public static Apcomplex integrate(Sampler f, Apfloat a, Apfloat b, long precision)
      throws Failure {
    return new ApfloatDoubleExponential(f, precision).integrate(a, b);
  }

  private Apcomplex integrate(Apfloat a, Apfloat b) throws Failure {
    Apcomplex previous = null;
    Apcomplex sum = Apcomplex.ZERO;
    Apfloat h = new Apfloat(1, workPrecision);
    for (int level = 0; level <= MAX_LEVELS; level++) {
      // the new abscissas are the odd multiples of h; level 0 takes every integer
      final int step = level == 0 ? 1 : 2;
      final int start = level == 0 ? 0 : 1;
      sum = sum.add(sweep(a, b, h, start, step, 1));
      sum = sum.add(sweep(a, b, h, Math.max(start, 1), step, -1));
      Apcomplex estimate = sum.multiply(h);
      if (previous != null) {
        Apfloat difference = ApcomplexMath.abs(estimate.subtract(previous));
        Apfloat scale = ApcomplexMath.abs(estimate);
        if (difference.compareTo(epsilon.multiply(scale.signum() == 0 ? Apfloat.ONE : scale)) <= 0
            || difference.signum() == 0) {
          return estimate.precision(precision);
        }
      }
      previous = estimate;
      h = h.divide(new Apfloat(2, workPrecision));
    }
    throw new Failure("no convergence");
  }

  /**
   * The weighted samples at <code>t = sign*k*h</code>, <code>k = start, start+step, ...</code>,
   * until they are negligible.
   */
  private Apcomplex sweep(Apfloat a, Apfloat b, Apfloat h, int start, int step, int sign)
      throws Failure {
    final EvalEngine engine = EvalEngine.get();
    Apcomplex sum = Apcomplex.ZERO;
    // the largest weighted sample so far: a term is negligible relative to it
    Apfloat largest = Apfloat.ZERO;
    int negligible = 0;
    for (int k = start;; k += step) {
      engine.checkTimeLimit();
      Apfloat t = h.multiply(new Apfloat(sign * (long) k, workPrecision));
      if (ApfloatMath.abs(t).compareTo(new Apfloat(8, workPrecision)) > 0) {
        return sum;
      }
      Apcomplex term = weightedSample(a, b, t);
      if (term == null) {
        // the abscissa merged with an end of the interval
        return sum;
      }
      sum = sum.add(term);
      Apfloat magnitude = ApcomplexMath.abs(term);
      if (magnitude.compareTo(largest) > 0) {
        largest = magnitude;
      }
      if (magnitude.compareTo(epsilon.multiply(largest)) <= 0) {
        if (++negligible >= 3) {
          return sum;
        }
      } else {
        negligible = 0;
      }
    }
  }

  /**
   * The precision of an abscissa <code>end +- distance</code> which keeps the working precision of
   * the distance: an end of zero needs no extra digits.
   */
  private long guardedDigits(Apfloat end, Apfloat distance) {
    if (end.signum() == 0) {
      return workPrecision;
    }
    return workPrecision + Math.max(0L, end.scale() - distance.scale());
  }

  /**
   * <code>w(t)*f(x(t))</code>, or <code>null</code> if <code>x(t)</code> can't be told apart from
   * a finite end.
   */
  private Apcomplex weightedSample(Apfloat a, Apfloat b, Apfloat t) throws Failure {
    final Apfloat u = halfPi.multiply(ApfloatMath.sinh(t));
    final Apfloat du = halfPi.multiply(ApfloatMath.cosh(t));
    final Apfloat x;
    final Apfloat weight;
    if (a != null && b != null) {
      // x = (a+b)/2 + (b-a)/2*tanh(u), the distance to the nearer end is (b-a)/(1+E^(2|u|))
      final Apfloat half = b.subtract(a).divide(new Apfloat(2, workPrecision));
      final Apfloat e = ApfloatMath.exp(ApfloatMath.abs(u).multiply(new Apfloat(2, workPrecision)));
      final Apfloat distance = b.subtract(a).divide(Apfloat.ONE.add(e));
      if (distance.signum() == 0 || distance.compareTo(ApfloatMath.abs(b.subtract(a))
          .multiply(ApfloatMath.pow(new Apfloat(10, workPrecision), -2 * workPrecision))) < 0) {
        return null;
      }
      final Apfloat nearEnd = u.signum() >= 0 ? b : a;
      final long digits = guardedDigits(nearEnd, distance);
      x = u.signum() >= 0 ? b.precision(digits).subtract(distance)
          : a.precision(digits).add(distance);
      if (x.compareTo(a) <= 0 || x.compareTo(b) >= 0) {
        // the abscissa rounded onto an end, where the integrand may be singular
        return null;
      }
      final Apfloat cosh = ApfloatMath.cosh(u);
      weight = half.multiply(du).divide(cosh.multiply(cosh));
    } else if (a != null || b != null) {
      // x = a + E^u or x = b - E^u
      final Apfloat e = ApfloatMath.exp(u);
      final Apfloat end = a != null ? a : b;
      if (end.signum() != 0 && e.compareTo(ApfloatMath.abs(end)
          .multiply(ApfloatMath.pow(new Apfloat(10, workPrecision), -workPrecision))) < 0) {
        // the abscissa merged with the finite end
        return null;
      }
      final long digits = guardedDigits(end, e);
      x = a != null ? a.precision(digits).add(e) : b.precision(digits).subtract(e);
      if (x.compareTo(end) == 0) {
        return null;
      }
      weight = du.multiply(e);
    } else {
      // x = Sinh(u)
      x = ApfloatMath.sinh(u);
      weight = du.multiply(ApfloatMath.cosh(u));
    }
    if (weight.signum() == 0) {
      return Apcomplex.ZERO;
    }
    final Apcomplex value = f.value(x);
    if (value == null) {
      throw new Failure("not numerical");
    }
    return value.multiply(weight);
  }
}
