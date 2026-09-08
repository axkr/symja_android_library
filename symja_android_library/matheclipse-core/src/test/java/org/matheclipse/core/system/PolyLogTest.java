package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.functions.PolyLog;

/**
 * Accuracy of the <code>double</code> polylogarithm.
 *
 * <p>
 * The reference values here come from WMA at 25 digits.
 */
public class PolyLogTest {

  private static final double MAX_ERROR = 1.0e-12;

  // {n, z, PolyLog[n,z]}; NaN where WMA returned a complex value
  private static final double[][] REFERENCE = {{-2.0, -0.99, -0.0012562496835583183},
      {-2.0, -0.9, -0.013121446274967197}, {-2.0, -0.5, -0.07407407407407407},
      {-2.0, -0.1, -0.067618332081142}, {-2.0, -0.001, -0.000996008984024964},
      {-2.0, 0.001, 0.001004009016025036}, {-2.0, 0.1, 0.15089163237311384}, {-2.0, 0.5, 6.0},
      {-2.0, 0.9, 1710.0}, {-2.0, 0.99, 1970100.0}, {-1.0, -0.99, -0.24999368702810534},
      {-1.0, -0.9, -0.24930747922437674}, {-1.0, -0.5, -0.2222222222222222},
      {-1.0, -0.1, -0.08264462809917356}, {-1.0, -0.001, -0.000998002996004994},
      {-1.0, 0.001, 0.001002003004005006}, {-1.0, 0.1, 0.12345679012345678}, {-1.0, 0.5, 2.0},
      {-1.0, 0.9, 90.0}, {-1.0, 0.99, 9900.0}, {0.0, -0.99, -0.49748743718592964},
      {0.0, -0.9, -0.47368421052631576}, {0.0, -0.5, -0.3333333333333333},
      {0.0, -0.1, -0.09090909090909091}, {0.0, -0.001, -0.000999000999000999},
      {0.0, 0.001, 0.001001001001001001}, {0.0, 0.1, 0.1111111111111111}, {0.0, 0.5, 1.0},
      {0.0, 0.9, 9.0}, {0.0, 0.99, 99.0}, {1.0, -0.99, -0.688134638736401},
      {1.0, -0.9, -0.6418538861723948}, {1.0, -0.5, -0.4054651081081644},
      {1.0, -0.1, -0.09531017980432487}, {1.0, -0.001, -0.0009995003330835331},
      {1.0, 0.001, 0.0010005003335835335}, {1.0, 0.1, 0.1053605156578263},
      {1.0, 0.5, 0.6931471805599453}, {1.0, 0.9, 2.302585092994046}, {1.0, 0.99, 4.605170185988092},
      {2.0, -0.99, -0.8155258814773397}, {2.0, -0.9, -0.7521631792172616},
      {2.0, -0.5, -0.4484142069236462}, {2.0, -0.1, -0.09760523522932159},
      {2.0, -0.001, -0.0009997501110486512}, {2.0, 0.001, 0.001000250111173651},
      {2.0, 0.1, 0.10261779109939113}, {2.0, 0.5, 0.5822405264650125},
      {2.0, 0.9, 1.2997147230049588}, {2.0, 0.99, 1.5886254480763753},
      {3.0, -0.99, -0.8933115301022457}, {3.0, -0.9, -0.8186382015443638},
      {3.0, -0.5, -0.47259784465889687}, {3.0, -0.1, -0.09878555018070007},
      {3.0, -0.001, -0.0009998750370214201}, {3.0, 0.001, 0.00100012503705267},
      {3.0, 0.1, 0.10128868447922298}, {3.0, 0.5, 0.5372131936080402}, {3.0, 0.9, 1.04965895018644},
      {3.0, 0.99, 1.1858329336450368}, {5.0, -0.99, -0.9626471656573472},
      {5.0, -0.9, -0.8771870233926207}, {5.0, -0.5, -0.49264922998110783},
      {5.0, -0.1, -0.09969152064714734}, {5.0, -0.001, -0.0009999687541142502},
      {5.0, 0.001, 0.0010000312541162033}, {5.0, 0.1, 0.10031671621746135},
      {5.0, 0.5, 0.5084005792422687}, {5.0, 0.9, 0.9292671964460057},
      {5.0, 0.99, 1.026110477101306}, {0.5, -0.99, -0.6010844711497179},
      {0.5, -0.9, -0.5655259484586528}, {0.5, -0.5, -0.37375223798097307},
      {0.5, -0.1, -0.09346038091036438}, {0.5, -0.001, -0.0009992934700695294},
      {0.5, 0.001, 0.0010007076846319035}, {0.5, 0.1, 0.10770334016557237},
      {0.5, 0.5, 0.8061267230428523}, {0.5, 0.9, 4.021950427473361}, {0.5, 0.99, 16.22183075342811},
      {3.5, -0.99, -0.9188764689160662}, {3.5, -0.9, -0.8403158617197696},
      {3.5, -0.5, -0.480176397992635}, {3.5, -0.1, -0.09913675260758552},
      {3.5, -0.001, -0.000999911633027886}, {3.5, 0.001, 0.0010000884097388077},
      {3.5, 0.1, 0.1009060858545029}, {3.5, 0.5, 0.5254123061416474}, {3.5, 0.9, 0.996771274795817},
      {3.5, 0.99, 1.1133740813808741}, {-2.5, -0.99, 0.08686894060141709},
      {-2.5, -0.9, 0.07701109773747435}, {-2.5, -0.5, 0.0045360934276796085},
      {-2.5, -0.1, -0.05630211907078386}, {-2.5, -0.001, -0.0009943587022635894},
      {-2.5, 0.001, 0.0010056724747627506}, {-2.5, 0.1, 0.1760192519669209},
      {-2.5, 0.5, 11.991219584288189}, {-2.5, 0.9, 8753.948769678005},
      {-2.5, 0.99, 32654587.08032041},};

  @Test
  public void testAgainstWMA() {
    int compared = 0;
    for (double[] row : REFERENCE) {
      double n = row[0];
      double z = row[1];
      double expected = row[2];
      if (Double.isNaN(expected) || !PolyLog.isSupported(n, z)) {
        continue;
      }
      double actual = PolyLog.polyLog(n, z);
      if (Double.isNaN(actual)) {
        continue; // withdrawn on cancellation - the caller falls back to arbitrary precision
      }
      compared++;
      double error = Math.abs(expected) < 1.0e-290 ? Math.abs(actual - expected)
          : Math.abs((actual - expected) / expected);
      assertTrue(error < MAX_ERROR, "PolyLog(" + n + ", " + z + "): expected " + expected
          + " but was " + actual + " (relative error " + error + ")");
    }
    assertTrue(compared > 90, "expected a broad comparison, only did " + compared);
  }

  /**
   * The closed forms for order 1, 0 and the negative integers, checked independently of any
   * reference. These are what make the negative orders usable at all - the defining series would
   * have to sum thousands of large terms to reach the same value near <code>z = 1</code>.
   */
  @Test
  public void testClosedForms() {
    for (double z : new double[] {-0.9, -0.5, -0.1, 0.1, 0.5, 0.9, 0.99}) {
      assertEquals(-Math.log1p(-z), PolyLog.polyLog(1.0, z), Math.abs(Math.log1p(-z)) * 1.0e-15,
          "Li_1(" + z + ")");
      assertEquals(z / (1.0 - z), PolyLog.polyLog(0.0, z), Math.abs(z / (1.0 - z)) * 1.0e-15,
          "Li_0(" + z + ")");
      double li1 = z / ((1.0 - z) * (1.0 - z));
      assertEquals(li1, PolyLog.polyLog(-1.0, z), Math.abs(li1) * 1.0e-14, "Li_-1(" + z + ")");
      double li2 = z * (1.0 + z) / Math.pow(1.0 - z, 3);
      assertEquals(li2, PolyLog.polyLog(-2.0, z), Math.abs(li2) * 1.0e-14, "Li_-2(" + z + ")");
    }
  }

  /**
   * Two classical closed forms at <code>z = 1/2</code>:
   * <code>Li_2(1/2) = pi^2/12 - ln(2)^2/2</code> and
   * <code>Li_3(1/2) = 7*zeta(3)/8 - pi^2*ln(2)/12 + ln(2)^3/6</code>. Neither involves the series
   * this class sums, so they check the result rather than restate it.
   */
  @Test
  public void testClassicalValuesAtHalf() {
    double ln2 = Math.log(2.0);
    double expected2 = Math.PI * Math.PI / 12.0 - ln2 * ln2 / 2.0;
    assertEquals(expected2, PolyLog.polyLog(2.0, 0.5), Math.abs(expected2) * 1.0e-14, "Li_2(1/2)");

    double zeta3 = 1.2020569031595942854;
    double expected3 = 7.0 * zeta3 / 8.0 - Math.PI * Math.PI * ln2 / 12.0 + ln2 * ln2 * ln2 / 6.0;
    assertEquals(expected3, PolyLog.polyLog(3.0, 0.5), Math.abs(expected3) * 1.0e-14, "Li_3(1/2)");
  }

  /**
   * A negative argument at a negative non-integer order makes the series alternate with terms that
   * grow before they decay - at <code>n = -7.25, z = -0.999</code> they peak near 1e28 while the
   * answer is 1.23. Those points must be withdrawn, not guessed at.
   */
  @Test
  public void testCancellationIsWithdrawn() {
    assertTrue(Double.isNaN(PolyLog.polyLog(-7.25, -0.999)), "n=-7.25 z=-0.999");
    assertTrue(Double.isNaN(PolyLog.polyLog(-2.5, -0.99)), "n=-2.5 z=-0.99");
    // the same orders on the positive side are perfectly well conditioned
    assertFalse(Double.isNaN(PolyLog.polyLog(-7.25, 0.9)), "n=-7.25 z=0.9");
    assertFalse(Double.isNaN(PolyLog.polyLog(-2.5, 0.99)), "n=-2.5 z=0.99");
  }

  @Test
  public void testDomainBoundaries() {
    assertTrue(PolyLog.isSupported(2.0, 0.999));
    assertFalse(PolyLog.isSupported(2.0, 0.9991), "beyond the series range");
    assertTrue(PolyLog.isSupported(-2.0, 5.0), "a negative integer order is rational everywhere");
    assertFalse(PolyLog.isSupported(-2.0, 1.0), "except at the pole");
    assertFalse(PolyLog.isSupported(Double.NaN, 0.5));
    assertEquals(0.0, PolyLog.polyLog(3.7, 0.0), 0.0);
  }
}
