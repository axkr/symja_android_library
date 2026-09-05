package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/** Tests for DSolve */
public class DSolveTest extends ExprEvaluatorTestCase {

  @Test
  public void testDSolveUnevaluated() {
    // DSolve: For some branches of the general solution, unable to solve for the conditions
    check("DSolve({a *(f'(x))^2+f'''(x)==0,f'(0)==0},f(x),x)", //
        "DSolve({a*f'(x)^2+Derivative(3)[f][x]==0,f'(0)==0},f(x),x)");
  }

  @Test
  public void testDSolve001() {
    check("DSolve(y''(x) == 0, y(x), x)", //
        "{{y(x)->C(1)+x*C(2)}}");
    check("DSolve(y''(x) == y(x), y(x), x)", //
        "{{y(x)->C(1)/E^x+E^x*C(2)}}");
    check("DSolve(y''(x) == y(x), y, x)", //
        "{{y->Function({x},C(1)/E^x+E^x*C(2))}}");
    check("DSolve(D(f(x, y), x)/f(x, y) + 3*D(f(x, y), y) / f(x, y) == 2, f, {x, y})", //
        "{{f->Function({x,y},E^(2*x)*C(1)[-3*x+y])}}");
    check("DSolve(D(f(x, y), x)*x + D(f(x, y), y)*y == 2, f(x, y), {x, y})", //
        "{{f(x,y)->2*Log(x)+C(1)[y/x]}}");
    check("DSolve(D(y(x, t), t) + 2*D(y(x, t), x) == 0, y(x, t), {x, t})", //
        "{{y(x,t)->C(1)[1/2*(2*t-x)]}}");
  }

  @Test
  public void testDSolve002() {
    check("DSolve({y'(x)==2*x*y(x)^2},y(x), x)", //
        "{{y(x)->1/(-x^2-C(1))}}");
    check("DSolve(y'(t)==t+y(t), y, t)", //
        "{{y->Function({t},-1-t+E^t*C(1))}}");

    check("DSolve(y'(x)==2*x*y(x)^2,Null,x)", //
        "DSolve(y'(x)==2*x*y(x)^2,Null,x)");
    check("DSolve({},y,t)", //
        "DSolve({},y,t)");

    check("DSolve(y'(t)==y(t), y, t)", //
        "{{y->Function({t},E^t*C(1))}}");

    check("DSolve(y'(x)==2*x*y(x)^2, y, x)", //
        "{{y->Function({x},1/(-x^2-C(1)))}}");
    check("DSolve(y'(x)==2*x*y(x)^2, y(x), x)", //
        "{{y(x)->1/(-x^2-C(1))}}");
    check("DSolve({y'(x)==2*x*y(x)^2},y(x), x)", //
        "{{y(x)->1/(-x^2-C(1))}}");

    check("DSolve(D(f(x, y), x) == D(f(x, y), y), f, {x, y})", //
        "{{f->Function({x,y},C(1)[x+y])}}");

    check("DSolve({y'(x)==y(x)+2,y(0)==1},y(x), x)", //
        "{{y(x)->-2+3*E^x}}");

    check("DSolve(y'(x) + y(x) == a*Sin(x), y(x), x)", //
        "{{y(x)->C(1)/E^x-1/2*a*Cos(x)+1/2*a*Sin(x)}}");

    check("DSolve(y'(x)-x ==0, y(x), x)", //
        "{{y(x)->x^2/2+C(1)}}");
    check("DSolve(y'(x)+k*y(x) ==0, y(x), x)", //
        "{{y(x)->C(1)/E^(k*x)}}");

    check("DSolve(y'(x)-3/x*y(x)-7==0, y(x), x)", //
        "{{y(x)->-7/2*x+x^3*C(1)}}");
    check("DSolve(y'(x)== 0, y(x), x)", //
        "{{y(x)->C(1)}}");
    check("DSolve(y'(x) + y(x)*Tan(x) == 0, y(x), x)", //
        "{{y(x)->C(1)*Cos(x)}}");
    check("DSolve(y'(x) + y(x)*Cos(x) == 0, y(x), x)", //
        "{{y(x)->C(1)/E^Sin(x)}}");
    check("DSolve(y'(x) == 3*y(x), y(x), x)", //
        "{{y(x)->E^(3*x)*C(1)}}");
    check("DSolve(y'(x) + 2*y(x)/(1-x^2) == 0, y(x), x)", //
        "{{y(x)->C(1)/(1+x)+(-x*C(1))/(1+x)}}");
    check("DSolve(y'(x) == -y(x), y(x), x)", //
        "{{y(x)->C(1)/E^x}}");
    check("DSolve(y'(x) == y(x)+a*Cos(x), y(x), x)", //
        "{{y(x)->E^x*C(1)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");
    // not implemented yet
    check("DSolve(y'(x) == -3*y(x)^2, y(x), x)", //
        "{{y(x)->1/(3*x-C(1))}}");
    check("DSolve({y'(x) == -3*y(x)^2, y(0)==2}, y(x), x)", //
        "{{y(x)->2/(1+6*x)}}");
  }

  @Test
  public void testDSolveSystem001() {
    check("tst(0)=1;DSolve({a *(f'(x))^2+f'''(x)==0,f'(0)==0,tst(0)==0},f(x),x)", //
        "DSolve({a*f'(x)^2+Derivative(3)[f][x]==0,f'(0)==0,False},f(x),x)");

    // Simple harmonic oscillator
    check("DSolve({x'(t)==y(t), y'(t)==-x(t)}, {x(t), y(t)}, t)", //
        "{{x(t)->C(1)*Cos(t)+C(2)*Sin(t),y(t)->C(2)*Cos(t)-C(1)*Sin(t)}}");

    // Uncoupled diagonal system
    check("DSolve({x'(t)==2*x(t), y'(t)==3*y(t)}, {x(t), y(t)}, t)", //
        "{{x(t)->E^(2*t)*C(1),y(t)->E^(3*t)*C(2)}}");

    // Nilpotent matrix system (yields polynomial solutions)
    check("DSolve({x'(t)==y(t), y'(t)==0}, {x(t), y(t)}, t)", //
        "{{x(t)->C(1)+t*C(2),y(t)->C(2)}}");
  }

  @Test
  public void testDSolveSystemPureFunction() {
    // Pure function return type for systems
    check("DSolve({x'(t)==y(t), y'(t)==-x(t)}, {x, y}, t)", //
        "{{x->Function({t},C(1)*Cos(t)+C(2)*Sin(t)),y->Function({t},C(2)*Cos(t)-C(1)*Sin(t))}}");
  }

  @Test
  public void testDSolveSystemOrder2() {
    check("Solve[{y''(x)+4  == 0}, y''(x)]", // )
        "{{y''(x)->-4}}");
    check("DSolve({y''(x)+4*y(x) == 7}, y(x), x)", //
        "{{y(x)->7/4+C(1)*Cos(2*x)+C(2)*Sin(2*x)}}");
  }

  @Test
  public void testDSolveSystemEqns() {
    check("DSolve({y'(x)-3*z(x) == Sin(x), y(x) + z(x) == 1/5, y(Pi/2) == 1/2}, {y, z}, x)", //
        "{{y->Function({x},1/10*(2-Cos(x)+3*Sin(x))),z->Function({x},1/10*(Cos(x)-3*Sin(x)))}}");
  }

  @Test
  public void testDSolveRiccati() {
    // Full Riccati Equation with constant coefficients: y'(x) = y(x)^2 + 1
    // Matches the separation of variables logic, leading to ArcTan integration
    check("DSolve(y'(x) == y(x)^2 + 1, y(x), x)", //
        "{{y(x)->Tan(x+C(1))}}");

    // Full Riccati Equation with different constants: y'(x) = y(x)^2 - 1
    check("DSolve(y'(x) == y(x)^2 - 1, y(x), x)", //
        "{{y(x)->-E^x/(E^x+C(1)/E^x)+C(1)/(E^x*(E^x+C(1)/E^x))}}");
  }

  @Test
  public void testDSolveBernoulli() {
    // Pure Bernoulli Equation (Riccati with c=0): y'(x) = y(x)^2
    // y = -u'/a*u -> triggers the Bernoulli bypass
    check("DSolve(y'(x) == y(x)^2, y(x), x)", //
        "{{y(x)->1/(-x-C(1))}}");

    // Bernoulli Equation with linear term: y'(x) = y(x)^2 + y(x)
    // Converts to linear ODE u' + u = -1
    check("DSolve(y'(x) == y(x)^2 + y(x), y(x), x)", //
        "{{y(x)->1/(-1+C(1)/E^x)}}");

    // Alternate format detection: y'(x) - y(x)^2 == 0
    check("DSolve(y'(x) - y(x)^2 == 0, y(x), x)", //
        "{{y(x)->1/(-x-C(1))}}");
  }

  @Test
  public void testDSolveBernoulliGeneral() {
    // Bernoulli equation with n=3: y'(x) - y(x) = y(x)^3
    // Standard substitution u = y^-2 leads to linear ODE u' + 2u = -2
    check("DSolve(y'(x) - y(x) == y(x)^3, y(x), x)", //
        "{{y(x)->-1/Sqrt(-1+C(1)/E^(2*x))},{y(x)->1/Sqrt(-1+C(1)/E^(2*x))}}");

    // Bernoulli equation with variable coefficients and n=3: x*y'(x) + y(x) == x^3*y(x)^3
    // Transforms to y' + (1/x)y = x^2 y^3
    check("DSolve(x*y'(x) + y(x) == x^3*y(x)^3, y(x), x)", //
        "{{y(x)->-1/Sqrt(-2*x^3+x^2*C(1))},{y(x)->1/Sqrt(-2*x^3+x^2*C(1))}}");

    // Bernoulli equation with n=4: y'(x) + y(x) == x*y(x)^4
    // u = y^-3 leads to u' - 3u = -3x
    check("DSolve(y'(x) + y(x) == x*y(x)^4, y(x), x)", //
        "{{y(x)->1/(1/3+x+E^(3*x)*C(1))^(1/3)}}");
  }

  @Test
  public void testDSolveExact() {
    // Exact ODE (Non-separable, non-linear): (2x + y^2) + (2xy)y' = 0
    // M = 2x + y^2, N = 2xy. dM/dy = 2y = dN/dx.
    // Implicit solution is x^2 + x*y^2 = C. Solving for y extracts the first root.
    check("DSolve(2*x + y(x)^2 + 2*x*y(x)*y'(x) == 0, y(x), x)", //
        "{{y(x)->Sqrt(-x^2+C(1))/Sqrt(x)},{y(x)->-Sqrt(-x^2+C(1))/Sqrt(x)}}");

    // Exact ODE (Non-separable, non-linear): (y^2 + 2xy) + (2xy + x^2)y' = 0
    // M = y^2 + 2xy, N = 2xy + x^2. dM/dy = 2y + 2x = dN/dx.
    // Implicit solution is x*y^2 + x^2*y = C.
    check("DSolve(y(x)^2 + 2*x*y(x) + (2*x*y(x) + x^2)*y'(x) == 0, y(x), x)", //
        "{{y(x)->-x/2-Sqrt(x^4+4*x*C(1))/(2*x)},{y(x)->-x/2+Sqrt(x^4+4*x*C(1))/(2*x)}}");
  }

  @Test
  public void testDSolveHomogeneous() {
    // Classic Homogeneous: y' = (y + x) / x => x*y' - y - x = 0
    // Substitute y = v*x leads to v' = 1/x => v = Log(x) + C => y = x*(Log(x) + C)
    check("DSolve(x*y'(x) - y(x) - x == 0, y(x), x)", //
        "{{y(x)->x*C(1)+x*Log(x)}}");

    // Quadratic Homogeneous: y' = (y^2 + x^2) / (x*y) => x*y*y' - y^2 - x^2 = 0
    // Substitute y = v*x
    check("DSolve(x*y(x)*y'(x) - y(x)^2 - x^2 == 0, y(x), x)", //
        "{{y(x)->-Sqrt(2*x^2*C(1)+2*x^2*Log(x))},{y(x)->Sqrt(2*x^2*C(1)+2*x^2*Log(x))}}");
  }

  @Test
  public void testDSolveClairaut() {
    // Basic Clairaut: y = x*y' + (y')^2
    // Expected general solution: y = C_1*x + C_1^2
    check("DSolve(y(x) - x*y'(x) - y'(x)^2 == 0, y(x), x)", //
        "{{y(x)->x*C(1)+C(1)^2}}");

    // Clairaut with trigonometric function: y = x*y' + Sin(y')
    // Expected general solution: y = C_1*x + Sin(C_1)
    check("DSolve(y(x) == x*y'(x) + Sin(y'(x)), y(x), x)", //
        "{{y(x)->x*C(1)+Sin(C(1))}}");

    // Alternate arrangement checking: -y + x*y' + Exp(y') = 0
    check("DSolve(-y(x) + x*y'(x) + E^y'(x) == 0, y(x), x)", //
        "{{y(x)->E^C(1)+x*C(1)}}");
  }

  @Test
  public void testDSolveIntegratingFactor() {
    // Integrating factor depending only on x: mu(x) = x
    // ODE: (x^2 + y^2 + x) + (x*y)*y' = 0
    // Exact equation becomes: (x^3 + x*y^2 + x^2) + (x^2*y)*y' = 0
    // Result implicit: 1/2*x^2*y^2 + 1/4*x^4 + 1/3*x^3 = C_1
    check("DSolve(x^2 + y(x)^2 + x + x * y(x) * y'(x) == 0, y(x), x)", //
        "{{y(x)->Sqrt(-4*x^3-3*x^4+12*C(1))/(Sqrt(6)*x)},{y(x)->-Sqrt(-4*x^3-3*x^4+12*C(1))/(Sqrt(\n" //
            + "6)*x)}}");

    // Integrating factor depending only on y: mu(y) = 1/y^2
    // ODE: y + (y^2 - x)*y' = 0
    // Exact equation becomes: 1/y + (1 - x/y^2)*y' = 0
    // Result implicit: x/y + y = C_1 => y^2 - C_1*y + x = 0
    check("DSolve(y(x) + (y(x)^2 - x) * y'(x) == 0, y(x), x)", //
        "{{y(x)->C(1)/2-Sqrt(-4*x+C(1)^2)/2},{y(x)->C(1)/2+Sqrt(-4*x+C(1)^2)/2}}");
  }

  @Test
  public void testDSolveMultipleConstants() {
    check("DSolve(25*y(x)+10*y'(x)+y''(x)==0,y(x),x)", //
        "{{y(x)->C(1)/E^(5*x)+(x*C(2))/E^(5*x)}}");
  }

  @Test
  public void testDSolveNoArgMessage() {
    // message: The function Derivative(1)[y] appears with no arguments.
    check("DSolve(25*y+10*y'(x)+y''(x)==0, y(x), x)", //
        "DSolve(25*y+10*y'(x)+y''(x)==0,y(x),x)");
    // message: The function Derivative(1)[y] appears with no arguments.
    check("DSolve(25*y(x)+10*y'+y''(x)==0, y(x), x)", //
        "DSolve(25*y(x)+10*Derivative(1)[y]+y''(x)==0,y(x),x)");
  }

  @Test
  public void testDSolvePDE() {
    // Basic Homogeneous PDE: u_x + u_y = 0
    // Expected general solution: u(x,y) = C_1(y - x)
    check("DSolve(D(u(x, y), x) + D(u(x, y), y) == 0, u(x,y), {x, y})", //
        "{{u(x,y)->C(1)[-x+y]}}");

    // Non-Homogeneous PDE: u_x + u_y = x
    // Equation contains a loose term 'x'. General solution: u(x,y) = x^2/2 + C_1(y - x)
    check("DSolve(D(u(x, y), x) + D(u(x, y), y) == x, u(x,y), {x, y})", //
        "{{u(x,y)->x^2/2+C(1)[-x+y]}}");

    // Quasilinear PDE with variable coefficients: x*u_x + y*u_y = 2*u
    // General solution: u(x,y) = x^2 * C_1(y/x)
    check("DSolve(x*D(u(x, y), x) + y*D(u(x, y), y) == 2*u(x,y), u(x,y), {x, y})", //
        "{{u(x,y)->x^2*C(1)[y/x]}}");

    check("DSolve(D(y(x, t), t) + 2 D(y(x, t), x) == 0, y(x, t), {x, t})", //
        "{{y(x,t)->C(1)[1/2*(2*t-x)]}}");
  }

  @Test
  public void testDSolveBoundaryWithCoefficient() {
    // =====================================================================
    // Tests for improved solveSingleBoundary that handles non-trivial
    // boundary conditions (coefficients, fractions, combined terms).
    // =====================================================================

    // Boundary condition with integer coefficient: 2*y(0)==6 means y(0)=3
    // General solution of y'(x)==y(x) is E^x*C(1); with C(1)=3 -> 3*E^x
    check("DSolve({y'(x)==y(x), 2*y(0)==6}, y(x), x)", //
        "{{y(x)->3*E^x}}");

    // Boundary condition with fractional coefficient: y(0)/3==2 means y(0)=6
    check("DSolve({y'(x)==y(x), y(0)/3==2}, y(x), x)", //
        "{{y(x)->6*E^x}}");

    // Combined same-point terms: 3*y(0)+2*y(0)==10 evaluates to 5*y(0)==10, y(0)=2
    check("DSolve({y'(x)==y(x), 3*y(0)+2*y(0)==10}, y(x), x)", //
        "{{y(x)->2*E^x}}");

    // Coefficient form equivalent to known result: 2*y(0)==2 means y(0)=1
    // Must match: DSolve({y'(x)==y(x)+2, y(0)==1}, y(x), x) == {{y(x)->-2+3*E^x}}
    check("DSolve({y'(x)==y(x)+2, 2*y(0)==2}, y(x), x)", //
        "{{y(x)->-2+3*E^x}}");

    // Non-linear ODE with coefficient boundary condition: 3*y(0)==6 means y(0)=2
    // Must match: DSolve({y'(x)==-3*y(x)^2, y(0)==2}, y(x), x) == {{y(x)->2/(1+6*x)}}
    check("DSolve({y'(x)==-3*y(x)^2, 3*y(0)==6}, y(x), x)", //
        "{{y(x)->2/(1+6*x)}}");
  }

  @Test
  public void testDSolveMultipleBoundaryConditions() {
    // ==========================================================================
    // Tests for multiple boundary/initial conditions on higher-order ODEs.
    // The general solution of y'' + y == 0 is C(1)*Cos(x) + C(2)*Sin(x).
    // Two BCs are needed to fully determine both constants.
    // ==========================================================================

    // IVP: y(0)==1, y'(0)==0 → C(1)=1, C(2)=0 → Cos(x)
    check("DSolve({y''(x) + y(x) == 0, y(0)==1, y'(0)==0}, y(x), x)", //
        "{{y(x)->Cos(x)}}");

    // IVP: y(0)==0, y'(0)==1 → C(1)=0, C(2)=1 → Sin(x)
    check("DSolve({y''(x) + y(x) == 0, y(0)==0, y'(0)==1}, y(x), x)", //
        "{{y(x)->Sin(x)}}");

    // IVP with non-unit values: y(0)==2, y'(0)==3 → C(1)=2, C(2)=3
    check("DSolve({y''(x) + y(x) == 0, y(0)==2, y'(0)==3}, y(x), x)", //
        "{{y(x)->2*Cos(x)+3*Sin(x)}}");

    // BCs can appear in any position among the equations
    check("DSolve({y(0)==0, y''(x) + y(x) == 0, y'(0)==1}, y(x), x)", //
        "{{y(x)->Sin(x)}}");

    // Non-homogeneous 2nd-order: y'' + 4*y == 7 with two BCs
    // General solution: 7/4 + C(1)*Cos(2x) + C(2)*Cos(x)*Sin(x)
    // y(0)==7/4 → C(1)=0; y'(0)==2 → 2*C(2)=2 → C(2)=1
    check("DSolve({y''(x) + 4*y(x) == 7, y(0)==7/4, y'(0)==2}, y(x), x)", //
        "{{y(x)->7/4+Sin(2*x)}}");

    // ==========================================================================
    // Regression: single BC for first-order ODE still works via applyUnaryBCs
    // ==========================================================================

    check("DSolve({y'(x)==y(x)+2, y(0)==1}, y(x), x)", //
        "{{y(x)->-2+3*E^x}}");

    check("DSolve({y'(x)==y(x), 2*y(0)==6}, y(x), x)", //
        "{{y(x)->3*E^x}}");

    check("DSolve({y(0)==0, y'(x) + y(x) == a*Sin(x)}, y(x), x)", //
        "{{y(x)->a/(2*E^x)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");

    check("DSolve({y'(x) == -3*y(x)^2, y(0)==2}, y(x), x)", //
        "{{y(x)->2/(1+6*x)}}");
  }

  @Test
  void testDegenerateNonODE() {
    // No derivative present
    check("DSolve(x + y(x) == 0, y(x), x)", //
        "{{y(x)->-x}}");
    check("DSolve(y(x)^2 - x == 0, y(x), x)", //
        "{{y(x)->-Sqrt(x)},{y(x)->Sqrt(x)}}");
  }

  @Test
  public void testDSolveSystemPDE() {
    // Decoupled system of homogeneous PDEs
    // u_x + u_y = 0 => u = C(1)[y - x]
    // v_x - v_y = 0 => v = C(2)[x + y]
    check(
        "DSolve({D(u(x, y), x) + D(u(x, y), y) == 0, "
            + "D(v(x, y), x) - D(v(x, y), y) == 0}, {u(x,y), v(x,y)}, {x, y})", //
        "{{u(x,y)->C(1)[-x+y],v(x,y)->C(2)[x+y]}}");

    // Same system with pure function output
    check(
        "DSolve({D(u(x, y), x) + D(u(x, y), y) == 0, "
            + "D(v(x, y), x) - D(v(x, y), y) == 0}, {u, v}, {x, y})", //
        "{{u->Function({x,y},C(1)[-x+y]),v->Function({x,y},C(2)[x+y])}}");

    // Decoupled system with a non-homogeneous term in the first equation
    check(
        "DSolve({D(u(x, y), x) + D(u(x, y), y) == x, "
            + "D(v(x, y), x) + D(v(x, y), y) == 0}, {u(x,y), v(x,y)}, {x, y})", //
        "{{u(x,y)->x^2/2+C(1)[-x+y],v(x,y)->C(2)[-x+y]}}");

    // Equations in reversed order relative to functions — matching should still work
    check(
        "DSolve({D(v(x, y), x) - D(v(x, y), y) == 0, "
            + "D(u(x, y), x) + D(u(x, y), y) == 0}, {u(x,y), v(x,y)}, {x, y})", //
        "{{u(x,y)->C(1)[-x+y],v(x,y)->C(2)[x+y]}}");
  }

  @Test
  public void testDSolveEulerCauchy() {
    // Second Order Homogeneous with repeated roots
    // Characteristic equation: r^2 - 2r + 1 = 0 -> r = 1
    // The second linearly independent solution is x*Log(x)
    check("DSolve(x^2*y''(x) - x*y'(x) + y(x) == 0, y(x), x)", //
        "{{y(x)->x*C(1)+x*C(2)*Log(x)}}");

    // Second Order Homogeneous with distinct roots
    // Characteristic equation: r^2 - 4r + 3 = 0 -> r = 1, 3
    check("DSolve(x^2*y''(x) - 3*x*y'(x) + 3*y(x) == 0, y(x), x)", //
        "{{y(x)->x*C(1)+x^3*C(2)}}");

    // Non-Homogeneous Euler-Cauchy
    // x^2*y'' - 2*y = x transforms to u'' - u' - 2u = e^t
    // Particular solution is -(1/2)x
    check("DSolve(x^2*y''(x) - 2*y(x) == x, y(x), x)", //
        "{{y(x)->-x/2+C(1)/x+x^2*C(2)}}");
  }

  @Test
  public void testDSolveReductionOfOrder() {
    // Missing dependent variable y(x): x*y'' - y' = 0
    // Substitute y' = v -> x*v' - v = 0 -> v = C_1*x -> y = C_2 + x^2/2 * C_1
    // (Note: the constant absorption absorbs the 1/2 multiplier)
    check("DSolve(x*y''(x) - y'(x) == 0, y(x), x)", //
        "{{y(x)->1/2*x^2*C(1)+C(2)}}");

    // Missing independent variable x: y*y'' + (y')^2 == 0
    // Substitute y' = v(y) -> y*v*v' + v^2 = 0 -> v = C_1/y
    // Backsubstitute: y' = C_1/y -> y^2/2 = C_1*x + C_2
    check("DSolve(y(x)*y''(x) + y'(x)^2 == 0, y(x), x)", //
        "{{y(x)->-Sqrt(2*x*C(1)-C(2))},{y(x)->Sqrt(2*x*C(1)-C(2))}}");
  }

  @Test
  public void testDSolveSpecialFunctions() {
    // 1. Standard Airy's Equation: y'' - x*y = 0
    // Maps directly to AiryAi and AiryBi functions
    check("DSolve(y''(x) - x*y(x) == 0, y(x), x)", //
        "{{y(x)->AiryAi(x)*C(1)+AiryBi(x)*C(2)}}");

    // 2. Generalized Airy's Equation: y'' + x*y = 0
    // Tests the correct fractional power mapping for q = -1 -> (-1)^(1/3)
    check("DSolve(y''(x) + x*y(x) == 0, y(x), x)", //
        "{{y(x)->AiryAi((-1)^(1/3)*x)*C(1)+AiryBi((-1)^(1/3)*x)*C(2)}}");

    // 3. Standard Bessel's Equation: x^2 y'' + x y' + (x^2 - a^2) y = 0
    // General symbolic parameter 'a'
    check("DSolve(x^2*y''(x) + x*y'(x) + (x^2 - a^2)*y(x) == 0, y(x), x)", //
        "{{y(x)->BesselJ(a,x)*C(1)+BesselY(a,x)*C(2)}}");

    // 4. Generalized Bessel's Equation: x^2 y'' + x y' + (4x^2 - 9) y = 0
    // Tests constant extraction: a^2 = 4 (a=2) and nu^2 = 9 (nu=3)
    check("DSolve(x^2*y''(x) + x*y'(x) + (4*x^2 - 9)*y(x) == 0, y(x), x)", //
        "{{y(x)->BesselJ(3,2*x)*C(1)+BesselY(3,2*x)*C(2)}}");
  }

  /**
   * Puts the solution back into the equation and evaluates the residual at one point. This is for
   * the answers whose printed form is long or carries logarithms and radicals, where pinning the
   * exact string says less than seeing the equation solved.
   */
  private void checkResidual(String equation, String residual, String point) {
    check("With({s=DSolve(" + equation + ", y, x)}, Head(s)===List && Abs(N((" + residual
        + ") /. s[[1]] /. " + point + ")) < 10^-6)", //
        "True");
  }

  @Test
  public void testDSolveEulerShiftedCentre() {
    // A Cauchy-Euler equation need not be centred at 0; the centre is read off the leading
    // coefficient as x - n*c(n)/c(n)'.
    check("DSolve((x+1)^2*y''(x) - 3*(x+1)*y'(x) + 3*y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)+x*C(1)+C(2)+3*x*C(2)+3*x^2*C(2)+x^3*C(2)}}");

    checkResidual("(x+1)^2*y''(x) - 3*(x+1)*y'(x) + 3*y(x) == x^2",
        "(x+1)^2*y''(x) - 3*(x+1)*y'(x) + 3*y(x) - x^2", "{C(1)->7/5, C(2)->3/4, x->13/10}");
  }

  @Test
  public void testDSolveCoefficientNormalization() {
    // Solved for the highest derivative the equation has a rational right hand side; multiplying
    // by the denominators makes it the Cauchy-Euler equation x^3*y'''(x) - 24*y(x) == 24*x.
    checkResidual("y'''(x) == (24*x + 24*y(x))/x^3", "y'''(x) - (24*x + 24*y(x))/x^3",
        "{C(1)->7/5, C(2)->3/4, C(3)->2/3, x->13/10}");

    // Every coefficient carries a factor of x; dividing it out leaves constant coefficients.
    checkResidual("x*y'''(x) + 2*x*y''(x) - x*y'(x) - 2*x*y(x) == 1",
        "x*y'''(x) + 2*x*y''(x) - x*y'(x) - 2*x*y(x) - 1",
        "{C(1)->7/5, C(2)->3/4, C(3)->2/3, x->13/10}");

    check("DSolve(x*y''(x) + 3*x*y'(x) + 2*x*y(x) == 1, y(x), x)", //
        "{{y(x)->C(1)/E^(2*x)+C(2)/E^x+ExpIntegralEi(x)/E^x-ExpIntegralEi(2*x)/E^(2*x)}}");
  }

  @Test
  public void testDSolveLegendre() {
    // (1-x^2)*y'' - 2*x*y' + nu*(nu+1)*y == 0 with nu*(nu+1) == 15/4, so nu == 3/2.
    check("DSolve((1-x^2)*y''(x) - 2*x*y'(x) + 15/4*y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)*LegendreP(3/2,x)+C(2)*LegendreQ(3/2,x)}}");
  }

  @Test
  public void testDSolveBesselFamilies() {
    // y'' == A*x^m*y is Bessel's equation of order 1/(m+2) in x^((m+2)/2).
    check("DSolve(y''(x) - x^4*y(x) == 0, y(x), x)", //
        "{{y(x)->Sqrt(x)*BesselI(1/6,x^3/3)*C(1)+Sqrt(x)*BesselK(1/6,x^3/3)*C(2)}}");

    // y'' == A*E^(lambda*x)*y is Bessel's equation of order 0 in E^(lambda*x/2).
    check("DSolve(y''(x) - E^(5*x)*y(x) == 0, y(x), x)", //
        "{{y(x)->BesselI(0,2/5*E^(5/2*x))*C(1)+BesselK(0,2/5*E^(5/2*x))*C(2)}}");

    // The form without a first derivative, which neither of the two rows above covers.
    check("DSolve(y''(x) + (a + b/x^2)*y(x) == 0, y(x), x)", //
        "{{y(x)->Sqrt(x)*BesselJ(Sqrt(1/4-b),Sqrt(a)*x)*C(1)+Sqrt(x)*BesselY(Sqrt(1/4-b),Sqrt(a)*x)*C(\n" //
            + "2)}}");

    // Airy's equation is of the pure power form as well, so it has to be recognized first.
    check("DSolve(y''(x) - (x + 2)*y(x) == 0, y(x), x)", //
        "{{y(x)->AiryAi(2+x)*C(1)+AiryBi(2+x)*C(2)}}");
  }

  @Test
  public void testDSolveHypergeometric() {
    check("DSolve(x*y''(x) + (b - x)*y'(x) - a*y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)*Hypergeometric1F1(a,b,x)+x^(1-b)*C(2)*Hypergeometric1F1(1+a-b,2-b,x)}}");

    check("DSolve((x^2 - x)*y''(x) + ((a + b + 1)*x - c)*y'(x) + a*b*y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)*Hypergeometric2F1(a,b,c,x)+x^(1-c)*C(2)*Hypergeometric2F1(1+a-c,1+b-c,\n" //
            + "2-c,x)}}");

    // An integer c makes the second solution a copy of the first, so this is not a basis.
    check("DSolve((x^2 - x)*y''(x) + ((a + b + 1)*x - 2)*y'(x) + a*b*y(x) == 0, y(x), x)", //
        "DSolve(a*b*y(x)+(-2+(1+a+b)*x)*y'(x)+(-x+x^2)*y''(x)==0,y(x),x)");
  }

  @Test
  public void testDSolveInvalidInput() {
    // DSolve: The function Derivative(2)[y] appears with no arguments.
    check("DSolve(y'' - x*y(x) == 0, y(x), x)", //
        "DSolve(-x*y(x)+Derivative(2)[y]==0,y(x),x)");

    // DSolve: DSolve: The function Derivative(2)[y] appears with no arguments.
    check("DSolve(y''() - x*y(x) == 0, y(x), x)", //
        "DSolve(-x*y(x)+Derivative(2)[y][]==0,y(x),x)");
  }

  @Test
  public void testDSolveThreeUnknowns() {
    check("DSolve(y'(x)+5*y(x)==1,y,x)", //
        "{{y->Function({x},1/5+C(1)/E^(5*x))}}");
    check("DSolve({x'(t)==y(t)+z(t),y'(t)+z(t)-x(t)==0,z'(t)+y(t)==x(t)},{x,y,z},t)", //
        "{{x->Function({t},C(1)/(3*E^(2*t))+2/3*E^t*C(1)-C(2)/(3*E^(2*t))+1/3*E^t*C(2)-C(\n" //
            + "3)/(3*E^(2*t))+1/3*E^t*C(3)),y->Function({t},-C(1)/(3*E^(2*t))+1/3*E^t*C(1)+C(2)/(\n" //
            + "3*E^(2*t))+2/3*E^t*C(2)+C(3)/(3*E^(2*t))-1/3*E^t*C(3)),z->Function({t},-C(1)/(3*E^(\n" //
            + "2*t))+1/3*E^t*C(1)+C(2)/(3*E^(2*t))-1/3*E^t*C(2)+C(3)/(3*E^(2*t))+2/3*E^t*C(3))}}");
  }

  @Test
  public void testDSolveLaplace() {
    // Second-order ODE with Dirac Delta impulse
    // The Laplace transform beautifully manages point-mass excitations mapping them to delayed sine
    // waves.
    check("DSolve({y''(t) + y(t) == DiracDelta(t - Pi), y(0)==0, y'(0)==0}, y(t), t)", //
        "{{y(t)->-HeavisideTheta(-Pi+t)*Sin(t)}}");

    // First-order ODE with Heaviside step function
    // Tests that Laplace effectively catches 1st order linear equations where standard solvers
    // might struggle.
    check("DSolve({y'(t) + y(t) == HeavisideTheta(t - 1), y(0)==0}, y(t), t)", //
        // TODO Integrate must handle HeavisideTheta
        // "{{y(t)->Integrate(E^t*HeavisideTheta(-1+t),t)/E^t}}");
        "{{y(t)->HeavisideTheta(-1+t)-E^(1-t)*HeavisideTheta(-1+t)}}");
  }

  @Test
  public void testDSolveHigherOrderConstantCoefficients() {
    // Solved through the roots of the characteristic polynomial. Building the equivalent first
    // order system and taking a matrix exponential of its companion matrix instead did not return
    // for any of these.
    check("DSolve(y'''(x) - 6*y''(x) + 11*y'(x) - 6*y(x) == 0, y(x), x)", //
        "{{y(x)->E^x*C(1)+E^(2*x)*C(2)+E^(3*x)*C(3)}}");

    // A pair of conjugate complex roots stays real, as Cos and Sin of the imaginary part.
    check("DSolve(y'''(x) + 4*y'(x) == 5*y(x), y(x), x)", //
        "{{y(x)->E^x*C(1)+(C(2)*Cos(1/2*Sqrt(19)*x))/E^(x/2)+(C(3)*Sin(1/2*Sqrt(19)*x))/E^(x/\n" //
            + "2)}}");

    // A repeated root contributes the extra solution x*E^(r*x); here the root 0 twice.
    check("DSolve(y'''(x) + y''(x) == 0, y(x), x)", //
        "{{y(x)->C(1)/E^x+C(2)+x*C(3)}}");

    check("DSolve(y''''(x) - y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)/E^x+E^x*C(4)+C(2)*Cos(x)+C(3)*Sin(x)}}");
  }

  @Test
  public void testDSolveVariationOfParameters() {
    check("DSolve(y''(x) + y(x) == Sec(x), y(x), x)", //
        "{{y(x)->C(1)*Cos(x)+Cos(x)*Log(Cos(x))+x*Sin(x)+C(2)*Sin(x)}}");

    check("DSolve(y''(x) - 3*y'(x) + 2*y(x) == E^(3*x), y(x), x)", //
        "{{y(x)->E^(3*x)/2+E^x*C(1)+E^(2*x)*C(2)}}");
  }

  @Test
  public void testDSolveNonlinearInDerivative() {
    // Reading a coefficient of y'(x) accounts for the linear term only and used to answer
    // x+C(1). Solving the quadratic for y'(x) first gives two quadratures.
    check("DSolve(y'(x) + x*y'(x)^2 == 1, y(x), x)", //
        "{{y(x)->-Sqrt(1+4*x)+C(1)-Log(1-Sqrt(1+4*x))},{y(x)->Sqrt(1+4*x)+C(1)-Log(1+Sqrt(\n" //
            + "1+4*x))}}");
  }

  @Test
  public void testDSolveDeclinesInsteadOfAnswering() {
    // No method covers a linear equation of second order with these variable coefficients. The
    // first order solvers used to be offered it anyway, and answered from the part of it they
    // could read, which produced an expression containing y''(x) itself.
    check("DSolve(x*y''(x) + 2*y'(x) - x*y(x) == Sin(x), y(x), x)", //
        "DSolve(-x*y(x)+2*y'(x)+x*y''(x)==Sin(x),y(x),x)");

    // The second integration is elliptic, so this has no solution in elementary terms.
    check("DSolve(y''(x) == y(x)^2 + 1, y(x), x)", //
        "DSolve(y''(x)==1+y(x)^2,y(x),x)");

    // A system which is not linear in its unknowns has no coefficient matrix. Treating E^z(x) as
    // if it were a forcing function produced an answer to a different system.
    check("DSolve({y'(x) == Exp(z(x)) + 1, z'(x) == y(x) - x}, {y, z}, x)", //
        "DSolve({y'(x)==1+E^z(x),z'(x)==-x+y(x)},{y,z},x)");
  }

  @Test
  public void testDSolveSystemDecoupled() {
    // The two equations share no unknown, so they are separate problems. As one system neither
    // fits the matrix construction, because the first coefficient depends on x.
    check("DSolve({y'(x) == x^2*y(x), z'(x) == 5*z(x)}, {y, z}, x)", //
        "{{y->Function({x},E^(x^3/3)*C(1)),z->Function({x},E^(5*x)*C(2))}}");

    check("DSolve({y'(x) == x^2*y(x), z'(x) == 5*z(x), y(0) == 1, z(0) == 2}, {y, z}, x)", //
        "{{y->Function({x},E^(x^3/3)),z->Function({x},2*E^(5*x))}}");
  }

  @Test
  public void testDSolveSystemComplexEigenvalues() {
    check(
        "DSolve({y'(x) == y(x) - 2*z(x), z'(x) == y(x) - z(x), y(0) == 1, z(0) == 4}, {y, z}, x)", //
        "{{y->Function({x},Cos(x)-7*Sin(x)),z->Function({x},4*Cos(x)-3*Sin(x))}}");
  }

  @Test
  public void testDSolveReductionOfOrderWithConditions() {
    // The general second integration of y'^2 == y^4/2 + C(1) is elliptic. Determining C(1) from
    // y'(0) before integrating again makes it elementary, which is the only way this is solvable.
    check("DSolve({y''(x) == y(x)^3, y(0) == 5, y'(0) == 25/Sqrt(2)}, y(x), x)", //
        "{{y(x)->1/(1/5-x/Sqrt(2))}}");
  }

  @Test
  public void testDSolveHomogeneousWithRoot() {
    // y'(x) == y(x)/x + Sqrt(y(x)/x) is homogeneous but neither exact nor does it have an
    // integrating factor in one variable, so the substitution y == v*x has to solve it.
    check("DSolve(y'(x) - Sqrt(y(x)/x) == y(x)/x, y(x), x)", //
        "{{y(x)->1/4*x*C(1)^2+1/2*x*C(1)*Log(x)+1/4*x*Log(x)^2}}");
  }

  @Test
  public void testDSolvePDESingleDerivative() {
    // Only one of the two derivatives occurs, so the other variable is a parameter and the
    // constant of the integration is an arbitrary function of it.
    check("DSolve(D(u(x, y), x) == 1, u(x,y), {x, y})", //
        "{{u(x,y)->x+C(1)[y]}}");
  }

  @Test
  public void testDSolvePDESecondOrder() {
    // Elliptic: the characteristic directions are imaginary.
    check("DSolve(D(u(x,y), {x,2}) + D(u(x,y), {y,2}) == 0, u(x,y), {x, y})", //
        "{{u(x,y)->C(1)[-I*x+y]+C(2)[I*x+y]}}");

    // Hyperbolic: d'Alembert's two families of real characteristics.
    check("DSolve(D(u(x,t), {x,2}) - D(u(x,t), {t,2}) == 0, u(x,t), {t, x})", //
        "{{u(x,t)->C(1)[-t+x]+C(2)[t+x]}}");

    check("DSolve(2*D(u(x,y),{x,2}) + 7*D(u(x,y),x,y) - D(u(x,y),{y,2}) == 0, u, {x, y})", //
        "{{u->Function({x,y},C(1)[(-7/4-Sqrt(57)/4)*x+y]+C(2)[(-7/4+Sqrt(57)/4)*x+y])}}");

    // Parabolic: the repeated direction contributes a factor x, as a repeated root of a
    // characteristic polynomial does for an ordinary equation.
    check("DSolve(3*D(u(x,y),{x,2}) + 30*D(u(x,y),x,y) + 75*D(u(x,y),{y,2}) == 0, u, {x, y})", //
        "{{u->Function({x,y},C(1)[-5*x+y]+x*C(2)[-5*x+y])}}");

    // The principal part is the mixed derivative alone, so the operator factors directly.
    check("DSolve(D(u(x,y),x,y) == 0, u(x,y), {x,y})", //
        "{{u(x,y)->C(1)[y]+C(2)[x]}}");
  }

  @Test
  public void testDSolvePDECompleteIntegral() {
    // A nonlinear first-order equation has no general solution in terms of an arbitrary function.
    // What is returned is a complete integral, a family with two parameters.
    check("DSolve(D(u(x,y),x)*D(u(x,y),y) == 1, u, {x, y})", //
        "{{u->Function({x,y},y/C(1)+x*C(1)+C(2))}}");

    // The eikonal equation: one branch per root of the equation for the second derivative.
    check("DSolve(D(u(x,y),x)^2 + D(u(x,y),y)^2 == 1, u, {x, y})", //
        "{{u->Function({x,y},x*C(1)-y*Sqrt(1-C(1)^2)+C(2))},{u->Function({x,y},x*C(1)+y*Sqrt(\n" //
            + "1-C(1)^2)+C(2))}}");

    // Clairaut's equation u == x*u_x + y*u_y + f(u_x,u_y): replacing the derivatives by the two
    // parameters is already the complete integral.
    check("DSolve(u(x,y) == x*D(u(x,y),x) + y*D(u(x,y),y) + Sin(D(u(x,y),x) + D(u(x,y),y)), u, {x, y})", //
        "{{u->Function({x,y},x*C(1)+y*C(2)+Sin(C(1)+C(2)))}}");

    // Separable in the two groups of variables, so each side is a constant and one quadrature per
    // variable remains.
    check("DSolve(D(u(x,y),x)^2 + a*D(u(x,y),y) == x + 3*y, u, {x, y})", //
        "{{u->Function({x,y},(3*y-C(1))^2/(6*a)-2/3*(x+C(1))^(3/2)+C(2))},{u->Function({x,y},(\n" //
            + "3*y-C(1))^2/(6*a)+2/3*(x+C(1))^(3/2)+C(2))}}");
  }

  @Test
  public void testDSolvePDEInitialConditions() {
    // The initial profile is carried along the characteristics, so it simply travels.
    check("DSolve({D(u(t,x),t) + c*D(u(t,x),x) == 0, u(0,x) == E^(-x^2)}, u, {t, x})", //
        "{{u->Function({t,x},E^(-(-c*t+x)^2))}}");

    check("DSolve({x*D(u(x,y),y) + y*D(u(x,y),x) == -4*x*y*u(x,y), u(x,0) == E^(-x^2)}, u, {x, y})", //
        "{{u->Function({x,y},E^(-x^2-y^2))}}");
  }

  @Test
  public void testDSolveInferredArguments() {
    // The unknown and the variable it depends on can be read off the equation.
    check("DSolve(y'(x) == y(x))", //
        "{{y(x)->E^x*C(1)}}");

    // An equation written without arguments cannot say what the variable is, so x is used, and
    // the answer is a pure function which can be applied.
    check("DSolve(y' == y)", //
        "{{y->Function({x},E^x*C(1))}}");

    check("DSolve({y'(x) == x*y(x), y(0) == 3})", //
        "{{y(x)->3*E^(x^2/2)}}");

    check("DSolve({y'(x) == z(x), z'(x) == -y(x)})", //
        "{{y(x)->C(1)*Cos(x)+C(2)*Sin(x),z(x)->C(2)*Cos(x)-C(1)*Sin(x)}}");
  }

  @Test
  public void testDSolveGeneratedParameters() {
    check("DSolve(y''(x) - 4*y(x) == 0, y(x), x, GeneratedParameters -> f)", //
        "{{y(x)->f(1)/E^(2*x)+E^(2*x)*f(2)}}");

    // The arbitrary function of a partial differential equation is renamed as well.
    check("DSolve(D(u(x,y),x) + 3*D(u(x,y),y) + u(x,y) == 1, u, {x, y}, GeneratedParameters -> f)", //
        "{{u->Function({x,y},1+f(1)[-3*x+y]/E^x)}}");
  }

  @Test
  public void testDSolveValue001() {
    // Basic First-Order ODE
    // DSolve returns {{y(x) -> E^x * C(1)}}, DSolveValue strips the rules and returns the value
    check("DSolveValue(y'(x) == y(x), y(x), x)", //
        "E^x*C(1)");

    // Initial Value Problem (IVP)
    check("DSolveValue({y'(x) == y(x), y(0) == 3}, y(x), x)", //
        "3*E^x");
  }

  @Test
  public void testDSolveValue002() {
    // Evaluating a derivative expression
    check("DSolveValue({y'(x) == 2*y(x), y(0) == 5}, D(y(x), x), x)", //
        "10*E^(2*x)");

    // Second-Order ODE
    check("DSolveValue(y''(x) + y(x) == 0, y(x), x)", //
        "C(1)*Cos(x)+C(2)*Sin(x)");

    // Evaluating an arbitrary expression (not just the bare function)
    // Here, we ask for y(x)^2. It should solve for y(x) = 3*E^x, then substitute it into y(x)^2
    check("DSolveValue({y'(x) == y(x), y(0) == 3}, y(x)^2, x)", //
        "9*E^(2*x)");


  }

  @Test
  public void testDSolveValue003() {
    // System of ODEs
    // Should return a list of values corresponding to {x(t), y(t)}
    check("DSolveValue({x'(t) == y(t), y'(t) == -x(t)}, {x(t), y(t)}, t)", //
        "{C(1)*Cos(t)+C(2)*Sin(t),C(2)*Cos(t)-C(1)*Sin(t)}");

    // System of ODEs with target expression arithmetic
    check("DSolveValue({x'(t) == y(t), y'(t) == -x(t)}, x(t) + y(t), t)", //
        "C(1)*Cos(t)+C(2)*Cos(t)-C(1)*Sin(t)+C(2)*Sin(t)");
  }

  @Test
  public void testDSolveValue004() {
    // System of ODEs
    // Should return a list of values corresponding to {x(t), y(t)}
    check("DSolveValue({y'(x)==y(x)+2},y(x), x)", //
        "-2+E^x*C(1)");

    // System of ODEs with target expression arithmetic
    check("DSolveValue({y'(x)==y(x)+2,y(0)==1},y(x), x)", //
        "-2+3*E^x");
  }

  @Test
  public void testDSolveWithExpIntegral() {
    check("DSolve({y'(x) + y(x) == 1/x^2}, y(x), x)", //
        "{{y(x)->-1/x+C(1)/E^x+ExpIntegralEi(x)/E^x}}");

    // Standard Bernoulli integration factor fallback
    check("DSolve({x*y'(x) - y(x) == E^x}, y(x), x)", //
        "{{y(x)->-E^x+x*C(1)+x*ExpIntegralEi(x)}}");
  }

  @Test
  public void testAndOperatorInsteadOfList() {
    // the equations and the boundary conditions may be combined with `&&` instead of a list
    check("DSolve(y'(x)==y(x)+2 && y(0)==1, y(x), x)", //
        "{{y(x)->-2+3*E^x}}");
    check("DSolve(y''(x) + y(x) == 0 && y(0)==1 && y'(0)==0, y(x), x)", //
        "{{y(x)->Cos(x)}}");
    check("DSolve(y'(x) == -3*y(x)^2 && y(0)==2, y(x), x)", //
        "{{y(x)->2/(1+6*x)}}");
    check("DSolveValue(y'(x)==y(x)+2 && y(0)==1, y(x), x)", //
        "-2+3*E^x");
  }

  @Test
  public void testDSolveSimplified() {
    // Substituting the integration constant leaves the fractions it came with nested inside each
    // other, so the particular solution is put over a common denominator.
    check("DSolve({y'(x) == x * y(x)^2, y(0) == 2}, y(x), x)", //
        "{{y(x)->2/(1-x^2)}}");

    check("DSolve({y'(x) == y(x)^2, y(0) == 1}, y(x), x)", //
        "{{y(x)->1/(1-x)}}");

    check("DSolve({y'(x) == y(x)^3, y(0) == 1}, y(x), x)", //
        "{{y(x)->1/Sqrt(1-2*x)}}");

    // The x-factor may be any closed-form function of x, not just a monomial.
    check("DSolve({y'(x) == Cos(x) * y(x)^2, y(0) == 1}, y(x), x)", //
        "{{y(x)->1/(1-Sin(x))}}");

    // A quotient separates too: y' == x/y.
    check("DSolve({y'(x) == x / y(x), y(0) == 1}, y(x), x)", //
        "{{y(x)->Sqrt(1+x^2)}}");

    // DSolve(..., y, x) asks for the Function form.
    check("DSolve({y'(t) == -t * y(t)^2, y(0) == 1}, y, t)", //
        "{{y->Function({t},2/(2+t^2))}}");

    check("DSolve({y'(t) == (t - t^3) * y(t)^2, y(0) == 1}, y, t)", //
        "{{y->Function({t},4/(4-2*t^2+t^4))}}");

    // Undoing the Bernoulli substitution u == y^(1-n) for an even exponent gives both signs of the
    // root, and only the initial condition tells them apart: with y(0) == -1 the answer is the
    // negative branch, not the positive one.
    check("DSolve({y'(x) == x / y(x), y(0) == -1}, y(x), x)", //
        "{{y(x)->-Sqrt(1+x^2)}}");

    // Without conditions both branches are returned.
    check("DSolve(y'(x) == x / y(x), y(x), x)", //
        "{{y(x)->-Sqrt(x^2-C(1))},{y(x)->Sqrt(x^2-C(1))}}");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
