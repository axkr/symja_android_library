/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2022 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.random;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.precision.Magnitude;
import de.tilman_neumann.jml.precision.Precision;
import de.tilman_neumann.jml.transcendental.Exp;
import de.tilman_neumann.jml.transcendental.Ln;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
import static de.tilman_neumann.jml.base.BigDecimalConstants.*;

/**
 * Generator for pseudo-random natural and floating point numbers.
 * @author Tilman Neumann
 */
public class Rng extends Random {

	private static final long serialVersionUID = 1821148670513516540L;

	private static final Logger LOG = LogManager.getLogger(Rng.class);

	private static final boolean DEBUG = true; // XXX some improvement seems to be possible here
	
	public Rng() {
		super();
	}
	
	public Rng(long seed) {
		super(seed);
	}
	
	/**
	 * Creates a random integer from the uniform distribution U[minValue, maxValue-1].
	 * Works also for negative arguments; the only requirement is maxValue > minValue.
	 * @param minValue
	 * @param maxValue
	 * @return random int in the desired range
	 */
	public int nextInt(int minValue, int maxValue) {
		return minValue + nextInt(maxValue - minValue);
	}

	/**
	 * Creates a random long from the uniform distribution U[0, maxValue-1].
	 * The only requirement is maxValue > 0.
	 * 
	 * The numbers produced by java.util.Random.nextLong(bound) have better quality, but that needs Java 17.
	 * 
	 * @param maxValue
	 * @return random long in the desired range
	 */
	public long nextLong(long maxValue) {
		if (maxValue < 1) {
			throw new IllegalArgumentException("maxValue = " + maxValue + " is not positive");
		}
		
		long rand = nextLong(); // Long.MIN_VALUE...Long.MAX_VALUE
		long normalized; // shall become 0...(max-1)
		if (rand >= 0) {
			normalized = rand % maxValue;
		} else if (rand > Long.MIN_VALUE) {
			normalized = (-rand) % maxValue;
		} else { // special treatment because -Long.MIN_VALUE == Long.MIN_VALUE
			normalized = Long.MAX_VALUE % maxValue;
		}
		return normalized;
	}

	/**
	 * Creates a random long from the uniform distribution U[minValue, maxValue-1].
	 * Works also for negative arguments; the only requirement is maxValue > minValue.
	 * @param minValue
	 * @param maxValue
	 * @return random long in the desired range
	 */
	public long nextLong(long minValue, long maxValue) {
		return minValue + nextLong(maxValue - minValue);
	}
	
	/**
	 * Generates a BigInteger from the uniform distribution U[0, maxValue-1].
	 * @param maxValue exclusive
	 * @return
	 */
	public BigInteger nextBigInteger(BigInteger maxValue) {
		int maxBits = maxValue.bitLength()+1;
		
		// do we use the best upper bound? Does not look like that yet!
		BigInteger twoPowMaxBits = I_1.shiftLeft(maxBits);
		if (DEBUG) {
			LOG.debug("rng: maxValue  = " + maxValue);
			LOG.debug("rng: 2^maxBits = " + twoPowMaxBits);
		}
		
		BigInteger randomValue = null;
		do {
			randomValue = nextBigInteger(maxBits); // 0 ... 2^maxBits - 1
		} while (randomValue.compareTo(maxValue) >= 0); // too big
		return randomValue;
	}
	
	/**
	 * Generates a BigInteger from the uniform distribution U[minValue, maxValue-1].
	 * @param minValue inclusive
	 * @param maxValue exclusive
	 * @return
	 */
	public BigInteger nextBigInteger(BigInteger minValue, BigInteger maxValue) {
		BigInteger diff = maxValue.subtract(minValue);
		if (diff.signum() < 1) throw new IllegalArgumentException("maxValue=" + maxValue + " must be bigger than minValue=" + minValue);
		return nextBigInteger(diff).add(minValue);
	}

	/**
	 * Get uniformly distributed random integer with the specified maximum number of bits,
	 * i.e. a number from U[0, 2^maxBits - 1].
	 * 
	 * @param maxBits size in bits
	 */
	public BigInteger nextBigInteger(int maxBits) {
		if (maxBits<0) throw new IllegalArgumentException("maxBits=" + maxBits + " but must be non-negative");
		return new BigInteger(maxBits, this);
	}
	
	/**
	 * Generates a random BigInteger from the log-distribution in [0, maxValue-1].
	 * 
	 * The generation is quite expensive, because it involves exp() and ln() computations.
	 * 
	 * @param maxValue
	 * @return
	 */
	public BigInteger nextLogDistributedBigInteger(BigInteger maxValue) {
		int logDigits = Magnitude.of(maxValue) + 1;
		Precision logDigitsPrecision = Precision.valueOf(logDigits);
		// scale 1..maxValue -> 0..ln(maxValue)
		BigDecimal normedLogMaxValue = Ln.ln(new BigDecimal(maxValue), logDigitsPrecision);
		// get uniform random value from log scale
		BigDecimal randomLogValue = nextBigDecimal(normedLogMaxValue, logDigits);
		// exponentiation gives log-distributed random on original range 1..maxValue
		// subtraction of 1 gives log-random from [0..maxValue-1]
		BigInteger randomValue = Exp.exp(randomLogValue, logDigitsPrecision).toBigInteger().subtract(I_1);
		return randomValue;
	}
	
	/**
	 * Generates a random BigInteger from the log-distribution in [minValue, maxValue-1].
	 * 
	 * The generation is quite expensive, because it involves exp() and ln() computations.
	 * 
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	public BigInteger nextLogDistributedBigInteger(BigInteger minValue, BigInteger maxValue) {
		int logDigits = Magnitude.of(maxValue) + 1;
		Precision logDigitsPrecision = Precision.valueOf(logDigits);
		BigDecimal logMinValue = (minValue.compareTo(I_1)!=0) ? Ln.ln(new BigDecimal(minValue), logDigitsPrecision) : F_0;
		BigDecimal logMaxValue = Ln.ln(new BigDecimal(maxValue), logDigitsPrecision);
		BigDecimal normedLogMaxValue = logMaxValue.subtract(logMinValue);
		BigDecimal randomLogValue = nextBigDecimal(normedLogMaxValue, logDigits);
		BigInteger randomValue = Exp.exp(randomLogValue, logDigitsPrecision).toBigInteger().add(minValue);
		return randomValue;
	}

	/**
	 * Generates a BigDecimal from [0, maxValue) with the given number of decimal digits
	 * after the floating point.
	 * 
	 * @param maxValue exclusive
	 * @param resultScale
	 * @return
	 */
	public BigDecimal nextBigDecimal(BigDecimal maxValue, int resultScale) {
		// the biggest value we can resolve at result scale is maxValue with that scale rounded down.
		BigDecimal maxValueWithResultScale = maxValue.setScale(resultScale, RoundingMode.FLOOR);
		BigInteger maxInt = maxValueWithResultScale.unscaledValue();
		BigInteger resultInt = nextBigInteger(maxInt.add(I_1)); // 0..maxInt
		return new BigDecimal(resultInt, resultScale);
	}

	/**
	 * Generates a BigDecimal from U[0, maxValue) with minDigits...maxDigits decimal digits
	 * after the floating point.
	 * 
	 * @param maxValue exclusive
	 * @param minDigits
	 * @param maxDigits
	 * @return
	 */
	public BigDecimal nextBigDecimal(BigDecimal maxValue, int minScale, int maxScale) {
		int resultScale = nextInt(minScale, maxScale+1); // minScale...maxScale
		return nextBigDecimal(maxValue, resultScale);
	}
	
	/**
	 * Generates a BigDecimal from U[minValue, maxValue) with minDigits...maxDigits decimal digits
	 * after the floating point.
	 * 
	 * @param minValue inclusive
	 * @param maxValue exclusive
	 * @param minScale
	 * @param maxScale
	 * @return
	 */
	public BigDecimal nextBigDecimal(BigDecimal minValue, BigDecimal maxValue, int minScale, int maxScale) {
		int resultScale = nextInt(minScale, maxScale+1); // minScale...maxScale
		BigDecimal normedMaxValue = maxValue.subtract(minValue);
		BigDecimal normedResultValue = nextBigDecimal(normedMaxValue, resultScale);
		return normedResultValue.add(minValue).setScale(resultScale);
	}
	
	/**
	 * Generates a random BigDecimal from the log-distribution in [minValue, maxValue)
	 * that has minScale..maxScale digits after the floating point.
	 * 
	 * The generation is quite expensive, because it involves exp() and ln() computations.
	 * 
	 * @param minValue
	 * @param maxValue
	 * @param minScale
	 * @param maxScale
	 * @return
	 */
	public BigDecimal nextLogDistributedBigDecimal(BigDecimal minValue, BigDecimal maxValue, int minScale, int maxScale) {
		Precision logDigits = Precision.valueOf(Magnitude.of(maxValue) + maxScale);
		BigDecimal logMinValue = Ln.ln(minValue, logDigits);
		BigDecimal logMaxValue = Ln.ln(maxValue, logDigits);
		BigDecimal normedLogMaxValue = logMaxValue.subtract(logMinValue);
		BigDecimal randomLogValue = nextBigDecimal(normedLogMaxValue, maxScale);
		BigDecimal randomValue = Exp.exp(randomLogValue, logDigits).add(minValue);
		int scale = nextInt(minScale, maxScale+1);
		return randomValue.setScale(scale, RoundingMode.HALF_EVEN);
	}
}
