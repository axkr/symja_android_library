package org.matheclipse.core.sympy.ntheory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.system.TestTags;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestFactor extends ExprEvaluatorTestCase {

  @Test
  public void testTrailingBitcount() {
    assertEquals(Factor.trailing(F.C0), //
        0);
    assertEquals(Factor.trailing(F.C1), //
        0);
    assertEquals(Factor.trailing(F.CN1), //
        0);
    assertEquals(Factor.trailing(F.C2), //
        1);
    assertEquals(Factor.trailing(F.C7), //
        0);
    assertEquals(Factor.trailing(F.CN7), //
        0);

    assertEquals(Factor.trailing(F.ZZ(128)), //
        7);
    assertEquals(Factor.trailing(F.ZZ(63)), //
        0);

    BigInteger b = BigInteger.ONE;
    b = b.shiftLeft(100001);
    assertEquals(Factor.trailing(F.ZZ(b)), //
        100001);
    // for i in range(100):
    // assert trailing(1 << i) == i
    // assert trailing((1 << i) * 31337) == i
    // assert trailing(1 << 1000001) == 1000001
    // assert trailing((1 << 273956)*7**37) == 273956
    // # issue 12709
    // big = small_trailing[-1]*2
    // assert trailing(-big) == trailing(big)
    // assert bitcount(-big) == bitcount(big)
  }

  @Test
  public void testDivisors() {
    assertEquals(Factor.divisors(F.CN1).toString(), //
        "{1}");
    assertEquals(Factor.divisors(F.C0).toString(), //
        "{}");
    assertEquals(Factor.divisors(F.C1).toString(), //
        "{1}");
    assertEquals(Factor.divisors(F.C2).toString(), //
        "{1,2}");
    assertEquals(Factor.divisors(F.C3).toString(), //
        "{1,3}");
    assertEquals(Factor.divisors(F.ZZ(17)).toString(), //
        "{1,17}");
    assertEquals(Factor.divisors(F.C10).toString(), //
        "{1,2,5,10}");
    assertEquals(Factor.divisors(F.C100).toString(), //
        "{1,2,4,5,10,20,25,50,100}");
    assertEquals(Factor.divisors(F.ZZ(101)).toString(), //
        "{1,101}");

    assertEquals(Factor.divisorCount(F.C0).toString(), //
        "0");
    assertEquals(Factor.divisorCount(F.CN1).toString(), //
        "1");
    assertEquals(Factor.divisorCount(F.C1).toString(), //
        "1");
    assertEquals(Factor.divisorCount(F.ZZ(6)).toString(), //
        "4");
    assertEquals(Factor.divisorCount(F.ZZ(12)).toString(), //
        "6");

    // assert divisor_count(180, 3) == divisor_count(180//3)
    assertEquals(Factor.divisorCount(F.ZZ(180), 3, false).toString(), //
        Factor.divisorCount(F.ZZ(60)).toString());
    // assert divisor_count(2*3*5, 7) == 0
    assertEquals(Factor.divisorCount(F.ZZ(30), 7, false).toString(), //
        "0");
    // assert divisor_count(6, 2) == 2
    assertEquals(Factor.divisorCount(F.ZZ(6), 2, false).toString(), //
        "2");
    // assert divisor_count(6, proper=True) == 3
    assertEquals(Factor.divisorCount(F.ZZ(6), 1, true).toString(), //
        "3");
  }

  @Test
  public void testPrimeFactors() {
    // assert primefactors(6) == [2, 3]
    assertEquals(Factor.primeFactors(6).toString(), //
        "{2,3}");
    // assert primefactors(-5) == [5]
    assertEquals(Factor.primeFactors(-5).toString(), //
        "{5}");
    // assert primefactors(123456) == [2, 3, 643]
    assertEquals(Factor.primeFactors(123456).toString(), //
        "{2,3,643}");
  }

  @Test
  public void testPerfectPowerWithFactor() {
    // assert perfect_power(2**3*3**3) == (6, 3)
    assertEquals(Factor.perfectPower(F.ZZ(216)).toString(), //
        "{6,3}");
    // assert perfect_power(2**4*3**2) == (12, 2)
    assertEquals(Factor.perfectPower(F.ZZ(144)).toString(), //
        "{12,2}");
    // assert perfect_power(2**6*3**4, big=False) == (72, 2)
    assertEquals(Factor.perfectPower(F.ZZ(5184), F.NIL, false, true).toString(), //
        "{72,2}");
    // assert perfect_power(2**2*3) is False
    assertEquals(Factor.perfectPower(F.ZZ(12)).isNIL(), //
        true);
  }

  @Test
  public void testMultiplicity() {
    assertEquals(Factor.multiplicity(F.C10, F.C10.powerRational(10023)), //
        10023);
    assertEquals(Factor.multiplicity(F.C10.powerRational(10), F.C10.powerRational(10)), //
        1);

    for (int b = 2; b < 20; b++) {
      for (int i = 0; i < 100; i++) {
        // System.out.println("(" + b + "," + i);
        assertEquals(Factor.multiplicity(F.ZZ(b), F.ZZ(b).powerRational(i)), //
            i);
        assertEquals(Factor.multiplicity(F.ZZ(b), F.ZZ(b).powerRational(i).multiply(23)), //
            i);
        assertEquals(Factor.multiplicity(F.ZZ(b), F.ZZ(b).powerRational(i).multiply(1000249)), //
            i);
      }
    }
    // assert multiplicity(b, b**i) == i
    // assert multiplicity(b, (b**i) * 23) == i
    // assert multiplicity(b, (b**i) * 1000249) == i
  }

  @Test
  public void testPerfectPower() {
    assertEquals(Factor.perfectPower(F.C0).isPresent(), //
        false);
    assertEquals(Factor.perfectPower(F.C1).isPresent(), //
        false);
    assertEquals(Factor.perfectPower(F.C2).isPresent(), //
        false);
    assertEquals(Factor.perfectPower(F.C3).isPresent(), //
        false);
    assertEquals(Factor.perfectPower(F.C4).toString(), //
        "{2,2}");
    assertEquals(Factor.perfectPower(F.ZZ(14)).isPresent(), //
        false);
    assertEquals(Factor.perfectPower(F.ZZ(25)).toString(), //
        "{5,2}");
    assertEquals(Factor.perfectPower(F.ZZ(22)).isPresent(), //
        false);
    assertEquals(Factor.perfectPower(F.ZZ(22), F.List(2)).isPresent(), //
        false);
    // assert perfect_power(137**(3*5*13)) == (137, 3*5*13)
    assertEquals(Factor.perfectPower(F.ZZ(137).powerRational(3 * 5 * 13)).toString(), //
        "{137,195}");
    // assert perfect_power(137**(3*5*13) + 1) is False
    assertEquals(Factor.perfectPower(F.ZZ(137).powerRational(3 * 5 * 13).add(F.C1)).isPresent(), //
        false);
    // assert perfect_power(137**(3*5*13) - 1) is False
    assertEquals(
        Factor.perfectPower(F.ZZ(137).powerRational(3 * 5 * 13).subtractFrom(F.C1)).isPresent(), //
        false);
    // assert perfect_power(103005006004**7) == (103005006004, 7)
    assertEquals(Factor.perfectPower(F.ZZ(103005006004L).powerRational(7)).toString(), //
        "{103005006004,7}");
    // assert perfect_power(103005006004**7 + 1) is False
    assertEquals(Factor.perfectPower(F.ZZ(103005006004L).powerRational(7).add(F.C1)).isPresent(), //
        false);
    // assert perfect_power(103005006004**7 - 1) is False
    assertEquals(
        Factor.perfectPower(F.ZZ(103005006004L).powerRational(7).subtract(F.C1)).isPresent(), //
        false);
    // assert perfect_power(103005006004**12) == (103005006004, 12)
    assertEquals(Factor.perfectPower(F.ZZ(103005006004L).powerRational(12)).toString(), //
        "{103005006004,12}");
    // assert perfect_power(103005006004**12 + 1) is False
    assertFalse(Factor.perfectPower(F.ZZ(103005006004L).powerRational(12).add(F.C1)).isPresent());
    // assert perfect_power(103005006004**12 - 1) is False
    assertFalse(
        Factor.perfectPower(F.ZZ(103005006004L).powerRational(12).subtract(F.C1)).isPresent());
    // assert perfect_power(3**3*5**3) == (15, 3)
    assertEquals(Factor.perfectPower(F.ZZ(27 * 125)).toString(), //
        "{15,3}");
    // assert perfect_power((9**99 + 1)**60) == (9**99 + 1, 60)
    IInteger nine99 = F.ZZ(9).powerRational(99).add(F.C1);
    IPair p9 = Factor.perfectPower(nine99.powerRational(60));
    assertTrue(p9.isPresent() && p9.first().equals(nine99) && p9.second().equals(F.ZZ(60)));
    // assert perfect_power((9**99 + 1)**60 + 1) is False
    assertFalse(Factor.perfectPower(nine99.powerRational(60).add(F.C1)).isPresent());
    // assert perfect_power((9**99 + 1)**60 - 1) is False
    assertFalse(Factor.perfectPower(nine99.powerRational(60).subtract(F.C1)).isPresent());
    // assert perfect_power(13**4, [3, 5]) is False
    assertFalse(Factor.perfectPower(F.ZZ(13).powerRational(4), F.List(3, 5)).isPresent());
    // assert perfect_power(3**4, [3, 10], factor=0) is False
    assertFalse(
        Factor.perfectPower(F.ZZ(3).powerRational(4), F.List(3, 10), true, false).isPresent());
    // assert perfect_power(2**3*5**5) is False
    assertFalse(Factor.perfectPower(F.ZZ(8 * 3125)).isPresent());
    // assert perfect_power(2*13**4) is False
    assertFalse(Factor.perfectPower(F.ZZ(2 * 28561)).isPresent());
    // assert perfect_power(2**5*3**3) is False
    assertFalse(Factor.perfectPower(F.ZZ(32 * 27)).isPresent());
    // t = 2**24
    // for d in divisors(24):
    // m = perfect_power(t*3**d)
    // assert m and m[1] == d or d == 1
    // m = perfect_power(t*3**d, big=False)
    // assert m and m[1] == 2 or d == 1 or d == 3, (d, m)
    IInteger t = F.ZZ(2).powerRational(24);
    for (int d : new int[] {1, 2, 3, 4, 6, 8, 12, 24}) {
      IInteger n = t.multiply(F.ZZ(3).powerRational(d));
      IPair m = Factor.perfectPower(n);
      assertTrue(d == 1 || (m.isPresent() && m.second().toIntDefault() == d), "d=" + d + " " + m);
      m = Factor.perfectPower(n, F.NIL, false, true);
      assertTrue(d == 1 || d == 3 || (m.isPresent() && m.second().toIntDefault() == 2),
          "big=False d=" + d + " " + m);
    }

    // # negatives and non-integer rationals
    // assert perfect_power(-4) is False
    assertFalse(Factor.perfectPower(F.ZZ(-4)).isPresent());
    // assert perfect_power(-8) == (-2, 3)
    assertEquals(Factor.perfectPower(F.ZZ(-8)).toString(), //
        "{-2,3}");
    // assert perfect_power(-S(1)/8) == (-S(1)/2, 3)
    assertEquals(Factor.perfectPower(F.QQ(-1, 8)).toString(), //
        "{-1/2,3}");
    // assert perfect_power(S(1)/3) == False
    assertFalse(Factor.perfectPower(F.QQ(1, 3)).isPresent());
    // assert perfect_power(Rational(1, 2)**3) == (S.Half, 3)
    assertEquals(Factor.perfectPower(F.QQ(1, 8)).toString(), //
        "{1/2,3}");
    // assert perfect_power(Rational(-3, 2)**3) == (-3*S.Half, 3)
    assertEquals(Factor.perfectPower(F.QQ(-27, 8)).toString(), //
        "{-3/2,3}");
    // assert perfect_power(-5**15) == (-5, 15)
    assertEquals(Factor.perfectPower(F.ZZ(5).powerRational(15).negate()).toString(), //
        "{-5,15}");
    // assert perfect_power(-5**15, big=False) == (-3125, 3)
    assertEquals(
        Factor.perfectPower(F.ZZ(5).powerRational(15).negate(), F.NIL, false, true).toString(), //
        "{-3125,3}");
    // assert perfect_power(-5**15, [15]) == (-5, 15)
    assertEquals(Factor.perfectPower(F.ZZ(5).powerRational(15).negate(), F.List(15)).toString(), //
        "{-5,15}");
    // n = -3 ** 60
    // assert perfect_power(n) == (-81, 15)
    assertEquals(Factor.perfectPower(F.ZZ(3).powerRational(60).negate()).toString(), //
        "{-81,15}");
    // assert perfect_power(n, big=False) == (-3486784401, 3)
    assertEquals(
        Factor.perfectPower(F.ZZ(3).powerRational(60).negate(), F.NIL, false, true).toString(), //
        "{-3486784401,3}");
  }

  @Tag(TestTags.SLOW)
  @Test
  public void testPerfectPowerLarge() {
    // the test case limits the bit length to 200000
    final int maxBitLength = Config.MAX_BIT_LENGTH;
    Config.MAX_BIT_LENGTH = Integer.MAX_VALUE;
    try {
      perfectPowerLarge();
    } finally {
      Config.MAX_BIT_LENGTH = maxBitLength;
    }
  }

  private void perfectPowerLarge() {
    // assert perfect_power(2**10007) == (2, 10007)
    assertEquals(Factor.perfectPower(F.ZZ(2).powerRational(10007)).toString(), //
        "{2,10007}");
    // assert perfect_power(2**10007 + 1) is False
    assertFalse(Factor.perfectPower(F.ZZ(2).powerRational(10007).add(F.C1)).isPresent());
    // assert perfect_power(2**10007 - 1) is False
    assertFalse(Factor.perfectPower(F.ZZ(2).powerRational(10007).subtract(F.C1)).isPresent());
    // assert perfect_power((10**40000)**2, big=False) == (10**40000, 2)
    IInteger ten40000 = F.ZZ(10).powerRational(40000);
    IPair m = Factor.perfectPower(ten40000.powerRational(2), F.NIL, false, true);
    assertTrue(m.isPresent() && m.first().equals(ten40000) && m.second().equals(F.C2));
    // assert perfect_power(10**100000) == (10, 100000)
    assertEquals(Factor.perfectPower(F.ZZ(10).powerRational(100000)).toString(), //
        "{10,100000}");
    // assert perfect_power(10**100001) == (10, 100001)
    assertEquals(Factor.perfectPower(F.ZZ(10).powerRational(100001)).toString(), //
        "{10,100001}");
  }

  @Test
  public void testPrivatePerfectPower() {
    // for x in [2, 3, 5, 6, 7, 12, 15, 105, 100003]:
    // for y in range(2, 100):
    // assert _perfect_power(x**y) == (x, y)
    // assert _perfect_power(101*x**y) == False
    // # Catalan's conjecture
    // if x**y not in [8, 9]:
    // assert _perfect_power(x**y + 1) == False
    // assert _perfect_power(x**y - 1) == False
    for (int x : new int[] {2, 3, 5, 6, 7, 12, 15, 105, 100003}) {
      for (int y = 2; y < 30; y++) {
        IInteger n = F.ZZ(x).powerRational(y);
        assertEquals("{" + x + "," + y + "}", Factor.perfectPower(n).toString());
        assertFalse(Factor.perfectPower(n.multiply(F.ZZ(101))).isPresent(), x + "^" + y + "*101");
        if (!n.equals(F.C8) && !n.equals(F.C9)) {
          assertFalse(Factor.perfectPower(n.add(F.C1)).isPresent(), x + "^" + y + "+1");
          assertFalse(Factor.perfectPower(n.subtract(F.C1)).isPresent(), x + "^" + y + "-1");
        }
      }
    }
    // for x in range(1, 10):
    // for y in range(1, 10):
    // g = gcd(x, y)
    // if g == 1: assert _perfect_power(5**x * 101**y) == False
    // else: assert _perfect_power(5**x * 101**y) == (5**(x//g) * 101**(y//g), g)
    for (int x = 1; x < 10; x++) {
      for (int y = 1; y < 10; y++) {
        int g = BigInteger.valueOf(x).gcd(BigInteger.valueOf(y)).intValue();
        IInteger n = F.ZZ(5).powerRational(x).multiply(F.ZZ(101).powerRational(y));
        if (g == 1) {
          assertFalse(Factor.perfectPower(n).isPresent(), "5^" + x + "*101^" + y);
        } else {
          IInteger base = F.ZZ(5).powerRational(x / g).multiply(F.ZZ(101).powerRational(y / g));
          assertEquals("{" + base + "," + g + "}", Factor.perfectPower(n).toString(),
              "5^" + x + "*101^" + y);
        }
      }
    }
  }

  @Test
  public void testProperDivisorCount() {
    // assert proper_divisor_count(0) == 0
    assertEquals("0", Factor.divisorCount(F.C0, 1, true).toString());
    // assert proper_divisor_count(-1) == 0
    assertEquals("0", Factor.divisorCount(F.CN1, 1, true).toString());
    // assert proper_divisor_count(1) == 0
    assertEquals("0", Factor.divisorCount(F.C1, 1, true).toString());
    // assert proper_divisor_count(36) == 8
    assertEquals("8", Factor.divisorCount(F.ZZ(36), 1, true).toString());
    // assert proper_divisor_count(2*3*5) == 7
    assertEquals("7", Factor.divisorCount(F.ZZ(30), 1, true).toString());
    // assert proper_divisor_count(6) == 3
    assertEquals("3", Factor.divisorCount(F.ZZ(6), 1, true).toString());
    // assert proper_divisor_count(108) == 11
    assertEquals("11", Factor.divisorCount(F.ZZ(108), 1, true).toString());
  }
}
