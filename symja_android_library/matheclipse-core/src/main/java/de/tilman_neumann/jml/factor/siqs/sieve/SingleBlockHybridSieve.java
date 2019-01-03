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
 * Combination of a monolithic sieve for large primes > sieveArraySize/3,
 * and a single block sieve for p < sieveArraySize/3.
 * 
 * Sieving incomplete blocks makes no sense, because we have to run through all primes,
 * but harvest only a small x-range. Thus, we arrange blocks such that block size divides sieve array size.
 * 
 * @author Tilman Neumann
 */
public class SingleBlockHybridSieve implements Sieve {
//	private static final Logger LOG = Logger.getLogger(SingleBlockHybridSieve.class);
//	private static final boolean DEBUG = false;

	// prime base
	private int primeBaseSize;
	/** we do not sieve with primes p_i, i<pMinIndex */
	private int pMinIndex;
	/** p_i with i>p1Index have at most 1 solution in the sieve array for each of x1, x2 */
	private int p1Index;
	private int p2Index;
	private int p3Index;
	
	private SolutionArrays solutionArrays;

	// sieve
	private int sieveArraySize;
	/** basic building block for fast initialization of sieve array */
	private byte[] initializer;
	/** the array holding logP sums for all x */
	private byte[] sieveArray;
	private byte[] sieveBlock;
	/** sieve block size */
	private int desiredBlockSize;
	private int effectiveBlockSize;
	/** number of complete blocks */
	private int blockCount;
	
	/** index of largest prime fitting into a block */
	int r_s;
	
	private int[] xPosArray;
	private int[] xNegArray;
	private int[] dPosArray;
	private int[] dNegArray;

	private BinarySearch binarySearch = new BinarySearch();

	// timings
	private boolean profile;
	private Timer timer = new Timer();
	private long initDuration, sieveDuration, collectDuration;

	/**
	 * Full constructor.
	 * @param blockSize size of a sieve segment
	 */
	public SingleBlockHybridSieve(int blockSize) {
		this.desiredBlockSize = blockSize;
	}
	
	@Override
	public String getName() {
		return "singleHybrid(" + sieveArraySize + "/" + effectiveBlockSize + ")";
	}
	
	@Override
	public void initializeForN(SieveParams sieveParams, int mergedBaseSize, boolean profile) {
		this.pMinIndex = sieveParams.pMinIndex;
		int pMax = sieveParams.pMax;
		initializer = sieveParams.getInitializerBlock();

		// Find effectiveBlockSize, blockCount and sieveArraySize such that
		// * sieveArraySize is near to sieveArraySize0
		// * effectiveBlockSize is near to desiredBlockSize
		// * c | effectiveBlockSize, where c is the number of bytes collected at once
		// * effectiveBlockSize | sieveArraySize
		int sieveArraySize0 = sieveParams.sieveArraySize;
		blockCount = BlockSieveUtil.computeBestBlockCount(sieveArraySize0, desiredBlockSize);
		int blockBase = 16*blockCount;
		sieveArraySize = blockBase * /*floor*/(sieveArraySize0/blockBase);
		effectiveBlockSize = sieveArraySize / blockCount; // exact
//		if (DEBUG) {
//			LOG.debug("sieveArraySize0=" + sieveArraySize0 + ", desiredBlockSize=" + desiredBlockSize + " -> blockCount=" + blockCount + ", sieveArraySize=" + sieveArraySize + ", effectiveBlockSize=" + effectiveBlockSize);
//			assertEquals(sieveArraySize, blockCount*effectiveBlockSize);
//		}

		// Allocate sieve array: Typically SIQS adjusts such that 2.75 * sieveArraySize ~ pMax.
		// For large primes with 0 or 1 sieve locations we need to allocate pMax+1 entries;
		// For primes p[i], i<p1Index, we need p[i]+sieveArraySize = 2*sieveArraySize entries.
		int sieveAllocationSize = Math.max(pMax+1, 2*sieveArraySize);
		sieveArray = new byte[sieveAllocationSize];
//		if (DEBUG) LOG.debug("pMax = " + pMax + ", sieveArraySize = " + sieveArraySize + " --> sieveAllocationSize = " + sieveAllocationSize);
		sieveBlock = new byte[effectiveBlockSize];

		// profiling
		this.profile = profile;
		initDuration = sieveDuration = collectDuration = 0;
	}

	@Override
	public void initializeForAParameter(SolutionArrays solutionArrays, int filteredBaseSize) {
		this.solutionArrays = solutionArrays;
		int[] powers = solutionArrays.powers;
		this.primeBaseSize = filteredBaseSize;
		
		p1Index = binarySearch.getInsertPosition(powers, primeBaseSize, sieveArraySize);
		p2Index = binarySearch.getInsertPosition(powers, p1Index, (sieveArraySize+1)/2);
		p3Index = binarySearch.getInsertPosition(powers, p2Index, (sieveArraySize+2)/3);
		// find the index of the largest prime < p3 fitting into a block
		r_s = binarySearch.getInsertPosition(powers, p3Index, effectiveBlockSize);
//		if (DEBUG) LOG.debug("primeBaseSize=" + primeBaseSize + ", p1Index=" + p1Index + ", p2Index=" + p2Index + ", p3Index=" + p3Index + ", r_s = " + r_s);

		xPosArray = new int[p3Index];
		xNegArray = new int[p3Index];
		dPosArray = new int[p3Index];
		dNegArray = new int[p3Index];
	}

	@Override
	public List<Integer> sieve() {
		if (profile) timer.capture();
		this.initializeSieveArray(sieveArraySize);
		
		// prepare single-block data for smallish primes:
		// this needs to be done in sieve(), because it depends on the the x-arrays
		final int[] powers = solutionArrays.powers;
		final int[] x1Array = solutionArrays.x1Array;
		final int[] x2Array = solutionArrays.x2Array;
		int x1, x2;
		for (int i=pMinIndex; i<p3Index; i++) {
			x1 = x1Array[i];
			x2 = x2Array[i];
			if (x1<x2) {
				xPosArray[i] = x1;
				xNegArray[i] = powers[i] - x2;
				dNegArray[i] = dPosArray[i] = x2 - x1;
			} else {
				xPosArray[i] = x2;
				xNegArray[i] = powers[i] - x1;
				dNegArray[i] = dPosArray[i] = x1 - x2;
			}
		}
		if (profile) initDuration += timer.capture();

		// Sieve with positive x, large primes:
		List<Integer> smoothXList = new ArrayList<Integer>();
		final byte[] logPArray = solutionArrays.logPArray;
		int i;
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
		if (profile) sieveDuration += timer.capture();

		// Positive x, small primes:
		for (int b=0; b<blockCount; b++) { // bottom-up order is required because in each block, the data for the next block is adjusted
			// positive x: initialize block
			final int blockOffset = b*effectiveBlockSize;
			System.arraycopy(sieveArray, blockOffset, sieveBlock, 0, effectiveBlockSize);
			if (profile) initDuration += timer.capture();
			
			// positive x: sieve block [b*B, (b+1)*B] with prime index ranges 0...r_s-1 and r_s...max
			//LOG.debug("sieve pos. block " + b + " (" + effectiveBlockSize + " bytes) with primes " + pMinIndex + " ... " + r_s + " ... " + p3Index);
			sievePositiveXBlock(powers, logPArray, effectiveBlockSize, pMinIndex, r_s, p3Index);
			if (profile) sieveDuration += timer.capture();
			
			// collect block
			// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
			final int blockOffset1 = blockOffset+1;
			final int blockOffset2 = blockOffset+2;
			final int blockOffset3 = blockOffset+3;
			final int blockOffset4 = blockOffset+4;
			for (int x=effectiveBlockSize-1; x>=0; ) {
				// Unfortunately, in Java we can not cast byte[] to int[] or long[].
				// So we have to use 'or'. More than 4 'or's do not pay out.
				if (((sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--]) & 0x80) != 0) {
					// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division!
					if (sieveBlock[x+1] < 0) smoothXList.add(x+blockOffset1);
					if (sieveBlock[x+2] < 0) smoothXList.add(x+blockOffset2);
					if (sieveBlock[x+3] < 0) smoothXList.add(x+blockOffset3);
					if (sieveBlock[x+4] < 0) smoothXList.add(x+blockOffset4);
				}
			} // end for (x)
			if (profile) collectDuration += timer.capture();
		}
		
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
		if (profile) sieveDuration += timer.capture();

		// negative x, small primes:
		for (int b=0; b<blockCount; b++) { // bottom-up order is required because in each block, the data for the next block is adjusted
			// negative x: initialize block
			final int blockOffset = b*effectiveBlockSize;
			System.arraycopy(sieveArray, blockOffset, sieveBlock, 0, effectiveBlockSize);
			if (profile) initDuration += timer.capture();
			
			// negative x: sieve block [b*B, (b+1)*B] with prime index ranges 0...r_s-1 and r_s...max
			//LOG.debug("sieve neg. block " + b + " (" + effectiveBlockSize + " bytes) with primes " + pMinIndex + " ... " + r_s + " ... " + p3Index);
			sieveNegativeXBlock(powers, logPArray, effectiveBlockSize, pMinIndex, r_s, p3Index);
			if (profile) sieveDuration += timer.capture();
			
			// Collect block: We collect 4 bytes at once, thus we need 4 | effectiveBlockSize -> see initialize()
			final int blockOffset1 = blockOffset+1;
			final int blockOffset2 = blockOffset+2;
			final int blockOffset3 = blockOffset+3;
			final int blockOffset4 = blockOffset+4;
			for (int x=effectiveBlockSize-1; x>=0; ) {
				// Unfortunately, in Java we can not cast byte[] to int[] or long[].
				// So we have to use 'or'. More than 4 'or's do not pay out.
				if (((sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--]) & 0x80) != 0) {
					// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division!
					if (sieveBlock[x+1] < 0) smoothXList.add(-(x+blockOffset1));
					if (sieveBlock[x+2] < 0) smoothXList.add(-(x+blockOffset2));
					if (sieveBlock[x+3] < 0) smoothXList.add(-(x+blockOffset3));
					if (sieveBlock[x+4] < 0) smoothXList.add(-(x+blockOffset4));
				}
			} // end for (x)
			if (profile) collectDuration += timer.capture();
		}
		return smoothXList;
	}
	
	private void sievePositiveXBlock(final int[] primesArray, final byte[] logPArray, final int B,
			                         final int r_start, final int r_medium, final int r_max) {
		int r, x, d1;
		// positive x, large primes
		for (r=r_max-1; r>=r_medium; r--) {
			x = xPosArray[r];
			final byte logP = logPArray[r];
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			if ((d1 = dPosArray[r]) != 0) { // two x-solutions
				//LOG.debug("p=" + p + ", x1=" + x + ", d1=" + d1);
				if (x<B) {
					sieveBlock[x] += logP;
					x += d1;
					final int d2 = primesArray[r] - d1;
					if (x<B) {
						sieveBlock[x] += logP;
						x += d2;
						// the difference is still correct
					} else {
						dPosArray[r] = d2;
					}
				}
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				if (x<B) {
					sieveBlock[x] += logP;
					x += primesArray[r];
				}
			} // end if (x2 == x1)
			xPosArray[r] = x-B;
		}
		// positive x, small primes
		for (; r>=r_start; r--) {
			x = xPosArray[r];
			final byte logP = logPArray[r];
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			if ((d1 = dPosArray[r]) != 0) { // two x-solutions
				//LOG.debug("p=" + p + ", x1=" + x + ", d1=" + d1);
				final int d2 = primesArray[r]-d1;
				final int M_d = B - d1;
				for ( ; x<M_d; ) {
					sieveBlock[x] += logP;
					x += d1;
					sieveBlock[x] += logP;
					x += d2;
				}
				// sieve last location
				if (x<B) {
					sieveBlock[x] += logP;
					x += d1;
					dPosArray[r] = d2;
				} // else: the difference is still correct
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				final int p = primesArray[r];
				for ( ; x<B; x+=p) {
					sieveBlock[x] += logP;
				}
			} // end if (x2 == x1)
			xPosArray[r] = x-B;
		}
	}
	
	private void sieveNegativeXBlock(final int[] primesArray, final byte[] logPArray, final int B,
			                         final int r_start, final int r_medium, final int r_max) {
		int r, x, d1;
		// negative x, large primes
		for (r=r_max-1; r>=r_medium; r--) {
			final byte logP = logPArray[r];
			x = xNegArray[r];
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			if ((d1 = dNegArray[r]) != 0) { // two x-solutions
				//LOG.debug("p=" + p + ", x1=" + x + ", d1=" + d1);
				if (x<B) {
					sieveBlock[x] += logP;
					x += d1;
					final int d2 = primesArray[r] - d1;
					if (x<B) {
						sieveBlock[x] += logP;
						x += d2;
						// the difference is still correct
					} else {
						dNegArray[r] = d2;
					}
				}
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				if (x<B) {
					sieveBlock[x] += logP;
					x += primesArray[r];
				}
			} // end if (x2 == x1)
			xNegArray[r] = x-B;
		}
		// negative x, small primes
		for (; r>=r_start; r--) {
			final byte logP = logPArray[r];
			x = xNegArray[r];
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			if ((d1 = dNegArray[r]) != 0) { // two x-solutions
				//LOG.debug("p=" + p + ", x1=" + x + ", d1=" + d1);
				final int d2 = primesArray[r]-d1;
				final int M_d = B - d1;
				for ( ; x<M_d; ) {
					sieveBlock[x] += logP;
					// d1 = (p-x2)-(p-x1) = x1-x2
					x += d1;
					sieveBlock[x] += logP;
					// d2 = p + (p-x1)-(p-x2) = p+x2-x1
					x += d2;
				}
				// sieve last locations
				if (x<B) {
					sieveBlock[x] += logP;
					x += d1;
					dNegArray[r] = d2;
				} // else: the difference is still correct
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				final int p = primesArray[r];
				for ( ; x<B; x+=p) {
					sieveBlock[x] += logP;
				}
			} // end if (x2 == x1)
			xNegArray[r] = x-B;
		}
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
		sieveArray = null;
		sieveBlock = null;
		xPosArray = null;
		xNegArray = null;
		dPosArray = null;
		dNegArray = null;
	}
}
