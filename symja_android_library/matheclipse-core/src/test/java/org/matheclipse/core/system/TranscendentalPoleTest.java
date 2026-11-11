package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * A pole of the integrand at a zero of a transcendental denominator inside the interval, and the
 * output digits after a numerical sum.
 */
public class TranscendentalPoleTest extends ExprEvaluatorTestCase {

  @Test
  public void testPoleAtLogarithm() {
    // E^x == 3 at x == Log(3), inside both intervals: the integrals diverge
    check("Integrate(x/(E^x - 3), {x, 0, Infinity})", //
        "Integrate(x/(-3+E^x),{x,0,Infinity})");
    check("Integrate(x/(E^x - 3), {x, 0, 2})", //
        "Integrate(x/(-3+E^x),{x,0,2})");
    // the pole outside
    check("Abs(N(Integrate(x/(E^x - 3), {x, 2, Infinity})) - "
        + "NIntegrate(x/(E^x - 3), {x, 2, Infinity})) < 10^-10", //
        "True");
  }

  @Test
  public void testDigitsAfterNSum() {
    check("NSum(1/n^n, {n, 1, Infinity}); 1/7.0", //
        "0.142857");
  }
}
