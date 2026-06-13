package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * JUnit test cases for the Asymptotic function.
 */
public class AsymptoticTest extends ExprEvaluatorTestCase {

  @Test
  public void testStandardSeriesFallback() {
    // Test basic asymptotic expansion near 0
    // Asymptotic(Cos(x), {x, 0, 4})
    check("Asymptotic(Cos(x), {x, 0, 4})", //
        "1-x^2/2+x^4/24");

    // Test asymptotic expansion at a non-zero finite point
    // Asymptotic(Log(x), {x, 1, 3})
    check("Asymptotic(Log(x), {x, 1, 3})", //
        "-1-(1-x)^2/2+(-1+x)^3/3+x");
  }

  @Test
  public void testInfiniteExpansion() {
    // Test asymptotic behavior as x approaches Infinity

    // Asymptotic(Exp(1/x), {x, Infinity, 2})
    check("Asymptotic(Exp(1/x), {x, Infinity, 2})", //
        "1+1/(2*x^2)+1/x");

    // Asymptotic(Sin(1/x), x -> Infinity)
    check("Asymptotic(Sin(1/x), x -> Infinity)", //
        "1/x");


  }

  @Test
  public void testCompositeFunctionFaaDiBruno() {
    // Test the specific BellY polynomial structural expansion for f(g(x))
    // Asymptotic(Exp(Sin(x)), {x, 0, 3})
    // Expansion should be: 1 + x + x^2/2
    check("Asymptotic(Exp(Sin(x)), {x, 0, 3})", //
        "1+x+x^2/2");

    // Asymptotic(Log(Cos(x)), {x, 0, 4})
    // Expansion should be: -x^2/2 - x^4/12
    check("Asymptotic(Log(Cos(x)), {x, 0, 4})", //
        "-x^2/2-x^4/12");
  }

  @Test
  public void testDelegationToAsymptoticSolve() {
    // Test ordinal switch delegation to AsymptoticSolve
    // Asymptotic(Solve(x^2 + y^2 == 1, y), {x, 0, 2})
    // The roots are y = Sqrt(1-x^2) and y = -Sqrt(1-x^2)

    // TODO WMA returns: {{y -> -Sqrt[1 - x^2]}, {y -> Sqrt[1 - x^2]}}
    check("Asymptotic(Solve(x^2 + y^2 == 1, y), {x, 0, 2})",
        "{{y->-1+x^2/2},{y->1-x^2/2}}");
  }

  @Test
  public void testDelegationToAsymptoticDSolveValue() {
    // Test ordinal switch delegation to AsymptoticDSolveValue
    // Asymptotic(DSolveValue(y''(x) + y(x) == 0, y(x), x), {x, 0, 2})
    // General solution involves constants C(1) and C(2)
    check("Asymptotic(DSolveValue(y''(x) + y(x) == 0, y(x), x), {x, 0, 2})",
        "C(1)-1/2*x^2*C(1)+x*C(2)");
  }

  @Test
  public void testOptionsAndDefaultTermGoal() {
    // Test that SeriesTermGoal defaults to 3 if not explicitly provided
    // Asymptotic(Tan(x), x -> 0)
    check("Asymptotic(Tan(x), x -> 0)", //
        "x");

    // Explicitly testing higher order
    // Asymptotic(Tan(x), {x, 0, 5})
    check("Asymptotic(Tan(x), {x, 0, 5})", //
        "x+x^3/3+2/15*x^5");
  }

  @Test
  public void testIndefiniteIntegration() {
    // AsymptoticIntegrate(Cos(x), x, {x, 0, 4})
    // Exact integral is Sin(x). Asymptotic expansion around 0 is x - x^3/6
    check("AsymptoticIntegrate(Cos(x), x, {x, 0, 4})", //
        "x-x^3/6");

    check("AsymptoticIntegrate(Cos(x), x, x->0)", //
        "x");

    // AsymptoticIntegrate(Exp(x), x, {x, 0, 3})
    // Exact integral is Exp(x). Expansion is 1 + x + x^2/2 + x^3/6
    check("AsymptoticIntegrate(Exp(x), x, {x, 0, 3})", //
        "1+x+x^2/2+x^3/6");
  }

  @Test
  public void testDefiniteIntegration() {
    // AsymptoticIntegrate(Cos(t), {t, 0, x}, {x, 0, 4})
    // Exact integral is Sin(x). Asymptotic expansion around 0 is x - x^3/6
    check("AsymptoticIntegrate(Cos(t), {t, 0, x}, {x, 0, 4})", //
        "x-x^3/6+x^5/120");

    // AsymptoticIntegrate(1/(1+t^2), {t, 0, x}, {x, 0, 3})
    // Exact integral is ArcTan(x). Expansion is x - x^3/3
    check("AsymptoticIntegrate(1/(1+t^2), {t, 0, x}, {x, 0, 3})", //
        "x-x^3/3");
  }

  @Test
  public void testParameterExpansion() {
    // Expansion with respect to a parameter 'e' rather than the integration variable.
    // AsymptoticIntegrate(Exp(e*x), {x, 0, 1}, {e, 0, 3})
    // Integrand expanded around e=0: 1 + e*x + e^2*x^2/2 + e^3*x^3/6
    // Integrated wrt x from 0 to 1: 1 + e/2 + e^2/6 + e^3/24
    check("AsymptoticIntegrate(Exp(e*x), {x, 0, 1}, {e, 0, 3})", //
        "1+e/2+e^2/6+e^3/24");
  }

  @Test
  public void testInfinityExpansion() {
    // AsymptoticIntegrate(1/(t + x), {t, 0, 1}, {x, Infinity, 3})
    // Exact integral is -Log(x) + Log(1+x) = Log(1 + 1/x)
    // Series at infinity: 1/x - 1/(2*x^2) + 1/(3*x^3)
    check("AsymptoticIntegrate(1/(t + x), {t, 0, 1}, {x, Infinity, 3})", //
        "1/(3*x^3)-1/(2*x^2)+1/x");
  }

  @Test
  public void testRuleSyntax() {
    // Test the x -> 0 rule syntax implicitly specifying the point
    // AsymptoticIntegrate(Sin(x), x, x -> 0)
    // Note: Default term goal is 3.
    // Integral of Sin(x) is -Cos(x). Series of -Cos(x) at 0 is -1 + x^2/2
    check("AsymptoticIntegrate(Sin(x), x, x -> 0)", //
        "-1");
  }
}
