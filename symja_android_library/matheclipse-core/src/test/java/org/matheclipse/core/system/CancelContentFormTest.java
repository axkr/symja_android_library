package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The form of a fraction of polynomials in one variable which <code>Cancel</code> gives back: the
 * rational content of the numerator in front, with the sign of the leading coefficient, and the
 * lowest power of the variable taken out of the denominator.
 */
public class CancelContentFormTest extends ExprEvaluatorTestCase {

  @Test
  public void testNumeratorContent() {
    check("Cancel((6-6*x)/(x+2*x^2))", //
        "(-6*(-1+x))/(x*(1+2*x))");
    check("Cancel((3+3*x)/(x^2*(2+x)))", //
        "(3*(1+x))/(x^2*(2+x))");
    check("Cancel((3*x+3*x^2)/(2+5*x))", //
        "(3*(x+x^2))/(2+5*x)");
    check("Cancel((5-5*x^3)/(x^2+4*x^3))", //
        "(-5*(-1+x^3))/(x^2*(1+4*x))");
  }

  @Test
  public void testUnitContent() {
    check("Cancel((3+x)/(x^2+2*x^3))", //
        "(3+x)/(x^2*(1+2*x))");
    check("Cancel((x-4)/(x^2-3*x))", //
        "(-4+x)/((-3+x)*x)");
    check("Cancel((3-x)/(x*(1+5*x)))", //
        "(3-x)/(x*(1+5*x))");
    check("Cancel(3/(x+5*x^2))", //
        "3/(x*(1+5*x))");
  }

  @Test
  public void testDenominatorMonomialAndContent() {
    // the content of the denominator goes in front with its common monomial
    check("Cancel((x+3)/(3*x+6*x^2))", //
        "(3+x)/(3*x*(1+2*x))");
    check("Cancel(3/(2*x+7*x^3))", //
        "3/(x*(2+7*x^2))");
    // several variables
    check("Cancel((x-y)/(x*y^2+x^2*y^3+y^2))", //
        "(x-y)/(y^2*(1+x+x^2*y))");
    // the numerator 1 keeps the denominator, a sum without a common monomial is not factored
    check("Cancel(1/(x+3*x^2))", //
        "1/(x+3*x^2)");
    check("Cancel((x+5)/(x^2-4))", //
        "(5+x)/(-4+x^2)");
  }

  @Test
  public void testDenominatorSign() {
    // a negative leading coefficient of the denominator goes into the numerator
    check("Cancel((x-5)/(-3*x-x^2))", //
        "(5-x)/(x*(3+x))");
    // a numerator -1 goes into a factor with a negative lowest coefficient
    check("Cancel(-1/(x^3-2*x^2))", //
        "1/((2-x)*x^2)");
    check("Cancel(-1/(3+6*x))", //
        "-1/(3*(1+2*x))");
    check("Cancel(-1/(-3+x))", //
        "1/(3-x)");
    // a power of a sum
    check("Cancel(x/(3-3*x)^3)", //
        "-x/(27*(-1+x)^3)");
  }

  @Test
  public void testSeveralVariables() {
    check("Cancel((3*x+3*y)/(x*y+y^2*x^2))", //
        "(3*(x+y))/(x*y*(1+x*y))");
    // a content which is a rational multiple of I
    check("Cancel((2*I*a+2*I*b)/(c+c^2))", //
        "(I*2*(a+b))/(c*(1+c))");
  }

  @Test
  public void testProductDenominator() {
    // each sum of a product gets a last term with a positive coefficient, the sign of an odd
    // power goes into the numerator
    check("Cancel((x+5)/(x^2*(3-x)))", //
        "(-5-x)/((-3+x)*x^2)");
    check("Cancel((x+5)/((3-x)^3*(1+x)))", //
        "(-5-x)/((-3+x)^3*(1+x))");
    check("Cancel((x+5)/(x*(3+6*x)))", //
        "(5+x)/(3*x*(1+2*x))");
    // already normalized
    check("Cancel((x+5)/(x*(3+x)))", //
        "(5+x)/(x*(3+x))");
  }

  @Test
  public void testSeveralVariablesSign() {
    check("Cancel((a+b)/(-2*a-3*b))", //
        "(-a-b)/(2*a+3*b)");
    check("Cancel((a-b)/(-a*b-c))", //
        "(-a+b)/(a*b+c)");
    // the lexicographically leading term decides the sign: a before b, a before b^2
    check("Cancel((a+b)/(b-a))", //
        "(-a-b)/(a-b)");
    check("Cancel((a+b)/(b^2-a))", //
        "(-a-b)/(a-b^2)");
    check("Cancel((a+b)/(a-b^3))", //
        "(a+b)/(a-b^3)");
  }
}
