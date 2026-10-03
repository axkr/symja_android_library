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
        "1.001077380456106236+I*(-1.1714200841467698589)");
    check("N(EllipticE(3), 20)", //
        "0.475223935351017111+I*1.0130180585994313264");
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
