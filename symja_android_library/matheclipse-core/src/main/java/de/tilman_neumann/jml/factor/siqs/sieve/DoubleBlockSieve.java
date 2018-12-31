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
 * Double block sieve implementation, essentially following [Wambach, Wettig 1995].
 * 
 * @author Tilman Neumann
 */
public class DoubleBlockSieve implements Sieve {
//	private static final Logger LOG = Logger.getLogger(DoubleBlockSieve.class);
//	private static final boolean DEBUG = false;

	// prime base
	private int filteredBaseSize;
	private int pMinIndex;

	private SolutionArrays solutionArrays;

	// sieve
	private int sieveArraySize;
	private byte[] sieveBlock;
	/** sieve block sizes */
	private int B1, B2;
	/** number of blocks */
	private int k1, k2;
	private int effectiveB1, effectiveB2;
	private int geometricMeanOfB1AndB2;
	private byte[] initializedBlock;
	
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
	 * @param B1 inner block size, e.g. 32kB; B1 should divide B2
	 * @param B2 outer block size
	 */
	public DoubleBlockSieve(int B1, int B2) {
		this.B1 = B1;
		this.B2 = B2;
		this.k1 = B2 / B1; // number of inner blocks per outer block
	}
	
	@Override
	public String getName() {
		return "doubleBlock(" + sieveArraySize + "/" + effectiveB2 + "/" + effectiveB1 + ")";
	}
	
	@Override
	public void initializeForN(SieveParams sieveParams, int mergedBaseSize, boolean profile) {
		this.pMinIndex = sieveParams.pMinIndex;
		byte[] initializer = sieveParams.getInitializerBlock();

		// Find effectiveB1, effectiveB2, k1, k2 and sieveArraySize such that
		// * sieveArraySize is near to sieveArraySize0, effectiveB2 is near to B2, effectiveB1 is near to B1
		// * effectiveB2 | sieveArraySize and k2 = sieveArraySize / effectiveB2
		// * effectiveB1 | effectiveB2 and k1 = effectiveB2 / effectiveB1
		// * c | effectiveB1, where c is the number of bytes collected at once
		int sieveArraySize0 = sieveParams.sieveArraySize;
		k2 = BlockSieveUtil.computeBestBlockCount(sieveArraySize0, B2);
		k1 = BlockSieveUtil.computeBestBlockCount(sieveArraySize0/k2, B1);
		int B2Base = 16*k1*k2;
		sieveArraySize = B2Base * /*floor*/(sieveArraySize0/B2Base);
		effectiveB2 = sieveArraySize / k2; // exact
		effectiveB1 = effectiveB2 / k1; // exact
		geometricMeanOfB1AndB2 = (int) Math.sqrt(effectiveB1 * (long) effectiveB2);
//		if (DEBUG) {
//			LOG.debug("sieveArraySize0=" + sieveArraySize0 + ", B2=" + B2 + ", B1=" + B1 + " -> sieveArraySize=" + sieveArraySize + ", k2=" + k2 + ", effectiveB2=" + effectiveB2 + ", k1=" + k1 + ", effectiveB1=" + effectiveB1);
//			assertEquals(sieveArraySize, k2*effectiveB2);
//			assertEquals(effectiveB2, k2*effectiveB1);
//		}

		// create initialized block
		initializedBlock = new byte[effectiveB2];
		int filled = Math.min(256, effectiveB2);
		System.arraycopy(initializer, 0, initializedBlock, 0, filled);
		int unfilled = effectiveB2-filled;
		while (unfilled>0) {
			int fillNext = Math.min(unfilled, filled);
			System.arraycopy(initializedBlock, 0, initializedBlock, filled, fillNext);
			filled += fillNext;
			unfilled = effectiveB2-filled;
		}
		
		// allocate sieve block
		sieveBlock = new byte[effectiveB2];

		// allocate "bookkeeping arrays" (slightly too big because before filtering)
		xPosArray = new int[mergedBaseSize];
		xNegArray = new int[mergedBaseSize];
		dPosArray = new int[mergedBaseSize];
		dNegArray = new int[mergedBaseSize];

		// profiling
		this.profile = profile;
		initDuration = sieveDuration = collectDuration = 0;
	}
	
	@Override
	public void initializeForAParameter(SolutionArrays solutionArrays, int filteredBaseSize) {
		this.solutionArrays = solutionArrays;
		this.filteredBaseSize = filteredBaseSize;
	}
	
	@Override
	public List<Integer> sieve() {
		if (profile) timer.capture();

		// preprocessing
		final int[] powers = solutionArrays.powers;
		final int[] x1Array = solutionArrays.x1Array;
		final int[] x2Array = solutionArrays.x2Array;
		final byte[] logPArray = solutionArrays.logPArray;
		int x1, x2;
		for (int i=pMinIndex; i<filteredBaseSize; i++) {
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

		int r_l = binarySearch.getInsertPosition(powers, filteredBaseSize, effectiveB2);
		int r_m = binarySearch.getInsertPosition(powers, r_l, geometricMeanOfB1AndB2);
		int r_s = binarySearch.getInsertPosition(powers, r_m, effectiveB1);
//		if (DEBUG) {
//			LOG.debug("db: sieveArraySize=" + sieveArraySize + ", effectiveB2=" + effectiveB2 + ", k2=" + k2 + ", effectiveB1=" + effectiveB1 + ", k1=" + k1);
//			LOG.debug("db: r_s=" + r_s + ", r_m = " + r_m + ", r_l = " + r_l);
//		}

		List<Integer> smoothXList = new ArrayList<Integer>();
		for (int b2=0; b2<k2; b2++) { // bottom-up order is required because in each block, the data for the next block is adjusted
			// positive x: initialize block
			System.arraycopy(initializedBlock, 0, sieveBlock, 0, effectiveB2);
			if (profile) initDuration += timer.capture();

			for (int b1=0; b1<k1; b1++) {
				// sieve inner block [b1*B1, (b1+1)*B1] with prime index ranges 0...r_s-1 and r_s...r_m
				//LOG.debug("db: b2 = " + b2 + ", b1 = " + b1);
				sievePositiveXBlock(powers, logPArray, effectiveB1, b1*effectiveB1, pMinIndex, r_s, r_m);
			}
			// sieve outer block [b2*B2, (b2+1)*B2] with prime index ranges r_m...r_l-1 and r_l...max
			sievePositiveXBlock(powers, logPArray, effectiveB2, 0, r_m, r_l, filteredBaseSize);
			if (profile) sieveDuration += timer.capture();

			// collect block
			// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
			final int b2Offset = b2*effectiveB2;
			final int b2Offset1 = b2Offset+1;
			final int b2Offset2 = b2Offset+2;
			final int b2Offset3 = b2Offset+3;
			final int b2Offset4 = b2Offset+4;
			for (int x=effectiveB2-1; x>=0; ) {
				// Unfortunately, in Java we can not cast byte[] to int[] or long[].
				// So we have to use 'or'. More than 4 'or's do not pay out.
				if (((sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--]) & 0x80) != 0) {
					// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division!
					if (sieveBlock[x+1] < 0) smoothXList.add(x+b2Offset1);
					if (sieveBlock[x+2] < 0) smoothXList.add(x+b2Offset2);
					if (sieveBlock[x+3] < 0) smoothXList.add(x+b2Offset3);
					if (sieveBlock[x+4] < 0) smoothXList.add(x+b2Offset4);
				}
			} // end for (x)
			if (profile) collectDuration += timer.capture();
			
			// negative x: initialize block
			System.arraycopy(initializedBlock, 0, sieveBlock, 0, effectiveB2);
			if (profile) initDuration += timer.capture();
			
			for (int b1=0; b1<k1; b1++) {
				// sieve inner block [b1*B1, (b1+1)*B1] with prime index ranges 0...r_s-1 and r_s...r_m
				//LOG.debug("db: b2 = " + b2 + ", b1 = " + b1);
				sieveNegativeXBlock(powers, logPArray, effectiveB1, b1*effectiveB1, pMinIndex, r_s, r_m);
			}
			// sieve outer block [b2*B2, (b2+1)*B2] with prime index ranges r_m...r_l-1 and r_l...max
			sieveNegativeXBlock(powers, logPArray, effectiveB2, 0, r_m, r_l, filteredBaseSize);
			if (profile) sieveDuration += timer.capture();
			
			// collect block
			// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
			for (int x=effectiveB2-1; x>=0; ) {
				// Unfortunately, in Java we can not cast byte[] to int[] or long[].
				// So we have to use 'or'. More than 4 'or's do not pay out.
				if (((sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--]) & 0x80) != 0) {
					// at least one of the tested Q(-x) is sufficiently smooth to be passed to trial division!
					if (sieveBlock[x+1] < 0) smoothXList.add(-(x+b2Offset1));
					if (sieveBlock[x+2] < 0) smoothXList.add(-(x+b2Offset2));
					if (sieveBlock[x+3] < 0) smoothXList.add(-(x+b2Offset3));
					if (sieveBlock[x+4] < 0) smoothXList.add(-(x+b2Offset4));
				}
			} // end for (x)
			if (profile) collectDuration += timer.capture();
		}
		return smoothXList;
	}
	
	
	/**
	 * sieve a single block for positive x. this method is used to sieve inner and outer blocks.
	 * 
	 * @param primesArray
	 * @param logPArray
	 * @param B the size of the block to sieve
	 * @param blockOffset offset of the current block in an outer block
	 * @param r_start index of the first prime to sieve with
	 * @param r_medium index of the first prime > B -> all smaller primes have at least 2 x-solutions in the block
	 * @param r_max index of the biggest prime to sieve with
	 */
	private void sievePositiveXBlock(final int[] primesArray, final byte[] logPArray,
			                         final int B, final int blockOffset,
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
					sieveBlock[x+blockOffset] += logP;
					x += d1;
					final int d2 = primesArray[r] - d1;
					if (x<B) {
						sieveBlock[x+blockOffset] += logP;
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
					sieveBlock[x+blockOffset] += logP;
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
					sieveBlock[x+blockOffset] += logP;
					x += d1;
					sieveBlock[x+blockOffset] += logP;
					x += d2;
				}
				// sieve last location
				if (x<B) {
					sieveBlock[x+blockOffset] += logP;
					x += d1;
					dPosArray[r] = d2;
				} // else: the difference is still correct
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				final int p = primesArray[r];
				for ( ; x<B; x+=p) {
					sieveBlock[x+blockOffset] += logP;
				}
			} // end if (x2 == x1)
			xPosArray[r] = x-B;
		}
	}
	
	/**
	 * sieve a single block for negative x. this method is used to sieve inner and outer blocks.
	 * 
	 * @param primesArray
	 * @param logPArray
	 * @param B the size of the block to sieve
	 * @param blockOffset offset of the current block in an outer block
	 * @param r_start index of the first prime to sieve with
	 * @param r_medium index of the first prime > B -> all smaller primes have at least 2 x-solutions in the block
	 * @param r_max index of the biggest prime to sieve with
	 */
	private void sieveNegativeXBlock(final int[] primesArray, final byte[] logPArray,
			                         final int B, final int blockOffset,
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
					sieveBlock[x+blockOffset] += logP;
					x += d1;
					final int d2 = primesArray[r] - d1;
					if (x<B) {
						sieveBlock[x+blockOffset] += logP;
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
					sieveBlock[x+blockOffset] += logP;
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
					sieveBlock[x+blockOffset] += logP;
					// d1 = (p-x2)-(p-x1) = x1-x2
					x += d1;
					sieveBlock[x+blockOffset] += logP;
					// d2 = p + (p-x1)-(p-x2) = p+x2-x1
					x += d2;
				}
				// sieve last locations
				if (x<B) {
					sieveBlock[x+blockOffset] += logP;
					x += d1;
					dNegArray[r] = d2;
				} // else: the difference is still correct
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				final int p = primesArray[r];
				for ( ; x<B; x+=p) {
					sieveBlock[x+blockOffset] += logP;
				}
			} // end if (x2 == x1)
			xNegArray[r] = x-B;
		}
	}
	
	@Override
	public SieveReport getReport() {
		return new SieveReport(initDuration, sieveDuration, collectDuration);
	}
	
	@Override
	public void cleanUp() {
		sieveBlock = null;
		initializedBlock = null;
		xPosArray = null;
		xNegArray = null;
		dPosArray = null;
		dNegArray = null;
	}
}
