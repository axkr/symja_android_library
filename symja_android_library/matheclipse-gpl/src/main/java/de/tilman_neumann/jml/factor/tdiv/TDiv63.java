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
 * Trial division factor algorithm using the safe AutoExpandingPrimesArray class.
 * 
 * @author Tilman Neumann
 */
public class TDiv63 extends FactorAlgorithm {
	
	private static AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get().ensurePrimeCount(NUM_PRIMES_FOR_31_BIT_TDIV);

	private int pLimit = Integer.MAX_VALUE;

	@Override
	public String getName() {
		return "TDiv63";
	}

	/**
	 * Set the upper limit of primes to be tested.
	 * @param pLimit upper limit of primes to be tested
	 * @return this
	 */
	public TDiv63 setTestLimit(int pLimit) {
		this.pLimit = pLimit;
		return this;
	}

	@Override
	public void factor(BigInteger Nbig, SortedMultiset<BigInteger> primeFactors) {
		long N = Nbig.longValue();
		
		int i=1, p;
		while ((p = SMALL_PRIMES.getPrime(i++)) <= pLimit) { // upper bound avoids positive int overflow
			if (N%p == 0) {
				int exp = 0;
				do {
					exp++;
					N = N/p;
				} while (N%p == 0);
				primeFactors.add(BigInteger.valueOf(p), exp);
			}
			if (p*(long)p > N) {
				break;
			}
		}
		
		if (N>1) {
			primeFactors.add(BigInteger.valueOf(N));
		}
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		if (N.bitLength() > 63) throw new IllegalArgumentException("TDiv63.findSingleFactor() does not work for N>63 bit, but N=" + N);
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}
	
	public int findSingleFactor(long N) {
		if (N<0) N = -N; // sign does not matter
		if (N<4) return 1; // prime
		if ((N&1)==0) return 2; // N even
		
		int i=1, p;
		while ((p = SMALL_PRIMES.getPrime(i++)) <= pLimit) { // upper bound avoids positive int overflow
			if (N%p==0) return p;
		}
		
		// nothing found up to pLimit
		return 1;
	}
}
