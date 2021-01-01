/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.jml.base;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

import de.tilman_neumann.util.ConfigUtil;

/**
 * An incomplete 128 bit unsigned int implementation.
 *
 * <p>Implementation notes: * a+Long.MIN_VALUE <> b+Long-MIN_VALUE is an inlined compareUnsigned(a,
 * b) <> 0.
 *
 * @author Tilman Neumann
 */
public class Uint128 {
  private static final Logger LOG = Logger.getLogger(Uint128.class);

  private static final boolean DEBUG = false;

  private long high, low;

  public Uint128(long high, long low) {
    this.high = high;
    this.low = low;
  }

  public long getHigh() {
    return high;
  }

  public long getLow() {
    return low;
  }

  /**
   * Add two unsigned 128 bit integers.
   *
   * @param other
   * @return this + other
   */
  public Uint128 add_v1(Uint128 other) {
    // We know for sure that low overflows if both low and o_lo are 64 bit. If only one of the input
    // 'low's
    // is 64 bit, then we can recognize an overflow if the result.lo is not 64 bit.
    final long o_lo = other.getLow();
    final long o_hi = other.getHigh();
    final long r_lo = low + o_lo;
    long r_hi = high + o_hi;
    if ((low < 0 && o_lo < 0) || ((low < 0 || o_lo < 0) && (r_lo >= 0))) r_hi++;
    return new Uint128(r_hi, r_lo);
  }

  /**
   * Add two unsigned 128 bit integers.
   *
   * <p>Simpler carry recognition and thus much faster than the first version, thanks to Ben, see
   * https://www.mersenneforum.org/showpost.php?p=524300&postcount=173.
   *
   * @param other
   * @return this + other
   */
  public Uint128 add /*_v2*/(Uint128 other) {
    long a = low + other.getLow();
    long b = high + other.getHigh();
    if (a + Long.MIN_VALUE < low + Long.MIN_VALUE) b++;
    return new Uint128(b, a);
  }

  /**
   * Compute the sum of this and other, return the high part.
   *
   * @param other
   * @return high part of this + other
   */
  public long add_getHigh(Uint128 other) {
    long a = low + other.getLow();
    long b = high + other.getHigh();
    return (a + Long.MIN_VALUE < low + Long.MIN_VALUE) ? b + 1 : b;
  }

  /**
   * Subtract two unsigned 128 bit integers. XXX experimental, probably wrong...
   *
   * @param other
   * @return this - other
   */
  public Uint128 subtract(Uint128 other) {
    long r_lo = low - other.getLow();
    long r_hi = high - other.getHigh();
    // check for underflow of low 64 bits, subtract carry to high
    if (Long.compareUnsigned(r_lo, low) > 0) {
      --r_hi;
    }
    return new Uint128(r_hi, r_lo);
  }

  /**
   * Multiplication of unsigned 63 bit integers, following
   * https://stackoverflow.com/questions/18859207/high-bits-of-long-multiplication-in-java.
   *
   * <p>This method ignores overflows of the "middle term". As such it won't work for 64 bit inputs
   * but is otherwise faster than mul64().
   *
   * @param a
   * @param b
   * @return a*b accurate for inputs <= 63 bit
   */
  public static Uint128 mul63(long a, long b) {
    final long a_hi = a >>> 32;
    final long b_hi = b >>> 32;
    final long a_lo = a & 0xFFFFFFFFL;
    final long b_lo = b & 0xFFFFFFFFL;
    final long lo_prod = a_lo * b_lo;
    final long med_term = a_hi * b_lo + a_lo * b_hi; // possible overflow here
    final long hi_prod = a_hi * b_hi;
    final long r_hi = (((lo_prod >>> 32) + med_term) >>> 32) + hi_prod;
    final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
    return new Uint128(r_hi, r_lo);
  }

  /**
   * Multiplication of unsigned 64 bit integers, following
   * https://stackoverflow.com/questions/18859207/high-bits-of-long-multiplication-in-java.
   *
   * <p>This method takes notice of overflows of the "middle term". As such it works for 64 bit
   * inputs but is slightly slower than mul63().
   *
   * @param a unsigned long
   * @param b unsigned long
   * @return a*b
   */
  public static Uint128 mul64_v1(long a, long b) {
    final long a_hi = a >>> 32;
    final long b_hi = b >>> 32;
    final long a_lo = a & 0xFFFFFFFFL;
    final long b_lo = b & 0xFFFFFFFFL;

    final long lo_prod = a_lo * b_lo;
    final long med_prod1 = a_hi * b_lo;
    final long med_prod2 = a_lo * b_hi;
    final long med_term = med_prod1 + med_prod2;
    final long hi_prod = a_hi * b_hi;

    // the medium term could overflow
    long r_hi = (((lo_prod >>> 32) + med_term) >>> 32) + hi_prod;
    if ((med_prod1 < 0 && med_prod2 < 0) || ((med_prod1 < 0 || med_prod2 < 0) && med_term >= 0))
      r_hi += 1L << 32;
    final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
    return new Uint128(r_hi, r_lo);
  }

  /**
   * Multiplication of unsigned 64 bit integers with simplified carry recognition.
   *
   * <p>This is the fastest version so far, with the strange exception that it falls behind version
   * 1 for N>=52 bit in PollardRhoBrentMontgomery64.
   *
   * @param a unsigned long
   * @param b unsigned long
   * @return a*b
   */
  public static Uint128 mul64 /*_v2*/(long a, long b) {
    final long a_hi = a >>> 32;
    final long b_hi = b >>> 32;
    final long a_lo = a & 0xFFFFFFFFL;
    final long b_lo = b & 0xFFFFFFFFL;

    final long lo_prod = a_lo * b_lo;
    final long med_prod1 = a_hi * b_lo;
    final long med_prod2 = a_lo * b_hi;
    final long med_term = med_prod1 + med_prod2;
    final long hi_prod = a_hi * b_hi;

    // the medium term could overflow
    final long carry = (med_term + Long.MIN_VALUE < med_prod1 + Long.MIN_VALUE) ? 1L << 32 : 0;
    final long r_hi = (((lo_prod >>> 32) + med_term) >>> 32) + hi_prod + carry;
    final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;

    // With newer hardware and Java 10+, the following might be faster, using intrinsics
    //		final long r_lo2 = a*b;
    //		final long r_hi2 = Math.multiplyHigh(a, b);
    //		assertEquals(r_hi, r_hi2); // TODO false if a<0
    //		assertEquals(r_lo, r_lo2);

    return new Uint128(r_hi, r_lo);
  }

  /**
   * The square of an unsigned 64 bit integer.
   *
   * @param a unsigned long
   * @return a^2
   */
  public static Uint128 square64(long a) {
    final long a_hi = a >>> 32;
    final long a_lo = a & 0xFFFFFFFFL;

    final long lo_prod = a_lo * a_lo;
    final long med_prod = a_hi * a_lo;
    final long med_term = med_prod << 1;
    final long hi_prod = a_hi * a_hi;

    // the medium term could overflow
    final long carry = (med_term + Long.MIN_VALUE < med_prod + Long.MIN_VALUE) ? 1L << 32 : 0;
    final long r_hi = (((lo_prod >>> 32) + med_term) >>> 32) + hi_prod + carry;
    final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
    return new Uint128(r_hi, r_lo);
  }

  /**
   * Computes the low part of the product of two unsigned 64 bit integers.
   *
   * <p>Overflows of the "middle term" are not interesting here because they'ld only affect the high
   * part of the multiplication result.
   *
   * @param a
   * @param b
   * @return (a*b) & 0xFFFFFFFFL
   */
  public static long mul64_getLow(long a, long b) {
    final long a_hi = a >>> 32;
    final long b_hi = b >>> 32;
    final long a_lo = a & 0xFFFFFFFFL;
    final long b_lo = b & 0xFFFFFFFFL;
    final long lo_prod = a_lo * b_lo;
    final long med_term = a_hi * b_lo + a_lo * b_hi;
    final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
    return r_lo;
  }

  /**
   * Compute quotient and remainder of this / v. The quotient will be correct only if it is <= 64
   * bit. Ported from
   * https://codereview.stackexchange.com/questions/67962/mostly-portable-128-by-64-bit-division.
   *
   * @param v 64 bit unsigned integer
   * @return [quotient, remainder] of this / v
   */
  public long[] spDivide(long v) {
    long p_lo;
    long p_hi;
    long q = 0;
    long r;

    long r_hi = getHigh();
    long r_lo = getLow();
    if (DEBUG)
      LOG.debug("r_hi=" + Long.toUnsignedString(r_hi) + ", r_lo=" + Long.toUnsignedString(r_lo));

    int s = 0;
    if (0 == (v >>> 63)) {
      // Normalize so quotient estimates are no more than 2 in error.
      // Note: If any bits get shifted out of r_hi at this point, the result would overflow.
      s = Long.numberOfLeadingZeros(v);
      int t = 64 - s;

      v <<= s;
      r_hi = (r_hi << s) | (r_lo >>> t);
      r_lo <<= s;
    }
    if (DEBUG)
      LOG.debug("s=" + s + ", b=" + Long.toUnsignedString(v) + ", r_lo=" + r_lo + ", r_hi=" + r_hi);

    long b_hi = v >>> 32;

    /*
    The first full-by-half division places b
    across r_hi and r_lo, making the reduction
    step a little complicated.

    To make this easier, u_hi and u_lo will hold
    a shifted image of the remainder.

    [u_hi||    ][u_lo||    ]
          [r_hi||    ][r_lo||    ]
                [ b  ||    ]
    [p_hi||    ][p_lo||    ]
                  |
                  V
                [q_hi||    ]
    */

    long q_hat = divideUnsignedLong(r_hi, b_hi);
    if (DEBUG) LOG.debug("q_hat=" + Long.toUnsignedString(q_hat));

    Uint128 mulResult = mul64(v, q_hat);
    p_lo = mulResult.getLow();
    p_hi = mulResult.getHigh();
    if (DEBUG)
      LOG.debug("p_lo=" + Long.toUnsignedString(p_lo) + ", p_hi=" + Long.toUnsignedString(p_hi));

    long u_hi = r_hi >>> 32;
    long u_lo = (r_hi << 32) | (r_lo >>> 32);

    // r -= b*q_hat
    //
    // At most 2 iterations of this...
    while ((p_hi + Long.MIN_VALUE > u_hi + Long.MIN_VALUE)
        || ((p_hi == u_hi) && (p_lo + Long.MIN_VALUE > u_lo + Long.MIN_VALUE))) {
      if (p_lo + Long.MIN_VALUE < v + Long.MIN_VALUE) {
        --p_hi;
      }
      p_lo -= v;
      --q_hat;
    }

    long w_lo = (p_lo << 32);
    long w_hi = (p_hi << 32) | (p_lo >>> 32);
    if (DEBUG)
      LOG.debug("w_lo=" + Long.toUnsignedString(w_lo) + ", w_hi=" + Long.toUnsignedString(w_hi));

    if (w_lo + Long.MIN_VALUE > r_lo + Long.MIN_VALUE) {
      if (DEBUG) LOG.debug("increment w_hi!");
      ++w_hi;
    }

    r_lo -= w_lo;
    r_hi -= w_hi;
    if (DEBUG)
      LOG.debug("r_lo=" + Long.toUnsignedString(r_lo) + ", r_hi=" + Long.toUnsignedString(r_hi));

    q = q_hat << 32;

    /*
    The lower half of the quotient is easier,
    as b is now aligned with r_lo.

          |r_hi][r_lo||    ]
                [ b  ||    ]
    [p_hi||    ][p_lo||    ]
                        |
                        V
                [q_hi||q_lo]
    */

    q_hat = divideUnsignedLong((r_hi << 32) | (r_lo >>> 32), b_hi);
    if (DEBUG)
      LOG.debug("b=" + Long.toUnsignedString(v) + ", q_hat=" + Long.toUnsignedString(q_hat));

    mulResult = mul64(v, q_hat);
    p_lo = mulResult.getLow();
    p_hi = mulResult.getHigh();
    if (DEBUG)
      LOG.debug("2: p_lo=" + Long.toUnsignedString(p_lo) + ", p_hi=" + Long.toUnsignedString(p_hi));

    // r -= b*q_hat
    //
    // ...and at most 2 iterations of this.
    while ((p_hi + Long.MIN_VALUE > r_hi + Long.MIN_VALUE)
        || ((p_hi == r_hi) && (p_lo + Long.MIN_VALUE > r_lo + Long.MIN_VALUE))) {
      if (p_lo + Long.MIN_VALUE < v + Long.MIN_VALUE) {
        --p_hi;
      }
      p_lo -= v;
      --q_hat;
    }

    r_lo -= p_lo;

    q |= q_hat;

    r = r_lo >>> s;

    return new long[] {q, r};
  }

  /**
   * A good replacement for the slow Long.divideUnsigned(). Taken from the Huldra project, see
   * BigInt.div(..) at https://github.com/bwakell/Huldra.
   *
   * @param a
   * @param b
   * @return unsigned a/b
   */
  private static long divideUnsignedLong(long a, long b) {
    long qhat = (a >>> 1) / b << 1;
    long t = a - qhat * b;
    if (t + Long.MIN_VALUE >= b + Long.MIN_VALUE) qhat++;
    //		if (DEBUG) assertEquals(Long.divideUnsigned(a, b), qhat);
    return qhat;
  }

  /**
   * Shift this 'bits' bits to the left.
   *
   * @param bits
   * @return this << bits
   */
  public Uint128 shiftLeft(int bits) {
    if (bits < 64) {
      long rh = (high << bits) | (low >>> (64 - bits));
      long rl = low << bits;
      return new Uint128(rh, rl);
    }
    return new Uint128(low << (bits - 64), 0);
  }

  /**
   * Shift this 'bits' bits to the right.
   *
   * @param bits
   * @return this >>> bits
   */
  public Uint128 shiftRight(int bits) {
    if (bits < 64) {
      long rh = high >>> bits;
      long rl = (low >>> bits) | (high << (64 - bits));
      return new Uint128(rh, rl);
    }
    return new Uint128(0, high >>> (bits - 64));
  }

  /**
   * Bitwise "and" operation with a long.
   *
   * @param other
   * @return this & other
   */
  public long and(long other) {
    return low & other;
  }

  public double doubleValue() {
    return toBigInteger().doubleValue(); // TODO more efficient solution
  }

  /**
   * Convert this to BigInteger.
   *
   * @return this unsigned 128 bit integer converted to BigInteger
   */
  public BigInteger toBigInteger() {
    return new BigInteger(Long.toBinaryString(high), 2)
        .shiftLeft(64)
        .add(new BigInteger(Long.toBinaryString(low), 2));
  }

  @Override
  public String toString() {
    return toBigInteger().toString();
  }

  private static void testCorrectness() {
    SecureRandom RNG = new SecureRandom();

    for (int i = 0; i < 100000; i++) {
      BigInteger a_hi_big = new BigInteger(63, RNG);
      BigInteger a_lo_big = new BigInteger(64, RNG);
      BigInteger b_hi_big = new BigInteger(63, RNG);
      BigInteger b_lo_big = new BigInteger(64, RNG);

      long a_hi = a_hi_big.longValue();
      long a_lo = a_lo_big.longValue();
      long b_hi = b_hi_big.longValue();
      long b_lo = b_lo_big.longValue();

      // test addition
      Uint128 a128 = new Uint128(a_hi, a_lo);
      Uint128 b128 = new Uint128(b_hi, b_lo);
      Uint128 sum128 = a128.add_v1(b128);
      BigInteger sum128Big = sum128.toBigInteger();
      BigInteger sumBig = a128.toBigInteger().add(b128.toBigInteger());
      //			assertEquals(sumBig, sum128Big);

      Uint128 sum128_v2 = a128.add /*_v2*/(b128);
      BigInteger sum128Big_v2 = sum128_v2.toBigInteger();
      //			assertEquals(sumBig, sum128Big_v2);

      // test multiplication with 63 bit numbers
      Uint128 prod128 = mul63(a_hi, b_hi);
      BigInteger prod128Big = prod128.toBigInteger();
      BigInteger correctProd = a_hi_big.multiply(b_hi_big);
      //			assertEquals(correctProd, prod128Big);

      // test multiplication with 64 bit numbers
      correctProd = a_lo_big.multiply(b_lo_big);

      prod128 = mul64_v1(a_lo, b_lo);
      prod128Big = prod128.toBigInteger();
      if (!correctProd.equals(prod128Big)) {
        LOG.error(
            "mul64_v1: "
                + a_lo_big
                + "*"
                + b_lo_big
                + ": correct = "
                + correctProd
                + " but result = "
                + prod128Big);
      }
      //      assertEquals(correctProd, prod128Big);

      prod128 = mul64 /*_v2*/(a_lo, b_lo);
      prod128Big = prod128.toBigInteger();
      if (!correctProd.equals(prod128Big)) {
        LOG.error(
            "mul64_v2: "
                + a_lo_big
                + "*"
                + b_lo_big
                + ": correct = "
                + correctProd
                + " but result = "
                + prod128Big);
      }
      //      assertEquals(correctProd, prod128Big);
    }
  }

  private static void testPerformance() {
    SecureRandom RNG = new SecureRandom();
    int NCOUNT = 10000000;

    // set up test numbers
    long[] a_arr = new long[NCOUNT];
    long[] b_arr = new long[NCOUNT];
    Uint128[] a128_arr = new Uint128[NCOUNT];
    Uint128[] b128_arr = new Uint128[NCOUNT];

    for (int i = 0; i < NCOUNT; i++) {
      a_arr[i] = RNG.nextLong();
      b_arr[i] = RNG.nextLong();
      a128_arr[i] = new Uint128(a_arr[i], RNG.nextLong());
      b128_arr[i] = new Uint128(b_arr[i], RNG.nextLong());
    }

    // test performance of add implementations
    long t0 = System.currentTimeMillis();
    for (int i = 0; i < NCOUNT; i++) {
      a128_arr[i].add_v1(b128_arr[i]);
    }
    long t1 = System.currentTimeMillis();
    LOG.info("add_v1 took " + (t1 - t0) + "ms");

    t0 = System.currentTimeMillis();
    for (int i = 0; i < NCOUNT; i++) {
      a128_arr[i].add /*_v2*/(b128_arr[i]);
    }
    t1 = System.currentTimeMillis();
    LOG.info("add_v2 took " + (t1 - t0) + "ms");
    // The results of this comparison seem to be misleading. If we compare the two implementations
    // in different PollardRhoBrentMontgomery63 variants than v2 is much faster...

    // test performance of mul64 implementations
    t0 = System.currentTimeMillis();
    for (int i = 0; i < NCOUNT; i++) {
      mul64_v1(a_arr[i], b_arr[i]);
    }
    t1 = System.currentTimeMillis();
    LOG.info("mul64_v1 took " + (t1 - t0) + "ms");

    t0 = System.currentTimeMillis();
    for (int i = 0; i < NCOUNT; i++) {
      mul64 /*_v2*/(a_arr[i], b_arr[i]);
    }
    t1 = System.currentTimeMillis();
    LOG.info("mul64_v2 took " + (t1 - t0) + "ms");
  }

  /**
   * Test.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();
    testCorrectness();
    testPerformance();
  }
}
