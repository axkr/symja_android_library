package org.matheclipse.core.system;

public class BesselFunctionTest extends ExprEvaluatorTestCase {
  public BesselFunctionTest(String name) {
    super(name);
  }

  public void testHankelH1() {
    check("HankelH1(1317624576693539401,I*1/2)", //
        "HankelH1(1317624576693539401,I*1/2)");
    check("HankelH1(#2,#2)", //
        "HankelH1(#2,#2)");
    checkNumeric("HankelH1(2.0,3)", //
        "0.486091260585958+I*(-0.1604003934849758)");
    checkNumeric("HankelH1(3,1.2)", //
        "0.03287433692500416+I*(-3.589899629613203)");
    checkNumeric("HankelH1(4.0,Pi)", //
        "0.15142457763119113+I*(-0.8284254758008354)");
    checkNumeric("HankelH1(23/47,5.0-I)", //
        "-0.8840126453738697+I*(-0.37582703842509113)");
  }

  public void testHankelH2() {
    checkNumeric("HankelH2(3,1.2)", //
        "0.03287433692500416+I*3.589899629613203");
    checkNumeric("HankelH2(0.2,3)", //
        "-0.1337869677248155+I*(-0.4383481561883242)");
    checkNumeric("HankelH2(4.0,3*Pi)", //
        "-0.27212624415123904+I*0.0156318257242769");
    checkNumeric("HankelH2(23/47,5.0-I)", //
        "-0.1272388528903588+I*0.02641083531092656");
  }


  public void testSphericalHankelH1() {
    check("SphericalHankelH1(3, 1.5)", //
        "0.0283246+I*(-3.78927)");
    check("SphericalHankelH1(2, -5.0)", //
        "0.134731+I*(-0.164995)");
    check("SphericalHankelH1(-0.5, 1.0)", //
        "0.959033+I*0.110614");

    check("SphericalHankelH1(2 + I, 5.0 + I)", //
        "0.192197+I*0.15964");
  }

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

  public void testWeberE() {
    check("WeberE(1.5, 3.5)", //
        "0.212207*HypergeometricPFQ({1.0},{0.25,1.75},-3.0625)+0.891268*HypergeometricPFQ({1.0},{0.75,2.25},-3.0625)");
  }
}
