/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.util.SortedMultiset;

/**
 * Trial division factor algorithm using double-valued Barrett reduction, thus replacing division by multiplications.
 * 
 * Instead of dividing N by consecutive primes, we store the reciprocals of those primes, too,
 * and multiply N by those reciprocals. Only if such a result is near to an integer we need
 * to do a division.
 * 
 * This variant abstains from testing N%primes[i] when the discriminator test indicates a neat division,
 * and unrolls the loop in findSingleFactor().
 * 
 * @author Thilo Harich + Tilman Neumann
 */
public class TDiv31Inverse extends FactorAlgorithm {
	
	private AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get();	// "static" would be slightly slower

	// The allowed discriminator bit size is d <= 53 - bitLength(N/p), thus d<=23 would be safe
	// for any integer N and p>=2. d=10 is the value that performs best, determined by experiment.
	private static final double DISCRIMINATOR = 1.0/(1<<10);

	private int[] primes;
	private double[] reciprocals;
	
	public TDiv31Inverse() {
		primes = new int[NUM_PRIMES_FOR_31_BIT_TDIV];
		reciprocals = new double[NUM_PRIMES_FOR_31_BIT_TDIV];
		for (int i=0; i<NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
			int p = SMALL_PRIMES.getPrime(i);
			primes[i] = p;
			reciprocals[i] = 1.0/p;
		}
	}
	
	@Override
	public String getName() {
		return "TDiv31Inverse";
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
		
		int q;
		for (int i=0; ; i++) {
			final int p = primes[i];
			while ((q = (int) (N*reciprocals[i] + DISCRIMINATOR)) * p == N) {
				primeFactors.add(BigInteger.valueOf(p), Nexp);
				N = q; // avoiding a division here by storing q benefits the int version but not the long version
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
		if (N.bitLength() > 31) throw new IllegalArgumentException("TDiv31Inverse.findSingleFactor() does not work for N>31 bit, but N=" + N);
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
			if ((int) (N*reciprocals[i] + DISCRIMINATOR) * primes[i] == N) return primes[i];
			if ((int) (N*reciprocals[++i] + DISCRIMINATOR) * primes[i] == N) return primes[i];
			if ((int) (N*reciprocals[++i] + DISCRIMINATOR) * primes[i] == N) return primes[i];
			if ((int) (N*reciprocals[++i] + DISCRIMINATOR) * primes[i] == N) return primes[i];
			if ((int) (N*reciprocals[++i] + DISCRIMINATOR) * primes[i] == N) return primes[i];
			if ((int) (N*reciprocals[++i] + DISCRIMINATOR) * primes[i] == N) return primes[i];
			if ((int) (N*reciprocals[++i] + DISCRIMINATOR) * primes[i] == N) return primes[i];
			if ((int) (N*reciprocals[++i] + DISCRIMINATOR) * primes[i] == N) return primes[i];
		}
		for ( ; i<NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
			if ((int) (N*reciprocals[i] + DISCRIMINATOR) * primes[i] == N) return primes[i];
		}
		// otherwise N is prime
		return 1;
	}
}
