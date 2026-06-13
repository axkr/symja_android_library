package org.matheclipse.core.reflection.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

public class NDTest {

  private ExprEvaluator util;

  @BeforeEach
  public void setUp() {
    // Initialize the evaluator before each test
    util = new ExprEvaluator(false, (short) 100);
  }

  /**
   * Helper method to evaluate an input string and assert it matches the expected canonical output.
   */
  private void check(String input, String expected) {
    // Reset constant counter before each evaluation so C(1), C(2) are deterministic
    util.getEvalEngine().setConstantCounter(1);

    IExpr result = util.eval(input);
    assertEquals(expected, result.toString());
  }

  @Test
  public void testND() {

    check("ND(Sin(x), x, Pi*I)", //
        "(11.59195)");
    check("ND(Cos(I*x), x, 1 + I)", //
        "(0.634964+I*1.29846)");

    check("ND(Exp(x), x, 1)", //
        "2.71828");
    check("ND(Sin(x),x,0.0)", //
        "1.0");
    check("ND(Cos(x)^3, {x,2}, 0)", //
        "-3.0");

    check("ND(Cos(x)^3, {x,2}, 1)", //
        "1.82226");
    check("ND(BesselY(10.0,x), x, 1)", //
        "1.2094*10^9");

    check("ND(x^2, x, 1+I)", //
        "(2.0+I*2.0)");
    check("ND(x^3, x,2.0)", //
        "12.0");
    check("ND(x^3, {x,2}, 1+I)", //
        "(6.0+I*6.0)");
  }

  @Test
  public void testHigherOrder() {
    // 3rd derivative of x^4 at x = 2.0
    // d^3/dx^3 (x^4) = 24x => 24 * 2.0 = 48.0
    check("ND(x^4, {x, 3}, 2.0)", //
        "48.0");

    // 3rd derivative of Sin(x) at x = 0.0
    // d^3/dx^3 (Sin(x)) = -Cos(x) => -Cos(0) = -1.0
    check("ND(Sin(x), {x, 3}, 0.0)", //
        "-1.0");

    // 4th derivative of x^5 at x = 1.0
    // d^4/dx^4 (x^5) = 120x => 120 * 1.0 = 120.0
    check("ND(x^5, {x, 4}, 1.0)", //
        "120.0");

    // --- Complex Higher Order Tests ---

    // 3rd derivative of x^4 at x = 1 + I
    // d^3/dx^3 (x^4) = 24x => 24(1+I) = 24.0 + I*24.0
    check("ND(x^4, {x, 3}, 1+I)", //
        "(24.0+I*24.0)");

    // 4th derivative of Exp(x) at x = I
    // d^4/dx^4 (Exp(x)) = Exp(x) => Exp(I) = Cos(1) + I*Sin(1)
    // Cos(1) ~ 0.540302, Sin(1) ~ 0.841471
    check("ND(Exp(x), {x, 4}, I)", //
        "(0.540302+I*0.841471)");

    // 3rd derivative of Sin(x) at x = I
    // d^3/dx^3 (Sin(x)) = -Cos(x) => -Cos(I) = -Cosh(1) ~ -1.54308
    check("ND(Sin(x), {x, 3}, I)", //
        "(-1.54308-I*3.19744*10^-14)");
  }

  @Test
  public void testInvalidArguments() {
    // ND: ND called with 2 arguments; 3 or more arguments are expected.
    check("ND(x^2, x)", //
        "ND(x^2,x)");
    // ND: Positive integer expected at position 2 in {x,-1}.
    check("ND(x^2,{x,-1},2.0)", //
        "ND(x^2,{x,-1},2.0)");
  }
}
