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
package de.tilman_neumann.jml.factor;

import static de.tilman_neumann.jml.base.BigIntConstants.I_0;
import static de.tilman_neumann.jml.base.BigIntConstants.I_1;
import static de.tilman_neumann.jml.base.BigIntConstants.I_2;
import static de.tilman_neumann.jml.base.BigIntConstants.I_MINUS_1;
import java.math.BigInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.tilman_neumann.jml.factor.base.FactorArguments;
import de.tilman_neumann.jml.factor.base.FactorResult;
import de.tilman_neumann.jml.primes.probable.BPSWTest;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * Abstraction of integer factorization algorithms.
 * This class provides a framework to find the complete prime factorization of N,
 * requiring only to implement the method findSingleFactor(BigInteger).
 * 
 * @author Tilman Neumann
 */
abstract public class FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(FactorAlgorithm.class);
	
	private static final boolean DEBUG = false;
	
	/** the number of primes needed to factor any int <= 2^31 - 1 using trial division */
	protected static final int NUM_PRIMES_FOR_31_BIT_TDIV = 4793;

	private BPSWTest bpsw = new BPSWTest();
	
	protected Integer tdivLimit;
	
	private static FactorAlgorithm DEFAULT = null;

	/**
	 * @return The best available single-threaded factor algorithm. (multi-threading may not always be wanted)
	 */
	public static FactorAlgorithm getDefault() {
		if (DEFAULT == null) {
			DEFAULT = new CombinedFactorAlgorithm(1);
		}
		return DEFAULT;
	}

	public FactorAlgorithm() {
		tdivLimit = null; // automatic determination based on experimental results
	}
	
	public FactorAlgorithm(Integer tdivLimit) {
		this.tdivLimit = tdivLimit;
	}
	
	/**
	 * @return The name of the algorithm, possibly including important parameters.
	 */
	abstract public String getName();

	/**
	 * Decomposes the argument N into prime factors.
	 * The result is a multiset of BigIntegers, sorted bottom-up.
	 * @param N Number to factor.
	 * @return The prime factorization of N
	 */
	public SortedMultiset<BigInteger> factor(BigInteger N) {
		SortedMultiset<BigInteger> primeFactors = new SortedMultiset_BottomUp<BigInteger>();
		factor(N, primeFactors);
		return primeFactors; 
	}

	/**
	 * Decomposes the argument N into prime factors.
	 * @param N Number to factor.
	 * @param primeFactors a map to which found factors are added
	 */
    public void factor(BigInteger N, SortedMultiset<BigInteger> primeFactors) {
		// Make N positive
		if (N.signum()<0) {
			primeFactors.add(I_MINUS_1);
			N = N.negate();
		}
		// Get rid of case |N| <= 1:
		if (N.compareTo(I_1) <= 0) {
			// If the original N was -1, then that "factor" has already been added to the map.
            // If N == 0, we still need to add that "factor":
			if (N.equals(I_0)) primeFactors.add(I_0);
			// If the original N was +1 then "the set of prime factors of 1 is the empty set" (https://oeis.org/wiki/Empty_product#Prime_factorization_of_1)
			return;
		}
		
		// Remove multiples of 2:
		int lsb = N.getLowestSetBit();
		if (lsb > 0) {
			primeFactors.add(I_2, lsb);
			N = N.shiftRight(lsb);
			if (N.equals(I_1)) {
				// N was a power of 2
				return;
			}
		}
		
		// N contains larger factors...
		FactorArguments args = new FactorArguments(N, 1);
		FactorResult factorResult = new FactorResult(primeFactors, new SortedMultiset_BottomUp<BigInteger>(), new SortedMultiset_BottomUp<BigInteger>(), 3);
		SortedMultiset<BigInteger> untestedFactors = factorResult.untestedFactors; // ArrayList would be faster
		untestedFactors.add(N);
		while (true) {
			if (DEBUG) LOG.debug("1: factorResult: " + factorResult);
			// resolve untested factors
			while (untestedFactors.size()>0) {
				BigInteger untestedFactor = untestedFactors.firstKey();
				int exp = untestedFactors.removeAll(untestedFactor);
				if (bpsw.isProbablePrime(untestedFactor)) {
					// The untestedFactor is probable prime. In exceptional cases this prediction may be wrong and untestedFactor composite
					// -> then we would falsely predict untestedFactor to be prime. BPSW is known to be exact for arguments <= 64 bit.
					//LOG.debug(untestedFactor + " is probable prime.");
					factorResult.primeFactors.add(untestedFactor, exp);
				} else {
					factorResult.compositeFactors.add(untestedFactor, exp);
				}
			}
			// now untestedFactors is empty
			if (DEBUG) LOG.debug("2: factorResult: " + factorResult);

			// factor composite factors; iteration needs to be fail-safe against element addition and removal
			while (true) {
				if (factorResult.compositeFactors.isEmpty()) {
					if (factorResult.untestedFactors.isEmpty()) {
						// all factors are prime factors now
						return;
					}
					// else there are still untested factors
					break;
				}
				
				BigInteger compositeFactor = factorResult.compositeFactors.firstKey();
				int exp = factorResult.compositeFactors.removeAll(compositeFactor);
				args.N = compositeFactor;
				args.NBits = compositeFactor.bitLength();
				args.exp = exp;
				searchFactors(args, factorResult);
				if (DEBUG) LOG.debug("3: factorResult: " + factorResult);
			}
		}
	}
	
	/**
	 * Try to find at least one factor of the given args.N, which is composite and odd.
	 * This is a default implementation for algorithms that will only find a single factor or none at all.
	 * For sub-algorithms that may find more factors at once this method should be overwritten appropriately.
	 * 
	 * @param args
	 * @param result the result of the factoring attempt. Should be initialized only once by the caller to reduce overhead.
	 */
	public void searchFactors(FactorArguments args, FactorResult result) {
		BigInteger N = args.N;
		BigInteger factor1 = findSingleFactor(N);
		if (factor1.compareTo(I_1) > 0 && factor1.compareTo(N) < 0) {
			// We found a factor, but here we cannot know if it is prime or composite
			result.untestedFactors.add(factor1, args.exp);
			result.untestedFactors.add(N.divide(factor1), args.exp);
		}

		// nothing found
	}
	
	/**
	 * Find a single factor of the given N, which is composite and odd.
	 * @param N number to be factored.
	 * @return factor
	 */
	abstract public BigInteger findSingleFactor(BigInteger N);
}
