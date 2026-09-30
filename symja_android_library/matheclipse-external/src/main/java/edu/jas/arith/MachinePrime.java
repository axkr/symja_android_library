package edu.jas.arith;

/**
 * Deterministic primality tests for machine-word integers. Used by JAS and Symja so that
 * <code>int</code> and <code>long</code> values are always tested with the same algorithm.
 *
 * <ul>
 * <li><code>0 &lt;= n &lt; 2^16</code>: bitmap lookup.
 * <li><code>2^16 &lt;= n &lt; 2^32</code>: trial division by 2, 3, 5, 7 and a single strong
 * probable-prime test whose base is selected by a hash of <code>n</code> (Forišek &amp; Jančina
 * <i>FJ32_256</i>).
 * <li><code>2^32 &lt;= n &lt; 2^63</code>: trial division by the primes up to 13 and the
 * Baillie-PSW test (strong probable-prime test to base 2 followed by a strong Lucas test with
 * Selfridge parameters), both in Montgomery arithmetic. There is no Baillie-PSW pseudoprime below
 * <code>2^64</code> (Feitsma/Gilchrist), so the test is deterministic for all <code>long</code>
 * values.
 * </ul>
 *
 * See: M. Forišek, J. Jančina, <a href="https://ceur-ws.org/Vol-1326/020-Forisek.pdf">Fast
 * Primality Testing for Integers That Fit into a Machine Word</a>, SOFSEM 2015.
 */
public final class MachinePrime {

  /**
   * Bases for the FJ32 hash buckets. For each of the 256 buckets the smallest base was chosen for
   * which the strong probable-prime test is correct for every odd <code>121 &lt;= n &lt; 2^32</code>
   * coprime to 210 in that bucket; computed by an exhaustive search over <code>[0, 2^32)</code> and
   * verified exhaustively against a sieve.
   */
  private static final int[] FJ32_BASES = {15591, 2018, 166, 7429, 8064, 16045, 10503, 4399, 1949,
      1295, 2776, 3620, 560, 3128, 5212, 2657, 2300, 2021, 4652, 1471, 9336, 4018, 2398, 20462,
      10277, 8028, 2213, 6219, 620, 3763, 4852, 5012, 3185, 1333, 6227, 5298, 1074, 2391, 5113,
      7061, 803, 1269, 3875, 422, 751, 580, 4729, 10239, 746, 2951, 556, 2206, 3778, 481, 1522,
      3476, 481, 2487, 3266, 5633, 488, 3373, 6441, 3344, 17, 15105, 1490, 4154, 2036, 1882, 1813,
      467, 3307, 14042, 6371, 658, 1005, 903, 737, 1887, 7447, 1888, 2848, 1784, 7559, 3400, 951,
      13969, 4304, 177, 41, 19875, 3110, 13221, 8726, 571, 7043, 6943, 1199, 352, 6435, 165, 1169,
      3315, 978, 233, 3003, 2562, 2994, 10587, 10030, 2377, 1902, 5354, 4447, 1555, 263, 27027,
      2283, 305, 669, 1912, 601, 6186, 429, 1930, 14873, 1784, 1661, 524, 3577, 236, 2360, 6146,
      2850, 55637, 1753, 4178, 8466, 222, 2579, 2743, 2031, 2226, 2276, 374, 2132, 813, 23788,
      1610, 4422, 5159, 1725, 3597, 3366, 14336, 579, 165, 1375, 10018, 12616, 9816, 1371, 536,
      1867, 10864, 857, 2206, 5788, 434, 8085, 17618, 727, 3639, 1595, 4944, 2129, 2029, 8195,
      8344, 6232, 9183, 8126, 1870, 3296, 7455, 8947, 25017, 541, 19115, 368, 566, 5674, 411, 522,
      1027, 8215, 2050, 6544, 10049, 614, 774, 2333, 3007, 35201, 4706, 1152, 1785, 1028, 1540,
      3743, 493, 4474, 2521, 26845, 8354, 864, 18915, 5465, 2447, 42, 4511, 1660, 166, 1249, 6259,
      2553, 304, 272, 7286, 73, 6554, 899, 2816, 5197, 13330, 7054, 2818, 3199, 811, 922, 350,
      7514, 4452, 3449, 2663, 4708, 418, 1621, 1171, 3471, 88, 11345, 412, 1559, 194};

  /** Numbers below this limit are looked up in {@link #ODD_PRIME_BITS}. */
  private static final int BITMAP_LIMIT = 1 << 16;

  /** Bit <code>(n &gt;&gt;&gt; 1) &amp; 63</code> of word <code>n &gt;&gt;&gt; 7</code> is set for odd primes n. */
  private static final long[] ODD_PRIME_BITS = new long[BITMAP_LIMIT >>> 7];

  static {
    boolean[] composite = new boolean[BITMAP_LIMIT];
    for (int i = 3; i < BITMAP_LIMIT; i += 2) {
      if (!composite[i]) {
        ODD_PRIME_BITS[i >>> 7] |= 1L << ((i >>> 1) & 63);
        if (i < 256) {
          for (int j = i * i; j < BITMAP_LIMIT; j += 2 * i) {
            composite[j] = true;
          }
        }
      }
    }
  }

  private MachinePrime() {}

  /**
   * Deterministic primality test.
   *
   * @param n integer to test
   * @return <code>true</code> if <code>n</code> is prime; <code>false</code> for all
   *         <code>n &lt; 2</code>
   */
  public static boolean isPrime(int n) {
    if (n < BITMAP_LIMIT) {
      if ((n & 1) == 0) {
        return n == 2;
      }
      return n > 0 && (ODD_PRIME_BITS[n >>> 7] & (1L << ((n >>> 1) & 63))) != 0;
    }
    if ((n & 1) == 0 || n % 3 == 0 || n % 5 == 0 || n % 7 == 0) {
      return false;
    }
    return isStrongProbablePrime(n, FJ32_BASES[fj32Hash(n)]);
  }

  /**
   * Deterministic primality test.
   *
   * @param n integer to test
   * @return <code>true</code> if <code>n</code> is prime; <code>false</code> for all
   *         <code>n &lt; 2</code>
   */
  public static boolean isPrime(long n) {
    if (n <= Integer.MAX_VALUE) {
      return n >= 2 && isPrime((int) n);
    }
    if ((n & 1) == 0 || n % 3 == 0 || n % 5 == 0 || n % 7 == 0 || n % 11 == 0 || n % 13 == 0) {
      return false;
    }
    Montgomery m = new Montgomery(n);
    if (n < (1L << 32)) {
      // the FJ32 table is valid for all n < 2^32
      return m.isStrongProbablePrime(FJ32_BASES[fj32Hash((int) n)]);
    }
    if (!m.isStrongProbablePrime(2)) {
      return false;
    }
    // Selfridge's method A: first D in 5, -7, 9, -11, ... with Jacobi(D/n) == -1
    long d = 5;
    for (int i = 0;; i++) {
      int j = jacobi(d, n);
      if (j == -1) {
        break;
      }
      if (j == 0) {
        // |D| < n shares a factor with n
        return false;
      }
      if (i == 10 && isSquare(n)) {
        return false;
      }
      d = d > 0 ? -(d + 2) : -(d - 2);
    }
    return m.isStrongLucasProbablePrime(d);
  }

  /**
   * Primality test of <code>|n|</code>: deterministic if <code>n</code> fits into a
   * <code>long</code>, otherwise {@link java.math.BigInteger#isProbablePrime(int)}.
   *
   * @param n integer to test
   * @param certainty certainty for numbers with 64 or more bits
   * @return <code>true</code> if <code>|n|</code> is (probably) prime
   */
  public static boolean isProbablePrime(java.math.BigInteger n, int certainty) {
    if (n.bitLength() < 64) {
      // Math.abs(Long.MIN_VALUE) stays negative, and 2^63 isn't prime
      return isPrime(Math.abs(n.longValue()));
    }
    return n.isProbablePrime(certainty);
  }

  private static int fj32Hash(int n) {
    long h = n & 0xFFFFFFFFL;
    h = ((h >>> 16) ^ h) * 0x45d9f3bL;
    h = ((h >>> 16) ^ h) * 0x45d9f3bL;
    return (int) (((h >>> 16) ^ h) & 255);
  }

  /** Strong probable-prime test of odd <code>n &gt; 2</code> to base <code>a &gt; 0</code>. */
  private static boolean isStrongProbablePrime(int n, int a) {
    long nl = n;
    long b = a % nl;
    if (b == 0) {
      return true;
    }
    int d = n - 1;
    int s = Integer.numberOfTrailingZeros(d);
    d >>>= s;
    long x = 1;
    while (true) {
      if ((d & 1) != 0) {
        x = x * b % nl;
      }
      d >>>= 1;
      if (d == 0) {
        break;
      }
      b = b * b % nl;
    }
    if (x == 1 || x == nl - 1) {
      return true;
    }
    while (--s > 0) {
      x = x * x % nl;
      if (x == nl - 1) {
        return true;
      }
    }
    return false;
  }

  /** Jacobi symbol <code>(a/n)</code> for odd <code>n &gt; 0</code>. */
  private static int jacobi(long a, long n) {
    a %= n;
    if (a < 0) {
      a += n;
    }
    int t = 1;
    while (a != 0) {
      while ((a & 1) == 0) {
        a >>>= 1;
        long r = n & 7;
        if (r == 3 || r == 5) {
          t = -t;
        }
      }
      long tmp = a;
      a = n;
      n = tmp;
      if ((a & 3) == 3 && (n & 3) == 3) {
        t = -t;
      }
      a %= n;
    }
    return n == 1 ? t : 0;
  }

  private static boolean isSquare(long n) {
    long r = Math.min((long) Math.sqrt((double) n), 3037000499L); // floor(sqrt(Long.MAX_VALUE))
    while (r * r > n) {
      r--;
    }
    while (r < 3037000499L && (r + 1) * (r + 1) <= n) {
      r++;
    }
    return r * r == n;
  }

  /** Unsigned high 64 bits of the 128-bit product <code>a * b</code>. */
  private static long unsignedMultiplyHigh(long a, long b) {
    return Math.multiplyHigh(a, b) + ((a >> 63) & b) + ((b >> 63) & a);
  }

  /** Montgomery arithmetic modulo an odd <code>n</code>, <code>2^31 &lt; n &lt; 2^63</code>. */
  private static final class Montgomery {
    final long n;
    /** <code>n^(-1) mod 2^64</code> */
    final long nInverse;
    /** <code>2^64 mod n</code>, the Montgomery form of 1 */
    final long one;
    /** <code>2^128 mod n</code> */
    final long r2;

    Montgomery(long n) {
      this.n = n;
      long inv = n; // correct to 3 bits, each Newton step doubles the precision
      for (int i = 0; i < 5; i++) {
        inv *= 2 - n * inv;
      }
      nInverse = inv;
      one = Long.remainderUnsigned(-n, n);
      long r = one;
      for (int i = 0; i < 64; i++) {
        r <<= 1;
        if (r < 0 || r >= n) {
          r -= n;
        }
      }
      r2 = r;
    }

    /** Montgomery product of <code>0 &lt;= a, b &lt; n</code>. */
    long mul(long a, long b) {
      long m = a * b * nInverse;
      long t = Math.multiplyHigh(a, b) - unsignedMultiplyHigh(m, n);
      return t < 0 ? t + n : t;
    }

    /** Montgomery form of <code>a &gt;= 0</code>. */
    long toMontgomery(long a) {
      return mul(a % n, r2);
    }

    long add(long a, long b) {
      long s = a + b - n;
      return s < 0 ? s + n : s;
    }

    long sub(long a, long b) {
      long s = a - b;
      return s < 0 ? s + n : s;
    }

    long half(long a) {
      return (a & 1) == 0 ? a >>> 1 : (a >>> 1) + (n >>> 1) + 1;
    }

    boolean isStrongProbablePrime(long a) {
      long b = toMontgomery(a);
      if (b == 0) {
        return true;
      }
      long minusOne = n - one;
      long d = n - 1;
      int s = Long.numberOfTrailingZeros(d);
      d >>>= s;
      long x = one;
      while (true) {
        if ((d & 1) != 0) {
          x = mul(x, b);
        }
        d >>>= 1;
        if (d == 0) {
          break;
        }
        b = mul(b, b);
      }
      if (x == one || x == minusOne) {
        return true;
      }
      while (--s > 0) {
        x = mul(x, x);
        if (x == minusOne) {
          return true;
        }
      }
      return false;
    }

    /** Strong Lucas probable-prime test with <code>P = 1, Q = (1 - D) / 4</code>. */
    boolean isStrongLucasProbablePrime(long discriminant) {
      long p = one;
      long qSigned = (1 - discriminant) / 4;
      long q = qSigned >= 0 ? toMontgomery(qSigned) : sub(0, toMontgomery(-qSigned));
      long dm = discriminant >= 0 ? toMontgomery(discriminant) : sub(0, toMontgomery(-discriminant));
      long k = n + 1; // treated as unsigned
      int s = Long.numberOfTrailingZeros(k);
      k >>>= s;
      // left-to-right binary ladder starting at U_1 = 1, V_1 = P
      long u = one;
      long v = p;
      long qk = q;
      for (int i = 62 - Long.numberOfLeadingZeros(k); i >= 0; i--) {
        u = mul(u, v);
        v = sub(mul(v, v), add(qk, qk));
        qk = mul(qk, qk);
        if (((k >>> i) & 1) != 0) {
          long u1 = half(add(mul(p, u), v));
          v = half(add(mul(dm, u), mul(p, v)));
          u = u1;
          qk = mul(qk, q);
        }
      }
      if (u == 0 || v == 0) {
        return true;
      }
      for (int r = 1; r < s; r++) {
        v = sub(mul(v, v), add(qk, qk));
        if (v == 0) {
          return true;
        }
        qk = mul(qk, qk);
      }
      return false;
    }
  }
}
