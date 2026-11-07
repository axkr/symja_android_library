package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class FullSimplifyPolynomialFormsTest extends ExprEvaluatorTestCase {

  @Test
  public void testUnivariatePolynomialForms() {
    // the low order terms plus the remainder with its content, sign and power pulled out
    check("FullSimplify({2 + 7*y - 9*y^2, 5 - 2*y + 8*y^2 - 4*y^3, 3*y^2 - 2*y^3 + 7*y^4})", //
        "{2+(7-9*y)*y,5-2*y*(1+2*(-2+y)*y),y^2*(3+y*(-2+7*y))}");
    // the content times the primitive part; a factorization wins a tie with the expanded form
    check("FullSimplify({12 + 18*y + 30*y^2, 15 - 8*y + y^2, 9 + 9*y})", //
        "{6*(2+y*(3+5*y)),(-5+y)*(-3+y),9*(1+y)}");
    // nothing cheaper
    check("FullSimplify({y^3 + y^5, 1 + y + y^2 + y^3 + y^4 + y^5})", //
        "{y^3+y^5,1+y+y^2+y^3+y^4+y^5}");
  }

  @Test
  public void testUnivariateQuotientForms() {
    // numerator and denominator are searched on their own
    check("FullSimplify((6 + 6*y - 7*y^2)/(3 - 3*y + 3*y^2 - 3*y^3))", //
        "(-6+y*(-6+7*y))/(3*(-1+y)*(1+y^2))");
    // the partial fractions are cheaper, with the factor 1/2 inside the denominator
    check("FullSimplify((3 + 6*y - 7*y^2)/(2 - 2*y + 2*y^2 - 2*y^3))", //
        "1/(2-2*y)+(1+4*y)/(1+y^2)");
    check("FullSimplify((2 + 5*y - y^2 - y^3)/(-3 + y))", //
        "((2-y)*(1+y*(3+y)))/(-3+y)");
    // a constant numerator or a second variable leaves the quotient alone
    check("FullSimplify({-1/(-5 + y), 1/(3*y^2 - 3*y^3), 1/(y^2 - 4*z^2)})", //
        "{1/(5-y),1/(3*y^2-3*y^3),1/(y^2-4*z^2)}");
    // Simplify keeps the expanded forms
    check("Simplify({2 + 7*y - 9*y^2, 15 - 8*y + y^2})", //
        "{2+7*y-9*y^2,15-8*y+y^2}");
  }
}
