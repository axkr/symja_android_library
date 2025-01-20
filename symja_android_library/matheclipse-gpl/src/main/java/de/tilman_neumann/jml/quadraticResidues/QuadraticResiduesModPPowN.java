/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is pd on the PSIQS 4.0 factoring project.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.modular.JacobiSymbol;

/**
 * Methods to generate quadratic residues or test for quadratic residuosity modulus p^n,
 * where p is an odd prime.
 * 
 * @author Tilman Neumann
 */
public class QuadraticResiduesModPPowN {

	private static final Logger LOG = LogManager.getLogger(QuadraticResiduesModPPowN.class);

	private static final boolean DEBUG = false;

	private static final JacobiSymbol JACOBI_ENGINE = new JacobiSymbol();
	private static final Gcd63 GCD_ENGINE = new Gcd63();

	/**
	 * Computes if 'a' is a quadratic residue modulo p^n.
	 * Iterative implementation for longs.
	 * 
	 * @param a argument
	 * @param p
	 * @param n exponent of the modulus m=p^n
	 * @return true if 'a' is a quadratic residue modulo p^n
	 */
	private static boolean isQuadraticResidueModPPowN_v1(long a, int p, int n) {
		long m = (long) Math.pow(p, n);
		if (a >= m) a %= m; // now 0 <= a < m
		
		long lastM = m;
		for (int i=n; i>=1; i--) {
			lastM /= p;
			if (DEBUG) LOG.debug("n=" + n + ", i=" + i + ", lastM = " + lastM + ", k = " + a);
			long rem = a % lastM;
			if (rem == 0) {
				long quotient = a / lastM;
				if ((i & 1) == 1) { // odd i
					// if quotient is not a quadratic residue mod p
					if (quotient>1 && JACOBI_ENGINE.jacobiSymbol((int)quotient, p) != 1) return false;
				} else {
					if (quotient != 0) return false;
				}
				return true; // otherwise a = rem = 0, which is always true
			}
			a = rem;
		}
		
		return true;
	}
	
	/**
	 * Implementation following https://en.wikipedia.org/wiki/Quadratic_residue. Much faster than the first approach.
	 * @param a
	 * @param p
	 * @param n
	 * @return true if 'a' is a quadratic residue modulo p^n
	 */
	// TODO return -1, 0, 1 to check for non-residues, too ?
	public static boolean isQuadraticResidueModPPowN/*_v2*/(long a, int p, int n) {
		long m = (long) Math.pow(p, n);
		if (a >= m) a %= m; // now 0 <= a < m
		
		// "A number a relatively prime to an odd prime p is a residue modulo any power of p if and only if it is a residue modulo p"
		long gcd = GCD_ENGINE.gcd(a, p);
		if (gcd==1) return JACOBI_ENGINE.jacobiSymbol((int)a, p) == 1;

		// "If the modulus is p^n, then p^k*a ..."
		long aRest = a;
		int k = 0;
		while (aRest>0 && aRest % p == 0) {
			aRest /= p;
			k++;
		}
		
		// "... is a residue modulo p^n if k ≥ n"
		if (aRest == 0 || k >= n) return true;
		
		// now exp < n -> "... is a nonresidue modulo p^n if k < n is odd"
		if ((k & 1) == 1) return false; // non-residue
		
		// "... is a residue modulo p^n if k < n is even and a is a residue"
	    // "... is a nonresidue modulo p^n if k < n is even and a is a nonresidue."
		return JACOBI_ENGINE.jacobiSymbol((int)aRest, p) == 1;
	}

	/**
	 * Compute all quadratic residues modulus p^n.
	 * 
	 * @param p
	 * @param n
	 * @return list of quadratic residue modulus p^n
	 */
	static List<Long> getQuadraticResiduesModPPowN_testAll(int p, int n) {
		List<Long> list = new ArrayList<>();
		long m = (long) Math.pow(p, n);
		for (long a=0; a<m; a++) {
			if (isQuadraticResidueModPPowN_v1(a, p, n)) {
				list.add(a);
			}
		}
		return list;
	}

	/**
	 * Compute all quadratic residues modulus p^n.
	 * 
	 * @param p
	 * @param n
	 * @return list of quadratic residue modulus p^n
	 */
	static List<Long> getQuadraticResiduesModPPowN_testAll_v2(int p, int n) {
		List<Long> list = new ArrayList<>();
		long m = (long) Math.pow(p, n);
		for (long a=0; a<m; a++) {
			if (isQuadraticResidueModPPowN/*_v2*/(a, p, n)) {
				list.add(a);
			}
		}
		return list;
	}

	/**
	 * Compute all quadratic residues modulus p^n. This is the fastest implementation yet.
	 * @param p
	 * @param n
	 * @return list of quadratic residues
	 */
	public static List<Long> getQuadraticResiduesModPPowN(int p, int n) {
		if (n == 0) return Arrays.asList(new Long[] {0L});
		
		List<Long> pResidues = getBaseResidues(p);
		
		// result for i=1
		List<Long> lastList = pResidues;

		long lastM = p;
		for (int i=2; i<=n; i++, lastM *= p) {
			List<Long> nextList = powerUp(lastList, p, lastM);
			
			// remove some stuff
			if (DEBUG) LOG.debug("i=" + i + ", m=" + (p*lastM) + ": nextList before removal = " + nextList);
			if ((i & 1) == 1) {
				// odd i: remove all {j*lastM} where j is not a quadratic residue modulo p.
				// This could be sped up computing the complement of pResidues.
				for (int j = 1; j<p; j++) {
					long elem = Long.valueOf(j*lastM);
					if (!pResidues.contains(Long.valueOf(j))) {
						nextList.remove(elem);
					}
				}
			} else {
				// even i: remove all {j*lastM}, j=1...p-1
				for (int j = 1; j<p; j++) {
					nextList.remove(Long.valueOf(j*lastM));
				}
			}

			if (DEBUG) LOG.debug("i=" + i + ", m=" + (p*lastM) + ": nextList after removal = " + nextList);
			lastList = nextList;
		}
		return lastList;
	}
	
	private static List<Long> getBaseResidues(int p) {
		// We use the Jacobi symbol to compute the quadratic residues modulo p only because it is faster than the Legendre symbol.
		// Note that the result will be correct only for p being an odd prime.
		// For some reason, Arrays.asList(new Long[] {0L, 1L}) would throw an UnsupportedOperationException if we add Longs to the list...
		ArrayList<Long> result = new ArrayList<>();
		result.add(0L);
		result.add(1L);
		
		for (int i=2; i<p; i++) {
			if (JACOBI_ENGINE.jacobiSymbol(i, p) == 1) {
				result.add(Long.valueOf(i));
			}
		}
		if (DEBUG) LOG.debug("p=" + p + ": result = " + result);
		return result;
	}
	
	private static ArrayList<Long> powerUp(List<Long> lastList, int p, long lastM) {
		ArrayList<Long> nextList = new ArrayList<>(lastList); // copy
		for (int i=1; i<p; i++) {
			long offset = i*lastM;
			for (long elem : lastList) {
				nextList.add(elem + offset);
			}
		}
		return nextList;
	}
}
