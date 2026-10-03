package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Elliptic integrals off the principal strip of the amplitude, at the pole <code>m == 1</code>, for a
 * large negative characteristic, and over <code>Interval</code> and <code>IntervalData</code>.
 */
public class EllipticIntegralsTest extends ExprEvaluatorTestCase {

  @Test
  public void testEllipticEMachineOffThePrincipalStrip() {
    // at m == 1 the integral is Sin(r)+2*k for phi == r+k*Pi; the amplitude was handed on unreduced
    // and came back as a number of the order 10^10
    check("EllipticE(2.5, 1.0)", //
        "1.40153");
    check("EllipticE(-4.0, 1.0)", //
        "-2.7568");
    check("N(EllipticE(5/2, 1), 25)", //
        "1.401527855896043505948145");
    check("N(EllipticE(-4, 1), 25)", //
        "-2.756802495307928251372639");
    // the exact form stays as it is
    check("EllipticE(5/2, 1)", //
        "EllipticE(5/2,1)");
    // the machine value and the arbitrary precision value agree
    check("EllipticE(7.5, 0.25)", //
        "7.02869");
    check("N(EllipticE(15/2, 1/4), 25)", //
        "7.028685194161879741475398");
    check("EllipticE(-7.5, -1.75)", //
        "-10.02053");
    check("N(EllipticE(-15/2, -7/4), 25)", //
        "-10.02052861143608434482502");
  }

  @Test
  public void testPoleAtParameterOne() {
    // 1/Cos(t) is not integrable over Pi/2
    check("EllipticF(2.5, 1.0)", //
        "ComplexInfinity");
    check("N(EllipticF(5/2, 1), 25)", //
        "ComplexInfinity");
    check("EllipticPi(0.25, 2.5, 1.0)", //
        "ComplexInfinity");
    check("N(EllipticPi(1/4, 5/2, 1), 25)", //
        "ComplexInfinity");
    // before the pole the value is finite
    check("EllipticPi(0.25, 1.25, 1.0)", //
        "2.08472");
  }

  @Test
  public void testEllipticPiLargeNegativeCharacteristic() {
    // Pi/(2*Sqrt(-n)) to the first order. The two Carlson terms cancel down to that size, and the
    // machine value used to be EllipticK(0.4)
    check("EllipticPi(-1.0*10^24, 0.4)", //
        "1.5708*10^-12");
    check("N(EllipticPi(-10^24, 2/5), 30)", //
        "0.00000000000157079632679527474646391441316");
    // twice the precision gives the same digits
    check("N(EllipticPi(-10^24, 2/5), 60)", //
        "0.00000000000157079632679527474646391441315556253707477905304159248457054");
    check("EllipticPi(-1.0*10^24, 0.9, 0.4)", //
        "1.5708*10^-12");
    check("N(EllipticPi(-10^24, 9/10, 2/5), 30)", //
        "0.0000000000015707963267942973735050251932");
  }

  @Test
  public void testEllipticPiNegativeCharacteristic() {
    check("EllipticPi(-6.5, 0.9, 0.4)", //
        "0.485313");
    check("N(EllipticPi(-13/2, 9/10, 2/5), 25)", //
        "0.4853129233674435799861194");
    // amplitude off the principal strip
    check("EllipticPi(-6.5, 5.25, 0.4)", //
        "1.93481");
    check("N(EllipticPi(-13/2, 21/4, 2/5), 25)", //
        "1.934813752556782897973313");
    check("N(EllipticPi(-13/2, 21/4, 2/5), 50)", //
        "1.9348137525567828979733130280452656820387691726301");
    check("EllipticPi(-2.5, 0.4)", //
        "0.91447");
    check("N(EllipticPi(-5/2, 2/5), 25)", //
        "0.914470187116909914298357");
    // negative parameter
    check("EllipticPi(-6.5, 1.1, -2.25)", //
        "0.442488");
    check("N(EllipticPi(-13/2, 11/10, -9/4), 25)", //
        "0.4424875964409359666026842");
  }

  @Test
  public void testEllipticPiQuasiPeriod() {
    // EllipticPi(n, phi+Pi, m) == EllipticPi(n, phi, m)+2*EllipticPi(n, m)
    check("N(EllipticPi(2/7, 3/5+Pi, 2/5), 25)", //
        "4.88706721591214727559938");
    check("N(EllipticPi(2/7, 3/5, 2/5) + 2*EllipticPi(2/7, 2/5), 25)", //
        "4.88706721591214727559938");
    // an inexact amplitude is reduced like an exact one
    check("N(EllipticPi(2/7, 3.7415926535897932384626434`25, 2/5), 25)", //
        "4.887067215912147275599379");
    // the principal value for n > 1
    check("N(EllipticPi(7/4, 2/5), 20)", //
        "-0.26306693863820197492");
  }

  @Test
  public void testArgumentRoundedOntoOne() {
    // 20 digits cannot tell 1-10^-40 from 1: no exception leaves the evaluation
    check("N(EllipticE(1 - 10^(-40)), 20)", //
        "1");
    check("N(EllipticE(1 - 10^(-30)), 20)", //
        "1");
    check("N(EllipticK(1 - 10^(-30)), 20)", //
        "ComplexInfinity");
    // with digits enough for the argument: Log(4)+15*Log(10) to the first order
    check("N(EllipticK(1 - 10^(-30)), 40)", //
        "35.92507075603057587910433606319054751785");
  }

  @Test
  public void testEllipticPiSymbolic() {
    // odd in the amplitude
    check("EllipticPi(n, -phi, m)", //
        "-EllipticPi(n,phi,m)");
    check("EllipticPi(n, -3*phi, m)", //
        "-EllipticPi(n,3*phi,m)");
    check("EllipticPi(n, phi, 0)", //
        "EllipticPi(n,phi,0)");
    check("EllipticPi(Infinity, m)", //
        "0");
    check("EllipticPi(-Infinity, m)", //
        "0");
    check("EllipticPi(n, Infinity)", //
        "0");
    check("EllipticPi(n, -Infinity)", //
        "0");
  }

  @Test
  public void testEllipticKInterval() {
    // increasing for m < 1
    check("EllipticK(Interval({0.25, 0.75}))", //
        "Interval({1.68575,2.15652})");
    check("EllipticK(Interval({1/4, 3/4}))", //
        "Interval({EllipticK(1/4),EllipticK(3/4)})");
    check("EllipticK(Interval({-2.0, -1.0}, {0.25, 0.75}))", //
        "Interval({1.17142,1.31103},{1.68575,2.15652})");
    // an interval which reaches the pole is not mapped
    check("EllipticK(Interval({0, 1}))", //
        "EllipticK(Interval({0,1}))");
    check("EllipticK(Interval({0.75, 1.25}))", //
        "EllipticK(Interval({0.75,1.25}))");
  }

  @Test
  public void testEllipticEInterval() {
    // decreasing for m <= 1: the end points change places
    check("EllipticE(Interval({0.25, 0.75}))", //
        "Interval({1.21106,1.46746})");
    check("EllipticE(Interval({-1, 0}))", //
        "Interval({Pi/2,(Pi^2+2*Gamma(3/4)^4)/(2*Sqrt(2*Pi)*Gamma(3/4)^2)})");
    check("EllipticE(Interval({0, 1}))", //
        "Interval({1,Pi/2})");
    check("EllipticE(Interval({0.75, 1.25}))", //
        "EllipticE(Interval({0.75,1.25}))");
  }

  @Test
  public void testEllipticKIntervalData() {
    check("EllipticK(IntervalData({0.25, Less, LessEqual, 0.75}))", //
        "IntervalData({1.68575,Less,LessEqual,2.15652})");
    check("EllipticK(IntervalData({-1, Less, Less, 0}))", //
        "IntervalData({Gamma(1/4)^2/(4*Sqrt(2*Pi)),Less,Less,Pi/2})");
    check("EllipticK(IntervalData({0, LessEqual, Less, 1}))", //
        "EllipticK(IntervalData({0,LessEqual,Less,1}))");
    check("EllipticK(IntervalData({0.75, Less, LessEqual, 1.25}))", //
        "EllipticK(IntervalData({0.75,Less,LessEqual,1.25}))");
  }

  @Test
  public void testEllipticEIntervalData() {
    // the open and the closed end change places with the end points
    check("EllipticE(IntervalData({0.25, Less, LessEqual, 0.75}))", //
        "IntervalData({1.21106,LessEqual,Less,1.46746})");
    check("EllipticE(IntervalData({0, LessEqual, Less, 1}))", //
        "IntervalData({1,Less,LessEqual,Pi/2})");
  }

  @Test
  public void testCarlsonArbitraryPrecision() {
    check("N(CarlsonRF(2, 3, 5), 30)", //
        "0.559406346700304447071282314417");
    check("N(CarlsonRD(2, 3, 5), 30)", //
        "0.133211092782014589043491564806");
    check("N(CarlsonRJ(2, 3, 5, 7), 30)", //
        "0.106488423573093638325250972854");
    check("N(CarlsonRG(2, 3, 5), 30)", //
        "1.81302733147391264209168400404");
    // two arguments zero: the integral is infinite
    check("N(CarlsonRF(0, 0, 5), 30)", //
        "ComplexInfinity");
  }
}
