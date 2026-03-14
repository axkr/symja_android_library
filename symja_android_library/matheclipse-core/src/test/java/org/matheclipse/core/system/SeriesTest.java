package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;

public class SeriesTest extends ExprEvaluatorTestCase {

  @Test
  public void testComposeSeries() {
    check("ComposeSeries(Series(Exp(x), {x, 0, 10}), Series(Sin(x), {x, 0, 10}))", //
        "1+x+x^2/2-x^4/8-x^5/15-x^6/240+x^7/90+31/5760*x^8+x^9/5670-2951/3628800*x^10+O(x)^\n"
            + "11");
    check("ComposeSeries(Series(x*Exp(x), {x, 0, 10}), Series(Sin(x), {x, 0, 10}))", //
        "x+x^2+x^3/3-x^4/6-x^5/5-7/120*x^6+13/630*x^7+37/1680*x^8+2/405*x^9-4043/1814400*x^\n"
            + "10+O(x)^11");
    check("Series(Sin(x)*Exp(Sin(x)), {x, 0, 10}) // InputForm", //
        "SeriesData(x,0,{1,1,1/3,-1/6,-1/5,-7/120,13/630,37/1680,2/405,-4043/1814400},1,\n"
            + "11,1)");
    check("Series(Log(x), {x, 1, 1}) ", //
        "(-1+x)+O(-1+x)^2");
    check("Series(Log(x), {x, 1, 5}) ", //
        "(-1+x)-(1-x)^2/2+(-1+x)^3/3-(1-x)^4/4+(-1+x)^5/5+O(-1+x)^6");
    check("SeriesData(x, 0, {1,3,0,0,0}, 0, 5, 1)", //
        "1+3*x+O(x)^5");
    check("ComposeSeries(Series(Log(x), {x, 1, 1}), SeriesData(x, 0, {1,3,0,0,0}, 0, 5, 1))", //
        "3*x+O(x)^5");
    check("ComposeSeries(Series(Log(x), {x, 1, 2}), SeriesData(x, 0, {1,3,0,0,0}, 0, 5, 1))", //
        "3*x-9/2*x^2+O(x)^5");
    check("ComposeSeries(Series(Log(x), {x, 1, 5}), SeriesData(x, 0, {1,3,0,0,0}, 0, 5, 1))", //
        "3*x-9/2*x^2+9*x^3-81/4*x^4+O(x)^5");

    check("s2 = Series(Sin(x), {x, 0, 10})", //
        "x-x^3/6+x^5/120-x^7/5040+x^9/362880+O(x)^11");
    check("s2^0", //
        "1+O(x)^11");
    check("s1 = Series(Exp(x), {x, 0, 10})", //
        "1+x+x^2/2+x^3/6+x^4/24+x^5/120+x^6/720+x^7/5040+x^8/40320+x^9/362880+x^10/\n"
            + "3628800+O(x)^11");
    check("ComposeSeries(s1, s2)", //
        "1+x+x^2/2-x^4/8-x^5/15-x^6/240+x^7/90+31/5760*x^8+x^9/5670-2951/3628800*x^10+O(x)^\n"
            + "11");

    check("s1=SeriesData(x, 0, {1, 1,1,1}, 0, 4, 1)", //
        "1+x+x^2+x^3+O(x)^4");
    check("s2=SeriesData(x, 0, {1, 3}, 2, 4, 1)", //
        "x^2+3*x^3+O(x)^4");
    check("ComposeSeries(s2, s1)", "ComposeSeries(x^2+3*x^3+O(x)^4,1+x+x^2+x^3+O(x)^4)");
    check("ComposeSeries(s2, s1-1)", "x^2+5*x^3+O(x)^4");

    check("s1=SeriesData(x, 0, {1, 1,0,0}, 0, 4, 1)", //
        "1+x+O(x)^4");
    check("s2=SeriesData(x, 0, {1, 3}, 2, 4, 1)", //
        "x^2+3*x^3+O(x)^4");
    check("ComposeSeries(s2, s1-1)", //
        "x^2+3*x^3+O(x)^4");

    check(
        "ComposeSeries(SeriesData(x, 0, {1, 3}, 2, 4, 1), SeriesData(x, 0, {1, 1,0,0}, 0, 4, 1) - 1)", //
        "x^2+3*x^3+O(x)^4");
  }

  @Test
  public void testInverseSeries() {
    check("Series(a1*x+a2*x^2+a3*x^3+a4*x^4+a5*x^5+a6*x^6+a7*x^7, {x, 0, 7})", //
        "a1*x+a2*x^2+a3*x^3+a4*x^4+a5*x^5+a6*x^6+a7*x^7+O(x)^8");
    check("InverseSeries(Series(a1*x+a2*x^2+a3*x^3+a4*x^4+a5*x^5+a6*x^6+a7*x^7, {x, 0, 3}))", //
        "x/a1+(-a2*x^2)/a1^3+((2*a2^2-a1*a3)*x^3)/a1^5+O(x)^4");
    check("InverseSeries(Series(a1*x+a2*x^2+a3*x^3+a4*x^4+a5*x^5+a6*x^6+a7*x^7, {x, 0, 6}))", //
        "x/a1+(-a2*x^2)/a1^3+((2*a2^2-a1*a3)*x^3)/a1^5+((-5*a2^3+5*a1*a2*a3-a1^2*a4)*x^4)/a1^\n" //
            + "7+((14*a2^4-21*a1*a2^2*a3+3*a1^2*a3^2+6*a1^2*a2*a4-a1^3*a5)*x^5)/a1^9+((-42*a2^5+\n" //
            + "84*a1*a2^3*a3-28*a1^2*a2*a3^2-28*a1^2*a2^2*a4+7*a1^3*a3*a4+7*a1^3*a2*a5-a1^4*a6)*x^\n" //
            + "6)/a1^11+O(x)^7");

    // needs Puiseux/Laurent series to handle this
    check("InverseSeries(Series(x*Sin(x), {x, 0, 10}))", //
        "Sqrt(x)+x^(3/2)/12+29/1440*x^(5/2)+263/40320*x^(7/2)+23479/9676800*x^(9/2)+O(x)^\n" //
            + "5");
    check("InverseSeries(Series(x*Sin(x), {x, 0, 10})) // InputForm", //
        "SeriesData(x,0,{1,0,1/12,0,29/1440,0,263/40320,0,23479/9676800},1,10,2)");

    // 2nd arg is new result variable
    check("InverseSeries(SeriesData(x,0,{1,2,3},0,5,1),y)", //
        "1/2*(-1+y)-3/8*(1-y)^2+9/16*(-1+y)^3-135/128*(1-y)^4+O(-1+y)^5");

    check("Series(E^x,{x,0,4})^(-1)", //
        "1-x+x^2/2-x^3/6+x^4/24+O(x)^5");

    check("InverseSeries(SeriesData(x,0,{1,1,1},0,3,1))", //
        "(-1+x)-(1-x)^2+O(-1+x)^3");
    check("InverseSeries(SeriesData(x,x0,{1,1,1},0,3,1))", //
        "x0+(-1+x)-(1-x)^2+O(-1+x)^3");
    check("InverseSeries(Series(Sin(x), {x, 0, 9}))", //
        "x+x^3/6+3/40*x^5+5/112*x^7+35/1152*x^9+O(x)^10");

    check("InverseSeries(Series(Exp(x), {x, 0, 6}))", //
        "(-1+x)-(1-x)^2/2+(-1+x)^3/3-(1-x)^4/4+(-1+x)^5/5-(1-x)^6/6+O(-1+x)^7");
    check("InverseSeries(SeriesData(x,x0,{1,2,3},0,5,1))", //
        "x0+1/2*(-1+x)-3/8*(1-x)^2+9/16*(-1+x)^3-135/128*(1-x)^4+O(-1+x)^5");
    check("InverseSeries(SeriesData(x,0,{1,2,3},0,5,1))", //
        "1/2*(-1+x)-3/8*(1-x)^2+9/16*(-1+x)^3-135/128*(1-x)^4+O(-1+x)^5");
    check("InverseSeries(SeriesData(x,-4,{1,2,3},0,5,1))", //
        "-4+1/2*(-1+x)-3/8*(1-x)^2+9/16*(-1+x)^3-135/128*(1-x)^4+O(-1+x)^5");
    check("InverseSeries(Series(Log(x+1), {x, 0, 9}))", //
        "x+x^2/2+x^3/6+x^4/24+x^5/120+x^6/720+x^7/5040+x^8/40320+x^9/362880+O(x)^10");

    check("Series((x+1),{x, 0, 9})", //
        "1+x+O(x)^10");
    check("InverseSeries(Series((x+1),{x, 0, 9}))", //
        "(-1+x)+O(-1+x)^10");

    check("InverseSeries(Series(ArcSin(x), {x, 0, 9}))", //
        "x-x^3/6+x^5/120-x^7/5040+x^9/362880+O(x)^10");
    check("InverseSeries(Series(Log(x+1), {x, 0, 9}))", //
        "x+x^2/2+x^3/6+x^4/24+x^5/120+x^6/720+x^7/5040+x^8/40320+x^9/362880+O(x)^10");
  }

  @Test
  public void testNormal() {
    check("Normal(Series(Product((1+x^(k*(k+1)/2)), {k, 3}), {x, 0,6}))", //
        "1+x+x^3+x^4+x^6");
    check("Normal(SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1))", //
        "1/x-x-4*x^2-17*x^3-88*x^4-549*x^5");
    check("Normal(Series(Exp(x),{x,0,5}))", //
        "1+x+x^2/2+x^3/6+x^4/24+x^5/120");
    check("Normal(SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2))", //
        "Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880");
  }

  @Test
  public void testSeriesTaylor() {
    // issue #545
    check("Series(((x^3 + 72*x^2 + 600*x + 720)/(12*x^2 + 240*x+720)),{x,0,6})", //
        "1+x/2-x^2/12+x^3/48-x^4/180+13/8640*x^5-53/129600*x^6+O(x)^7");
    check("Series(1/(1-x),{x,0,2})", //
        "1+x+x^2+O(x)^3");
  }

  @Test
  public void testCoefficientList() {
    check("CoefficientList(Series(Log(1-x), {x, 0, 9}), x)", //
        "{0,-1,-1/2,-1/3,-1/4,-1/5,-1/6,-1/7,-1/8,-1/9}");
    check("CoefficientList(Series(Log(1-x), {x, 0, 9}), x)", //
        "{0,-1,-1/2,-1/3,-1/4,-1/5,-1/6,-1/7,-1/8,-1/9}");
    check("CoefficientList(Series(Sin(x), {x, 0, 5}),x)", //
        "{0,1,0,-1/6,0,1/120}");

    check("Series(Sin(x), {x, 1, 5})", //
        "Sin(1)+Cos(1)*(-1+x)-1/2*Sin(1)*(1-x)^2-1/6*Cos(1)*(-1+x)^3+1/24*Sin(1)*(1-x)^4+\n" //
            + "1/120*Cos(1)*(-1+x)^5+O(-1+x)^6");
    check("CoefficientList(Series(Sin(x), {x, 1, 5}), x)", //
        "{-101/120*Cos(1)+13/24*Sin(1),13/24*Cos(1)+5/6*Sin(1),5/12*Cos(1)-Sin(1)/4,-Cos(\n"//
            + "1)/12-Sin(1)/6,-Cos(1)/24+Sin(1)/24,Cos(1)/120}");
  }

  @Test
  public void testSeries() {
    check("Series(Sin(x)^2,{x,0,5})//InputForm", //
        "SeriesData(x,0,{1,0,-1/3},2,6,1)");

    check("Series(b ,{x,0,5}) // InputForm", //
        "b");
    check("Series(b *x ,{x,0,5}) // InputForm", //
        "SeriesData(x,0,{b},1,6,1)");

    check("Series(Sin(x),{x,2,3})", //
        "Sin(2)+Cos(2)*(-2+x)-1/2*Sin(2)*(2-x)^2-1/6*Cos(2)*(-2+x)^3+O(-2+x)^4");
    check("Series(Sqrt(Sin(x)), {x, 0, 10}) // InputForm", //
        "SeriesData(x,0,{1,0,0,0,-1/12,0,0,0,1/1440,0,0,0,-1/24192,0,0,0,-67/29030400},1,\n" //
            + "21,2)");

    check("Series(1/Sin(x)^10, {x, 0, 2}) // InputForm", //
        "SeriesData(x,0,{1,0,5/3,0,13/9,0,164/189,0,128/315,0,14797/93555,0,6803477/\n" //
            + "127702575},-10,3,1)");

    // check("Series(Gamma(x), {x, 0, 3}) // InputForm", //
    // "");
    // check("Series(1/(x^3+x), {x, 0, 3})", //
    // "");
    // check("Series((x^3-2x^2-9x+18)/(x^3+x), {x, 0, 3})", //
    // "");

    check("Series((1 + x)^n, {x, 0, 4}) // InputForm", //
        "SeriesData(x,0,{1,n,1/2*(-1+n)*n,1/6*(-2+n)*(-1+n)*n,1/24*(-3+n)*(-2+n)*(-1+n)*n},\n"
            + "0,5,1)");
    check("Series(x^x, {x, 0, 4})", //
        "1+Log(x)*x+1/2*Log(x)^2*x^2+1/6*Log(x)^3*x^3+1/24*Log(x)^4*x^4+O(x)^5");
    // https://oeis.org/A053614
    check(
        "nn=10; t=Rest(CoefficientList(Series(Product((1+x^(k*(k+1)/2)), {k, nn}), {x, 0, nn*(nn+1)/2}), x)); Flatten(Position(t, 0))",
        "{2,5,8,12,23,33}");
    // check("InverseSeries(Series(x,{x,0,3})) // FullForm", //
    // "Log(a)+(-a+x)/a-(-a+x)^2/(2*a^2)+(-a+x)^3/(3*a^3)+O(-a+x)^4");
    // check("Series(1/(3*x^2),{x,0,10})", //
    // "1/(3*x^2)+O(x)^11");
    // check("Series(x^a ,{x,0,5})",//
    // "");
    // check("Series(x^a*Sin(x),{x,0,5})",//
    // "");
    check("Series(ProductLog(x), {x, 0, 3}) ", //
        "x-x^2+3/2*x^3+O(x)^4");
    check("Series(Exp(Sin(x)), {x, 0, 10})", //
        "1+x+x^2/2-x^4/8-x^5/15-x^6/240+x^7/90+31/5760*x^8+x^9/5670-2951/3628800*x^10+O(x)^\n"
            + "11");

    check("Series(Sin(x)^3,{x,0,5})//InputForm", //
        "SeriesData(x,0,{1,0,-1/2},3,6,1)");

    check("Series(Sin(x) ,{x,0,-2})//InputForm", //
        "SeriesData(x,0,{1},1,2,1)");
    check("Series(b*Sin(x) ,{x,0,-2})//InputForm", //
        "SeriesData(x,0,{b},1,2,1)");

    check("Series(b*x ,{x,0,-2})//InputForm", //
        "SeriesData(x,0,{b},1,2,1)");
    check("Series(b*x ,{x,0,5})//InputForm", //
        "SeriesData(x,0,{b},1,6,1)");

    check("Series(b*Sin(x) ,{x,0,5})//InputForm", //
        "SeriesData(x,0,{b,0,-b/6,0,b/120},1,6,1)");
    check("Series(b+Sin(x) ,{x,0,5})//InputForm", //
        "SeriesData(x,0,{b,1,0,-1/6,0,1/120},0,6,1)");
    check("Series(f(b) ,{x,0,5})", //
        "f(b)");
    check("Series(x,{x,0,5})", //
        "x+O(x)^6");
    check("Series(f(b)^(x^(1/3)),{x,0,5})", //
        "1+Log(f(b))*x^(1/3)+1/2*Log(f(b))^2*x^(2/3)+1/6*Log(f(b))^3*x+1/24*Log(f(b))^4*x^(\n"
            + "4/3)+1/120*Log(f(b))^5*x^(5/3)+1/720*Log(f(b))^6*x^2+1/5040*Log(f(b))^7*x^(7/3)+\n"
            + "1/40320*Log(f(b))^8*x^(8/3)+1/362880*Log(f(b))^9*x^3+1/3628800*Log(f(b))^10*x^(\n"
            + "10/3)+1/39916800*Log(f(b))^11*x^(11/3)+1/479001600*Log(f(b))^12*x^4+1/6227020800*Log(f(b))^\n"
            + "13*x^(13/3)+1/87178291200*Log(f(b))^14*x^(14/3)+1/1307674368000*Log(f(b))^15*x^5+O(x)^(\n"
            + "16/3)");
    check("Series(E^(x^(2/3)),{x,0,5})", //
        "1+x^(2/3)+x^(4/3)/2+x^2/6+x^(8/3)/24+x^(10/3)/120+x^4/720+x^(14/3)/5040+O(x)^(16/\n"
            + "3)");
    check("Series(E^(x^(1/3)),{x,0,5})", //
        "1+x^(1/3)+x^(2/3)/2+x/6+x^(4/3)/24+x^(5/3)/120+x^2/720+x^(7/3)/5040+x^(8/3)/\n"
            + "40320+x^3/362880+x^(10/3)/3628800+x^(11/3)/39916800+x^4/479001600+x^(13/3)/\n"
            + "6227020800+x^(14/3)/87178291200+x^5/1307674368000+O(x)^(16/3)");
    check("Series(E^Sqrt(x),{x,0,5})", //
        "1+Sqrt(x)+x/2+x^(3/2)/6+x^2/24+x^(5/2)/120+x^3/720+x^(7/2)/5040+x^4/40320+x^(9/2)/\n"
            + "362880+x^5/3628800+O(x)^(11/2)");

    check("s1=Series(Sin(x),{x,0,10})", //
        "x-x^3/6+x^5/120-x^7/5040+x^9/362880+O(x)^11");
    check("s2=Series(Cos(x),{x,0,10})", //
        "1-x^2/2+x^4/24-x^6/720+x^8/40320-x^10/3628800+O(x)^11");

    check("s1^2+s2^2 // InputForm", //
        "SeriesData(x,0,{1},0,11,1)");
    check("Series(Sin(x^5)/x^10,{x,0,15})", //
        "1/x^5-x^5/6+x^15/120+O(x)^16");
    check("Series(E^x,{x,0,5})", //
        "1+x+x^2/2+x^3/6+x^4/24+x^5/120+O(x)^6");

    check("Series(1/(3*x^2),{x,0,10})", //
        "1/(3*x^2)+O(x)^11");
    check("Series(ArcSin(x)/(3*x^2),{x,0,10})", //
        "1/(3*x)+x/18+x^3/40+5/336*x^5+35/3456*x^7+21/2816*x^9+O(x)^11");
    check("Series(ArcSin(x)/x,{x,0,10})", //
        "1+x^2/6+3/40*x^4+5/112*x^6+35/1152*x^8+63/2816*x^10+O(x)^11");
    check("Series(ArcSin(x)/x,{x,0,3})", //
        "1+x^2/6+O(x)^4");
    check("Series(Log(x),{x,a,3})", //
        "Log(a)+(-a+x)/a-(-a+x)^2/(2*a^2)+(-a+x)^3/(3*a^3)+O(-a+x)^4");
    check("Series(f(x),{x,a,3})", //
        "f(a)+f'(a)*(-a+x)+1/2*f''(a)*(-a+x)^2+1/6*Derivative(3)[f][a]*(-a+x)^3+O(-a+x)^4");
    check("s1=SeriesData(x, 0, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 2, 9, 1)", //
        "x^2+x^3+x^4+x^5+x^6+x^7+x^8+O(x)^9");
    check("s2=SeriesData(x, 0, {1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800}, 3, 11, 1)", //
        "x^3+x^4+2*x^5+6*x^6+24*x^7+120*x^8+720*x^9+5040*x^10+O(x)^11");
    // SeriesData[x, 0, {1, 2, 4, 10, 34, 154, 874}, 5, 12, 1]
    check("s1*s2", "x^5+2*x^6+4*x^7+10*x^8+34*x^9+154*x^10+874*x^11+O(x)^12");
    check("s1+s2", //
        "x^2+2*x^3+2*x^4+3*x^5+7*x^6+25*x^7+121*x^8+O(x)^9");

    check("s1=SeriesData(x, 0, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 0, 11, 1)", //
        "1+x+x^2+x^3+x^4+x^5+x^6+x^7+x^8+x^9+x^10+O(x)^11");
    check("s2=SeriesData(x, 0, {1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800}, 0, 11, 1)", //
        "1+x+2*x^2+6*x^3+24*x^4+120*x^5+720*x^6+5040*x^7+40320*x^8+362880*x^9+3628800*x^\n" //
            + "10+O(x)^11");
    // check("s1*s2 // FullForm",
    // "1+2*x+4*x^2+10*x^3+34*x^4+154*x^5+874*x^6+5914*x^7+46234*x^8+409114*x^9+4037914*x^\n" +
    // "10+O(x)^11");
    check("s1", //
        "1+x+x^2+x^3+x^4+x^5+x^6+x^7+x^8+x^9+x^10+O(x)^11");
    check("s2", //
        "1+x+2*x^2+6*x^3+24*x^4+120*x^5+720*x^6+5040*x^7+40320*x^8+362880*x^9+3628800*x^\n" + //
            "10+O(x)^11");

    check("s1*s2 // InputForm", //
        "SeriesData(x,0,{1,2,4,10,34,154,874,5914,46234,409114,4037914},0,11,1)");

    check("Series(x*4+x^2-y*x^10, {x, 0, 10})// InputForm", //
        "SeriesData(x,0,{4,1,0,0,0,0,0,0,0,-y},1,11,1)");
    check("Series(x*4+x^2-y*x^11, {x, 0, 10}) // InputForm", //
        "SeriesData(x,0,{4,1},1,11,1)");

    check("D(Csc(x),{x,2})", //
        "Cot(x)^2*Csc(x)+Csc(x)^3");
    check("D(Tan(x)/Sin(x),x)", //
        "Sec(x)*Tan(x)");
    check("D(Tan(x)/Sin(x),{x,2})", //
        "Sec(x)^3+Sec(x)*Tan(x)^2");
    check("Series(Tan(x)/Sin(x),{x,a,2})", //
        "Sec(a)+Sec(a)*Tan(a)*(-a+x)+1/2*(Sec(a)^3+Sec(a)*Tan(a)^2)*(-a+x)^2+O(-a+x)^3");
    check("Series(Tan(x)*Csc(x),{x,a,2})", //
        "Sec(a)+Sec(a)*Tan(a)*(-a+x)+1/2*(Sec(a)^3+Sec(a)*Tan(a)^2)*(-a+x)^2+O(-a+x)^3");
    check("Series(f(x)+g(x),{x,a,2})", //
        "f(a)+g(a)+(f'(a)+g'(a))*(-a+x)+(f''(a)/2+g''(a)/2)*(-a+x)^2+O(-a+x)^3");

    check("Series(Sin(f(x)),{x,0,2}) // InputForm",
        "SeriesData(x,0,{Sin(f(0)),Cos(f(0))*f'(0),-1/2*Sin(f(0))*f'(0)^2+1/2*Cos(f(0))*f''(\n" //
            + "0)},0,3,1)");
    check("Series(Sin(x),{x,0,2})", "x+O(x)^3");

    check("Series(f(x),{x,a,3})",
        "f(a)+f'(a)*(-a+x)+1/2*f''(a)*(-a+x)^2+1/6*Derivative(3)[f][a]*(-a+x)^3+O(-a+x)^4");
    check("Series(Exp(x),{x,0,2})", //
        "1+x+x^2/2+O(x)^3");
    check("Series(Exp(f(x)),{x,0,2})",
        "E^f(0)+E^f(0)*f'(0)*x+1/2*(E^f(0)*f'(0)^2+E^f(0)*f''(0))*x^2+O(x)^3");
    check("Series(Exp(x),{x,0,5})", //
        "1+x+x^2/2+x^3/6+x^4/24+x^5/120+O(x)^6");
    check("Series(100,{x,a,5})", //
        "100");
    check("Series(y,{x,a,5})", //
        "y");
  }

  @Test
  public void testSeries0() {
    check("Series[(1/w)*Log[(a^w+b^w)/2],w->0]", //
        "1/2*(Log(a)+Log(b))+O(w)^1");
  }

  @Test
  public void testSeriesMrv() {
    check("Series((-1/E^(E^(-E^x*t^0)-1/x)+E^(E^(-x)+(E^(-E^x*t^0))^0/E^x^2+1/x))*E^x*t^0,{t,0,1})", //
        "(-1/E^(E^(-E^x)-1/x)+E^(E^(-x)+E^(-x^2)+1/x))*E^x");
    check("Series((1+1/(1/t))^2/((-1+1+1/(1/t))*(1+1+1/(1/t))),{t,0,1})", //
        "1/(2*t)+3/4+t/8+O(t)^2");
  }

  @Test
  public void testSeriesRational() {
    check("Series((x + x^2 + 2x^3 + x^4 - x^5)/(1 + 4x^3 - x^6), {x, 0, 7})", //
        "x+x^2+2*x^3-3*x^4-5*x^5-8*x^6+13*x^7+O(x)^8");
    check("Series((x + x^2 + 2x^3 + x^4 - x^5)/(1 + 4x^3 - x^6), {x, 0, 50})", //
        "x+x^2+2*x^3-3*x^4-5*x^5-8*x^6+13*x^7+21*x^8+34*x^9-55*x^10-89*x^11-144*x^12+233*x^\n"
            + "13+377*x^14+610*x^15-987*x^16-1597*x^17-2584*x^18+4181*x^19+6765*x^20+10946*x^21-\n"
            + "17711*x^22-28657*x^23-46368*x^24+75025*x^25+121393*x^26+196418*x^27-317811*x^28-\n"
            + "514229*x^29-832040*x^30+1346269*x^31+2178309*x^32+3524578*x^33-5702887*x^34-\n"
            + "9227465*x^35-14930352*x^36+24157817*x^37+39088169*x^38+63245986*x^39-102334155*x^\n"
            + "40-165580141*x^41-267914296*x^42+433494437*x^43+701408733*x^44+1134903170*x^45-\n"
            + "1836311903*x^46-2971215073*x^47-4807526976*x^48+7778742049*x^49+12586269025*x^50+O(x)^\n"
            + "51");
    check("SeriesData(x, 0, {1, 1, 2, -3, -5}, 1, 6, 1)", //
        "x+x^2+2*x^3-3*x^4-5*x^5+O(x)^6");
  }

  @Test
  public void testSeriesData() {
    check("Series(Exp(x), {x,0,2}) // FullForm", //
        "SeriesData(x, 0, List(1, 1, Rational(1,2)), 0, 3, 1)");

    check(
        "s1=SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)*SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1) //InputForm", //
        "SeriesData(x,0,{1,0,-1/3,0,2/45,0,-1/360,0,1/14400},2,12,1)");

    check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, -2, 11, 1)^2", //
        "1/x^4-1/(3*x^2)+2/45-x^2/360+x^4/14400+O(x)^9");
    check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, -2, 11, 3)^2", //
        "1/x^(4/3)-1/(3*x^(2/3))+2/45-x^(2/3)/360+x^(4/3)/14400+O(x)^3");

    check(
        "SeriesData(x, 0,{1,1,1,1,1}, 1, 11,3) + SeriesData(x, 0,{1,1,1,1,1}, 1, 4,3) // InputForm",
        "SeriesData(x,0,{2,2,2},1,4,3)");
    check(
        "SeriesData(x, 0,{1,1,1,1,1}, 1, 11,3) + SeriesData(x, 0,{1,1,1,1,1}, 1, 4,5) // InputForm",
        "SeriesData(x,0,{1,0,1,1,0,0,1,1},3,12,15)");
    // if (ToggleFeature.SERIES_DENOMINATOR) {

    check(
        "SeriesData(x, 0, {1, 0, -1/3, 0, 2/45, 0, -1/360, 0, 1/14400}, 2, 12, 3)*SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 3) // InputForm", //
        "SeriesData(x,0,{1,0,-1/2,0,13/120,0,-7/540,0,13/14400},3,13,3)");
    // }
    check("SeriesData(x, 0,{1,1,1,1,1}, 1, 11)", //
        "x+x^2+x^3+x^4+x^5+O(x)^11");
    check("SeriesData(x, 0,{1,1,1,1,1}, 1, 11)^2 // InputForm", //
        "SeriesData(x,0,{1,2,3,4,5,4,3,2,1},2,12,1)");
    // check("SeriesData(x, 0,{1,1,1,1,1}, 1, 11)*SeriesData(x, 0,{1,1,1,1,1}, 1, 5)", //
    // "x^2+2*x^3+3*x^4+4*x^5+O(x)^6");
    // check("SeriesData(x, 0,{1,1,1,1,1}, 1, 11, 3)*SeriesData(x, 0,{1,1,1,1,1}, 1, 5, 3)", //
    // "x^(2/3)+2*x+3*x^(4/3)+4*x^(5/3)+O(x)^2");

    check("s1=SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^2//InputForm", //
        "SeriesData(x,0,{1,0,-1/3,0,2/45,0,-1/360,0,1/14400},2,12,1)");
    // if (ToggleFeature.SERIES_DENOMINATOR) {
    check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, -2, 11, 3)", //
        "1/x^(2/3)-1/6+x^(2/3)/120+O(x)^(11/3)");
    check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 3)^2", //
        "x^(2/3)-x^(4/3)/3+2/45*x^2-x^(8/3)/360+x^(10/3)/14400+O(x)^4");
    check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 3)^3", //
        "x-x^(5/3)/2+13/120*x^(7/3)-7/540*x^3+13/14400*x^(11/3)+O(x)^(13/3)");
    check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 3)", //
        "x^(1/3)-x/6+x^(5/3)/120+O(x)^(11/3)");
    // }

    check("s1=SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1)", //
        "1/x-x-4*x^2-17*x^3-88*x^4-549*x^5+O(x)^6");
    check("s1[[1]]", //
        "x");
    check("s1[[2]]", //
        "0");
    check("s1[[3]]", //
        "{1,0,-1,-4,-17,-88,-549}");
    check("s1[[4]]", //
        "-1");
    check("s1[[5]]", //
        "6");
    check("s1[[6]]", //
        "1");
    check("s1=SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)", //
        "x-x^3/6+x^5/120+O(x)^11");
    check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^3 // InputForm", //
        "SeriesData(x,0,{1,0,-1/2,0,13/120,0,-7/540,0,13/14400},3,13,1)");

    // check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^12", //
    // "x^12-2*x^14+29/15*x^16-649/540*x^18+3883/7200*x^20+O(x)^22");

    check(
        "SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)*SeriesData(x, 0,{1,0,-1/5,0,1/110}, 1, 11, 1) // InputForm", //
        "SeriesData(x,0,{1,0,-11/30,0,67/1320,0,-7/2200,0,1/13200},2,12,1)");
    check(
        "SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)*SeriesData(x, 0,{1,0,-1/5,0,1/110}, 1, 11, 1)", //
        "x^2-11/30*x^4+67/1320*x^6-7/2200*x^8+x^10/13200+O(x)^12");
    check(
        "SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)-SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)", //
        "O(x)^11");
    check(
        "SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)+SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)", //
        "2*x-x^3/3+x^5/60+O(x)^11");
    // if (ToggleFeature.SERIES_DENOMINATOR) {
    check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)*7",
        "7*Sqrt(x)-7/6*x^(3/2)+7/120*x^(5/2)-x^(7/2)/720+x^(9/2)/51840+O(x)^(11/2)");
    check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)+4", //
        "4+Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)");
    check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)",
        "Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)");
    // }

    check("SeriesData(100, 0, Table(i^2, {i, 10}), 0, 10, 1)", //
        "Indeterminate");
    check("SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1)", //
        "1+4*x+9*x^2+16*x^3+25*x^4+36*x^5+49*x^6+64*x^7+81*x^8+100*x^9+O(x)^10");
    check("D(SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1), x)", //
        "4+18*x+48*x^2+100*x^3+180*x^4+294*x^5+448*x^6+648*x^7+900*x^8+O(x)^9");
    check("Integrate(SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1), x)", //
        "x+2*x^2+3*x^3+4*x^4+5*x^5+6*x^6+7*x^7+8*x^8+9*x^9+10*x^10+O(x)^11");
  }

  @Test
  public void testDSeriesData() {
    check("D(SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1), x)", //
        "4+18*x+48*x^2+100*x^3+180*x^4+294*x^5+448*x^6+648*x^7+900*x^8+O(x)^9");
    check("D(Series(Log(x),{x,a,5}), x)", //
        "1/a-(-a+x)/a^2+(-a+x)^2/a^3-(-a+x)^3/a^4+(-a+x)^4/a^5+O(-a+x)^5");
  }

  @Test
  public void testIntegrateSeriesData() {
    check("Integrate(SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1), x)", //
        "x+2*x^2+3*x^3+4*x^4+5*x^5+6*x^6+7*x^7+8*x^8+9*x^9+10*x^10+O(x)^11");
    check("Integrate(Series(Log(x),{x,a,5}), x)", //
        "Log(a)*(-a+x)+(-a+x)^2/(2*a)-(-a+x)^3/(6*a^2)+(-a+x)^4/(12*a^3)-(-a+x)^5/(20*a^4)+(-a+x)^\n"
            + "6/(30*a^5)+O(-a+x)^7");
  }

  @Test
  public void testSeriesCoefficient() {
    check("SeriesCoefficient(EllipticK(x),{x,2,3})", //
        "1/64*(-9*EllipticE(2)-5*EllipticK(2))");

    if (Config.EXPENSIVE_JUNIT_TESTS) {
      check(
          "SeriesCoefficient((-1/2-x+x^2-2*x^3-5/2*x^5)/(-1/2-x/2+5/2*x^2-5/2*x^3+x^5+x^6),{x,0,11})", //
          "12");
    }

    check("SeriesCoefficient(Cot(x),{x,0,3})", //
        "-1/45");

    check("SeriesCoefficient((-x-2*x^2)/(-1+x^3),{x,0,4})", //
        "1");
    check("SeriesCoefficient(f(x),{x,a,2})", //
        "f''(a)/2");
    check("SeriesCoefficient(f(x),{x,2,4})", //
        "Derivative(4)[f][2]/24");
    check("SeriesCoefficient(x^x,{x,2,4})", //
        "1/6*(4+9*Log(2)+9*Log(2)^2+4*Log(2)^3+Log(2)^4)");

    check("SeriesCoefficient(ArcTan(x),{x,n,12})", //
        "-I*1/24*(-1/(I-n)^12+1/(I+n)^12)");
    // "(3*n-55*n^3+198*n^5-198*n^7+55*n^9-3*n^11)/(3*(1+12*n^2+66*n^4+220*n^6+495*n^8+\n" //
    // + "792*n^10+924*n^12+792*n^14+495*n^16+220*n^18+66*n^20+12*n^22+n^24))");

    check("SeriesCoefficient(Fibonacci(z), {z, 0, n})", //
        "Piecewise({{(-(-I*Pi-ArcCsch(2))^n-(I*Pi-ArcCsch(2))^n+2*ArcCsch(2)^n)/(2*Sqrt(5)*n!),n>=\n" //
            + "1}},0)");

    check("SeriesCoefficient(1/x,{x,0,n})", //
        "0");
    check("SeriesCoefficient(f(x),{x,a,7/3})", //
        "0");
    check("SeriesCoefficient(f(x),{x,0,4})", //
        "Derivative(4)[f][0]/24");
    check("SeriesCoefficient(b^(a+c*x), {x, 0, n})", //
        "Piecewise({{(b^a*(c*Log(b))^n)/n!,n>=0}},0)");
    check("SeriesCoefficient(E^x, {x, 0, n})", //
        "Piecewise({{1/n!,n>=0}},0)");
    check("SeriesCoefficient(E^(-x), {x, 0, n})", //
        "Piecewise({{(-1)^n/n!,n>=0}},0)");

    check("SeriesCoefficient(Series(Exp(Sin(x)), {x, 0, 10}), 8)", //
        "31/5760");
    check("SeriesCoefficient(Series(Exp(Sin(x)), {x, 0, 10}), 10)", //
        "-2951/3628800");
    check("SeriesCoefficient(Series(Exp(Sin(x)), {x, 0, 10}), 11)", //
        "Indeterminate");
    check("SeriesCoefficient(a^x, {x, 0, n})", //
        "Piecewise({{Log(a)^n/n!,n>=0}},0)");

    check("SeriesCoefficient(x^x,{x,0,4})", //
        "Log(x)^4/24");

    check("SeriesCoefficient(ChebyshevT(k, x), {x, 0, 2})", //
        "((-1+k)*(1+k)*k^2*Pi)/(8*Gamma(1/2*(3-k))*Gamma(1/2*(3+k)))");
    // "1/2*k^2*Cos(1/2*(-2+k)*Pi)");
    check("SeriesCoefficient(d+4*x^e+7*x^f,{x, a, n})", //
        "Piecewise({{(4*a^e*Binomial(e,n)+7*a^f*Binomial(f,n))/a^n,n>0},{4*a^e+7*a^f+d,n==\n"
            + "0}},0)");
    check("SeriesCoefficient(1/(3*x^2),{x,0,4})", //
        "0");
    check("SeriesCoefficient(1/(3*x^2),{x,a,4})", //
        "5/3*1/a^6");
    check("SeriesCoefficient(1/x^3,{x,0,-3})", //
        "1");
    check("SeriesCoefficient(1/x,{x,0,-1})", //
        "1");
    check("SeriesCoefficient(1/x,{x, a, n})", //
        "Piecewise({{1/((-a)^n*a),n>=0}},0)");
    check("SeriesCoefficient(1/x^5,{x, a, n})", //
        "Piecewise({{((1+n)*(2+n)*(3+n)*(4+n))/(24*(-a)^n*a^5),n>=0}},0)");
    check("SeriesCoefficient(1/x^2,{x, a, n})", //
        "Piecewise({{(1+n)/((-a)^n*a^2),n>=0}},0)");
    check("SeriesCoefficient(1/x^3,{x, a, n})", //
        "Piecewise({{((1+n)*(2+n))/(2*(-a)^n*a^3),n>=0}},0)");
    check("SeriesCoefficient(x^b,{x, a, n})", //
        "Piecewise({{a^(b-n)*Binomial(b,n),n>0},{a^b,n==0}},0)");

    check("SeriesCoefficient(d+4*x^e+7*x^f+Sin(x),{x, a, 10})", //
        "(4*a^e*Binomial(e,10)+7*a^f*Binomial(f,10))/a^10-Sin(a)/3628800");
    check("SeriesCoefficient(x^42,{x, a, n})", //
        "Piecewise({{a^(42-n)*Binomial(42,n),0<=n<=42}},0)");

    check("SeriesCoefficient(f(x),{x, a, n})", //
        "SeriesCoefficient(f(x),{x,a,n})");
    check("SeriesCoefficient(b^x,{x, a, n})", //
        "Piecewise({{(b^a*Log(b)^n)/n!,n>=0}},0)");

    check("SeriesCoefficient(Sin(b),{x,a,2})", //
        "0");
    check("SeriesCoefficient(f(42),{x,a,n})", //
        "Piecewise({{f(42),n==0}},0)");
    check("SeriesCoefficient(f(x),{x,a,2})", //
        "f''(a)/2");

    check("SeriesCoefficient(Cos(x),{x,0,n})", //
        "Piecewise({{((1+(-1)^n)*I^n)/(2*n!),n>=0}},0)");
    check("SeriesCoefficient(Cos(x),{x,Pi/2,n})", //
        "Piecewise({{(-I*1/2*(-1+(-1)^n)*I^n)/n!,n>=0}},0)");
    check("SeriesCoefficient(Cos(x),{x,f+g,n})", //
        "Piecewise({{Cos(f+g+1/2*n*Pi)/n!,n>=0}},0)");

    check("SeriesCoefficient(Sin(x),{x,0,n})", //
        "Piecewise({{(I*1/2*(-1+(-1)^n)*I^n)/n!,n>=0}},0)");
    check("SeriesCoefficient(Sin(x),{x,Pi/2,n})", //
        "Piecewise({{((1+(-1)^n)*I^n)/(2*n!),n>=0}},0)");
    check("SeriesCoefficient(Sin(x),{x,f+g,n})", //
        "Piecewise({{Sin(f+g+1/2*n*Pi)/n!,n>=0}},0)");

    check("SeriesCoefficient(Tan(x),{x,0,n})", //
        "Piecewise({{((-1+(-1)^n)*I^(1+n)*2^n*(-1+2^(1+n))*BernoulliB(1+n))/(1+n)!,n>=1}},\n"
            + "0)");
    check("SeriesCoefficient(Tan(x),{x,0,3})", //
        "1/3");
    check("SeriesCoefficient(Tan(x),{x,Pi/2,n})", //
        "Piecewise({{-1,n==-1},{((-1+(-1)^n)*I^(1+n)*2^n*BernoulliB(1+n))/(1+n)!,n>=0}},0)");

    check("SeriesCoefficient(Cot(x),{x,0,n})", //
        "Piecewise({{1,n==-1},{(-I*(-1+(-1)^n)*(I*2)^n*BernoulliB(1+n))/(1+n)!,n>=0}},0)");

    check("SeriesCoefficient(Cot(x),{x,Pi/2,n})", //
        "Piecewise({{(-I*(-1+(-1)^n)*(I*2)^n*(-1+2^(1+n))*BernoulliB(1+n))/(1+n)!,n>=1}},\n" + //
            "0)");
    check("SeriesCoefficient(Cot(x),{x,Pi/2,3})", //
        "-1/3");
    check("SeriesCoefficient(d+4*x^e+7*x^f,{x, a,3})", //
        "(2/3*a^e*(-2+e)*(-1+e)*e+7/6*a^f*(-2+f)*(-1+f)*f)/a^3");
    check("SeriesCoefficient(d+4*x^e+7*x^f,{x, a, n})", //
        "Piecewise({{(4*a^e*Binomial(e,n)+7*a^f*Binomial(f,n))/a^n,n>0},{4*a^e+7*a^f+d,n==\n"
            + "0}},0)");
  }

  @Test
  public void testSeriesCoefficientArcCoth() {
    // TODO fails currently due to 1/0 Division by Zero in the generic a_ rule
    // Should return the complex constant term: (I*Pi)/2
    // check("SeriesCoefficient(ArcCoth(x), {x, 0, 0})", //
    // "(I*Pi)/2");
    //
    // // Check an odd term to ensure the new base rule works for n > 0
    // check("SeriesCoefficient(ArcCoth(x), {x, 0, 1})", //
    // "1");
  }

  @Test
  public void testSeriesCoefficientChebyshevU() {
    // TODO fails currently because the formula is missing the (1 + k) multiplier

    // // ChebyshevU(1, x) is mathematically `2x`. The x^1 coefficient must be 2.
    // // The buggy rule currently evaluates this to 1.
    // check("SeriesCoefficient(ChebyshevU(1, x), {x, 0, 1})", "2");
    //
    // // ChebyshevU(2, x) is mathematically `4x^2 - 1`. The x^0 coefficient must be -1.
    // // The buggy rule currently evaluates this to -1/3.
    // check("SeriesCoefficient(ChebyshevU(2, x), {x, 0, 0})", "-1");
    //
    // // ChebyshevU(3, x) is mathematically `8x^3 - 4x`. The x^1 coefficient must be -4.
    // // The buggy rule currently evaluates this to -1.
    // check("SeriesCoefficient(ChebyshevU(3, x), {x, 0, 1})", "-4");
  }

  @Test
  public void testDivisionByZero() {
    // message: General: -1 is not a valid variable.
    check("Series(1/0,{-1,-2,3})", //
        "Series(ComplexInfinity,{-1,-2,3})");
  }

  @Test
  public void testExpSeries() {
    check("SeriesCoefficient(Exp(x), {x, a, n})", //
        "Piecewise({{E^a/n!,n>=0}},0)");
    check("Series(Exp(1/x), {x, 0, 2}) // InputForm", //
        "E^SeriesData(x,0,{1},-1,3,1)");

    check("Series(Exp(1/x), {x, Infinity, 3}) // InputForm", //
        "SeriesData(x,Infinity,{1,1,1/2,1/6},0,4,1)");
    check("Series(Exp(1/x), {x, -Infinity, 3}) // InputForm", //
        "SeriesData(x,-Infinity,{1,1,1/2,1/6},0,4,1)");
  }

  @Test
  public void testLogSeries() {
    check("SeriesCoefficient(Log(x), {x, a, n})", //
        "Piecewise({{(-1)^(1+n)/(a^n*n),n>=1},{Log(a),n==0}},0)");
    check("SeriesCoefficient(Log(b+c*x), {x, a, n})", //
        "Piecewise({{-(-c/(b+a*c))^n/n,n>0},{Log(b+a*c),n==0}},0)");
    check("Series(Log(x), {x, 0, 3})", //
        "Log(x)+O(x)^4");
    check("Series(Exp(2*x)*Log(x), {x, 0, 2})", //
        "Log(x)+2*Log(x)*x+2*Log(x)*x^2+O(x)^3");
    check("Series(Log(x), {x, 0, 3})", //
        "Log(x)+O(x)^4");
  }

  @Test
  public void testSinSeries() {
    check("Series(Sin(x)/x^2, {x, 0, 3})", //
        "1/x-x/6+x^3/120+O(x)^4");
  }

  @Test
  public void testGammaSinSeries() {
    check("Series(Gamma(Sin(z) - z)^3, {z,0,1}) // InputForm", //
        "SeriesData(z,0,{-216,0,-162/5,-108*EulerGamma,-432/175,-54/5*EulerGamma,-36*(87/\n" //
            + "28000+EulerGamma^2/2+3*(17/126000+1/12*(EulerGamma^2+Pi^2/6))),-387/700*EulerGamma,-\n" //
            + "27*(11/91875+EulerGamma^2/30+4*(563/388080000+1/120*(-EulerGamma^2-Pi^2/6)+1/240*(EulerGamma^\n" //
            + "2+Pi^2/6))+2/5*(17/126000+1/12*(EulerGamma^2+Pi^2/6))),-24*(37/112000*EulerGamma+\n" //
            + "1/12*EulerGamma*(87/28000+EulerGamma^2/2+3*(17/126000+1/12*(EulerGamma^2+Pi^2/6)))+\n" //
            + "5/4*EulerGamma*(17/126000+1/12*(EulerGamma^2+Pi^2/6))+9/2*(EulerGamma/60480+6*(-EulerGamma/\n" //
            + "362880+1/1296*(EulerGamma^3+1/2*EulerGamma*Pi^2-PolyGamma(2,1))))),108/5*(-43/\n" //
            + "50400*EulerGamma^2+11/8400*(-87/28000-EulerGamma^2/2-3*(17/126000+1/12*(EulerGamma^\n" //
            + "2+Pi^2/6)))+1/80*(11/91875+EulerGamma^2/30+4*(563/388080000+1/120*(-EulerGamma^2-Pi^\n" //
            + "2/6)+1/240*(EulerGamma^2+Pi^2/6))+2/5*(17/126000+1/12*(EulerGamma^2+Pi^2/6)))+2/\n" //
            + "75*(-17/126000+1/12*(-EulerGamma^2-Pi^2/6))-5*(127/50450400000+1/2400*(-EulerGamma^\n" //
            + "2-Pi^2/6)+13/25200*(EulerGamma^2+Pi^2/6))+11/20*(-563/388080000+1/240*(-EulerGamma^\n" //
            + "2-Pi^2/6)+1/120*(EulerGamma^2+Pi^2/6)))},-9,2,1)");

    check("Series(Gamma(Sin(x) - x)^3, x -> 0) // InputForm", //
        "SeriesData(x,0,{-216},-9,-7,1)");

  }

  @Test
  public void testGammaPowerAnomaly() {
    // Check the innermost argument to ensure the base polynomial is correct
    check("Series(Sin(z) - z, {z, 0, 5}) // InputForm", //
        "SeriesData(z,0,{-1/6,0,1/120},3,6,1)");

    // Check the raw inversion of the inner argument (Tests the probeLimit loop!)
    check("Series(1 / (Sin(z) - z), {z, 0, 1})// InputForm", //
        "SeriesData(z,0,{-6,0,-3/10,0,-11/1400},-3,2,1)");

    // Check the cubed inversion
    check("Series(1 / (Sin(z) - z)^3, {z, 0, -5})// InputForm", //
        "SeriesData(z,0,{-216,0,-162/5,0,-432/175},-9,-4,1)");

    // different from WMA
    check("Series(Gamma(Sin(z) - z), {z, 0, 0})// InputForm", //
        "SeriesData(z,0,{-6,0,-3/10,-EulerGamma},-3,1,1)");

    // Check the manual rewrite rule that our code applies internally
    check("Series(Gamma(Sin(z) - z + 1) / (Sin(z) - z), {z, 0, 0})// InputForm", //
        "SeriesData(z,0,{-6,0,-3/10,-EulerGamma},-3,1,1)");
  }

  @Test
  public void testLeadingTermRule() {
    check("Series(Exp(u),{u,Infinity,4})// InputForm", //
        "E^SeriesData(u,Infinity,{1},-1,5,1)");
    check("Series(-u, u -> Infinity) // InputForm", //
        "SeriesData(u,Infinity,{-1},-1,2,1)");
    check("Series(u, u -> Infinity) // InputForm", //
        "SeriesData(u,Infinity,{1},-1,2,1)");
    check("Series(Exp(u), u -> Infinity) // InputForm", //
        "E^SeriesData(u,Infinity,{1},-1,2,1)");
    check("Series(Sin(x), x -> 0) // InputForm", //
        "SeriesData(x,0,{1},1,2,1)");
    check("Series(Cos(x) - 1, x -> 0) // InputForm", //
        "SeriesData(x,0,{-1/2},2,3,1)");
    check("Series(Exp(x) - 1 + x^2, x -> 0) // InputForm", //
        "SeriesData(x,0,{1},1,2,1)");
  }

  // @Test
  // public void testLeadingTermRuleSlowTest() {
  // check("Series(Gamma(Sin(x) - x)^3, x -> 0) // InputForm", //
  // "SeriesData(x,0,{-216},-9,-7,1)");
  // }

  @Test
  public void testSeriesInfinity() {
    // TODO
    // check("Series(ArcTan(x), {x, Infinity, 5}) // InputForm", //
    // "Pi/2-1/x+1/(3*x^3)-1/(5*x^5)+O(1/x)^6");
    // check("Series(ArcTan(x), {x, -Infinity, 5})", //
    // "-Pi/2-1/x+1/(3*x^3)-1/(5*x^5)+O(1/x)^6");

    check("SeriesData(x,Infinity,{1,0,-1/6,0,1/120},1,6,1)", //
        "1/x-1/(6*x^3)+1/(120*x^5)+O(1/x)^6");
    check("Series(Sin(1/x), {x, -Infinity, 5})", //
        "1/x-1/(6*x^3)+1/(120*x^5)+O(1/x)^6");

    check("Series(Exp(1/x), {x, Infinity, 5})", //
        "1+1/x+1/(2*x^2)+1/(6*x^3)+1/(24*x^4)+1/(120*x^5)+O(1/x)^6");

    check("Series((x^2+1)/(x-1), {x, Infinity, 3})", //
        "x+1+2/x+2/x^2+2/x^3+O(1/x)^4");

    check("Series(x * Sin(1/x), {x, Infinity, 4})", //
        "1-1/(6*x^2)+1/(120*x^4)+O(1/x)^5");
  }


  @Test
  public void testPadeApproximant() {
    // message PadeApproximant: lbcf not invertible {{-1,-2,3}}
    check("PadeApproximant({{-1,-2,3}},{0,0,{0,0}})", //
        "PadeApproximant({{-1,-2,3}},{0,0,{0,0}})");

    // TODO https://github.com/kredel/java-algebra-system/issues/29
    check("PadeApproximant(((x^3 + 72*x^2 + 600*x + 720)/(12*x^2 + 240*x+720)),{x,0,{3,1}})", //
        "(15/4+23/8*x+3/16*x^2-x^3/192)/(15/4+x)");
    check("PadeApproximant( x^3 + 72*x^2 + 600*x + 720 , {x, 0, {3,1}})", //
        "720+600*x+72*x^2+x^3");
    check("PadeApproximant(x, {x, 0, {3,1}})", //
        "x");
    check("PadeApproximant(x^3, {x, 0, {3,1}})", //
        "x^3");
    check("PadeApproximant(x*y^2, {x, 0, {3,1}})", //
        "x*y^2");
    // TODO
    check("PadeApproximant(Sin(x), {x, 0, {3,1}})", //
        "PadeApproximant(Sin(x),{x,0,{3,1}})");
  }

  @Test
  public void testPolyGammaSeries() {
    check("Series(PolyGamma[x], {x, 1, 4})", //
        "-EulerGamma+1/6*Pi^2*(-1+x)+1/2*PolyGamma(2,1)*(1-x)^2+1/90*Pi^4*(-1+x)^3+1/24*PolyGamma(\n"
            + "4,1)*(1-x)^4+O(-1+x)^5");
  }

  @Test
  public void testPowerSeries() {
    check("Series((a + x)^n, {x, 0, 2}) // InputForm", //
        "SeriesData(x,0,{a^n,n/a^(1-n),((-1+n)*n)/(2*a^(2-n))},0,3,1)");
    // "a^n+(n*x)/a^(1-n)+((-1+n)*n*x^2)/(2*a^(2-n))+O(x)^3");
    check("Series((a(x) + x)^n, {x, 0, 2})", //
        "a(0)^n+(n*(1+a'(0))*x)/a(0)^(1-n)+1/2*(((-1+n)*n*(1+a'(0))^2)/a(0)^(2-n)+(n*a''(\n"
            + "0))/a(0)^(1-n))*x^2+O(x)^3");
    check("Series(x^x, {x, 0, 4})", //
        "1+Log(x)*x+1/2*Log(x)^2*x^2+1/6*Log(x)^3*x^3+1/24*Log(x)^4*x^4+O(x)^5");
  }

  @Test
  public void testLaurentSeries() {
    // Test basic Laurent series generation with negative exponents

    check("Series(1/x + x, {x, 0, 2})", //
        "1/x+x+O(x)^3");

    check("Series(Cos(x)/x^2, {x, 0, 3})", //
        "1/x^2-1/2+x^2/24+O(x)^4");
  }

  @Test
  public void testPuiseuxSeriesFractionalPower() {
    // Test Puiseux series with explicit fractional powers causing denominator > 1

    // Series(Sqrt(x), (x, 0, 2))
    check("Series(Sqrt(x), {x, 0, 2}) // InputForm", //
        "SeriesData(x,0,{1},1,5,2)");

    // Series(1/Sqrt(x), (x, 0, 2)) -> Laurent and Puiseux combined
    check("Series(1/Sqrt(x), {x, 0, 2}) // InputForm", //
        "SeriesData(x,0,{1},-1,5,2)");

    // Series(x^(3/2) + x^(5/2), (x, 0, 3))
    check("Series(x^(3/2) + x^(5/2), {x, 0, 3}) // InputForm", //
        "SeriesData(x,0,{1,0,1},3,7,2)");
  }

  @Test
  public void testInverseSeriesPuiseux() {
    // Test InverseSeries yielding a Puiseux series (Lagrange inversion with fractional roots)

    // InverseSeries(Series(x^2, (x, 0, 4))) -> should yield Sqrt(x)
    check("InverseSeries(Series(x^2, {x, 0, 4})) // InputForm", //
        "SeriesData(x,0,{1},1,4,2)");

    // InverseSeries(Series(x^2 - x^4, (x, 0, 4)))
    // Reversion of w = x^2 - x^4 yields x = w^(1/2) + 1/2 w^(3/2) + ...
    check("InverseSeries(Series(x^2 - x^4, {x, 0, 4})) // InputForm", //
        "SeriesData(x,0,{1,0,1/2},1,4,2)");

    // InverseSeries(Series(x^3 + x^4, (x, 0, 4)))
    // Reversion of a cubic leading term yields a denominator of 3
    check("InverseSeries(Series(x^3 + x^4, {x, 0, 4})) // InputForm", //
        "SeriesData(x,0,{1,-1/3},1,3,3)");
  }

  @Test
  public void testLogarithmicPoles() {
    // Inverse of a logarithmic singularity yielding a Laurent series starting at x^-1
    // 1 / (x - x^2/2 + x^3/3 - ...) = x^-1 + 1/2 - x/12 + x^2/24 + ...
    check("Series(1/Log(1+x), {x, 0, 2}) // InputForm", //
        "SeriesData(x,0,{1,1/2,-1/12,1/24},-1,3,1)");

    // Cancellation of the pole by multiplying with x
    check("Series(x/Log(1+x), {x, 0, 3}) // InputForm", //
        "SeriesData(x,0,{1,1/2,-1/12,1/24},0,4,1)");

    // Logarithmic expansion around x0 = 1, generating a pole due to the denominator
    check("Series(Log(x)/(x-1)^2, {x, 1, 2}) // InputForm", //
        "SeriesData(x,1,{1,-1/2,1/3,-1/4},-1,3,1)");
  }

  @Test
  public void testLogarithmicExpansionsAtInfinity() {
    // Logarithmic mapping x -> 1/x at Infinity
    check("Series(Log(1+1/x), {x, Infinity, 3}) // InputForm",
        "SeriesData(x,Infinity,{1,-1/2,1/3},1,4,1)");

    // Infinity limit where leading term is a constant (x^0)
    check("Series(x * Log(1+1/x), {x, Infinity, 2}) // InputForm",
        "SeriesData(x,Infinity,{1,-1/2,1/3},0,3,1)");

    // Series combining Exponentials and Logarithms at Infinity yielding poles
    check("Series(Exp(1/x) / Log(1+1/x), {x, Infinity, 2}) // InputForm",
        "SeriesData(x,Infinity,{1,3/2,11/12,3/8},-1,3,1)");
  }

  @Test
  public void testLogarithmicTrigonometricPoles() {
    // Multiplying a Laurent trigonometric pole (Cot) with a standard Taylor Log expansion
    // Cot(x) = x^-1 - x/3 ... multiplied by Log(1+x) = x - x^2/2 ...
    // Yields an exact integer bounded series starting at x^0
    check("Series(Cot(x)*Log(1+x), {x, 0, 2}) // InputForm", //
        "SeriesData(x,0,{1,-1/2},0,3,1)");

    // Deeper pole interplay: Csc(x)^2 generates an x^-2 pole, neutralized heavily by Log(1+x)^2
    check("Series(Csc(x)^2 * Log(1+x)^2, {x, 0, 2}) // InputForm", //
        "SeriesData(x,0,{1,-1,5/4},0,3,1)");
  }

  @Test
  public void testLeadingTermInfinity() {
    // TODO returns wrong SeriesData(x,-Infinity,{1},4,5,1)
    check("Series(1/(x^4+1), x -> -Infinity) // InputForm", //
        "SeriesData(x,-Infinity,{1},4,5,1)");
    // TODO returns wrong SeriesData(x,Infinity,{1},1,2,1)
    check("Series(Sin(1/x), x -> Infinity) // InputForm", //
        "SeriesData(x,Infinity,{1},1,2,1)");
    // TODO returns wrong SeriesData(x,-Infinity,{1},1,2,1)
    check("Series(Sin(1/x), x -> -Infinity) // InputForm", //
        "SeriesData(x,-Infinity,{1},1,2,1)");
    check("Series((1 + x^2)/(1 - x), x -> Infinity) // InputForm", //
        "SeriesData(x,Infinity,{-1},-1,0,1)");

    check("Series(Cos(1/x), x -> Infinity) // InputForm", //
        "SeriesData(x,Infinity,{1},0,2,1)");
    check("Series(Exp(1/x) - 1, x -> Infinity) // InputForm", //
        "SeriesData(x,Infinity,{1},1,2,1)");

    check("Series((1 + x^2)/(1 - x), x -> -Infinity) // InputForm", //
        "SeriesData(x,-Infinity,{-1},-1,0,1)");
  }

}
