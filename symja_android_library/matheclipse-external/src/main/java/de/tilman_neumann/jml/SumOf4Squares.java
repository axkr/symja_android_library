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
package de.tilman_neumann.jml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.quadraticResidues.QuadraticResidues;
import de.tilman_neumann.jml.quadraticResidues.QuadraticResiduesMod2PowN;
import de.tilman_neumann.util.ConfigUtil;

/**
 * Stuff concerning sums of 4 squares representations of natural numbers.
 * 
 * @author Tilman Neumann
 */
public class SumOf4Squares {

	private static final Logger LOG = Logger.getLogger(SumOf4Squares.class);

	private static final boolean SHOW_ELEMENTS = true;
	
	/**
	 * Compute all elements of A004215 below m, i.e. all k<m such that k can be expressed as a sum of 4 squares
	 * but not by a sum of less than four squares.
	 * As commented in http://oeis.org/A004215, these are numbers of the form 4^i(8j+7), i >= 0, j >= 0.
	 * 
	 * @param m
	 * @return A004215 entries < m
	 */
	public static TreeSet<Long> getA004215(long m) {
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
	 * Compute all elements of A004215 below m = 2^n, i.e. all k<m such that k can be expressed as a sum of 4 squares
	 * but not by a sum of less than 4 squares.
	 * 
	 * This implementation seems to be faster than v1 but in the current form suffers from garbage collection.
	 * 
	 * @param n
	 * @return A004215 entries < 2^n
	 */
	public static TreeSet<Long> getA004215_v2(int n) {
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
	 * Another implementation using arrays, much faster than the previous ones.
	 * 
	 * @param n such that m=2^n
	 * @param array an array big enough to take roughly 2^n/6 values
	 * @return number of entries
	 */
	public static int getA004215_v3(int n, long[] array) {
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

	/**
	 * A test of the hypothesis that A023105(2^n) == 2 + the number of entries of A004215 that are less than 2^n, for n>0.
	 * Confirmed until n=29.
	 * 
	 * @param args ignored
	 */
	// TODO split into more specific tests
	public static void main(String[] args) {
		ConfigUtil.initProject();
		
		ArrayList<Integer> quadraticResidueCounts = new ArrayList<Integer>();
		ArrayList<Integer> quadraticResidueCounts_v2 = new ArrayList<Integer>();
		
		ArrayList<Integer> a004215EntryCounts = new ArrayList<Integer>();
		ArrayList<Integer> a004215EntryCounts_v2 = new ArrayList<Integer>();
		ArrayList<Integer> a004215EntryCounts_v3 = new ArrayList<Integer>();
		
		for (int n=0; n<=10; n++) {
			LOG.info("n = " + n + ":");
			
			long m = 1L<<n;
			long t0 = System.currentTimeMillis();
			TreeSet<Long> quadraticResiduesMod2PowN = QuadraticResidues.getQuadraticResidues(m);
			long t1 = System.currentTimeMillis();
			LOG.info("v1: There are " + quadraticResiduesMod2PowN.size() + " quadratic residues % " + m + (SHOW_ELEMENTS ? ": " + quadraticResiduesMod2PowN : "") + " -- duration: " + (t1-t0) + "ms");
			quadraticResidueCounts.add(quadraticResiduesMod2PowN.size());

			t0 = System.currentTimeMillis();
			List<Long> quadraticResiduesMod2PowN_v2 = QuadraticResiduesMod2PowN.getQuadraticResiduesMod2PowN(n);
			t1 = System.currentTimeMillis();
			LOG.info("v2: There are " + quadraticResiduesMod2PowN_v2.size() + " quadratic residues % " + m + (SHOW_ELEMENTS ? ": " + quadraticResiduesMod2PowN_v2 : "") + " -- duration: " + (t1-t0) + "ms");
			quadraticResidueCounts_v2.add(quadraticResiduesMod2PowN_v2.size());

			t0 = System.currentTimeMillis();
			TreeSet<Long> a004215Entries = getA004215(m);
			t1 = System.currentTimeMillis();
			LOG.info("v1: There are " + a004215Entries.size() + " A004215 entries < " + m + (SHOW_ELEMENTS ? ": " + a004215Entries : "") + " -- duration: " + (t1-t0) + "ms");
			a004215EntryCounts.add(a004215Entries.size());
			
			t0 = System.currentTimeMillis();
			TreeSet<Long> a004215Entries_v2 = getA004215_v2(n);
			t1 = System.currentTimeMillis();
			LOG.info("v2: There are " + a004215Entries_v2.size() + " A004215 entries < " + m + (SHOW_ELEMENTS ? ": " + a004215Entries_v2 : "") + " -- duration: " + (t1-t0) + "ms");
			a004215EntryCounts_v2.add(a004215Entries_v2.size());
			
			t0 = System.currentTimeMillis();
			long[] a004215Entries_v3 = new long[((1<<n) / 6) + 4]; // #{A004215(k) | k<m} is always near to m/6
			int count = getA004215_v3(n, a004215Entries_v3);
			t1 = System.currentTimeMillis();
			LOG.info("v3: There are " + count + " A004215 entries < " + m + (SHOW_ELEMENTS ? ": " + Arrays.toString(a004215Entries_v3) : "") + " -- duration: " + (t1-t0) + "ms");
			a004215EntryCounts_v3.add(count);
			
			LOG.info("");
		}
		
		// A023105(n) = 1, 2, 2, 3, 4, 7, 12, 23, 44, 87, 172, 343, ...
		LOG.info("v1: quadraticResidueCounts = " + quadraticResidueCounts);
		LOG.info("v2: quadraticResidueCounts = " + quadraticResidueCounts_v2);

		LOG.info("v1: a004215EntryCounts = " + a004215EntryCounts);
		LOG.info("v2: a004215EntryCounts = " + a004215EntryCounts_v2);
		LOG.info("v3: a004215EntryCounts = " + a004215EntryCounts_v3);
	}
}
