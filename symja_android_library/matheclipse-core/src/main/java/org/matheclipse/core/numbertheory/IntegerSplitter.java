package org.matheclipse.core.numbertheory;

import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntFunction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.TimeoutException;

/**
 * Splits a composite integer into two nontrivial factors.
 *
 * <p>
 * The methods are tried from the cheapest to the most general one:
 *
 * <ol>
 * <li>trial division for numbers below <code>2^40</code>,
 * <li>perfect powers,
 * <li>a few steps of Fermat's method, which finds two factors close to the square root,
 * <li>Brent's variant of Pollard's rho method with a bounded number of steps, which finds factors
 * up to about <code>10^9</code>,
 * <li>Pollard's <code>p-1</code> method with a small bound, which finds a prime factor
 * <code>p</code> whose <code>p-1</code> has only small prime factors, whatever the size of
 * <code>p</code>,
 * <li>for numbers of {@link Siqs#MIN_DIGITS} to {@link Siqs#MAX_DIGITS} digits: Lenstra's elliptic
 * curve method for the factors with up to <code>2/7</code> of the digits of the number, then the
 * self-initializing quadratic sieve {@link Siqs}, whose running time depends only on the size of
 * the number,
 * <li>the <code>p-1</code> method with a large bound, then the elliptic curve method on Montgomery
 * curves with Suyama's parametrization, with a new curve for every attempt and a rising smoothness
 * bound. Stage 1 multiplies by the prime powers with {@link LucasChains}. It runs until a factor is
 * found or the thread is interrupted.
 * </ol>
 *
 * <p>
 * See R. P. Brent, "An improved Monte Carlo factorization algorithm", BIT 20 (1980); P. L.
 * Montgomery, "Speeding the Pollard and elliptic curve methods of factorization", Math. Comp. 48
 * (1987).
 *
 * <p>
 * All arithmetic modulo <code>n</code> is done with {@link MontgomeryLimbs}. The tables of the
 * smoothness levels (primes, stage 1 programs, stage 2 plans) are built on first use outside of
 * any lock, can be interrupted, and are held by soft references so that the garbage collector can
 * reclaim them.
 */
final class IntegerSplitter {

  private static final BigInteger ONE = BigInteger.ONE;
  private static final BigInteger TWO = BigInteger.valueOf(2);
  private static final BigInteger THREE = BigInteger.valueOf(3);
  private static final BigInteger FIVE = BigInteger.valueOf(5);

  /** Numbers with at most this many bits are split by trial division. */
  private static final int TRIAL_DIVISION_BITS = 40;

  /** The number of steps after which the bounded rho method gives up. */
  private static final long RHO_STEPS = 1L << 16;

  /** The number of rho steps whose differences are multiplied before one gcd is taken. */
  private static final int RHO_BATCH = 128;

  /** The number of steps of Fermat's method. */
  private static final int FERMAT_STEPS = 32;

  /** Smoothness bounds of the <code>p-1</code> method. */
  private static final int PM1_B1 = 100_000;

  private static final int PM1_B2 = 2_000_000;

  /** The stage 1 bound of the <code>p+1</code> method; stage 2 goes to 100 times of it. */
  private static final int PP1_B1 = 100_000;

  /**
   * The start values <code>V_1</code> of the <code>p+1</code> method as fractions: 3 and 5, with
   * the discriminants <code>V_1^2-4 = 5</code> and <code>21</code>. Each one finds a factor with a
   * smooth <code>p+1</code> for about half of the primes; the discriminants -3 and -1 of other
   * common choices are residues of all the primes <code>p = 1 (mod 12)</code>, so they miss the
   * primes whose <code>p+1</code> is twice a product of primes greater than 3.
   */
  private static final int[][] PP1_SEEDS = {{3, 1}, {5, 1}};

  /** Smoothness bounds of the short <code>p-1</code> probe after the rho method. */
  private static final int PM1_PROBE_B1 = 2_000;

  private static final int PM1_PROBE_B2 = 50_000;

  /**
   * Numbers with at least this many digits get the <code>p-1</code> method with the large bounds
   * before the quadratic sieve; for smaller ones it costs a noticeable part of the sieve's time.
   */
  private static final int PM1_SIQS_DIGITS = 70;

  /** The number of primes whose prime powers are combined into one exponent in stage 1. */
  private static final int PM1_BLOCK = 1024;

  /**
   * Stage 1 bounds of the elliptic curve method and the number of curves per bound. Each level is
   * suited to prime factors of 15, 20, 25, 30 and 35 digits; the last level is repeated.
   */
  private static final int[] ECM_B1 = {2_000, 11_000, 50_000, 250_000, 1_000_000};

  private static final int[] ECM_CURVES = {25, 90, 300, 700, 1800};

  /** The number of digits of the prime factors each level of {@link #ECM_B1} is suited to. */
  private static final int[] ECM_DIGITS = {15, 20, 25, 30, 35};

  /** Numbers with at least this many digits get the elliptic curve method on several threads. */
  private static final int ECM_PARALLEL_DIGITS = 45;

  /** Numbers up to this many digits get the first level of the elliptic curve method. */
  private static final int ECM_SMALL_DIGITS = 25;

  /** Stage 2 bound of the elliptic curve method as a multiple of the stage 1 bound. */
  private static final int ECM_B2_FACTOR = 100;

  /**
   * The number of giant steps of stage 2 whose points are converted to affine coordinates with
   * one modular inversion.
   */
  private static final int GIANT_BLOCK = 64;

  /** The giant step of the elliptic curve method's stage 2: <code>2*3*5*7*11</code>. */
  private static final int D = 2310;

  /** The first Suyama parameter; 0, 1, 3 and 5 give singular curves. */
  private static final long FIRST_SIGMA = 6;

  private static final ConcurrentHashMap<Integer, SoftReference<byte[]>> STAGE1_PROGRAMS =
      new ConcurrentHashMap<>();

  private static final ConcurrentHashMap<Integer, SoftReference<Stage2Plan>> STAGE2_PLANS =
      new ConcurrentHashMap<>();

  /** The primes up to at least <code>limit</code>, see {@link #primesUpTo(int)}. */
  private static final class PrimeTable {
    final int limit;
    final int[] primes;

    PrimeTable(int limit, int[] primes) {
      this.limit = limit;
      this.primes = primes;
    }
  }

  private static volatile SoftReference<PrimeTable> primeTable = new SoftReference<>(null);

  private IntegerSplitter() {}

  /**
   * Determine a nontrivial divisor of the composite number <code>n</code>.
   *
   * @param n a composite number greater than <code>3</code>
   * @return a divisor <code>d</code> with <code>1 &lt; d &lt; n</code>, which isn't necessarily
   *         prime, or {@link BigInteger#ONE} if <code>n</code> is a prime below <code>2^40</code>
   * @throws TimeoutException if the thread is interrupted
   */
  static BigInteger split(BigInteger n) {
    if (!n.testBit(0)) {
      return TWO;
    }
    if (n.bitLength() <= TRIAL_DIVISION_BITS) {
      return trialDivision(n.longValue());
    }
    BigInteger[] power = Primality.perfectPower(n, 2);
    if (power[1].compareTo(ONE) > 0) {
      return power[0];
    }
    BigInteger divisor = fermat(n, FERMAT_STEPS);
    if (isProperDivisor(divisor, n)) {
      return divisor;
    }
    if (n.bitLength() <= 63) {
      // the rho method in 64 bit arithmetic with up to 19 polynomials and 2^23 steps each
      long factor = Siqs.rhoSplit(n.longValue());
      if (factor > 1) {
        return BigInteger.valueOf(factor);
      }
    }
    divisor = brentRho(n, ONE, RHO_STEPS);
    if (isProperDivisor(divisor, n)) {
      return divisor;
    }
    divisor = pMinus1(n, PM1_PROBE_B1, PM1_PROBE_B2);
    if (isProperDivisor(divisor, n)) {
      return divisor;
    }
    final int digits = decimalDigits(n);
    // the elliptic curve method has run the levels for the factors up to this many digits
    int ecmDigits = 0;
    boolean pMinus1Done = false;
    if (digits >= Siqs.MIN_DIGITS && digits <= Siqs.MAX_DIGITS) {
      // the first level finds the factors of a small number faster than the sieve
      int ecmBeforeSieve = Math.max(2 * digits / 7, digits <= ECM_SMALL_DIGITS ? ECM_DIGITS[0] : 0);
      divisor = ecm(n, ecmDigits, ecmBeforeSieve);
      if (isProperDivisor(divisor, n)) {
        return divisor;
      }
      ecmDigits = ecmBeforeSieve;
      if (digits >= PM1_SIQS_DIGITS) {
        divisor = pMinus1(n, PM1_B1, PM1_B2);
        if (isProperDivisor(divisor, n)) {
          return divisor;
        }
        pMinus1Done = true;
      }
      divisor = Siqs.factor(n);
      if (divisor != null && isProperDivisor(divisor, n) && n.mod(divisor).signum() == 0) {
        return divisor;
      }
    }
    if (!pMinus1Done) {
      divisor = pMinus1(n, PM1_B1, PM1_B2);
      if (isProperDivisor(divisor, n)) {
        return divisor;
      }
    }
    // the first levels of the elliptic curve method are cheaper than the p+1 method
    divisor = ecm(n, ecmDigits, ECM_DIGITS[1]);
    if (isProperDivisor(divisor, n)) {
      return divisor;
    }
    ecmDigits = Math.max(ecmDigits, ECM_DIGITS[1]);
    for (int[] seed : PP1_SEEDS) {
      divisor = pPlus1(n, seed[0], seed[1], PP1_B1);
      if (isProperDivisor(divisor, n)) {
        return divisor;
      }
    }
    return ecm(n, ecmDigits, Integer.MAX_VALUE);
  }

  /** The number of decimal digits of the positive <code>n</code>. */
  static int decimalDigits(BigInteger n) {
    int digits = (int) (n.bitLength() * 0.3010299956639812) + 1;
    return n.compareTo(BigInteger.TEN.pow(digits - 1)) < 0 ? digits - 1 : digits;
  }

  /**
   * Fermat's method: <code>a^2 - n</code> for <code>a = ceil(sqrt(n)), ...</code> is a square
   * <code>b^2</code> for <code>n = (a-b)*(a+b)</code>. The first steps find two factors whose
   * difference is at most about <code>n^(1/4)</code> times the square root of the number of steps.
   *
   * @return a divisor greater than <code>1</code>, which can be <code>n</code> itself, or
   *         {@link BigInteger#ONE}
   */
  static BigInteger fermat(BigInteger n, int steps) {
    BigInteger a = n.sqrt();
    if (a.multiply(a).equals(n)) {
      return a;
    }
    a = a.add(ONE);
    BigInteger b2 = a.multiply(a).subtract(n);
    // the increment 2*a+1 of a^2 to (a+1)^2
    BigInteger increment = a.shiftLeft(1).add(ONE);
    for (int i = 0; i < steps; i++) {
      // squares are 0, 1, 4, 9, 16, 17, 25, 33, 36, 41, 49, 57 mod 64
      if (((0x0202021202030213L >>> (b2.intValue() & 63)) & 1) != 0) {
        BigInteger b = b2.sqrt();
        if (b.multiply(b).equals(b2)) {
          return a.subtract(b);
        }
      }
      b2 = b2.add(increment);
      a = a.add(ONE);
      increment = increment.add(TWO);
    }
    return ONE;
  }

  private static boolean isProperDivisor(BigInteger divisor, BigInteger n) {
    return divisor.compareTo(ONE) > 0 && divisor.compareTo(n) < 0;
  }

  private static void checkInterrupted() {
    if (Thread.currentThread().isInterrupted()) {
      throw TimeoutException.TIMED_OUT;
    }
  }

  private static BigInteger trialDivision(long n) {
    if (n % 3 == 0) {
      return THREE;
    }
    for (long d = 5; d * d <= n; d += 6) {
      if (n % d == 0) {
        return BigInteger.valueOf(d);
      }
      if (n % (d + 2) == 0) {
        return BigInteger.valueOf(d + 2);
      }
    }
    return ONE;
  }

  /**
   * Brent's variant of Pollard's rho method with the polynomial <code>x^2+c</code>, which takes one
   * gcd per {@link #RHO_BATCH} steps.
   *
   * @param n an odd composite number
   * @param c the constant of the polynomial
   * @param maxSteps the number of steps after which the search is given up
   * @return a divisor greater than <code>1</code>, which can be <code>n</code> itself, or
   *         {@link BigInteger#ONE} if no divisor was found within <code>maxSteps</code>
   * @throws TimeoutException if the thread is interrupted
   */
  static BigInteger brentRho(BigInteger n, BigInteger c, long maxSteps) {
    return brentRho(new MontgomeryLimbs(n), c.mod(n), maxSteps);
  }

  /** {@link #brentRho(BigInteger, BigInteger, long)} with a Montgomery multiplier modulo n. */
  private static BigInteger brentRho(MontgomeryLimbs m, BigInteger c, long maxSteps) {
    final long[] cm = m.newElement();
    m.toMontgomery(c, cm);
    long[] y = m.newElement();
    m.toMontgomery(TWO, y);
    final long[] x = y.clone();
    final long[] ys = y.clone();
    final long[] q = m.one.clone();
    final long[] difference = m.newElement();
    BigInteger g = ONE;
    long steps = 0;
    for (long r = 1; g.equals(ONE); r <<= 1) {
      if (steps >= maxSteps) {
        return ONE;
      }
      System.arraycopy(y, 0, x, 0, m.k);
      for (long i = 0; i < r; i++) {
        m.sqr(y, y);
        m.add(y, cm, y);
      }
      for (long k = 0; k < r && g.equals(ONE); k += RHO_BATCH) {
        checkInterrupted();
        System.arraycopy(y, 0, ys, 0, m.k);
        long batch = Math.min(RHO_BATCH, r - k);
        for (long i = 0; i < batch; i++) {
          m.sqr(y, y);
          m.add(y, cm, y);
          m.sub(x, y, difference);
          m.mul(q, difference, q);
        }
        g = m.gcd(q);
      }
      steps += 2 * r;
    }
    if (g.equals(m.modulus)) {
      // the batch contains a multiple of every factor; repeat it one step at a time
      do {
        m.sqr(ys, ys);
        m.add(ys, cm, ys);
        m.sub(x, ys, difference);
        g = m.gcd(difference);
      } while (g.equals(ONE));
    }
    return g;
  }

  /**
   * Pollard's <code>p-1</code> method with base <code>3</code>. Stage 1 raises the base to all
   * prime powers up to <code>b1</code>; stage 2 tries one more prime up to <code>b2</code>.
   *
   * @return a divisor greater than <code>1</code>, which can be <code>n</code> itself, or
   *         {@link BigInteger#ONE}
   */
  static BigInteger pMinus1(BigInteger n, int b1, int b2) {
    int[] primes = primesUpTo(b2);
    int b1Count = countPrimesUpTo(primes, b1);
    int b2Count = countPrimesUpTo(primes, b2);
    BigInteger a = THREE;
    for (int start = 0; start < b1Count; start += PM1_BLOCK) {
      checkInterrupted();
      int end = Math.min(b1Count, start + PM1_BLOCK);
      List<BigInteger> powers = new ArrayList<>(end - start);
      for (int i = start; i < end; i++) {
        powers.add(BigInteger.valueOf(primePower(primes[i], b1)));
      }
      BigInteger next = a.modPow(product(powers, 0, powers.size()), n);
      BigInteger g = next.subtract(ONE).gcd(n);
      if (g.equals(ONE)) {
        a = next;
        continue;
      }
      if (!g.equals(n)) {
        return g;
      }
      // every factor was found in this block; repeat it one prime at a time
      for (int i = start; i < end; i++) {
        BigInteger q = BigInteger.valueOf(primes[i]);
        for (long qk = primes[i]; qk <= b1; qk *= primes[i]) {
          a = a.modPow(q, n);
          g = a.subtract(ONE).gcd(n);
          if (!g.equals(ONE)) {
            return g;
          }
        }
      }
      return ONE;
    }

    if (b1Count >= b2Count) {
      return ONE;
    }
    return pMinus1Stage2(new MontgomeryLimbs(n), a, primes, b1Count, b2Count);
  }

  /**
   * Stage 2 of the <code>p-1</code> method: <code>a^q</code> for the consecutive primes
   * <code>q = primes[from..to-1]</code>, each from the previous one and a table of
   * <code>a^gap</code>.
   */
  private static BigInteger pMinus1Stage2(MontgomeryLimbs m, BigInteger a, int[] primes, int from,
      int to) {
    final BigInteger n = m.modulus;
    long[][] gapPowers = new long[128][];
    int previous = primes[from];
    long[] x = m.newElement();
    m.toMontgomery(a.modPow(BigInteger.valueOf(previous), n), x);
    long[] accumulated = m.newElement();
    m.sub(x, m.one, accumulated);
    long[] factor = m.newElement();
    long[] distantStep = m.newElement();
    for (int i = from + 1; i < to; i++) {
      int halfGap = (primes[i] - previous) >> 1;
      previous = primes[i];
      long[] step;
      if (halfGap < gapPowers.length && gapPowers[halfGap] != null) {
        step = gapPowers[halfGap];
      } else {
        step = halfGap < gapPowers.length ? m.newElement() : distantStep;
        m.toMontgomery(a.modPow(BigInteger.valueOf(2L * halfGap), n), step);
        if (halfGap < gapPowers.length) {
          gapPowers[halfGap] = step;
        }
      }
      m.mul(x, step, x);
      m.sub(x, m.one, factor);
      m.mul(accumulated, factor, accumulated);
      if ((i & 0xFFF) == 0) {
        checkInterrupted();
        BigInteger g = m.gcd(accumulated);
        if (!g.equals(ONE)) {
          return g;
        }
      }
    }
    return m.gcd(accumulated);
  }

  /**
   * Williams' <code>p+1</code> method with the Lucas sequence <code>V_k = a^k + a^(-k)</code>,
   * <code>V_1 = numerator/denominator</code>. It finds a prime factor <code>p</code> whose
   * <code>p+1</code> has only small prime factors if <code>V_1^2-4</code> is a quadratic
   * non-residue modulo <code>p</code>, and acts like the <code>p-1</code> method otherwise.
   * Stage 1 runs the {@link LucasChains} program of the bound <code>b1</code>; stage 2 takes the
   * pairs <code>(k, j)</code> of the elliptic curve method's {@link Stage2Plan} and multiplies the
   * differences <code>V_(k*D) - V_j</code>, which vanish modulo <code>p</code> for every prime
   * <code>k*D &plusmn; j</code> which completes the order.
   *
   * <p>
   * See H. C. Williams, "A p+1 method of factoring", Math. Comp. 39 (1982).
   *
   * @return a divisor greater than <code>1</code>, which can be <code>n</code> itself, or
   *         {@link BigInteger#ONE}
   */
  static BigInteger pPlus1(BigInteger n, int numerator, int denominator, int b1) {
    BigInteger d = BigInteger.valueOf(denominator);
    BigInteger g = d.gcd(n);
    if (!g.equals(ONE)) {
      return g;
    }
    MontgomeryLimbs m = new MontgomeryLimbs(n);
    LucasLimbs lucas = new LucasLimbs(m);
    long[] v = m.newElement();
    m.toMontgomery(BigInteger.valueOf(numerator).multiply(d.modInverse(n)).mod(n), v);
    LucasChains.run(lucas, v, stage1Program(b1));
    long[] difference = m.newElement();
    m.sub(v, lucas.two, difference);
    g = m.gcd(difference);
    if (!g.equals(ONE)) {
      return g;
    }
    Stage2Plan plan = stage2Plan(b1);
    final int babyCount = plan.babies.length;
    // V_j for the odd j < D/2: V_(j+2) = V_j*V_2 - V_(j-2)
    long[][] baby = new long[babyCount][];
    long[] v2 = m.newElement();
    lucas.dbl(v, v2);
    long[] before = v.clone();
    long[] current = v.clone();
    long[] next = m.newElement();
    int index = 0;
    for (int j = 1; j < D / 2 && index < babyCount; j += 2) {
      if (j > 1) {
        lucas.add(current, v2, before, next);
        long[] recycled = before;
        before = current;
        current = next;
        next = recycled;
      }
      if (plan.babies[index] == j) {
        baby[index++] = current.clone();
      }
    }
    // V_(k*D) for k = kFirst, kFirst+1, ...: V_((k+1)*D) = V_(k*D)*V_D - V_((k-1)*D)
    long[] giant = lucas.multiply(v, D);
    long[] previous = lucas.multiply(v, (long) (plan.kFirst - 1) * D);
    long[] gk = lucas.multiply(v, (long) plan.kFirst * D);
    long[] accumulated = m.one.clone();
    long[] factor = m.newElement();
    for (int k = plan.kFirst; k <= plan.kLast; k++) {
      final int g0 = k - plan.kFirst;
      for (int p = plan.pairStart[g0]; p < plan.pairStart[g0 + 1]; p++) {
        m.sub(gk, baby[plan.pairBabies[p] & 0xFF], factor);
        m.mul(accumulated, factor, accumulated);
      }
      lucas.add(gk, giant, previous, next);
      long[] recycled = previous;
      previous = gk;
      gk = next;
      next = recycled;
      if ((k & 0xFF) == 0) {
        checkInterrupted();
        g = m.gcd(accumulated);
        if (!g.equals(ONE)) {
          return g;
        }
      }
    }
    return m.gcd(accumulated);
  }

  /**
   * The Lucas sequence <code>V_k</code> as a {@link LucasChains.DifferentialCurve}:
   * <code>V_(2k) = V_k^2 - 2</code> and <code>V_(a+b) = V_a*V_b - V_(a-b)</code>, in
   * {@link MontgomeryLimbs} arithmetic.
   */
  static final class LucasLimbs implements LucasChains.DifferentialCurve<long[]> {
    final MontgomeryLimbs m;

    /** the Montgomery form of <code>2</code> */
    final long[] two;

    private final long[] t;

    LucasLimbs(MontgomeryLimbs m) {
      this.m = m;
      two = m.newElement();
      m.add(m.one, m.one, two);
      t = m.newElement();
    }

    @Override
    public long[] newPoint() {
      return m.newElement();
    }

    @Override
    public void copy(long[] from, long[] to) {
      System.arraycopy(from, 0, to, 0, m.k);
    }

    @Override
    public void dbl(long[] p, long[] out) {
      m.sqr(p, out);
      m.sub(out, two, out);
    }

    @Override
    public void add(long[] p, long[] q, long[] difference, long[] out) {
      m.mul(p, q, t);
      m.sub(t, difference, out);
    }

    /** <code>V_(k*j)</code> from <code>V_j = v</code> by the ladder of <code>V_i, V_(i+1)</code> */
    long[] multiply(long[] v, long k) {
      if (k == 0) {
        return two.clone();
      }
      long[] r0 = v.clone();
      long[] r1 = m.newElement();
      dbl(v, r1);
      for (int i = 62 - Long.numberOfLeadingZeros(k); i >= 0; i--) {
        if ((k >>> i & 1L) != 0) {
          add(r1, r0, v, r0);
          dbl(r1, r1);
        } else {
          add(r0, r1, v, r1);
          dbl(r0, r0);
        }
      }
      return r0;
    }
  }

  /**
   * Lenstra's elliptic curve method. Every attempt uses a new curve, with the smoothness bound
   * rising after a number of curves.
   *
   * @param doneDigits the levels for prime factors of at most this many digits have already run
   * @param maxDigits the levels of {@link #ECM_B1} for prime factors of at most this many digits
   *        are run; the last level is repeated if it is reached
   * @return a divisor <code>d</code> with <code>1 &lt; d &lt; n</code>, or {@link BigInteger#ONE}
   *         if the levels up to <code>maxDigits</code> found none
   * @throws TimeoutException if the thread is interrupted
   */
  static BigInteger ecm(BigInteger n, int doneDigits, int maxDigits) {
    if (ECM_DIGITS[0] > maxDigits) {
      return ONE;
    }
    long firstCurve = 0;
    for (int level = 0; level < ECM_DIGITS.length - 1 && ECM_DIGITS[level] <= doneDigits; level++) {
      firstCurve += ECM_CURVES[level];
    }
    int threads = decimalDigits(n) >= ECM_PARALLEL_DIGITS ? workerThreads() : 1;
    if (threads > 1) {
      int granted = acquireWorkers(threads);
      try {
        if (granted > 1) {
          return ecmParallel(n, firstCurve, maxDigits, granted);
        }
      } finally {
        releaseWorkers(granted);
      }
    }
    MontgomeryLimbs m = new MontgomeryLimbs(n);
    for (long curve = firstCurve;; curve++) {
      int level = ecmLevel(curve);
      if (ECM_DIGITS[level] > maxDigits) {
        return ONE;
      }
      checkInterrupted();
      int b1 = ECM_B1[level];
      BigInteger divisor = ecmCurve(m, FIRST_SIGMA + curve, stage1Program(b1), stage2Plan(b1));
      if (isProperDivisor(divisor, n)) {
        return divisor;
      }
    }
  }

  /**
   * The level of {@link #ECM_B1} of the curve number <code>curve</code>: the levels have
   * {@link #ECM_CURVES} curves each, and the last level is repeated.
   */
  private static int ecmLevel(long curve) {
    for (int level = 0; level < ECM_CURVES.length - 1; level++) {
      if (curve < ECM_CURVES[level]) {
        return level;
      }
      curve -= ECM_CURVES[level];
    }
    return ECM_CURVES.length - 1;
  }

  /**
   * {@link #ecm(BigInteger, int, int)} with the curves on <code>threads</code> worker threads. The
   * curves and their parameters are the same as on one thread; the calling thread waits for the
   * first proper divisor, and when it is interrupted it stops the workers before it throws the
   * timeout.
   */
  private static BigInteger ecmParallel(BigInteger n, long firstCurve, int maxDigits,
      int threads) {
    final AtomicLong nextCurve = new AtomicLong(firstCurve);
    final AtomicReference<BigInteger> found = new AtomicReference<>();
    final AtomicReference<Throwable> failure = new AtomicReference<>();
    final AtomicInteger running = new AtomicInteger(threads);
    final Object signal = new Object();
    Thread[] workers = new Thread[threads];
    for (int i = 0; i < threads; i++) {
      workers[i] = Config.THREAD_FACTORY.newThread(() -> {
        try {
          MontgomeryLimbs m = new MontgomeryLimbs(n);
          while (found.get() == null) {
            long curve = nextCurve.getAndIncrement();
            int level = ecmLevel(curve);
            if (ECM_DIGITS[level] > maxDigits) {
              break;
            }
            int b1 = ECM_B1[level];
            BigInteger divisor =
                ecmCurve(m, FIRST_SIGMA + curve, stage1Program(b1), stage2Plan(b1));
            if (isProperDivisor(divisor, n)) {
              found.compareAndSet(null, divisor);
            }
          }
        } catch (TimeoutException e) {
          // stopped
        } catch (Throwable t) {
          failure.compareAndSet(null, t);
        } finally {
          running.decrementAndGet();
          synchronized (signal) {
            signal.notifyAll();
          }
        }
      });
      workers[i].setDaemon(true);
      workers[i].start();
    }
    try {
      synchronized (signal) {
        while (found.get() == null && running.get() > 0 && failure.get() == null) {
          try {
            signal.wait(20);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw TimeoutException.TIMED_OUT;
          }
          checkInterrupted();
        }
      }
      if (found.get() == null && failure.get() != null) {
        Throwable t = failure.get();
        if (t instanceof Error) {
          throw (Error) t;
        }
        throw t instanceof RuntimeException ? (RuntimeException) t : new IllegalStateException(t);
      }
      return found.get() == null ? ONE : found.get();
    } finally {
      found.compareAndSet(null, ONE);
      stopWorkers(workers);
    }
  }

  /**
   * The number of worker threads of the parallel methods: {@link Config#FACTOR_INTEGER_THREADS},
   * but one processor is left to the calling thread, and <code>1</code> if the platform has no
   * threads.
   */
  static int workerThreads() {
    if (Config.JAS_NO_THREADS || Config.TIMECONSTRAINED_NO_THREAD) {
      return 1;
    }
    return Math.max(1,
        Math.min(Config.FACTOR_INTEGER_THREADS, Runtime.getRuntime().availableProcessors() - 1));
  }

  /**
   * The worker threads all factorizations together may run, so that concurrent evaluations, as on
   * a server, don't start a set of threads each.
   */
  private static final Semaphore WORKER_PERMITS =
      new Semaphore(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));

  /**
   * Take up to <code>wanted</code> of the free worker threads, without waiting.
   *
   * @return the number taken, which {@link #releaseWorkers(int)} gives back
   */
  static int acquireWorkers(int wanted) {
    int granted = 0;
    while (granted < wanted && WORKER_PERMITS.tryAcquire()) {
      granted++;
    }
    return granted;
  }

  /** Give back the worker threads of {@link #acquireWorkers(int)}. */
  static void releaseWorkers(int granted) {
    if (granted > 0) {
      WORKER_PERMITS.release(granted);
    }
  }

  /** Interrupt the <code>workers</code> and wait until all of them have ended. */
  static void stopWorkers(Thread[] workers) {
    for (Thread worker : workers) {
      worker.interrupt();
    }
    boolean interrupted = false;
    for (Thread worker : workers) {
      while (worker.isAlive()) {
        try {
          worker.join();
        } catch (InterruptedException e) {
          interrupted = true;
        }
      }
    }
    if (interrupted) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Run both stages of the elliptic curve method on the curve for the Suyama parameter
   * <code>sigma</code>.
   *
   * @return the gcd found, which can be <code>1</code> or <code>n</code>
   */
  static BigInteger ecmCurve(MontgomeryLimbs m, long sigma, byte[] program, Stage2Plan plan) {
    BigInteger[] start = suyama(m.modulus, sigma);
    if (start.length == 1) {
      return start[0];
    }
    CurveLimbs curve = new CurveLimbs(m, start[2]);
    long[][] q = stage1(curve, start, program);
    BigInteger g = m.gcd(q[1]);
    if (!g.equals(ONE)) {
      return g;
    }
    return stage2(curve, q, plan);
  }

  /**
   * Stage 1 of the elliptic curve method: the start point multiplied by the stage 1
   * <code>program</code>.
   *
   * @param start <code>{X, Z, (A+2)/4}</code> from {@link #suyama(BigInteger, long)}
   * @return the point as Montgomery forms <code>{X, Z}</code>
   */
  static long[][] stage1(CurveLimbs curve, BigInteger[] start, byte[] program) {
    long[][] q = curve.newPoint();
    curve.m.toMontgomery(start[0], q[0]);
    curve.m.toMontgomery(start[1], q[1]);
    LucasChains.run(curve, q, program);
    return q;
  }

  /**
   * The start point <code>(X:Z)</code> and <code>(A+2)/4</code> of the Montgomery curve for
   * Suyama's parameter <code>sigma</code>.
   *
   * @return <code>{X, Z, (A+2)/4}</code>, or <code>{g}</code> if the denominator of
   *         <code>(A+2)/4</code> has the gcd <code>g &gt; 1</code> with <code>n</code>
   */
  static BigInteger[] suyama(BigInteger n, long sigma) {
    BigInteger s = BigInteger.valueOf(sigma);
    BigInteger u = s.multiply(s).subtract(FIVE).mod(n);
    BigInteger v = s.shiftLeft(2).mod(n);
    BigInteger u3 = u.multiply(u).multiply(u).mod(n);
    BigInteger v3 = v.multiply(v).multiply(v).mod(n);
    // (A+2)/4 = (v-u)^3 * (3*u+v) / (16 * u^3 * v)
    BigInteger vu = v.subtract(u);
    BigInteger numerator =
        vu.multiply(vu).mod(n).multiply(vu).multiply(u.multiply(THREE).add(v)).mod(n);
    BigInteger denominator = u3.multiply(v).shiftLeft(4).mod(n);
    BigInteger g = denominator.gcd(n);
    if (!g.equals(ONE)) {
      return new BigInteger[] {g};
    }
    return new BigInteger[] {u3, v3, numerator.multiply(denominator.modInverse(n)).mod(n)};
  }

  /**
   * The standard continuation of the elliptic curve method: for every prime
   * <code>p = k*D +- j</code> in <code>(B1, B2]</code> multiply <code>x([k*D]Q) - x([j]Q)</code>
   * into an accumulator. Both x-coordinates are affine; the giant points are converted in blocks
   * of {@link #GIANT_BLOCK} with one modular inversion each.
   *
   * @param q the result of stage 1 as Montgomery forms <code>{X, Z}</code>
   * @return the gcd found, which can be <code>1</code> or <code>n</code>
   */
  static BigInteger stage2(CurveLimbs curve, long[][] q, Stage2Plan plan) {
    final MontgomeryLimbs m = curve.m;
    final int babyCount = plan.babies.length;

    BigInteger[] babyX = new BigInteger[babyCount];
    BigInteger[] babyZ = new BigInteger[babyCount];
    long[][] q2 = curve.newPoint();
    curve.dbl(q, q2);
    long[][] before = curve.copy(q);
    long[][] current = curve.copy(q);
    long[][] next = curve.newPoint();
    int index = 0;
    for (int j = 1; j < D / 2; j += 2) {
      if (j > 1) {
        curve.add(current, q2, before, next);
        long[][] recycled = before;
        before = current;
        current = next;
        next = recycled;
      }
      if (index < babyCount && plan.babies[index] == j) {
        babyX[index] = MontgomeryLimbs.toBigInteger(current[0]);
        babyZ[index] = MontgomeryLimbs.toBigInteger(current[1]);
        index++;
      }
    }
    // X*R / (Z*R) is the affine x-coordinate itself
    BigInteger[] babyAffine = new BigInteger[babyCount];
    BigInteger g = affine(babyX, babyZ, m.modulus, babyAffine);
    if (!g.equals(ONE)) {
      return g;
    }
    long[][] baby = new long[babyCount][];
    for (int i = 0; i < babyCount; i++) {
      baby[i] = m.newElement();
      m.toMontgomery(babyAffine[i], baby[i]);
    }

    long[][] giant = curve.newPoint();
    curve.multiply(q, BigInteger.valueOf(D), giant);
    long[][] gk = curve.newPoint();
    curve.multiply(q, BigInteger.valueOf((long) plan.kFirst * D), gk);
    long[][] gk1 = curve.newPoint();
    curve.multiply(q, BigInteger.valueOf((long) (plan.kFirst + 1) * D), gk1);
    long[][] gNext = curve.newPoint();
    long[] accumulated = m.one.clone();
    long[] factor = m.newElement();
    long[] inverse = m.newElement();
    long[][] giantX = new long[GIANT_BLOCK][];
    long[][] giantZ = new long[GIANT_BLOCK][];
    long[][] prefix = new long[GIANT_BLOCK][];
    for (int i = 0; i < GIANT_BLOCK; i++) {
      giantX[i] = m.newElement();
      giantZ[i] = m.newElement();
      prefix[i] = m.newElement();
    }
    for (int k = plan.kFirst; k <= plan.kLast; k += GIANT_BLOCK) {
      checkInterrupted();
      int count = Math.min(GIANT_BLOCK, plan.kLast - k + 1);
      for (int i = 0; i < count; i++) {
        System.arraycopy(gk[0], 0, giantX[i], 0, m.k);
        System.arraycopy(gk[1], 0, giantZ[i], 0, m.k);
        curve.add(gk1, giant, gk, gNext);
        long[][] recycled = gk;
        gk = gk1;
        gk1 = gNext;
        gNext = recycled;
      }
      // x = X/Z for the whole block with one inversion: prefix[i] = Z[0]*...*Z[i]
      System.arraycopy(giantZ[0], 0, prefix[0], 0, m.k);
      for (int i = 1; i < count; i++) {
        m.mul(prefix[i - 1], giantZ[i], prefix[i]);
      }
      g = m.gcd(prefix[count - 1]);
      if (!g.equals(ONE)) {
        return g;
      }
      m.inverse(prefix[count - 1], inverse);
      for (int i = count - 1; i > 0; i--) {
        // 1/Z[i] = inverse * prefix[i-1], then inverse = 1/(Z[0]*...*Z[i-1])
        m.mul(inverse, prefix[i - 1], factor);
        m.mul(giantX[i], factor, giantX[i]);
        m.mul(inverse, giantZ[i], inverse);
      }
      m.mul(giantX[0], inverse, giantX[0]);

      for (int i = 0, g0 = k - plan.kFirst; i < count; i++) {
        final long[] x = giantX[i];
        for (int p = plan.pairStart[g0 + i]; p < plan.pairStart[g0 + i + 1]; p++) {
          m.sub(x, baby[plan.pairBabies[p] & 0xFF], factor);
          m.mul(accumulated, factor, accumulated);
        }
      }
      g = m.gcd(accumulated);
      if (!g.equals(ONE)) {
        return g;
      }
    }
    return g;
  }

  /**
   * x-only arithmetic on the Montgomery curve <code>B*y^2 = x^3 + A*x^2 + x</code> modulo n in
   * {@link MontgomeryLimbs} arithmetic. A point <code>(X:Z)</code> is the array
   * <code>{X, Z}</code> of Montgomery forms. The output point of an operation may be one of its
   * inputs.
   */
  static final class CurveLimbs implements LucasChains.DifferentialCurve<long[][]> {
    final MontgomeryLimbs m;

    /** Montgomery form of <code>(A+2)/4</code> */
    private final long[] a24;

    private final long[] s;
    private final long[] d;
    private final long[] t;
    private final long[] u;
    private final long[] v;
    private final long[] w;

    CurveLimbs(MontgomeryLimbs m, BigInteger a24) {
      this.m = m;
      this.a24 = m.newElement();
      m.toMontgomery(a24, this.a24);
      s = m.newElement();
      d = m.newElement();
      t = m.newElement();
      u = m.newElement();
      v = m.newElement();
      w = m.newElement();
    }

    @Override
    public long[][] newPoint() {
      return new long[][] {m.newElement(), m.newElement()};
    }

    long[][] copy(long[][] p) {
      return new long[][] {p[0].clone(), p[1].clone()};
    }

    @Override
    public void copy(long[][] from, long[][] to) {
      System.arraycopy(from[0], 0, to[0], 0, m.k);
      System.arraycopy(from[1], 0, to[1], 0, m.k);
    }

    /** <code>out = [2]p</code> */
    @Override
    public void dbl(long[][] p, long[][] out) {
      final MontgomeryLimbs m = this.m;
      m.add(p[0], p[1], s);
      m.sqr(s, s);
      m.sub(p[0], p[1], d);
      m.sqr(d, d);
      m.sub(s, d, t);
      m.mul(a24, t, w);
      m.add(d, w, w);
      m.mul(t, w, out[1]);
      m.mul(s, d, out[0]);
    }

    /** <code>out = p+q</code> from <code>p</code>, <code>q</code> and <code>p-q</code> */
    @Override
    public void add(long[][] p, long[][] q, long[][] difference, long[][] out) {
      final MontgomeryLimbs m = this.m;
      m.sub(p[0], p[1], u);
      m.add(q[0], q[1], w);
      m.mul(u, w, u);
      m.add(p[0], p[1], v);
      m.sub(q[0], q[1], w);
      m.mul(v, w, v);
      m.add(u, v, w);
      m.sqr(w, w);
      m.sub(u, v, t);
      m.sqr(t, t);
      // read the difference before writing the output, which may be the difference
      m.mul(difference[0], t, t);
      m.mul(difference[1], w, out[0]);
      System.arraycopy(t, 0, out[1], 0, m.k);
    }

    /** <code>out = [k]p</code> for <code>k &gt;= 1</code> with the Montgomery ladder */
    void multiply(long[][] p, BigInteger k, long[][] out) {
      final long[][] base = copy(p);
      final long[][] r0 = copy(p);
      final long[][] r1 = newPoint();
      dbl(base, r1);
      for (int i = k.bitLength() - 2; i >= 0; i--) {
        if ((i & 0xFFF) == 0) {
          checkInterrupted();
        }
        if (k.testBit(i)) {
          add(r1, r0, base, r0);
          dbl(r1, r1);
        } else {
          add(r0, r1, base, r1);
          dbl(r0, r0);
        }
      }
      System.arraycopy(r0[0], 0, out[0], 0, m.k);
      System.arraycopy(r0[1], 0, out[1], 0, m.k);
    }
  }

  /**
   * Store <code>x[i]/z[i] mod n</code> in <code>result</code> with one modular inversion.
   *
   * @return <code>1</code>, or the gcd <code>g &gt; 1</code> of the product of the
   *         <code>z[i]</code> and <code>n</code>, in which case <code>result</code> isn't set
   */
  private static BigInteger affine(BigInteger[] x, BigInteger[] z, BigInteger n,
      BigInteger[] result) {
    final int count = x.length;
    BigInteger[] prefix = new BigInteger[count];
    prefix[0] = z[0].mod(n);
    for (int i = 1; i < count; i++) {
      prefix[i] = prefix[i - 1].multiply(z[i]).mod(n);
    }
    BigInteger g = prefix[count - 1].gcd(n);
    if (!g.equals(ONE)) {
      return g;
    }
    BigInteger inverse = prefix[count - 1].modInverse(n);
    for (int i = count - 1; i > 0; i--) {
      result[i] = x[i].multiply(inverse.multiply(prefix[i - 1])).mod(n);
      inverse = inverse.multiply(z[i]).mod(n);
    }
    result[0] = x[0].multiply(inverse).mod(n);
    return ONE;
  }

  /**
   * The pairs <code>(k, j)</code> of the stage 2 continuation for which <code>k*D+j</code> or
   * <code>k*D-j</code> is a prime in <code>(B1, B2]</code>.
   */
  static final class Stage2Plan {
    /** the odd numbers <code>j &lt; D/2</code> which are coprime to <code>D</code> */
    final int[] babies;

    final int kFirst;

    final int kLast;

    /**
     * The indices into {@link #babies} of the pairs for the giant step <code>k</code> are
     * <code>pairBabies[pairStart[k - kFirst] .. pairStart[k - kFirst + 1] - 1]</code>, as unsigned
     * bytes.
     */
    final byte[] pairBabies;

    final int[] pairStart;

    Stage2Plan(long b1, long b2) {
      int[] babyIndex = new int[D / 2];
      List<Integer> babyList = new ArrayList<>();
      for (int j = 1; j < D / 2; j += 2) {
        babyIndex[j] = -1;
        if (BigInteger.valueOf(j).gcd(BigInteger.valueOf(D)).equals(ONE)) {
          babyIndex[j] = babyList.size();
          babyList.add(j);
        }
      }
      babies = babyList.stream().mapToInt(Integer::intValue).toArray();
      kFirst = (int) Math.max(1, b1 / D);
      kLast = (int) ((b2 + D / 2) / D);
      final int giants = kLast - kFirst + 1;
      BitSet pairs = new BitSet(giants * babies.length);
      int[] basePrimes = primesUpTo((int) Math.sqrt((double) b2) + 1);
      final int segmentLength = 1 << 18;
      boolean[] composite = new boolean[segmentLength];
      for (long low = b1 + 1; low <= b2; low += segmentLength) {
        checkInterrupted();
        long high = Math.min(b2, low + segmentLength - 1);
        int length = (int) (high - low + 1);
        java.util.Arrays.fill(composite, 0, length, false);
        for (int p : basePrimes) {
          long square = (long) p * p;
          if (square > high) {
            break;
          }
          long start = Math.max(square, ((low + p - 1) / p) * p);
          for (long m = start; m <= high; m += p) {
            composite[(int) (m - low)] = true;
          }
        }
        for (int i = 0; i < length; i++) {
          if (!composite[i]) {
            long prime = low + i;
            long k = (prime + D / 2) / D;
            int j = (int) Math.abs(prime - k * D);
            if (k >= kFirst && j < babyIndex.length && babyIndex[j] >= 0) {
              pairs.set((int) (k - kFirst) * babies.length + babyIndex[j]);
            }
          }
        }
      }
      pairBabies = new byte[pairs.cardinality()];
      pairStart = new int[giants + 1];
      int count = 0;
      for (int g = 0; g < giants; g++) {
        pairStart[g] = count;
        final int end = (g + 1) * babies.length;
        for (int bit = pairs.nextSetBit(g * babies.length); bit >= 0 && bit < end; bit =
            pairs.nextSetBit(bit + 1)) {
          pairBabies[count++] = (byte) (bit - g * babies.length);
        }
      }
      pairStart[giants] = count;
    }
  }

  /**
   * The value cached for <code>key</code>, or a new one from <code>build</code>. The value is built
   * outside of any lock, so two threads may each build it once; it is held by a soft reference.
   */
  private static <T> T cached(ConcurrentHashMap<Integer, SoftReference<T>> cache, int key,
      IntFunction<T> build) {
    SoftReference<T> reference = cache.get(key);
    T value = reference == null ? null : reference.get();
    if (value == null) {
      value = build.apply(key);
      cache.put(key, new SoftReference<>(value));
    }
    return value;
  }

  /**
   * The {@link LucasChains} program of stage 1, which multiplies by every prime power up to
   * <code>b1</code>.
   */
  static byte[] stage1Program(int b1) {
    return cached(STAGE1_PROGRAMS, b1, bound -> {
      int[] primes = primesUpTo(bound);
      return LucasChains.stage1Program(primes, countPrimesUpTo(primes, bound), bound);
    });
  }

  /**
   * The stage 2 plan for the stage 1 bound <code>b1</code>; the stage 2 bound is
   * {@link #ECM_B2_FACTOR}<code>*b1</code>.
   */
  static Stage2Plan stage2Plan(int b1) {
    return cached(STAGE2_PLANS, b1, bound -> new Stage2Plan(bound, (long) ECM_B2_FACTOR * bound));
  }

  /** The largest power of the prime <code>p</code> which doesn't exceed <code>bound</code>. */
  private static long primePower(long p, long bound) {
    long power = p;
    while (power <= bound / p) {
      power *= p;
    }
    return power;
  }

  /** The product of <code>factors[from..to-1]</code> as a balanced product tree. */
  private static BigInteger product(List<BigInteger> factors, int from, int to) {
    if (to - from <= 8) {
      BigInteger result = ONE;
      for (int i = from; i < to; i++) {
        result = result.multiply(factors.get(i));
      }
      return result;
    }
    int middle = (from + to) >>> 1;
    return product(factors, from, middle).multiply(product(factors, middle, to));
  }

  private static int countPrimesUpTo(int[] primes, int bound) {
    int index = java.util.Arrays.binarySearch(primes, bound);
    return index >= 0 ? index + 1 : -index - 1;
  }

  /**
   * The primes up to at least <code>limit</code> in ascending order. The returned array is shared
   * and must not be modified.
   */
  static int[] primesUpTo(int limit) {
    PrimeTable table = primeTable.get();
    if (table != null && table.limit >= limit) {
      return table.primes;
    }
    // sieve of Eratosthenes over the odd numbers; bit i stands for 2*i+1
    int size = limit / 2 + 1;
    BitSet composite = new BitSet(size);
    composite.set(0);
    for (int i = 1; (long) (2 * i + 1) * (2 * i + 1) <= limit; i++) {
      if (!composite.get(i)) {
        int p = 2 * i + 1;
        for (long m = (long) p * p; m <= limit; m += 2L * p) {
          composite.set((int) (m >> 1));
        }
      }
    }
    int[] primes = new int[size - composite.cardinality() + 1];
    int count = 0;
    primes[count++] = 2;
    for (int i = composite.nextClearBit(1); i < size && 2L * i + 1 <= limit; i =
        composite.nextClearBit(i + 1)) {
      primes[count++] = 2 * i + 1;
    }
    primes = java.util.Arrays.copyOf(primes, count);
    primeTable = new SoftReference<>(new PrimeTable(limit, primes));
    return primes;
  }
}
