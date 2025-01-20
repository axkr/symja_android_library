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
package de.tilman_neumann.jml.roots;

import java.math.BigDecimal;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.base.BigDecimalMath;
import de.tilman_neumann.jml.precision.Magnitude;
import de.tilman_neumann.jml.precision.Scale;
import de.tilman_neumann.jml.powers.Pow2;

import static de.tilman_neumann.jml.base.BigDecimalConstants.F_0;
import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Compute square root of large numbers using Heron's method with a good initial guess.
 * Adapted from http://www.merriampark.com/bigsqrt.htm
 *  
 * @author Tilman Neumann
 */
public class SqrtReal {
	private static final Logger LOG = LogManager.getLogger(SqrtReal.class);
	private static final boolean DEBUG = false;
	
	/**
	 * Compute square root.
	 * 
	 * @param x argument
	 * @param resultScale desired precision in after-comma digits
	 * @return sqrt(x) with error < 0.5*10^-resultScale, i.e. resultScale decimal digits are rounded correctly
	 */
	public static BigDecimal sqrt(BigDecimal x, Scale resultScale) {
		// get initial guess correct to double precision (52 bit)
		BigDecimal guess = getInitialApproximation(x);
		if (DEBUG) LOG.debug("initial guess: sqrt(" + x + ") ~ " + guess);
		// iteration
		return sqrt(x, guess, resultScale);
  	}

	/**
	 * Get initial approximation.
	 * @param x argument
	 */
	private static BigDecimal getInitialApproximation(BigDecimal x) {
		// too big arguments are shifted right an even number of times so that the result fits into double
		int xMag = Magnitude.of(x);
		int shiftsRight = 0;
		if (xMag > 307) { // Double.MAX_VALUE is about 1.7*10^308
			shiftsRight = xMag - 307;
			if ((shiftsRight&1) == 1) {
				shiftsRight++;
			}
		} else if (xMag < -307) {
			shiftsRight = xMag + 307;
			if ((shiftsRight&1) == 1) {
				shiftsRight++;
			}
		}
		
		double xShifted_dbl = x.movePointLeft(shiftsRight).doubleValue();
		// compute double estimate
		double sqrt_dbl = Math.sqrt(xShifted_dbl);
		// shift left half the number of right shifts
		BigDecimal guess = new BigDecimal(sqrt_dbl).movePointRight(shiftsRight>>1);
		if (guess.equals(BigDecimal.ZERO)) {
			// double precision was not enough, avoid division by zero in the approximation loop
			guess = new BigDecimal(I_1, 52);
		}
		return guess;
	}

	/**
	 * Compute square root with initial guess.
	 * 
	 * @param x argument
	 * @param guess initial guess of sqrt(x)
	 * @param resultScale desired precision in after-comma digits
	 * @return sqrt(x) with error < 0.5*10^-resultScale, i.e. resultScale decimal digits are rounded correctly
	 */
	public static BigDecimal sqrt(BigDecimal x, BigDecimal guess, Scale resultScale) {
		// Make sure x is a positive number
		int cmpToZero = x.compareTo(F_0);
		if (cmpToZero > 0) {
			BigDecimal lastGuess;
			BigDecimal maxAllowedError = resultScale.getErrorBound();
			
			// Iterate while error too big
			Scale internalScale = resultScale.add(1);
			BigDecimal error;
			
			do {
				lastGuess = guess;
				guess = BigDecimalMath.divide(x, guess, internalScale);
				guess = Pow2.divPow2(guess.add(lastGuess), 1);
				if (DEBUG) LOG.debug("next guess: sqrt(" + x + ") ~ " + guess);
				error = guess.subtract(lastGuess).abs();
				if (DEBUG) LOG.debug("error = " + error);
			} while (error.compareTo(maxAllowedError)>=0);
	    
			return resultScale.applyTo(guess);
		}
		if (cmpToZero == 0) {
			return F_0;
		}
		
		throw new IllegalArgumentException("x = " + x + ", but sqrt(x) is defined for x>=0 only!");
  	}
}
