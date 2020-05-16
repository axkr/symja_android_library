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
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.StringUtil;
import de.tilman_neumann.jml.factor.TestsetGenerator;
import de.tilman_neumann.jml.factor.tdiv.TDiv63Inverse;
import de.tilman_neumann.jml.factor.TestNumberNature;

/**
 * Analyze the frequency with which different k find a factor.
 * k that are multiples of 315, 45, 15, 3 perform best.
 * 
 * @author Tilman Neumann
 */
public class Lehman_AnalyzeKStructure {
	private static final Logger LOG = Logger.getLogger(Lehman_AnalyzeKStructure.class);

	private static class Progression {
		public int offset;
		public int step;
		
		public Progression(int offset, int step) {
			this.offset = offset;
			this.step = step;
		}
	}
	
	/** Analyze data accumulated over some progressions? Not successful yet... */
	private static final boolean ANALYZE_PROGRESSIONS = true;
	
	/** Use congruences a==kN mod 2^s if true, congruences a==(k+N) mod 2^s if false */
	private static final boolean USE_kN_CONGRUENCES = true;
	
	// algorithm options
	/** number of test numbers */
	private static final int N_COUNT = 100000;

	/** This is a constant that is below 1 for rounding up double values to long. */
	private static final double ROUND_UP_DOUBLE = 0.9999999665;

	private long fourN;
	private double sqrt4N;
	private final Gcd63 gcdEngine = new Gcd63();
	private final TDiv63Inverse tdiv = new TDiv63Inverse(1<<21);

	/** The number of N's factored by the individual k values */
	private int[][] kFactorCounts;
	private int arrayIndex;
	
	public long findSingleFactor(long N) {
		final int cbrt = (int) Math.cbrt(N);
		fourN = N<<2;
		sqrt4N = Math.sqrt(fourN);
		
		final int kLimit = cbrt;
		final double sixthRootTerm = 0.25 * Math.pow(N, 1/6.0); // double precision is required for stability
		for (int k=1; k <= kLimit; k++) {
			double sqrtK = Math.sqrt(k);
			final double sqrt4kN = sqrt4N * sqrtK;
			// only use long values
			final long aStart = (long) (sqrt4kN + ROUND_UP_DOUBLE); // much faster than ceil()
			long aLimit = (long) (sqrt4kN + sixthRootTerm / sqrtK);
			long aStep;
			if (USE_kN_CONGRUENCES) {
				long kN = k*N;
				if ((k & 1) == 0) {
					// k even -> make sure aLimit is odd
					aLimit |= 1L;
					aStep = 2;
				} else {
					final long kNp1 = kN + 1;
					if ((kNp1 & 3) == 0) {
						aLimit += (kNp1 - aLimit) & 7;
						aStep = 8;
					} else if ((kNp1 & 7) == 6) {
						final long adjust1 = (kNp1 - aLimit) & 31;
						final long adjust2 = (-kNp1 - aLimit) & 31;
						aLimit += adjust1<adjust2 ? adjust1 : adjust2;
						aStep = 4;
					} else { // (kN+1) == 2 (mod 8)
						final long adjust1 = (kNp1 - aLimit) & 15;
						final long adjust2 = (-kNp1 - aLimit) & 15;
						aLimit += adjust1<adjust2 ? adjust1 : adjust2;
						aStep = 4;
					}
				}
			} else {
				if ((k & 1) == 0) {
					// k even -> make sure aLimit is odd
					aLimit |= 1L;
					aStep = 2;
				} else {
					final long kPlusN = k + N;
					if ((kPlusN & 3) == 0) {
						aLimit += ((kPlusN - aLimit) & 7);
						aStep = 8;
					} else {
						final long adjust1 = (kPlusN - aLimit) & 15;
						final long adjust2 = (-kPlusN - aLimit) & 15;
						aLimit += adjust1<adjust2 ? adjust1 : adjust2;
						aStep = 4; // stepping over both adjusts with step width 16 would be more exact but is not faster
					}
				}
			}
			
			// processing the a-loop top-down is faster than bottom-up
			final long fourkN = k * fourN;
			for (long a=aLimit; a >= aStart; a-=aStep) {
				final long test = a*a - fourkN;
				// Here test<0 is possible because of double to long cast errors in the 'a'-computation.
				// But then b = Math.sqrt(test) gives 0 (sic!) => 0*0 != test => no errors.
				final long b = (long) Math.sqrt(test);
				if (b*b == test) {
					long gcd = gcdEngine.gcd(a+b, N);
					if (gcd>1 && gcd<N) {
						kFactorCounts[k][arrayIndex]++;
					}
				}
			}
		}
		
		// If sqrt(4kN) is very near to an exact integer then the fast ceil() in the 'aStart'-computation
		// may have failed. Then we need a "correction loop":
		final int kTwoA = (((cbrt >> 6) + 6) / 6) * 6;
		for (int k=kTwoA + 1; k <= kLimit; k++) {
			long a = (long) (sqrt4N * Math.sqrt(k)+ ROUND_UP_DOUBLE) - 1;
			long test = a*a - k*fourN;
			long b = (long) Math.sqrt(test);
			if (b*b == test) {
				//return gcdEngine.gcd(a+b, N);
			}
	    }

		return 1; // fail
	}
	
	private void test() {
		// zero-init count arrays
		int kMax = (int) Math.cbrt(1L<<(39+1));
		kFactorCounts = new int[kMax][10];
		String[] kFactorStrings = new String[kMax];
		int maxFactorStrLength = 0;
		for (int k=1; k<kMax; k++) {
			String factorString = tdiv.factor(BigInteger.valueOf(k)).toString();
			kFactorStrings[k] = factorString;
			int len = factorString.length();
			if (len > maxFactorStrLength) {
				maxFactorStrLength = len;
			}
		}

		// test from 30 to 39 bits
		for (arrayIndex=0; arrayIndex<10; arrayIndex++) {
			int bits = arrayIndex+30;
			BigInteger[] testNumbers = TestsetGenerator.generate(N_COUNT, bits, TestNumberNature.MODERATE_SEMIPRIMES);
			LOG.info("Test N having " + bits + " bit");
			for (BigInteger N : testNumbers) {
				this.findSingleFactor(N.longValue());
			}
		}
		
		String factorStringMask = StringUtil.repeat(" ", maxFactorStrLength);
		for (int k=1; k<kMax; k++) {
			String kStr = StringUtil.formatLeft(String.valueOf(k), "     ");
			String factorStr = StringUtil.formatLeft(kFactorStrings[k], factorStringMask);
			String countsStr = "";
			int[] successes = kFactorCounts[k];
			for (int i=0; i<10; i++) {
				String countStr = StringUtil.formatRight(String.valueOf(successes[i]), "    ");
				countsStr += countStr + ", ";
			}
			countsStr = countsStr.substring(0, countsStr.length()-2);
			LOG.info("k = " + kStr + " = " + factorStr + ": successes = " + countsStr);
		}
		
		if (ANALYZE_PROGRESSIONS) {
			LOG.info("");
			for (int i=0; i<10; i++) {
				LOG.info("Analyze progressions for " + (30+i) + " bit numbers:");
				TreeMap<Integer, List<Progression>> successRate2Progressions = new TreeMap<Integer, List<Progression>>(Collections.reverseOrder());
				for (int j=1; j<1000; j++) {
					analyzeProgression(j, 2*j, i, successRate2Progressions);
					analyzeProgression(2*j, 2*j, i, successRate2Progressions);
				}
				int j=0;
				for (Map.Entry<Integer, List<Progression>> entry : successRate2Progressions.entrySet()) {
					int successRate = entry.getKey();
					List<Progression> progressions = entry.getValue();
					for (Progression progression : progressions) {
						LOG.info("    #" + j + ": Progression (" + progression.offset + ", " +  progression.step + ") has successRate " + successRate);
						j++;
					}
					if (j>=100) break;
				}
			}
		}
	}

	private void analyzeProgression(int start, int step, int arrayIndex, TreeMap<Integer, List<Progression>> successRate2Progressions) {
		int successSum = 0;
		int numCount = 0;
		final int kLimit = (int) Math.cbrt(1L<<(30+arrayIndex+1));
		for (int k=start; k<kLimit; k+=step) {
			int successCount = kFactorCounts[k][arrayIndex];
			successSum += successCount;
			numCount++;
		}
		int avgSuccessCount = (int) (successSum / (float) numCount);
		//LOG.info("    Progression " + step + "*m + " + start + ": Avg. successes = " + avgSuccessCount + ", #tests = " + numCount);
		
		List<Progression> progressions = successRate2Progressions.get(avgSuccessCount);
		if (progressions == null) progressions = new ArrayList<Progression>();
		progressions.add(new Progression(start, step));
		successRate2Progressions.put(avgSuccessCount, progressions);
	}
	
	public static void main(String[] args) {
    	ConfigUtil.initProject();
		// test N with BITS bits
    	Lehman_AnalyzeKStructure testEngine = new Lehman_AnalyzeKStructure();
		testEngine.test();
	}
}
