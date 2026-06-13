package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
