package org.matheclipse.core.numerics.functions;

/**
 * The modified Bessel functions <code>I</code> and <code>K</code> of arbitrary <b>real</b> order for
 * <code>double</code> arguments - the companion of {@link BesselJY}, by the same Temme/Steed
 * approach adapted to the modified equation.
 *
 * <p>
 * The Apfloat route costs about 19 ms for <code>K</code> and 2 ms for <code>I</code>. The
 * <code>besselK</code> in {@link BesselJS} is not an alternative: measured against Apfloat at 40
 * digits it is wrong by 8e-7, far outside the 1e-12 callers rely on, so it must not be wired in as
 * it stands.
 *
 * <p>
 * The structure mirrors {@link BesselJY}:
 * <ul>
 * <li>the order splits as <code>nu = xmu + nl</code> with <code>|xmu| &lt;= 1/2</code>;</li>
 * <li>a continued fraction gives <code>I'/I</code> at the full order, and downward recurrence
 * carries it to <code>xmu</code> - downward is the stable direction for <code>I</code>;</li>
 * <li>for <code>x &lt; 2</code> Temme's series gives <code>K_xmu</code> and <code>K_xmu+1</code>;
 * for larger <code>x</code> Steed's second continued fraction does. The Wronskian
 * <code>I*K' - I'*K = -1/x</code> then normalizes <code>I</code>;</li>
 * <li>upward recurrence carries <code>K</code> back to <code>nu</code> - upward is the stable
 * direction for <code>K</code>, which grows in that direction.</li>
 * </ul>
 *
 * <p>
 * Unlike <code>J</code> and <code>Y</code> these do not oscillate, so nothing is lost to phase.
 * What bounds the domain instead is plain overflow: <code>I</code> grows like
 * <code>e^x/sqrt(x)</code> and leaves the double range just past <code>x = 700</code>, while
 * <code>K</code> decays like <code>e^-x</code> and underflows there.
 */
public final class BesselIK {

  private static final double X_MIN = 2.0;

  /** Below this argument I is taken from its power series rather than from the Wronskian. */
  private static final double SERIES_X = 0.1;

  private static final double EPS = 1.0e-16;

  private static final double FP_MIN = 1.0e-300;

  private static final int MAX_ITERATIONS = 10000;

  /**
   * Largest argument handled here. <code>I</code> overflows a double at about 713 and
   * <code>K</code> underflows at about 745, so past this the answer is not representable whatever
   * the algorithm does.
   */
  private static final double X_MAX = 700.0;

  /** Largest order handled here; beyond it the downward recurrence dominates the cost. */
  private static final double NU_MAX = 1.0e3;

  private BesselIK() {}

  /**
   * Whether {@link #besselI(double, double)} and {@link #besselK(double, double)} are trusted here.
   * Both need <code>x &gt; 0</code>: <code>K</code> is not real for a negative argument and
   * <code>I</code> is real there only at integer orders.
   */
  public static boolean isSupported(double v, double x) {
    if (Double.isNaN(v) || Double.isNaN(x)) {
      return false;
    }
    return x > 0.0 && x <= X_MAX && Math.abs(v) <= NU_MAX;
  }

  /**
   * <code>I_v(x)</code>, or NaN when a double cannot carry the result - see
   * {@link #besselIK(double, double)}.
   */
  public static double besselI(double v, double x) {
    return besselIK(v, x)[0];
  }

  /**
   * <code>K_v(x)</code>, or NaN when a double cannot carry the result. <code>K</code> is even in the
   * order, so the sign of <code>v</code> never matters.
   */
  public static double besselK(double v, double x) {
    return besselIK(v, x)[1];
  }

  /**
   * Both at once, which is what the algorithm computes anyway.
   *
   * @return <code>{I_v(x), K_v(x)}</code>, or <code>{NaN, NaN}</code> when the iteration failed or
   *         the negative-order reflection lost its significant digits
   */
  public static double[] besselIK(final double v, final double x) {
    if (v >= 0.0) {
      return ik(v, x);
    }
    final double av = -v;
    final double[] base = ik(av, x);
    if (Double.isNaN(base[0])) {
      return base;
    }
    // K is even in the order: K_-v = K_v, exactly.
    final double rk = base[1];
    // I_-v = I_v + (2/Pi)*sin(v*Pi)*K_v. At an integer order the sine vanishes and I is even,
    // which has to be taken exactly - K_v can be enormous next to I_v for a small argument, so
    // a sine that is merely 1e-16 rather than 0 would swamp the answer.
    if (Math.rint(av) == av) {
      return new double[] {base[0], rk};
    }
    final double correction = (2.0 / Math.PI) * Math.sin(Math.PI * av) * rk;
    final double ri = base[0] + correction;
    // withdraw the point if the sum cancelled away the mantissa
    final double largest = Math.max(Math.abs(base[0]), Math.abs(correction));
    if (largest > 0.0 && Math.abs(ri) <= 1.0e4 * EPS * largest) {
      return new double[] {Double.NaN, rk};
    }
    return new double[] {ri, rk};
  }

  /**
   * <code>I_nu(x) = sum_k (x/2)^(2k+nu) / (k! * Gamma(nu+k+1))</code>, evaluated directly.
   *
   * <p>
   * Only used for a small argument, where each term is a tiny fraction of the one before it and the
   * whole sum is a couple of terms long - so the leading factor carries the accuracy and the series
   * adds essentially nothing to the error.
   *
   * @return NaN if the leading factor is not representable
   */
  private static double besselISeries(final double nu, final double x) {
    final double half = 0.5 * x;
    final double lead = Math.pow(half, nu) / org.hipparchus.special.Gamma.gamma(nu + 1.0);
    if (!Double.isFinite(lead)) {
      return Double.NaN;
    }
    final double quarter = half * half;
    double term = 1.0;
    double sum = 1.0;
    for (int k = 1; k <= 60; k++) {
      term *= quarter / (k * (nu + k));
      sum += term;
      if (Math.abs(term) < EPS * Math.abs(sum)) {
        break;
      }
    }
    return lead * sum;
  }

  /** The core: <code>{I_nu(x), K_nu(x)}</code> for <code>nu &gt;= 0</code>, <code>x &gt; 0</code>. */
  private static double[] ik(final double nu, final double x) {
    final int nl = (int) (nu + 0.5);
    final double xmu = nu - nl;
    final double xmu2 = xmu * xmu;
    final double xi = 1.0 / x;
    final double xi2 = 2.0 * xi;

    // --- CF1: I'_nu/I_nu ---
    double h = nu * xi;
    if (h < FP_MIN) {
      h = FP_MIN;
    }
    double b = xi2 * nu;
    double d = 0.0;
    double c = h;
    int i;
    for (i = 0; i < MAX_ITERATIONS; i++) {
      b += xi2;
      d = 1.0 / (b + d);
      c = b + 1.0 / c;
      final double del = c * d;
      h = del * h;
      if (Math.abs(del - 1.0) <= EPS) {
        break;
      }
    }
    if (i >= MAX_ITERATIONS) {
      return new double[] {Double.NaN, Double.NaN};
    }

    // --- downward recurrence for I from nu to xmu, on an arbitrary scale ---
    double ril = FP_MIN;
    double ripl = h * ril;
    final double ril1 = ril;
    double fact = nu * xi;
    for (int l = nl - 1; l >= 0; l--) {
      final double ritemp = fact * ril + ripl;
      fact -= xi;
      ripl = fact * ritemp + ril;
      ril = ritemp;
    }
    final double f = ripl / ril;

    double rkmu;
    double rk1;

    if (x < X_MIN) {
      // --- Temme's series for K_xmu and K_xmu+1 ---
      final double x2 = 0.5 * x;
      final double pimu = Math.PI * xmu;
      final double factPi = (Math.abs(pimu) < EPS) ? 1.0 : pimu / Math.sin(pimu);
      d = -Math.log(x2);
      double e = xmu * d;
      final double factSinh = (Math.abs(e) < EPS) ? 1.0 : Math.sinh(e) / e;

      final double[] gam = BesselJY.chebyshevGamma(xmu);
      final double gam1 = gam[0];
      final double gam2 = gam[1];
      final double gampl = gam[2];
      final double gammi = gam[3];

      double ff = factPi * (gam1 * Math.cosh(e) + gam2 * factSinh * d);
      double sum = ff;
      e = Math.exp(e);
      double p = 0.5 * e / gampl;
      double q = 0.5 / (e * gammi);
      c = 1.0;
      d = x2 * x2;
      double sum1 = p;
      for (i = 1; i <= MAX_ITERATIONS; i++) {
        ff = (i * ff + p + q) / (i * i - xmu2);
        c *= d / i;
        p /= (i - xmu);
        q /= (i + xmu);
        final double del = c * ff;
        sum += del;
        final double del1 = c * (p - i * ff);
        sum1 += del1;
        if (Math.abs(del) < Math.abs(sum) * EPS) {
          break;
        }
      }
      if (i > MAX_ITERATIONS) {
        return new double[] {Double.NaN, Double.NaN};
      }
      rkmu = sum;
      rk1 = sum1 * xi2;
    } else {
      // --- Steed's CF2 for the modified equation ---
      b = 2.0 * (1.0 + x);
      d = 1.0 / b;
      double delh = d;
      h = delh;
      double q1 = 0.0;
      double q2 = 1.0;
      final double a1 = 0.25 - xmu2;
      double q = a1;
      c = a1;
      double a = -a1;
      double s = 1.0 + q * delh;
      for (i = 1; i < MAX_ITERATIONS; i++) {
        a -= 2 * i;
        c = -a * c / (i + 1.0);
        final double qnew = (q1 - b * q2) / a;
        q1 = q2;
        q2 = qnew;
        q += c * qnew;
        b += 2.0;
        d = 1.0 / (b + a * d);
        delh = (b * d - 1.0) * delh;
        h += delh;
        final double dels = q * delh;
        s += dels;
        if (Math.abs(dels / s) <= EPS) {
          break;
        }
      }
      if (i >= MAX_ITERATIONS) {
        return new double[] {Double.NaN, Double.NaN};
      }
      h = a1 * h;
      rkmu = Math.sqrt(Math.PI / (2.0 * x)) * Math.exp(-x) / s;
      rk1 = rkmu * (xmu + x + 0.5 - h) * xi;
    }

    // normalize I from the Wronskian, then recur K upward to nu
    final double rkmup = xmu * xi * rkmu - rk1;
    final double rimu = xi / (f * rkmu - rkmup);
    double ri = (rimu * ril1) / ril;
    // For a tiny argument that normalization is the weak point: K_nu blows up like x^-nu while
    // I_nu vanishes like x^nu, so recovering I from a Wronskian involving K throws away most of
    // the mantissa (measured 1.1e-10 at nu=3.5, x=1e-6). The defining power series has no such
    // problem there and converges in a handful of terms, so use it directly.
    if (x < SERIES_X) {
      final double series = besselISeries(nu, x);
      if (!Double.isNaN(series)) {
        ri = series;
      }
    }
    for (i = 1; i <= nl; i++) {
      final double rktemp = (xmu + i) * xi2 * rk1 + rkmu;
      rkmu = rk1;
      rk1 = rktemp;
    }
    return new double[] {ri, rkmu};
  }
}
