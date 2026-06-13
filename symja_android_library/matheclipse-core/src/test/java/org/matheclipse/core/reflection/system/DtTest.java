package org.matheclipse.core.reflection.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;

public class DtTest {

  private ExprEvaluator util;

  @BeforeEach
  public void setUp() {
    util = new ExprEvaluator();
    // Clear any previous definitions to ensure a clean state
    util.clearVariables();
  }

  /**
   * Helper method to evaluate the input string and check against the expected output string.
   */
  private void check(String input, String expected) {
    assertEquals(expected, util.eval(input).toString());
  }

  @Test
  public void testDt001() {

    check("Dt(Log[f])", //
        "Dt(f)/f");

    check("Dt(a*x^n,x)", //
        "x^n*Dt(a,x)+a*x^n*(n/x+Dt(n,x)*Log(x))");
  }

  @Test
  public void testTotalDerivativeBasic() {
    // Dt(x, x) -> 1
    check("Dt(x, x)", "1");

    // Dt(y, x) -> Dt(y, x) (unevaluated because y is assumed to depend on x)
    check("Dt(y, x)", "Dt(y,x)");

    // Dt(number, x) -> 0
    check("Dt(42, x)", "0");
    check("Dt(3.14, x)", "0");
  }

  @Test
  public void testTotalDerivativeArithmetic() {
    // Power rule: Dt(x^2, x) -> 2*x
    check("Dt(x^2, x)", "2*x");

    // Plus rule: Dt(x + y, x) -> 1 + Dt(y, x)
    check("Dt(x + y, x)", "1+Dt(y,x)");

    // Product rule: Dt(x * y, x) -> y + x*Dt(y, x)
    check("Dt(x * y, x)", "y+x*Dt(y,x)");

    // Product rule with multiple variables: Dt(x * y * z, x)
    check("Dt(x * y * z, x)", "y*z+x*z*Dt(y,x)+x*y*Dt(z,x)");
  }

  @Test
  public void testTotalDerivativeFunctions() {
    // Chain rule on general functions
    check("Dt(f(x), x)", //
        "f'(x)");

    // Chain rule on multiple arguments
    check("Dt(f(x, y), x)", //
        "Dt(y,x)*Derivative(0,1)[f][x,y]+Derivative(1,0)[f][x,y]");

    // Specific functions (e.g., Sin, Cos, Exp)
    check("Dt(Sin(x), x)", //
        "Cos(x)");
    check("Dt(Sin(x*y), x)", //
        "Cos(x*y)*(y+x*Dt(y,x))");
    check("Dt(Exp(x), x)", //
        "E^x");
  }

  @Test
  public void testTotalDifferential() {
    // Dt(x) -> Dt(x)
    check("Dt(x)", "Dt(x)");

    // Dt(x^2) -> 2*x*Dt(x)
    check("Dt(x^2)", "2*x*Dt(x)");

    // Dt(x*y) -> y*Dt(x) + x*Dt(y)
    check("Dt(x * y)", "y*Dt(x)+x*Dt(y)");

    // Dt(Sin(x)) -> Cos(x)*Dt(x)
    check("Dt(Sin(x))", "Cos(x)*Dt(x)");
  }

  @Test
  public void testConstantsOption() {
    // Total differential with Constants
    check("Dt(c * x^2, Constants -> {c})", //
        "2*c*x*Dt(x,Constants->{c})");
    // Using Constants list
    check("Dt(c * x, x, Constants -> {c})", //
        "c");
    check("Dt(x * y * z, x, Constants -> {y, z})", //
        "y*z");

  }

  @Test
  public void testConstantAttribute() {
    // If a symbol has the Constant attribute, its derivative is 0
    util.eval("SetAttributes(c, Constant)");
    check("Dt(c * x, x)", //
        "c");
    check("Dt(c)", //
        "0");

    // Functions with Constant heads are treated as constants
    check("Dt(c(x), x)", //
        "0");
    check("Dt(c(x))", //
        "0");
  }

  @Test
  public void testMultipleDerivatives() {
    // Dt(x^3, {x, 2}) -> 6*x
    check("Dt(x^3, {x, 2})", //
        "6*x");

    // Dt(x^4, {x, 3}) -> 24*x
    check("Dt(x^4, {x, 3})", //
        "24*x");

    // Dt(Sin(x), {x, 2}) -> -Sin(x)
    check("Dt(Sin(x), {x, 2})", //
        "-Sin(x)");

    // Dt(x, {x, 2}) -> 0
    check("Dt(x, {x, 2})", //
        "0");

    check("Dt(Dt(y,x),y)", //
        "0");
  }

  @Test
  public void testSuccessiveDerivatives() {
    // Dt(x^2 * y^2, x, y) -> Dt( Dt(x^2 * y^2, x), y)
    // Dt(x^2 * y^2, x) = 2*x*y^2 + 2*x^2*y*Dt(y, x)
    // Expected to just unfold the nested structure correctly based on the Dt rules
    check("Dt(x^2 * y^2, x, y)  ", //
        "4*x*y+2*y^2*Dt(x,y)+2*x^2*Dt(y,x)+4*x*y*Dt(x,y)*Dt(y,x)");

    // Successive mixed with Constants
    check("Dt(x^2 * y^2 * c, x, y, Constants -> {c})",
        "4*c*x*y+2*c*y^2*Dt(x,y,Constants->{c})+2*c*x^2*Dt(y,x,Constants->{c})+4*c*x*y*Dt(x,y,Constants->{c})*Dt(y,x,Constants->{c})");
  }
}
