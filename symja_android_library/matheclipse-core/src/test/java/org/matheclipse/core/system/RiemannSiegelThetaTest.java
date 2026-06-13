package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/** JUnit Jupiter tests for the {@code RiemannSiegelTheta} function. */
public class RiemannSiegelThetaTest {

  @BeforeEach
  public void setUp() throws InterruptedException {
    // wait for initialization of the built-in rules
    F.await();
  }

  @Test
  public void testZeroIsExact() {
    ExprEvaluator eval = new ExprEvaluator();
    assertEquals("0", eval.eval("RiemannSiegelTheta(0)").toString());
  }

  @Test
  public void testRealDouble() {
    ExprEvaluator eval = new ExprEvaluator();
    // theta(10) approx -3.067069
    assertEquals(-3.067069, eval.eval("N(RiemannSiegelTheta(10))").evalf(), 1e-4);
  }

  @Test
  public void testArbitraryPrecision() {
    ExprEvaluator eval = new ExprEvaluator();
    IExpr result = eval.eval("N(RiemannSiegelTheta(10), 20)");
    assertEquals(-3.0670743962898954, result.evalf(), 1e-9);
  }

  @Test
  public void testSymbolicStaysUnevaluated() {
    ExprEvaluator eval = new ExprEvaluator();
    assertEquals("RiemannSiegelTheta(t)", eval.eval("RiemannSiegelTheta(t)").toString());
  }
}
