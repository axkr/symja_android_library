package org.matheclipse.core.numerics.integral;

import org.hipparchus.analysis.integration.LebedevQuadrature;
import org.hipparchus.analysis.integration.gauss.GaussIntegrator;
import org.hipparchus.analysis.integration.gauss.GaussIntegratorFactory;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;

/**
 * Integrals of a function of three variables over the surface of a sphere and over a ball, by the
 * Lebedev rules on the unit sphere (and a Gauss-Legendre rule in the radius for the ball). The
 * rules grow until two successive estimates agree to the tolerance, or until the next rule would
 * take the number of evaluations of the function over {@link #MAX_EVALUATIONS}.
 */
public final class SphericalQuadrature {

  /** A function of three real variables with complex values. */
  @FunctionalInterface
  public interface Integrand {
    /** The value at <code>(x, y, z)</code>, {@link Complex#NaN} if there is none. */
    Complex value(double x, double y, double z);
  }

  /** The result of an integration. */
  public static final class Result {
    /** The last estimate of the integral. */
    public final Complex value;

    /** The absolute difference of the last two estimates. */
    public final double error;

    /** Whether the last two estimates agree to the tolerance. */
    public final boolean converged;

    Result(Complex value, double error, boolean converged) {
      this.value = value;
      this.error = error;
      this.converged = converged;
    }

    /** Whether the estimate has finite real and imaginary parts. */
    public boolean isFinite() {
      return Double.isFinite(value.getReal()) && Double.isFinite(value.getImaginary());
    }
  }

  /**
   * The number of evaluations of the function which a rule may bring the total to; a rule which
   * would exceed it isn't tried.
   */
  static final int MAX_EVALUATIONS = 50_000;

  /**
   * The numbers of points of the Lebedev rules which are tried in turn; each step raises the
   * degree of the polynomials integrated exactly by about half.
   */
  private static final int[] SPHERE_ORDERS =
      {26, 50, 110, 194, 302, 590, 1202, 2030, 3074, 4334, 5810};

  /** The numbers of radial Gauss-Legendre nodes used together with {@link #BALL_ORDERS}. */
  private static final int[] BALL_RADIAL_NODES = {4, 6, 8, 12, 16, 24, 32, 48};

  /** The numbers of points of the Lebedev rules used together with {@link #BALL_RADIAL_NODES}. */
  private static final int[] BALL_ORDERS = {26, 50, 110, 194, 302, 590, 1202, 2030};

  /** A rule of a sequence: its number of evaluations and its estimate of the integral. */
  private interface Rule {
    int evaluations(int k);

    Complex estimate(int k);
  }

  private SphericalQuadrature() {}

  /**
   * The integral of <code>f</code> over the surface of the sphere with the center
   * <code>(cx, cy, cz)</code> and the radius <code>r</code>.
   *
   * @param epsrel the relative tolerance
   * @param epsabs the absolute tolerance
   */
  public static Result sphere(Integrand f, double cx, double cy, double cz, double r,
      double epsrel, double epsabs) {
    EvalEngine engine = EvalEngine.get();
    double area = 4.0 * Math.PI * r * r;
    return untilConverged(SPHERE_ORDERS.length, new Rule() {
      @Override
      public int evaluations(int k) {
        return SPHERE_ORDERS[k];
      }

      @Override
      public Complex estimate(int k) {
        engine.checkTimeLimit();
        return surfaceMean(f, LebedevQuadrature.getRule(SPHERE_ORDERS[k]), cx, cy, cz, r)
            .multiply(area);
      }
    }, epsrel, epsabs);
  }

  /**
   * The integral of <code>f</code> over the ball with the center <code>(cx, cy, cz)</code> and the
   * radius <code>r</code>.
   *
   * @param epsrel the relative tolerance
   * @param epsabs the absolute tolerance
   */
  public static Result ball(Integrand f, double cx, double cy, double cz, double r, double epsrel,
      double epsabs) {
    EvalEngine engine = EvalEngine.get();
    GaussIntegratorFactory factory = new GaussIntegratorFactory();
    return untilConverged(BALL_ORDERS.length, new Rule() {
      @Override
      public int evaluations(int k) {
        return BALL_RADIAL_NODES[k] * BALL_ORDERS[k];
      }

      @Override
      public Complex estimate(int k) {
        LebedevQuadrature.Rule rule = LebedevQuadrature.getRule(BALL_ORDERS[k]);
        GaussIntegrator radial = factory.legendre(BALL_RADIAL_NODES[k], 0.0, r);
        Complex sum = Complex.ZERO;
        for (int i = 0; i < radial.getNumberOfPoints(); i++) {
          engine.checkTimeLimit();
          double rho = radial.getPoint(i);
          sum = sum.add(
              surfaceMean(f, rule, cx, cy, cz, rho).multiply(radial.getWeight(i) * rho * rho));
        }
        return sum.multiply(4.0 * Math.PI);
      }
    }, epsrel, epsabs);
  }

  /**
   * The estimates of the rules <code>0, 1, ...</code> up to <code>count - 1</code>, until two
   * successive ones agree to the tolerance, an estimate isn't finite, or the next rule would exceed
   * {@link #MAX_EVALUATIONS}.
   */
  private static Result untilConverged(int count, Rule rule, double epsrel, double epsabs) {
    Complex previous = null;
    double difference = Double.NaN;
    int evaluations = 0;
    for (int k = 0; k < count; k++) {
      evaluations += rule.evaluations(k);
      if (k > 1 && evaluations > MAX_EVALUATIONS) {
        break;
      }
      Complex current = rule.estimate(k);
      Result result = new Result(current, difference, false);
      if (!result.isFinite()) {
        return result;
      }
      if (previous != null) {
        difference = current.subtract(previous).norm();
        if (difference <= Math.max(epsrel * current.norm(), epsabs)) {
          return new Result(current, difference, true);
        }
      }
      previous = current;
    }
    return new Result(previous, difference, false);
  }

  /**
   * The mean of <code>f</code> over the sphere with the center <code>(cx, cy, cz)</code> and the
   * radius <code>r</code> by the Lebedev rule <code>rule</code>, whose weights sum to 1.
   */
  private static Complex surfaceMean(Integrand f, LebedevQuadrature.Rule rule, double cx,
      double cy, double cz, double r) {
    double[] x = rule.getX();
    double[] y = rule.getY();
    double[] z = rule.getZ();
    double[] w = rule.getW();
    double re = 0.0;
    double im = 0.0;
    for (int i = 0; i < w.length; i++) {
      Complex value = f.value(cx + r * x[i], cy + r * y[i], cz + r * z[i]);
      re += w[i] * value.getReal();
      im += w[i] * value.getImaginary();
    }
    return new Complex(re, im);
  }
}
