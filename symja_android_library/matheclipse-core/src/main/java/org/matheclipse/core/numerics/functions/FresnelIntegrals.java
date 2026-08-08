package org.matheclipse.core.numerics.functions;

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
   * Largest <code>|z|</code> for the complex series.
   *
   * <p>
   * Only the series is offered for a complex argument, not the continued fraction: the CF is
   * derived for the large-<b>real</b>-argument regime and its convergence off the real axis was not
   * established here, so extending it would be guesswork. The series converges everywhere and the
   * cancellation guard below decides where it is still trustworthy - which measurement puts at
   * about 6, since the terms carry <code>(Pi z^2/2)^k</code> and grow before they decay.
   */
  private static final double COMPLEX_Z_MAX = 6.0;

  /** Whether {@link #fresnelCS(Complex)} is trusted here. */
  public static boolean isSupported(Complex z) {
    if (z == null || Double.isNaN(z.getReal()) || Double.isNaN(z.getImaginary())) {
      return false;
    }
    return z.norm() <= COMPLEX_Z_MAX;
  }

  /**
   * <code>{FresnelC(z), FresnelS(z)}</code> for complex <code>z</code>, or null outside the
   * validated radius.
   *
   * <p>
   * Both are entire, so there is no branch to place - the series is the whole story.
   */
  public static Complex[] fresnelCS(final Complex z) {
    if (!isSupported(z)) {
      return null;
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
}
