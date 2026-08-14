package org.matheclipse.core.numbertheory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

/** Tests for {@link Primality#pollardRhoFactors(BigInteger, java.util.Map)} and its callers. */
public class PrimalityTest {

  /** <code>Prime(10^5)</code> */
  private static final BigInteger P1 = BigInteger.valueOf(1299709);
  /** <code>Prime(10^6)</code> */
  private static final BigInteger P2 = BigInteger.valueOf(15485863);
  /** <code>Prime(2*10^6)</code> */
  private static final BigInteger P3 = BigInteger.valueOf(32452843);

  /**
   * Assert that the factorization is complete and that every factor really is a prime number.
   * Pollards rho algorithm is randomized, so a wrong result may only show up in some of the runs.
   */
  private static void assertFactorization(BigInteger value,
      Map<BigInteger, Integer> expectedFactors, int runs) {
    for (int i = 0; i < runs; i++) {
      SortedMap<BigInteger, Integer> map = new Primality().factorInteger(value);
      BigInteger product = BigInteger.ONE;
      for (Map.Entry<BigInteger, Integer> entry : map.entrySet()) {
        assertTrue(entry.getKey().isProbablePrime(32), //
            "run " + i + ": factor " + entry.getKey() + " of " + value + " isn't a prime number");
        product = product.multiply(entry.getKey().pow(entry.getValue()));
      }
      assertEquals(value, product, //
          "run " + i + ": the factors " + map + " don't multiply up to " + value);
      assertEquals(expectedFactors, map, "run " + i);
    }
  }

  private static Map<BigInteger, Integer> factors(Object... primeExponentPairs) {
    Map<BigInteger, Integer> result = new TreeMap<BigInteger, Integer>();
    for (int i = 0; i < primeExponentPairs.length; i += 2) {
      result.put((BigInteger) primeExponentPairs[i], (Integer) primeExponentPairs[i + 1]);
    }
    return result;
  }

  /**
   * For a large prime power the cycle detection in <code>Primality#rho()</code> tends to return a
   * composite divisor instead of a prime one. That happened in about 4 of 1000 runs, so a high
   * number of runs is needed to detect it reliably.
   */
  @Test
  public void testFactorIntegerLargePrimePowerRepeatedly() {
    assertFactorization(P1.pow(10), factors(P1, 10), 1000);
  }

  @Test
  public void testFactorIntegerPrimePowers() {
    for (int exponent = 1; exponent <= 12; exponent++) {
      assertFactorization(P1.pow(exponent), factors(P1, exponent), 20);
      assertFactorization(P2.pow(exponent), factors(P2, exponent), 5);
    }
  }

  /** A perfect power with a composite exponent has to be decomposed completely. */
  @Test
  public void testFactorIntegerCompositeExponents() {
    assertFactorization(P1.pow(4), factors(P1, 4), 20);
    assertFactorization(P1.pow(6), factors(P1, 6), 20);
    assertFactorization(P1.pow(9), factors(P1, 9), 20);
    assertFactorization(P1.pow(30), factors(P1, 30), 200);
    // (P1*P2)^6 is a perfect power with a composite, non-prime-power base
    assertFactorization(P1.multiply(P2).pow(6), factors(P1, 6, P2, 6), 20);
  }

  /**
   * A proper divisor returned by <code>Primality#rho()</code> isn't necessarily prime, so it has to
   * be factored further instead of being recorded as a prime factor.
   */
  @Test
  public void testFactorIntegerSeveralLargePrimes() {
    assertFactorization(P1.multiply(P2), factors(P1, 1, P2, 1), 50);
    assertFactorization(P1.multiply(P2).multiply(P3), factors(P1, 1, P2, 1, P3, 1), 50);
    assertFactorization(P1.pow(4).multiply(P2.pow(3)), factors(P1, 4, P2, 3), 50);
    assertFactorization(P1.pow(2).multiply(P2).multiply(P3), factors(P1, 2, P2, 1, P3, 1), 50);
  }

  /** Small prime factors are stripped before Pollards rho algorithm is used. */
  @Test
  public void testFactorIntegerSmallAndLargePrimes() {
    assertFactorization(BigInteger.valueOf(2), factors(BigInteger.valueOf(2), 1), 1);
    assertFactorization(BigInteger.valueOf(1024), factors(BigInteger.valueOf(2), 10), 1);
    assertFactorization(BigInteger.valueOf(360),
        factors(BigInteger.valueOf(2), 3, BigInteger.valueOf(3), 2, BigInteger.valueOf(5), 1), 1);
    assertFactorization(BigInteger.valueOf(12).multiply(P1.pow(5)),
        factors(BigInteger.valueOf(2), 2, BigInteger.valueOf(3), 1, P1, 5), 20);
  }
}
