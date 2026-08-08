package org.matheclipse.core.numerics.functions;

import org.hipparchus.complex.Complex;

/**
 * The Airy functions <code>Ai</code>, <code>Bi</code> and their derivatives for complex
 * <code>double</code> arguments.
 *
 * <p>
 * The Apfloat route costs about 14 ms for <code>Ai</code> and 10 ms for <code>Ai'</code>.
 *
 * <p>
 * All four are built from the two entire solutions of <code>w'' = z w</code>,
 * 
 * <pre>
 *   f(z) = sum a_k z^(3k),     a_k = a_(k-1) / ((3k)(3k-1)),     a_0 = 1
 *   g(z) = sum b_k z^(3k+1),   b_k = b_(k-1) / ((3k+1)(3k)),     b_0 = 1
 * </pre>
 * 
 * through <code>Ai = c1 f - c2 g</code> and <code>Bi = sqrt(3) (c1 f + c2 g)</code>, with
 * <code>c1 = Ai(0)</code> and <code>c2 = -Ai'(0)</code>. The derivatives come from the same
 * coefficients differentiated term by term, so all four are one traversal.
 *
 * <p>
 * The series converge for every <code>z</code>, but <code>Ai</code> decays like
 * <code>exp(-2/3 z^(3/2))</code> while the terms grow like <code>exp(|z|^(3/2))</code> - so far
 * out, the answer is the small difference of two large sums and the cancellation guard withdraws
 * it. That is the honest bound on this approach and the reason the Apfloat fall-back is kept.
 */
public final class ComplexAiry {

  /** <code>Ai(0) = 3^(-2/3) / Gamma(2/3)</code>. */
  private static final double AI_ZERO = 0.35502805388781723926;

  /** <code>-Ai'(0) = 3^(-1/3) / Gamma(1/3)</code>. */
  private static final double MINUS_AI_PRIME_ZERO = 0.25881940379280679841;

  private static final double SQRT_3 = 1.7320508075688772935;

  private static final double EPS = 1.0e-16;

  private static final int MAX_TERMS = 400;

  /**
   * Largest <code>|z|</code> offered.
   */
  private static final double MAX_NORM = 12.0;

  private ComplexAiry() {}

  /** Whether the results here are trusted for this argument. */
  public static boolean isSupported(Complex z) {
    if (z == null || Double.isNaN(z.getReal()) || Double.isNaN(z.getImaginary())) {
      return false;
    }
    return z.norm() <= MAX_NORM;
  }

  /** <code>Ai(z)</code>, or null where the value is not trusted. */
  public static Complex airyAi(Complex z) {
    final Complex[] all = airy(z);
    return all == null ? null : all[0];
  }

  /** <code>Ai'(z)</code>, or null where the value is not trusted. */
  public static Complex airyAiPrime(Complex z) {
    final Complex[] all = airy(z);
    return all == null ? null : all[1];
  }

  /** <code>Bi(z)</code>, or null where the value is not trusted. */
  public static Complex airyBi(Complex z) {
    final Complex[] all = airy(z);
    return all == null ? null : all[2];
  }

  /** <code>Bi'(z)</code>, or null where the value is not trusted. */
  public static Complex airyBiPrime(Complex z) {
    final Complex[] all = airy(z);
    return all == null ? null : all[3];
  }

  /**
   * All four at once - they share the same two series, so asking for one costs the same as asking
   * for all of them.
   *
   * @return <code>{Ai(z), Ai'(z), Bi(z), Bi'(z)}</code>, or null
   */
  public static Complex[] airy(final Complex z) {
    if (!isSupported(z)) {
      return null;
    }
    final Complex zCubed = z.multiply(z).multiply(z);

    Complex fTerm = Complex.ONE; // a_k z^(3k), k = 0
    Complex gTerm = z; // b_k z^(3k+1), k = 0
    Complex f = fTerm;
    Complex g = gTerm;
    // the derivatives: f' has no k = 0 term, g' starts at 1
    Complex fPrime = Complex.ZERO;
    Complex gPrime = Complex.ONE;
    double largest = Math.max(1.0, z.norm());

    int k;
    for (k = 1; k <= MAX_TERMS; k++) {
      final double threeK = 3.0 * k;
      fTerm = fTerm.multiply(zCubed).divide(threeK * (threeK - 1.0));
      gTerm = gTerm.multiply(zCubed).divide((threeK + 1.0) * threeK);
      f = f.add(fTerm);
      g = g.add(gTerm);
      // d/dz of a_k z^(3k) is 3k a_k z^(3k-1); dividing the term by z keeps one power in hand
      fPrime = fPrime.add(fTerm.multiply(threeK).divide(z));
      gPrime = gPrime.add(gTerm.multiply(threeK + 1.0).divide(z));
      largest = Math.max(largest, Math.max(fTerm.norm(), gTerm.norm()));
      if (fTerm.norm() + gTerm.norm() < EPS * (f.norm() + g.norm())) {
        break;
      }
    }
    if (k > MAX_TERMS) {
      return null;
    }

    final Complex c1f = f.multiply(AI_ZERO);
    final Complex c2g = g.multiply(MINUS_AI_PRIME_ZERO);
    final Complex c1fPrime = fPrime.multiply(AI_ZERO);
    final Complex c2gPrime = gPrime.multiply(MINUS_AI_PRIME_ZERO);

    final Complex ai = c1f.subtract(c2g);
    final Complex aiPrime = c1fPrime.subtract(c2gPrime);
    final Complex bi = c1f.add(c2g).multiply(SQRT_3);
    final Complex biPrime = c1fPrime.add(c2gPrime).multiply(SQRT_3);

    // Ai is the difference of two sums that are individually far larger than it once |z| grows;
    // measure that loss rather than assume it away. Bi is the sum, so it never suffers - but the
    // four are returned together, so the strictest of them decides.
    if (!wellConditioned(ai, largest) || !wellConditioned(aiPrime, largest)) {
      return null;
    }
    if (!Double.isFinite(bi.getReal()) || !Double.isFinite(biPrime.getReal())) {
      return null;
    }
    return new Complex[] {ai, aiPrime, bi, biPrime};
  }

  private static boolean wellConditioned(final Complex value, final double largestTerm) {
    if (!Double.isFinite(value.getReal()) || !Double.isFinite(value.getImaginary())) {
      return false;
    }
    return largestTerm <= 1.0e3 * Math.max(value.norm(), 1.0e-300);
  }
}
