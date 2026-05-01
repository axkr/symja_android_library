package org.matheclipse.core.reflection.system;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

public class AsymptoticDSolveValueTest {

  private ExprEvaluator util;

  @Before
  public void setUp() {
    // Initialize the standard Symja expression evaluator
    util = new ExprEvaluator(false, (short) 100);
  }

  /**
   * Helper method to evaluate an input string and assert it matches the expected canonical output.
   */
  private void check(String input, String expected) {
    // Reset constant counter to ensure deterministic output for arbitrary constants
    util.getEvalEngine().setConstantCounter(1);

    IExpr result = util.eval(input);
    assertEquals(expected, result.toString());
  }

  @Test
  public void testFirstOrderExponential() {
    // y'(x) = y(x) with y(0) = 1
    // The solution is E^x, so the series is 1 + x + x^2/2 + x^3/6
    check("AsymptoticDSolveValue({y'(x) == y(x), y(0) == 1}, y(x), {x, 0, 3})", //
        "1+x+x^2/2+x^3/6");
  }

  @Test
  public void testFirstOrderGeneralConstant() {
    // y'(x) = 2*y(x), no initial conditions.
    // The solution is C(1)*E^(2x).
    check("AsymptoticDSolveValue(y'(x) == 2*y(x), y(x), {x, 0, 2})", //
        "(1+2*x+2*x^2)*C(1)");
  }

  @Test
  public void testSecondOrderHarmonicOscillator() {
    // y''(x) + y(x) = 0 with y(0) = 0, y'(0) = 1
    // The solution is Sin(x), so the series is x - x^3/6 + x^5/120
    check("AsymptoticDSolveValue({y''(x) + y(x) == 0, y(0) == 0, y'(0) == 1}, y(x), {x, 0, 5})", //
        "x-x^3/6+x^5/120");

    // y''(x) + y(x) = 0 with y(0) = 1, y'(0) = 0
    // The solution is Cos(x), so the series is 1 - x^2/2 + x^4/24
    check("AsymptoticDSolveValue({y''(x) + y(x) == 0, y(0) == 1, y'(0) == 0}, y(x), {x, 0, 4})", //
        "1-x^2/2+x^4/24");

    check("AsymptoticDSolveValue({y''(x) + y(x) == 0, y(0) == 1, y'(0) == 0}, y(x), {x, -2,2})", //
        "-Cos(2)-2*x*Cos(2)-1/2*x^2*Cos(2)+2*Sin(2)+x*Sin(2)");
  }

  @Test
  public void testNonHomogeneousEquation() {
    // y'(x) + y(x) = 1 with y(0) = 0
    // The exact solution is 1 - E^(-x).
    // The series is x - x^2/2 + x^3/6
    check("AsymptoticDSolveValue({y'(x) + y(x) == 1, y(0) == 0}, y(x), {x, 0, 3})", //
        "x-x^2/2+x^3/6");
  }

  @Test
  public void testExpansionPointOffset1() {
    // Expand around x = 1 instead of x = 0
    // y'(x) = y(x), y(1) = 2
    check("AsymptoticDSolveValue({y'(x) == y(x), y(1) == 2}, y(x), {x, 1, 2})", //
        "1+x^2");
  }

  @Test
  public void testExpansionPointOffset2() {
    // Expand around x = 1 instead of x = 0
    // y'(x) = y(x), y(1) = 2
    check(
        "AsymptoticDSolveValue({y''(x)+2*y(x)==0,y(3)==1,y'(3)==0},y(x),{x, 3, 10})", //
        "-181/350-141/140*x-x^2/280+32/35*x^3-13/30*x^4+7/50*x^5-11/180*x^6+2/105*x^7-x^8/\n"
            + "315+x^9/3780-x^10/113400");
  }

}
