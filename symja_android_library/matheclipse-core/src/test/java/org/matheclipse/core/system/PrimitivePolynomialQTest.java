package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * JUnit Jupiter tests for {@code PrimitivePolynomialQ}.
 */
public class PrimitivePolynomialQTest extends ExprEvaluatorTestCase {

  @Test
  public void testDegreeOne() {
    // A degree-1 polynomial x + c is primitive iff its root -c generates GF(p)^*. The root of x
    // is 0, which generates nothing, while the root of x + 1 over GF(2) is 1 and GF(2)^* = {1}.
    check("PrimitivePolynomialQ(x, 2)", //
        "False");
    check("PrimitivePolynomialQ(x + 1, 2)", //
        "True");
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
    // a negative modulus is used with its absolute value
    check("PrimitivePolynomialQ(x^2 + x + 1, -2)", //
        "True");

    // Primitivity is only defined over GF(p) for a prime p, so anything else stays unevaluated
    // instead of answering False to a question that wasn't asked.
    check("PrimitivePolynomialQ(x^2 + x + 1, 4)", //
        "PrimitivePolynomialQ(1+x+x^2,4)");
    check("PrimitivePolynomialQ(x^2 + x + 1, 1)", //
        "PrimitivePolynomialQ(1+x+x^2,1)");
    check("PrimitivePolynomialQ(x^2 + x + 1, 0)", //
        "PrimitivePolynomialQ(1+x+x^2,0)");
    check("PrimitivePolynomialQ(x^2 + x + 1, 2)", //
        "True");

  }

  @Test
  public void testNonIntegerModulus() {
    check("PrimitivePolynomialQ(x^2 + x + 1, 2.5)", //
        "PrimitivePolynomialQ(1+x+x^2,2.5)");
  }

  /** The modulus is a required argument. */
  @Test
  public void testMissingModulus() {
    check("PrimitivePolynomialQ(1 + x + x^2)", //
        "PrimitivePolynomialQ(1+x+x^2)");
  }

  @Test
  public void testPrimitiveMod2Extra() {
    check("PrimitivePolynomialQ(1 + x + x^2, 2)", //
        "True");
    check("PrimitivePolynomialQ(1 + x + x^3, 2)", //
        "True");
    check("PrimitivePolynomialQ(1 + x^2 + x^3, 2)", //
        "True");
    check("PrimitivePolynomialQ(1 + x + x^4, 2)", //
        "True");
    check("PrimitivePolynomialQ(x^5 + x^2 + 1, 2)", //
        "True");
    check("PrimitivePolynomialQ(1 + x, 2)", //
        "True");
    // reducible over GF(2): 1 + x^3 == (1 + x)*(1 + x + x^2), 1 + x^2 == (1 + x)^2
    check("PrimitivePolynomialQ(1 + x^3, 2)", //
        "False");
    check("PrimitivePolynomialQ(1 + x^2, 2)", //
        "False");
    check("PrimitivePolynomialQ(x, 2)", //
        "False");
    check("PrimitivePolynomialQ(1 + x + x^2 + x^3 + x^4, 2)", //
        "False");
  }

  @Test
  public void testOddPrimeModulus() {
    check("PrimitivePolynomialQ(2 + x + x^2, 3)", //
        "True");
    // irreducible mod 3, but the order of x is 4 rather than 3^2 - 1 == 8
    check("PrimitivePolynomialQ(1 + x^2, 3)", //
        "False");
    // 1 + x + x^2 == (x - 1)^2 mod 3
    check("PrimitivePolynomialQ(1 + x + x^2, 3)", //
        "False");
    check("PrimitivePolynomialQ(3 + 2*x + x^2, 5)", //
        "True");
    check("PrimitivePolynomialQ(x^4 + 3*x^3 + 2*x^2 + x + 7, 13)", //
        "True");
  }

}
