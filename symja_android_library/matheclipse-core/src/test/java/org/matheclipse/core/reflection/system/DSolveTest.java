package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.system.ExprEvaluatorTestCase;
import org.matheclipse.core.system.TestTags;

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
        "{{y(x)->-E^(2*x)/(E^(2*x)+C(1))+C(1)/(E^(2*x)+C(1))}}");
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
    check(
        "With({s=DSolve(" + equation + ", y, x)}, Head(s)===List && Abs(N((" + residual
            + ") /. s[[1]] /. " + point + ")) < 10^-6)", //
        "True");
  }

  @Test
  public void testDSolveSeparableBranchMeetsTheCondition() {
    // A relation of second degree in y is two functions, and which of the two the condition picks
    // is not decided when the relation is inverted. Only the first was kept, so an initial value
    // problem whose answer was the other one came back as one with no answer at all.
    check("DSolve({y'(x) == (2 - E^x)/(3 + 2*y(x)), y(0) == 0}, y(x), x)", //
        "{{y(x)->1/2*(-3+Sqrt(13-4*E^x+8*x))}}");
    check("DSolve({y'(x) == 2*x/(y(x) + x^2*y(x)), y(0) == -2}, y(x), x)", //
        "{{y(x)->-Sqrt(2)*Sqrt(2+Log(1+x^2))}}");
    check("DSolve({y'(x) == (x^2 + 1)*x/(4*y(x)^3), y(0) == -Sqrt(2)/2}, y(x), x)", //
        "{{y(x)->-(1+2*x^2+x^4)^(1/4)/Sqrt(2)}}");
    // and the general solution says both of them
    check("DSolve(y'(x) == (2 - E^x)/(3 + 2*y(x)), y(x), x)", //
        "{{y(x)->-3/2-Sqrt(9-4*E^x+8*x-4*C(1))/2},{y(x)->-3/2+Sqrt(9-4*E^x+8*x-4*C(1))/2}}");

    // A branch which the condition rules out is still ruled out, and solving for the constant is
    // not what decides it: the minus branch here can be fitted to y(a) == b for every b, because
    // Solve answers formally, and it meets the condition only where b is negative. Both branches
    // come back for the general solution and one of them for this problem.
    check("DSolve({y'(x) == y(x)*Sqrt(y(x)^2 - 1), y(a) == b}, y(x), x)", //
        "{{y(x)->Sqrt(1+Tan(a-x-ArcTan(Sqrt(-1+b^2)))^2)}}");
    check("DSolve(y'(x) == y(x)*Sqrt(y(x)^2 - 1), y(x), x)", //
        "{{y(x)->Sqrt(1+Tan(x+C(1))^2)},{y(x)->-Sqrt(1+Tan(x+C(1))^2)}}");
  }

  @Test
  public void testDSolveRiccatiParticularSolution() {
    // Once one solution y_p of a Riccati equation is known, y == y_p + 1/v leaves a linear
    // equation for v. These are set so that one can be guessed: y == 1, y == x^2 and
    // y == 2*Log(x) respectively.
    check("DSolve(y'(x) == 1 + x - (2*x + 1)*y(x) + x*y(x)^2, y(x), x)", //
        "{{y(x)->1+1/(1+x+E^x*C(1))}}");
    check("DSolve(x^3*y'(x) == -2*x^4 + 2*x^2*y(x) + 2*y(x)^2, y(x), x)", //
        "{{y(x)->-x^2+1/(1/(2*x^2)+x^2*C(1))}}");
    checkResidual("x*Log(x)^2*y'(x) == -4*Log(x)^2 + y(x)*Log(x) + y(x)^2", //
        "x*Log(x)^2*y'(x) - (-4*Log(x)^2 + y(x)*Log(x) + y(x)^2)", "{C(1)->3/7, x->13/10}");

    // The linearization y == -u'/(a*u) gives u'' - (b + a'/a)*u' + a*c*u == 0; the sign of a'/a
    // was the other one, which agrees only when a is constant, and this was declined.
    check("DSolve(y'(x) == y(x)^2/E^x + 4*y(x) + 2*E^x, y(x), x)", //
        "{{y(x)->(-2*E^(2*x))/(E^x+C(1))+(-E^x*C(1))/(E^x+C(1))}}");
    // and with a constant the linearization is unchanged
    check("DSolve(y'(x) == 1 + y(x)^2, y(x), x)", //
        "{{y(x)->Tan(x+C(1))}}");
  }

  @Test
  public void testDSolveVariationOfParametersOnAFoundBasis() {
    // The methods for variable coefficients answer the homogeneous equation, and the inhomogeneous
    // one used to be declined though its homogeneous part was solved. Its basis {t, t*E^t} is all
    // variation of parameters needs.
    check("DSolve(t^2*y''(t) - t*(t + 2)*y'(t) + (t + 2)*y(t) == 2*t^3, y(t), t)", //
        "{{y(t)->-2*t^2+t*C(1)+E^t*t*C(2)}}");
    checkResidualIn("t*y''(t) - (1 + t)*y'(t) + y(t) == t^2*E^(2*t)", //
        "t*y''(t) - (1 + t)*y'(t) + y(t) - t^2*E^(2*t)", "{C(1)->3/7, C(2)->5/11, t->7/10}");
    // the equation Kovacic finds the homogeneous basis of, and did not carry a forcing term for
    checkResidualIn("(t^2 - 1)*y''(t) - 2*t*y'(t) + 2*y(t) == t^2 - 1", //
        "(t^2 - 1)*y''(t) - 2*t*y'(t) + 2*y(t) - (t^2 - 1)", "{C(1)->3/7, C(2)->5/11, t->3/10}");
  }

  /** {@link #checkResidual} for an equation in <code>t</code>. */
  private void checkResidualIn(String equation, String residual, String point) {
    check("With({s=DSolve(" + equation + ", y, t)}, Head(s)===List && Abs(N((" + residual
        + ") /. s[[1]] /. " + point + ")) < 10^-6)", //
        "True");
  }

  @Test
  public void testDSolveFittedConstantSolvesTheEquation() {
    // A condition can have two solutions for the constant, and only one of them solves the
    // equation. The general solution here is (C(1)*E^x - 1)^2, and y(0) == 1 asks for
    // (C(1) - 1)^2 == 1: C(1) == 0 gives y == 1, whose residual is -4 everywhere, because the
    // squaring introduced it; C(1) == 2 gives the solution. The first root used to be taken.
    check("DSolve({y'(x) - 2*y(x) == 2*Sqrt(y(x)), y(0) == 1}, y(x), x)", //
        "{{y(x)->1-4*E^x+4*E^(2*x)}}");
    checkResidual("{y'(x) - 2*y(x) == 2*Sqrt(y(x)), y(0) == 1}", //
        "y'(x) - 2*y(x) - 2*Sqrt(y(x))", "{x->13/10}");
    // the same with a forcing term: (2 + x)^2 was returned, with a residual of -3.78 at x == 7/10
    checkResidual("{y'(x) - y(x) == x*Sqrt(y(x)), y(0) == 4}", //
        "y'(x) - y(x) - x*Sqrt(y(x))", "{x->7/10}");
  }

  @Test
  public void testDSolveConditionAtASingularPoint() {
    // The general solution of these is written with ExpIntegralEi(t), which does not reach t == 0:
    // putting the point in gives Indeterminate from 0*(-Infinity). That is not the condition
    // failing, it is the wrong way to ask, and the limit there says C(2) == 0. One condition and
    // two constants, so one constant stays free, which is right.
    check("DSolve({t*x''(t) + (t - 2)*x'(t) + x(t) == 0, x(0) == 0}, x(t), t)", //
        "{{x(t)->(t^3*C(1))/E^t}}");
    check("DSolve({t*x''(t) + (3*t - 1)*x'(t) + 3*x(t) == 0, x(0) == 0}, x(t), t)", //
        "{{x(t)->(t^2*C(1))/E^(3*t)}}");

    // A condition which no solution meets is still refused: every solution of x'(t) == x(t)/t is
    // zero at the origin, so it cannot be 5 there.
    check("DSolve({x'(t) == x(t)/t, x(0) == 5}, x(t), t)", //
        "DSolve({x'(t)==x(t)/t,x(0)==5},x(t),t)");
    // and the ordinary initial value problems are untouched
    check("DSolve({y''(x) + 4*y(x) == 0, y(0) == 1, y'(0) == 0}, y(x), x)", //
        "{{y(x)->Cos(2*x)}}");
  }

  @Test
  public void testDSolveExactAfterClearingTheDenominator() {
    // Exact as written, and not exact by the time the solvers see it: the coefficient of y' is
    // cleared of its denominator first, which multiplies the pair by x. The integrating factor
    // which puts that back is 1/x, and the ratio saying so is a function of x alone only after a
    // common factor is cancelled out of it. Both of these were declined for that reason.
    checkResidual("x^3 + y(x)/x + (y(x)^2 + Log(x))*y'(x) == 0", //
        "x^3 + y(x)/x + (y(x)^2 + Log(x))*y'(x)", "{C(1)->7/5, x->7/10}");
    check("DSolve(E^y(x) + Cos(x)*y(x) + (x*E^y(x) + Sin(x))*y'(x) == 0, y(x), x)", //
        "{{y(x)->Log((ProductLog(E^(C(1)*Csc(x))*x*Csc(x))*Sin(x))/x)}}");

    // An exact equation whose first integral is a polynomial in y still answers as it did: the
    // three branches of the cubic, each of which solves the equation.
    check("Length(DSolve(2*x*y(x) + (x^2 + 3*y(x)^2)*y'(x) == 0, y(x), x))", //
        "3");
    checkResidual("2*x*y(x) + (x^2 + 3*y(x)^2)*y'(x) == 0", //
        "2*x*y(x) + (x^2 + 3*y(x)^2)*y'(x)", "{C(1)->7/5, x->13/10}");

    // The first integral of this one mixes y with E^y, so there is nothing explicit to find and it
    // is declined rather than answered implicitly.
    check("FreeQ(DSolve(Cos(x) + Log(y(x)) + (x/y(x) + E^y(x))*y'(x) == 0, y(x), x), Rule)", //
        "True");
  }

  @Test
  public void testDSolveProductLogRelation() {
    // A relation which mixes a linear form with its own logarithm is what a first order equation
    // separates into whenever the denominator shares a factor with the numerator. Nothing
    // algebraic inverts it and ProductLog does, by its definition.
    check("DSolve(y'(x) == (2*y(x) + 3)/(5*y(x) + 7), y(x), x)", //
        "{{y(x)->-3/2-ProductLog(-10/E^(15+4*x-4*C(1)))/10}}");
    // The reciprocal form is the same relation in 1/u, and it is what a homogeneous equation
    // reduces to along y == v*x.
    check("DSolve(y'(x) == (x + 3*y(x))/(x - y(x)), y(x), x)", //
        "{{y(x)->-x+(-2*x)/ProductLog((-2*x)/E^C(1))}}");
    // the shape which already inverted is unchanged
    check("DSolve(y'(t) == Cot(t)*y(t)/(1 + y(t)), y(t), t)", //
        "{{y(t)->ProductLog(E^C(1)*Sin(t))}}");

    // A relation whose exponential is a quintic in y has no explicit solution and is declined
    // rather than answered with one branch of it.
    check("FreeQ(DSolve(y'(x) == (4*y(x) - 3*x)/(2*x - y(x)), y(x), x), Rule)", //
        "True");
  }

  @Test
  public void testDSolveAutonomousSumOfLogarithms() {
    // An autonomous equation separates into the antiderivative of a rational function, which is a
    // sum of logarithms, and nothing inverts that as it stands. Raised to the power which clears
    // the denominators of its coefficients and then exponentiated, the same relation is algebraic
    // in y and is solved outright.
    check("DSolve(y'(x) == E^y(x) - 1, y(x), x)", //
        "{{y(x)->-Log(1+E^(x-C(1)))}}");
    checkResidual("y'(x) == y(x)*(y(x) - 2)*(y(x) - 1)", //
        "y'(x) - y(x)*(y(x) - 2)*(y(x) - 1)", "{C(1)->3/4, x->13/10}");

    // A term which is not a logarithm and not y itself is refused rather than searched for: the
    // repeated root here leaves a 1/y, whose exponential is no more invertible than the sum was,
    // and these are the equations whose answer has to stay implicit.
    check("FreeQ(DSolve(y'(x) == y(x)^2*(y(x)^2 - 1), y(x), x), Rule)", //
        "True");
    check("FreeQ(DSolve(y'(x) == (1 - y(x))^2*y(x)^2, y(x), x), Rule)", //
        "True");

    // and the equations which already inverted are unchanged
    check("DSolve(y'(x) == y(x)*(1 - y(x)), y(x), x)", //
        "{{y(x)->1/(1+C(1)/E^x)}}");
    check("DSolve(y'(x) == y(x), y(x), x)", //
        "{{y(x)->E^x*C(1)}}");
  }

  @Test
  public void testDSolveSeparableConstantFromTheRelation() {
    // The relation these separate into is a cubic in y, and inverting it puts the constant under a
    // square root and inside a cube root, where solving for it afterwards fails. The condition
    // names it on the relation instead, where it stands on its own, and the cubic is then solved
    // in numbers. Both answers are nested radicals, so what is checked is that they answer the
    // equation and meet the condition.
    checkResidual("{y'(x) == (3*x^2 + 1)/(-6*y(x) + 3*y(x)^2), y(0) == 1}", //
        "y'(x) - (3*x^2 + 1)/(-6*y(x) + 3*y(x)^2)", "{x->7/10}");
    check("With({s = DSolve({y'(x) == (3*x^2 + 1)/(-6*y(x) + 3*y(x)^2), y(0) == 1}, y, x)},"
        + " Abs(N((y(0) /. s[[1]]) - 1)) < 10^-6)", //
        "True");
    checkResidual("{y'(x) == 3*x^2/(-4 + 3*y(x)^2), y(1) == 0}", //
        "y'(x) - 3*x^2/(-4 + 3*y(x)^2)", "{x->13/10}");
    check("With({s = DSolve({y'(x) == 3*x^2/(-4 + 3*y(x)^2), y(1) == 0}, y, x)},"
        + " Abs(N(y(1) /. s[[1]])) < 10^-6)", //
        "True");
    // the general solution is unchanged: it keeps its constant, and both branches of it
    check("Length(DSolve(y'(x) == 3*x^2/(-4 + 3*y(x)^2), y(x), x))", //
        "3");
  }

  @Test
  public void testDSolveFourthOrderInitialValues() {
    // Four conditions determine the four constants of a fourth order equation, but Solve can
    // answer such a system without mentioning a constant which one condition fixes on its own
    // (y(0) == 0 makes C(1) == 0 outright). What it left out is asked about again, rather than the
    // whole problem being refused as unfitted.
    check("DSolve({Derivative(4)[y][x]+8*y''(x)+16*y(x)==0, y(0)==0, y'(0)==0, y''(0)==0, "
        + "Derivative(3)[y][0]==1}, y(x), x)", //
        "{{y(x)->1/16*(-2*x*Cos(2*x)+Sin(2*x))}}");
    check("DSolve({Derivative(4)[y][x]+2*y''(x)+y(x)==E^(2*x), y(0)==0, y'(0)==0, y''(0)==0, "
        + "Derivative(3)[y][0]==0}, y(x), x)", //
        "{{y(x)->1/50*(2*E^(2*x)-2*Cos(x)+10*x*Cos(x)-14*Sin(x)-5*x*Sin(x))}}");
    // the shapes which already worked are unchanged
    check("DSolve({y''(x)+4*y(x)==0, y(0)==1, y'(0)==0}, y(x), x)", //
        "{{y(x)->Cos(2*x)}}");
    check("DSolve({y''(x)+y(x)==Sin(x), y(0)==0, y'(0)==0}, y(x), x)", //
        "{{y(x)->1/2*(-x*Cos(x)+Sin(x))}}");
    check("DSolve({y'(x)+y(x)==0, y(0)==3}, y(x), x)", //
        "{{y(x)->3/E^x}}");
  }

  @Test
  public void testDSolveUndeterminedCoefficients() {
    // A right hand side which is a power of x times an exponential times a sine is answered from a
    // linear system for the coefficients of an ansatz of the same shape, rather than by integrating
    // the basis against it.
    check("DSolve(y''(x)-y(x)==x^2, y(x), x)", //
        "{{y(x)->-2-x^2+C(1)/E^x+E^x*C(2)}}");
    check("DSolve(y''(x)+y'(x)-2*y(x)==E^(3*x), y(x), x)", //
        "{{y(x)->E^(3*x)/10+C(1)/E^(2*x)+E^x*C(2)}}");
    // the forcing function solves the homogeneous equation, so the ansatz carries a power of x:
    // once for a simple root of the characteristic polynomial
    check("DSolve(y''(x)+y(x)==Sin(x), y(x), x)", //
        "{{y(x)->-1/2*x*Cos(x)+C(1)*Cos(x)+C(2)*Sin(x)}}");
    check("DSolve({y''(x)+y(x)==Sin(x), y(0)==0, y'(0)==0}, y(x), x)", //
        "{{y(x)->1/2*(-x*Cos(x)+Sin(x))}}");
    // twice for a double one
    check("DSolve(y''(x)-2*y'(x)+y(x)==E^x, y(x), x)", //
        "{{y(x)->1/2*E^x*x^2+E^x*C(1)+E^x*x*C(2)}}");
    // and the polynomial factor is raised along with it
    check("DSolve(y''(x)+4*y(x)==x*Cos(2*x), y(x), x)", //
        "{{y(x)->1/16*x*Cos(2*x)+C(1)*Cos(2*x)+1/8*x^2*Sin(2*x)+C(2)*Sin(2*x)}}");
    checkResidual("y''(x)+4*y(x)==x*Cos(2*x)", //
        "y''(x)+4*y(x)-x*Cos(2*x)", "{x->13/10, C(1)->7/5, C(2)->2/5}");
    // a right hand side of another shape is left to variation of parameters, as before
    check("DSolve(y''(x)+y(x)==Sec(x), y(x), x)", //
        "{{y(x)->C(1)*Cos(x)+Cos(x)*Log(Cos(x))+x*Sin(x)+C(2)*Sin(x)}}");
  }

  /**
   * Variation of parameters answers this one too, but it spends a minute and a half on the integrals
   * of its basis against the forcing function -- the integrands send a zero test inside the
   * integration into a factorization over the Gaussian rationals -- and the answer it produces
   * carries eleven terms where four will do.
   */
  @Test
  @Tag(TestTags.SLOW)
  public void testDSolveUndeterminedCoefficientsHighFrequency() {
    checkResidual("y''(x)+3*y'(x)+3*y(x)==8*Cos(10*x)+6*Sin(10*x)", //
        "y''(x)+3*y'(x)+3*y(x)-8*Cos(10*x)-6*Sin(10*x)", "{x->13/10, C(1)->7/5, C(2)->2/5}");
  }

  @Test
  public void testDSolveSeparableWithCoefficient() {
    // Separating the variables starts by dividing out the coefficient of y'(x). Without that the
    // method only ever saw equations which were already written as y'(x) == f(x)*g(y), and every
    // one of these was declined.
    check("DSolve((x+1)^2*y'(x) == (1+y(x))^2, y(x), x)", //
        "{{y(x)->-1+1/(1/(1+x)+C(1))}}");
    check("DSolve(2*Sqrt(x)*y'(x) == Cos(y(x))^2, y(x), x)", //
        "{{y(x)->ArcTan(Sqrt(x)+C(1))}}");
    check("DSolve(2*Sqrt(x)*y'(x) == Sqrt(1-y(x)^2), y(x), x)", //
        "{{y(x)->Sin(Sqrt(x)+C(1))}}");
    // a right hand side which arrives as a sum has to be factored before it sorts into an x part
    // and a y part
    check("DSolve(x^2*y'(x) == 1-x^2+y(x)^2-x^2*y(x)^2, y(x), x)", //
        "{{y(x)->-Tan(1/x+x-C(1))}}");
    checkResidual("(x+1)^2*y'(x) == (1+y(x))^2", //
        "(x+1)^2*y'(x) - (1+y(x))^2", "{x->13/10, C(1)->7/5}");
    checkResidual("x^2*y'(x) == 1-x^2+y(x)^2-x^2*y(x)^2", //
        "x^2*y'(x) - (1-x^2+y(x)^2-x^2*y(x)^2)", "{x->13/10, C(1)->7/5}");
    // a root of a product of the two variables is a single factor as it stands, and separates
    // only once it is written as a product of two roots
    check("DSolve(y'(x) == 3*Sqrt(y(x)*x), y(x), x)", //
        "{{y(x)->x^3+x^(3/2)*C(1)+C(1)^2/4}}");
    checkResidual("y'(x) == 3*Sqrt(y(x)*x)", //
        "y'(x) - 3*Sqrt(y(x)*x)", "{x->13/10, C(1)->7/5}");
    // the equations the homogeneous reduction answers keep their answers
    check("DSolve(y'(x) == (x+y(x))/x, y(x), x)", //
        "{{y(x)->x*C(1)+x*Log(x)}}");
    check("DSolve(x*y'(x) == y(x) + Sqrt(x^2+y(x)^2), y(x), x)", //
        "{{y(x)->x*Sinh(C(1)+Log(x))}}");
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
  @Tag(TestTags.SLOW)
  public void testDSolveSecondOrderSymmetry() {
    // A projective symmetry: the coordinates in which it is a translation reduce the equation to
    // one of the first order.
    check("DSolve(x^3*y''(x) == (y(x) - x*y'(x))^2, y(x), x)", //
        "{{y(x)->x*C(1)+x*Log(x)-x*Log(1+x*C(2))}}");

    check("DSolve(y''(x) == (x*y'(x) - y(x))^2/x^3, y(x), x)", //
        "{{y(x)->x*C(1)+x*Log(x)-x*Log(1+x*C(2))}}");

    // A scaling symmetry. The coefficient of the second derivative depends on y here.
    check("DSolve(2*x^2*y''(x)*y(x) + y(x)^2 == x^2*y'(x)^2, y(x), x)", //
        "{{y(x)->E^C(1)*x*C(2)^2+2*E^C(1)*x*C(2)*Log(x)+E^C(1)*x*Log(x)^2}}");

    checkResidual("x^2*(x + y(x))*y''(x) == (x*y'(x) - y(x))^2",
        "x^2*(x + y(x))*y''(x) - (x*y'(x) - y(x))^2", "{C(1)->7/5, C(2)->3/4, x->13/10}");
  }

  @Test
  public void testDSolveSecondOrderSymmetryFamilies() {
    // The same symmetry with other coefficients, so that the method is not fitted to one equation.
    for (int k : new int[] {1, 2, 3, -1, -2}) {
      checkResidual("x^3*y''(x) == " + k + "*(y(x) - x*y'(x))^2",
          "x^3*y''(x) - " + k + "*(y(x) - x*y'(x))^2", "{C(1)->7/5, C(2)->3/4, x->13/10}");
    }
    for (int m : new int[] {2, 3, -1}) {
      checkResidual(m + "*x^2*y''(x)*y(x) + y(x)^2 == x^2*y'(x)^2",
          m + "*x^2*y''(x)*y(x) + y(x)^2 - x^2*y'(x)^2", "{C(1)->7/5, C(2)->3/4, x->13/10}");
    }
  }

  @Test
  public void testDSolveSymmetryLeavesLinearAlone() {
    // A linear equation of the second order has an eight dimensional symmetry algebra, so the
    // search would always succeed and answer nothing the methods before it do not own. It is not
    // asked.
    check("DSolve(y''(x) + y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)*Cos(x)+C(2)*Sin(x)}}");
    check("DSolve(y''(x) - x*y(x) == 0, y(x), x)", //
        "{{y(x)->AiryAi(x)*C(1)+AiryBi(x)*C(2)}}");
  }

  @Test
  public void testDSolveSystemAutonomous() {
    // The variable does not appear, so the curve the solutions trace out satisfies one equation
    // for one unknown, and knowing it turns the first equation into another. This is the one
    // system solver here which answers a nonlinear system.
    check("DSolve({x'(t) == y(t), y'(t) == y(t)^2/x(t)}, {x(t), y(t)}, t)", //
        "{{x(t)->E^(t*C(2))*C(1),y(t)->E^(t*C(2))*C(1)*C(2)}}");

    check("DSolve({x'(t) == -1/y(t), y'(t) == 1/x(t)}, {x(t), y(t)}, t)", //
        "{{x(t)->C(1)/E^(t/C(2)),y(t)->(E^(t/C(2))*C(2))/C(1)}}");

    check("DSolve({x'(t) == 1/y(t), y'(t) == 1/x(t)}, {x(t), y(t)}, t)", //
        "{{x(t)->-Sqrt(-C(1)+(2*t)/C(2)),y(t)->-Sqrt(-C(1)+(2*t)/C(2))*C(2)}}");

    // The curve here carries a root, which makes the equation for the variable one the cascade
    // rarely answers, so this declines rather than spending the time.
    check("DSolve({x'(t) == y(t)/(x(t)-y(t)), y'(t) == x(t)/(x(t)-y(t))}, {x(t), y(t)}, t)", //
        "DSolve({x'(t)==y(t)/(x(t)-y(t)),y'(t)==x(t)/(x(t)-y(t))},{x(t),y(t)},t)");
  }

  @Test
  public void testDSolveSystemVariableCoefficients() {
    // A rotation whose radius grows: the matrix is (1/t)*I plus a constant one, so it commutes
    // with its own integral and has an exponential.
    check("DSolve({x'(t) == x(t)/t + y(t), y'(t) == -x(t) + y(t)/t}, {x(t), y(t)}, t)", //
        "{{x(t)->t*C(1)*Cos(t)+t*C(2)*Sin(t),y(t)->t*C(2)*Cos(t)-t*C(1)*Sin(t)}}");

    // A constant matrix with a scalar function in front of it.
    check(
        "With({s=DSolve({t*x'(t) + y(t) == 0, t*y'(t) + x(t) == 0}, {x, y}, t)}, "
            + "Head(s)===List && Max(Abs(N({t*x'(t) + y(t), t*y'(t) + x(t)} /. s[[1]] /. "
            + "{C(1)->7/5, C(2)->3/4, t->13/10}))) < 10^-6)", //
        "True");

    // The same shape with a forcing term.
    check("With({s=DSolve({t*x'(t) + 2*x(t) - 2*y(t) == t, t*y'(t) + x(t) + 5*y(t) == t^2}, "
        + "{x, y}, t)}, Head(s)===List && "
        + "Max(Abs(N({t*x'(t) + 2*x(t) - 2*y(t) - t, t*y'(t) + x(t) + 5*y(t) - t^2} /. s[[1]] /. "
        + "{C(1)->7/5, C(2)->3/4, t->13/10}))) < 10^-6)", //
        "True");

    // A multiple of the identity plus a multiple of one constant matrix, both depending on t.
    check("With({s=DSolve({x'(t) == -x(t) + t*y(t), y'(t) == t*x(t) - y(t)}, {x, y}, t)}, "
        + "Head(s)===List && Max(Abs(N({x'(t) + x(t) - t*y(t), y'(t) - t*x(t) + y(t)} /. s[[1]] "
        + "/. {C(1)->7/5, C(2)->3/4, t->13/10}))) < 10^-6)", //
        "True");

    check(
        "With({s=DSolve({x'(t) == x(t)*Cos(t) - Sin(t)*y(t), y'(t) == x(t)*Sin(t) "
            + "+ y(t)*Cos(t)}, {x, y}, t)}, Head(s)===List && "
            + "Max(Abs(N({x'(t) - x(t)*Cos(t) + Sin(t)*y(t), y'(t) - x(t)*Sin(t) - y(t)*Cos(t)} "
            + "/. s[[1]] /. {C(1)->7/5, C(2)->3/4, t->13/10}))) < 10^-6)", //
        "True");
  }

  @Test
  public void testDSolveSystemHigherOrder() {
    // Carrying the first derivatives as unknowns of their own turns these into first order
    // systems, which is the shape the matrix engine solves.
    check(
        "With({s=DSolve({x''(t) == 4*y(t), y''(t) == 4*x(t)}, {x, y}, t)}, Head(s)===List && "
            + "Max(Abs(N({x''(t) - 4*y(t), y''(t) - 4*x(t)} /. s[[1]] /. "
            + "{C(1)->7/5, C(2)->3/4, C(3)->2/3, C(4)->5/6, t->13/10}))) < 10^-6)", //
        "True");

    // The two unknowns need not be differentiated equally often.
    check(
        "With({s=DSolve({x''(t) + x'(t) + y'(t) - 2*y(t) == 0, x'(t) + x(t) - y'(t) == 0}, "
            + "{x, y}, t)}, Head(s)===List && "
            + "Max(Abs(N({x''(t) + x'(t) + y'(t) - 2*y(t), x'(t) + x(t) - y'(t)} /. s[[1]] /. "
            + "{C(1)->7/5, C(2)->3/4, C(3)->2/3, t->13/10}))) < 10^-6)", //
        "True");

    // With a forcing term the particular solution comes from variation of parameters.
    check("With({s=DSolve({x''(t) == 4*y(t) + E^t, y''(t) == 4*x(t) - E^t}, {x, y}, t)}, "
        + "Head(s)===List && Max(Abs(N({x''(t) - 4*y(t) - E^t, y''(t) - 4*x(t) + E^t} /. s[[1]] "
        + "/. {C(1)->7/5, C(2)->3/4, C(3)->2/3, C(4)->5/6, t->13/10}))) < 10^-6)", //
        "True");

    // Two equations of the second order carry four arbitrary constants.
    check(
        "Length(Union(Cases(DSolve({x''(t) == 2*x(t) - 3*y(t), y''(t) == x(t) - 2*y(t)}, "
            + "{x, y}, t), C(_), Infinity)))", //
        "4");
  }

  /**
   * A coefficient which is a sum, as in <code>y'(x) == (1+x)*y(x)</code>. Reading the coefficient
   * of the unknown off the equation and subtracting it back leaves nothing, but only once the
   * difference is expanded; unexpanded it still mentions the unknown, which used to be read as a
   * forcing term that is not free of it, and the equation went past the linear and Bernoulli
   * methods to the ones which cannot answer it.
   */
  @Test
  public void testDSolveFirstOrderSummedCoefficient() {
    check("DSolve(y'(x) == (1+x)*y(x), y(x), x)", //
        "{{y(x)->E^(x+x^2/2)*C(1)}}");
    check("DSolve(y'(x) == (k-x^2)*y(x), y(x), x)", //
        "{{y(x)->E^(k*x-x^3/3)*C(1)}}");
    check("DSolve(y'(x) == (Sin(x)+1)*y(x), y(x), x)", //
        "{{y(x)->E^(x-Cos(x))*C(1)}}");
    check("DSolve(y'(x) == (2/x+1/x^2)*y(x), y(x), x)", //
        "{{y(x)->(x^2*C(1))/E^(1/x)}}");

    // with a forcing term, and the same coefficient
    check("DSolve(y'(x) + (1+x)*y(x) == 1+x, y(x), x)", //
        "{{y(x)->1+C(1)/E^(x+x^2/2)}}");
    check("DSolve(y'(x) == (1+1/x)*y(x) + x, y(x), x)", //
        "{{y(x)->-x+E^x*x*C(1)}}");

    // Bernoulli reads its coefficient the same way
    checkResidual("y'(x) == (1+x)*y(x) + x*y(x)^3", //
        "y'(x) - (1+x)*y(x) - x*y(x)^3", "{C(1)->7/5, x->13/10}");

    // a coefficient which is one term is unchanged
    check("DSolve(y'(x) == 2*x*y(x)^2, y(x), x)", //
        "{{y(x)->1/(-x^2-C(1))}}");
    check("DSolve(y'(x) == -x^2*y(x), y(x), x)", //
        "{{y(x)->C(1)/E^(x^3/3)}}");

    // no elementary antiderivative for the forcing term, so this one still declines
    check("DSolve(y'(x) == (2-x^2)*y(x) + x, y(x), x)", //
        "DSolve(y'(x)==x+(2-x^2)*y(x),y(x),x)");
  }

  /**
   * Whittaker's equation, whose potential has one double pole and a constant term. The Bessel row
   * takes the same potential without the <code>1/x</code> term, so what reaches this is what that
   * one did not want.
   */
  @Test
  @Tag(TestTags.SLOW)
  public void testDSolveWhittaker() {
    checkResidual("y''(x) + (-1/4 + k/x + (1/4-m^2)/x^2)*y(x) == 0", //
        "y''(x) + (-1/4 + k/x + (1/4-m^2)/x^2)*y(x)",
        "{C(1)->7/5, C(2)->3/4, k->3/10, m->7/10, x->13/10}");
    // the same equation multiplied through, which is how the corpus writes it
    checkResidual("x^2*y''(x) + (c*x^2 + b*x + a)*y(x) == 0", //
        "x^2*y''(x) + (c*x^2 + b*x + a)*y(x)",
        "{C(1)->7/5, C(2)->3/4, a->-3/10, b->1/5, c->-1/10, x->13/10}");
    check(
        "FreeQ(DSolve(y''(x) + (-1/4 + k/x + (1/4-m^2)/x^2)*y(x) == 0, y(x), x),"
            + " Hypergeometric1F1)", //
        "False");

    // twice the order is a whole number here, so the two solutions are one and the second does
    // not exist; this is left to the methods which can say something about it
    check("Head(DSolve(y''(x) + (-1/4 + 2/x - 3/(4*x^2))*y(x) == 0, y(x), x))", //
        "DSolve");
    // a potential with no 1/x term belongs to the Bessel row above and still goes there
    check("DSolve(y''(x) + (1 + (1/4-m^2)/x^2)*y(x) == 0, y(x), x)", //
        "{{y(x)->Sqrt(x)*BesselJ(Sqrt(m^2),x)*C(1)+Sqrt(x)*BesselY(Sqrt(m^2),x)*C(2)}}");
  }

  /**
   * The hypergeometric equation about two finite singular points which are not <code>0</code> and
   * <code>1</code>, which is what Gegenbauer's and Jacobi's equations are once the degree is left
   * symbolic.
   */
  @Test
  @Tag(TestTags.SLOW)
  public void testDSolveFuchsian() {
    checkResidual("(1-x^2)*y''(x) - (2*a+1)*x*y'(x) + n*(n+2*a)*y(x) == 0", //
        "(1-x^2)*y''(x) - (2*a+1)*x*y'(x) + n*(n+2*a)*y(x)",
        "{C(1)->7/5, C(2)->3/4, a->3/10, n->7/10, x->3/10}");
    checkResidual("(1-x^2)*y''(x) + (b-a-(a+b+2)*x)*y'(x) + n*(n+a+b+1)*y(x) == 0", //
        "(1-x^2)*y''(x) + (b-a-(a+b+2)*x)*y'(x) + n*(n+a+b+1)*y(x)",
        "{C(1)->7/5, C(2)->3/4, a->3/10, b->1/5, n->7/10, x->3/10}");
    checkResidual("(x^2-1)*y''(x) + (a*x+b)*y'(x) + c*y(x) == 0", //
        "(x^2-1)*y''(x) + (a*x+b)*y'(x) + c*y(x)",
        "{C(1)->7/5, C(2)->3/4, a->3/10, b->1/5, c->7/10, x->3/10}");
    // a singular point which is a parameter of the equation
    checkResidual("x*(a0+x)*y''(x) + (b1*x+a1)*y'(x) + a2*y(x) == 0", //
        "x*(a0+x)*y''(x) + (b1*x+a1)*y'(x) + a2*y(x)",
        "{C(1)->7/5, C(2)->3/4, a0->-2, a1->1/5, a2->7/10, b1->3/10, x->13/10}");
    check(
        "FreeQ(DSolve((1-x^2)*y''(x) - (2*a+1)*x*y'(x) + n*(n+2*a)*y(x) == 0, y(x), x),"
            + " Integrate)", //
        "True");

    // one finite singular point is a confluent equation, which the rows above answer, and this
    // one is not asked about it
    check("Head(DSolve(x^2*y''(x) + x*y'(x) + (x^3-1)*y(x) == 0, y(x), x))", //
        "DSolve");
  }

  /**
   * An equation which is Airy's or Bessel's only once its first derivative has been taken out of it
   * by <code>y == Exp(-Integrate(p/2))*z</code>.
   */
  @Test
  public void testDSolveNormalFormPrePass() {
    check("DSolve(y''(x) + 2*y'(x) + (1-x)*y(x) == 0, y(x), x)", //
        "{{y(x)->(AiryAi(x)*C(1))/E^x+(AiryBi(x)*C(2))/E^x}}");
    check("DSolve(y''(x) - 2*x*y'(x) + (x^2-1)*y(x) == 0, y(x), x)", //
        "{{y(x)->E^(x^2/2)*C(1)+E^(x^2/2)*x*C(2)}}");
    checkResidual("y''(x) + (2/x)*y'(x) + y(x) == 0", //
        "y''(x) + (2/x)*y'(x) + y(x)", "{C(1)->7/5, C(2)->3/4, x->13/10}");
  }

  /**
   * Nonlinear equations of the second order which can be integrated once, leaving a first order
   * equation the cascade then solves. The method above is the first order one, which multiplies an
   * equation into an exact one rather than integrating it.
   */
  @Test
  @Tag(TestTags.SLOW)
  public void testDSolveSecondOrderIntegratingFactor() {
    checkResidual("y(x)*y'(x) + y''(x) == 1", //
        "y(x)*y'(x) + y''(x) - 1", "{C(1)->7/5, C(2)->3/4, x->13/10}");
    checkResidual("y''(x) - y(x)*y'(x) == 6", //
        "y''(x) - y(x)*y'(x) - 6", "{C(1)->7/5, C(2)->3/4, x->13/10}");
    checkResidual("y''(x) == a*(1 + 2*y(x)*y'(x))", //
        "y''(x) - a*(1 + 2*y(x)*y'(x))", "{C(1)->7/5, C(2)->3/4, a->3/10, x->13/10}");
    check("DSolve(y''(x) + 2*y(x)*y'(x) == 0, y(x), x)", //
        "{{y(x)->Sqrt(C(1))*Tanh(x*Sqrt(C(1))+Sqrt(C(1))*C(2))}}");

    // the factor of the second kind, which is a solution of a linear equation in x times a
    // function of y
    check("DSolve(y(x)*y''(x) - y'(x)^2 + 2*x*y(x)^2 == 0, y(x), x)", //
        "{{y(x)->E^(-x^3/3+x*C(1)+C(2))}}");
    check("DSolve(y(x)*y''(x) == y'(x)^2, y(x), x)", //
        "{{y(x)->E^(x*C(1))*C(2)}}");

    // a linear equation has one too, and the methods for those answer it better
    check("DSolve(y''(x) + y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)*Cos(x)+C(2)*Sin(x)}}");
    check("DSolve(y''(x) + (2/x)*y'(x) + y(x) == 0, y(x), x)", //
        "{{y(x)->(C(1)*Cos(x))/x+(-C(2)*Sin(x))/x}}");
    // and an equation with no y in it is reduced in order rather than integrated
    check("DSolve(y''(x) == y'(x)^3, y(x), x)", //
        "{{y(x)->Sqrt(-2*x-C(1))+C(2)},{y(x)->-Sqrt(-2*x-C(1))+C(2)}}");
  }

  /**
   * First order equations which a shift of the unknown turns into a quadrature:
   * <code>y' == -phi'/c + g(x)*(phi(x) + c*y)^p</code> under <code>u == phi + c*y</code>.
   */
  @Test
  public void testDSolvePolynomialShift() {
    check("DSolve(y'(x) == -1 + x*Sqrt(x + y(x)), y(x), x)", //
        "{{y(x)->-x+x^4/16+1/4*x^2*C(1)+C(1)^2/4}}");
    checkResidual("y'(x) == -1 + x*Sqrt(x + y(x))", //
        "y'(x) + 1 - x*Sqrt(x + y(x))", "{C(1)->7/5, x->13/10}");
    checkResidual("y'(x) == -2*x + (x^2 + y(x))^(1/3)", //
        "y'(x) + 2*x - (x^2 + y(x))^(1/3)", "{C(1)->7/5, x->13/10}");

    // the coefficient of the unknown under the root has to be a constant, or the substitution
    // puts the unknown back into the equation instead of taking it out
    check("Head(DSolve(y'(x) == (x + 1 + 2*Sqrt(4*x^2*y(x) + 1)*x^3)/(2*x^3*(x + 1)), y(x), x))", //
        "DSolve");
    // and what is under it has to be of the first degree in the unknown
    check("Head(DSolve(y'(x) == Sqrt(x + y(x)^2), y(x), x))", //
        "DSolve");
    // an equation with no such power is left to the methods below, which answer this one
    check("DSolve(y'(x) == 2*x*y(x)^2, y(x), x)", //
        "{{y(x)->1/(-x^2-C(1))}}");
  }

  /**
   * Inverting a periodic function writes a whole number into the answer to choose a branch, and
   * every value of it names the same solution. Left standing it is indistinguishable from a
   * constant the conditions were supposed to determine.
   */
  @Test
  public void testDSolvePrincipalBranch() {
    check("DSolve({y'(x) == 1 + y(x)^2, y(0) == 0}, y(x), x)", //
        "{{y(x)->Tan(x)}}");
    check("DSolve({y'(x) == 1 + y(x)^2, y(0) == 1}, y(x), x)", //
        "{{y(x)->Tan(Pi/4+x)}}");
    // Log(3*E^(2*x) - 2), in the shape the reduction along 2*x - y(x) writes it
    check("DSolve({y'(x) == 6*E^(2*x - y(x)), y(0) == 0}, y(x), x)", //
        "{{y(x)->2*x+Log(3-2/E^(2*x))}}");
    // and the general solution of it keeps its arbitrary constant rather than a branch index
    check("DSolve(y'(x) == 6*E^(2*x - y(x)), y(x), x)", //
        "{{y(x)->2*x+Log(3-1/E^(2*x-2*C(1)))}}");
    // the general solution of the same equations keeps its arbitrary constant
    check("DSolve(y'(x) == 1 + y(x)^2, y(x), x)", //
        "{{y(x)->Tan(x+C(1))}}");
    check("DSolve(v'(x) == E^v(x), v(x), x)", //
        "{{v(x)->-Log(-x-C(1))}}");

    // The condition can be met, and used to be answered with y(x) -> Undefined and then refused:
    // solving the general solution for its constant is what failed, not the problem. Naming the
    // constant on the separated relation instead gives (1 + 2*x/3)^(3/2), which is the answer.
    check("DSolve({y'(x) == y(x)^(1/3), y(0) == 1}, y(x), x)", //
        "{{y(x)->Sqrt(2/3)*Sqrt(3/2+x)+2/3*Sqrt(2/3)*x*Sqrt(3/2+x)}}");
    checkResidual("{y'(x) == y(x)^(1/3), y(0) == 1}", //
        "y'(x) - y(x)^(1/3)", "{x->13/10}");
    checkResidual("{y'(x) == y(x)^(1/3), y(0) == 0}", //
        "y'(x) - y(x)^(1/3)", "{x->13/10}");
  }

  /**
   * A homogeneous equation whose reduction leaves a radical. Substituting <code>y == v*x</code>
   * makes the right side a function of <code>v</code> alone, but only where <code>x</code> is
   * positive does the radical say so.
   */
  @Test
  public void testDSolveHomogeneousRadical() {
    checkResidual("y'(x)*x == y(x) + Sqrt(x^2+y(x)^2)", //
        "y'(x)*x - y(x) - Sqrt(x^2+y(x)^2)", "{C(1)->7/5, x->13/10}");
    checkResidual("y'(x)*x == y(x) + 2*Sqrt(y(x)*x)", //
        "y'(x)*x - y(x) - 2*Sqrt(y(x)*x)", "{C(1)->7/5, x->13/10}");
    checkResidual("y(x)*y'(x)*x == y(x)^2 + x*Sqrt(4*x^2+y(x)^2)", //
        "y(x)*y'(x)*x - y(x)^2 - x*Sqrt(4*x^2+y(x)^2)", "{C(1)->7/5, x->13/10}");
    // this one has no radical in it, and answers now because the inversion of its reduced
    // equation no longer carries the branch of a logarithm
    checkResidual("x^2*y'(x) == y(x)*x + x^2*E^(y(x)/x)", //
        "x^2*y'(x) - y(x)*x - x^2*E^(y(x)/x)", "{C(1)->7/5, x->13/10}");

    // the ones which never needed it keep their answers
    check("DSolve(y'(x) == (x+y(x))/x, y(x), x)", //
        "{{y(x)->x*C(1)+x*Log(x)}}");
    check("DSolve(y'(x) == (x^2+y(x)^2)/(x*y(x)), y(x), x)", //
        "{{y(x)->-Sqrt(x^2*C(1)+2*x^2*Log(x))},{y(x)->Sqrt(x^2*C(1)+2*x^2*Log(x))}}");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testDSolveChangeOfVariable() {
    // Under t == Cos(x) this is Legendre's equation, which the rows above then recognize.
    for (int k : new int[] {1, 2, 3, 4}) {
      int degree = k * (k + 1);
      checkResidual("y''(x) + Cot(x)*y'(x) + " + degree + "*y(x) == 0",
          "y''(x) + Cot(x)*y'(x) + " + degree + "*y(x)", "{C(1)->7/5, C(2)->3/4, x->13/10}");
    }

    // The same equation multiplied through by Sin(x).
    for (int m : new int[] {1, 2, 3}) {
      int degree = m * (m + 1);
      checkResidual("y''(x)*Sin(x) + y'(x)*Cos(x) + " + degree + "*y(x)*Sin(x) == 0",
          "y''(x)*Sin(x) + y'(x)*Cos(x) + " + degree + "*y(x)*Sin(x)",
          "{C(1)->7/5, C(2)->3/4, x->13/10}");
    }

    // Nothing of this kind makes these coefficients rational, so it declines rather than spending
    // the time on every equation with a sine in it.
    check("DSolve(y''(x) + Sin(x)*y(x) == 0, y(x), x)", //
        "DSolve(Sin(x)*y(x)+y''(x)==0,y(x),x)");

    // Under t == E^x the coefficients are rational in t and the equation is then Liouvillian, so
    // Kovacic's method finds it; one solution is E^(-E^x) and the other carries ExpIntegralEi.
    checkResidual("y''(x) + (E^x - E^(2*x))*y(x) == 0", //
        "y''(x) + (E^x - E^(2*x))*y(x)", "{C(1)->7/5, C(2)->3/4, x->13/10}");

    // With the rate and the coefficient left symbolic the substitution still does its work -- the
    // coefficients come out as 1/t and a*(b-a*t)/(b^2*t) -- but the equation it hands on is one
    // Kovacic's method does not solve with symbolic parameters, so this one still declines.

    // Two rates whose ratio is not an integer: no substitution of this kind makes both of them
    // polynomial, so it declines instead of simplifying its way through the whole budget.
    check("Head(DSolve(y''(x) + (E^x + E^(Sqrt(2)*x))*y(x) == 0, y(x), x))", //
        "DSolve");

    // An exponential of something other than a multiple of x is not reached by this either.
    check("Head(DSolve(y''(x) + (1+E^(x^2/2))^(-2)*y(x) == 0, y(x), x))", //
        "DSolve");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testDSolveSymmetricSquare() {
    // The solutions are the products of the solutions of u''(x) == (x+2)*u(x), which is Airy's
    // equation about the centre -2.
    check("DSolve(y'''(x) - 4*(x + 2)*y'(x) - 2*y(x) == 0, y(x), x)", //
        "{{y(x)->AiryAi(2+x)^2*C(1)+AiryAi(2+x)*AiryBi(2+x)*C(2)+AiryBi(2+x)^2*C(3)}}");

    // The same for a second order equation whose solutions are Bessel functions of order Sqrt(3).
    checkResidual("x^3*y'''(x) + 3*x^2*y''(x) + (4*x^3 - 11*x)*y'(x) + 4*x^2*y(x) == 0",
        "x^3*y'''(x) + 3*x^2*y''(x) + (4*x^3 - 11*x)*y'(x) + 4*x^2*y(x)",
        "{C(1)->7/5, C(2)->3/4, C(3)->2/3, x->13/10}");
  }

  @Test
  public void testDSolveKovacicAlgebraicPair() {
    // The logarithmic derivative is -1/(4*x) +- Sqrt(x)/2, which is not rational; what is rational
    // is the sum of the two, and that is what the search looks for.
    check("DSolve(y''(x) == (x/4 + 5/(16*x^2))*y(x), y(x), x)", //
        "{{y(x)->(E^(x^(3/2)/3)*C(1))/x^(1/4)+C(2)/(E^(x^(3/2)/3)*x^(1/4))}}");

    checkResidual("y''(x) == ((x-1)/4 + 5/(16*(x-1)^2))*y(x)",
        "D(y(x),{x,2}) - ((x-1)/4 + 5/(16*(x-1)^2))*y(x)", "{x->17/13, C(1)->3/7, C(2)->5/11}");

    check(
        "With({b=(y(x) /. DSolve(y''(x) == (x/4 + 5/(16*x^2))*y(x), y(x), x)[[1,1]])},"
            + " Abs(N(Wronskian({D(b,C(1)), D(b,C(2))}, x) /. x->17/13)) > 10^-6)", //
        "True");

    // A solution of this kind is algebraic, and where the equation has none the method declines
    // rather than returning one of the roots on its own.
    check("DSolve(y''(x) == (3/(16*x^2) + 2/(9*(x-1)^2) - 3/(16*x*(x-1)))*y(x), y(x), x)", //
        "DSolve(y''(x)==(2/(9*(1-x)^2)+3/(16*x^2)-3/(16*(-1+x)*x))*y(x),y(x),x)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testDSolveKovacicApparentSingularities() {
    // Chebyshev's equation. Its solutions have zeros where r has no pole, which the guess of the
    // plain case cannot put anywhere; those zeros go into a polynomial factor of their own.
    check("DSolve((1-x^2)*y''(x) - x*y'(x) + 4*y(x) == 0, y(x), x)", //
        "{{y(x)->-C(1)/2+x^2*C(1)+x*Sqrt(1-x^2)*C(2)}}");

    check("DSolve((1-x^2)*y''(x) - x*y'(x) + 9*y(x) == 0, y(x), x)", //
        "{{y(x)->-3/4*x*C(1)+x^3*C(1)-1/4*Sqrt(1-x^2)*C(2)+x^2*Sqrt(1-x^2)*C(2)}}");

    // Gegenbauer's equation, whose two solutions come from two different choices at the poles and
    // so need no integral between them.
    check("DSolve((1-x^2)*y''(x) - 3*x*y'(x) + 3*y(x) == 0, y(x), x)", //
        "{{y(x)->x*C(1)-C(2)/(2*Sqrt(1-x^2))+(x^2*C(2))/Sqrt(1-x^2)}}");

    check(
        "With({b=(y(x) /. DSolve((1-x^2)*y''(x) - 3*x*y'(x) + 3*y(x) == 0, y(x), x)[[1,1]])},"
            + " Abs(N(Wronskian({D(b,C(1)), D(b,C(2))}, x) /. x->1/3)) > 10^-6)", //
        "True");
    // Laguerre's equation. Kovacic finds its polynomial x^2-6*x+6, and the integral which carries
    // that first solution to a second is E^x/(x^2*(x^2-6*x+6)^2) - which used to come back partly
    // evaluated, so the whole equation was declined.
    checkResidual("x*y''(x) + (2-x)*y'(x) + 2*y(x) == 0",
        "x*D(y(x),{x,2}) + (2-x)*D(y(x),x) + 2*y(x)", "{x->11/3, C(1)->3/7, C(2)->5/11}");

  }

  @Test
  public void testDSolveKovacicPolynomialFactor() {
    // A solution with zeros in it has no polynomial logarithmic derivative, so the guess which
    // looks only for one misses it. Splitting the zeros off into a factor of their own leaves an
    // exponential: here x*Exp(x^2/2) solves the reduced equation.
    checkResidual("y''(x) == (x^2 + 3)*y(x)", "D(y(x),{x,2}) - (x^2 + 3)*y(x)",
        "{x->17/13, C(1)->3/7, C(2)->5/11}");

    // The factor may be the constant one, in which case the solution is the exponential itself.
    check("DSolve(y''(x) == (x^2 - 1)*y(x), y(x), x)", //
        "{{y(x)->C(1)/E^(x^2/2)+(C(2)*Erfi(x))/E^(x^2/2)}}");

    // Hermite's equation of the first degree, whose first solution is x.
    checkResidual("y''(x) - 2*x*y'(x) + 2*y(x) == 0", "D(y(x),{x,2}) - 2*x*D(y(x),x) + 2*y(x)",
        "{x->17/13, C(1)->3/7, C(2)->5/11}");

    check(
        "With({b=(y(x) /. DSolve(y''(x) == (x^2 + 3)*y(x), y(x), x)[[1,1]])},"
            + " Abs(N(Wronskian({D(b,C(1)), D(b,C(2))}, x) /. x->17/13)) > 10^-6)", //
        "True");

    // The degree the factor would have to have is not always a whole number, and where it is not
    // there is no solution of this kind.
    check("DSolve(y''(x) == (x^2 + 2)*y(x), y(x), x)", //
        "DSolve(y''(x)==(2+x^2)*y(x),y(x),x)");
  }

  @Test
  public void testDSolveNormalizedCoefficients() {
    // Both coefficients are a sum of two fractions. Reading the denominator without putting them
    // over a common one first reports 1, so nothing was cleared, the equation was not recognized
    // as one with constant coefficients, and the cascade below spent minutes on it.
    check("DSolve(y''(x)/x + y''(x)/(x-1) - y(x)/x - y(x)/(x-1) == 0, y(x), x)", //
        "{{y(x)->C(1)/E^x+E^x*C(2)}}");

    // A coefficient which is not a rational function of x is left alone rather than handed to the
    // polynomial routines, and the equation declines quickly instead of grinding.
    check("DSolve(y''(x) + (1+E^(x^2/2))^(-2)*y(x) == 0, y(x), x)", //
        "DSolve(y(x)/(1+E^(x^2/2))^2+y''(x)==0,y(x),x)");

    // The clearing and the division by a common factor still work.
    check("DSolve(x^2*y''(x) + x*y'(x) - y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)/x+x*C(2)}}");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testDSolvePoschlTeller() {
    // A potential built from Csc(x)^2 and Sec(x)^2. Multiplying the coefficient by
    // Sin(x)^2*Cos(x)^2 clears both and leaves an even quadratic in Cos(x), which is where the two
    // exponents and the rate are read off; the solutions are hypergeometric in Sin(x)^2.
    checkResidual("y''(x) == (a + p*(p-1)*Csc(x)^2 + q*(q-1)*Sec(x)^2)*y(x)",
        "D(y(x),{x,2}) - (a + p*(p-1)*Csc(x)^2 + q*(q-1)*Sec(x)^2)*y(x)",
        "{x->13/10, C(1)->7/5, C(2)->3/4, a->-3/10, p->13/10, q->7/10}");

    checkResidual("y''(x) + a*Csc(x)^2*y(x) == 0", "D(y(x),{x,2}) + a*Csc(x)^2*y(x)",
        "{x->13/10, C(1)->7/5, C(2)->3/4, a->-3/10}");

    // The same potential written two other ways.
    checkResidual("y''(x) == ((a*Cos(x)^2 + b*Sin(x)^2 + c)*y(x))/Sin(x)^2",
        "D(y(x),{x,2}) - ((a*Cos(x)^2 + b*Sin(x)^2 + c)*y(x))/Sin(x)^2",
        "{x->13/10, C(1)->7/5, C(2)->3/4, a->-3/10, b->1/5, c->1/10}");
    checkResidual("y''(x) == (a + b*Cot(x)^2)*y(x)", "D(y(x),{x,2}) - (a + b*Cot(x)^2)*y(x)",
        "{x->13/10, C(1)->7/5, C(2)->3/4, a->-3/10, b->1/5}");

    // An odd power of the cosecant is not this shape, and the row must not claim it.
    check("DSolve(y''(x) == (a + b*Csc(x)^3)*y(x), y(x), x)", //
        "DSolve(y''(x)==(a+b*Csc(x)^3)*y(x),y(x),x)");

    // The row runs last, so an equation one of the others owns keeps the answer it had: this one
    // stays elementary rather than becoming a hypergeometric pair.
    check("FreeQ(DSolve(y''(x) == (2*Csc(x)^2 - 1)*y(x), y(x), x), Hypergeometric2F1)", //
        "True");

    // A symbolic degree already worked, through the change of variable t == Cos(x);
    checkResidual("y''(x) + Cot(x)*y'(x) + k*(k+1)*y(x) == 0",
        "D(y(x),{x,2}) + Cot(x)*D(y(x),x) + k*(k+1)*y(x)",
        "{x->13/10, C(1)->7/5, C(2)->3/4, k->7/10}");
  }

  @Test
  public void testDSolveKovacicCase1() {
    // z1 == x*(x-1) solves the reduced form, so the logarithmic derivative 1/x + 1/(x-1) is
    // rational and the pole part of the guess reaches it.
    checkResidual("y''(x) == 2*y(x)/(x*(x-1))", "D(y(x),{x,2}) - 2*y(x)/(x*(x-1))",
        "{x->17/13, C(1)->3/7, C(2)->5/11}");

    // A polynomial part in the guess as well as a pole part.
    checkResidual("y''(x) == (1 + 2/x)*y(x)", "D(y(x),{x,2}) - (1 + 2/x)*y(x)",
        "{x->17/13, C(1)->3/7, C(2)->5/11}");

    checkResidual("y''(x) == (2/x^2 + 2/x + 1)*y(x)", "D(y(x),{x,2}) - (2/x^2 + 2/x + 1)*y(x)",
        "{x->17/13, C(1)->3/7, C(2)->5/11}");

    // A first derivative in the equation, which the reduction to the normal form takes out. The
    // two solutions are conjugates of one another, and are given back as the real pair they span.
    check("DSolve(y''(x) - (2/x)*y'(x) + (2/x^2 + 1)*y(x) == 0, y(x), x)", //
        "{{y(x)->x*C(1)*Cos(x)-x*C(2)*Sin(x)}}");

    // Two solutions, and they are different ones.
    check(
        "With({b=(y(x) /. DSolve(y''(x) == 2*y(x)/(x*(x-1)), y(x), x)[[1,1]])},"
            + " Abs(N(Wronskian({D(b,C(1)), D(b,C(2))}, x) /. x->17/13)) > 10^-6)", //
        "True");
  }

  @Test
  public void testDSolveOperatorFactor() {
    // The operator of this equation is (D^2 - 1)(D + 2/x), so 1/x^2 solves it and dividing that
    // factor out leaves an equation of the second order which the cascade already answers.
    checkResidual("y'''(x) + 2*y''(x)/x - 4*y'(x)/x^2 + 4*y(x)/x^3 - y'(x) - 2*y(x)/x == 0",
        "D(y(x),{x,3}) + 2*D(y(x),{x,2})/x - 4*D(y(x),x)/x^2 + 4*y(x)/x^3 - D(y(x),x)"
            + " - 2*y(x)/x",
        "{x->17/13, C(1)->3/7, C(2)->5/11, C(3)->2/9}");

    // The same equation cleared of its denominators.
    checkResidual("x^3*y'''(x) + 2*x^2*y''(x) - 4*x*y'(x) + 4*y(x) - x^3*y'(x) - 2*x^2*y(x) == 0",
        "x^3*D(y(x),{x,3}) + 2*x^2*D(y(x),{x,2}) - 4*x*D(y(x),x) + 4*y(x)"
            + " - x^3*D(y(x),x) - 2*x^2*y(x)",
        "{x->17/13, C(1)->3/7, C(2)->5/11, C(3)->2/9}");

    // An equation of the third order has a solution space of three dimensions, and the answer
    // spans it: the three solutions it is built from are independent.
    check(
        "With({b=(y(x) /. DSolve(y'''(x) + 2*y''(x)/x - 4*y'(x)/x^2 + 4*y(x)/x^3 - y'(x)"
            + " - 2*y(x)/x == 0, y(x), x)[[1,1]])},"
            + " Abs(N(Wronskian({D(b,C(1)), D(b,C(2)), D(b,C(3))}, x) /. x->17/13)) > 10^-6)", //
        "True");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testDSolvePDEInitialValue() {
    // d'Alembert: what starts at a point of the string reaches it again from both sides at the
    // speed the equation names, so the shape contributes its value at the two ends of the interval
    // the signal has crossed and the speed contributes its mean over that interval.
    check(
        "DSolve({D(u(x,t),{t,2}) == D(u(x,t),{x,2}), u(x,0) == f(x),"
            + " Derivative(0,1)[u][x,0] == g(x)}, u(x,t), {x,t})", //
        "{{u(x,t)->1/2*(f(-t+x)+f(t+x))+Integrate(g(K),{K,-t+x,t+x})/2}}");

    // The speed of the signal is read off the equation, and shows up in both places.
    check(
        "DSolve({D(u(x,t),{t,2}) == 4*D(u(x,t),{x,2}), u(x,0) == f(x),"
            + " Derivative(0,1)[u][x,0] == g(x)}, u(x,t), {x,t})", //
        "{{u(x,t)->1/2*(f(-2*t+x)+f(2*t+x))+Integrate(g(K),{K,-2*t+x,2*t+x})/4}}");

    // The conditions need not be given at t == 0, nor in any particular order.
    check(
        "DSolve({f(x) == u(x,1), D(u(x,t),{t,2}) == D(u(x,t),{x,2}),"
            + " Derivative(0,1)[u][x,1] == g(x)}, u(x,t), {x,t})", //
        "{{u(x,t)->1/2*(f(1-t+x)+f(-1+t+x))+Integrate(g(K),{K,1-t+x,-1+t+x})/2}}");

    // The heat kernel: every point of the starting temperature contributes everywhere, weighted by
    // the temperature a single hot point would have spread to. The integral is left as it stands,
    // having no closed form for a temperature which is not given.
    check("DSolve({D(u(x,t),t) == D(u(x,t),{x,2}), u(x,0) == f(x)}, u(x,t), {x,t})", //
        "{{u(x,t)->Integrate((E^(-(-K+x)^2/(4*t))*f(K))/Sqrt(4*Pi*t),{K,-Infinity,Infinity})}}");

    check("DSolve({D(u(x,t),t) == 3*D(u(x,t),{x,2}), u(x,0) == f(x)}, u(x,t), {x,t})", //
        "{{u(x,t)->Integrate((E^(-(-K+x)^2/(12*t))*f(K))/Sqrt(12*Pi*t),{K,-Infinity,Infinity})}}");

    // Only these two shapes: a term of lower order, a drift along the rod, a diffusivity of the
    // wrong sign, or two displacements instead of a displacement and a speed are all declined.
    check(
        "DSolve({D(u(x,t),{t,2}) == D(u(x,t),{x,2}) - u(x,t), u(x,0) == f(x),"
            + " Derivative(0,1)[u][x,0] == g(x)}, u(x,t), {x,t})", //
        "DSolve({Derivative(0,2)[u][x,t]==-u(x,t)+Derivative(2,0)[u][x,t],u(x,0)==f(x),Derivative(\n"
            + "0,1)[u][x,0]==g(x)},u(x,t),{x,t})");

    check("DSolve({D(u(x,t),t) == -D(u(x,t),{x,2}), u(x,0) == f(x)}, u(x,t), {x,t})", //
        "DSolve({Derivative(0,1)[u][x,t]==-Derivative(2,0)[u][x,t],u(x,0)==f(x)},u(x,t),{x,t})");

    check("DSolve({D(u(x,t),t) == D(u(x,t),{x,2}) + D(u(x,t),x), u(x,0) == f(x)}, u(x,t), {x,t})", //
        "DSolve({Derivative(0,1)[u][x,t]==Derivative(1,0)[u][x,t]+Derivative(2,0)[u][x,t],u(x,\n"
            + "0)==f(x)},u(x,t),{x,t})");

    check(
        "DSolve({D(u(x,t),{t,2}) == D(u(x,t),{x,2}), u(x,0) == f(x), u(x,1) == g(x)},"
            + " u(x,t), {x,t})", //
        "DSolve({Derivative(0,2)[u][x,t]==Derivative(2,0)[u][x,t],u(x,0)==f(x),u(x,1)==g(x)},u(x,t),{x,t})");
  }

  @Test
  public void testDSolveTriangularSystem() {
    // Coupled, so the system does not split into blocks, but one equation mentions only x and
    // solving it makes the other one a scalar equation in y. The coefficients depend on the
    // variable, which is what puts the system out of reach of the matrix construction; a system
    // whose matrix is constant is left to that construction, which names the constants after the
    // unknowns rather than after the order the unknowns are found in.
    check("With({s=DSolve({x'(t) == x(t), y'(t) == x(t) + t*y(t)}, {x(t), y(t)}, t)[[1]]},"
        + " Block({X=x(t)/.s, Y=y(t)/.s},"
        + " Max(Abs(N({D(X,t)-X, D(Y,t)-X-t*Y} /. {t->17/13, C(1)->3/7, C(2)->5/11})))) < 10^-6)", //
        "True");

    check("With({s=DSolve({y'(t) == t^2*y(t), x'(t) == y(t)}, {x(t), y(t)}, t)[[1]]},"
        + " Block({X=x(t)/.s, Y=y(t)/.s},"
        + " Max(Abs(N({D(Y,t)-t^2*Y, D(X,t)-Y} /. {t->17/13, C(1)->3/7, C(2)->5/11})))) < 10^-6)", //
        "True");

    // The conditions are fitted the same way as for any other system.
    check(
        "With({s=DSolve({x'(t) == x(t), y'(t) == x(t) + t*y(t), x(0) == 1, y(0) == 0},"
            + " {x(t), y(t)}, t)[[1]]}, Block({X=x(t)/.s, Y=y(t)/.s},"
            + " Max(Abs(N({D(X,t)-X, D(Y,t)-X-t*Y, X-1, Y} /. t->0)))) < 10^-6)", //
        "True");

    // Unknowns which depend on one another in a circle put no equation in that position.
    check("DSolve({x'(t) == x(t)^2*y(t), y'(t) == x(t)}, {x(t), y(t)}, t)", //
        "DSolve({x'(t)==x(t)^2*y(t),y'(t)==x(t)},{x(t),y(t)},t)");
  }

  @Test
  public void testDSolveFactorable() {
    // Anything solving one factor solves the product, so each factor is an equation of its own
    // and the branches are collected. The branches are alternatives rather than parts of one
    // solution, so each names its constants from the same place.
    check("DSolve((y'(x) - y(x))*(y'(x) + y(x)) == 0, y(x), x)", //
        "{{y(x)->E^x*C(1)},{y(x)->C(1)/E^x}}");

    // The same equation multiplied out, which is the shape it is usually written in.
    check("DSolve(y'(x)^2 - y(x)^2 == 0, y(x), x)", //
        "{{y(x)->E^x*C(1)},{y(x)->C(1)/E^x}}");

    check("DSolve(x*y'(x)^2 - (x + y(x))*y'(x) + y(x) == 0, y(x), x)", //
        "{{y(x)->x+C(1)},{y(x)->x*C(1)}}");

    // The factors need not be of the first order.
    check("DSolve(y''(x)^2 - y'(x)^2 == 0, y(x), x)", //
        "{{y(x)->C(1)+E^x*C(2)},{y(x)->C(1)/E^x+C(2)}}");
  }

  @Test
  public void testDSolveFirstOrderReduction() {
    // The right hand side depends on x and y only through x + y, so in that combination the
    // equation has no x left and is separable.
    check("DSolve(y'(x) == (x + y(x))^2, y(x), x)", //
        "{{y(x)->-x+Tan(x+C(1))}}");

    check("DSolve(y'(x) == (x + y(x) + 1)^2, y(x), x)", //
        "{{y(x)->-1-x+Tan(x+C(1))}}");

    // The two lines in the ratio meet at (1, 2); moving the origin there cancels both constant
    // terms and leaves an equation which is homogeneous of degree zero.
    checkResidual("y'(x) == (y(x) - 2)/(x + y(x) - 3)", "D(y(x),x)*(x + y(x) - 3) - (y(x) - 2)",
        "{x->17/13, C(1)->3/7}");

    // Not every such reduction ends in something which can be solved for y: this one leaves
    // 2*Sqrt(v) - 2*Log(1+Sqrt(v)) == x + C, which is not invertible, so the equation is declined
    // rather than answered in a shape DSolve does not otherwise return.
    check("DSolve(y'(x) == Sqrt(x + y(x)), y(x), x)", //
        "DSolve(y'(x)==Sqrt(x+y(x)),y(x),x)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testDSolveLinearizable() {
    // u == Log(y) makes this linear in u.
    check("DSolve(y'(x) == y(x)*(E^x + Log(y(x))), y(x), x)", //
        "{{y(x)->E^(E^x*x+E^x*C(1))}}");

    // u == Exp(y).
    check("DSolve(y'(x) == E^(x - y(x))*(E^x - E^y(x)), y(x), x)", //
        "{{y(x)->Log(-1+E^x+C(1)/E^E^x)}}");

    // u == Sin(y), which the Tan and Sec of the equation are made of.
    check("DSolve(y'(x) - Tan(y(x))/(x+1) == (x+1)*E^x*Sec(y(x)), y(x), x)", //
        "{{y(x)->ArcSin(E^x+E^x*x+C(1)+x*C(1))}}");

    // u == Sin(y) again, leaving a Bernoulli equation rather than a linear one. The coefficient of
    // the derivative depends on y here, which is no obstacle to solving for the derivative.
    check("DSolve(y'(x)*Cos(y(x)) - Cos(x)*Sin(y(x))^2 - Sin(y(x)) == 0, y(x), x)", //
        "{{y(x)->ArcSin(1/(C(1)/E^x-Cos(x)/2-Sin(x)/2))}}");

    // u == Cos(y); the Cos(2*y) has to be written in Cos(y) before the substitution is made.
    check(
        "DSolve(y'(x) == (-2*Cos(y(x)) + x^3*Cos(2*y(x))*Log(x) + x^3*Log(x))"
            + "/(2*Sin(y(x))*Log(x)*x), y(x), x)", //
        "{{y(x)->ArcCos(1/(x^3/3-x^3/(9*Log(x))+C(1)/Log(x)))}}");

    // A separable equation is left to the solver for those, which answers it in a simpler form.
    check("DSolve(y'(x) == Sin(x)*y(x), y(x), x)", //
        "{{y(x)->C(1)/E^Cos(x)}}");
  }

  @Test
  public void testDSolveRiccatiWithAiryReduction() {
    // y' == x + y^2 reduces to u''(x) + x*u(x) == 0, whose solutions are Airy functions of
    // (-1)^(1/3)*x. Putting the answer over a common denominator used to change its value, so it
    // was rejected by the back substitution and the equation was declined.
    checkResidual("y'(x) == x + y(x)^2", "y'(x) - x - y(x)^2", "{C(1)->7/5, x->13/10}");

    // The same equation with the other sign, whose Airy functions are real.
    checkResidual("y'(x) == -x + y(x)^2", "y'(x) + x - y(x)^2", "{C(1)->7/5, x->13/10}");
  }

  @Test
  public void testDSolveHomogeneousLogarithmicIntegral() {
    // Separating the variables here leaves Integrate(1/(1-v^2), v), which Symja answers with a sum
    // of logarithms that nothing could solve for v; collecting it into ArcTanh(v) first makes the
    // inversion succeed.
    check("DSolve(y'(x) == (y(x)^2 + x*y(x) - x^2)/x^2, y(x), x)", //
        "{{y(x)->x*Tanh(C(1)-Log(x))}}");
  }

  @Test
  @Tag(TestTags.SLOW)
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
  public void testDSolveHermite() {
    // Hermite's equation with a symbolic degree. Both solutions are series in x^2, one even and
    // one odd, which is a basis whatever the degree is.
    check("DSolve(y''(x) - 2*x*y'(x) + 2*n*y(x) == 0, y(x), x)", //
        "{{y(x)->x*C(2)*Hypergeometric1F1(1/2-n/2,3/2,x^2)+C(1)*Hypergeometric1F1(-n/2,1/\n" //
            + "2,x^2)}}");

    // A whole degree makes one of the pair a polynomial. Naming HermiteH here instead would give
    // the same function twice: HermiteH(3,x) is the odd solution, and it is proportional to the
    // even series only for an even degree, so the pair would be a basis for one parity and not
    // the other.
    check("DSolve(y''(x) - 2*x*y'(x) + 6*y(x) == 0, y(x), x)", //
        "{{y(x)->x*C(2)-2/3*x^3*C(2)+C(1)*Hypergeometric1F1(-3/2,1/2,x^2)}}");

    // A coefficient in front of y'' and a positive b, where the substitution's constant factor is
    // imaginary. It is a constant, so the arbitrary constant beside it absorbs it, and the
    // argument x^2*(-b/2) stays real.
    check("DSolve(3*y''(x) + x*y'(x) - 4*y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)+2/3*x^2*C(1)+1/27*x^4*C(1)+x*C(2)*Hypergeometric1F1(-3/2,3/2,-x^2/6)}}");

    check("DSolve(5*y''(x) - 2*x*y'(x) + 10*y(x) == 0, y(x), x)", //
        "{{y(x)->x*C(2)-4/15*x^3*C(2)+4/375*x^5*C(2)+C(1)*Hypergeometric1F1(-5/2,1/2,x^2/\n" //
            + "5)}}");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testDSolveHypergeometric() {
    check("DSolve(x*y''(x) + (b - x)*y'(x) - a*y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)*Hypergeometric1F1(a,b,x)+x^(1-b)*C(2)*Hypergeometric1F1(1+a-b,2-b,x)}}");

    check("DSolve((x^2 - x)*y''(x) + ((a + b + 1)*x - c)*y'(x) + a*b*y(x) == 0, y(x), x)", //
        "{{y(x)->C(1)*Hypergeometric2F1(a,b,c,x)+x^(1-c)*C(2)*Hypergeometric2F1(1+a-c,1+b-c,\n" //
            + "2-c,x)}}");

    // An integer c makes the second solution a copy of the first, so this row has no basis for
    // it. The row which maps the singular points about instead answers it: the exponents there
    // are not the ones this row divides out, and the pair it builds from them is independent.
    check(
        "FreeQ(DSolve((x^2 - x)*y''(x) + ((a + b + 1)*x - 2)*y'(x) + a*b*y(x) == 0, y(x), x),"
            + " Hypergeometric2F1)", //
        "False");
    checkResidual("(x^2 - x)*y''(x) + ((a + b + 1)*x - 2)*y'(x) + a*b*y(x) == 0", //
        "(x^2 - x)*y''(x) + ((a + b + 1)*x - 2)*y'(x) + a*b*y(x)",
        "{C(1)->7/5, C(2)->3/4, a->3/10, b->1/5, x->3/10}");
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
    // The first order solvers used to be offered this, and answered from the part of it they could
    // read, which produced an expression containing y''(x) itself. It has an answer of its own now:
    // (x*y)'' is x*y'' + 2*y', so this is w'' - w == Sin(x) in w == x*y, and variation of parameters
    // on the homogeneous basis E^(+-x)/x gives the particular solution -Sin(x)/(2*x).
    check("DSolve(x*y''(x) + 2*y'(x) - x*y(x) == Sin(x), y(x), x)", //
        "{{y(x)->C(1)/(E^x*x)+(E^x*C(2))/x-Sin(x)/(2*x)}}");

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
    check("DSolve({y'(x) == y(x) - 2*z(x), z'(x) == y(x) - z(x), y(0) == 1, z(0) == 4}, {y, z}, x)", //
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
    check(
        "DSolve(u(x,y) == x*D(u(x,y),x) + y*D(u(x,y),y) + Sin(D(u(x,y),x) + D(u(x,y),y)), u, {x, y})", //
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
