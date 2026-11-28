package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Tests for the tier 2.2 special functions: MittagLefflerE, QGamma, the Neville theta functions, InverseEllipticNomeQ, CarlsonRE, AppellF2, AppellF3, AppellF4, CoulombH1, CoulombH2, FresnelF, FresnelG and CardinalBSplineBasis. */
public class ClassicalSpecialFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testMittagLefflerE() {
    check("MittagLefflerE(a, 0)", //
        "1");
    check("MittagLefflerE(1, z)", //
        "E^z");
    check("MittagLefflerE(2, z)", //
        "Cosh(Sqrt(z))");
    check("MittagLefflerE(1/2, z)", //
        "E^z^2*Erfc(-z)");
    check("MittagLefflerE(3, z)", //
        "1/3*(E^z^(1/3)+(2*Cos(1/2*Sqrt(3)*z^(1/3)))/E^(z^(1/3)/2))");
    check("MittagLefflerE(4, z)", //
        "1/4*(E^(-z^(1/4))+E^z^(1/4)+2*Cos(z^(1/4)))");
    check("{N(MittagLefflerE(3, 2)), MittagLefflerE(3, 2.)}", //
        "{1.33891,1.33891}");
    check("MittagLefflerE(1, 2, z)", //
        "(-1+E^z)/z");
    check("MittagLefflerE(a, b, 0)", //
        "1/Gamma(b)");
    check("MittagLefflerE(0.5, 0.7)", //
        "2.7387");
    check("MittagLefflerE(2.5, -3.0)", //
        "0.170395");
    check("MittagLefflerE(1.5, 0.5, 2.0)", //
        "4.16363");
    check("MittagLefflerE(3, -40.0)", //
        "-3.61554");
  }

  @Test
  public void testArbitraryPrecision() {
    check("N(MittagLefflerE(3/2, 1), 30)", //
        "1.93948726143374896654090184586");
    check("N(AppellF4(1/2, 1, 2, 3, 1/10, 1/5), 25)", //
        "1.069706943330583917909136");
  }

  @Test
  public void testQGamma() {
    check("QGamma(5, 1/2)", //
        "315/64");
    check("QGamma(3, q)", //
        "1+q");
    check("QGamma(0, q)", //
        "ComplexInfinity");
    check("QGamma(2.5, 0.3)", //
        "1.12395");
  }

  @Test
  public void testNevilleThetaS() {
    check("NevilleThetaS(0, m)", //
        "0");
    check("NevilleThetaS(-z, m)", //
        "-NevilleThetaS(z,m)");
    check("NevilleThetaS(0.7, 0.3)/NevilleThetaN(0.7, 0.3)", //
        "0.632305");
  }

  @Test
  public void testNevilleThetaC() {
    check("NevilleThetaC(z, 0)", //
        "Cos(z)");
  }

  @Test
  public void testNevilleThetaN() {
    check("NevilleThetaN(z, 1)", //
        "Cosh(z)");
  }

  @Test
  public void testNevilleThetaSAgainstJacobiSN() {
    check("JacobiSN(0.7, 0.3)", //
        "0.632305");
  }

  @Test
  public void testNevilleThetaAgainstJacobi() {
    check("Chop(NevilleThetaC(0.4, 0.6)/NevilleThetaN(0.4, 0.6) - JacobiCN(0.4, 0.6))", //
        "0");
    check("Chop(NevilleThetaD(0.4, 0.6)/NevilleThetaN(0.4, 0.6) - JacobiDN(0.4, 0.6))", //
        "0");
  }

  @Test
  public void testInverseEllipticNomeQ() {
    check("InverseEllipticNomeQ(EllipticNomeQ(0.37))", //
        "0.37");
    check("InverseEllipticNomeQ(0)", //
        "0");
  }

  @Test
  public void testCarlsonRE() {
    check("CarlsonRE(4, 4)", //
        "2");
    check("CarlsonRE(0, y)", //
        "(2*Sqrt(y))/Pi");
    check("CarlsonRE(1.0, 2.0)", //
        "1.21601");
    check("CarlsonRE(1, 2)", //
        "CarlsonRE(1,2)");
  }

  @Test
  public void testAppellF() {
    check("AppellF2(a, b1, b2, c1, c2, x, 0)", //
        "Hypergeometric2F1(a,b1,c1,x)");
    check("AppellF3(a1, a2, b1, b2, c, 0, y)", //
        "Hypergeometric2F1(a2,b2,c,y)");
    check("AppellF4(a, b, c1, c2, 0, 0)", //
        "1");
    check("AppellF2(1/2, 1, 2, 3, 4, 0.2, 0.3)", //
        "1.13465");
    check("AppellF3(1, 2, 1/2, 1/3, 3, 0.4, -0.5)", //
        "0.984154");
    check("AppellF4(1/2, 1, 2, 3, 0.1, 0.2)", //
        "1.06971");
    check("AppellF2(1, 1, 1, 2, 2, 0.7, 0.6)", //
        "AppellF2(1.0,1.0,1.0,2.0,2.0,0.7,0.6)");
    check("AppellF2(-2, 1, 1, 2, 2, 3.0, 4.0)", //
        "8.33333");
  }

  @Tag(TestTags.SLOW)
  @Test
  public void testCoulombH() {
    check("CoulombH1(0, 1.0, 2.0)", //
        "1.27578+I*0.661782");
    check("CoulombH2(0, 1.0, 2.0)", //
        "1.27578+I*(-0.661782)");
    check("CoulombH1(l, eta, rho)", //
        "CoulombH1(l,eta,rho)");
  }

  @Test
  public void testFresnelF() {
    check("FresnelF(0)", //
        "1/2");
    check("FresnelF(1.3)", //
        "0.229507");
    check("FresnelF(x)", //
        "FresnelF(x)");
  }

  @Test
  public void testFresnelG() {
    check("FresnelG(Infinity)", //
        "0");
    check("FresnelG(1.3)", //
        "0.0352552");
  }

  @Test
  public void testCardinalBSplineBasis() {
    check("CardinalBSplineBasis(3, 0)", //
        "2/3");
    check("CardinalBSplineBasis(3, 1/2)", //
        "23/48");
    check("CardinalBSplineBasis(2, 7)", //
        "0");
    check("CardinalBSplineBasis(1, 0.25)", //
        "0.75");
    check("CardinalBSplineBasis(3, x)", //
        "CardinalBSplineBasis(3,x)");
  }
}
