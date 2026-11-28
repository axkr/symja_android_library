package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for ArrayFilter, ArrayResample, ArrayComponents, MaxDetect, MinDetect and SubsetCount. */
public class ArrayFilterFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testArrayFilter() {
    check("ArrayFilter(Total, {3, 1, 4, 1, 5}, 1)", //
        "{7,8,6,10,11}");
    check("ArrayFilter(Max, {{1, 7, 2}, {5, 3, 9}}, 1)", //
        "{{7,9,9},{7,9,9}}");
    check("ArrayFilter(g, {p, q, r}, 1)", //
        "{g({p,p,q}),g({p,q,r}),g({q,r,r})}");
    check("ArrayFilter(Mean, {2, 4, 6, 8}, 0)", //
        "{2,4,6,8}");
    check("ArrayFilter(Total, {{1, 2, 3}, {4, 5, 6}}, {0, 1})", //
        "{{{1,1,2},{1,2,3},{2,3,3}},{{4,4,5},{4,5,6},{5,6,6}}}");
  }

  @Test
  public void testArrayResample() {
    check("ArrayResample({1, 5}, 5)", //
        "{1,2,3,4,5}");
    check("ArrayResample({0, 3, 9}, 4)", //
        "{0,2,5,9}");
    check("ArrayResample({{1, 2}, {3, 4}}, {3, 3})", //
        "{{1,3/2,2},{2,5/2,3},{3,7/2,4}}");
    check("ArrayResample({a, b, c}, 2)", //
        "{a,c}");
    check("ArrayResample({1, 2, 3}, 0)", //
        "ArrayResample({1,2,3},0)");
    check("ArrayResample({2.0, 6.0}, 3)", //
        "{2.0,4.0,6.0}");
  }

  @Test
  public void testArrayComponents() {
    check("ArrayComponents({x, y, x, 0, z, y})", //
        "{1,2,1,0,3,2}");
    check("ArrayComponents({{3, 3, 8}, {8, 0, 1}})", //
        "{{1,1,2},{2,0,3}}");
    check("ArrayComponents({{a, b}, {a, b}, {c}}, 1)", //
        "{1,1,2}");
    check("ArrayComponents({p, q, r, p}, 1, {q -> 1})", //
        "{2,1,3,2}");
    check("ArrayComponents({p, q, r}, 1, {p -> 5, r -> 2})", //
        "{5,1,2}");
  }

  @Test
  public void testMaxDetect() {
    check("MaxDetect({1, 3, 2, 2, 5, 5, 4})", //
        "{0,1,0,0,1,1,0}");
    check("MaxDetect({{1, 2, 1}, {2, 9, 2}, {1, 2, 8}})", //
        "{{0,0,0},{0,1,0},{0,0,0}}");
    check("MaxDetect({7, 7, 7})", //
        "{1,1,1}");
    check("MaxDetect({1/2, Pi, 2.5})", //
        "{0,1,0}");
    check("MaxDetect({a, 1})", //
        "MaxDetect({a,1})");
  }

  @Test
  public void testMinDetect() {
    check("MinDetect({4, 1, 1, 6, 2, 7})", //
        "{0,1,1,0,1,0}");
    check("MinDetect({{5, 5, 5}, {5, 2, 5}, {5, 5, 1}})", //
        "{{0,0,0},{0,0,0},{0,0,1}}");
  }

  @Test
  public void testSubsetCount() {
    // two subsets without a common position
    check("SubsetCount({a, b, a, b, a}, {a, b})", //
        "2");
    check("SubsetCount({3, 1, 2, 1, 3, 2}, {1, 2})", //
        "2");
    check("SubsetCount({1, 2, 3, 4, 5, 6}, {x_, y_} /; x + y == 7)", //
        "3");
  }
}
