/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.util.Ensure;

/**
 * An incomplete 128 bit unsigned int implementation.
 * 
 * Implementation notes:
 * * a+Long.MIN_VALUE <> b+Long-MIN_VALUE is an inlined compareUnsigned(a, b) <> 0.
 * 
 * @author Tilman Neumann
 */
// TODO Now that there are signed methods, this class needs a refactoring
public class Uint128 {
	private static final Logger LOG = LogManager.getLogger(Uint128.class);
	
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
	 * @param other
	 * @return this + other
	 */
	public Uint128 add_v1(Uint128 other) {
		// We know for sure that low overflows if both low and o_lo are 64 bit. If only one of the input 'low's
		// is 64 bit, then we can recognize an overflow if the result.lo is not 64 bit.
		final long o_lo = other.getLow();
		final long o_hi = other.getHigh();
		final long r_lo = low + o_lo;
		long r_hi = high + o_hi;
		if ((low<0 && o_lo<0) || ((low<0 || o_lo<0) && (r_lo >= 0))) r_hi++;
		return new Uint128(r_hi, r_lo);
	}

	/**
	 * Add two unsigned 128 bit integers.
	 * 
	 * Simpler carry recognition and thus much faster than the first version,
	 * thanks to Ben, see https://www.mersenneforum.org/showpost.php?p=524300&postcount=173.
	 * 
	 * @param other
	 * @return this + other
	 */
	public Uint128 add/*_v2*/(Uint128 other) {
		long a = low + other.getLow();
		long b = high + other.getHigh();
		if (a+Long.MIN_VALUE < low+Long.MIN_VALUE) b++;
		return new Uint128(b, a);
	}

	/**
	 * Compute the sum of this and other, return the high part.
	 * @param other
	 * @return high part of this + other
	 */
	public long add_getHigh(Uint128 other) {
		long a = low + other.getLow();
		long b = high + other.getHigh();
		return (a+Long.MIN_VALUE < low+Long.MIN_VALUE) ? b + 1 : b;
	}

	/**
	 * Subtract two unsigned 128 bit integers.
	 * 
	 * @param other
	 * @return this - other
	 */
	// XXX experimental, probably wrong...
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
	 * Multiplication of unsigned 63 bit integers,
	 * following https://stackoverflow.com/questions/18859207/high-bits-of-long-multiplication-in-java.
	 * 
	 * This method ignores overflows of the "middle term".
	 * As such it won't work for 64 bit inputs but is otherwise faster than mul64().
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
	 * Multiplication of unsigned 64 bit integers,
	 * following https://stackoverflow.com/questions/18859207/high-bits-of-long-multiplication-in-java.
	 * 
	 * This method takes notice of overflows of the "middle term".
	 * As such it works for 64 bit inputs but is slightly slower than mul63().
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
		if ((med_prod1<0 && med_prod2<0) || ((med_prod1<0 || med_prod2<0) && med_term>=0)) r_hi += 1L<<32;
		final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
		return new Uint128(r_hi, r_lo);
	}

	/**
	 * Multiplication of unsigned 64 bit integers with simplified carry recognition.
	 * 
	 * Faster than v1 except for N>=52 bit in PollardRhoBrentMontgomery64 (strange)
	 * 
	 * @param a unsigned long
	 * @param b unsigned long
	 * @return a*b
	 */
	public static Uint128 mul64/*_v2*/(long a, long b) {
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
		final long carry = (med_term+Long.MIN_VALUE < med_prod1+Long.MIN_VALUE) ? 1L<<32 : 0;
		final long r_hi = (((lo_prod >>> 32) + med_term) >>> 32) + hi_prod + carry;
		final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;

		return new Uint128(r_hi, r_lo);
	}

	/**
	 * Multiplication of unsigned 64 bit integers.
	 * 
	 * <strong>Experimental version</strong>, pretty slow when used in TinyEcm.
	 * 
	 * @param a unsigned long
	 * @param b unsigned long
	 * @return a*b
	 */
	public static Uint128 mul64_v3(long a, long b) { // derived from mul64Signed()
		final long a_hi = a >> 32;
		final long a_lo = a & 0xFFFFFFFFL;
		final long b_hi = b >> 32;
		final long b_lo = b & 0xFFFFFFFFL;
		
		// use b_lo twice as first argument hoping that this optimizes register usage
		final long w0 = b_lo * a_lo;
		final long t = b_lo * a_hi + (w0 >>> 32);
		// same with t
		final long w2 = t >> 32;
		final long w1 = (t & 0xFFFFFFFFL) + a_lo * b_hi;
	    
		long r_hi = a_hi * b_hi + w2 + (w1 >> 32);
		// so far we computed the signed solution; now make it unsigned
		if (a<0) r_hi += b;
		if (b<0) r_hi += a;

		final long r_lo = a * b;
		
		return new Uint128(r_hi, r_lo);
	}
	
	/**
	 * Multiplication of two unsigned 64-bit integers using Math.multiplyHigh().
	 * Pretty fast if supported by intrinsics, which needs newer hardware and Java 10+.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Uint128 mul64_MH(long a, long b) {
		final long r_lo = a*b;
		long r_hi = Math.multiplyHigh(a, b);
		if (a<0) r_hi += b;
		if (b<0) r_hi += a;
		
		if (DEBUG) {
			// compare to pure Java implementation
			Uint128 testResult = mul64(a, b);
			Ensure.ensureEquals(testResult.high, r_hi);
			Ensure.ensureEquals(testResult.low, r_lo);
		}

		return new Uint128(r_hi, r_lo);
	}
	
	/**
	 * Special implementation for the multiplication of two unsigned 64-bit integers using Math.multiplyHigh().
	 * Pretty fast if supported by intrinsics, which needs newer hardware and Java 10+.<br><br>
	 * 
	 * <strong>WARNING: This implementation is not generally correct.</strong><br><br>
	 * 
	 * However, it seems to be sufficient for the purposes of TinyEcm and PollardRhoBrentMontgomery
	 * implementations in this library, and should be a bit faster <em>there</em>.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Uint128 spMul64_MH(long a, long b) {
		final long r_lo = a*b;
		long r_hi = Math.multiplyHigh(a, b);
		if (a<0) r_hi += b;
		// For general correctness we would need as well
		//if (b<0) r_hi += a;
		// See implementation of java.lang.Math.unsignedMultiplyHigh() starting from Java 18.
		
		if (DEBUG) {
			// compare to pure Java implementation
			Uint128 testResult = mul64(a, b);
			Ensure.ensureEquals(testResult.high, r_hi);
			Ensure.ensureEquals(testResult.low, r_lo);
		}

		return new Uint128(r_hi, r_lo);
	}

	/**
	 * Multiplication of two signed 64 bit integers, adapted from Henry S. Warren, Hacker's Delight, Addison-Wesley, 2nd edition, chapter 8-2.
	 * This is more or less what Java9 does in Math.multiplyHigh(), but I think I made it a bit faster optimizing register usage.
	 * 
	 * @param a signed long
	 * @param b signed long
	 * @return a*b as a signed 127 bit number
	 */
	public static Uint128 mul64Signed(long a, long b) {
		final long a_hi = a >> 32;
		final long a_lo = a & 0xFFFFFFFFL;
		final long b_hi = b >> 32;
		final long b_lo = b & 0xFFFFFFFFL;
		
		// use b_lo twice as first argument hoping that this optimizes register usage
		final long w0 = b_lo * a_lo;
		final long t = b_lo * a_hi + (w0 >>> 32);
		// same with t
		final long w2 = t >> 32;
		final long w1 = (t & 0xFFFFFFFFL) + a_lo * b_hi;
	    
		final long r_hi = a_hi * b_hi + w2 + (w1 >> 32);
		final long r_lo = a * b;
		return new Uint128(r_hi, r_lo);
	}

	/**
	 * Multiplication of two signed 64-bit integers using Math.multiplyHigh().
	 * Pretty fast if supported by intrinsics, which needs newer hardware and Java 10+.<br><br>
	 * 
	 * @param a signed long
	 * @param b signed long
	 * @return a*b as a signed 127 bit number
	 */
	public static Uint128 mul64SignedMH(long a, long b) {
		final long r_lo = a*b;
		final long r_hi = Math.multiplyHigh(a, b);
		
		if (DEBUG) {
			// compare to pure Java implementation
			Uint128 testResult = mul64Signed(a, b);
			Ensure.ensureEquals(testResult.high, r_hi);
			Ensure.ensureEquals(testResult.low, r_lo);
		}

		return new Uint128(r_hi, r_lo);
	}

	/**
	 * The square of an unsigned 64 bit integer.
	 * 
	 * @param a unsigned long
	 * @return a^2
	 */
	// XXX speed up using intrinsics like in mul64_MH() ?
	public static Uint128 square64(long a) {
		final long a_hi = a >>> 32;
		final long a_lo = a & 0xFFFFFFFFL;
		
		final long lo_prod = a_lo * a_lo;
		final long med_prod = a_hi * a_lo;
		final long med_term = med_prod<<1;
		final long hi_prod = a_hi * a_hi;
		
		// the medium term could overflow
		final long carry = (med_term+Long.MIN_VALUE < med_prod+Long.MIN_VALUE) ? 1L<<32 : 0;
		final long r_hi = (((lo_prod >>> 32) + med_term) >>> 32) + hi_prod + carry;
		final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
		return new Uint128(r_hi, r_lo);
	}

	/**
	 * Computes the low part of the product of two unsigned 64 bit integers.
	 * 
	 * Overflows of the "middle term" are not interesting here because they'ld only
	 * affect the high part of the multiplication result.
	 * 
	 * @param a
	 * @param b
	 * @return (a*b) & 0xFFFFFFFFL
	 */
	// XXX a*b should give the same result !?
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
	 * Multiplication of two unsigned 128 bit integers.
	 * 
	 * @param a Uint128
	 * @param b Uint128
	 * @return a*b as an array of [low, high] Uint128 objects;
	 */
	public static Uint128[] mul128/*_v2*/(Uint128 a, Uint128 b) {
		final long a_hi = a.getHigh(); // a >>> 32;
		final long b_hi = b.getHigh(); // b >>> 32;
		final long a_lo = a.getLow(); // a & 0xFFFFFFFFL;
		final long b_lo = b.getLow(); // b & 0xFFFFFFFFL;
		
		final Uint128 lo_prod = mul64(a_lo, b_lo); // a_lo * b_lo;
		final Uint128 med_prod1 = mul64(a_hi, b_lo); // a_hi * b_lo;
		final Uint128 med_prod2 = mul64(a_lo, b_hi); // a_lo * b_hi;
		final Uint128 med_term = med_prod1.add(med_prod2); // med_prod1 + med_prod2;
		final Uint128 hi_prod = mul64(a_hi, b_hi); // a_hi * b_hi;
		
		// the medium term could overflow
		//final long carry = (med_term+Long.MIN_VALUE < med_prod1+Long.MIN_VALUE) ? 1L<<32 : 0;
		final Uint128 carry = (med_term.getHigh()+Long.MIN_VALUE < med_prod1.getHigh()+Long.MIN_VALUE) ? new Uint128(1, 0) : new Uint128(0, 0);
		
		//final long r_hi = (((lo_prod >>> 32) + med_term) >>> 32) + hi_prod + carry;
		final long lo_prod_hi = lo_prod.getHigh(); // (lo_prod >>> 32)
		final Uint128 intermediate = new Uint128(0, lo_prod_hi).add(med_term); // ((lo_prod >>> 32) + med_term)
		final long intermediate_hi = intermediate.getHigh(); // (((lo_prod >>> 32) + med_term) >>> 32)
		final Uint128 r_hi = new Uint128(0, intermediate_hi).add(hi_prod).add(carry);
		
		//final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
		final long med_term_lo = med_term.getLow(); // (med_term & 0xFFFFFFFFL)
		final Uint128 r_lo = new Uint128(med_term_lo, 0).add(lo_prod);
		
		//return new Uint128(r_hi, r_lo);
		return new Uint128[] {r_lo, r_hi};
	}

	/**
	 * Get the lower 128 bit integer of a multiplication of two unsigned 128 bit integers.
	 * 
	 * @param a Uint128
	 * @param b Uint128
	 * @return the low Uint128 of a*b
	 */
	public static Uint128 mul128_getLow(Uint128 a, Uint128 b) { // derived from mul128_v2
		final long a_hi = a.getHigh(); // a >>> 32;
		final long b_hi = b.getHigh(); // b >>> 32;
		final long a_lo = a.getLow(); // a & 0xFFFFFFFFL;
		final long b_lo = b.getLow(); // b & 0xFFFFFFFFL;
		
		final Uint128 lo_prod = mul64(a_lo, b_lo); // a_lo * b_lo;
		final Uint128 med_prod1 = mul64(a_hi, b_lo); // a_hi * b_lo;
		final Uint128 med_prod2 = mul64(a_lo, b_hi); // a_lo * b_hi;
		final Uint128 med_term = med_prod1.add(med_prod2); // med_prod1 + med_prod2;
		
		//final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
		final long med_term_lo = med_term.getLow(); // (med_term & 0xFFFFFFFFL)
		final Uint128 r_lo = new Uint128(med_term_lo, 0).add(lo_prod);
		
		//return new Uint128(r_hi, r_lo);
		return r_lo;
	}

	/**
	 * Get the lower 128 bit integer of a multiplication of two unsigned 128 bit integers, using Math.multiplyHigh().
	 * 
	 * @param a Uint128
	 * @param b Uint128
	 * @return the low Uint128 of a*b
	 */
	public static Uint128 mul128MH_getLow(Uint128 a, Uint128 b) { // derived from mul128_v2
		final long a_hi = a.getHigh(); // a >>> 32;
		final long b_hi = b.getHigh(); // b >>> 32;
		final long a_lo = a.getLow(); // a & 0xFFFFFFFFL;
		final long b_lo = b.getLow(); // b & 0xFFFFFFFFL;
		
		final Uint128 lo_prod = mul64_MH(a_lo, b_lo); // a_lo * b_lo;
		final Uint128 med_prod1 = mul64_MH(a_hi, b_lo); // a_hi * b_lo;
		final Uint128 med_prod2 = mul64_MH(a_lo, b_hi); // a_lo * b_hi;
		final Uint128 med_term = med_prod1.add(med_prod2); // med_prod1 + med_prod2;
		
		//final long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
		final long med_term_lo = med_term.getLow(); // (med_term & 0xFFFFFFFFL)
		final Uint128 r_lo = new Uint128(med_term_lo, 0).add(lo_prod);
		
		//return new Uint128(r_hi, r_lo);
		return r_lo;
	}

	/**
	 * Compute quotient and remainder of this / v.
	 * The quotient will be correct only if it is <= 64 bit.
	 * Ported from https://codereview.stackexchange.com/questions/67962/mostly-portable-128-by-64-bit-division.
	 * 
	 * @param v 64 bit unsigned integer
	 * @return [quotient, remainder] of this / v
	 */
	public long[] spDivide(long v)
	{
		long p_lo;
		long p_hi;
		long q = 0;
		long r;
		
		long r_hi = getHigh();
		long r_lo = getLow();
		if (DEBUG) LOG.debug("r_hi=" + Long.toUnsignedString(r_hi) + ", r_lo=" + Long.toUnsignedString(r_lo));
		
		int s = 0;
		if(0 == (v >>> 63)){
		    // Normalize so quotient estimates are no more than 2 in error.
		    // Note: If any bits get shifted out of r_hi at this point, the result would overflow.
		    s = Long.numberOfLeadingZeros(v);
		    int t = 64 - s;
		
		    v <<= s;
		    r_hi = (r_hi << s)|(r_lo >>> t);
		    r_lo <<= s;
		}
		if (DEBUG) LOG.debug("s=" + s + ", b=" + Long.toUnsignedString(v) + ", r_lo=" + r_lo + ", r_hi=" + r_hi);
		
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
		if (DEBUG) LOG.debug("p_lo=" + Long.toUnsignedString(p_lo) + ", p_hi=" + Long.toUnsignedString(p_hi));
		
		long u_hi = r_hi >>> 32;
		long u_lo = (r_hi << 32)|(r_lo >>> 32);
		
		// r -= b*q_hat
		//
		// At most 2 iterations of this...
		while( (p_hi+Long.MIN_VALUE > u_hi+Long.MIN_VALUE) || ((p_hi == u_hi) && (p_lo+Long.MIN_VALUE > u_lo+Long.MIN_VALUE)) )
		{
		    if (p_lo+Long.MIN_VALUE < v+Long.MIN_VALUE) {
		        --p_hi;
		    }
		    p_lo -= v;
		    --q_hat;
		}
		
		long w_lo = (p_lo << 32);
		long w_hi = (p_hi << 32)|(p_lo >>> 32);
		if (DEBUG) LOG.debug("w_lo=" + Long.toUnsignedString(w_lo) + ", w_hi=" + Long.toUnsignedString(w_hi));
		
		if (w_lo+Long.MIN_VALUE > r_lo+Long.MIN_VALUE) {
			if (DEBUG) LOG.debug("increment w_hi!");
		    ++w_hi;
		}
		
		r_lo -= w_lo;
		r_hi -= w_hi;
		if (DEBUG) LOG.debug("r_lo=" + Long.toUnsignedString(r_lo) + ", r_hi=" + Long.toUnsignedString(r_hi));
		
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
		
		q_hat = divideUnsignedLong((r_hi << 32)|(r_lo >>> 32), b_hi);
		if (DEBUG) LOG.debug("b=" + Long.toUnsignedString(v) + ", q_hat=" + Long.toUnsignedString(q_hat));
		
		mulResult = mul64(v, q_hat);
		p_lo = mulResult.getLow();
		p_hi = mulResult.getHigh();
		if (DEBUG) LOG.debug("2: p_lo=" + Long.toUnsignedString(p_lo) + ", p_hi=" + Long.toUnsignedString(p_hi));
		
		// r -= b*q_hat
		//
		// ...and at most 2 iterations of this.
		while( (p_hi+Long.MIN_VALUE > r_hi+Long.MIN_VALUE) || ((p_hi == r_hi) && (p_lo+Long.MIN_VALUE > r_lo+Long.MIN_VALUE)) )
		{
		    if(p_lo+Long.MIN_VALUE < v+Long.MIN_VALUE){
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
	 * Compute quotient and remainder of this / v.
	 * The quotient will be correct only if it is <= 64 bit.
	 * Ported from https://codereview.stackexchange.com/questions/67962/mostly-portable-128-by-64-bit-division.
	 * 
	 * In this variant we use Math.multiplyHigh() to multiply two unsigned 64 bit integers. This makes hardly a difference in terms of performance, though.
	 * Otherwise the implementation does not differ from spDivide().
	 * 
	 * @param v 64 bit unsigned integer
	 * @return [quotient, remainder] of this / v
	 */
	// XXX The name sp_divide stems from YaFu's tinyEcm.c. I guess that "sp" stands for "special". But here we have a full division; so some improvement potential may be given for certain applications.
	public long[] spDivide_MH(long v)
	{
		long p_lo;
		long p_hi;
		long q = 0;
		long r;
		
		long r_hi = getHigh();
		long r_lo = getLow();
		if (DEBUG) LOG.debug("r_hi=" + Long.toUnsignedString(r_hi) + ", r_lo=" + Long.toUnsignedString(r_lo));
		
		int s = 0;
		if(0 == (v >>> 63)){
		    // Normalize so quotient estimates are no more than 2 in error.
		    // Note: If any bits get shifted out of r_hi at this point, the result would overflow.
		    s = Long.numberOfLeadingZeros(v);
		    int t = 64 - s;
		
		    v <<= s;
		    r_hi = (r_hi << s)|(r_lo >>> t);
		    r_lo <<= s;
		}
		if (DEBUG) LOG.debug("s=" + s + ", b=" + Long.toUnsignedString(v) + ", r_lo=" + r_lo + ", r_hi=" + r_hi);
		
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
		
		// In TinyEcm64MH* variants, spMul64_MH() is slightly faster than mul64_MH(), and with mul64Signed() it doesn't work at all.
		Uint128 mulResult = spMul64_MH(v, q_hat);
		p_lo = mulResult.getLow();
		p_hi = mulResult.getHigh();
		if (DEBUG) LOG.debug("p_lo=" + Long.toUnsignedString(p_lo) + ", p_hi=" + Long.toUnsignedString(p_hi));
		
		long u_hi = r_hi >>> 32;
		long u_lo = (r_hi << 32)|(r_lo >>> 32);
		
		// r -= b*q_hat
		//
		// At most 2 iterations of this...
		while( (p_hi+Long.MIN_VALUE > u_hi+Long.MIN_VALUE) || ((p_hi == u_hi) && (p_lo+Long.MIN_VALUE > u_lo+Long.MIN_VALUE)) )
		{
		    if (p_lo+Long.MIN_VALUE < v+Long.MIN_VALUE) {
		        --p_hi;
		    }
		    p_lo -= v;
		    --q_hat;
		}
		
		long w_lo = (p_lo << 32);
		long w_hi = (p_hi << 32)|(p_lo >>> 32);
		if (DEBUG) LOG.debug("w_lo=" + Long.toUnsignedString(w_lo) + ", w_hi=" + Long.toUnsignedString(w_hi));
		
		if (w_lo+Long.MIN_VALUE > r_lo+Long.MIN_VALUE) {
			if (DEBUG) LOG.debug("increment w_hi!");
		    ++w_hi;
		}
		
		r_lo -= w_lo;
		r_hi -= w_hi;
		if (DEBUG) LOG.debug("r_lo=" + Long.toUnsignedString(r_lo) + ", r_hi=" + Long.toUnsignedString(r_hi));
		
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
		
		q_hat = divideUnsignedLong((r_hi << 32)|(r_lo >>> 32), b_hi);
		if (DEBUG) LOG.debug("b=" + Long.toUnsignedString(v) + ", q_hat=" + Long.toUnsignedString(q_hat));
		
		mulResult = spMul64_MH(v, q_hat);
		p_lo = mulResult.getLow();
		p_hi = mulResult.getHigh();
		if (DEBUG) LOG.debug("2: p_lo=" + Long.toUnsignedString(p_lo) + ", p_hi=" + Long.toUnsignedString(p_hi));
		
		// r -= b*q_hat
		//
		// ...and at most 2 iterations of this.
		while( (p_hi+Long.MIN_VALUE > r_hi+Long.MIN_VALUE) || ((p_hi == r_hi) && (p_lo+Long.MIN_VALUE > r_lo+Long.MIN_VALUE)) )
		{
		    if(p_lo+Long.MIN_VALUE < v+Long.MIN_VALUE){
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
	 * A good replacement for the slow Long.divideUnsigned(). Taken from the Huldra project,
	 * see BigInt.div(..) at https://github.com/bwakell/Huldra.
	 * @param a
	 * @param b
	 * @return unsigned a/b
	 */
	private static long divideUnsignedLong(long a, long b) {
		long qhat = (a >>> 1)/b << 1;
		long t = a - qhat*b;
		if (t+Long.MIN_VALUE >= b+Long.MIN_VALUE) qhat++;
		if (DEBUG) Ensure.ensureEquals(Long.divideUnsigned(a, b), qhat);
		return qhat;
	}

	/**
	 * Shift this 'bits' bits to the left.
	 * @param bits
	 * @return this << bits
	 */
	public Uint128 shiftLeft(int bits) {
		if (bits<64) {
			long rh = (high<<bits) | (low>>>(64-bits));
			long rl = low<<bits;
			return new Uint128(rh, rl);
		}
		return new Uint128(low<<(bits-64), 0);
	}
	
	/**
	 * Shift this 'bits' bits to the right.
	 * @param bits
	 * @return this >>> bits
	 */
	public Uint128 shiftRight(int bits) {
		if (bits<64) {
			long rh = high>>>bits;
			long rl = (low>>>bits) | (high<<(64-bits));
			return new Uint128(rh, rl);
		}
		return new Uint128(0, high>>>(bits-64));
	}

	/**
	 * Bitwise "and" operation with a long.
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
	 * @return this unsigned 128 bit integer converted to BigInteger
	 */
	public BigInteger toBigInteger() {
		return new BigInteger(Long.toBinaryString(high), 2).shiftLeft(64).add(new BigInteger(Long.toBinaryString(low), 2));
	}
	
	@Override
	public String toString() {
		return toBigInteger().toString();
	}
}
