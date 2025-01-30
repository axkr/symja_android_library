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
package de.tilman_neumann.jml.factor.squfof;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.sequence.SquarefreeSequence63;

/**
 * Shanks' SQUFOF algorithm, 31-bit version.
 * This implementation works without fail for N <= 2^52 and is faster than SquFoF63 for N <= 2^51.
 * 
 * @author Tilman Neumann
 */
public class SquFoF31 extends FactorAlgorithm {

	private static final int[] BASE_MULTIPLIERS = new int[] {1680, 48, 8, 1};

	// input
	private long N, kN;
	private int floor_sqrt_kN;
	
	// maximum number of iterations
	private int maxI;
	
	// gcd engine
	private Gcd63 gcdEngine = new Gcd63();
	
	@Override
	public String getName() {
		return "SquFoF31";
	}
	
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}

	/**
	 * Find a factor of the given composite N.</br></br>
	 * <em>Warning:</em> This method will not return when called with a prime argument.
	 * @param N composite integer
	 * @return factor of N
	 */
	public long findSingleFactor(long N) {
		this.N = N;
		
		// stopMult was derived experimentally
		int bits = bitLength(N);
		float stopMult = (bits<=45) ? 0.8F : 0.8F + (bits-45)*0.18F;
		// max iterations
		this.maxI = (int) (stopMult * Math.pow(N, 0.2)); // stopMult * (5.th root of N)
		
		// test all factors of the base multipliers:
		// This is required for N that consist of them only.
		int[] baseMultiplierFactors = new int[] {2, 3, 5, 7}; // 1680 = 2^4 * 3 * 5 * 7
		for (int baseMultiplierFactor : baseMultiplierFactors) {
			if (N%baseMultiplierFactor == 0) return baseMultiplierFactor;
		}

		// Several base multipliers allow to expand the fail-free working range until N=2^52.
		// None of the base multipliers is a square of another one.
		for (int baseMultiplierIndex=0; baseMultiplierIndex<BASE_MULTIPLIERS.length; baseMultiplierIndex++) {
			int baseMultiplier = BASE_MULTIPLIERS[baseMultiplierIndex];
			// sequence: for each new N start again with the first k
			SquarefreeSequence63 kSequence = new SquarefreeSequence63(baseMultiplier);
			kSequence.reset();
			while (true) {
				long k = kSequence.next().longValue();
				this.kN = k*N;
				if (bitLength(kN) > 60) break; // use next smaller base multiplier; the inner loops need 3 bit tolerance
				
				// The cast may be wrong for some bigger kN, but fixing the cast would mean a small performance
				// penalty, so we ignore it. Return immediately if kN is square.
				floor_sqrt_kN = (int) Math.sqrt(kN);
				int diff = (int) (kN - ((long)floor_sqrt_kN) * floor_sqrt_kN);
				if (diff==0) return gcdEngine.gcd(N, floor_sqrt_kN);
				
				// search square Q_i
				Long factor = test(diff);
				if (factor!=null) return factor;
			}
		}
		return 0; // not found
	}

	private static int bitLength(long arg) {
		return 64 - Long.numberOfLeadingZeros(arg);
	}

	protected Long test(int Q_ip1) {
		// initialization for first iteration step
		int i = 0;
		int P_im1 = 1;
		int P_i = floor_sqrt_kN;
		int Q_i = 1;
		
		// first iteration step
		while (true) {
			// [McMath 2004] points out that we have to look for a square Q_i at some even i.
			// Here I test Q_i+1, so I have to look for square Q_i+1 at odd i!
			if ((i&1)==1) {
				int Q_ip1_sqrt = (int) Math.sqrt(Q_ip1);
				if (Q_ip1_sqrt*Q_ip1_sqrt==Q_ip1) {
					// Q_i+1 is square -> do reverse iteration
					Long factor = reverseIteration(P_i, Q_ip1_sqrt);
					if (factor!=null) return factor; // if factor is null we try another k
				}
			}
		
			// exit ?
			if (++i==maxI) return null;
			// keep values from last round
			P_im1 = P_i;
			int Q_im1 = Q_i;
			Q_i = Q_ip1;
			// compute next values
			int b_i = (floor_sqrt_kN + P_im1)/Q_i; // floor(rational result)
			P_i = b_i*Q_i - P_im1;
			Q_ip1 = Q_im1 + b_i*(P_im1-P_i);
		}
	}
	
	private Long reverseIteration(int found_P, int found_Q_sqrt) {
		// initialization for second iteration step
		int i = 0;
		int b_i = (floor_sqrt_kN-found_P)/found_Q_sqrt; // floor(rational result)
		int P_i = b_i*found_Q_sqrt + found_P;
		int Q_i = found_Q_sqrt;
		int Q_ip1 = (int) ((kN - ((long)P_i) * P_i) / found_Q_sqrt);
		
		// second iteration step
		int P_im1, Q_im1;
		do {
			// exit ?
			if (++i==maxI) return null;
			// keep values from last round
			P_im1 = P_i;
			Q_im1 = Q_i;
			Q_i = Q_ip1;
			// compute next values
			b_i = (floor_sqrt_kN+P_im1)/Q_i; // floor(rational result)
			P_i = b_i*Q_i - P_im1;
			Q_ip1 = Q_im1 + b_i*(P_im1-P_i);
		} while (P_i != P_im1);
		
		// result
		long gcd = gcdEngine.gcd(N, P_i);
		return (gcd>1 && gcd<N) ? gcd : null;
	}
}
