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

  /**
   * A <code>WhenEvent</code> whose first argument is a real valued expression fires where it
   * changes its sign, in both directions; <code>"StopIntegration"</code>, also thrown, ends the
   * solution there.
   */
  @Test
  public void testStopIntegrationDomainEndsAtEvent() {
    check("te = 0; s = NDSolve({y'(t) == -y(t), y(0) == 4, "
        + "WhenEvent(y(t) == 1, te = t; \"StopIntegration\")}, y, {t, 0, 3}); "
        + "{te, (y(\"Domain\") /. First(s))[[1, 2]] == te}", //
        "{1.38629,True}");
  }

  @Test
  public void testWhenEventRealValuedExpression() {
    check("tt = 0; s = NDSolve({y''(t) == -4*y(t), y(0) == 2, y'(0) == 0, "
        + "WhenEvent(y(t) - 1, Throw(tt = t, \"StopIntegration\"))}, y, {t, 0, 5});"
        + "{Round(10^6*tt), Round(10^6*(y(\"Domain\") /. First(s))), Round(10^6*Pi/6)}", //
        "{523599,{{0,523599}},523599}");
    check("tt = 0; NDSolve({y'(t) == -2*y(t), y(0) == 3, "
        + "WhenEvent(3*(y(t) - 1), tt = t; \"StopIntegration\")}, y, {t, 0, 4});"
        + "{Round(10^6*tt), Round(10^6*Log(3)/2)}", //
        "{549306,549306}");
    // Pi/6, 5*Pi/6 and 13*Pi/6, upwards and downwards
    check("tt = {}; NDSolve({y'(t) == Cos(t), y(0) == 0, WhenEvent(y(t) - 1/2, AppendTo(tt, t))}, "
        + "y, {t, 0, 7}); Round(10^5*tt)", //
        "{52360,261799,680678}");
  }

  /** The first event of a fast oscillator is located, with the derivative there. */
  @Test
  public void testWhenEventFastOscillator() {
    check("Table(tt = 0; s = NDSolve({y''(t) == -w^2*y(t), y(0) == 1, y'(0) == 0, "
        + "WhenEvent(y(t) - 1/4, tt = t; \"StopIntegration\")}, y, {t, 0, 50}); f = y /. First(s);"
        + "{Round(10^6*(tt - ArcCos(1/4)/w)), Round(10^3*(f'(tt)/w + Sin(ArcCos(1/4))))}, "
        + "{w, {30, 150, 400}})", //
        "{{0,0},{0,0},{0,0}}");
  }

  /** A machine number initial value of a constant solution is its value everywhere. */
  @Test
  public void testNDSolveConstantSolutionKeepsInitialValue() {
    check("s = y /. First(NDSolve({y'(t) == 0, y(0) == 0.3}, y, {t, 0, 40}));"
        + "Union(Table(s(t) - 0.3, {t, 0, 40, 0.23}))", //
        "{0.0}");
    check("s = y /. First(NDSolve({y'(t) == 0, y(0) == 0.7}, y, {t, -2, 9}));"
        + "Union(Table(s(t) - 0.7, {t, -2, 9, 0.11}))", //
        "{0.0}");
  }

  @Test
  public void testNDSolveAlgebraicEquationWithConsistentDerivativeValue() {
    // w(t) == 3*v(t) fixes w; v'(0) == 3 only repeats what the equations give at t == 0
    checkApprox("sol=NDSolve({v'(t)==w(t),w(t)==3*v(t),v(0)==1,v'(0)==3},{v,w},{t,0,1});"
        + "v(0.5)/.First(sol)", //
        Math.exp(1.5), 1.0e-6);
    checkApprox("sol=NDSolve({v'(t)==w(t),w(t)==3*v(t),v(0)==1,v'(0)==3},{v,w},{t,0,1});"
        + "w(0.5)/.First(sol)", //
        3.0 * Math.exp(1.5), 1.0e-5);
    // an inconsistent derivative value isn't dropped
    check("NDSolve({v'(t)==w(t),w(t)==3*v(t),v(0)==1,v'(0)==2},{v,w},{t,0,1})", //
        "NDSolve({v'(t)==w(t),w(t)==3*v(t),v(0)==1,v'(0)==2},{v,w},{t,0,1})");
  }

  @Test
  public void testNDSolveIndexArgumentUnknowns() {
    // the unknowns q(1, t), q(2, t) of a function q whose first argument is an index
    checkApprox("s=NDSolve({Derivative(0,1)[q][1,t]==2*q(2,t),Derivative(0,1)[q][2,t]==-2*q(1,t),"
        + "q(1,0)==0,q(2,0)==1},{q(1,t),q(2,t),Derivative(0,1)[q][2,t]},{t,0,1});"
        + "q(1,t)/.First(s)/.t->0.25", //
        Math.sin(0.5), 1.0e-6);
    checkApprox("s=NDSolve({Derivative(0,1)[q][1,t]==2*q(2,t),Derivative(0,1)[q][2,t]==-2*q(1,t),"
        + "q(1,0)==0,q(2,0)==1},{q(1,t),q(2,t),Derivative(0,1)[q][2,t]},{t,0,1});"
        + "Derivative(0,1)[q][2,t]/.First(s)/.t->0.25", //
        -2.0 * Math.sin(0.5), 1.0e-5);
    check("s=NDSolve({Derivative(0,1)[q][1,t]==2*q(2,t),Derivative(0,1)[q][2,t]==-2*q(1,t),"
        + "q(1,0)==0,q(2,0)==1},{q(1,t),q(2,t),Derivative(0,1)[q][2,t]},{t,0,1});"
        + "Keys(First(s))", //
        "{q(1,t),q(2,t),Derivative(0,1)[q][2,t]}");
  }

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

  @Test
  public void testNDSolveEventLocatorListOfEvents() {
    // of the two events y(t)==0.8 and y(t)==0.25 of y'==-y, y(0)==1 the first one met, at
    // Log(5/4), stops the integration
    checkNumeric(
        "s=NDSolve({y'(t)==-y(t),y(0)==1},y,{t,0,10},Method->{\"EventLocator\",\"Event\"->{y(t)-0.25,y(t)-0.8},\"EventAction\":>Throw(stopT=t,\"StopIntegration\")});fy=y/.Part(s,1);{stopT,fy(stopT)}", //
        "{0.22314355131420976,0.8}", 1.0e-8);
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

  // the unknown written with the space variable first, its derivatives and its domain in that
  // order
  @Test
  public void testNDSolvePDESpaceFirst() {
    check("u=NDSolveValue({D(w(x,t),t)==D(w(x,t),x,x), w(x,0)==Sin(Pi*x), w(0,t)==0, w(1,t)==0}, "
        + "w, {t,0,2}, {x,0,1});{Abs(u(0.25,0.05)-Sin(Pi/4)*Exp(-Pi^2*0.05))<10^-3, u(\"Domain\")}", //
        "{True,{{0.0,1.0},{0.0,2.0}}}");
  }

  // the slope at an end, a DirichletCondition written into the equation or given at the start
  @Test
  public void testNDSolvePDEEndConditions() {
    // the steady state of w(t,0) == 0 and a slope 1/2 at x == 2 is w = x/2
    check("u=NDSolveValue({D(w(t,x),t)==3*D(w(t,x),x,x), w(0,x)==0, w(t,0)==0, "
        + "Derivative(0,1)[w][t,2]==1/2}, w, {t,0,20}, {x,0,2});Round(1000*{u(20,1),u(20,2)})", //
        "{500,1000}");
    check("u=NDSolveValue({D(w(x,t),t)==D(w(x,t),x,x), w(x,0)==0, Derivative(1,0)[w][0,t]==-2, "
        + "w(3,t)==0}, w, {t,0,40}, {x,0,3});Round(1000*{u(0,40),u(1,40)})", //
        "{6000,4000}");
    // both ends insulated: the cosine mode decays as Exp(-k^2*t)
    check("u=NDSolveValue({D(w(t,x),t)==D(w(t,x),x,x), w(0,x)==Cos(2*x), "
        + "Derivative(0,1)[w][t,0]==0, Derivative(0,1)[w][t,Pi]==0}, w, {t,0,1/4}, {x,0,Pi});"
        + "Abs(u(1/4,1/2)-Exp(-1)*Cos(1))<10^-3", //
        "True");
    check("u=NDSolveValue({D(w(t,x),t)==D(w(t,x),x,x)+DirichletCondition(w(t,x)==1,x==0)"
        + "+DirichletCondition(w(t,x)==3,x==2), DirichletCondition(w(t,x)==1,t==0)}, w, "
        + "{t,0,10}, {x,0,2});Round(1000*u(10,1))", //
        "2000");
  }

  // an If on the state switches the right hand side where its test changes, followed exactly
  @Test
  public void testNDSolveIfSwitch() {
    check("s=NDSolveValue({y'(t)==If(y(t)<1/100,5000,(2-y(t))/10), y(0)==0}, y, {t,0,300});"
        + "{s(\"Domain\"), Abs(s(10^-6)-1/200)<10^-8, Abs(s(50)-(2-199/100*Exp(-5)))<10^-5}", //
        "{{{0.0,300.0}},True,True}");
    check("s=NDSolveValue({y'(t)==If(t>=1,0,3), y(0)==0}, y, {t,0,2});Abs(s(2)-3)<10^-8", //
        "True");
    check("s=NDSolveValue({y'(t)==If(y(t)>1,-1,1), y(0)==0}, y, {t,0,3});Abs(s(1/2)-1/2)<10^-8", //
        "True");
  }

  // a NeumannValue is the flux into the bar through its end: the total grows by it
  @Test
  public void testNDSolvePDENeumannValue() {
    check("u=NDSolveValue({D(w(x,t),t)-3*D(w(x,t),x,x)==NeumannValue(2,x==0)+NeumannValue(0,x==1), "
        + "w(x,0)==0}, w, {t,0,1/2}, {x,0,1});Round(100*NIntegrate(u(x,0.5),{x,0,1}))", //
        "100");
    // an end without a condition is insulated: w = t + (x-1)^2/2 - 1/6 once the start has died away
    check("u=NDSolveValue({D(w(x,t),t)-D(w(x,t),x,x)==NeumannValue(1,x==0), w(x,0)==0}, "
        + "w, {t,0,1}, {x,0,1});{Abs(u(0,1)-4/3)<10^-3, Abs(u(1,1)-5/6)<10^-3}", //
        "{True,True}");
    // a value at one end and a flux at the other; the steady state is w = x/2
    check("u=NDSolveValue({D(w(x,t),t)==D(w(x,t),x,x)+NeumannValue(1/2,x==1), w(x,0)==0, "
        + "DirichletCondition(w(x,t)==0,x==0)}, w, {t,0,6}, {x,0,1});Abs(u(0.6,6)-0.3)<10^-3", //
        "True");
    // before the steady state
    check("u=NDSolveValue({D(w(x,t),t)==D(w(x,t),x,x)+NeumannValue(1,x==1), w(x,0)==0, "
        + "DirichletCondition(w(x,t)==0,x==0)}, w, {t,0,5}, {x,0,1});Abs(u(1,1.0)-0.93128)<10^-4", //
        "True");
    // with a drift term the NeumannValue is still the diffusive flux
    check("u=NDSolveValue({D(w(x,t),t)==D(w(x,t),x,x)-D(w(x,t),x)+NeumannValue(1,x==0), "
        + "w(x,0)==0}, w, {t,0,1}, {x,0,1});Abs(u(0.5,1)-1.49114)<10^-4", //
        "True");
  }

  // a field which diffuses, coupled through its end to a quantity without a space derivative: what
  // the one gains the other loses
  @Test
  public void testNDSolvePDECoupledReservoir() {
    check("s=NDSolveValue({D(p(x,t),t)-2*D(p(x,t),x,x)==NeumannValue(-(p(x,t)-q(x,t)),x==1), "
        + "p(x,0)==1, 3*D(q(x,t),t)==-(q(x,t)-p(x,t)), q(x,0)==0}, {p,q}, {t,0,1}, {x,0,1});"
        + "Round(1000*(NIntegrate(s[[1]](x,1),{x,0,1})+3*s[[2]](1,1)))", //
        "1000");
  }

  @Test
  public void testNDSolveValueOfExpression() {
    // a value of the solution, not the solution: y(x) = E^(-2*x)
    checkApprox("NDSolveValue({y'(x)==-2*y(x),y(0)==1},y(1),{x,0,1})", //
        Math.exp(-2.0), 1.0e-6);
    checkApprox("NDSolveValue({y'(x)==-2*y(x),y(0)==1},y(1/2)+y(1),{x,0,1})", //
        Math.exp(-1.0) + Math.exp(-2.0), 1.0e-6);
  }

  @Test
  public void testParametricNDSolveValueFunctionOfParameter() {
    // y(x) = 3*E^(c*x)
    check("pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y,{x,0,2},{c});"
        + "{Head(pf),MatchQ(pf,_ParametricFunction),Head(pf(0.5))}", //
        "{ParametricFunction,True,InterpolatingFunction}");
    checkApprox(
        "pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y,{x,0,2},{c});f=pf(0.5);f(2.0)", //
        3.0 * Math.exp(1.0), 1.0e-5);
    checkApprox(
        "pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y,{x,0,2},{c});f=pf(-3/4);f(2.0)", //
        3.0 * Math.exp(-1.5), 1.0e-5);
  }

  @Test
  public void testParametricNDSolveValueExpressionTargets() {
    // u(s) = h*Cos(w*s); the parameter w is given with a range
    checkApprox("pf=ParametricNDSolveValue({u''(s)==-w^2*u(s),u(0)==h,u'(0)==0},u(1),{s,0,1},"
        + "{{w,1,3},h});pf(2.0,0.5)", //
        0.5 * Math.cos(2.0), 1.0e-5);
    checkApprox("pf=ParametricNDSolveValue({u''(s)==-w^2*u(s),u(0)==h,u'(0)==0},{u(1),u'(1)},"
        + "{s,0,1},{w,h});v=pf(2.0,0.5);v[[2]]", //
        -0.5 * 2.0 * Math.sin(2.0), 1.0e-5);
  }

  @Test
  public void testParametricNDSolveValueSolutionsAreKept() {
    // the same parameter value gives the same solution object, not a second solution
    check("pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y,{x,0,2},{c});"
        + "{pf(0.25)===pf(0.25),pf(0.25)===pf(0.75)}", //
        "{True,False}");
    // only a few recent values are kept: after a sweep over 20 others the first one is solved
    // again, with the same values
    check("pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y,{x,0,2},{c});f=pf(0.25);"
        + "Do(pf(k/10.0),{k,20});g=pf(0.25);{f===g,f(2.0)==g(2.0),pf(2.0)===pf(2.0)}", //
        "{False,True,True}");
  }

  @Test
  public void testParametricNDSolveValueOutsideRangeAndComplex() {
    // a range doesn't restrict the values; y(1) = 3*E^c
    checkApprox("pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y(1),{x,0,2},{{c,0,1}});"
        + "pf(-2.0)", //
        3.0 * Math.exp(-2.0), 1.0e-5);
    check("pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y(1),{x,0,2},{{c,0,1}});"
        + "v=pf(I);Round(10^5*{Re(v),Im(v)})", //
        "{162091,252441}");
  }

  @Test
  public void testParametricNDSolveValueUnevaluated() {
    // a symbolic value stays without a message, the wrong number of values gives one
    check("pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y,{x,0,2},{c});"
        + "{pf(z),Check(pf(1.0,2.0),err)}", //
        "{ParametricFunction(<>)[z],err}");
    // fewer values than parameters give the same message
    check("pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==d},y,{x,0,2},{c,d});"
        + "{Check(pf(1.0),err),pf(1.0)}", //
        "{err,ParametricFunction(<>)[1.0]}");
    // no parameter symbol
    check("Check(ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y,{x,0,2},{Sin}),err)", //
        "err");
    check("ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y,{x,0,2},{Sin})", //
        "ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y,{x,0,2},{Sin})");
  }

  @Test
  public void testParametricNDSolveValueRepeatedParameter() {
    // a repeated parameter takes one value per position; the first one is used
    check("pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y(1),{x,0,2},{c,c});"
        + "{Head(pf),Check(pf(0.5),err)}", //
        "{ParametricFunction,err}");
    checkApprox("pf=ParametricNDSolveValue({y'(x)==c*y(x),y(0)==3},y(1),{x,0,2},{c,c});"
        + "pf(0.5,7.0)", //
        3.0 * Math.exp(0.5), 1.0e-5);
  }
}
