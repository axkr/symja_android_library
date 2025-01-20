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
 * 31-bit implementation of Pollard's Rho method.
 * 
 * Improvement by Dave McGuigan:
 * Use squareAddModN31() instead of nested addModN(squareModN())
 * 
 * @author Tilman Neumann
 */
public class PollardRho31 extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(PollardRho31.class);
	private static final boolean DEBUG = false;
	private static final SpRand32 RNG = new SpRand32();

	private Gcd31 gcdEngine = new Gcd31();
	
	/** absolute value of the number to factor */
	private int n;
	
	@Override
	public String getName() {
		return "PollardRho31";
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
		
        int gcd;
        int x = RNG.nextInt(n); // uniform random int from [0, n)
        int xx = x;
        do {
        	int c = RNG.nextInt(n); // uniform random int from [0, n)
	        do {
	            x  = squareAddModN31(x, c);
	            xx = squareAddModN31(xx, c);
	            xx = squareAddModN31(xx, c);
	            gcd = gcdEngine.gcd(x-xx, n);
	        } while (gcd==1);
        } while (gcd==n); // leave loop if factor found; otherwise continue with a new random c
        if (DEBUG) LOG.debug("Found factor of " + nOriginal + " = " + gcd);
        return gcd;
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
