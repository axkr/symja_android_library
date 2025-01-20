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
import de.tilman_neumann.jml.factor.tdiv.TDiv63Inverse;
import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.sequence.SquarefreeSequence63;

/**
 * A variant of Hart's one line factorizer using k = 315 * s, where s is squarefree (1,2,3,5,6,7,10,11,13,...).
 * For semiprimes having smaller factor >= cbrt(N) this seems to be the fastest algorithm for N from 23 to 35 bits.
 * 
 * @authors Tilman Neumann
 */
public class HartSquarefree extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(HartSquarefree.class);
	
	private static final boolean DEBUG = false;
	
	/**
	 * We only test k-values that are multiples of this constant.
	 */
	private static final int K_MULT = 315; // 3*3*5*7
	
	/** Size of arrays */
	private static final int I_MAX = 1<<21;
	
	/** This constant is used for fast rounding of double values to long. */
	private static final double ROUND_UP_DOUBLE = 0.9999999665;

	private final boolean doTDivFirst;
	private final double[] sqrt;
	private final long[] kArray;
	private final TDiv63Inverse tdiv = new TDiv63Inverse(I_MAX);
	private final Gcd63 gcdEngine = new Gcd63();

	/**
	 * Full constructor.
	 * @param doTDivFirst If true then trial division is done before the Lehman loop.
	 * This is recommended if arguments N are known to have factors < cbrt(N) frequently.
	 */
	public HartSquarefree(boolean doTDivFirst) {
		this.doTDivFirst = doTDivFirst;
		// Precompute sqrt(i*K_MULT) for all i < I_MAX
		sqrt = new double[I_MAX];
		kArray = new long[I_MAX];
		SquarefreeSequence63 sfs = new SquarefreeSequence63(1);
		sfs.reset();
		for (int i=0; i<I_MAX; i++) {
			long s = sfs.next();
			kArray[i] = s*K_MULT;
			sqrt[i] = Math.sqrt(s*K_MULT);
		}
	}
	
	@Override
	public String getName() {
		return "HartSquarefree(" + doTDivFirst + ")";
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
		} // else: if there are factors < cbrt(N) then some of them may not be found
		
		// test for exact squares
		final double sqrtN = Math.sqrt(N);
		final long floorSqrtN = (long) sqrtN;
		if (floorSqrtN*floorSqrtN == N) return floorSqrtN;
		
		final long fourN = N<<2;
		final double sqrt4N = sqrtN*2;
		long a, b, test, gcd;
		try {
			for (int i=0; ; i++) {
				long k = kArray[i];
				a = (long) (sqrt4N * sqrt[i] + ROUND_UP_DOUBLE);
				if ((k&1)==0) {
					a |= 1;
				} else {
					a = adjustAForOddK(a, k*N+1);
				}
				test = a*a - k * fourN;
				b = (long) Math.sqrt(test);
				if (b*b == test) {
					if ((gcd = gcdEngine.gcd(a+b, N))>1 && gcd<N) return gcd;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			if (DEBUG) LOG.error(getName() + ": Failed to factor N=" + N + ". Either it has factors < cbrt(N) needing trial division, or the arrays are too small.");
			return 1;
		}
	}
	
	private long adjustAForOddK(long a, long kNp1) {
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
		return a;
	}
}
