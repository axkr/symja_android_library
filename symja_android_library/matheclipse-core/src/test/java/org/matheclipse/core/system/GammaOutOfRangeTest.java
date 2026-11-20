package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Gamma(x)</code> of a real number whose value lies outside the range of numbers, or close to
 * its edges.
 */
public class GammaOutOfRangeTest extends ExprEvaluatorTestCase {

  @Test
  public void testNegativeArgumentIsTiny() {
    // Gamma(x) = Pi/(Sin(Pi*x)*Gamma(1-x)): far below the range of numbers, not above it
    check("Gamma(N(-10^20-1/2,30))", //
        "Underflow()");
    check("Gamma(N(10^20+1/2,30))", //
        "Overflow()");
  }

  @Test
  public void testMachineArgumentKeepsItsDigits() {
    // the half of 1.0*^15+0.5 counts: Gamma grows by a factor of x^(1/2) with it
    check("Round(Log10(Gamma(1.0*^15+0.5)/Gamma(1.0*^15)), 0.01)", //
        "7.5");
    // below the machine range, as a machine underflow; the digits after the decimal point decide
    // that this is no pole
    check("Gamma(-1.0*^15-0.5)", //
        "0.0");
  }

  @Test
  public void testMachineArgumentNextToTheMachineRange() {
    check("{Gamma(-170.25)<0, Gamma(-171.25)>0}", //
        "{True,True}");
    // above the machine range next to the pole at 0: promoted, not a machine infinity
    check("Round(Gamma(-1.0*^-309)*1.0*^-309)", //
        "-1");
  }
}
