package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * JUnit Jupiter tests for {@code PrimitivePolynomialQ}.
 */
public class PrimitivePolynomialQTest extends ExprEvaluatorTestCase {

  @Test
  public void testDegreeOne() {
    // Every degree-1 monic polynomial is primitive: m = p - 1 has no prime divisors leading to
    // x^((p-1)/q) = 1 ... in fact the order-loop is vacuous when m = 1.
    check("PrimitivePolynomialQ(x, 2)", //
        "True");
    check("PrimitivePolynomialQ(x + 1, 2)", //
        "False");
  }

  @Test
  public void testPrimitiveMod2() {
    check("PrimitivePolynomialQ(x^2 + x + 1, 2)", //
        "True");
    check("PrimitivePolynomialQ(x^3 + x + 1, 2)", //
        "True");
    check("PrimitivePolynomialQ(x^3 + x^2 + 1, 2)", //
        "True");
    check("PrimitivePolynomialQ(x^4 + x + 1, 2)", //
        "True");
  }

  @Test
  public void testIrreducibleButNotPrimitiveMod2() {
    // x^4 + x^3 + x^2 + x + 1 is the 5-th cyclotomic polynomial; irreducible over GF(2) but the
    // order of x is 5, not 2^4 - 1 = 15.
    check("PrimitivePolynomialQ(x^4 + x^3 + x^2 + x + 1, 2)", //
        "False");
  }

  @Test
  public void testReducibleMod2() {
    // x^2 + 1 = (x + 1)^2 over GF(2): reducible, hence not primitive.
    check("PrimitivePolynomialQ(x^2 + 1, 2)", //
        "False");
    check("PrimitivePolynomialQ(x^2 + x, 2)", //
        "False");
  }

  @Test
  public void testPrimitiveMod3() {
    // x^2 + 2 x + 2 is primitive mod 3: x has order 8 = 3^2 - 1 in GF(9).
    check("PrimitivePolynomialQ(x^2 + 2*x + 2, 3)", //
        "True");
    // x^2 + 1 is irreducible mod 3 (no root in GF(3)) but x has order 4, not 8 -> not primitive.
    check("PrimitivePolynomialQ(x^2 + 1, 3)", //
        "False");
  }

  @Test
  public void testConstants() {
    check("PrimitivePolynomialQ(0, 2)", //
        "False");
    check("PrimitivePolynomialQ(1, 2)", //
        "False");
    check("PrimitivePolynomialQ(5, 2)", //
        "False");
  }

  @Test
  public void testMultivariate() {
    check("PrimitivePolynomialQ(x + y, 2)", //
        "False");
    check("PrimitivePolynomialQ(x^2 + y^2, 2)", //
        "False");
  }

  @Test
  public void testNonPrimeModulus() {
    check("PrimitivePolynomialQ(x^2 + x + 1, -2)", //
        "True");

    check("PrimitivePolynomialQ(x^2 + x + 1, 4)", //
        "False");
    check("PrimitivePolynomialQ(x^2 + x + 1, 1)", //
        "False");
    check("PrimitivePolynomialQ(x^2 + x + 1, 0)", //
        "False");
    check("PrimitivePolynomialQ(x^2 + x + 1, 2)", //
        "True");

  }

  @Test
  public void testNonIntegerModulus() {
    check("PrimitivePolynomialQ(x^2 + x + 1, 2.5)", //
        "False");
  }

}
