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

/**
 * Multinomial coefficient implementations.
 * 
 * @author Tilman Neumann
 */
public class Multinomial {

	/**
	 * Multinomial coefficient.
	 * 
	 * @param N Category counts.
	 * @return (N1+...+Nk) choose (N1, ..., Nk)
	 */
	public static BigInteger multinomial(int N[]) {
		// get dimension
		int k = N.length;
	
		// get total count
		int Nall = 0;
		for (int i = 0; i<k; i++) Nall += N[i];
	
		// calculate the multinomial
		BigInteger ret = Factorial.factorial(Nall);
		for (int i=0; i<k; i++) ret = ret.divide(Factorial.factorial(N[i]));
	
		// return the result
		return ret;
	}
}
