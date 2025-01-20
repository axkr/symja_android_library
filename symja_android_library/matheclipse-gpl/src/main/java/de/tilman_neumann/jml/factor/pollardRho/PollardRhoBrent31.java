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

import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.gcd.Gcd31;
import de.tilman_neumann.jml.random.SpRand32;

/**
 * Brents's improvement of Pollard's Rho algorithm, following [Richard P. Brent: An improved Monte Carlo Factorization Algorithm, 1980].
 * 
 * 31 bit version.
 * 
 * Improvements by Dave McGuigan:
 * 1. Use squareAddModN31() instead of nested addModN(squareModN())
 * 2. reinitialize q before each inner loop
 * 3. Compute the number of steps before each gcd by m=log(n)
 * 4. Use faster "mulMod"
 * 
 * @author Tilman Neumann
 */
public class PollardRhoBrent31 extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(PollardRhoBrent31.class);
	private static final boolean DEBUG = false;
	private static final SpRand32 RNG = new SpRand32();

	private int n;

	private Gcd31 gcd = new Gcd31();
	
	@Override
	public String getName() {
		return "PollardRhoBrent31";
	}
	
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		if (N.bitLength() > 31) { // this check should be negligible in terms of performance
			throw new IllegalArgumentException("N = " + N + " has " + N.bitLength() + " bit, but " + getName() + " only supports arguments <= 31 bit");
		}
		int factorInt = findSingleFactor(N.intValue());
        return BigInteger.valueOf(factorInt);
	}
	
	public int findSingleFactor(int nOriginal) {
		this.n = nOriginal<0 ? -nOriginal : nOriginal; // RNG.nextInt(n) below would crash for negative arguments
		int G;
		int ys, x;
        do {
	        // start with random x0, c from [0, N-1]
        	int c = RNG.nextInt(n);
            int x0 = RNG.nextInt(n);
            int y = x0;

            // Brent: "The probability of the algorithm failing because q_i=0 increases, so it is best not to choose m too large"
            // DM:  failing is a bit strong. q(i)=0 happens often when there are small powers of small factor (i.e.5*5)
            //      This occurs because multiple instances of the factor are found with larger blocks. The loop below 
            //      addresses that by re-doing the last block and checking each individual difference. The "failure" is 
            //      is that larger blocks have more to re-do. Empirical testing indicates 2*log(N) is better than a fixed choice
            //      for large N. In 31 bit versions, it was determined just log(N) is best.
            final int m = Math.max(8, 32 - Integer.numberOfLeadingZeros(n)); // Don't want it too small
        	int r = 1;
        	do {
	    	    x = y;
	    	    for (int i=1; i<=r; i++) {
    	            y = squareAddModN31(y, c);
	    	    }
	    	    int k = 0;
	        	int q = 1;
	    	    do {
	    	        ys = y;
	    	        final int iMax = Math.min(m, r-k);
	    	        for (int i=1; i<=iMax; i++) {
	    	            y = squareAddModN31(y, c);
	    	            // the "mulMod" operation...
	    	            // DM: "Apparently getting things into a 64 bit register at the start has benefits"
	    	            //q = (int) ((((long)x-y) * q) % n);
	    	            // But we still want to compute x-y in ints?
	    	            //q = (int) (((x-y) * (long)q) % n);
	    	            //q = (int) (((long)(x-y) * q) % n);
	    	            q = (int) (((long)q * (x-y)) % n);
	    	        }
	    	        G = gcd.gcd(q, n);
	    	        // if q==0 then G==N -> the loop will be left and restarted with new x0, c
	    	        // after checking each diff separately in the loop below.
	    	        k += m;
	    	        if (DEBUG) LOG.debug("r = " + r + ", k = " + k);
	    	    } while (k<r && G==1);
	    	    r <<= 1;
	    	    if (DEBUG) LOG.debug("r = " + r + ", G = " + G);
	    	} while (G==1);
	    	if (G==n) {
	    	    do {
    	            ys = squareAddModN31(ys, c);
    	            G = gcd.gcd(x-ys, n);
	    	    } while (G==1);
	    	    if (DEBUG) LOG.debug("G = " + G);
	    	}
        } while (G==n);
        if (DEBUG) LOG.debug("Found factor of " + nOriginal + " = " + G);
        return G;
	}

	/**
	 * x^2+c modulo N.
	 * @param x
	 * @return
	 */
	private int squareAddModN31(int x, int c) {
		// internal computation must be long, not only for the multiplication, but also for the addition of 31 bit numbers
		return (int)( ((long)x*x+c) % n);
	}
}
