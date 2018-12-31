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

/**
 * Rudimentary 127 bit int implementation.
 * 
 * @author Tilman Neumann
 */
public class Int127 {
//	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(Int127.class);
	
	private long high, low;
	
	public Int127(long high, long low) {
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
	 * Multiplication of 64 bit integers,
	 * following https://stackoverflow.com/questions/18859207/high-bits-of-long-multiplication-in-java
	 * @param x
	 * @param y
	 * @return
	 */
	// TODO fails for 64 bit inputs
	public static Int127 mul64(long x, long y) {
		final long x_hi = x >>> 32;
		final long y_hi = y >>> 32;
		final long x_lo = x & 0xFFFFFFFFL;
		final long y_lo = y & 0xFFFFFFFFL;
		final long lo_prod = x_lo * y_lo;
		final long med_term = x_hi * y_lo + x_lo * y_hi;
		final long hi_prod = x_hi * y_hi;
		long r_hi = (((lo_prod >>> 32) + med_term) >>> 32) + hi_prod;
		long r_lo = ((med_term & 0xFFFFFFFFL) << 32) + lo_prod;
		return new Int127(r_hi, r_lo);
	}

	/**
	 * Computes the low part of the product of two 64 bit integers.
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
	 * Add two 127 bit integers.
	 * @param other
	 * @return this + other
	 */
	public Int127 add(Int127 other) {
		// We know for sure that low overflows if both low and o_lo are 64 bit.
		// If only one of the input 'low's is 64 bit, then we can recognize an overflow
		// if the result.lo is not 64 bit.
		long o_lo = other.getLow();
		long o_hi = other.getHigh();
		boolean sureCarry = (low<0) && (o_lo<0);
		long r_hi, r_lo;
		if (sureCarry) {
			r_hi = high + o_hi + 1;
			r_lo = low + o_lo; // overflow bit gets dropped, no masks required
		} else {
			boolean checkCarry = (low<0) || (o_lo<0);
			r_hi = high + o_hi;
			r_lo = low + o_lo;
			if (checkCarry) {
				//LOG.debug("check carry!");
				if (r_lo >= 0) {
					// low overflow!
					//LOG.debug("low overflow!");
					r_hi++;
				}
			}
		}
		//LOG.debug("low = " + Long.toBinaryString(low));
		//LOG.debug("o_lo = " + Long.toBinaryString(o_lo));
		//LOG.debug("r_lo = " + Long.toBinaryString(r_lo));
		return new Int127(r_hi, r_lo);
	}
	
	/**
	 * Shift this 'bits' bits to the left.
	 * @param bits
	 * @return
	 */
	public Int127 shiftLeft(int bits) {
		if (bits<64) {
			long rh = (high<<bits) | (low>>>(64-bits));
			long rl = low<<bits;
			return new Int127(rh, rl);
		}
		return new Int127(low<<(bits-64), 0);
	}
	
	/**
	 * Shift this 'bits' bits to the right.
	 * @param bits
	 * @return
	 */
	public Int127 shiftRight(int bits) {
		if (bits<64) {
			long rh = high>>>bits;
			long rl = (low>>>bits) | (high<<(64-bits));
			return new Int127(rh, rl);
		}
		return new Int127(0, high>>>(bits-64));
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
//			Int127 a128 = new Int127(a_hi, a_lo);
//			Int127 b128 = new Int127(b_hi, b_lo);
//			Int127 sum128 = a128.add(b128);
//			BigInteger sum128Big = sum128.toBigInteger();
//			BigInteger sumBig = a128.toBigInteger().add(b128.toBigInteger());
//			assertEquals(sumBig, sum128Big);
//
//			// test multiplication
//			Int127 mul128 = mul64(a_hi, b_hi);
//			BigInteger mul128Big = mul128.toBigInteger();
//			BigInteger mulBig = a_hi_big.multiply(b_hi_big);
//			assertEquals(mulBig, mul128Big);
//		}
//	}
}
