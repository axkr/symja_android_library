package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * JUnit Jupiter tests for {@code Factor} over number fields (the {@code Extension} and
 * {@code GaussianIntegers} options and Gaussian coefficients), with radicals as generators, over
 * {@code GF(p)} and for homogeneous polynomials.
 */
public class FactorNumberFieldTest extends ExprEvaluatorTestCase {

  @Test
  public void testExtensionSeveralVariables() {
    check("Factor(x^2-5*y^2, Extension->Sqrt(5))", //
        "(x+Sqrt(5)*y)*(x-Sqrt(5)*y)");
    check("Factor(5*x^2-y^2, Extension->Sqrt(5))", //
        "1/5*(5*x+Sqrt(5)*y)*(5*x-Sqrt(5)*y)");
    check("Factor(x^3-5*y^3, Extension->5^(1/3))", //
        "(x-5^(1/3)*y)*(x^2+5^(1/3)*x*y+5^(2/3)*y^2)");
    check("Factor(x^2*z-3*z*y^2, Extension->Sqrt(3))", //
        "(x+Sqrt(3)*y)*(x-Sqrt(3)*y)*z");
    check("Factor(x^4-25*y^4, Extension->{Sqrt(5),I})", //
        "(-I*x+Sqrt(5)*y)*(I*x+Sqrt(5)*y)*(x+Sqrt(5)*y)*(x-Sqrt(5)*y)");
    check("Factor(x^2+x*y-y^2, Extension->Sqrt(5))", //
        "1/4*(2*x+(1-Sqrt(5))*y)*(2*x+(1+Sqrt(5))*y)");
    // the field is the one the radicals in the generators give
    check("Factor(x^2-3*y^2, Extension->Sqrt(27))", //
        "(x+Sqrt(3)*y)*(x-Sqrt(3)*y)");
    check("Factor(x^2-3*y^2, Extension->1+Sqrt(3))", //
        "(x+Sqrt(3)*y)*(x-Sqrt(3)*y)");
    check("Factor(x^2-2*Sqrt(3)*x*y+3*y^2, Extension->Automatic)", //
        "(x-Sqrt(3)*y)^2");
    check("Factor((x^2-5*y^2)/(x^2+4*y^2), Extension->{I,Sqrt(5)})", //
        "((x+Sqrt(5)*y)*(x-Sqrt(5)*y))/((x-I*2*y)*(x+I*2*y))");
    // the numerator and the denominator share the field of all coefficients
    check("Factor((x^2+2*Sqrt(5)*x*y+5*y^2)/(x-2), Extension->Automatic)", //
        "(x+Sqrt(5)*y)^2/(-2+x)");
    check("Factor((x^2-5)/(x-Sqrt(5)), Extension->Automatic)", //
        "Sqrt(5)+x");
  }

  @Test
  public void testExtensionOneVariable() {
    check("Factor(x^2-7, Extension->Sqrt(7))", //
        "(-Sqrt(7)+x)*(Sqrt(7)+x)");
    check("Factor(x^2+3, Extension->Sqrt(3))", //
        "3+x^2");
    // Sqrt(2) lies in the field of the 8th roots of unity: the 8th and the 24th cyclotomic
    // polynomials split, the others don't
    check("Factor(x^24-1, Extension->Sqrt(2))//Length", //
        "10");
    // Q(Sqrt(5)) lies in the field of the d-th roots of unity for d = 5, 10, 20, but not for 8
    check("Factor(x^8-x^6+x^4-x^2+1, Extension->Sqrt(5))", //
        "1/4*(2+(-1+Sqrt(5))*x^2+2*x^4)*(2-(1+Sqrt(5))*x^2+2*x^4)");
    check("Factor(x^4+x^3+x^2+x+1, Extension->Sqrt(5))", //
        "1/4*(2+(1-Sqrt(5))*x+2*x^2)*(2+(1+Sqrt(5))*x+2*x^2)");
    check("Factor(x^4+1, Extension->Sqrt(5))", //
        "1+x^4");
  }

  @Test
  public void testFactorListExtension() {
    check("FactorList(x^2-5*y^2, Extension->Sqrt(5))", //
        "{{-1,1},{-x+Sqrt(5)*y,1},{x+Sqrt(5)*y,1}}");
    check("FactorList(Sqrt(5)*x^2-5*Sqrt(5), Extension->Sqrt(5))", //
        "{{-Sqrt(5),1},{Sqrt(5)-x,1},{Sqrt(5)+x,1}}");
    check("FactorList(x^2+16*y^2, GaussianIntegers->True)", //
        "{{1,1},{x-I*4*y,1},{x+I*4*y,1}}");
  }

  @Test
  public void testGaussianIntegers() {
    check("Factor(x^2+16*y^2, GaussianIntegers->True)", //
        "(x-I*4*y)*(x+I*4*y)");
    check("Factor(x^2+2*x*y+5*y^2, GaussianIntegers->True)", //
        "(x+(1-I*2)*y)*(x+(1+I*2)*y)");
    check("Factor(x^4-y^4, GaussianIntegers->True)", //
        "(x-y)*(x-I*y)*(x+I*y)*(x+y)");
    check("Factor(0, GaussianIntegers->True)", //
        "0");
    // no cyclotomic factor of x^99-1 splits over Q(I)
    check("Factor(x^99-1, GaussianIntegers->True)//Length", //
        "6");
  }

  @Test
  public void testGaussianCoefficients() {
    check("Factor((3+3*I)*x^2+3+3*I)", //
        "(3+I*3)*(-I+x)*(I+x)");
    // a unit content I goes into the first factor
    check("Factor(I*y^2+I)", //
        "(1+I*y)*(I+y)");
    check("Factor(I*z^2+4*I)", //
        "(2+I*z)*(I*2+z)");
    // the unit I of a content r*I goes into the factor, r stays in front
    check("Factor(2*I*y^2 + 2*I)", //
        "2*(1+I*y)*(I+y)");
    check("Factor(-3*I*z^2 - 12*I)", //
        "-3*(2+I*z)*(I*2+z)");
    check("Factor((2-I)+(6-2*I)*x+4*x^2)", //
        "(1+2*x)*(2-I+2*x)");
  }

  @Test
  public void testModulus() {
    check("Factor(6*x^2+6, Modulus->3)", //
        "0");
    check("Factor(x^4-y^4, Modulus->5)", //
        "(x+y)*(x+2*y)*(x+3*y)*(x+4*y)");
    check("Factor((x^2-4)/(x+2), Modulus->7)", //
        "5+x");
    // a product is factored base by base
    check("Factor((x+1)^4096*(x^2+x+1), Modulus->2)", //
        "(1+x)^4096*(1+x+x^2)");
    check("Factor(2*(3*x+3)^3*(x^2+2), Modulus->7)", //
        "5*(1+x)^3*(2+x^2)");
    // 1/7 has no residue modulo 7
    check("Factor(x/7+1, Modulus->7)", //
        "Factor(1+x/7,Modulus->7)");
    // the leading coefficient vanishes modulo 5, the factor y remains
    check("Factor(5*x^2+x*y+2*y^2, Modulus->5)", //
        "y*(x+2*y)");
    check("Factor(Expand(10*x^3+(x+y)^2*y), Modulus->5)", //
        "y*(x+y)^2");
  }

  @Test
  public void testModulusMessages() {
    // Modulus 2.5 must be an integer or an irreducible univariate polynomial.
    check("Factor(x^2+3, Modulus->2.5)", //
        "Factor(3+x^2,Modulus->2.5)");
    // Conflicting option setting. If Modulus is not 0, then Extension should be None.
    check("Factor(x^2+3, Modulus->5, Extension->Sqrt(2))", //
        "Factor(3+x^2,Modulus->5,Extension->Sqrt(2))");
    // Factoring noninteger polynomials with respect to a modulus is not implemented.
    check("Factor(x^2+3*I, Modulus->5)", //
        "Factor(I*3+x^2,Modulus->5)");
    check("FactorSquareFree(x^2-9, GaussianIntegers->True)", //
        "FactorSquareFree(-9+x^2,GaussianIntegers->True)");
  }

  @Test
  public void testFactorTerms() {
    // over GF(p) no number is taken out
    check("FactorTerms(7*x+21, Modulus->7)", //
        "0");
    check("FactorTerms(4*x+11, Modulus->5)", //
        "1+4*x");
    check("FactorTermsList(c - b)", //
        "{-1,b-c}");
    // {content, the factor free of the variable, the rest}
    check("FactorTermsList(-6*y^3*w + 9*w, {y})", //
        "{-3,w,-3+2*y^3}");
    check("FactorTermsList(6*u^2*v+9*u*v^3, {v})", //
        "{3,u,2*u*v+3*v^3}");
    // the number of a monomial factor is the content
    check("FactorTermsList(3*y*z*x^2 + 6*y*z, {x})", //
        "{3,y*z,2+x^2}");
    // the leading term follows the order of the given variables
    check("FactorTermsList(y - x, {y, x})", //
        "{1,-x+y}");
    check("FactorTermsList(y - x, {x, y})", //
        "{-1,x-y}");
    // FactorList is not Listable
    check("FactorList({x^2-9, y})", //
        "{{1,1},{{-9+x^2,y},1}}");
  }

  @Test
  public void testHomogeneous() {
    check("Factor(x^60-y^60)//Length", //
        "12");
    check("Factor(x^26-y^26)", //
        "(x-y)*(x+y)*(x^12+x^11*y+x^10*y^2+x^9*y^3+x^8*y^4+x^7*y^5+x^6*y^6+x^5*y^7+x^4*y^\n"
            + "8+x^3*y^9+x^2*y^10+x*y^11+y^12)*(x^12-x^11*y+x^10*y^2-x^9*y^3+x^8*y^4-x^7*y^5+x^\n"
            + "6*y^6-x^5*y^7+x^4*y^8-x^3*y^9+x^2*y^10-x*y^11+y^12)");
  }

  @Test
  public void testRadicalGenerators() {
    check("Factor(Sqrt(5)*x^2-Sqrt(5))", //
        "Sqrt(5)*(-1+x)*(1+x)");
    check("Factor((Sqrt(5)*x^2-Sqrt(5))/(x-1))", //
        "Sqrt(5)*(1+x)");
    // only a unit could be taken out
    check("Factor(x^2+2*I*Sqrt(3)*x-3)", //
        "-3+I*2*Sqrt(3)*x+x^2");
  }

  @Test
  public void testRationalFunctionSigns() {
    check("Factor((9-x^2)/(x-3))", //
        "-3-x");
    check("Factor((x-2)/Expand((5*x-2*y)*(3+2*y)*(2+x*y)))", //
        "(-2+x)/((5*x-2*y)*(3+2*y)*(2+x*y))");
  }

  @Test
  public void testFactorSquareFree() {
    check("FactorSquareFree((x^2-4)^2/(x+2)^3)", //
        "(2-x)^2/(2+x)");
    check("FactorSquareFree(Expand(E*(x+2)^2))", //
        "E*(2+x)^2");
    check("FactorSquareFree(Expand(Pi*(x-3)^3))", //
        "Pi*(-3+x)^3");
    check("FactorSquareFreeList((x^2-9)^2/(x+3)^3)", //
        "{{1,1},{-3+x,2},{3+x,-1}}");
  }

  @Test
  public void testTogetherCancelApart() {
    // the factor x^2 of the squared denominator cancels
    check("Together((3*x^3)/(2*x+3*x^2-x^3)^2)", //
        "(3*x)/(2+3*x-x^2)^2");
    check("Cancel((3*x^3)/(2*x+3*x^2-x^3)^2)", //
        "(3*x)/(2+3*x-x^2)^2");
    // the factors are split by GCDs only, not into irreducibles
    check("Together((4*x^4+2*x^3)/((3*x-x^3+2*x^2)^2*(2*x+1)^2))", //
        "(2*x)/((1+2*x)*(3+2*x-x^2)^2)");
    check("Together((6*x^4+3*x^3)/((2*x-x^3+x^2)^2*(2*x+1)^2))", //
        "(3*x)/((1+2*x)*(2+x-x^2)^2)");
    check("Together((-1+I)/(x+2/7+I/7))", //
        "(-7+I*7)/(2+I+7*x)");
    check("Apart(x^2/(b*(x+2)),x)", //
        "-2/b+x/b+4/(b*(2+x))");
  }
}
