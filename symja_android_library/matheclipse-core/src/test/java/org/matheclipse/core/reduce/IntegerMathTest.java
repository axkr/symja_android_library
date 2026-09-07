package org.matheclipse.core.reduce;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

/** Unit tests of the exact integer primitives. */
public class IntegerMathTest {

  private static BigInteger n(long value) {
    return BigInteger.valueOf(value);
  }

  @Test
  public void testSignedDivisionRounds() {
    assertEquals(n(2), IntegerMath.floorDiv(n(7), n(3)));
    assertEquals(n(-3), IntegerMath.floorDiv(n(-7), n(3)));
    assertEquals(n(-3), IntegerMath.floorDiv(n(7), n(-3)));
    assertEquals(n(2), IntegerMath.floorDiv(n(-7), n(-3)));
    assertEquals(n(3), IntegerMath.ceilDiv(n(7), n(3)));
    assertEquals(n(-2), IntegerMath.ceilDiv(n(-7), n(3)));
    assertEquals(n(-2), IntegerMath.ceilDiv(n(7), n(-3)));
    assertEquals(n(3), IntegerMath.ceilDiv(n(-7), n(-3)));
    // an exact quotient is not rounded away
    assertEquals(n(-2), IntegerMath.floorDiv(n(-6), n(3)));
    assertEquals(n(-2), IntegerMath.ceilDiv(n(-6), n(3)));
  }

  @Test
  public void testEuclideanModIsNonNegative() {
    assertEquals(n(2), IntegerMath.euclideanMod(n(-7), n(3)));
    assertEquals(n(1), IntegerMath.euclideanMod(n(7), n(3)));
    assertEquals(n(0), IntegerMath.euclideanMod(n(-9), n(3)));
  }

  @Test
  public void testExtendedGcdSatisfiesBezout() {
    long[][] pairs = {{3, 5}, {6, 4}, {-6, 4}, {0, 7}, {7, 0}, {-12, -18}, {1234567, 7654321}};
    for (long[] pair : pairs) {
      BigInteger a = n(pair[0]);
      BigInteger b = n(pair[1]);
      BigInteger[] result = IntegerMath.extendedGcd(a, b);
      assertEquals(a.gcd(b), result[0], "gcd of " + a + " and " + b);
      assertTrue(result[0].signum() >= 0);
      assertEquals(result[0], result[1].multiply(a).add(result[2].multiply(b)),
          "Bezout identity for " + a + " and " + b);
    }
  }

  @Test
  public void testLcm() {
    assertEquals(n(12), IntegerMath.lcm(n(4), n(6)));
    assertEquals(n(12), IntegerMath.lcm(n(-4), n(6)));
    assertEquals(n(0), IntegerMath.lcm(n(0), n(6)));
  }

  @Test
  public void testSolveLinearCongruence() {
    // 6 x + 4 == 0 (mod 10) is x == 1 (mod 5)
    assertArrayEquals(new BigInteger[] {n(1), n(5)},
        IntegerMath.solveLinearCongruence(n(6), n(-4), n(10)));
    // 2 x + 1 == 0 (mod 4) has no solution
    assertNull(IntegerMath.solveLinearCongruence(n(2), n(-1), n(4)));
    // a unit coefficient keeps the modulus
    assertArrayEquals(new BigInteger[] {n(1), n(3)},
        IntegerMath.solveLinearCongruence(n(1), n(1), n(3)));
    // a coefficient coprime to the modulus is inverted
    assertArrayEquals(new BigInteger[] {n(4), n(7)},
        IntegerMath.solveLinearCongruence(n(3), n(5), n(7)));
    for (int a = -6; a <= 6; a++) {
      for (int b = -6; b <= 6; b++) {
        for (int m = 1; m <= 8; m++) {
          BigInteger[] solved =
              IntegerMath.solveLinearCongruence(n(a), n(b), n(m));
          for (int x = 0; x < m; x++) {
            boolean holds =
                IntegerMath.euclideanMod(n((long) a * x - b), n(m)).signum() == 0;
            boolean predicted = solved != null
                && IntegerMath.euclideanMod(n(x).subtract(solved[0]), solved[1]).signum() == 0;
            assertEquals(holds, predicted, a + "*x == " + b + " (mod " + m + "), x = " + x);
          }
        }
      }
    }
  }

  @Test
  public void testCrtPair() {
    assertArrayEquals(new BigInteger[] {n(7), n(15)},
        IntegerMath.crtPair(n(1), n(3), n(2), n(5)));
    // non coprime but consistent: 1 mod 4 and 3 mod 6 meet at 9 mod 12
    assertArrayEquals(new BigInteger[] {n(9), n(12)},
        IntegerMath.crtPair(n(1), n(4), n(3), n(6)));
    // non coprime and inconsistent: one class is odd, the other even
    assertNull(IntegerMath.crtPair(n(1), n(4), n(2), n(6)));
  }

  @Test
  public void testIsSquare() {
    assertTrue(IntegerMath.isSquare(n(0)));
    assertTrue(IntegerMath.isSquare(n(49)));
    assertFalse(IntegerMath.isSquare(n(48)));
    assertFalse(IntegerMath.isSquare(n(-4)));
    BigInteger big = n(9007199254740993L);
    assertTrue(IntegerMath.isSquare(big.multiply(big)));
    assertFalse(IntegerMath.isSquare(big.multiply(big).add(BigInteger.ONE)));
  }
}
