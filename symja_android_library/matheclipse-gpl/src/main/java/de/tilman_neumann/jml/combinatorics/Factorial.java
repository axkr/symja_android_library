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

import java.math.BigInteger;

import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Implementations of the factorial function.
 * @author Tilman Neumann
 */
public class Factorial {

	private static final AutoExpandingPrimesArray PRIMES_ARRAY = new AutoExpandingPrimesArray().ensureLimit(1000);

	/**
	 * Computes the factorial for non-negative integer arguments by the
	 * simple product rule.
	 * 
	 * @param n Argument
	 * @return n! if n is a non-negative integer
	 * @throws IllegalArgumentException if n is a negative integer
	 */
	public static BigInteger simpleProduct(int n) throws IllegalArgumentException {
		if (n >= 0) {
			BigInteger ret = I_1;
			for (int i=2; i<=n; i++) {
				ret = ret.multiply(BigInteger.valueOf(i));
			}
			return ret;
		}
		throw new IllegalArgumentException("factorial currently supports only non-negative integers, but the argument is n=" + n);
	}
	
	/**
	 * Computes the factorial for non-negative integer arguments applying the
	 * simple product rule, but allowing for a previously computed start value.
	 * 
	 * Adapted from http://www.jonelo.de by Johann Nepomuk Loefflmann (jonelo@jonelo.de),
	 * published under GNU General Public License.
	 * 
	 * @param n Argument
	 * @param start Argument of the start result
	 * @param startResult Factorial for start
	 * @return n! if n is a non-negative integer
	 * @throws ArithmeticException if n is a negative integer
	 */
	public static BigInteger withStartResult(int n, int start, BigInteger startResult) throws ArithmeticException {
        if (n<0) throw new ArithmeticException("The factorial function supports only non-negative arguments.");
        if (n==0) return I_1;
        if (n==start) return startResult;
        if (n<start) {
        	start=1; 
        	startResult = I_1; 
        }
        BigInteger x = startResult;
        for (int i=start+1; i <= n; i++) {
            x=x.multiply(BigInteger.valueOf(i));
        }
        return x;
	}

	/**
	 * Peter Luschny's swinging prime factorial algorithm, see http://luschny.de/math/factorial/SwingIntro.pdf
	 * @param n
	 * @return n!
	 * @throws ArithmeticException if n is a negative integer
	 */
	public static BigInteger factorial/*Luschny*/(int n) throws ArithmeticException {
        if (n<0) throw new ArithmeticException("The factorial function supports only non-negative arguments.");
		if (n<2) return I_1;
		BigInteger f = factorial/*Luschny*/(n>>1); // floor(n/2)
		return f.multiply(f).multiply(primeSwing(n));
	}
	
	private static BigInteger primeSwing(int n) {
		// ensure we find all primes <= n
		PRIMES_ARRAY.ensureLimit(n);

		BigInteger product = I_1;
		int i=0;
		while (true) {
			int prime = PRIMES_ARRAY.getPrime(i++); // starts with p[0] = 2
			if (prime>n) break;
			
			int q=n;
			int p=1;
			do {
				q /= prime; // floor(q/prime)
				if ((q&1)==1) p *= prime;
			} while (q>0);
			if (p>1) product = product.multiply(BigInteger.valueOf(p));
		}
		return product;
	}
}
