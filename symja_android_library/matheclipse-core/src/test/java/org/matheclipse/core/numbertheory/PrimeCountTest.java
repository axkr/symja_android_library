package org.matheclipse.core.numbertheory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Tests for {@link PrimeCount} and {@link PartitionNumbers}. */
public class PrimeCountTest {

  /** The method with several tuning factors against a plain sieve, near segment edges too. */
  @Test
  public void testLmoAgainstSieve() {
    long[] edges = {4_000_001L, 4_194_304L, 4_194_305L, 9_999_991L, 16_777_216L, 27_000_000L,
        27_000_001L};
    for (long x : edges) {
      long expected = PrimeCount.countSieve(x);
      for (double alpha : new double[] {1.0, 3.5, 12.0}) {
        assertEquals(expected, PrimeCount.lmo(x, alpha), "x=" + x + " alpha=" + alpha);
      }
    }
    Random random = new Random(20261019L);
    for (int i = 0; i < 12; i++) {
      long x = 4_000_001L + (long) (random.nextDouble() * 60_000_000L);
      assertEquals(PrimeCount.countSieve(x), PrimeCount.pi(x), "x=" + x);
    }
  }

  @Test
  public void testPiPowersOfTen() {
    assertEquals(0, PrimeCount.pi(1));
    assertEquals(4, PrimeCount.pi(10));
    assertEquals(50847534L, PrimeCount.pi(1_000_000_000L));
    assertEquals(4118054813L, PrimeCount.pi(100_000_000_000L));
  }

  /** The n-th prime is a prime p with pi(p) = n and pi(p-1) = n-1. */
  @Test
  public void testNthPrime() {
    Random random = new Random(4747L);
    for (int i = 0; i < 8; i++) {
      long n = 200_001L + (long) (random.nextDouble() * 30_000_000L);
      long p = PrimeCount.nthPrime(n);
      assertTrue(BigInteger.valueOf(p).isProbablePrime(40), "p=" + p);
      assertEquals(n, PrimeCount.pi(p));
      assertEquals(n - 1, PrimeCount.pi(p - 1));
    }
    assertEquals(15485863L, PrimeCount.nthPrime(1_000_000L));
  }

  /** p(n) against the convolution of the counts with the divisor sums, n p(n) = sum s(k) p(n-k). */
  @Test
  public void testPartitionNumbers() {
    int n = 300;
    BigInteger[] p = new BigInteger[n + 1];
    p[0] = BigInteger.ONE;
    for (int m = 1; m <= n; m++) {
      BigInteger sum = BigInteger.ZERO;
      for (int k = 1; k <= m; k++) {
        long sigma = 0;
        for (int d = 1; d <= k; d++) {
          if (k % d == 0) {
            sigma += d;
          }
        }
        sum = sum.add(p[m - k].multiply(BigInteger.valueOf(sigma)));
      }
      p[m] = sum.divide(BigInteger.valueOf(m));
      assertEquals(p[m], PartitionNumbers.partitionsP(m), "p(" + m + ")");
    }
    // q(n) as the coefficients of prod(1+x^k)
    BigInteger[] q = new BigInteger[n + 1];
    java.util.Arrays.fill(q, BigInteger.ZERO);
    q[0] = BigInteger.ONE;
    for (int k = 1; k <= n; k++) {
      for (int m = n; m >= k; m--) {
        q[m] = q[m].add(q[m - k]);
      }
    }
    for (int m = 0; m <= n; m++) {
      assertEquals(q[m], PartitionNumbers.partitionsQ(m), "q(" + m + ")");
    }
  }
}
