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
import de.tilman_neumann.jml.powers.Pow;
import de.tilman_neumann.jml.precision.Magnitude;
import de.tilman_neumann.jml.precision.Scale;

import static de.tilman_neumann.jml.base.BigDecimalConstants.F_0;

/**
 * i.th root of floating point numbers.
 * 
 * The current state-of-the-art is a Heron-style algorithm with a good initial guess derived from double computations.
 * The Heron-style algorithm realizes the iteration formula x(k+1) = 1/n * ( (n-1) * x(k) + N/(x(k)^(n-1)) ),
 * see {@link "https://en.wikipedia.org/wiki/Nth_root_algorithm"}.</br></br>
 *  
 * @author Tilman Neumann
 */
public class RootsReal {
	private static final Logger LOG = LogManager.getLogger(RootsReal.class);

	private static final boolean DEBUG = false;
	
	/**
	 * Compute i.th root of x.
	 * 
	 * @param x argument
	 * @param i the degree of the root
	 * @param resultScale desired precision in after-comma digits
	 * @return i.th root(x) with error < 0.5*10^-resultScale, i.e. resultScale decimal digits are rounded correctly
	 */
	public static BigDecimal ithRoot(BigDecimal x, int i, Scale resultScale) {
		// get initial guess correct to double precision (52 bit)
		BigDecimal guess = getInitialApproximation(x, i);
		if (DEBUG) LOG.debug("initial guess: " + i + ".th root(" + x + ") ~ " + guess);
		// iteration
		return ithRoot(x, i, guess, resultScale);
  	}

	/**
	 * Get initial approximation.
	 * @param x argument
	 * @param i the degree of the root
	 */
	private static BigDecimal getInitialApproximation(BigDecimal x, int i) {
		// x > 10^307 are shifted right an number of times that is a multiple of i so that the result has a double representation != 0
		// x < 10^-307 are shifted left an number of times that is a multiple of i so that the result has a double representation != 0
		// In big decimals, a left shift means to use movePointRight(), a right shift to use movePointLeft().
		// Shifts are done in decimal digits, not bits.
		int xMag = Magnitude.of(x);
		if (DEBUG) LOG.debug("xPrec=" + x.precision() + ", xScale=" + x.scale() + ", xMag=" + xMag);
		int shiftsRight = 0;
		if (xMag > 307) { // Double.MAX_VALUE is about 1.7*10^308
			shiftsRight = xMag - 307;
			int shiftsModi = shiftsRight % i;
			if (shiftsModi > 0) {
				shiftsRight += i - shiftsModi;
			}
		} else if (xMag < -307) {
			shiftsRight = xMag + 307;
			int shiftsModi = shiftsRight % i;
			if (shiftsModi > 0) {
				shiftsRight += i - shiftsModi;
			}
		}
		
		double xShifted_dbl = x.movePointLeft(shiftsRight).doubleValue();
		if (DEBUG) LOG.debug("shiftsRight=" + shiftsRight + ", xShifted_dbl=" + xShifted_dbl);
		// compute double estimate
		double root_dbl = Math.pow(xShifted_dbl, 1.0/i);
		// shift left the number of right shifts / i
		if (DEBUG) LOG.debug("i=" + i + ", shiftsLeft=" + shiftsRight/i);
		BigDecimal guess = new BigDecimal(root_dbl).movePointRight(shiftsRight/i);
		if (guess.equals(BigDecimal.ZERO)) {
			// double precision was not enough
			throw new ArithmeticException("root_dbl was 0 !?");
			//guess = new BigDecimal(I_1, 52); // old handling
		}
		return guess;
	}

	/**
	 * Compute the i.th root with initial guess.
	 * 
	 * @param x argument
	 * @param i degree of the root
	 * @param guess initial guess of i.th root(x)
	 * @param resultScale desired precision in after-comma digits
	 * @return i.th root(x) with error < 0.5*10^-resultScale, i.e. resultScale decimal digits are rounded correctly
	 */
	public static BigDecimal ithRoot(BigDecimal x, int i, BigDecimal guess, Scale resultScale) {
		// Make sure x is a positive number
		int cmpToZero = x.compareTo(F_0);
		if (cmpToZero > 0) {
			BigDecimal lastGuess;
			BigDecimal maxAllowedError = resultScale.getErrorBound();
			
			// Iterate while error too big
			Scale internalScale = resultScale.add(1);
			BigDecimal error;
			
			// x(k+1) = 1/i * ( (i-1) * x(k) + x(0)/(x(k)^(i-1)) )
			BigDecimal i_big = BigDecimal.valueOf(i);
			BigDecimal im1_big = BigDecimal.valueOf(i-1);
			do {
				lastGuess = guess;
				BigDecimal term1 = guess.multiply(im1_big);
				BigDecimal pow = Pow.pow(guess, i-1, internalScale);
				BigDecimal term2 = BigDecimalMath.divide(x, pow, internalScale);
				guess = BigDecimalMath.divide(term1.add(term2), i_big, internalScale);
				if (DEBUG) LOG.debug("next guess: " + i + ".th root(" + x + ") ~ " + guess);
				error = guess.subtract(lastGuess).abs();
				if (DEBUG) LOG.debug("error = " + error);
			} while (error.compareTo(maxAllowedError)>=0);
	    
			return resultScale.applyTo(guess);
		}
		if (cmpToZero == 0) {
			return F_0;
		}
		
		throw new IllegalArgumentException("x = " + x + ", but i.th root(x) is defined for x>=0 only!");
  	}
}
