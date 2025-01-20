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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.base.BigDecimalMath;
import de.tilman_neumann.jml.base.BigRational;
import de.tilman_neumann.jml.powers.Pow2;
import de.tilman_neumann.jml.precision.Magnitude;
import de.tilman_neumann.jml.precision.Scale;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
import static de.tilman_neumann.jml.base.BigDecimalConstants.F_0;

/**
 * Computations of Pi = 3.1415... to arbitrary precision.
 * @author Tilman Neumann
 */
public class Pi {
	private static final Logger LOG = LogManager.getLogger(Pi.class);
	private static final boolean DEBUG = false;

	/** PI computed to PI_SCALE decimal after-floating point digits. */
    private static BigDecimal PI;
    private static Scale PI_SCALE = Scale.valueOf(0);
    
	/**
	 * Compute Pi using the approximation formula found by Plouffe and the Borwein brothers also used in mpfr.
	 * The result is accurate to at least <code>scale</code> decimal after-floating point digits.
	 * 
	 * @param scale Wanted precision in decimal after-floating point digits
	 * @return PI = 3.1415...
	 */
    public static BigDecimal pi(Scale scale) {
	    if (scale.compareTo(PI_SCALE) > 0) {
	        // need to recompute Pi with higher precision...
            PI = F_0;
	        BigDecimal maxErr = scale.getErrorBound();
	        int i=0;
	        
        	// Do internal computations with a few extra digits to get the rounding right
	        Scale internalScale = scale.add(2);
	        BigDecimal sElement;
	        
	        do {
	            // Compute i.th series element: ----------------
		        // Denominator is 16^i == 2^(4*i)
	        	int four_i = i<<2, eight_i = i<<3;
		        BigDecimal den = Pow2.pow2(four_i);
		        int denMagnitude = Magnitude.of(den);
		        // The required numerator scale is internalScale + magnitude(den):
		        Scale numScale = internalScale.add(denMagnitude);
		        BigRational c1 = new BigRational(I_4, BigInteger.valueOf(eight_i+1));
		        BigRational c2 = new BigRational(I_1, BigInteger.valueOf(four_i+2));
		        BigRational c3 = new BigRational(I_1, BigInteger.valueOf(eight_i+5));
		        BigRational c4 = new BigRational(I_1, BigInteger.valueOf(eight_i+6));
	            sElement = c1.subtract(c2).subtract(c3).subtract(c4).toBigDecimal(numScale);
		        // Divide by denominator with the result having internalScale
	            sElement = BigDecimalMath.divide(sElement, den, internalScale);

	            // Add new series element to the total:
	            PI = PI.add(sElement);
	            if (DEBUG) LOG.debug("i=" + i + ", sElement=" + sElement + ", PI=" + PI);
	            i++;
	        // Stop if the last series element is smaller than the series computed
	        // so far multiplied with the desired relative error.
	        } while (sElement.compareTo(maxErr) > 0);
	        
	        // store PI with wished precision:
	        PI_SCALE = scale;
	    }

	    // assign output with wished accuracy:
	    return scale.applyTo(PI);
	}
}
