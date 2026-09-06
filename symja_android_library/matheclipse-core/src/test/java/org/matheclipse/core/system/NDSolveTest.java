package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for the numerical ODE solvers <code>NDSolve</code> and <code>NDSolveValue</code>.
 *
 * <p>
 * The solutions are compared against their closed forms with a tolerance rather than against the
 * printed digits of the current implementation, so that a change of step size, solver or error
 * control does not break a test whose answer got no worse. See
 * {@link ExprEvaluatorTestCase#checkApprox(String, double, double)}.
 *
 * <p>
 * A solution is always bound to a symbol before it is applied - <code>f=y/.Part(s,1);f(1.0)</code>
 * rather than <code>(y/.Part(s,1))(1.0)</code>. Only an identifier can head a function application
 * in the relaxed syntax these tests are parsed with; a parenthesized expression followed by another
 * parenthesized expression is implicit multiplication, so the second form silently evaluates to
 * <code>1.0*InterpolatingFunction(...)</code> instead of sampling the solution at <code>1.0</code>.
 */
public class NDSolveTest extends ExprEvaluatorTestCase {

  // exponential_growth
  @Test
  public void testNDSolveExponentialGrowth() {
    checkApprox("sol=NDSolve({y'(x)==y(x),y(0)==1},y,{x,0,1});y(0.5)/.Part(sol,1)", //
        Math.exp(0.5), 0.001);
  }

  // linear_growth
  @Test
  public void testNDSolveLinearGrowth() {
    checkApprox("sol=NDSolve({y'(x)==1,y(0)==0},y,{x,0,1});y(0.5)/.Part(sol,1)", //
        0.5, 0.001);
  }

  // quadratic_growth
  @Test
  public void testNDSolveQuadraticGrowth() {
    checkApprox("sol=NDSolve({y'(x)==x,y(0)==0},y,{x,0,1});y(1)/.Part(sol,1)", //
        0.5, 0.001);
  }

  // interpolating_function_display / dependent_variable_form_keeps_the_argument
  @Test
  public void testNDSolveInterpolatingFunctionDisplay1() {
    check("NDSolve({y'(x)==y(x),y(0)==1},y(x),{x,0,1})", //
        "{{y(x)->InterpolatingFunction({{0.0,1.0}},<>)(x)}}");
  }

  // dependent_variable_form_keeps_the_argument
  @Test
  public void testNDSolveInterpolatingFunctionDisplay2() {
    check("NDSolve({y'(x)==y(x),y(0)==1},y,{x,0,1})", //
        "{{y->InterpolatingFunction({{0.0,1.0}},<>)}}");
  }

  // second_order_harmonic
  @Test
  public void testNDSolveSecondOrderHarmonic() {
    checkApprox("sol=NDSolve({y''(x)+y(x)==0,y(0)==1,y'(0)==0},y,{x,0,4});y(N(Pi))/.Part(sol,1)", //
        -1.0, 0.01);
  }

  // coupled_first_order_system
  @Test
  public void testNDSolveCoupledFirstOrderSystem() {
    checkApprox(
        "s=NDSolve({x'(t)==y(t),y'(t)==-x(t),x(0)==1,y(0)==0},{x,y},{t,0,4});fx=x/.Part(s,1);fx(N(Pi))", //
        -1.0, 0.01);
  }

  // implicit_second_order_system
  @Test
  public void testNDSolveImplicitSecondOrderSystem() {
    // u''(t) + v''(t) == -(u(t) + v(t))
    // u''(t) - v''(t) == -(u(t) - v(t))
    checkApprox(
        "s=NDSolve({u''(t)+v''(t)==-1*(u(t)+v(t)),u''(t)-v''(t)==-1*(u(t)-v(t)),u(0)==1,u'(0)==0,v(0)==0,v'(0)==0},{u,v},{t,0,3});fu=u/.Part(s,1);fu(1.0)", //
        Math.cos(1.0), 0.01);
  }

  // interior_initial_point_integrates_both_directions
  @Test
  public void testNDSolveInteriorInitialPoint() {
    // w(0) and w(2) are E^-1 and E^1. Neither is reproduced to the last bit: the values come out of
    // an adaptive integrator started at the interior point t==1 and run in both directions, so they
    // carry its error - measured at roughly 4*10^-12 backwards and 3*10^-11 forwards. Compared with
    // a relative tolerance for that reason, rather than as printed strings.
    checkNumeric("s=NDSolve({w'(t)==w(t),w(1)==1},w,{t,0,2});f=w/.Part(s,1);{f(0.0),f(2.0)}", //
        "{0.36787944117144233,2.718281828459045}", 1.0e-8);
  }

  // event_locator_stops_integration
  @Test
  public void testNDSolveEventLocatorStopsIntegration() {
    // the event y(t)==0.5 of y'==-y, y(0)==1 is located at Log(2). The root is bracketed on the
    // integrator's continuous extension, so its accuracy is bounded by the solver tolerance and not
    // by the root finder - compared with a relative tolerance for that reason.
    checkNumeric(
        "s=NDSolve({y'(t)==-y(t),y(0)==1},y,{t,0,10},Method->{\"EventLocator\",\"Event\"->y(t)-0.5,\"EventAction\":>Throw(stopT=t,\"StopIntegration\")});fy=y/.Part(s,1);{stopT,fy(stopT)}", //
        "{0.6931471805599453,0.5}", 1.0e-8);
  }

  // symbolic_initial_condition_value
  @Test
  public void testNDSolveSymbolicInitialConditionValue() {
    checkApprox("s=NDSolve({y'(t)==0,y(0)==-1*ArcCos(31/40)},y,{t,0,1});fy=y/.Part(s,1);fy(1.0)", //
        -Math.acos(31.0 / 40.0), 0.001);
  }

  // ndsolve_value returns_interpolating_function
  @Test
  public void testNDSolveValueReturnsInterpolatingFunction() {
    check("NDSolveValue({y'(x)==-y(x),y(0)==1},y,{x,0,10})", //
        "InterpolatingFunction({{0.0,10.0}},<>)");
  }

  // ndsolve_value can_evaluate
  @Test
  public void testNDSolveValueCanEvaluate() {
    checkApprox("f=NDSolveValue({y'(x)==-y(x),y(0)==1},y,{x,0,10});f(0)", //
        1.0, 0.001);
  }

  // the equations and the initial conditions may be combined with `&&` instead of a list
  @Test
  public void testAndOperatorInsteadOfList() {
    check("NDSolve(y'(x)==y(x) && y(0)==1,y(x),{x,0,1})", //
        "{{y(x)->InterpolatingFunction({{0.0,1.0}},<>)(x)}}");
    checkApprox("sol=NDSolve(y'(x)==y(x) && y(0)==1,y,{x,0,1});y(0.5)/.Part(sol,1)", //
        Math.exp(0.5), 0.001);
    checkApprox(
        "sol=NDSolve(y''(x)+y(x)==0 && y(0)==1 && y'(0)==0,y,{x,0,4});y(N(Pi))/.Part(sol,1)", //
        -1.0, 0.01);
    checkApprox(
        "s=NDSolve(x'(t)==y(t) && y'(t)==-x(t) && x(0)==1 && y(0)==0,{x,y},{t,0,4});fx=x/.Part(s,1);fx(N(Pi))", //
        -1.0, 0.01);
    check("NDSolveValue(y'(x)==-y(x) && y(0)==1,y,{x,0,10})", //
        "InterpolatingFunction({{0.0,10.0}},<>)");
  }

  // blow_up_before_domain_end_returns_a_truncated_interpolating_function
  @Test
  public void testNDSolveBlowUpBeforeDomainEnd() {
    // y' == y^2, y(0) == 1 is 1/(1-t), which has a pole at t == 1. Asking for {t,0,2} used to throw
    // away every point which had been integrated up to the pole and leave NDSolve unevaluated. A
    // shooting method guesses initial values whose trajectory blows up before the endpoint all the
    // time, so failing the whole call on such a guess breaks the search which made it. The solution
    // has to come back as an InterpolatingFunction over the range it did reach instead.
    check("sol=NDSolve({y'(t)==y(t)^2, y(0)==1}, y, {t,0,2});Head(Part(sol,1,1,2))", //
        "InterpolatingFunction");
    // Message NDSolve: At t == 1.0, step size is effectively zero; singularity or stiff system
    // suspected.
    // The range ends just short of the pole rather than at the requested 2. Matched as a pattern
    // because where exactly the stepping gives up is a property of the solver and its tolerances.
    checkRegex("sol=NDSolve({y'(t)==y(t)^2, y(0)==1}, y, {t,0,2});f=y/.Part(sol,1);Normal(f)", //
        "InterpolatingFunction\\(\\{\\{0\\.0,0\\.9\\d*\\}\\},<>\\)");
    // and the part of the solution which was integrated is the right one
    checkApprox("sol=NDSolve({y'(t)==y(t)^2, y(0)==1}, y, {t,0,2});f=y/.Part(sol,1);f(0.5)", //
        2.0, 1e-6);
    checkApprox("sol=NDSolve({y'(t)==y(t)^2, y(0)==1}, y, {t,0,2});f=y/.Part(sol,1);f(0.9)", //
        10.0, 1e-4);
  }

  // the same for an integration which runs backwards from the initial point
  @Test
  public void testNDSolveBlowUpBackwards() {
    checkRegex("sol=NDSolve({y'(t)==y(t)^2, y(0)==-1}, y, {t,-2,0});f=y/.Part(sol,1);Normal(f)", //
        "InterpolatingFunction\\(\\{\\{-0\\.9\\d*,0\\.0\\}\\},<>\\)");
    // y' == y^2, y(0) == -1 is -1/(1+t), with its pole at t == -1
    checkApprox("sol=NDSolve({y'(t)==y(t)^2, y(0)==-1}, y, {t,-2,0});f=y/.Part(sol,1);f(-0.5)", //
        -2.0, 1e-6);
  }
}
