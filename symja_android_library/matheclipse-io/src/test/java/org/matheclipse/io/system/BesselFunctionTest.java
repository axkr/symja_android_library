package org.matheclipse.io.system;

public class BesselFunctionTest extends AbstractTestCase {
  public BesselFunctionTest(String name) {
    super(name);
  }

  public void testSphericalHankelH1() {
    check(
        "SphericalHankelH1(3, 1.5)", //
        "0.0283246+I*(-3.78927)");
  }
  
  public void testSphericalHankelH2() {
    check(
        "SphericalHankelH2(3, 1.5)", //
        "0.0283246+I*3.78927");
  }

  public void testWeberE() {
    check(
        "WeberE(1.5, 3.5)", //
        "0.212207*HypergeometricPFQ({1.0},{0.25,1.75},-3.0625)+0.891268*HypergeometricPFQ({1.0},{0.75,2.25},-3.0625)");
  }
}
