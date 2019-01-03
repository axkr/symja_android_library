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

import de.tilman_neumann.jml.primes.bounds.PrimeCountUpperBounds;

/**
 * Segmented sieve of Eratosthenes based on Kim Walisch's implementation at http://primesieve.org/segmented_sieve.html
 * 
 * @author Tilman Neumann
 */
public class SegmentedSieve {
//	private static final Logger LOG = Logger.getLogger(SegmentedSieve.class);
	
	private SieveCallback clientCallback;

	public SegmentedSieve(SieveCallback clientCallback) {
		this.clientCallback = clientCallback;
	}
	
	/**
	 * Generate primes.
	 * @param limit biggest number to test for prime
	 */
	public void sieve(long limit) {
		// small primes not delivered by the sieve below
		clientCallback.processPrime(2);

		int segmentSize = (int) Math.min(limit, 131072); // best segmentSize determined by experiment
		int sqrt = (int) Math.sqrt(limit);
		
		// Generate small primes <= sqrt(limit).
		// This is done marking as composite all multiples of i that have not been marked by smaller i, e.g.
		// i=2: 4, 6, 8, 10, 12, 14, 16, 18, 20, ...
		// i=3: 9, 12, 15, 18, 21, 24, 27, 30, ...
		// i=4: skipped
		// i=5: 25, 30, 35, 40, 45, 50, 55, 60, ...
		// i=6: skipped
		// i=7: 49, 56, 63, 70, 77, 84, 91, 98, ...
		// etc.
		boolean[] isComposite = new boolean[sqrt+1]; // initialized with false
		for (int i=2; i*i <= sqrt; i++) {
			if (!isComposite[i]) {
				for (int j = i*i; j <= sqrt; j+=i) {
					isComposite[j] = true;
				}
			}
		}

		int arraySize = (int) PrimeCountUpperBounds.combinedUpperBound(sqrt);
		// 2 * small primes used for sieving
		long[] doublePrimes = new long[arraySize];
		// multiples of small primes to cross out, reduced by low, so that they fit for the segment
		long[] next = new long[arraySize];
		int primesCount = 0;

		// segment loop
		long s = 3;
		long n = 3;
		for (long low=0; low <= limit; low += segmentSize) {
			boolean[] sieveIsComposite = new boolean[segmentSize]; // initialized with false
			// current segment = interval [low, high]
			long high = Math.min(low + segmentSize - 1, limit);
			
			// add new sieving primes <= sqrt(high)
			for ( ; s*s <= high; s+=2) {
				if (!isComposite[(int) s]) {
					doublePrimes[primesCount] = s<<1;
					next[primesCount] = s*s - low;
					primesCount++;
				}
			}
			
			// Sieve the current segment (the most time-consuming part)
			for (int i=0; i<primesCount; i++) {
				long j = next[i];
				long k = doublePrimes[i];
				for ( ; j<segmentSize; j+=k) {
					sieveIsComposite[(int)j] = true;
				}
				next[i] = j-segmentSize;
			}
			
			// Collect primes from the segment (second-most expensive)
			int nn = (int) (n-low);
			int nnMax = (int) Math.min(segmentSize - 1, limit-low);
			for ( ; nn<=nnMax; nn+=2) {
				if (!sieveIsComposite[nn]) {
					clientCallback.processPrime(nn+low);
				}
			}
			n = nn+low;
		}
	}
	
	public void finalize() {
		// resolve possible circular dependency that would cause a memory leak
		clientCallback = null;
	}
	
	/**
	 * Test performance without load caused by processPrime().
	 * @param args ignored
	 */
//	public static void main(String[] args) {
//    	ConfigUtil.initProject();
//		long limit = 1000000;
//		while (true) {
//			long start = System.nanoTime();
//			CountingCallback callback = new CountingCallback(); // initialize count=0 for each limit
//			SegmentedSieve sieve = new SegmentedSieve(callback);
//			sieve.sieve(limit);
//			LOG.info("Sieving x <= " + limit + " found " + callback.getCount() + " primes in " + ((System.nanoTime()-start) / 1000000) + " ms");
//			limit *=10;
//		}
//	}
}
