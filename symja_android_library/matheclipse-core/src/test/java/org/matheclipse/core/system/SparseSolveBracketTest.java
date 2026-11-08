package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Sparse arrays which stay sparse under KroneckerProduct and listable arithmetic, numerical sparse
 * linear systems, and the root of a bracketing FindRoot method to the precision of x.
 */
public class SparseSolveBracketTest extends ExprEvaluatorTestCase {

  @Test
  public void testKroneckerProductOfSparseMatrices() {
    check("s = KroneckerProduct(SparseArray({{1, 1} -> 2, {2, 1} -> 5}, {2, 2}), "
        + "SparseArray({{1, 2} -> 3, {2, 2} -> -1}, {2, 2})); {Head(s), Length(ArrayRules(s))}", //
        "{SparseArray,5}");
    check("Normal(KroneckerProduct(SparseArray({{1, 1} -> 2, {2, 1} -> 5}, {2, 2}), "
        + "SparseArray({{1, 2} -> 3, {2, 2} -> -1}, {2, 2}))) == "
        + "KroneckerProduct({{2, 0}, {5, 0}}, {{0, 3}, {0, -1}})", //
        "True");
  }

  @Test
  public void testListableArithmeticOfSparseArrays() {
    check("s = SparseArray({{1, 1} -> 4, {3, 2} -> 1}, {3, 3}) + "
        + "SparseArray({{1, 1} -> -4, {2, 2} -> 7}, {3, 3}); {Head(s), Normal(s)}", //
        "{SparseArray,{{0,0,0},{0,7,0},{0,1,0}}}");
    check("Normal(2*SparseArray({{2, 1} -> 3}, {2, 2}) - SparseArray({{1, 2} -> 1}, {2, 2}))", //
        "{{0,-1},{6,0}}");
    // a position stored in an operand stays stored where the values cancel
    check("ArrayRules(SparseArray({{2, 3} -> 5, {1, 1} -> 2}, {3, 3}) + "
        + "SparseArray({{2, 3} -> -5}, {3, 3}))", //
        "{{1,1}->2,{2,3}->0,{_,_}->0}");
    // the stored default is no difference to an array without it
    check("SparseArray({{2, 1} -> 4}, {2, 2}) - SparseArray({{2, 1} -> 4}, {2, 2}) === "
        + "SparseArray({}, {2, 2})", //
        "True");
    check("KroneckerProduct(SparseArray({{1, 1} -> 1}, {70000, 70000}), "
        + "SparseArray({{1, 1} -> 1}, {70000, 70000}))", //
        "KroneckerProduct(SparseArray(Number of elements: 1 Dimensions: {70000,70000} Default "
            + "value: 0),SparseArray(Number of elements: 1 Dimensions: {70000,70000} Default "
            + "value: 0))");
  }

  @Test
  public void testSparseLinearSolve() {
    // a tridiagonal system of 3000 unknowns with the solution 1, 2, 3, ...
    check("n = 3000; m = SparseArray({Band({1, 1}) -> 4.0, Band({1, 2}) -> -1.0, "
        + "Band({2, 1}) -> -1.0}, {n, n}); x = LinearSolve(m, m . N(Range(n))); "
        + "Max(Abs(x - Range(n))) < 10^-9", //
        "True");
    // a symmetric matrix with a wide band: conjugate gradients
    check("n = 2000; m = SparseArray({Band({1, 1}) -> 10.0, Band({1, 500}) -> 1.0, "
        + "Band({500, 1}) -> 1.0}, {n, n}); b = N(Table(Sin(k), {k, n})); "
        + "Max(Abs(m . LinearSolve(m, b) - b)) < 10^-12", //
        "True");
    // an exact entry is evaluated to the working precision, not widened from machine digits
    check("x = LinearSolve(SparseArray({{1, 1} -> 1/7, {2, 2} -> 2}, {2, 2}), N({1, 1}, 50)); "
        + "{Precision(x), Abs(x[[1]] - 7) < 10^-47}", //
        "{50,True}");
    // 40 digits by iterative refinement of the machine solution
    check("n = 500; m = N(SparseArray({Band({1, 1}) -> 3, Band({1, 2}) -> 1, "
        + "Band({2, 1}) -> -1}, {n, n}), 40); x = LinearSolve(m, N(UnitVector(n, 1), 40)); "
        + "{Precision(x[[1]]), Max(Abs(m . x - UnitVector(n, 1))) < 10^-38}", //
        "{40,True}");
  }

  @Test
  public void testBracketingToThePrecisionOfX() {
    // the root of Cos(x) == x/3, to a few ulps, not to the accuracy goal
    check("r = x /. FindRoot(Cos(x) == x/3, {x, 0, 2}, Method -> \"Brent\"); "
        + "s = x /. FindRoot(Cos(x) == x/3, {x, 1}, WorkingPrecision -> 30); Abs(r - s) < 10^-14", //
        "True");
    check("r = x /. FindRoot(Cos(x) == x/3, {x, 0, 2}, Method -> \"Bisection\"); "
        + "s = x /. FindRoot(Cos(x) == x/3, {x, 1}, WorkingPrecision -> 30); Abs(r - s) < 10^-14", //
        "True");
    // the monitor runs at every evaluation of the function
    check("n = 0; FindRoot(Exp(x) == 5, {x, 0, 3}, Method -> \"Brent\", "
        + "EvaluationMonitor :> n++); n > 3", //
        "True");
    check("n = 0; FindRoot(Exp(x) == 5, {x, 1}, EvaluationMonitor :> n++); n > 2", //
        "True");
    // a triple root at 0, where a few ulps aren't reached within the evaluations: the accuracy
    // goal as before
    check("Abs(x /. FindRoot(x^3, {x, -1, 2}, Method -> \"Brent\")) < 10^-5", //
        "True");
  }
}
