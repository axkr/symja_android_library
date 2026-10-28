package org.matheclipse.core.numerics.functions;

import org.apfloat.Apfloat;
import org.hipparchus.special.Beta;
import org.hipparchus.special.Gamma;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.external.apfloat.ApfloatInverseGammaBetaMath;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Inverses of the regularized incomplete gamma and beta functions in <code>double</code>
 * precision.
 *
 * <p>
 * The forward functions are taken from {@link Gamma#regularizedGammaP(double, double)},
 * {@link Gamma#regularizedGammaQ(double, double)} and
 * {@link Beta#regularizedBeta(double, double, double)}. Each inverse starts from an analytic
 * approximation and refines it with a Halley iteration against the closed form derivative; a
 * bisection in <code>Log(x)</code> is used as a safety net if the iteration fails to converge.
 * </p>
 *
 * <p>
 * All routines keep <em>relative</em> accuracy in the tail which the caller selects: the lower
 * tail for <code>P</code> and the beta function, the upper tail for <code>Q</code>. This is what
 * distinguishes them from a generic bracketing solve over the CDF, which can only reach absolute
 * accuracy and returns exactly <code>0.0</code> or <code>1.0</code> for extreme probabilities.
 * </p>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Incomplete_gamma_function">Wikipedia - Incomplete
 *      gamma function</a>
 * @see <a href="https://en.wikipedia.org/wiki/Beta_function#Incomplete_beta_function">Wikipedia -
 *      Incomplete beta function</a>
 */
public final class InverseGammaBetaJS {

  private InverseGammaBetaJS() {}

  /** Relative convergence threshold of the Halley iterations. */
  private static final double EPS = 1.0e-15;

  /** Maximum number of Halley steps before the bisection safety net takes over. */
  private static final int MAX_HALLEY = 32;

  /** Maximum number of bisection steps of the safety net. */
  private static final int MAX_BISECTION = 300;

  /**
   * Solve <code>GammaRegularized(a, 0, x) == p</code> for <code>x</code>, i.e. invert the lower
   * regularized incomplete gamma function <code>P(a,x)</code>.
   *
   * @param a the shape parameter; must be positive
   * @param p a probability in the range <code>[0, 1]</code>
   * @return the unique <code>x >= 0</code> with <code>P(a,x) == p</code>
   */
  public static double invRegularizedGammaP(double a, double p) {
    checkShape(a, "a");
    checkProbability(p);
    if (p == 0.0) {
      return 0.0;
    }
    if (p == 1.0) {
      return Double.POSITIVE_INFINITY;
    }
    return inverseGamma(a, p, 1.0 - p);
  }

  /**
   * Solve <code>GammaRegularized(a, x) == q</code> for <code>x</code>, i.e. invert the upper
   * regularized incomplete gamma function <code>Q(a,x)</code>.
   *
   * @param a the shape parameter; must be positive
   * @param q a probability in the range <code>[0, 1]</code>
   * @return the unique <code>x >= 0</code> with <code>Q(a,x) == q</code>
   */
  public static double invRegularizedGammaQ(double a, double q) {
    checkShape(a, "a");
    checkProbability(q);
    if (q == 0.0) {
      return Double.POSITIVE_INFINITY;
    }
    if (q == 1.0) {
      return 0.0;
    }
    return inverseGamma(a, 1.0 - q, q);
  }

  /**
   * Invert the regularized incomplete gamma function.
   *
   * <p>
   * Both tail probabilities are passed in so that the residual can be formed in whichever tail is
   * small; <code>p</code> and <code>q</code> sum to one but the caller knows which of them carries
   * the full relative accuracy.
   * </p>
   */
  private static double inverseGamma(double a, double p, double q) {
    final boolean lowerTail = p <= q;
    final double lnGammaA = Gamma.logGamma(a);
    final double a1 = a - 1.0;

    // analytic initial guess, Numerical Recipes "gammpInv"
    double x;
    if (a > 1.0) {
      final double pp = lowerTail ? p : q;
      final double t = FastMath.sqrt(-2.0 * FastMath.log(pp));
      double u = (2.30753 + t * 0.27061) / (1.0 + t * (0.99229 + t * 0.04481)) - t;
      if (lowerTail) {
        u = -u;
      }
      x = FastMath.max(1.0e-3,
          a * cube(1.0 - 1.0 / (9.0 * a) - u / (3.0 * FastMath.sqrt(a))));
    } else {
      final double t = 1.0 - a * (0.253 + a * 0.12);
      if (p < t) {
        x = FastMath.pow(p / t, 1.0 / a);
      } else {
        x = 1.0 - FastMath.log1p(-(p - t) / (1.0 - t));
      }
    }

    // Halley refinement against the closed form derivative P'(a,x) = x^(a-1) * E^(-x) / Gamma(a)
    for (int j = 0; j < MAX_HALLEY; j++) {
      if (x <= 0.0) {
        break;
      }
      final double err = lowerTail //
          ? Gamma.regularizedGammaP(a, x) - p //
          : q - Gamma.regularizedGammaQ(a, x);
      final double density = FastMath.exp(a1 * FastMath.log(x) - x - lnGammaA);
      if (density == 0.0 || Double.isNaN(density) || Double.isInfinite(density)) {
        break;
      }
      final double u = err / density;
      double step = u / (1.0 - 0.5 * FastMath.min(1.0, u * (a1 / x - 1.0)));
      if (Double.isNaN(step)) {
        break;
      }
      x -= step;
      if (x <= 0.0) {
        x = 0.5 * (x + step);
      }
      if (FastMath.abs(step) < EPS * x) {
        break;
      }
    }

    if (gammaConverged(a, x, p, q, lowerTail)) {
      return x;
    }
    return bisectGamma(a, p, q, lowerTail);
  }

  private static boolean gammaConverged(double a, double x, double p, double q,
      boolean lowerTail) {
    if (!(x > 0.0) || Double.isNaN(x)) {
      return false;
    }
    final double target = lowerTail ? p : q;
    final double actual =
        lowerTail ? Gamma.regularizedGammaP(a, x) : Gamma.regularizedGammaQ(a, x);
    return FastMath.abs(actual - target) <= 1.0e-11 * target;
  }

  /** Bisection in <code>Log(x)</code>; only reached if the Halley iteration failed. */
  private static double bisectGamma(double a, double p, double q, boolean lowerTail) {
    // see the note in bisectBeta - the root can underflow the double range entirely
    if (gammaResidual(a, Double.MIN_NORMAL, p, q, lowerTail) >= 0.0) {
      return 0.0;
    }
    double lo = FastMath.log(FastMath.max(Double.MIN_NORMAL, a * 1.0e-8));
    double hi = FastMath.log(FastMath.max(1.0, a) * 4.0);
    while (gammaResidual(a, FastMath.exp(lo), p, q, lowerTail) > 0.0 && lo > -740.0) {
      lo -= 8.0;
    }
    while (gammaResidual(a, FastMath.exp(hi), p, q, lowerTail) < 0.0 && hi < 740.0) {
      hi += 8.0;
    }
    for (int j = 0; j < MAX_BISECTION && (hi - lo) > 1.0e-16 * FastMath.abs(hi); j++) {
      final double mid = 0.5 * (lo + hi);
      if (gammaResidual(a, FastMath.exp(mid), p, q, lowerTail) < 0.0) {
        lo = mid;
      } else {
        hi = mid;
      }
    }
    return FastMath.exp(0.5 * (lo + hi));
  }

  /** Increasing in <code>x</code>, zero at the root. */
  private static double gammaResidual(double a, double x, double p, double q, boolean lowerTail) {
    return lowerTail ? Gamma.regularizedGammaP(a, x) - p : q - Gamma.regularizedGammaQ(a, x);
  }

  /**
   * Solve <code>BetaRegularized(x, a, b) == p</code> for <code>x</code>, i.e. invert the
   * regularized incomplete beta function <code>I_x(a,b)</code>.
   *
   * @param p a probability in the range <code>[0, 1]</code>
   * @param a the first shape parameter; must be positive
   * @param b the second shape parameter; must be positive
   * @return the unique <code>x</code> in <code>[0, 1]</code> with <code>I_x(a,b) == p</code>
   */
  public static double invRegularizedBeta(double p, double a, double b) {
    checkShape(a, "a");
    checkShape(b, "b");
    checkProbability(p);
    if (p == 0.0) {
      return 0.0;
    }
    if (p == 1.0) {
      return 1.0;
    }
    return inverseBeta(p, a, b);
  }

  @SuppressFBWarnings(value = "FE_FLOATING_POINT_EQUALITY",
      justification = "the iteration stops at the exact end points of the interval")
  private static double inverseBeta(double p, double a, double b) {
    final double a1 = a - 1.0;
    final double b1 = b - 1.0;
    final double lnBeta = Beta.logBeta(a, b);

    // analytic initial guess, Numerical Recipes "betaiInv"
    double x;
    if (a >= 1.0 && b >= 1.0) {
      final double pp = p < 0.5 ? p : 1.0 - p;
      final double t = FastMath.sqrt(-2.0 * FastMath.log(pp));
      double u = (2.30753 + t * 0.27061) / (1.0 + t * (0.99229 + t * 0.04481)) - t;
      if (p < 0.5) {
        u = -u;
      }
      final double al = (u * u - 3.0) / 6.0;
      final double h = 2.0 / (1.0 / (2.0 * a - 1.0) + 1.0 / (2.0 * b - 1.0));
      final double w = (u * FastMath.sqrt(al + h) / h)
          - (1.0 / (2.0 * b - 1.0) - 1.0 / (2.0 * a - 1.0)) * (al + 5.0 / 6.0 - 2.0 / (3.0 * h));
      x = a / (a + b * FastMath.exp(2.0 * w));
    } else {
      // power series branch - this is what keeps tiny roots such as
      // InverseBetaRegularized(10^-12, 1/10, 1/10) from underflowing to zero
      final double t = FastMath.exp(a * FastMath.log(a / (a + b))) / a;
      final double u = FastMath.exp(b * FastMath.log(b / (a + b))) / b;
      final double w = t + u;
      if (p < t / w) {
        x = FastMath.pow(a * w * p, 1.0 / a);
      } else {
        x = -FastMath.expm1(FastMath.log(b * w * (1.0 - p)) / b);
      }
    }
    if (!(x > 0.0)) {
      x = Double.MIN_NORMAL;
    } else if (!(x < 1.0)) {
      x = 1.0 - EPS;
    }

    // Halley refinement against I'_x(a,b) = x^(a-1) * (1-x)^(b-1) / Beta(a,b)
    for (int j = 0; j < MAX_HALLEY; j++) {
      if (x == 0.0 || x == 1.0) {
        break;
      }
      final double err = Beta.regularizedBeta(x, a, b) - p;
      final double density =
          FastMath.exp(a1 * FastMath.log(x) + b1 * FastMath.log1p(-x) - lnBeta);
      if (density == 0.0 || Double.isNaN(density) || Double.isInfinite(density)) {
        break;
      }
      final double u = err / density;
      double step = u / (1.0 - 0.5 * FastMath.min(1.0, u * (a1 / x - b1 / (1.0 - x))));
      if (Double.isNaN(step)) {
        break;
      }
      x -= step;
      if (x <= 0.0) {
        x = 0.5 * (x + step);
      }
      if (x >= 1.0) {
        x = 0.5 * (x + step + 1.0);
      }
      if (FastMath.abs(step) < EPS * x && j > 0) {
        break;
      }
    }

    if (betaConverged(x, a, b, p)) {
      return x;
    }
    return bisectBeta(p, a, b);
  }

  private static boolean betaConverged(double x, double a, double b, double p) {
    if (Double.isNaN(x) || x < 0.0 || x > 1.0) {
      return false;
    }
    return FastMath.abs(Beta.regularizedBeta(x, a, b) - p) <= 1.0e-11 * p;
  }

  /** Bisection in <code>Log(x)</code>; only reached if the Halley iteration failed. */
  private static double bisectBeta(double p, double a, double b) {
    // For extreme shape parameters the exact root can be far outside the double range - for
    // example I_x(1/1000, 1000) == 10^-12 has the root 10^-12003. Report the representable
    // boundary rather than an arbitrary subnormal.
    if (Beta.regularizedBeta(Double.MIN_NORMAL, a, b) >= p) {
      return 0.0;
    }
    if (Beta.regularizedBeta(Math.nextDown(1.0), a, b) <= p) {
      return 1.0;
    }
    double lo = -745.0;
    double hi = 0.0;
    for (int j = 0; j < MAX_BISECTION && (hi - lo) > 1.0e-16; j++) {
      final double mid = 0.5 * (lo + hi);
      if (Beta.regularizedBeta(FastMath.exp(mid), a, b) - p < 0.0) {
        lo = mid;
      } else {
        hi = mid;
      }
    }
    return FastMath.exp(0.5 * (lo + hi));
  }

  /**
   * Solve <code>BetaRegularized(z1, x, a, b) == z2</code> for <code>x</code>, the generalized
   * (four argument) inverse.
   *
   * @param z1 the lower integration limit
   * @param z2 the value of the generalized regularized beta function
   * @param a the first shape parameter; must be positive
   * @param b the second shape parameter; must be positive
   * @return the <code>x</code> with <code>I_x(a,b) == I_z1(a,b) + z2</code>
   */
  public static double invRegularizedBeta(double z1, double z2, double a, double b) {
    checkShape(a, "a");
    checkShape(b, "b");
    if (z2 == 0.0) {
      return z1;
    }
    if (z1 == 0.0) {
      return invRegularizedBeta(z2, a, b);
    }
    if (z1 == 1.0) {
      // I_z1(a,b) is exactly 1 here, so the target is 1 + z2 with z2 <= 0. Reflecting through
      // I_x(a,b) == 1 - I_(1-x)(b,a) keeps the small quantity -z2 intact instead of forming
      // 1 + z2 and losing its leading digits.
      return 1.0 - invRegularizedBeta(-z2, b, a);
    }
    return invRegularizedBeta(Beta.regularizedBeta(z1, a, b) + z2, a, b);
  }

  /**
   * Solve <code>GammaRegularized(a, z1, x) == z2</code> for <code>x</code>, the three argument
   * inverse.
   *
   * @param a the shape parameter; must be positive
   * @param z1 the lower integration limit
   * @param z2 the value of the generalized regularized gamma function
   * @return the <code>x</code> with <code>Q(a,x) == Q(a,z1) - z2</code>
   */
  public static double invRegularizedGamma(double a, double z1, double z2) {
    checkShape(a, "a");
    if (z1 == 0.0) {
      // Q(a,0) is exactly 1, so this is the lower inverse. Going through the upper inverse with
      // 1 - z2 would throw away every significant digit of a small z2.
      return invRegularizedGammaP(a, z2);
    }
    if (Double.isInfinite(z1)) {
      return invRegularizedGammaQ(a, -z2);
    }
    return invRegularizedGammaQ(a, Gamma.regularizedGammaQ(a, z1) - z2);
  }

  // ---------------------------------------------------------------------------------------
  // arbitrary precision: org.matheclipse.external.apfloat.ApfloatInverseGammaBetaMath, which is
  // given the machine precision result as its starting value where there is one
  // ---------------------------------------------------------------------------------------

  /**
   * Arbitrary precision version of {@link #invRegularizedBeta(double, double, double)}.
   *
   * @param p a probability in the range <code>[0, 1]</code>
   * @param a the first shape parameter; must be positive
   * @param b the second shape parameter; must be positive
   * @param precision the requested number of decimal digits
   * @return the <code>x</code> with <code>I_x(a,b) == p</code>
   */
  public static Apfloat invRegularizedBeta(Apfloat p, Apfloat a, Apfloat b, long precision) {
    double seed = Double.NaN;
    try {
      seed = invRegularizedBeta(p.doubleValue(), a.doubleValue(), b.doubleValue());
    } catch (RuntimeException rex) {
      // no machine number to start from
    }
    return ApfloatInverseGammaBetaMath.inverseBetaRegularized(p, a, b, precision, seed);
  }

  /**
   * Arbitrary precision version of {@link #invRegularizedGammaP(double, double)}.
   *
   * @param a the shape parameter; must be positive
   * @param p a probability in the range <code>[0, 1]</code>
   * @param precision the requested number of decimal digits
   * @return the <code>x</code> with <code>P(a,x) == p</code>
   */
  public static Apfloat invRegularizedGammaP(Apfloat a, Apfloat p, long precision) {
    double seed = Double.NaN;
    try {
      seed = invRegularizedGammaP(a.doubleValue(), p.doubleValue());
    } catch (RuntimeException rex) {
      // no machine number to start from
    }
    return ApfloatInverseGammaBetaMath.inverseGammaRegularized(a, p, false, precision, seed);
  }

  /**
   * Arbitrary precision version of {@link #invRegularizedGammaQ(double, double)}.
   *
   * @param a the shape parameter; must be positive
   * @param q a probability in the range <code>[0, 1]</code>
   * @param precision the requested number of decimal digits
   * @return the <code>x</code> with <code>Q(a,x) == q</code>
   */
  public static Apfloat invRegularizedGammaQ(Apfloat a, Apfloat q, long precision) {
    double seed = Double.NaN;
    try {
      seed = invRegularizedGammaQ(a.doubleValue(), q.doubleValue());
    } catch (RuntimeException rex) {
      // no machine number to start from
    }
    return ApfloatInverseGammaBetaMath.inverseGammaRegularized(a, q, true, precision, seed);
  }

  /**
   * Arbitrary precision version of {@link #invRegularizedBeta(double, double, double, double)}.
   *
   * @return the <code>x</code> with <code>I_x(a,b) == I_z1(a,b) + z2</code>
   */
  public static Apfloat invRegularizedBeta(Apfloat z1, Apfloat z2, Apfloat a, Apfloat b,
      long precision) {
    return ApfloatInverseGammaBetaMath.inverseBetaRegularized(z1, z2, a, b, precision);
  }

  /**
   * Arbitrary precision version of {@link #invRegularizedGamma(double, double, double)}.
   *
   * @return the <code>x</code> with <code>Q(a,x) == Q(a,z1) - z2</code>
   */
  public static Apfloat invRegularizedGamma(Apfloat a, Apfloat z1, Apfloat z2, long precision) {
    return ApfloatInverseGammaBetaMath.inverseGammaRegularized(a, z1, z2, precision);
  }

  private static double cube(double x) {
    return x * x * x;
  }

  private static void checkShape(double a, String name) {
    if (Double.isNaN(a)) {
      throw new ArgumentTypeException("Argument " + name + " is not a number.");
    }
    if (a <= 0.0) {
      throw new ArgumentTypeException("Argument " + name + " is not positive: " + a);
    }
  }

  private static void checkProbability(double p) {
    if (Double.isNaN(p)) {
      throw new ArgumentTypeException("Probability is not a number.");
    }
    if (p < 0.0 || p > 1.0) {
      throw new ArgumentTypeException("Probability out of range [0, 1]: " + p);
    }
  }
}
