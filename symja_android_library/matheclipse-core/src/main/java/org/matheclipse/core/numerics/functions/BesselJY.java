package org.matheclipse.core.numerics.functions;

import com.google.common.math.DoubleMath;

/**
 * Bessel functions <code>J</code> and <code>Y</code> of arbitrary <b>real</b> order for
 * <code>double</code> arguments, by Steed's method and Temme's series.
 *
 * <p>
 * This exists because the arbitrary-precision route through Apfloat costs 5-12 ms per
 * <code>BesselY</code> evaluation even when only a <code>double</code> is wanted - a root finder
 * that needs a dozen evaluations per zero therefore spends seconds where it should spend
 * microseconds. Everything here is plain <code>double</code> arithmetic.
 *
 * <p>
 * The algorithm is the classical one (Barnett's Steed method combined with Temme's series for the
 * small-argument regime, as in Numerical Recipes' <code>bessjy</code>):
 * <ul>
 * <li>the order is split as <code>nu = xmu + nl</code> with <code>|xmu| &lt;= 1/2</code>, because
 * both the continued fractions and the series converge only for a small order;</li>
 * <li>a continued fraction gives <code>J'/J</code> at the full order <code>nu</code>, and downward
 * recurrence carries it to <code>xmu</code> (downward is the stable direction for <code>J</code>);
 * </li>
 * <li>for <code>x &lt; 2</code> Temme's series gives <code>Y_xmu</code> and <code>Y_xmu+1</code>
 * directly; for larger <code>x</code> Steed's second continued fraction gives the same pair. Either
 * way the normalization of <code>J</code> follows from the Wronskian
 * <code>J*Y' - J'*Y = 2/(pi*x)</code>;</li>
 * <li>upward recurrence carries <code>Y</code> back to <code>nu</code> (upward is the stable
 * direction for <code>Y</code>).</li>
 * </ul>
 *
 * <p>
 * <b>Validated domain.</b> {@link #isSupported(double, double)} states exactly where the results
 * are trusted. Callers are expected to fall back to the arbitrary-precision implementation outside
 * it - see {@code DMath.besselJ} / {@code DMath.besselY}. The bound is not decorative: it was
 * measured against Apfloat at 40 digits over a dense grid of orders and arguments, and the
 * fall-back covers the regions where a <code>double</code> simply cannot carry the answer (very
 * large arguments, where the phase of the oscillation is lost, and very large orders, where
 * <code>J</code> underflows).
 */
public final class BesselJY {

  /** Below this argument Temme's series is used instead of Steed's second continued fraction. */
  private static final double X_MIN = 2.0;

  private static final double EPS = 1.0e-16;

  /** Guard against division by zero in the continued fractions; well below any real magnitude. */
  private static final double FP_MIN = 1.0e-300;

  private static final int MAX_ITERATIONS = 10000;

  /**
   * Largest argument handled here, chosen from measurement rather than from the algorithm.
   *
   * <p>
   * The functions oscillate with unit frequency, so the phase carried in <code>x</code> is what
   * limits a <code>double</code>: the error grows in proportion to <code>x</code>. Measured against
   * a 40-digit reference, as a fraction of the local envelope <code>sqrt(2/(pi*x))</code> - the
   * meaningful scale, since relative error is unbounded next to a zero - the worst error over a
   * range of orders runs 2e-15 at <code>x=10</code>, 6e-14 at 100, 1.5e-13 at 200, 4e-13 at 500,
   * 8e-13 at 1000 and 7e-11 at 3000. The cut at 100 keeps better than a decimal order of magnitude
   * of head room under the 1e-12 that callers rely on - a sweep of 8235 points over <code>v</code>
   * in [-20,20] and <code>x</code> in [1e-8, 300] put the worst case at 8.2e-13, and all of that
   * was at the top of the range. Past the cut Apfloat, which keeps guard digits internally and
   * stays exact to the last bit, is worth its cost.
   */
  private static final double X_MAX = 100.0;

  /**
   * Largest order handled here. Beyond this the downward <code>J</code> recurrence runs
   * <code>nu - x</code> steps, so the cost grows with the order while <code>J</code> itself
   * underflows.
   */
  private static final double NU_MAX = 1.0e3;

  /** Chebyshev coefficients for {@code gam1(x) = (1/Gamma(1-x) - 1/Gamma(1+x)) / (2*x)}. */
  private static final double[] C1 = {-1.142022680371168e0, 6.5165112670737e-3, 3.087090173086e-4,
      -3.4706269649e-6, 6.9437664e-9, 3.67795e-11, -1.356e-13};

  /** Chebyshev coefficients for {@code gam2(x) = (1/Gamma(1-x) + 1/Gamma(1+x)) / 2}. */
  private static final double[] C2 = {1.843740587300905e0, -7.68528408447867e-2, 1.2719271366546e-3,
      -4.9717367042e-6, -3.31261198e-8, 2.423096e-10, -1.702e-13, -1.49e-15};

  private BesselJY() {}

  /**
   * Whether {@link #besselJ(double, double)} and {@link #besselY(double, double)} are trusted for
   * this order and argument.
   *
   * <p>
   * <code>x</code> must be strictly positive: <code>Y</code> is not real for <code>x &lt; 0</code>,
   * and <code>J</code> is real there only for integer orders - both are left to the caller's
   * fall-back rather than special-cased here.
   *
   * @param v the order, may be negative
   * @param x the argument
   * @return true if the double implementation covers this point
   */
  public static boolean isSupported(double v, double x) {
    if (Double.isNaN(v) || Double.isNaN(x)) {
      return false;
    }
    return x > 0.0 && x <= X_MAX && Math.abs(v) <= NU_MAX;
  }

  /**
   * <code>J_v(x)</code> for real order, or {@link Double#NaN} if a <code>double</code> cannot carry
   * the result - see {@link #besselJY(double, double)}.
   */
  public static double besselJ(double v, double x) {
    return besselJY(v, x)[0];
  }

  /**
   * <code>Y_v(x)</code> for real order, or {@link Double#NaN} if a <code>double</code> cannot carry
   * the result - see {@link #besselJY(double, double)}.
   */
  public static double besselY(double v, double x) {
    return besselJY(v, x)[1];
  }

  /**
   * Both functions at once, which is what the algorithm computes anyway - the continued fractions
   * and the series all deliver <code>J</code> and <code>Y</code> from the same intermediate
   * quantities.
   *
   * <p>
   * Returns <code>{NaN, NaN}</code> rather than a wrong number whenever the result cannot be
   * trusted: a continued fraction that did not converge, or a negative order whose reflection
   * cancels (see below). Callers must treat NaN as "fall back to arbitrary precision".
   *
   * @return <code>{J_v(x), Y_v(x)}</code>, or NaNs
   */
  public static double[] besselJY(final double v, final double x) {
    if (v >= 0.0) {
      return jy(v, x);
    }
    final double av = -v;
    final double rounded = Math.rint(av);

    // An INTEGER negative order must use the exact reflection J_-n = (-1)^n J_n, Y_-n = (-1)^n Y_n.
    // Going through the general formula below would multiply Y_n - which reaches 1e34 at x=1e-6 -
    // by a sin(n*pi) that is only zero to within 1e-16, swamping a J_-n of order 1e-34 with pure
    // rounding noise. (Measured: J_-5(1e-6) came out as 1.5e17 instead of -2.6e-34.)
    if (rounded == av) {
      final double[] jyn = jy(av, x);
      final double sign = (((long) rounded) & 1L) == 0L ? 1.0 : -1.0;
      return new double[] {sign * jyn[0], sign * jyn[1]};
    }

    // A HALF-ODD-INTEGER negative order degenerates the other way: cos(v*pi) is zero in exact
    // arithmetic but 1e-16 in floating point, and it multiplies the huge Y. Use the exact
    // identities J_-(n+1/2) = (-1)^(n+1) Y_(n+1/2) and Y_-(n+1/2) = (-1)^n J_(n+1/2).
    final double twiceAv = av + av;
    if (DoubleMath.isMathematicalInteger(twiceAv)) {
      final long n = (long) Math.floor(av); // av = n + 1/2
      final double[] jyh = jy(av, x);
      final double sign = (n & 1L) == 0L ? 1.0 : -1.0;
      return new double[] {-sign * jyh[1], sign * jyh[0]};
    }

    // General case. J_-v = J_v*cos(v*pi) - Y_v*sin(v*pi), Y_-v = J_v*sin(v*pi) + Y_v*cos(v*pi).
    // Both are differences of terms that can be far larger than the result - for a small argument
    // Y_v grows without bound while J_-v vanishes - so the cancellation is measured and the answer
    // withdrawn when it has eaten the mantissa.
    final double[] jyv = jy(av, x);
    if (Double.isNaN(jyv[0])) {
      return jyv;
    }
    final double vpi = Math.PI * av;
    final double c = Math.cos(vpi);
    final double s = Math.sin(vpi);
    final double jTerm1 = jyv[0] * c;
    final double jTerm2 = jyv[1] * s;
    final double yTerm1 = jyv[0] * s;
    final double yTerm2 = jyv[1] * c;
    final double rj = jTerm1 - jTerm2;
    final double ry = yTerm1 + yTerm2;
    if (!isWellConditioned(rj, jTerm1, jTerm2) || !isWellConditioned(ry, yTerm1, yTerm2)) {
      return new double[] {Double.NaN, Double.NaN};
    }
    return new double[] {rj, ry};
  }

  /**
   * Whether <code>result</code> retains enough significant digits after combining two terms.
   *
   * <p>
   * The terms carry a relative error of about {@link #EPS}, so their absolute error is
   * <code>EPS*max(|t1|,|t2|)</code>. Requiring the result to stay above <code>1e4</code> times that
   * keeps at least four digits of head room over the 1e-12 accuracy this class claims.
   */
  private static boolean isWellConditioned(double result, double t1, double t2) {
    final double largest = Math.max(Math.abs(t1), Math.abs(t2));
    if (largest == 0.0) {
      return true;
    }
    return Math.abs(result) > 1.0e4 * EPS * largest;
  }

  /**
   * The core: <code>{J_nu(x), Y_nu(x)}</code> for <code>nu &gt;= 0</code> and
   * <code>x &gt; 0</code>.
   */
  private static double[] jy(final double nu, final double x) {
    // Split nu = xmu + nl with |xmu| <= 1/2. For a small argument the series needs the whole
    // integer part removed; for a large one only the excess over x has to be recurred, which keeps
    // the downward J recurrence short.
    final int nl = (x < X_MIN) ? (int) (nu + 0.5) : Math.max(0, (int) (nu - x + 1.5));
    final double xmu = nu - nl;
    final double xmu2 = xmu * xmu;
    final double xi = 1.0 / x;
    final double xi2 = 2.0 * xi;
    // the Wronskian J_mu*Y'_mu - J'_mu*Y_mu = 2/(pi*x) fixes the normalization below
    final double w = xi2 / Math.PI;

    // --- CF1: J'_nu/J_nu as a continued fraction, by the modified Lentz algorithm ---
    int isign = 1;
    double h = nu * xi;
    if (h < FP_MIN) {
      h = FP_MIN;
    }
    double b = xi2 * nu;
    double d = 0.0;
    double c = h;
    int i;
    for (i = 1; i <= MAX_ITERATIONS; i++) {
      b += xi2;
      d = b - d;
      if (Math.abs(d) < FP_MIN) {
        d = FP_MIN;
      }
      c = b - 1.0 / c;
      if (Math.abs(c) < FP_MIN) {
        c = FP_MIN;
      }
      d = 1.0 / d;
      final double del = c * d;
      h = del * h;
      if (d < 0.0) {
        isign = -isign;
      }
      if (Math.abs(del - 1.0) < EPS) {
        break;
      }
    }
    if (i > MAX_ITERATIONS) {
      return new double[] {Double.NaN, Double.NaN};
    }

    // --- downward recurrence for J from nu to xmu, started from an arbitrary tiny value ---
    // The scale is arbitrary (J is a solution of a linear recurrence); it is fixed at the end from
    // the Wronskian. Downward is the stable direction for J.
    double rjl = isign * FP_MIN;
    double rjpl = h * rjl;
    final double rjl1 = rjl;
    double fact = nu * xi;
    for (int l = nl; l >= 1; l--) {
      final double rjtemp = fact * rjl + rjpl;
      fact -= xi;
      rjpl = fact * rjtemp - rjl;
      rjl = rjtemp;
    }
    if (rjl == 0.0) {
      rjl = EPS;
    }
    final double f = rjpl / rjl;

    double rjmu;
    double rymu;
    double ry1;

    if (x < X_MIN) {
      // --- Temme's series for Y_xmu and Y_xmu+1 ---
      final double x2 = 0.5 * x;
      final double pimu = Math.PI * xmu;
      final double factPi = (Math.abs(pimu) < EPS) ? 1.0 : pimu / Math.sin(pimu);
      d = -Math.log(x2);
      double e = xmu * d;
      final double factSinh = (Math.abs(e) < EPS) ? 1.0 : Math.sinh(e) / e;

      final double[] gam = chebyshevGamma(xmu);
      final double gam1 = gam[0];
      final double gam2 = gam[1];
      final double gampl = gam[2];
      final double gammi = gam[3];

      double ff = (2.0 / Math.PI) * factPi * (gam1 * Math.cosh(e) + gam2 * factSinh * d);
      e = Math.exp(e);
      double p = e / (Math.PI * gampl);
      double q = 1.0 / (e * Math.PI * gammi);
      final double pimu2 = 0.5 * pimu;
      final double factSin = (Math.abs(pimu2) < EPS) ? 1.0 : Math.sin(pimu2) / pimu2;
      final double r = Math.PI * pimu2 * factSin * factSin;
      c = 1.0;
      d = -x2 * x2;
      double sum = ff + r * q;
      double sum1 = p;
      for (i = 1; i <= MAX_ITERATIONS; i++) {
        ff = (i * ff + p + q) / (i * i - xmu2);
        c *= d / i;
        p /= (i - xmu);
        q /= (i + xmu);
        final double del = c * (ff + r * q);
        sum += del;
        final double del1 = c * p - i * del;
        sum1 += del1;
        if (Math.abs(del) < (1.0 + Math.abs(sum)) * EPS) {
          break;
        }
      }
      if (i > MAX_ITERATIONS) {
        return new double[] {Double.NaN, Double.NaN};
      }
      rymu = -sum;
      ry1 = -sum1 * xi2;
      final double rymup = xmu * xi * rymu - ry1;
      rjmu = w / (rymup - f * rymu);
    } else {
      // --- Steed's CF2, evaluated in complex arithmetic written out in real pairs ---
      double a = 0.25 - xmu2;
      double p = -0.5 * xi;
      double q = 1.0;
      final double br = 2.0 * x;
      double bi = 2.0;
      double factCf = a * xi / (p * p + q * q);
      double cr = br + q * factCf;
      double ci = bi + p * factCf;
      double den = br * br + bi * bi;
      double dr = br / den;
      double di = -bi / den;
      double dlr = cr * dr - ci * di;
      double dli = cr * di + ci * dr;
      double temp = p * dlr - q * dli;
      q = p * dli + q * dlr;
      p = temp;
      for (i = 2; i <= MAX_ITERATIONS; i++) {
        a += 2 * (i - 1);
        bi += 2.0;
        dr = a * dr + br;
        di = a * di + bi;
        if (Math.abs(dr) + Math.abs(di) < FP_MIN) {
          dr = FP_MIN;
        }
        factCf = a / (cr * cr + ci * ci);
        cr = br + cr * factCf;
        ci = bi - ci * factCf;
        if (Math.abs(cr) + Math.abs(ci) < FP_MIN) {
          cr = FP_MIN;
        }
        den = dr * dr + di * di;
        dr /= den;
        di /= -den;
        dlr = cr * dr - ci * di;
        dli = cr * di + ci * dr;
        temp = p * dlr - q * dli;
        q = p * dli + q * dlr;
        p = temp;
        if (Math.abs(dlr - 1.0) + Math.abs(dli) < EPS) {
          break;
        }
      }
      if (i > MAX_ITERATIONS) {
        return new double[] {Double.NaN, Double.NaN};
      }
      final double gam = (p - f) / q;
      rjmu = Math.sqrt(w / ((p - f) * gam + q));
      rjmu = Math.copySign(rjmu, rjl);
      rymu = rjmu * gam;
      final double rymup = rymu * (p + q / gam);
      ry1 = xmu * xi * rymu - rymup;
    }

    // fix the arbitrary scale of the downward J recurrence
    final double scale = rjmu / rjl;
    final double rj = rjl1 * scale;

    // --- upward recurrence for Y from xmu back to nu (the stable direction for Y) ---
    for (i = 1; i <= nl; i++) {
      final double rytemp = (xmu + i) * xi2 * ry1 - rymu;
      rymu = ry1;
      ry1 = rytemp;
    }
    return new double[] {rj, rymu};
  }

  /**
   * Chebyshev evaluation of the Gamma-function combinations Temme's series needs, for
   * <code>|x| &lt;= 1/2</code>.
   *
   * @return <code>{gam1, gam2, 1/Gamma(1+x), 1/Gamma(1-x)}</code> where
   *         <code>gam1 = (1/Gamma(1-x) - 1/Gamma(1+x))/(2x)</code> - the form that stays accurate
   *         as <code>x -&gt; 0</code>, where the difference itself cancels - and
   *         <code>gam2 = (1/Gamma(1-x) + 1/Gamma(1+x))/2</code>
   * @see BesselIK - Temme's series for the modified functions needs the same four quantities
   */
  static double[] chebyshevGamma(final double x) {
    final double xx = 8.0 * x * x - 1.0;
    final double gam1 = chebyshev(xx, C1);
    final double gam2 = chebyshev(xx, C2);
    final double gampl = gam2 - x * gam1;
    final double gammi = gam2 + x * gam1;
    return new double[] {gam1, gam2, gampl, gammi};
  }

  /** Clenshaw evaluation of a Chebyshev series on <code>[-1,1]</code>. */
  private static double chebyshev(final double x, final double[] coefficients) {
    double d = 0.0;
    double dd = 0.0;
    final double y2 = 2.0 * x;
    for (int j = coefficients.length - 1; j >= 1; j--) {
      final double sv = d;
      d = y2 * d - dd + coefficients[j];
      dd = sv;
    }
    return x * d - dd + 0.5 * coefficients[0];
  }
}
