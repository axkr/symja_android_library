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
 * A variant of the Pollard-Rho-Brent algorithm by Dave McGuigan,
 * adding "mod-blocking", i.e. computing the mod() only every few rounds.
 * 
 * @author Dave McGuigan
 */
public class PollardRhoBrentModBlock extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(PollardRhoBrentModBlock.class);
	private static final boolean DEBUG = false;
	private static final Random RNG = new Random();

	private BigInteger N;

	@Override
	public String getName() {
		return "PollardRhoBrentModBlock";
	}
	
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		this.N = N;
		int Nbits = N.bitLength();
        BigInteger G, x, ys;
        do {
	        // start with random x0, c from [0, N-1]
        	BigInteger c = new BigInteger(Nbits, RNG);
            if (c.compareTo(N)>=0) c = c.subtract(N);
            BigInteger x0 = new BigInteger(Nbits, RNG);
            if (x0.compareTo(N)>=0) x0 = x0.subtract(N);
            BigInteger y = x0;

            // Brent: "The probability of the algorithm failing because q_i=0 increases, so it is best not to choose m (gcdBlock) too large"
            // DM:  failing is a bit strong. q(i)=0 happens often when there are small powers of small factor (i.e.5*5)
            //      This often occurs because multiple instances of the factor are found with larger blocks. The loop below 
            //      addresses that by re-doing the last block and checking each individual difference. The "failure" is 
            //      is that larger blocks have more to re-do. There are cases where a single difference = n, but this would happen even when m=1.
            //      Empirical testing indicates 2*log(N) is better than a fixed choice for large N.
            //      In 31 bit versions, is was determined just log(N) is best.
            final int logN = N.bitLength()-1;
        	final int gcdBlock = Math.max(100, 2*logN);
        	final int modBlock = 4; // DM: no real difference between 4 and 8. Conservatively choosing 4.
        	
        	int r = 1;
 
			do {
				x = y;
				for (int i = 1; i <= r; i++) {
					y = squareAddModN(y, c);
				}
				ys = y;
				BigInteger q = I_1;
				int k = 0;
				do {
					final int iMax = Math.min(gcdBlock, r - k);
					final int jMax = Math.min(modBlock, iMax);
					for (int i = 1; i <= iMax; i = i + jMax) {
						for (int j = 0; j < jMax; j++) {
							y = squareAddModN(y, c);
							// In BigInteger variants it is slightly faster to use the absolute value of the difference
		    	            final BigInteger diff = x.compareTo(y) < 0 ? y.subtract(x) : x.subtract(y);
		    	            q = diff.multiply(q).mod(N);
						}
						q = q.mod(N); // one mod for the block
					}
					G = q.gcd(N);
					// if q==0 then G==N -> the loop will be left and restarted with new x0, c
	    	        // after checking each diff separately in the loop below.
					k += gcdBlock;
					if (DEBUG) LOG.debug("r = " + r + ", k = " + k);
				} while (k < r && G.equals(I_1));
				r <<= 1;
			} while (G.equals(I_1));
			
	    	if (G.equals(N)) {
	    	    do {
	    	        ys = squareAddModN(ys, c);
    	            final BigInteger diff = x.compareTo(ys) < 0 ? ys.subtract(x) : x.subtract(ys);
    	            G = diff.gcd(N);
	    	    } while (G.equals(I_1));
	    	    if (DEBUG) LOG.debug("G = " + G);
	    	}
        } while (G.equals(N));
        if (DEBUG) LOG.debug("Found factor of " + N + " = " + G);
        return G;
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
