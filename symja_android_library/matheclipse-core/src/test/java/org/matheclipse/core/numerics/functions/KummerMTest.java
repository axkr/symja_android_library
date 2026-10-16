package org.matheclipse.core.numerics.functions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.junit.jupiter.api.Test;

/** The double precision kernel of <code>Hypergeometric1F1(a, b, z)</code>. */
public class KummerMTest {

  private static void assertRelative(double expected, double actual, double tolerance) {
    assertTrue(Math.abs(actual - expected) <= tolerance * Math.abs(expected),
        "expected " + expected + " but was " + actual);
  }

  private static double m(double a, double b, double z) {
    return KummerM.hypergeometric1F1(a, b, z);
  }

  @Test
  public void testClosedForms() {
    // M(a, a, z) == E^z, M(1, 2, z) == (E^z-1)/z
    assertRelative(Math.exp(-3.25), m(2.75, 2.75, -3.25), 1.0e-14);
    assertRelative(Math.expm1(4.5) / 4.5, m(1.0, 2.0, 4.5), 1.0e-14);
    // M(-2, b, z) == 1 - 2*z/b + z^2/(b*(b+1))
    double b = 3.4;
    for (double z : new double[] {0.6, 7.5, -12.0}) {
      assertRelative(1 - 2 * z / b + z * z / (b * (b + 1)), m(-2.0, b, z), 1.0e-13);
    }
    // the series ends before the pole of b == -3: M(-2, -3, z) == 1 + 2*z/3 + z^2/6
    assertRelative(1 + 2 * 2.5 / 3 + 2.5 * 2.5 / 6, m(-2.0, -3.0, 2.5), 1.0e-14);
  }

  @Test
  public void testAlternatingSeries() {
    // the series of a negative a alternates in sign; the transformation M(a, b, z) ==
    // E^z*M(b-a, b, -z) or the error bound decides. Where the terms cancel too much the kernel
    // declines (the caller computes in arbitrary precision); an accepted value is checked to
    // 30 digits
    FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(30);
    double[][] points = {{-2.5, 4.5, 1.5}, {-1.75, 2.25, 3.5}, {-5.7, 2.5, 6.5}, {-8.5, 6.25, 4.0}};
    for (double[] p : points) {
      double value = m(p[0], p[1], p[2]);
      if (Double.isNaN(value)) {
        continue;
      }
      double expected = helper.hypergeometric1F1(new Apfloat(new BigDecimal(p[0]), 30),
          new Apfloat(new BigDecimal(p[1]), 30), new Apfloat(new BigDecimal(p[2]), 30))
          .doubleValue();
      assertRelative(expected, value, TricomiU.TOLERANCE);
    }
    assertTrue(!Double.isNaN(m(-2.5, 4.5, 1.5)) && !Double.isNaN(m(-1.75, 2.25, 3.5)));
  }

  @Test
  public void testAgainstArbitraryPrecision() {
    FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(30);
    java.util.Random random = new java.util.Random(20261016L);
    int accepted = 0;
    for (int i = 0; i < 80; i++) {
      double a = random.nextDouble() * 20.0 - 8.0;
      double b = random.nextDouble() * 16.0 - 3.0;
      double z = (random.nextBoolean() ? 1 : -1) * Math.pow(10.0, random.nextDouble() * 3.5 - 1.5);
      double value = m(a, b, z);
      if (Double.isNaN(value)) {
        continue;
      }
      accepted++;
      double expected = helper.hypergeometric1F1(new Apfloat(new BigDecimal(a), 30),
          new Apfloat(new BigDecimal(b), 30), new Apfloat(new BigDecimal(z), 30)).doubleValue();
      assertRelative(expected, value, TricomiU.TOLERANCE);
    }
    assertTrue(accepted >= 65, "accepted " + accepted);
  }

  @Test
  public void testHypergeometric0F1() {
    // 0F1(; 3/2; z^2/4) == Sinh(z)/z, 0F1(; 1/2; -z^2/4) == Cos(z)
    assertRelative(Math.sinh(2.5) / 2.5, KummerM.hypergeometric0F1(1.5, 2.5 * 2.5 / 4), 1.0e-14);
    assertRelative(Math.cos(0.75), KummerM.hypergeometric0F1(0.5, -0.75 * 0.75 / 4), 1.0e-14);
    // next to a zero of Cos the series cancels: declined, not wrong
    double next = KummerM.hypergeometric0F1(0.5, -(Math.PI / 2 - 1.0e-9) * (Math.PI / 2 - 1.0e-9) / 4);
    assertTrue(Double.isNaN(next) || Math.abs(next - Math.sin(1.0e-9)) <= TricomiU.TOLERANCE * 1.0e-9);
    assertTrue(Double.isNaN(KummerM.hypergeometric0F1(-2.0, 1.5)));
  }

  @Test
  public void testComplexHypergeometric1F1() {
    FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(30);
    double[][] points = {{1.25, 0.5, 2.5, -0.75, 1.5, 2.0}, {-2.25, 0.25, 1.75, 0.5, 3.5, -1.25},
        {0.5, -1.5, 3.25, 0.0, -2.5, 4.0}};
    for (double[] p : points) {
      org.hipparchus.complex.Complex value = KummerM.hypergeometric1F1(
          new org.hipparchus.complex.Complex(p[0], p[1]),
          new org.hipparchus.complex.Complex(p[2], p[3]),
          new org.hipparchus.complex.Complex(p[4], p[5]));
      assertTrue(value != null);
      org.apfloat.Apcomplex expected = helper.hypergeometric1F1(
          new org.apfloat.Apcomplex(new Apfloat(new BigDecimal(p[0]), 30), new Apfloat(new BigDecimal(p[1]), 30)),
          new org.apfloat.Apcomplex(new Apfloat(new BigDecimal(p[2]), 30), new Apfloat(new BigDecimal(p[3]), 30)),
          new org.apfloat.Apcomplex(new Apfloat(new BigDecimal(p[4]), 30), new Apfloat(new BigDecimal(p[5]), 30)));
      double re = expected.real().doubleValue(), im = expected.imag().doubleValue();
      double size = Math.hypot(re, im);
      assertTrue(Math.hypot(value.getReal() - re, value.getImaginary() - im) <= TricomiU.TOLERANCE * size,
          "expected " + re + "+" + im + "*I but was " + value);
    }
  }

  @Test
  public void testDeclined() {
    // the pole of a non-positive integer b
    assertTrue(Double.isNaN(m(1.5, -2.0, 3.0)));
    assertTrue(Double.isNaN(m(-3.0, -2.0, 3.0)));
    assertTrue(Double.isNaN(m(1.5, 2.5, Double.NaN)));
  }
}
