package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Orthogonal polynomials of a large integer degree at inexact arguments, which are evaluated with
 * the recurrence relation in the degree: their explicit sums cancel catastrophically. The machine
 * values agree with independent 60 digit calculations to about 1e-15.
 */
public class OrthogonalPolynomialNumericTest extends ExprEvaluatorTestCase {

  @Test
  public void testLegendreP() {
    checkNumeric("LegendreP(1000,0.3)", //
        "-0.02566916750793618");
    // P(-n-1) == P(n)
    checkNumeric("LegendreP(-1001,0.3)", //
        "-0.02566916750793618");
    // all arguments inexact
    checkNumeric("LegendreP(1000.0,0.3)", //
        "-0.02566916750793618");
    check("LegendreP(1000,0.3`30)", //
        "-0.0256691675079361898775335961185");
    checkNumeric("LegendreP(-4,0.3)", //
        "-0.3825");
  }

  @Test
  public void testAssociatedLegendreP() {
    checkNumeric("LegendreP(300,3,0.3)", //
        "555489.1732329841");
    checkNumeric("LegendreP(300,3,2,0.3)", //
        "555489.1732329841");
    checkNumeric("LegendreP(5,2,3,2.0)", //
        "3465.0");
    checkNumeric("LegendreP(4,-2,0.3)", //
        "-0.007014583333333333");
    checkNumeric("LegendreP(300,3,3,1.5)", //
        "2.3139441588814647E131");
    checkNumeric("LegendreP(300,-3,0.3)", //
        "-7.544853728596479E-10");
  }

  @Test
  public void testLegendreQ() {
    checkNumeric("LegendreQ(300,0.3)", //
        "-0.03247033088373957");
  }

  @Test
  public void testGegenbauerC() {
    checkNumeric("GegenbauerC(300,2,0.3)", //
        "-101.84751168834799");
    // 2/n*ChebyshevT(n,z)
    checkNumeric("GegenbauerC(300,0.3)", //
        "-0.006365731241832996");
  }

  @Test
  public void testJacobiP() {
    checkNumeric("JacobiP(300,1,2,0.3)", //
        "-0.12085921288276064");
    // a negative integer parameter, where the recurrence meets a zero coefficient
    checkNumeric("JacobiP(300,-1,-1,0.3)", //
        "-0.02222888405311752");
    checkNumeric("JacobiP(300,2,-3,0.3)", //
        "0.03310915692786527");
  }

  @Test
  public void testSphericalHarmonicY() {
    checkNumeric("SphericalHarmonicY(300,3,0.3,0.2)", //
        "0.47970612570075666+I*0.3281846177788743");
    // Y(l,-m) == (-1)^m*Conjugate(Y(l,m))
    checkNumeric("SphericalHarmonicY(2,-1,0.3,0.2)", //
        "0.21375920550357796+I*(-0.04333113613793583)");
    checkNumeric("SphericalHarmonicY(300,-3,0.3,0.2)", //
        "-0.47970612570075666+I*0.3281846177788743");
  }

  @Test
  public void testZernikeR() {
    checkNumeric("ZernikeR(150,2,0.3)", //
        "-0.03474182538886356");
    checkNumeric("ZernikeR(6,2,0.4)", //
        "0.50944");
  }
}
