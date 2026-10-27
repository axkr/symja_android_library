package org.matheclipse.core.numerics.series.dp.complex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.hipparchus.complex.Complex;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.series.dp.WynnEpsilon;

public class WynnEpsilonComplexTest {

  private static final int TERMS = 16;

  /** The term <code>(-1)^k*c/(2*k+1)</code> of an alternating series. */
  private static Complex term(int k, Complex c) {
    return c.multiply((k % 2 == 0 ? 1.0 : -1.0) / (2.0 * k + 1.0));
  }

  @Test
  public void testShanksOnRealValuesAgreesWithRealTransform() {
    WynnEpsilon real = new WynnEpsilon(1.0e-14, 100, 2, WynnEpsilon.ShanksMethod.SHANKS);
    WynnEpsilonComplex complex =
        new WynnEpsilonComplex(1.0e-14, 100, 2, WynnEpsilonComplex.ShanksMethod.SHANKS);
    double sum = 0.0;
    Complex complexSum = Complex.ZERO;
    for (int k = 0; k < TERMS; k++) {
      Complex e = term(k, Complex.ONE);
      sum += e.getReal();
      complexSum = complexSum.add(e);
      double expected = real.next(e.getReal(), sum);
      Complex estimate = complex.next(e, complexSum);
      assertEquals(expected, estimate.getReal(), 1.0e-14 * Math.max(1.0, Math.abs(expected)),
          "term " + k);
      assertEquals(0.0, estimate.getImaginary(), 0.0, "term " + k);
    }
  }

  @Test
  public void testShanksComplexAlternatingSeries() {
    // Sum((-1)^k*(2+I)/(2*k+1), {k, 0, Infinity}) == (2+I)*Pi/4
    Complex c = new Complex(2.0, 1.0);
    WynnEpsilonComplex complex =
        new WynnEpsilonComplex(1.0e-14, 100, 2, WynnEpsilonComplex.ShanksMethod.SHANKS);
    Complex sum = Complex.ZERO;
    Complex estimate = Complex.NaN;
    for (int k = 0; k < TERMS; k++) {
      Complex e = term(k, c);
      sum = sum.add(e);
      estimate = complex.next(e, sum);
    }
    Complex expected = c.multiply(Math.PI / 4.0);
    assertEquals(expected.getReal(), estimate.getReal(), 1.0e-9);
    assertEquals(expected.getImaginary(), estimate.getImaginary(), 1.0e-9);
  }
}
