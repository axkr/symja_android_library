/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Hyperfactorials.
 * 
 * @author Tilman Neumann
 */
public class HyperFactorial {
	/**
	 * A002109 or the standard "hyperfactorial" is the product {1^1*2^2*..n^n}.
	 * @param n
	 * @return hyperfactorial(n)
	 */
	public static BigInteger standard(int n) {
		BigInteger result = I_1;
		for (int k=2; k<=n; k++) {
			result = result.multiply(BigInteger.valueOf(k).pow(k));
		}
		return result;
	}
	
	/**
	 * A000197 or what I call the "inverse hyperfactorial" is the product
	 * 1^n*2^(n-1)*..*(n-1)^2*n^1 = 1!*2!*3!*...(n-1)!*n!.
	 * @param n
	 * @return the "inverse hyperfactorial" of n aka 1!*2!*3!*...(n-1)!*n!
	 */
	public static BigInteger inverse(int n) {
		BigInteger result = I_1;
		BigInteger kFactorial = I_1;
		for (int k=2; k<=n; k++) {
			kFactorial = kFactorial.multiply(BigInteger.valueOf(k));
			result = result.multiply(kFactorial);
		}
		return result;
	}
}
