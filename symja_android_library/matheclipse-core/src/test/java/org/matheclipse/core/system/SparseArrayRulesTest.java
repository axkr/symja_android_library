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

  @Test
  public void testRejectedRulesGiveNoArray() {
    // SparseArray({7 -> u}, 3) stays an unevaluated call, which other functions don't take apart
    check("Join(SparseArray({7 -> u}, 3), {v})", //
        "Join(SparseArray({7->u},3),{v})");
    check("Join(SparseArray({7 -> u}, 3), SparseArray({2 -> v}, 3))[[1]]", //
        "SparseArray({7->u},3)");
    check("Join(SparseArray({7 -> u}, 3))", //
        "SparseArray({7->u},3)");
    check("Riffle(SparseArray({7 -> u}, 3), w)", //
        "Riffle(SparseArray({7->u},3),w)");
    check("ArrayReshape(SparseArray({7 -> u}, 3), {3})", //
        "ArrayReshape(SparseArray({7->u},3),{3})");
    check("Flatten({{SparseArray({7 -> u}, 3)}})", //
        "{SparseArray({7->u},3)}");
    // a sparse array joins with lists only
    check("Check(Join(h(u), SparseArray({2 -> v}, 3)), failed)", //
        "failed");
    check("Join({u}, SparseArray({2 -> v}, 3))", //
        "{u,0,v,0}");
    // sparse vectors join into a sparse vector
    check("Normal(Join(SparseArray({3 -> u}, 3), SparseArray({1 -> v}, 2)))", //
        "{0,0,u,v,0}");
    check("Head(Join(SparseArray({3 -> u}, 3), SparseArray({1 -> v}, 2)))", //
        "SparseArray");
  }

  @Test
  public void testSymbolicRightHandSide() {
    // the matrix is neither decomposed nor written out
    check("Head(LinearSolve(SparseArray({i_, i_} -> 3.0, {2500, 2500}), w))", //
        "LinearSolve");
  }
}
