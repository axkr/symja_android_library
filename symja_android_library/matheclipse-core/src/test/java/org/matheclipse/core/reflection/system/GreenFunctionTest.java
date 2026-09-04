package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/** Tests for the {@link GreenFunction} built-in function. */
public class GreenFunctionTest extends ExprEvaluatorTestCase {

  @Test
  public void testGreenFunctionBoundaryValueProblem() {
    // The classical Green's function of the second derivative on the unit interval.
    check("GreenFunction({-u''(x), u(0) == 0, u(1) == 0}, u(x), {x, 0, 1}, y)", //
        "(y-x*y)*HeavisideTheta(x-y)+(x-x*y)*HeavisideTheta(-x+y)");

    check("GreenFunction({u''(x) + u(x), u(0) == 0, u(Pi/2) == 0}, u(x), {x, 0, Pi/2}, s)", //
        "-Cos(x)*HeavisideTheta(-s+x)*Sin(s)-Cos(s)*HeavisideTheta(s-x)*Sin(x)");

    // A condition on the derivative singles out a solution just as well as one on the value.
    check("GreenFunction({u''(x) + u(x), u'(0) == 0, u'(Pi/2) == 0}, u(x), {x, 0, Pi/2}, s)", //
        "Cos(x)*HeavisideTheta(s-x)*Sin(s)+Cos(s)*HeavisideTheta(-s+x)*Sin(x)");
    check("GreenFunction({u''(x), u(0) == 0, u'(1) == 0}, u(x), {x, 0, 1}, s)", //
        "-x*HeavisideTheta(s-x)-s*HeavisideTheta(-s+x)");

    // Mixed conditions, each combining the value and the derivative at one end.
    check("GreenFunction({u''(x) + u(x), u(0) + 3*u'(0) == 0, u(Pi/2) - u'(Pi/2) == 0},"
        + " u(x), {x, 0, Pi/2}, s)", //
        "1/2*HeavisideTheta(s-x)*(Cos(s)-Sin(s))*(-3*Cos(x)+Sin(x))+1/2*HeavisideTheta(-s+x)*(\n" //
            + "3*Cos(s)-Sin(s))*(-Cos(x)+Sin(x))");

    // The endpoints and the coefficients may be symbolic.
    check("GreenFunction({T*u''(x), u(0) == 0, u(p) == 0}, u(x), {x, 0, p}, y)", //
        "((-p+x)*y*HeavisideTheta(x-y))/(p*T)+(x*(-p+y)*HeavisideTheta(-x+y))/(p*T)");
  }

  @Test
  public void testGreenFunctionVariableCoefficients() {
    check("GreenFunction({x^2*u''(x) + x*u'(x), u(1) == 0, u(E) == 0}, u(x), {x, 1, E}, s)", //
        "(HeavisideTheta(-s+x)*Log(s)*(-1+Log(x)))/s+(HeavisideTheta(s-x)*(-1+Log(s))*Log(x))/s");
  }

  @Test
  public void testGreenFunctionInitialValueProblem() {
    // Both conditions at the near end make the Green's function causal: it vanishes before the
    // source, and after it is the response to the impulse.
    check("GreenFunction({u''(x) + 5*u'(x) + 6*u(x), u(0) == 0, u'(0) == 0},"
        + " u(x), {x, 0, Infinity}, y)", //
        "(E^(2*(-x+y))-E^(3*(-x+y)))*HeavisideTheta(x-y)");

    // First order needs one condition only.
    check("GreenFunction({u'(x) + u(x), u(0) == 0}, u(x), {x, 0, Infinity}, y)", //
        "HeavisideTheta(x-y)/E^(x-y)");

    // The impulse response of a linear time invariant system.
    check("GreenFunction({2*y''(t) + 8*y'(t) + 6*y(t), y(0) == 0, y'(0) == 0},"
        + " y(t), {t, 0, Infinity}, s)", //
        "1/4*(E^(s-t)-E^(3*(s-t)))*HeavisideTheta(-s+t)");
  }

  @Test
  public void testGreenFunctionPureFunction() {
    // Asking about u rather than u(x) asks for the function itself.
    check("GreenFunction({u''(x) + 5*u'(x) + 6*u(x), u(0) == 0, u'(0) == 0},"
        + " u, {x, 0, Infinity}, y)", //
        "Function({x,y},(E^(2*(-x+y))-E^(3*(-x+y)))*HeavisideTheta(x-y))");
  }

  @Test
  public void testGreenFunctionJumpCondition() {
    // What defines the Green's function is the step its derivative takes across the source, which
    // has to be the reciprocal of the coefficient of the highest derivative there. Reading the two
    // branches off the HeavisideTheta terms keeps this independent of the form they come out in.
    check("g = GreenFunction({u''(x) - u(x), u(0) == 0, u(1) == 0}, u(x), {x, 0, 1}, s);"
        + " below = Coefficient(g, HeavisideTheta(s - x));"
        + " above = Coefficient(g, HeavisideTheta(x - s));"
        + " Simplify((D(above, x) - D(below, x)) /. x -> s)", //
        "1");

    check("g = GreenFunction({x^2*u''(x) + x*u'(x), u(1) == 0, u(E) == 0}, u(x), {x, 1, E}, s);"
        + " below = Coefficient(g, HeavisideTheta(s - x));"
        + " above = Coefficient(g, HeavisideTheta(x - s));"
        + " Simplify((D(above, x) - D(below, x)) /. x -> s)", //
        "1/s^2");
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
    check("GreenFunction({u''(x) + u(x), u(0) == 0, u(Pi) == 0}, u(x), {x, 0, Pi}, s)", //
        "GreenFunction({u(x)+u''(x),u(0)==0,u(Pi)==0},u(x),{x,0,Pi},s)");

    // Not linear in the unknown.
    check("GreenFunction({u''(x)*u(x), u(0) == 0, u(1) == 0}, u(x), {x, 0, 1}, s)", //
        "GreenFunction({u(x)*u''(x),u(0)==0,u(1)==0},u(x),{x,0,1},s)");

    // Conditions which are not homogeneous are not met by the zero solution, so they do not give
    // a Green's function.
    check("GreenFunction({u''(x), u(0) == 1, u'(0) == 0}, u(x), {x, 0, Infinity}, s)", //
        "GreenFunction({u''(x),u(0)==1,u'(0)==0},u(x),{x,0,Infinity},s)");

    // Third order is not built.
    check("GreenFunction({Derivative(3)[u][x], u(0) == 0, u'(0) == 0, u''(0) == 0},"
        + " u(x), {x, 0, Infinity}, s)", //
        "GreenFunction({Derivative(3)[u][x],u(0)==0,u'(0)==0,u''(0)==0},u(x),{x,0,Infinity},s)");
  }
}
