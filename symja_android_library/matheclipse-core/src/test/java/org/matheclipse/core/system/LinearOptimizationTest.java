package org.matheclipse.core.system;

import org.junit.Test;

/** Tests built-in functions */
public class LinearOptimizationTest extends ExprEvaluatorTestCase {

  @Test
  public void testLinearOptimization() {
    check("LinearOptimization(x + y, {x + 2*y >= 3,  x >= 0, y >= 0}, {x, y})", //
        "{x->0.0,y->1.5}");
    // message LinearOptimization: There are no points which satisfy the constraints.
    check("LinearOptimization(x + y, {x + 2*y >= 3,  x < 0, y < 0}, {x, y})", //
        "{x->Indeterminate,y->Indeterminate}");
  }

  @Test
  public void testLinearProgramming() {
    check("LinearProgramming({1, 1}, {{1, 2}}, {3})", //
        "{0.0,1.5}");

    check("LinearProgramming({},{{1,2}},{{3,0}})", //
        "LinearProgramming({},{{1,2}},{{3,0}})");
    check("LinearProgramming(Indeterminate,{{1,2}},{{3,0}})", //
        "LinearProgramming(Indeterminate,{{1,2}},{{3,0}})");

    check("LinearProgramming({1, 1}, {{1, 2}}, {{3,0}})", //
        "{0.0,1.5}");
    check("LinearProgramming({1, 1}, {{1, 2}}, {{3,-1}})", //
        "{0.0,0.0}");
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
        "{4.0,0.0,1.0}");
    // message: LinearProgramming: inconsistent dimensions: 2 != 3.
    check("LinearProgramming({-2, 1, -5}, {{1, 2},{3, 2},{0,1}},{{6,-1},{12,-1},{0,1}})", //
        "LinearProgramming({-2,1,-5},{{1,2},{3,2},{0,1}},{{6,-1},{12,-1},{0,1}})");
  }

}
