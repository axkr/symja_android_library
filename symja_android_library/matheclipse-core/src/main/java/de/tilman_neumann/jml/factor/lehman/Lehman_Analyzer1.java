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

import static de.tilman_neumann.jml.base.BigIntConstants.I_1;

import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.util.SortedMultiset_BottomUp;
import de.tilman_neumann.jml.factor.FactorAlgorithmBase;
import de.tilman_neumann.jml.factor.TestsetGenerator;

/**
 * Analyze the moduli of a-values that help the Lehman algorithm to find factors.
 * 
 * @author Tilman Neumann
 */
public class Lehman_Analyzer1 extends FactorAlgorithmBase {
	private static final Logger LOG = Logger.getLogger(Lehman_Analyzer1.class);

	// algorithm options
	/** number of test numbers */
	private static final int N_COUNT = 100000;
	/** the bit size of N to start with */
	private static final int START_BITS = 30;
	/** the increment in bit size from test set to test set */
	private static final int INCR_BITS = 1;
	/** maximum number of bits to test (no maximum if null) */
	private static final Integer MAX_BITS = 63;
	
	private final Gcd63 gcdEngine = new Gcd63();
	
	private SortedMultiset_BottomUp<Integer>[][] aValues;
	
	private static final int N_MOD = 6;
	private static final int k_MOD = 6;
	private static final int a_MOD = 4;

	@SuppressWarnings("unchecked")
	public Lehman_Analyzer1() {
		aValues = new SortedMultiset_BottomUp[N_MOD][k_MOD];
		for (int NMod=0; NMod<N_MOD; NMod++) {
			aValues[NMod] = new SortedMultiset_BottomUp[k_MOD];
			for (int kMod=0; kMod<k_MOD; kMod++) {
				aValues[NMod][kMod] = new SortedMultiset_BottomUp<Integer>();
			}
		}
	}
	
	@Override
	public String getName() {
		return "Lehman_Analyzer1";
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}
	
	public long findSingleFactor(long N) {
		int cbrt = (int) Math.ceil(Math.cbrt(N));
		double sixthRoot = Math.pow(N, 1/6.0); // double precision is required for stability
		for (int k=1; k <= cbrt; k++) {
			long fourKN = k*N<<2;
			double fourSqrtK = Math.sqrt(k<<4);
			long sqrt4kN = (long) Math.ceil(Math.sqrt(fourKN));
			long limit = (long) (sqrt4kN + sixthRoot / fourSqrtK);
			for (long a = sqrt4kN; a <= limit; a++) {
				long test = a*a - fourKN;
				long b = (long) Math.sqrt(test);
				if (b*b == test) {
					long gcd = gcdEngine.gcd(a+b, N);
					if (gcd>1 && gcd<N) {
						aValues[(int)(N%N_MOD)][k%k_MOD].add((int) (a%a_MOD));
						return gcd;
					}
				}
			}
	    }
		
		return 0; // Fail
	}
	
	private void testRange(int bits) {
		BigInteger N_min = I_1.shiftLeft(bits-1);
		// find N-set for square tests
		ArrayList<BigInteger> NSet = TestsetGenerator.generate(bits, N_COUNT);
		LOG.info("Test N with " + bits + " bits, i.e. N >= " + N_min);
		
		for (BigInteger N : NSet) {
			this.findSingleFactor(N);
		}
		
		int lehmanCount = 0;
		for (int NMod=0; NMod<N_MOD; NMod++) {
			boolean logged = false;
			for (int kMod=0; kMod<k_MOD; kMod++) {
				SortedMultiset_BottomUp<Integer> aCounts = aValues[NMod][kMod];
				if (aCounts.size() > 0) {
					int totalCount = aCounts.totalCount();
					LOG.info("Successful a-values %" + a_MOD + " for N%" + N_MOD + "==" + NMod + ", k%" + k_MOD + "==" + kMod + " : " + totalCount + " (" + aCounts + ")");
					lehmanCount += totalCount;
					logged = true;
				}
			}
			if (logged) LOG.info("");
		}
		int tdivCount = N_COUNT - lehmanCount;
		LOG.info("Factored by trial division: " + tdivCount);
		LOG.info("");
	}

//	public static void main(String[] args) {
//    	ConfigUtil.initProject();
//		int bits = START_BITS;
//		while (true) {
//			// test N with the given number of bits, i.e. 2^(bits-1) <= N <= (2^bits)-1
//	    	Lehman_Analyzer1 testEngine = new Lehman_Analyzer1();
//			testEngine.testRange(bits);
//			bits += INCR_BITS;
//			if (MAX_BITS!=null && bits > MAX_BITS) break;
//		}
//	}
}
