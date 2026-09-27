package org.matheclipse.core.numerics.functions;

import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.exception.ArgumentTypeException;

/**
 * The zeros of the Airy functions <code>Ai(x)</code> and <code>Bi(x)</code> on the negative real
 * axis.
 *
 * <p>
 * See <a href="https://dlmf.nist.gov/9.9">DLMF - 9.9 Zeros</a>
 */
public class AiryZeros {

  private AiryZeros() {}

  /**
   * Asymptotic approximation <code>T(t)</code> from
   * <a href="https://dlmf.nist.gov/9.9#iv">DLMF 9.9(iv)</a>
   */
  private static double asymptoticT(double t) {
    double t2 = 1.0 / (t * t);
    return Math.pow(t, 2.0 / 3.0) * (1.0 + t2 * (5.0 / 48.0 + t2 * (-5.0 / 36.0
        + t2 * (77125.0 / 82944.0 + t2 * (-108056875.0 / 6967296.0)))));
  }

  /**
   * The <code>k</code>-th zero of the Airy function <code>Ai(x)</code>.
   *
   * @param k the index of the zero <code>k &gt;= 1</code>
   * @return
   */
  public static double airyAiZero(int k) {
    return airyAiZero(k, 20).doubleValue();
  }

  /**
   * The <code>k</code>-th zero of the Airy function <code>Bi(x)</code>.
   *
   * @param k the index of the zero <code>k &gt;= 1</code>
   * @return
   */
  public static double airyBiZero(int k) {
    return airyBiZero(k, 20).doubleValue();
  }

  /**
   * The <code>k</code>-th zero of the Airy function <code>Ai(x)</code>.
   *
   * @param k the index of the zero <code>k &gt;= 1</code>
   * @param precision the precision of the result in decimal digits
   * @return
   */
  public static Apfloat airyAiZero(int k, long precision) {
    if (k < 1) {
      throw new ArgumentTypeException("Positive index expected in AiryAiZero instead of " + k);
    }
    // a(k) = -T(3/8*Pi*(4*k-1))
    return newton(false, -asymptoticT(0.375 * Math.PI * (4.0 * k - 1.0)), precision);
  }

  /**
   * The <code>k</code>-th zero of the Airy function <code>Bi(x)</code>.
   *
   * @param k the index of the zero <code>k &gt;= 1</code>
   * @param precision the precision of the result in decimal digits
   * @return
   */
  public static Apfloat airyBiZero(int k, long precision) {
    if (k < 1) {
      throw new ArgumentTypeException("Positive index expected in AiryBiZero instead of " + k);
    }
    // b(k) = -T(3/8*Pi*(4*k-3))
    return newton(true, -asymptoticT(0.375 * Math.PI * (4.0 * k - 3.0)), precision);
  }

  private static Apfloat newton(boolean bi, double start, long precision) {
    // the number of correct digits is doubled in every Newton step
    long workingPrecision = 20;
    FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
    Apfloat x = new Apfloat(start, workingPrecision);
    Apfloat epsilon = new Apfloat(1.0e-17, workingPrecision);
    for (int i = 0; i < 50; i++) {
      Apfloat delta = step(h, bi, x);
      x = h.subtract(x, delta);
      if (h.abs(delta).compareTo(h.multiply(epsilon, h.abs(x))) <= 0) {
        break;
      }
    }
    final long guardedPrecision = precision + 5;
    while (workingPrecision < guardedPrecision) {
      workingPrecision = Math.min(2 * workingPrecision - 4, guardedPrecision);
      h = new FixedPrecisionApfloatHelper(workingPrecision);
      x = x.precision(workingPrecision);
      x = h.subtract(x, step(h, bi, x));
    }
    if (precision > 20) {
      // one additional step for safety
      x = h.subtract(x, step(h, bi, x));
    }
    return x.precision(precision);
  }

  private static Apfloat step(FixedPrecisionApfloatHelper h, boolean bi, Apfloat x) {
    if (bi) {
      return h.divide(h.airyBi(x), h.airyBiPrime(x));
    }
    return h.divide(h.airyAi(x), h.airyAiPrime(x));
  }
}
