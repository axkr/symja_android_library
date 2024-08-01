package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.BuiltinFunctionCalls;

public class IntegrateTest extends ExprEvaluatorTestCase {

  @Test
  public void testIntegrateDefinite() {
    check("Integrate(1/(x^3+1), {x,0,1})", //
        "Pi/(3*Sqrt(3))+Log(2)/3");
    check("Integrate(1/(x^3+1), x)", //
        "ArcTan((-1+2*x)/Sqrt(3))/Sqrt(3)+Log(1+x)/3-Log(1-x+x^2)/6");
    check("Integrate(x^4+x^2+1, {x,1,3})", //
        "886/15");
    check("Integrate(a*x^2 + b*x + c, {x,-2,2})", //
        "16/3*a+4*c");
    check("Integrate((4*x^2-7*x- 12)/((x+2)*(x-3)), {x, -1, 2})", //
        "12-21/5*Log(4)");
    check("Integrate(1/((2 + x^2)*Sqrt(4 + 3*x^2)),x)", //
        "ArcTanh(x/Sqrt(4+3*x^2))/2");
    // same as ArcCosh(Sqrt(3/2))
    check("Integrate(1/((2 + x^2)*Sqrt(4 + 3*x^2)), {x, -Infinity, Infinity})", //
        "ArcTanh(1/Sqrt(3))");
    check("Integrate((1 + x^3)*x^(1/3), {x, -1, 1})", //
        "51/52+27/52*(-1)^(1/3)");
    if (Config.PROFILE_MODE) {
      BuiltinFunctionCalls.printStatistics();
    }
  }

  @Test
  public void testIntegrateSurd() {
    check("Integrate(Surd(x,-1), x)", //
        "Log(x)");
    check("Integrate(CubeRoot(x), x)", //
        "3/4*x*Surd(x,3)");
    check("Integrate(Surd(x,3), x)", //
        "3/4*x*Surd(x,3)");
    check("Integrate(Surd(x,6), x)", //
        "6/7*x*Surd(x,6)");
    check("Integrate(Surd(x,-5), x)", //
        "5/4*x/Surd(x,5)");
    check("Integrate(Surd(x,-3), x)", //
        "3/2*x/Surd(x,3)");
    check("Integrate(x^3*CubeRoot(x), x)", //
        "3/13*x^4*Surd(x,3)");
    check("Integrate((1 + x^3)*CubeRoot(x), {x, -1, 1})", //
        "6/13");
    check("Integrate(x^a*CubeRoot(x)^p, x)", //
        "(x^(1+a)*Surd(x,3)^p)/(1+a+p/3)");
  }

  @Test
  public void testIntegrateIssue330() {
    check("Integrate(x*ArcSin(x) ,x)", //
        "1/6*x^2*(3*ArcSin(x)+3/2*(x*(Sqrt(1-x^2)*Sqrt(x^2)-ArcSin(Sqrt(x^2))))/(x^2)^(3/\n" //
            + "2))");
    check("Limit(x/Sqrt(1-x^2),x->1)", //
        "Indeterminate");
    check("Integrate(x*(1/2*Pi-ArcSin(x)),x)", //
        "1/4*x^2*(Pi-2*ArcSin(x))+1/4*(-1+x^2)*(x/Sqrt(1-x^2)-ArcTan(x/Sqrt(1-x^2))+(-x^2*ArcTan(x/Sqrt(\n"
            + "1-x^2)))/(1-x^2))");
  }

  @Test
  public void testIntegrateIssue851() {
    check("Integrate(x^n*Haversine(m*x^p),x)", //
        "(x^(1+n)*(2*p*(m^2*x^(2*p))^((1+n)/p)+(1+n)*(I*m*x^p)^((1+n)/p)*Gamma((1+n)/p,-I*m*x^p)+(\n" //
            + "1+n)*(-I*m*x^p)^((1+n)/p)*Gamma((1+n)/p,I*m*x^p)))/(4*(1+n)*p*(m^2*x^(2*p))^((1+n)/p))");
    check("Integrate(x^n*ArcSin(m*x^p),x)", //
        "(x^(1+n)*ArcSin(m*x^p))/(1+n)+(-m*p*x^(1+n+p)*Hypergeometric2F1(1/2,(1+n+p)/(2*p),\n"
            + "1+(1+n+p)/(2*p),m^2*x^(2*p)))/((1+n)*(1+n+p))");
    check("Integrate(x^n*ArcSinh(m*x^p),x)", //
        "(x^(1+n)*ArcSinh(m*x^p))/(1+n)+(-m*p*x^(1+n+p)*Hypergeometric2F1(1/2,(1+n+p)/(2*p),\n"
            + "1+(1+n+p)/(2*p),-m^2*x^(2*p)))/((1+n)*(1+n+p))");

    check("Integrate(PolyGamma(m*x),x)", //
        "LogGamma(m*x)/m");

    check("Integrate(LogisticSigmoid(m*x),x)", //
        "-Log(1-LogisticSigmoid(m*x))/m");
    check("Integrate(Haversine(m*x),x)", //
        "x/2-Sin(m*x)/(2*m)");
    check("Integrate(x^n*Haversine(m*x),x)", //
        "1/4*x^n*((2*x)/(1+n)+(x*Gamma(1+n,-I*m*x))/(-I*m*x)^(1+n)+(x*Gamma(1+n,I*m*x))/(I*m*x)^(\n"//
            + "1+n))");
    check("Integrate(InverseHaversine(m*x),x)", //
        "(Sqrt(m*x*(1-m*x))+(-1+2*m*x)*ArcSin(Sqrt(m*x)))/m");
    check("Integrate(x^n*InverseHaversine(m*x),x)", //
        "(2*x^(1+n)*((3+2*n)*ArcSin(Sqrt(m*x))-Sqrt(m*x)*Hypergeometric2F1(1/2,3/2+n,5/2+n,m*x)))/((\n"//
            + "1+n)*(3+2*n))");

    check("Integrate(InverseErf(m*x),x)", //
        "-1/(E^InverseErf(m*x)^2*m*Sqrt(Pi))");
    check("Integrate(InverseErfc(m*x),x)", //
        "1/(E^InverseErfc(m*x)^2*m*Sqrt(Pi))");

    check("Integrate(EllipticE(m*x),x)", //
        "2/3*((1+m*x)*EllipticE(m*x)+(-1+m*x)*EllipticK(m*x))/m");
    check("Integrate(EllipticK(m*x),x)", //
        "(2*(EllipticE(m*x)+(-1+m*x)*EllipticK(m*x)))/m");
    check("Integrate(x*EllipticE(m*x),x)", //
        "1/4*Pi*x^2*HypergeometricPFQ({-1/2,1/2,2},{1,3},m*x)");
    check("Integrate(x*EllipticK(m*x),x)", //
        "1/4*Pi*x^2*HypergeometricPFQ({1/2,1/2,2},{1,3},m*x)");
    check("Integrate(x^n*EllipticE(m*x),x)", //
        "(Pi*x^(1+n)*HypergeometricPFQ({-1/2,1/2,1+n},{1,2+n},m*x))/(2+2*n)");
    check("Integrate(x^n*EllipticK(m*x),x)", //
        "(Pi*x^(1+n)*HypergeometricPFQ({1/2,1/2,1+n},{1,2+n},m*x))/(2+2*n)");

    check("Integrate(x^n*CubeRoot(m*x),x)", //
        "(x^(1+n)*Surd(m*x,3))/(4/3+n)");
    check("Integrate(Surd(m*x,7),x)", //
        "7/8*x*Surd(m*x,7)");
    check("Integrate(x^n*Surd(m*x,7),x)", //
        "(x^(1+n)*Surd(m*x,7))/(8/7+n)");

    check("Integrate(x^n*ArcCot(11*Sin(s)*x),x)", //
        "(x^(1+n)*ArcCot(11*x*Sin(s)))/(1+n)+(11*x^(2+n)*Hypergeometric2F1(1,1/2*(2+n),1/\n"//
            + "2*(4+n),-121*x^2*Sin(s)^2)*Sin(s))/(2+3*n+n^2)");

    check("Integrate(ArcSin(x),x)", //
        "Sqrt(1-x^2)+x*ArcSin(x)");
    check("Integrate(x^(-3)*ArcSin(x),x)", //
        "(-x*Sqrt(1-x^2)-ArcSin(x))/(2*x^2)");

    check("Integrate(x*ArcSin(x),{x,0,1})", //
        "Pi/8");
    check("Integrate(x*ArcSin(x),x)", //
        "1/6*x^2*(3*ArcSin(x)+3/2*(x*(Sqrt(1-x^2)*Sqrt(x^2)-ArcSin(Sqrt(x^2))))/(x^2)^(3/\n" //
            + "2))");
    check("Integrate(x^n*ArcSin(m*x),x)", //
        "(x^(1+n)*ArcSin(m*x))/(1+n)+(-m*x^(2+n)*Hypergeometric2F1(1/2,1/2*(2+n),1/2*(4+n),m^\n" //
            + "2*x^2))/(2+3*n+n^2)");

    check("Integrate(x^n*ArcTanh(m*x),x)", //
        "(x^(1+n)*ArcTanh(m*x))/(1+n)+(-m*x^(2+n)*Hypergeometric2F1(1,1/2*(2+n),1/2*(4+n),m^\n"
            + "2*x^2))/(2+3*n+n^2)");
    check("Integrate(x*ArcTanh(3*x),{x,0,1})", //
        "1/6*(1/9*(9-3*ArcTanh(3))+3*ArcTanh(3))");
  }

  @Test
  public void testIntegrate() {
    // message Integrate: Invalid integration variable or limit(s) in {x}.
    check("Integrate(x^2,{x})", //
        "Integrate(x^2,{x})");
    check("Integrate(Cos(x^3), x)", //
        "(-x*Gamma(1/3,-I*x^3))/(6*(-I*x^3)^(1/3))+(-x*Gamma(1/3,I*x^3))/(6*(I*x^3)^(1/3))");
    check("Integrate(Round(1.235512+1.23787m, 0.01),m)", //
        "100000/123787*Rubi`subst[Integrate(Round(m,1/100),m),m,154439/125000+123787/\n"
            + "100000*m]");
    check("Integrate(Tan(x),Cos(x))", //
        "Integrate(Tan(x),Cos(x))");
    check("Integrate(Piecewise({{1/(2 x^2), Abs(x) > 1} },4),x)", //
        "Piecewise({{-1/(2*x),Abs(x)>1}},4*x)");
    check("Integrate(Piecewise({{x^2, x <= 0}, {x, x > 0}}),x)", //
        "Piecewise({{x^3/3,x<=0},{x^2/2,x>0}},0)");

    check("Integrate((Sinh(x)-x)/(x^2*Sinh(x)),x)", //
        "-1/x-Integrate(Csch(x)/x,x)");
    check("Refine(Integrate(Abs(E+Pi*x^(-8)),x), Element(x,Reals))", //
        "-Pi/(7*x^7)+E*x");
    check("Refine(Integrate(Abs(Pi+42*x^6),x), Element(x,Reals))", //
        "Pi*x+6*x^7");
    check("Refine(Integrate(Abs(E+Pi*x^(-1)),x), Element(x,Reals))", //
        "Piecewise({{E*x+Pi*Log(x),x<=-Pi/E},{-E*x+2*Pi*(-2+I*Pi+Log(Pi))-Pi*Log(x),-Pi/E<x&&x<=\n"
            + "0}},E*x+Pi*Log(x))");
    check("Refine(Integrate(Abs(E+2*x^(-1)),x), Element(x,Reals))", //
        "Piecewise({{E*x+2*Log(x),x<=-2/E},{-E*x+4*(-2+I*2+Log(2))-2*Log(x),-2/E<x&&x<=0}},E*x+\n"
            + "2*Log(x))");

    check("Refine(Integrate(Abs(E+2*x),x), Element(x,Reals))", //
        "Piecewise({{-E*x-x^2,x<=-E/2}},E^2/Pi+E*x+x^2)");
    check("Refine(Integrate(Abs(E+Pi*x),x), Element(x,Reals))", //
        "Piecewise({{-E*x-1/2*Pi*x^2,x<=-E/Pi}},E^2/Pi+E*x+1/2*Pi*x^2)");
    check("Refine(Integrate(Abs(a+b*x),x), Element(x,Reals))", //
        "Integrate(Abs(a+b*x),x)");
    check("Refine(Integrate(Abs(x),x), Element(x,Reals))", //
        "Piecewise({{-x^2/2,x<=0}},x^2/2)");
    check("Refine(Integrate(Abs(x^3),x), Element(x,Reals))", //
        "Piecewise({{-x^4/4,x<=0}},x^4/4)");
    check("Refine(Integrate(Abs(x^2),x), Element(x,Reals))", //
        "x^3/3");
    check("Refine(Integrate(Abs(x^4),x), Element(x,Reals))", //
        "x^5/5");

    check("Refine(Integrate(Abs(x^(-1)),x), Element(x,Reals))", //
        "Piecewise({{-Log(x),x<=0}},Log(x))");
    check("Refine(Integrate(Abs(x^(-5)),x), Element(x,Reals))", //
        "Piecewise({{1/(4*x^4),x<=0}},-1/(4*x^4))");
    check("Refine(Integrate(Abs(x^(-7)),x), Element(x,Reals))", //
        "Piecewise({{1/(6*x^6),x<=0}},-1/(6*x^6))");

    check("Refine(Integrate(Abs(x^(-2)),x), Element(x,Reals))", //
        "-1/x");
    check("Refine(Integrate(Abs(x^(-4)),x), Element(x,Reals))", //
        "-1/(3*x^3)");
    check("Refine(Integrate(Abs(x^(-6)),x), Element(x,Reals))", //
        "-1/(5*x^5)");

    // see github #155
    check("Integrate(x^2,{x,-2,2})", //
        "16/3");
    check("Integrate((x-1)^2,{x,-2,2})", //
        "28/3");
    check("Integrate(x^3,{x,-2,2})", //
        "0");
    check("Integrate((x-1)^3,{x,-2,2})", //
        "-20");

    // see github #153
    check("Integrate(E^(-x^2.0),{x,-1/2,1/2})", //
        "Sqrt(Pi)*Erf(1/2)");
    check("Integrate(E^(-x^2.0),{x,-1/2,1/2}) //N", //
        "0.922562");
    check("N(Integrate(E^(-x^2),{x,-1/2,1/2}))", //
        "0.922562");

    check("Integrate(Log(x^3)/E^(2+x),{x,1,2})", //
        "(3*ExpIntegralEi(-2))/E^2+(-3*ExpIntegralEi(-1))/E^2-Log(8)/E^4");
    // check("Limit(1/9*x*(9-x^2)^(3/2)*Hypergeometric2F1(1,2,3/2,x^2/9),x->3)", //
    // "");

    // see github #128
    // check("Apart(a/((8/3*a*b^(2/3)-16/9*b)^2*(4/3*a*b^(2/3)+16/9*b)))", //
    // "a/(a^3-4/3*a*b^(2/3)+16/27*b)");
    check("Integrate(a/(a^3-4/3*a*b^(2/3)+16/27*b),a)", //
        "-1/(3*a-2*b^(1/3))+Log(3*a-2*b^(1/3))/(3*b^(1/3))-Log(3*a+4*b^(1/3))/(3*b^(1/3))");
    check(
        "Simplify(D(-1/(3*a-2*b^(1/3))+Log(3*a-2*b^(1/3))/(3*b^(1/3))-Log(3*a+4*b^(1/3))/(3*b^(1/3)),a))", //
        "(27*a)/(27*a^3-36*a*b^(2/3)+16*b)"); // "3/(3*a-2*b^(1/3))^2+1/(3*a*b^(1/3)-2*b^(2/3))-1/(3*a*b^(1/3)+4*b^(2/3))");
    // expensive JUnit test
    // check("Integrate(((a+b)/(a^(1/3)+b^(1/3))-(a*b)^(1/3))/(a^(1/3)-b^(1/3))^2,a)", //
    // "a+3/2*a^(2/3)*b^(1/3)+6*a^(1/3)*b^(2/3)+(-3*b^(4/3))/(a^(1/3)-b^(1/3))-3/2*a^(1/\n" +
    // "3)*(a*b)^(1/3)-6*b^(1/3)*(a*b)^(1/3)+(3*b*(a*b)^(1/3))/(a^(1/3)*(a^(1/3)-b^(1/3)))+\n" +
    // "3*b*Log(a^(1/3)-b^(1/3))+(-9*b^(2/3)*(a*b)^(1/3)*Log(a^(1/3)-b^(1/3)))/a^(1/3)-3*b*Log(a^(\n"
    // +
    // "1/3)+b^(1/3))+3*b*Log(a-a^(2/3)*b^(1/3)-a^(1/3)*b^(2/3)+b)");

    // see github #120
    check("Integrate(Ln(x)^2, {x,0,2})", //
        "4-4*Log(2)+2*Log(2)^2");
    check("Integrate(Ln(x)^2, {x,0,2}) // N", //
        "2.18832");
    check("NIntegrate(Ln(x)^2, {x,0,2}) // N", //
        "2.1857");

    // see github #116
    // should give (2*ArcTan((1 + 2*x)/Sqrt(3)))/Sqrt(3)
    check(" Integrate(1/(x^2+x+1),x) ", //
        "(2*ArcTan((1+2*x)/Sqrt(3)))/Sqrt(3)");

    // see github #109
    check("Int(1/Sqrt(9*x^4+1),{x,0,999})//N", //
        "1.07012");
    check("Integrate(x^n,x)", //
        "x^(1+n)/(1+n)");
    check("Integrate(x^n,{x,0,1})", //
        "ConditionalExpression(1/(1+n),n>-1)");
    // https://github.com/RuleBasedIntegration/Rubi/issues/12
    check("Integrate(Tan(Log(x)),x)", //
        "-I*x+I*2*x*Hypergeometric2F1(-I*1/2,1,1-I*1/2,-x^(I*2))");

    check("Integrate(5*E^(3*x),{x,2,a})", //
        "1/3*(-5*E^6+5*E^(3*a))");
    check("Integrate(Sqrt(9-x^2),x)", //
        "1/2*x*Sqrt(9-x^2)+9/2*ArcSin(x/3)");
    check("Integrate(Sqrt(9-x^2),{x,0,3})", //
        "9/4*Pi");
    check("Integrate({Sin(x),Cos(x)},x)", //
        "{-Cos(x),Sin(x)}");
    check("Integrate({Sin(x),Cos(x)},{x,a,b})", //
        "{Cos(a)-Cos(b),-Sin(a)+Sin(b)}");
    check("Integrate(2*x,x)", "x^2");
    check("Integrate(Tan(x) ^ 5, x)", //
        "-Log(Cos(x))-Tan(x)^2/2+Tan(x)^4/4");
    check("Integrate(x*Sin(x),{x,1.0,2*Pi})", //
        "-6.58435");

    // check("Integrate(x/(1+x+x^7),x)", "");
    check("Integrate(1/y(x)^2,y(x))", //
        "-1/y(x)");
    check("Integrate(f(x,y),x)", //
        "Integrate(f(x,y),x)");
    check("Integrate(f(x,x),x)", //
        "Integrate(f(x,x),x)");

    if (Config.PROFILE_MODE) {
      BuiltinFunctionCalls.printStatistics();
    }
  }


  @Test
  public void testNIntegrate() {
    // avoid Indeterminate at 0
    checkNumeric("NIntegrate(1/Sqrt(x), {x, 0.001, 1}, Method -> \"GaussLobatto\")", //
        "1.9367544467966367");
    checkNumeric("NIntegrate(1/Sqrt(x), {x, 0.001, 1}, Method -> \"NewtonCotesRule\")", //
        "1.936754446797901");
    checkNumeric("NIntegrate(Sqrt(x), {x, 0, 1}, Method -> \"ClenshawCurtisRule\")", //
        "0.6666666666773636");

    // "tanh-sinh" strategy
    checkNumeric("NIntegrate(Exp(x)/x, {x, -Infinity, -1}, Method ->DoubleExponential)", //
        "-0.2193839343955203");
    // message NIntegrate failed to converge after 1000 refinements in x in the region
    // {-Infinity,-1}.
    checkNumeric("NIntegrate(x, {x, -Infinity, -1}, Method ->DoubleExponential)", //
        "NIntegrate(x,{x,-Infinity,-1},Method->doubleexponential)");

    // https://github.com/Hipparchus-Math/hipparchus/issues/279
    checkNumeric("NIntegrate(Exp(-x),{x,0,Infinity})", //
        "1.0");
    checkNumeric("NIntegrate(Exp(-x^2),{x,0,Infinity})", //
        "0.8862269254527579");
    checkNumeric("NIntegrate(Exp(-x^2),{x,-Infinity,Infinity})", //
        "1.772453850905516");

    // TOTO integrable singularity at x==0
    checkNumeric("NIntegrate(1/Sqrt(x),{x,0,1}, Method->GaussKronrod)", //
        "NIntegrate(1/Sqrt(x),{x,0,1},Method->gausskronrod)");
    checkNumeric("NIntegrate(1/Sqrt(x),{x,0,1}, Method->LegendreGauss )", //
        "1.9913364016175945");
    checkNumeric("NIntegrate(Cos(200*x),{x,0,1}, Method->GaussKronrod)", //
        "-0.0043664864860701");
    checkNumeric("NIntegrate(Cos(200*x),{x,0,1}, Method->LegendreGauss )", //
        "-0.0043664864860699");

    // github #150
    // NIntegrate: (method=LegendreGauss) 1,001 is larger than the maximum (1,000)
    checkNumeric("NIntegrate(1/x, {x, 0, 1}, MaxPoints->1001)", //
        "NIntegrate(1/x,{x,0,1},MaxPoints->1001)");
    // wrong result
    checkNumeric("NIntegrate(1/x, {x,0,5}, Method->LegendreGauss)", //
        "10.374755035279286");

    // github #61
    // these methods correctly show "NIntegrate(method=method-nsme) maximal count (xxxxx) exceeded"
    checkNumeric("NIntegrate(1/x, {x,0,5}, Method->Romberg)", //
        "NIntegrate(1/x,{x,0,5},Method->romberg)");
    checkNumeric("NIntegrate(1/x, {x,0,5}, Method->Simpson)", //
        "NIntegrate(1/x,{x,0,5},Method->simpson)");
    checkNumeric("NIntegrate(1/x, {x,0,5}, Method->Trapezoid)", //
        "NIntegrate(1/x,{x,0,5},Method->trapezoid)");

    // github #26
    checkNumeric(
        "NIntegrate(ln(x^2), {x, -5, 99}, Method->Romberg, MaxPoints->400, MaxIterations->10000000)", //
        "717.9282476448197");

    checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1})", //
        "-0.0208333333333333");
    // LegendreGauss is default method
    checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->LegendreGauss)", //
        "-0.0208333333333333");
    checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Simpson)", //
        "-0.0208333320915699");
    checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Trapezoid)", //
        "-0.0208333271245165");
    checkNumeric(
        "NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Trapezoid, MaxIterations->5000)", //
        "-0.0208333271245165");
    checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Romberg)", //
        "-0.0208333333333333");
    checkNumeric("NIntegrate (x, {x, 0,2}, Method->Simpson)", //
        "2.0");
    checkNumeric("NIntegrate(Cos(x), {x, 0, Pi})", //
        "1.0E-16");
    checkNumeric("NIntegrate(1/Sin(Sqrt(x)), {x, 0, 1}, PrecisionGoal->10)", //
        "2.1108620052");
  }
}
