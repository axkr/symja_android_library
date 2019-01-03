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

import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithmBase;
import de.tilman_neumann.jml.gcd.Gcd63;

/**
 * 31-bit implementation of Pollard' Rho method.
 * 
 * @author Tilman Neumann
 */
public class PollardRho31 extends FactorAlgorithmBase {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PollardRho31.class);
	private static final SecureRandom RNG = new SecureRandom();

	private Gcd63 gcdEngine = new Gcd63();

	/** factor argument converted to int */
	private int n;

	@Override
	public String getName() {
		return "PollardRho31";
	}
	
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		this.n = N.intValue();
		
        long gcd;
        long x = RNG.nextInt(n); // uniform random int from [0, n)
        long xx = x;
        do {
        	int c = RNG.nextInt(n); // uniform random int from [0, n)
	        do {
	            x  = addModN(squareModN(x), c);
	            xx = addModN(squareModN(xx), c);
	            xx = addModN(squareModN(xx), c);
	            gcd = gcdEngine.gcd(x-xx, n);
	        } while(gcd==1);
        } while (gcd==n); // leave loop if factor found; otherwise continue with a new random c
		//LOG.debug("Found factor of " + N + " = " + factor);
        return BigInteger.valueOf(gcd);
	}

	/**
	 * Addition modulo N, with <code>a, b < N</code>.
	 * @param a
	 * @param b
	 * @return (a+b) mod N
	 */
	private long addModN(long a, int b) {
		long sum = a + b;
		return sum<n ? sum : sum-n;
	}

	/**
	 * x^2 modulo N.
	 * @param x
	 * @return
	 */
	private long squareModN(long x) {
		return (x * x) % n;
	}
}
