package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class ReduceTest extends ExprEvaluatorTestCase {

  @Test
  public void testReduce001() {
    check("Reduce({x > 1 && x < 5, x >= 5 && x < 8})", //
        "False");
  }

  @Test
  public void testReduceInequalityByExtrema() {
    // globally decided via Minimize/Maximize: min(x^2+1) == 1 > 0
    check("Reduce(x^2 + 1 > 0, x)", //
        "x∈Reals");
    check("Reduce(x^2 + 1 >= 0, x)", //
        "x∈Reals");
    check("Reduce(x^2 + 1 < 0, x)", //
        "False");
    check("Reduce(x^2 + 1 <= 0, x)", //
        "False");
    // max(-x^2 - 1) == -1 < 0
    check("Reduce(-x^2 - 1 < 0, x)", //
        "x∈Reals");
  }

  @Test
  public void testReduce002() {
    check("Reduce(x > 1 && x < 5 || x >= 5 && x < 8)", //
        "x>1&&x<8");
    check("Reduce(x > 1 && x < 5 || x >= 9/2 && x < 8)", //
        "x>1&&x<8");
    check("Reduce(x > 1 && x < 4 || x >= 9/2 && x < 8)", //
        "(x>1&&x<4)||(x>=9/2&&x<8)");
  }

  @Test
  public void testReduceXReals() {
    check("Reduce(x > 1 && x < 5 || x >= 5 && x < 8,x,Reals)", //
        "x>1&&x<8");
    check("Reduce(x > 1 && x < 5 || x >= 9/2 && x < 8,x,Reals)", //
        "x>1&&x<8");
    check("Reduce(x > 1 && x < 4 || x >= 9/2 && x < 8,x,Reals)", //
        "(x>1&&x<4)||(x>=9/2&&x<8)");
  }

  @Test
  public void testReduce003() {
    check("Reduce({x > 1, x > 2, x >= 5})", //
        "x>=5");
    check("Reduce({x > 1, x > 2, x > 5})", //
        "x>5");
  }

  @Test
  public void testReduce004() {
    check("Reduce({x < 1, x < 2, x <= 5})", //
        "x<1");
    check("Reduce({x <= 1, x < 2, x <= 5})", //
        "x<=1");
  }

  @Test
  public void testReduceEquals() {
    check("Reduce(x == 1 || x == 42)", //
        "x==1||x==42");
    check("Reduce({x == 7, x <= 7})", //
        "x==7");
    check("Reduce({x == 7, x < 7})", //
        "False");
    check("Reduce({x == 13/2, x <= 7})", //
        "x==13/2");
    check("Reduce({x == 13/2, x <= 5})", //
        "False");
    check("Reduce({x == 13/2, x >= 7})", //
        "False");
    check("Reduce({x == 13/2, x >= 5})", //
        "x==13/2");
    check("Reduce({x > 1, x > 2, x == 5})", //
        "x==5");
  }

  @Test
  public void testReduce005() {
    check("Reduce(x<1 &&x < 2 || x < 7 && x>1/2)", //
        "x<7");

    check("Reduce(x>1 &&x < 2 || x < 7 && x>1/2)", //
        "x>1/2&&x<7");
    check("Reduce(x<1 &&x < 2 )", //
        "x<1");

    check("Reduce(x > 1 && x < 4 || x >= 4)", //
        "x>1");
    check("Reduce(x < 2 || x < 7 && x>3)", //
        "x<2||(x>3&&x<7)");
    check("Reduce(x < 2 || x < 7 && x>1/2)", //
        "x<7");
  }

  @Test
  public void testReduce006() {
    check(
        "Reduce((x==1||x==-1||x==(-1)^(1/3)||x==-(-1)^(1/3)||x==(-1)^(2/3)||x==-(-1)^(2/3))&&x<0,x)", //
        "x==-1");
    check(
        "Reduce((x==1||x==-1||x==(-1)^(1/3)||x==-(-1)^(1/3)||x==(-1)^(2/3)||x==-(-1)^(2/3))&&x>0,x)", //
        "x==1");
    check("Reduce(x^6-1==0&&x>0,x)", //
        "x==1");
    // check("Reduce(x^6-1==0,x,Reals)", //
    // "x==-1||x==1");
    check("Reduce(x==1&&x>0,x)", //
        "x==1");

    check("Reduce(x>0&&x==(-1),x)", //
        "False");
    check("Reduce(x==(-1)&&x>0,x)", //
        "False");

    // complex values should return False
    check("Reduce(x==(-1)^(2/3)&&x>0,x)", //
        "False");
    // ommit variable
    check("Reduce(x^6-1==0&&x>0 )", //
        "x==1");
  }

  @Test
  public void testReduceQuadratic() {
    check("Reduce(a*x^2 + b*x + c == 0, x)", //
        "(a!=0&&(x==(-b-Sqrt(b^2-4*a*c))/(2*a)||x==(-b+Sqrt(b^2-4*a*c))/(2*a)))||(a==0&&b!=\n" //
            + "0&&x==-c/b)||(a==0&&b==0&&c==0)");
    // TODO wrong result
    check("Reduce(a*x^2 + b*x + c == 0&&x>0, x)", //
        "x>0");
  }

  @Test
  public void testReduceMultivariate() {
    // TODO Reduce: Reduce is only implemented to reduce one variable.
    check("Reduce(x^2 - y^3 == 1, {x, y})", //
        "Reduce(x^2-y^3==1,{x,y})");
  }

  @Test
  public void testReduceComplexes() {
    check("Reduce(x^3==EulerGamma,x)", //
        "x==-(-EulerGamma)^(1/3)||x==EulerGamma^(1/3)||x==(-1)^(2/3)*EulerGamma^(1/3)");
    check("Reduce(x^6-1==0,x)", //
        "x==-1||x==1||x==-(-1)^(1/3)||x==(-1)^(1/3)||x==-(-1)^(2/3)||x==(-1)^(2/3)");
  }

  @Test
  public void testReduceConstant() {
    check("Reduce(x^3==EulerGamma,x,Reals)", //
        "x==EulerGamma^(1/3)");
  }

  @Test
  public void testReduceAndOr() {
    check("{a = x > 1 && x < 5, b = x > 5 && x < 8}", //
        "{x>1&&x<5,x>5&&x<8}");
    check("Reduce(a&&b)", //
        "False");

    check("{a = x > 1 && x < 5, b = x >= 5 && x < 8}", //
        "{x>1&&x<5,x>=5&&x<8}");
    check("Reduce(a||b)", //
        "x>1&&x<8");



  }
}
