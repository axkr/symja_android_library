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
package de.tilman_neumann.jml.factor.hart;

import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;

/**
 * A factoring algorithm racing Hart's one line factorizer against trial division.
 * 
 * This variant is slightly faster than HartTDivRace for N >= 45 bits, but will fail for some N having small factors.
 * HartFast is faster for hard semiprimes.
 * 
 * @authors Thilo Harich & Tilman Neumann
 */
public class HartTDivRace2 extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(HartTDivRace2.class);
	
	private static final boolean DEBUG = false;

	/**
	 * We only test k-values that are multiples of this constant.
	 * Best values for performance are 315, 45, 105, 15 and 3, in that order.
	 */
	private static final int K_MULT = 315;
	
	/** Size of arrays, sufficient to factor all numbers <= 52 bit. */
	private static final int I_MAX = 1<<21;
	
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
	public HartTDivRace2() {
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
		return "HartTDivRace2";
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
		long a, b, test, gcd;
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
						if ((gcd = gcdEngine.gcd(a+b, N))>1 && gcd<N) return gcd;
					}
					k += K_MULT;

					// tdiv step
					if (N%primes[i]==0) return primes[i];

					// even k -> a must be odd
					a = (long) (sqrt4N * sqrt[i++] + ROUND_UP_DOUBLE) | 1L;
					test = a*a - k * fourN;
					b = (long) Math.sqrt(test);
					if (b*b == test) {
						if ((gcd = gcdEngine.gcd(a+b, N))>1 && gcd<N) return gcd;
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
					if ((gcd = gcdEngine.gcd(a+b, N))>1 && gcd<N) return gcd;
				}
				k += K_MULT;

				// tdiv step
				if ((long) (N*reciprocals[i] + DISCRIMINATOR) * primes[i] == N) return primes[i];

				// even k -> a must be odd
				a = (long) (sqrt4N * sqrt[i++] + ROUND_UP_DOUBLE) | 1L;
				test = a*a - k * fourN;
				b = (long) Math.sqrt(test);
				if (b*b == test) {
					if ((gcd = gcdEngine.gcd(a+b, N))>1 && gcd<N) return gcd;
				}
				k += K_MULT;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			if (DEBUG) LOG.error(getName() + ": Failed to factor N=" + N + " because the arrays are too small.");
			return 1;
		}
	}
}
