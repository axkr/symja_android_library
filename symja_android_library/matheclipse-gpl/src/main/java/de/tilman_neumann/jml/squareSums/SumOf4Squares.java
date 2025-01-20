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
package de.tilman_neumann.jml.squareSums;

import java.util.TreeSet;

import de.tilman_neumann.jml.quadraticResidues.QuadraticResiduesMod2PowN;

/**
 * Stuff concerning sums of 4 squares representations of natural numbers.
 * 
 * @author Tilman Neumann
 */
public class SumOf4Squares {
	
	/**
	 * Compute all k<=m that can be expressed as a sum of 4 but no fewer non-zero squares.
	 * In other words, compute all A004215 entries <= m.
	 * 
	 * As commented in http://oeis.org/A004215, these are numbers of the form 4^i(8j+7), i >= 0, j >= 0.
	 * 
	 * @param m
	 * @return A004215 entries <= m
	 */
	static TreeSet<Long> computeNumbersThatAreNoLessThan4NonzeroSquares_v1(long m) {
		TreeSet<Long> result = new TreeSet<Long>();
		int mBits = 64 - Long.numberOfLeadingZeros(m);
		int iMax = (mBits+1)>>1;
		for (int i=0; i<=iMax; i++) {
			long leftTerm = 1L << (i<<1); // 4^i == 2^(2*i)
			int j=0;
			while (true) {
				long entry = leftTerm * (8*j+7);
				if (entry > m) break;
				result.add(entry);
				j++;
			}
		}
		return result;
	}

	/**
	 * Compute all k<2^n that can be expressed as a sum of 4 but no fewer non-zero squares.
	 * In other words, compute all A004215 entries < 2^n.
	 * 
	 * This implementation derives the number to compute from the set of quadratic residues modulo 2^n.
	 * 
	 * @param n
	 * @return A004215 entries < 2^n
	 */
	static TreeSet<Long> computeNumbersThatAreNoLessThan4NonzeroSquares_v2(int n) {
		if (n < 3) return new TreeSet<>();

		TreeSet<Long> complement = QuadraticResiduesMod2PowN.getComplementOfQuadraticResiduesMod2PowN(n);
		
		// remove the entries not needed for A004215
		complement.remove(0L);
		boolean nOdd = (n & 1) == 1;
		if (nOdd) {
			complement.remove(Long.valueOf(1 << (n-1)));
		} else {
			complement.remove(Long.valueOf((1 << (n-1)) + (1 << (n-2))));
		}
		return complement;
	}

	/**
	 * Compute all k<2^n that can be expressed as a sum of 4 but no fewer non-zero squares.
	 * In other words, compute all A004215 entries < 2^n.
	 * 
	 * This implementation derives the number to compute from the set of quadratic residues modulo 2^n.
	 * Besides it uses arrays to speed up performance.
	 * 
	 * Fastest implementation so far.
	 * 
	 * @param n
	 * @param array an array big enough to take roughly 2^n/6 values
	 * @return A004215 entries < 2^n
	 */
	public static int computeNumbersThatAreNoLessThan4NonzeroSquares/*_v3*/(int n, long[] array) {
		if (n < 3) return 0;
		
		long[] residues = new long[array.length];
		int count = QuadraticResiduesMod2PowN.getQuadraticResiduesMod2PowN(n, residues);
		
		long m = 1L<<n;
		for (int i=0; i<count; i++) {
			array[i] = m - residues[count-i-1];
		}
		
		// remove the entries not needed for A004215
		int outCount = 0;
		boolean nOdd = (n & 1) == 1;
		if (nOdd) {
			for (int j=0; j<count-1; j++) { // remove m
				long elem = array[j];
				if (elem != (1L << (n-1))) {
					array [outCount++] = elem;
				}
			}
		} else {
			for (int j=0; j<count-1; j++) { // remove m
				long elem = array[j];
				if (elem != (1 << (n-1)) + (1 << (n-2))) {
					array [outCount++] = elem;
				}
			}
		}
		
		return outCount;
	}
}
