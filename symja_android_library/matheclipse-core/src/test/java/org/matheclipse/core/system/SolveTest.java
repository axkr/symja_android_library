package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests forSolve and Roots functions */
public class SolveTest extends ExprEvaluatorTestCase {

  public SolveTest(String name) {
    super(name);
  }

  public void testSinX() {
    check("Solve(Sin(x)==b,x)", //
        "{{x->ArcSin(b)}}");
  }

  public void testEliminate() {
    check("Eliminate({(a*x + b)/(c*x + d)==y},x)", //
        "True");
    // check(
    // "Eliminate(E^(-x)==0,x)", //
    // "False");
    checkNumeric("Eliminate(Abs(x-1)==(-1),x)", //
        "True");
  }

  public void testRootsX6001() {
    check("Roots(x^6-1==0, x)", //
        "x==-1||x==1||x==(-1)^(1/3)||x==-(-1)^(1/3)||x==(-1)^(2/3)||x==-(-1)^(2/3)");
  }

  public void testRootsX16001() {
    check("Roots(x^16-1==0,x)", //
        "x==-1||x==-I||x==1||x==(-1)^(1/8)||x==-(-1)^(1/8)||x==(-1)^(1/4)||x==-(-1)^(1/4)||x==(-\n"
            + "1)^(3/8)||x==-(-1)^(3/8)||x==I||x==(-1)^(5/8)||x==-(-1)^(5/8)||x==(-1)^(3/4)||x==-(-\n"
            + "1)^(3/4)||x==(-1)^(7/8)||x==-(-1)^(7/8)");
  }

  public void testSolveX16001() {
    check("Solve(x^16-1==0,x)", //
        "{{x->-1},{x->-I},{x->I},{x->1},{x->-(-1)^(1/8)},{x->(-1)^(1/8)},{x->-(-1)^(1/4)},{x->(-\n"
            + "1)^(1/4)},{x->-(-1)^(3/8)},{x->(-1)^(3/8)},{x->-(-1)^(5/8)},{x->(-1)^(5/8)},{x->-(-\n"
            + "1)^(3/4)},{x->(-1)^(3/4)},{x->-(-1)^(7/8)},{x->(-1)^(7/8)}}");
  }

  public void testSolveX16002() {
    check("Solve(x^16+1==0,x)", //
        "{{x->-(-1)^(1/16)},{x->(-1)^(1/16)},{x->-(-1)^(3/16)},{x->(-1)^(3/16)},{x->-(-1)^(\n"
            + "5/16)},{x->(-1)^(5/16)},{x->-(-1)^(7/16)},{x->(-1)^(7/16)},{x->-(-1)^(9/16)},{x->(-\n"
            + "1)^(9/16)},{x->-(-1)^(11/16)},{x->(-1)^(11/16)},{x->-(-1)^(13/16)},{x->(-1)^(13/\n"
            + "16)},{x->-(-1)^(15/16)},{x->(-1)^(15/16)}}");
  }

  public void testSolveAX3B001() {
    check("Solve(a*x^3+b==0,x)",
        "{{x->-b^(1/3)/a^(1/3)},{x->((-1)^(1/3)*b^(1/3))/a^(1/3)},{x->(-(-1)^(2/3)*b^(1/3))/a^(\n"
            + "1/3)}}");
  }

  public void testSolveX4B001() {
    // check("Trace(Solve(x^6-b==0,x))",
    // "???");
    check("Solve(x^4+b==0,x)", //
        "{{x->(-1)^(1/4)*b^(1/4)},{x->-(-1)^(1/4)*b^(1/4)},{x->(-1)^(3/4)*b^(1/4)},{x->-(-\n"
            + "1)^(3/4)*b^(1/4)}}");
  }

  public void testSolveX4B002() {
    check("Solve(x^4-b==0,x)", //
        "{{x->-b^(1/4)},{x->-I*b^(1/4)},{x->I*b^(1/4)},{x->b^(1/4)}}");
  }

  public void testSolveAX5B001() {
    check("Solve(a*x^5+b==0,x)",
        "{{x->-b^(1/5)/a^(1/5)},{x->((-1)^(1/5)*b^(1/5))/a^(1/5)},{x->(-(-1)^(2/5)*b^(1/5))/a^(\n"
            + "1/5)},{x->((-1)^(3/5)*b^(1/5))/a^(1/5)},{x->(-(-1)^(4/5)*b^(1/5))/a^(1/5)}}");
  }

  public void testSolveAX5B002() {
    check("Solve(a*x^5-b==0,x)",
        "{{x->b^(1/5)/a^(1/5)},{x->(-(-1)^(1/5)*b^(1/5))/a^(1/5)},{x->((-1)^(2/5)*b^(1/5))/a^(\n"
            + "1/5)},{x->(-(-1)^(3/5)*b^(1/5))/a^(1/5)},{x->((-1)^(4/5)*b^(1/5))/a^(1/5)}}");
  }

  public void testSolve7X519002() {
    check("Solve(7*x^5-19==0,x) ",
        "{{x->(19/7)^(1/5)},{x->(-1)^(2/5)*(19/7)^(1/5)},{x->-(-1)^(3/5)*(19/7)^(1/5)},{x->(-\n"
            + "1)^(4/5)*(19/7)^(1/5)},{x->-(-19)^(1/5)/7^(1/5)}}");
    check("Solve(7*x^5-19==0,x) // N",
        "{{x->1.22104},{x->0.377323+I*1.16128},{x->0.377323+I*(-1.16128)},{x->-0.987845+I*0.717711},{x->-0.987845+I*(-0.717711)}}");
  }

  public void testSolveX6B001() {
    check("Solve(x^6+b==0,x)", //
        "{{x->-I*b^(1/6)},{x->I*b^(1/6)},{x->(-1)^(1/6)*b^(1/6)},{x->-(-1)^(1/6)*b^(1/6)},{x->(-\n"
            + "1)^(5/6)*b^(1/6)},{x->-(-1)^(5/6)*b^(1/6)}}");
  }

  public void testSolveX6B002() {
    check("Solve(x^6-b==0,x)",
        "{{x->-b^(1/6)},{x->b^(1/6)},{x->(-1)^(1/3)*b^(1/6)},{x->-(-1)^(1/3)*b^(1/6)},{x->(-\n"
            + "1)^(2/3)*b^(1/6)},{x->-(-1)^(2/3)*b^(1/6)}}");
  }

  public void testSolveX8B001() {
    check("Solve(x^8+b==0,x)", //
        "{{x->(-1)^(1/8)*b^(1/8)},{x->-(-1)^(1/8)*b^(1/8)},{x->(-1)^(3/8)*b^(1/8)},{x->-(-\n"
            + "1)^(3/8)*b^(1/8)},{x->(-1)^(5/8)*b^(1/8)},{x->-(-1)^(5/8)*b^(1/8)},{x->(-1)^(7/8)*b^(\n"
            + "1/8)},{x->-(-1)^(7/8)*b^(1/8)}}");
  }

  public void testSolveX8B002() {
    check("Solve(x^8-b==0,x)", //
        "{{x->-b^(1/8)},{x->-I*b^(1/8)},{x->I*b^(1/8)},{x->b^(1/8)},{x->(-1)^(1/4)*b^(1/8)},{x->-(-\n"
            + "1)^(1/4)*b^(1/8)},{x->(-1)^(3/4)*b^(1/8)},{x->-(-1)^(3/4)*b^(1/8)}}");
  }

  public void testSolveX10001() {
    check("Solve(x^10-1==0,x)", //
        "{{x->-1},{x->1},{x->-(-1)^(1/5)},{x->(-1)^(1/5)},{x->-(-1)^(2/5)},{x->(-1)^(2/5)},{x->-(-\n"
            + "1)^(3/5)},{x->(-1)^(3/5)},{x->-(-1)^(4/5)},{x->(-1)^(4/5)}}");
  }

  public void testSolveX10002() {
    check("Solve(x^10-15==0,x)", //
        "{{x->-15^(1/10)},{x->15^(1/10)},{x->(-1)^(1/5)*15^(1/10)},{x->-(-1)^(1/5)*15^(1/\n"
            + "10)},{x->(-1)^(2/5)*15^(1/10)},{x->-(-1)^(2/5)*15^(1/10)},{x->(-1)^(3/5)*15^(1/\n"
            + "10)},{x->-(-1)^(3/5)*15^(1/10)},{x->(-1)^(4/5)*15^(1/10)},{x->-(-1)^(4/5)*15^(1/\n"
            + "10)}}");
  }

  public void testSolveX10B001() {
    check("Solve(x^10+b==0,x)",
        "{{x->-I*b^(1/10)},{x->I*b^(1/10)},{x->(-1)^(1/10)*b^(1/10)},{x->-(-1)^(1/10)*b^(\n"
            + "1/10)},{x->(-1)^(3/10)*b^(1/10)},{x->-(-1)^(3/10)*b^(1/10)},{x->(-1)^(7/10)*b^(1/\n"
            + "10)},{x->-(-1)^(7/10)*b^(1/10)},{x->(-1)^(9/10)*b^(1/10)},{x->-(-1)^(9/10)*b^(1/\n"
            + "10)}}");
  }

  public void testSolveX10B002() {
    check("Solve(x^10-b==0,x)", //
        "{{x->-b^(1/10)},{x->b^(1/10)},{x->(-1)^(1/5)*b^(1/10)},{x->-(-1)^(1/5)*b^(1/10)},{x->(-\n"
            + "1)^(2/5)*b^(1/10)},{x->-(-1)^(2/5)*b^(1/10)},{x->(-1)^(3/5)*b^(1/10)},{x->-(-1)^(\n"
            + "3/5)*b^(1/10)},{x->(-1)^(4/5)*b^(1/10)},{x->-(-1)^(4/5)*b^(1/10)}}");
  }

  public void testSolveAX10B001() {
    check("Solve(a*x^10+b==0,x)",
        "{{x->(-I*b^(1/10))/a^(1/10)},{x->(I*b^(1/10))/a^(1/10)},{x->((-1)^(1/10)*b^(1/10))/a^(\n"
            + "1/10)},{x->(-(-1)^(1/10)*b^(1/10))/a^(1/10)},{x->((-1)^(3/10)*b^(1/10))/a^(1/10)},{x->(-(-\n"
            + "1)^(3/10)*b^(1/10))/a^(1/10)},{x->((-1)^(7/10)*b^(1/10))/a^(1/10)},{x->(-(-1)^(7/\n"
            + "10)*b^(1/10))/a^(1/10)},{x->((-1)^(9/10)*b^(1/10))/a^(1/10)},{x->(-(-1)^(9/10)*b^(\n"
            + "1/10))/a^(1/10)}}");
  }

  public void testSolveAX8B001() {
    check("Solve(a*x^8+b==0,x)", //
        "{{x->((-1)^(1/8)*b^(1/8))/a^(1/8)},{x->(-(-1)^(1/8)*b^(1/8))/a^(1/8)},{x->((-1)^(\n"
            + "3/8)*b^(1/8))/a^(1/8)},{x->(-(-1)^(3/8)*b^(1/8))/a^(1/8)},{x->((-1)^(5/8)*b^(1/8))/a^(\n"
            + "1/8)},{x->(-(-1)^(5/8)*b^(1/8))/a^(1/8)},{x->((-1)^(7/8)*b^(1/8))/a^(1/8)},{x->(-(-\n"
            + "1)^(7/8)*b^(1/8))/a^(1/8)}}");
  }

  public void testSolveAX11B001() {
    check("Solve(a*x^11-b==0,x)",
        "{{x->b^(1/11)/a^(1/11)},{x->(-(-1)^(1/11)*b^(1/11))/a^(1/11)},{x->((-1)^(2/11)*b^(\n"
            + "1/11))/a^(1/11)},{x->(-(-1)^(3/11)*b^(1/11))/a^(1/11)},{x->((-1)^(4/11)*b^(1/11))/a^(\n"
            + "1/11)},{x->(-(-1)^(5/11)*b^(1/11))/a^(1/11)},{x->((-1)^(6/11)*b^(1/11))/a^(1/11)},{x->(-(-\n"
            + "1)^(7/11)*b^(1/11))/a^(1/11)},{x->((-1)^(8/11)*b^(1/11))/a^(1/11)},{x->(-(-1)^(9/\n"
            + "11)*b^(1/11))/a^(1/11)},{x->((-1)^(10/11)*b^(1/11))/a^(1/11)}}");
  }

  public void testSolveX24B001() {
    check("Solve(x^24==1,x)", //
        "{{x->-1},{x->-I},{x->I},{x->1},{x->-(-1)^(1/12)},{x->(-1)^(1/12)},{x->-(-1)^(1/6)},{x->(-\n"
            + "1)^(1/6)},{x->-(-1)^(1/4)},{x->(-1)^(1/4)},{x->-(-1)^(1/3)},{x->(-1)^(1/3)},{x->-(-\n"
            + "1)^(5/12)},{x->(-1)^(5/12)},{x->-(-1)^(7/12)},{x->(-1)^(7/12)},{x->-(-1)^(2/3)},{x->(-\n"
            + "1)^(2/3)},{x->-(-1)^(3/4)},{x->(-1)^(3/4)},{x->-(-1)^(5/6)},{x->(-1)^(5/6)},{x->-(-\n"
            + "1)^(11/12)},{x->(-1)^(11/12)}}");
  }

  public void testSolveX4_15001() {
    // github #204
    check("Solve(x^4 - 15 == 0, x)", //
        "{{x->-15^(1/4)},{x->-I*15^(1/4)},{x->I*15^(1/4)},{x->15^(1/4)}}");
  }

  public void testSolveX4_15002() {
    check("Solve(x^4 + 15 == 0, x) ", //
        "{{x->-(-15)^(1/4)},{x->-I*(-15)^(1/4)},{x->I*(-15)^(1/4)},{x->(-15)^(1/4)}}");
  }

  public void testSolveX3_15001() {
    check("Solve(x^3 + 15 == 0, x)", //
        "{{x->(-15)^(1/3)},{x->-15^(1/3)},{x->-(-1)^(2/3)*15^(1/3)}}");
  }

  public void testSolveX3_15002() {
    check("Solve(x^3 - 15 == 0, x)", //
        "{{x->-(-15)^(1/3)},{x->15^(1/3)},{x->(-1)^(2/3)*15^(1/3)}}");
  }

  public void testSolveX7_15001() {
    check("Solve(x^7 + 15 == 0, x)", //
        "{{x->(-15)^(1/7)},{x->-15^(1/7)},{x->-(-1)^(2/7)*15^(1/7)},{x->(-1)^(3/7)*15^(1/\n"
            + "7)},{x->-(-1)^(4/7)*15^(1/7)},{x->(-1)^(5/7)*15^(1/7)},{x->-(-1)^(6/7)*15^(1/7)}}");
  }

  public void testSolveX7_15002() {
    check("Solve(x^7 - 15 == 0, x)", //
        "{{x->-(-15)^(1/7)},{x->15^(1/7)},{x->(-1)^(2/7)*15^(1/7)},{x->-(-1)^(3/7)*15^(1/\n"
            + "7)},{x->(-1)^(4/7)*15^(1/7)},{x->-(-1)^(5/7)*15^(1/7)},{x->(-1)^(6/7)*15^(1/7)}}");
  }

  public void testSolve001() {

    check("Solve((5*x^4-2)/(x+1)/(x^2-1)==0,x)", //
        "{{x->-(2/5)^(1/4)},{x->-I*(2/5)^(1/4)},{x->I*(2/5)^(1/4)},{x->(2/5)^(1/4)}}");
  }

  public void testSolve002() {
    check("Solve((x^2 + 2)*(x^2 - 2) == 0, x, Reals)", //
        "{{x->-Sqrt(2)},{x->Sqrt(2)}}");
  }

  public void testSolve003() {
    check("Solve( 2/x==3/4 ,x)", //
        "{{x->8/3}}");
  }

  public void testSolveX3_89001() {
    check("Solve(x^3-89==0, x)", //
        "{{x->-(-89)^(1/3)},{x->89^(1/3)},{x->(-1)^(2/3)*89^(1/3)}}");
  }

  public void testSolve7X4_2ab001() {
    check("Solve(7*x^4+2*a*b==0, x)", //
        "{{x->((-2)^(1/4)*a^(1/4)*b^(1/4))/7^(1/4)},{x->(-(-2)^(1/4)*a^(1/4)*b^(1/4))/7^(\n"
            + "1/4)},{x->(-I*(-2)^(1/4)*a^(1/4)*b^(1/4))/7^(1/4)},{x->(I*(-2)^(1/4)*a^(1/4)*b^(\n"
            + "1/4))/7^(1/4)}}");
    check("Solve(7*x^4+2*a*b==0, x)// N", //
        "{{x->(0.516973+I*0.516973)*a^0.25*b^0.25},{x->(-0.516973+I*(-0.516973))*a^0.25*b^0.25},{x->(0.516973+I*(-0.516973))*a^0.25*b^0.25},"
            + "{x->(-0.516973+I*0.516973)*a^0.25*b^0.25}}");
  }

  public void testSolve7X4_2ab002() {
    check("Solve(7*x^4-2*a*b==0, x)", //
        "{{x->(2/7)^(1/4)*a^(1/4)*b^(1/4)},{x->-(2/7)^(1/4)*a^(1/4)*b^(1/4)},{x->-I*(2/7)^(\n"
            + "1/4)*a^(1/4)*b^(1/4)},{x->I*(2/7)^(1/4)*a^(1/4)*b^(1/4)}}");
    check("Solve(7*x^4-2*a*b==0, x)// N", //
        "{{x->0.73111*a^0.25*b^0.25},{x->-0.73111*a^0.25*b^0.25},{x->(I*(-0.73111))*a^0.25*b^0.25},{x->(I*0.73111)*a^0.25*b^0.25}}");
  }

  public void testSolve7X4_2ab003() {
    check("Solve(-7*x^4-2*a*b==0, x)", //
        "{{x->(-2/7)^(1/4)*a^(1/4)*b^(1/4)},{x->-(-2/7)^(1/4)*a^(1/4)*b^(1/4)},{x->(-1)^(\n"
            + "3/4)*(2/7)^(1/4)*a^(1/4)*b^(1/4)},{x->-(-1)^(3/4)*(2/7)^(1/4)*a^(1/4)*b^(1/4)}}");
    check("Solve(-7*x^4-2*a*b==0, x)// N", //
        "{{x->(0.516973+I*0.516973)*a^0.25*b^0.25},{x->(-0.516973+I*(-0.516973))*a^0.25*b^0.25},{x->(-0.516973+I*0.516973)*a^0.25*b^0.25},{x->(0.516973+I*(-0.516973))*a^0.25*b^0.25}}");
  }

  public void testSolve7cX4_2ab004() {
    // TODO
    // check(
    // "Solve(-7*c*x^4-2*a*b==0, x)", //
    //
    // "{{x->((2/7)^(1/4)*a^(1/4)*b^(1/4))/(-c)^(1/4)},{x->(-(2/7)^(1/4)*a^(1/4)*b^(1/4))/(-c)^(\r\n"
    // +
    // "1/4)},{x->(-I*(2/7)^(1/4)*a^(1/4)*b^(1/4))/(-c)^(1/4)},{x->(I*(2/7)^(1/4)*a^(1/4)*b^(\r\n"
    // + "1/4))/(-c)^(1/4)}}");
    // check(
    // "Solve(-7*c*x^4-2*a*b==0, x)// N", //
    //
    // "{{x->(0.516973+I*0.516973)*a^0.25*b^0.25},{x->(-0.516973+I*(-0.516973))*a^0.25*b^0.25},{x->(-0.516973+I*0.516973)*a^0.25*b^0.25},{x->(0.516973+I*(-0.516973))*a^0.25*b^0.25}}");
  }

  public void testSolveGalleryExample() {
    check("Solve({x^2-11==y, x+y==-9}, {x,y})", //
        "{{x->-2,y->-7},{x->1,y->-10}}");
  }

  public void testInverseFunctionProductLog() {
    check("InverseFunction(#*a^#*d &)", //
        "ProductLog((#1*Log(a))/d)/Log(a)&");
    check("InverseFunction(#^2+2^# &)", //
        "InverseFunction(2^#1+#1^2&)");
  }

  public void testSolveSlot() {
    check("Solve(f(x)^2+1==0,f(x))", //
        "{{f(x)->-I},{f(x)->I}}");
    check("Solve(#^2+1==0,#)", //
        "{{#1->-I},{#1->I}}");
    check("Solve(x^2+1==0,x)", //
        "{{x->-I},{x->I}}");

    check("Solve(#*2+1==0,#)", //
        "{{#1->-1/2}}");

    check("Solve(#*a^#*d==0,#)", //
        "{{#1->0}}");
  }

  public void testSolveProductLog() {
    check("Solve(x^2==2^x,x)", //
        "{{x->2},{x->(-2*ProductLog(Log(2)/2))/Log(2)}}");
  }

  public void testSolveIssue329() {
    check("Solve(a1+a2+5*x+4*Sqrt(a+b*x+25/16*x^2)+z1+z2==0, x)", //
        "{{x->(-16*a+a1^2+2*a1*a2+a2^2+2*a1*z1+2*a2*z1+z1^2+2*a1*z2+2*a2*z2+2*z1*z2+z2^2)/(\n"
            + "16*b-10*(a1+a2+z1+z2))}}");
    check("Solve(5*x+4*Sqrt(a+b*x+25/16*x^2)+z==0, x)", //
        "{{x->(-16*a+z^2)/(16*b-10*z)}}");
    check("Solve(4*x+2*Sqrt(a+b*x+4*x^2)+z==0, x)", //
        "{{x->(-4*a+z^2)/(4*b-8*z)}}");

    check("Solve(-42*Sqrt(a+b*x)+z==0, x)", //
        "{{x->(-1764*a+z^2)/(1764*b)}}");
    check("Solve(y*x-Sqrt(a+b*x)+z==0, x)", //
        "{{x->(b-2*y*z+Sqrt(b^2+4*a*y^2-4*b*y*z))/(2*y^2)},{x->(b-2*y*z-Sqrt(b^2+4*a*y^2-\n"
            + "4*b*y*z))/(2*y^2)}}");

    check("Solve(4*x+2*Sqrt(a+b*x+4*x^2)+z==0, x)", //
        "{{x->(-4*a+z^2)/(4*b-8*z)}}");
    check("Solve(y*x+Sqrt(a+b*x+y^2*x^2)+z==0, x)", //
        "{{x->(-a+z^2)/(b-2*y*z)}}");
    check("Solve(x+Sqrt(a+b*x+x^2)==y, x)", //
        "{{x->(-a+y^2)/(b+2*y)}}");
    check("Solve(x+Sqrt(a+b*x+c*x^2)==y, x)", //
        "{{x->(-b-2*y-Sqrt(4*a+b^2-4*a*c+4*b*y+4*c*y^2))/(2*(-1+c))},{x->(-b-2*y+Sqrt(4*a+b^\n"
            + "2-4*a*c+4*b*y+4*c*y^2))/(2*(-1+c))}}");
    check("Solve(x+Sqrt(x+a) == y, x)", //
        "{{x->1/2*(1+2*y-Sqrt(1+4*a+4*y))},{x->1/2*(1+2*y+Sqrt(1+4*a+4*y))}}");
    check("Solve(-2+Sqrt(-2*x+x^2+3) == 0, x)", //
        "{{x->1/2*(2-2*Sqrt(2))},{x->1/2*(2+2*Sqrt(2))}}");
  }

  public void testSolveInequality() {
    // TODO github #210
    // check(
    // "Solve({x==y,y>2},x)", //
    // "{{x->ConditionalExpression(y,y>2)}} ");
  }

  public void testSolve() {
    // github #261 - JUnit test for Apfloat switching to complex Power calculation
    check("Solve(0.00004244131815783 == x^5 , x)", //
        "{{x->-0.10802279680851234+I*0.07848315587546605},{x->-0.10802279680851212+I*(-0.07848315587546605)},"//
            + "{x->0.04126103682102799+I*(-0.1269884137508598)}," //
        + "{x->0.04126103682102799+I*0.1269884137508598},{x->0.1335235199749684}}");

    // github #247
    check("Solve((k+3)/(4)==(k)/2,{k})", //
        "{{k->3}}");
    check("Solve(k+3/(4)==(k)/2,{k})", //
        "{{k->-3/2}}");

    check("Solve({{x,y}=={3,4}},{x,y})", //
        "{{x->3,y->4}}");

    // https://en.wikipedia.org/wiki/Lambert_W_function#Solving_equations
    // TODO generate 2 solutions
    check("Solve(3^x==2*x+2, x)", //
        "{{x->-(Log(3)+ProductLog(-Log(3)/6))/Log(3)}}");
    check("Solve(3^x==2*x, x)", //
        "{{x->-ProductLog(-Log(3)/2)/Log(3)}}");
    check("Solve(3^x==-4*x, x)", //
        "{{x->-ProductLog(Log(3)/4)/Log(3)}}");
    check("Solve(x^y==a*y, y)", //
        "{{y->-ProductLog(-Log(x)/a)/Log(x)}}");
    check("Solve(x^y==y, y)", //
        "{{y->-ProductLog(-Log(x))/Log(x)}}");
    check("Solve(x^y==-y, y)", //
        "{{y->-ProductLog(Log(x))/Log(x)}}");

    check("Solve(2*Log(2)* x^2 - Log(4)*x^2 + x - 1 == 0, x)", //
        "{{x->1}}");
    // message - Solve: Maximum AST dimension 9223372036854775807 exceeded
    check("Solve(x^(1/7)-x^(1/5)==x^(1/3)-x^(1/2),x)", //
        "{{x->1}}");
    check("Solve(Log(2,x)+4*Log(x,2)-5==0,x)", //
        "{{x->2},{x->16}}");
    // TODO
    // check(
    // "Solve(GammaRegularized(a, b) == x, b)", //
    // "{{b->InverseGammaRegularized(a,x)}}");

    check("Solve({x+y+z==6, x+y-z==0, y+z==5}, {x,y,z})", //
        "{{x->1,y->2,z->3}}");
    // github #201 begin
    check(
        "Solve({m1*u1^2 + m2*u2^2 == m1*v1^2 + m2*v2^2, m1*u1 + m2*u2 == m1*v1 + m2*v2}, {v1, v2})", //
        "{{v2->u2,v1->u1}," //
            + "{v2->(2*m1*u1-m1*u2+m2*u2)/(m1+m2)," //
            + "v1->(m1*u1-m2*u1+2*m2*u2)/(m1+m2)}}");
    check("Solve({m1*u1 + m2*u2 == m1*v1 + m2*v2, u2 - u1 == v2 - v1}, {v1, v2})", //
        "{{v1->(-m1*u1-m2*u2+m2*(-u1+u2))/(-m1-m2),v2->(-m1*u1+m1*(u1-u2)-m2*u2)/(-m1-m2)}}");
    check("Solve({m1*u1 + m2*u2 == m1*v1 + m2*v2, u2 - u1 == -(v2 - v1)}, {v1, v2})", //
        "{{v1->(m1*u1+m2*u2+m2*(-u1+u2))/(m1+m2),v2->(m1*u1+m1*(u1-u2)+m2*u2)/(m1+m2)}}");
    // github #201 end

    // github #200 begin
    check("Solve({x^2+y^2==5, x+y^2==-7, y>0}, {x,y})", //
        "{}");
    check("Solve({x^2+y^2==5, x+y^2==-7, x>0}, {x,y})", //
        "{{x->4,y->-I*Sqrt(11)},{x->4,y->I*Sqrt(11)}}");
    check("Solve({x^2+5x+3==0, x<0}, x)", //
        "{{x->1/2*(-5-Sqrt(13))},{x->1/2*(-5+Sqrt(13))}}");
    check("Solve({x^2+5x+3==0, x>0}, x)", //
        "{}");
    check("Solve({x^2 == 4, x > 0}, x)", //
        "{{x->2}}");
    // github #200 end

    check("Solve(2*x^(x-3)==3^(x-2),x)", //
        "Solve(2/x^(3-x)==3^(-2+x),x)");

    // https://github.com/tranleduy2000/ncalc/issues/79
    // 0x + 50y + 2z = 20
    // -6x - 12y + 20z = 8
    // 6x + 62y - 18z = 12
    check("Solve({50*y+2*z==20, -6*x-12*y+20*z==8, 6*x+62*y-18*z==12},{x,y,z})", //
        "{{x->1/75*(-160+256*z),y->1/25*(10-z)}}");
    check("Solve({50*y+2*z==20, -6*x-12*y+20*z==8, 6*x+62*y-18*z==12},{x,y,z}) // N", //
        "{{x->0.0133333*(-160.0+256.0*z),y->0.04*(10.0-z)}}");

    // 0x + 50y + 2z = 20
    // -6x - 12y + 20z = 8
    // 6x + 62y - 18z = 13
    check("Solve({50*y+2*z==20, -6*x-12*y+20*z==8, 6*x+62*y-18*z==13},{x,y,z})", //
        "{}");
    check("Solve({50*y+2*z==20, -6*x-12*y+20*z==8, 6*x+62*y-18*z==13},{x,y,z}) // N", //
        "{}");

    check("-(-I*Pi-f(a))/b", //
        "(I*Pi+f(a))/b");
    check("Solve(3*x^x==7,x)", //
        "{{x->Log(7/3)/ProductLog(Log(7/3))}}");
    check("Solve(3*x^x==-7,x)", //
        "{{x->(I*Pi+Log(7/3))/ProductLog(I*Pi+Log(7/3))}}");
    check("Solve(x^x==7,x)", //
        "{{x->Log(7)/ProductLog(Log(7))}}");
    check("Solve(x^x==-7,x)", //
        "{{x->(I*Pi+Log(7))/ProductLog(I*Pi+Log(7))}}");
    check(
        "ReplaceAll(x^3-89, {{x->-(-89)^(1/3)},{x->(-89)^(1/3)*(-1)^(1/3)},{x->-(-89)^(1/3)*(-1)^(2/3)}})", //
        "{0,0,0}");

    check("Solve((5.0*x)/y==(0.8*y)/x,x)", //
        "{{x->-0.4*y},{x->0.4*y}}");
    check("Solve(x==0,x)", //
        "{{x->0}}");
    check("Solve(5*y^x==8,x)", //
        "{{x->Log(8/5)/Log(y)}}");
    check("Solve(x^y+8==a*b,x)", //
        "{{x->(-8+a*b)^(1/y)}}");
    check("Solve(x^2==0,x)", //
        "{{x->0}}");
    check("Solve(x^3==0,x)", //
        "{{x->0}}");
    check("Solve(x+1==0,x)", //
        "{{x->-1}}");
    check("Solve(x^2+1==0,x)", //
        "{{x->-I},{x->I}}");
    check("Solve(2*x^2+1==0,x)", //
        "{{x->-I/Sqrt(2)},{x->I/Sqrt(2)}}");
    check("Solve(3*(x+5)*(x-4)==0,x)", //
        "{{x->-5},{x->4}}");
    check("Solve(3*(x+a)*(x-b)==0,x)", //
        "{{x->-a},{x->b}}");
    check("Solve(a*x^2+b==0,x)", //
        "{{x->(-I*Sqrt(b))/Sqrt(a)},{x->(I*Sqrt(b))/Sqrt(a)}}");
    check("Solve(x^2+2*x+1==0,x)", //
        "{{x->-1},{x->-1}}");
    // TODO simplify result
    check("Solve(-5*Sqrt(14)*x-14*x^2*Sqrt(83)-10==0,x)", //
        "{{x->(-5*Sqrt(14)-Sqrt(350-560*Sqrt(83)))/(28*Sqrt(83))},{x->(-5*Sqrt(14)+Sqrt(\n"
            + "350-560*Sqrt(83)))/(28*Sqrt(83))}}");

    check("Solve(8*x^3-26x^2+3x+9==0,x)", //
        "{{x->-1/2},{x->3/4},{x->3}}");

    check("Solve((a*x^2+1)==0,x)", //
        "{{x->-I/Sqrt(a)},{x->I/Sqrt(a)}}");
    check("Solve(Sqrt(x)-2*x+x^2==0,x)", //
        "{{x->0},{x->1}}");
    check("Solve((2*x+x^2)^2-x==0,x)", //
        "{{x->0},{x->-4/3+(43/2+3/2*Sqrt(177))^(1/3)/3+4/3*2^(1/3)/(43+3*Sqrt(177))^(1/3)},{x->-\n"
            + "4/3-2/3*2^(1/3)/(43+3*Sqrt(177))^(1/3)+(I*2*2^(1/3))/(Sqrt(3)*(43+3*Sqrt(177))^(\n"
            + "1/3))-(43+3*Sqrt(177))^(1/3)/(6*2^(1/3))+(-I*1/2*(43+3*Sqrt(177))^(1/3))/(2^(1/3)*Sqrt(\n"
            + "3))},{x->-4/3-2/3*2^(1/3)/(43+3*Sqrt(177))^(1/3)+(-I*2*2^(1/3))/(Sqrt(3)*(43+3*Sqrt(\n"
            + "177))^(1/3))-(43+3*Sqrt(177))^(1/3)/(6*2^(1/3))+(I*1/2*(43+3*Sqrt(177))^(1/3))/(\n"
            + "2^(1/3)*Sqrt(3))}}");

    check("Solve({x^2-11==y, x+y==-9}, {x,y})", //
        "{{x->-2,y->-7},{x->1,y->-10}}");

    check("Solve(30*x/0.0002==30,{x})", //
        "{{x->0.0002}}");
    // TODO
    // check("Solve(30*x/0.000000002==30,x)", //
    // "{{x->2.*10^-9}}");

    // check("Factor(E^(3*x)-4*E^x+3*E^(-x))", //
    // "((-1+E^x)*(1+E^x)*(-3+E^(2*x)))/E^x");
    check("Solve((-3+E^(2*x))==0,x)", //
        "{{x->ConditionalExpression(I*Pi*C(1)+Log(3)/2,C(1)∈Integers)}}");
    check("Solve(E^(3*x)-4*E^x+3*E^(-x)==0,x)", //
        "{{x->ConditionalExpression(I*2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(I*Pi+\n"
            + "I*2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(I*2*Pi*C(1)+Log(3)/2,C(1)∈Integers)},{x->ConditionalExpression(I*Pi+\n"
            + "I*2*Pi*C(1)+Log(3)/2,C(1)∈Integers)}}");

    check("Solve(1+E^x==0,x)", //
        "{{x->ConditionalExpression(I*Pi+I*2*Pi*C(1),C(1)∈Integers)}}");
    check("Solve(a+E^(b*x)==0,x)", //
        "{{x->ConditionalExpression((I*2*Pi*C(1)+Log(-a))/b,C(1)∈Integers)}}");

    check("Solve(E^x==b,x)", //
        "{{x->ConditionalExpression(I*2*Pi*C(1)+Log(b),C(1)∈Integers)}}");
    check("Solve(a^x==42,x)", //
        "{{x->Log(42)/Log(a)}}");
    // check("Solve(2+(-I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))+(I*3*(E^(-I*3*x)-E^(I*3*x)))/(E^(-I*3*x)+E^(I*3*x))==0,x)",
    // //
    // "((2-I*2)*(I+(-1/2-I*1/2)*E^(I*2*x)+E^(I*4*x)))/((-1-I*E^(I*x)+E^(I*2*x))*(-1+I*E^(I*x)+E^(\n"
    // + "I*2*x)))");

    check("Solve(Log(2,x)+4*Log(x,2)-5==0,x)", //
        "{{x->2},{x->16}}");
    check("Solve(x^(1/Log(2))-1==0,x)", //
        "{{x->1}}");
    check("Solve(Log((x-1)*(x+1))==0,x)", //
        "{{x->-Sqrt(2)},{x->Sqrt(2)}}");
    check("{Re @ #, Im @ #} & /@ Last @@@ Solve(x^3 + 3 == 0, x)", //
        "{{3^(1/3)/2,3^(5/6)/2},{-3^(1/3),0},{3^(1/3)/2,-3^(5/6)/2}}");

    // github #117
    check("Solve({x+y^2==9.1, y==2*x+2}, {x,y})", //
        "{{x->-2.71893,y->-3.43787},{x->0.468934,y->2.93787}}");

    check("Solve(-28 - 4*Sqrt(-1 + x) + 4*x==0,x)", //
        "{{x->10}}");
    check("Solve(Sqrt(5*x-25)-Sqrt(x-1)==2,x)", //
        "{{x->10}}");

    check("Solve(Sqrt(x+6)-Sqrt(x-1)==1,x)", //
        "{{x->10}}");

    check("Solve(Sin((x+1)*(x-1))==2,x)", //
        "{{x->-Sqrt(1+ArcSin(2))},{x->Sqrt(1+ArcSin(2))}}"); //
    check("Solve(Log((x+1)*(x-1))==2,x)", //
        "{{x->-Sqrt(1+E^2)},{x->Sqrt(1+E^2)}}");
    check("Solve(Log(x^2-1)==3,x)", //
        "{{x->-Sqrt(1+E^3)},{x->Sqrt(1+E^3)}}");

    check("Solve(a^x==b,x)", //
        "{{x->Log(b)/Log(a)}}");

    checkNumeric("Eliminate(Abs(x-1)==(-1),x)", //
        "True");
    checkNumeric("Solve(Abs(x-1)==(-1),x)", //
        "{}");
    checkNumeric("Solve(Abs(x-1)==1,x)", //
        "{{x->0},{x->2}}");

    // TODO
    // checkNumeric("Solve(30*x/0.000002==30,x)", //
    // "{{x->2.0E-6}}");
    check("Solve((a*x + b)/(c*x + d)==y,x)", //
        "{{x->(-b+d*y)/(a-c*y)}}");

    check("E^((Log(a)+Log(b))/m)", //
        "E^((Log(a)+Log(b))/m)");
    check("Solve(a0*x^p+a1*x^q==0,x)", //
        "{{x->E^((-I*Pi+Log(a0)-Log(a1))/(-p+q))}}");

    check("Solve(a*x^2+b*x==0, x)", //
        "{{x->0},{x->-b/a}}");

    check("Solve({Cos(x)*x==0, x > 10}, x)", //
        "{{x->0},{x->Pi/2}}");
    // TODO select a better starting value for internally used FindRoot:
    check("NSolve({Cos(x)*x==0, x > 10}, x)", //
        "{{x->0.0},{x->1.5708}}");

    check("Solve({Cos(x)*x==0, x==0}, x)", //
        "{{x->0}}");
    check("Solve({Cos(x)*x==0, x < 10}, x)", //
        "{{x->0},{x->Pi/2}}");

    // check("Solve((x^4 - 1)*(x^4 - 4) == 0, x, )", "");
    check("Solve(x == x, x)", //
        "{{}}");
    // check("Solve(x == 1 && x == 2, x)", //
    // "{}");

    check("Solve((5.0*x)/y==(0.8*y)/x,x)", //
        "{{x->-0.4*y},{x->0.4*y}}");

    // gh issue #2
    check("Solve(x^2+y^2==5,x)", //
        "{{x->-Sqrt(5-y^2)},{x->Sqrt(5-y^2)}}");

    // check("x=20.796855124168776", "20.79686");
    // check("Clear(x);Solve(x==(-1.0000000000000002)*Sqrt(y^2.0),y)",
    // "{{y->1.0*Sqrt(x^2.0)}}");

    // Issue #175
    check("Solve(Sqrt(-16.0+a^2.0)/(20.0-2.0*92)==0.5,a)", //
        "{}");

    // Issue #166
    check("Solve(2*x/y==x/z,x)", //
        "{{x->0}}");
    // Issue #165
    check("Solve((3.0*y)/x==(1.5*y)/z,x)", //
        "{{x->2.0*z}}");
    // Issue #162
    check("Solve((5.0*x)/y==(0.8*y)/x,x)", //
        "{{x->-0.4*y},{x->0.4*y}}");
    // Issue #161
    checkNumeric("Solve((0.6000000000000001*2.5)/y==z/x,x)", //
        "{{x->0.6666666666666665*y*z}}");
    // Issue #160
    checkNumeric("Solve((2.10937501*y)/(0.6923076944378698*z)==(0.6923076944378698*z)/x,x)", //
        "{{x->(0.2272189352323269*z^2.0)/y}}");
    // Issue #159
    check("Solve(x==2*Sqrt(y)*Sqrt(z),y)", //
        "{{y->x^2/(4*z)}}");
    check("Solve(x==2.0*Sqrt(y)*Sqrt(z),y)", //
        "{{y->(0.25*x^2.0)/z}}");

    // Issue #155
    check("Solve(x==2*Sqrt(y)*Sqrt(z),y)", //
        "{{y->x^2/(4*z)}}");

    // Issue #151
    check("Solve(60+abc==120.0,abc)", //
        "{{abc->60.0}}");

    // Issue #152
    check("Solve(Sqrt(x)==16.1,x)", //
        "{{x->259.21}}");

    // TODO check type of result in Solve()
    // check("Solve(x^3 == 1, x, Reals)", "{{x->1}}");

    check("Solve(x+5.0==a,x)", //
        "{{x->-5.0+a}}");

    checkNumeric("Solve(-8828.206-582.222*b+55.999*b^2.0+4.8*b^3.0==0, b)", //
        "{{b->-11.735882719537253+I*(-4.250200714726689)},{b->-11.735882719537253+I*4.250200714726689},{b->11.805307105741177}}");
    // check("Solve(Abs((-3+x^2)/x) ==2,{x})",
    // "{{x->-3},{x->-1},{x->1},{x->3}}");
    check("Solve(x^3==-2,x)", //
        "{{x->(-2)^(1/3)},{x->-2^(1/3)},{x->-(-1)^(2/3)*2^(1/3)}}");

    // timeouts in Cream engine
    // check("Solve({x^2 + x y + y^2 == 109}, {x, y}, )", "");
    // check("Solve({x^12345 - 2 x^777 + 1 == 0}, {x}, )", "");
    // check("Solve({2 x + 3 y - 5 z == 1 , 3 x - 4 y + 7 z == 3}, {x,
    // y, z}, )", "");

    check("Solve((k*Q*q)/r^2+1/r^4==E,r)", //
        "{{r->Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(\n"
            + "4*E+k^2*q^2*Q^2))},{r->Sqrt(1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(\n"
            + "1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))}}");
    // issue #120
    check("Solve(Sin(x)*x==0, x)", //
        "{{x->0}}");
    check("Solve(Cos(x)*x==0, x)", //
        "{{x->0},{x->Pi/2}}");
    // issue #121
    check("Solve(Sqrt(x)==-1, x)", //
        "{}");
    check("Solve(x^2+1==0, x)", //
        "{{x->-I},{x->I}}");
    check("Solve((k*Q*q)/r^2==E,r)", //
        "{{r->Sqrt(k*q*Q)/Sqrt(E)},{r->-Sqrt(k*q*Q)/Sqrt(E)}}");
    check("Solve((k*Q*q)/r^2+1/r^4==E,r)", //
        "{{r->Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(\n"
            + "4*E+k^2*q^2*Q^2))},{r->Sqrt(1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(\n"
            + "1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))}}");
    check("Solve((k*Q*q)/r^2+1/r^4==0,r)", //
        "{{r->-I/Sqrt(k*q*Q)},{r->I/Sqrt(k*q*Q)}}");
    check("Solve(Abs(x-1) ==1,{x})", //
        "{{x->0},{x->2}}");
    check("Solve(Abs(x^2-1) ==0,{x})", //
        "{{x->-1},{x->1}}");
    check("Solve(Xor(a, b, c, d) && (a || b) && ! (c || d), {a, b, c, d}, Booleans)", //
        "{{a->True,b->False,c->False,d->False},{a->False,b->True,c->False,d->False}}");
    check("Solve({x^2-11==y, x+y==-9}, {x,y})", //
        "{{x->-2,y->-7},{x->1,y->-10}}");

    // issue 42
    // check("$sol=Solve(x^3 + 2x^2 - 5x -3 ==0,x);N($sol)",
    // "{{x->-3.2534180395878516},{x->-0.5199693720627901},{x->1.773387411650642}}");

    // check("Solve(x^3 + 2x^2 - 5x -3 ==0, x)",
    // "{{x->(-1/3)*((1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(1/2)^(1/3)*(-I*9*331^(1/2)+25)^(\n"
    // +
    // "1/3)+2)},{x->(-1/3)*((-I*1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(I*\n"
    // +
    // "1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(-I*9*331^(1/2)+25)^(1/3)+2)},{x->(-1/3)*((I*1/2*3^(\n"
    // +
    // "1/2)-1/2)*(1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(-I*1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(-I*\n"
    // + "9*331^(1/2)+25)^(1/3)+2)}}");
    check("Solve(2*Sin(x)==1/2,x)", //
        "{{x->ArcSin(1/4)}}");
    check("Solve(3+2*Cos(x)==1/2,x)", //
        "{{x->ArcCos(-5/4)}}");
    check("Solve(Sin(x)==0,x)", //
        "{{x->0}}");
    check("Solve(Sin(x)==0.0,x)", //
        "{{x->0.0}}");
    check("Solve(Sin(x)==1/2,x)", //
        "{{x->Pi/6}}");
    checkNumeric("Solve(sin(x)==0.5,x)", //
        "{{x->0.5235987755982989}}");
    check("Solve(x^2-2500.00==0,x)", //
        "{{x->-50.0},{x->50.0}}");
    check("Solve(x^2+a*x+1 == 0, x)", //
        "{{x->1/2*(-a-Sqrt(-4+a^2))},{x->1/2*(-a+Sqrt(-4+a^2))}}");
    check("Solve((-3)*x^3 +10*x^2-11*x == (-4), {x})", //
        "{{x->1},{x->4/3}}");

    checkNumeric("Solve(x^2+50*x-2500.00==0,x)", //
        "{{x->-80.90169943749474},{x->30.90169943749474}}");

    check("Solve(a*x + y == 7 && b*x - y == 1, {x, y})", //
        "{{x->-8/(-a-b),y->(a-7*b)/(-a-b)}}");
    check("Solve({a*x + y == 7, b*x - y == 1}, {x, y})", //
        "{{x->-8/(-a-b),y->(a-7*b)/(-a-b)}}");

    check("Solve(-Infinity==(2*a2)/a3+(-2*a5)/a3,a3)", //
        "Solve(-Infinity==(2*a2)/a3+(-2*a5)/a3,a3)");

    // Issue #168
    checkNumeric("y=297.0004444386505", //
        "297.0004444386505");
    checkNumeric("z=22.13904248493947", // 7
        "22.13904248493947");
    checkNumeric("Solve(x/y==z/x,x)", //
        "{{x->-81.08825721072822},{x->81.08825721072822}}");
  }

  public void testNSolve() {
    // github #261 - JUnit test for Apfloat switching to complex Power calculation
    check("NSolve(0.00004244131815783 == x^5 , x)", //
        "{{x->-0.10802279680851234+I*0.07848315587546605},{x->-0.10802279680851212+I*(-0.07848315587546605)},{x->0.04126103682102799+I*(-0.1269884137508598)},"
            //
            + "{x->0.04126103682102799+I*0.1269884137508598},{x->0.1335235199749684}}");
    // github #247
    check("NSolve((k+3)/(4)==(k)/2,{k})", //
        "{{k->3.0}}");
    check("NSolve(k+3/(4)==(k)/2,{k})", //
        "{{k->-1.5}}");

    check("NSolve({{x,y}=={3,4}},{x,y})", //
        "{{x->3.0,y->4.0}}");

    // https://en.wikipedia.org/wiki/Lambert_W_function#Solving_equations
    // TODO generate 2 solutions
    check("NSolve(3^x==2*x+2, x)", //
        "{{x->-0.79011}}");
    check("NSolve(3^x==2*x, x)", //
        "{{x->0.664769+I*(-0.797037)}}");
    check("NSolve(3^x==-4*x, x)", //
        "{{x->-0.200562}}");
    check("NSolve(x^y==a*y, y)", //
        "{{y->-ProductLog(-Log(x)/a)/Log(x)}}");
    check("NSolve(x^y==y, y)", //
        "{{y->-ProductLog(-Log(x))/Log(x)}}");
    check("NSolve(x^y==-y, y)", //
        "{{y->-ProductLog(Log(x))/Log(x)}}");

    check("NSolve(2*Log(2)* x^2 - Log(4)*x^2 + x - 1 == 0, x)", //
        "{{x->1.0}}");
    check("NSolve(x^(1/7)-x^(1/5)==x^(1/3)-x^(1/2),x)", //
        "{{x->1.0}}");
    check("NSolve(Log(2,x)+4*Log(x,2)-5==0,x)", //
        "{{x->2.0},{x->16.0}}");

    check("NSolve({x+y+z==6, x+y-z==0, y+z==5}, {x,y,z})", //
        "{{x->1.0,y->2.0,z->3.0}}");
    // github #201 begin
    check(
        "NSolve({m1*u1^2 + m2*u2^2 == m1*v1^2 + m2*v2^2, m1*u1 + m2*u2 == m1*v1 + m2*v2}, {v1, v2})",
        //
        "{{v2->u2,v1->u1},{v2->(2.0*m1*u1-m1*u2+m2*u2)/(m1+m2),v1->(m1*u1+m2*u2+(-m2*(2.0*m1*u1-m1*u2+m2*u2))/(m1+m2))/m1}}");
    check("NSolve({m1*u1 + m2*u2 == m1*v1 + m2*v2, u2 - u1 == v2 - v1}, {v1, v2})", //
        "{{v1->(-m1*u1-m2*u2+m2*(-u1+u2))/(-m1-m2),v2->(-m1*u1+m1*(u1-u2)-m2*u2)/(-m1-m2)}}");
    check("NSolve({m1*u1 + m2*u2 == m1*v1 + m2*v2, u2 - u1 == -(v2 - v1)}, {v1, v2})", //
        "{{v1->(m1*u1+m2*u2+m2*(-u1+u2))/(m1+m2),v2->(m1*u1+m1*(u1-u2)+m2*u2)/(m1+m2)}}");
    // github #201 end

    // github #200 begin
    check("NSolve({x^2+y^2==5, x+y^2==-7, y>0}, {x,y})", //
        "{}");
    check("NSolve({x^2+y^2==5, x+y^2==-7, x>0}, {x,y})", //
        "{{x->4.0,y->I*(-3.31662)},{x->4.0,y->I*3.31662}}");
    check("NSolve({x^2+5x+3==0, x<0}, x)", //
        "{{x->-4.30278},{x->-0.697224}}");
    check("NSolve({x^2+5x+3==0, x>0}, x)", //
        "{}");
    check("NSolve({x^2 == 4, x > 0}, x)", //
        "{{x->2.0}}");
    // github #200 end


    // prints error message / wrong result in numeric mode
    check("NSolve(2*x^(x-3)==3^(x-2),x)", //
        "{}");

    // https://github.com/tranleduy2000/ncalc/issues/79
    // 0x + 50y + 2z = 20
    // -6x - 12y + 20z = 8
    // 6x + 62y - 18z = 12
    check("NSolve({50*y+2*z==20, -6*x-12*y+20*z==8, 6*x+62*y-18*z==12},{x,y,z})", //
        "{{x->0.0133333*(-160.0+256.0*z),y->0.04*(10.0-z)}}");
    check("NSolve({50*y+2*z==20, -6*x-12*y+20*z==8, 6*x+62*y-18*z==12},{x,y,z}) // N", //
        "{{x->0.0133333*(-160.0+256.0*z),y->0.04*(10.0-z)}}");

    // 0x + 50y + 2z = 20
    // -6x - 12y + 20z = 8
    // 6x + 62y - 18z = 13
    check("NSolve({50*y+2*z==20, -6*x-12*y+20*z==8, 6*x+62*y-18*z==13},{x,y,z})", //
        "{}");
    check("NSolve({50*y+2*z==20, -6*x-12*y+20*z==8, 6*x+62*y-18*z==13},{x,y,z}) // N", //
        "{}");

    check("-(-I*Pi-f(a))/b", //
        "(I*Pi+f(a))/b");
    check("NSolve(3*x^x==7,x)", //
        "{{x->1.66397}}");
    check("NSolve(3*x^x==-7,x)", //
        "{{x->2.06626+I*1.7231}}");
    check("NSolve(x^x==7,x)", //
        "{{x->2.31645}}");
    check("NSolve(x^x==-7,x)", //
        "{{x->2.55836+I*1.57368}}");
    check(
        "ReplaceAll(x^3-89, {{x->-(-89)^(1/3)},{x->(-89)^(1/3)*(-1)^(1/3)},{x->-(-89)^(1/3)*(-1)^(2/3)}})", //
        "{0,0,0}");

    check("NSolve((5.0*x)/y==(0.8*y)/x,x)", //
        "{{x->-0.4*y},{x->0.4*y}}");
    check("NSolve(x==0,x)", //
        "{{x->0.0}}");
    check("NSolve(5*y^x==8,x)", //
        "{{x->0.470004/Log(y)}}");
    check("NSolve(x^y+8==a*b,x)", //
        "{{x->(-8.0+a*b)^(1/y)}}");
    check("NSolve(x^2==0,x)", //
        "{{x->0.0}}");
    check("NSolve(x^3==0,x)", //
        "{{x->0.0}}");
    check("NSolve(x+1==0,x)", //
        "{{x->-1.0}}");
    check("NSolve(x^2+1==0,x)", //
        "{{x->I*(-1.0)},{x->I*1.0}}");
    check("NSolve(2*x^2+1==0,x)", //
        "{{x->I*(-0.707107)},{x->I*0.707107}}");
    check("NSolve(3*(x+5)*(x-4)==0,x)", //
        "{{x->-5.0},{x->4.0}}");
    check("NSolve(3*(x+a)*(x-b)==0,x)", //
        "{{x->-a},{x->b}}");
    check("NSolve(a*x^2+b==0,x)", //
        "{{x->((I*(-1.0))*Sqrt(b))/Sqrt(a)},{x->((I*1.0)*Sqrt(b))/Sqrt(a)}}");
    check("NSolve(x^2+2*x+1==0,x)", //
        "{{x->-1.0},{x->-1.0}}");

    check("NSolve(-5*Sqrt(14)*x-14*x^2*Sqrt(83)-10==0,x)", //
        "{{x->-0.0733393+I*(-0.27023)},{x->-0.0733393+I*0.27023}}");

    check("NSolve(8*x^3-26x^2+3x+9==0,x)", //
        "{{x->-0.5},{x->0.75},{x->3.0}}");

    check("NSolve((a*x^2+1)==0,x)", //
        "{{x->(I*(-1.0))/Sqrt(a)},{x->(I*1.0)/Sqrt(a)}}");
    check("NSolve(Sqrt(x)-2*x+x^2==0,x)", //
        "{{x->0.0},{x->1.0}}");
    check("NSolve((2*x+x^2)^2-x==0,x)", //
        "{{x->0.0},{x->0.205569},{x->-2.10278+I*(-0.665457)},{x->-2.10278+I*0.665457}}");

    check("NSolve({x^2-11==y, x+y==-9}, {x,y})", //
        "{{x->-2.0,y->-7.0},{x->1.0,y->-10.0}}");

    check("NSolve(30*x/0.0002==30,{x})", //
        "{{x->0.0002}}");
    // see Config.DEFAULT_CHOP_DELTA
    check("NSolve(30*x/0.000000002==30,x)", //
        "{{x->0.0}}");

    // check("Factor(E^(3*x)-4*E^x+3*E^(-x))", //
    // "((-1+E^x)*(1+E^x)*(-3+E^(2*x)))/E^x");
    check("NSolve((-3+E^(2*x))==0,x)", //
        "{{x->0.549306}}");
    check("NSolve(E^(3*x)-4*E^x+3*E^(-x)==0,x)", //
        "{{x->ConditionalExpression((I*6.28319)*C(1.0),C(1)∈Integers)},{x->ConditionalExpression(I*3.14159+(I*6.28319)*C(1.0),C(\n"
            + "1)∈Integers)},{x->ConditionalExpression(0.549306+(I*6.28319)*C(1.0),C(1)∈Integers)},{x->ConditionalExpression(0.549306+I*3.14159+(I*6.28319)*C(1.0),C(\n"
            + "1)∈Integers)}}");
    check("NSolve(E^(3*x)-4*E^x+3*E^(-x)==0,x)", //
        "{{x->ConditionalExpression((I*6.28319)*C(1.0),C(1)∈Integers)},{x->ConditionalExpression(I*3.14159+(I*6.28319)*C(1.0),C(\n"
            + "1)∈Integers)},{x->ConditionalExpression(0.549306+(I*6.28319)*C(1.0),C(1)∈Integers)},{x->ConditionalExpression(0.549306+I*3.14159+(I*6.28319)*C(1.0),C(\n"
            + "1)∈Integers)}}");

    check("NSolve(1+E^x==0,x)", //
        "{{x->I*3.14159}}");
    check("Solve(1+E^x==0,x)", //
        "{{x->ConditionalExpression(I*Pi+I*2*Pi*C(1),C(1)∈Integers)}}");
    check("NSolve(a+E^(b*x)==0,x)", //
        "{{x->ConditionalExpression(((I*6.28319)*C(1.0)+Log(-a))/b,C(1)∈Integers)}}");

    check("NSolve(E^x==b,x)", //
        "{{x->ConditionalExpression((I*6.28319)*C(1.0)+Log(b),C(1)∈Integers)}}");
    check("NSolve(a^x==42,x)", //
        "{{x->3.73767/Log(a)}}");
    //
    // check("NSolve(2+(-I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))+(I*3*(E^(-I*3*x)-E^(I*3*x)))/(E^(-I*3*x)+E^(I*3*x))==0,x)",
    // // //
    // //
    // "((2-I*2)*(I+(-1/2-I*1/2)*E^(I*2*x)+E^(I*4*x)))/((-1-I*E^(I*x)+E^(I*2*x))*(-1+I*E^(I*x)+E^(\n"
    // // + "I*2*x)))");
    //
    // // check("NSolve(4^(2*x+1)*5^(x-2)-6^(1-x)==0,x)", //
    // // "");
    // check("NSolve(Log(2,x)+4*Log(x,2)-5==0,x)", //
    // "{{x->2},{x->16}}");
    // check("NSolve(x^(1/Log(2))-1==0,x)", //
    // "{{x->1}}");
    // check("NSolve(Log((x-1)*(x+1))==0,x)", //
    // "{{x->-Sqrt(2)},{x->Sqrt(2)}}");
    // check("{Re @ #, Im @ #} & /@ Last @@@ NSolve(x^3 + 3 == 0, x)", //
    // "{{3^(1/3)/2,3^(5/6)/2},{-3^(1/3),0},{3^(1/3)/2,-3^(5/6)/2}}");
    //
    // // github #117
    // check("NSolve({x+y^2==9.1, y==2*x+2}, {x,y})", //
    // "{{x->-2.71893,y->-3.43787},{x->0.468934,y->2.93787}}");
    //
    // check("NSolve(-28 - 4*Sqrt(-1 + x) + 4*x==0,x)", //
    // "{{x->10}}");
    // check("NSolve(Sqrt(5*x-25)-Sqrt(x-1)==2,x)", //
    // "{{x->10}}");
    //
    // check("NSolve(Sqrt(x+6)-Sqrt(x-1)==1,x)", //
    // "{{x->10}}");
    //
    // check("NSolve(Sin((x+1)*(x-1))==2,x)", //
    // "{{x->-Sqrt(1+ArcSin(2))},{x->Sqrt(1+ArcSin(2))}}"); //
    // check("NSolve(Log((x+1)*(x-1))==2,x)", //
    // "{{x->-Sqrt(1+E^2)},{x->Sqrt(1+E^2)}}");
    // check("NSolve(Log(x^2-1)==3,x)", //
    // "{{x->-Sqrt(1+E^3)},{x->Sqrt(1+E^3)}}");
    //
    // check("NSolve(a^x==b,x)", //
    // "{{x->Log(b)/Log(a)}}");
    //
    // checkNumeric("Eliminate(Abs(x-1)==(-1),x)", //
    // "True");
    // checkNumeric("NSolve(Abs(x-1)==(-1),x)", //
    // "{}");
    // checkNumeric("NSolve(Abs(x-1)==1,x)", //
    // "{{x->0},{x->2}}");
    //
    // checkNumeric("NSolve(30*x/0.000002==30,x)", //
    // "{{x->2.0E-6}}");
    // check("NSolve((a*x + b)/(c*x + d)==y,x)", //
    // "{{x->(-b+d*y)/(a-c*y)}}");
    //
    // check("E^((Log(a)+Log(b))/m)", //
    // "E^((Log(a)+Log(b))/m)");
    // check("NSolve(a0*x^p+a1*x^q==0,x)", //
    // "{{x->E^((-I*Pi+Log(a0)-Log(a1))/(-p+q))}}");
    //
    // check("NSolve(a*x^2+b*x==0, x)", //
    // "{{x->0},{x->-b/a}}");
    //
    // check("NSolve({Cos(x)*x==0, x > 10}, x)", //
    // "{{x->0},{x->Pi/2}}");
    // // TODO select a better starting value for internally used FindRoot:
    // check("NSolve({Cos(x)*x==0, x > 10}, x)", //
    // "{{x->0.0},{x->1.5708}}");
    //
    // check("NSolve({Cos(x)*x==0, x==0}, x)", //
    // "{{x->0}}");
    // check("NSolve({Cos(x)*x==0, x < 10}, x)", //
    // "{{x->0},{x->Pi/2}}");
    //
    // // check("NSolve((x^4 - 1)*(x^4 - 4) == 0, x, )", "");
    // check("NSolve(x == x, x)", //
    // "{{}}");
    // // check("NSolve(x == 1 && x == 2, x)", //
    // // "{}");
    //
    // check("NSolve((5.0*x)/y==(0.8*y)/x,x)", //
    // "{{x->-0.4*y},{x->0.4*y}}");
    //
    // // gh issue #2
    // check("NSolve(x^2+y^2==5,x)", //
    // "{{x->-Sqrt(5-y^2)},{x->Sqrt(5-y^2)}}");
    //
    // // check("x=20.796855124168776", "20.79686");
    // // check("Clear(x);NSolve(x==(-1.0000000000000002)*Sqrt(y^2.0),y)",
    // // "{{y->1.0*Sqrt(x^2.0)}}");
    //
    // // Issue #175
    // check("NSolve(Sqrt(-16.0+a^2.0)/(20.0-2.0*92)==0.5,a)", //
    // "{}");
    //
    // // Issue #166
    // check("NSolve(2*x/y==x/z,x)", //
    // "{{x->0}}");
    // // Issue #165
    // check("NSolve((3.0*y)/x==(1.5*y)/z,x)", //
    // "{{x->2.0*z}}");
    // // Issue #162
    // check("NSolve((5.0*x)/y==(0.8*y)/x,x)", //
    // "{{x->-0.4*y},{x->0.4*y}}");
    // // Issue #161
    // checkNumeric("NSolve((0.6000000000000001*2.5)/y==z/x,x)", //
    // "{{x->0.6666666666666665*y*z}}");
    // // Issue #160
    // checkNumeric("NSolve((2.10937501*y)/(0.6923076944378698*z)==(0.6923076944378698*z)/x,x)", //
    // "{{x->(0.2272189352323269*z^2.0)/y}}");
    // // Issue #159
    // check("NSolve(x==2*Sqrt(y)*Sqrt(z),y)", //
    // "{{y->x^2/(4*z)}}");
    // check("NSolve(x==2.0*Sqrt(y)*Sqrt(z),y)", //
    // "{{y->(0.25*x^2.0)/z}}");
    //
    // // Issue #155
    // check("NSolve(x==2*Sqrt(y)*Sqrt(z),y)", //
    // "{{y->x^2/(4*z)}}");
    //
    // // Issue #151
    // check("NSolve(60+abc==120.0,abc)", //
    // "{{abc->60.0}}");
    //
    // // Issue #152
    // checkNumeric("NSolve(Sqrt(x)==16.1,x)", //
    // "{{x->259.21}}");
    //
    // // TODO check type of result in NSolve()
    // // check("NSolve(x^3 == 1, x, Reals)", "{{x->1}}");
    //
    // check("NSolve(x+5.0==a,x)", //
    // "{{x->-5.0+a}}");
    //
    // checkNumeric("NSolve(-8828.206-582.222*b+55.999*b^2.0+4.8*b^3.0==0, b)", //
    // "{{b->11.805307105741173},{b->-11.735882719537255+I*(-4.250200714726687)},{b->-11.735882719537255+I*4.250200714726687}}");
    // // check("NSolve(Abs((-3+x^2)/x) ==2,{x})",
    // // "{{x->-3},{x->-1},{x->1},{x->3}}");
    // check("NSolve(x^3==-2,x)", //
    // "{{x->(-2)^(1/3)},{x->-2^(1/3)},{x->-(-1)^(2/3)*2^(1/3)}}");
    //
    // // timeouts in Cream engine
    // // check("NSolve({x^2 + x y + y^2 == 109}, {x, y}, )", "");
    // // check("NSolve({x^12345 - 2 x^777 + 1 == 0}, {x}, Integers)", "");
    // // check("NSolve({2 x + 3 y - 5 z == 1 , 3 x - 4 y + 7 z == 3}, {x,
    // // y, z}, Integers)", "");
    //
    // check("NSolve((k*Q*q)/r^2+1/r^4==E,r)", //
    // "{{r->Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(\n"
    // + "4*E+k^2*q^2*Q^2))},{r->Sqrt(1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(\n"
    // + "1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))}}");
    // // issue #120
    // check("NSolve(Sin(x)*x==0, x)", //
    // "{{x->0}}");
    // check("NSolve(Cos(x)*x==0, x)", //
    // "{{x->0},{x->Pi/2}}");
    // // issue #121
    // check("NSolve(Sqrt(x)==-1, x)", //
    // "{}");
    // check("NSolve(x^2+1==0, x)", //
    // "{{x->-I},{x->I}}");
    // check("NSolve((k*Q*q)/r^2==E,r)", //
    // "{{r->Sqrt(k*q*Q)/Sqrt(E)},{r->-Sqrt(k*q*Q)/Sqrt(E)}}");
    // check("NSolve((k*Q*q)/r^2+1/r^4==E,r)", //
    // "{{r->Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(\n"
    // + "4*E+k^2*q^2*Q^2))},{r->Sqrt(1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(\n"
    // + "1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))}}");
    // check("NSolve((k*Q*q)/r^2+1/r^4==0,r)", //
    // "{{r->-I/Sqrt(k*q*Q)},{r->I/Sqrt(k*q*Q)}}");
    // check("NSolve(Abs(x-1) ==1,{x})", //
    // "{{x->0},{x->2}}");
    // check("NSolve(Abs(x^2-1) ==0,{x})", //
    // "{{x->-1},{x->1}}");
    // check("NSolve(Xor(a, b, c, d) && (a || b) && ! (c || d), {a, b, c, d}, Booleans)", //
    // "{{a->True,b->False,c->False,d->False},{a->False,b->True,c->False,d->False}}");
    // check("NSolve({x^2-11==y, x+y==-9}, {x,y})", //
    // "{{x->-2,y->-7},{x->1,y->-10}}");
    //
    // // issue 42
    // // check("$sol=NSolve(x^3 + 2x^2 - 5x -3 ==0,x);N($sol)",
    // // "{{x->-3.2534180395878516},{x->-0.5199693720627901},{x->1.773387411650642}}");
    //
    // // check("NSolve(x^3 + 2x^2 - 5x -3 ==0, x)",
    // // "{{x->(-1/3)*((1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(1/2)^(1/3)*(-I*9*331^(1/2)+25)^(\n"
    // // +
    // // "1/3)+2)},{x->(-1/3)*((-I*1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(I*\n"
    // // +
    // // "1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(-I*9*331^(1/2)+25)^(1/3)+2)},{x->(-1/3)*((I*1/2*3^(\n"
    // // +
    // // "1/2)-1/2)*(1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(-I*1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(-I*\n"
    // // + "9*331^(1/2)+25)^(1/3)+2)}}");
    // check("NSolve(2*Sin(x)==1/2,x)", //
    // "{{x->ArcSin(1/4)}}");
    // check("NSolve(3+2*Cos(x)==1/2,x)", //
    // "{{x->ArcCos(-5/4)}}");
    // check("NSolve(Sin(x)==0,x)", //
    // "{{x->0}}");
    // check("NSolve(Sin(x)==0.0,x)", //
    // "{{x->0}}");
    // check("NSolve(Sin(x)==1/2,x)", //
    // "{{x->Pi/6}}");
    // checkNumeric("NSolve(sin(x)==0.5,x)", //
    // "{{x->0.5235987755982988}}");
    // check("NSolve(x^2-2500.00==0,x)", //
    // "{{x->-50.0},{x->50.0}}");
    // check("NSolve(x^2+a*x+1 == 0, x)", //
    // "{{x->1/2*(-a-Sqrt(-4+a^2))},{x->1/2*(-a+Sqrt(-4+a^2))}}");
    // check("NSolve((-3)*x^3 +10*x^2-11*x == (-4), {x})", //
    // "{{x->1},{x->4/3}}");
    //
    // checkNumeric("NSolve(x^2+50*x-2500.00==0,x)", //
    // "{{x->-80.90169943749474},{x->30.90169943749474}}");
    //
    // check("NSolve(a*x + y == 7 && b*x - y == 1, {x, y})", //
    // "{{x->-8/(-a-b),y->(a-7*b)/(-a-b)}}");
    // check("NSolve({a*x + y == 7, b*x - y == 1}, {x, y})", //
    // "{{x->-8/(-a-b),y->(a-7*b)/(-a-b)}}");
    //
    // check("NSolve(-Infinity==(2*a2)/a3+(-2*a5)/a3,a3)", //
    // "NSolve(-Infinity==(2*a2)/a3+(-2*a5)/a3,a3)");
    //
    // // Issue #168
    // checkNumeric("y=297.0004444386505", //
    // "297.0004444386505");
    // checkNumeric("z=22.13904248493947", // 7
    // "22.13904248493947");
    // checkNumeric("NSolve(x/y==z/x,x)", //
    // "{{x->-81.08825721072822},{x->81.08825721072822}}");
  }

  public void testChocoSolver001() {
    check("Together(1/x+1/y-1/20 )", //
        "(20*x+20*y-x*y)/(20*x*y)");
    check("Solve(20*x+20*y-x*y==0,{x,y},Integers)", //
        "{{x->-380,y->19},{x->-180,y->18},{x->-80,y->16},{x->-60,y->15},{x->-30,y->12},{x->-\n"
            + "20,y->10},{x->-5,y->4},{x->0,y->0},{x->4,y->-5},{x->10,y->-20},{x->12,y->-30},{x->\n"
            + "15,y->-60},{x->16,y->-80},{x->18,y->-180},{x->19,y->-380},{x->21,y->420},{x->22,y->\n"
            + "220},{x->24,y->120},{x->25,y->100},{x->28,y->70},{x->30,y->60},{x->36,y->45},{x->\n"
            + "40,y->40},{x->45,y->36},{x->60,y->30},{x->70,y->28},{x->100,y->25},{x->120,y->24},{x->\n"
            + "220,y->22},{x->420,y->21}}");
    check("Solve(1/x+1/y==1/20,{x,y},Integers)", //
        "Solve(1/x+1/y==1/20,{x,y},Integers)");

    // https://github.com/chocoteam/choco-solver/blob/a0b4b67949f59f4f4d0063d15d0393a59acc45ad/solver/src/test/java/org/chocosolver/solver/expression/discrete/ExpressionTest.java#L498
    check(
        "Solve({ ((x1-x2)^2+(y1-y2)^2)^2 == d, 0<d<100, 0 < x1 <2,0 < y1 < 2, 0 < x2 <2, 0 < y2 <2 }, {d,x1,x2,y1,y2}, Integers)", //
        "{}");
    check(
        "Solve({ ((x1-x2)^2+(y1-y2)^2)^2 == d, 0<=d<=100, 0 <= x1 <=2,0 <= y1 <= 2, 0 <= x2 <=2, 0 <= y2 <=2 }, {d,x1,x2,y1,y2}, Integers)", //
        "{{d->0,x1->0,x2->0,y1->0,y2->0},{d->0,x1->0,x2->0,y1->1,y2->1},{d->0,x1->0,x2->0,y1->\n" //
            + "2,y2->2},{d->0,x1->1,x2->1,y1->0,y2->0},{d->0,x1->1,x2->1,y1->1,y2->1},{d->0,x1->\n" //
            + "1,x2->1,y1->2,y2->2},{d->0,x1->2,x2->2,y1->0,y2->0},{d->0,x1->2,x2->2,y1->1,y2->\n" //
            + "1},{d->0,x1->2,x2->2,y1->2,y2->2},{d->1,x1->0,x2->0,y1->0,y2->1},{d->1,x1->0,x2->\n" //
            + "0,y1->1,y2->0},{d->1,x1->0,x2->0,y1->1,y2->2},{d->1,x1->0,x2->0,y1->2,y2->1},{d->\n" //
            + "1,x1->0,x2->1,y1->0,y2->0},{d->1,x1->0,x2->1,y1->1,y2->1},{d->1,x1->0,x2->1,y1->\n" //
            + "2,y2->2},{d->1,x1->1,x2->0,y1->0,y2->0},{d->1,x1->1,x2->0,y1->1,y2->1},{d->1,x1->\n" //
            + "1,x2->0,y1->2,y2->2},{d->1,x1->1,x2->1,y1->0,y2->1},{d->1,x1->1,x2->1,y1->1,y2->\n" //
            + "0},{d->1,x1->1,x2->1,y1->1,y2->2},{d->1,x1->1,x2->1,y1->2,y2->1},{d->1,x1->1,x2->\n" //
            + "2,y1->0,y2->0},{d->1,x1->1,x2->2,y1->1,y2->1},{d->1,x1->1,x2->2,y1->2,y2->2},{d->\n" //
            + "1,x1->2,x2->1,y1->0,y2->0},{d->1,x1->2,x2->1,y1->1,y2->1},{d->1,x1->2,x2->1,y1->\n" //
            + "2,y2->2},{d->1,x1->2,x2->2,y1->0,y2->1},{d->1,x1->2,x2->2,y1->1,y2->0},{d->1,x1->\n" //
            + "2,x2->2,y1->1,y2->2},{d->1,x1->2,x2->2,y1->2,y2->1},{d->4,x1->0,x2->1,y1->0,y2->\n" //
            + "1},{d->4,x1->0,x2->1,y1->1,y2->0},{d->4,x1->0,x2->1,y1->1,y2->2},{d->4,x1->0,x2->\n" //
            + "1,y1->2,y2->1},{d->4,x1->1,x2->0,y1->0,y2->1},{d->4,x1->1,x2->0,y1->1,y2->0},{d->\n" //
            + "4,x1->1,x2->0,y1->1,y2->2},{d->4,x1->1,x2->0,y1->2,y2->1},{d->4,x1->1,x2->2,y1->\n" //
            + "0,y2->1},{d->4,x1->1,x2->2,y1->1,y2->0},{d->4,x1->1,x2->2,y1->1,y2->2},{d->4,x1->\n" //
            + "1,x2->2,y1->2,y2->1},{d->4,x1->2,x2->1,y1->0,y2->1},{d->4,x1->2,x2->1,y1->1,y2->\n" //
            + "0},{d->4,x1->2,x2->1,y1->1,y2->2},{d->4,x1->2,x2->1,y1->2,y2->1},{d->16,x1->0,x2->\n" //
            + "0,y1->0,y2->2},{d->16,x1->0,x2->0,y1->2,y2->0},{d->16,x1->0,x2->2,y1->0,y2->0},{d->\n" //
            + "16,x1->0,x2->2,y1->1,y2->1},{d->16,x1->0,x2->2,y1->2,y2->2},{d->16,x1->1,x2->1,y1->\n" //
            + "0,y2->2},{d->16,x1->1,x2->1,y1->2,y2->0},{d->16,x1->2,x2->0,y1->0,y2->0},{d->16,x1->\n" //
            + "2,x2->0,y1->1,y2->1},{d->16,x1->2,x2->0,y1->2,y2->2},{d->16,x1->2,x2->2,y1->0,y2->\n" //
            + "2},{d->16,x1->2,x2->2,y1->2,y2->0},{d->25,x1->0,x2->1,y1->0,y2->2},{d->25,x1->0,x2->\n" //
            + "1,y1->2,y2->0},{d->25,x1->0,x2->2,y1->0,y2->1},{d->25,x1->0,x2->2,y1->1,y2->0},{d->\n" //
            + "25,x1->0,x2->2,y1->1,y2->2},{d->25,x1->0,x2->2,y1->2,y2->1},{d->25,x1->1,x2->0,y1->\n" //
            + "0,y2->2},{d->25,x1->1,x2->0,y1->2,y2->0},{d->25,x1->1,x2->2,y1->0,y2->2},{d->25,x1->\n" //
            + "1,x2->2,y1->2,y2->0},{d->25,x1->2,x2->0,y1->0,y2->1},{d->25,x1->2,x2->0,y1->1,y2->\n" //
            + "0},{d->25,x1->2,x2->0,y1->1,y2->2},{d->25,x1->2,x2->0,y1->2,y2->1},{d->25,x1->2,x2->\n" //
            + "1,y1->0,y2->2},{d->25,x1->2,x2->1,y1->2,y2->0},{d->64,x1->0,x2->2,y1->0,y2->2},{d->\n" //
            + "64,x1->0,x2->2,y1->2,y2->0},{d->64,x1->2,x2->0,y1->0,y2->2},{d->64,x1->2,x2->0,y1->\n" //
            + "2,y2->0}}");
  }

  public void testChocoSolver002() {
    check("Solve({ Max(3*x+4*y)==z, z > 0, x+2*y<=14,3*x-y>=0,x-y<= 2}, {x,y,z}, Integers)", //
        "{{x->1,y->0,z->3},{x->1,y->1,z->7},{x->1,y->2,z->11},{x->1,y->3,z->15},{x->2,y->\n"
            + "0,z->6},{x->2,y->1,z->10},{x->2,y->2,z->14},{x->2,y->3,z->18},{x->2,y->4,z->22},{x->\n"
            + "2,y->5,z->26},{x->2,y->6,z->30},{x->3,y->1,z->13},{x->3,y->2,z->17},{x->3,y->3,z->\n"
            + "21},{x->3,y->4,z->25},{x->3,y->5,z->29},{x->4,y->2,z->20},{x->4,y->3,z->24},{x->\n"
            + "4,y->4,z->28},{x->4,y->5,z->32},{x->5,y->3,z->27},{x->5,y->4,z->31},{x->6,y->4,z->\n"
            + "34}}");
  }

  public void testSolveIntegers() {
    check("Solve({x > 0, y > 0, x^2 + 2*y^3 == 3681}, {x, y}, Integers)", //
        "{{x->15,y->12},{x->41,y->10},{x->57,y->6}}");
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      check("Solve(2 x + 3 y - 5 z == 1 && 3 x - 4 y + 7 z == 3, {x, y, z}, Integers)", //
          "{{x->-564,y->16378,z->9601},{x->-563,y->16349,z->9584},{x->-562,y->16320,z->9567},{x->-\n"
              + "561,y->16291,z->9550},{x->-560,y->16262,z->9533},{x->-559,y->16233,z->95<<SHORT>>",
          160);
    }

    // check("Roots(x^4 == 1 - I, x)", //
    // "x==-(-1+I)^(1/4)||x==(-1+I)^(1/4)||x==I*(-1+I)^(1/4)||x==-I*(-1+I)^(1/4)");
    // check("Solve(x^3 == 1 - I, x)", //
    // "{{x->-(-1+I)^(1/3)},{x->(-1+I)^(1/3)},{x->-(-1)^(2/3)*(-1+I)^(1/3)}}");
    check("Solve(1-1/10*i*1==0,{},Integers)", //
        "{{i->10}}");
    check("Solve(1-1/10*i*1==0,{i,Null},Integers)", //
        "{{i->ConditionalExpression(10,Null∈Integers)}}");
    check("Solve({},{x,y},Integers)", //
        "{{}}");
    check("Solve(1 - (i*1)/10 == 0, i, Integers)", //
        "{{i->10}}");
    // check("Solve({x^2 + 2*y^3 == 3681, x > 0, y > 0}, {x, y}, Integers)", //
    // "{{x->15,y->12},{x->41,y->10},{x->57,y->6}}");
    check("Solve({x>=0,y>=0,x+y==7,2*x+4*y==20},{x,y}, Integers)", //
        "{{x->4,y->3}}");
    check("Solve(x>=0 && y>=0 && x+y==7 && 2*x+4*y==20,{x,y}, Integers)", //
        "{{x->4,y->3}}");
    check("Solve({2*x + 3*y == 4, 3*x - 4*y <= 5,x - 2*y > -21}, {x,  y}, Integers)", //
        "{{x->-7,y->6},{x->-4,y->4},{x->-1,y->2}}");
  }

  // public void testSolveReals() {
  // check(
  // "Solve(x^3 == 1, x, Reals)", //
  // "{{x->1.0}}");
  // check(
  // "Solve({x > 0, y > 0, x^2 + 2*y^3 == 3681}, {x, y}, Reals)", //
  // "{{x->26.43562,y->11.4244}}");
  // check(
  // "Solve({2*x + 3*y == 4, 3*x - 4*y <= 5,x - 2*y > -21}, {x, y}, Reals)", //
  // "{{x->0.666667,y->0.888889}}");
  // check(
  // "Solve(2*x + 3*y - 5*z == 1 && 3*x - 4*y + 7*z == 3, {x, y, z}, Reals)", //
  // "{{x->0.745995,y->0.366124,z->0.318073}}");
  // }

  public void testSolveIssue130() {
    check("Sqrt(1-x)+Sqrt(3+x)", //
        "Sqrt(1-x)+Sqrt(3+x)");
    check("Sqrt(-1)*(-1)^(1/10)", //
        "(-1)^(3/5)");
    check("-1*Sqrt(-1)*(-1)^(1/10)", //
        "-(-1)^(3/5)");

    check("Solve(x^258==1,x)",
        "{{x->-1},{x->1},{x->-(-1)^(1/129)},{x->(-1)^(1/129)},{x->-(-1)^(2/129)},{x->(-1)^(\n"
            + "2/129)},{x->-(-1)^(1/43)},{x->(-1)^(1/43)},{x->-(-1)^(4/129)},{x->(-1)^(4/129)},{x->-(-\n"
            + "1)^(5/129)},{x->(-1)^(5/129)},{x->-(-1)^(2/43)},{x->(-1)^(2/43)},{x->-(-1)^(7/\n"
            + "129)},{x->(-1)^(7/129)},{x->-(-1)^(8/129)},{x->(-1)^(8/129)},{x->-(-1)^(3/43)},{x->(-\n"
            + "1)^(3/43)},{x->-(-1)^(10/129)},{x->(-1)^(10/129)},{x->-(-1)^(11/129)},{x->(-1)^(\n"
            + "11/129)},{x->-(-1)^(4/43)},{x->(-1)^(4/43)},{x->-(-1)^(13/129)},{x->(-1)^(13/129)},{x->-(-\n"
            + "1)^(14/129)},{x->(-1)^(14/129)},{x->-(-1)^(5/43)},{x->(-1)^(5/43)},{x->-(-1)^(16/\n"
            + "129)},{x->(-1)^(16/129)},{x->-(-1)^(17/129)},{x->(-1)^(17/129)},{x->-(-1)^(6/43)},{x->(-\n"
            + "1)^(6/43)},{x->-(-1)^(19/129)},{x->(-1)^(19/129)},{x->-(-1)^(20/129)},{x->(-1)^(\n"
            + "20/129)},{x->-(-1)^(7/43)},{x->(-1)^(7/43)},{x->-(-1)^(22/129)},{x->(-1)^(22/129)},{x->-(-\n"
            + "1)^(23/129)},{x->(-1)^(23/129)},{x->-(-1)^(8/43)},{x->(-1)^(8/43)},{x->-(-1)^(25/\n"
            + "129)},{x->(-1)^(25/129)},{x->-(-1)^(26/129)},{x->(-1)^(26/129)},{x->-(-1)^(9/43)},{x->(-\n"
            + "1)^(9/43)},{x->-(-1)^(28/129)},{x->(-1)^(28/129)},{x->-(-1)^(29/129)},{x->(-1)^(\n"
            + "29/129)},{x->-(-1)^(10/43)},{x->(-1)^(10/43)},{x->-(-1)^(31/129)},{x->(-1)^(31/\n"
            + "129)},{x->-(-1)^(32/129)},{x->(-1)^(32/129)},{x->-(-1)^(11/43)},{x->(-1)^(11/43)},{x->-(-\n"
            + "1)^(34/129)},{x->(-1)^(34/129)},{x->-(-1)^(35/129)},{x->(-1)^(35/129)},{x->-(-1)^(\n"
            + "12/43)},{x->(-1)^(12/43)},{x->-(-1)^(37/129)},{x->(-1)^(37/129)},{x->-(-1)^(38/\n"
            + "129)},{x->(-1)^(38/129)},{x->-(-1)^(13/43)},{x->(-1)^(13/43)},{x->-(-1)^(40/129)},{x->(-\n"
            + "1)^(40/129)},{x->-(-1)^(41/129)},{x->(-1)^(41/129)},{x->-(-1)^(14/43)},{x->(-1)^(\n"
            + "14/43)},{x->-(-1)^(1/3)},{x->(-1)^(1/3)},{x->-(-1)^(44/129)},{x->(-1)^(44/129)},{x->-(-\n"
            + "1)^(15/43)},{x->(-1)^(15/43)},{x->-(-1)^(46/129)},{x->(-1)^(46/129)},{x->-(-1)^(\n"
            + "47/129)},{x->(-1)^(47/129)},{x->-(-1)^(16/43)},{x->(-1)^(16/43)},{x->-(-1)^(49/\n"
            + "129)},{x->(-1)^(49/129)},{x->-(-1)^(50/129)},{x->(-1)^(50/129)},{x->-(-1)^(17/43)},{x->(-\n"
            + "1)^(17/43)},{x->-(-1)^(52/129)},{x->(-1)^(52/129)},{x->-(-1)^(53/129)},{x->(-1)^(\n"
            + "53/129)},{x->-(-1)^(18/43)},{x->(-1)^(18/43)},{x->-(-1)^(55/129)},{x->(-1)^(55/\n"
            + "129)},{x->-(-1)^(56/129)},{x->(-1)^(56/129)},{x->-(-1)^(19/43)},{x->(-1)^(19/43)},{x->-(-\n"
            + "1)^(58/129)},{x->(-1)^(58/129)},{x->-(-1)^(59/129)},{x->(-1)^(59/129)},{x->-(-1)^(\n"
            + "20/43)},{x->(-1)^(20/43)},{x->-(-1)^(61/129)},{x->(-1)^(61/129)},{x->-(-1)^(62/\n"
            + "129)},{x->(-1)^(62/129)},{x->-(-1)^(21/43)},{x->(-1)^(21/43)},{x->-(-1)^(64/129)},{x->(-\n"
            + "1)^(64/129)},{x->-(-1)^(65/129)},{x->(-1)^(65/129)},{x->-(-1)^(22/43)},{x->(-1)^(\n"
            + "22/43)},{x->-(-1)^(67/129)},{x->(-1)^(67/129)},{x->-(-1)^(68/129)},{x->(-1)^(68/\n"
            + "129)},{x->-(-1)^(23/43)},{x->(-1)^(23/43)},{x->-(-1)^(70/129)},{x->(-1)^(70/129)},{x->-(-\n"
            + "1)^(71/129)},{x->(-1)^(71/129)},{x->-(-1)^(24/43)},{x->(-1)^(24/43)},{x->-(-1)^(\n"
            + "73/129)},{x->(-1)^(73/129)},{x->-(-1)^(74/129)},{x->(-1)^(74/129)},{x->-(-1)^(25/\n"
            + "43)},{x->(-1)^(25/43)},{x->-(-1)^(76/129)},{x->(-1)^(76/129)},{x->-(-1)^(77/129)},{x->(-\n"
            + "1)^(77/129)},{x->-(-1)^(26/43)},{x->(-1)^(26/43)},{x->-(-1)^(79/129)},{x->(-1)^(\n"
            + "79/129)},{x->-(-1)^(80/129)},{x->(-1)^(80/129)},{x->-(-1)^(27/43)},{x->(-1)^(27/\n"
            + "43)},{x->-(-1)^(82/129)},{x->(-1)^(82/129)},{x->-(-1)^(83/129)},{x->(-1)^(83/129)},{x->-(-\n"
            + "1)^(28/43)},{x->(-1)^(28/43)},{x->-(-1)^(85/129)},{x->(-1)^(85/129)},{x->-(-1)^(\n"
            + "2/3)},{x->(-1)^(2/3)},{x->-(-1)^(29/43)},{x->(-1)^(29/43)},{x->-(-1)^(88/129)},{x->(-\n"
            + "1)^(88/129)},{x->-(-1)^(89/129)},{x->(-1)^(89/129)},{x->-(-1)^(30/43)},{x->(-1)^(\n"
            + "30/43)},{x->-(-1)^(91/129)},{x->(-1)^(91/129)},{x->-(-1)^(92/129)},{x->(-1)^(92/\n"
            + "129)},{x->-(-1)^(31/43)},{x->(-1)^(31/43)},{x->-(-1)^(94/129)},{x->(-1)^(94/129)},{x->-(-\n"
            + "1)^(95/129)},{x->(-1)^(95/129)},{x->-(-1)^(32/43)},{x->(-1)^(32/43)},{x->-(-1)^(\n"
            + "97/129)},{x->(-1)^(97/129)},{x->-(-1)^(98/129)},{x->(-1)^(98/129)},{x->-(-1)^(33/\n"
            + "43)},{x->(-1)^(33/43)},{x->-(-1)^(100/129)},{x->(-1)^(100/129)},{x->-(-1)^(101/\n"
            + "129)},{x->(-1)^(101/129)},{x->-(-1)^(34/43)},{x->(-1)^(34/43)},{x->-(-1)^(103/\n"
            + "129)},{x->(-1)^(103/129)},{x->-(-1)^(104/129)},{x->(-1)^(104/129)},{x->-(-1)^(35/\n"
            + "43)},{x->(-1)^(35/43)},{x->-(-1)^(106/129)},{x->(-1)^(106/129)},{x->-(-1)^(107/\n"
            + "129)},{x->(-1)^(107/129)},{x->-(-1)^(36/43)},{x->(-1)^(36/43)},{x->-(-1)^(109/\n"
            + "129)},{x->(-1)^(109/129)},{x->-(-1)^(110/129)},{x->(-1)^(110/129)},{x->-(-1)^(37/\n"
            + "43)},{x->(-1)^(37/43)},{x->-(-1)^(112/129)},{x->(-1)^(112/129)},{x->-(-1)^(113/\n"
            + "129)},{x->(-1)^(113/129)},{x->-(-1)^(38/43)},{x->(-1)^(38/43)},{x->-(-1)^(115/\n"
            + "129)},{x->(-1)^(115/129)},{x->-(-1)^(116/129)},{x->(-1)^(116/129)},{x->-(-1)^(39/\n"
            + "43)},{x->(-1)^(39/43)},{x->-(-1)^(118/129)},{x->(-1)^(118/129)},{x->-(-1)^(119/\n"
            + "129)},{x->(-1)^(119/129)},{x->-(-1)^(40/43)},{x->(-1)^(40/43)},{x->-(-1)^(121/\n"
            + "129)},{x->(-1)^(121/129)},{x->-(-1)^(122/129)},{x->(-1)^(122/129)},{x->-(-1)^(41/\n"
            + "43)},{x->(-1)^(41/43)},{x->-(-1)^(124/129)},{x->(-1)^(124/129)},{x->-(-1)^(125/\n"
            + "129)},{x->(-1)^(125/129)},{x->-(-1)^(42/43)},{x->(-1)^(42/43)},{x->-(-1)^(127/\n"
            + "129)},{x->(-1)^(127/129)},{x->-(-1)^(128/129)},{x->(-1)^(128/129)}}");

    check("Solve(y==x+((1)/(x)),y)", //
        "{{y->(1+x^2)/x}}");
    check("Solve(y==((1-x)^(1/(2)))+((x+3)^(1/(2))),y)", //
        "{{y->Sqrt(1-x)+Sqrt(3+x)}}");
  }

  public void testSolveIssue413() {
    // eval quiet without message
    check("Solve({8.0*E - 9 == x0/x1, x0==x1^4.0},{x0, x1}) ", //
        "{{x0->29.77443,x1->2.33594},{x0->-14.88721+I*(-25.78541),x1->-1.16797+I*(-2.02298)},{x0->-14.88721+I*25.78541,x1->-1.16797+I*2.02298}}");
    // eval quiet without message
    check("Solve({x0^2.0*Sin(x1)==5.0,x1^3.0*Cos(x0)==5.0},{x0,x1}) ", //
        "Solve({x0^2.0*Sin(x1)==5.0,x1^3.0*Cos(x0)==5.0},{x0,x1})");

    // // ifun message will be printed:
    check("Solve({Sin(x0)==5.0},{x0})", //
        "{{x0->1.5708+I*(-2.29243)}}");

  }

  public void testSolveCircleHyperbolic() {
    check("Eliminate(-x-y+x*y==27,x)", //
        "True");
    check("Solve({-x+x^2-y+y^2==814,-x-y+x*y==27},{x,y})", //
        "{{y->2,x->29},{y->29,x->2},{y->-14-Sqrt(197),x->-14+Sqrt(197)},{y->-14+Sqrt(197),x->-\n"
            + "14-Sqrt(197)}}");
  }

  public void testSolveConditionalExpression() {
    check("Solve(E^x-2==0,x,Reals)", //
        "{{x->Log(2)}}");
    check("Solve(E^x-2==0,x)", //
        "{{x->ConditionalExpression(I*2*Pi*C(1)+Log(2),C(1)∈Integers)}}");
  }

  public void testSolveHO1() {
    // https: //
    // www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
    // TODO improve result by avoiding GCD 1/2
    check("Solve(E^(3*x)-4*E^x+3*E^(-x)==0,x)", //
        "{{x->ConditionalExpression(I*2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(I*Pi+\n"
            + "I*2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(I*2*Pi*C(1)+Log(3)/2,C(1)∈Integers)},{x->ConditionalExpression(I*Pi+\n"
            + "I*2*Pi*C(1)+Log(3)/2,C(1)∈Integers)}}");
  }

  public void testSolveHO2() {
    // https: //
    // www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
    check("Solve(4^(2*x+1)*5^(x-2)-6^(1-x)==0,x)", //
        "{{x->(Log(2)-Log(75))/(-5*Log(2)-Log(15))}}");
  }

  public void testSolveHO3() {
    // TODO return unevaluated expr
    // https: //
    // www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
    check("Solve(4^(1+2*x)/5^(2-x)-6^(1-x)==-42,x)", //
        "{}");
    // "Solve(4^(1+2*x)/5^(2-x)-6^(1-x)==-42,x)");
  }

  public void testSolveHO4() {
    // https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf

    // TODO check result
    check(
        "Solve(Cos(x)+ Cos(x)^3 + Cos(x)^5 - 3*Cos(x)*Sin(x)^2 - 10*Cos(x)^3*Sin(x)^2 + 5*Cos(x)*Sin(x)^4 ==0,x)", //
        "{{x->Pi/2},{x->ConditionalExpression(Pi/8+2*Pi*C(1),C(1)∈Integers)}}");
  }

  public void testSolveHO5() {
    // https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf

    // TODO check result
    check(
        "Solve(1/(E^(I*x)*2) + E^(I*x)/2 + 1/2/E^(3*I*x) + (1/2)*E^(3*I*x) + 1/2/E^(5*I*x) + (1/2)*E^(5*I*x) ==0,x)", //
        "{{x->ConditionalExpression(-5/6*Pi+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(-\n"
            + "2/3*Pi+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(-Pi/2+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(-Pi/\n"
            + "3+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(-Pi/6+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(Pi/\n"
            + "6+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(Pi/3+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(Pi/\n"
            + "2+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(2/3*Pi+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(\n"
            + "5/6*Pi+2*Pi*C(1),C(1)∈Integers)}}");
  }

  public void testSolveHO6() {
    // check("TrigToExp(Cos(x) + Cos(3*x) + Cos(5*x) )", //
    // "1/(2*E^(I*5*x))+1/(2*E^(I*3*x))+1/(2*E^(I*x))+E^(I*x)/2+E^(I*3*x)/2+E^(I*5*x)/2");
    //
    // check("Factor(1/(2*E^(I*5*x))+1/(2*E^(I*3*x))+1/(2*E^(I*x))+E^(I*x)/2+E^(I*3*x)/2+E^(I*5*x)/2)",
    // //
    // "");
    // https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
    check("Solve(Cos(x) + Cos(3*x) + Cos(5*x) == 0,x)", //
        "{{x->ConditionalExpression(-5/6*Pi+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(-\n"
            + "2/3*Pi+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(-Pi/2+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(-Pi/\n"
            + "3+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(-Pi/6+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(Pi/\n"
            + "6+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(Pi/3+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(Pi/\n"
            + "2+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(2/3*Pi+2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(\n"
            + "5/6*Pi+2*Pi*C(1),C(1)∈Integers)}}");
  }

  public void testSolveHO7() {
    //
    https: // www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
    check("Solve(Cos(x) + Tan(3*x) + Cos(5*x) ==0,x)", //
        "Solve(Cos(x)+Cos(5*x)+Tan(3*x)==0,x)");
  }

  public void testSolveIssue535() {
    check("Solve({{-1+x+2*y}==0,{3+x+4*y}==0},{x,y})", //
        "{{x->5,y->-2}}");
    check("Solve({{1,2},{1,4}}.{{x},{y}}=={{1},{-3}},{x,y})", //
        "{{x->5,y->-2}}");
  }

  public void testSolveIssue538() {
    // TODO issue 538
    check("Solve(-3+x^(1/3)+x^2==0,x)", //
        "{{x->-1.56443+I*(-0.310515)},{x->-1.56443+I*0.310515},{x->1.37413}}");
    check("Solve(-3+x+x^6==0,x)", //
        "{{x->-1.27391},{x->-0.567906+I*(-1.10869)},{x->-0.567906+I*1.10869},{x->0.648981+I*(-0.971557)},{x->0.648981+I*0.971557},{x->1.11176}}");

  }

  public void testSolveIssue539() {
    check("Solve(Conjugate(x)==5-I*20,x)", //
        "{{x->5+I*20}}");
  }

  public void testSystemSqrtSqrt() {
    check("Solve(Sqrt(x+2)-Sqrt(x+3)==1,x)", //
        "{}");
  }

  public void testIssue685() {
    check("Solve({a*b + c == 2020, a + b*c == 2021},{a,b,c},Integers)", //
        "{{a->673,b->2,c->674},{a->2021,b->0,c->2020}}");
  }

  public void testSystem805() {
    check("Solve({x^2==4,x+20==10},x)", "{}");
    check("Solve(4*x^(-2)-1==0,x)", //
        "{{x->-2},{x->2}}");

    check("Solve(x^2==a^2,x)", "{{x->-a},{x->a}}");
    check("Solve((x^2-1)/(x-1)==0,x)", "{{x->-1}}");

    // LinearSolve[{{1,1,1},{1,1,-1},{1,-1,-1}},{100,50,10}]
    // Fraction[][] testData = { { new Fraction(1), new Fraction(1), new
    // Fraction(1) },
    // { new Fraction(1), new Fraction(1), new Fraction(-1) }, { new
    // Fraction(1), new Fraction(-1), new Fraction(-1) } };
    // Fraction[] testVector = { new Fraction(100), new Fraction(50), new
    // Fraction(10) };
    // FieldMatrix<Fraction> aMatrix = new Array2DRowFieldMatrix(testData);
    // FieldVector<Fraction> bVector = new
    // ArrayFieldVector<Fraction>(testVector);
    // try {
    //
    // final FieldLUDecomposition<Fraction> lu = new
    // FieldLUDecomposition<Fraction>(aMatrix);
    //
    // FieldDecompositionSolver<Fraction> fds = lu.getSolver();
    // // lu.getL();
    // FieldVector<Fraction> xVector = fds.solve(bVector);
    // assertEquals("", xVector.toString());
    //
    // } catch (final ClassCastException e) {
    // if (Config.SHOW_STACKTRACE) {
    // e.printStackTrace();
    // }
    // } catch (final IndexOutOfBoundsException e) {
    // if (Config.SHOW_STACKTRACE) {
    // e.printStackTrace();
    // }
    // }

    // check("solve({x + y + z == 100,x + y - z == 50,x - y - z ==
    // 10},{x,y,z})", "{{x->55,y->20,z->25}}");
    // check("Solve(y+x/a==0,y)", "{{y->-a^(-1)*x}}");
    // check("Solve(((x-8.5)^2)+(y+9.5)^2==1.4,x)",
    // "{{x->0.5*(-4.0*y^2.0-76.0*y-355.4)^0.5+8.5},{x->-0.5*(-4.0*y^2.0-76.0*y-355.4)^0.5+8.5}}");
    // check("Solve(((x-8.5556577)^2)+(y+9.551234)^2==14/10,x)",
    // "{{x->0.5*(-4.0*y^2.0-76.409872*y-359.30428369102395)^0.5+8.5556577},{x->-0.5*(-4.0*y^2.0-76.409872*y-359.30428369102395)^0.5+8.5556577}}");
    //
    // check("Solve(a+2x==0,x)", "{{x->(-1/2)*a}}");
    // check("Together((x^2-1)/(x-1))", "x+1");

    // check("Solve(x*(-0.006*x^2.0+1.0)^2.0-0.1*x==7.217,x)",
    // "{{x->16.955857433561537},{x->-14.046984987941926+I*(-3.7076756332964123)},{x->-14.046984987941926+I*3.7076783744216715},{x->5.569057466623865+I*(-5.000248815113639)},{x->5.569057466623865+I*5.000251556238898}}");

    // issue #68
    check("Solve(x^(1/2)==0,x)", "{{x->0}}");
    check("solve(sqrt(112*x)==0,x)", "{{x->0}}");
    check("Solve(7^(1/2)*x^(1/2)==0,x)", "{{x->0}}");

    check("Solve({x+y==1, x-y==0}, {x,y})", "{{x->1/2,y->1/2}}");
    check("Solve(x*(-0.006*x^2.0+1.0)^2.0-0.1*x==7.217,x)",
        "{{x->-14.04698+I*(-3.70768)},{x->-14.04698+I*3.70768},{x->5.56906+I*(-5.00025)},{x->5.56906+I*5.00025},{x->16.95586}}");

    checkNumeric("CoefficientList(x*(-0.006*x^2.0+1.0)^2.0-0.1*x-7.217,x)",
        "{-7.217,0.9,0,-0.012,0,3.6E-5}");

    checkNumeric("Solve(2.5*x^2+1650==0,x)", //
        "{{x->I*(-25.69046515733026)},{x->I*25.69046515733026}}");
    checkNumeric("Solve(x*(x^2+1)^2==7,x)",
        "{{x->-0.9784917834108953+I*(-1.038932735856145)},{x->-0.9784917834108953+I*1.038932735856145},{x->0.38213058392542043+I*(-1.6538990550344321)},{x->0.38213058392542043+I*1.6538990550344321},{x->1.1927223989709494}}");
    checkNumeric("NSolve(x*(x^2+1)^2==7,x)",
        "{{x->-0.9784917834108953+I*(-1.038932735856145)},{x->-0.9784917834108953+I*1.038932735856145},{x->0.38213058392542043+I*(-1.6538990550344321)},{x->0.38213058392542043+I*1.6538990550344321},{x->1.1927223989709494}}");
    check("Solve(x^2==a^2,x)", "{{x->-a},{x->a}}");
    check("Solve(4*x^(-2)-1==0,x)", //
        "{{x->-2},{x->2}}");
    check("Solve((x^2-1)/(x-1)==0,x)", //
        "{{x->-1}}");

    check("Solve(x+5==a,x)", "{{x->-5+a}}");
    check("Solve(x+5==10,x)", "{{x->5}}");
    check("Solve(x^2==a,x)", "{{x->-Sqrt(a)},{x->Sqrt(a)}}");
    check("Solve(x^2+b*c*x+3==0, x)", //
        "{{x->1/2*(-b*c-Sqrt(-12+b^2*c^2))},{x->1/2*(-b*c+Sqrt(-12+b^2*c^2))}}");
    check("Solve({x+2*y==10,3*x+y==20},{x,y})", "{{x->6,y->2}}");
    check("Solve(x^2==0,{x,y,z})", "{{x->0}}");
    check("Solve(x^2==0,x)", "{{x->0}}");
    check("Solve(x^2==4,x)", "{{x->-2},{x->2}}");
    check("Solve({x^2==4,x+y==10},{x,y})", "{{x->-2,y->12},{x->2,y->8}}");

    check("Solve({x^2==4,x+y^2==6},{x,y})",
        "{{x->-2,y->-2*Sqrt(2)},{x->-2,y->2*Sqrt(2)},{x->2,y->-2},{x->2,y->2}}");
    check("Solve({x^2==4,x+y^2==6,x+y^2+z^2==24},{x,y,z})",
        "{{x->-2,y->-2*Sqrt(2),z->-3*Sqrt(2)},{x->-2,y->-2*Sqrt(2),z->3*Sqrt(2)},{x->-2,y->\n"
            + "2*Sqrt(2),z->-3*Sqrt(2)},{x->-2,y->2*Sqrt(2),z->3*Sqrt(2)},{x->2,y->-2,z->-3*Sqrt(\n"
            + "2)},{x->2,y->-2,z->3*Sqrt(2)},{x->2,y->2,z->-3*Sqrt(2)},{x->2,y->2,z->3*Sqrt(2)}}");
  }

  public void testSolveLinearEquations() {
    // https://github.com/asc-community/AngouriMath/issues/608
    check("Solve({2 * x1 * (-66) - 6 * x2 + 24 * x3 - 12 * x4 + 270 == 0,\n"//
        + "-6 * x1 - 2 * x2 * 74 - 8 * x3 + 4 * x4 - 440 == 0,\n"//
        + "24 * x1 - 8 * x2 - 2 * x3 * 59 - 16 * x4 - 190 == 0,\n"//
        + "-12 * x1 + 4 * x2 - 16 * x3 - 2 * x4 * 71 + 20 == 0},"//
        + "{x1,x2,x3,x4})", //
        "{{x1->2,x2->-3,x3->-1,x4->0}}");
  }

  public void testSolveIssue731() {
    // issue #731
    // check("Eliminate(1-2*x+Sqrt(-15*x+4*x^2)==0,x)", //
    // "");
    check("Solve((4*x^2-15*x)^(1/2) - 2*x == -1, x)", //
        "{}");
  }

  public void testSolveNumericMode() {
    check("Solve( 1.4==9-16.4*E^((-20.0*10^-6)/(18000*x)) , x)", //
        "{{x->1.44463*10^-9}}");
  }

  public void testSolveIssue746() {
    check("Solve({a==b+c, b==3, c==d*e, d==3, e==5}, a)", //
        "Solve({a==b+c,b==3,c==d*e,d==3,e==5},a)");
    check("Solve({a==b+c, b==3, c==d*e, d==3, e==5}, {a,b,c,d,e})", //
        "{{a->18,b->3,c->15,d->3,e->5}}");
  }

  public void testFactorQuadratic001() {
    check("Factor(4x^2+20x+16)", //
        "4*(1+x)*(4+x)");
  }


  // public void testSolveFindRoot() {
  // // multivariate FindRoot cases
  // check("Solve({2*x1+x2==E^(-x1), -x1+2*x2==E^(-x2)},{x1,x2})", //
  // "{x1->0.197594,x2->0.425514}");
  // check(
  // "Solve({Exp(-Exp(-(x1+x2)))-x2*(1+x1^2), x1*Cos(x2)+x2*Sin(x1)-0.5},{x1,x2})", //
  // "{x1->0.353247,x2->0.606082}");
  // check("Solve({Exp(x - 2) == y, y^2 == x}, {x,y})", //
  // "{x->0.019026,y->0.137935}");
  // check("Solve({y == E^x, x + y == 2}, {x,y})", //
  // "{x->0.442854,y->1.55715}");
  // check("Solve({Sin(x + y), Cos(x - y), x^2 + y^2 - z}, {x,y,z})", //
  // "{x->0.785398,y->-0.785398,z->1.2337}");
  // }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
