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
  public void testArgumentOrderSign() {
    // Sres(j) of the exchanged arguments differs by (-1)^((m-j)*(n-j)), so the resultant - the
    // first element - changes sign when both degrees are odd
    check("Subresultants(3*x + 9, 6*x^3 - 3*x + 12, x)", //
        "{-3807,9}");
    check("Subresultants(6*x^3 - 3*x + 12, 3*x + 9, x)", //
        "{3807,9}");
    // the first element stays the resultant in either order
    check("Resultant(3*x + 9, 6*x^3 - 3*x + 12, x)", //
        "-3807");
    check("Resultant(6*x^3 - 3*x + 12, 3*x + 9, x)", //
        "3807");
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

    check("Subresultants((x - 1)^2*(x - 2)*(x - 3), (x - 1)*(x - 4)^2, x, Modulus -> 7)", //
        "{0,1,4,1}");
    check("Subresultants(x^5 + 3, 2*x^2 + 1, x, Modulus -> 5)", //
        "{4,4,3}");
    check("Subresultants(a*x^2 + b, x + 1, x, Modulus -> 5)", //
        "{a+b,1}");
  }

  /**
   * The degrees have to be read off the polynomials <em>after</em> they were reduced, so that a
   * coefficient vanishing modulo the modulus shortens the result list.
   */
  @Test
  public void testSubresultantModulusReducesDegree() {
    // 2*x^2-1 is the constant 1 modulo 2, so Min(2,0)+1 == 1 coefficient is returned
    check("Subresultants(x^2 + 1, 2*x^2 - 1, x, Modulus -> 2)", //
        "{1}");
    // 3*x^3+x is x modulo 3, so Min(1,2)+1 == 2 coefficients are returned
    check("Subresultants(3*x^3 + x, x^2 + 4, x, Modulus -> 3)", //
        "{1,1}");
    check("Subresultants(x^2 + 1, 2*x^2 - 1, x)", //
        "{9,0,1}");
    check("Subresultants(3*x^3 + x, x^2 + 4, x)", //
        "{484,-11,1}");
  }

  /** A polynomial which vanishes completely modulo the modulus has no subresultants. */
  @Test
  public void testSubresultantModulusZeroPolynomial() {
    check("Subresultants(x^2 + 1, 5, x, Modulus -> 5)", //
        "{}");
    check("Subresultants(5*x^2 + 5, x + 3, x, Modulus -> 5)", //
        "{}");
    check("Subresultants(x^2 + 1, 0, x)", //
        "{}");
    // without the modulus the same inputs are ordinary polynomials
    check("Subresultants(x^2 + 1, 5, x)", //
        "{25}");
    check("Subresultants(5*x^2 + 5, x + 3, x)", //
        "{50,1}");
  }

  /**
   * If the first polynomial has the lower degree the coefficients are reported for the swapped
   * argument pair, multiplied by the transposition sign <code>(-1)^((m-j)*(n-j))</code> which isn't
   * reduced again. A coefficient <code>v</code> with a negative sign is printed as
   * <code>v-modulus</code>.
   */
  @Test
  public void testSubresultantModulusSignedSwap() {
    // -6 is the residue 1 modulo 7, -4 and -3 are the residues 1 and 2 modulo 5
    check("Subresultants(x + 1, x^3 + 2, x, Modulus -> 5)", //
        "{-4,1}");
    check("Subresultants(x + 1, x^3 + 2, x, Modulus -> 7)", //
        "{-6,1}");
    check("Subresultants(x + 2, x^3 + 5, x, Modulus -> 5)", //
        "{-3,1}");
    check("Subresultants(x^2 + x + 1, x^4 + 3, x, Modulus -> 5)", //
        "{2,-4,1}");

    // the swapped argument order needs no sign, so the same coefficients stay in 0...modulus-1
    check("Subresultants(x^3 + 2, x + 1, x, Modulus -> 5)", //
        "{4,1}");
    check("Subresultants(x^3 + 2, x + 1, x, Modulus -> 7)", //
        "{6,1}");
    check("Subresultants(x^3 + 5, x + 2, x, Modulus -> 5)", //
        "{3,1}");
    check("Subresultants(x^4 + 3, x^2 + x + 1, x, Modulus -> 5)", //
        "{2,4,1}");

    check("Subresultants(x + 1, x^3 + 6, x, Modulus -> 7)", //
        "{-2,1}");

    // a zero coefficient has no sign to apply: x==-1 is a common root of x+1 and x^3+1
    check("Resultant(x + 1, x^3 + 1, x)", //
        "0");
    check("Subresultants(x + 1, x^3 + 1, x, Modulus -> 7)", //
        "{0,1}");

    // an even sign leaves the coefficient in 0...modulus-1 even though the degrees are swapped
    check("Subresultants(3*x^3 + x, x^2 + 4, x, Modulus -> 3)", //
        "{1,1}");
  }

}
