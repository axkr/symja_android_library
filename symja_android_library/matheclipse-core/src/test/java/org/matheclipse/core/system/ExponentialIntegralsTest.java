package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.functions.ExponentialIntegrals;

/**
 * Accuracy of the <code>double</code> exponential and trigonometric integrals.
 *
 * <p>
 * These replace a route that went through the Complex implementations in {@code GammaJS}, which
 * were measurably wrong: against Apfloat at 40 digits, {@code Num.expIntegralEi} was off by 4.9e-4
 * on the negative side, {@code Num.cosIntegral} by 2.2e-7 and {@code Num.sinIntegral} by 3.2e-10.
 * Three baked expectations in {@code GammaBetaErfTest} had been taken from that route and were
 * corrected along with this. The reference values below are Apfloat at 40 digits, baked in rather
 * than recomputed - producing them is exactly the cost this implementation avoids.
 */
public class ExponentialIntegralsTest {

  /**
   * Generous next to the measured worst case of about 4e-14, tight enough to catch a regression.
   */
  private static final double MAX_ERROR = 1.0e-12;

  private static final double[][] REFERENCE = {
      // {x, Si(x), Ci(x), Shi(x), Chi(x), Ei(x), E1(x)} at 40 digits;
      // NaN marks a value a double cannot hold at that argument
      {1.0E-6, 9.999999999999443E-7, -13.238294893062992, 1.0000000000000554E-6,
          -13.238294893062491, -13.238293893062492, 13.23829589306249},
      {0.01, 0.009999944444611112, -4.027979520982392, 0.01000005555572222, -4.027929520982392,
          -4.017929465426669, 4.037929576538114},
      {0.25, 0.24913357031975716, -0.8246630625809456, 0.2508696848909122, -0.7934129495528259,
          -0.5425432646619137, 1.0442826344437381},
      {0.7, 0.6812222391166113, 0.10051470700889784, 0.7193380189288998, 0.3455691756953907,
          1.0649071946242905, 0.3737688432335091},
      {1.0, 0.9460830703671831, 0.3374039229009681, 1.0572508753757286, 0.8378669409802083,
          1.895117816355937, 0.21938393439552029},
      {1.5, 1.3246835311721197, 0.47035631719539983, 1.7006525157682153, 1.6006329333615827,
          3.301285449129798, 0.10001958240663264},
      {1.9999, 1.6053675097543678, 0.4230016343635392, 2.501386095206357, 2.4524788172265986,
          4.953864912432956, 0.04890727797975845},
      {2.0, 1.605412976802695, 0.422980828774865, 2.5015674333549756, 2.4526669226469147,
          4.954234356001891, 0.04890051070806111},
      {2.0001, 1.6054584394970441, 0.42296001968007074, 2.501748781247422, 2.4528550367960436,
          4.9546038180434655, 0.04889374445137839},
      {2.8, 1.8320965890813223, 0.18648838964317585, 4.34807650812719, 4.331221215681974,
          8.679297723809164, 0.01685529244521605},
      {3.5, 1.8331253986659972, -0.03212854851248111, 6.966162067504942, 6.959191927647393,
          13.925353995152335, 0.006970139857548393},
      {5.0, 1.5499312449446743, -0.19002974965664388, 20.093211825697228, 20.09206353010595,
          40.18527535580318, 0.0011482955912753257},
      {7.3, 1.486436445063168, 0.1037886664320276, 122.55809730180925, 122.55801491456481,
          245.11611221637406, 8.238724444880837E-5},
      {10.0, 1.6583475942188741, -0.04545643300445537, 1246.1144901994232, 1246.1144860424545,
          2492.2289762418777, 4.156968929685324E-6},
      {15.0, 1.6181944437083686, 0.04627867767436043, 117477.92624539374, 117477.92624537456,
          234955.8524907683, 1.918627892147867E-8},
      {25.0, 1.5314825509999612, -0.006848597179702591, 1.5029754532627742E9, 1.5029754532627742E9,
          3.0059509065255485E9, 5.348899755340217E-13},
      {40.0, 1.5869851193547846, 0.01902000789620876, 3.01985913180562E15, 3.01985913180562E15,
          6.039718263611241E15, 1.036773261451656E-19},
      {80.0, 1.5723308869124875, -0.01240250115507095, 3.507300002452399E32, 3.507300002452399E32,
          7.014600004904798E32, 2.228543258688473E-37},
      {150.0, 1.5661668327225209, -0.004796488992910548, 4.677091364132689E62, 4.677091364132689E62,
          9.354182728265378E62, 4.751924906560163E-68},
      {300.0, 1.5708810882137496, -0.0033321999185921118, 3.2482412540443327E127,
          3.2482412540443327E127, 6.496482508088665E127, 1.7103842768045102E-133},};

  private static void assertClose(double expected, double actual, String what) {
    if (Double.isNaN(expected)) {
      return; // a double cannot hold the reference here
    }
    double error = Math.abs(expected) < 1.0e-290 ? Math.abs(actual - expected)
        : Math.abs((actual - expected) / expected);
    assertTrue(error < MAX_ERROR,
        what + ": expected " + expected + " but was " + actual + " (relative error " + error + ")");
  }

  @Test
  public void testAgainstReferenceValues() {
    for (double[] row : REFERENCE) {
      double x = row[0];
      assertClose(row[1], ExponentialIntegrals.sinIntegral(x), "Si(" + x + ")");
      assertClose(row[2], ExponentialIntegrals.cosIntegral(x), "Ci(" + x + ")");
      assertClose(row[3], ExponentialIntegrals.sinhIntegral(x), "Shi(" + x + ")");
      assertClose(row[4], ExponentialIntegrals.coshIntegral(x), "Chi(" + x + ")");
      assertClose(row[5], ExponentialIntegrals.expIntegralEi(x), "Ei(" + x + ")");
      assertClose(row[6], ExponentialIntegrals.expIntegralE1(x), "E1(" + x + ")");
    }
  }

  /** Si and Shi are odd; the negative side is not covered by the table above. */
  @Test
  public void testOddSymmetry() {
    for (double[] row : REFERENCE) {
      double x = row[0];
      assertEquals(-ExponentialIntegrals.sinIntegral(x), ExponentialIntegrals.sinIntegral(-x),
          Math.abs(row[1]) * 1.0e-15, "Si(-" + x + ")");
      if (Double.isFinite(row[3])) {
        assertEquals(-ExponentialIntegrals.sinhIntegral(x), ExponentialIntegrals.sinhIntegral(-x),
            Math.abs(row[3]) * 1.0e-15, "Shi(-" + x + ")");
      }
    }
  }

  /**
   * Ci and Chi are not real for a negative argument - they pick up an imaginary Pi - so they must
   * report NaN and let the caller fall back rather than return a real number.
   */
  @Test
  public void testNotRealForNegativeArgument() {
    for (double x : new double[] {-0.5, -1.0, -7.3, -100.0}) {
      assertTrue(Double.isNaN(ExponentialIntegrals.cosIntegral(x)), "Ci(" + x + ")");
      assertTrue(Double.isNaN(ExponentialIntegrals.coshIntegral(x)), "Chi(" + x + ")");
      assertTrue(Double.isNaN(ExponentialIntegrals.expIntegralE1(x)), "E1(" + x + ")");
    }
  }

  /**
   * The identities Chi + Shi = Ei and Chi - Shi = -E1 tie the hyperbolic integrals to the
   * exponential one, and hold across the series/continued-fraction switch at |x| = 2.
   */
  @Test
  public void testHyperbolicIdentities() {
    for (double x : new double[] {0.1, 0.5, 1.0, 1.9999, 2.0, 2.0001, 3.0, 10.0, 30.0}) {
      double shi = ExponentialIntegrals.sinhIntegral(x);
      double chi = ExponentialIntegrals.coshIntegral(x);
      double ei = ExponentialIntegrals.expIntegralEi(x);
      double e1 = ExponentialIntegrals.expIntegralE1(x);
      assertEquals(ei, chi + shi, Math.abs(ei) * 1.0e-12, "Chi+Shi=Ei at x=" + x);
      // Chi - Shi is judged on an ABSOLUTE tolerance scaled by the operands, not a relative one.
      // Both grow like e^x/(2x) while their difference decays like e^-x/x, so by x=10 they are
      // about 1100 each and the difference is 4e-6: nine decades of cancellation. Rounding of the
      // two large values at 1e-16 relative therefore swamps the small difference, and no
      // implementation carrying doubles can do better - the relative error of the difference is
      // simply not a property of this code.
      assertEquals(-e1, chi - shi, Math.max(Math.abs(chi), Math.abs(shi)) * 1.0e-13,
          "Chi-Shi=-E1 at x=" + x);
    }
  }

  /** Ei(-x) = -E1(x) ties the two exponential integrals together. */
  @Test
  public void testEiAndE1() {
    for (double x : new double[] {0.05, 0.5, 1.0, 2.0, 5.0, 20.0, 100.0}) {
      assertEquals(-ExponentialIntegrals.expIntegralE1(x), ExponentialIntegrals.expIntegralEi(-x),
          Math.abs(ExponentialIntegrals.expIntegralE1(x)) * 1.0e-13, "Ei(-x) = -E1(x) at x=" + x);
    }
  }

  @Test
  public void testDomainBoundaries() {
    assertTrue(ExponentialIntegrals.isSupported(500.0));
    assertFalse(ExponentialIntegrals.isSupported(500.1));
    assertFalse(ExponentialIntegrals.isSupported(-500.1));
    assertFalse(ExponentialIntegrals.isSupported(Double.NaN));
  }
}
