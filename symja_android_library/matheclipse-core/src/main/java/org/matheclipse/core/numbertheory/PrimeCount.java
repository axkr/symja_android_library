package org.matheclipse.core.numbertheory;

import org.matheclipse.core.eval.EvalEngine;

/**
 * Prime counting <code>pi(x)</code> with the Lagarias-Miller-Odlyzko method and the n-th prime by
 * an estimate from Riemann's <code>R</code> function, one exact <code>pi</code> and a sieve over
 * the gap.
 *
 * <p>
 * <code>pi(x) = phi(x, a) + a - 1 - P2(x, a)</code> with <code>y = alpha*x^(1/3)</code> and
 * <code>a = pi(y)</code>. The ordinary leaves of <code>phi(x, a)</code> use a closed form for the
 * first <code>c</code> primes; the special leaves are counted on a segmented bitset sieve over
 * <code>[1, x/y]</code>. For a fixed level the leaf arguments grow when <code>m</code> falls, so the
 * counts are taken by one monotone popcount sweep per level and segment.
 */
public final class PrimeCount {

  /** The largest argument {@link #pi(long)} accepts. */
  public static final long MAX_X = 100_000_000_000_000L;

  /** <code>pi(MAX_X)</code>, the largest index {@link #nthPrime(long)} answers. */
  public static final long MAX_INDEX = 3_204_941_750_802L;

  /** Below this bound {@link #pi(long)} sieves directly. */
  private static final long SIEVE_LIMIT = 4_000_000L;

  /** Number of bits of one sieve segment. */
  private static final int SEGMENT_BITS = 1 << 18;

  /** The first primes; <code>PRIMORIAL[c]</code> is the product of the first c primes. */
  private static final int[] SMALL_PRIMES = {0, 2, 3, 5, 7, 11, 13};

  private static final int MAX_C = 6;

  private static final int[] PRIMORIAL = {1, 2, 6, 30, 210, 2310, 30030};

  private static final int[] TOTIENT = {1, 1, 2, 8, 48, 480, 5760};

  /**
   * <code>PHI_TABLE[c][r]</code> is the number of integers in <code>[1, r]</code> coprime to
   * <code>PRIMORIAL[c]</code>.
   */
  private static final int[][] PHI_TABLE = new int[MAX_C + 1][];

  static {
    for (int c = 0; c <= MAX_C; c++) {
      int pp = PRIMORIAL[c];
      int[] table = new int[pp];
      int count = 0;
      for (int r = 0; r < pp; r++) {
        if (r > 0) {
          boolean coprime = true;
          for (int i = 1; i <= c; i++) {
            if (r % SMALL_PRIMES[i] == 0) {
              coprime = false;
              break;
            }
          }
          if (coprime) {
            count++;
          }
        }
        table[r] = count;
      }
      PHI_TABLE[c] = table;
    }
  }

  private PrimeCount() {}

  /**
   * The number of primes <code>&lt;= x</code>.
   *
   * @param x a number <code>&lt;= {@link #MAX_X}</code>
   * @return <code>pi(x)</code>
   * @throws IllegalArgumentException if <code>x &gt; MAX_X</code>
   */
  public static long pi(long x) {
    if (x < 2) {
      return 0;
    }
    if (x > MAX_X) {
      throw new IllegalArgumentException("pi(x) for x > " + MAX_X);
    }
    if (x <= SIEVE_LIMIT) {
      return countSieve(x);
    }
    return lmo(x, tuningFactor(x));
  }

  /**
   * The n-th prime.
   *
   * @param n a positive number with <code>p_n &lt;= {@link #MAX_X}</code>
   * @return the n-th prime or <code>-1</code> if <code>p_n</code> exceeds {@link #MAX_X}
   */
  public static long nthPrime(long n) {
    if (n < 1) {
      throw new IllegalArgumentException("nthPrime(" + n + ")");
    }
    if (n <= 1_000_000L) {
      return Primality.prime(n);
    }
    double estimate = inverseRiemannR(n);
    if (estimate > 2.0 * MAX_X) {
      return -1;
    }
    long x = Math.min((long) estimate, MAX_X);
    long count = pi(x);
    if (Math.abs(count - n) > 20_000L) {
      // the estimate was far off; correct it once by the density of the primes
      x = Math.max(2, Math.min(x + (long) ((n - count) * Math.log(x)), MAX_X));
      count = pi(x);
    }
    if (count < n) {
      if (x == MAX_X) {
        return -1;
      }
      return primeAbove(x, n - count);
    }
    return primeAtOrBelow(x, count - n + 1);
  }

  /** The <code>k</code>-th prime <code>&gt; x</code>. */
  private static long primeAbove(long x, long k) {
    EvalEngine engine = EvalEngine.get();
    int[] primes = null;
    long low = x + 1;
    final int segment = SEGMENT_BITS;
    while (true) {
      long high = low + segment;
      long root = isqrt(high) + 1;
      if (primes == null || primes[primes.length - 1] < root) {
        primes = primesUpTo((int) Math.min(Integer.MAX_VALUE - 1, 2 * root + 1000));
      }
      long[] bits = segmentPrimes(low, high, primes);
      for (int w = 0; w < bits.length; w++) {
        long word = bits[w];
        int c = Long.bitCount(word);
        if (c < k) {
          k -= c;
          continue;
        }
        while (true) {
          int t = Long.numberOfTrailingZeros(word);
          if (--k == 0) {
            return low + ((long) w << 6) + t;
          }
          word &= word - 1;
        }
      }
      low = high;
      engine.checkTimeLimit();
    }
  }

  /** The <code>k</code>-th prime <code>&lt;= x</code> counted downwards from <code>x</code>. */
  private static long primeAtOrBelow(long x, long k) {
    EvalEngine engine = EvalEngine.get();
    long high = x + 1;
    int[] primes = primesUpTo((int) (isqrt(high) + 1000));
    while (high > 2) {
      long low = Math.max(2, high - SEGMENT_BITS);
      long[] bits = segmentPrimes(low, high, primes);
      for (int w = bits.length - 1; w >= 0; w--) {
        long word = bits[w];
        int c = Long.bitCount(word);
        if (c < k) {
          k -= c;
          continue;
        }
        while (true) {
          int t = 63 - Long.numberOfLeadingZeros(word);
          if (--k == 0) {
            return low + ((long) w << 6) + t;
          }
          word &= ~(1L << t);
        }
      }
      high = low;
      engine.checkTimeLimit();
    }
    throw new IllegalStateException("primeAtOrBelow(" + x + ")");
  }

  /**
   * The bits of the primes in <code>[low, high)</code>; bit <code>i</code> stands for
   * <code>low + i</code>.
   *
   * @param primes all primes up to at least <code>sqrt(high)</code>, ascending
   */
  private static long[] segmentPrimes(long low, long high, int[] primes) {
    int length = (int) (high - low);
    long[] bits = new long[(length + 63) >>> 6];
    java.util.Arrays.fill(bits, -1L);
    if ((length & 63) != 0) {
      bits[bits.length - 1] = (1L << (length & 63)) - 1;
    }
    for (int i = 0; i < primes.length; i++) {
      long p = primes[i];
      if (p * p >= high) {
        break;
      }
      long start = Math.max(p * p, ((low + p - 1) / p) * p);
      for (long k = start - low; k < length; k += p) {
        bits[(int) (k >>> 6)] &= ~(1L << k);
      }
    }
    // 0 and 1 are not prime
    for (long v = low; v < Math.min(2, high); v++) {
      bits[(int) ((v - low) >>> 6)] &= ~(1L << (v - low));
    }
    return bits;
  }

  /**
   * An approximation of the inverse of <code>R(x) ~ li(x) - li(x^(1/2))/2 - li(x^(1/3))/3</code>
   * by Newton's method.
   */
  private static double inverseRiemannR(double n) {
    double x = n * Math.log(n);
    for (int i = 0; i < 100; i++) {
      double r = riemannR(x);
      double next = x - (r - n) * Math.log(x);
      if (Math.abs(next - x) < 0.5) {
        return next;
      }
      x = next;
    }
    return x;
  }

  private static double riemannR(double x) {
    return logIntegral(x) - logIntegral(Math.sqrt(x)) / 2.0 - logIntegral(Math.cbrt(x)) / 3.0;
  }

  /** Ramanujan's series for the logarithmic integral <code>li(x)</code>, <code>x &gt; 1</code>. */
  private static double logIntegral(double x) {
    final double gamma = 0.57721566490153286061;
    double lnx = Math.log(x);
    double sum = 0.0;
    double inner = 0.0;
    double factor = 1.0;
    double power = 1.0;
    for (int k = 1; k < 200; k++) {
      power *= lnx / 2.0;
      factor *= k;
      if (((k - 1) & 1) == 0) {
        inner += 1.0 / (2 * ((k - 1) / 2) + 1);
      }
      double term = ((k & 1) == 0 ? -2.0 : 2.0) * power / factor * inner;
      sum += term;
      if (Math.abs(term) < 1e-17 * Math.abs(sum)) {
        break;
      }
    }
    return gamma + Math.log(lnx) + Math.sqrt(x) * sum;
  }

  private static double tuningFactor(long x) {
    double l = Math.log(x);
    // measured optimum: about 6 at 10^11, 8 from 10^12 to 10^14
    return Math.max(1.0, Math.min(l * l * l / 2500.0, 8.0));
  }

  /** <code>pi(x)</code> by the Lagarias-Miller-Odlyzko method. */
  static long lmo(long x, double alpha) {
    EvalEngine engine = EvalEngine.get();
    long x13 = iroot3(x);
    long sqrtx = isqrt(x);
    long y = (long) (alpha * x13);
    y = Math.max(y, x13);
    y = Math.min(y, sqrtx - 1);
    long z = x / y;
    int[] primes = primesWithZero((int) sqrtx);
    int piY = upperIndex(primes, y);
    int c = Math.min(piY, MAX_C);

    int yi = (int) y;
    int[] lpf = new int[yi + 1];
    byte[] mu = new byte[yi + 1];
    leastPrimeFactorAndMoebius(yi, primes, lpf, mu);

    long s1 = 0;
    int pc = primes[c];
    for (int n = 1; n <= yi; n++) {
      if (mu[n] != 0 && lpf[n] > pc) {
        s1 += mu[n] * phiTiny(x / n, c);
      }
    }

    long s2 = specialLeaves(x, y, z, c, piY, primes, lpf, mu, engine);
    long p2 = p2(x, y, piY, primes, engine);
    return s1 + s2 + piY - 1 - p2;
  }

  private static long phiTiny(long v, int c) {
    int pp = PRIMORIAL[c];
    return (v / pp) * TOTIENT[c] + PHI_TABLE[c][(int) (v % pp)];
  }

  /**
   * The sum of the special leaves <code>-mu(m)*phi(x/(p_b*m), b-1)</code> for <code>c &lt; b &lt;
   * pi(y)</code>, <code>m &lt;= y &lt; p_b*m</code> and <code>lpf(m) &gt; p_b</code>.
   */
  private static long specialLeaves(long x, long y, long z, int c, int piY, int[] primes,
      int[] lpf, byte[] mu, EvalEngine engine) {
    long limit = z + 1;
    final int segment = SEGMENT_BITS;
    long[] bits = new long[segment >>> 6];
    long[] next = new long[piY + 1];
    for (int b = 1; b <= piY; b++) {
      next[b] = primes[b];
    }
    long[] phi = new long[piY + 1];
    long sqrtY = isqrt(y);
    long sum = 0;
    for (long low = 1; low < limit; low += segment) {
      long high = Math.min(low + segment, limit);
      int length = (int) (high - low);
      java.util.Arrays.fill(bits, -1L);
      int words = (length + 63) >>> 6;
      if ((length & 63) != 0) {
        bits[words - 1] = (1L << (length & 63)) - 1;
      }
      for (int w = words; w < bits.length; w++) {
        bits[w] = 0L;
      }
      long count = length;
      int b = 1;
      for (; b <= c; b++) {
        count -= crossOff(bits, primes[b], low, high, next, b);
      }
      for (; b < piY; b++) {
        long prime = primes[b];
        long minM = Math.max(x / (prime * high), y / prime);
        long maxM = Math.min(x / (prime * low), y);
        if (prime >= maxM) {
          break;
        }
        int pos = 0;
        long swept = 0;
        if (prime > sqrtY) {
          // m <= y < prime^2 with lpf(m) > prime: m is a prime, mu(m) = -1
          long lowestM = Math.max(minM, prime);
          for (int j = upperIndex(primes, maxM); j > 0 && primes[j] > lowestM; j--) {
            int q = (int) (x / (prime * primes[j]) - low) + 1;
            swept += countBits(bits, pos, q);
            pos = q;
            sum += phi[b] + swept;
          }
        } else {
          for (long m = maxM; m > minM; m--) {
            int mi = (int) m;
            if (mu[mi] != 0 && prime < lpf[mi]) {
              int q = (int) (x / (prime * m) - low) + 1;
              swept += countBits(bits, pos, q);
              pos = q;
              sum -= mu[mi] * (phi[b] + swept);
            }
          }
        }
        phi[b] += count;
        count -= crossOff(bits, (int) prime, low, high, next, b);
      }
      engine.checkTimeLimit();
    }
    return sum;
  }

  /** Clears the multiples of <code>prime</code> in the segment; returns the number cleared. */
  private static long crossOff(long[] bits, int prime, long low, long high, long[] next, int b) {
    long k = next[b];
    long cleared = 0;
    for (; k < high; k += prime) {
      long i = k - low;
      int w = (int) (i >>> 6);
      long old = bits[w];
      bits[w] = old & ~(1L << i);
      cleared += (old >>> i) & 1L;
    }
    next[b] = k;
    return cleared;
  }

  /** The number of set bits in <code>[from, to)</code>. */
  private static long countBits(long[] bits, int from, int to) {
    if (from >= to) {
      return 0;
    }
    int fw = from >>> 6;
    int tw = (to - 1) >>> 6;
    long firstMask = -1L << from;
    long lastMask = -1L >>> (63 - ((to - 1) & 63));
    if (fw == tw) {
      return Long.bitCount(bits[fw] & firstMask & lastMask);
    }
    long count = Long.bitCount(bits[fw] & firstMask);
    for (int w = fw + 1; w < tw; w++) {
      count += Long.bitCount(bits[w]);
    }
    return count + Long.bitCount(bits[tw] & lastMask);
  }

  /**
   * <code>P2(x, a) = sum(pi(x/p) - pi(p) + 1)</code> over the primes <code>y &lt; p &lt;=
   * sqrt(x)</code>, with a segmented sieve over <code>(sqrt(x), x/y]</code>.
   */
  private static long p2(long x, long y, int piY, int[] primes, EvalEngine engine) {
    long sqrtx = isqrt(x);
    int piSqrt = upperIndex(primes, sqrtx);
    if (piY >= piSqrt) {
      return 0;
    }
    long z = x / y;
    long sum = 0;
    // sum of -(pi(p) - 1) = -(i - 1)
    for (int i = piY + 1; i <= piSqrt; i++) {
      sum -= i - 1;
    }
    int i = piSqrt;
    long count = piSqrt;
    long low = sqrtx + 1;
    while (i > piY && x / primes[i] < low) {
      sum += count;
      i--;
    }
    final int segment = SEGMENT_BITS;
    int[] sievePrimes = java.util.Arrays.copyOfRange(primes, 1, primes.length);
    while (i > piY && low <= z) {
      long high = Math.min(low + segment, z + 1);
      long[] bits = segmentPrimes(low, high, sievePrimes);
      int pos = 0;
      long swept = 0;
      while (i > piY) {
        long v = x / primes[i];
        if (v >= high) {
          break;
        }
        int q = (int) (v - low) + 1;
        swept += countBits(bits, pos, q);
        pos = q;
        sum += count + swept;
        i--;
      }
      count += swept + countBits(bits, pos, (int) (high - low));
      low = high;
      engine.checkTimeLimit();
    }
    return sum;
  }

  private static void leastPrimeFactorAndMoebius(int y, int[] primes, int[] lpf, byte[] mu) {
    java.util.Arrays.fill(mu, (byte) 1);
    lpf[1] = Integer.MAX_VALUE;
    for (int b = 1; b < primes.length; b++) {
      int p = primes[b];
      if (p > y) {
        break;
      }
      for (int m = p; m <= y; m += p) {
        if (lpf[m] == 0) {
          lpf[m] = p;
        }
        mu[m] = (byte) -mu[m];
      }
      long p2 = (long) p * p;
      for (long m = p2; m <= y; m += p2) {
        mu[(int) m] = 0;
      }
    }
  }

  /** The index of the largest entry <code>&lt;= v</code> in the 1-indexed prime array. */
  private static int upperIndex(int[] primes, long v) {
    int lo = 0;
    int hi = primes.length - 1;
    while (lo < hi) {
      int mid = (lo + hi + 1) >>> 1;
      if (primes[mid] <= v) {
        lo = mid;
      } else {
        hi = mid - 1;
      }
    }
    return lo;
  }

  /** The primes <code>&lt;= limit</code> with a leading 0, so that <code>primes[1] = 2</code>. */
  private static int[] primesWithZero(int limit) {
    int[] p = primesUpTo(limit);
    int[] result = new int[p.length + 1];
    System.arraycopy(p, 0, result, 1, p.length);
    return result;
  }

  /** The primes <code>&lt;= limit</code>, ascending. */
  public static int[] primesUpTo(int limit) {
    if (limit < 2) {
      return new int[0];
    }
    boolean[] composite = sieve(limit);
    int count = 0;
    for (int i = 2; i <= limit; i++) {
      if (!composite[i]) {
        count++;
      }
    }
    int[] result = new int[count];
    int j = 0;
    for (int i = 2; i <= limit; i++) {
      if (!composite[i]) {
        result[j++] = i;
      }
    }
    return result;
  }

  private static boolean[] sieve(int limit) {
    boolean[] composite = new boolean[limit + 1];
    for (long p = 2; p * p <= limit; p++) {
      if (!composite[(int) p]) {
        for (long k = p * p; k <= limit; k += p) {
          composite[(int) k] = true;
        }
      }
    }
    return composite;
  }

  /** <code>pi(x)</code> by a segmented sieve. */
  public static long countSieve(long x) {
    if (x < 2) {
      return 0;
    }
    int[] primes = primesUpTo((int) isqrt(x) + 1);
    long count = 0;
    EvalEngine engine = EvalEngine.get();
    for (long low = 0; low <= x; low += SEGMENT_BITS) {
      long high = Math.min(low + SEGMENT_BITS, x + 1);
      long[] bits = segmentPrimes(low, high, primes);
      for (long word : bits) {
        count += Long.bitCount(word);
      }
      engine.checkTimeLimit();
    }
    return count;
  }

  public static long isqrt(long x) {
    long r = (long) Math.sqrt((double) x);
    while (r * r > x) {
      r--;
    }
    while ((r + 1) * (r + 1) <= x) {
      r++;
    }
    return r;
  }

  public static long iroot3(long x) {
    long r = (long) Math.cbrt((double) x);
    while (r * r * r > x) {
      r--;
    }
    while ((r + 1) * (r + 1) * (r + 1) <= x) {
      r++;
    }
    return r;
  }
}
