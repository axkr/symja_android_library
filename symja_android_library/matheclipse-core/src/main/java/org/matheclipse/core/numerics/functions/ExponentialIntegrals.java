package org.matheclipse.core.numerics.functions;

import org.hipparchus.complex.Complex;

/**
 * The exponential and trigonometric integrals for <code>double</code> arguments:
 * <code>SinIntegral</code>, <code>CosIntegral</code>, <code>SinhIntegral</code>,
 * <code>CoshIntegral</code>, <code>ExpIntegralEi</code> and <code>ExpIntegralE1</code>.
 *
 * <p>
 * These are the slowest functions in the whole Apfloat-backed set - measured at 17 digits,
 * <code>cosIntegral</code> and <code>sinIntegral</code> cost about 77 ms per call,
 * <code>coshIntegral</code> and <code>sinhIntegral</code> about 71 ms, and
 * <code>expIntegralEi</code> about 36 ms - so a single plot or table of any of them takes minutes.
 * Everything here is plain <code>double</code> arithmetic and runs in well under a microsecond.
 *
 * <p>
 * Two regimes, switching at <code>|x| = 2</code> where the two happen to meet in accuracy:
 * <ul>
 * <li><b>small argument</b>: the defining power series, which converge quickly and lose nothing to
 * cancellation while the terms stay ordered;</li>
 * <li><b>large argument</b>: a continued fraction for the complex exponential integral
 * <code>E1(i*x)</code> evaluated by the modified Lentz algorithm, from which <code>Si</code> and
 * <code>Ci</code> follow as the imaginary and real parts. For <code>Ei</code> the real asymptotic
 * series is used instead, truncated at its smallest term - it is divergent, so carrying it past
 * that point makes the answer worse rather than better.</li>
 * </ul>
 *
 * <p>
 * <b>Validated domain.</b> {@link #isSupported(double)} states where the results are trusted, and
 * callers fall back to arbitrary precision elsewhere. The bound was measured against Apfloat at 40
 * digits, not assumed.
 */
public final class ExponentialIntegrals {

  private static final double EULER_GAMMA = 0.5772156649015328606;

  private static final double HALF_PI = 1.5707963267948966;

  private static final double EPS = 1.0e-16;

  /** Below this the series is used, above it the continued fraction / asymptotic series. */
  private static final double SERIES_LIMIT = 2.0;

  private static final int MAX_ITERATIONS = 200;

  /** Guards the Lentz recurrences against an exact zero denominator. */
  private static final double FP_MIN = 1.0e-300;

  /**
   * Largest magnitude handled here.
   *
   * <p>
   * <code>Si</code> and <code>Ci</code> oscillate about their limits with an amplitude that decays
   * like <code>1/x</code>, so what limits a <code>double</code> is the phase held in
   * <code>x</code>, exactly as for the Bessel functions. <code>Shi</code> and <code>Chi</code> grow
   * like <code>e^x/x</code> and overflow a <code>double</code> near 710, which is the binding
   * constraint for those two. The cut is placed below both.
   */
  private static final double X_MAX = 500.0;

  private ExponentialIntegrals() {}

  /**
   * Whether the functions here are trusted for this argument. Callers must fall back to arbitrary
   * precision when this is false.
   */
  public static boolean isSupported(double x) {
    return !Double.isNaN(x) && Math.abs(x) <= X_MAX;
  }

  /**
   * <code>{Si(x), Ci(x)}</code> for an argument beyond the validated domain, from the auxiliary
   * functions <code>f(x) = 1/x - 2!/x^3 + 4!/x^5 - ...</code> and
   * <code>g(x) = 1/x^2 - 3!/x^4 + 5!/x^6 - ...</code>:
   * <code>Si(x) = Pi/2 - f(x)*Cos(x) - g(x)*Sin(x)</code> and
   * <code>Ci(x) = f(x)*Sin(x) - g(x)*Cos(x)</code>. The sine and the cosine are taken of the
   * argument itself, which {@link Math} reduces with as many digits of <code>Pi</code> as its size
   * needs: <code>Ci(x)</code> is of the size <code>1/x</code> and has no digit without them.
   *
   * @param x the argument, above the largest one of {@link #isSupported(double)}
   * @return <code>{Si(x), Ci(x)}</code>
   * @see <a href="https://dlmf.nist.gov/6.12#ii">DLMF 6.12(ii)</a>
   */
  public static double[] sinCosIntegralOfLarge(final double x) {
    final double w = 1.0 / x / x;
    double f = 1.0;
    double g = 1.0;
    double termF = 1.0;
    double termG = 1.0;
    for (int k = 1; k < 20; k++) {
      // (2k)!/x^(2k) and (2k+1)!/x^(2k)
      termF *= -(2.0 * k - 1.0) * (2.0 * k) * w;
      termG *= -(2.0 * k) * (2.0 * k + 1.0) * w;
      f += termF;
      g += termG;
      if (Math.abs(termG) < 1.0e-17) {
        break;
      }
    }
    f /= x;
    g *= w;
    final double sin = Math.sin(x);
    final double cos = Math.cos(x);
    return new double[] {Math.PI / 2.0 - f * cos - g * sin, f * sin - g * cos};
  }

  /**
   * <code>SinIntegral(x)</code>, the odd entire function
   * <code>Si(x) = integral from 0 to x of sin(t)/t dt</code>.
   */
  public static double sinIntegral(double x) {
    return sinCosIntegral(x)[0];
  }

  /**
   * <code>CosIntegral(x)</code>, <code>Ci(x) = EulerGamma + log(x) + integral from 0 to x of
   * (cos(t)-1)/t dt</code>. Real only for <code>x &gt; 0</code>; returns NaN otherwise, since
   * <code>Ci</code> of a negative argument carries an imaginary <code>Pi</code>.
   */
  public static double cosIntegral(double x) {
    if (x < 0.0) {
      return Double.NaN;
    }
    return sinCosIntegral(x)[1];
  }

  /**
   * Both at once - the large-argument branch produces them from the same continued fraction, so
   * asking for them together costs no more than asking for one.
   *
   * @return <code>{Si(x), Ci(x)}</code>; <code>Ci</code> is NaN for a negative argument
   */
  public static double[] sinCosIntegral(final double x) {
    final double t = Math.abs(x);
    if (t == 0.0) {
      return new double[] {0.0, Double.NEGATIVE_INFINITY};
    }

    double si;
    double ci;
    if (t > SERIES_LIMIT) {
      // Continued fraction for E1(i*t), by the modified Lentz algorithm in complex arithmetic
      // written out as real pairs. Si and Ci are then its imaginary and real parts.
      double br = 1.0;
      double bi = t;
      double cr = 1.0 / FP_MIN;
      double cci = 0.0;
      double den = br * br + bi * bi;
      double dr = br / den;
      double di = -bi / den;
      double hr = dr;
      double hi = di;
      int i;
      for (i = 2; i <= MAX_ITERATIONS; i++) {
        final double a = -(double) (i - 1) * (i - 1);
        br += 2.0;
        // d = 1/(a*d + b)
        double tr = a * dr + br;
        double ti = a * di + bi;
        den = tr * tr + ti * ti;
        if (den < FP_MIN) {
          den = FP_MIN;
        }
        dr = tr / den;
        di = -ti / den;
        // c = b + a/c
        den = cr * cr + cci * cci;
        if (den < FP_MIN) {
          den = FP_MIN;
        }
        cr = br + a * cr / den;
        cci = bi + a * (-cci) / den;
        // del = c*d; h *= del
        final double delr = cr * dr - cci * di;
        final double deli = cr * di + cci * dr;
        tr = hr * delr - hi * deli;
        hi = hr * deli + hi * delr;
        hr = tr;
        if (Math.abs(delr - 1.0) + Math.abs(deli) < EPS) {
          break;
        }
      }
      if (i > MAX_ITERATIONS) {
        return new double[] {Double.NaN, Double.NaN};
      }
      // h *= cos(t) - i*sin(t)
      final double cosT = Math.cos(t);
      final double sinT = Math.sin(t);
      final double hr2 = hr * cosT + hi * sinT;
      final double hi2 = hi * cosT - hr * sinT;
      ci = -hr2;
      si = HALF_PI + hi2;
    } else {
      // Si(x) = sum (-1)^k x^(2k+1) / ((2k+1)(2k+1)!)
      // Ci(x) = EulerGamma + log x + sum (-1)^k x^(2k) / ((2k)(2k)!)
      // Both series are generated by one loop over k, alternating which sum a term belongs to.
      double sum = 0.0;
      double sums = 0.0;
      double sumc = 0.0;
      double sign = 1.0;
      double fact = 1.0;
      boolean odd = true;
      int k;
      for (k = 1; k <= MAX_ITERATIONS; k++) {
        fact *= t / k;
        final double term = fact / k;
        sum += sign * term;
        final double err = term / Math.abs(sum);
        if (odd) {
          sign = -sign;
          sums = sum;
          sum = sumc;
        } else {
          sumc = sum;
          sum = sums;
        }
        if (err < EPS) {
          break;
        }
        odd = !odd;
      }
      if (k > MAX_ITERATIONS) {
        return new double[] {Double.NaN, Double.NaN};
      }
      si = sums;
      ci = sumc + Math.log(t) + EULER_GAMMA;
    }
    if (x < 0.0) {
      // Si is odd; Ci of a negative argument is not real
      return new double[] {-si, Double.NaN};
    }
    return new double[] {si, ci};
  }

  /**
   * <code>ExpIntegralEi(x)</code>, the Cauchy principal value of
   * <code>integral from -Infinity to x of e^t/t dt</code>. Real only for <code>x != 0</code>.
   */
  public static double expIntegralEi(final double x) {
    if (x == 0.0) {
      return Double.NEGATIVE_INFINITY;
    }
    if (x < 0.0) {
      // Ei(-y) = -E1(y)
      return -expIntegralE1(-x);
    }
    if (x < 1.0e-300) {
      return Math.log(x) + EULER_GAMMA;
    }
    if (x <= -Math.log(EPS)) {
      // convergent series, good while the terms stay smaller than the sum
      double sum = 0.0;
      double fact = 1.0;
      for (int k = 1; k <= MAX_ITERATIONS; k++) {
        fact *= x / k;
        final double term = fact / k;
        sum += term;
        if (term < EPS * sum) {
          return sum + Math.log(x) + EULER_GAMMA;
        }
      }
      return Double.NaN;
    }
    // Divergent asymptotic series: truncate at the smallest term, since summing past it moves the
    // answer away from the true value rather than towards it.
    double sum = 0.0;
    double term = 1.0;
    for (int k = 1; k <= MAX_ITERATIONS; k++) {
      final double previous = term;
      term *= k / x;
      if (term < EPS) {
        break;
      }
      if (term < previous) {
        sum += term;
      } else {
        sum -= previous; // past the smallest term - back the last one out and stop
        break;
      }
    }
    return Math.exp(x) * (1.0 + sum) / x;
  }

  /**
   * <code>E1(x) = integral from x to Infinity of e^-t/t dt</code> for <code>x &gt; 0</code>, which
   * is <code>ExpIntegralE(1, x)</code>. Used here to build {@link #expIntegralEi(double)} and the
   * hyperbolic integrals on the negative side.
   */
  public static double expIntegralE1(final double x) {
    if (x <= 0.0) {
      return Double.NaN;
    }
    if (x > 1.0) {
      // continued fraction (Lentz)
      double b = x + 1.0;
      double c = 1.0 / FP_MIN;
      double d = 1.0 / b;
      double h = d;
      for (int i = 1; i <= MAX_ITERATIONS; i++) {
        final double a = -(double) i * i;
        b += 2.0;
        d = 1.0 / (a * d + b);
        c = b + a / c;
        final double del = c * d;
        h *= del;
        if (Math.abs(del - 1.0) < EPS) {
          return h * Math.exp(-x);
        }
      }
      return Double.NaN;
    }
    // series: E1(x) = -EulerGamma - log(x) + sum_{k>=1} (-1)^(k+1) x^k / (k*k!)
    double sum = -EULER_GAMMA - Math.log(x);
    double fact = 1.0;
    for (int i = 1; i <= MAX_ITERATIONS; i++) {
      fact *= -x / i;
      final double del = -fact / i;
      sum += del;
      if (Math.abs(del) < Math.abs(sum) * EPS) {
        return sum;
      }
    }
    return Double.NaN;
  }

  /**
   * <code>ExpIntegralE(n, x) = integral from 1 to Infinity of e^(-x*t)/t^n dt</code> for an integer
   * <code>n &gt;= 1</code> and <code>x &gt; 0</code>: a continued fraction (Lentz) for
   * <code>x &gt; 1</code>, the power series below, whose term <code>k = n-1</code> carries
   * <code>-Log(x) + PolyGamma(n)</code>.
   *
   * @return <code>NaN</code> if the iteration does not converge
   */
  public static double expIntegralE(final int n, final double x) {
    if (n < 1 || !(x > 0.0)) {
      return Double.NaN;
    }
    if (n == 1) {
      return expIntegralE1(x);
    }
    final int nm1 = n - 1;
    if (x > 1.0) {
      double b = x + n;
      double c = 1.0 / FP_MIN;
      double d = 1.0 / b;
      double h = d;
      for (int i = 1; i <= MAX_ITERATIONS; i++) {
        final double a = -(double) i * (nm1 + i);
        b += 2.0;
        d = 1.0 / (a * d + b);
        c = b + a / c;
        final double del = c * d;
        h *= del;
        if (Math.abs(del - 1.0) < EPS) {
          return h * Math.exp(-x);
        }
      }
      return Double.NaN;
    }
    double sum = 1.0 / nm1;
    double fact = 1.0;
    for (int i = 1; i <= MAX_ITERATIONS; i++) {
      fact *= -x / i;
      double del;
      if (i != nm1) {
        del = -fact / (i - nm1);
      } else {
        double psi = -EULER_GAMMA;
        for (int k = 1; k <= nm1; k++) {
          psi += 1.0 / k;
        }
        del = fact * (-Math.log(x) + psi);
      }
      sum += del;
      if (Math.abs(del) < Math.abs(sum) * EPS) {
        return sum;
      }
    }
    return Double.NaN;
  }

  /**
   * <code>SinhIntegral(x) = integral from 0 to x of sinh(t)/t dt</code>, an odd entire function.
   */
  public static double sinhIntegral(double x) {
    return sinhCoshIntegral(x)[0];
  }

  /**
   * <code>CoshIntegral(x) = EulerGamma + log(x) + integral from 0 to x of (cosh(t)-1)/t dt</code>.
   * Real only for <code>x &gt; 0</code>.
   */
  public static double coshIntegral(double x) {
    if (x < 0.0) {
      return Double.NaN;
    }
    return sinhCoshIntegral(x)[1];
  }

  /**
   * Both hyperbolic integrals at once.
   *
   * <p>
   * For a large argument they follow from <code>Chi + Shi = Ei</code> and
   * <code>Chi - Shi = -E1</code>. For a small one that identity is useless - both sides tend to the
   * same logarithmic singularity and the difference loses every significant digit - so the defining
   * series are summed directly, which is also where they converge fastest.
   *
   * @return <code>{Shi(x), Chi(x)}</code>; <code>Chi</code> is NaN for a negative argument
   */
  public static double[] sinhCoshIntegral(final double x) {
    final double t = Math.abs(x);
    if (t == 0.0) {
      return new double[] {0.0, Double.NEGATIVE_INFINITY};
    }
    double shi;
    double chi;
    if (t <= SERIES_LIMIT) {
      // Shi(x) = sum x^(2k+1)/((2k+1)(2k+1)!), Chi(x) = EulerGamma + log x + sum x^(2k)/((2k)(2k)!)
      double sumShi = 0.0;
      double sumChi = 0.0;
      double fact = 1.0;
      int k;
      for (k = 1; k <= MAX_ITERATIONS; k++) {
        fact *= t / k;
        final double term = fact / k;
        if ((k & 1) == 1) {
          sumShi += term;
        } else {
          sumChi += term;
        }
        if (term < EPS * (Math.abs(sumShi) + Math.abs(sumChi))) {
          break;
        }
      }
      if (k > MAX_ITERATIONS) {
        return new double[] {Double.NaN, Double.NaN};
      }
      shi = sumShi;
      chi = sumChi + Math.log(t) + EULER_GAMMA;
    } else {
      final double ei = expIntegralEi(t);
      final double e1 = expIntegralE1(t);
      if (Double.isNaN(ei) || Double.isNaN(e1)) {
        return new double[] {Double.NaN, Double.NaN};
      }
      shi = 0.5 * (ei + e1);
      chi = 0.5 * (ei - e1);
    }
    if (x < 0.0) {
      return new double[] {-shi, Double.NaN};
    }
    return new double[] {shi, chi};
  }

  // ----------------------------------------------------------------------------------------
  // Complex arguments
  // ----------------------------------------------------------------------------------------

  /**
   * Largest <code>|z|</code> at which the complex series is used.
   *
   * <p>
   * The defining series converge for every <code>z</code>, but their terms grow before they decay
   * and the intermediate magnitudes have to be paid for in cancellation. Measured over 16
   * directions, the worst ratio of largest term to final sum runs 1.5 at <code>|z| = 3</code>, 46
   * at 8, 548 at 12, 7.4e3 at 15 and 6.5e5 at 20 - so 12 costs under three digits while 20 costs
   * six. Past the cut the caller falls back to arbitrary precision.
   */
  private static final double COMPLEX_SERIES_RADIUS = 12.0;

  /** Whether the complex methods here are trusted for this argument. */
  public static boolean isSupported(Complex z) {
    if (z == null || Double.isNaN(z.getReal()) || Double.isNaN(z.getImaginary())) {
      return false;
    }
    double norm = z.norm();
    return norm > 0.0 && norm <= COMPLEX_SERIES_RADIUS;
  }

  /**
   * <code>{Si(z), Ci(z)}</code> for complex <code>z</code>, or null outside the validated radius.
   *
   * <p>
   * <code>Si</code> is entire. <code>Ci</code> carries the <code>EulerGamma + log(z)</code> that
   * gives it its branch cut along the negative real axis, and taking that logarithm as the
   * PRINCIPAL one reproduces the convention Symja uses - at <code>z = -1</code> it yields
   * <code>Ci</code> with an imaginary part of <code>Pi</code>, matching the reference values,
   * without any explicit branch handling.
   */
  public static Complex[] sinCosIntegral(final Complex z) {
    if (!isSupported(z)) {
      return null;
    }
    // Si = sum (-1)^k z^(2k+1)/((2k+1)(2k+1)!), Ci = gamma + log z + sum (-1)^k z^(2k)/((2k)(2k)!)
    final Complex minusZSquared = z.multiply(z).negate();
    Complex termOdd = z; // z^1/1!
    Complex termEven = Complex.ONE; // z^0/0!
    Complex si = z;
    Complex ci = Complex.ZERO;
    double largest = z.norm();
    int k;
    for (k = 1; k <= MAX_ITERATIONS; k++) {
      // advance the even term to z^(2k)/(2k)! and the odd one to z^(2k+1)/(2k+1)!
      termEven = termEven.multiply(minusZSquared).divide((2.0 * k - 1.0) * (2.0 * k));
      final Complex ciTerm = termEven.divide(2.0 * k);
      ci = ci.add(ciTerm);
      termOdd = termOdd.multiply(minusZSquared).divide((2.0 * k) * (2.0 * k + 1.0));
      final Complex siTerm = termOdd.divide(2.0 * k + 1.0);
      si = si.add(siTerm);
      largest = Math.max(largest, Math.max(ciTerm.norm(), siTerm.norm()));
      if (ciTerm.norm() + siTerm.norm() < EPS * (ci.norm() + si.norm())) {
        break;
      }
    }
    if (k > MAX_ITERATIONS) {
      return null;
    }
    ci = ci.add(logPlusGamma(z));
    return wellConditioned(new Complex[] {si, ci}, largest);
  }

  /**
   * <code>{Shi(z), Chi(z)}</code> for complex <code>z</code>, or null outside the validated radius.
   * Same structure as {@link #sinCosIntegral(Complex)} without the alternating sign.
   */
  public static Complex[] sinhCoshIntegral(final Complex z) {
    if (!isSupported(z)) {
      return null;
    }
    final Complex zSquared = z.multiply(z);
    Complex termOdd = z;
    Complex termEven = Complex.ONE;
    Complex shi = z;
    Complex chi = Complex.ZERO;
    double largest = z.norm();
    int k;
    for (k = 1; k <= MAX_ITERATIONS; k++) {
      termEven = termEven.multiply(zSquared).divide((2.0 * k - 1.0) * (2.0 * k));
      final Complex chiTerm = termEven.divide(2.0 * k);
      chi = chi.add(chiTerm);
      termOdd = termOdd.multiply(zSquared).divide((2.0 * k) * (2.0 * k + 1.0));
      final Complex shiTerm = termOdd.divide(2.0 * k + 1.0);
      shi = shi.add(shiTerm);
      largest = Math.max(largest, Math.max(chiTerm.norm(), shiTerm.norm()));
      if (chiTerm.norm() + shiTerm.norm() < EPS * (chi.norm() + shi.norm())) {
        break;
      }
    }
    if (k > MAX_ITERATIONS) {
      return null;
    }
    chi = chi.add(logPlusGamma(z));
    return wellConditioned(new Complex[] {shi, chi}, largest);
  }

  /**
   * <code>Ei(z)</code> for complex <code>z</code>, or null outside the validated radius.
   *
   * <p>
   * <code>Ei(z) = EulerGamma + log(z) + sum z^k/(k k!)</code>. As for <code>Ci</code>, the
   * principal logarithm is what produces the branch: at <code>z = -1</code> this returns
   * <code>-0.2193839344 + Pi i</code>, where the real-argument {@link #expIntegralEi(double)} above
   * returns the principal value <code>-0.2193839344</code> without it. The two conventions are both
   * standard and both correct for their own domain; they must simply not be mixed.
   */
  public static Complex expIntegralEi(final Complex z) {
    Complex series = expIntegralEiSeries(z);
    return series != null ? series : expIntegralEiFraction(z);
  }

  /**
   * <code>Ei(z)</code> away from the origin and from the positive real axis, where the power
   * series loses its digits to cancellation, or null where this does not converge.
   *
   * <p>
   * <code>Ei(z) = -E1(-z) + (Log(z) - Log(1/z))/2 - Log(-z)</code>, with the continued fraction
   * <code>E1(w) = Exp(-w)/(w + 1/(1 + 1/(w + 2/(1 + 2/(w + ...)))))</code> evaluated by the
   * modified Lentz method in its even form <code>1/(w + 1 - 1/(w + 3 - 4/(w + 5 - ...)))</code>.
   */
  private static Complex expIntegralEiFraction(final Complex z) {
    final double re = z.getReal();
    final double im = z.getImaginary();
    if (!Double.isFinite(re) || !Double.isFinite(im) || z.norm() < 2.0
        || (re > 0.0 && Math.abs(im) < 1.0)) {
      // near the origin the series is the method; near the positive real axis, which is the cut of
      // E1(-z), the fraction converges too slowly to be trusted
      return null;
    }
    final Complex w = z.negate();
    final double tiny = 1.0e-300;
    Complex b = w.add(1.0);
    Complex c = new Complex(1.0 / tiny, 0.0);
    Complex d = Complex.ONE.divide(b);
    Complex h = d;
    boolean converged = false;
    for (int i = 1; i <= 5000; i++) {
      final double a = -(double) i * i;
      b = b.add(2.0);
      d = b.add(d.multiply(a));
      if (d.norm() < tiny) {
        d = new Complex(tiny, 0.0);
      }
      c = b.add(new Complex(a, 0.0).divide(c));
      if (c.norm() < tiny) {
        c = new Complex(tiny, 0.0);
      }
      d = Complex.ONE.divide(d);
      final Complex delta = c.multiply(d);
      h = h.multiply(delta);
      if (delta.subtract(1.0).norm() < EPS) {
        converged = true;
        break;
      }
    }
    if (!converged) {
      return null;
    }
    final Complex e1 = h.multiply(w.negate().exp());
    // the logarithms of the formula add up to +-Pi*I, by the side of the real axis z lies on, and
    // to nothing on the axis itself; taken as that, they cost the real part no digits
    final Complex result =
        e1.negate().add(new Complex(0.0, im > 0.0 ? Math.PI : im < 0.0 ? -Math.PI : 0.0));
    return Double.isFinite(result.getReal()) && Double.isFinite(result.getImaginary()) ? result
        : null;
  }

  private static Complex expIntegralEiSeries(final Complex z) {
    if (!isSupported(z)) {
      return null;
    }
    // The principal logarithm below would hand back
    // +Pi i on the axis itself, since atan2(+0.0, negative) is +Pi, so that case is taken directly.
    if (z.getImaginary() == 0.0 && z.getReal() < 0.0) {
      return new Complex(expIntegralEi(z.getReal()), 0.0);
    }
    Complex term = Complex.ONE;
    Complex sum = Complex.ZERO;
    double largest = 0.0;
    int k;
    for (k = 1; k <= MAX_ITERATIONS; k++) {
      term = term.multiply(z).divide(k);
      final Complex contribution = term.divide(k);
      sum = sum.add(contribution);
      largest = Math.max(largest, contribution.norm());
      if (contribution.norm() < EPS * sum.norm()) {
        break;
      }
    }
    if (k > MAX_ITERATIONS) {
      return null;
    }
    Complex[] checked = wellConditioned(new Complex[] {sum.add(logPlusGamma(z))}, largest);
    return checked == null ? null : checked[0];
  }

  /** <code>EulerGamma + Log(z)</code> with the principal logarithm. */
  private static Complex logPlusGamma(final Complex z) {
    return z.log().add(EULER_GAMMA);
  }

  /**
   * Withdraw the results when the series cancelled away its significant digits - the complex
   * counterpart of the guard on the real side.
   */
  private static Complex[] wellConditioned(final Complex[] values, final double largestTerm) {
    for (Complex value : values) {
      if (!Double.isFinite(value.getReal()) || !Double.isFinite(value.getImaginary())) {
        return null;
      }
      if (largestTerm > 1.0e3 * Math.max(value.norm(), 1.0e-300)) {
        return null;
      }
    }
    return values;
  }
}
