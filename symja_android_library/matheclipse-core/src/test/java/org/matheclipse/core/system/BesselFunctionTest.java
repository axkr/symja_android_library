package org.matheclipse.core.system;

import org.junit.Test;

public class BesselFunctionTest extends ExprEvaluatorTestCase {


  @Test
  public void testBesselI() {
    check("N(BesselI(0, 1), 50)", //
        "1.2660658777520083355982446252147175376076703113549");

    check("BesselI(3/2, z)", //
        "Sqrt(2/Pi)*Sqrt(z)*(Cosh(z)/z-Sinh(z)/z^2)");

    check("BesselI(-1/2, z)", //
        "(Sqrt(2/Pi)*Cosh(z))/Sqrt(z)");
    check("BesselI(1/2, z)", //
        "(Sqrt(2/Pi)*Sinh(z))/Sqrt(z)");

    checkNumeric("BesselI(I+1, -I*Infinity)", //
        "0");
    checkNumeric("BesselI(42, I*Infinity)", //
        "0");

    checkNumeric("BesselI(0, 0)", //
        "1");
    checkNumeric("BesselI(4, 0)", //
        "0");
    checkNumeric("BesselI(-42, 0)", //
        "0");
    checkNumeric("BesselI(-42.5, 0)", //
        "ComplexInfinity");
    checkNumeric("BesselI(I, 0)", //
        "Indeterminate");

    checkNumeric("BesselI(0,1.0 )", //
        "1.2660658777520082");
    checkNumeric("BesselI(0,2.0 )", //
        "2.2795853023360673");
    checkNumeric("BesselI(3 + I, 1.5 - I)", //
        "-0.2566499289084423+I*0.04927707435297578");
    checkNumeric("BesselI({0, 1, 2}, 1.)", //
        "{1.2660658777520082,0.565159103992485,0.13574766976703828}");
  }

  @Test
  public void testBesselJ() {

    check("BesselJ(3/2, x)", //
        "Sqrt(2/Pi)*Sqrt(x)*(-Cos(x)/x+Sin(x)/x^2)");
    check("BesselJ(-3/2, x)", //
        "-Sqrt(2/Pi)*Sqrt(x)*(Cos(x)/x^2+Sin(x)/x)");

    check("BesselJ(1/2,-1)", //
        "I*Sqrt(2/Pi)*Sin(1)");
    check("BesselJ(-1/2,-Infinity)", //
        "0");
    // https://github.com/mtommila/apfloat/issues/37
    checkNumeric("BesselJ(-1.9999999999998,3.0)", //
        "0.48609126058580704");
    checkNumeric("BesselJ(-1.999888,3.0)", //
        "0.48604418359704343");

    checkNumeric("BesselJ(0,0.001)", //
        "0.9999997500000157");
    checkNumeric("BesselJ(0,5.2)", //
        "-0.11029043979098654");
    checkNumeric("BesselJ(0,4.0)", //
        "-0.39714980986384735");
    checkNumeric("BesselJ(1,3.6 )", //
        "0.0954655471778764");
    checkNumeric("BesselJ(7/3 + I, 4.5 - I)", //
        "1.189083603364091+I*0.7156530815957028");
    //
    check("BesselJ(-42, z)", //
        "BesselJ(42,z)");
    check("BesselJ(-43, z)", //
        "-BesselJ(43,z)");
    // check("BesselJ(0.5, z)", //
    // "(Sqrt(2)*Sin(z))/(Sqrt(Pi)*Sqrt(z))");
    check("BesselJ(-0.5, 1.2)", //
        "0.263929");
    check("BesselJ(-0.5, 17)", //
        "-0.0532484");
    // check("BesselJ(-0.5, z)", //
    // "(Sqrt(2)*Sin(1.5708+z))/(Sqrt(Pi)*Sqrt(z))");
    check("BesselJ(1/2, z)", //
        "(Sqrt(2/Pi)*Sin(z))/Sqrt(z)");
    check("BesselJ(-1/2, z)", //
        "(Sqrt(2/Pi)*Cos(z))/Sqrt(z)");
    check("BesselJ(-2.5, 1.333)", //
        "1.6236");
    check("BesselJ(-2.5, z)", //
        "(Sqrt(2/Pi)*(-z)^2.0*(((2*Cos(z))/z^3-Cos(z)/z+(2*Sin(z))/z^2)/z+(Cos(z)/z^2+Sin(z)/z)/z^\n"
            + "2))/Sqrt(z)");
    check("BesselJ(-5/2, z)", //
        "Sqrt(2/Pi)*z^(3/2)*(((2*Cos(z))/z^3-Cos(z)/z+(2*Sin(z))/z^2)/z+(Cos(z)/z^2+Sin(z)/z)/z^\n"
            + "2)");
    check("BesselJ(0, 5.2)", "-0.11029");
    checkNumeric("BesselJ(3.5, 1.2)", //
        "0.013270419445925403");
    check("BesselJ(4.0, 0.0)", //
        "0.0");
    check("BesselJ(1.0, -3.0)", //
        "-0.339059");
    check("BesselJ(0.0, 0.0)", //
        "1.0");
    check("BesselJ(-3.0, 0.0)", //
        "0.0");
    check("BesselJ(-3, 0)", //
        "0");
    check("BesselJ(0, 0)", //
        "1");
    check("BesselJ(4, 0)", //
        "0");
    check("BesselJ(0.0, 4)", //
        "-0.39715");
    check("BesselJ(1, {0.5, 1.0, 1.5})", //
        "{0.242268,0.440051,0.557937}");
  }

  @Test
  public void testBesselJZero() {
    checkNumeric("BesselJZero(1.5, 1.0)", //
        "4.493409429988828");

    checkNumeric("BesselJZero(1.3, 3)", //
        "10.613804865461777");
    checkNumeric("BesselJZero(0.0,1)", //
        "2.4048258314347084");
    checkNumeric("BesselJZero(0.0,2)", //
        "5.520078275367197");
    checkNumeric("BesselJZero(1.0,5)", //
        "16.470629879496084");
    checkNumeric("BesselJZero(0, {1, 2, 3}) // N", //
        "{2.4048258314347084,5.520078275367197,8.653728211806023}");
    checkNumeric("BesselJZero(1, 1)/Pi // N", //
        "1.2196698060629994");

    checkNumeric("Table(BesselJZero(x,1), {x,-2,2,0.25})", //
        "{5.135622011324211,2.128941679966946,2.798386125853118,3.3441822516097046,3.8317057025328065,1.058507887548149," //
            + "1.5707967013019248,2.006299462916374,2.4048258314347084,2.7808878554491905,3.141593028096821,3.491008184755069," //
            + "3.8317057025328065,4.165425980713131,4.493409429988828,4.816574141184002,5.135622011324211}");
  }

  @Test
  public void testBesselK() {
    check("BesselK(3/2, x)", //
        "Sqrt(Pi/2)*(1/(E^x*x^2)+1/(E^x*x))*Sqrt(x)");
    check("BesselK(-3/2, x)", //
        "Sqrt(Pi/2)*(1/(E^x*x^2)+1/(E^x*x))*Sqrt(x)");
    check("BesselK(1317624576693539401,3.0-2*I)", //
        "BesselK(1.31762*10^18,3.0+I*(-2.0))");
    check("BesselK(-1/2, z)", //
        "Sqrt(Pi/2)/(E^z*Sqrt(z))");
    check("BesselK(1/2, z)", //
        "Sqrt(Pi/2)/(E^z*Sqrt(z))");

    checkNumeric("BesselK(I+1, -I*Infinity)", //
        "0");
    checkNumeric("BesselK(42, I*Infinity)", //
        "0");

    checkNumeric("BesselK(0, 0)", //
        "Infinity");
    checkNumeric("BesselK(4, 0)", //
        "ComplexInfinity");
    checkNumeric("BesselK(I, 0)", //
        "Indeterminate");

    checkNumeric("BesselK(0,0.53)", //
        "0.8765603804164857");
    checkNumeric("BesselK(0,4.0)", //
        "0.01115967608585302");
    checkNumeric("BesselK(1 + I, 3.0 - 2* I)", //
        "-0.0225107551369776+I*0.01696073734722752");
    checkNumeric("BesselK(23, 1.0)", //
        "4.661145573743599E27");
    checkNumeric("BesselK({1, 2, 3}, 1.0)", //
        "{0.6019072301972349,1.6248388986351774,7.101262824737945}");
  }

  @Test
  public void testBesselY() {
    check("BesselY(3.5,-5)", //
        "I*(-0.0275521)");
    checkNumeric("BesselY(2.5,-5.0)", //
        "I*(-0.2943723749617925)");
    checkNumeric("BesselY(I+1, -Infinity)", //
        "0");
    checkNumeric("BesselY(42, Infinity)", //
        "0");

    check("BesselY(-5/2, z)", //
        "(Sqrt(2/Pi)*((-3*Cos(z))/z-Sin(z)+(3*Sin(z))/z^2))/Sqrt(z)");
    check("BesselY(5/2, z)", //
        "(Sqrt(2/Pi)*(Cos(z)+(-3*Cos(z))/z^2+(-3*Sin(z))/z))/Sqrt(z)");
    check("BesselY(-3/2, z)", //
        "(Sqrt(2/Pi)*(Cos(z)-Sin(z)/z))/Sqrt(z)");
    check("BesselY(3/2, z)", //
        "(Sqrt(2/Pi)*(-Cos(z)/z-Sin(z)))/Sqrt(z)");
    check("BesselY(-1/2, z)", //
        "(Sqrt(2/Pi)*Sin(z))/Sqrt(z)");
    check("BesselY(1/2, z)", //
        "(-Sqrt(2/Pi)*Cos(z))/Sqrt(z)");
    checkNumeric("BesselY(0, 0)", //
        "-Infinity");
    checkNumeric("BesselY(4, 0)", //
        "ComplexInfinity");
    checkNumeric("BesselY(I, 0)", //
        "Indeterminate");

    checkNumeric("BesselY(10.0,1.0)", //
        "-1.216180142786892E8");
    checkNumeric("BesselY(0,2.5)", //
        "0.49807035961523183");
    checkNumeric("BesselY(0,1.0)", //
        "0.08825696421567694");
    checkNumeric("BesselY(0.5*I, 3.0 - I)", //
        "1.0468646059974471+I*0.8847844476974699");
    checkNumeric("BesselY(0, {1.0, 2.0, 3.0})", //
        "{0.08825696421567694,0.510375672649745,0.3768500100127904}");
  }

  @Test
  public void testBesselYZero() {
    // https://github.com/paulmasson/math/issues/11
    checkNumeric("BesselYZero(0.0,1)", //
        "0.8935771552464262");

    checkNumeric("BesselYZero(1.3, 3)", //
        "9.031260842335175");
    checkNumeric("BesselYZero(0.0,2)", //
        "3.957678435648244");

    checkNumeric("Table(BesselYZero(x,1), {x,-2,2,0.25})", //
        "{3.3842419063910434,3.971831811344723,4.493409218807862,1.597155616577941,2.197141057759451,"//
            + "2.6938504302755852,3.141593028096821,0.5089486250933928,0.8935771552464262,1.241662317881476,"//
            + "1.5707962332014729,1.888077462109278,2.197141057759451,2.500121683376498,2.79838644140362,"//
            + "3.0928694051018533,3.3842419063910434}");

  }

  @Test
  public void testHankelH1() {
    check("HankelH1(1317624576693539401,I*1/2)", //
        "HankelH1(1317624576693539401,I*1/2)");
    check("HankelH1(#2,#2)", //
        "HankelH1(#2,#2)");
    checkNumeric("HankelH1(2.0,3)", //
        "0.486091260585958+I*(-0.16040039348491514)");
    checkNumeric("HankelH1(3,1.2)", //
        "0.03287433692500416+I*(-3.5898996296131624)");
    checkNumeric("HankelH1(4.0,Pi)", //
        "0.15142457763119113+I*(-0.8284254758009458)");
    checkNumeric("HankelH1(23/47,5.0-I)", //
        "-0.8840126453738697+I*(-0.37582703842509113)");
  }

  @Test
  public void testHankelH2() {
    checkNumeric("HankelH2(3,1.2)", //
        "0.03287433692500416+I*3.5898996296131624");
    checkNumeric("HankelH2(0.2,3)", //
        "-0.1337869677248155+I*(-0.4383481561883242)");
    checkNumeric("HankelH2(4.0,3*Pi)", //
        "-0.27212624415123904+I*0.015631826514063165");
    checkNumeric("HankelH2(23/47,5.0-I)", //
        "-0.1272388528903588+I*0.02641083531092656");
  }


  @Test
  public void testSphericalHankelH1() {
    check("SphericalHankelH1(a,b)", //
        "SphericalHankelH1(a,b)");
    
    check("SphericalHankelH1(3, 1.5)", //
        "0.0283246+I*(-3.78927)");
    check("SphericalHankelH1(2, -5.0)", //
        "0.134731+I*(-0.164995)");
    check("SphericalHankelH1(-0.5, 1.0)", //
        "0.959033+I*0.110614");

    check("SphericalHankelH1(2 + I, 5.0 + I)", //
        "0.192197+I*0.15964");
  }

  @Test
  public void testSphericalHankelH2() {
    check("SphericalHankelH2(3, 1.5)", //
        "0.0283246+I*3.78927");
    check("SphericalHankelH2(2, -5.0)", //
        "0.134731+I*0.164995");
    check("SphericalHankelH2(-0.5, 1.0)", //
        "0.959033+I*(-0.110614)");

    check("SphericalHankelH2(1 + I, 5.0 + I)", //
        "-0.084523+I*(-0.120937)");
  }

  @Test
  public void testWeberE() {
    check("WeberE(1.5, 3.5)", //
        "0.212207*HypergeometricPFQ({1},{0.25,1.75},-3.0625)+0.891268*HypergeometricPFQ({\n"
            + "1},{0.75,2.25},-3.0625)");
  }
}
