package org.matheclipse.core.numerics.functions;

import org.hipparchus.complex.Complex;

/**
 * <code>LogGamma</code> and <code>Gamma</code> for complex <code>double</code> arguments, by the
 * Lanczos approximation.
 *
 * <p>
 * This exists chiefly so {@link BarnesG} can be given a complex form: the Barnes G-function is
 * built on the recurrence <code>G(z+1) = Gamma(z) G(z)</code>, so a complex <code>log Gamma</code>
 * is the prerequisite.
 *
 * <p>
 * <b>LogGamma is not log(Gamma).</b> The two differ by a multiple of <code>2 Pi i</code> once the
 * argument leaves the right half-plane: <code>Gamma</code> winds, and the principal logarithm of it
 * folds that winding back into <code>(-Pi, Pi]</code>, while <code>LogGamma</code> - the function
 * Symja mean - continues analytically across the strips instead. That is why the left half-plane is
 * reached by the recurrence rather than by reflection; see {@link #logGamma(Complex)}.
 * 
 */
public final class ComplexGamma {

  /** Lanczos parameter g, paired with the coefficient table below (g = 7, n = 9). */
  private static final double LANCZOS_G = 7.0;

  private static final double[] LANCZOS_COEFFICIENTS = {0.99999999999980993, 676.5203681218851,
      -1259.1392167224028, 771.32342877765313, -176.61502916214059, 12.507343278686905,
      -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7};

  private static final double LOG_SQRT_TWO_PI = 0.91893853320467274178;

  /** Beyond this the argument is so large that the shift loops in callers stop being cheap. */
  private static final double MAX_NORM = 1.0e8;

  /** Cap on the shift loop; the real part only has to reach 1/2, so this is a safety net. */
  private static final int MAX_SHIFTS = 100000;

  private ComplexGamma() {}

  /**
   * Whether the results here are trusted. The non-positive integers are excluded: they are the
   * poles of <code>Gamma</code> and the branch points of <code>LogGamma</code>.
   */
  public static boolean isSupported(Complex z) {
    if (z == null || Double.isNaN(z.getReal()) || Double.isNaN(z.getImaginary())) {
      return false;
    }
    if (z.getImaginary() == 0.0 && z.getReal() <= 0.0 && Math.rint(z.getReal()) == z.getReal()) {
      return false;
    }
    return z.norm() <= MAX_NORM;
  }

  /**
   * <code>LogGamma(z)</code> - the analytic continuation, not <code>log(Gamma(z))</code>.
   *
   * @return null where the value is not trusted
   */
  public static Complex logGamma(final Complex z) {
    if (!isSupported(z)) {
      return null;
    }
    if (z.getReal() >= 0.5) {
      return lanczosLogGamma(z);
    }

    // Shift into the right half-plane with the functional equation rather than reflecting.
    //
    // LogGamma(w+1) = LogGamma(w) + Log(w) holds with the PRINCIPAL logarithm for every w off the
    // negative real axis, so walking the argument right and subtracting the logs collected on the
    // way needs no branch bookkeeping at all. Reflection through
    // log(Pi) - log(sin(Pi z)) - LogGamma(1-z) does not: the principal log of the sine folds the
    // winding into (-Pi, Pi], and the multiple of 2 Pi i that removes is a function of both the
    // real and imaginary parts - measured at 0, -2Pi, -4Pi and -6Pi across the reference grid with
    // no simple dependence on either alone. The recurrence sidesteps that entirely.
    Complex shifted = z;
    Complex collected = Complex.ZERO;
    int guard = 0;
    while (shifted.getReal() < 0.5) {
      collected = collected.add(shifted.log());
      shifted = shifted.add(1.0);
      if (++guard > MAX_SHIFTS) {
        return null;
      }
    }
    return lanczosLogGamma(shifted).subtract(collected);
  }

  /** The Lanczos series, valid for <code>Re(z) &gt;= 1/2</code>. */
  private static Complex lanczosLogGamma(final Complex z) {
    // series is written for z-1, so shift once
    final Complex w = z.subtract(1.0);
    Complex series = new Complex(LANCZOS_COEFFICIENTS[0], 0.0);
    for (int i = 1; i < LANCZOS_COEFFICIENTS.length; i++) {
      series = series.add(w.add(i).reciprocal().multiply(LANCZOS_COEFFICIENTS[i]));
    }
    final Complex t = w.add(LANCZOS_G + 0.5);
    return t.log().multiply(w.add(0.5)).subtract(t).add(LOG_SQRT_TWO_PI).add(series.log());
  }

  /**
   * <code>Gamma(z)</code> for complex <code>z</code>, as <code>exp(LogGamma(z))</code>.
   *
   * @return null where the value is not trusted, or where the exponential would overflow
   */
  public static Complex gamma(final Complex z) {
    final Complex logGamma = logGamma(z);
    if (logGamma == null) {
      return null;
    }
    if (logGamma.getReal() > 709.0) {
      return null; // would overflow a double
    }
    final Complex result = logGamma.exp();
    return Double.isFinite(result.getReal()) && Double.isFinite(result.getImaginary()) ? result
        : null;
  }

  /**
   * <code>Beta(a, b) = Gamma(a) Gamma(b) / Gamma(a+b)</code> for complex arguments.
   *
   * <p>
   * Computed through the logarithms rather than as a ratio of gammas. That is what keeps it usable
   * next to the poles: <code>Beta(z, 3)</code> at <code>z = -5</code> is of order
   * <code>10^15</code>, and forming it as a quotient of two separately-overflowing gammas loses it
   * entirely.
   *
   * @return null where the value is not trusted or would overflow
   */
  public static Complex beta(final Complex a, final Complex b) {
    final Complex logA = logGamma(a);
    final Complex logB = logGamma(b);
    final Complex logAB = logGamma(a.add(b));
    if (logA == null || logB == null || logAB == null) {
      return null;
    }
    final Complex logBeta = logA.add(logB).subtract(logAB);
    if (logBeta.getReal() > 709.0) {
      return null;
    }
    final Complex result = logBeta.exp();
    return Double.isFinite(result.getReal()) && Double.isFinite(result.getImaginary()) ? result
        : null;
  }
}
