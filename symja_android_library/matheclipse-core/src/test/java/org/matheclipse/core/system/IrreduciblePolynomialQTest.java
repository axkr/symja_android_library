package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * JUnit Jupiter tests for {@code IrreduciblePolynomialQ}.
 */
public class IrreduciblePolynomialQTest extends ExprEvaluatorTestCase {

  @Test
  public void testConstants() {
    check("IrreduciblePolynomialQ(0)", //
        "False");
    check("IrreduciblePolynomialQ(1)", //
        "False");
    check("IrreduciblePolynomialQ(2)", //
        "False");
    check("IrreduciblePolynomialQ(-3)", //
        "False");
    check("IrreduciblePolynomialQ(2/3)", //
        "False");
    check("IrreduciblePolynomialQ(5)", //
        "False");
  }

  @Test
  public void testUnivariate() {
    check("IrreduciblePolynomialQ(x)", //
        "True");
    check("IrreduciblePolynomialQ(x+1)", //
        "True");
    check("IrreduciblePolynomialQ(x^2+1)", //
        "True");
    check("IrreduciblePolynomialQ(x^2-1)", //
        "False");
    check("IrreduciblePolynomialQ(x^2+2)", //
        "True");
    check("IrreduciblePolynomialQ(x^3-8)", //
        "False");
    check("IrreduciblePolynomialQ(x^3-2)", //
        "True");
    check("IrreduciblePolynomialQ((x-1)^2)", //
        "False");
  }

  @Test
  public void testRationals() {
    check("IrreduciblePolynomialQ(x^2 + 1)", //
        "True");
    check("IrreduciblePolynomialQ(x^2 - 1)", //
        "False");
    check("IrreduciblePolynomialQ(x^2 - 2)", //
        "True");
    check("IrreduciblePolynomialQ(x^4 + 1)", //
        "True");
    check("IrreduciblePolynomialQ((x - 1)^2)", //
        "False");
  }

  @Test
  public void testGaussianIntegers() {
    check("IrreduciblePolynomialQ(x^2 + 1, GaussianIntegers -> True)", //
        "False");
    check("IrreduciblePolynomialQ(x^2 + 2, GaussianIntegers -> True)", //
        "True");
  }

  @Test
  public void testExtension() {
    check("IrreduciblePolynomialQ(x^2 + 1, Extension -> {I})", //
        "False");
    check("IrreduciblePolynomialQ(x^2 - 2, Extension -> {Sqrt(2)})", //
        "False");
  }

  @Test
  public void testExtensionAutomatic() {
    // Sqrt(2) appears as a coefficient => Extension generators = {Sqrt(2)}
    check("IrreduciblePolynomialQ(x^2 - 2*Sqrt(2)*x + 2, Extension -> Automatic)", //
        "False");
    // No algebraic numbers in the polynomial => equivalent to Extension -> None
    check("IrreduciblePolynomialQ(x^2 + 1, Extension -> Automatic)", //
        "True");
  }


  @Test
  public void testExtensionMultivariate() {
    // x^4 - 3 y^2 = (x^2 - Sqrt(3) y)(x^2 + Sqrt(3) y). */
    check("IrreduciblePolynomialQ(x^4 - 3 y^2, Extension -> Sqrt(3))", //
        "False");
    // x^2 - 3 y^2 = (x - Sqrt(3) y)(x + Sqrt(3) y). */
    check("IrreduciblePolynomialQ(x^2 - 3 y^2, Extension -> Sqrt(3))", "False");
    // x^4 - 3 y^4 = (x^2 - Sqrt(3) y^2)(x^2 + Sqrt(3) y^2). */
    check("IrreduciblePolynomialQ(x^4 - 3 y^4, Extension -> Sqrt(3))", "False");
    // x^2 - 2 y^4 stays irreducible over Q(Sqrt(3)) -- Sqrt(2) is not adjoined.
    check("IrreduciblePolynomialQ(x^2 - 2 y^4, Extension -> Sqrt(3))", "True");
    // Combined Listable + multivariate-extension: the headline bug report (`{x^2 - 2 y^4, x^4 - 3
    check("IrreduciblePolynomialQ({x^2 - 2 y^4, x^4 - 3 y^2},  Extension -> Sqrt(3))", //
        "{True,False}");

    // x * y stays reducible via the existing factor path (independent of the extension); the probe
    // must not regress this.
    check("IrreduciblePolynomialQ(x*y, Extension -> Sqrt(3))", //
        "False");

  }

  @Test
  public void testModulus() {
    check("IrreduciblePolynomialQ(x^2 + x + 1, Modulus -> 2)", //
        "True");
    check("IrreduciblePolynomialQ(x^2 + 1, Modulus -> 2)", //
        "False");
  }

  @Test
  public void testMultivariate() {
    check("IrreduciblePolynomialQ(x^2 + y^2)", //
        "True");
    check("IrreduciblePolynomialQ(x^2 + y^2, GaussianIntegers -> True)", //
        "False");
    check("IrreduciblePolynomialQ(x^2 - y^2)", //
        "False");
  }

  @Test
  public void testListable() {
    check("IrreduciblePolynomialQ({x^2 + 1, x^2 - 1, x^2 - 2})", //
        "{True,False,True}");
  }

  @Test
  public void testSqrt() {
    check("IrreduciblePolynomialQ(x^4 - 3 y^2, Extension -> Sqrt(3))", //
        "False");
    check("IrreduciblePolynomialQ(x^2 - 3 y^2, Extension -> Sqrt(3))", //
        "False");
    check("IrreduciblePolynomialQ(x^4 - 3 y^4, Extension -> Sqrt(3))", //
        "False");

    check("IrreduciblePolynomialQ(x^2 - 2 y^4, Extension -> Sqrt(3))", //
        "True");
  }

  @Test
  public void testExtensionAll() {
    check("IrreduciblePolynomialQ(x^2 + y^2, Extension -> All)", //
        "False");
  }

  @Test
  public void testImaginaryUnit() {
    check("IrreduciblePolynomialQ(x^2 + 2*I*x - 1)", //
        "False");
  }

  @Test
  public void testCubeRoot() {
    check("IrreduciblePolynomialQ(x^3 - 3, Extension -> 2^(1/3))", //
        "True");
  }

  @Test
  public void testGaussian() {
    check("IrreduciblePolynomialQ(x^2 + 1, GaussianIntegers -> True)", //
        "False");
    check("IrreduciblePolynomialQ(x^2 + 1, GaussianIntegers -> False)", //
        "True");
    check("IrreduciblePolynomialQ(x^2 + y^2, GaussianIntegers -> True)", //
        "False");
  }

}
