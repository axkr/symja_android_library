package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/** Tests for the {@link GreenFunction} built-in function. */
public class GreenFunctionTest extends ExprEvaluatorTestCase {

  @Test
  public void testGreenFunctionConstantCoefficients() {
    // The classical Green's function of the second derivative on the unit interval.
    check("GreenFunction({y''(x), y(0) == 0, y(1) == 0}, y, {x, 0, 1}, s)", //
        "Piecewise({{(-1+s)*x,x<=s}},s*(-1+x))");

    check("GreenFunction({y''(x) + y(x), y(0) == 0, y(Pi/2) == 0}, y, {x, 0, Pi/2}, s)", //
        "Piecewise({{-Cos(s)*Sin(x),x<=s}},-Cos(x)*Sin(s))");

    // A condition on the derivative singles out a solution just as well as one on the value.
    check("GreenFunction({y''(x), y(0) == 0, y'(1) == 0}, y, {x, 0, 1}, s)", //
        "Piecewise({{-x,x<=s}},-s)");

    // The source may be given as a number.
    check("GreenFunction({y''(x), y(0) == 0, y(1) == 0}, y, {x, 0, 1}, 1/2)", //
        "Piecewise({{-x/2,x<=1/2}},1/2*(-1+x))");
  }

  @Test
  public void testGreenFunctionVariableCoefficients() {
    check("GreenFunction({x^2*y''(x) + x*y'(x), y(1) == 0, y(E) == 0}, y, {x, 1, E}, s)", //
        "Piecewise({{((-1+Log(s))*Log(x))/s,x<=s}},(Log(s)*(-1+Log(x)))/s)");
  }

  @Test
  public void testGreenFunctionJumpCondition() {
    // What defines the Green's function is the step its derivative takes across the source, which
    // has to be the reciprocal of the coefficient of the highest derivative there. This holds
    // whatever form the two branches come out in.
    check("g = GreenFunction({y''(x) - y(x), y(0) == 0, y(1) == 0}, y, {x, 0, 1}, s);"
        + " Simplify((D(g[[2]], x) - D(g[[1,1,1]], x)) /. x -> s)", //
        "1");

    check("g = GreenFunction({x^2*y''(x) + x*y'(x), y(1) == 0, y(E) == 0}, y, {x, 1, E}, s);"
        + " Simplify((D(g[[2]], x) - D(g[[1,1,1]], x)) /. x -> s)", //
        "1/s^2");

    check("g = GreenFunction({y''(x) + y(x), y(0) == 0, y(Pi/2) == 0}, y, {x, 0, Pi/2}, s);"
        + " Simplify((D(g[[2]], x) - D(g[[1,1,1]], x)) /. x -> s)", //
        "1");
  }

  @Test
  public void testGreenFunctionSolvesTheProblem() {
    // Integrating the Green's function against a right hand side gives the solution of the
    // boundary value problem, so its second derivative is that right hand side and it vanishes at
    // both ends.
    check("y = Simplify(Integrate(x*(s-1), {s, x, 1}) + Integrate(s*(x-1), {s, 0, x}));"
        + " {D(y, {x, 2}), y /. x -> 0, y /. x -> 1}", //
        "{1,0,0}");

    check("y = Simplify(Integrate(x*(s-1)*s, {s, x, 1}) + Integrate(s*(x-1)*s, {s, 0, x}));"
        + " {D(y, {x, 2}), y /. x -> 0, y /. x -> 1}", //
        "{x,0,0}");
  }

  @Test
  public void testGreenFunctionDeclines() {
    // Sin(x) solves this equation and meets both conditions, so the solution of the boundary value
    // problem is not unique and there is no Green's function. That is exactly the case in which
    // the two solutions are proportional and their Wronskian vanishes.
    check("GreenFunction({y''(x) + y(x), y(0) == 0, y(Pi) == 0}, y, {x, 0, Pi}, s)", //
        "GreenFunction({y(x)+y''(x),y(0)==0,y(Pi)==0},y,{x,0,Pi},s)");

    // Only the second order case is built.
    check("GreenFunction({y'(x), y(0) == 0}, y, {x, 0, 1}, s)", //
        "GreenFunction({y'(x),y(0)==0},y,{x,0,1},s)");

    // Not linear in the unknown.
    check("GreenFunction({y''(x)*y(x), y(0) == 0, y(1) == 0}, y, {x, 0, 1}, s)", //
        "GreenFunction({y(x)*y''(x),y(0)==0,y(1)==0},y,{x,0,1},s)");

    // Both conditions at the same end do not separate the two solutions.
    check("GreenFunction({y''(x), y(0) == 0, y'(0) == 0}, y, {x, 0, 1}, s)", //
        "GreenFunction({y''(x),y(0)==0,y'(0)==0},y,{x,0,1},s)");
  }
}
