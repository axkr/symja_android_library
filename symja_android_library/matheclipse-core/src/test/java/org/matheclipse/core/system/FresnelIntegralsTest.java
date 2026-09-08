package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.functions.FresnelIntegrals;

/**
 * Accuracy of the <code>double</code> Fresnel integrals. Reference values are Apfloat at 40 digits,
 * baked in rather than recomputed.
 */
public class FresnelIntegralsTest {

  private static final double MAX_ERROR = 1.0e-12;

  // {x, FresnelC(x), FresnelS(x)}
  private static final double[][] REFERENCE = {{0.001, 9.999999999997533E-4, 5.235987755982066E-10},
      {0.25, 0.2497591503565432, 0.008175600235777757},
      {0.5, 0.4923442258714464, 0.06473243285999927}, {1.0, 0.7798934003768229, 0.4382591473903547},
      {1.49, 0.4545865201776368, 0.7011132249982799}, {1.5, 0.4452611760398215, 0.6975049600820931},
      {1.51, 0.43611616803601727, 0.6934614219159762},
      {2.0, 0.48825340607534073, 0.34341567836369824}, {3.0, 0.6057207892976856, 0.496312998967375},
      {5.0, 0.5636311887040122, 0.49919138191711687},
      {10.0, 0.4998986942055157, 0.46816997858488224},
      {25.0, 0.5127323855397702, 0.4999935154694762}, {50.0, 0.499999189430728, 0.4936338025859387},
      {100.0, 0.49999989867881794, 0.4968169011478375},
      {200.0, 0.49999998733485207, 0.4984084505693834},
      {300.0, 0.4999999962473635, 0.49893896704609386},
      {-0.5, -0.4923442258714464, -0.06473243285999927},
      {-1.5, -0.4452611760398215, -0.6975049600820931},
      {-10.0, -0.4998986942055157, -0.46816997858488224},
      {-100.0, -0.49999989867881794, -0.4968169011478375},};

  private static void assertClose(double expected, double actual, String what) {
    double error = Math.abs(expected) < 1.0e-290 ? Math.abs(actual - expected)
        : Math.abs((actual - expected) / expected);
    assertTrue(error < MAX_ERROR,
        what + ": expected " + expected + " but was " + actual + " (relative error " + error + ")");
  }

  @Test
  public void testAgainstReferenceValues() {
    for (double[] row : REFERENCE) {
      assertClose(row[1], FresnelIntegrals.fresnelC(row[0]), "FresnelC(" + row[0] + ")");
      assertClose(row[2], FresnelIntegrals.fresnelS(row[0]), "FresnelS(" + row[0] + ")");
    }
  }

  /** Both are odd, which the series and the continued fraction have to agree on. */
  @Test
  public void testOddSymmetry() {
    for (double x : new double[] {0.001, 0.5, 1.0, 1.5, 2.0, 7.0, 40.0, 250.0}) {
      assertEquals(-FresnelIntegrals.fresnelC(x), FresnelIntegrals.fresnelC(-x), 1.0e-15);
      assertEquals(-FresnelIntegrals.fresnelS(x), FresnelIntegrals.fresnelS(-x), 1.0e-15);
    }
  }

  /**
   * Small-argument behaviour: C(x) -> x and S(x) -> Pi*x^3/6, which pins the normalization - a
   * factor of Pi/2 in the wrong place would show up here and nowhere else.
   */
  @Test
  public void testSmallArgumentLimits() {
    double x = 1.0e-4;
    assertEquals(x, FresnelIntegrals.fresnelC(x), x * 1.0e-8);
    assertEquals(Math.PI * x * x * x / 6.0, FresnelIntegrals.fresnelS(x),
        Math.abs(x * x * x) * 1.0e-8);
  }

  /** Both tend to 1/2 as the argument grows. */
  @Test
  public void testLargeArgumentLimit() {
    assertEquals(0.5, FresnelIntegrals.fresnelC(280.0), 2.0e-3);
    assertEquals(0.5, FresnelIntegrals.fresnelS(280.0), 2.0e-3);
  }

  @Test
  public void testDomainBoundaries() {
    assertTrue(FresnelIntegrals.isSupported(300.0));
    assertFalse(FresnelIntegrals.isSupported(300.1));
    assertFalse(FresnelIntegrals.isSupported(Double.NaN));
  }
}
