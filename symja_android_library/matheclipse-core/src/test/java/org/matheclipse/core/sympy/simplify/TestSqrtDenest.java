package org.matheclipse.core.sympy.simplify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestSqrtDenest extends ExprEvaluatorTestCase {

  private IExpr eval(String str) {
    return evaluator.getEvalEngine().evaluate(str);
  }

  /**
   * Check that the input is denested to an expression with the nesting depth
   * <code>expectedDepth</code>, which has the same numeric value as the input and as the expected
   * expression.
   */
  private void checkDenest(String input, String expected, int expectedDepth) {
    IExpr expr = eval(input);
    IExpr denested = SqrtDenest.sqrtdenest(expr);
    assertEquals(expectedDepth, SqrtDenest.sqrtDepth(denested),
        input + " was denested to " + denested);
    assertZero(F.Subtract(expr, denested), input + " was denested to " + denested);
    assertZero(F.Subtract(eval(expected), denested), input + " was denested to " + denested);
  }

  private void assertZero(IExpr difference, String message) {
    org.hipparchus.complex.Complex value = evaluator.getEvalEngine().evalN(difference).evalfc();
    assertTrue(value.norm() < 1e-9, message + " difference: " + value);
  }

  private void checkUnchanged(String input) {
    IExpr expr = eval(input);
    IExpr denested = SqrtDenest.sqrtdenest(expr);
    assertEquals(SqrtDenest.sqrtDepth(expr), SqrtDenest.sqrtDepth(denested),
        input + " was denested to " + denested);
    assertZero(F.Subtract(expr, denested), input + " was denested to " + denested);
  }

  @Test
  public void testSqrtDepth() {
    // >>> sqrt_depth(1 + sqrt(2)*(1 + sqrt(3)))
    assertEquals(1, SqrtDenest.sqrtDepth(eval("1 + Sqrt(2)*(1 + Sqrt(3))")));
    // >>> sqrt_depth(1 + sqrt(2)*sqrt(1 + sqrt(3)))
    assertEquals(2, SqrtDenest.sqrtDepth(eval("1 + Sqrt(2)*Sqrt(1 + Sqrt(3))")));
    assertEquals(0, SqrtDenest.sqrtDepth(eval("7/3")));
    assertEquals(1, SqrtDenest.sqrtDepth(eval("I")));
    assertTrue(SqrtDenest.isAlgebraic(eval("Sqrt(2)*(3/(Sqrt(7) + Sqrt(5)*Sqrt(2)))")));
    assertTrue(!SqrtDenest.isAlgebraic(eval("Sqrt(2)*(3/(Sqrt(7) + Sqrt(5)*Cos(2)))")));
  }

  @Test
  public void testSplitSurds() {
    // >>> split_surds(3*sqrt(3) + sqrt(5)/7 + sqrt(6) + sqrt(10) + sqrt(15))
    // (3, sqrt(2) + sqrt(5) + 3, sqrt(5)/7 + sqrt(10))
    IExpr expr = eval("3*Sqrt(3) + Sqrt(5)/7 + Sqrt(6) + Sqrt(10) + Sqrt(15)");
    IExpr[] split = RadSimp.splitSurds(expr);
    assertEquals("3", split[0].toString());
    assertZero(F.Subtract(split[1], eval("Sqrt(2) + Sqrt(5) + 3")), split[1].toString());
    assertZero(F.Subtract(split[2], eval("Sqrt(5)/7 + Sqrt(10)")), split[2].toString());
    // >>> rad_rationalize(sqrt(3), 1 + sqrt(2)/3)
    // (-sqrt(3) + sqrt(6)/3, -7/9)
    IExpr[] numDen = RadSimp.radRationalize(eval("Sqrt(3)"), eval("1 + Sqrt(2)/3"));
    assertEquals("-7/9", numDen[1].toString());
    assertZero(F.Subtract(numDen[0], eval("-Sqrt(3) + Sqrt(6)/3")), numDen[0].toString());
    // >>> radsimp(1/(2 + sqrt(2)))
    // (2 - sqrt(2))/2
    assertEquals("1-1/Sqrt(2)", RadSimp.radsimp(eval("1/(2 + Sqrt(2))")).toString());
  }

  @Test
  public void testSqrtDenest() {
    // https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_sqrtdenest.py
    // d = {sqrt(5 + 2 * r6): r2 + r3,
    checkDenest("Sqrt(5 + 2*Sqrt(6))", "Sqrt(2)+Sqrt(3)", 1);
    // sqrt(5. + 2 * r6): sqrt(5. + 2 * r6),
    // sqrt(5. + 4*sqrt(5 + 2 * r6)): sqrt(5.0 + 4*r2 + 4*r3),
    // sqrt(r2): sqrt(r2),
    checkUnchanged("Sqrt(Sqrt(2))");
    // sqrt(5 + r7): sqrt(5 + r7),
    checkUnchanged("Sqrt(5 + Sqrt(7))");
    // sqrt(3 + sqrt(5 + 2*r7)): 3*r2*(5 + 2*r7)**Rational(1, 4)/(2*sqrt(6 + 3*r7)) +
    // r2*sqrt(6 + 3*r7)/(2*(5 + 2*r7)**Rational(1, 4)),
    // sqrt(3 + 2*r3): 3**Rational(3, 4)*(r6/2 + 3*r2/2)/3}
    checkDenest("Sqrt(3 + 2*Sqrt(3))", "3^(3/4)*(Sqrt(6)/2 + 3*Sqrt(2)/2)/3", 1);

    // assert sqrtdenest(sqrt(16 - 2*r29 + 2*sqrt(55 - 10*r29))) == r5 + sqrt(11 - 2*r29)
    checkDenest("Sqrt(16 - 2*Sqrt(29) + 2*Sqrt(55 - 10*Sqrt(29)))",
        "Sqrt(5) + Sqrt(11 - 2*Sqrt(29))", 2);
    // r = sqrt(1 + r7)
    // assert sqrtdenest(sqrt(1 + r)) == sqrt(1 + r)
    checkUnchanged("Sqrt(1 + Sqrt(1 + Sqrt(7)))");
    // assert sqrtdenest(sqrt(5*r3 + 6*r2)) == sqrt(2)*root(3, 4) + root(3, 4)**3
    checkDenest("Sqrt(5*Sqrt(3) + 6*Sqrt(2))", "Sqrt(2)*3^(1/4) + 3^(3/4)", 1);
    // assert sqrtdenest(sqrt(r6 + sqrt(15))) == sqrt(r6 + sqrt(15))
    checkUnchanged("Sqrt(Sqrt(6) + Sqrt(15))");
  }

  @Test
  public void testSqrtDenestRec() {
    // assert sqrtdenest(sqrt(-4*sqrt(14) - 2*r6 + 4*sqrt(21) + 33)) == -r2 + r3 + 2*r7
    checkDenest("Sqrt(-4*Sqrt(14) - 2*Sqrt(6) + 4*Sqrt(21) + 33)", "-Sqrt(2) + Sqrt(3) + 2*Sqrt(7)",
        1);
    // assert sqrtdenest(sqrt(-28*r7 - 14*r5 + 4*sqrt(35) + 82)) == -7 + r5 + 2*r7
    checkDenest("Sqrt(-28*Sqrt(7) - 14*Sqrt(5) + 4*Sqrt(35) + 82)", "-7 + Sqrt(5) + 2*Sqrt(7)", 1);
    // assert sqrtdenest(sqrt(6*r2/11 + 2*sqrt(22)/11 + 6*sqrt(11)/11 + 2)) ==
    // sqrt(11)*(r2 + 3 + sqrt(11))/11
    checkDenest("Sqrt(6*Sqrt(2)/11 + 2*Sqrt(22)/11 + 6*Sqrt(11)/11 + 2)",
        "Sqrt(11)*(Sqrt(2) + 3 + Sqrt(11))/11", 1);
    // assert sqrtdenest(sqrt(468*r3 + 3024*r2 + 2912*r6 + 19735)) == 9*r3 + 26 + 56*r6
    checkDenest("Sqrt(468*Sqrt(3) + 3024*Sqrt(2) + 2912*Sqrt(6) + 19735)",
        "9*Sqrt(3) + 26 + 56*Sqrt(6)", 1);
    // z = sqrt(-490*r3 - 98*sqrt(115) - 98*sqrt(345) - 2107)
    // assert sqrtdenest(z) == sqrt(-1)*(7*r5 + 7*r15 + 7*sqrt(23))
    checkDenest("Sqrt(-490*Sqrt(3) - 98*Sqrt(115) - 98*Sqrt(345) - 2107)",
        "I*(7*Sqrt(5) + 7*Sqrt(15) + 7*Sqrt(23))", 1);
    // z = sqrt(-4*sqrt(14) - 2*r6 + 4*sqrt(21) + 34)
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(-4*Sqrt(14) - 2*Sqrt(6) + 4*Sqrt(21) + 34)");
    // assert sqrtdenest(sqrt(-8*r2 - 2*r5 + 18)) == -r10 + 1 + r2 + r5
    checkDenest("Sqrt(-8*Sqrt(2) - 2*Sqrt(5) + 18)", "-Sqrt(10) + 1 + Sqrt(2) + Sqrt(5)", 1);
    // assert sqrtdenest(sqrt(8*r2 + 2*r5 - 18)) == sqrt(-1)*(-r10 + 1 + r2 + r5)
    checkDenest("Sqrt(8*Sqrt(2) + 2*Sqrt(5) - 18)", "I*(-Sqrt(10) + 1 + Sqrt(2) + Sqrt(5))", 1);
    // assert sqrtdenest(sqrt(8*r2/3 + 14*r5/3 + Rational(154, 9))) ==
    // -r10/3 + r2 + r5 + 3
    checkDenest("Sqrt(8*Sqrt(2)/3 + 14*Sqrt(5)/3 + 154/9)", "-Sqrt(10)/3 + Sqrt(2) + Sqrt(5) + 3",
        1);
    // assert sqrtdenest(sqrt(sqrt(2*r6 + 5) + sqrt(2*r7 + 8))) ==
    // sqrt(1 + r2 + r3 + r7)
    checkDenest("Sqrt(Sqrt(2*Sqrt(6) + 5) + Sqrt(2*Sqrt(7) + 8))",
        "Sqrt(1 + Sqrt(2) + Sqrt(3) + Sqrt(7))", 2);
    // assert sqrtdenest(sqrt(4*r15 + 8*r5 + 12*r3 + 24)) == 1 + r3 + r5 + r15
    checkDenest("Sqrt(4*Sqrt(15) + 8*Sqrt(5) + 12*Sqrt(3) + 24)",
        "1 + Sqrt(3) + Sqrt(5) + Sqrt(15)", 1);
    checkDenest("Sqrt(10+2*Sqrt(6)+2*Sqrt(10)+2*Sqrt(15))", "Sqrt(2)+Sqrt(3)+Sqrt(5)", 1);

    // w = 1 + r2 + r3 + r5 + r7
    // assert sqrtdenest(sqrt((w**2).expand())) == w
    checkDenest("Sqrt(Expand((1 + Sqrt(2) + Sqrt(3) + Sqrt(5) + Sqrt(7))^2))",
        "1 + Sqrt(2) + Sqrt(3) + Sqrt(5) + Sqrt(7)", 1);
    // z = sqrt((w**2).expand() + 1)
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(Expand((1 + Sqrt(2) + Sqrt(3) + Sqrt(5) + Sqrt(7))^2)+1)");
    // z = sqrt(2*r10 + 6*r2 + 4*r5 + 12 + 10*r15 + 30*r3)
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(2*Sqrt(10) + 6*Sqrt(2) + 4*Sqrt(5) + 12 + 10*Sqrt(15) + 30*Sqrt(3))");
  }

  @Test
  public void testSqrtDenest2() {
    // z = sqrt(13 - 2*r10 + 2*r2*sqrt(-2*r10 + 11))
    // assert sqrtdenest(z) == -1 + r2 + r10
    checkDenest("Sqrt(13 - 2*Sqrt(10) + 2*Sqrt(2)*Sqrt(-2*Sqrt(10) + 11))",
        "-1 + Sqrt(2) + Sqrt(10)", 1);
    // z = sqrt(sqrt(r2 + 2) + 2)
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(Sqrt(Sqrt(2) + 2) + 2)");
    // assert sqrtdenest(sqrt(-2*r10 + 4*r2*sqrt(-2*r10 + 11) + 20)) ==
    // sqrt(-2*r10 - 4*r2 + 8*r5 + 20)
    checkDenest("Sqrt(-2*Sqrt(10) + 4*Sqrt(2)*Sqrt(-2*Sqrt(10) + 11) + 20)",
        "Sqrt(-2*Sqrt(10) - 4*Sqrt(2) + 8*Sqrt(5) + 20)", 2);
    // assert sqrtdenest(sqrt((112 + 70*r2) + (46 + 34*r2)*r5)) ==
    // r10 + 5 + 4*r2 + 3*r5
    checkDenest("Sqrt((112 + 70*Sqrt(2)) + (46 + 34*Sqrt(2))*Sqrt(5))",
        "Sqrt(10) + 5 + 4*Sqrt(2) + 3*Sqrt(5)", 1);
    // z = sqrt(2*r2*sqrt(r2 + 2) + 5*r2 + 4*sqrt(r2 + 2) + 8)
    // assert sqrtdenest(z) == r2 + sqrt(r2 + 2) + 2
    checkDenest("Sqrt(2*Sqrt(2)*Sqrt(Sqrt(2) + 2) + 5*Sqrt(2) + 4*Sqrt(Sqrt(2) + 2) + 8)",
        "Sqrt(2) + Sqrt(Sqrt(2) + 2) + 2", 2);
  }

  @Test
  public void testSqrtRatcomb() {
    // assert sqrtdenest(sqrt(1 + r3) + sqrt(3 + 3*r3) - sqrt(10 + 6*r3)) == 0
    IExpr expr = eval("Sqrt(1 + Sqrt(3)) + Sqrt(3 + 3*Sqrt(3)) - Sqrt(10 + 6*Sqrt(3))");
    assertEquals("0", SqrtDenest.sqrtdenest(expr).toString());
  }

  @Test
  public void testReciprocal() {
    checkDenest("1/Sqrt(5 + 2*Sqrt(6))", "Sqrt(3)-Sqrt(2)", 1);
  }

  @Test
  public void testFullSimplify() {
    check("FullSimplify(Sqrt(10+2*Sqrt(6)+2*Sqrt(10)+2*Sqrt(15)))", //
        "Sqrt(2)+Sqrt(3)+Sqrt(5)");
    check("FullSimplify(Sqrt(12+2*Sqrt(6)+2*Sqrt(14)+2*Sqrt(21)))", //
        "Sqrt(2)+Sqrt(3)+Sqrt(7)");
    check("FullSimplify(Sqrt(16 - 2*Sqrt(29) + 2*Sqrt(55 - 10*Sqrt(29))))", //
        "Sqrt(5)+Sqrt(11-2*Sqrt(29))");
    check("FullSimplify(Sqrt(5+2*Sqrt(6)))", //
        "Sqrt(2)+Sqrt(3)");
    check("FullSimplify(Sqrt(1+Sqrt(3)))", //
        "Sqrt(1+Sqrt(3))");
    check("FullSimplify(Sqrt(x+Sqrt(x^2-1)))", //
        "Sqrt(x+Sqrt(-1+x^2))");
  }
}
