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
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.jml.factor.TestsetGenerator;
import de.tilman_neumann.jml.factor.TestNumberNature;

/**
 * Analyze the moduli of a-values that help the Lehman algorithm to find factors.
 * 
 * If we analyze the data in terms of (a0, adjust) pairs, we notice that we always get antidiagonals, each of
 * them representing a "successful a", because a == (a0 + adjust) (mod KNMOD). So all we need to investigate is "a" (mod KNMOD).
 * 
 * Version 2 doubles KNMOD step-by-step and analyzes or allows to analyze incremental changes.
 * Only odd k are analyzed, because the result for even k is trivial (we need all odd "a"-values).
 * 
 * The number of successful a from original k+N congruences are 1, 2, 6, 16, 64, 256, 1024, ...
 * The last incremental improvement occurs at KNMOD=16. This is trivial.
 * 
 * For the more interesting k*N congruences I found experimentally:
 * The number of successful a are #a = 1, 2, 6, 16, 56, 192, 736, 2816, 11136, 44032, ... (not found in OEIS)
 *                  dropped a are #d = 0, 2, 2,  8,  8,  32,  32,  128,   128,   512, ...
 * 
 * This yields the iterative solutions
 * #a(KNMOD_EXP=1) = 1
 * if (KNMOD > 1) {
 *     KNMOD_EXP = ld(KNMOD);
 *     rightExp = 2*floor(KNMOD_EXP/2) - 1
 *     #a(KNMOD_EXP) = #a(KNMOD_EXP-1)*4 - 2^rightExp
 * }
 * 
 * The first values are
 * KNMOD=   2: KNMOD_EXP= 1, #a = 2^0 = 1
 * KNMOD=   4: KNMOD_EXP= 2, #a = (2^0)*4 - 2^1 = 2^2 - 2^1 = 2
 * KNMOD=   8: KNMOD_EXP= 3, #a = (2^2 - 2^1)*4 - 2^1 = 2^4 - 2^3 - 2^1 = 6
 * KNMOD=  16: KNMOD_EXP= 4, #a = (2^4 - 2^3 - 2^1)*4 - 2^3 = 2^6 - 2^5 - 2^4 = 16
 * KNMOD=  32: KNMOD_EXP= 5, #a = (2^6 - 2^5 - 2^4)*4 - 2^3 = 2^8 - 2^7 - 2^6 - 2^3 = 56
 * KNMOD=  64: KNMOD_EXP= 6, #a = (2^8 - 2^7 - 2^6 - 2^3)*4 - 2^5 = 2^10 - 2^9 - 2^8 - 2^6 = 192
 * KNMOD= 128: KNMOD_EXP= 7, #a = (2^10 - 2^9 - 2^8 - 2^6)*4 - 2^5 = 2^12 - 2^11 - 2^10 - 2^8 - 2^5 = 736
 * KNMOD= 256: KNMOD_EXP= 8, #a = (2^12 - 2^11 - 2^10 - 2^8 - 2^5)*4 - 2^7 = 2^14 - 2^13 - 2^12 - 2^10 - 2^8 = 2816
 * KNMOD= 512: KNMOD_EXP= 9, #a = (2^14 - 2^13 - 2^12 - 2^10 - 2^8)*4 - 2^7 = 2^16 - 2^15 - 2^14 - 2^12 - 2^10 - 2^7 = 11136
 * KNMOD=1024: KNMOD_EXP=10, #a = (2^16 - 2^15 - 2^14 - 2^12 - 2^10 - 2^7)*4 - 2^9 = 2^18 - 2^17 - 2^16 - 2^14 - 2^12 - 2^10 = 44032
 * etc...
 * 
 * So my hypothesis is that
 * #a = 1, 2, 6, 16, 56, 192, 736, 2816, 11136, 44032, 175616, 700416, 2799616, 11190272, 44752896, 178978816, 715882496, 2863398912, 11453464576, 45813334016, 183252811776, 733009149952, 2932034502656, 11728129622016, 46912510099456, 187650006843392, 750599993819136, 3002399841058816, 12009599230017536, 48038396383199232, ...
 * 
 * For #a[2*n] = 2, 16, 192, 2816, 44032, 700416, 11190272, 178978816, 2863398912, 45813334016, 733009149952, ...
 * we have #a[2*n]/2 = A083086[2*n].
 * 
 * It shouldn't be too difficult to find a closed formula as well.
 * 
 * @author Tilman Neumann
 */
public class Lehman_AnalyzeCongruences2 {
	private static final Logger LOG = Logger.getLogger(Lehman_AnalyzeCongruences2.class);
	
	/** Use congruences a==kN mod 2^s if true, congruences a==(k+N) mod 2^s if false */
	private static final boolean USE_kN_CONGRUENCES = true;
	private static final boolean PRINT_LAST_SUCCESSFUL_A = false;

	private final Gcd63 gcdEngine = new Gcd63();
	
	private int[][] counts; // dimensions: kN%KNMOD, a%KNMOD
	ArrayList<Integer>[] aForKN = null;

	public long findSingleFactor(long N, int KNMOD) {
		int cbrt = (int) Math.ceil(Math.cbrt(N));
		double sixthRoot = Math.pow(N, 1/6.0); // double precision is required for stability
		for (int k=1; k <= cbrt; k++) {
			long fourKN = k*N<<2;
			double fourSqrtK = Math.sqrt(k<<4);
			long sqrt4kN = (long) Math.ceil(Math.sqrt(fourKN));
			long limit = (long) (sqrt4kN + sixthRoot / fourSqrtK);
			for (long a0 = sqrt4kN; a0 <= limit; a0++) {
				for (int adjust=0; adjust<KNMOD; adjust++) {
					long a = a0 + adjust;
					final long test = a*a - fourKN;
					final long b = (long) Math.sqrt(test);
					if (b*b == test) {
						long gcd = gcdEngine.gcd(a+b, N);
						if (gcd>1 && gcd<N) {
							// congruences are the same for all odd k
							if ((k & 1) == 1) {
								// We know that all elements of an antidiagonal (a0, adjust) with a0 + adjust == a (mod KNMOD)
								// represent the same "successful a". Thus we only need to store results for "a" !
								long kNTerm = USE_kN_CONGRUENCES ? k*N : k+N;
								counts[(int)(kNTerm%KNMOD)][(int)(a%KNMOD)]++;
							}
							return gcd; // removes the blur at even k!
						}
					}
				}
			}
	    }
		
		return 0; // Fail
	}
	
	private void test() {
		for (int KNMOD = 2; ; KNMOD<<=1) {
			LOG.info("Test KNMOD = " + KNMOD + " ...");
			
			counts = new int[KNMOD][KNMOD];
			
			int bits = 30;
			BigInteger[] testNumbers = TestsetGenerator.generate(KNMOD*2000, bits, TestNumberNature.MODERATE_SEMIPRIMES);
			
			for (BigInteger N : testNumbers) {
				//if (N.mod(I_6).equals(I_5)) // makes no difference
				this.findSingleFactor(N.longValue(), KNMOD); // this is the expensive part
			}
			
			// extrapolate last KNMOD results
			List<Integer>[] lastAForKN = null;
			if (aForKN != null) {
				lastAForKN = new List[KNMOD];
				int kN=0;
				for (; kN<KNMOD/2; kN++) {
					// copy old a-values
					lastAForKN[kN] = new ArrayList<>();
					ArrayList<Integer> lastAList = aForKN[kN];
					if (lastAList != null && !lastAList.isEmpty()) {
						lastAForKN[kN].addAll(lastAList);
						// extend on horizontal axis
						if (lastAList.get(0) == 0) {
							lastAForKN[kN].add(KNMOD/2);
							lastAList.remove(0);
						}
						Collections.reverse(lastAList);
						for (int a : lastAList) {
							int elem = KNMOD - a;
							lastAForKN[kN].add(elem);
						}
					}
				}
				// extend on vertical axis
				for (; kN<KNMOD; kN++) {
					lastAForKN[kN] = new ArrayList<>(lastAForKN[kN-(KNMOD>>1)]);
				}
			}
			// now we have extrapolated the last a-values for [KNMOD/2][KNMOD/2] to [KNMOD][KNMOD]
			
			LOG.debug("Compute a-lists...");
			String kNStr = USE_kN_CONGRUENCES ? "kN" : "k+N";
			aForKN = new ArrayList[KNMOD];
			int totalACount = 0;
			
			for (int kN=0; kN<KNMOD; kN++) {
				int[] aSuccessCounts = counts[kN];
				int knSuccessCount = 0;
				ArrayList<Integer> aList = new ArrayList<>();
				for (int a=0; a<KNMOD; a++) {
					if (aSuccessCounts[a] > 0) {
						knSuccessCount += aSuccessCounts[a];
						aList.add(a);
					}
				}
				if (knSuccessCount > 0) {
					int avgASuccessCount = knSuccessCount/aList.size(); // avg. factoring successes per "a"
					LOG.info("(" + kNStr + ")%" + KNMOD + "=" + kN + ": successful a = " + aList + " (mod " + KNMOD + "), avg hits = " + avgASuccessCount);
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
					for (int kN=knStart; kN<KNMOD; kN+=2) {
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

			// compute the "a" that have changed from successful at last KNMOD to unsuccessful at current KNMOD
			int totalDroppedACount = 0;
			int knStart = USE_kN_CONGRUENCES ? 1 : 0;
			if (lastAForKN != null) {
				for (int kN=knStart; kN<KNMOD; kN+=2) {
					List<Integer> droppedAList = new ArrayList<Integer>();
					for (int a : lastAForKN[kN]) {
						if (!aForKN[kN].contains(a)) {
							droppedAList.add(a);
						}
					}
					if (!droppedAList.isEmpty()) {
						LOG.info("(" + kNStr + ")%" + KNMOD + "=" + kN + ": dropped a = " + droppedAList + " (mod " + KNMOD + ")");
						totalDroppedACount += droppedAList.size();
					}
				}
				LOG.info("");
			}
			
			// create data plot for odd k
			for (int kN=knStart; kN<KNMOD; kN+=2) {
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
	
	private static void computeHypotheticalACounts(int num) {
		List<Long> aCounts = new ArrayList<>();
		aCounts.add(1L); // for KNMOD=2, KNMOD_EXP=1
		for (int KNMOD_EXP = 2; KNMOD_EXP<=num; KNMOD_EXP++) {
			long lastACount = aCounts.get(KNMOD_EXP-2);
			int rightExp = ((KNMOD_EXP>>1) << 1) - 1;
			long nextACount = (lastACount<<2L) - (1L<<rightExp);
			aCounts.add(nextACount);
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
