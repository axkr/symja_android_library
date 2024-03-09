package org.matheclipse.core.system;

import org.junit.Test;

public class FunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testBezierFunction01() {
    check("pts = {{0, 0}, {1, 1}, {2, 0}, {3, 2}};", //
        "");
    check("bz= BezierFunction(pts)", //
        "BezierFunction({{0,0},{1,1},{2,0},{3,2}})");
    check("bz(0.5)", //
        "{1.5,0.625}");
  }

  @Test
  public void testBezierFunction02() {
    check("pts = {{0, 1, 2}, {1, 0, 3}, {2, 1, 0}};", //
        "");
    check("bz= BezierFunction(pts)", //
        "BezierFunction({{0,1,2},{1,0,3},{2,1,0}})");
    check("bz(0.5)", //
        "{1.0,0.5,2.0}");
  }

  @Test
  public void testBezierFunction03() {
    check(
        "f=BezierFunction({{0, 0}, {0, 0.8}, {1, 1}});FindRoot(0.5==Indexed(f(x), 2), {x, 0.2, 0.5}, Method->\"Bisection\")", //
        "{x->0.361508}");
  }


}
