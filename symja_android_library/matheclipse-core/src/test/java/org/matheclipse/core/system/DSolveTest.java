package org.matheclipse.core.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for DSolve */
public class DSolveTest extends ExprEvaluatorTestCase {

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
        "{{y(x)->C(1)/E^(2*ArcTanh(x))}}");
    check("DSolve(y'(x) == -y(x), y(x), x)", //
        "{{y(x)->C(1)/E^x}}");
    check("DSolve(y'(x) == y(x)+a*Cos(x), y(x), x)", //
        "{{y(x)->E^x*C(1)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");
    // not implemented yet
    check("DSolve(y'(x) == -3*y(x)^2, y(x), x)", //
        "{{y(x)->1/(3*x-C(1))}}");
    check("DSolve({y'(x) == -3*y(x)^2, y(0)==2}, y(x), x)", //
        "{{y(x)->1/(1/2+3*x)}}");
  }

  @Test
  public void testDSolveSystem001() {
    check("tst(0)=1;DSolve({a *(f'(x))^2+f'''(x)==0,f'(0)==0,tst(0)==0},f(x),x)", //
        "DSolve({a*f'(x)^2+Derivative(3)[f][x]==0,f'(0)==0,False},f(x),x)");
    // DSolve: For some branches of the general solution, unable to solve for the conditions
    check("DSolve({a *(f'(x))^2+f'''(x)==0,f'(0)==0},f(x),x)", //
        "DSolve({a*f'(x)^2+Derivative(3)[f][x]==0,f'(0)==0},f(x),x)");

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
        "{{y->Function({x},1/5-Cos(x)/10+3/10*Sin(x)),z->Function({x},Cos(x)/10-3/10*Sin(x))}}");
  }

  @Test
  public void testDSolveRiccati() {
    // Full Riccati Equation with constant coefficients: y'(x) = y(x)^2 + 1
    // Matches the separation of variables logic, leading to ArcTan integration
    check("DSolve(y'(x) == y(x)^2 + 1, y(x), x)", //
        "{{y(x)->Tan(x+C(1))}}");

    // Full Riccati Equation with different constants: y'(x) = y(x)^2 - 1
    check("DSolve(y'(x) == y(x)^2 - 1, y(x), x)", //
        "{{y(x)->-Tanh(x+C(1))}}");
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
        "{{y(x)->1/Sqrt(-1+C(1)/E^(2*x))}}");

    // Bernoulli equation with variable coefficients and n=3: x*y'(x) + y(x) == x^3*y(x)^3
    // Transforms to y' + (1/x)y = x^2 y^3
    check("DSolve(x*y'(x) + y(x) == x^3*y(x)^3, y(x), x)", //
        "{{y(x)->1/Sqrt(-2*x^3+x^2*C(1))}}");

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
    // Must match: DSolve({y'(x)==-3*y(x)^2, y(0)==2}, y(x), x) == {{y(x)->1/(1/2+3*x)}}
    check("DSolve({y'(x)==-3*y(x)^2, 3*y(0)==6}, y(x), x)", //
        "{{y(x)->1/(1/2+3*x)}}");
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
        "{{y(x)->1/(1/2+3*x)}}");
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
        "{{y(x)->Sqrt(2*x*C(1)-C(2))}}");
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
