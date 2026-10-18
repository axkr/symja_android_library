package org.matheclipse.core.numerics.functions;

import java.math.BigDecimal;
import org.hipparchus.complex.Complex;

/**
 * The Fresnel integrals <code>FresnelC(x) = integral from 0 to x of cos(Pi*t^2/2) dt</code> and
 * <code>FresnelS(x) = integral from 0 to x of sin(Pi*t^2/2) dt</code> for <code>double</code>
 * arguments, in the normalization Symja use.
 *
 * <p>
 * The Apfloat route costs about 27 ms per call for each of them. Note that the
 * <code>fresnelC</code> / <code>fresnelS</code> in {@link GammaJS} are no help: despite living
 * beside the genuine double ports in this package they only wrap Apfloat, and on the <b>full
 * precision</b> helper rather than the double one, so routing through them measures ~258 ms - an
 * order of magnitude slower than the thing they would replace.
 *
 * <p>
 * Two regimes meeting at <code>|x| = 1.5</code>:
 * <ul>
 * <li><b>small argument</b>: the power series in <code>Pi*x^2/2</code>, whose terms feed
 * <code>C</code> and <code>S</code> alternately;</li>
 * <li><b>large argument</b>: a continued fraction, by the modified Lentz algorithm, for the complex
 * error function <code>w(z)</code> that both integrals descend from. Both fall out of one
 * evaluation - <code>C + i*S</code> is a single complex number - so asking for them together costs
 * no more than asking for one.</li>
 * </ul>
 *
 * <p>
 * <b>Validated domain.</b> See {@link #isSupported(double)}; callers fall back to arbitrary
 * precision outside it. The bound was measured against Apfloat at 40 digits.
 */
public final class FresnelIntegrals {

  private static final double PI_BY_2 = 1.5707963267948966;

  private static final double EPS = 1.0e-16;

  /** Below this the series is used, above it the continued fraction. */
  private static final double SERIES_LIMIT = 1.5;

  private static final int MAX_ITERATIONS = 300;

  private static final double FP_MIN = 1.0e-300;

  /**
   * Largest magnitude handled here.
   *
   * <p>
   * Both integrals approach <code>+-1/2</code> with an oscillation whose phase is
   * <code>Pi*x^2/2</code> - quadratic in <code>x</code>, so the phase error grows as
   * <code>x^2</code> and accuracy is lost twice as fast as for a function oscillating with unit
   * frequency. Measured against a 40-digit reference the worst error over the range runs 4e-16 at
   * <code>|x| = 1</code>, 2e-15 at 10, 2e-14 at 100 and 2e-13 at 300, so the cut keeps a decimal
   * order of magnitude of head room under the 1e-12 callers rely on.
   */
  private static final double X_MAX = 300.0;

  private FresnelIntegrals() {}

  /** Whether the results here are trusted for this argument. */
  public static boolean isSupported(double x) {
    return !Double.isNaN(x) && Math.abs(x) <= X_MAX;
  }

  /**
   * <code>{FresnelC(x), FresnelS(x)}</code> for an argument beyond the validated domain, from the
   * auxiliary functions <code>f</code> and <code>g</code>:
   * <code>C(x) = 1/2 + f(x)*Sin(Pi*x^2/2) - g(x)*Cos(Pi*x^2/2)</code> and
   * <code>S(x) = 1/2 - f(x)*Cos(Pi*x^2/2) - g(x)*Sin(Pi*x^2/2)</code>.
   *
   * <p>
   * The phase is <code>Pi</code> times <code>x^2/2</code> modulo 2. The square of a double has 106
   * bits, so it is formed exactly: the product of two doubles has no digit of the phase left from
   * <code>10^8</code> on, where the oscillating part is still <code>3*10^-9</code> of the value.
   *
   * @param x the argument, of a size above the largest one of {@link #isSupported(double)}
   * @return <code>{C(x), S(x)}</code>
   * @see <a href="https://dlmf.nist.gov/7.12#ii">DLMF 7.12(ii)</a>
   */
  public static double[] fresnelCSOfLarge(final double x) {
    final double t = Math.abs(x);
    final double r = new BigDecimal(t).pow(2).divide(BigDecimal.valueOf(2L))
        .remainder(BigDecimal.valueOf(2L)).doubleValue();
    // Sin(Pi*r) and Cos(Pi*r), exact at the multiples of 1/2
    final double n = Math.rint(2.0 * r);
    final double angle = Math.PI * (r - n / 2.0);
    double sin = Math.sin(angle);
    double cos = Math.cos(angle);
    switch (((int) n) & 3) {
      case 1: {
        final double s = sin;
        sin = cos;
        cos = -s;
        break;
      }
      case 2:
        sin = -sin;
        cos = -cos;
        break;
      case 3: {
        final double s = sin;
        sin = -cos;
        cos = s;
        break;
      }
      default:
        break;
    }
    // u = 1/(Pi*t^2/2): f = (1 - 3/4*u^2 + 105/16*u^4)/(Pi*t), g = (u/2 - 15/8*u^3)/(Pi*t)
    final double u = 2.0 / Math.PI / t / t;
    final double u2 = u * u;
    final double f = (1.0 - 0.75 * u2 + 6.5625 * u2 * u2) / Math.PI / t;
    final double g = (0.5 * u - 1.875 * u * u2) / Math.PI / t;
    final double c = 0.5 + f * sin - g * cos;
    final double s = 0.5 - f * cos - g * sin;
    return x < 0.0 ? new double[] {-c, -s} : new double[] {c, s};
  }

  /** <code>FresnelC(x)</code>, an odd function. */
  public static double fresnelC(double x) {
    return fresnelCS(x)[0];
  }

  /** <code>FresnelS(x)</code>, an odd function. */
  public static double fresnelS(double x) {
    return fresnelCS(x)[1];
  }

  /**
   * Both at once.
   *
   * @return <code>{FresnelC(x), FresnelS(x)}</code>, or <code>{NaN, NaN}</code> if the iteration
   *         did not converge
   */
  public static double[] fresnelCS(final double x) {
    final double ax = Math.abs(x);
    double c;
    double s;

    if (ax < 1.0e-150) {
      // C(x) -> x and S(x) -> Pi*x^3/6; below this the cube underflows and C is just x
      c = ax;
      s = 0.0;
    } else if (ax <= SERIES_LIMIT) {
      // C(x) = sum (-1)^k (Pi/2)^(2k) x^(4k+1) / ((2k)! (4k+1))
      // S(x) = sum (-1)^k (Pi/2)^(2k+1) x^(4k+3) / ((2k+1)! (4k+3))
      // One loop generates both: each term alternates between the two sums.
      double sum = 0.0;
      double sums = 0.0;
      double sumc = ax;
      double sign = 1.0;
      final double fact = PI_BY_2 * ax * ax;
      double term = ax;
      boolean odd = true;
      int n = 3;
      int k;
      for (k = 1; k <= MAX_ITERATIONS; k++) {
        term *= fact / k;
        sum += sign * term / n;
        final double test = Math.abs(sum) * EPS;
        if (odd) {
          sign = -sign;
          sums = sum;
          sum = sumc;
        } else {
          sumc = sum;
          sum = sums;
        }
        if (term < test) {
          break;
        }
        odd = !odd;
        n += 2;
      }
      if (k > MAX_ITERATIONS) {
        return new double[] {Double.NaN, Double.NaN};
      }
      s = sums;
      c = sumc;
    } else {
      // Continued fraction, Lentz, in complex arithmetic written out as real pairs.
      final double pix2 = Math.PI * ax * ax;
      double br = 1.0;
      double bi = -pix2;
      double cr = 1.0 / FP_MIN;
      double ci = 0.0;
      double den = br * br + bi * bi;
      double dr = br / den;
      double di = -bi / den;
      double hr = dr;
      double hi = di;
      int n = -1;
      int k;
      for (k = 2; k <= MAX_ITERATIONS; k++) {
        n += 2;
        final double a = -(double) n * (n + 1);
        br += 4.0;
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
        den = cr * cr + ci * ci;
        if (den < FP_MIN) {
          den = FP_MIN;
        }
        cr = br + a * cr / den;
        ci = bi - a * ci / den;
        // del = c*d; h *= del
        final double delr = cr * dr - ci * di;
        final double deli = cr * di + ci * dr;
        tr = hr * delr - hi * deli;
        hi = hr * deli + hi * delr;
        hr = tr;
        if (Math.abs(delr - 1.0) + Math.abs(deli) < EPS) {
          break;
        }
      }
      if (k > MAX_ITERATIONS) {
        return new double[] {Double.NaN, Double.NaN};
      }
      // h *= ax - i*ax
      double tr = hr * ax + hi * ax;
      hi = hi * ax - hr * ax;
      hr = tr;
      // cs = (1/2 + i/2) * (1 - (cos(pix2/2) + i*sin(pix2/2)) * h)
      final double cosT = Math.cos(0.5 * pix2);
      final double sinT = Math.sin(0.5 * pix2);
      final double er = cosT * hr - sinT * hi;
      final double ei = cosT * hi + sinT * hr;
      final double ur = 1.0 - er;
      final double ui = -ei;
      c = 0.5 * (ur - ui);
      s = 0.5 * (ur + ui);
    }

    if (x < 0.0) {
      return new double[] {-c, -s};
    }
    return new double[] {c, s};
  }

  // ----------------------------------------------------------------------------------------
  // Complex arguments
  // ----------------------------------------------------------------------------------------

  /**
   * Largest <code>|z|</code> for the complex series; above it the asymptotic expansion is used.
   *
   * <p>
   * The continued fraction is not used for a complex argument: it is derived for the
   * large-<b>real</b>-argument regime and its convergence off the real axis was not established
   * here. The series converges everywhere and the cancellation guard below decides where it is
   * still trustworthy - which measurement puts at about 6, since the terms carry
   * <code>(Pi z^2/2)^k</code> and grow before they decay. From there on the smallest term of the
   * asymptotic expansion is below <code>Exp(-Pi*36/2)</code>, far under the double rounding.
   */
  private static final double COMPLEX_Z_MAX = 6.0;

  /**
   * Largest <code>|Re z * Im z|</code> of the asymptotic expansion: beyond it the factor
   * <code>Cosh(Pi*Re z*Im z)</code> of the result is near the double range.
   */
  private static final double COMPLEX_PHASE_MAX = 220.0;

  /**
   * <code>{FresnelC(z), FresnelS(z)}</code> for complex <code>z</code>, or null where neither the
   * series nor the asymptotic expansion is trusted, or the result is beyond the double range.
   *
   * <p>
   * Both are entire, so there is no branch to place.
   */
  public static Complex[] fresnelCS(final Complex z) {
    if (z == null || !Double.isFinite(z.getReal()) || !Double.isFinite(z.getImaginary())) {
      return null;
    }
    if (z.norm() > COMPLEX_Z_MAX) {
      return fresnelCSOfLarge(z);
    }
    if (z.getReal() == 0.0 && z.getImaginary() == 0.0) {
      return new Complex[] {Complex.ZERO, Complex.ZERO};
    }
    // With u = (Pi/2) z^2 the two series are
    // C(z) = z * sum (-1)^k u^(2k) / ((2k)! (4k+1))
    // S(z) = z * sum (-1)^k u^(2k+1) / ((2k+1)! (4k+3))
    // so termC tracks z*u^(2k)/(2k)! and termS tracks z*u^(2k+1)/(2k+1)!, each divided by its own
    // odd denominator only when it is added. Keeping the term and its contribution separate matters
    // - folding the denominator into the recurrence is what made the first version of this wrong by
    // two orders of magnitude.
    final Complex u = z.multiply(z).multiply(PI_BY_2);
    final Complex uSquared = u.multiply(u);
    Complex termC = z; // k = 0
    Complex termS = z.multiply(u); // k = 0
    Complex c = termC; // /(4*0+1)
    Complex s = termS.divide(3.0); // /(4*0+3)
    double largest = Math.max(c.norm(), s.norm());
    int k;
    for (k = 1; k <= MAX_ITERATIONS; k++) {
      termC = termC.multiply(uSquared).divide((2.0 * k - 1.0) * (2.0 * k)).negate();
      final Complex contributionC = termC.divide(4.0 * k + 1.0);
      c = c.add(contributionC);
      termS = termS.multiply(uSquared).divide((2.0 * k) * (2.0 * k + 1.0)).negate();
      final Complex contributionS = termS.divide(4.0 * k + 3.0);
      s = s.add(contributionS);
      largest = Math.max(largest, Math.max(contributionC.norm(), contributionS.norm()));
      if (contributionC.norm() + contributionS.norm() < EPS * (c.norm() + s.norm())) {
        break;
      }
    }
    if (k > MAX_ITERATIONS) {
      return null;
    }
    if (largest > 1.0e3 * Math.max(c.norm(), 1.0e-300)
        || largest > 1.0e3 * Math.max(s.norm(), 1.0e-300)) {
      return null; // the series cancelled away its significant digits
    }
    return new Complex[] {c, s};
  }

  /**
   * <code>{FresnelC(z), FresnelS(z)}</code> for <code>|z| &gt; 6</code> from the auxiliary
   * functions: <code>C(z) = 1/2 + f(z)*Sin(Pi*z^2/2) - g(z)*Cos(Pi*z^2/2)</code> and
   * <code>S(z) = 1/2 - f(z)*Cos(Pi*z^2/2) - g(z)*Sin(Pi*z^2/2)</code>, with the asymptotic series of
   * <code>f</code> and <code>g</code>.
   *
   * <p>
   * The expansion is used in the sector <code>|Arg(z)| &lt;= Pi/4</code> only; the other sectors
   * are reached by <code>C(-z) = -C(z)</code>, <code>C(I*z) = I*C(z)</code> and
   * <code>S(I*z) = -I*S(z)</code>. The real part of the phase <code>z^2/2</code> is reduced modulo 2
   * exactly, as in {@link #fresnelCSOfLarge(double)}.
   *
   * @return the pair, or null if the result leaves the double range
   * @see <a href="https://dlmf.nist.gov/7.12#ii">DLMF 7.12(ii)</a>
   */
  private static Complex[] fresnelCSOfLarge(final Complex z) {
    double x = z.getReal();
    double y = z.getImaginary();
    // C(z) = rotC*C(w), S(z) = rotS*S(w) with w in the right sector
    boolean rotated = false;
    if (Math.abs(y) > Math.abs(x)) {
      // z = I*w
      final double t = x;
      x = y;
      y = -t;
      rotated = true;
    }
    boolean negated = false;
    if (x < 0.0) {
      x = -x;
      y = -y;
      negated = true;
    }
    final double b = x * y;
    if (Math.abs(b) > COMPLEX_PHASE_MAX) {
      return null;
    }
    // Re(w^2/2) modulo 2 in (-1, 1], from the exact squares: a phase just below a multiple of 2
    // keeps its relative digits, which the imaginary parts of the results need
    BigDecimal phase = new BigDecimal(x).pow(2).subtract(new BigDecimal(y).pow(2))
        .divide(BigDecimal.valueOf(2L)).remainder(BigDecimal.valueOf(2L));
    if (phase.compareTo(BigDecimal.ONE) > 0) {
      phase = phase.subtract(BigDecimal.valueOf(2L));
    } else if (phase.compareTo(BigDecimal.ONE.negate()) <= 0) {
      phase = phase.add(BigDecimal.valueOf(2L));
    }
    final double a = phase.doubleValue();
    final double sinA = sinPi(a);
    final double cosA = cosPi(a);
    final double coshB = Math.cosh(Math.PI * b);
    final double sinhB = Math.sinh(Math.PI * b);
    // Sin(Pi*(a+I*b)) and Cos(Pi*(a+I*b))
    final Complex sin = new Complex(sinA * coshB, cosA * sinhB);
    final Complex cos = new Complex(cosA * coshB, -sinA * sinhB);

    final Complex w = new Complex(x, y);
    final Complex inverseU = w.multiply(w).multiply(PI_BY_2).reciprocal();
    // terms (1/2)_k / u^k: even k feed f, odd k feed g, with alternating signs in each
    Complex term = Complex.ONE;
    Complex f = Complex.ONE;
    Complex g = Complex.ZERO;
    double previous = Double.MAX_VALUE;
    boolean converged = false;
    for (int k = 1; k <= MAX_ITERATIONS; k++) {
      term = term.multiply(inverseU).multiply(k - 0.5);
      final double size = term.norm();
      if (size > previous) {
        break; // the asymptotic series started to diverge
      }
      previous = size;
      final Complex signed = ((k >> 1) & 1) == 0 ? term : term.negate();
      if ((k & 1) == 0) {
        f = f.add(signed);
      } else {
        g = g.add(signed);
      }
      if (size < EPS) {
        converged = true;
        break;
      }
    }
    if (!converged) {
      return null;
    }
    final Complex scale = w.multiply(Math.PI).reciprocal();
    f = f.multiply(scale);
    g = g.multiply(scale);
    Complex c = f.multiply(sin).subtract(g.multiply(cos)).add(0.5);
    Complex s = f.multiply(cos).add(g.multiply(sin)).negate().add(0.5);
    if (negated) {
      c = c.negate();
      s = s.negate();
    }
    if (rotated) {
      c = c.multiply(Complex.I);
      s = s.multiply(Complex.MINUS_I);
    }
    if (c.isNaN() || c.isInfinite() || s.isNaN() || s.isInfinite()) {
      return null;
    }
    return new Complex[] {c, s};
  }

  /** <code>Sin(Pi*r)</code>, exact at the multiples of 1/2. */
  private static double sinPi(double r) {
    final double n = Math.rint(2.0 * r);
    final double angle = Math.PI * (r - n / 2.0);
    switch (((int) n) & 3) {
      case 1:
        return Math.cos(angle);
      case 2:
        return -Math.sin(angle);
      case 3:
        return -Math.cos(angle);
      default:
        return Math.sin(angle);
    }
  }

  /** <code>Cos(Pi*r)</code>, exact at the multiples of 1/2. */
  private static double cosPi(double r) {
    final double n = Math.rint(2.0 * r);
    final double angle = Math.PI * (r - n / 2.0);
    switch (((int) n) & 3) {
      case 1:
        return -Math.sin(angle);
      case 2:
        return -Math.cos(angle);
      case 3:
        return Math.sin(angle);
      default:
        return Math.cos(angle);
    }
  }
}
