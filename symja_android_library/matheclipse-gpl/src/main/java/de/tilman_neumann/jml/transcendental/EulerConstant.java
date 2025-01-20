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
package de.tilman_neumann.jml.transcendental;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.base.BigDecimalMath;
import de.tilman_neumann.jml.precision.Magnitude;
import de.tilman_neumann.jml.precision.Scale;

// TODO there must be faster formulas to be able to compute billions of digits as has been done
public class EulerConstant {
	private static final Logger LOG = LogManager.getLogger(EulerConstant.class);

	private static final boolean DEBUG = false;
	
	/** Euler's constant with 200 digits precision */
	private static BigDecimal EULER = new BigDecimal("0.57721566490153286060651209008240243104215933593992359880576723488486772677766467093694706329174674951463144724980708248096050401448654283622417399764492353625350033374293733773767394279259525824709491");
	private static Scale EULER_SCALE = Scale.valueOf(200);
	
	public static BigDecimal gamma(Scale outScale) {
	    //LOG.debug("outScale = " + outScale);
	    if (outScale.compareTo(EULER_SCALE) > 0) {
	    	// in case that this method is called in a loop with slowly increasing scale, precompute gamma with a much bigger scale
	    	EULER_SCALE = Scale.valueOf(Math.max(EULER_SCALE.digits()<<1, outScale.digits()));
	    	EULER = gamma_v2(EULER_SCALE);
	    }

	    // assign output:
	    return outScale.applyTo(EULER);
	}

	/**
	 * Compute Eulers gamma constant via the formula
	 * 
	 * gamma = 1 - log k * sum_(r=1..12k+1) [(-1)^(r-1)*k^(r+1)]/[(r-1)!*(r+1)] + sum_(r=1..12k+1) [(-1)^(r-1)*k^(r+1)]/[(r-1)!*(r+1)^2] + O(2^-k)
	 * 
	 * <=> gamma = 1 - log k * sum_(r=1..12k+1) (-k)^(r+1)/[(r-1)!*(r+1)] + sum_(r=1..12k+1) (-k)^(r+1)/[(r-1)!*(r+1)^2] + O(2^-k)
	 *
	 * https://math.stackexchange.com/questions/129777/what-is-the-fastest-most-efficient-algorithm-for-estimating-eulers-constant-g?noredirect=1&lq=1
	 *
	 * I realized that the first sum converges to 1 with error 2^(-sqrt(2)*k).
	 * Thus replacing the first sum by 1 gives a faster algorithm, implemented in gamma_v2().
	 * 
	 * @param scale
	 * @return
	 */
	static BigDecimal gamma_v1(Scale scale) {
		int k = Magnitude.decimalToBinary((int) (scale.digits()/1.41421356) + 2);
		double lnkEstimate =  Magnitude.of(k)*Math.log(10);
		int lnkMagnitude = lnkEstimate!=0 ? Magnitude.of(lnkEstimate) : 0;
		Scale internalScale = scale.add(lnkMagnitude + 2);

		// start with r=1: num = (-k)^2, den1 = 0! * 2^1 = 2, den2 = 0! * 2^2 = 4
		BigInteger minusK = BigInteger.valueOf(k).negate();
		BigInteger num = minusK.multiply(minusK);
		BigDecimal sum1 = BigDecimalMath.divide(new BigDecimal(num), I_2, internalScale);
		BigDecimal sum2 = BigDecimalMath.divide(new BigDecimal(num), I_4, internalScale);
		
		// loop
		BigInteger factorial = I_1;
		int rMax = 12*k+1;
		for (int r=2; r<=rMax; r++) {
			// numerator
			num = num.multiply(minusK);
			// denominators
			factorial = factorial.multiply(BigInteger.valueOf(r-1));
			BigInteger rp1 = BigInteger.valueOf(r+1);
			BigInteger rp1Square = rp1.multiply(rp1);
			BigInteger den1 = factorial.multiply(rp1);
			BigInteger den2 = factorial.multiply(rp1Square);
			// add to sums
			BigDecimal elem1 = BigDecimalMath.divide(new BigDecimal(num), den1, internalScale);
			sum1 = sum1.add(elem1);
			BigDecimal elem2 = BigDecimalMath.divide(new BigDecimal(num), den2, internalScale);
			sum2 = sum2.add(elem2);
		}
		
		BigDecimal lnK = Ln.ln(BigDecimal.valueOf(k), internalScale);
		if (DEBUG) {
			LOG.debug("sum1 = " + sum1); // this is very near to 1 -> the sum can be dropped!
			LOG.debug("sum2 = " + sum2);
			LOG.debug("lnK = " + lnK);
		}
		
		BigDecimal gamma = BigDecimal.ONE.subtract(lnK.multiply(sum1)).add(sum2);
		return scale.applyTo(gamma);
	}

	/**
	 * Simplified implementation realizing that the first sum converges to 1. So here we use the approximation
	 * 
	 * gamma = 1 - log k + sum_(r=1..12k+1) (-k)^(r+1)/[(r-1)!*(r+1)^2] + O(2^-k)
	 * 
	 * @param scale
	 * @return Eulers gamma constant
	 */
	static BigDecimal gamma_v2(Scale scale) {
		int k = Magnitude.decimalToBinary((int) (scale.digits()/1.41421356) + 2);
		double lnkEstimate =  Magnitude.of(k)*Math.log(10);
		int lnkMagnitude = lnkEstimate!=0 ? Magnitude.of(lnkEstimate) : 0;
		Scale internalScale = scale.add(lnkMagnitude + 1);
		
		// start with r=1: num = (-k)^2, den1 = 0! * 2^1 = 2, den2 = 0! * 2^2 = 4
		BigInteger minusK = BigInteger.valueOf(k).negate();
		BigInteger num = minusK.multiply(minusK);
		BigDecimal sum2 = BigDecimalMath.divide(new BigDecimal(num), I_4, internalScale);
		
		// loop
		BigInteger factorial = I_1;
		int rMax = 12*k+1;
		for (int r=2; r<=rMax; r++) {
			// numerator
			num = num.multiply(minusK);
			// denominators
			factorial = factorial.multiply(BigInteger.valueOf(r-1));
			BigInteger rp1 = BigInteger.valueOf(r+1);
			BigInteger rp1Square = rp1.multiply(rp1);
			BigInteger den2 = factorial.multiply(rp1Square);
			// add to sums
			BigDecimal elem2 = BigDecimalMath.divide(new BigDecimal(num), den2, internalScale);
			sum2 = sum2.add(elem2);
		}
		
		BigDecimal lnK = Ln.ln(BigDecimal.valueOf(k), internalScale);
		if (DEBUG) {
			LOG.debug("sum2 = " + sum2);
			LOG.debug("lnK = " + lnK);
		}
		
		BigDecimal gamma = BigDecimal.ONE.subtract(lnK).add(sum2);
		return scale.applyTo(gamma);
	}
}
