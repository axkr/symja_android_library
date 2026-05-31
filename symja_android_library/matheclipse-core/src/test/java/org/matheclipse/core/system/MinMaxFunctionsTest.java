package org.matheclipse.core.system;

import org.junit.Test;

public class MinMaxFunctionsTest extends ExprEvaluatorTestCase {
  /**
   * Feature #1: unconstrained multivariate quadratic. If the Hessian is positive (negative)
   * definite, there is a unique minimizer (maximizer) {@code x* = -1/2*Inverse(Q).b}.
   *
   * <p>
   * NOTE: not yet implemented - expected results are the target closed forms.
   */
  @Test
  public void testUnconstrainedQuadratic() {
    // positive definite Hessian -> unique minimum at origin
    check("Minimize(x^2 + y^2 + 2, {x, y})", //
        "{2,{x->0,y->0}}");
    // negative definite Hessian -> unique maximum at origin
    check("Maximize(-x^2 - y^2 + 4, {x, y})", //
        "{4,{x->0,y->0}}");

    // positive definite Hessian with cross term and linear part
    // grad: 2*x+y==0, x+2*y-3==0 -> x=-1, y=2
    check("Minimize(x^2 + x*y + y^2 - 3*y, {x, y})", //
        "{-3,{x->-1,y->2}}");

    // negative definite Hessian with cross term and linear part
    // grad: -2*x-y+2==0, -x-2*y+2==0 -> x=2/3, y=2/3
    check("Maximize(-x^2 - x*y - y^2 + 2*x + 2*y, {x, y})", //
        "{4/3,{x->2/3,y->2/3}}");
  }

  /**
   * Feature #2: linear objective over an axis-aligned ellipsoid / n-sphere
   * {@code Sum(k_i*v_i^2) <= R^2}. By Cauchy-Schwarz the optimum value is
   * {@code c0 +/- R*Sqrt(Sum(a_i^2/k_i))}.
   *
   * <p>
   * NOTE: not yet implemented - expected results are the target closed forms.
   */
  @Test
  public void testLinearEllipsoidConstraint() {
    // ellipsoid 9*x^2 + 4*y^2 <= 36 ; value = 6*Sqrt(9/9 + 16/4)/... designed to be rational
    check("Maximize({9*x + 8*y, 9*x^2 + 4*y^2 <= 36}, {x, y})", //
        "{30,{x->6/5,y->12/5}}");
    check("Minimize({9*x + 8*y, 9*x^2 + 4*y^2 <= 36}, {x, y})", //
        "{-30,{x->-6/5,y->-12/5}}");

    // 3-variable sphere radius 3
    check("Maximize({2*x + 2*y + z, x^2 + y^2 + z^2 <= 9}, {x, y, z})", //
        "{9,{x->2,y->2,z->1}}");
    check("Minimize({2*x + 2*y + z, x^2 + y^2 + z^2 <= 9}, {x, y, z})", //
        "{-9,{x->-2,y->-2,z->-1}}");
  }

  /**
   * Feature #3: Lagrange multipliers for a single smooth equality / active inequality constraint.
   * Solve {@code Grad(f) == lambda*Grad(g)} together with the constraint and classify candidates.
   *
   * <p>
   * NOTE: not yet implemented - expected results are the target closed forms.
   */
  @Test
  public void testLagrangeConstraint() {
    // bilinear objective on a disk: x*y <= (x^2+y^2)/2 = 1
    check("Maximize({x*y, x^2 + y^2 <= 2}, {x, y})", //
        "{1,{x->-1,y->-1}}");

    // quadratic objective with a linear equality constraint x+y==2
    check("Minimize({x^2 + y^2, x + y == 2}, {x, y})", //
        "{2,{x->1,y->1}}");

    // the previously unsupported non-linear case (boundary minimum)
    check("Minimize({x^2 - (y - 1)^2, x^2 + y^2 <= 4}, {x, y})", //
        "{-9,{x->0,y->-2}}");
  }

  /**
   * Feature #6: exact (rational) linear programming. A linear objective with linear inequality /
   * equality constraints returns the exact rational optimum at a vertex of the polytope.
   * Convention: the variables range over all reals (no implicit non-negativity), so every test uses
   * a constraint set that bounds the feasible region.
   */
  @Test
  public void testLinearProgrammingExact() {
    // bounded polytope, integer vertex optima
    check("Maximize({3*x + 2*y, x >= 0 && y >= 0 && x + y <= 4 && x + 2*y <= 6}, {x, y})", //
        "{12,{x->4,y->0}}");
    check("Minimize({3*x + 2*y, x >= 0 && y >= 0 && x + y <= 4 && x + 2*y <= 6}, {x, y})", //
        "{0,{x->0,y->0}}");

    // fractional vertex optimum
    check("Maximize({x + y, 3*x + 2*y <= 12 && x + 2*y <= 6 && x >= 0 && y >= 0}, {x, y})", //
        "{9/2,{x->3,y->3/2}}");

    // equality constraint with bounds
    check("Minimize({x + 2*y, x + y == 10 && x >= 0 && y >= 0 && x <= 7}, {x, y})", //
        "{13,{x->7,y->3}}");
    check("Maximize({x + 2*y, x + y == 10 && x >= 0 && y >= 0 && x <= 7}, {x, y})", //
        "{20,{x->0,y->10}}");
  }

  @Test
  public void testMaximize() {
    check("Maximize({3*x + 4*y, x^2 + y^2 <= 1}, {x, y})", //
        "{5,{x->3/5,y->4/5}}");

    // print message - Maximize: The maximum is not attained at any point satisfying the
    // constraints.
    check("Maximize(1/x, x)", //
        "{}");

    check("Maximize(-x^4-7*x^3+2*x^2 - 42,x)", //
        "{-42-7/512*(-21-Sqrt(505))^3+(21+Sqrt(505))^2/32-(21+Sqrt(505))^4/4096,{x->1/8*(-\n"
            + "21-Sqrt(505))}}");
    check("Maximize(x^4+7*Tan(x)-2*x^2 + 42, x)", //
        "Maximize(42-2*x^2+x^4+7*Tan(x),x)");
    check("Maximize(x^4+7*x^3-2*x^2 + 42, x)", //
        "{Infinity,{x->-Infinity}}");
    check("Maximize(-2*x^2 - 3*x + 5, x)", //
        "{49/8,{x->-3/4}}");
  }

  @Test
  public void testMinimize() {
    check("Minimize({3*x + 4*y, x^2 + y^2 <= 1}, {x, y})", //
        "{-5,{x->-3/5,y->-4/5}}");
    check("Minimize(-1+x^2,x)", //
        "{-1,{x->0}}");

    // check("Minimize(Sin(x),x)", //
    // "");

    // print message - Minimize: The minimum is not attained at any point satisfying the
    // constraints.
    check("Minimize(1/x, x)", //
        "{}");


    check("Minimize(x^2+4*x+4, {x})", //
        "{0,{x->-2}}");

    check("Minimize(x^4+7*x^3-2*x^2 + 42, x)", //
        "{42+7/512*(-21-Sqrt(505))^3-(21+Sqrt(505))^2/32+(21+Sqrt(505))^4/4096,{x->1/8*(-\n"
            + "21-Sqrt(505))}}");
    check("Minimize(2*x^2 - 3*x + 5, x)", //
        "{31/8,{x->3/4}}");
  }

  @Test
  public void testNArgMax() {
    checkNumeric("NArgMax(-2*x^2-3*x+5,x)", //
        "{-0.7500000000000001}");
    checkNumeric("NArgMax(1-(x*y-3)^2, {x, y})", //
        "{1.0,3.0}");
  }


  @Test
  public void testNArgMin() {
    checkNumeric("NArgMin(2*x^2-3*x+5,x)", //
        "{0.7499999999999998}");

    checkNumeric("NArgMin((x*y-3)^2+1, {x,y})", //
        "{1.0,3.0}");
    checkNumeric("NArgMin({x-2*y, x+y<= 1}, {x, y})", //
        "{0.0,1.0}");
  }

  @Test
  public void testNMaxValue() {
    checkNumeric("NMaxValue(-2*x^2-3*x+5,x)", //
        "6.125");
    checkNumeric("NMaxValue(1-(x*y-3)^2, {x, y})", //
        "1.0");
  }

  @Test
  public void testNMinValue() {
    checkNumeric("NMinValue(2*x^2-3*x+5,x)", //
        "3.875");

    checkNumeric("NMinValue((x*y-3)^2+1, {x,y})", //
        "1.0");
    checkNumeric("NMinValue({x-2*y, x+y<= 1}, {x, y})", //
        "-2.0");
  }

  @Test
  public void testIssue80() {
    // issue #80: LinearProgramming with expressions
    check("NMinimize({-2*x+y-5, 2*y+x<=6&&2*y+3*x<=12&&y>=0},{x,y})", //
        "{-13.0,{x->4.0,y->0.0}}");
    check("NMaximize({-2*x+y-5, 2*y+x<=6&&2*y+3*x<=12&&y>=0},{x,y})", //
        "{-2.0,{x->0.0,y->3.0}}");
  }

  @Test
  public void testNMaximize() {
    // checkNumeric("NMaximize({Sin(x^2), 0 <= x && x <= 0.5}, {x})", //
    // "{0.24740395925452294,{x->0.5}}");

    checkNumeric("NMaximize(-x^4 - 3* x^2 + x, x)", //
        "{0.08258881886826407,{x->0.16373996720676331}}");

    check("NMaximize({-2*x+y-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
        "{-2.0,{x->0.0,y->3.0}}");
    check("NMaximize({-x - y, 3*x + 2*y >= 7 && x + 2*y >= 6}, {x, y})", //
        "{-3.25,{x->0.5,y->2.75}}");
  }

  @Test
  public void testNMinimize() {
    check("NMinimize({-5-2*x+y,x+2*y<=6&&3*x+2*y<=12},{x,y})", //
        "{-13.0,{x->4.0,y->0.0}}");

    check("NMinimize({-Sinc(x)-Sinc(y)}, {x, y})", //
        "{-2.0,{x->0.0,y->0.0}}");

    check("NMinimize(x^2 + y^2 + 2, {x,y})", //
        "{2.0,{x->0.0,y->0.0}}");
    check("NMinimize({Sinc(x)+Sinc(y)}, {x, y})", //
        "{-0.434467,{x->4.49341,y->4.49341}}");

    checkNumeric("NMinimize({Sinc(x)+Sinc(y)}, {x, y})", //
        "{-0.4344672564224433,{x->4.493409457896563,y->4.493409457738778}}");

    checkNumeric("NMinimize(x^4 - 3*x^2 - x, x)", //
        "{-3.513905038934788,{x->1.3008395656679863}}");

    // TODO non-linear not supported
    // check("NMinimize({x^2 - (y - 1)^2, x^2 + y^2 <= 4}, {x, y})", "");
    check("NMinimize({-2*y+x-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
        "{-11.0,{x->0.0,y->3.0}}");
    check("NMinimize({-2*y+x-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
        "{-11.0,{x->0.0,y->3.0}}");
    check("NMinimize({-2*x+y-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
        "{-13.0,{x->4.0,y->0.0}}");
    check("NMinimize({x + 2*y, -5*x + y == 7 && x + y >= 26 && x >= 3 && y >= 4}, {x, y})", //
        "{48.83333,{x->3.16667,y->22.83333}}");
    check("NMinimize({x + y, 3*x + 2*y >= 7 && x + 2*y >= 6 }, {x, y})", //
        "{3.25,{x->0.5,y->2.75}}");
  }
}
