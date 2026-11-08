package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The forms Simplify chooses for a quotient of polynomials: the power of the variable pulled out
 * of the denominator, the sign of a denominator leading with a negative term, no polynomial
 * division outside FullSimplify.
 */
public class SimplifyQuotientFormsTest extends ExprEvaluatorTestCase {

  @Test
  public void testDenominatorMonomial() {
    // a tie is decided by a leading negative term in the numerator or the denominator
    check("Simplify((-3+7*y)/(2*y+5*y^2))", //
        "(-3+7*y)/(y*(2+5*y))");
    check("Simplify((3+7*y)/(2*y+5*y^2))", //
        "(3+7*y)/(2*y+5*y^2)");
    check("Simplify((3+y)/(-7*y+2*y^2))", //
        "(3+y)/(y*(-7+2*y))");
    check("Simplify((3+y)/(7*y-2*y^2))", //
        "(3+y)/(7*y-2*y^2)");
    check("Simplify((1+4*y+4*y^2)/(3*y+5*y^2))", //
        "(1+2*y)^2/(y*(3+5*y))");
  }

  @Test
  public void testDenominatorSign() {
    check("Simplify((-3-y-2*y^2)/(-5+7*y))", //
        "(3+y+2*y^2)/(5-7*y)");
    check("Simplify(-6/(-3*y+3*y^2))", //
        "2/(y-y^2)");
    check("Simplify(-1/(-3+9*y))", //
        "1/(3-9*y)");
  }

  @Test
  public void testNoPolynomialDivision() {
    check("Simplify((2+5*y+3*y^2)/(-7+2*y-y^2))", //
        "-(2+5*y+3*y^2)/(7-2*y+y^2)");
  }

  @Test
  public void testFullSimplifyPartialFractionDenominator() {
    // the denominator of the coefficient 18/7 is absorbed into the partial fraction
    check("FullSimplify((2+3*y+7*y^2)/(-1+7*y))", //
        "4/7+y+18/(-7+49*y)");
  }

  @Test
  public void testFactoredNumeratorAndNegativeConstant() {
    // a factored numerator takes the factored denominator
    check("Simplify(((2+y)*(3+y))/(2*y+7*y^2))", //
        "((2+y)*(3+y))/(y*(2+7*y))");
    // a tie goes to the flipped sign if the numerator is a negative number
    check("Simplify(-1/(-2+5*y+y^2))", //
        "1/(2-5*y-y^2)");
  }
}
