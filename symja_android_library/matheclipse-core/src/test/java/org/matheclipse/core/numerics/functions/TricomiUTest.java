package org.matheclipse.core.numerics.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.junit.jupiter.api.Test;

/** The double precision kernel of <code>HypergeometricU(a, b, z)</code>. */
public class TricomiUTest {

  private static void assertRelative(double expected, double actual, double tolerance) {
    assertTrue(Math.abs(actual - expected) <= tolerance * Math.abs(expected),
        "expected " + expected + " but was " + actual);
  }

  private static double u(double a, double b, double z) {
    return TricomiU.hypergeometricU(a, b, z);
  }

  @Test
  public void testClosedForms() {
    // U(a, a+1, z) == z^(-a)
    assertRelative(Math.pow(2.75, -1.35), u(1.35, 2.35, 2.75), 1.0e-14);
    // U(1, 1, z) == E^z*E1(z), at the small, the middle and the large argument
    for (double z : new double[] {0.3, 3.7, 41.0}) {
      assertRelative(Math.exp(z) * ExponentialIntegrals.expIntegralE1(z), u(1.0, 1.0, z), 1.0e-13);
    }
    // U(-2, b, z) == b*(b+1) - 2*(b+1)*z + z^2
    double b = 0.65;
    for (double z : new double[] {0.2, 1.9, 13.0}) {
      assertRelative(b * (b + 1) - 2 * (b + 1) * z + z * z, u(-2.0, b, z), 1.0e-13);
    }
    assertEquals(1.0, u(0.0, 3.3, 2.2), 0.0);
  }

  @Test
  public void testIdentities() {
    // Kummer's transformation U(a, b, z) == z^(1-b)*U(a-b+1, 2-b, z), each side by its own route
    double[][] points = {{0.45, 2.3, 0.6}, {2.6, -1.7, 5.5}, {-0.3, 3.0, 4.4}, {4.2, 1.0, 9.0}};
    for (double[] p : points) {
      double a = p[0], b = p[1], z = p[2];
      assertRelative(u(a, b, z), Math.pow(z, 1 - b) * u(a - b + 1, 2 - b, z), 1.0e-12);
    }
    // the recurrence U(a-1) + (b-2a-z)*U(a) + a*(a-b+1)*U(a+1) == 0
    double a = 1.8, b = 0.7, z = 6.3;
    double lhs = u(a - 1, b, z);
    double rhs = -(b - 2 * a - z) * u(a, b, z) - a * (a - b + 1) * u(a + 1, b, z);
    assertRelative(lhs, rhs, 1.0e-12);
  }

  @Test
  public void testAgainstArbitraryPrecision() {
    FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(30);
    java.util.Random random = new java.util.Random(20261016L);
    int accepted = 0;
    for (int i = 0; i < 60; i++) {
      double a = random.nextDouble() * 12.0 - 3.0;
      double b = random.nextDouble() * 12.0 - 3.0;
      if (i % 4 == 1) {
        // the logarithmic case of an integer b
        b = Math.rint(b);
      }
      double z = Math.pow(10.0, random.nextDouble() * 3.5 - 1.5);
      double value = u(a, b, z);
      if (Double.isNaN(value)) {
        continue;
      }
      accepted++;
      double expected;
      try {
        expected = helper.hypergeometricU(new Apfloat(new BigDecimal(a), 30),
            new Apfloat(new BigDecimal(b), 30), new Apfloat(new BigDecimal(z), 30)).doubleValue();
      } catch (ArithmeticException ae) {
        // the arbitrary precision routine divides by zero for some integer b
        continue;
      }
      assertRelative(expected, value, TricomiU.TOLERANCE);
    }
    // the kernel declines only where the arbitrary precision routine is needed
    assertTrue(accepted >= 55, "accepted " + accepted);
  }

  @Test
  public void testDeclined() {
    assertTrue(Double.isNaN(u(1.5, 2.5, -3.0)));
    assertTrue(Double.isNaN(u(1.5, 2.5, 0.0)));
    assertTrue(Double.isNaN(u(Double.NaN, 2.5, 1.0)));
  }
}
