// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: tests ported from
// ortools/util/fp_roundtrip_conv_test.cc to JUnit 5, plus tests of the fixed or exponent
// notation choice.

package org.matheclipse.ortools.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class RoundTripDoubleFormatTest {

  private static void assertRoundTrip(double value) {
    final String s = RoundTripDoubleFormat.toString(value);
    final double back = RoundTripDoubleFormat.parse(s);
    assertEquals(Double.doubleToLongBits(value), Double.doubleToLongBits(back),
        () -> value + " printed as " + s + " parses as " + back);
  }

  @Test
  public void testSomeValues() {
    assertEquals("0", RoundTripDoubleFormat.toString(0.0));
    assertEquals("-0", RoundTripDoubleFormat.toString(-0.0));
    assertEquals("0.1", RoundTripDoubleFormat.toString(0.1));
    assertEquals("10", RoundTripDoubleFormat.toString(10.0));
    assertEquals("0.5", RoundTripDoubleFormat.toString(0.5));
    assertEquals("inf", RoundTripDoubleFormat.toString(Double.POSITIVE_INFINITY));
    assertEquals("-inf", RoundTripDoubleFormat.toString(Double.NEGATIVE_INFINITY));
    assertEquals("nan", RoundTripDoubleFormat.toString(Double.NaN));
  }

  @Test
  public void testFixedOrExponentNotation() {
    // the shorter notation wins, a tie goes to the fixed notation
    assertEquals("100", RoundTripDoubleFormat.toString(100.0));
    assertEquals("123456789.123", RoundTripDoubleFormat.toString(123456789.123));
    assertEquals("-2.25", RoundTripDoubleFormat.toString(-2.25));
    assertEquals("1e+30", RoundTripDoubleFormat.toString(1e30));
    assertEquals("-1e+30", RoundTripDoubleFormat.toString(-1e30));
    assertEquals("1e+16", RoundTripDoubleFormat.toString(1e16));
    assertEquals("1e-04", RoundTripDoubleFormat.toString(1e-4));
    assertEquals("0.001", RoundTripDoubleFormat.toString(1e-3));
    assertEquals("1.5e-07", RoundTripDoubleFormat.toString(1.5e-7));
    assertEquals("1.25e+100", RoundTripDoubleFormat.toString(1.25e100));
    // Double.toString gives 4.9E-324 and 1.0E23: one digit is enough
    assertEquals("5e-324", RoundTripDoubleFormat.toString(Double.MIN_VALUE));
    assertEquals("1e+23", RoundTripDoubleFormat.toString(1e23));
    assertEquals("1.7976931348623157e+308", RoundTripDoubleFormat.toString(Double.MAX_VALUE));
    // more digits than the 6 of the former export format
    assertEquals("0.3333333333333333", RoundTripDoubleFormat.toString(1.0 / 3.0));
    assertEquals("1234567", RoundTripDoubleFormat.toString(1234567.0));
  }

  @Test
  public void testRoundTrip() {
    final double[] values = {0.0, -0.0, 0.1, Math.nextUp(0.1), Math.nextDown(0.1),
        Math.nextUp(-0.1), Math.nextDown(-0.1), Double.MAX_VALUE, -Double.MAX_VALUE,
        Math.nextDown(Double.MAX_VALUE), Math.nextUp(-Double.MAX_VALUE), Double.MIN_NORMAL,
        -Double.MIN_NORMAL, Double.MIN_VALUE, -Double.MIN_VALUE, Math.nextUp(Double.MIN_VALUE),
        -Math.nextUp(Double.MIN_VALUE), Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    for (double value : values) {
      assertRoundTrip(value);
    }
    final String nan = RoundTripDoubleFormat.toString(Double.NaN);
    assertTrue(Double.isNaN(RoundTripDoubleFormat.parse(nan)));
  }

  @Test
  public void testSmallAndBigIntegers() {
    for (int i = 0; i < 1000; ++i) {
      final int signed = i - 500;
      assertRoundTrip(signed);
      final double magnitude = Double.MAX_VALUE / Math.pow(10.0, Math.abs(signed));
      assertRoundTrip(signed < 0 ? -magnitude : magnitude);
    }
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testRandomRoundTrip() {
    final Random random = new Random(20261026L);
    for (int i = 0; i < 2000; ++i) {
      assertRoundTrip(2.0 * random.nextDouble() - 1.0);
      final double bits = Double.longBitsToDouble(random.nextLong());
      if (!Double.isNaN(bits)) {
        assertRoundTrip(bits);
      }
    }
  }

  @Test
  public void testParseBadInputs() {
    assertThrows(NumberFormatException.class, () -> RoundTripDoubleFormat.parse(""));
    assertThrows(NumberFormatException.class, () -> RoundTripDoubleFormat.parse("\"3.0\""));
    assertThrows(NumberFormatException.class, () -> RoundTripDoubleFormat.parse("3.0\n"));
    assertThrows(NumberFormatException.class, () -> RoundTripDoubleFormat.parse("3.0 "));
    assertThrows(NumberFormatException.class, () -> RoundTripDoubleFormat.parse("13.1.2"));
    assertThrows(NumberFormatException.class, () -> RoundTripDoubleFormat.parse("3.0d"));
    assertThrows(NumberFormatException.class, () -> RoundTripDoubleFormat.parse("0x1p3"));
  }
}
