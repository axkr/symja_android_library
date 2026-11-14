package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Elliptic integrals and Carlson symmetric integrals in arbitrary precision, and the Cauchy
 * principal value of <code>EllipticPi</code> for a characteristic <code>n &gt; 1</code>.
 */
public class EllipticArbitraryPrecisionTest extends ExprEvaluatorTestCase {

  @Test
  public void testEllipticKBeyondOne() {
    // WMA: N[EllipticK[3], 20] - the complex value of EllipticK, not the one of EllipticE
    check("N(EllipticK(3), 20)", //
        "1.0010773804561062361+I*(-1.1714200841467698589)");
    check("N(EllipticE(3), 20)", //
        "0.47522393535101711103+I*1.0130180585994313265");
  }

  @Test
  public void testArgumentsOfOwnPrecision() {
    // numbers of 25 digits outside N(): the value has their digits, not those of a machine number
    check("Abs(EllipticF(N(2/7, 25), N(3/11, 25)) - N(EllipticF(2/7, 3/11), 25)) < 10^-23", //
        "True");
    check("Precision(EllipticE(N(2/7, 25), N(3/11, 25)))", //
        "25");
    check("Precision(EllipticPi(N(1/9, 25), N(2/7, 25), N(3/11, 25)))", //
        "25");
    // the lowest precision of the arguments
    check("Precision(EllipticF(N(2/7, 40), N(3/11, 22)))", //
        "22");
    check("Precision(InverseJacobiSN(N(2/7, 25), N(3/11, 25)))", //
        "25");
    check("Precision(CarlsonRF(N(2/7, 25), N(3/11, 25), N(1/5, 25)))", //
        "25");
    // real arguments inside the real range give a real machine number
    check("Head(EllipticF(0.29, 0.25))", //
        "Real");
    check("Head(EllipticE(0.29, 0.25))", //
        "Real");
    check("Head(EllipticPi(0.1, 0.29, 0.25))", //
        "Real");
    check("Head(EllipticPi(0.29, 0.25))", //
        "Real");
    check("Head(EllipticPi(-2.0, -0.7))", //
        "Real");
    // a real value compares with a real number
    check("EllipticPi(0.5, 0.2) < 3", //
        "True");
    check("Max(EllipticPi(0.5, 0.2), 2)", //
        "2.36967");
    // m > 1: the value is complex
    check("Head(EllipticPi(0.5, 1.5))", //
        "Complex");
  }

  @Test
  public void testEllipticFArbitraryPrecision() {
    check("N(EllipticF(1, 1/3), 20)", //
        "1.0515224356380810195");
    // quasi-periodic continuation off the principal strip
    check("N(EllipticF(5, 1/3), 20)", //
        "5.5516548850856641337");
    check("N(EllipticF(1 + I, 1/3), 20)", //
        "0.88732054939399825808+I*1.132389514228930602");
    check("N(EllipticF(1, 3), 20)", //
        "1.0010773804561062361+I*(-0.7284539368191767961)");
    check("N(EllipticF(1/2, 1/3), 20)", //
        "0.5068477562654311092");
    // mpmath: 0.506847756265431109203677128745872542883984995337 - the duplication runs until the
    // requested precision is reached
    check("N(EllipticF(1/2, 1/3), 50)", //
        "0.506847756265431109203677128745872542883984995337");
  }

  @Test
  public void testEllipticEArbitraryPrecision() {
    // WMA: N[{EllipticE[1, 1/3], EllipticE[5, 1/3], EllipticE[1 + I, 1/3]}, 20]
    check("N(EllipticE(1, 1/3), 20)", //
        "0.95265941432230398348");
    check("N(EllipticE(5, 1/3), 20)", //
        "4.5273618829012259406");
    check("N(EllipticE(1 + I, 1/3), 20)", //
        "1.0767375710706947669+I*0.85507271523663404161");
    // mpmath: 4.52736188290122594061898647807
    check("N(EllipticE(5, 1/3), 30)", //
        "4.52736188290122594061898647807");
  }

  @Test
  public void testEllipticPiArbitraryPrecision() {
    // WMA: N[{EllipticPi[1/2, 1/3], EllipticPi[2, 1/3], EllipticPi[1/2, 1, 1/3],
    // EllipticPi[2, 1, 1/3]}, 20]
    check("N(EllipticPi(1/2, 1/3), 20)", //
        "2.4952460470776346637");
    check("N(EllipticPi(2, 1/3), 20)", //
        "-0.17427532075260804865");
    check("N(EllipticPi(1/2, 1, 1/3), 20)", //
        "1.2467794552770125348");
    check("N(EllipticPi(2, 1, 1/3), 20)", //
        "0.73138107867560133953");
    check("N(EllipticPi(1/2, 5, 1/3), 20)", //
        "8.1675311664202220789");
    // the amplitude Pi/2 is on the principal strip and isn't reduced
    check("N(EllipticPi(1/2, Pi/2, 1/3), 30)", //
        "2.49524604707763466370027723508");
  }

  @Test
  public void testEllipticPiPrincipalValue() {
    // WMA: {EllipticPi[2, 0.3], EllipticPi[2, 1., 0.3], EllipticPi[0.5, 5., 0.3]}
    check("{EllipticPi(2, 0.3), EllipticPi(2, 1., 0.3), EllipticPi(0.5, 5., 0.3)}", //
        "{-0.151823,0.73559,8.04974}");
    // m > 1: the integrand itself is complex, so there is no real principal value
    check("EllipticPi(2.0, 3.0)", //
        "-0.439554+I*(-0.564853)");
  }

  @Test
  public void testEllipticPiZeroImaginaryCharacteristic() {
    // (1.5*I)^2*(-4/3) is 3.0-0.0*I: a complex number with a zero imaginary part gets the principal
    // value of the real characteristic
    check("{EllipticPi((1.5*I)^2*(-4/3), 1.2, 0.5), EllipticPi(3.0, 1.2, 0.5)}", //
        "{0.0828968,0.0828968}");
    check("{EllipticPi((1.5*I)^2*(-4/3), 0.5), EllipticPi(3.0, 0.5)}", //
        "{-0.192988,-0.192988}");
    // one characteristic in two forms, the second one the square of an imaginary sum
    check("n1 = (2 + Sqrt(3))^2/(1 + Sqrt(2)); n2 = -(2*I + I*Sqrt(3))^2/(1 + Sqrt(2)); "
        + "PossibleZeroQ(EllipticPi(n1, 6/5, 1/2) - EllipticPi(n2, 6/5, 1/2))", //
        "True");
    check("N(EllipticPi(n2, 6/5, 1/2))", //
        "0.0203948");
    check("Chop(N(EllipticPi(n1, 6/5, 1/2) - EllipticPi(n2, 6/5, 1/2), 30))", //
        "0");
    check("Chop(N(EllipticPi(n1, 1/2) - EllipticPi(n2, 1/2), 30))", //
        "0");
  }

  @Test
  public void testEllipticPiComplexCharacteristicNextToTheAxis() {
    // a characteristic just above the real axis beyond 1: conjugate characteristics have conjugate
    // values, and both are computed as fast
    check("N(EllipticPi(3 + I/1000, 1/2), 20)", //
        "-0.19264343884415621527+I*1.2168075848531545776");
    check("N(EllipticPi(3 - I/1000, 1/2), 20)", //
        "-0.19264343884415621527+I*(-1.2168075848531545776)");
    // a parameter whose branch cut is on the other side of the path
    check("EllipticPi(3.0 + 0.001*I, 2.0 - 0.25*I)", //
        "0.155618+I*3.19355");
    check("N(EllipticPi(3 + I/1000, 2 - I/3), 20)", //
        "0.29939103205457682546+I*3.1213347863305381828");
  }

  @Test
  public void testCarlsonArbitraryPrecision() {
    check("N(CarlsonRF(1, 2, 3), 30)", //
        "0.72694593546890819853957062602");
    check("N(CarlsonRD(1, 2, 3), 30)", //
        "0.290460281028990644232653385659");
    check("N(CarlsonRG(1, 2, 3), 30)", //
        "1.40184709999089509943135215626");
    check("N(CarlsonRJ(1, 2, 3, 4), 30)", //
        "0.239848099749567762175861671042");
    // mpmath: 0.72694593546890819853957062601989181443786387872278
    check("N(CarlsonRF(1, 2, 3), 50)", //
        "0.72694593546890819853957062601989181443786387872278");
    // divergent integrals, and CarlsonRG(0,0,z) == Sqrt(z)/2
    check("{N(CarlsonRF(0, 0, 1), 30), N(CarlsonRD(0, 0, 1), 30)}", //
        "{ComplexInfinity,ComplexInfinity}");
    check("{CarlsonRG(0, 0, 4), CarlsonRG(0., 0., 1.)}", //
        "{1,0.5}");
  }
}
