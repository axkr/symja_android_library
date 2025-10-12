package org.matheclipse.core.sympy.ntheory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
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

    // TODO modulus
    // assert divisor_count(180, 3) == divisor_count(180//3)
    // assert divisor_count(2*3*5, 7) == 0
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
    // // assert perfect_power(103005006004**12 + 1) is False
    // assertEquals(Factor.perfectPower(F.ZZ(103005006004L).powerRational(12).add(F.C1)).toString(),
    // //
    // "{103005006004,12}");
    // // assert perfect_power(103005006004**12 - 1) is False
    // assertEquals(
    // Factor.perfectPower(F.ZZ(103005006004L).powerRational(12).subtract(F.C1)).toString(), //
    // "{103005006004,12}");
    // assert perfect_power(3**3*5**3) == (15, 3)
    assertEquals(Factor.perfectPower(F.ZZ(27 * 125)).toString(), //
        "{15,3}");

    // SLOW - assert perfect_power(2**10007) == (2, 10007)
    // assertEquals(Factor.perfectPower(F.ZZ(2).powerRational(10007)).toString(), //
    // "{2,10007}");
    // SLOW assert perfect_power(2**10007 + 1) is False
    // assertEquals(Factor.perfectPower(F.ZZ(2).powerRational(10007).add(F.C1)).toString(), //
    // "{2,10007}");
    // SLOW assert perfect_power(2**10007 - 1) is False
    // assertEquals(Factor.perfectPower(F.ZZ(2).powerRational(10007).subtract(F.C1)).toString(), //
    // "{2,10007}")



    // assert perfect_power(2**10007 - 1) is False
    // assert perfect_power((9**99 + 1)**60) == (9**99 + 1, 60)
    // assert perfect_power((9**99 + 1)**60 + 1) is False
    // assert perfect_power((9**99 + 1)**60 - 1) is False
    // assert perfect_power((10**40000)**2, big=False) == (10**40000, 2)
    // assert perfect_power(10**100000) == (10, 100000)
    // assert perfect_power(10**100001) == (10, 100001)
    // assert perfect_power(13**4, [3, 5]) is False
    // assert perfect_power(3**4, [3, 10], factor=0) is False

    // assert perfect_power(2**3*5**5) is False
    // assert perfect_power(2*13**4) is False
    // assert perfect_power(2**5*3**3) is False
    // t = 2**24
    // for d in divisors(24):
    // m = perfect_power(t*3**d)
    // assert m and m[1] == d or d == 1
    // m = perfect_power(t*3**d, big=False)
    // assert m and m[1] == 2 or d == 1 or d == 3, (d, m)
    //
    // # negatives and non-integer rationals
    // assert perfect_power(-4) is False
    // assert perfect_power(-8) == (-2, 3)
    // assert perfect_power(Rational(1, 2)**3) == (S.Half, 3)
    // assert perfect_power(Rational(-3, 2)**3) == (-3*S.Half, 3)
  }
}
