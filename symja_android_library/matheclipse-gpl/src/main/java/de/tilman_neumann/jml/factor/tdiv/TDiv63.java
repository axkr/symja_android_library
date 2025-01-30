/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2025 Tilman Neumann - tilman.neumann@web.de
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

import static de.tilman_neumann.jml.base.BigIntConstants.I_2;

import java.math.BigInteger;
import java.util.SortedMap;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.base.FactorArguments;
import de.tilman_neumann.jml.factor.base.FactorResult;
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
		
		int i=0, p;
		while ((p = SMALL_PRIMES.getPrime(i++)) <= pLimit) { // upper bound avoids positive int overflow
			if (N%p == 0) {
				int exp = 0;
				do {
					exp++;
					N /= p;
				} while (N%p == 0);
				primeFactors.add(BigInteger.valueOf(p), exp);
			}
			// for random composite N, it is much much faster to check the termination condition after each p;
			// for semiprime N, it would be ~40% faster to do it only after successful divisions
			if (((long)p) * p > N) { // move p as long into registers makes a performance difference
				break;
			}
		}
		
		if (N>1) {
			primeFactors.add(BigInteger.valueOf(N));
		}
	}
	
	/**
	 * Try to find small factors of a positive argument N by doing trial division by all primes p <= pLimit.
	 * 
	 * @param args
	 * @param result a pre-initialized data structure to add results to
	 */
	@Override
	public void searchFactors(FactorArguments args, FactorResult result) {
		if (args.NBits > 63) throw new IllegalArgumentException(getName() + ".searchFactors() does not work for N>63 bit, but N=" + args.N + " has " + args.NBits + " bit");
		
		long N = args.N.longValue();
		int Nexp = args.exp;
		SortedMap<BigInteger, Integer> primeFactors = result.primeFactors;
		
		// Remove multiples of 2:
		int lsb = Long.numberOfTrailingZeros(N);
		if (lsb > 0) {
			primeFactors.put(I_2, lsb*Nexp);
			N >>>= lsb;
		}
		
		if (N == 1) return;
		
		SMALL_PRIMES.ensureLimit(pLimit);
		
		int p_i;
		for (int i=1; (p_i=SMALL_PRIMES.getPrime(i))<=pLimit; i++) {
			int exp = 0;
			while (N%p_i == 0) {
				N /= p_i;
				exp++;
			}
			if (exp > 0) {
				// At least one division has occurred, add the factor(s) to the result map
				addToMap(BigInteger.valueOf(p_i), exp*Nexp, primeFactors);
			}
			// for random composite N, it is much much faster to check the termination condition after each p;
			// for semiprime N, it would be ~40% faster to do it only after successful divisions
			if (((long)p_i) * p_i > N) { // move p as long into registers makes a performance difference
				// the remaining N is 1 or prime
				if (N>1) addToMap(BigInteger.valueOf(N), Nexp, primeFactors);
				result.smallestPossibleFactor = p_i; // may be helpful in following factor algorithms
				return;
			}
		}
		
		result.smallestPossibleFactor = p_i; // may be helpful in following factor algorithms
		if (N>1) result.untestedFactors.add(BigInteger.valueOf(N), Nexp); // we do not know if the remaining N is prime or composite
	}

	private void addToMap(BigInteger N, int exp, SortedMap<BigInteger, Integer> map) {
		Integer oldExp = map.get(N);
		// replaces old entry if oldExp!=null
		map.put(N, (oldExp == null) ? exp : oldExp+exp);
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
