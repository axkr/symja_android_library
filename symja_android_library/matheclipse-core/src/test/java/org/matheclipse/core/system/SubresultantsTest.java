package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * JUnit Jupiter tests for {@code Subresultants}.
 */
public class SubresultantsTest extends ExprEvaluatorTestCase {

  @Test
  public void testBasicEqualDegree() {
    // Sylvester matrix determinant (resultant) is 2; principal subresultant
    // coefficients of degree 1 and 2 are 1 and 1.
    check("Subresultants(x^2+1, x^2+x, x)", //
        "{2,1,1}");
  }

  @Test
  public void testDifferentDegree() {
    // first element is the resultant, last element is the leading-coefficient term
    check("Subresultants(x^3-x, x^2+1, x)", //
        "{4,-2,1}");
  }

  @Test
  public void testLengthIsMinExponentPlusOne() {
    // Length == Min(Exponent(p1,x), Exponent(p2,x)) + 1
    check("Length(Subresultants((x-y)^2-2, y^3-5, y))", //
        "3");
  }

  @Test
  public void testFirstElementIsResultant() {
    // the first principal subresultant coefficient equals the resultant
    check("Subresultants((x-y)^2-2, y^3-5, y)[[1]]", //
        "17-60*x+12*x^2-10*x^3-6*x^4+x^6");
    check("First(Subresultants(x^3-x, x^2+1, x))", //
        "4");
  }

  @Test
  public void testModulusOption() {
    // {2,1,1} reduced modulo 2 -> {0,1,1}
    check("Subresultants(x^2+1, x^2+x, x, Modulus->2)", //
        "{0,1,1}");
  }

  @Test
  public void testCommonRootMakesResultantZero() {
    // x^2-1 and x-1 share the common root x==1 -> resultant (first element) is 0
    check("First(Subresultants(x^2-1, x-1, x))", //
        "0");
  }


  @Test
  public void testSubresultant001() {
    check("Subresultants((x - 1)*(x - 2)^2*(x - 3)^3, (x - 1)*(x - 2)^2*(x -  4)*(x - 5)^2, x)", //
        "{0,0,0,-64,44,-5,1}");
  }

  @Test
  public void testSubresultantPSC() {
    check("psc = Subresultants((x - a)*(x - b)*(x - c), (x - 1)*(x - 2)*(x - 3),  x)", //
        "{-216+396*a-216*a^2+36*a^3+396*b-726*a*b+396*a^2*b-66*a^3*b-216*b^2+396*a*b^2-\n"
            + "216*a^2*b^2+36*a^3*b^2+36*b^3-66*a*b^3+36*a^2*b^3-6*a^3*b^3+396*c-726*a*c+396*a^\n"
            + "2*c-66*a^3*c-726*b*c+1331*a*b*c-726*a^2*b*c+121*a^3*b*c+396*b^2*c-726*a*b^2*c+\n"
            + "396*a^2*b^2*c-66*a^3*b^2*c-66*b^3*c+121*a*b^3*c-66*a^2*b^3*c+11*a^3*b^3*c-216*c^\n"
            + "2+396*a*c^2-216*a^2*c^2+36*a^3*c^2+396*b*c^2-726*a*b*c^2+396*a^2*b*c^2-66*a^3*b*c^\n"
            + "2-216*b^2*c^2+396*a*b^2*c^2-216*a^2*b^2*c^2+36*a^3*b^2*c^2+36*b^3*c^2-66*a*b^3*c^\n"
            + "2+36*a^2*b^3*c^2-6*a^3*b^3*c^2+36*c^3-66*a*c^3+36*a^2*c^3-6*a^3*c^3-66*b*c^3+121*a*b*c^\n"
            + "3-66*a^2*b*c^3+11*a^3*b*c^3+36*b^2*c^3-66*a*b^2*c^3+36*a^2*b^2*c^3-6*a^3*b^2*c^3-\n"
            + "6*b^3*c^3+11*a*b^3*c^3-6*a^2*b^3*c^3+a^3*b^3*c^3,85-60*a+11*a^2-60*b+36*a*b-6*a^\n"
            + "2*b+11*b^2-6*a*b^2+a^2*b^2-60*c+36*a*c-6*a^2*c+36*b*c-12*a*b*c+a^2*b*c-6*b^2*c+a*b^\n"
            + "2*c+11*c^2-6*a*c^2+a^2*c^2-6*b*c^2+a*b*c^2+b^2*c^2,-6+a+b+c,1}");
    check("psc /. {a->1}", //
        "{0,36-30*b+6*b^2-30*c+25*b*c-5*b^2*c+6*c^2-5*b*c^2+b^2*c^2,-5+b+c,1}");
    check("psc /. {a->1,b->2}", //
        "{0,0,-3+c,1}");
  }

  @Test
  public void testSubresultantModulus() {
    check("Subresultants((x-1)^2*(x-2)*(x-3), (x-1)*(x-4)^2, x)", //
        "{0,36,11,1}");
    check("Subresultants((x-1)^2*(x-2)*(x-3), (x-1)*(x-4)^2, x, Modulus->2)", //
        "{0,0,1,1}");
    check("Subresultants((x-1)^2*(x-2)*(x-3), (x-1)*(x-4)^2, x, Modulus->7)", //
        "{0,1,4,1}");
  }

}
