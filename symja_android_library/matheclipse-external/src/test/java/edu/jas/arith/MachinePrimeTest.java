package edu.jas.arith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class MachinePrimeTest {

  /** Primes below 2^16, used to sieve segments below 2^32. */
  private static int[] smallPrimes() {
    boolean[] composite = new boolean[1 << 16];
    return IntStream.range(2, 1 << 16).filter(i -> {
      if (composite[i]) {
        return false;
      }
      for (long j = (long) i * i; j < composite.length; j += i) {
        composite[(int) j] = true;
      }
      return true;
    }).toArray();
  }

  /** Compares {@link MachinePrime} with a sieve on <code>[lo, lo + length)</code>. */
  private static long checkSegment(long lo, int length, int[] primes) {
    boolean[] composite = new boolean[length];
    for (int p : primes) {
      long pp = (long) p * p;
      if (pp >= lo + length) {
        break;
      }
      for (long j = Math.max(pp, (lo + p - 1) / p * p); j < lo + length; j += p) {
        composite[(int) (j - lo)] = true;
      }
    }
    long count = 0;
    for (int i = 0; i < length; i++) {
      long n = lo + i;
      boolean isPrime = n >= 2 && !composite[i];
      if (isPrime) {
        count++;
      }
      if (MachinePrime.isPrime(n) != isPrime
          || (n <= Integer.MAX_VALUE && MachinePrime.isPrime((int) n) != isPrime)) {
        throw new AssertionError("wrong result for " + n);
      }
    }
    return count;
  }

  @Test
  public void testSieveBelow2Pow24() {
    // pi(2^24) = 1077871
    assertEquals(1077871L, checkSegment(0, 1 << 24, smallPrimes()));
  }

  @Test
  @Tag("slow")
  public void testExhaustiveBelow2Pow32() {
    int[] primes = smallPrimes();
    final int segment = 1 << 22;
    AtomicLong count = new AtomicLong();
    IntStream.range(0, (int) ((1L << 32) / segment)).parallel()
        .forEach(s -> count.addAndGet(checkSegment((long) s * segment, segment, primes)));
    // pi(2^32) = 203280221
    assertEquals(203280221L, count.get());
  }

  @Test
  public void testNegativeAndSmall() {
    assertFalse(MachinePrime.isPrime(Integer.MIN_VALUE));
    assertFalse(MachinePrime.isPrime(Long.MIN_VALUE));
    assertFalse(MachinePrime.isPrime(-2));
    assertFalse(MachinePrime.isPrime(-7L));
    assertFalse(MachinePrime.isPrime(0));
    assertFalse(MachinePrime.isPrime(1));
    assertTrue(MachinePrime.isPrime(2));
    assertTrue(MachinePrime.isPrime(3L));
    assertTrue(MachinePrime.isPrime(65521)); // largest prime below 2^16
    assertFalse(MachinePrime.isPrime(65535));
    assertTrue(MachinePrime.isPrime(65537));
    assertTrue(MachinePrime.isPrime(Integer.MAX_VALUE));
    assertTrue(MachinePrime.isPrime((long) Integer.MAX_VALUE));
  }

  @Test
  public void testLongEdgeCases() {
    assertTrue(MachinePrime.isPrime(4294967291L)); // largest prime below 2^32
    assertTrue(MachinePrime.isPrime(4294967311L)); // smallest prime above 2^32
    assertTrue(MachinePrime.isPrime(2305843009213693951L)); // 2^61 - 1
    assertTrue(MachinePrime.isPrime(9223372036854775783L)); // largest prime below 2^63
    assertFalse(MachinePrime.isPrime(Long.MAX_VALUE));
    assertFalse(MachinePrime.isPrime(4611686014132420609L)); // (2^31 - 1)^2
    assertFalse(MachinePrime.isPrime(1000000007L * 1000000009L));
  }

  @Test
  public void testStrongPseudoprimesToBase2() {
    long[] pseudoprimes = {
        // below 2^32, strong pseudoprimes to several small bases
        2047L, 1373653L, 25326001L, 3215031751L,
        // above 2^32
        4294967297L, 4297078001L, 4297753027L, 1099805981633L, 1099993840849L, 1100101530877L,
        2152302898747L, 3474749660383L, 341550071728321L, 3825123056546413051L};
    for (long n : pseudoprimes) {
      assertFalse(MachinePrime.isPrime(n), Long.toString(n));
      if (n <= Integer.MAX_VALUE) {
        assertFalse(MachinePrime.isPrime((int) n), Long.toString(n));
      }
    }
  }

  @Test
  public void testRandomLongs() {
    Random random = new Random(0x5EED);
    for (int i = 0; i < 200_000; i++) {
      long n = (i & 1) == 0 ? random.nextLong() >>> 1
          : (1L << 32) + Math.floorMod(random.nextLong(), 1L << 40);
      assertEquals(BigInteger.valueOf(n).isProbablePrime(64), MachinePrime.isPrime(n),
          Long.toString(n));
    }
  }

  @Test
  public void testIsProbablePrimeBigInteger() {
    assertTrue(MachinePrime.isProbablePrime(BigInteger.valueOf(-7), 32));
    assertFalse(MachinePrime.isProbablePrime(BigInteger.valueOf(Long.MIN_VALUE), 32));
    assertTrue(MachinePrime.isProbablePrime(BigInteger.valueOf(9223372036854775783L), 32));
    // 2^89 - 1
    assertTrue(MachinePrime.isProbablePrime(BigInteger.ONE.shiftLeft(89).subtract(BigInteger.ONE), 32));
    assertFalse(MachinePrime.isProbablePrime(BigInteger.ONE.shiftLeft(89).add(BigInteger.ONE), 32));
  }
}
