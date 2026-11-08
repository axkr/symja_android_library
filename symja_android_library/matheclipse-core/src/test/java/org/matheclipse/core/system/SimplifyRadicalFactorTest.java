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
    // Sqrt(3)*(5-7*Sqrt(5)) is strictly cheaper, -Sqrt(3)*(-5+7*Sqrt(5)) would weigh more
    check("Simplify(5*Sqrt(3)-7*Sqrt(15))", //
        "Sqrt(3)*(5-7*Sqrt(5))");
    check("Simplify(7*Sqrt(2)-9*Sqrt(14))", //
        "Sqrt(2)*(7-9*Sqrt(7))");
    // both factored forms weigh at least as much as the sum
    check("Simplify(4*Sqrt(3)-9*Sqrt(33))", //
        "4*Sqrt(3)-9*Sqrt(33)");
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

  @Test
  public void testSignedNumericContent() {
    // -2*(-7+Sqrt(3)) weighs as much as the sum, the last radical of the sum is positive
    check("Simplify(14-2*Sqrt(3))", //
        "-2*(-7+Sqrt(3))");
    check("FullSimplify(14-2*Sqrt(3))", //
        "-2*(-7+Sqrt(3))");
    check("Simplify(12-2*Sqrt(5)-4*Sqrt(7))", //
        "-2*(-6+Sqrt(5)+2*Sqrt(7))");
    // -5*(-1+Sqrt(3)) weighs more
    check("Simplify(5-5*Sqrt(3))", //
        "5-5*Sqrt(3)");
  }

  @Test
  public void testNegativeUnitCofactor() {
    // -(Sqrt(5)*(-2+Sqrt(11))) weighs as much as the sum and evaluates to Sqrt(5)*(2-Sqrt(11))
    check("Simplify(2*Sqrt(5)-Sqrt(55))", //
        "Sqrt(5)*(2-Sqrt(11))");
    check("Simplify(Sqrt(5)*(2-Sqrt(11)))", //
        "Sqrt(5)*(2-Sqrt(11))");
    check("Simplify(2*Sqrt(5)-3*Sqrt(55))", //
        "2*Sqrt(5)-3*Sqrt(55)");
  }

  @Test
  public void testFractionRadicands() {
    check("Simplify(Sqrt(12)-5*Sqrt(17/3))", //
        "(6-5*Sqrt(17))/Sqrt(3)");
    check("Simplify((5*Sqrt(3)-Sqrt(21))/Sqrt(19))", //
        "Sqrt(3/19)*(5-Sqrt(7))");
  }

  @Test
  public void testNumericQuotientForms() {
    // the rationalized form Sqrt(7)*(4-Sqrt(15)) weighs the same
    check("Simplify(Sqrt(7)/(4+Sqrt(15)))", //
        "Sqrt(7)/(4+Sqrt(15))");
    check("Simplify(1/(3+Sqrt(12)))", //
        "-1+2/Sqrt(3)");
    check("Simplify((-2-Sqrt(3))/Sqrt(11))", //
        "-(2+Sqrt(3))/Sqrt(11)");
  }

  @Test
  public void testNestedRadicalOppositeSigns() {
    check("FullSimplify(4*Sqrt(3)-Sqrt(2))", //
        "Sqrt(50-8*Sqrt(6))");
    // the negative nested root weighs more
    check("FullSimplify(Sqrt(2)-4*Sqrt(3))", //
        "Sqrt(2)-4*Sqrt(3)");
    // the expanded radicand doesn't win a tie with the sum
    check("FullSimplify(3*Sqrt(7)-Sqrt(5))", //
        "-Sqrt(5)+3*Sqrt(7)");
    // a tie without a content keeps the sum
    check("FullSimplify(2*Sqrt(5)-Sqrt(3))", //
        "-Sqrt(3)+2*Sqrt(5)");
    // a tie of the same sign goes to the nested root
    check("FullSimplify(2*Sqrt(3)+3*Sqrt(6))", //
        "Sqrt(66+36*Sqrt(2))");
    // and the denested sum doesn't take it back
    check("FullSimplify(Sqrt(66+36*Sqrt(2)))", //
        "Sqrt(66+36*Sqrt(2))");
    check("Simplify(2*Sqrt(3)+3*Sqrt(6))", //
        "Sqrt(3)*(2+3*Sqrt(2))");
  }

  @Test
  public void testNestedRadicalContent() {
    // a content which is not a square stays a factor of the radicand if that weighs no more
    check("FullSimplify(3*Sqrt(3)-Sqrt(15))", //
        "Sqrt(6*(7-3*Sqrt(5)))");
    check("FullSimplify(4*Sqrt(3)-Sqrt(6))", //
        "Sqrt(6*(9-4*Sqrt(2)))");
    // 14*(3-2*Sqrt(2)) weighs more than 42-28*Sqrt(2); the expanded radicand wins the tie with
    // Sqrt(7)*(2-Sqrt(2))
    check("FullSimplify(2*Sqrt(7)-Sqrt(14))", //
        "Sqrt(42-28*Sqrt(2))");
    check("FullSimplify(Sqrt(14*(3-2*Sqrt(2))))", //
        "Sqrt(42-28*Sqrt(2))");
    check("FullSimplify(Sqrt(42-28*Sqrt(2)))", //
        "Sqrt(42-28*Sqrt(2))");
  }

  @Test
  public void testSimplifyKeepsNestedRadical() {
    // Simplify doesn't denest, even where that is cheaper; FullSimplify does
    check("Simplify(Sqrt(66+36*Sqrt(2)))", //
        "Sqrt(66+36*Sqrt(2))");
    check("Simplify(Sqrt(7-4*Sqrt(3)))", //
        "Sqrt(7-4*Sqrt(3))");
    check("FullSimplify(Sqrt(7-4*Sqrt(3)))", //
        "2-Sqrt(3)");
    check("Simplify(Sqrt(11-6*Sqrt(2)))", //
        "Sqrt(11-6*Sqrt(2))");
    // the root of a square is no denesting
    check("Simplify(Sqrt((2+Sqrt(3))^2))", //
        "2+Sqrt(3)");
    // a power of a sum of square roots is expanded on a tie
    check("Simplify((2+Sqrt(3))^2)", //
        "7+4*Sqrt(3)");
    check("Simplify((1+x)^2)", //
        "(1+x)^2");
  }
}
