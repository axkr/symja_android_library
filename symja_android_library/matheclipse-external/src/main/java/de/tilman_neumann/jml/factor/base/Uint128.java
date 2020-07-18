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
package de.tilman_neumann.jml.factor.base;


import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.log4j.Logger;


/**
 * Rudimentary 128 bit unsigned int implementation.
 * 
 * @author Tilman Neumann
 */
public class Uint128 {
	private static final Logger LOG = Logger.getLogger(Uint128.class);
	
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
	 * Multiplication of unsigned 63 bit integers,
	 * following https://stackoverflow.com/questions/18859207/high-bits-of-long-multiplication-in-java.
	 * 
	 * This method ignores overflows of the "middle term".
	 * As such it won't work for 64 bit inputs but is otherwise faster than mul64().
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Uint128 mul63(long x, long y) {
		final long x_hi = x >>> 32;
		final long y_hi = y >>> 32;
		final long x_lo = x & 0xFFFFFFFFL;
		final long y_lo = y & 0xFFFFFFFFL;
		final long lo_prod = x_lo * y_lo;
		final long med_term = x_hi * y_lo + x_lo * y_hi; // possible overflow here
		final long hi_prod = x_hi * y_hi;
		long r_hi = (((lo_prod >>> 32) + med_term) >>> 32) + hi_prod;
		long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
		return new Uint128(r_hi, r_lo);
	}

	/**
	 * Multiplication of unsigned 64 bit integers,
	 * following https://stackoverflow.com/questions/18859207/high-bits-of-long-multiplication-in-java.
	 * 
	 * This method takes notice of overflows of the "middle term".
	 * As such it works for 64 bit inputs but is slightly slower than mul63().
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Uint128 mul64(long x, long y) {
		final long x_hi = x >>> 32;
		final long y_hi = y >>> 32;
		final long x_lo = x & 0xFFFFFFFFL;
		final long y_lo = y & 0xFFFFFFFFL;
		
		final long lo_prod = x_lo * y_lo;
		final long med_prod1 = x_hi * y_lo;
		final long med_prod2 = x_lo * y_hi;
		final long med_term = med_prod1 + med_prod2;
		long hi_prod = x_hi * y_hi;
		
		// the medium term could overflow
		boolean carry = (med_prod1<0) && (med_prod2<0);
		if (!carry) {
			boolean checkCarry = (med_prod1<0) || (med_prod2<0);
			if (checkCarry && med_term>=0) {
				carry = true;
			}
		}
		
		long r_hi = (((lo_prod >>> 32) + med_term) >>> 32) + hi_prod;
		if (carry) r_hi += 1L<<32;
		long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
		return new Uint128(r_hi, r_lo);
	}

	/**
	 * Computes the low part of the product of two unsigned 64 bit integers.
	 * 
	 * Overflows of the "middle term" are not interesting here because they'ld only
	 * affect the high part of the multiplication result.
	 * 
	 * @param x
	 * @param y
	 * @return (x*y) & 0xFFFFFFFFL
	 */
	public static long mul64_getLow(long x, long y) {
		final long x_hi = x >>> 32;
		final long y_hi = y >>> 32;
		final long x_lo = x & 0xFFFFFFFFL;
		final long y_lo = y & 0xFFFFFFFFL;
		final long lo_prod = x_lo * y_lo;
		final long med_term = x_hi * y_lo + x_lo * y_hi;
		long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
		return r_lo;
	}

	/**
	 * Add two unsigned 128 bit integers.
	 * @param other
	 * @return this + other
	 */
	public Uint128 add(Uint128 other) {
		// We know for sure that low overflows if both low and o_lo are 64 bit.
		// If only one of the input 'low's is 64 bit, then we can recognize an overflow
		// if the result.lo is not 64 bit.
		long o_lo = other.getLow();
		long o_hi = other.getHigh();
		boolean sureCarry = (low<0) && (o_lo<0);
		long r_hi = high + o_hi;
		long r_lo = low + o_lo;
		if (sureCarry) {
			r_hi++;
		} else {
			boolean checkCarry = (low<0) || (o_lo<0);
			if (checkCarry && r_lo >= 0) {
				r_hi++;
			}
		}
		return new Uint128(r_hi, r_lo);
	}

	/**
	 * Shift this 'bits' bits to the left.
	 * @param bits
	 * @return
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
	 * @return
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
	 * @param mask
	 * @return this & other
	 */
	public long and(long other) {
		return low & other;
	}

	/**
	 * Convert this to BigInteger.
	 * @return
	 */
	public BigInteger toBigInteger() {
		return new BigInteger(Long.toBinaryString(high), 2).shiftLeft(64).add(new BigInteger(Long.toBinaryString(low), 2));
	}
	
	@Override
	public String toString() {
		return toBigInteger().toString();
	}
	
	/**
	 * Test.
	 * @param args ignored
	 */
//	public static void main(String[] args) {
//		ConfigUtil.initProject();
//		SecureRandom RNG = new SecureRandom();
//		
//		for (int i=0; i<100000; i++) {
//			BigInteger a_hi_big = new BigInteger(63, RNG);
//			BigInteger a_lo_big = new BigInteger(64, RNG);
//			BigInteger b_hi_big = new BigInteger(63, RNG);
//			BigInteger b_lo_big = new BigInteger(64, RNG);
//			
//			long a_hi = a_hi_big.longValue();
//			long a_lo = a_lo_big.longValue();
//			long b_hi = b_hi_big.longValue();
//			long b_lo = b_lo_big.longValue();
//			
//			// test addition
//			Uint128 a128 = new Uint128(a_hi, a_lo);
//			Uint128 b128 = new Uint128(b_hi, b_lo);
//			Uint128 sum128 = a128.add(b128);
//			BigInteger sum128Big = sum128.toBigInteger();
//			BigInteger sumBig = a128.toBigInteger().add(b128.toBigInteger());
//			assertEquals(sumBig, sum128Big);
//
//			// test multiplication with 63 bit numbers
//			Uint128 prod128 = mul63(a_hi, b_hi);
//			BigInteger prod128Big = prod128.toBigInteger();
//			BigInteger prodBig = a_hi_big.multiply(b_hi_big);
//			assertEquals(prodBig, prod128Big);
//
//			// test multiplication with 64 bit numbers
//			prod128 = mul64(a_lo, b_lo);
//			prod128Big = prod128.toBigInteger();
//			prodBig = a_lo_big.multiply(b_lo_big);
//			//LOG.debug("Test " + a_lo_big + "*" + b_lo_big + ":");
//			//LOG.debug("BigInt result = " + prodBig);
//			//LOG.debug("int127 result = " + prod128Big);
//			if (!prodBig.equals(prod128Big)) {
//				LOG.debug("error! diff = " + prodBig.subtract(prod128Big));
//			}
//			assertEquals(prodBig, prod128Big);
//		}
//	}
}
