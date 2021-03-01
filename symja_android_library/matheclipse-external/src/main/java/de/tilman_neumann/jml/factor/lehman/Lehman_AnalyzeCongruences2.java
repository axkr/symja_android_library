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
package de.tilman_neumann.jml.factor.lehman;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.quadraticResidues.QuadraticResiduesMod2PowN;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.jml.factor.TestsetGenerator;
import de.tilman_neumann.jml.factor.TestNumberNature;

/**
 * Analyze a-values that help the Lehman algorithm to find factors, modulo powers of 2.
 * 
 * If we analyze the data in terms of (a0, adjust) pairs, we notice that we always get antidiagonals, each of
 * them representing a "successful a", because a == (a0 + adjust) (mod m), with m=2^n. So all we need to investigate is "a" (mod m).
 * 
 * Version 2 doubles m step-by-step and analyzes or allows to analyze incremental changes.
 * Only odd k are analyzed, because the result for even k is trivial (we need all odd "a"-values).
 * 
 * The number of successful a from original k+N congruences are 1, 2, 6, 16, 64, 256, 1024, ...
 * The last incremental improvement occurs at m=16. This is trivial.
 * 
 * For the more interesting k*N congruences I found experimentally:
 * The number of successful a are a(n) = 1, 2, 6, 16, 56, 192, 736, 2816, 11136, 44032, ... (not found in OEIS)
 *                  dropped a are d(n) = 0, 2, 2,  8,  8,  32,  32,  128,   128,   512, ... (quite trivial)
 * 
 * From that data I derived the iterative formula
 * a(1) = 1
 * if (m > 1) {
 *     n = ld(m);
 *     rightExp = 2*floor(n/2) - 1
 *     a(n) = a(n-1)*4 - 2^rightExp
 * }
 * 
 * having first values
 * n= 1, m=   2: a(n) = 2^0 = 1
 * n= 2, m=   4: a(n) = (2^0)*4 - 2^1 = 2^2 - 2^1 = 2
 * n= 3, m=   8: a(n) = (2^2 - 2^1)*4 - 2^1 = 2^4 - 2^3 - 2^1 = 6
 * n= 4, m=  16: a(n) = (2^4 - 2^3 - 2^1)*4 - 2^3 = 2^6 - 2^5 - 2^4 = 16
 * n= 5, m=  32: a(n) = (2^6 - 2^5 - 2^4)*4 - 2^3 = 2^8 - 2^7 - 2^6 - 2^3 = 56
 * n= 6, m=  64: a(n) = (2^8 - 2^7 - 2^6 - 2^3)*4 - 2^5 = 2^10 - 2^9 - 2^8 - 2^6 = 192
 * n= 7, m= 128: a(n) = (2^10 - 2^9 - 2^8 - 2^6)*4 - 2^5 = 2^12 - 2^11 - 2^10 - 2^8 - 2^5 = 736
 * n= 8, m= 256: a(n) = (2^12 - 2^11 - 2^10 - 2^8 - 2^5)*4 - 2^7 = 2^14 - 2^13 - 2^12 - 2^10 - 2^8 = 2816
 * n= 9, m= 512: a(n) = (2^14 - 2^13 - 2^12 - 2^10 - 2^8)*4 - 2^7 = 2^16 - 2^15 - 2^14 - 2^12 - 2^10 - 2^7 = 11136
 * n=10, m=1024: a(n) = (2^16 - 2^15 - 2^14 - 2^12 - 2^10 - 2^7)*4 - 2^9 = 2^18 - 2^17 - 2^16 - 2^14 - 2^12 - 2^10 = 44032
 * ...
 * 
 * Computing more values from the iterative formula gives the sequence (starting at n=1)
 * a(n) = 1, 2, 6, 16, 56, 192, 736, 2816, 11136, 44032, 175616, 700416, 2799616, 11190272, 44752896, 178978816, 715882496, 2863398912, 11453464576, 45813334016, 183252811776, 733009149952, 2932034502656, 11728129622016, 46912510099456, 187650006843392, 750599993819136, 3002399841058816, 12009599230017536, 48038396383199232, ...
 *
 * Closed formulas I found later on:
 * a(n) = 2^(n-2) * A023105(n), for n>0
 * a(2n+2) = 2*A083086(2n), for n>=0
 * 
 * @author Tilman Neumann
 */
public class Lehman_AnalyzeCongruences2 {
	private static final Logger LOG = Logger.getLogger(Lehman_AnalyzeCongruences2.class);
	
	/** Use congruences a==kN mod 2^s if true, congruences a==(k+N) mod 2^s if false */
	private static final boolean USE_kN_CONGRUENCES = true;
	private static final boolean PRINT_LAST_SUCCESSFUL_A = false;

	private final Gcd63 gcdEngine = new Gcd63();
	
	private int[][] counts; // dimensions: kN%m, a%m
	List<Integer>[] aForKN = null;

	public long findSingleFactor(long N, int m) {
		int cbrt = (int) Math.ceil(Math.cbrt(N));
		double sixthRoot = Math.pow(N, 1/6.0); // double precision is required for stability
		for (int k=1; k <= cbrt; k++) {
			long fourKN = k*N<<2;
			double fourSqrtK = Math.sqrt(k<<4);
			long sqrt4kN = (long) Math.ceil(Math.sqrt(fourKN));
			long limit = (long) (sqrt4kN + sixthRoot / fourSqrtK);
			// LOG.debug("k=" + k + ": aRange = " + (limit - sqrt4kN));
			for (long a = sqrt4kN; a <= limit; a++) {
				final long test = a*a - fourKN;
				final long b = (long) Math.sqrt(test);
				if (b*b == test) {
					long gcd = gcdEngine.gcd(a+b, N);
					if (gcd>1 && gcd<N) {
						// congruences are the same for all odd k
						if ((k & 1) == 1) {
							long kNTerm = USE_kN_CONGRUENCES ? k*N : k+N;
							counts[(int)(kNTerm%m)][(int)(a%m)]++;
						}
						return gcd; // removes the blur at even k
					}
				}
			}
	    }
		
		return 0; // Fail
	}
	
	private void test() {
		for (int m = 2; ; m<<=1) {
			LOG.info("Test m = " + m + " ...");
			
			counts = new int[m][m];
			
			int bits = 30;
			BigInteger[] testNumbers = TestsetGenerator.generate(m*2000, bits, TestNumberNature.MODERATE_SEMIPRIMES);
			
			for (BigInteger N : testNumbers) {
				//if (N.mod(I_6).equals(I_5)) // makes no difference
				this.findSingleFactor(N.longValue(), m); // this is the expensive part
			}
			
			// extrapolate last m results
			List<Integer>[] lastAForKN = null;
			if (aForKN != null) {
				lastAForKN = createListArray(m);
				int kN=0;
				for (; kN<m/2; kN++) {
					// copy old a-values
					lastAForKN[kN] = new ArrayList<>();
					List<Integer> lastAList = aForKN[kN];
					if (lastAList != null && !lastAList.isEmpty()) {
						lastAForKN[kN].addAll(lastAList);
						// extend on horizontal axis
						if (lastAList.get(0) == 0) {
							lastAForKN[kN].add(m/2);
							lastAList.remove(0);
						}
						Collections.reverse(lastAList);
						for (int a : lastAList) {
							int elem = m - a;
							lastAForKN[kN].add(elem);
						}
					}
				}
				// extend on vertical axis
				for (; kN<m; kN++) {
					lastAForKN[kN] = new ArrayList<>(lastAForKN[kN-(m>>1)]);
				}
			}
			// now we have extrapolated the last a-values for [m/2][m/2] to [m][m]
			
			LOG.debug("Compute a-lists...");
			String kNStr = USE_kN_CONGRUENCES ? "kN" : "k+N";
			aForKN = createListArray(m);
			int totalACount = 0;
			
			for (int kN=0; kN<m; kN++) {
				int[] aSuccessCounts = counts[kN];
				int knSuccessCount = 0;
				ArrayList<Integer> aList = new ArrayList<>();
				for (int a=0; a<m; a++) {
					if (aSuccessCounts[a] > 0) {
						knSuccessCount += aSuccessCounts[a];
						aList.add(a);
					}
				}
				if (knSuccessCount > 0) {
					int avgASuccessCount = knSuccessCount/aList.size(); // avg. factoring successes per "a"
					LOG.info("(" + kNStr + ")%" + m + "=" + kN + ": successful a = " + aList + " (mod " + m + "), avg hits = " + avgASuccessCount);
				}
				// collect data plot for odd k (results are equal for all odd k)
				aForKN[kN] = aList;
				totalACount += aList.size();
			}
			LOG.info("");

			// the following block is only for debugging
			if (PRINT_LAST_SUCCESSFUL_A) {
				if (lastAForKN != null) {
					int knStart = USE_kN_CONGRUENCES ? 1 : 0;
					for (int kN=knStart; kN<m; kN+=2) {
						String row = "";
						int i=0;
						for (int a : lastAForKN[kN]) {
							while (i++<a) row += " ";
							row += "x";
						}
						LOG.info(row);
					}
				}
				LOG.info("");
			}

			// compute the "a" that have changed from successful at last m to unsuccessful at current m
			int totalDroppedACount = 0;
			int knStart = USE_kN_CONGRUENCES ? 1 : 0;
			if (lastAForKN != null) {
				for (int kN=knStart; kN<m; kN+=2) {
					List<Integer> droppedAList = new ArrayList<Integer>();
					for (int a : lastAForKN[kN]) {
						if (!aForKN[kN].contains(a)) {
							droppedAList.add(a);
						}
					}
					if (!droppedAList.isEmpty()) {
						LOG.info("(" + kNStr + ")%" + m + "=" + kN + ": dropped a = " + droppedAList + " (mod " + m + ")");
						totalDroppedACount += droppedAList.size();
					}
				}
				LOG.info("");
			}
			
			// create data plot for odd k
			for (int kN=knStart; kN<m; kN+=2) {
				String row = "";
				int i=0;
				if (lastAForKN == null) {
					for (int a : aForKN[kN]) {
						while (i++<a) row += " ";
						row += "x";
					}
				} else {
					for (int a : lastAForKN[kN]) {
						while (i++<a) row += " ";
						row += aForKN[kN].contains(a) ? "x" : ".";
					}
				}
				LOG.info(row);
			}

			LOG.info("");
			LOG.info("totalACount = " + totalACount);
			LOG.info("totalDroppedACount = " + totalDroppedACount);
			LOG.info("");
		}
	}
	
	// Encapsulate compiler warning and suppress it.
	@SuppressWarnings("unchecked")
	private static List<Integer>[] createListArray(int size) {
		return new List[size];
	}
	
	private static void computeHypotheticalACounts(int num) {
		List<Long> aCounts = new ArrayList<>();
		aCounts.add(1L); // for n=1, m=2
		for (int n=2; n<=num; n++) {
			long lastACount = aCounts.get(n-2);
			int rightExp = ((n>>1) << 1) - 1;
			long nextACount = (lastACount<<2L) - (1L<<rightExp);
			aCounts.add(nextACount);
			
			// test closed formula
//			assertEquals((1<<(n-2)) * QuadraticResiduesMod2PowN.getNumberOfQuadraticResiduesMod2PowN(n), nextACount);
		}
		LOG.info("Hypothetical aCounts = " + aCounts);
		LOG.info("");
	}

	public static void main(String[] args) {
    	ConfigUtil.initProject();
    	
    	computeHypotheticalACounts(30);
    	
    	new Lehman_AnalyzeCongruences2().test();
	}
}
