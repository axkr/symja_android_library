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
import de.tilman_neumann.jml.factor.base.UnsafeUtil;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;
import de.tilman_neumann.util.Timer;
import sun.misc.Unsafe;

/**
 * Single block sieve implementation, essentially following [Wambach, Wettig 1995].
 * 
 * @author Tilman Neumann
 */
public class SingleBlockSieveU implements Sieve {
//	private static final Logger LOG = Logger.getLogger(SingleBlockSieveU.class);
//	private static final boolean DEBUG = false;
	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

	// prime base
	private int filteredBaseSize;
	private int pMinIndex;

	private SolutionArrays solutionArrays;

	// sieve
	private int sieveArraySize;
	private long sieveBlockAddress;
	/** sieve block size */
	private int desiredBlockSize;
	private int effectiveBlockSize;
	/** number of complete blocks */
	private int blockCount;
	private long initializedBlockAddress;
	
	private long[] xPosArray;
	private long[] xNegArray;
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
	public SingleBlockSieveU(int blockSize) {
		this.desiredBlockSize = blockSize;
	}
	
	@Override
	public String getName() {
		return "singleBlockU(" + sieveArraySize + "/" + effectiveBlockSize + ")";
	}
	
	@Override
	public void initializeForN(SieveParams sieveParams, int mergedBaseSize, boolean profile) {
		this.pMinIndex = sieveParams.pMinIndex;
		byte initializer = sieveParams.initializer;

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

		// create initialized block
		initializedBlockAddress = UnsafeUtil.allocateMemory(effectiveBlockSize);
		UNSAFE.setMemory(initializedBlockAddress, 256, initializer);
		int filled = 256;
		int unfilled = effectiveBlockSize-filled;
		while (unfilled>0) {
			int fillNext = Math.min(unfilled, filled);
			UNSAFE.copyMemory(initializedBlockAddress, initializedBlockAddress + filled, fillNext);
			filled += fillNext;
			unfilled = effectiveBlockSize-filled;
		}

		// allocate sieve block
		sieveBlockAddress = UnsafeUtil.allocateMemory(effectiveBlockSize);

		// allocate "bookkeeping arrays" (slightly too big because before filtering)
		xPosArray = new long[mergedBaseSize];
		xNegArray = new long[mergedBaseSize];
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
		int r_s = binarySearch.getInsertPosition(powers, filteredBaseSize, effectiveBlockSize);

		final int[] x1Array = solutionArrays.x1Array;
		final int[] x2Array = solutionArrays.x2Array;
		final byte[] logPArray = solutionArrays.logPArray;
		int x1, x2;
		for (int i=pMinIndex; i<filteredBaseSize; i++) {
			x1 = x1Array[i];
			x2 = x2Array[i];
			if (x1<x2) {
				xPosArray[i] = sieveBlockAddress + x1;
				xNegArray[i] = sieveBlockAddress + powers[i] - x2;
				dNegArray[i] = dPosArray[i] = x2 - x1;
			} else {
				xPosArray[i] = sieveBlockAddress + x2;
				xNegArray[i] = sieveBlockAddress + powers[i] - x1;
				dNegArray[i] = dPosArray[i] = x1 - x2;
			}
		}
		
		List<Integer> smoothXList = new ArrayList<Integer>();
		for (int b=0; b<blockCount; b++) { // bottom-up order is required because in each block, the data for the next block is adjusted
			// positive x: initialize block
			UNSAFE.copyMemory(initializedBlockAddress, sieveBlockAddress, effectiveBlockSize);
			if (profile) initDuration += timer.capture();
			
			// positive x: sieve block [b*B, (b+1)*B] with prime index ranges 0...r_s-1 and r_s...max
			long nextBlockAddress = sieveBlockAddress + effectiveBlockSize;
			sievePositiveXBlock(powers, logPArray, effectiveBlockSize, pMinIndex, r_s, filteredBaseSize);
			if (profile) sieveDuration += timer.capture();
			
			// collect block
			// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
			final int blockOffset = b*effectiveBlockSize;
			long y0, y1;
			for (long x=nextBlockAddress; x>sieveBlockAddress; ) {
				if ((((y0 = UNSAFE.getLong(x-=8)) | (y1 = UNSAFE.getLong(x-=8))) & 0x8080808080808080L) != 0) {
					// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division
					final int relativeX = (int) (blockOffset+x-sieveBlockAddress);
					if ((y0 & 0x8080808080808080L) != 0) {
						final int y00 = (int) (y0 & 0x80808080L);
						final int y01 = (int) (y0 >> 32);
						if ((y00 &       0x80) != 0) smoothXList.add(relativeX+8);
						if ((y00 &     0x8000) != 0) smoothXList.add(relativeX+9);
						if ((y00 &   0x800000) != 0) smoothXList.add(relativeX+10);
						if ((y00 & 0x80000000) != 0) smoothXList.add(relativeX+11);
						if ((y01 &       0x80) != 0) smoothXList.add(relativeX+12);
						if ((y01 &     0x8000) != 0) smoothXList.add(relativeX+13);
						if ((y01 &   0x800000) != 0) smoothXList.add(relativeX+14);
						if ((y01 & 0x80000000) != 0) smoothXList.add(relativeX+15);
					}
					if ((y1 & 0x8080808080808080L) != 0) {
						final int y10 = (int) (y1 & 0x80808080L);
						final int y11 = (int) (y1 >> 32);
						if ((y10 &       0x80) != 0) smoothXList.add(relativeX);
						if ((y10 &     0x8000) != 0) smoothXList.add(relativeX+1);
						if ((y10 &   0x800000) != 0) smoothXList.add(relativeX+2);
						if ((y10 & 0x80000000) != 0) smoothXList.add(relativeX+3);
						if ((y11 &       0x80) != 0) smoothXList.add(relativeX+4);
						if ((y11 &     0x8000) != 0) smoothXList.add(relativeX+5);
						if ((y11 &   0x800000) != 0) smoothXList.add(relativeX+6);
						if ((y11 & 0x80000000) != 0) smoothXList.add(relativeX+7);
					}
				}
			}
			if (profile) collectDuration += timer.capture();
			
			// negative x: initialize block
			UNSAFE.copyMemory(initializedBlockAddress, sieveBlockAddress, effectiveBlockSize);
			if (profile) initDuration += timer.capture();
			
			// sieve block [b*B, (b+1)*B] with prime index ranges 0...r_s-1 and r_s...max
			sieveNegativeXBlock(powers, logPArray, effectiveBlockSize, pMinIndex, r_s, filteredBaseSize);
			if (profile) sieveDuration += timer.capture();
			
			// collect block
			for (long x=nextBlockAddress; x>sieveBlockAddress; ) {
				if ((((y0 = UNSAFE.getLong(x-=8)) | (y1 = UNSAFE.getLong(x-=8))) & 0x8080808080808080L) != 0) {
					// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division
					final int relativeX = (int) (blockOffset+x-sieveBlockAddress);
					if ((y0 & 0x8080808080808080L) != 0) {
						final int y00 = (int) (y0 & 0x80808080L);
						final int y01 = (int) (y0 >> 32);
						if ((y00 &       0x80) != 0) smoothXList.add(-(relativeX+8));
						if ((y00 &     0x8000) != 0) smoothXList.add(-(relativeX+9));
						if ((y00 &   0x800000) != 0) smoothXList.add(-(relativeX+10));
						if ((y00 & 0x80000000) != 0) smoothXList.add(-(relativeX+11));
						if ((y01 &       0x80) != 0) smoothXList.add(-(relativeX+12));
						if ((y01 &     0x8000) != 0) smoothXList.add(-(relativeX+13));
						if ((y01 &   0x800000) != 0) smoothXList.add(-(relativeX+14));
						if ((y01 & 0x80000000) != 0) smoothXList.add(-(relativeX+15));
					}
					if ((y1 & 0x8080808080808080L) != 0) {
						final int y10 = (int) (y1 & 0x80808080L);
						final int y11 = (int) (y1 >> 32);
						if ((y10 &       0x80) != 0) smoothXList.add(- relativeX   );
						if ((y10 &     0x8000) != 0) smoothXList.add(-(relativeX+1));
						if ((y10 &   0x800000) != 0) smoothXList.add(-(relativeX+2));
						if ((y10 & 0x80000000) != 0) smoothXList.add(-(relativeX+3));
						if ((y11 &       0x80) != 0) smoothXList.add(-(relativeX+4));
						if ((y11 &     0x8000) != 0) smoothXList.add(-(relativeX+5));
						if ((y11 &   0x800000) != 0) smoothXList.add(-(relativeX+6));
						if ((y11 & 0x80000000) != 0) smoothXList.add(-(relativeX+7));
					}
				}
			}
			if (profile) collectDuration += timer.capture();
		}
		return smoothXList;
	}
	
	private void sievePositiveXBlock(final int[] primesArray, final byte[] logPArray, final int B,
			                         final int r_start, final int r_medium, final int r_max) {
		int r, d1;
		long x;
		// positive x, large primes
		for (r=r_max-1; r>=r_medium; r--) {
			x = xPosArray[r];
			final byte logP = logPArray[r];
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			if ((d1 = dPosArray[r]) != 0) { // two x-solutions
				//LOG.debug("p=" + p + ", x1=" + x + ", d1=" + d1);
				// For some reasons it is faster to do the comparison with sieveBlockAddress+B instead of with
				// a precomputed nextBlockAddress = sieveBlockAddress+B. Probably a cache-access effect.
				if (x < sieveBlockAddress+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					final int d2 = primesArray[r] - d1;
					if (x < sieveBlockAddress+B) {
						UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
						x += d2;
						// the difference is still correct
					} else {
						dPosArray[r] = d2;
					}
				}
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				if (x < sieveBlockAddress+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
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
				final long M_d = sieveBlockAddress+B - d1;
				for ( ; x<M_d; ) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d2;
				}
				// sieve last location
				if (x < sieveBlockAddress+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					dPosArray[r] = d2;
				} // else: the difference is still correct
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				final int p = primesArray[r];
				for ( ; x < sieveBlockAddress+B; x+=p) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
				}
			} // end if (x2 == x1)
			xPosArray[r] = x-B;
		}
	}
	
	private void sieveNegativeXBlock(final int[] primesArray, final byte[] logPArray, final int B,
			                         final int r_start, final int r_medium, final int r_max) {
		int r, d1;
		long x;
		// negative x, large primes
		for (r=r_max-1; r>=r_medium; r--) {
			final byte logP = logPArray[r];
			x = xNegArray[r];
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			if ((d1 = dNegArray[r]) != 0) { // two x-solutions
				//LOG.debug("p=" + p + ", x1=" + x + ", d1=" + d1);
				// For some reasons it is faster to do the comparison with sieveBlockAddress+B instead of with
				// a precomputed nextBlockAddress = sieveBlockAddress+B. Probably a cache-access effect.
				if (x < sieveBlockAddress+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					final int d2 = primesArray[r] - d1;
					if (x < sieveBlockAddress+B) {
						UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
						x += d2;
						// the difference is still correct
					} else {
						dNegArray[r] = d2;
					}
				}
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				if (x < sieveBlockAddress+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
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
				final long M_d = sieveBlockAddress+B - d1;
				for ( ; x<M_d; ) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					// d1 = (p-x2)-(p-x1) = x1-x2
					x += d1;
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					// d2 = p + (p-x1)-(p-x2) = p+x2-x1
					x += d2;
				}
				// sieve last locations
				if (x < sieveBlockAddress+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					dNegArray[r] = d2;
				} // else: the difference is still correct
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				final int p = primesArray[r];
				for ( ; x < sieveBlockAddress+B; x+=p) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
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
		UnsafeUtil.freeMemory(sieveBlockAddress);
		UnsafeUtil.freeMemory(initializedBlockAddress);
		xPosArray = null;
		xNegArray = null;
		dPosArray = null;
		dNegArray = null;
	}
}
