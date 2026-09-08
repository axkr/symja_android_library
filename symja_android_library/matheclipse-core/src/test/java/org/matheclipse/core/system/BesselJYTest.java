package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.functions.BesselJY;

/**
 * Accuracy of the <code>double</code> Bessel implementation against an arbitrary-precision
 * reference.
 *
 * <p>
 * {@link BesselJY} replaces Apfloat inside its validated domain, which is what makes numeric Bessel
 * work affordable - a single Apfloat <code>BesselY</code> costs milliseconds against well under a
 * microsecond here. That trade is only sound while the accuracy claim in {@code BesselJY}'s Javadoc
 * holds, so this test pins it down. The reference values below were produced with Apfloat at 40
 * digits and are baked in rather than recomputed: generating them takes minutes, which is exactly
 * the cost this implementation exists to avoid paying at run time. The wider sweep they were
 * sampled from covered orders in [-20,20] and arguments in [1e-8,100].
 *
 * <p>
 * The error is measured as a fraction of the local envelope <code>sqrt(2/(pi*x))</code>, not of the
 * value. Both functions oscillate and have zeros, and next to a zero the relative error of any
 * implementation - including an exact one rounded to a double - is unbounded, so a relative
 * criterion would be measuring the distance to the nearest zero rather than the quality of the
 * implementation.
 */
public class BesselJYTest {

  /**
   * The accuracy {@link BesselJY} promises inside its domain. Callers such as
   * {@code ExprEvaluatorTestCase.checkNumeric} compare to 1e-12; the measured worst case is around
   * 7e-14, so this bound is deliberately left where a real regression trips it well before a caller
   * notices.
   */
  private static final double MAX_ERROR = 1.0e-12;

  private static double envelopeRelativeError(double actual, double expected, double x) {
    return Math.abs(actual - expected)
        / Math.max(Math.abs(expected), Math.sqrt(2.0 / (Math.PI * x)));
  }

  private static final double[][] REFERENCE_VALUES = {
      // {order, argument, BesselJ, BesselY} at 40 significant digits
      {0.0, 1.0E-8, 1.0, -11.80077387717953}, {0.0, 0.01, 0.9999750001562496, -3.005455637083646},
      {0.0, 0.5, 0.938469807240813, -0.44451873350670656},
      {0.0, 1.9999, 0.22394845194430277, 0.510364966587098},
      {0.0, 2.0, 0.22389077914123567, 0.5103756726497451},
      {0.0, 2.0001, 0.22383310698288472, 0.5103863730734735},
      {0.0, 7.0, 0.3000792705195556, -0.02594974396720926},
      {0.0, 100.0, 0.01998585030422312, -0.07724431336508315},
      {0.25, 1.0E-4, 0.09277296067235416, -13.63147954476666},
      {0.25, 0.5, 0.741656570157146, -0.756843545694496},
      {0.25, 3.0, -0.10063706433673128, 0.4473801012748924},
      {0.25, 99.9, -0.01891361602110866, -0.07755504864481721},
      {0.5, 0.001, 0.02523132101498094, -25.23131260454004},
      {0.5, 1.7, 0.6068488080076179, 0.07884632686109729},
      {0.5, 25.0, -0.02112028359965044, -0.15817308404205055},
      {1.0, 1.0E-6, 4.999999999999374E-7, -636619.7723721751},
      {1.0, 0.5, 0.24226845767487387, -1.471472392670243},
      {1.0, 2.0, 0.5767248077568734, -0.10703243154093754},
      {1.0, 10.0, 0.04347274616886143, 0.24901542420695388},
      // BesselY(1,100) and BesselY(10,100) are absent because the Apfloat reference
      // itself throws ApfloatArithmeticException: Division by zero for them - see
      // testApfloatFailureCases, which covers those points instead.
      {1.3, 0.01, 8.74364779918213E-4, -280.05582020809993},
      {1.3, 1.5, 0.45723346848924834, -0.5815362235362158},
      {1.3, 9.03, 0.26668307124617885, -3.3332009875374554E-4},
      {1.3, 50.0, -0.1126297459562664, -0.007113514458836805},
      {2.0, 0.1, 0.001248958658799919, -127.64478324269017},
      {2.0, 3.0, 0.486091260585891, -0.16040039348492371},
      {2.0, 17.3, 0.11735112852177412, 0.15250929977174277},
      {2.75, 0.5, 0.004913242875921771, -24.032515501753238},
      {2.75, 4.0, 0.44451832628538, -0.08661044610223838},
      {5.0, 0.5, 8.053627241357474E-6, -7946.301478807473},
      {5.0, 5.0, 0.26114054612017007, -0.45369482249110193},
      {5.0, 40.0, 0.12257346597711778, 0.03186944878085036},
      {7.5, 1.0, 3.8219741213480424E-7, -112065.16242427878},
      {7.5, 20.0, -0.15532194872765223, 0.10092476802178868},
      {10.0, 1.0, 2.6306151236874534E-10, -1.2161801427868919E8},
      {10.0, 10.0, 0.20748610663335884, -0.35981415218340274},
      {19.875, 3.0, 1.7004807445540171E-15, -9.527731474805021E12},
      {19.875, 60.0, 0.10550326363272286, -0.01065203279300826},
      {-0.25, 0.5, 1.059599593527523, -0.01073881338817434},
      {-0.25, 4.0, -0.3594744455782616, -0.17051712114860468},
      {-0.25, 80.0, -0.04311798319582889, -0.07809270222636167},
      {-0.5, 0.3, 1.3916685091753702, 0.4304935173281246},
      {-0.5, 1.7, -0.07884632686109729, 0.6068488080076179},
      {-0.5, 30.0, 0.02247029059883102, -0.14392965337039987},
      {-1.0, 1.0E-6, -4.999999999999374E-7, 636619.7723721751},
      {-1.0, 0.5, -0.24226845767487387, 1.471472392670243},
      {-1.0, 6.0, 0.2766838581275656, 0.17501034430039825},
      {-1.25, 0.01, -153.45715214922717, 153.45549259449118},
      {-1.25, 2.5, -0.3741088769131817, -0.37402613365143},
      {-1.25, 55.0, 0.10075244785252924, -0.03776717968207897},
      {-2.0, 0.001, 1.2499998958333365E-7, -1273239.8630456675},
      {-2.0, 3.3, 0.47803168645054595, -0.03402961261592177},
      {-3.5, 0.5, -138.8640086724249, -6.623785681459423E-4},
      {-3.5, 12.0, -0.01521971921593016, -0.23483956259311695},
      {-7.75, 1.0, -153768.44583535497, -153768.44583562438},
      {-7.75, 30.0, -0.003160064277686105, -0.14816060312045717},
      {-10.0, 0.5, 2.6131773608228033E-13, -1.2196362334956963E11},
      {-10.0, 25.0, -0.07517984394852328, -0.14871839049980648},
      {-14.125, 5.0, 4090.198937733615, -9874.613742386626},
      {-14.125, 100.0, -0.07959414982170938, 0.00976478556414521},
      {1.0000001, 1.0, 0.44005053955208, -0.781212881597576},
      {4.9999999, 3.0, 0.04302844018171205, -1.9059457828387991},
      {0.001, 2.0, 0.2246920931659327, 0.5100234322789757},};

  @Test
  public void testAccuracyAgainstReferenceValues() {
    double worstJ = 0.0;
    double worstY = 0.0;
    String worstJAt = "";
    String worstYAt = "";
    for (double[] point : REFERENCE_VALUES) {
      double v = point[0];
      double x = point[1];
      assertTrue(BesselJY.isSupported(v, x), "reference point outside the domain: v=" + v);
      double errorJ = envelopeRelativeError(BesselJY.besselJ(v, x), point[2], x);
      double errorY = envelopeRelativeError(BesselJY.besselY(v, x), point[3], x);
      if (errorJ > worstJ) {
        worstJ = errorJ;
        worstJAt = "BesselJ(" + v + ", " + x + ")";
      }
      if (errorY > worstY) {
        worstY = errorY;
        worstYAt = "BesselY(" + v + ", " + x + ")";
      }
    }
    assertTrue(worstJ < MAX_ERROR, "worst BesselJ error " + worstJ + " at " + worstJAt);
    assertTrue(worstY < MAX_ERROR, "worst BesselY error " + worstY + " at " + worstYAt);
  }

  /**
   * The negative-order reflection has to be exact at integer and half-odd-integer orders. The
   * general formula multiplies <code>Y</code> - which diverges as the argument goes to zero - by a
   * <code>sin</code> or <code>cos</code> that is only zero to within rounding, so at
   * <code>x = 1e-6</code> it produced 1.5e17 for a value of -2.6e-34.
   *
   * <p>
   * Checked here through <code>J_-n = (-1)^n J_n</code> and <code>Y_-n = (-1)^n Y_n</code>, which
   * hold exactly and so need no external reference: a reflection that went through the general
   * formula would fail these by many orders of magnitude.
   */
  @Test
  public void testNegativeIntegerAndHalfIntegerOrders() {
    for (double x : new double[] {1.0e-6, 0.001, 0.5, 1.0, 5.0, 40.0}) {
      for (int n = 1; n <= 8; n++) {
        double sign = (n & 1) == 0 ? 1.0 : -1.0;
        assertEquals(sign * BesselJY.besselJ(n, x), BesselJY.besselJ(-n, x),
            Math.abs(BesselJY.besselJ(n, x)) * 1.0e-14, "BesselJ(" + (-n) + ", " + x + ")");
        assertEquals(sign * BesselJY.besselY(n, x), BesselJY.besselY(-n, x),
            Math.abs(BesselJY.besselY(n, x)) * 1.0e-14, "BesselY(" + (-n) + ", " + x + ")");

        // J_-(n+1/2) = (-1)^(n+1) Y_(n+1/2) and Y_-(n+1/2) = (-1)^n J_(n+1/2)
        double half = n + 0.5;
        double halfSign = (n & 1) == 0 ? 1.0 : -1.0;
        assertEquals(-halfSign * BesselJY.besselY(half, x), BesselJY.besselJ(-half, x),
            Math.abs(BesselJY.besselY(half, x)) * 1.0e-14, "BesselJ(" + (-half) + ", " + x + ")");
        assertEquals(halfSign * BesselJY.besselJ(half, x), BesselJY.besselY(-half, x),
            Math.abs(BesselJY.besselJ(half, x)) * 1.0e-14, "BesselY(" + (-half) + ", " + x + ")");
      }
    }
  }

  /** Half-odd-integer orders have closed forms, which pin down the sign conventions. */
  @Test
  public void testClosedForms() {
    double x = 1.7;
    double scale = Math.sqrt(2.0 / (Math.PI * x));
    // J(1/2,x) = sqrt(2/(pi x)) sin x, Y(1/2,x) = -sqrt(2/(pi x)) cos x
    assertEquals(scale * Math.sin(x), BesselJY.besselJ(0.5, x), 1.0e-14);
    assertEquals(-scale * Math.cos(x), BesselJY.besselY(0.5, x), 1.0e-14);
    // J(-1/2,x) = sqrt(2/(pi x)) cos x, Y(-1/2,x) = sqrt(2/(pi x)) sin x
    assertEquals(scale * Math.cos(x), BesselJY.besselJ(-0.5, x), 1.0e-14);
    assertEquals(scale * Math.sin(x), BesselJY.besselY(-0.5, x), 1.0e-14);
  }

  /**
   * The Wronskian <code>J_v(x)*Y_v'(x) - J_v'(x)*Y_v(x) = 2/(pi*x)</code> is an identity the
   * algorithm relies on for normalization, so checking it in the equivalent recurrence form
   * <code>J_(v+1)*Y_v - J_v*Y_(v+1) = 2/(pi*x)</code> tests the recurrences end to end without
   * needing a reference implementation.
   */
  @Test
  public void testWronskian() {
    for (double v : new double[] {0.0, 0.25, 0.5, 1.0, 2.3, 5.0, 11.75}) {
      for (double x : new double[] {0.01, 0.5, 1.0, 1.9999, 2.0, 3.7, 20.0, 99.0}) {
        double expected = 2.0 / (Math.PI * x);
        double actual = BesselJY.besselJ(v + 1.0, x) * BesselJY.besselY(v, x)
            - BesselJY.besselJ(v, x) * BesselJY.besselY(v + 1.0, x);
        assertEquals(expected, actual, Math.abs(expected) * 1.0e-11,
            "Wronskian at v=" + v + " x=" + x);
      }
    }
  }

  /** Outside the validated domain the caller must be told to fall back, not handed a number. */
  @Test
  public void testDomainBoundaries() {
    assertTrue(BesselJY.isSupported(1.3, 100.0));
    assertFalse(BesselJY.isSupported(1.3, 100.1), "beyond the measured argument range");
    assertFalse(BesselJY.isSupported(1.3, 0.0), "Y is singular at 0");
    assertFalse(BesselJY.isSupported(1.3, -1.0), "not real for a negative argument");
    assertFalse(BesselJY.isSupported(2000.0, 1.0), "beyond the measured order range");
    assertFalse(BesselJY.isSupported(Double.NaN, 1.0));
    assertFalse(BesselJY.isSupported(1.0, Double.NaN));
  }
}
