package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * JUnit Jupiter tests for {@code PolynomialMod}.
 */
public class PolynomialModTest extends ExprEvaluatorTestCase {

  @Test
  public void testIntegerModulusUnivariate() {
    // Coefficients reduced modulo 2: 3->1, 5->1, 7->1
    check("PolynomialMod(3*x^2 + 5*x + 7, 2)", //
        "1+x+x^2");
    // Coefficients reduced modulo 3: 3->0, 5->2, 7->1
    check("PolynomialMod(3*x^2 + 5*x + 7, 3)", //
        "1+2*x");
    // All coefficients divisible by 5 -> zero polynomial
    check("PolynomialMod(5*x^3 + 10*x + 15, 5)", //
        "0");
  }

  @Test
  public void testIntegerModulusMultivariate() {
    // 2*y^2 -> 0 mod 2, 3 -> 1 mod 2
    check("PolynomialMod(x^2 + 2*y^2 + 3, 2)", //
        "1+x^2");
    check("PolynomialMod(4*x*y + 6*x + 9*y + 11, 3)", //
        "2+x*y");
  }

  @Test
  public void testNegativeModulus() {
    // Negative modulus should behave like its absolute value.
    check("PolynomialMod(3*x^2 + 5*x + 7, -2)", //
        "1+x+x^2");
  }

  @Test
  public void testModulusZero() {
    // Modulus 0 returns the (expanded) input unchanged.
    check("PolynomialMod(x^2 + 1, 0)", //
        "1+x^2");
    check("PolynomialMod((x + 1)^2, 0)", //
        "1+2*x+x^2");
  }

  @Test
  public void testModulusOne() {
    // Every coefficient is reduced to 0.
    check("PolynomialMod(x^2 + 1, 1)", //
        "0");
    check("PolynomialMod(x^2 + 1, -1)", //
        "0");
  }

  @Test
  public void testConstantPolynomial() {
    check("PolynomialMod(7, 3)", //
        "1");
    check("PolynomialMod(7, 0)", //
        "7");
    check("PolynomialMod(0, 5)", //
        "0");
  }

  @Test
  public void testListOfModuli() {
    // First reduce modulo 3: 3*x^2+5*x+7 -> 1+2x
    // Then reduce 1+2x modulo 2: 1
    check("PolynomialMod(3*x^2 + 5*x + 7, {3, 2})", //
        "1");
    // Empty list: nothing to reduce.
    check("PolynomialMod(x^2 + 1, {})", //
        "1+x^2");
  }

  @Test
  public void testPolynomialMod() {
    // x^2 + 3 x + 5 = (x + 1)(x + 2) + 3 -> remainder 3
    check("PolynomialMod(x^2 + 3*x + 5, x + 1)", //
        "3");
    // Self-modulus is zero.
    check("PolynomialMod(x^2 + 1, x^2 + 1)", //
        "0");
    // x^3 + 2 x + 5 mod x^2 + 1: x*(x^2+1) gives x^3+x; remainder x + 5
    check("PolynomialMod(x^3 + 2*x + 5, x^2 + 1)", //
        "5+x");
  }

  @Test
  public void testPolynomialModulusOption() {
    // 2x-1 mod 3 = 2x+2 -> monic x+1; x^2 mod (x+1) = 1.
    check("PolynomialMod(x^2, 2*x - 1, Modulus -> 3)", //
        "1");
    // x^3 + 2 over GF(2) divided by x+1: x+1 is monic; x^3 = (x+1)(x^2+x+1) + 1 -> remainder 1+2=3
    // mod 2 = 1.
    check("PolynomialMod(x^3 + 2, x + 1, Modulus -> 2)", //
        "1");
  }

  @Test
  public void testPolynomialDomain() {
    check("PolynomialMod(x^2, 2 x - 1)", //
        "1/4");
    // TODO
    check("PolynomialMod(x^2, 2*x-1, CoefficientDomain -> Integers)", //
        "x^2");
  }

  @Test
  public void testExpandedInput() {
    // Verify the input is expanded before reduction.
    check("PolynomialMod((x + 1)^2, 2)", //
        "1+x^2");
    check("PolynomialMod((2*x + 4)*(x + 1), 2)", //
        "0");
  }
}
