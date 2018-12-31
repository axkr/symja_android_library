/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
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
import java.security.SecureRandom;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithmBase;

/**
 * From: http://www.cs.princeton.edu/introcs/79crypto/PollardRho.java
 * (INTRODUCTION TO COMPUTER SCIENCE by Robert Sedgewick and Kevin Wayne)
 *
 * Pollards Rho method. Pollard's rho method is a randomized factoring algorithm
 * that can factor 128 bit numbers in a reasonable amount of time, especially if
 * the numbers have some small factors. It is based on the following fact: if d is
 * the smallest nontrivial factor of N and x - y is a nontrivial multiple of d then
 * gcd(x-y, N) = d. A naive method would be to generate a bunch of random values
 * x[1], x[2], ..., x[m] and compute gcd(x[i]-x[j], N) for all pairs i and j.
 * Pollard's rho method is an ingenious method way to find x and y without doing
 * all of the pairwise computations. It works as follows: choose a and b at random
 * between 1 and N-1, and initialize x = y = a. Repeatedly update x = f(x), y = f(f(y)),
 * where f(x) = x^2 + b as long as gcd(x-y, N) = 1. The gcd is a factor of N, but if you
 * get unlucky, it could be equal to N. By randomly choosing a and b each time, we
 * ensure that we never get too unlucky.
 * 
 * @author Tilman Neumann
 */
public class PollardRho extends FactorAlgorithmBase {
//	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(PollardRho.class);
	private static final SecureRandom RNG = new SecureRandom();
	
	private BigInteger N;

	@Override
	public String getName() {
		return "PollardRho";
	}
	
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		this.N = N;
        BigInteger gcd;
		int bitLength = N.bitLength();
		// get random x0 from [0, N-1]
        BigInteger x = new BigInteger(bitLength, RNG);
        if (x.compareTo(N)>=0) x=x.subtract(N);
        BigInteger xx = x;

        do {
    		// get random c from [0, N-1]
        	BigInteger c = new BigInteger(bitLength, RNG);
            if (c.compareTo(N)>=0) c=c.subtract(N);
	        
	        do {
	            x  = addModN( x.multiply(x) .mod(N), c);
	            xx = addModN(xx.multiply(xx).mod(N), c);
	            xx = addModN(xx.multiply(xx).mod(N), c);
	            gcd = x.subtract(xx).gcd(N);
	        } while(gcd.equals(I_1));
	        
	    // leave loop if factor found; otherwise continue with a new random c
        } while (gcd.equals(N));
		//LOG.debug("Found factor of " + N + " = " + factor);
        return gcd;
	}

	/**
	 * Addition modulo N, with <code>a, b < N</code>.
	 * @param a
	 * @param b
	 * @return (a+b) mod N
	 */
	private BigInteger addModN(BigInteger a, BigInteger b) {
		BigInteger sum = a.add(b);
		return sum.compareTo(N)<0 ? sum : sum.subtract(N);
	}
}
