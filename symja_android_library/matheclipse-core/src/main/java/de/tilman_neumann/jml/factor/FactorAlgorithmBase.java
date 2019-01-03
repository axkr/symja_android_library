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
package de.tilman_neumann.jml.factor;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.Map;

import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.jml.primes.probable.BPSWTest;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * Abstraction of integer factorization algorithms. This class provides a framework to find the complete prime
 * factorization of N, requiring only to implement the method findSingleFactor(BigInteger).
 * 
 * @author Tilman Neumann
 */
abstract public class FactorAlgorithmBase implements SingleFactorFinder {
	@SuppressWarnings("unused")
	// private static final Logger LOG = Logger.getLogger(FactorAlgorithmBase.class);

	/** the number of primes needed to factor any int <= 2^31 - 1 using trial division */
	protected static final int NUM_PRIMES_FOR_31_BIT_TDIV = 4793;

	protected static AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get()
			.ensurePrimeCount(NUM_PRIMES_FOR_31_BIT_TDIV);

	private BPSWTest probablePrimeTest;

	/**
	 * Complete constructor.
	 */
	public FactorAlgorithmBase() {
		probablePrimeTest = new BPSWTest();
	}

	/**
	 * Factoring of composite integers.
	 * 
	 * @param N
	 *            Number to decompose into prime factors.
	 * @return Factors or null if N prime.
	 */
	public SortedMultiset<BigInteger> factor(BigInteger N) {
		SortedMultiset<BigInteger> factors = new SortedMultiset_BottomUp<BigInteger>();
		// first get rid of case |N|<=1:
		if (N.abs().compareTo(I_1) <= 0) {
			if (!N.equals(BigInteger.ONE)) {
				factors.add(N);
			}
			return factors;
		}
		// make N positive:
		if (N.signum() < 0) {
			factors.add(I_MINUS_1);
			N = N.abs();
		}
		// Remove multiples of 2:
		int lsb = N.getLowestSetBit();
		if (lsb > 0) {
			factors.add(I_2, lsb);
			N = N.shiftRight(lsb);
		}
		if (N.equals(I_1)) {
			// N was a power of 2
			return factors;
		}

		if (N.bitLength() > 45) {
			// TODO This is a crude decision strategy, think about a better one.

			// Do some trial division to factor smooth numbers much faster.
			// We start at p[1]=3, because powers of 2 have already been removed.
			// We run over all p_i<2^31-1 as long as N can still have a prime factor >= p_i.
			// This would find all prime factors < 46340.
			for (int i = 1; i < NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
				BigInteger p_i = BigInteger.valueOf(SMALL_PRIMES.getPrime(i));
				BigInteger[] div = N.divideAndRemainder(p_i);
				if (div[1].equals(I_0)) {
					// p_i divides N at least once
					do {
						factors.add(p_i);
						N = div[0];
						div = N.divideAndRemainder(p_i);
					} while (div[1].equals(I_0));

					// check if we are done
					BigInteger p_i_square = p_i.multiply(p_i);
					if (p_i_square.compareTo(N) > 0) {
						// the remaining N is 1 or prime
						if (N.compareTo(I_1) > 0)
							factors.add(N);
						return factors;
					}
				}
			}

			// TODO For large and not so smooth N, an advanced small factor test like ECM or parallel Pollard-Rho would
			// be nice here
		}

		// N contains larger factors...
		SortedMultiset<BigInteger> otherFactors = factor_recurrent(N);
		// LOG.debug(this.factorAlg + ": pow2Factors=" + factors + ", otherFactors=" + otherFactors);
		factors.addAll(otherFactors);
		// LOG.debug(this.factorAlg + ": => all factors = " + factors);
		return factors;
	}

	private SortedMultiset<BigInteger> factor_recurrent(BigInteger N) {
		SortedMultiset<BigInteger> factors = new SortedMultiset_BottomUp<BigInteger>();
		if (probablePrimeTest.isProbablePrime(N)) {
			// N is probably prime. In exceptional cases this prediction
			// may be wrong and N composed -> then we would falsely predict N to be prime.
			// LOG.debug(N + " is probable prime.");
			factors.add(N);
			return factors;
		} // else: N is surely not prime

		// this is the expensive part:
		// find a factor of N, where N is composed and has no 2s
		BigInteger factor1 = findSingleFactor(N);
		// Is it possible to further decompose the parts?
		// LOG.debug("found factor1 = " + factor1, new Throwable());
		SortedMultiset<BigInteger> subfactors1 = factor_recurrent(factor1);
		// LOG.debug("subfactors of " + factor1 + " = " + subfactors1);
		factors.addAll(subfactors1);
		BigInteger factor2 = N.divide(factor1);
		SortedMultiset<BigInteger> subfactors2 = factor_recurrent(factor2);
		// LOG.debug("subfactors of " + factor2 + " = " + subfactors2);
		factors.addAll(subfactors2);
		// LOG.debug("result = " + factors);
		return factors;
	}

	/**
	 * Returns a product-like representation of the given factorization, with distinct keys separated by "*" and the
	 * multiplicity indicated by "^".
	 */
	public String getPrettyFactorString(SortedMultiset<BigInteger> factorization) {
		if (factorization.size() > 0) {
			// Implementation note: Is faster with String than with StringBuffer!
			String factorStr = "";
			for (Map.Entry<BigInteger, Integer> entry : factorization.entrySet()) {
				factorStr += entry.getKey();
				Integer multiplicity = entry.getValue();
				if (multiplicity.intValue() > 1) {
					factorStr += "^" + multiplicity;
				}
				factorStr += " * ";
			}
			// remove the last ", "
			return factorStr.substring(0, factorStr.length() - 3);
		}

		// no elements
		return "1";
	}
}
