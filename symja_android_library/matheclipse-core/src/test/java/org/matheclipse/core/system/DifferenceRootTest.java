package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class DifferenceRootTest extends ExprEvaluatorTestCase {
  @Test
  public void testDifferenceRoot01() {
    // Fibonacci
    check(
        "DifferenceRoot(Function({y, n}, {y(n) == y(n - 1) + y(n - 2), y(1) == 1, y(2) == 1}))", //
        "DifferenceRoot[Function({y,n},{y(n)==y(-1+n)+y(-2+n),y(1)==1,y(2)==1})]");
    check(
        "DifferenceRoot(Function({y, n}, {y(n) == y(n - 1) + y(n - 2), y(1) == 1, y(2) == 1}))[10]", //
        "55");
  }

  @Test
  public void testDifferenceRoot02() {
    // Factorial y(n) = n * y(n-1)
    check("DifferenceRoot(Function({y, n}, {y(n) == n * y(n - 1), y(1) == 1}))[5]", //
        "120");
  }

  @Test
  public void testDifferenceRoot03() {
    // with parameters
    check(
        "DifferenceRoot(Function({y, n}, {y(n + 2) == x*y(n + 1) - (n + 1)*y(n), y(0) == 1,y(1) == -3}))[4]", //
        "3+15*x-x^2-3*x^3");
  }

  @Test
  public void testDifferenceRoot04() {
    // numerical
    check(
        "DifferenceRoot(Function({y, n}, {3.5*y(n + 1) + 1 == (n + 1)*y(n), y(1) == 1}))[{3, 5, 10}]", //
        "{-0.0408163,-0.760516,-61.38267}");
  }

  @Test
  public void testDifferenceRoot05() {
    // complex numerical
    check(
        "DifferenceRoot(Function({y, n},{y(n+2)==y(n + 1) - (n + 1)*y(n),y(0) == 1 + 2*I, y(1) == -3 + 2 I}))[4]", //
        "14-I*4");
  }

  @Test
  public void testDifferenceRoot06() {
    check(
        "DifferenceRoot(Function({y, n}, {y(n + 2) == y(n + 1) - y(n), y(0) == 1,  y(1) == 1/3}))[-5]", //
        "1/3");
  }

  @Test
  public void testDifferenceRoot07() {
    check(
        "DifferenceRoot(Function({y, n}, {y(n + 1)/(n + 1)-y(n)==0, y(0) == 0,   y(1) == 1}))[{1, 2, 3, 4, 5}]", //
        "{1,2,6,24,120}");
  }

  @Test
  public void testDifferenceRoot08() {
    check(
        "DifferenceRoot(Function({y, n}, {y(n+1)/(n + 1)-y(n)==0, y(-1) == 1,y(1) == 1}))[{{3, -1}, {-2, 6}}]", //
        "{{6,1},{-1,720}}");
  }


}
