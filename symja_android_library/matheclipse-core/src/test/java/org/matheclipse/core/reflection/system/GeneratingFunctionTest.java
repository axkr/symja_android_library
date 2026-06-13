package org.matheclipse.core.reflection.system;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

public class GeneratingFunctionTest {

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
    IExpr result = util.eval(input);
    assertEquals(expected, result.toString());
  }

  // ==========================================================
  // Tests for GeneratingFunction (Ordinary Generating Functions)
  // GF(a_n, x) = Sum_{n=0}^{Infinity} a_n * x^n
  // ==========================================================

  @Test
  public void testGFConstant() {
    // a_n = 1 => Sum(x^n) = 1/(1-x)
    check("GeneratingFunction(1, n, x)", "1/(1-x)");
  }

  @Test
  public void testGFPower() {
    // a_n = c^n => Sum((cx)^n) = 1/(1-c*x)
    check("GeneratingFunction(c^n, n, x)", "1/(1-c*x)");

    // a_n = 2^n => Sum((2x)^n) = 1/(1-2*x)
    check("GeneratingFunction(2^n, n, x)", "1/(1-2*x)");
  }

  @Test
  public void testGFLinear() {
    // a_n = n => Sum(n*x^n) = x/(1-x)^2
    check("GeneratingFunction(n, n, x)", //
        "x/(1-x)^2");
  }

  @Test
  public void testGFFactorialDenominator() {
    // a_n = 1/n! => Sum(x^n / n!) = E^x
    check("GeneratingFunction(1/n!, n, x)", "E^x");
  }

  @Test
  public void testGFMultidimensional() {
    // Multidimensional: a_{n,m} = 1 => 1/((1-x)*(1-y))
    check("GeneratingFunction(1, {n, m}, {x, y})", //
        "1/((-1+x)*(-1+y))");
  }

  // ==========================================================
  // Tests for ExponentialGeneratingFunction (EGF)
  // EGF(a_n, x) = Sum_{n=0}^{Infinity} a_n * x^n / n!
  // ==========================================================

  @Test
  public void testEGFConstant() {
    // a_n = 1 => Sum(x^n / n!) = E^x
    check("ExponentialGeneratingFunction(1, n, x)", //
        "E^x");
  }

  @Test
  public void testEGFPower() {
    // a_n = c^n => Sum((cx)^n / n!) = E^(c*x)
    check("ExponentialGeneratingFunction(c^n, n, x)", //
        "E^(c*x)");

    // a_n = 2^n => Sum((2x)^n / n!) = E^(2*x)
    check("ExponentialGeneratingFunction(2^n, n, x)", "E^(2*x)");
  }

  @Test
  public void testEGFLinear() {
    // a_n = n => Sum(n * x^n / n!) = x * E^x
    check("ExponentialGeneratingFunction(n, n, x)", "E^x*x");
  }

  @Test
  public void testEGFFactorial() {
    // a_n = n! => Sum(n! * x^n / n!) = Sum(x^n) = 1/(1-x)
    check("ExponentialGeneratingFunction(n!, n, x)", "1/(1-x)");
  }

  @Test
  public void testEGFMultidimensional() {
    // Multidimensional: a_{n,m} = 1 => E^x * E^y = E^(x+y)
    check("ExponentialGeneratingFunction(1, {n, m}, {x, y})", "E^(x+y)");
  }

  // ==========================================================
  // Error Handling and Fallback Tests
  // ==========================================================

  @Test
  public void testInvalidArguments() {
    // GeneratingFunction: GeneratingFunction called with 2 arguments; 3 or 4 arguments are
    // expected.
    check("GeneratingFunction(1, n)", //
        "GeneratingFunction(1,n)");

    // Mismatched list sizes for multidimensional
    check("GeneratingFunction(1, {n, m}, {x})", //
        "GeneratingFunction(1,{n,m},{x})");

    // GeneratingFunction: 2 is not a valid variable.
    check("GeneratingFunction(1, 2, x)", //
        "GeneratingFunction(1,2,x)");
  }
}
