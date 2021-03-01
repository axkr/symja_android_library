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

import java.math.BigInteger;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.util.ConfigUtil;

/**
 * A factoring algorithm racing Hart's one line factorizer against trial division.
 * 
 * This variant is slightly faster than Hart_TDiv_Race for N >= 45 bits, but will fail for some N having small factors.
 * Hart_Fast is faster for hard semiprimes.
 * 
 * @authors Thilo Harich & Tilman Neumann
 */
public class Hart_TDiv_Race2 extends FactorAlgorithm {
	private static final Logger LOG = Logger.getLogger(Hart_TDiv_Race2.class);
	
	/**
	 * We only test k-values that are multiples of this constant.
	 * Best values for performance are 315, 45, 105, 15 and 3, in that order.
	 */
	private static final int K_MULT = 315;
	
	/** Size of arrays, sufficient to factor all numbers <= 52 bit. */
	private static final int I_MAX = 1<<20;
	
	private static final int DISCRIMINATOR_BITS = 10; // experimental result
	private static final double DISCRIMINATOR = 1.0/(1<<DISCRIMINATOR_BITS);
	
	/** This constant is used for fast rounding of double values to long. */
	private static final double ROUND_UP_DOUBLE = 0.9999999665;

	private double[] sqrt;
	private int[] primes;
	private double[] reciprocals;
	
	private final AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get();
	private final Gcd63 gcdEngine = new Gcd63();

	/**
	 * Full constructor.
	 */
	public Hart_TDiv_Race2() {
		sqrt = new double[I_MAX];
		primes = new int[I_MAX];
		reciprocals = new double[I_MAX];
		for (int i=1; i<I_MAX; i++) {
			sqrt[i] = Math.sqrt(i*K_MULT);
			int p = SMALL_PRIMES.getPrime(i);
			primes[i] = p;
			reciprocals[i] = 1.0/p;
		}
	}
	
	@Override
	public String getName() {
		return "Hart_TDiv_Race2";
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}

	/**
	 * Find a factor of long N.
	 * @param N
	 * @return factor of N
	 */
	public long findSingleFactor(long N) {
		int Nbits = 64-Long.numberOfLeadingZeros(N);
		int pMinBits = Nbits - 53 + DISCRIMINATOR_BITS;
		
		// test for exact squares
		final double sqrtN = Math.sqrt(N);
		final long floorSqrtN = (long) sqrtN;
		if (floorSqrtN*floorSqrtN == N) return floorSqrtN;
		
		final long fourN = N<<2;
		final double sqrt4N = sqrtN*2;
		long a, b, test;
		int k = K_MULT;
		try {
			int i=1;
			if (pMinBits>0) {
				// For the smallest primes we do standard trial division.
				// This seems to be not necessary for correctness, but to give a small speedup.
				int pMin = 1<<pMinBits;
				for ( ; primes[i]<pMin; ) {
					// tdiv step
					if (N%primes[i]==0) return primes[i];

					// odd k -> adjust a mod 8, 16, 32
					final long kN = k*N;
					a = (long) (sqrt4N * sqrt[i++] + ROUND_UP_DOUBLE);
					final long kNp1 = kN + 1;
					if ((kNp1 & 3) == 0) {
						a += (kNp1 - a) & 7;
					} else if ((kNp1 & 7) == 6) {
						final long adjust1 = (kNp1 - a) & 31;
						final long adjust2 = (-kNp1 - a) & 31;
						a += adjust1<adjust2 ? adjust1 : adjust2;
					} else { // (kN+1) == 2 (mod 8)
						final long adjust1 = (kNp1 - a) & 15;
						final long adjust2 = (-kNp1 - a) & 15;
						a += adjust1<adjust2 ? adjust1 : adjust2;
					}
					
					test = a*a - (kN << 2);
					b = (long) Math.sqrt(test);
					if (b*b == test) {
						return gcdEngine.gcd(a+b, N);
					}
					k += K_MULT;

					// tdiv step
					if (N%primes[i]==0) return primes[i];

					// even k -> a must be odd
					a = (long) (sqrt4N * sqrt[i++] + ROUND_UP_DOUBLE) | 1L;
					test = a*a - k * fourN;
					b = (long) Math.sqrt(test);
					if (b*b == test) {
						return gcdEngine.gcd(a+b, N);
					}
					k += K_MULT;
				}
			}
			
			// continue with Hart and fast inverse trial division
			for (; ;) {
				// tdiv step
				if ((long) (N*reciprocals[i] + DISCRIMINATOR) * primes[i] == N) return primes[i];

				// odd k -> adjust a mod 8, 16, 32
				final long kN = k*N;
				a = (long) (sqrt4N * sqrt[i++] + ROUND_UP_DOUBLE);
				final long kNp1 = kN + 1;
				if ((kNp1 & 3) == 0) {
					a += (kNp1 - a) & 7;
				} else if ((kNp1 & 7) == 6) {
					final long adjust1 = (kNp1 - a) & 31;
					final long adjust2 = (-kNp1 - a) & 31;
					a += adjust1<adjust2 ? adjust1 : adjust2;
				} else { // (kN+1) == 2 (mod 8)
					final long adjust1 = (kNp1 - a) & 15;
					final long adjust2 = (-kNp1 - a) & 15;
					a += adjust1<adjust2 ? adjust1 : adjust2;
				}
				
				test = a*a - (kN << 2);
				b = (long) Math.sqrt(test);
				if (b*b == test) {
					return gcdEngine.gcd(a+b, N);
				}
				k += K_MULT;

				// tdiv step
				if ((long) (N*reciprocals[i] + DISCRIMINATOR) * primes[i] == N) return primes[i];

				// even k -> a must be odd
				a = (long) (sqrt4N * sqrt[i++] + ROUND_UP_DOUBLE) | 1L;
				test = a*a - k * fourN;
				b = (long) Math.sqrt(test);
				if (b*b == test) {
					return gcdEngine.gcd(a+b, N);
				}
				k += K_MULT;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			//LOG.error("Hart_TDiv_Race_Unsafe: Failed to factor N=" + N + " because the arrays are too small.");
			return 1;
		}
	}
	
	/**
	 * Test.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		ConfigUtil.initProject();

		// These test number were too hard for previous versions:
		long[] testNumbers = new long[] {
				5640012124823L,
				7336014366011L,
				19699548984827L,
				52199161732031L,
				73891306919159L,
				112454098638991L,
				
				32427229648727L,
				87008511088033L,
				92295512906873L,
				338719143795073L,
				346425669865991L,
				1058244082458461L,
				1773019201473077L,
				6150742154616377L,

				44843649362329L,
				67954151927287L,
				134170056884573L,
				198589283218993L,
				737091621253457L,
				1112268234497993L,
				2986396307326613L,
				
				26275638086419L,
				62246008190941L,
				209195243701823L,
				290236682491211L,
				485069046631849L,
				1239671094365611L,
				2815471543494793L,
				5682546780292609L,
				
				// test numbers that required large arrays
				135902052523483L,
				1454149122259871L,
				5963992216323061L,
				26071073737844227L,
				8296707175249091L,
				35688516583284121L,
				//35245060305489557L, // too big for I_MAX
				//107563481071570333L, // too big for I_MAX
				//107326406641253893L, // too big for I_MAX
				//120459770277978457L, // too big for I_MAX
				
				// failures with random odd composites
				949443, // = 3 * 11 * 28771
				996433, // = 31 * 32143
				1340465, // = 5 * 7 * 38299
				1979435, // = 5 * 395887
				2514615, // = 3 * 5 * 167641
				5226867, // =  3^2 * 580763
				10518047, // = 61 * 172427
				30783267, // = 3^3 * 1140121
				62230739, // = 67 * 928817
				84836647, // = 7 * 17 * 712913
				94602505,
				258555555,
				436396385,
				612066705,
				2017001503,
				3084734169L,
				6700794123L,
				16032993843L, // fine here
				26036808587L,
				41703657595L, // fine here
				68889614021L,
				197397887859L, // fine here
				
				2157195374713L,
				8370014680591L,
				22568765132167L,
				63088136564083L,
				
				// more test numbers with small factors
				// 30 bit
				712869263, // = 89 * 8009767
				386575807, // = 73 * 5295559
				569172749, // = 83 * 6857503
				// 40 bit
				624800360363L, // = 233 * 2681546611
				883246601513L, // = 251 * 3518910763
				
				// problems found by Thilo
				35184372094495L,
				893, // FAILS with doTDivFirst==false
				35, // FAILS with doTDivFirst==false
				9,
				
				// squares
				100140049,
				10000600009L,
				1000006000009L,
				6250045000081L,
				10890006600001L,
				14062507500001L,
				25000110000121L,
				100000380000361L,
				// the following N require an explicit square test
				10000001400000049L,
				1000000014000000049L,
			};
		
		Hart_TDiv_Race2 holf = new Hart_TDiv_Race2();
		for (long N : testNumbers) {
			long factor = holf.findSingleFactor(N);
			LOG.info("N=" + N + " has factor " + factor);
		}
	}
}
