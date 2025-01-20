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
import de.tilman_neumann.jml.primes.bounds.PrimeCountUpperBounds;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.util.SortedMultiset;

/**
 * Trial division factor algorithm replacing division by multiplications.
 * 
 * Instead of dividing N by consecutive primes, we store the reciprocals of those primes, too,
 * and multiply N by those reciprocals. Only if such a result is near to an integer we need
 * to do a division.
 * 
 * Assuming that we want to identify "near integers" with a precision of 2^-d.
 * Then the approach works for primes p if bitLength(p) >= bitLength(N) - 53 + d.
 * 
 * For some unknown reason, storing and reusing the quotient q = (long) (N*r + DISCRIMINATOR)
 * only helps in TDiv31Inverse but not in TDiv63Inverse.
 * 
 * @authors Thilo Harich + Tilman Neumann
 */
public class TDiv63Inverse extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(TDiv63Inverse.class);
	
	private static AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get();

	private static final int DISCRIMINATOR_BITS = 10; // experimental result
	private static final double DISCRIMINATOR = 1.0/(1<<DISCRIMINATOR_BITS);

	private int[] primes;
	private double[] reciprocals;
	private int factorLimit, pLimit, primeCountBound;

	/**
	 * Create a trial division algorithm that is capable of finding factors up to factorLimit.
	 * @param factorLimit
	 */
	public TDiv63Inverse(int factorLimit) {
		this.factorLimit = factorLimit;
		pLimit = factorLimit; // default if not set explicitly
		primeCountBound = (int) PrimeCountUpperBounds.combinedUpperBound(factorLimit);
		primes = new int[primeCountBound];
		reciprocals = new double[primeCountBound];
		for (int i=0; i<primeCountBound; i++) {
			int p = SMALL_PRIMES.getPrime(i);
			primes[i] = p;
			reciprocals[i] = 1.0/p;
		}
	}
	
	@Override
	public String getName() {
		return "TDiv63Inverse";
	}

	/**
	 * Set the upper limit of primes to be tested.
	 * @param pLimit the limit; must be smaller than the factorLimit parameter passed to the constructor
	 * @return this
	 * @throws IllegalStateException if pLimit &gt; factorLimit
	 */
	public TDiv63Inverse setTestLimit(int pLimit) {
		if (pLimit > factorLimit) {
			throw new IllegalStateException("Requested pLimit=" + pLimit + " exceeds the factorLimit=" + factorLimit + " passed to the constructor!");
		}
		this.pLimit = pLimit;
		return this;
	}

	@Override
	public void factor(BigInteger Nbig, SortedMultiset<BigInteger> primeFactors) {
		int NBits = Nbig.bitLength();
		if (NBits>63) {
			throw new IllegalArgumentException("Argument N=" + Nbig  + " (" + NBits + " bit) is too large for algorithm " + getName());
		}
		
		long N = Nbig.longValue();
		
		int i=0;
		int pMinBits = NBits - 53 + DISCRIMINATOR_BITS;
		if (pMinBits>0) {
			// for the smallest primes we must do standard trial division
			int pMin = 1<<pMinBits, p;
			for (; (p=primes[i])<pMin; i++) {
				int exp = 0;
				while (N%p == 0) {
					exp++;
					N /= p;
				}
				if (exp>0) {
					primeFactors.add(BigInteger.valueOf(p), exp);
				}
			}
		}

		int p, exp;
		for (; (p=primes[i])<=pLimit; i++) {
			exp = 0;
			double r = reciprocals[i];
			while ((long) (N*r + DISCRIMINATOR) * p == N) {
				exp++;
				N /= p;
			}
			if (exp>0) {
				primeFactors.add(BigInteger.valueOf(p), exp);
			}
			if (p*(long)p > N) {
				break; // the remaining N is prime
			}
		}
		
		if (N>1) {
			// either N is prime, or we could not find all factors with p<=pLimit -> add the rest to the result
			primeFactors.add(BigInteger.valueOf(N));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation will return 1 if the smallest factor of N is greater than pLimit.
	 */
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		if (N.bitLength() > 63) throw new IllegalArgumentException("TDiv63Inverse.findSingleFactor() does not work for N>63 bit, but N=" + N);
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}
	
	public int findSingleFactor(long N) {
		if (N<0) N = -N; // sign does not matter
		if (N<4) return 1; // prime
		if ((N&1)==0) return 2; // N even
		
		int i=1;
		//LOG.debug("N=" + N);
		int Nbits = 64-Long.numberOfLeadingZeros(N);
		int pMinBits = Nbits - 53 + DISCRIMINATOR_BITS;
		try {
			if (pMinBits>0) {
				// for the smallest primes we must do standard trial division
				int pMin = 1<<pMinBits;
				for ( ; primes[i]<pMin; i++) {
					if (N%primes[i]==0) {
						return primes[i];
					}
				}
			}
			
			// Now the primes are big enough to apply trial division by inverses; unroll the loop.
			// We stop when pLimit is reached, which may have been set before via setTestLimit().
			for (; primes[i]<=pLimit; i++) {
				//LOG.debug("N=" + N + ": Test p=" + primes[i]);
				if (((long) (N*reciprocals[i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
				if (((long) (N*reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
				if (((long) (N*reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
				if (((long) (N*reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
				if (((long) (N*reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
				if (((long) (N*reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
				if (((long) (N*reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
				if (((long) (N*reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			int pMaxIndex = primeCountBound-1;
			int pMax = primes[pMaxIndex];
			LOG.error("TDiv63Inverse has been set up to find factors until p[" + pMaxIndex + "] = " + pMax + ", but now you are trying to access p[" + i + "] !");
		}
		
		// nothing found up to pLimit
		return 1;
	}
}
