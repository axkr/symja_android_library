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
package de.tilman_neumann.jml.factor.siqs.poly;

import java.math.BigInteger;

/**
 * Interface for generators that produce the leading coefficient <code>a</code> of the quadratic polynomial
 * Q(x) = (d*a*x+b)^2 - kN used by SIQS. d is typically 1 or 2.</br></br>
 * 
 * The a-parameter in SIQS is chosen as a product of primes from the prime base: <code>a = q1 * ... * q_s</code>.
 * Its value should be roughly a ~ sqrt(2*k*N)/(d*M), where M is the sieve array size, such that |Q(x)|
 * is about the same at x=+M and x=-M, and |Q(x)| <= kN for all x.
 * 
 * The quality of the a-generator is crucial for both stability and performance of SIQS.
 * 
 * @author Tilman Neumann
 */
public interface AParamGenerator {

	String getName();
	
	/**
	 * Initialize this a-parameter generator for a new N.
	 * One result has to be a <code>qCount</code> value fixed throughout the rest of the factorization of N.
	 * @param k
	 * @param N
	 * @param kN
	 * @param d the d-value in Q(x) = (d*a*x + b)^2 - kN; typically 1 or 2
	 * @param primeBaseSize
	 * @param primesArray
	 * @param tArray the modular square roots t with t^2 == kN (mod p)
	 * @param sieveArraySize
	 */
	void initialize(int k, BigInteger N, BigInteger kN, int d, int primeBaseSize, int[] primesArray, int[] tArray, int sieveArraySize);
	
	BigInteger computeNextAParameter();
	
	/**
	 * @return number of primes <code>s</code> with <code>a-parameter = q_1 * ... * q_s</code>
	 */
	int getQCount();
	
	/**
	 * @return the q-values that give the <code>a-parameter = q_1 * ... * q_s</code>
	 */
	int[] getQArray();
	
	/**
	 * @return the modular sqrt values for the chosen q's.
	 */
	int[] getQTArray();
	
	/**
	 * Release memory after a factorization.
	 */
	void cleanUp();
}
