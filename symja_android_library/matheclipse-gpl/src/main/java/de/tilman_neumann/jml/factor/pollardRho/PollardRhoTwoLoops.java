/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2025 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.factor.pollardRho;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorAlgorithm;

/**
 * A variant of the original Pollard-Rho method by Dave McGuigan,
 * using a second loop like in Pollard-Rho-Brent.
 * 
 * @author Dave McGuigan
 */
public class PollardRhoTwoLoops extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(PollardRhoTwoLoops.class);
	private static final boolean DEBUG = false;
	private static final Random RNG = new Random();
	
	private BigInteger N;

	@Override
	public String getName() {
		return "PollardRhoTwoLoops";
	}
	
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		this.N = N;
        BigInteger gcd;
		int bitLength = N.bitLength();
		// get random x0 from [0, N-1]
        BigInteger x = new BigInteger(bitLength, RNG);
        if (x.compareTo(N)>=0) x=x.subtract(N);

        do {
    		// get random c from [0, N-1]
        	BigInteger c = new BigInteger(bitLength, RNG);
            if (c.compareTo(N)>=0) c=c.subtract(N);
    		
            BigInteger xx = x;
	        BigInteger xs;
	        BigInteger xxs;
        	BigInteger prod = I_1;
        	int m = 100;
	        do {
	        	xs = x;
	        	xxs = xx;
		        for(int i=0; i<m; i++) {
		            x  = squareAddModN(x, c);
		            xx = squareAddModN(xx, c);
		            xx = squareAddModN(xx, c);
					// In BigInteger variants it is slightly faster to use the absolute value of the difference
    	            final BigInteger diff = x.compareTo(xx) < 0 ? xx.subtract(x) : x.subtract(xx);
		            prod = diff.multiply(prod).mod(N);
	        	}
	            gcd = prod.gcd(N);
	        } while (gcd.equals(I_1));
 	    	if (gcd.equals(N)) {
	    	    do {
		            xs  = squareAddModN(xs, c);
		            xxs = squareAddModN(xxs, c);
		            xxs = squareAddModN(xxs, c);
    	            final BigInteger diff = xs.compareTo(xxs) < 0 ? xxs.subtract(xs) : xs.subtract(xxs);
	    	        gcd = diff.gcd(N);
	    	    } while (gcd.equals(I_1));
	    	}	        
	    // leave loop if factor found; otherwise continue with a new random c
        } while (gcd.equals(N));
        if (DEBUG) LOG.debug("Found factor of " + N + " = " + gcd);
        return gcd;
	}
	
	/**
	 * Square and add modulo N, with <code>a, b < N</code>.
	 * @param y
	 * @param c
	 * @return () mod N
	 */
	private BigInteger squareAddModN(BigInteger y, BigInteger c) {
		return y.multiply(y).add(c).mod(N);
	}
}
