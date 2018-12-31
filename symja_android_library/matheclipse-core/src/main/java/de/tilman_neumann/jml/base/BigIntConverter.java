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

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;

//import org.apache.log4j.Logger;

//import de.tilman_neumann.util.ConfigUtil;

/**
 * Conversion from doubles to BigInteger with minimal precision loss and no need of slow BigDecimal.
 * @see https://de.wikipedia.org/wiki/IEEE_754#Allgemeines
 * 
 * @author Tilman Neumann
 */
public class BigIntConverter {
//	private static final Logger LOG = Logger.getLogger(BigIntConverter.class);
	
	/**
	 * Create a BigInteger from double, with minimal precision loss.
	 * @param d
	 * @return BigInteger
	 */
	public static BigInteger fromDouble(double d) {
		long dblBitRep = Double.doubleToRawLongBits(d);
		//LOG.debug("bits = " + bitString(dblBitRep));
		
		// Following the wikipedia page: x = s*m*2^e (with base b=2 in IEEE 754)
		// bit 63 represents the sign s
		boolean isNegative = (dblBitRep & 0x8000000000000000L) != 0;
		// Bits 62-52 (the bits that are selected by the mask 0x7ff0000000000000L) represent the exponent.
		// We have to subtract the fixed bias value (1023 for doubles) to get the real exponent e,
		// and use the unsigned shift operation >>> to forget the sign bit and shift in "0"s from the left.
		int e = (int) ((dblBitRep & 0x7ff0000000000000L) >>> 52) - 1023;
		// Bits 51-0 (the bits that are selected by the mask 0x000fffffffffffffL) 
		// represent the significand M (sometimes called the mantissa) of the floating-point number. 
		long M = dblBitRep & 0x000fffffffffffffL;
		// m = 1 + M/2^p with p=52 for doubles
		// => x = s*(1+M/2^p)*2^e = s * [2^e + M * 2^(e-p)]
		// Now we can reduce the loss of precision we'ld get converting M/2^p to integer !!
		// Note that shiftLeft(n) will do a right shift for negative n
		BigInteger result = I_1.shiftLeft(e).add(BigInteger.valueOf(M).shiftLeft(e-52));
		return (isNegative) ? result.negate() : result;
	}
	
	/**
	 * Compute BigInteger from double multiplied with a power of 2, i.e. result ~ d * 2^e2, with minimal precision loss.
	 * 
	 * @param d
	 * @param e2
	 * @return
	 */
	public static BigInteger fromDoubleMulPow2(double d, int e2) {
		long dblBitRep = Double.doubleToRawLongBits(d);
		//LOG.debug("bits = " + bitString(dblBitRep));
		
		// Following the wikipedia page: x = s*m*2^e (with base b=2 in IEEE 754)
		// bit 63 represents the sign s
		boolean isNegative = (dblBitRep & 0x8000000000000000L) != 0;
		// Bits 62-52 (the bits that are selected by the mask 0x7ff0000000000000L) represent the exponent.
		// We have to subtract the fixed bias value (1023 for doubles) to get the real exponent e,
		// and use the unsigned shift operation >>> to forget the sign bit and shift in "0"s from the left.
		int e = (int) ((dblBitRep & 0x7ff0000000000000L) >>> 52) - 1023;
		// Bits 51-0 (the bits that are selected by the mask 0x000fffffffffffffL) 
		// represent the significand M (sometimes called the mantissa) of the floating-point number. 
		long M = dblBitRep & 0x000fffffffffffffL;
		// m = 1 + M/2^p with p=52 for doubles
		// => x = s*(1+M/2^p)*2^e = s * [2^e + M * 2^(e-p)]
		// Now we can reduce the loss of precision we'ld get converting M/2^p to integer !!
		// Note that shiftLeft(n) will do a right shift for negative n.
		// Exponent e2 can simply be added, but will only have beneficial effects on the result precision if it is positive.
		BigInteger result = I_1.shiftLeft(e+e2).add(BigInteger.valueOf(M).shiftLeft(e+e2-52));
		return (isNegative) ? result.negate() : result;
	}

	@SuppressWarnings("unused")
	private static String bitString(long l) {
		String str = "";
		long mask = 0x8000000000000000L;
		for (int i=63; i>=0; i--) {
			long bit = l & mask;
			//LOG.debug("bit = " + bit);
			str += bit!=0 ? "1" : "0";
			if (i==63 || i==52) str += "|";
			mask >>>= 1; // unsigned shift -> shifts "0" into leftmost position
		}
		return str;
	}
	
//	private static void test(double d) {
//		LOG.info(d + " -> " + fromDouble(d));
//	}
	
//	private static void test(double d, int e2) {
//		LOG.info(d + " * 2^" + e2 + " -> " + fromDoubleMulPow2(d, e2));
//	}
	
//	public static void main(String[] args) {
//		ConfigUtil.initProject();
//		test(2); 
//		test(3);
//		test(3.1415);
//		test(5.99);
//		test(6.0001);
//		test(-6.0001);
//		test(-6.333, 4);
//		test(101.333, -4);
//	}
}
