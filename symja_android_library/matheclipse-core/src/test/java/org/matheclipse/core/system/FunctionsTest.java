package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class FunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testBezierFunction01() {
    check("BezierFunction({{0},{1},{4}}) // InputForm", //
        "BezierFunction(1,{{0.0`,1.0`}},{2},{{{0.0`},{1.0`},{4.0`}},{}},{0},MachinePrecision,\"Unevaluated\")");
    check("BezierFunction({{0,0},{1,1},{2,0}}) // InputForm", //
        "BezierFunction(1,{{0.0`,1.0`}},{2},{{{0.0`,0.0`},{1.0`,1.0`},{2.0`,0.0`}},{}},{0},MachinePrecision,\"Unevaluated\")");
    check(
        "BezierFunction(1, {{0., 1.}}, {2}, {{{0., 0.}, {1., 1.}, {2., 0.}}, {}}, {0},  MachinePrecision, \"Unevaluated\")[1]",
        "{2.0,0.0}");
    check("pts = {{0, 0}, {1, 1}, {2, 0}, {3, 2}};", //
        "");
    check("bz= BezierFunction(pts)", //
        "BezierFunction({{0,0},{1,1},{2,0},{3,2}})");
    check("bz(0.5)", //
        "{1.5,0.625}");
    // Quadratic at half
    check("BezierFunction({{0,0},{1,1},{2,0}})[0.5]", //
        "{1.0,0.5}");

    // At zero
    check("BezierFunction({{0,0},{1,1},{2,0}})[0]", //
        "{0.0,0.0}");

    // At one
    check("BezierFunction({{0,0},{1,1},{2,0}})[1]", //
        "{2.0,0.0}");

    // Cubic
    check("BezierFunction({{0,0},{1,1},{2,0},{3,2}})[0.25]", //
        "{0.75,0.453125}");

    // Cubic midpoint with saved binding
    check("f = BezierFunction({{0, 0}, {1, 1}, {2, 0}, {3, 2}}); f(0.5)", //
        "{1.5,0.625}");

    // One dimensional
    check("BezierFunction({{0},{1},{4}})[0.5]", //
        "{1.5}");

    // With rational parameter
    check("BezierFunction({{0,0},{1,1},{2,0}})[1/3]", //
        "{0.666667,0.444444}");

    // Unevaluated symbolic expands into the 7-argument structured form
    check("BezierFunction({{0,0},{1,1},{2,0}}) // InputForm", //
        "BezierFunction(1,{{0.0`,1.0`}},{2},{{{0.0`,0.0`},{1.0`,1.0`},{2.0`,0.0`}},{}},{0},MachinePrecision,\"Unevaluated\")");

    // Non-numeric argument evaluates the arguments but leaves the application unevaluated
    check("BezierFunction({{0,0},{1,1},{2,0}})[t]  // InputForm", //
        "BezierFunction(1,{{0.0`,1.0`}},{2},{{{0.0`,0.0`},{1.0`,1.0`},{2.0`,0.0`}},{}},{0},MachinePrecision,\"Unevaluated\")[t]");
  }

  @Test
  public void testBezierFunction02() {
    check("pts = {{0, 1, 2}, {1, 0, 3}, {2, 1, 0}};", //
        "");
    check("bz= BezierFunction(pts)", //
        "BezierFunction({{0,1,2},{1,0,3},{2,1,0}})");
    check("bz(0.5)", //
        "{1.0,0.5,2.0}");
  }

  @Test
  public void testBezierFunction03() {
    check(
        "f=BezierFunction({{0, 0}, {0, 0.8}, {1, 1}});FindRoot(0.5==Indexed(f(x), 2), {x, 0.2, 0.5}, Method->\"Bisection\")", //
        "{x->0.361508}");
  }


}
