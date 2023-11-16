package org.matheclipse.core.system;

import org.junit.Test;

/** Tests for FindInstance function */
public class FindInstanceTest extends ExprEvaluatorTestCase {

  @Test
  public void testDiophantine() {
    // TODO return condition with extra variable C1
    check("FindInstance(13*x+51*y==0, {x,y}, Integers, 6)", //
        "{{x->-153,y->39},{x->-102,y->26},{x->-51,y->13},{x->0,y->0},{x->51,y->-13},{x->\n"
            + "102,y->-26}}");
  }

  @Test
  public void testEmptySolution() {
    check("FindInstance({2*x+7*y==1,x>y,y>0},{x,y},Integers)", //
        "{}");
  }

  @Test
  public void testMod() {
    check(
        "FindInstance(Mod(x^2+y^2,2) == 1 && Mod(x-2*y,3) == 2, {x, y}, Integers,5)", //
        "{{x->0,y->-25},{x->0,y->-19},{x->0,y->-13},{x->0,y->-7},{x->0,y->-1}}");
  }

  @Test
  public void testFindInstanceBooleans() {
    // message - FindInstance: Illegal arguments: "1" in LogicFormula.
    check("FindInstance(1,{a,b,c,d},Booleans)", //
        "FindInstance(1,{a,b,c,d},Booleans)");
    check("FindInstance((a || b || c) && (! a || ! b || ! c) && True, {a, b, c}, Booleans,2)",
        "{{a->False,b->True,c->True},{a->False,b->True,c->False}}");
    check("FindInstance(Xor(a, b, c, d) && (a || b) && ! (c || d), {a, b, c, d}, Booleans)",
        "{{a->False,b->True,c->False,d->False}}");
  }

  @Test
  public void testFindInstance() {
    check("FindInstance(-1+4*Sin(x)==0,x)", //
        "{{x->ArcSin(1/4)}}");
    check("FindInstance(2*Sin(x)==1/2,x)", //
        "{{x->ArcSin(1/4)}}");


    check("FindInstance({x^2==4,x+y^2==6}, {x,y})", //
        "{{x->-2,y->-2*Sqrt(2)}}");
    check("FindInstance(x+5.0==a,x)", "{{x->-5.0+a}}");


    check("FindInstance(Sin((-3+x^2)/x) ==2,{x})", //
        "{{x->1/2*(ArcSin(2)-Sqrt(12+ArcSin(2)^2))}}");
    // check("FindInstance(Abs((-3+x^2)/x) ==2,{x})", "{{x->-3}}");
    check("FindInstance({x^2-11==y, x+y==-9}, {x,y})", //
        "{{x->-2,y->-7}}");

    check("FindInstance(3+2*Cos(x)==1/2,x)", //
        "{{x->ArcCos(-5/4)}}");
    check("FindInstance(Sin(x)==0,x)", //
        "{{x->0}}");
    check("FindInstance(Sin(x)==0.0,x)", //
        "{{x->0}}");
    check("FindInstance(Sin(x)==1/2,x)", //
        "{{x->Pi/6}}");
    checkNumeric("FindInstance(sin(x)==0.5,x)", //
        "{{x->0.5235987755982989}}");
    check("FindInstance(x^2-2500.00==0,x)", //
        "{{x->-50.0}}");
    check("FindInstance(x^2+a*x+1 == 0, x)", //
        "{{x->1/2*(-a-Sqrt(-4+a^2))}}");
    check("FindInstance((-3)*x^3 +10*x^2-11*x == (-4), {x})", //
        "{{x->1}}");

    checkNumeric("FindInstance(x^2+50*x-2500.00==0,x)", //
        "{{x->-80.90169943749474}}");

    check("FindInstance(a*x + y == 7 && b*x - y == 1, {x, y})", //
        "{{x->-8/(-a-b),y->(a-7*b)/(-a-b)}}");
    check("FindInstance({a*x + y == 7, b*x - y == 1}, {x, y})", //
        "{{x->-8/(-a-b),y->(a-7*b)/(-a-b)}}");
  }
}
