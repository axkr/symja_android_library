package org.matheclipse.core.reflection.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

public class AsymptoticSolveTest {


  private ExprEvaluator util;

  @BeforeEach
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
  public void testLinearAsymptotic() {
    // Tests exact solution followed by series expansion around x -> 0
    check("AsymptoticSolve(y(x) - Exp(x) == 0, y(x), {x, 0, 3})", "{{y(x)->1+x+x^2/2+x^3/6}}");
  }

  @Test
  public void testSystemAsymptotic() {
    // Tests expansion of a system of equations
    check(
        "AsymptoticSolve({y(x) + z(x) == Sin(x), y(x) - z(x) == Cos(x)}, {y(x), z(x)}, {x, 0, 2})",
        "{{y(x)->1/4*(2+2*x-x^2),z(x)->1/4*(-2+2*x+x^2)}}");
  }

  @Test
  public void testBasePointSpecification() {
    // y^2 == 1+x has two roots: y->1 and y->-1 at x->0.
    // Specifying the base point forces AsymptoticSolve to select only the matching branch.
    check("AsymptoticSolve(y(x)^2 == 1 + x, y(x) -> 1, {x, 0, 3})", //
        "{{y(x)->1/16*(16+8*x-2*x^2+x^3)}}");

    check("AsymptoticSolve(y(x)^2 == 1 + x, y(x) -> -1, {x, 0, 2})", //
        "{{y(x)->1/8*(-8-4*x+x^2)}}");
  }

  @Test
  public void testExpansionAtInfinity() {
    check("AsymptoticSolve(y(x) == Exp(1/x), y(x), {x, Infinity, 3})", //
        "{}");
    // Tests Laurent series expansion at Infinity
    check("AsymptoticSolve(y(x) == (x^2+1)/(x-1), y(x), {x, Infinity, 3})",
        "{}");

  }

  @Test
  public void testAlgebraicFallback() {
    // A non-linear polynomial equation: y^2 + y - 2 - x == 0
    // At x=0, y=1 is a root.
    // y = 1 + c1 x + c2 x^2
    // y^2 + y - 2 - x = (1 + 2 c1 x + 2 c2 x^2 + c1^2 x^2) + (1 + c1 x + c2 x^2) - 2 - x = 0
    // => x^1: 2 c1 + c1 - 1 = 0 => 3 c1 = 1 => c1 = 1/3
    // => x^2: 2 c2 + c1^2 + c2 = 0 => 3 c2 + 1/9 = 0 => c2 = -1/27
    check("AsymptoticSolve(y^2 + y - 2 - x == 0, y -> 1, {x, 0, 2})", //
        "{{y->1+x/3-x^2/27}}");
  }

  @Test
  public void testInvalidArguments() {
    // Missing domain/range specifications should safely return unevaluated
    check("AsymptoticSolve(y(x) == x, y(x))", "AsymptoticSolve(y(x)==x,y(x))");
  }
}
