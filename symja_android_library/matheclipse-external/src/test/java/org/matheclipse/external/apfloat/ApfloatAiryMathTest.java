package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatAiryMath}.
 */
public class ApfloatAiryMathTest {

  private static double ai(int k) {
    return ApfloatAiryMath.airyAiZero(k, 20).doubleValue();
  }

  private static double bi(int k) {
    return ApfloatAiryMath.airyBiZero(k, 20).doubleValue();
  }

  @Test
  @Tag("slow")
  public void testAiryAiZero() {
    // https://dlmf.nist.gov/9.9#T1
    assertEquals(-2.33810741, ai(1), 1e-8);
    assertEquals(-4.08794944, ai(2), 1e-8);
    assertEquals(-5.52055983, ai(3), 1e-8);
    assertEquals(-12.82877675, ai(10), 1e-8);
  }

  @Test
  @Tag("slow")
  public void testAiryBiZero() {
    // https://dlmf.nist.gov/9.9#T1
    assertEquals(-1.17371322, bi(1), 1e-8);
    assertEquals(-3.27109330, bi(2), 1e-8);
    assertEquals(-4.83073784, bi(3), 1e-8);
    assertEquals(-12.38641714, bi(10), 1e-8);
  }

  @Tag("slow")
  @Test
  public void testResidual() {
    // the function value at the zero must vanish at the requested precision
    Apfloat tiny = new Apfloat("1e-45");
    for (int k = 1; k <= 4; k += 3) {
      Apfloat a = ApfloatAiryMath.airyAiZero(k, 50);
      assertTrue(ApfloatMath.abs(ApfloatMath.airyAi(a)).compareTo(tiny) < 0, "AiryAiZero " + k);
      Apfloat b = ApfloatAiryMath.airyBiZero(k, 50);
      assertTrue(ApfloatMath.abs(ApfloatMath.airyBi(b)).compareTo(tiny) < 0, "AiryBiZero " + k);
    }
    // consecutive zeros are strictly decreasing, i.e. no zero is skipped or found twice
    double previous = 0.0;
    for (int k = 1; k <= 8; k++) {
      double a = ai(k);
      double b = bi(k);
      // zeros of Ai and Bi interlace: b(k) > a(k) > b(k+1)
      assertTrue(b < previous && a < b, "index " + k);
      previous = a;
    }
  }
}
