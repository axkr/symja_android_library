package org.matheclipse.core.reflection.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

/**
 * JUnit tests for the SolveAlways function.
 */
public class SolveAlwaysTest {

  private ExprEvaluator util;

  @BeforeEach
  public void setUp() {
    // Initialize the standard Symja expression evaluator
    // without history to keep the test cases isolated
    util = new ExprEvaluator(false, (short) 100);
  }

  /**
   * Helper method to evaluate an input string and compare its string representation to the expected
   * output.
   */
  private void check(String input, String expected) {
    IExpr result = util.eval(input);
    assertEquals(expected, result.toString());
  }

  @Test
  public void testLinearEquation() {
    // Test SolveAlways(a*x + b == 0, x)
    check("SolveAlways[a*x + b == 0, x]", "{{a->0,b->0}}");
  }

  @Test
  public void testQuadraticEquation() {
    // Test SolveAlways(a*x^2 + b*x + c == 0, x)
    check("SolveAlways[a*x^2 + b*x + c == 0, x]", "{{a->0,b->0,c->0}}");
  }

  @Test
  public void testEquivalence() {
    // Test SolveAlways(a*x + b == 2*x + 3, x)
    check("SolveAlways[a*x + b == 2*x + 3, x]", "{{a->2,b->3}}");
  }

  @Test
  public void testMultipleVariables() {
    // Test SolveAlways(a*x + b*y == x - y, {x, y})
    check("SolveAlways[a*x + b*y == x - y, {x, y}]", "{{a->1,b->-1}}");
  }

  @Test
  public void testSystemOfEquations() {
    // Test SolveAlways({a*x + b == 0, c*x + d == 0}, x)
    check("SolveAlways[{a*x + b == 0, c*x + d == 0}, x]", "{{a->0,b->0,c->0,d->0}}");
  }

  @Test
  public void testNoSolution() {
    // Test SolveAlways(a*x == x + 1, x)
    // No parameter 'a' can make this true for all x, should return empty list {}
    check("SolveAlways[a*x == x + 1, x]", "{}");
  }

  @Test
  public void testAlwaysTrue() {
    // Test SolveAlways(x == x, x)
    // Always true regardless of parameters, should return empty rules list {{}}
    check("SolveAlways[x == x, x]", "{{}}");
  }

  @Test
  public void testMissingVariables() {
    // Test SolveAlways(a == 5, x)
    // The variable x does not appear, so it just solves the parameter equation
    check("SolveAlways[a == 5, x]", "{{a->5}}");
  }
}