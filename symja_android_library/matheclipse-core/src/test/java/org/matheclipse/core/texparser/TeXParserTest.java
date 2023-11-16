package org.matheclipse.core.texparser;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.tex.TeXParser;
import org.matheclipse.core.interfaces.IExpr;
import junit.framework.TestCase;

import static org.junit.Assert.assertEquals;


@RunWith(JUnit4.class)
public class TeXParserTest  {
  TeXParser texConverter;

  @Test
  public void test001() {
    check("x=\\frac{-b + \\sqrt{b^2-4ac}}{2a}\\times\\sqrt{89}",
        "x==(Sqrt(89)*(-b+Sqrt(b^2-4*a*c)))/(2*a)");
  }

  @Test
  public void test1() {
    check("2 \\cdot 3 ", "2*3");
  }

  @Test
  public void test2() {
    check("\\int _ { a } ^ { b } x ^ { 2 } d x =" + " \\frac { 1 } { 3 } x ^ { 3 }",
        "Integrate(x^2,{x,a,b})==x^3/3");
  }

  @Test
  public void test3() {
    check("12 + 5 x - 8 = 12 x - 10", "-8+12+5*x==-10+12*x");
  }

  @Test
  public void test4() {
    check("\\operatorname { det } \\left| \\begin{array} { l l } { 4 } & { 5"
        + " } \\\\ { 7 } & { 2 } \\end{array} \\right|", "Det({{4,5},{7,2}})");
  }

  @Test
  public void test13() {
    check("\\operatorname { det } \\left| \\begin{array} { l l } { 4 } & { 5"
        + " } \\\\ { 7 } & { 2 } \\end{array} \\right|", //
        "Det({{4,5},{7,2}})");
  }

  @Test
  public void test5() {
    check("\\int _ { 0 } ^ { 1 } x e ^ { 2 x - 1 } d x", "Integrate(x/E^(1-2*x),{x,0,1})");
  }

  @Test
  public void test10() {
    check("\\int _ { 0 } ^ { 1 } x d x", "Integrate(x,{x,0,1})");
  }

  @Test
  public void test6() {
    check(
        "f ( x ) = 2 \\operatorname { cos } x ( \\operatorname { sin } x + \\operatorname { cos } x )",
        "f(x)==2*Cos(x(Sin(x+Cos(x))))");
  }

  @Test
  public void test7() {
    check("h ( x ) = \\frac { x ^ { 2 } - 2 x - 15 } { x + 3 }", "h(x)==(-15+-2*x+x^2)/(3+x)");
  }

  @Test
  public void test8() {
    check("[ \\frac { n ( n + 1 ) } { 2 } ] ^ { 2 }", "(n(1+n)*1/2)^2");
  }

  // @Test
  // public void test9() {
  // check("8 \\longdiv { 7 2 0 0 }", "8*Sqrt(7200)");
  // }

  // @Test
  // public void testSqrt10() {
  // check("x + 6 \\longdiv { x ^ { 3 } - x ^ { 2 } - 4 7 x - 3 0 }",
  // "x+(6*sqrt(x^3-x^2-(47*x)-30))");
  // }

  @Test
  public void testArray11() {
    check("\\left[ \\begin{array} { c c } { a } & { 5 b } \\\\ { b } & { a } \\end{array} \\right]",
        "{{a,5*b},{b,a}}");
  }

  // @Test
  // public void testArray13() {
  // check(
  // "\\left. \\begin{array} { l } { 2 ^ { 2 - 3 \\sqrt { 5 } }"
  // + " \\cdot 8 ^ { \\sqrt { 5 } } } \\\\"
  // + " { 3 ^ { 1 + 2 \\sqrt [ 3 ] { 2 } } : 9 ^ { \\sqrt [ 3 ] { 2 } } }"
  // + " \\\\ { \\frac { 10 ^ { 2 + \\sqrt { 7 } } } { 2 ^ { 2 + \\sqrt { 7 } } "
  // + "\\cdot 5 ^ { 1 + \\sqrt { 7 } } } }"
  // + " \\\\ { ( 4 ^ { 2 \\sqrt { 3 } } - 4 ^ { \\sqrt { 3 } - 1 } ) "
  // + "\\cdot 2 ^ { - 2 \\sqrt { 3 } } } \\end{array} \\right.",
  // "{{2^(2-(3*sqrt(5)))*8^sqrt(5)},{3^(1+(2*surd(2,3)))/9^surd(2,3)},{10^(2+sqrt(7))/(2^(2+sqrt(7))*5^(1+sqrt(7)))},{(4^(2*sqrt(3))-4^(sqrt(3)-1))*2^(-2*sqrt(3))}}");
  // }

  // @Test
  // public void testArray14() {
  // check(
  // "\\left. \\begin{array} { l } { " + "( \\frac { 1 } { 16 } ) ^ { - \\frac { 3 } { 4"
  // + " } } + 810000 ^ { 0,25 } - ( 7 \\frac { 19 } { 32 } ) ^ { \\frac { 1 } {"
  // + " 5 } } } " + "\\\\ " + "{ ( 0.001 ) ^ { - \\frac { 1 } { 3 } } - 2 ^ { - 2 } \\cdot "
  // + "64 ^ { \\frac { 2 } { 3 } } - 8 ^ { - 1 \\frac { 1 } { 3 } } } " + "\\\\" + " { 27 ^"
  // + " { \\frac { 2 } { 3 } } - ( - 2 ) ^ { - 2 } + ( 3 \\frac { 3 } { 8 } ) ^ "
  // + "{ - \\frac { 1 } { 3 } } } " + "\\\\" + " { ( - 0.5 ) ^ { - 4 } - 625 ^ { 0.25 } -"
  // + " ( 2 \\frac { 1 } { 4 } ) ^ { - 1 \\frac { 1 } { 2 } } } " + "\\end{array} \\right.",
  //
  // "{{(1/16)^(-(3/4))+810000^rationalize(0.25)-surd((7*(19/32)),5)},{rationalize(0.001)^(-(1/3))-2^(-2)*surd(64^2,3)-8^((-1)*(1/3))},{surd(27^2,3)-(-2)^(-2)+(3*(3/8))^(-(1/3))},{(-rationalize(0.5))^(-4)-625^rationalize(0.25)-(2*(1/4))^((-1)*(1/2))}}");
  // }

  // @Test
  // public void testArray15() {
  // check(
  // "\\left. \\begin{array} { l } { ( \\frac { 1 } { 16 } ) ^ { - \\frac { 3 } { "
  // + "4 } } + 810000 ^ { 0,25 } - ( 7 \\frac { 19 } { 32 } ) ^ { \\frac { 1 "
  // + "} { 5 } } } \\\\ { ( 0,001 ) ^ { - \\frac { 1 } { 3 } } - 2 ^ { - "
  // + "2 } \\cdot 64 ^ { \\frac { 2 } { 3 } } - 8 ^ { - 1 \\frac { 1 } { 3 } } "
  // + "} \\\\ { 27 ^ { \\frac { 2 } { 3 } } - ( - 2 ) ^ { - 2 } + ( 3 \\frac { "
  // + "3 } { 8 } ) ^ { - \\frac { 1 } { 3 } } } \\\\ { ( - 0,5 ) ^ { - 4 } - 6"
  // + "25 ^ { 0,25 } - ( 2 \\frac { 1 } { 4 } ) ^ { - 1 \\frac { 1 } { 2 } } "
  // + "} \\end{array} \\right.",
  //
  // "{{(1/16)^(-(3/4))+810000^rationalize(0.25)-surd((7*(19/32)),5)},{rationalize(0.001)^(-(1/3))-2^(-2)*surd(64^2,3)-8^((-1)*(1/3))},{surd(27^2,3)-(-2)^(-2)+(3*(3/8))^(-(1/3))},{(-rationalize(0.5))^(-4)-625^rationalize(0.25)-(2*(1/4))^((-1)*(1/2))}}");
  // }

  // @Test
  // public void testArray16() {
  // check(
  // "\\left. \\begin{array} { l } { \\frac { a ^ { \\frac { 4 } { 3 } } ( a ^ { - \\f"
  // + "rac { 1 } { 3 } } + a ^ { \\frac { 2 } { 3 } } ) } { a ^ { \\frac { 1 } { 4"
  // + " } } ( a ^ { \\frac { 3 } { 4 } } + a ^ { - \\frac { 1 } { 4 } } ) } } \\\\"
  // + " { \\frac { a ^ { \\frac { 1 } { 3 } } \\sqrt { b } + b ^ { - \\frac { 1 } "
  // + "{ 4 } } } { \\sqrt [ 6 ] { a } + \\sqrt [ 6 ] { b } } } \\\\ { ( \\sqrt [ 3"
  // + " ] { a } + \\sqrt [ 3 ] { b } ) ( a ^ { \\frac { 2 } { 3 } } + b ^ { \\frac"
  // + " { 2 } { 3 } } - \\sqrt [ 3 ] { a b } ) } \\\\ { ( a ^ { \\frac { 1 } { 3 }"
  // + " } + b ^ { \\frac { 1 } { 3 } } ) : ( 2 + \\sqrt [ 3 ] { \\frac { a } { b }"
  // + " } + \\sqrt [ 3 ] { \\frac { b } { a } } ) } \\end{array} \\right.",
  // "{{(surd(a^4,3)*(a^(-(1/3))+surd(a^2,3)))/(((a)^(1/(4)))*(((a^3)^(1/(4)))+a^(-(1/4))))},{((surd(a,3)*sqrt(b))+b^(-(1/4)))/(((a)^(1/(6)))+((b)^(1/(6))))},{(surd(a,3)+surd(b,3))*(surd(a^2,3)+surd(b^2,3)-surd((a*b),3))},{(surd(a,3)+surd(b,3))/(2+surd(a/b,3)+surd(b/a,3))}}");
  // }

  @Test
  public void testArray17() {
    check("\\left. " + "\\begin{array} " + "{ l }"
        + " { \\int _ { 0 } ^ { 1 } ( y ^ { 3 } + 3 y ^ { 2 } - 2 ) d y } \\\\ "
        + "{ \\int _ { 4 } ^ { 4 } ( t + \\frac { 1 } { \\sqrt { t } } - \\frac { 1 } { t ^ { 2 } } ) d t }"
        + " \\end{array}" + " \\right.",
        "{Integrate(-2+3*y^2+y^3,{y,0,1}),Integrate(1/Sqrt(t)+t-1/t^2,{t,4,4})}");
  }

  @Test
  public void test20() {
    check("\\int _ { 0 } ^ { 1 } ( y ^ { 3 } + 3 y ^ { 2 } - 2 ) d y ",
        "Integrate(-2+3*y^2+y^3,{y,0,1})");
    check(
        "\\int _ { 4 } ^ { 4 } ( t + \\frac { 1 } { \\sqrt { t } } - \\frac { 1 } { t ^ { 2 } } ) d t",
        "Integrate(1/Sqrt(t)+t-1/t^2,{t,4,4})");
  }

  @Test
  public void testSum2() {
    check("\\sum_{n=1}^{\\infty} 2^{-n} = 1", "Sum(2^(-n),{n,1,Infinity})==1");
  }

  @Test
  public void testProd2() {
    check("\\prod_{i=a}^{b} f(i)", "Product(f(i),{i,a,b})");
  }

  @Test
  public void testLim20() {
    check("\\lim_{x\\to\\infty} f(x)", //
        "Limit(f(x),x->Infinity)");
  }

  @Test
  public void testLim21() {
    check(
        "\\operatorname { lim } _ { n \\rightarrow 0 } \\frac { ( - 3 ) ^ { n } + 2.5 ^ { n } } { 1 - 5 ^ { n } }",
        "Limit(((-3)^n+2.5^n)/(1-5^n),n->0)");
  }

  @Test
  public void testLim22() {
    check(
        "\\operatorname { lim } _ { n \\rightarrow 0 } ( \\sqrt { n ^ { 2 } + 2 n + 1 } - \\sqrt { n ^ { 2 } + n - 1 } )",
        "Limit(Sqrt(1+2*n+n^2)-Sqrt(-1+n+n^2),n->0)");
  }

  @Test
  public void testLim23() {
    check("\\operatorname { lim } _ { x \\rightarrow - 2 } \\frac { x + 5 } { x ^ { 2 } + x - 3 }",
        "Limit((5+x)/(-3+x+x^2),x->-2)");
  }

  @Test
  public void testLim24() {
    check("\\operatorname { lim } _ { x \\rightarrow 3  } \\sqrt { x ^ { 2 } + 8 x + 3 }",
        "Limit(Sqrt(3+8*x+x^2),x->3)");
  }

  @Test
  public void testLim25() {
    check(
        "\\operatorname { lim } _ { x \\rightarrow + \\infty } ( x ^ { 3 } + 2 x ^ { 2 } \\sqrt { x } - 1 )",
        "Limit(-1+2*Sqrt(x)*x^2+x^3,x->Infinity)");
  }

  @Test
  public void testLim26() {
    check(
        "\\operatorname { lim } _ { x \\rightarrow - 1 } "
            + "\\frac { 2 x ^ { 3 } - 5 x - 4 } { ( x + 1 ) ^ { 2 } }",
        "Limit((-4+-5*x+2*x^3)/(1+x)^2,x->-1)");
  }

  @Test
  public void testLim27() {
    check(
        "\\operatorname { lim } _ { x \\rightarrow 0 } "
            + "\\frac { \\sqrt { x ^ { 2 } + 1 } - 1 } { 4 - \\sqrt { x ^ { 2 } + 16 } }",
        "Limit((-1+Sqrt(1+x^2))/(4-Sqrt(16+x^2)),x->0)");
  }

  @Test
  public void testLim28() {
    check(
        "\\operatorname { lim } _ { x \\rightarrow 2 } ( \\frac { 1 } { x ^ { 2 } - 4 } - \\frac { 1 } { x - 2 } )",
        "Limit(-1/(-2+x)+1/(-4+x^2),x->2)");
  }

  @Test
  public void testSum29() {
    check("\\sum _ { n = 1 } ^ { 9 } ( - 2 ) ^ { n - 1 }", "Sum((-2)^(-1+n),{n,1,9})");
  }

  @Test
  public void testSum30() {
    check("\\sum _ { k = 0 } ^ { 10 } ( - 7 + 6 k )", "Sum(-7+6*k,{k,0,10})");
  }

  @Test
  public void test31() {
    check("y = \\frac { x + 1 } { x - 1 }", "y==(1+x)/(-1+x)");
  }

  @Test
  public void test32() {
    check("y = - x ^ { 4 } + 4 x ^ { 2 } + 1", "y==1+4*x^2-x^4");
  }

  @Test
  public void test33() {
    check("y = x ^ { 3 } + 2 x + 1", "y==1+2*x+x^3");
  }

  @Test
  public void test34() {
    check("y = \\frac { x ^ { 2 } + x + 2 } { x - 1 }", "y==(2+x+x^2)/(-1+x)");
  }

  @Test
  public void test35() {
    check("y = \\frac { 4 + m x } { x + m }", "y==(4+m*x)/(m+x)");
  }

  @Test
  public void test36() {
    check("y = \\frac { 1 } { 3 } x ^ { 3 } - 2 m x ^ { 2 } + ( m + 3 ) x - 5 + m",
        "y==-5+m+(3+m)*x-2*m*x^2+x^3/3");
  }

  @Test
  public void test37() {
    check("f ( x ) = x ^ { 3 } - 3 m x ^ { 2 } + 3 ( m ^ { 2 } - 1 ) x + 2016",
        "f(x)==2016+3*(-1+m^2)*x-3*m*x^2+x^3");
  }

  @Test
  public void test38() {
    check("\\operatorname { lim } _ { x \\rightarrow + \\infty } f ( x ) = 2",
        "Limit(f(x),x->Infinity)==2");
  }

  @Test
  public void test39() {
    check("y = \\frac { ( a - 2 b ) x ^ { 2 } + b x + 1 } { x ^ { 2 } + x - b }\n",
        "y==(1+b*x+(a-2*b)*x^2)/(-b+x+x^2)");
  }

  @Test
  public void test40() {
    check(
        "A = \\frac { a ^ { \\frac { 1 } { 3 } } \\sqrt { b } + b ^ { \\frac { 1 } { 3 } } \\sqrt { a } } { \\sqrt [ 6 ] { a } + \\sqrt [ 6 ] { b } }",
        "A==(Sqrt(a)*b^(1/3)+a^(1/3)*Sqrt(b))/(a^(1/6)+b^(1/6))");
  }

  @Test
  public void test41() {
    check(
        "M = 64 ^ { \\frac { 1 } { 2 } \\operatorname { log } _ { 2 } 10 } + 9 ^ { \\frac { 1 } { \\operatorname { log } _ { 6 } 3 } }",
        "M==9^(1/Log(6,3))+64^(Log(2,10)/2)");
  }

  @Test
  public void testLog() {
    check("\\operatorname { log } _ { 2 } 10", "Log(2,10)");
  }


  @Test
  public void test42() {
    check("\\operatorname { log } _ { 2 } ( 3 x - 2 ) = 4", "Log(2,-2+3*x)==4");
  }

  @Test
  public void test43() {
    check("2 ^ { x - 1 } = \\frac { 1 } { 2 }", "2^(-1+x)==1/2");
  }

  @Test
  public void testLog44() {
    check("\\operatorname { log } ( x + 2 ) = \\operatorname { log } x ^ { 2 }",
        "Log10(2+x)==Log10(x^2)");
  }

  @Test
  public void test45() {
    check("\\int 5 x ^ { 6 } d x", "Integrate(5*x^6,x)");
  }

  @Test
  public void test46() {
    check("\\int \\sqrt { 1 - 3 x } d x", "Integrate(Sqrt(1-3*x),x)");
  }

  @Test
  public void test47() {
    check(
        "\\int \\frac { 2 x - 1 } { x ^ { 2 } - 6 x + 9 } d x = a "
            + "\\operatorname { ln } \\left| x - 3 \\right| + \\frac { b } { x - 3 } + C",
        "Integrate((-1+2*x)/(9-6*x+x^2),x)==C+b/(-3+x)+a*Log(Abs(-3+x))");
  }

  @Test
  public void test12() {
    check(
        "\\int \\frac { 2 x - 1 } { x ^ { 2 } - 6 x + 9 } d x = a "
            + "\\operatorname { ln } ( x - 3 ) + \\frac { b } { x - 3 } + C",
        "Integrate((-1+2*x)/(9-6*x+x^2),x)==C+b/(-3+x)+a*Log(-3+x)");
  }

  @Test
  public void test48() {
    check("\\int _ { 0 } ^ { 2 } 2 e ^ { 2 x } d x", //
        "Integrate(2*E^(2*x),{x,0,2})");
  }

  @Test
  public void test49() {
    check("\\int _ { 0 } ^ { m } ( 2 x + 5 ) \\cdot d x = 6", "Integrate(d*(5+2*x),{x,0,m})==6");
  }

  @Test
  public void test50() {
    check(" \\left| x ^ { 2 } - 2 x + 2 \\right| = 5", //
        "Abs(2-2*x+x^2)==5");
  }

  @Test
  public void test51() {
    check("2 \\left| x ^ { 2 } - 1 \\right| = \\left| 4 - x ^ { 2 } \\right|", //
        "2*Abs(-1+x^2)==Abs(4-x^2)");
  }

  @Test
  public void test52() {
    check("\\frac { 3 \\left| x - 1 \\right| } { 3 } = \\frac { 12 } { 3 }", //
        "3*Abs(-1+x)*1/3==4");
  }

  @Test
  public void test53() {
    check(
        "\\left\\{ \\begin{array} { l } { 2 x - y = 3 } \\\\ { x + 2 y = 4 } \\end{array} \\right.",
        "{2*x-y==3,x+2*y==4}");
  }

  @Test
  public void test54() {
    check("( x ^ { 2 } + y ^ { 2 } = 1 , ( x - 2 ) ^ { 2 } + ( y - 1 ) ^ { 2 } = 4 )", //
        "{x^2+y^2==1,(-2+x)^2+(-1+y)^2==4}");
  }

  @Test
  public void test55() {
    check("\\frac { d } { d x } ( x ^ { 4 } \\operatorname { sin } ( x ) )", //
        "D(x^4*Sin(x),x)");
  }

  @Test
  public void test56() {
    check("\\frac { \\partial } { \\partial x } ( e ^ { a x } ) =", //
        "(E^(a*x)*∂)/(x*∂)==Null");
  }

  // @Test
  // public void test57() {
  // check("\\left\\{ \\begin{array} { l } { 8 x _ { 1 } - 2 x _ { 2 } - x _ { 3 } = 5 }" +
  // " \\\\ { - x _ { 1 } + 6 x _ { 2 } - x _ { 3 } = 4 } \\\\ { - x _ { 1 }" +
  // " - 3 x _ { 2 } + 9 x _ { 3 } = 5 } \\end{array} \\right.",
  // "list({{(8*x1)-(2*x2)-x3==5},{(-x1)+(6*x2)-x3==4},{(-x1)-(3*x2)+(9*x3)==5}})",
  // "List({{8*x1-2*x2-x3==5},{-x1+6*x2-x3==4},{-x1-3*x2+9*x3==5}})");
  // }
  //
  // @Test
  // public void test58() {
  // check("\\left\\{ \\begin{array} { l } { 8 x _ { 1 2 } - 2 x _ { 2 } - x _ { 3 } = 5 }" +
  // " \\\\ { - x _ { 1 } + 6 x _ { 2 } - x _ { 3 } = 4 } \\\\ { - x _ { 1 }" +
  // " - 3 x _ { 2 } + 9 x _ { 3 } = 5 } \\end{array} \\right.",
  // "list({{(8*x12)-(2*x2)-x3==5},{(-x1)+(6*x2)-x3==4},{(-x1)-(3*x2)+(9*x3)==5}})",
  // "List({{8*x12-2*x2-x3==5},{-x1+6*x2-x3==4},{-x1-3*x2+9*x3==5}})");
  // }
  //
  // @Test
  // public void test59() {
  // check("x _ { 1 2 }",
  // "x12",
  // "x12");
  // }

  @Test
  public void test60() {
    check("\\begin{array} { l } { 2 } \\\\ { 3 } \\end{array}", //
        "{2,3}");
  }

  @Test
  public void test61() {
    check("\\begin{array} {ll} { 2 } & {3} \\\\ { 3 } & {4} \\end{array}", "{{2,3},{3,4}}");
  }


  @Test
  public void test1a() {
    check("0", "0");
    check("1", "1");
    check("-3.14", "-3.14");
    check("(-7.13)(1.5)", "(-7.13)*1.5");
    check("x", "x");
    check("2x", "2*x");
    check("x^2", "x^2");
    check("x^{3 + 1}", "x^(1+3)");
    check("-c", "-c");
  }

  @Test
  public void test2a() {
    check("a \\cdot b", "a*b");
    check("a / b", "a/b");
    check("a \\div b", "a/b");
    check("a + b", "a+b");
  }

  @Test
  public void test3a() {

    check("a + b - a", "-a+a+b");
    check("a^2 + b^2 = c^2", "a^2+b^2==c^2");
  }

  @Test
  public void test4a() {
    check("\\sin \\theta", "Sin(θ)");
    check("\\sin(\\theta)", "Sin(θ)");
    check("\\sin^{-1} a", "ArcSin(a)");
    check("\\sin a \\cos b", "Cos(b)*Sin(a)");
    check("\\sin \\cos \\theta", "Sin(Cos*θ)");
    check("\\sin(\\cos \\theta)", "Sin(Cos(θ))");
    check("(\\csc x)(\\sec y)", "Csc(x)*Sec(y)");

  }

  @Test
  public void testFrac() {
    check("\\frac{a}{b}", "a/b");
    check("\\frac{a + b}{c}", "(a+b)/c");
    check("\\frac{7}{3}", "7/3");
  }

  @Test
  public void test15() {
    check("-2(-3)", "(-3)*(-2)");
  }

  @Test
  public void testDerivative2() {

    check("\\frac{d}{dx} x", "D(x,x)");
    check("\\frac{d}{dt} x", "D(x,t)");
  }

  @Test
  public void testLim() {
    check("\\lim_{x \\to 3} a", "Limit(a,x->3)");
    check("\\lim_{x \\rightarrow 3} a", "Limit(a,x->3)");
    check("\\lim_{x \\Rightarrow 3} a", "Limit(a,x->3)");
    check("\\lim_{x \\longrightarrow 3} a", "Limit(a,x->3)");
    check("\\lim_{x \\Longrightarrow 3} a", "Limit(a,x->3)");
    check("\\lim_{x \\to \\infty} \\frac{1}{x}", "Limit(1/x,x->Infinity)");
  }

  @Test
  public void testLimWithDir() {
    check("\\lim_{x \\to 3^{+}} a", "Limit(a,x->3,Direction->1)");
    check("\\lim_{x \\to 3^{-}} a", "Limit(a,x->3,Direction->-1)");
  }

  @Test
  public void testConstant() {
    check("\\infty", "Infinity");

  }

  @Test
  public void testFunction() {
    check("f(x)", "f(x)");
    check("f(x, y)", "f(x,y)");
    check("f(x, y, z)", "f(x,y,z)");
  }

  @Test
  public void testAbs2() {
    check("|x|", //
        "Abs(x)");
    check("\\left|\\left|x\\right|\\right|", //
        "Abs(Abs(x))");
    check("\\left|x\\right|\\left|y\\right|", //
        "Abs(x)*Abs(y)");
    check("\\left|\\left|x\\right|\\left|y\\right|\\right|", //
        "Abs(Abs(x)*Abs(y))");
  }

  @Test
  public void testDerivative() {
    check("\\frac{d f(x)}{dx}", //
        "D(f(x),x)");
    check("\\frac{d\\theta(x)}{dx}", //
        "D(θ(x),x)");
  }

  @Test
  public void testD() {
    check("( \\sqrt { x } ) ^ { \\prime }", //
        "Derivative(1)[Sqrt(x)]");
  }

  // @Test
  // public void testD1() {
  // check("f ^ { \\prime } ( x ) = \\frac { 5 x ^ { 2 } - 6 x - 5 } { ( 5 x - 3 ) ^ { 2 } }",
  // "d(sqrt(x),x)");
  // }

  @Test
  public void testAbs() {
    check("\\pi^{|xy|}", "Pi^Abs(x*y)");
  }

  @Test
  public void testIntegral() {
    check("\\int x dx", "Integrate(x,x)");
    check("\\int x d\\theta", "Integrate(x,θ)");
    check("\\int x + a dx", //
        "Integrate(a+x,x)");
    check("\\int (x^2 - y)dx", //
        "Integrate(x^2-y,x)");
  }

  @Test
  public void testInt2() {
    check("\\int^{5x}_{2} x^2 dy", "Integrate(x^2,{y,2,5*x})");
  }

  @Test
  public void testInt3() {
    check("\\int da", "Integrate(1,a)");
    check("\\int_0^7 dx", "Integrate(1,{x,0,7})");
    check("\\int_a^b x dx", "Integrate(x,{x,a,b})");
  }

  @Test
  public void testInt4() {
    check("\\int^b_a x dx", "Integrate(x,{x,a,b})");
    check("\\int_{a}^b x dx", "Integrate(x,{x,a,b})");
    check("\\int^{b}_a x dx", "Integrate(x,{x,a,b})");
    check("\\int_{a}^{b} x dx", "Integrate(x,{x,a,b})");
    check("\\int^{b}_{a} x dx", "Integrate(x,{x,a,b})");
  }

  @Test
  public void testInt5() {
    check("\\int_{f(a)}^{f(b)} f(z) dz", "Integrate(f(z),{z,f(a),f(b)})");
    check("\\int a + b + c dx", "Integrate(a+b+c,x)");
    check("\\int \\frac{dz}{z}", "Integrate(1/z,z)");

    check("\\int \\frac{3}{z} dz", "Integrate(3/z,z)");
    check("\\int \\frac{3 dz}{z}", "Integrate((3*d*z)/z)");
  }

  @Test
  public void testInt6() {
    check("\\int \\frac{1}{x} dx", //
        "Integrate(1/x,x)");
    check("\\int \\frac{1}{a} + \\frac{1}{b} dx", "Integrate(1/a+1/b,x)");
    check("\\int \\frac{1}{x} + 1 dx", //
        "Integrate(1+1/x,x)");
    check("\\int \\frac{3 \\cdot d\\theta}{\\theta}", //
        "Integrate((3*d*θ)/θ)");
  }

  @Test
  public void testInt7() {
    check("\\int_{5x}^{2} x^2 dx", "Integrate(x^2,{x,5*x,2})");
  }

  @Test
  public void testInt8() {
    check("\\int x^2 dx", "Integrate(x^2,x)");
  }

  @Test
  public void testInt9() {
    check("\\int (x+a)", "Integrate(a+x)");
  }

  @Test
  public void testFactorial() {
    check("x!!!", "((x!)!)!");
    check("x!", "x!");
    check("100!", "100!");
    check("\\theta!", "θ!");
    check("(x + 1)!", "(1+x)!");
    check("(x!)!", "(x!)!");
  }

  @Test
  public void testSqrt() {
    check("\\sqrt{x}", "Sqrt(x)");
    check("\\sqrt{x + b}", "Sqrt(b+x)");
    check("\\sqrt[3]{\\sin x}", "Sin(x)^(1/3)");
    check("\\sqrt[y]{\\sin x}", "Sin(x)^(1/y)");
    check("\\sqrt[\\theta]{\\sin x}", "Sin(x)^(1/θ)");
  }

  @Test
  public void testComparision() {
    check("x < y", "x<y");
    check("x \\leq y", "x<=y");
    check("x > y", "x>y");
    check("x \\geq y", "x>=y");
  }

  @Test
  public void testSum() {
    check("\\sum_{k = 1}^{3} c", "Sum(c,{k,1,3})");
    check("\\sum_{k = 1}^3 c", "Sum(c,{k,1,3})");
    check("\\sum^{3}_{k = 1} c", "Sum(c,{k,1,3})");
    check("\\sum^3_{k = 1} c", "Sum(c,{k,1,3})");
    check("\\sum_{k = 1}^{10} k^2", "Sum(k^2,{k,1,10})");
    check("\\sum_{n = 0}^{\\infty} \\frac{1}{n!}", "Sum(1/n!,{n,0,Infinity})");
  }

  @Test
  public void testProduct() {
    check("\\prod_{a = b}^{c} x", "Product(x,{a,b,c})");
    check("\\prod_{a = b}^c x", "Product(x,{a,b,c})");
    check("\\prod^{c}_{a = b} x", "Product(x,{a,b,c})");
    check("\\prod^c_{a = b} x", "Product(x,{a,b,c})");
  }

  @Test
  public void testLog01() {
    check("\\ln xy", //
        "y*Log(x)");
    check("\\log_{a} x \\log_{a} x", //
        "Log(a,x)*Log(a,x)");

    check("\\ln x", //
        "Log(x)");
    check("\\ln xy", //
        "y*Log(x)");
    check("\\log x", //
        "Log10(x)");

    check("\\log_2 x", //
        "Log(2,x)");
    check("\\log_{2} x", //
        "Log(2,x)");
    check("\\log_a x", //
        "Log(a,x)");
    check("\\log_{a} x", //
        "Log(a,x)");

    check("\\log_{11} x", //
        "Log(11,x)");
    check("\\log_{a^2} x", //
        "Log(a^2,x)");
  }

  @Test
  public void test14() {
    check("[x]", "x");
    check("[a + b]", "a+b");
  }

  @Test
  public void test17() {
    check("\\operatorname { tan } ( 2 x - \\frac { \\pi } { 4 } )", //
        "Tan(-Pi*1/4+2*x)");
    check("y = \\operatorname { cos } x \\cdot \\operatorname { tan } 2 x", //
        "y==x*Cos(x)*Tan(2)");
    check(
        "y = \\frac { 2 \\operatorname { cos } ( \\frac { 5 \\pi } { 2 } + x ) - 5 "
            + "\\operatorname { tan } ( x + 3 \\pi ) } { 2 - \\operatorname { cos } 2 x }",
        "y==(2*Cos(5*Pi*1/2+x)-5*Tan(3*Pi+x))/(2-x*Cos(2))");
  }

  @Test
  public void test18() {
    check("2 \\operatorname { cos } ^ { 2 } x - \\operatorname { cos } x - 1 = 0",
        "-1+-Cos(x)+2*Cos(x)^2==0");
    check("2 \\operatorname { sin } ^ { 2 } x + \\sqrt { 3 } \\operatorname { sin } 2 x = 3",
        "Sqrt(3)*x*Sin(2)+2*Sin(x)^2==3");
    check(
        " \\operatorname { sin } 2 x = \\operatorname { cos } ^ { 4 } \\frac { x } { 2 } - \\operatorname { sin } ^ { 4 } \\frac { x } { 2 }",
        "x*Sin(2)==Cos(x*1/2)^4-Sin(x*1/2)^4");
    check("\\operatorname { sin } 7 x \\operatorname { cos } 4 x", "x*x*Cos(4)*Sin(7)");
    check("2 x \\operatorname { cos } x = \\operatorname { sin } 7 x \\operatorname { cos } 4 x",
        "2*x*Cos(x)==x*x*Cos(4)*Sin(7)");

  }

  @Test
  public void test19() {
    check(
        " \\operatorname { log } _ { 2 } ^ { 3 } a + \\operatorname { log } _ { 2 } ^ { 3 } b + \\operatorname { log } _ { 2 } ^ { 3 } c \\leq 1",
        "Log(2,a)^3+Log(2,b)^3+Log(2,c)^3<=1");
    check("\\frac { x + 1 } { \\sqrt { m ( x - 1 ) ^ { 2 } + 4 } }", //
        "(1+x)/Sqrt(4+m*(-1+x)^2)");
    check(
        " 64 ^ { \\frac { 1 } { 2 } } \\cdot 64 ^ { \\frac { 1 } { 3 } } \\cdot \\sqrt [ 6 ] { 6 }", //
        "6^(1/6)*64^(1/3)*Sqrt(64)");
    check("P = ( 7 + 4 \\sqrt { 3 } ) ^ { 2017 } ( 4 \\sqrt { 3 } - 7 ) ^ { 2016 }",
        "P==(-7+4*Sqrt(3))^2016*(7+4*Sqrt(3))^2017");
    check("P = \\sqrt [ 4 ] { x \\cdot \\sqrt [ 3 ] { x ^ { 2 } \\cdot \\sqrt { x ^ { 3 } } } }",
        "P==(x*(x^2*Sqrt(x^3))^(1/3))^(1/4)");
  }

  @Test
  public void test20a() {
    check("\\{ x ^ { 2 } + y ^ { 2 } = 1 , ( x - 2 ) ^ { 2 } + ( y - 1 ) ^ { 2 } = 4 \\}",
        "{x^2+y^2==1,(-2+x)^2+(-1+y)^2==4}");
  }

  @Test
  public void test22() {
    // check("\\frac { d } { d x } ( x f ( x ^ { 2 } ) ) =",
    // "d((x*f(x^2)),x)",
    // "D([x*f(x^(2))],[x])");
  }

  @Test
  public void test23() {
    check(
        "\\{ \\frac { \\partial ( x ^ { 2 } y ^ { 4 } ) } { \\partial x } , \\frac { \\partial ( x ^ { 2 } y ^ { 4 } ) } { \\partial y } \\}",
        "{D(x^2*y^4,x),D(x^2*y^4,y)}");
  }

  @Test
  public void test24() {
    check("y = x ^ { 3 } - 5 x + 1 , y ^ { \\prime } = 3 x ^ { 2 } - 5",
        "{y==1-5*x+x^3,Derivative(1)[y]==-5+3*x^2}");
  }

  @Test
  public void test25() {
    check(
        "\\left\\{ \\begin{array} { l } { y - x = 1814 } \\\\ { y = 9 x + 182 } \\end{array} \\right.",
        "{-x+y==1814,y==182+9*x}");
  }

  @Test
  public void test26() {
    check(
        "\\left\\{ \\begin{array} { l } { 5 x + 2 y = 9 } \\\\ { x - 14 y = 5 } \\end{array} \\right.",
        "{5*x+2*y==9,x-14*y==5}");
  }

  @Test
  public void test27() {
    check("-3.14", "-3.14");
  }


  @Test
  public void test29() {
    check(
        "f \\left(\\right. x \\left.\\right) = - 7 \\left(\\left(\\right. x - 2 \\left.\\right)\\right)^{2} - 9",
        "f(x)==-9-7*(-2+x)^2");
  }

  @Test
  public void test30() {
    check("\\tan \\left(\\right. x \\left.\\right) + \\sec \\left(\\right. x \\left.\\right)",
        "Sec(x)+Tan(x)");
  }

  @Test
  public void test31a() {
    check(
        "g \\left(\\right. x \\left.\\right) = ln \\left(\\right. \\left|\\right. x + 1 \\left.\\right| \\left.\\right)",
        "g(x)==l*n(Abs(1+x))");
  }

  // @Test
  // public void test399() {
  // check("998\\left(\\cos(\\sin(666^{-1}^{-1}^{-1}^{-1}))^{-1}^{-1}\\right)", //
  // "tan(x)+sec(x)");
  // }

  @Test
  public void test400() {
    check("\\log_2345+3", //
        "3+Log(2,345)");
  }

  @Test
  public void test401() {
    String tex = "4711";
    TeXParser teXSliceParser = new TeXParser();
    IExpr ast = teXSliceParser.parse(tex);
    assertEquals(ast.toString(), "4711");
  }

  @Test
  public void test402() {
    // issue #794
    String tex = "\\int_0^52xdx";
    TeXParser teXSliceParser = new TeXParser();
    IExpr ast = teXSliceParser.parse(tex);
    assertEquals(ast.toString(), "Integrate(2*x,{x,0,5})");
  }

  @Test
  public void test403() {
    String tex = "\\log xy";
    TeXParser teXSliceParser = new TeXParser();
    IExpr ast = teXSliceParser.parse(tex);
    assertEquals(ast.toString(), "y*Log10(x)");

    tex = "\\sin xy";
    teXSliceParser = new TeXParser();
    ast = teXSliceParser.parse(tex);
    assertEquals(ast.toString(), "y*Sin(x)");
  }

  @Test
  public void test404() {
    // issue #794
    String tex = "x^.5";
    TeXParser teXSliceParser = new TeXParser();
    IExpr ast = teXSliceParser.parse(tex);
    assertEquals(ast.toString(), "Sqrt(x)");
  }

  public void check(String strEval, String strResult) {
    IExpr expr = texConverter.parse(strEval);
    assertEquals(expr.toString(), strResult);
  }

  public void checkFullForm(String strEval, String strResult) {
    IExpr expr = texConverter.parse(strEval);
    assertEquals(expr.fullFormString(), strResult);
  }

  public void checkEval(String strEval, String strResult) {
    EvalEngine engine = EvalEngine.get();
    EvalEngine.setReset(engine);
    IExpr expr = texConverter.parse(strEval);
    expr = engine.evaluate(expr);
    assertEquals(expr.toString(), strResult);
  }
  // private void check(String latex, String expectedSymjaExpr) {
  // check(latex, expectedSymjaExpr, "");
  // }

  /** The JUnit setup method */
  @Before
  public void setUp() {
    try {
      // F.initSymbols();
      F.await();
      EvalEngine.get().setRelaxedSyntax(true);
      texConverter = new TeXParser();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
