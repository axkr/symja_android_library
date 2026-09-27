package org.matheclipse.core.numerics.integral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.integral.Quadrature.QuadratureResult;

public class OscillatoryTailTest {

  private static final double TOL = 1.0e-10;

  private static void assertTail(double expected, java.util.function.DoubleUnaryOperator f,
      double a, double w, double c, boolean cosine) {
    QuadratureResult result = OscillatoryTail.integrate(f, a, w, c, cosine, TOL, TOL, 10000);
    assertEquals(QuadratureResult.STATUS_OK, result.status, result.toString());
    assertEquals(expected, result.estimate, 1.0e-8);
  }

  @Test
  public void testWynnEpsilonAlternatingSeries() {
    // 1 - 1/2 + 1/3 - ... == Log(2), within 20 terms
    WynnEpsilon epsilon = new WynnEpsilon(20);
    double sum = 0.0;
    double estimate = 0.0;
    for (int k = 1; k <= 20; k++) {
      sum += (k % 2 != 0 ? 1.0 : -1.0) / k;
      estimate = epsilon.add(sum);
    }
    assertEquals(Math.log(2.0), estimate, 1.0e-12);
  }

  @Test
  public void testSinc() {
    assertTail(Math.PI / 2.0, x -> x == 0.0 ? 1.0 : Math.sin(x) / x, 0.0, 1.0, 0.0, false);
    // Sin(3*x)/x has the same integral
    assertTail(Math.PI / 2.0, x -> x == 0.0 ? 3.0 : Math.sin(3.0 * x) / x, 0.0, 3.0, 0.0, false);
  }

  @Test
  public void testDampedCosine() {
    // Integrate(Cos(x)/(1+x^2), {x,0,Infinity}) == Pi/(2*E)
    assertTail(Math.PI / (2.0 * Math.E), x -> Math.cos(x) / (1.0 + x * x), 0.0, 1.0, 0.0, true);
  }

  @Test
  public void testFrequencySignAndPhase() {
    // Sin(-x)/x and Sin(x+Pi)/x are both -Sin(x)/x
    assertTail(-Math.PI / 2.0, x -> x == 0.0 ? -1.0 : Math.sin(-x) / x, 0.0, -1.0, 0.0, false);
    assertTail(-Math.PI / 2.0, x -> x == 0.0 ? -1.0 : Math.sin(x + Math.PI) / x, 0.0, 1.0,
        Math.PI, false);
  }

  @Test
  public void testFastDecay() {
    // the partial sums settle before the epsilon table does - its HUGE sentinel must not leak
    assertTail(0.5, x -> Math.exp(-x) * Math.sin(x), 0.0, 1.0, 0.0, false);
    assertTail(0.5, x -> Math.exp(-4.0 * x) * Math.sin(x) * 17.0 / 2.0, 0.0, 1.0, 0.0, false);
  }

  @Test
  public void testLowerLimitAfterZeros() {
    // Integrate(Sin(x)/x, {x,1,Infinity}) == Pi/2 - SinIntegral(1)
    assertTail(Math.PI / 2.0 - 0.9460830703671830, x -> Math.sin(x) / x, 1.0, 1.0, 0.0, false);
  }
}
