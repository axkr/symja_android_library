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
package de.tilman_neumann.jml.primes.exact;

//import org.apache.log4j.Logger;
 
import de.tilman_neumann.jml.primes.bounds.NthPrimeUpperBounds;
import de.tilman_neumann.jml.primes.bounds.PrimeCountUpperBounds;

/**
 * An auto-expanding facade for the segmented sieve of Eratosthenes.
 * 
 * Singleton implementation to avoid spending too much memory on the primes in different instances.
 * 
 * @author Tilman Neumann
 */
public class AutoExpandingPrimesArray implements SieveCallback {
//	private static final Logger LOG = Logger.getLogger(AutoExpandingPrimesArray.class);
	private static final boolean DEBUG = false;
	
	// variables must be initialized to avoid exceptions in ensureMaxPrime()
	private int[] array = new int[] {2}; // the array to store the primes
	private int count = 1; // actual element count
	private int capacity = 1; // maximal array capacity
	
//	private BinarySearch bs = new BinarySearch();
	
	// singleton
	private static final AutoExpandingPrimesArray THE_PRIMES_ARRAY = new AutoExpandingPrimesArray();
	
	public static AutoExpandingPrimesArray get() {
		return THE_PRIMES_ARRAY;
	}
	
	/**
	 * Ensures that the array contains at least the first 'desiredCount' primes.
	 * @param desiredCount
	 * @return PrimeGenerator
	 */
	public AutoExpandingPrimesArray ensurePrimeCount(int desiredCount) {
		if (count < desiredCount) {
			// The current primes array is to small -> expansion needed.
			// Compute (tight) bound such that there are at least count primes in (0, nthPrimeUpperBound]
			long nthPrimeUpperBound = NthPrimeUpperBounds.combinedUpperBound(desiredCount);
			fetchPrimes(desiredCount, nthPrimeUpperBound);
		}
		return this;
	}

	/**
	 * Ensures that the array contains all primes <= x.
	 * @param x
	 * @return PrimeGenerator
	 */
	public AutoExpandingPrimesArray ensureLimit(int x) {
		if (array[count-1] < x) {
			// The current primes array is to small -> expansion needed.
			// Compute upper bound for the number of primes in (0, x]
			int countUpperBound = (int) PrimeCountUpperBounds.combinedUpperBound(x);
			fetchPrimes(countUpperBound, x);
//			if (DEBUG) LOG.debug("pMax = " + array[count-1] + ", x = " + x);
		}
		return this;
	}
	
	/**
	 * @param x
	 * @return the index where x would be inserted into the prime array.
	 */
//	public int getInsertPosition(int x) {
//		return bs.getInsertPosition(array, count, x);
//	}

	/**
	 * Get the n.th prime, e.g. p[0]=2. This method is auto-expanding the prime array when required.
	 * This should be slower than "unsafe" raw array access, but in typical applications like trial division
	 * I could not spot any performance penalty at all.
	 * 
	 * @param n
	 * @return n.th prime, where n starts at 0, e.g. p[0] = 2
	 */
	public int getPrime(int n) {
		if (count <= n) {
			// The current primes array is too small -> expansion needed.
			int nextCount = 3*count; // trade-off between speed and memory waste
			// Compute (tight) bound such that there are at least count primes in (0, nthPrimeUpperBound]
			long nthPrimeUpperBound = NthPrimeUpperBounds.combinedUpperBound(nextCount);
			fetchPrimes(nextCount, nthPrimeUpperBound);
		}
		return array[n];
	}
	
	/**
	 * Run the sieve to expand the primes array. Thread-safe.
	 * @param desiredCount wanted number of primes
	 * @param limit maximum value to be checked for being prime.
	 */
	private synchronized void fetchPrimes(int desiredCount, long limit) {
		// Is the array still too small when the current thread gets its go?
		if (desiredCount > count) {
			array = new int[desiredCount];
			capacity = desiredCount;
			count = 0;
			SegmentedSieve segmentedSieve = new SegmentedSieve(this);
			segmentedSieve.sieve(limit);
		}
	}

	/**
	 * Fallback method: Receives new primes from the sieve and stores them in the array.
	 */
	@Override
	public void processPrime(long prime) {
		if (count == capacity) return; // array is full
		array[count++] = (int) prime;
	}
}
