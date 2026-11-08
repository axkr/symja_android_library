package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Simplify pulls a common square root out of a sum of square roots; FullSimplify also writes two
 * square roots of the same sign as one nested root and cancels a square root common to the
 * denominator of a quotient.
 */
public class SimplifyRadicalFactorTest extends ExprEvaluatorTestCase {

  @Test
  public void testCommonSquareRoot() {
    check("Simplify(5*Sqrt(3)+7*Sqrt(15))", //
        "Sqrt(3)*(5+7*Sqrt(5))");
    check("Simplify(-6*Sqrt(3)-3*Sqrt(15))", //
        "-3*Sqrt(3)*(2+Sqrt(5))");
    check("Simplify((Sqrt(35)-4*Sqrt(5))/9)", //
        "1/9*Sqrt(5)*(-4+Sqrt(7))");
  }

  @Test
  public void testCommonSquareRootKept() {
    // the factored form -Sqrt(3)*(-5+7*Sqrt(5)) weighs more
    check("Simplify(5*Sqrt(3)-7*Sqrt(15))", //
        "5*Sqrt(3)-7*Sqrt(15)");
    check("Simplify(Sqrt(3)+Sqrt(15))", //
        "Sqrt(3)+Sqrt(15)");
  }

  @Test
  public void testNestedRadicalSum() {
    check("FullSimplify(3*Sqrt(10)+3*Sqrt(14))", //
        "6*Sqrt(6+Sqrt(35))");
    check("FullSimplify(1+3*Sqrt(10)+3*Sqrt(14))", //
        "1+6*Sqrt(6+Sqrt(35))");
    // mixed signs stay
    check("FullSimplify(3*Sqrt(10)-3*Sqrt(14))", //
        "3*(Sqrt(10)-Sqrt(14))");
  }

  @Test
  public void testDenominatorRadical() {
    check("FullSimplify((Sqrt(45)-Sqrt(21))/(Sqrt(3)+Sqrt(33)))", //
        "(-Sqrt(7)+Sqrt(15))/(1+Sqrt(11))");
  }

  @Test
  public void testDenominatorRadicalInSimplify() {
    check("Simplify((Sqrt(12)-Sqrt(15))/(Sqrt(3)+Sqrt(21)))", //
        "(2-Sqrt(5))/(1+Sqrt(7))");
  }
}
