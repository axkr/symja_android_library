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
    check("Reduce(x^6-1==0,x,Reals)", //
        "x==-1||x==1");
    check("Reduce(x^6-1==0,x,Complexes)", //
        "x==-1||x==1||x==-(-1)^(1/3)||x==(-1)^(1/3)||x==-(-1)^(2/3)||x==(-1)^(2/3)");
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
        "(a!=0&&(x==-b/(2*a)-Sqrt(b^2-4*a*c)/(2*a)||x==-b/(2*a)+Sqrt(b^2-4*a*c)/(2*a)))||(a==\n"
            + "0&&b!=0&&x==-c/b)||(a==0&&b==0&&c==0)");
    // parametric quadratic equation with a positivity constraint: left unevaluated
    // (previously returned the incorrect "x>0", which silently dropped the equation)
    check("Reduce(a*x^2 + b*x + c == 0&&x>0, x)", //
        "Reduce(c+b*x+a*x^2==0&&x>0,x)");
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
  public void testReduceIntegersEquation() {
    check("Reduce(x^2 == 4, x, Integers)", //
        "x==-2||x==2");
    // no integer root
    check("Reduce(x^2 == 3, x, Integers)", //
        "False");
    // equation combined with an inequality constraint
    check("Reduce(x^2 == 4 && x > 0, x, Integers)", //
        "x==2");
  }

  @Test
  public void testReduceIntegersInterval() {
    check("Reduce(x > 0 && x < 4, x, Integers)", //
        "x==1||x==2||x==3");
  }

  @Test
  public void testReducePrimes() {
    check("Reduce(x > 1 && x < 10, x, Primes)", //
        "x==2||x==3||x==5||x==7");
  }

  @Test
  public void testReduceLinearDiophantine() {
    check("Reduce(2*x + 3*y == 1, {x, y}, Integers)", //
        "C(1)∈Integers&&x==-1+3*C(1)&&y==1-2*C(1)");
  }

  @Test
  public void testReduceElementInput() {
    check("Reduce(x > 0 && x < 4 && Element(x, Integers))", //
        "x==1||x==2||x==3");
  }

  @Test
  public void testReduceBooleans() {
    check("Reduce(p || ! p, {p}, Booleans)", //
        "True");
    check("Reduce(p && ! p, {p}, Booleans)", //
        "False");
    check("Reduce(p && q, {p, q}, Booleans)", //
        "p&&q");
  }

  @Test
  public void testReduceForAll() {
    check("Reduce(ForAll(x, x^2 + 1 > 0))", //
        "True");
  }

  @Test
  public void testReduceExists() {
    check("Reduce(Exists(x, x^2 == 4))", //
        "True");
  }

  @Test
  public void testReduceMultivariateSystem() {
    check("Reduce({x + y == 1, x - y == 3}, {x, y})", //
        "x==2&&y==-1");
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

  @Test
  public void testPeriodicFunctions() {
    check("Reduce(Sin(a*x)+b==0, x)", //
        "(a==0&&b==0)||(C(1)∈Integers&&a!=0&&(x==(-ArcSin(b)+2*Pi*C(1))/a||x==(Pi+ArcSin(b)+\n"
            + "2*Pi*C(1))/a))");
    check("Reduce(Tan(a*x)+b==0, x)", //
        "(a==0&&b==0)||(C(1)∈Integers&&a!=0&&x==(-ArcTan(b)+Pi*C(1))/a)");
  }

  @Test
  public void testReduceIssue1413() {
    // Implement Reduce for inequality
    check("Reduce(3*x^2 - 3 < 0, x)", //
        "x>-1&&x<1");
    check("Reduce(3*x^4 - 3 < 0, x)", //
        "x>-1&&x<1");
    check("f := 2*(x - 1) < x + 3;", //
        "");
    check("Reduce(f,x)", //
        "x<5");
    check("Reduce(0 < x < 2&& 1 < x < 4, x)", //
        "x>1&&x<2");
    check("Reduce({0 < x < 2, 1 < x < 4}, x)", //
        "x>1&&x<2");
    // cubic inequality reduces over the reals even in the default domain
    check("Reduce(x^3-2*x+1<0,x)", //
        "x<-1/2-Sqrt(5)/2||(x>-1/2+Sqrt(5)/2&&x<1)");
  }

  @Test
  public void testReduceIssue1427() {
    // Implement Reduce for cubic polynomial inequality.
    // `(x>-1&&x<0)||x>1` is Symja's compact form of `-1 < x < 0 || x > 1`
    check("Reduce(4*x^3-4*x>0,x,Reals)", //
        "(x>-1&&x<0)||x>1");
    // inequalities are real-valued, so the same reduction happens in the default domain
    check("Reduce(4*x^3-4*x>0,x)", //
        "(x>-1&&x<0)||x>1");
  }

  @Test
  public void testReducePolynomialInequalityReals() {
    // non-strict relation includes the roots
    check("Reduce(4*x^3-4*x>=0,x,Reals)", //
        "(x>=-1&&x<=0)||x>=1");
    // opposite direction
    check("Reduce(4*x^3-4*x<0,x,Reals)", //
        "x<-1||(x>0&&x<1)");
    // even multiplicity root: sign doesn't change, strict `>` excludes the touching point x==1
    check("Reduce((x-1)^2*(x+2)>0,x,Reals)", //
        "(x>-2&&x<1)||x>1");
    // ... and the non-strict `>=` collapses to a single half-line
    check("Reduce((x-1)^2*(x+2)>=0,x,Reals)", //
        "x>=-2");
    // irrational (exact) roots
    check("Reduce(x^2-2>0,x,Reals)", //
        "x<-Sqrt(2)||x>Sqrt(2)");
    // higher degree (quintic) with five real roots
    check("Reduce(x^5-5*x^3+4*x>0,x,Reals)", //
        "(x>-2&&x<-1)||(x>0&&x<1)||x>2");
  }

  @Test
  public void testReduceContradiction() {
    // strict relations exclude the meeting point -> unsatisfiable
    check("Reduce(x<a&&x>a,x)", //
        "False");
    // non-strict relations include the root -> single point solution
    check("Reduce(x<=a&&x>=a,x)", //
        "x==a");
  }
}
