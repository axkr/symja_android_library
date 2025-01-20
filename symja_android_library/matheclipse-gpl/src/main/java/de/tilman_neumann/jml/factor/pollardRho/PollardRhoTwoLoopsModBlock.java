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
 * Another variant of the original Pollard-Rho method by Dave McGuigan,
 * using a second loop like in Pollard-Rho-Brent,
 * and "mod-blocking", i.e. computing the mod() only every few rounds.
 * 
 * @author Dave McGuigan
 */
public class PollardRhoTwoLoopsModBlock extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(PollardRhoTwoLoopsModBlock.class);
	private static final boolean DEBUG = false;
	private static final Random RNG = new Random();
	
	private BigInteger N;

	@Override
	public String getName() {
		return "PollardRhoTwoLoopsModBlock";
	}
	
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		this.N = N;
        BigInteger gcd;
		int bitLength = N.bitLength();
		// get random x0 from [0, N-1]
        BigInteger x = new BigInteger(bitLength, RNG);
        if (x.compareTo(N)>=0) x=x.subtract(N);

        // Brent: "The probability of the algorithm failing because q_i=0 increases, so it is best not to choose m (gcdBlockMax) too large"
        // DM:  failing is a bit strong. q(i)=0 happens often when there are small powers of small factor (i.e.5*5)
        //      This often occurs because multiple instances of the factor are found with larger blocks. The loop below 
        //      addresses that by re-doing the last block and checking each individual difference. The "failure" is 
        //      is that larger blocks have more to re-do. There are cases where a single difference = n, but this would happen even when m=1.
        //      Empirical testing indicates 2*log(N) is better than a fixed choice for large N.
        //      In 31 bit versions, is was determined just log(N) is best.
        final int logN = N.bitLength()-1;
    	final int gcdBlockMax = Math.max(100, 2*logN);
	  	final int modBlock = 4;
        do {
    		// get random c from [0, N-1]
        	BigInteger c = new BigInteger(bitLength, RNG);
            if (c.compareTo(N)>=0) c=c.subtract(N);


            BigInteger xx = x;
	        BigInteger xs;
	        BigInteger xxs;
		  	int gcdBlock = 1;
		  	gcd = I_1;
	        do {
	        	xs = x;
	        	xxs = xx;
	        	BigInteger prod = I_1;
    	        for (int i=0; i<gcdBlock; i = i+modBlock) {
					final int jMax = Math.min(modBlock, gcdBlock-i);
    	        	for (int j=0; j<jMax; j++) {
    		            x  = squareAddModN(x, c);
    		            xx = squareAddModN(xx, c);
    		            xx = squareAddModN(xx, c);
						// In BigInteger variants it is slightly faster to use the absolute value of the difference
        	            final BigInteger diff = x.compareTo(xx) < 0 ? xx.subtract(x) : x.subtract(xx);
    		            prod = diff.multiply(prod);
    	        	}
    	        	prod = prod.mod(N);
    	        }
	            gcd = prod.gcd(N);
	            // grow the gdBlock like R in Brent; this improves performance for "larger" N only, like 70 bit or more
		    	if (gcdBlock<gcdBlockMax) {
		    		gcdBlock <<= 1;
		    		if (gcdBlock > gcdBlockMax) gcdBlock = gcdBlockMax;
		    	}
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
