package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Machine values of special functions which are rounded correctly. <code>SetPrecision(x, 20)</code>
 * shows the binary value of a machine number <code>x</code>, so a wrong last bit is visible.
 */
public class MachineSpecialFunctionAccuracyTest extends ExprEvaluatorTestCase {

  @Test
  public void testArcTanh() {
    check("SetPrecision(ArcTanh(0.3), 20)", //
        "0.30951960420311175337");
    check("SetPrecision(ArcTanh(-0.7), 20)", //
        "-0.86730052769405308055");
    check("SetPrecision(ArcTanh(0.05), 20)", //
        "0.050041729278491271782");
  }

  @Test
  public void testSincAtMachineZero() {
    check("Sinc(0.0)", //
        "1.0");
    check("Sinc(0)", //
        "1");
  }

  @Test
  public void testCosIntegralNegative() {
    // Ci(-x) == Ci(x)+I*Pi
    check("SetPrecision(Re(CosIntegral(-1.5)), 20)", //
        "0.47035631719539988671");
    check("Im(CosIntegral(-1.5))", //
        "3.14159");
    check("SetPrecision(Re(CoshIntegral(-0.8)), 20)", //
        "0.51839998483339155833");
    check("Im(CoshIntegral(-0.8))", //
        "3.14159");
    check("SetPrecision(SinIntegral(1.3), 20)", //
        "1.1839580090760628561");
  }

  @Test
  public void testPolyLogSeries() {
    check("SetPrecision(PolyLog(3,-0.4), 20)", //
        "-0.38203710293930998043");
    check("SetPrecision(PolyLog(4,0.45), 20)", //
        "0.46397949403631344456");
  }

  @Test
  public void testNFractionalPrecision() {
    check("Precision(N(E,1/3))", //
        "0.3333333333333333");
    check("Precision(N(Pi,0.5))", //
        "0.5");
  }
}
