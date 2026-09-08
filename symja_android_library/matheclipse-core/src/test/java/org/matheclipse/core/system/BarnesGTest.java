package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.functions.BarnesG;

/**
 * Accuracy of the <code>double</code> Barnes G-function. Reference values are at 25 digits; NaN
 * marks the entries returned as complex (log G on the negative axis) or that overflow a double (G
 * past about 26).
 */
public class BarnesGTest {

  private static final double MAX_ERROR = 1.0e-12;

  // {x, BarnesG(x), LogBarnesG(x)}
  private static final double[][] REFERENCE = {{0.1, 0.10880645561709085, -2.218184611604621},
      {0.5, 0.6032442812094462, -0.5054330544896953}, {1.0, 1.0, 0.0},
      {1.5, 1.069222649266413, 0.06693188843500471}, {2.0, 1.0, 0.0},
      {2.5, 0.9475739010838258, -0.05385034920024052}, {3.0, 1.0, 0.0},
      {4.0, 2.0, 0.6931471805599453}, {5.0, 12.0, 2.4849066497880004},
      {7.5, 733746.3839521464, 13.505918721938054}, {10.0, 5056584744960000.0, 36.15946769873876},
      {20.0, 4.3061925649977154e+120, 277.77026527738514}, {50.0, Double.NaN, 2915.918514771744},
      {100.0, Double.NaN, 15258.061392148826}, {200.0, Double.NaN, 75291.59442008073},
      {-0.5, -0.17017206989656153, Double.NaN}, {-1.5, -0.07200698193480054, Double.NaN},
      {-2.5, 0.07617297965686111, Double.NaN}, {-2.1, 0.001979943556191996, Double.NaN},};

  @Test
  public void testBarnesGAgainstWMA() {
    int compared = 0;
    for (double[] row : REFERENCE) {
      double x = row[0];
      double expected = row[1];
      if (Double.isNaN(expected) || !BarnesG.isSupportedG(x)) {
        continue;
      }
      double actual = BarnesG.barnesG(x);
      compared++;
      double error = Math.abs((actual - expected) / expected);
      assertTrue(error < MAX_ERROR, "BarnesG(" + x + "): expected " + expected + " but was "
          + actual + " (relative error " + error + ")");
    }
    assertTrue(compared >= 14, "only compared " + compared + " points");
  }

  /**
   * <code>log G</code> is judged on an ABSOLUTE scale. It passes through zero at 1, 2 and 3 - those
   * are the three consecutive arguments where <code>G</code> equals 1 - so a relative criterion
   * there would be measuring the distance to a zero rather than the implementation.
   */
  @Test
  public void testLogBarnesGAgainstWMA() {
    int compared = 0;
    for (double[] row : REFERENCE) {
      double x = row[0];
      double expected = row[2];
      if (Double.isNaN(expected) || !BarnesG.isSupportedLogG(x)) {
        continue;
      }
      double actual = BarnesG.logBarnesG(x);
      compared++;
      double error = Math.abs(actual - expected) / Math.max(1.0, Math.abs(expected));
      assertTrue(error < MAX_ERROR, "LogBarnesG(" + x + "): expected " + expected + " but was "
          + actual + " (absolute-scaled error " + error + ")");
    }
    assertTrue(compared >= 14, "only compared " + compared + " points");
  }

  /**
   * At a positive integer <code>G(n) = 1! * 2! * ... * (n-2)!</code>, which is exact and
   * independent of the asymptotic expansion the implementation actually uses.
   */
  @Test
  public void testIntegerValues() {
    for (int n = 1; n <= 12; n++) {
      // G(n) = 1! * 2! * ... * (n-2)!
      double expected = 1.0;
      for (int k = 1; k <= n - 2; k++) {
        expected *= factorial(k);
      }
      assertEquals(expected, BarnesG.barnesG(n), Math.abs(expected) * 1.0e-13, "G(" + n + ")");
    }
  }

  private static double factorial(int k) {
    double result = 1.0;
    for (int i = 2; i <= k; i++) {
      result *= i;
    }
    return result;
  }

  /** The defining recurrence <code>G(z+1) = Gamma(z) G(z)</code>, end to end. */
  @Test
  public void testRecurrence() {
    for (double z : new double[] {0.3, 0.75, 1.2, 2.5, 4.4, 8.0, 11.9, 12.0, 12.1, 17.5}) {
      double expected = org.hipparchus.special.Gamma.gamma(z) * BarnesG.barnesG(z);
      double actual = BarnesG.barnesG(z + 1.0);
      assertEquals(expected, actual, Math.abs(expected) * 1.0e-12, "G(" + (z + 1) + ")");
    }
  }

  /** Exact zeros at every non-positive integer, and the sign pattern between them. */
  @Test
  public void testZerosAndSigns() {
    for (int n = 0; n <= 8; n++) {
      assertEquals(0.0, BarnesG.barnesG(-n), 0.0, "G(" + (-n) + ")");
    }
    // between consecutive zeros G keeps one sign; these come from the reference table
    assertTrue(BarnesG.barnesG(-0.5) < 0.0, "G(-0.5)");
    assertTrue(BarnesG.barnesG(-1.5) < 0.0, "G(-1.5)");
    assertTrue(BarnesG.barnesG(-2.5) > 0.0, "G(-2.5)");
  }

  @Test
  public void testDomainBoundaries() {
    assertTrue(BarnesG.isSupportedLogG(1.0e6));
    assertFalse(BarnesG.isSupportedLogG(1.0e6 + 1.0));
    assertFalse(BarnesG.isSupportedLogG(0.0), "log G is not real at 0");
    assertFalse(BarnesG.isSupportedLogG(-1.5), "nor on the negative axis");
    assertTrue(BarnesG.isSupportedG(26.0));
    assertFalse(BarnesG.isSupportedG(26.1), "G overflows a double past here");
    assertTrue(BarnesG.isSupportedG(-1.5), "but G itself is real and finite below zero");
  }
}
