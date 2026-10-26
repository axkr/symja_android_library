package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests built-in functions */
public class LinearOptimizationTest extends ExprEvaluatorTestCase {

  @Test
  public void testLinearOptimization() {
    check("LinearOptimization(x + y, {x + 2*y >= 3,  x >= 0, y >= 0}, {x, y})", //
        "{x->0.0,y->1.5}");
    // message LinearOptimization: There are no points which satisfy the constraints.
    check("LinearOptimization(x + y, {x + 2*y >= 3,  x < 0, y < 0}, {x, y})", //
        "{x->Indeterminate,y->Indeterminate}");
    // message LinearOptimization: The constraint x!=1 is not convex.
    // the minimum {x->0.0,y->1.5} of the problem without the constraint is no answer
    check("LinearOptimization(x + y, {x + 2*y >= 3, x != 1, x >= 0, y >= 0}, {x, y})", //
        "LinearOptimization(x+y,{x+2*y>=3,x!=1,x>=0,y>=0},{x,y})");
    // integer domains: minimize -5x-4y with 6x+4y<=26, x+2y<=6, x,y>=0
    // over the reals the vertex (3.5, 1.25)
    check("LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, {x, y})", //
        "{x->3.5,y->1.25}");
    // x integer: x=4, y<=0.5 gives -22, x=3, y<=1.5 only -21
    check("LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, {Element(x,Integers), y})", //
        "{x->4,y->0.5}");
    check("LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, {x, y}, {Integers, Reals})", //
        "{x->4,y->0.5}");
    // both integers: (4,0) is the only point of value -20, none has -21 or less
    check("LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, {x, y}, Integers)", //
        "{x->4,y->0}");
    check("LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, Element({x, y}, Integers))", //
        "{x->4,y->0}");
    check("LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, "
        + "{Element(x,Integers), Element(y,Integers)})", //
        "{x->4,y->0}");
    check("LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, {x, y}, Reals)", //
        "{x->3.5,y->1.25}");
    // message LinearOptimization: Complexes is not a valid domain specification; Reals is used
    check("LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, {x, y}, Complexes)", //
        "{x->3.5,y->1.25}");
    // no integer x with 2x==1
    check("LinearOptimization(x+y, {2*x==1}, {x, y}, Integers)", //
        "{x->Indeterminate,y->Indeterminate}");
    // message LinearOptimization: The problem is unbounded.
    check("LinearOptimization(-x-y, {x-y<=1}, {x, y})", //
        "LinearOptimization(-x-y,{x-y<=1},{x,y})");
    // message LinearOptimization: The variable x is given in the domains Integers and Reals.
    check("Head(LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, {Element(x,Reals), y}, "
        + "Integers))", //
        "LinearOptimization");
    // the same domain twice is no conflict
    check("LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, {Element(x,Integers), y}, "
        + "{Integers, Reals})", //
        "{x->4,y->0.5}");
    // a list of domains of the wrong length
    check("LinearOptimization(-5*x-4*y, {6*x+4*y<=26, x+2*y<=6}, {x, y}, {Integers})", //
        "LinearOptimization(-5*x-4*y,{6*x+4*y<=26,x+2*y<=6},{x,y},{Integers})");
    // Method -> "ORTools" without the matheclipse-or-tools module uses the default method
    check("LinearOptimization(x + y, {x + 2*y >= 3,  x >= 0, y >= 0}, {x, y}, "
        + "Method->\"ORTools\")", //
        "{x->0.0,y->1.5}");
  }

  @Test
  public void testLinearProgramming() {
    check("LinearProgramming({1, 1}, {{1, 2}}, {3})", //
        "{0,3/2}");

    check("LinearProgramming({},{{1,2}},{{3,0}})", //
        "LinearProgramming({},{{1,2}},{{3,0}})");
    check("LinearProgramming(Indeterminate,{{1,2}},{{3,0}})", //
        "LinearProgramming(Indeterminate,{{1,2}},{{3,0}})");

    check("LinearProgramming({1, 1}, {{1, 2}}, {{3,0}})", //
        "{0,3/2}");
    check("LinearProgramming({1, 1}, {{1, 2}}, {{3,-1}})", //
        "{0,0}");
    check("LinearProgramming({1., 1.}, {{5., 2.}}, {3.})", //
        "{0.6,0.0}");
  }

  @Test
  public void testLinearProgrammingNoFeasibleSolution() {
    // message: LinearProgramming: No solution can be found that satisfies the constraints.
    check("LinearProgramming({-2, -1,-2}, {{1, 2,0},{3,2,1},{0,1,0}}, {{5, -1},{4,-1},{3,1}})", //
        "LinearProgramming({-2,-1,-2},{{1,2,0},{3,2,1},{0,1,0}},{{5,-1},{4,-1},{3,1}})");
  }

  @Test
  public void testSystem803() {
    // see
    // http://google-opensource.blogspot.com/2009/06/introducing-apache-commons-math.html
    check(
        "LinearProgramming({-2, 1, -5}, {{1, 2, 0},{3, 2, 0},{0,1,0},{0,0,1}}, {{6,-1},{12,-1},{0,1},{1,0}})",
        "{4,0,1}");
    // message: LinearProgramming: inconsistent dimensions: 2 != 3.
    check("LinearProgramming({-2, 1, -5}, {{1, 2},{3, 2},{0,1}},{{6,-1},{12,-1},{0,1}})", //
        "LinearProgramming({-2,1,-5},{{1,2},{3,2},{0,1}},{{6,-1},{12,-1},{0,1}})");
  }

}
