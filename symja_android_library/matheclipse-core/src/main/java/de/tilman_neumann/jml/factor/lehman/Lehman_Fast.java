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

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithmBase;
import de.tilman_neumann.jml.gcd.Gcd63;
//import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.jml.factor.tdiv.TDiv63Inverse;

/**
 * Fast implementation of Lehman's factor algorithm.
 * Works flawlessly for N up to 60 bit.
 *
 * @authors Tilman Neumann + Thilo Harich
 */
public class Lehman_Fast extends FactorAlgorithmBase {
//	private static final Logger LOG = Logger.getLogger(Lehman_Fast.class);

	/** This is a constant that is below 1 for rounding up double values to long. */
	private static final double ROUND_UP_DOUBLE = 0.9999999665;

	private static final TDiv63Inverse tdiv = new TDiv63Inverse(1<<21);

	private static double[] sqrt, sqrtInv;

	static {
		// Precompute sqrts for all possible k. 2^21 entries are enough for N~2^63.
		final int kMax = 1<<21;
		sqrt = new double[kMax + 1];
		sqrtInv = new double[kMax + 1];
		for (int i = 1; i < sqrt.length; i++) {
			final double sqrtI = Math.sqrt(i);
			sqrt[i] = sqrtI;
			sqrtInv[i] = 1.0/sqrtI;
		}
	}

	private long N;
	private long fourN;
	private double sqrt4N;
	private boolean doLehmanBeforeTDiv;
	private final Gcd63 gcdEngine = new Gcd63();

	/**
	 * Full constructor.
	 * @param doLehmanBeforeTDiv If true than the Lehman loop is executed before trial division.
	 * This is recommended for "hard" numbers (semiprimes having factors of similar size).
	 */
	public Lehman_Fast(boolean doLehmanBeforeTDiv) {
		this.doLehmanBeforeTDiv = doLehmanBeforeTDiv;
	}

	@Override
	public String getName() {
		return "Lehman_Fast(" + doLehmanBeforeTDiv + ")";
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}

	public long findSingleFactor(long N) {
		this.N = N;
		final int cbrt = (int) Math.cbrt(N);

		long factor;
		tdiv.setTestLimit(cbrt);
		if (!doLehmanBeforeTDiv && (factor = tdiv.findSingleFactor(N))>1) return factor;

		fourN = N<<2;
		sqrt4N = Math.sqrt(fourN);

		// kLimit must be 0 mod 6, since we also want to search above of it
		final int kLimit = ((cbrt + 6) / 6) * 6;
		// For kTwoA = kLimit / 64 the range for a is at most 2. We make it 0 mod 6, too.
		final int kTwoA = (((cbrt >> 6) + 6) / 6) * 6;

		// Investigate solutions of a^2 - sqrt(k*n) = y^2, where we only have two possible 'a' values per k.
		// But do not go to far, since there we have a lower chance to find a factor.
		// Here we only inspect k == 0 (mod 6) which are most likely to have a solution x^2 - sqrt(k*n) = y^2.
		if ((factor = lehmanEven(kTwoA, kLimit))>1) return factor;

		// Investigate solutions of a^2 - sqrt(k*n) = y^2 where we might have more then two solutions 'a'.
		final double sixthRootTerm = 0.25 * Math.pow(N, 1/6.0); // double precision is required for stability
		for (int k=1; k < kTwoA; k++) {
			final double sqrt4kN = sqrt4N * sqrt[k];
			// only use long values
			final long aStart = (long) (sqrt4kN + ROUND_UP_DOUBLE); // much faster than ceil() !
			long aLimit = (long) (sqrt4kN + sixthRootTerm * sqrtInv[k]);
			long aStep;
			if ((k & 1) == 0) {
				// k even -> make sure aLimit is odd
				aLimit |= 1L;
				aStep = 2;
			} else {
				final long kPlusN = k + N;
				if ((kPlusN & 3) == 0) {
					aStep = 8;
					aLimit += ((kPlusN - aLimit) & 7);
				} else {
					aStep = 4;
					aLimit += ((kPlusN - aLimit) & 3);
				}
			}

			// processing the a-loop top-down is faster than bottom-up
			final long fourkN = k * fourN;
			for (long a=aLimit; a >= aStart; a-=aStep) {
				final long test = a*a - fourkN;
				final long b = (long) Math.sqrt(test);
				if (b*b == test) {
					return gcdEngine.gcd(a+b, N);
				}
			}
		}

		// additional loop for k = 3 mod 6 in the middle range
		// this loop has fewer possible a's than k = 0 mod 6 and therefore gives less often factors
		if ((factor = lehmanOdd(kTwoA + 3, kLimit)) > 1) return factor;

		// Continue k == 0 (mod 6) loop for larger k. Since we are looking at very high numbers this now done after the k = 3 mod 6 loop
		if ((factor = lehmanEven(kLimit, kLimit << 1)) > 1) return factor;

		// So far we have done loops for offset 0,3 -> Now do the missing 1,2,4,5.
		// This code will be executed very rarely, but to be sure we did not miss factors
		// from the lehman argument we have to execute it.
		if ((factor = lehmanOdd(kTwoA + 1, kLimit)) > 1) return factor;
		if ((factor = lehmanEven(kTwoA + 2, kLimit)) > 1) return factor;
		if ((factor = lehmanEven(kTwoA + 4, kLimit)) > 1) return factor;
		if ((factor = lehmanOdd(kTwoA + 5, kLimit)) > 1) return factor;

		// Check via trial division whether N has a nontrivial divisor d <= cbrt(N).
		if (doLehmanBeforeTDiv && (factor = tdiv.findSingleFactor(N))>1) return factor;
		
		// If sqrt(4kN) is very near to an exact integer then the fast ceil() operations may have failed.
		// It seems that it is enough to fix either the k%6=1 or the k%6=5 loop.
		for (int k=kTwoA + 1; k <= kLimit; k++) {
			long a = (long) (sqrt4N * sqrt[k] + ROUND_UP_DOUBLE) - 1;
			long test = a*a - k*fourN;
			long b = (long) Math.sqrt(test);
			if (b*b == test) {
				return gcdEngine.gcd(a+b, N);
			}
	    }
		
		return 0; // fail
	}

	private long lehmanOdd(int kBegin, final int kLimit) {
		for (int k = kBegin; k <= kLimit; k += 6) {
			long a = (long) (sqrt4N * sqrt[k] + ROUND_UP_DOUBLE);
			// make a == (k+N) (mod 4)
			final long kPlusN = k + N;
			if ((kPlusN & 3) == 0) {
				a += ((kPlusN - a) & 7);
			} else {
				a += ((kPlusN - a) & 3);
			}
			final long test = a*a - k * fourN;
			final long b = (long) Math.sqrt(test);
			if (b*b == test) {
				return gcdEngine.gcd(a+b, N);
			}
		}
		return -1;
	}

	private long lehmanEven(int kBegin, final int kEnd) {
		for (int k = kBegin; k <= kEnd; k += 6) {
			// k even -> a must be odd
			final long a = (long) (sqrt4N * sqrt[k] + ROUND_UP_DOUBLE) | 1L;
			final long test = a*a - k * fourN;
			final long b = (long) Math.sqrt(test);
			if (b*b == test) {
				return gcdEngine.gcd(a+b, N);
			}
		}
		return -1;
	}
	
	/**
	 * Test.
	 * @param args ignored
	 */
//	public static void main(String[] args) {
//		ConfigUtil.initProject();
//
//		// These test number were too hard for previous versions:
//		long[] testNumbers = new long[] {
//				5640012124823L,
//				7336014366011L,
//				19699548984827L,
//				52199161732031L,
//				73891306919159L,
//				112454098638991L,
//				
//				32427229648727L,
//				87008511088033L,
//				92295512906873L,
//				338719143795073L,
//				346425669865991L,
//				1058244082458461L,
//				1773019201473077L,
//				6150742154616377L,
//
//				44843649362329L,
//				67954151927287L,
//				134170056884573L,
//				198589283218993L,
//				737091621253457L,
//				1112268234497993L,
//				2986396307326613L,
//				
//				26275638086419L,
//				62246008190941L,
//				209195243701823L,
//				290236682491211L,
//				485069046631849L,
//				1239671094365611L,
//				2815471543494793L,
//				5682546780292609L,
//			};
//		
//		Lehman_Fast lehman = new Lehman_Fast(true);
//		for (long N : testNumbers) {
//			long factor = lehman.findSingleFactor(N);
//			LOG.info("N=" + N + " has factor " + factor);
//		}
//	}
}
