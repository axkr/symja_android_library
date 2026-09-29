package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigInteger;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numbertheory.Primality;

/**
 * Root extraction of integers and fractions whose cofactor after the small prime factors is a
 * perfect power, for example <code>102173269324018369 == 319645537^2</code> where
 * <code>319645537</code> is a prime greater than the trial division bound.
 */
public class PerfectPowerRootTest extends ExprEvaluatorTestCase {

  @Test
  public void testSqrtPerfectSquare() {
    check("Sqrt(102173269324018369)", //
        "319645537");
    check("Sqrt(-102173269324018369)", //
        "I*319645537");
    check("Sqrt(102173269324018369/4)", //
        "319645537/2");
    check("Sqrt(4/102173269324018369)", //
        "2/319645537");
    check("Sqrt(2*319645537^2)", //
        "319645537*Sqrt(2)");
    check("Sqrt(2*319645537^2/3)", //
        "319645537*Sqrt(2/3)");
    check("1/Sqrt(102173269324018369)", //
        "1/319645537");
    check("102173269324018369^(-1/2)", //
        "1/319645537");
    check("102173269324018369^(3/2)", //
        "32659229540121478556869153");
    // 32771 is the first prime greater than the trial division bound 32749
    check("Sqrt(1073938441)", //
        "32771");
    check("Sqrt(2*1073938441)", //
        "32771*Sqrt(2)");
    // two primes greater than the trial division bound
    check("Sqrt(1000072001494007128009801)", //
        "1000036000099");
  }

  @Test
  public void testNthRootPerfectPower() {
    check("(319645537^3)^(1/3)", //
        "319645537");
    check("32659229540121478556869153^(1/3)", //
        "319645537");
    check("(-319645537^3)^(1/3)", //
        "319645537*(-1)^(1/3)");
    check("(319645537^3/8)^(1/3)", //
        "319645537/2");
    check("(319645537^6)^(1/4)", //
        "319645537*Sqrt(319645537)");
    check("(319645537^2)^(1/4)", //
        "Sqrt(319645537)");
    check("Sqrt(319645537^3)", //
        "319645537*Sqrt(319645537)");
  }

  @Test
  public void testNoPerfectPower() {
    check("Sqrt(319646495936611)", //
        "Sqrt(319646495936611)");
    check("319645537^(1/3)", //
        "319645537^(1/3)");
    check("Sqrt(640605218)", //
        "17897*Sqrt(2)");
    check("319646495936611^(-1/2)", //
        "1/Sqrt(319646495936611)");
    check("8^(-1/2)", //
        "1/(2*Sqrt(2))");
    check("(8/27)^(-1/3)", //
        "3/2");
  }

  @Test
  public void testNthRootFloor() {
    Random random = new Random(42);
    for (int n = 2; n <= 40; n++) {
      for (int bits : new int[] {1, 5, 30, 60, 200, 1000}) {
        BigInteger r = new BigInteger(bits, random).add(BigInteger.TWO);
        BigInteger power = r.pow(n);
        assertEquals(r, Primality.nthRootFloor(power, n));
        assertEquals(r.subtract(BigInteger.ONE),
            Primality.nthRootFloor(power.subtract(BigInteger.ONE), n));
        assertEquals(r, Primality.nthRootFloor(power.add(BigInteger.ONE), n));
      }
    }
  }

  @Test
  public void testPerfectPower() {
    BigInteger p = BigInteger.valueOf(319645537L);
    BigInteger[] pp = Primality.perfectPower(p.pow(12), 11);
    assertEquals(p, pp[0]);
    assertEquals(BigInteger.valueOf(12), pp[1]);
    pp = Primality.perfectPower(p.pow(7).multiply(BigInteger.valueOf(1031).pow(7)), 11);
    assertEquals(p.multiply(BigInteger.valueOf(1031)), pp[0]);
    assertEquals(BigInteger.valueOf(7), pp[1]);
    pp = Primality.perfectPower(p.pow(3).multiply(BigInteger.valueOf(1031)), 11);
    assertEquals(BigInteger.ONE, pp[1]);
  }
}
