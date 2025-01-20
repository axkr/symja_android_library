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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.precision.Magnitude;
import de.tilman_neumann.jml.precision.Precision;
import de.tilman_neumann.jml.precision.Scale;

/**
 * Basic BigDecimal arithmetics.
 * 
 * @author Tilman Neumann
 */
public class BigDecimalMath {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(BigDecimalMath.class);

	private BigDecimalMath() {
		// static class
	}
	
	/**
	 * Computes the sum of a and b accurate to the given resultScale.<br>
	 * 
	 * Scale is the natural accuracy measure for additions because
	 * for each argument, each piece of it (bit, digit, ...) makes its own
	 * independent contribution to the result scale.
	 * 
	 * @param a
	 * @param b
	 * @param resultScale
	 * @return sum
	 */
	public static BigDecimal add(BigDecimal a, BigRational b, Scale resultScale) {
		Scale internalScale = resultScale.add(1); // for exact rounding
		BigDecimal result = a.add(b.toBigDecimal(internalScale));
		return resultScale.applyTo(result);
	}

	public static BigDecimal add(BigDecimal a, BigInteger b) {
		return a.add(new BigDecimal(b));
	}

	/**
	 * Computes the difference of a and b.
	 * @param a
	 * @param b
	 * @return a-b
	 */
	public static BigDecimal subtract(BigDecimal a, BigInteger b) {
		return a.subtract(new BigDecimal(b));
	}
	
	/**
	 * Computes the product of a and b.<br>
	 * 
	 * Precision is the natural accuracy measure for multiplications because
	 * for each argument, each piece of it (bit, digit, ...) makes its own
	 * independent contribution to the result precision.
	 * 
	 * @param a
	 * @param b
	 * @param resultPrecision
	 * @return product
	 */
	public static BigDecimal multiply(BigDecimal a, BigRational b, Precision resultPrecision) {
		Precision internalPrecision = resultPrecision.add(1);
		BigDecimal result = a.multiply(b.toBigDecimal(internalPrecision));
		return resultPrecision.applyTo(result);
	}

	public static BigDecimal multiply(BigDecimal a, BigRational b, Scale resultScale) {
		int resultMagnitude = Magnitude.of(a) + Magnitude.of(b);
		Precision resultPrecision = Precision.valueOf(Math.max(0, resultScale.digits() + resultMagnitude));
		return multiply(a, b, resultPrecision);
	}
	
	/**
	 * Multiplication without precision loss.
	 * 
	 * @param a big float
	 * @param b big integer
	 * @return a*b
	 */
	public static BigDecimal multiply(BigDecimal a, BigInteger b) {
		BigDecimal ret = new BigDecimal(a.unscaledValue().multiply(b), a.scale());
		//LOG.debug(a + " * " + b + " = " + ret);
		return ret;
	}

	/**
	 * Computes the product a*b without precision loss.
	 * 
	 * @param a
	 * @param b
	 * @return a*b
	 */
	public static BigDecimal multiply(BigDecimal a, long b) {
		BigDecimal ret = new BigDecimal(a.unscaledValue().multiply(BigInteger.valueOf(b)), a.scale());
		//LOG.debug(a + " * " + b + " = " + ret);
		return ret;
	}

	/**
	 * Division with guaranteed precision.
	 * 
	 * @param a dividend
	 * @param b divisor
	 * @param resultScale result accuracy in decimal digits after the floating point
	 * @return a / b
	 */
	public static BigDecimal divide(BigDecimal a, BigDecimal b, Scale resultScale) {
		// eventually we could reduce the scale of a and b before the operation
		// BigDecimal.divide() takes scale as parameter
		BigDecimal result = a.divide(b, resultScale.digits(), RoundingMode.HALF_EVEN);
		return result.setScale(resultScale.digits());
	}

	public static BigDecimal divide(BigDecimal a, BigDecimal b, Precision resultPrecision) {
		// compute result scale from result precision
		int resultMagnitude = Magnitude.of(a) - Magnitude.of(b);
		Scale resultScale = Scale.valueOf(resultPrecision.digits() - resultMagnitude);
		return divide(a, b, resultScale);
	}
	
	/**
	 * Division by an integer.
	 * 
	 * @param a dividend
	 * @param b divisor
	 * @param resultScale result accuracy in decimal digits after the floating point
	 * @return quotient with wanted precision.
	 */
	public static BigDecimal divide(BigDecimal a, BigInteger b, Scale resultScale) {
		return divide(a, new BigDecimal(b), resultScale);
	}

	public static BigDecimal divide(BigDecimal a, BigInteger b, Precision resultPrecision) {
		return divide(a, new BigDecimal(b), resultPrecision);
	}
	
	/**
	 * Returns the fractional part of x, with the same scale than x.
	 * @param x
	 * @return frac(x)
	 */
	// TODO: Check if there is a rounding problem similar to the one commented below...
	public static BigDecimal frac(BigDecimal x) {
		return x.subtract(x.setScale(0, RoundingMode.FLOOR));
	}
	
	/**
	 * Round x to the nearest integer.
	 * @param x
	 * @return round(x)
	 */
	public static BigInteger roundInt(BigDecimal x) {
		return x.setScale(0, RoundingMode.HALF_EVEN).toBigIntegerExact();
	}

	/**
	 * Returns ceil(x) as a big integer.
	 * @param x
	 * @return ceil(x)
	 */
	// TODO: Can give 71.000000000000000000000540264914491376961 -> 72 instead of 71
	public static BigInteger ceilInt(BigDecimal x) {
		return x.setScale(0, RoundingMode.CEILING).toBigIntegerExact();
	}
	
	/**
	 * Returns floor(x) as a big integer.
	 * @param x
	 * @return floor(x)
	 */
	// TODO: Can give 71.9999999999999999999977540264914491376961 -> 71 instead of 72
	public static BigInteger floorInt(BigDecimal x) {
		return x.setScale(0, RoundingMode.FLOOR).toBigIntegerExact();
	}

	// Comparison -------------------------------------------------------------------
	
	/**
     * @param a
     * @param b
     * @return <0/0/>0 if a is smaller/equal/greater than b.
     */
	public static int compare(BigDecimal a, BigInteger b) {
		return a.compareTo(new BigDecimal(b));
	}

}
