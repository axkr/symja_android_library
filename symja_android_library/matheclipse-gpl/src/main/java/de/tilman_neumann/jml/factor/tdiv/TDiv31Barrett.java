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
package de.tilman_neumann.jml.factor.tdiv;

import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.util.SortedMultiset;

import static de.tilman_neumann.jml.base.BigIntConstants.I_2;

/**
 * Trial division using long-valued Barrett reduction,
 * see https://en.wikipedia.org/wiki/Barrett_reduction. 
 * 
 * Significantly faster than TDiv31Inverse.
 * 
 * @author Tilman Neumann + Thilo Harich
 */
public class TDiv31Barrett extends FactorAlgorithm {
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(TDiv31Barrett.class);

	// "static" would be slightly slower ?
	private AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get().ensurePrimeCount(NUM_PRIMES_FOR_31_BIT_TDIV);
	
	private int[] primes;
	private long[] pinv;
	
	public TDiv31Barrett() {
		primes = new int[NUM_PRIMES_FOR_31_BIT_TDIV];
		pinv = new long[NUM_PRIMES_FOR_31_BIT_TDIV];
		for (int i=0; i<NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
			int p = SMALL_PRIMES.getPrime(i);
			primes[i] = p;
			pinv[i] = (1L<<32)/p;
		}
	}
	
	@Override
	public String getName() {
		return "TDiv31Barrett";
	}
	
	@Override
	public void factor(BigInteger Nbig, SortedMultiset<BigInteger> primeFactors) {
		factor(Nbig, 1, primeFactors);
	}

	/**
	 * Find all factor of NBig, which must have less than 32 bit.
	 * @param Nbig
	 * @param Nexp the exponent which with found factors are added to primeFactors
	 * @param primeFactors
	 */
	public void factor(BigInteger Nbig, int Nexp, SortedMultiset<BigInteger> primeFactors) {
		int N = Nbig.intValue();
		
		// Powers of 2 can be removed very fast.
		// This is required also because the Barrett division does not work with p=2.
		int lsb = Integer.numberOfTrailingZeros(N);
		if (lsb > 0) {
			primeFactors.add(I_2, lsb*Nexp);
			N >>= lsb;
		}
		
		// Test odd primes
		int q;
		for (int i=1; ; i++) {
			final int p = primes[i];
			while ((q = (1 + (int) ((N*pinv[i])>>32))) * p == N) {
				primeFactors.add(BigInteger.valueOf(p), Nexp);
				N = q;
			}
			if (p*(long)p > N) {
				break;
			}
		}
		
		if (N>1) {
			// either N is prime, or we could not find all factors -> add the rest to the result
			primeFactors.add(BigInteger.valueOf(N), Nexp);
		}
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		if (N.bitLength() > 31) throw new IllegalArgumentException("TDiv31Barrett.findSingleFactor() does not work for N>31 bit, but N=" + N);
		return BigInteger.valueOf(findSingleFactor(N.intValue()));
	}
	
	public int findSingleFactor(int N) {
		if (N<0) N = -N; // sign does not matter
		if (N<4) return 1; // prime
		if ((N&1)==0) return 2; // N even

		// if N is odd and composite then the loop runs maximally up to prime = floor(sqrt(N))
		// unroll the loop
		int i=1;
		int unrolledLimit = NUM_PRIMES_FOR_31_BIT_TDIV-8;
		for ( ; i<unrolledLimit; i++) {
			if ((1 + (int) ((N*pinv[i])>>32)) * primes[i] == N) return primes[i];
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i];
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i];
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i];
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i];
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i];
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i];
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i];
		}
		for ( ; i<NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
			if ((1 + (int) ((N*pinv[i])>>32)) * primes[i] == N) return primes[i];
		}
		// otherwise N is prime
		return 1;
	}
}
