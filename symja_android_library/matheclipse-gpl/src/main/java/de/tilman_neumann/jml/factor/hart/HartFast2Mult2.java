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
import java.util.HashSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.tdiv.TDiv63Inverse;
import de.tilman_neumann.jml.gcd.Gcd63;

/**
 * A variant of class HartFast2Mult that is about 10% faster in the long end.
 * 
 * @authors Thilo Harich & Tilman Neumann
 */
public class HartFast2Mult2 extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(HartFast2Mult2.class);
	
	private static final boolean DEBUG = false;

	// k multipliers. These are applied alternately; thus we kind of investigate two k-sets of different size "in parallel".
	// These two multipliers turned out to be the fastest in case of two sets. Three or more sets seemed to give a slowdown.
	private static final long K_MULT1 = 3465;
	private static final long K_MULT2 = 315;

	/** 
	 * Size of arrays: this is around 4*n^1/3.
	 * 2^21 should work for all number n up to 2^52.
	 */
	private static final int I_MAX = 1<<21;

	/** This constant is used for fast rounding of double values to long. */
	private static final double ROUND_UP_DOUBLE = 0.9999999665;

	private final boolean doTDivFirst;
	
	private final long[] kArr;
	private final double[] sqrtKArr;
	
	private final TDiv63Inverse tdiv = new TDiv63Inverse(I_MAX);
	private final Gcd63 gcdEngine = new Gcd63();

	/**
	 * Full constructor.
	 * @param doTDivFirst If true then trial division is done before the Hart loop.
	 * This is recommended if arguments N are known to have factors < cbrt(N) frequently.
	 * With doTDivFirst=false, this implementation is pretty fast for hard semiprimes.
	 * But the smaller possible factors get, it will become slower and slower.
	 */
	public HartFast2Mult2(boolean doTDivFirst) {
		this.doTDivFirst = doTDivFirst;
		// Precompute all required sqrt(k) for i < I_MAX
		HashSet<Long> kSet = new HashSet<>();
		kArr = new long[2*I_MAX];
		sqrtKArr = new double[2*I_MAX];
		int kCount = 0;
		for (int i=1; i<I_MAX; i++) {
			// Skipping k with too high powers of 2 improves performance.
			// 8 is faster than 16 for smaller N, but we want to optimize the long end.
			if (i%16 == 0) i++;
			
			long k1 = i*K_MULT1;
			double sqrt1 = Math.sqrt(k1);
			if (!kSet.contains(k1)) {
				kArr[kCount] = k1;
				sqrtKArr[kCount] = sqrt1;
				kSet.add(k1);
				kCount++;
			}
			
			long k2 = i*K_MULT2;
			double sqrt2 = Math.sqrt(k2);
			if (!kSet.contains(k2)) {
				kArr[kCount] = k2;
				sqrtKArr[kCount] = sqrt2;
				kSet.add(k2);
				kCount++;
			}
		}
	}

	@Override
	public String getName() {
		String tdivStr = doTDivFirst ? "with tdiv" : "without tdiv";
		return "HartFast2Mult2(" + tdivStr + ")";
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
		if (doTDivFirst) {
			// do trial division before the Hart loop
			tdiv.setTestLimit((int) Math.cbrt(N));
			final long factor = tdiv.findSingleFactor(N);
			if (factor > 1) return factor;
		}
		
		// test for exact squares
		final double sqrtN = Math.sqrt(N);
		final long floorSqrtN = (long) sqrtN;
		if (floorSqrtN*floorSqrtN == N) return floorSqrtN;

		final long fourN = N<<2;
		final double sqrt4N = sqrtN*2;
		long a, b, test, gcd;
		try {
			for (int i=0; ; i++) {
				long k = kArr[i];
				a = adjustA(N, (long) (sqrt4N * sqrtKArr[i] + ROUND_UP_DOUBLE), k);
				test = a*a - k * fourN;
				b = (long) Math.sqrt(test);
				if (b*b == test && (gcd = gcdEngine.gcd(a+b, N))>1 && gcd<N) {
					return gcd;
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			if (DEBUG) LOG.error(getName() + ": Failed to factor N=" + N + ". Either it has factors < cbrt(N) needing trial division, or the arrays are too small.");
			return 1;
		}
	}
	
	/**
	 * Increases x to return the next possible solution for x for x^2 - 4kn = b^2.
	 * Due to performance reasons we give back solutions for this equations modulo a
	 * power of 2, since we can determine the solutions just by additions and binary
	 * operations.
	 *
	 * if k is even x must be odd.
	 * if k*n == 3 mod 4 -> x = k*n+1 mod 8
	 * if k*n == 1 mod 8 -> x = k*n+1 mod 16 or -k*n+1 mod 16
	 * if k*n == 5 mod 8 -> x = k*n+1 mod 32 or -k*n+1 mod 32
	 *
	 * @param N
	 * @param x
	 * @param k
	 * @return
	 */
	private long adjustA(long N, long x, long k) {
		if ((k&1)==0) return x | 1;
		
		final long kNp1 = k*N+1;
		if ((kNp1 & 3) == 0) return x + ((kNp1 - x) & 7);
		
		if ((kNp1 & 7) == 2) {
			final long adjust1 = ( kNp1 - x) & 15;
			final long adjust2 = (-kNp1 - x) & 15;
			return x + (adjust1 < adjust2 ? adjust1 : adjust2);
		}
		
		final long adjust1 = ( kNp1 - x) & 31;
		final long adjust2 = (-kNp1 - x) & 31;
		return x + (adjust1 < adjust2 ? adjust1 : adjust2);
	}
}
