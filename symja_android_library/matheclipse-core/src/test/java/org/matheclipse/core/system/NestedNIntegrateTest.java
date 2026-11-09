package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Multidimensional <code>NIntegrate</code> with singular points on the boundary, cancellation in
 * the integrand, and <code>FindRoot</code> of a function given by a numerical integral.
 */
public class NestedNIntegrateTest extends ExprEvaluatorTestCase {

  @Test
  public void testBoundVariableWithTheNameOfAnIntegrationVariable() {
    // the integrand is evaluated before the outer value is substituted, which would also replace
    // the parameter u of the function
    check("k(f_) := NIntegrate(f(u, v), {u, 0, 1}, {v, 0, 2}); k(Function({u, v}, u*v^2))", //
        "1.33333");
    check("h(f_) := NIntegrate(f(u, v), {u, 0, 1}, {v, 0, 1}); "
        + "h(Function({u, v}, Sin(u) + v))", //
        "0.959698");
  }

  @Test
  public void testCosineCancellation() {
    // 1-Cos(x) keeps no digits for small x, the half angle form does
    check("Abs(NIntegrate(x^2/(1 - Cos(x)), {x, 0, 1}, PrecisionGoal -> 14) "
        + "- 2.057270784429146) < 10^-14", //
        "True");
    check("Abs(NIntegrate((x^2 + y^2)/(2 - Cos(x) - Cos(y)), {x, 0, 1/10}, {y, 0, 1/10}, "
        + "PrecisionGoal -> 14) - 0.0200075366101153) < 10^-15", //
        "True");
  }

  @Test
  public void testNarrowInteriorPeak() {
    // a peak of width 10^-6 in the inner range: the plain adaptive rule finds what QAGS gives up on
    check("Abs(NIntegrate(With({c = (Cos(1/1000) + Cos(b))/2}, "
        + "(1 - c)/((1 - c)^2 + (Sin(1/1000)/8 + Sin(b)/6)^2)), {b, -Pi, Pi}, "
        + "PrecisionGoal -> 12) - 37.69866653336808) < 10^-10", //
        "True");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testSingularVertexOnTheBoundary() {
    // singular at the corner {0, 0}: a peak along b = -ArcSin(3/4*Sin(a)) leaves it into the inner
    // range, which is split at the zero b = 0 of the denominator for a = 0
    check("Abs(NIntegrate(With({c = (Cos(a) + Cos(b))/2}, "
        + "(1 - c)/((1 - c)^2 + (Sin(a)/8 + Sin(b)/6)^2)), {a, 0, Pi}, {b, -Pi, Pi}, "
        + "PrecisionGoal -> 12) - 33.22696507415113) < 10^-9", //
        "True");
    check("Abs(NIntegrate(With({c = (2*Cos(a) + Cos(b))/3}, "
        + "(1 - c)/((1 - c)^2 + (Sin(a)/9 + Sin(b)/9)^2)), {a, 0, Pi}, {b, -Pi, Pi}, "
        + "PrecisionGoal -> 12) - 37.38019713222372) < 10^-9", //
        "True");
  }

  @Test
  public void testFindRootOfOpaqueFunction() {
    // a function of numeric arguments only has no derivative: the secant method from the two
    // start values, not Newton with numerical derivatives
    check("n = 0; g(x_?NumericQ) := (n++; x^3 - 2); "
        + "{x /. FindRoot(g(x) == 0, {x, 1, 2}), n <= 10}", //
        "{1.25992,True}");
    // a residual which stops changing in the last digits ends the search
    check("m = 0; r(x_?NumericQ) := (m++; Round(x^3 - 2, 10^-9)); "
        + "{Abs((x /. FindRoot(r(x), {x, 1, 2})) - 2^(1/3)) < 10^-9, m <= 20}", //
        "{True,True}");
  }
}
