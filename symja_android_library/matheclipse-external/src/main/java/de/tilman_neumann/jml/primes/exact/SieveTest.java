/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2019 Tilman Neumann (www.tilman-neumann.de)
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

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.primes.bounds.NthPrimeUpperBounds;
import de.tilman_neumann.jml.primes.exact.CountingCallback;
import de.tilman_neumann.jml.primes.exact.SegmentedSieve;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.util.ConfigUtil;

import static org.junit.Assert.*;

/**
 * Test performance and correctness of results of prime sieves.
 * @author Tilman Neumann
 */
public class SieveTest {
	private static final Logger LOG = Logger.getLogger(SieveTest.class);
	
	private static void testCorrectness(int maxCount) {
		for (int count=100; count<=maxCount; count*=10) {
			LOG.info("Test correctness of first " + count + " primes...");
			
			// get correct data
			CollectingCallback correctCallback = new CollectingCallback(count);
	    	SimpleSieve correctSieve = new SimpleSieve(correctCallback);
			int nthPrimeUpperBound = (int) NthPrimeUpperBounds.combinedUpperBound(count);
	    	correctSieve.sieve(nthPrimeUpperBound);
	    	int correctCount = correctCallback.count;
	    	int[] correctResult = correctCallback.array;
	    	assertEquals(count, correctCount);
	    	assertEquals(2, correctResult[0]);
	    	assertEquals(3, correctResult[1]);
	    	assertEquals(5, correctResult[2]);
	    	
			// test segmented sieve
			CollectingCallback segmentedCallback = new CollectingCallback(count);
	    	SegmentedSieve segmentedSieve = new SegmentedSieve(segmentedCallback);
			segmentedSieve.sieve(nthPrimeUpperBound);
			int[] segmentedResult = segmentedCallback.array;
	    	assertEquals(count, segmentedCallback.count);
	    	for (int i=0; i<count; i++) {
		    	assertEquals(correctResult[i], segmentedResult[i]);
	    	}
			
			// test sieve facade
	    	AutoExpandingPrimesArray primesArray = AutoExpandingPrimesArray.get().ensurePrimeCount(count);
	    	for (int i=0; i<count; i++) {
		    	assertEquals(correctResult[i], primesArray.getPrime(i));
	    	}
		}
	}
	
	private static void testPerformance() {
		for (long count=100; ; count*=10) {
			long start;
			
			// Test sieve facade:
			// * Since all primes are stored in an array, it does not work for too big arguments.
			// * Since it is a singleton implementation, it should take no time at all for number
			//   ranges already computed in the previous correctness test.
			if (count <= 100000000) {
				start = System.currentTimeMillis();
				AutoExpandingPrimesArray primesArray = AutoExpandingPrimesArray.get();
				primesArray.ensurePrimeCount( (int) count);
				LOG.info("AutoExpandingPrimesArray took " + (System.currentTimeMillis()-start) + "ms to identify the first " + count + " primes.");
				
				// Test access performance: Pretty fast.
				int oddPrimesCount = 0;
				start = System.currentTimeMillis();
				for (int i=0; i<count; i++) {
					int p = primesArray.getPrime(i);
					// the following statement excludes compiler optimizations
					if ((p&1) == 1) {
						oddPrimesCount++;
					}
				}
				LOG.debug("    Accessing " + count + " primes took " + (System.currentTimeMillis()-start) + "ms (" + oddPrimesCount + " odd primes found)");
			}

			// Test segmented sieve
			long nthPrimeUpperBound = NthPrimeUpperBounds.combinedUpperBound(count);
			CountingCallback callback = new CountingCallback();
	    	SegmentedSieve segmentedSieve = new SegmentedSieve(callback);
	    	start = System.currentTimeMillis();
			segmentedSieve.sieve(nthPrimeUpperBound);
			LOG.info("SegementedSieve took " + (System.currentTimeMillis()-start) + "ms to identify the first " + count + " primes.");
		}
	}
	
	/**
	 * Test.
	 * @param args ignored
	 */
	public static void main(String[] args) {
    	ConfigUtil.initProject();
    	testCorrectness(10000000); // 100m is quite slow but feasible; but array-storing algorithms will fail soon above that
    	testPerformance();
	}
}
