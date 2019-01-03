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
package de.tilman_neumann.jml.factor.siqs.sieve;

import java.util.ArrayList;
import java.util.List;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.BinarySearch;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;
import de.tilman_neumann.util.Timer;

/**
 * Advanced non-segmented sieve implementation.
 * 
 * Version 03:
 * -> The smallest primes are not used for sieving.
 *    A prime p makes an overall contribution proportional to log(p)/p to the sieve array,
 *    but the runtime of sieving with a prime p is proportional to sieveArraySize/p.
 *    Thus sieving with small primes is less effective, and skipping them improves performance.
 * -> Let counters run down -> simpler termination condition
 * -> Faster zero-initialization of sieve array with System.arrayCopy().
 * 
 * Version 03b:
 * -> Initialize sieve array such that a sieve hit is achieved if (logPSum & 0x80) != 0,
 *    and then use the or-trick in sieve:collect.
 * -> precompute minSolutionCounts for all p
 * -> allocate sieveArray with pMax extra entries to save size checks
 * 
 * Version 03c:
 * -> sieve positive x-values first, then negative x-values. Surprising improvement.
 * 
 * Version 03d:
 * -> Special treatment for large primes having 0-1 solutions for each of x1, x2 inside the sieve array.
 *    This is the biggest performance improvement since the 1.0.2 release!
 * 
 * Version 03e:
 * -> Collect smooth Q(x) for pos/neg x independently -> another small improvement
 * 
 * Version 03f:
 * -> Initialization is be done independently for pos/neg x, too -> now only 1 sieve array is needed!
 * 
 * Version 03g:
 * -> further unrolling of large primes
 * -> sieve with all primes as if they have 2 x-solutions
 * 
 * @author Tilman Neumann
 */
public class Sieve03g implements Sieve {
//	private static final Logger LOG = Logger.getLogger(Sieve03g.class);
//	private static final boolean DEBUG = false;

	// prime base
	private int primeBaseSize;
	/** we do not sieve with primes p_i, i<pMinIndex */
	private int pMinIndex;
	/** p_i with i>p1Index have at most 1 solution in the sieve array for each of x1, x2 */
	private int p1Index;
	private int p2Index;
	private int p3Index;
	private int[] minSolutionCounts_m3;
	
	private SolutionArrays solutionArrays;

	// sieve
	private int sieveArraySize;
	/** basic building block for fast initialization of sieve array */
	private byte[] initializer;
	/** the array holding logP sums for all x */
	private byte[] sieveArray;

	private BinarySearch binarySearch = new BinarySearch();

	// timings
	private boolean profile;
	private Timer timer = new Timer();
	private long initDuration, sieveDuration, collectDuration;
	
	@Override
	public String getName() {
		return "sieve03g";
	}
	
	@Override
	public void initializeForN(SieveParams sieveParams, int mergedBaseSize, boolean profile) {
		this.pMinIndex = sieveParams.pMinIndex;
		int pMax = sieveParams.pMax;
		initializer = sieveParams.getInitializerBlock();

		// Allocate sieve array: Typically SIQS adjusts such that 2.75 * sieveArraySize ~ pMax.
		// For large primes with 0 or 1 sieve locations we need to allocate pMax+1 entries;
		// For primes p[i], i<p1Index, we need p[i]+sieveArraySize = 2*sieveArraySize entries.
		this.sieveArraySize = sieveParams.sieveArraySize;
		int sieveAllocationSize = Math.max(pMax+1, 2*sieveArraySize);
		sieveArray = new byte[sieveAllocationSize];
//		if (DEBUG) LOG.debug("pMax = " + pMax + ", sieveArraySize = " + sieveArraySize + " --> sieveAllocationSize = " + sieveAllocationSize);

		// profiling
		this.profile = profile;
		initDuration = sieveDuration = collectDuration = 0;
	}

	@Override
	public void initializeForAParameter(SolutionArrays solutionArrays, int filteredBaseSize) {
		this.solutionArrays = solutionArrays;
		int[] powers = solutionArrays.powers;
		this.primeBaseSize = filteredBaseSize;
		
		this.p1Index = binarySearch.getInsertPosition(powers, primeBaseSize, sieveArraySize);
		this.p2Index = binarySearch.getInsertPosition(powers, p1Index, (sieveArraySize+1)/2);
		this.p3Index = binarySearch.getInsertPosition(powers, p2Index, (sieveArraySize+2)/3);
//		if (DEBUG) LOG.debug("primeBaseSize=" + primeBaseSize + ", p1Index=" + p1Index + ", p2Index=" + p2Index + ", p3Index=" + p3Index);
		
		// The minimum number of x-solutions in the sieve array is floor(sieveArraySize/p).
		// E.g. for p=3, sieveArraySize=8 there are solutions (0, 3, 6), (1, 4, 7), (2, 5)  <-- 8 is not in sieve array anymore
		// -> minSolutionCount = 2
		this.minSolutionCounts_m3 = new int[p3Index];
		for (int i=p3Index-1; i>=pMinIndex; i--) {
			minSolutionCounts_m3[i] = sieveArraySize/powers[i] - 3;
			//LOG.debug("p=" + primesArray[i] + ": minSolutionCount = " + minSolutionCounts_m3[i]);
		}
	}

	@Override
	public List<Integer> sieve() {
		if (profile) timer.capture();
		this.initializeSieveArray(sieveArraySize);
		if (profile) initDuration += timer.capture();
		
		// Sieve with positive x, large primes:
		final int[] powers = solutionArrays.powers;
		final int[] x1Array = solutionArrays.x1Array;
		final int[] x2Array = solutionArrays.x2Array;
		final byte[] logPArray = solutionArrays.logPArray;
		int i, x1, x2, j;
		for (i=primeBaseSize-1; i>=p1Index; i--) {
			// x1 == x2 happens only if p divides k -> for large primes p > k there are always 2 distinct solutions.
			// x1, x2 may exceed sieveArraySize, but we allocated the arrays somewhat bigger to save the size checks.
			final byte logP = logPArray[i];
			sieveArray[x1Array[i]] += logP;
			sieveArray[x2Array[i]] += logP;
		}
		for ( ; i>=p2Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1 = x1Array[i];
			x2 = x2Array[i];
			sieveArray[x1] += logP;
			sieveArray[x2] += logP;
			sieveArray[x1+p] += logP;
			sieveArray[x2+p] += logP;
		}
		for ( ; i>=p3Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1 = x1Array[i];
			x2 = x2Array[i];
			sieveArray[x1] += logP;
			sieveArray[x2] += logP;
			sieveArray[x1+p] += logP;
			sieveArray[x2+p] += logP;
			final int p2 = p<<1;
			sieveArray[x1+p2] += logP;
			sieveArray[x2+p2] += logP;
		}
		// Positive x, small primes:
		for ( ; i>=pMinIndex; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1 = x1Array[i];
			x2 = x2Array[i];
			// Solution x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k.
			// But there are very few of such primes (none if k==1), so we are better off avoiding that case distinction.
			// The last x may exceed sieveArraySize, but we allocated the arrays somewhat bigger to save the size checks.
			sieveArray[x1] += logP;
			sieveArray[x2] += logP;
			sieveArray[x1+p] += logP;
			sieveArray[x2+p] += logP;
			final int p2 = p<<1;
			sieveArray[x1+=p2] += logP;
			sieveArray[x2+=p2] += logP;
			for (j=minSolutionCounts_m3[i]; j>=0; j--) {
				sieveArray[x1+=p] += logP;
				sieveArray[x2+=p] += logP;
			}
		} // end for (p)
		if (profile) sieveDuration += timer.capture();

		// collect results
		List<Integer> smoothXList = new ArrayList<Integer>();
		// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
		for (int x=sieveArraySize-1; x>=0; ) {
			// Unfortunately, in Java we can not cast byte[] to int[] or long[].
			// So we have to use 'or'. More than 4 'or's do not pay out.
			if (((sieveArray[x--] | sieveArray[x--] | sieveArray[x--] | sieveArray[x--]) & 0x80) != 0) {
				// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division!
				if (sieveArray[x+1] < 0) smoothXList.add(x+1);
				if (sieveArray[x+2] < 0) smoothXList.add(x+2);
				if (sieveArray[x+3] < 0) smoothXList.add(x+3);
				if (sieveArray[x+4] < 0) smoothXList.add(x+4);
			}
		}
		if (profile) collectDuration += timer.capture();
		
		// re-initialize sieve array for negative x
		this.initializeSieveArray(sieveArraySize);
		if (profile) initDuration += timer.capture();

		// negative x, large primes:
		for (i=primeBaseSize-1; i>=p1Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			sieveArray[p-x1Array[i]] += logP;
			sieveArray[p-x2Array[i]] += logP;
		}
		for (; i>=p2Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1 = p-x1Array[i];
			x2 = p-x2Array[i];
			sieveArray[x1] += logP;
			sieveArray[x2] += logP;
			sieveArray[x1+p] += logP;
			sieveArray[x2+p] += logP;
		}
		for (; i>=p3Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1 = p-x1Array[i];
			x2 = p-x2Array[i];
			sieveArray[x1] += logP;
			sieveArray[x2] += logP;
			sieveArray[x1+p] += logP;
			sieveArray[x2+p] += logP;
			final int p2 = p<<1;
			sieveArray[x1+p2] += logP;
			sieveArray[x2+p2] += logP;
		}
		// negative x, small primes:
		for (; i>=pMinIndex; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1 = p-x1Array[i];
			x2 = p-x2Array[i];
			sieveArray[x1] += logP;
			sieveArray[x2] += logP;
			sieveArray[x1+p] += logP;
			sieveArray[x2+p] += logP;
			final int p2 = p<<1;
			sieveArray[x1+=p2] += logP;
			sieveArray[x2+=p2] += logP;
			for (j=minSolutionCounts_m3[i]; j>=0; j--) {
				sieveArray[x1+=p] += logP;
				sieveArray[x2+=p] += logP;
			}
		} // end for (p)
		if (profile) sieveDuration += timer.capture();

		// collect results
		// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
		for (int x=sieveArraySize-1; x>=0; ) {
			// Unfortunately, in Java we can not cast byte[] to int[] or long[].
			// So we have to use 'or'. More than 4 'or's do not pay out.
			if (((sieveArray[x--] | sieveArray[x--] | sieveArray[x--] | sieveArray[x--]) & 0x80) != 0) {
				// at least one of the tested Q(-x) is sufficiently smooth to be passed to trial division!
				if (sieveArray[x+1] < 0) smoothXList.add(-(x+1));
				if (sieveArray[x+2] < 0) smoothXList.add(-(x+2));
				if (sieveArray[x+3] < 0) smoothXList.add(-(x+3));
				if (sieveArray[x+4] < 0) smoothXList.add(-(x+4));
			}
		}
		if (profile) collectDuration += timer.capture();
		return smoothXList;
	}

	/**
	 * Initialize the sieve array(s) with the initializer value computed before.
	 * @param sieveArraySize
	 */
	private void initializeSieveArray(int sieveArraySize) {
		// overwrite existing arrays with initializer. we know that sieve array size is a multiple of 256
		System.arraycopy(initializer, 0, sieveArray, 0, 256);
		int filled = 256;
		int unfilled = sieveArraySize-filled;
		while (unfilled>0) {
			int fillNext = Math.min(unfilled, filled);
			System.arraycopy(sieveArray, 0, sieveArray, filled, fillNext);
			filled += fillNext;
			unfilled = sieveArraySize-filled;
		}
	}
	
	@Override
	public SieveReport getReport() {
		return new SieveReport(initDuration, sieveDuration, collectDuration);
	}
	
	@Override
	public void cleanUp() {
		solutionArrays = null;
		minSolutionCounts_m3 = null;
		sieveArray = null;
	}
}
