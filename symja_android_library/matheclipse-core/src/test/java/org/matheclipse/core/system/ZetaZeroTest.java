package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/** JUnit Jupiter tests for the {@code ZetaZero} function. */
public class ZetaZeroTest {

  @BeforeEach
  public void setUp() throws InterruptedException {
    // wait for initialization of the built-in rules
    F.await();
  }

  @Test
  public void testFirstZeroDouble() {
    ExprEvaluator eval = new ExprEvaluator();
    IExpr result = eval.eval("N(ZetaZero(1))");
    assertEquals(0.5, result.re().evalf(), 1e-6);
    assertEquals(14.134725, result.im().evalf(), 1e-4);
  }

  @Test
  public void testThirdZeroDouble() {
    ExprEvaluator eval = new ExprEvaluator();
    IExpr result = eval.eval("N(ZetaZero(3))");
    assertEquals(0.5, result.re().evalf(), 1e-6);
    assertEquals(25.010858, result.im().evalf(), 1e-4);
  }

  @Test
  public void testZeroAboveLowerBound() {
    // first zero with imaginary part greater than 20 is the second zero t2 = 21.022...
    ExprEvaluator eval = new ExprEvaluator();
    IExpr result = eval.eval("N(ZetaZero(1, 20))");
    assertEquals(0.5, result.re().evalf(), 1e-6);
    assertEquals(21.022040, result.im().evalf(), 1e-4);
  }

  @Test
  public void testArbitraryPrecision() {
    ExprEvaluator eval = new ExprEvaluator();
    IExpr result = eval.eval("N(ZetaZero(1), 20)");
    assertEquals(0.5, result.re().evalf(), 1e-15);
    assertEquals(14.134725141734693790, result.im().evalf(), 1e-9);
  }

  /**
   * The Lehmer pair #6709/#6710 lies 0.038 apart, closer than a scan step: counting between good
   * Gram points has to find both, not the neighbours #6708/#6711. #3206 and #126/#127 are zeros off
   * their Gram interval. The values are Mathematica's.
   */
  @Test
  public void testZerosOffTheirGramInterval() {
    ExprEvaluator eval = new ExprEvaluator();
    double[][] zeros = {{126, 279.22925092774518923}, {127, 282.46511476505209623},
        {3206, 3737.4267027849502652}, {6709, 7005.0628661749205814},
        {6710, 7005.1005646726467216}};
    for (double[] zero : zeros) {
      IExpr result = eval.eval("N(ZetaZero(" + (int) zero[0] + "))");
      assertEquals(zero[1], result.im().evalf(), 1e-10 * zero[1], "ZetaZero(" + (int) zero[0] + ")");
    }
  }

  @Test
  public void testTwentyDigits() {
    ExprEvaluator eval = new ExprEvaluator();
    // Mathematica: N[ZetaZero[6710], 20] = 0.5 + 7005.1005646726467216 I
    assertEquals("True", eval.eval(
        "Abs(Im(N(ZetaZero(6710), 20)) - 7005.1005646726467216`25) < 10^-15").toString());
    assertEquals("True", eval.eval(
        "Abs(Im(N(ZetaZero(10000), 20)) - 9877.7826540055011428`25) < 10^-15").toString());
  }

  /** Consecutive zeros increase, and none of them is skipped or found twice. */
  @Test
  public void testConsecutiveZerosIncrease() {
    ExprEvaluator eval = new ExprEvaluator();
    double previous = 0.0;
    for (int k = 6707; k <= 6712; k++) {
      double t = eval.eval("Im(N(ZetaZero(" + k + ")))").evalf();
      assertTrue(t > previous + 0.01, "ZetaZero(" + k + ") = " + t + " after " + previous);
      previous = t;
    }
  }

  /**
   * Mathematica: ZetaZero(-k) is the conjugate of the k-th zero; a zero or non-integer index gets
   * the intnz message and stays unevaluated.
   */
  @Test
  public void testNegativeIndexIsTheConjugateZero() {
    ExprEvaluator eval = new ExprEvaluator();
    IExpr result = eval.eval("N(ZetaZero(-1))");
    assertEquals(0.5, result.re().evalf(), 1e-15);
    assertEquals(-14.134725141734695, result.im().evalf(), 1e-12);
    assertEquals("True",
        eval.eval("Abs(Im(N(ZetaZero(-3), 20)) + 25.010857580145688763`25) < 10^-15").toString());
    assertEquals("ZetaZero(3.0)", eval.eval("ZetaZero(3.0)").toString());
    assertEquals("ZetaZero(0)", eval.eval("N(ZetaZero(0))").toString());
  }

  @Test
  public void testSymbolicStaysUnevaluated() {
    ExprEvaluator eval = new ExprEvaluator();
    // without N(...) the expression should remain symbolic
    assertEquals("ZetaZero(1)", eval.eval("ZetaZero(1)").toString());
    // k == 0 and negative k stay unevaluated
    assertEquals("ZetaZero(0)", eval.eval("ZetaZero(0)").toString());
    assertEquals("ZetaZero(-1)", eval.eval("ZetaZero(-1)").toString());
  }
}
