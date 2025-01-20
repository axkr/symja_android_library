/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.precision;

import java.math.BigDecimal;
import java.math.BigInteger;

import de.tilman_neumann.jml.base.BigRational;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

public class Magnitude {
	
	/** Multiplier to convert log2-values to log10-values. */
	public static final double LOG2_TO_LOG10_MULTIPLIER = Math.log10(2);
	/** Multiplier to convert log10-values to log2-values. */
	public static final double LOG10_TO_LOG2_MULTIPLIER = 1D/Math.log10(2);
	
	static final int ZERO_FLOAT_MAGNITUDE = (int) Math.ceil(Math.log10(Float.MIN_VALUE)-1);
	static final int ZERO_DOUBLE_MAGNITUDE = (int) Math.ceil(Math.log10(Double.MIN_VALUE)-1);
	
	// magnitude ---------------------------------------------------------------------
	
	public static int of(long x) {
		return x!=0 ? String.valueOf(Math.abs(x)).length() : 0;
	}

	public static int of(float x) {
		return x!=0 ? (int) Math.ceil(Math.log10(Math.abs(x))) : ZERO_FLOAT_MAGNITUDE;
	}
	
	public static int of(double x) {
		return x!=0 ? (int) Math.ceil(Math.log10(Math.abs(x))) : ZERO_DOUBLE_MAGNITUDE;
	}

	public static int of(BigDecimal x) {
		return (x.signum()!=0 ? x.precision() : 0) - x.scale();
	}

	public static int of(BigRational q) {
		return Magnitude.of(q.getNumerator()) - Magnitude.of(q.getDenominator());
	}
	
	/**
	 * Gives the absolute size of n in decimal digits.
	 * Fast for an exact implementation.
	 * @param n
	 * @return magnitude of n in decimal digits
	 */
	public static int of/*digits_fromBigDecimal*/(BigInteger n) {
		return n.compareTo(I_0)!=0 ? new BigDecimal(n).precision() : 0;
	}
	
	/**
	 * Gives the absolute size of n in decimal digits.
	 * Fast for an exact implementation.
	 * @param n
	 * @return magnitude of n in decimal digits
	 */
	static int digits_fromBigDecimal2(BigInteger n) {
		// faster BigInteger.ZERO check?
		return n.signum()!=0 ? new BigDecimal(n).precision() : 0;
	}
	
	// around factor 10 slower than BigDecimal-based versions
	static int digits_toString(BigInteger n) {
		// BigInteger.toString() allows for String concatenation with "+"; thus it should be fine for this purpose, too.
		// however, toString() seems to be more expensive than creating a new BigDecimal...
		return n.compareTo(I_0)!=0 ? n.abs().toString().length() : 0;
	}
	
	// slow
	static int digits_toString2(BigInteger n) {
		// BigInteger.toString() allows for String concatenation with +
		// thus it should be fine for this purpose, too.
		// however, toString() seems to be more expensive then creating a new BigDecimal...
		return n.signum()!=0 ? n.abs().toString().length() : 0;
	}

	// slow
	static int digits_mulTest(BigInteger n) {
		int digits = (bitsOf(n)*3)/10; // lower bound
		BigInteger test = I_10.pow(digits);
		while(test.compareTo(n)<=0) {
			test = test.multiply(I_10);
			digits++;
		}
		return digits;
	}

	/**
	 * Gives the approximate number of decimal digits before the floating point.
	 * 
	 * The result is not exact for two reasons:
	 * First: We try to convert the bit length, but this is not enough information.
	 * Second: The used multiplier has double precision; so if a number has a
	 * bit length with more than 15 digits (which is quite a big number),
	 * then the result will be erroneous due to lack of precision.
	 * 
	 * @param x Argument
	 * @return Number of decimal digits
	 */
	static int digits_approximate(BigInteger x) {
		return (int) Math.ceil(LOG2_TO_LOG10_MULTIPLIER * bitsOf(x));
	}
	
	// now exact but slow
	static int digits_multiplier(BigInteger x) {
		int testDigits = (int) Math.ceil(LOG2_TO_LOG10_MULTIPLIER * bitsOf(x)) - 1;
		BigInteger test = I_10.pow(testDigits);
		return test.compareTo(x) > 0 ? testDigits : testDigits+1;
	}
	
	// now exact but slow
	static int digits_multiplier2(BigInteger x) {
		int testDigits = (int) (LOG2_TO_LOG10_MULTIPLIER * bitsOf(x));
		BigInteger test = I_10.pow(testDigits);
		return test.compareTo(x) > 0 ? testDigits : testDigits+1;
	}

	/**
	 * Gives the size of absolute |n| in bits:
	 * 0 for 0, 1 for +-1, 2 for +-2, 2 for +-3, 3 for +-4, ...
	 * @param n
	 * @return number of bits of |n|
	 */
	public static int bitsOf(BigInteger n) {
		// numbers of the form n = -2^k have one bit less than their abs() values, thus abs()
		// is the easiest way to treat negative numbers like their positive counterparts
		return n.abs().bitLength(); // gives 0 for ZERO, too
	}

	// Conversion --------------------------------------------------------------------------------
	
	/**
	 * Computes the number of binary digits analogous to the specified
	 * number of decimal digits.
	 * @param decimalDigits Number of decimal digits
	 * @return number of binary digits
	 */
	public static int decimalToBinary(int decimalDigits) {
	    return( (int) Math.ceil(decimalDigits * LOG10_TO_LOG2_MULTIPLIER));
	}

	/**
	 * Compute the number of decimal digits analogous to the specified
	 * number of binary digits.
	 * @param binaryDigits Number of binary digits
	 * @return number of decimal digits
	 */
	public static int binaryToDecimal(int binaryDigits) {
	    return( (int) Math.ceil(binaryDigits * LOG2_TO_LOG10_MULTIPLIER));
	}
}
