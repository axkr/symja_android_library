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

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithmBase;

/**
 * Pollard's Rho algorithm improved by doing the GCD on products.
 * 
 * @author Tilman Neumann
 */
public class PollardRho_ProductGcd extends FactorAlgorithmBase {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PollardRho_ProductGcd.class);
	private static final SecureRandom RNG = new SecureRandom();

	private BigInteger N;

	@Override
	public String getName() {
		return "PollardRho_Prod";
	}
	
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		this.N = N;
		int bitLength = N.bitLength();
        BigInteger product, gcd;
        
        do {
	        // start with random x0, c from [0, 2^n] where n=upper(ld N)
        	BigInteger c = new BigInteger(bitLength, RNG);
            BigInteger x = new BigInteger(bitLength, RNG);
            BigInteger xx = x;
	        
	        do {
	        	product = I_1;
	        	for (int i=0; i<100; i++) {
		            x  = addModN( x.multiply(x) .mod(N), c);
		            xx = addModN(xx.multiply(xx).mod(N), c);
		            xx = addModN(xx.multiply(xx).mod(N), c);
		            product = product.multiply(x.subtract(xx)).mod(N);
	        	}
	            gcd = product.gcd(N); // the gcd function must give gcd(0,N) = N
	        } while(gcd.equals(I_1));
	        
	    // leave loop if factor found; otherwise continue with new random x0, c
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
