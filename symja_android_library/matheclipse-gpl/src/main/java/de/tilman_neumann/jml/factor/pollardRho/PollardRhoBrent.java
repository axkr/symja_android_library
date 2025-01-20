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
 * Brents's improvement of Pollard's Rho algorithm, following [Richard P. Brent: An improved Monte Carlo Factorization Algorithm, 1980].
 * 
 * Improvement by Dave McGuigan:
 * Use squareAddModN() instead of nested addModN(squareModN())
 * 
 * @author Tilman Neumann
 */
public class PollardRhoBrent extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(PollardRhoBrent.class);
	private static final boolean DEBUG = false;
	private static final Random RNG = new Random();

	private BigInteger N;

	@Override
	public String getName() {
		return "PollardRhoBrent";
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

            // Brent: "The probability of the algorithm failing because q_i=0 increases, so it is best not to choose m too large"
        	final int m = 100;
        	int r = 1;
        	BigInteger q = I_1;
        	do {
	    	    x = y;
	    	    for (int i=1; i<=r; i++) {
	    	        y = squareAddModN(y, c);
	    	    }
	    	    int k = 0;
	    	    do {
	    	        ys = y;
	    	        final int iMax = Math.min(m, r-k);
	    	        for (int i=1; i<=iMax; i++) {
	    	            y = squareAddModN(y, c);
						// In BigInteger variants it is slightly faster to use the absolute value of the difference
	    	            final BigInteger diff = x.compareTo(y) < 0 ? y.subtract(x) : x.subtract(y);
	    	            q = diff.multiply(q).mod(N);
	    	        }
	    	        G = q.gcd(N);
	    	        // if q==0 then G==N -> the loop will be left and restarted with new x0, c
	    	        // after checking each diff separately in the loop below.
	    	        k += m;
		    	    if (DEBUG) LOG.debug("r = " + r + ", k = " + k);
	    	    } while (k<r && G.equals(I_1));
	    	    r <<= 1;
	    	    if (DEBUG) LOG.debug("r = " + r + ", G = " + G);
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
