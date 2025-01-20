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
package de.tilman_neumann.jml.quadraticResidues;

import static de.tilman_neumann.jml.base.BigIntConstants.I_1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Methods to generate quadratic residues or test for quadratic residuosity modulus 2^n.
 * 
 * @author Tilman Neumann
 */
public class QuadraticResiduesMod2PowN {

	private static final Logger LOG = LogManager.getLogger(QuadraticResiduesMod2PowN.class);

	private static final boolean DEBUG = false;

	/**
	 * Compute A23105(n), the number of distinct quadratic residues mod 2^n, via the formula by David S. Dodson in http://oeis.org/search?q=A023105.
	 * @param n The power of 2.
	 * @return Number of distinct quadratic residues mod 2^n
	 */
	public static long getNumberOfQuadraticResiduesMod2PowN(int n) {
		if (n==0) return 1;
		int c = (n&1)==0 ? 16 : 20;
		return ((1<<n) + c/2)/6;
	}
	
	/**
	 * Computes if 'a' is a quadratic residue modulo 2^n.
	 * Iterative implementation for BigIntegers.
	 * 
	 * @param a argument
	 * @param n exponent of the modulus m=2^n
	 * @return true if 'a' is a quadratic residue modulo 2^n
	 */
	public static boolean isQuadraticResidueMod2PowN(BigInteger a, int n) {
		BigInteger m = I_1.shiftLeft(n);
		if (a.compareTo(m) >= 0) a = a.and(m.subtract(I_1)); // now 0 <= a < m
		
		BigInteger lastM = m;
		for (int i=n; i>=2; i--) {
			lastM = lastM.shiftRight(1);
			if ((i & 1) == 1) { // odd i
				if (a.equals(lastM.add(lastM.shiftRight(2)))) return false;
			} else {
				if (a.equals(lastM) || a.equals(lastM.add(lastM.shiftRight(1)))) return false;
			}
			if (a.compareTo(lastM) >= 0) a = a.subtract(lastM);
		}
		
		return true;
	}

	/**
	 * Computes if 'a' is a quadratic residue modulo 2^n.
	 * Iterative implementation for longs.
	 * 
	 * @param a argument
	 * @param n exponent of the modulus m=2^n
	 * @return true if 'a' is a quadratic residue modulo 2^n
	 */
	public static boolean isQuadraticResidueMod2PowN_v1(long a, int n) {
		long m = 1L<<n;
		if (a >= m) a &= (m - 1); // now 0 <= a < m
		
		long lastM = m;
		for (int i=n; i>=2; i--) {
			lastM >>>= 1;
			if ((i & 1) == 1) { // odd i
				if (a == lastM + (lastM>>2)) return false;
			} else {
				if (a == lastM || a == lastM + (lastM>>1)) return false;
			}
			if (a >= lastM) a -= lastM;
		}
		
		return true;
	}

	/**
	 * Computes if 'a' is a quadratic residue modulo 2^n.
	 * Implementation following https://en.wikipedia.org/wiki/Quadratic_residue.
	 * Much faster than v1.
	 * 
	 * @param a argument
	 * @param n exponent of the modulus m=2^n
	 * @return true if 'a' is a quadratic residue modulo 2^n
	 */
	public static boolean isQuadraticResidueMod2PowN/*_v2*/(long a, int n) {
		long m = 1L<<n;
		if (a >= m) a &= (m - 1); // now 0 <= a < m
		
		if (a == 0) return true; // Long.numberOfTrailingZeros(0) == 64
		
		// decompose 'a'
		int lsb = Long.numberOfTrailingZeros(a);
		int i = lsb>>1;
		long rest = a >> (i<<1); // a / 4^i
		// if rest is of the form 8*j+1 then a is of the form 4^i*(8*j+1)
		return (rest & 7) == 1;
	}

	/**
	 * Compute all quadratic residues modulus 2^n.
	 * 
	 * @param n
	 * @return list of quadratic residue modulus 2^n
	 */
	public static List<BigInteger> getQuadraticResiduesMod2PowN_testAll_big(int n) {
		List<BigInteger> list = new ArrayList<>();
		long m = 1L<<n;
		for (long a=0; a<m; a++) {
			BigInteger aBig = BigInteger.valueOf(a);
			if (isQuadraticResidueMod2PowN(aBig, n)) {
				list.add(aBig);
			}
		}
		return list;
	}
	
	/**
	 * Compute all quadratic residues modulus 2^n.
	 * 
	 * OutOfMemoryError at n=29.
	 * 
	 * @param n
	 * @return list of quadratic residue modulus 2^n
	 */
	public static List<Long> getQuadraticResiduesMod2PowN_testAll(int n) {
		List<Long> list = new ArrayList<>();
		long m = 1L<<n;
		for (long a=0; a<m; a++) {
			if (isQuadraticResidueMod2PowN(a, n)) {
				list.add(a);
			}
		}
		return list;
	}
	
	/**
	 * Compute all quadratic residues modulus 2^n.
	 * 
	 * OutOfMemoryError at n=31.
	 * 
	 * @param n
	 * @return list of quadratic residue modulus 2^n
	 */
	public static List<Long> getQuadraticResiduesMod2PowN_testAll_v2(int n) {
		List<Long> list = new ArrayList<>();
		long m = 1L<<n;
		for (long a=0; a<m; a++) {
			if (isQuadraticResidueMod2PowN/*_v2*/(a, n)) {
				list.add(a);
			}
		}
		return list;
	}

	/**
	 * Compute all quadratic residues modulus 2^n.
	 * 
	 * OutOfMemoryError at n=30.
	 * 
	 * @param n
	 * @return list of quadratic residues
	 */
	public static List<Long> getQuadraticResiduesMod2PowN(int n) {
		if (n == 0) return Arrays.asList(new Long[] {0L});
		
		// result for i=1, m=2
		List<Long> lastList = Arrays.asList(new Long[] {0L, 1L});
		
		long lastM = 2;
		for (int i=2; i<=n; i++, lastM <<= 1) {
			List<Long> nextList = new ArrayList<>(lastList); // copy
			for (long elem : lastList) {
				nextList.add(elem + lastM);
			}
			
			// remove some stuff
			if (DEBUG) LOG.debug("i=" + i + ", m=" + (2*lastM) + ": nextList before removal = " + nextList);
			if ((i & 1) == 1) { // odd i
				nextList.remove(Long.valueOf(lastM + (lastM>>2)));
			} else {
				nextList.remove(Long.valueOf(lastM));
				nextList.remove(Long.valueOf(lastM + (lastM>>1)));
			}
			if (DEBUG) LOG.debug("i=" + i + ", m=" + (2*lastM) + ": nextList after removal = " + nextList);
			lastList = nextList;
		}
		return lastList;
	}

	/**
	 * Compute all quadratic residues modulus 2^n.
	 * Fast implementation using a single array, not needing reallocations.
	 * 
	 * OutOfMemoryError at n=33.
	 * 
	 * @param n
	 * @param array the array to fill with quadratic residues
	 * @return number of quadratic residues modulus 2^n
	 */
	public static int getQuadraticResiduesMod2PowN(int n, long[] array) {
		if (n==0) {
			array[0] = 0;
			return 1;
		}
		
		// start with result for i=1, m=2
		array[0] = 0;
		array[1] = 1;
		
		int lastCount = 2;
		long lastM = 2;
		for (int i=2; i<=n; i++, lastM<<=1) {
			
			// duplicate
			for (int j=0; j<lastCount; j++) {
				array [j+lastCount] = array[j] + lastM;
			}
			lastCount <<= 1;
			
			if (DEBUG) LOG.debug("i=" + i + ", m=" + (2*lastM) + ", before removal: count = " + lastCount + ", array = " + Arrays.toString(array));
			
			// remove some stuff
			int nextCount = 0;
			boolean iOdd = (i & 1) == 1;
			for (int j=0; j<lastCount; j++) {
				long elem = array[j];
				if (iOdd) {
					if (elem != lastM + (lastM>>2)) {
						array [nextCount++] = elem;
					}
				} else {
					if (elem != lastM && elem != lastM + (lastM>>1)) {
						array [nextCount++] = elem;
					}
				}
			}
			if (DEBUG) LOG.debug("i=" + i + ", m=" + (2*lastM) + ", after removal:  count = " + lastCount + ", array = " + Arrays.toString(array));
			lastCount = nextCount;
		}
		
		return lastCount;
	}
	
	/**
	 * Returns the "complement" of quadratic residues modulo 2^n.
	 * The complement c of a quadratic residue qr is computed as c = 2^n - qr if qr>0, c = 0 if qr==0. 
	 * 
	 * A004215 can be computed based on these sets.
	 * 
	 * @param n
	 * @return set of complements
	 */
	public static TreeSet<Long> getComplementOfQuadraticResiduesMod2PowN(int n) {
		TreeSet<Long> complement = new TreeSet<>();
		if (n<1) {
			complement.add(0L);
			return complement;
		}
		
		long m = 1L << n;
		List<Long> list = QuadraticResiduesMod2PowN.getQuadraticResiduesMod2PowN(n);
		for (long elem : list) {
			complement.add(elem>0 ? m - elem : 0);
		}
		return complement;
	}
}
