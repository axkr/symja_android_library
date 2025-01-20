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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.primes.exact.SegmentedSieve;
import de.tilman_neumann.jml.primes.exact.SieveCallback;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * A factor algorithm using a prime sieve to compute the factorizations of many small numbers at once.
 * Unfortunately, this is currently still a bit slower than factoring all numbers individually.
 * 
 * Factoring too many numbers at once will need a lot of memory and eventually produce an OutOfMemoryError;
 * thus you should split such tasks into batches of an appropriate size like 1 million per batch.
 */
public class FactorSieve implements SieveCallback {
	private static final Logger LOG = LogManager.getLogger(FactorSieve.class);
	private static final boolean DEBUG = false;
	
	private Map<Long, SortedMultiset<Long>> factorizations; // maybe an array would be better but then we couldn't use long indices
	private long start;
	private long limit;
	
	/**
	 * Constructor to factor all numbers from 2 to the given limit.
	 * @param limit
	 */
	public FactorSieve(long limit) {
		this(2, limit);
	}
	
	/**
	 * Constructor to factor a range of numbers.
	 * @param start first number to factor
	 * @param limit last number to factor
	 */
	public FactorSieve(long start, long limit) {
		factorizations = new HashMap<>();
		this.start = start>1 ? start : 2;
		this.limit = limit;
	}
	
	/**
	 * Run the sieve. Thread-safe.
	 */
	public synchronized void sieve() {
		SegmentedSieve segmentedSieve = new SegmentedSieve(this);
		segmentedSieve.sieve(limit);
	}

	/**
	 * Fallback method: Receives new primes from the sieve and adds them to prime factorizations
	 */
	@Override
	public void processPrime(long prime) {
		if (DEBUG) LOG.debug("process prime " + prime + " (start = " + start + ", limit = " + limit + ")");
		long mod = start % prime;
		long n = mod>0 ? start-mod+prime : start;
		while (n<=limit) {
			if (DEBUG) LOG.debug("add prime " + prime + " to n = " + n);
			Long nL = Long.valueOf(n);
			SortedMultiset<Long> factors = factorizations.get(nL);
			if (factors == null) {
				factors = new SortedMultiset_BottomUp<Long>();
				factorizations.put(nL, factors);
			}
			int exponent = 1;
			long rest = n/prime;
			while (rest % prime == 0) {
				rest /= prime;
				exponent++;
			}
			factors.add(Long.valueOf(prime), exponent);
			n += prime;
		}
	}
	
	public SortedMultiset<Long> getFactorization(long n) {
		return factorizations.get(Long.valueOf(n));
	}
	
	public Map<Long, SortedMultiset<Long>> getFactorizations() {
		return factorizations;
	}
	
	public static long computeProduct(SortedMultiset<Long> factors) {
		if (factors == null) return 0;

		long result = 1;
		for (Long p : factors.keySet()) {
			int exponent = factors.get(p);
			result *= Math.pow(p.longValue(), exponent);
		}
		return result;
	}
}
