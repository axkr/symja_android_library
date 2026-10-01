package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The <code>Modulus</code> option of the polynomial functions: a field <code>GF(p)</code> needs a
 * prime, and a prime may be larger than a machine word.
 */
public class ModulusOptionTest extends ExprEvaluatorTestCase {

  /**
   * A modulus which is no prime gives no field. The functions used to answer anyway and lost
   * factors: <code>Factor(x^2+3*x+2, Modulus->6)</code> was <code>1+x</code>.
   */
  @Test
  public void testCompositeModulusWMA() {
    // WMA: Factor::modp: The value of the option Modulus -> 6 should be a prime number or zero.
    check("Factor(x^2 + 3*x + 2, Modulus -> 6)", //
        "Factor(2+3*x+x^2,Modulus->6)");
    check("Factor(x^2 - 1, Modulus -> 8)", //
        "Factor(-1+x^2,Modulus->8)");
    check("FactorList(x^2 + 3*x + 2, Modulus -> 6)", //
        "FactorList(2+3*x+x^2,Modulus->6)");
    check("FactorSquareFree(x^2 + 3*x + 2, Modulus -> 6)", //
        "FactorSquareFree(2+3*x+x^2,Modulus->6)");
    check("FactorSquareFreeList(x^2 + 3*x + 2, Modulus -> 6)", //
        "FactorSquareFreeList(2+3*x+x^2,Modulus->6)");
    check("IrreduciblePolynomialQ(x^2 + 3*x + 2, Modulus -> 6)", //
        "IrreduciblePolynomialQ(2+3*x+x^2,Modulus->6)");
    check("PolynomialGCD(x^2 - 1, x - 1, Modulus -> 6)", //
        "PolynomialGCD(-1+x^2,-1+x,Modulus->6)");
    check("PolynomialLCM(x^2 - 1, x - 1, Modulus -> 6)", //
        "PolynomialLCM(-1+x^2,-1+x,Modulus->6)");
    check("PolynomialExtendedGCD(x^2 - 1, x - 1, x, Modulus -> 6)", //
        "PolynomialExtendedGCD(-1+x^2,-1+x,x,Modulus->6)");
    check("Resultant(x^2 - 1, x - 2, x, Modulus -> 6)", //
        "Resultant(-1+x^2,-2+x,x,Modulus->6)");
    check("Factor(x^2 + 1, Modulus -> 1)", //
        "Factor(1+x^2,Modulus->1)");
    check("GroebnerBasis({x^2 + y^2 - 1, x - y}, {x, y}, Modulus -> 6)", //
        "GroebnerBasis({-1+x^2+y^2,x-y},{x,y},Modulus->6)");
  }

  @Test
  public void testModulusValuesWMA() {
    // WMA: a negative modulus is its absolute value
    check("Factor(x^2 + 1, Modulus -> -5)", //
        "(2+x)*(3+x)");
    check("PolynomialGCD(x^2 + 1, x + 2, Modulus -> -5)", //
        "2+x");
    // WMA: a modulus which is no integer is ignored
    check("Factor(x^2 + 1, Modulus -> a)", //
        "1+x^2");
    check("Factor(x^2 + 1, Modulus -> 0)", //
        "1+x^2");
    // where the ring of the integers modulo m is enough, m needn't be prime
    check("Expand((x + 1)^2*(x + 3), Modulus -> 4)", //
        "3+3*x+x^2+x^3");
    check("PolynomialMod(3*x^2 + 7*x + 9, 6)", //
        "3+x+3*x^2");
    check("CoefficientRules(3*x^2 + 7*x*y + 9, {x, y}, Modulus -> 6)", //
        "{{2,0}->3,{1,1}->1,{0,0}->3}");
  }

  @Test
  public void testSmallPrimes() {
    check("Factor(x^2 + 1, Modulus -> 5)", //
        "(2+x)*(3+x)");
    check("Factor(x^4 + 1, Modulus -> 2)", //
        "(1+x)^4");
    // the argument needn't be expanded
    check("Factor((x + 1)^2 + x, Modulus -> 5)", //
        "(4+x)^2");
    check("Factor(x^2*y + x*y, Modulus -> 5)", //
        "x*(1+x)*y");
    check("Factor(7, Modulus -> 5)", //
        "2");
    check("FactorList(2*x^2 + 2, Modulus -> 5)", //
        "{{2,1},{2+x,1},{3+x,1}}");
    check("FactorSquareFree(x^4 + x^2 + 1, Modulus -> 3)", //
        "(2+x^2)^2");
    // FactorSquareFreeList ignored the option
    check("FactorSquareFreeList(x^4 + 1, Modulus -> 2)", //
        "{{1,1},{1+x,4}}");
    check("FactorSquareFreeList(2*x^2 + 4*x + 2, Modulus -> 5)", //
        "{{2,1},{1+x,2}}");
    check("{IrreduciblePolynomialQ(x^2 + 1, Modulus -> 5), "
        + "IrreduciblePolynomialQ(x^2 + 2, Modulus -> 5)}", //
        "{False,True}");
    check("PolynomialGCD(x^3 - x, x^2 - x, x^2 + x, Modulus -> 5)", //
        "x");
    check("PolynomialLCM(x^2 - 1, x + 2, Modulus -> 5)", //
        "(2+x)*(4+x^2)");
    check("PolynomialExtendedGCD(x^2 + 1, x + 2, x, Modulus -> 5)", //
        "{2+x,{0,1}}");
    check("PolynomialQuotientRemainder(x^2 + 1, 2*x + 1, x, Modulus -> 7)", //
        "{5+4*x,3}");
    check("GroebnerBasis({x^2 + y^2 - 1, x - y}, {x, y}, Modulus -> 7)", //
        "{3+y^2,x+6*y}");
  }

  /**
   * A prime above <code>2^31</code>: the product of two residues doesn't fit into a
   * <code>long</code>, and the functions used to hang.
   */
  @Test
  public void testPrimesAboveMachineWordWMA() {
    // p == 1099511627791
    check("p = NextPrime(2^40); a = 2^39 + 12345; Factor(Expand((x + a)*(x + 7)), Modulus -> p)", //
        "(7+x)*(549755826233+x)");
    check("FactorList(Expand((x + a)^2*(x + 7)), Modulus -> p)", //
        "{{1,1},{7+x,1},{549755826233+x,2}}");
    check("PolynomialGCD(Expand((x + a)*(x + 7)), Expand((x + a)*(x + 9)), Modulus -> p)", //
        "549755826233+x");
    check("PolynomialExtendedGCD(Expand((x + a)*(x + 7)), Expand((x + a)*(x + 9)), x, "
        + "Modulus -> p)", //
        "{549755826233+x,{549755813895,549755813896}}");
    check("PolynomialLCM(x + a, x + 7, Modulus -> p)", //
        "(7+x)*(549755826233+x)");
    check("PolynomialQuotientRemainder(Expand((x + a)*(x + 7)) + 5, x + a, x, Modulus -> p)", //
        "{7+x,5}");
    check("{IrreduciblePolynomialQ(x^2 - 1, Modulus -> p), "
        + "FactorSquareFree(Expand((x + 3)^2*(x + 5)), Modulus -> p)}", //
        "{False,(3+x)^2*(5+x)}");
    check("GroebnerBasis({x^2 + y^2 - 1, x - y}, {x, y}, Modulus -> p)", //
        "{549755813895+y^2,x+1099511627790*y}");
    // the factors multiply up to the polynomial
    check("f = Expand((x + a)^2*(x + 7)*(x^3 + x + 1)); "
        + "PolynomialMod(Expand(Factor(f, Modulus -> p)) - f, p)", //
        "0");
    check("Factor(x^4 - 1, Modulus -> 2^61 - 1)", //
        "(1+x)*(2305843009213693950+x)*(1+x^2)");
    // the two primes next to the limit of a machine word
    check("{Factor(x^4 - 1, Modulus -> 2^31 - 1), Factor(x^4 - 1, Modulus -> NextPrime(2^31))}", //
        "{(1+x)*(2147483646+x)*(1+x^2),(1+x)*(2147483658+x)*(1+x^2)}");
  }

  /**
   * A prime beyond 64 bit: <code>Factor</code> used to ignore the option and factor over the
   * integers, <code>PolynomialGCD</code> threw an <code>ArithmeticException</code>.
   */
  @Test
  public void testPrimesBeyondLongWMA() {
    // q == 100000000000000000039
    check("q = NextPrime(10^20); Factor(x^2 - 1, Modulus -> q)", //
        "(1+x)*(100000000000000000038+x)");
    check("PolynomialGCD(x^2 - 1, x - 1, Modulus -> q)", //
        "100000000000000000038+x");
    check("Factor(x^2 + 1, Modulus -> q)", //
        "1+x^2");
    check("FactorList(3*x^2 - 3, Modulus -> q)", //
        "{{3,1},{1+x,1},{100000000000000000038+x,1}}");
    check("g = Expand(7*(x + 12345678901234567890)^3*(x + 5)^2*(x^2 + 1)); Factor(g, Modulus -> q)", //
        "7*(5+x)^2*(12345678901234567890+x)^3*(1+x^2)");
    check("PolynomialMod(Expand(Factor(g, Modulus -> q)) - g, q)", //
        "0");
    check("{IrreduciblePolynomialQ(x^2 + 1, Modulus -> q), "
        + "IrreduciblePolynomialQ(x^2 - 1, Modulus -> q)}", //
        "{True,False}");
    check("MonomialList((q + 3)*x^2 + 7*x + q - 1, {x}, Modulus -> q)", //
        "{3*x^2,7*x,100000000000000000038}");
    // several variables over such a prime aren't factored, and aren't factored over the integers
    check("Factor(x*y + x, Modulus -> q)", //
        "Factor(x+x*y,Modulus->100000000000000000039)");
  }

  @Test
  public void testCancelAndTogetherWMA() {
    // WMA: 1+x
    check("Cancel((x^2 - 1)/(x - 1), Modulus -> 5)", //
        "1+x");
    // WMA: (2 (3+x))/(x (1+x)) - the numerator and the denominator as their factors over GF(5)
    check("Together(1/x + 1/(x + 1), Modulus -> 5)", //
        "(2*(3+x))/(x*(1+x))");
    // x + 6 is x + 1 in GF(5)
    check("Together(1/(x + 1) + 1/(x + 6), Modulus -> 5)", //
        "2/(1+x)");
    check("Together(1/x + 1/(x + 1), Modulus -> 0)", //
        "(1+2*x)/(x*(1+x))");
    check("Cancel((x^2 - 1)/(x - 1), Modulus -> 6)", //
        "Cancel((-1+x^2)/(-1+x),Modulus->6)");
    // a denominator which vanishes in the field
    check("Together(x/5 + 1, Modulus -> 5)", //
        "Together(1+x/5,Modulus->5)");
    check("Cancel((x^2 - 1)/(x - 1), 7)", //
        "Cancel((-1+x^2)/(-1+x),7)");
  }

  /** Without the option nothing changes. */
  @Test
  public void testWithoutModulus() {
    check("Factor(x^4 - 1)", //
        "(-1+x)*(1+x)*(1+x^2)");
    check("FactorSquareFreeList(x^2 + 2*x + 1)", //
        "{{1+x,2}}");
    check("PolynomialGCD(x^2 - 1, x - 1)", //
        "-1+x");
    check("Cancel((x^2 - 1)/(x - 1))", //
        "1+x");
    check("Together(1/x + 1/(x + 1))", //
        "(1+2*x)/(x*(1+x))");
  }
}
