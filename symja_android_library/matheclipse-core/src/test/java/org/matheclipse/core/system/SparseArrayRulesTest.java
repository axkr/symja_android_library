package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** The SparseArray rule {p1, p2, ...} -> {v1, v2, ...} (WMA). */
public class SparseArrayRulesTest extends ExprEvaluatorTestCase {

  @Test
  public void testPositionListRuleWMA() {
    // it came out all zeros, silently
    check("Normal(SparseArray({{1, 1}, {3, 2}} -> {a, b}, {3, 3}))", //
        "{{a,0,0},{0,0,0},{0,b,0}}");
    check("Normal(SparseArray({{1, 1}, {2, 2}} -> {a, b}))", //
        "{{a,0},{0,b}}");
    check("Normal(SparseArray({{{1, 1}, {2, 2}} -> {a, b}, {3, 3} -> c}))", //
        "{{a,0,0},{0,b,0},{0,0,c}}");
    // positions and values of different lengths stay unevaluated
    check("SparseArray({{1, 1}, {2, 2}} -> {a, b, c})", //
        "SparseArray({{1,1},{2,2}}->{a,b,c})");
  }

  @Test
  public void testRuleOutsideTheDimensions() {
    // a position outside the dimensions leaves the call unevaluated
    check("SparseArray({{1, 4} -> a}, {3, 3})", //
        "SparseArray({{1,4}->a},{3,3})");
    check("SparseArray({{2, 2} -> a, {6, 2} -> b}, 5)", //
        "SparseArray({{2,2}->a,{6,2}->b},5)");
    check("SparseArray({{2, 0} -> a}, {3, 3})", //
        "SparseArray({{2,0}->a},{3,3})");
    check("SparseArray({{2} -> a}, {3, 3})", //
        "SparseArray({{2}->a},{3,3})");
    check("SparseArray({{4, _} -> a}, {3, 3})", //
        "SparseArray({{4,_}->a},{3,3})");
    check("SparseArray({5 -> a}, 3)", //
        "SparseArray({5->a},3)");
    // positions inside, also counted from the end, and patterns
    check("Normal(SparseArray({{-1, 2} -> a}, {3, 3}))", //
        "{{0,0,0},{0,0,0},{0,a,0}}");
    check("Normal(SparseArray({{i_, 1} -> a}, {2, 2}))", //
        "{{a,0},{a,0}}");
    check("Normal(SparseArray({{2, 3} -> a}, 3))", //
        "{{0,0,0},{0,0,a},{0,0,0}}");
  }

  @Test
  public void testLinearSolveOfNoMatrix() {
    // a list which is no matrix is reported, a symbol stays silently
    check("LinearSolve({3, 4}, {3, 4})", //
        "LinearSolve({3,4},{3,4})");
    check("LinearSolve({{3, 4}, {5}}, {3, 4})", //
        "LinearSolve({{3,4},{5}},{3,4})");
    check("LinearSolve(g, {3, 4})", //
        "LinearSolve(g,{3,4})");
    // an unevaluated SparseArray is no matrix either
    check("LinearSolve(SparseArray({{4, 1} -> 1}, {3, 3}), {1, 2, 3})", //
        "LinearSolve(SparseArray({{4,1}->1},{3,3}),{1,2,3})");
  }
}
