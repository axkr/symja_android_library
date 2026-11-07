package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The square root of a Gaussian rational pulls the square part of the rational content
 * <code>gcd(re, im)</code> out of the radical.
 */
public class GaussianSqrtContentTest extends ExprEvaluatorTestCase {

  @Test
  public void testSquareContent() {
    check("Sqrt(18-18*I)", //
        "3*Sqrt(2-I*2)");
    check("Sqrt(12+12*I)", //
        "2*Sqrt(3+I*3)");
    check("Sqrt(25+50*I)", //
        "5*Sqrt(1+I*2)");
    check("(25+50*I)^(1/2)", //
        "5*Sqrt(1+I*2)");
  }

  @Test
  public void testFractionalContent() {
    check("Sqrt(1/9+2/9*I)", //
        "Sqrt(1+I*2)/3");
    check("Sqrt((-50+100*I)/7)", //
        "5*Sqrt(-2/7+I*4/7)");
  }

  @Test
  public void testReciprocalRoot() {
    check("(8-8*I)^(-1/2)", //
        "1/(2*Sqrt(2-I*2))");
  }

  @Test
  public void testNoSquareContent() {
    check("Sqrt(3+6*I)", //
        "Sqrt(3+I*6)");
    // perfect squares and pure imaginary numbers keep their own forms
    check("Sqrt(12+16*I)", //
        "4+I*2");
    check("Sqrt(-8*I)", //
        "2-I*2");
  }

  @Test
  public void testNumericValue() {
    check("Chop(N(Sqrt(27-45*I))-N(3*Sqrt(3-5*I)))", //
        "0");
  }
}
