package org.matheclipse.core.numbertheory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    BigInteger n = BigInteger.TEN.pow(49).nextProbablePrime()
        .multiply(BigInteger.TEN.pow(50).nextProbablePrime());
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

  /** Two primes of about <code>digits/2</code> digits each from the seeded generator. */
  private static BigInteger[] balancedPrimes(int digits, Random random) {
    int bits = (int) Math.round(digits * 3.321928094887362 / 2);
    BigInteger p = BigInteger.probablePrime(bits, random);
    BigInteger q = BigInteger.probablePrime(bits, random);
    while (q.equals(p)) {
      q = BigInteger.probablePrime(bits, random);
    }
    return new BigInteger[] {p, q};
  }

  /** The quadratic sieve has to return a proper divisor of <code>n</code>. */
  private static void assertSiqsSplits(BigInteger n) {
    BigInteger d = Siqs.factor(n);
    assertTrue(d != null && d.compareTo(BigInteger.ONE) > 0 && d.compareTo(n) < 0
        && n.mod(d).signum() == 0, "no proper divisor of " + n + ": " + d);
  }

  /** Balanced semiprimes of 30 to 48 digits, where the quadratic sieve is used. */
  @Test
  public void testSiqsBalancedSemiprimes() {
    Random random = new Random(20261019L);
    for (int digits = 30; digits <= 48; digits += 6) {
      BigInteger[] pq = balancedPrimes(digits, random);
      BigInteger n = pq[0].multiply(pq[1]);
      assertSiqsSplits(n);
      assertFactorization(n, factors(pq[0], 1, pq[1], 1), 1);
    }
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testSiqsBalancedSemiprimesLarge() {
    Random random = new Random(1019L);
    for (int digits = 55; digits <= 70; digits += 5) {
      BigInteger[] pq = balancedPrimes(digits, random);
      BigInteger n = pq[0].multiply(pq[1]);
      assertSiqsSplits(n);
      assertFactorization(n, factors(pq[0], 1, pq[1], 1), 1);
    }
  }

  /**
   * <code>p^2*q</code> isn't a perfect power; the sieve still finds a divisor, and also one of a
   * product of three primes.
   */
  @Test
  public void testSiqsSquaredFactor() {
    Random random = new Random(424242L);
    BigInteger p = BigInteger.probablePrime(46, random);
    BigInteger q = BigInteger.probablePrime(48, random);
    BigInteger n = p.pow(2).multiply(q);
    assertSiqsSplits(n);
    assertFactorization(n, factors(p, 2, q, 1), 1);
    BigInteger r = BigInteger.probablePrime(47, random);
    BigInteger m = p.multiply(q).multiply(r);
    assertSiqsSplits(m);
    assertFactorization(m, factors(p, 1, q, 1, r, 1), 1);
  }

  /**
   * Numbers for which the Knuth-Schroeppel function picks a multiplier <code>k &gt; 1</code>; the
   * relations are congruences modulo <code>k*n</code>, the factors are those of <code>n</code>.
   */
  @Test
  public void testSiqsMultiplier() {
    Random random = new Random(777L);
    int withMultiplier = 0;
    for (int i = 0; i < 40 && withMultiplier < 4; i++) {
      BigInteger[] pq = balancedPrimes(36, random);
      BigInteger n = pq[0].multiply(pq[1]);
      int k = Siqs.multiplier(n);
      assertTrue(k >= 1 && k <= 73 && (k & 1) == 1, "multiplier " + k);
      if (k > 1) {
        withMultiplier++;
        assertSiqsSplits(n);
      }
    }
    assertEquals(4, withMultiplier);
  }

  /** A prime of the factor base which divides the number is returned at once. */
  @Test
  public void testSiqsFactorBasePrimeDivides() {
    Random random = new Random(31337L);
    BigInteger[] pq = balancedPrimes(34, random);
    BigInteger n = BigInteger.valueOf(1009).multiply(pq[0]).multiply(pq[1]);
    assertEquals(BigInteger.valueOf(1009), Siqs.factor(n));
  }

  /**
   * The Miller-Rabin test in 64 bit Montgomery arithmetic for numbers above <code>2^62</code>,
   * where the sum of two residues no longer fits into a signed long.
   */
  @Test
  public void testSiqsIsPrimeAbove2Pow62() {
    Random random = new Random(4242L);
    BigInteger low = BigInteger.ONE.shiftLeft(62);
    for (int i = 0; i < 500; i++) {
      long candidate = (random.nextLong() >>> 2) | (1L << 62);
      BigInteger prime = BigInteger.valueOf(candidate).nextProbablePrime();
      if (prime.bitLength() < 64) {
        assertTrue(Siqs.isPrime(prime.longValue()), prime + " is a prime number");
      }
      assertEquals(BigInteger.valueOf(candidate).isProbablePrime(64), Siqs.isPrime(candidate),
          "isPrime(" + candidate + ")");
    }
    // a product of two 31 bit primes above 2^62 and the largest prime below 2^63
    BigInteger p = BigInteger.valueOf(2147483659L);
    BigInteger q = low.divide(p).add(BigInteger.valueOf(1000)).nextProbablePrime();
    assertTrue(!Siqs.isPrime(p.multiply(q).longValue()));
    assertTrue(Siqs.isPrime(Long.MAX_VALUE - 24));
  }

  /** Several calls of factorInteger(n, map) add up the exponents of the product. */
  @Test
  public void testFactorIntegerAddsToMap() {
    SortedMap<BigInteger, Integer> map = new TreeMap<BigInteger, Integer>();
    map.put(BigInteger.valueOf(5), 1);
    Primality primality = new Primality();
    // -2^2*3*P1 and 2*3^2*P1*P2
    primality.factorInteger(BigInteger.valueOf(-12).multiply(P1), map);
    primality.factorInteger(BigInteger.valueOf(18).multiply(P1).multiply(P2), map);
    assertEquals(factors(BigInteger.valueOf(-1), 1, BigInteger.valueOf(2), 3,
        BigInteger.valueOf(3), 3, BigInteger.valueOf(5), 1, P1, 2, P2, 1), map);
  }

  /** Two factors close to the square root are found by the first steps of Fermat's method. */
  @Test
  public void testFactorIntegerCloseFactors() {
    BigInteger p = BigInteger.TEN.pow(44).add(BigInteger.valueOf(123456789)).nextProbablePrime();
    BigInteger q = p.add(BigInteger.TEN.pow(12)).nextProbablePrime();
    BigInteger n = p.multiply(q);
    assertEquals(p, IntegerSplitter.fermat(n, 1));
    assertTimeoutPreemptively(Duration.ofSeconds(10),
        () -> assertFactorization(n, factors(p, 1, q, 1), 1));
  }

  /**
   * A prime <code>p = 2*3*(small primes)-1</code> has a smooth <code>p+1</code>; with the start
   * value 2/7 the <code>p+1</code> method finds it because <code>-3</code> is a non-residue modulo
   * a prime <code>p = 2 (mod 3)</code>, with the start value 3 because <code>5</code> is a
   * non-residue modulo a prime <code>p = 2, 3 (mod 5)</code>.
   */
  @Test
  public void testPPlus1SmoothFactor() {
    Random random = new Random(5151L);
    BigInteger p;
    do {
      BigInteger m = BigInteger.valueOf(6);
      while (m.bitLength() < 100) {
        m = m.multiply(BigInteger.valueOf(2 + random.nextInt(20000)).nextProbablePrime());
      }
      p = m.subtract(BigInteger.ONE);
    } while (!p.isProbablePrime(40) || p.mod(BigInteger.valueOf(5)).intValue() == 1
        || p.mod(BigInteger.valueOf(5)).intValue() == 4);
    // too large for the sieve, so the p+1 method runs after the p-1 method
    BigInteger q = BigInteger.probablePrime(300, random);
    BigInteger n = p.multiply(q);
    assertEquals(p, IntegerSplitter.pPlus1(n, 2, 7, 25_000));
    assertEquals(p, IntegerSplitter.pPlus1(n, 3, 1, 25_000));
    assertFactorization(n, factors(p, 1, q, 1), 1);
  }

  /** Every dependency returned by the solver sums to the zero vector. */
  private static void assertDependencies(int[][] rows, int columns, List<int[]> dependencies,
      int minimum) {
    assertTrue(dependencies.size() >= minimum, "only " + dependencies.size() + " dependencies");
    for (int[] dependency : dependencies) {
      assertTrue(dependency.length > 0);
      int[] parity = new int[columns];
      for (int r : dependency) {
        for (int c : rows[r]) {
          parity[c] ^= 1;
        }
      }
      for (int c = 0; c < columns; c++) {
        assertEquals(0, parity[c], "column " + c + " of a dependency isn't zero");
      }
    }
  }

  /** Sparse random rows like the relations of the sieve: a few dense and many sparse columns. */
  private static int[][] randomSparseRows(int count, int columns, Random random) {
    int[][] rows = new int[count][];
    for (int r = 0; r < count; r++) {
      java.util.TreeSet<Integer> set = new java.util.TreeSet<>();
      int weight = 8 + random.nextInt(12);
      while (set.size() < weight) {
        // biased towards the small column indices
        double u = random.nextDouble();
        set.add((int) (columns * u * u * u));
      }
      rows[r] = set.stream().mapToInt(Integer::intValue).toArray();
    }
    return rows;
  }

  @Test
  public void testGF2SolverGaussianElimination() {
    Random random = new Random(99L);
    int columns = 800;
    int[][] rows = randomSparseRows(columns + 40, columns, random);
    assertDependencies(rows, columns, GF2Solver.dependencies(rows, columns, 64), 30);
  }

  @Test
  public void testGF2SolverBlockLanczos() {
    Random random = new Random(1234L);
    int columns = 4000;
    int[][] rows = randomSparseRows(columns + 100, columns, random);
    assertTrue(rows.length >= GF2Solver.LANCZOS_ROWS);
    assertDependencies(rows, columns, GF2Solver.dependencies(rows, columns, 64), 30);
  }

  /** The quadratic sieve stops when the thread is interrupted, also with worker threads. */
  @Test
  public void testSiqsInterrupted() throws InterruptedException {
    BigInteger n = BigInteger.TEN.pow(39).nextProbablePrime()
        .multiply(BigInteger.TEN.pow(41).add(BigInteger.valueOf(17)).nextProbablePrime());
    Throwable[] thrown = new Throwable[1];
    Thread thread = new Thread(() -> {
      try {
        Siqs.factor(n);
      } catch (Throwable t) {
        thrown[0] = t;
      }
    });
    thread.setDaemon(true);
    thread.start();
    Thread.sleep(300);
    thread.interrupt();
    thread.join(5000);
    assertTrue(!thread.isAlive(), "the sieve doesn't stop on interrupt");
    assertTrue(thrown[0] instanceof org.matheclipse.core.eval.exception.TimeoutException,
        String.valueOf(thrown[0]));
  }
}
