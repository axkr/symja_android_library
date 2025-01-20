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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Methods to generate quadratic residues or test for quadratic residuosity modulus 3^n.
 * This class is of no general interest, it was only used as a stepping stone to develop the methods for general p^n.
 * 
 * @author Tilman Neumann
 */
public class QuadraticResiduesMod3PowN {

	private static final Logger LOG = LogManager.getLogger(QuadraticResiduesMod3PowN.class);

	private static final boolean DEBUG = false;

	/**
	 * Computes if 'a' is a quadratic residue modulo 3^n.
	 * Iterative implementation for longs.
	 * 
	 * @param a argument
	 * @param n exponent of the modulus m=3^n
	 * @return true if 'a' is a quadratic residue modulo 3^n
	 */
	public static boolean isQuadraticResidueMod3PowN(long a, int n) {
		long m = (long) Math.pow(3, n);
		if (a >= m) a %= m; // now 0 <= a < m
		
		long lastM = m;
		for (int i=n; i>=1; i--) {
			lastM /= 3;
			if (DEBUG) LOG.debug("n=" + n + ", i=" + i + ", lastM = " + lastM + ", k = " + a);
			long rem = a % lastM;
			if (rem == 0) {
				long quotient = a / lastM;
				if ((i & 1) == 1) { // odd i
					if (quotient > 1) return false; // the quadratic residues mod 3 are {0, 1}
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
	 * Compute all quadratic residues modulus 3^n.
	 * 
	 * @param n
	 * @return list of quadratic residue modulus 3^n
	 */
	public static List<Long> getQuadraticResiduesMod3PowN_testAll(int n) {
		List<Long> list = new ArrayList<>();
		long m = (long) Math.pow(3, n);
		for (long a=0; a<m; a++) {
			if (isQuadraticResidueMod3PowN(a, n)) {
				list.add(a);
			}
		}
		return list;
	}

	/**
	 * Compute all quadratic residues modulus 3^n.
	 * @param n
	 * @return list of quadratic residues
	 */
	public static List<Long> getQuadraticResiduesMod3PowN(int n) {
		if (n == 0) return Arrays.asList(new Long[] {0L});
		
		// result for i=1, m=3
		List<Long> lastList = Arrays.asList(new Long[] {0L, 1L});

		long lastM = 3;
		for (int i=2; i<=n; i++, lastM *= 3) {
			List<Long> nextList = new ArrayList<>(lastList); // copy
			for (long elem : lastList) {
				nextList.add(elem + lastM);
			}
			for (long elem : lastList) {
				nextList.add(elem + 2*lastM);
			}
			
			// remove some stuff
			if (DEBUG) LOG.debug("i=" + i + ", m=" + (3*lastM) + ": nextList before removal = " + nextList);
			if ((i & 1) == 1) { // odd i
				nextList.remove(Long.valueOf(2*lastM));
			} else {
				nextList.remove(Long.valueOf(lastM));
				nextList.remove(Long.valueOf(2*lastM));
			}

			if (DEBUG) LOG.debug("i=" + i + ", m=" + (3*lastM) + ": nextList after removal = " + nextList);
			lastList = nextList;
		}
		return lastList;
	}
}
