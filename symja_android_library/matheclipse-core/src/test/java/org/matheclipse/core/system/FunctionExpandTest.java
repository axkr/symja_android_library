package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests forSolve and Roots functions */
public class FunctionExpandTest extends ExprEvaluatorTestCase {

  public FunctionExpandTest(String name) {
    super(name);
  }

  public void testFunctionExpand() {
    check("FunctionExpand(Factorial2(n))", //
        "2^(n/2+1/4*(1-Cos(n*Pi)))*Pi^(1/4*(-1+Cos(n*Pi)))*Gamma(1+n/2)");
    check("FunctionExpand(Pochhammer(a, n))", //
        "Gamma(a+n)/Gamma(a)");
    check("FunctionExpand(Gamma(x)/Gamma(x-1))", //
        "-1+x");
    check("FunctionExpand(Gamma(x-2)/Gamma(x-11))", //
        "(-11+x)*(-10+x)*(-9+x)*(-8+x)*(-7+x)*(-6+x)*(-5+x)*(-4+x)*(-3+x)");
    check("FunctionExpand(Gamma(x+2)/Gamma(x+11))", //
        "1/((2+x)*(3+x)*(4+x)*(5+x)*(6+x)*(7+x)*(8+x)*(9+x)*(10+x))");
    check("FunctionExpand(Gamma(x+2)/Gamma(x))", //
        "x*(1+x)");
    check("FunctionExpand(Gamma(x-2)/Gamma(x-1))", //
        "1/(-2+x)");
    check("FunctionExpand(Gamma(x)/Gamma(x+3))", //
        "1/(x*(1+x)*(2+x))");
    check("FunctionExpand(E^ProductLog(x))", //
        "x/ProductLog(x)");
    check("FunctionExpand(Log(ProductLog(x)))", //
        "x");
    check("FunctionExpand(ArcCot(1/x))", //
        "ArcTan(x)");
    check("FunctionExpand(ArcTan(1/x))", //
        "ArcCot(x)");
    check("FunctionExpand(ArcSin(1/x))", //
        "ArcCsc(x)");
    check("FunctionExpand(SphericalHarmonicY(3,1,t,p)) ", //
        "(2*Sqrt(7/3)*E^(I*p)*(3/16*Sqrt(Pi)*Cos(t)-15/16*Sqrt(Pi)*Cos(t)^3)*Sec(t)*Sqrt(Sin(t)^\n"
            + "2))/Pi");
    check("FunctionExpand(SphericalHarmonicY(l,m,t,p))", //
        "(E^(I*m*p)*Sqrt(1+2*l)*Sqrt(Gamma(1+l-m))*Hypergeometric2F1(-l,1+l,1-m,Sin(t/2)^\n"
            + "2)*Sin(t)^m)/(2*Sqrt(Pi)*(1-Cos(t))^m*Gamma(1-m)*Sqrt(Gamma(1+l+m)))");
    check("FunctionExpand({Degree, GoldenRatio})", //
        "{Pi/180,1/2*(1+Sqrt(5))}");
    check("FunctionExpand(ExpIntegralE(n,z))", //
        "Gamma(1-n,z)/z^(1-n)");
    check("FunctionExpand(Sin(Pi/2^4))", //
        "Sqrt(2-Sqrt(2+Sqrt(2)))/2");
    check("FunctionExpand(Cos(Pi/2^4))", //
        "Sqrt(2+Sqrt(2+Sqrt(2)))/2");
    check("FunctionExpand(Sin(Pi/2^5))", //
        "Sqrt(2-Sqrt(2+Sqrt(2+Sqrt(2))))/2");
    check("FunctionExpand(Cos(Pi/2^5))", //
        "Sqrt(2+Sqrt(2+Sqrt(2+Sqrt(2))))/2");
    check("FunctionExpand(GammaRegularized(a,b))", //
        "Gamma(a,b)/Gamma(a)");
    check("FunctionExpand(LogGamma(x),x>0)", //
        "Log(Gamma(x))");
    check("FunctionExpand(LogGamma(42))", //
        "Log(33452526613163807108170062053440751665152000000000)");
    check("FunctionExpand(Abs(x)^2,Element(x,Reals))", //
        "x^2");
    check("FunctionExpand(Abs(x)^3,Element(x,Reals))", //
        "Abs(x)^3");
    check("FunctionExpand(BetaRegularized(z,a,b))", //
        "(Beta(z,a,b)*Gamma(a+b))/(Gamma(a)*Gamma(b))");
    check("FunctionExpand(BetaRegularized(z0,z1,a,b))", //
        "((-Beta(z0,a,b)+Beta(z1,a,b))*Gamma(a+b))/(Gamma(a)*Gamma(b))");

    check("FunctionExpand(ProductLog(a*Log(a)), a > 42)", //
        "Log(a)");
    check("FunctionExpand(ProductLog(a*Log(a)), a >= 42)", //
        "Log(a)");
    check("FunctionExpand(ProductLog(a*Log(a)), a < 42)", //
        "ProductLog(a*Log(a))");
    check("FunctionExpand(ProductLog(a*Log(a)), a > 1/E)", //
        "Log(a)");
    check("FunctionExpand(ProductLog(a*Log(a)), a > 1/Pi)", //
        "ProductLog(a*Log(a))");
    check("FunctionExpand(ProductLog(a*Log(a)), a > 0.37)", //
        "Log(a)");
    check("FunctionExpand(ProductLog(a*Log(a)), a > 0.35)", //
        "ProductLog(a*Log(a))");

    check("FunctionExpand(Sin(4*ArcSin(x)))", //
        "x*(-4*Sqrt(1-x^2)+8*(1-x^2)^(3/2))");
    check("FunctionExpand(Sin(4*ArcTan(x)))", //
        "(4*x-4*x^3)/(1+x^2)^2");

    check("FunctionExpand(Cos(-7*ArcSin(x)))", //
        "-7*Sqrt(1-x^2)+56*(1-x^2)^(3/2)-112*(1-x^2)^(5/2)+64*(1-x^2)^(7/2)");
    check("FunctionExpand(Hypergeometric2F1(a, b, b -2, z))", //
        "(1+(-2*(-2-a+b)*z)/(-2+b)+((-2-a+b)*(-1-a+b)*z^2)/((-2+b)*(-1+b)))/(1-z)^(2+a)");
    check("FunctionExpand(PolyGamma(10, 1/2))", //
        "-7428153600*Zeta(11)");
    check("FunctionExpand(Gamma(1/2,z))", //
        "Sqrt(Pi)*(1-Erf(Sqrt(z)))");
    check("FunctionExpand(Gamma(0,z))", //
        "-ExpIntegralEi(-z)+1/2*(-Log(-1/z)+Log(-z))-Log(z)");
    check("FunctionExpand(Gamma(-1,z))", //
        "1/(E^z*z)+ExpIntegralEi(-z)+1/2*(Log(-1/z)-Log(-z))+Log(z)");

    check("FunctionExpand(Sqrt(5/6 + 2*Sqrt(1/6)))", //
        "1/Sqrt(2)+1/Sqrt(3)");
    check("FunctionExpand(Sqrt(3+2*Sqrt(2)))", //
        "1+Sqrt(2)");
    check("FunctionExpand(Sqrt(5+2*Sqrt(6)))", //
        "Sqrt(2)+Sqrt(3)");

    check("FunctionExpand(Degree)", //
        "Pi/180");
    check("FunctionExpand(GoldenRatio)", //
        "1/2*(1+Sqrt(5))");
    check("FunctionExpand(Cos(Sqrt(x^2)))", //
        "Cos(x)");
    check("FunctionExpand(Sin(Sqrt(x^2)))", //
        "(Sqrt(x^2)*Sin(x))/x");
    check("FunctionExpand(Haversine(hf))", //
        "1/2*(1-Cos(hf))");
    check("FunctionExpand(InverseHaversine(ihf))", //
        "2*ArcSin(Sqrt(ihf))");

    check("FunctionExpand(E^ArcSinh(x))", //
        "x+Sqrt(1+x^2)");
    check("FunctionExpand(E^ArcCosh(x))", //
        "x+Sqrt(-1+x)*Sqrt(1+x)");
    check("FunctionExpand(E^ArcTanh(x))", //
        "(1+x)/Sqrt(1-x^2)");
    check("FunctionExpand(E^ArcCsch(x))", //
        "Sqrt(1+1/x^2)+1/x");
    check("FunctionExpand(E^ArcSech(x))", //
        "Sqrt(-1+1/x)*Sqrt(1+1/x)+1/x");
    check("FunctionExpand(E^ArcCoth(x))", //
        "1/Sqrt((-1+x)/(1+x))");

    check("FunctionExpand(Log(Sqrt(x^2)))", //
        "Log(x^2)/2");
    check("FunctionExpand(CosIntegral(Sqrt(x^2)))", //
        "CosIntegral(x)-Log(x)+Log(x^2)/2");
    check("FunctionExpand(SinIntegral(Sqrt(x^2)))", //
        "(Sqrt(x^2)*SinIntegral(x))/x");

    check("FunctionExpand((x+y)^3)", //
        "(x+y)^3");
    check("FunctionExpand((x+y)*(a+b))", //
        "(a+b)*(x+y)");

    check("FunctionExpand(Binomial(n,k))", //
        "Gamma(1+n)/(Gamma(1+k)*Gamma(1-k+n))");
    check("FunctionExpand(Binomial(n,7))", //
        "1/5040*(-6+n)*(-5+n)*(-4+n)*(-3+n)*(-2+n)*(-1+n)*n");
    check("FunctionExpand(Binomial(3,k))", //
        "(6*Sin(k*Pi))/((-3+k)*(-2+k)*(-1+k)*k*Pi)");
    check("FunctionExpand(Binomial(6,k))", //
        "(720*Sin(k*Pi))/((-6+k)*(-5+k)*(-4+k)*(-3+k)*(-2+k)*(-1+k)*k*Pi)");
    check("FunctionExpand(BlackmanHarrisWindow(x))", //
        "Piecewise({{1/100000*(35875+48829*Cos(2*Pi*x)+14128*Cos(4*Pi*x)+1168*Cos(6*Pi*x)),-\n"
            + "1/2<=x<=1/2}},0)");
    check("BlackmanHarrisWindow(1/4)", //
        "21747/100000");
    check("FunctionExpand(BlackmanHarrisWindow(x))", //
        "Piecewise({{1/100000*(35875+48829*Cos(2*Pi*x)+14128*Cos(4*Pi*x)+1168*Cos(6*Pi*x)),-\n"
            + "1/2<=x<=1/2}},0)");
    check("FunctionExpand(BlackmanNuttallWindow(x))", //
        "Piecewise({{1/10000000*(3635819+4891775*Cos(2*Pi*x)+1365995*Cos(4*Pi*x)+106411*Cos(\n"
            + "6*Pi*x)),-1/2<=x<=1/2}},0)");
    check("FunctionExpand(BlackmanWindow(x))", //
        "Piecewise({{1/50*(21+25*Cos(2*Pi*x)+4*Cos(4*Pi*x)),-1/2<=x<=1/2}},0)");
    check("FunctionExpand(DirichletWindow(x))", //
        "Piecewise({{1,-1/2<=x<=1/2}},0)");
    check("FunctionExpand(FlatTopWindow(x))", //
        "Piecewise({{1/1000000000*(215578947+416631580*Cos(2*Pi*x)+277263158*Cos(4*Pi*x)+\n"
            + "83578947*Cos(6*Pi*x)+6947368*Cos(8*Pi*x)),-1/2<=x<=1/2}},0)");
    check("FunctionExpand(GaussianWindow(x))", //
        "Piecewise({{E^(-50/9*x^2),-1/2<=x<=1/2}},0)");
    check("FunctionExpand(HammingWindow(x))", //
        "Piecewise({{25/46+21/46*Cos(2*Pi*x),-1/2<=x<=1/2}},0)");
    check("FunctionExpand(HannWindow(x))", //
        "Piecewise({{1/2+Cos(2*Pi*x)/2,-1/2<=x<=1/2}},0)");
    check("FunctionExpand(NuttallWindow(x))", //
        "Piecewise({{1/250000*(88942+121849*Cos(2*Pi*x)+36058*Cos(4*Pi*x)+3151*Cos(6*Pi*x)),-\n"
            + "1/2<=x<=1/2}},0)");
    check("FunctionExpand(ParzenWindow(x))", //
        "Piecewise({{-2*(-1+2*x)^3,1/4<x&&x<=1/2},{2*(1+2*x)^3,-1/2<=x&&x<-1/4},{1-24*x^2-\n"
            + "48*x^3,-1/4<=x&&x<0},{1-24*x^2+48*x^3,0<=x<=1/4}},0)");
    check("FunctionExpand(TukeyWindow(x))", //
        "Piecewise({{1,x>=-1/6&&x<=1/6},{1/2*(1+Cos(3*Pi*(1/6+x))),x>=-1/2&&x<-1/6},{1/2*(\n" //
            + "1+Cos(3*Pi*(-1/6+x))),x>1/6&&x<=1/2}},0)");
    check("FunctionExpand(Log(10*E))", //
        "1+Log(10)");
    check("FunctionExpand(PolyGamma(-2, 1))", //
        "1/2*(Log(2)+Log(Pi))");
    check("FunctionExpand(PolyGamma(-3, 1))", //
        "Log(Glaisher)+1/4*(Log(2)+Log(Pi))");
    check("FunctionExpand(Fibonacci(n + 3), Element(n, Integers))", //
        "2*Fibonacci(n)+LucasL(n)");
    check("FunctionExpand(CatalanNumber(x))", //
        "(2^(2*x)*Gamma(1/2+x))/(Sqrt(Pi)*Gamma(2+x))");
    check("FunctionExpand(ChebyshevT(n, x))", //
        "Cos(n*ArcCos(x))");
    check("FunctionExpand(ChebyshevU(n, x))", //
        "Sin((1+n)*ArcCos(x))/(Sqrt(1-x)*Sqrt(1+x))");
    check("FunctionExpand(Factorial(x))", //
        "Gamma(1+x)");
    check("FunctionExpand(Factorial(x+3))", //
        "Gamma(4+x)");
    check("FunctionExpand(LegendreQ(3/2,1/2,x))", //
        "-Pi*LegendreP(3/2,-1/2,x)");
    check("FunctionExpand({Degree, GoldenRatio})", //
        "{Pi/180,1/2*(1+Sqrt(5))}");
    check("FunctionExpand(Beta(z,3,b))", //
        "(2*(1-(1-z)^b*(1+b*z+1/2*b*(1+b)*z^2)))/(b*(1+b)*(2+b))");
    check("FunctionExpand(BetaRegularized(z, a, b))", //
        "(Beta(z,a,b)*Gamma(a+b))/(Gamma(a)*Gamma(b))");
  }

  public void testFunctionExpandFactorialPower() {
    check("FunctionExpand(FactorialPower(b,3))", //
        "(-2+b)*(-1+b)*b");
    check("FunctionExpand(FactorialPower(b,3,-1))", //
        "b*(1+b)*(2+b)");
    check("FunctionExpand(FactorialPower(a,b))", //
        "Gamma(1+a)/Gamma(1+a-b)");
    check("FunctionExpand(FactorialPower(a,b,-1))", //
        "(a^b*Gamma(1-a))/((-a)^b*Gamma(1-a-b))");
  }

  public void testFunctionExpandHarmonicNumber() {
    check("FunctionExpand(HarmonicNumber(1/3-1))", //
        "-Pi/(2*Sqrt(3))-Log(6)-Log(Sqrt(3)/2)");
  }

  public void testFunctionExpandBinomial() {
    check("FunctionExpand(Binomial(a,b))", //
        "Gamma(1+a)/(Gamma(1+a-b)*Gamma(1+b))");
  }

  public void testFunctionC1D2TimesArcFunction() {
    check("FunctionExpand(Cos(ArcSin(x)/2))", //
        "Sqrt(1+Sqrt(1-x)*Sqrt(1+x))/Sqrt(2)");
    check("FunctionExpand(Cot(ArcCsc(x)/2))", //
        "x*Sqrt(1+Sqrt((-1+x)*(1+x))/Sqrt(x^2))*Sqrt((x^2+Sqrt(-1+x^2)*Sqrt(x^2))/x^2)");
    check("FunctionExpand(Csc(ArcTan(x)/2))", //
        "(Sqrt(2)*Sqrt(1+x^2)*Sqrt(1+1/Sqrt(1+x^2)))/x");
    check("FunctionExpand(Sec(ArcCot(x)/2))", //
        "Sqrt(2)/Sqrt(1+(Sqrt(-x)*Sqrt(x))/Sqrt(-1-x^2))");
    check("FunctionExpand(Sin(ArcCos(x)/2))", //
        "Sqrt(1-x)/Sqrt(2)");
    check("FunctionExpand(Tan(ArcSin(x)/2))", //
        "x/(1+Sqrt(1-x)*Sqrt(1+x))");
  }

  public void testFunctionExpandMultinomial() {
    check("FunctionExpand(Multinomial(a,b))", //
        "Gamma(1+a+b)/(Gamma(1+a)*Gamma(1+b))");
    check("FunctionExpand(Multinomial(a,b,c,d,e))", //
        "Gamma(1+a+b+c+d+e)/(Gamma(1+a)*Gamma(1+b)*Gamma(1+c)*Gamma(1+d)*Gamma(1+e))");
  }

  public void testFunctionExpandPolyGamma() {
    check("FunctionExpand(PolyGamma(-z))", //
        "1/z+Pi*Cot(Pi*z)+PolyGamma(0,z)");
    check("FunctionExpand(PolyGamma(0,1/2))", //
        "-EulerGamma-Log(4)");
    check("FunctionExpand(PolyGamma(0,1/3))", //
        "-EulerGamma-Pi/(2*Sqrt(3))-Log(6)-Log(Sqrt(3)/2)");
    check("FunctionExpand(PolyGamma(0,1/5))", //
        "-EulerGamma-1/2*Sqrt(1+2/Sqrt(5))*Pi-Log(10)+1/2*(-1+Sqrt(5))*Log(Sqrt(1/2*(5-Sqrt(\n" //
            + "5)))/2)+1/2*(-1-Sqrt(5))*Log(Sqrt(1/2*(5+Sqrt(5)))/2)");
  }

  public void testFunctionExpandSphericalHankelH1() {
    check("FunctionExpand(SphericalHankelH1(a,b))", //
        "(Sqrt(Pi/2)*BesselJ(1/2*(1+2*a),b))/Sqrt(b)+(I*Sqrt(Pi/2)*BesselY(1/2*(1+2*a),b))/Sqrt(b)");
  }

  public void testFunctionExpandSphericalHankelH2() {
    check("FunctionExpand(SphericalHankelH2(a,b))", //
        "(Sqrt(Pi/2)*BesselJ(1/2*(1+2*a),b))/Sqrt(b)+(-I*Sqrt(Pi/2)*BesselY(1/2*(1+2*a),b))/Sqrt(b)");
  }

  public void testFunctionExpandPower() {
    check("FunctionExpand(I^(x+y))", //
        "E^(I*1/2*Pi*x+I*1/2*Pi*y)");
    check("FunctionExpand(I^(3*x))", //
        "E^(I*3/2*Pi*x)");
  }

  public void testFunctionExpandLog() {
    check("FunctionExpand(Log((1 + I Sqrt(3))/2))", //
        "I*1/3*Pi");
  }

  public void testFunctionExpandSqrtDenest() {
    check("FunctionExpand( Sqrt(5+4*Sqrt(9)) )", //
        "Sqrt(17)");
    check("FunctionExpand( Sqrt(9+4*Sqrt(5)) )", //
        "2+Sqrt(5)");
    check("FunctionExpand( Sqrt(5+4*Sqrt(-9)) )", //
        "3+I*2");
    check("FunctionExpand( Sqrt(9+4*Sqrt(-5)) )", //
        "Sqrt(9+I*4*Sqrt(5))");

    check("FunctionExpand( Sqrt(-5+4*Sqrt(-9)) )", //
        "2+I*3");
    check("FunctionExpand( Sqrt(-9+4*Sqrt(-5)) )", //
        "Sqrt(-9+I*4*Sqrt(5))");
    check("FunctionExpand( Sqrt(5-4*Sqrt(-9)) )", //
        "3-I*2");
    check("FunctionExpand( Sqrt(9-4*Sqrt(-5)) )", //
        "Sqrt(9-I*4*Sqrt(5))");

    check("FunctionExpand( Sqrt(-5+4*Sqrt(9)) )", //
        "Sqrt(7)");
    check("FunctionExpand( Sqrt(-9+4*Sqrt(5)) )", //
        "I*(-2+Sqrt(5))");
    check("FunctionExpand( Sqrt(5-4*Sqrt(9)) )", //
        "I*Sqrt(7)");
    check("FunctionExpand( Sqrt(9-4*Sqrt(5)) )", //
        "-2+Sqrt(5)");
  }

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
