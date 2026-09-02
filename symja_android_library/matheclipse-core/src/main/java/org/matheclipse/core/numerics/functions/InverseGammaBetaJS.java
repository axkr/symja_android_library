package org.matheclipse.core.numerics.functions;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.LossOfPrecisionException;
import org.hipparchus.special.Beta;
import org.hipparchus.special.Gamma;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.eval.exception.ArgumentTypeException;

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
  // arbitrary precision
  // ---------------------------------------------------------------------------------------

  /** Number of correct decimal digits the <code>double</code> seed contributes. */
  private static final long SEED_PRECISION = 15;

  /** Extra digits carried through the refinement and dropped from the result. */
  private static final long GUARD_DIGITS = 10;

  /**
   * Working precision to try, as digits added on top of the requested precision.
   *
   * <p>
   * The incomplete beta and gamma functions are evaluated by series which cancel heavily for large
   * shape parameters - <code>B_(1/2)(1000,1000)</code> loses roughly 435 digits, enough that
   * apfloat reports a complete loss of accuracy. There is no cheap way to predict how much is
   * lost, so widen the working precision and try again.
   * </p>
   */
  private static final long[] EXTRA_PRECISION = {GUARD_DIGITS, 100, 700, 3000};

  /** Maximum number of Newton steps of an arbitrary precision refinement. */
  private static final int MAX_NEWTON_STEPS = 64;

  /**
   * Arbitrary precision version of {@link #invRegularizedBeta(double, double, double)}.
   *
   * <p>
   * The <code>double</code> result seeds a Newton iteration on
   * <code>g(x) = B_x(a,b) - p * B(a,b)</code>, whose derivative <code>x^(a-1) * (1-x)^(b-1)</code>
   * is available in closed form. The working precision doubles on every step, which is what makes
   * the cost of the refinement comparable to a single evaluation at the target precision.
   * </p>
   *
   * @param p a probability in the range <code>[0, 1]</code>
   * @param a the first shape parameter; must be positive
   * @param b the second shape parameter; must be positive
   * @param precision the requested number of decimal digits
   * @return the <code>x</code> with <code>I_x(a,b) == p</code>
   */
  public static Apfloat invRegularizedBeta(Apfloat p, Apfloat a, Apfloat b, long precision) {
    ApfloatRuntimeException failure = null;
    for (int attempt = 0; attempt < EXTRA_PRECISION.length; attempt++) {
      long working = precision + EXTRA_PRECISION[attempt];
      try {
        Apfloat x = refineBeta(p, a, b, working, attempt == 0);
        if (betaVerified(x, p, a, b, working, precision)) {
          return new FixedPrecisionApfloatHelper(precision).valueOf(x);
        }
      } catch (ApfloatRuntimeException are) {
        failure = are;
      }
    }
    throw failure != null ? failure
        : new LossOfPrecisionException("Complete loss of accurate digits");
  }

  /**
   * Newton refinement of <code>g(x) = B_x(a,b) - p * B(a,b)</code>, whose derivative
   * <code>x^(a-1) * (1-x)^(b-1)</code> is available in closed form.
   *
   * @param doubling refine with a doubling working precision, which is the cheap path; the
   *        retries run every step at the full working precision instead, because a low precision
   *        evaluation of the incomplete beta is worthless for large shape parameters and would
   *        push the iteration somewhere it cannot recover from
   */
  private static Apfloat refineBeta(Apfloat p, Apfloat a, Apfloat b, long working,
      boolean doubling) {
    Apfloat x = seed(invRegularizedBeta(p.doubleValue(), a.doubleValue(), b.doubleValue()));
    long prec = doubling ? SEED_PRECISION : working;
    for (int step = 0; step < MAX_NEWTON_STEPS; step++) {
      if (doubling) {
        if (prec >= working && step > 0) {
          break;
        }
        prec = Math.min(2 * prec, working);
      }
      final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(prec);
      final Apfloat ah = h.valueOf(a);
      final Apfloat bh = h.valueOf(b);
      final Apfloat one = h.valueOf(Apfloat.ONE);
      x = h.valueOf(x);
      try {
        final Apfloat scaled = h.multiply(h.valueOf(p), h.beta(ah, bh));
        final Apfloat g = h.subtract(h.beta(x, ah, bh), scaled);
        final Apfloat dg = h.multiply(h.pow(x, h.subtract(ah, one)), //
            h.pow(h.subtract(one, x), h.subtract(bh, one)));
        x = clampToUnitInterval(h, h.subtract(x, h.divide(g, dg)), x);
      } catch (LossOfPrecisionException lope) {
        // the residual cancelled completely, so x already agrees with the target to the full
        // working precision
        break;
      }
      if (!doubling && step >= 2) {
        break;
      }
    }
    return x;
  }

  /** Keep a Newton step inside <code>(0, 1)</code> by halving it towards the previous iterate. */
  private static Apfloat clampToUnitInterval(FixedPrecisionApfloatHelper h, Apfloat x,
      Apfloat previous) {
    final Apfloat one = h.valueOf(Apfloat.ONE);
    if (x.signum() <= 0 || x.compareTo(one) >= 0) {
      return h.divide(h.add(previous, h.valueOf(new Apfloat(x.signum() <= 0 ? 0 : 1))), //
          h.valueOf(new Apfloat(2)));
    }
    return x;
  }

  /** Check that <code>I_x(a,b)</code> reproduces <code>p</code> at the working precision. */
  private static boolean betaVerified(Apfloat x, Apfloat p, Apfloat a, Apfloat b, long working,
      long precision) {
    final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(working);
    if (x.signum() <= 0 || x.compareTo(h.valueOf(Apfloat.ONE)) >= 0) {
      return false;
    }
    final Apfloat actual;
    try {
      actual = h.divide(h.beta(x, a, b), h.beta(a, b));
    } catch (ApfloatRuntimeException are) {
      // the forward function is not computable at this working precision, so nothing is verified
      return false;
    }
    try {
      return h.abs(h.subtract(actual, h.valueOf(p))).compareTo(tolerance(h, p, precision)) <= 0;
    } catch (LossOfPrecisionException lope) {
      // the two values agree in every digit the working precision carries
      return true;
    }
  }

  /**
   * Relative tolerance <code>p * 10^-precision</code> of the verification.
   *
   * <p>
   * This is the <em>requested</em> precision, not the working precision. The working precision is
   * widened exactly because the forward function cancels, so the forward value carries fewer
   * accurate digits than the working precision suggests and demanding all of them back would
   * reject a perfectly good result.
   * </p>
   */
  private static Apfloat tolerance(FixedPrecisionApfloatHelper h, Apfloat p, long precision) {
    return h.abs(
        h.multiply(h.valueOf(p), h.pow(h.valueOf(new Apfloat(10)), new Apfloat(-precision))));
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
    return inverseGamma(a, p, precision, false);
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
    return inverseGamma(a, q, precision, true);
  }

  private static Apfloat inverseGamma(Apfloat a, Apfloat target, long precision, boolean upper) {
    // Both residuals subtract a multiple of Gamma(a) from an incomplete gamma value, so they
    // cancel when the requested tail probability is the large one. Inverting the other tail
    // instead is exact here, because the complement is formed at arbitrary precision.
    final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision + GUARD_DIGITS);
    final Apfloat complement = h.subtract(Apfloat.ONE, target);
    if (complement.signum() > 0 && complement.compareTo(h.valueOf(target)) < 0) {
      return inverseGamma(a, complement, precision, !upper);
    }
    ApfloatRuntimeException failure = null;
    for (int attempt = 0; attempt < EXTRA_PRECISION.length; attempt++) {
      long working = precision + EXTRA_PRECISION[attempt];
      try {
        Apfloat x = refineGamma(a, target, working, upper, attempt == 0);
        if (gammaVerified(x, a, target, working, precision, upper)) {
          return new FixedPrecisionApfloatHelper(precision).valueOf(x);
        }
      } catch (ApfloatRuntimeException are) {
        failure = are;
      }
    }
    throw failure != null ? failure
        : new LossOfPrecisionException("Complete loss of accurate digits");
  }

  private static Apfloat refineGamma(Apfloat a, Apfloat target, long working, boolean upper,
      boolean doubling) {
    final double ad = a.doubleValue();
    final double td = target.doubleValue();
    Apfloat x = seed(upper ? invRegularizedGammaQ(ad, td) : invRegularizedGammaP(ad, td));
    long prec = doubling ? SEED_PRECISION : working;
    for (int step = 0; step < MAX_NEWTON_STEPS; step++) {
      if (doubling) {
        if (prec >= working && step > 0) {
          break;
        }
        prec = Math.min(2 * prec, working);
      }
      final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(prec);
      final Apfloat ah = h.valueOf(a);
      final Apfloat one = h.valueOf(Apfloat.ONE);
      final Apfloat zero = h.valueOf(Apfloat.ZERO);
      x = h.valueOf(x);
      try {
        final Apfloat scaled = h.multiply(h.valueOf(target), h.gamma(ah));
        // upper: g(x) = Gamma(a,x) - q*Gamma(a), g'(x) = -x^(a-1)*E^(-x)
        // lower: g(x) = gamma(a,x) - p*Gamma(a), g'(x) = x^(a-1)*E^(-x)
        final Apfloat g = upper //
            ? h.subtract(h.gamma(ah, x), scaled) //
            : h.subtract(h.gamma(ah, zero, x), scaled);
        Apfloat dg = h.multiply(h.pow(x, h.subtract(ah, one)), h.exp(h.negate(x)));
        if (upper) {
          dg = h.negate(dg);
        }
        Apfloat next = h.subtract(x, h.divide(g, dg));
        x = next.signum() > 0 ? next : h.divide(x, h.valueOf(new Apfloat(2)));
      } catch (LossOfPrecisionException lope) {
        break;
      }
      if (!doubling && step >= 2) {
        break;
      }
    }
    return x;
  }

  /** Check that the regularized gamma function reproduces the target at the working precision. */
  private static boolean gammaVerified(Apfloat x, Apfloat a, Apfloat target, long working,
      long precision, boolean upper) {
    if (x.signum() <= 0) {
      return false;
    }
    final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(working);
    final Apfloat actual;
    try {
      actual = upper //
          ? h.divide(h.gamma(a, x), h.gamma(a)) //
          : h.divide(h.gamma(a, h.valueOf(Apfloat.ZERO), x), h.gamma(a));
    } catch (ApfloatRuntimeException are) {
      // the forward function is not computable at this working precision, so nothing is verified
      return false;
    }
    try {
      return h.abs(h.subtract(actual, h.valueOf(target)))
          .compareTo(tolerance(h, target, precision)) <= 0;
    } catch (LossOfPrecisionException lope) {
      // the two values agree in every digit the working precision carries
      return true;
    }
  }

  /**
   * Arbitrary precision version of {@link #invRegularizedBeta(double, double, double, double)}.
   *
   * @param z1 the lower integration limit
   * @param z2 the value of the generalized regularized beta function
   * @param a the first shape parameter; must be positive
   * @param b the second shape parameter; must be positive
   * @param precision the requested number of decimal digits
   * @return the <code>x</code> with <code>I_x(a,b) == I_z1(a,b) + z2</code>
   */
  public static Apfloat invRegularizedBeta(Apfloat z1, Apfloat z2, Apfloat a, Apfloat b,
      long precision) {
    if (z2.signum() == 0) {
      return z1;
    }
    if (z1.signum() == 0) {
      return invRegularizedBeta(z2, a, b, precision);
    }
    final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision + GUARD_DIGITS);
    if (h.valueOf(z1).compareTo(h.valueOf(Apfloat.ONE)) == 0) {
      // I_z1(a,b) is exactly 1 here, so the target is 1 + z2 with z2 <= 0. Reflecting through
      // I_x(a,b) == 1 - I_(1-x)(b,a) keeps the small quantity -z2 intact.
      Apfloat reflected = invRegularizedBeta(h.negate(z2), b, a, precision + GUARD_DIGITS);
      return new FixedPrecisionApfloatHelper(precision).subtract(Apfloat.ONE, reflected);
    }
    Apfloat lower = h.divide(h.beta(z1, a, b), h.beta(a, b));
    return invRegularizedBeta(h.add(lower, z2), a, b, precision);
  }

  /**
   * Arbitrary precision version of {@link #invRegularizedGamma(double, double, double)}.
   *
   * @param a the shape parameter; must be positive
   * @param z1 the lower integration limit
   * @param z2 the value of the generalized regularized gamma function
   * @param precision the requested number of decimal digits
   * @return the <code>x</code> with <code>Q(a,x) == Q(a,z1) - z2</code>
   */
  public static Apfloat invRegularizedGamma(Apfloat a, Apfloat z1, Apfloat z2, long precision) {
    if (z1.signum() == 0) {
      // Q(a,0) is exactly 1, so this is the lower inverse; going through the upper inverse with
      // 1 - z2 would throw away the leading digits of a small z2.
      return invRegularizedGammaP(a, z2, precision);
    }
    final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision + GUARD_DIGITS);
    Apfloat q = h.subtract(h.divide(h.gamma(a, z1), h.gamma(a)), z2);
    return invRegularizedGammaQ(a, q, precision);
  }

  /**
   * Turn the <code>double</code> result into a starting value for the refinement. A result which
   * saturated the <code>double</code> range carries no digits to refine, so the caller has to fall
   * back instead.
   */
  private static Apfloat seed(double seed) {
    if (!Double.isFinite(seed) || seed == 0.0) {
      throw new ArgumentTypeException(
          "The result is not representable as a machine number: " + seed);
    }
    return new Apfloat(seed, SEED_PRECISION);
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
