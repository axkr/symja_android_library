package org.matheclipse.core.numbertheory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.TestTags;

/** Tests for {@link Primality#pollardRhoFactors(BigInteger, java.util.Map)} and its callers. */
public class PrimalityTest {

  /** <code>Prime(10^5)</code> */
  private static final BigInteger P1 = BigInteger.valueOf(1299709);
  /** <code>Prime(10^6)</code> */
  private static final BigInteger P2 = BigInteger.valueOf(15485863);
  /** <code>Prime(2*10^6)</code> */
  private static final BigInteger P3 = BigInteger.valueOf(32452843);
  /** <code>2^61-1</code> */
  private static final BigInteger MERSENNE_61 = BigInteger.ONE.shiftLeft(61).subtract(BigInteger.ONE);
  /** <code>10^20+39</code> */
  private static final BigInteger Q20 = new BigInteger("100000000000000000039");

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
  @Tag(TestTags.SLOW)
  public void testFactorIntegerLargePrimePowerRepeatedly() {
    assertFactorization(P1.pow(10), factors(P1, 10), 1000);
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testFactorIntegerPrimePowers() {
    for (int exponent = 1; exponent <= 12; exponent++) {
      assertFactorization(P1.pow(exponent), factors(P1, exponent), 20);
      assertFactorization(P2.pow(exponent), factors(P2, exponent), 5);
    }
  }

  /** A perfect power with a composite exponent has to be decomposed completely. */
  @Test
  @Tag(TestTags.SLOW)
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
  @Tag(TestTags.SLOW)
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

  /**
   * <code>2^61-2</code> has only prime factors up to 1321, so the <code>p-1</code> method finds the
   * 19 digit factor at once. Pollards rho algorithm alone needs about 10^9 steps for it.
   */
  @Test
  public void testFactorIntegerSmoothPMinus1() {
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> assertFactorization(
        MERSENNE_61.multiply(Q20), factors(MERSENNE_61, 1, Q20, 1), 10));
  }

  /** Neither factor has a smooth <code>p-1</code>; the elliptic curve method has to find it. */
  @Test
  public void testFactorIntegerBalancedSemiprime() {
    BigInteger p = new BigInteger("1000000000000000003");
    assertTimeoutPreemptively(Duration.ofSeconds(30),
        () -> assertFactorization(p.multiply(Q20), factors(p, 1, Q20, 1), 1));
  }

  @Test
  public void testFactorIntegerThreeLargePrimes() {
    BigInteger p = new BigInteger("1000000000039");
    BigInteger q = new BigInteger("3000000000013");
    BigInteger r = new BigInteger("7000000000009");
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> assertFactorization(
        p.multiply(q).multiply(r), factors(p, 1, q, 1, r, 1), 3));
  }

  /** A square of a 16 digit prime is recognized as a perfect power. */
  @Test
  public void testFactorIntegerSquareOfLargePrime() {
    BigInteger p = new BigInteger("1000000000000037");
    assertTimeoutPreemptively(Duration.ofSeconds(10), () -> assertFactorization(
        P1.pow(4).multiply(p.pow(2)), factors(P1, 4, p, 2), 3));
  }

  /** The elliptic curve method stops when the thread is interrupted. */
  @Test
  public void testFactorIntegerInterrupted() throws InterruptedException {
    BigInteger n = BigInteger.TEN.pow(30).nextProbablePrime()
        .multiply(BigInteger.TEN.pow(31).nextProbablePrime());
    Throwable[] thrown = new Throwable[1];
    Thread thread = new Thread(() -> {
      try {
        new Primality().factorInteger(n);
      } catch (Throwable t) {
        thrown[0] = t;
      }
    });
    // a daemon, so that a factorization which ignores the interrupt can't keep the JVM alive
    thread.setDaemon(true);
    thread.start();
    Thread.sleep(200);
    thread.interrupt();
    thread.join(5000);
    assertTrue(!thread.isAlive(), "the factorization doesn't stop on interrupt");
    assertTrue(thrown[0] instanceof org.matheclipse.core.eval.exception.TimeoutException,
        String.valueOf(thrown[0]));
  }
}
