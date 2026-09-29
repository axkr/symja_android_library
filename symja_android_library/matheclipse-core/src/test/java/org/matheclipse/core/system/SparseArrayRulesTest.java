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
}
