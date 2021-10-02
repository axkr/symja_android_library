package org.matheclipse.io.system;

public class ReduceTest extends AbstractTestCase {

  public ReduceTest(String name) {
    super(name);
  }

  public void testReduce001() {
    check(
        "Reduce({x > 1 && x < 5, x >= 5 && x < 8})", //
        "False");
  }

  public void testReduce002() {
    check(
        "Reduce(x > 1 && x < 5 || x >= 5 && x < 8)", //
        "x>1&&x<8");
    check(
        "Reduce(x > 1 && x < 5 || x >= 9/2 && x < 8)", //
        "x>1&&x<8");
    check(
        "Reduce(x > 1 && x < 4 || x >= 9/2 && x < 8)", //
        "(x>1&&x<4)||(x>=9/2&&x<8)");
  }

  public void testReduce003() {
    check(
        "Reduce({x > 1, x > 2, x >= 5})", //
        "x>=5");
    check(
        "Reduce({x > 1, x > 2, x > 5})", //
        "x>5");
  }

  public void testReduce004() {
    check(
        "Reduce({x < 1, x < 2, x <= 5})", //
        "x<1");
    check(
        "Reduce({x <= 1, x < 2, x <= 5})", //
        "x<=1");
  }

  public void testReduceEquals() {
    check(
        "Reduce({x == 7, x <= 7})", //
        "x==7");
    check(
        "Reduce({x == 7, x < 7})", //
        "False");
    check(
        "Reduce({x == 13/2, x <= 7})", //
        "x==13/2");
    check(
        "Reduce({x == 13/2, x <= 5})", //
        "False");
    check(
        "Reduce({x == 13/2, x >= 7})", //
        "False");
    check(
        "Reduce({x == 13/2, x >= 5})", //
        "x==13/2");
    check(
        "Reduce(x == 1 || x == 42)", //
        "x==1||x==42");
    check(
        "Reduce({x > 1, x > 2, x == 5})", //
        "x==5");
  }

  public void testReduce005() {
    check(
        "Reduce(x<1 &&x < 2 || x < 7 && x>1/2)", //
        "x<7");

    check(
        "Reduce(x>1 &&x < 2 || x < 7 && x>1/2)", //
        "x>1/2&&x<7");
    check(
        "Reduce(x<1 &&x < 2 )", //
        "x<1");

    check(
        "Reduce(x > 1 && x < 4 || x >= 4)", //
        "x>1");
    check(
        "Reduce(x < 2 || x < 7 && x>3)", //
        "x<2||(x<7&&x>3)");
    check(
        "Reduce(x < 2 || x < 7 && x>1/2)", //
        "x<7");
  }
}
