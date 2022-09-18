package org.matheclipse.core.system;

public class BesselFunctionTest extends ExprEvaluatorTestCase {
  public BesselFunctionTest(String name) {
    super(name);
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
