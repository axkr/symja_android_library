package org.matheclipse.core.builtin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IRational;

/** The rounding of an exact rational number to the nearest double, ties to even. */
public class RoundToDoubleTest {

  private static IRational ratio(BigInteger numerator, BigInteger denominator) {
    return F.QQ(F.ZZ(numerator), F.ZZ(denominator));
  }

  private static IRational overPowerOfTwo(long numerator, int exponent) {
    return ratio(BigInteger.valueOf(numerator), BigInteger.ONE.shiftLeft(exponent));
  }

  @Test
  public void testNormalRange() {
    assertEquals(1.0, HypergeometricFunctions.roundToDouble(F.C1));
    assertEquals(1.0 / 7.0, HypergeometricFunctions.roundToDouble(F.QQ(1, 7)));
    assertEquals(-5.0 / 9.0, HypergeometricFunctions.roundToDouble(F.QQ(-5, 9)));
    assertEquals(0.0, HypergeometricFunctions.roundToDouble(F.C0));
  }

  @Test
  public void testSubnormalRange() {
    // 1.5 units of the smallest subnormal: the tie goes to the even 2 units
    assertEquals(2 * Double.MIN_VALUE,
        HypergeometricFunctions.roundToDouble(overPowerOfTwo(3, 1075)));
    // 2.5 units: the tie goes to the even 2 units
    assertEquals(2 * Double.MIN_VALUE,
        HypergeometricFunctions.roundToDouble(overPowerOfTwo(5, 1075)));
    // half a unit: the tie goes to 0
    assertEquals(0.0, HypergeometricFunctions.roundToDouble(overPowerOfTwo(1, 1075)));
    // just below 1.5 units is 1 unit; a rounding to 53 bits first would make it the tie 1.5
    // and then 2 units
    long justBelow = 3L * (1L << 59) - 1;
    assertEquals(Double.MIN_VALUE,
        HypergeometricFunctions.roundToDouble(overPowerOfTwo(justBelow, 1134)));
    // just above 1.5 units is 2 units
    assertEquals(2 * Double.MIN_VALUE,
        HypergeometricFunctions.roundToDouble(overPowerOfTwo(3L * (1L << 59) + 1, 1134)));
    // a third of the smallest normal number
    assertEquals(Double.MIN_NORMAL / 3, HypergeometricFunctions.roundToDouble(
        ratio(BigInteger.ONE, BigInteger.valueOf(3).shiftLeft(1022))));
  }
}
