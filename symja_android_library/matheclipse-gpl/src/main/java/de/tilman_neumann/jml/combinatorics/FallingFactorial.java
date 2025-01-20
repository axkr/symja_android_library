/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.combinatorics;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;

/**
 * Implementations of the falling factorial (n)_k = (n-k+1)*...*n.
 * 
 * In combinatorics this can be interpreted as the number of variations of (n-k) and k indistinguishable objects of two different kinds.
 * 
 * Note that the coefficients of the expanded polynomial are (signed) Stirling numbers of the first kind.
 * E.g. (n)_5 = (n-4)*(n-3)*(n-2)*(n-1)*n = 24n - 50n^2 + 35n^3 - 10n^4 + n^5 = s(5,1)n + s(5,2)n^2 + s(5,3)n^3 + s(5,4)n^4 + s(5,5)n^5
 * 
 * @author Tilman Neumann
 */
public class FallingFactorial {
	
	/**
	 * Computes the falling factorial.
	 * 
	 * @param n
	 * @param k
	 * @return falling factorial (n)_k
	 * @throws IllegalArgumentException if k&lt;0
	 */
	public static BigInteger fallingFactorial(int n, int k) throws IllegalArgumentException {
		// for k<0 see https://math.stackexchange.com/questions/612631/negative-falling-factorial/612637
		if (k<0) throw new IllegalArgumentException("FallingFactorial(" + n + ", k): Negative k are not supported yet, the result would be rational.");
		
		if (k==0) return I_1; // n!/n! = 1
		if (n==0) return I_0; 
		if (n>0) {
			// (n-k+1) * ... * n = 0 if n==0 or k>n
			if (k>n) return I_0;
			// n>0 and k<=n
			return (k<<1 < n) ? simpleProduct(n, k) : byFactorials(n, k); // XXX the turning point estimate is very crude
		}
		
		// treat n<0, k>0: see https://math.stackexchange.com/questions/111463/is-there-a-reasonable-generalization-of-the-falling-factorial-for-real-exponents
		return simpleProduct(n, k);
	}

	/**
	 * Computes the falling factorial as a fraction of factorials of non-negative arguments.
	 * Strong for small k because the factorial implements Luschny's algorithm.
	 * 
	 * @param n
	 * @param k
	 * @return falling factorial (n)_k
	 * @throws IllegalArgumentException if n&lt;0 or (n-k)&lt;0
	 */
	static BigInteger byFactorials(int n, int k) throws IllegalArgumentException {
		return Factorial.factorial(n).divide(Factorial.factorial(n-k));
	}

	/**
	 * Computes the falling factorial as a simple product. Works for n<0, k>=0, too.
	 * 
	 * @param n
	 * @param k
	 * @return falling factorial (n)_k
	 */
	static BigInteger simpleProduct(int n, int k) {
        BigInteger element = BigInteger.valueOf(n-k+1);
        BigInteger result = I_1;
        for (int i=0; i<k; i++) {
            result = result.multiply(element);
            element=element.add(I_1);
        }
        return result;
	}
}
