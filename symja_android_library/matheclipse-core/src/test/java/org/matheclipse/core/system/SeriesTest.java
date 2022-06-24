package org.matheclipse.core.system;

import org.matheclipse.core.basic.ToggleFeature;

public class SeriesTest extends ExprEvaluatorTestCase {

  public SeriesTest(String name) {
    super(name);
  }

  public void testComposeSeries() {
    check("ComposeSeries(Series(Exp(x), {x, 0, 10}), Series(Sin(x), {x, 0, 10}))", //
        "1+x+x^2/2-x^4/8-x^5/15-x^6/240+x^7/90+31/5760*x^8+x^9/5670-2951/3628800*x^10+O(x)^\n"
            + "11");
    check("ComposeSeries(Series(x*Exp(x), {x, 0, 10}), Series(Sin(x), {x, 0, 10}))", //
        "x+x^2+x^3/3-x^4/6-x^5/5-7/120*x^6+13/630*x^7+37/1680*x^8+2/405*x^9-4043/1814400*x^\n"
            + "10+O(x)^11");
    // TODO check power
    check("Series(Sin(x)*Exp(Sin(x)), {x, 0, 10})", //
        "x+x^2+x^3/3-x^4/6-x^5/5-7/120*x^6+13/630*x^7+37/1680*x^8+2/405*x^9-4043/1814400*x^\n"
            + "10+O(x)^11");
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

  public void testInverseSeries() {
    check("InverseSeries(SeriesData(x,0,{1,1,1},0,3,1))", //
        "(-1+x)-(1-x)^2+O(-1+x)^3");
    check("InverseSeries(SeriesData(x,x0,{1,1,1},0,3,1))", //
        "x0+(-1+x)-(1-x)^2+O(-1+x)^3");
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
    check("InverseSeries(Series(Sin(x), {x, 0, 9}))", //
        "x+x^3/6+3/40*x^5+5/112*x^7+35/1152*x^9+O(x)^10");
    check("InverseSeries(Series(ArcSin(x), {x, 0, 9}))", //
        "x-x^3/6+x^5/120-x^7/5040+x^9/362880+O(x)^10");
    check("InverseSeries(Series(Log(x+1), {x, 0, 9}))", //
        "x+x^2/2+x^3/6+x^4/24+x^5/120+x^6/720+x^7/5040+x^8/40320+x^9/362880+O(x)^10");
  }

  public void testNormal() {
    check("Normal(Series(Product((1+x^(k*(k+1)/2)), {k, 3}), {x, 0,6}))", //
        "1+x+x^3+x^4+x^6");
    check("Normal(SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1))", //
        "1/x-x-4*x^2-17*x^3-88*x^4-549*x^5");
    check("Normal(Series(Exp(x),{x,0,5}))", //
        "1+x+x^2/2+x^3/6+x^4/24+x^5/120");
    if (ToggleFeature.SERIES_DENOMINATOR) {
      check("Normal(SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2))", //
          "Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880");
    }
  }

  public void testSeriesTaylor() {
    // issue #545
    check("Series(((x^3 + 72*x^2 + 600*x + 720)/(12*x^2 + 240*x+720)),{x,0,6})", //
        "1+x/2-x^2/12+x^3/48-x^4/180+13/8640*x^5-53/129600*x^6+O(x)^7");
    check("Series(1/(1-x),{x,0,2})", //
        "1+x+x^2+O(x)^3");
  }

  public void testSeries() {
    // TODO check max power
    check("Series(Sin(x)^2,{x,0,5})//FullForm", //
        "SeriesData(x, 0, List(1, 0, Rational(-1,3)), 2, 6, 1)");

    check("Series(b ,{x,0,5})//FullForm", //
        "b");
    check("Series(b *x ,{x,0,5})//FullForm", //
        "SeriesData(x, 0, List(b), 1, 6, 1)");
    check("Series(b *x ,{x,0,-2})//FullForm", //
        "SeriesData(x, 0, List(), 0, 1, 1)");

    check("Series(Sin(x),{x,2,3})", //
        "Sin(2)+Cos(2)*(-2+x)-1/2*Sin(2)*(2-x)^2-1/6*Cos(2)*(-2+x)^3+O(-2+x)^4");
    // TODO handle positive>/negative fractional powers
    // check("Series(Sqrt(Sin(x)), {x, 0, 10})", //
    // "");
    // check("Series(1/Sin(x)^10, {x, 0, 2})",//
    // "");

    // check("Series(Gamma(x), {x, 0, 3})", //
    // "");
    // check("Series(1/(x^3+x), {x, 0, 3})", //
    // "");
    // check("Series((x^3-2x^2-9x+18)/(x^3+x), {x, 0, 3})", //
    // "");

    check("Series((1 + x)^n, {x, 0, 4})", //
        "1+n*x+1/2*(-1+n)*n*x^2+1/6*(-2+n)*(-1+n)*n*x^3+1/24*(-3+n)*(-2+n)*(-1+n)*n*x^4+O(x)^\n"
            + "5");
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

    // TODO check max power
    check("Series(Sin(x)^3,{x,0,5})//FullForm", //
        "SeriesData(x, 0, List(1, 0, Rational(-1,2)), 3, 6, 1)");

    // TODO check power value
    check("Series(Sin(x) ,{x,0,-2})//FullForm", //
        "SeriesData(x, 0, List(), 1, 1, 1)");
    // TODO check power value
    check("Series(b*Sin(x) ,{x,0,-2})//InputForm", //
        "SeriesData(x,0,{},1,1,1)");

    check("Series(b*x ,{x,0,-2})//InputForm", //
        "SeriesData(x,0,{},0,1,1)");
    check("Series(b*x ,{x,0,5})//InputForm", //
        "SeriesData(x,0,{b},1,6,1)");

    // TODO check power value
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
    // TODO check power value
    check("s1^2+s2^2", //
        "1+O(x)^11");
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

    // TODO check power
    check("s1*s2", //
        "1+2*x+4*x^2+10*x^3+34*x^4+154*x^5+874*x^6+5914*x^7+46234*x^8+409114*x^9+4037914*x^\n" + //
            "10+O(x)^11");

    check("Series(x*4+x^2-y*x^10, {x, 0, 10})", "4*x+x^2-y*x^10+O(x)^11");
    check("Series(x*4+x^2-y*x^11, {x, 0, 10})", "4*x+x^2+O(x)^11");

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

    check("Series(Sin(f(x)),{x,0,2})",
        "Sin(f(0))+Cos(f(0))*f'(0)*x+1/2*(-Sin(f(0))*f'(0)^2+Cos(f(0))*f''(0))*x^2+O(x)^3");
    check("Series(Sin(x),{x,0,2})", "x+O(x)^3");

    check("Series(f(x),{x,a,3})",
        "f(a)+f'(a)*(-a+x)+1/2*f''(a)*(-a+x)^2+1/6*Derivative(3)[f][a]*(-a+x)^3+O(-a+x)^4");
    check("Series(Exp(x),{x,0,2})", "1+x+x^2/2+O(x)^3");
    check("Series(Exp(f(x)),{x,0,2})",
        "E^f(0)+E^f(0)*f'(0)*x+1/2*(E^f(0)*f'(0)^2+E^f(0)*f''(0))*x^2+O(x)^3");
    check("Series(Exp(x),{x,0,5})", "1+x+x^2/2+x^3/6+x^4/24+x^5/120+O(x)^6");
    check("Series(100,{x,a,5})", "100");
    check("Series(y,{x,a,5})", "y");
  }

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

  public void testSeriesData() {
    check("Series(Exp(x), {x,0,2}) // FullForm", //
        "SeriesData(x, 0, List(1, 1, Rational(1,2)), 0, 3, 1)");

    check(
        "s1=SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)*SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)", //
        "x^2-x^4/3+2/45*x^6-x^8/360+x^10/14400+O(x)^11");

    // check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, -2, 11, 1)^2", //
    // "1/x^4-1/(3*x^2)+2/45-x^2/360+x^4/14400+O(x)^9");
    // check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, -2, 11, 3)^2", //
    // "1/(x^(4/3))-1/(3*x^(2/3))+2/45-x^(2/3)/360+x^(4/3)/14400+O(x)^3");

    // TODO wrong denominator handling
    // check("SeriesData(x, 0,{1,1,1,1,1}, 1, 11,3) + SeriesData(x, 0,{1,1,1,1,1}, 1, 4,3)",
    // "2*x^(1/3)+2*x^(2/3)+2*x+O(x)^(4/3)");
    // check("SeriesData(x, 0,{1,1,1,1,1}, 1, 11,3) + SeriesData(x, 0,{1,1,1,1,1}, 1, 4,5)",
    // "2*x^(1/3)+2*x^(2/3)+2*x+O(x)^(4/3)");
    if (ToggleFeature.SERIES_DENOMINATOR) {
      // TODO O(x)^(13/3)
      check(
          "SeriesData(x, 0, {1, 0, -1/3, 0, 2/45, 0, -1/360, 0, 1/14400}, 2, 12, 3)*SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 3)", //
          "x-x^(5/3)/2+13/120*x^(7/3)-7/540*x^3+13/14400*x^(11/3)+O(x)^(13/3)");
    }
    check("SeriesData(x, 0,{1,1,1,1,1}, 1, 11)", //
        "x+x^2+x^3+x^4+x^5+O(x)^11");
    check("SeriesData(x, 0,{1,1,1,1,1}, 1, 11)^2", //
        "x^2+2*x^3+3*x^4+4*x^5+5*x^6+4*x^7+3*x^8+2*x^9+x^10+O(x)^11");
    // check("SeriesData(x, 0,{1,1,1,1,1}, 1, 11)*SeriesData(x, 0,{1,1,1,1,1}, 1, 5)", //
    // "x^2+2*x^3+3*x^4+4*x^5+O(x)^6");
    // check("SeriesData(x, 0,{1,1,1,1,1}, 1, 11, 3)*SeriesData(x, 0,{1,1,1,1,1}, 1, 5, 3)", //
    // "x^(2/3)+2*x+3*x^(4/3)+4*x^(5/3)+O(x)^2");

    // TODO check order
    check("s1=SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^2//FullForm", //
        "SeriesData(x, 0, List(1, 0, Rational(-1,3), 0, Rational(2,45), 0, Rational(-1,360), 0, Rational(1,14400)), 2, 11, 1)");
    if (ToggleFeature.SERIES_DENOMINATOR) {
      check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, -2, 11, 3)", //
          "1/x^(2/3)-1/6+x^(2/3)/120+O(x)^(11/3)");
      check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 3)^2", //
          "x^(2/3)-x^(4/3)/3+2/45*x^2-x^(8/3)/360+x^(10/3)/14400+O(x)^4");
      check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 3)^3", //
          "x-x^(5/3)/2+13/120*x^(7/3)-7/540*x^3+13/14400*x^(11/3)+O(x)^(13/3)");
      check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 3)", //
          "x^(1/3)-x/6+x^(5/3)/120+O(x)^(11/3)");
    }

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
    check("s1=SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^2", //
        "x^2-x^4/3+2/45*x^6-x^8/360+x^10/14400+O(x)^11");
    check("s1 // FullForm", //
        "SeriesData(x, 0, List(1, 0, Rational(-1,3), 0, Rational(2,45), 0, Rational(-1,360), 0, Rational(1,14400)), 2, 11, 1)");
    check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^3", //
        "x^3-x^5/2+13/120*x^7-7/540*x^9+O(x)^11");

    // check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^12", //
    // "x^12-2*x^14+29/15*x^16-649/540*x^18+3883/7200*x^20+O(x)^22");

    check(
        "SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)*SeriesData(x, 0,{1,0,-1/5,0,1/110}, 1, 11, 1) // FullForm", //
        "SeriesData(x, 0, List(1, 0, Rational(-11,30), 0, Rational(67,1320), 0, Rational(-7,2200), 0, Rational(1,13200)), 2, 11, 1)");
    check(
        "SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)*SeriesData(x, 0,{1,0,-1/5,0,1/110}, 1, 11, 1)", //
        "x^2-11/30*x^4+67/1320*x^6-7/2200*x^8+x^10/13200+O(x)^11");
    check(
        "SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)-SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)", //
        "O(x)^11");
    check(
        "SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)+SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)", //
        "2*x-x^3/3+x^5/60+O(x)^11");
    if (ToggleFeature.SERIES_DENOMINATOR) {
      check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)*7",
          "7*Sqrt(x)-7/6*x^(3/2)+7/120*x^(5/2)-x^(7/2)/720+x^(9/2)/51840+O(x)^(11/2)");
      check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)+4", //
          "4+Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)");
      check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)",
          "Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)");
    }

    check("SeriesData(100, 0, Table(i^2, {i, 10}), 0, 10, 1)", //
        "Indeterminate");
    check("SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1)", //
        "1+4*x+9*x^2+16*x^3+25*x^4+36*x^5+49*x^6+64*x^7+81*x^8+100*x^9+O(x)^10");
    check("D(SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1), x)", //
        "4+18*x+48*x^2+100*x^3+180*x^4+294*x^5+448*x^6+648*x^7+900*x^8+O(x)^9");
    check("Integrate(SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1), x)", //
        "x+2*x^2+3*x^3+4*x^4+5*x^5+6*x^6+7*x^7+8*x^8+9*x^9+10*x^10+O(x)^11");
  }

  public void testDSeriesData() {
    check("D(SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1), x)", //
        "4+18*x+48*x^2+100*x^3+180*x^4+294*x^5+448*x^6+648*x^7+900*x^8+O(x)^9");
    check("D(Series(Log(x),{x,a,5}), x)", //
        "1/a-(-a+x)/a^2+(-a+x)^2/a^3-(-a+x)^3/a^4+(-a+x)^4/a^5+O(-a+x)^5");
  }

  public void testIntegrateSeriesData() {
    check("Integrate(SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1), x)", //
        "x+2*x^2+3*x^3+4*x^4+5*x^5+6*x^6+7*x^7+8*x^8+9*x^9+10*x^10+O(x)^11");
    check("Integrate(Series(Log(x),{x,a,5}), x)", //
        "Log(a)*(-a+x)+(-a+x)^2/(2*a)-(-a+x)^3/(6*a^2)+(-a+x)^4/(12*a^3)-(-a+x)^5/(20*a^4)+(-a+x)^\n"
            + "6/(30*a^5)+O(-a+x)^7");
  }

  public void testSeriesCoefficient() {
    // check("SeriesCoefficient(x^x,{x,2,4})", //
    // " ");
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
    check("SeriesCoefficient(a^x, {x, 0, n})", "Piecewise({{Log(a)^n/n!,n>=0}},0)");

    check("SeriesCoefficient(x^x,{x,0,4})", //
        "Log(x)^4/24");

    check("SeriesCoefficient(ChebyshevT(k, x), {x, 0, 2})", //
        "((-1+k)*(1+k)*k^2*Pi)/(8*Gamma(1/2*(3-k))*Gamma(1/2*(3+k)))");
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
    check("SeriesCoefficient(Cot(x),{x,0,3})", //
        "-1/45");
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

  public void testExpSeries() {
    check("SeriesCoefficient(Exp(x), {x, a, n})", //
        "Piecewise({{E^a/n!,n>=0}},0)");
    check("Series(Exp(1/x), {x, 0, 2})", //
        "Series(E^(1/x),{x,0,2})");
    // TODO
    // check("Series(Exp(1/x), {x, Infinity, 3})", //
    // " ");
  }

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

  public void testSinSeries() {
    check("Series(Sin(x)/x^2, {x, 0, 3})", //
        "1/x-x/6+x^3/120+O(x)^4");
  }

  public void testPolyGammaSeries() {
    check("Series(PolyGamma[x], {x, 1, 4})", //
        "-EulerGamma+1/6*Pi^2*(-1+x)+1/2*PolyGamma(2,1)*(1-x)^2+1/90*Pi^4*(-1+x)^3+1/24*PolyGamma(\n"
            + "4,1)*(1-x)^4+O(-1+x)^5");
  }

  public void testPowerSeries() {
    check("Series((a + x)^n, {x, 0, 2})", //
        "a^n+(n*x)/a^(1-n)+((-1+n)*n*x^2)/(2*a^(2-n))+O(x)^3");
    check("Series((a(x) + x)^n, {x, 0, 2})", //
        "a(0)^n+(n*(1+a'(0))*x)/a(0)^(1-n)+1/2*(((-1+n)*n*(1+a'(0))^2)/a(0)^(2-n)+(n*a''(\n"
            + "0))/a(0)^(1-n))*x^2+O(x)^3");
    check("Series(x^x, {x, 0, 4})", //
        "1+Log(x)*x+1/2*Log(x)^2*x^2+1/6*Log(x)^3*x^3+1/24*Log(x)^4*x^4+O(x)^5");
  }
}
