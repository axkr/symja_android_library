package org.matheclipse.core.numerics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.functions.AiryZeros;
import org.matheclipse.core.system.TestTags;

public class AiryZerosTest {

  @Test
  public void testAiryAiZero() {
    // https://dlmf.nist.gov/9.9#T1
    assertEquals(-2.33810741, AiryZeros.airyAiZero(1), 1e-8);
    assertEquals(-4.08794944, AiryZeros.airyAiZero(2), 1e-8);
    assertEquals(-5.52055983, AiryZeros.airyAiZero(3), 1e-8);
    assertEquals(-12.82877675, AiryZeros.airyAiZero(10), 1e-8);
  }

  @Test
  public void testAiryBiZero() {
    // https://dlmf.nist.gov/9.9#T1
    assertEquals(-1.17371322, AiryZeros.airyBiZero(1), 1e-8);
    assertEquals(-3.27109330, AiryZeros.airyBiZero(2), 1e-8);
    assertEquals(-4.83073784, AiryZeros.airyBiZero(3), 1e-8);
    assertEquals(-12.38641714, AiryZeros.airyBiZero(10), 1e-8);
  }

  @Tag(TestTags.SLOW)
  @Test
  public void testResidual() {
    // the function value at the zero must vanish at the requested precision
    Apfloat tiny = new Apfloat("1e-45");
    for (int k = 1; k <= 4; k += 3) {
      Apfloat a = AiryZeros.airyAiZero(k, 50);
      assertTrue(ApfloatMath.abs(ApfloatMath.airyAi(a)).compareTo(tiny) < 0, "AiryAiZero " + k);
      Apfloat b = AiryZeros.airyBiZero(k, 50);
      assertTrue(ApfloatMath.abs(ApfloatMath.airyBi(b)).compareTo(tiny) < 0, "AiryBiZero " + k);
    }
    // consecutive zeros are strictly decreasing, i.e. no zero is skipped or found twice
    double previous = 0.0;
    for (int k = 1; k <= 8; k++) {
      double a = AiryZeros.airyAiZero(k);
      double b = AiryZeros.airyBiZero(k);
      // zeros of Ai and Bi interlace: b(k) > a(k) > b(k+1)
      assertTrue(b < previous && a < b, "index " + k);
      previous = a;
    }
  }
}
