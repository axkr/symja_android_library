package org.matheclipse.core.sympy.simplify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.List;
import org.hipparchus.complex.Complex;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Port of <a href="https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_sqrtdenest.py">test_sqrtdenest.py</a>.
 *
 * <p>
 * Symja prints the results in a different form, therefore every case checks that the result has
 * at most the nesting depth of sympy's result and the same numeric value as the input and as
 * sympy's result. The symbols <code>x, y</code> are replaced by numbers for the numeric check.
 */
public class TestSqrtDenest extends ExprEvaluatorTestCase {

  private IExpr eval(String str) {
    return evaluator.getEvalEngine().evaluate(str);
  }

  private Complex numeric(IExpr expr) {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr substituted = F.subst(expr, engine.parse("x"), F.num(0.37));
    substituted = F.subst(substituted, engine.parse("y"), F.num(1.3));
    return engine.evalN(substituted).evalfc();
  }

  private void assertSameValue(IExpr expected, IExpr actual, String message) {
    Complex c1 = numeric(expected);
    Complex c2 = numeric(actual);
    assertTrue(c1.subtract(c2).norm() <= 1e-9 * (1.0 + c1.norm()),
        message + " difference: " + c1 + " " + c2);
  }

  /** <code>assert sqrtdenest(input) == expected</code> */
  private void checkDenest(String input, String expected) {
    checkDenest(input, expected, 3);
  }

  private void checkDenest(String input, String expected, int maxIter) {
    IExpr expr = eval(input);
    IExpr expectedExpr = eval(expected);
    IExpr denested = SqrtDenest.sqrtdenest(expr, maxIter);
    String message = input + " was denested to " + denested;
    assertTrue(SqrtDenest.sqrtDepth(denested) <= SqrtDenest.sqrtDepth(expectedExpr),
        message + " expected: " + expected);
    assertSameValue(expr, denested, message);
    assertSameValue(expectedExpr, denested, message + " expected: " + expected);
  }

  /** <code>assert sqrtdenest(z) == z</code> */
  private void checkUnchanged(String input) {
    IExpr expr = eval(input);
    IExpr denested = SqrtDenest.sqrtdenest(expr);
    String message = input + " was denested to " + denested;
    assertEquals(SqrtDenest.sqrtDepth(expr), SqrtDenest.sqrtDepth(denested), message);
    assertTrue(denested.leafCount() <= eval("ExpandAll(" + input + ")").leafCount() + 2, message);
    assertSameValue(expr, denested, message);
  }

  @Test
  public void testSqrtDepth() {
    // >>> sqrt_depth(1 + sqrt(2)*(1 + sqrt(3)))
    assertEquals(1, SqrtDenest.sqrtDepth(eval("1 + Sqrt(2)*(1 + Sqrt(3))")));
    // >>> sqrt_depth(1 + sqrt(2)*sqrt(1 + sqrt(3)))
    assertEquals(2, SqrtDenest.sqrtDepth(eval("1 + Sqrt(2)*Sqrt(1 + Sqrt(3))")));
    assertEquals(0, SqrtDenest.sqrtDepth(eval("7/3")));
    assertEquals(1, SqrtDenest.sqrtDepth(eval("I")));
    // >>> is_algebraic(sqrt(2)*(3/(sqrt(7) + sqrt(5)*sqrt(2))))
    assertTrue(SqrtDenest.isAlgebraic(eval("Sqrt(2)*(3/(Sqrt(7) + Sqrt(5)*Sqrt(2)))")));
    // >>> is_algebraic(sqrt(2)*(3/(sqrt(7) + sqrt(5)*cos(2))))
    assertTrue(!SqrtDenest.isAlgebraic(eval("Sqrt(2)*(3/(Sqrt(7) + Sqrt(5)*Cos(2)))")));
  }

  @Test
  public void testSplitSurds() {
    // >>> split_surds(3*sqrt(3) + sqrt(5)/7 + sqrt(6) + sqrt(10) + sqrt(15))
    // (3, sqrt(2) + sqrt(5) + 3, sqrt(5)/7 + sqrt(10))
    IExpr expr = eval("3*Sqrt(3) + Sqrt(5)/7 + Sqrt(6) + Sqrt(10) + Sqrt(15)");
    IExpr[] split = RadSimp.splitSurds(expr);
    assertEquals("3", split[0].toString());
    assertSameValue(eval("Sqrt(2) + Sqrt(5) + 3"), split[1], split[1].toString());
    assertSameValue(eval("Sqrt(5)/7 + Sqrt(10)"), split[2], split[2].toString());
    // >>> rad_rationalize(sqrt(3), 1 + sqrt(2)/3)
    // (-sqrt(3) + sqrt(6)/3, -7/9)
    IExpr[] numDen = RadSimp.radRationalize(eval("Sqrt(3)"), eval("1 + Sqrt(2)/3"));
    assertEquals("-7/9", numDen[1].toString());
    assertSameValue(eval("-Sqrt(3) + Sqrt(6)/3"), numDen[0], numDen[0].toString());
    // >>> radsimp(1/(2 + sqrt(2)))
    // (2 - sqrt(2))/2
    assertEquals("1-1/Sqrt(2)", RadSimp.radsimp(eval("1/(2 + Sqrt(2))")).toString());
  }

  @Test
  public void testSqrtdenest() {
    // d = {sqrt(5 + 2 * r6): r2 + r3,
    checkDenest("Sqrt(5 + 2*Sqrt(6))", "Sqrt(2)+Sqrt(3)");
    // sqrt(5. + 2 * r6): sqrt(5. + 2 * r6),
    checkUnchanged("Sqrt(5. + 2*Sqrt(6))");
    // sqrt(5. + 4*sqrt(5 + 2 * r6)): sqrt(5.0 + 4*r2 + 4*r3),
    checkDenest("Sqrt(5. + 4*Sqrt(5 + 2*Sqrt(6)))", "Sqrt(5.0 + 4*Sqrt(2) + 4*Sqrt(3))");
    // sqrt(r2): sqrt(r2),
    checkUnchanged("Sqrt(Sqrt(2))");
    // sqrt(5 + r7): sqrt(5 + r7),
    checkUnchanged("Sqrt(5 + Sqrt(7))");
    // sqrt(3 + sqrt(5 + 2*r7)):
    // 3*r2*(5 + 2*r7)**Rational(1, 4)/(2*sqrt(6 + 3*r7)) +
    // r2*sqrt(6 + 3*r7)/(2*(5 + 2*r7)**Rational(1, 4)),
    checkDenest("Sqrt(3 + Sqrt(5 + 2*Sqrt(7)))",
        "3*Sqrt(2)*(5 + 2*Sqrt(7))^(1/4)/(2*Sqrt(6 + 3*Sqrt(7))) + "
            + "Sqrt(2)*Sqrt(6 + 3*Sqrt(7))/(2*(5 + 2*Sqrt(7))^(1/4))");
    // sqrt(3 + 2*r3): 3**Rational(3, 4)*(r6/2 + 3*r2/2)/3}
    checkDenest("Sqrt(3 + 2*Sqrt(3))", "3^(3/4)*(Sqrt(6)/2 + 3*Sqrt(2)/2)/3");
  }

  @Test
  public void testSqrtdenest2() {
    // assert sqrtdenest(sqrt(16 - 2*r29 + 2*sqrt(55 - 10*r29))) == r5 + sqrt(11 - 2*r29)
    checkDenest("Sqrt(16 - 2*Sqrt(29) + 2*Sqrt(55 - 10*Sqrt(29)))",
        "Sqrt(5) + Sqrt(11 - 2*Sqrt(29))");
    // e = sqrt(-r5 + sqrt(-2*r29 + 2*sqrt(-10*r29 + 55) + 16))
    // assert sqrtdenest(e) == root(-2*r29 + 11, 4)
    checkDenest("Sqrt(-Sqrt(5) + Sqrt(-2*Sqrt(29) + 2*Sqrt(-10*Sqrt(29) + 55) + 16))",
        "(-2*Sqrt(29) + 11)^(1/4)");
    // r = sqrt(1 + r7)
    // assert sqrtdenest(sqrt(1 + r)) == sqrt(1 + r)
    checkUnchanged("Sqrt(1 + Sqrt(1 + Sqrt(7)))");
    // e = sqrt(((1 + sqrt(1 + 2*sqrt(3 + r2 + r5)))**2).expand())
    // assert sqrtdenest(e) == 1 + sqrt(1 + 2*sqrt(r2 + r5 + 3))
    checkDenest("Sqrt(Expand((1 + Sqrt(1 + 2*Sqrt(3 + Sqrt(2) + Sqrt(5))))^2))",
        "1 + Sqrt(1 + 2*Sqrt(Sqrt(2) + Sqrt(5) + 3))");
    // assert sqrtdenest(sqrt(5*r3 + 6*r2)) == sqrt(2)*root(3, 4) + root(3, 4)**3
    checkDenest("Sqrt(5*Sqrt(3) + 6*Sqrt(2))", "Sqrt(2)*3^(1/4) + 3^(3/4)");
    // assert sqrtdenest(sqrt(((1 + r5 + sqrt(1 + r3))**2).expand())) == 1 + r5 + sqrt(1 + r3)
    checkDenest("Sqrt(Expand((1 + Sqrt(5) + Sqrt(1 + Sqrt(3)))^2))",
        "1 + Sqrt(5) + Sqrt(1 + Sqrt(3))");
    // assert sqrtdenest(sqrt(((1 + r5 + r7 + sqrt(1 + r3))**2).expand())) ==
    // 1 + sqrt(1 + r3) + r5 + r7
    checkDenest("Sqrt(Expand((1 + Sqrt(5) + Sqrt(7) + Sqrt(1 + Sqrt(3)))^2))",
        "1 + Sqrt(1 + Sqrt(3)) + Sqrt(5) + Sqrt(7)");
    // e = sqrt(((1 + cos(2) + cos(3) + sqrt(1 + r3))**2).expand())
    // assert sqrtdenest(e) == cos(3) + cos(2) + 1 + sqrt(1 + r3)
    checkDenest("Sqrt(Expand((1 + Cos(2) + Cos(3) + Sqrt(1 + Sqrt(3)))^2))",
        "Cos(3) + Cos(2) + 1 + Sqrt(1 + Sqrt(3))");
    // e = sqrt(-2*r10 + 2*r2*sqrt(-2*r10 + 11) + 14)
    // assert sqrtdenest(e) == sqrt(-2*r10 - 2*r2 + 4*r5 + 14)
    checkDenest("Sqrt(-2*Sqrt(10) + 2*Sqrt(2)*Sqrt(-2*Sqrt(10) + 11) + 14)",
        "Sqrt(-2*Sqrt(10) - 2*Sqrt(2) + 4*Sqrt(5) + 14)");
    // # check that the result is not more complicated than the input
    // z = sqrt(-2*r29 + cos(2) + 2*sqrt(-10*r29 + 55) + 16)
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(-2*Sqrt(29) + Cos(2) + 2*Sqrt(-10*Sqrt(29) + 55) + 16)");
    // assert sqrtdenest(sqrt(r6 + sqrt(15))) == sqrt(r6 + sqrt(15))
    checkUnchanged("Sqrt(Sqrt(6) + Sqrt(15))");
    // z = sqrt(15 - 2*sqrt(31) + 2*sqrt(55 - 10*r29))
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(15 - 2*Sqrt(31) + 2*Sqrt(55 - 10*Sqrt(29)))");
  }

  @Test
  public void testSqrtdenestRec() {
    // assert sqrtdenest(sqrt(-4*sqrt(14) - 2*r6 + 4*sqrt(21) + 33)) == -r2 + r3 + 2*r7
    checkDenest("Sqrt(-4*Sqrt(14) - 2*Sqrt(6) + 4*Sqrt(21) + 33)",
        "-Sqrt(2) + Sqrt(3) + 2*Sqrt(7)");
    // assert sqrtdenest(sqrt(-28*r7 - 14*r5 + 4*sqrt(35) + 82)) == -7 + r5 + 2*r7
    checkDenest("Sqrt(-28*Sqrt(7) - 14*Sqrt(5) + 4*Sqrt(35) + 82)", "-7 + Sqrt(5) + 2*Sqrt(7)");
    // assert sqrtdenest(sqrt(6*r2/11 + 2*sqrt(22)/11 + 6*sqrt(11)/11 + 2)) ==
    // sqrt(11)*(r2 + 3 + sqrt(11))/11
    checkDenest("Sqrt(6*Sqrt(2)/11 + 2*Sqrt(22)/11 + 6*Sqrt(11)/11 + 2)",
        "Sqrt(11)*(Sqrt(2) + 3 + Sqrt(11))/11");
    // assert sqrtdenest(sqrt(468*r3 + 3024*r2 + 2912*r6 + 19735)) == 9*r3 + 26 + 56*r6
    checkDenest("Sqrt(468*Sqrt(3) + 3024*Sqrt(2) + 2912*Sqrt(6) + 19735)",
        "9*Sqrt(3) + 26 + 56*Sqrt(6)");
    // z = sqrt(-490*r3 - 98*sqrt(115) - 98*sqrt(345) - 2107)
    // assert sqrtdenest(z) == sqrt(-1)*(7*r5 + 7*r15 + 7*sqrt(23))
    checkDenest("Sqrt(-490*Sqrt(3) - 98*Sqrt(115) - 98*Sqrt(345) - 2107)",
        "I*(7*Sqrt(5) + 7*Sqrt(15) + 7*Sqrt(23))");
    // z = sqrt(-4*sqrt(14) - 2*r6 + 4*sqrt(21) + 34)
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(-4*Sqrt(14) - 2*Sqrt(6) + 4*Sqrt(21) + 34)");
    // assert sqrtdenest(sqrt(-8*r2 - 2*r5 + 18)) == -r10 + 1 + r2 + r5
    checkDenest("Sqrt(-8*Sqrt(2) - 2*Sqrt(5) + 18)", "-Sqrt(10) + 1 + Sqrt(2) + Sqrt(5)");
    // assert sqrtdenest(sqrt(8*r2 + 2*r5 - 18)) == sqrt(-1)*(-r10 + 1 + r2 + r5)
    checkDenest("Sqrt(8*Sqrt(2) + 2*Sqrt(5) - 18)", "I*(-Sqrt(10) + 1 + Sqrt(2) + Sqrt(5))");
    // assert sqrtdenest(sqrt(8*r2/3 + 14*r5/3 + Rational(154, 9))) == -r10/3 + r2 + r5 + 3
    checkDenest("Sqrt(8*Sqrt(2)/3 + 14*Sqrt(5)/3 + 154/9)",
        "-Sqrt(10)/3 + Sqrt(2) + Sqrt(5) + 3");
    // assert sqrtdenest(sqrt(sqrt(2*r6 + 5) + sqrt(2*r7 + 8))) == sqrt(1 + r2 + r3 + r7)
    checkDenest("Sqrt(Sqrt(2*Sqrt(6) + 5) + Sqrt(2*Sqrt(7) + 8))",
        "Sqrt(1 + Sqrt(2) + Sqrt(3) + Sqrt(7))");
    // assert sqrtdenest(sqrt(4*r15 + 8*r5 + 12*r3 + 24)) == 1 + r3 + r5 + r15
    checkDenest("Sqrt(4*Sqrt(15) + 8*Sqrt(5) + 12*Sqrt(3) + 24)",
        "1 + Sqrt(3) + Sqrt(5) + Sqrt(15)");
    // w = 1 + r2 + r3 + r5 + r7
    // assert sqrtdenest(sqrt((w**2).expand())) == w
    checkDenest("Sqrt(Expand((1 + Sqrt(2) + Sqrt(3) + Sqrt(5) + Sqrt(7))^2))",
        "1 + Sqrt(2) + Sqrt(3) + Sqrt(5) + Sqrt(7)");
    // z = sqrt((w**2).expand() + 1)
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(Expand((1 + Sqrt(2) + Sqrt(3) + Sqrt(5) + Sqrt(7))^2)+1)");
    // z = sqrt(2*r10 + 6*r2 + 4*r5 + 12 + 10*r15 + 30*r3)
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(2*Sqrt(10) + 6*Sqrt(2) + 4*Sqrt(5) + 12 + 10*Sqrt(15) + 30*Sqrt(3))");
    checkDenest("Sqrt(10+2*Sqrt(6)+2*Sqrt(10)+2*Sqrt(15))", "Sqrt(2)+Sqrt(3)+Sqrt(5)");
  }

  @Test
  public void testIssue6241() {
    // z = sqrt( -320 + 32*sqrt(5) + 64*r15)
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(-320 + 32*Sqrt(5) + 64*Sqrt(15))");
  }

  @Test
  public void testSqrtdenest3() {
    // z = sqrt(13 - 2*r10 + 2*r2*sqrt(-2*r10 + 11))
    // assert sqrtdenest(z) == -1 + r2 + r10
    checkDenest("Sqrt(13 - 2*Sqrt(10) + 2*Sqrt(2)*Sqrt(-2*Sqrt(10) + 11))",
        "-1 + Sqrt(2) + Sqrt(10)");
    // assert sqrtdenest(z, max_iter=1) == -1 + sqrt(2) + sqrt(10)
    checkDenest("Sqrt(13 - 2*Sqrt(10) + 2*Sqrt(2)*Sqrt(-2*Sqrt(10) + 11))",
        "-1 + Sqrt(2) + Sqrt(10)", 1);
    // z = sqrt(sqrt(r2 + 2) + 2)
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(Sqrt(Sqrt(2) + 2) + 2)");
    // assert sqrtdenest(sqrt(-2*r10 + 4*r2*sqrt(-2*r10 + 11) + 20)) ==
    // sqrt(-2*r10 - 4*r2 + 8*r5 + 20)
    checkDenest("Sqrt(-2*Sqrt(10) + 4*Sqrt(2)*Sqrt(-2*Sqrt(10) + 11) + 20)",
        "Sqrt(-2*Sqrt(10) - 4*Sqrt(2) + 8*Sqrt(5) + 20)");
    // assert sqrtdenest(sqrt((112 + 70*r2) + (46 + 34*r2)*r5)) == r10 + 5 + 4*r2 + 3*r5
    checkDenest("Sqrt((112 + 70*Sqrt(2)) + (46 + 34*Sqrt(2))*Sqrt(5))",
        "Sqrt(10) + 5 + 4*Sqrt(2) + 3*Sqrt(5)");
    // z = sqrt(5 + sqrt(2*r6 + 5)*sqrt(-2*r29 + 2*sqrt(-10*r29 + 55) + 16))
    // r = sqrt(-2*r29 + 11)
    // assert sqrtdenest(z) == sqrt(r2*r + r3*r + r10 + r15 + 5)
    checkDenest("Sqrt(5 + Sqrt(2*Sqrt(6) + 5)*Sqrt(-2*Sqrt(29) + 2*Sqrt(-10*Sqrt(29) + 55) + 16))",
        "Sqrt(Sqrt(2)*Sqrt(-2*Sqrt(29) + 11) + Sqrt(3)*Sqrt(-2*Sqrt(29) + 11) + Sqrt(10) + Sqrt(15) + 5)");
    // n = sqrt(2*r6/7 + 2*r7/7 + 2*sqrt(42)/7 + 2)
    // d = sqrt(16 - 2*r29 + 2*sqrt(55 - 10*r29))
    // assert sqrtdenest(n/d) == r7*(1 + r6 + r7)/(Mul(7, (sqrt(-2*r29 + 11) + r5),
    // evaluate=False))
    checkDenest(
        "Sqrt(2*Sqrt(6)/7 + 2*Sqrt(7)/7 + 2*Sqrt(42)/7 + 2)/Sqrt(16 - 2*Sqrt(29) + 2*Sqrt(55 - 10*Sqrt(29)))",
        "Sqrt(7)*(1 + Sqrt(6) + Sqrt(7))/(7*(Sqrt(-2*Sqrt(29) + 11) + Sqrt(5)))");
  }

  @Test
  public void testSqrtdenest4() {
    // see Denest_en.pdf in https://github.com/sympy/sympy/issues/3192
    // z = sqrt(8 - r2*sqrt(5 - r5) - sqrt(3)*(1 + r5))
    // c = sqrt(-r5 + 5)
    // z1 = ((-r15*c - r3*c + c + r5*c - r6 - r2 + r10 + sqrt(30))/4).expand()
    // assert sqrtdenest(z) == z1
    checkDenest("Sqrt(8 - Sqrt(2)*Sqrt(5 - Sqrt(5)) - Sqrt(3)*(1 + Sqrt(5)))",
        "(-Sqrt(15)*Sqrt(-Sqrt(5) + 5) - Sqrt(3)*Sqrt(-Sqrt(5) + 5) + Sqrt(-Sqrt(5) + 5) + "
            + "Sqrt(5)*Sqrt(-Sqrt(5) + 5) - Sqrt(6) - Sqrt(2) + Sqrt(10) + Sqrt(30))/4");
    // z = sqrt(2*r2*sqrt(r2 + 2) + 5*r2 + 4*sqrt(r2 + 2) + 8)
    // assert sqrtdenest(z) == r2 + sqrt(r2 + 2) + 2
    checkDenest("Sqrt(2*Sqrt(2)*Sqrt(Sqrt(2) + 2) + 5*Sqrt(2) + 4*Sqrt(Sqrt(2) + 2) + 8)",
        "Sqrt(2) + Sqrt(Sqrt(2) + 2) + 2");
    // w = 2 + r2 + r3 + (1 + r3)*sqrt(2 + r2 + 5*r3)
    // z = sqrt((w**2).expand())
    // assert sqrtdenest(z) == w.expand()
    checkDenest(
        "Sqrt(Expand((2 + Sqrt(2) + Sqrt(3) + (1 + Sqrt(3))*Sqrt(2 + Sqrt(2) + 5*Sqrt(3)))^2))",
        "2 + Sqrt(2) + Sqrt(3) + (1 + Sqrt(3))*Sqrt(2 + Sqrt(2) + 5*Sqrt(3))");
  }

  @Test
  public void testSqrtSymbolicDenest() {
    // z = sqrt(((1 + sqrt(sqrt(2 + x) + 3))**2).expand())
    // assert sqrtdenest(z) == sqrt((1 + sqrt(sqrt(2 + x) + 3))**2)
    checkDenest("Sqrt(Expand((1 + Sqrt(Sqrt(2 + x) + 3))^2))",
        "Sqrt((1 + Sqrt(Sqrt(2 + x) + 3))^2)");
    // z = sqrt(((1 + sqrt(sqrt(2 + cos(1)) + 3))**2).expand())
    // assert sqrtdenest(z) == 1 + sqrt(sqrt(2 + cos(1)) + 3)
    checkDenest("Sqrt(Expand((1 + Sqrt(Sqrt(2 + Cos(1)) + 3))^2))",
        "1 + Sqrt(Sqrt(2 + Cos(1)) + 3)");
    // z = ((1 + cos(2))**4 + 1).expand()
    // assert sqrtdenest(z) == z
    checkUnchanged("Expand((1 + Cos(2))^4 + 1)");
    // z = sqrt(((1 + sqrt(sqrt(2 + cos(3*x)) + 3))**2 + 1).expand())
    // assert sqrtdenest(z) == z
    checkUnchanged("Sqrt(Expand((1 + Sqrt(Sqrt(2 + Cos(3*x)) + 3))^2 + 1))");
    // c = cos(3)
    // c2 = c**2
    // assert sqrtdenest(sqrt(2*sqrt(1 + r3)*c + c2 + 1 + r3*c2)) == -1 - sqrt(1 + r3)*c
    checkDenest("Sqrt(2*Sqrt(1 + Sqrt(3))*Cos(3) + Cos(3)^2 + 1 + Sqrt(3)*Cos(3)^2)",
        "-1 - Sqrt(1 + Sqrt(3))*Cos(3)");
    // ra = sqrt(1 + r3)
    // z = sqrt(20*ra*sqrt(3 + 3*r3) + 12*r3*ra*sqrt(3 + 3*r3) + 64*r3 + 112)
    // assert sqrtdenest(z) == z
    // deviation from sympy: the evaluation in Symja combines Sqrt(1+Sqrt(3))*Sqrt(3+3*Sqrt(3)) to
    // Sqrt(3)*(1+Sqrt(3)), so z == Sqrt(208+120*Sqrt(3)) which denests to 10+6*Sqrt(3)
    checkDenest("Sqrt(20*Sqrt(1 + Sqrt(3))*Sqrt(3 + 3*Sqrt(3)) + "
        + "12*Sqrt(3)*Sqrt(1 + Sqrt(3))*Sqrt(3 + 3*Sqrt(3)) + 64*Sqrt(3) + 112)", "10+6*Sqrt(3)");
  }

  @Test
  public void testIssue5857() {
    // z = sqrt(1/(4*r3 + 7) + 1)
    // ans = (r2 + r6)/(r3 + 2)
    // assert sqrtdenest(z) == ans
    checkDenest("Sqrt(1/(4*Sqrt(3) + 7) + 1)", "(Sqrt(2) + Sqrt(6))/(Sqrt(3) + 2)");
    // assert sqrtdenest(1 + z) == 1 + ans
    checkDenest("1 + Sqrt(1/(4*Sqrt(3) + 7) + 1)", "1 + (Sqrt(2) + Sqrt(6))/(Sqrt(3) + 2)");
    // assert sqrtdenest(Integral(z + 1, (x, 1, 2))) == Integral(1 + ans, (x, 1, 2))
    // Integrate would be evaluated in Symja, use an undefined function instead
    IExpr denested = SqrtDenest.sqrtdenest(eval("f(1 + Sqrt(1/(4*Sqrt(3) + 7) + 1))"));
    assertEquals("f", denested.head().toString());
    assertTrue(SqrtDenest.sqrtDepth(denested.first()) <= 1, denested.toString());
    assertSameValue(eval("1 + (Sqrt(2) + Sqrt(6))/(Sqrt(3) + 2)"), denested.first(),
        denested.toString());
    // assert sqrtdenest(x + sqrt(y)) == x + sqrt(y)
    assertEquals("x+Sqrt(y)", SqrtDenest.sqrtdenest(eval("x + Sqrt(y)")).toString());
  }

  @Test
  public void testSubsets() {
    // assert subsets(1) == [[1]]
    assertEquals("[[1]]", toString(SqrtDenest.subsets(1)));
    // assert subsets(4) == [
    // [1, 0, 0, 0], [0, 1, 0, 0], [1, 1, 0, 0], [0, 0, 1, 0], [1, 0, 1, 0],
    // [0, 1, 1, 0], [1, 1, 1, 0], [0, 0, 0, 1], [1, 0, 0, 1], [0, 1, 0, 1],
    // [1, 1, 0, 1], [0, 0, 1, 1], [1, 0, 1, 1], [0, 1, 1, 1], [1, 1, 1, 1]]
    assertEquals(
        "[[1, 0, 0, 0], [0, 1, 0, 0], [1, 1, 0, 0], [0, 0, 1, 0], [1, 0, 1, 0], "
            + "[0, 1, 1, 0], [1, 1, 1, 0], [0, 0, 0, 1], [1, 0, 0, 1], [0, 1, 0, 1], "
            + "[1, 1, 0, 1], [0, 0, 1, 1], [1, 0, 1, 1], [0, 1, 1, 1], [1, 1, 1, 1]]",
        toString(SqrtDenest.subsets(4)));
  }

  private static String toString(List<int[]> list) {
    StringBuilder buf = new StringBuilder("[");
    for (int i = 0; i < list.size(); i++) {
      if (i > 0) {
        buf.append(", ");
      }
      buf.append(Arrays.toString(list.get(i)));
    }
    return buf.append("]").toString();
  }

  @Test
  public void testIssue5653() {
    // assert sqrtdenest(sqrt(2 + sqrt(2 + sqrt(2)))) == sqrt(2 + sqrt(2 + sqrt(2)))
    checkUnchanged("Sqrt(2 + Sqrt(2 + Sqrt(2)))");
  }

  @Test
  public void testIssue12420() {
    // assert sqrtdenest((3 - sqrt(2)*sqrt(4 + 3*I) + 3*I)/2) == I
    checkDenest("(3 - Sqrt(2)*Sqrt(4 + 3*I) + 3*I)/2", "I");
    // e = 3 - sqrt(2)*sqrt(4 + I) + 3*I
    // assert sqrtdenest(e) == e
    checkUnchanged("3 - Sqrt(2)*Sqrt(4 + I) + 3*I");
  }

  @Test
  public void testSqrtRatcomb() {
    // assert sqrtdenest(sqrt(1 + r3) + sqrt(3 + 3*r3) - sqrt(10 + 6*r3)) == 0
    IExpr expr = eval("Sqrt(1 + Sqrt(3)) + Sqrt(3 + 3*Sqrt(3)) - Sqrt(10 + 6*Sqrt(3))");
    assertEquals("0", SqrtDenest.sqrtdenest(expr).toString());
  }

  @Test
  public void testIssue18041() {
    // e = -sqrt(-2 + 2*sqrt(3)*I)
    // assert sqrtdenest(e) == -1 - sqrt(3)*I
    checkDenest("-Sqrt(-2 + 2*Sqrt(3)*I)", "-1 - Sqrt(3)*I");
  }

  @Test
  public void testIssue19914() {
    // assert sqrtdenest(sqrt(-8-sqrt(63))) == sqrt(14)*I/2 + 3*sqrt(2)*I/2
    checkDenest("Sqrt(-8-Sqrt(63))", "Sqrt(14)*I/2 + 3*Sqrt(2)*I/2");
  }

  @Test
  public void testReciprocal() {
    checkDenest("1/Sqrt(5 + 2*Sqrt(6))", "Sqrt(3)-Sqrt(2)");
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
