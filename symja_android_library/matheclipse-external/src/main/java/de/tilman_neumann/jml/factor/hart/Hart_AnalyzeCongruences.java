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
package de.tilman_neumann.jml.factor.hart;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.TestNumberNature;
import de.tilman_neumann.jml.factor.TestsetGenerator;
import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.util.ConfigUtil;

/**
 * Analyze the congruences best matching Hart's one-line factor algorithm when tested with 4kN values,
 * where k are multiples of some K_MULT.
 * 
 * @author Tilman Neumann
 */
public class Hart_AnalyzeCongruences {
	private static final Logger LOG = Logger.getLogger(Hart_AnalyzeCongruences.class);

	// algorithm options
	/** number of test numbers */
	private static final int N_COUNT = 100000;
	/** the bit size of N to start with */
	private static final int START_BITS = 30;
	/** the increment in bit size from test set to test set */
	private static final int INCR_BITS = 1;
	/** maximum number of bits to test (no maximum if null) */
	private static final Integer MAX_BITS = 63;

	/** Use congruences a==kN mod 2^s if true, congruences a==(k+N) mod 2^s if false */
	private static final boolean USE_kN_CONGRUENCES = true;
	
	/**
	 * We only test k-values that are multiples of this constant.
	 * Best values for performance are 315, 45, 105, 15 and 3, in that order.
	 */
	private static final int K_MULT = 3*3*5*7; // 315

	private static final int KMOD = 6;
	private static final int KNMOD = 6;
	private static final int AMOD = 6;

	/** This is a constant that is below 1 for rounding up double values to long. */
	private static final double ROUND_UP_DOUBLE = 0.9999999665;
	
	/** Size of arrays */
	private static final int I_MAX = 1<<20;

	private double[] sqrt;

	private final Gcd63 gcdEngine = new Gcd63();
	
	// dimensions: k%KMOD, (N+k)%KNMOD, a%AMOD, adjust%AMOD
	private int[][][][] counts;
	
	public Hart_AnalyzeCongruences() {
		// Precompute sqrts for all k < I_MAX
		sqrt = new double[I_MAX];
		for (int i=1; i<I_MAX; i++) {
			sqrt[i] = Math.sqrt(i*K_MULT);
		}
	}

	private void findSingleFactor(long N) {
		long fourN = N<<2;
		double sqrt4N = Math.sqrt(fourN);
		long a, b, test, gcd;
		int k = K_MULT;
		try {
			for (int i=1; ;) {
				final long a0 = (long) (sqrt4N * sqrt[i++] + ROUND_UP_DOUBLE);
				for (int adjust=0; adjust<AMOD; adjust++) {
					a = a0 + adjust;
					test = a*a - k * fourN;
					b = (long) Math.sqrt(test);
					if (b*b == test) {
						gcd = gcdEngine.gcd(a+b, N);
						if (gcd>1 && gcd<N) {
							if (USE_kN_CONGRUENCES) {
								counts[k%KMOD][(int)((k*N)%KNMOD)][(int)(a0%AMOD)][adjust]++;
							} else {
								counts[k%KMOD][(int)((k+N)%KNMOD)][(int)(a0%AMOD)][adjust]++;
							}
							return; // removes the blur at even k!
						}
					}
				}
				k += K_MULT;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// this may happen if this implementation is tested with doTDivFirst==false and N having
			// very small factors, or if N is too big
			return;
		}
	}
	
	private void testRange(int bits) {
		counts = new int[KMOD][KNMOD][AMOD][AMOD];
		
		BigInteger N_min = I_1.shiftLeft(bits-1);
		BigInteger[] testNumbers = TestsetGenerator.generate(N_COUNT, bits, TestNumberNature.MODERATE_SEMIPRIMES);
		LOG.info("Test N with " + bits + " bits, i.e. N >= " + N_min);
		
		for (BigInteger N : testNumbers) {
			this.findSingleFactor(N.longValue());
		}
		
		String kNStr = USE_kN_CONGRUENCES ? "kN" : "k+N";
		for (int k=0; k<KMOD; k++) {
			for (int Nk=0; Nk<KNMOD; Nk++) {
				for (int a=0; a<AMOD; a++) {
					LOG.info("Successful adjusts for k%" + KMOD + "=" + k + ", (" + kNStr + ")%" + KNMOD + "=" + Nk + ", a%" + AMOD + "=" + a + ": " + Arrays.toString(counts[k][Nk][a]));
				}
				LOG.info("");
			}
		}
		LOG.info("");
	}

	public static void main(String[] args) {
    	ConfigUtil.initProject();
		int bits = START_BITS;
		while (true) {
			// test N with the given number of bits, i.e. 2^(bits-1) <= N <= (2^bits)-1
	    	Hart_AnalyzeCongruences testEngine = new Hart_AnalyzeCongruences();
			testEngine.testRange(bits);
			bits += INCR_BITS;
			if (MAX_BITS!=null && bits > MAX_BITS) break;
		}
	}
}