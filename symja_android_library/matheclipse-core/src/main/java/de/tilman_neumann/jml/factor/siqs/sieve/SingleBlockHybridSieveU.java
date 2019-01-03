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
 * Combination of a monolithic sieve for large primes > sieveArraySize/3, and a single block sieve for p <
 * sieveArraySize/3.
 * 
 * Sieving incomplete blocks makes no sense, because we have to run through all primes, but harvest only a small
 * x-range. Thus, we arrange blocks such that block size divides sieve array size.
 * 
 * @author Tilman Neumann
 */
public class SingleBlockHybridSieveU implements Sieve {
	// private static final Logger LOG = Logger.getLogger(SingleBlockHybridSieveU.class);
	// private static final boolean DEBUG = false;
	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

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
	/** the value to initializate the sieve array with */
	private byte initializer;
	/** base address of the sieve array holding logP sums for all x */
	private long sieveArrayAddress;
	private long sieveBlockAddress;
	/** sieve block size */
	private int desiredBlockSize;
	private int effectiveBlockSize;
	/** number of complete blocks */
	private int blockCount;

	/** index of largest prime fitting into a block */
	int r_s;

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
	 * 
	 * @param blockSize
	 *            size of a sieve segment
	 */
	public SingleBlockHybridSieveU(int blockSize) {
		this.desiredBlockSize = blockSize;
	}

	@Override
	public String getName() {
		return "singleHybridU(" + sieveArraySize + "/" + effectiveBlockSize + ")";
	}

	@Override
	public void initializeForN(SieveParams sieveParams, int mergedBaseSize, boolean profile) {
		this.pMinIndex = sieveParams.pMinIndex;
		int pMax = sieveParams.pMax;
		this.initializer = sieveParams.initializer;

		// Find effectiveBlockSize, blockCount and sieveArraySize such that
		// * sieveArraySize is near to sieveArraySize0
		// * effectiveBlockSize is near to desiredBlockSize
		// * c | effectiveBlockSize, where c is the number of bytes collected at once
		// * effectiveBlockSize | sieveArraySize
		int sieveArraySize0 = sieveParams.sieveArraySize;
		blockCount = BlockSieveUtil.computeBestBlockCount(sieveArraySize0, desiredBlockSize);
		int blockBase = 16 * blockCount;
		sieveArraySize = blockBase * /* floor */(sieveArraySize0 / blockBase);
		effectiveBlockSize = sieveArraySize / blockCount; // exact
		// if (DEBUG) {
		// LOG.debug("sieveArraySize0=" + sieveArraySize0 + ", desiredBlockSize=" + desiredBlockSize + " -> blockCount="
		// + blockCount + ", sieveArraySize=" + sieveArraySize + ", effectiveBlockSize=" + effectiveBlockSize);
		// assertEquals(sieveArraySize, blockCount*effectiveBlockSize);
		// }

		// Allocate sieve array: Typically SIQS adjusts such that 2.75 * sieveArraySize ~ pMax.
		// For large primes with 0 or 1 sieve locations we need to allocate pMax+1 entries;
		// For primes p[i], i<p1Index, we need p[i]+sieveArraySize = 2*sieveArraySize entries.
		int sieveAllocationSize = Math.max(pMax + 1, 2 * sieveArraySize);
		sieveArrayAddress = UnsafeUtil.allocateMemory(sieveAllocationSize);
		// if (DEBUG) LOG.debug("pMax = " + pMax + ", sieveArraySize = " + sieveArraySize + " --> sieveAllocationSize =
		// " + sieveAllocationSize);
		sieveBlockAddress = UnsafeUtil.allocateMemory(effectiveBlockSize);

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
		p2Index = binarySearch.getInsertPosition(powers, p1Index, (sieveArraySize + 1) / 2);
		p3Index = binarySearch.getInsertPosition(powers, p2Index, (sieveArraySize + 2) / 3);
		// find the index of the largest prime < p3 fitting into a block
		r_s = binarySearch.getInsertPosition(powers, p3Index, effectiveBlockSize);
		// if (DEBUG) LOG.debug("primeBaseSize=" + primeBaseSize + ", p1Index=" + p1Index + ", p2Index=" + p2Index + ",
		// p3Index=" + p3Index + ", r_s = " + r_s);

		xPosArray = new long[p3Index];
		xNegArray = new long[p3Index];
		dPosArray = new int[p3Index];
		dNegArray = new int[p3Index];
	}

	@Override
	public List<Integer> sieve() {
		if (profile)
			timer.capture();
		this.initializeSieveArray(sieveArraySize);

		// prepare single-block data for smallish primes:
		// this needs to be done in sieve(), because it depends on the the x-arrays
		final int[] powers = solutionArrays.powers;
		final int[] x1Array = solutionArrays.x1Array;
		final int[] x2Array = solutionArrays.x2Array;
		int x1, x2;
		for (int i = pMinIndex; i < p3Index; i++) {
			x1 = x1Array[i];
			x2 = x2Array[i];
			if (x1 < x2) {
				// From x1 < x2 follows (p-x2) < (p-x1)
				xPosArray[i] = sieveBlockAddress + x1;
				xNegArray[i] = sieveBlockAddress + powers[i] - x2;
				dNegArray[i] = dPosArray[i] = x2 - x1;
			} else {
				xPosArray[i] = sieveBlockAddress + x2;
				xNegArray[i] = sieveBlockAddress + powers[i] - x1;
				dNegArray[i] = dPosArray[i] = x1 - x2;
			}
		}
		if (profile)
			initDuration += timer.capture();

		// Sieve with positive x, large primes:
		List<Integer> smoothXList = new ArrayList<Integer>();
		final byte[] logPArray = solutionArrays.logPArray;
		int i;
		long x1Addr, x2Addr;
		for (i = primeBaseSize - 1; i >= p1Index; i--) {
			// x1 == x2 happens only if p divides k -> for large primes p > k there are always 2 distinct solutions.
			// x1, x2 may exceed sieveArraySize, but we allocated the arrays somewhat bigger to save the size checks.
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
		}
		for (; i >= p2Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			x1Addr += p;
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr += p;
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
		}
		for (; i >= p3Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			x1Addr += p;
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr += p;
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			x1Addr += p;
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr += p;
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
		}
		if (profile)
			sieveDuration += timer.capture();

		// Positive x, small primes:
		long nextBlockAddress = sieveBlockAddress + effectiveBlockSize;
		for (int b = 0; b < blockCount; b++) { // bottom-up order is required because in each block, the data for the
												// next block is adjusted
			// positive x: initialize block
			final int blockOffset = b * effectiveBlockSize;
			UNSAFE.copyMemory(sieveArrayAddress + blockOffset, sieveBlockAddress, effectiveBlockSize);
			if (profile)
				initDuration += timer.capture();

			// positive x: sieve block [b*B, (b+1)*B] with prime index ranges 0...r_s-1 and r_s...max
			// LOG.debug("sieve pos. block " + b + " (" + effectiveBlockSize + " bytes) with primes " + pMinIndex + "
			// ... " + r_s + " ... " + p3Index);
			sieveXBlock(powers, logPArray, xPosArray, dPosArray, effectiveBlockSize, pMinIndex, r_s, p3Index);
			if (profile)
				sieveDuration += timer.capture();

			// Collect block: We collect 16 bytes at once, thus we need 16 | effectiveBlockSize -> see initialize()
			long y0, y1;
			for (long x = nextBlockAddress; x > sieveBlockAddress;) {
				if ((((y0 = UNSAFE.getLong(x -= 8)) | (y1 = UNSAFE.getLong(x -= 8))) & 0x8080808080808080L) != 0) {
					// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division
					final int relativeX = (int) (blockOffset + x - sieveBlockAddress);
					if ((y0 & 0x8080808080808080L) != 0) {
						final int y00 = (int) (y0 & 0x80808080L);
						final int y01 = (int) (y0 >> 32);
						if ((y00 & 0x80) != 0)
							smoothXList.add(relativeX + 8);
						if ((y00 & 0x8000) != 0)
							smoothXList.add(relativeX + 9);
						if ((y00 & 0x800000) != 0)
							smoothXList.add(relativeX + 10);
						if ((y00 & 0x80000000) != 0)
							smoothXList.add(relativeX + 11);
						if ((y01 & 0x80) != 0)
							smoothXList.add(relativeX + 12);
						if ((y01 & 0x8000) != 0)
							smoothXList.add(relativeX + 13);
						if ((y01 & 0x800000) != 0)
							smoothXList.add(relativeX + 14);
						if ((y01 & 0x80000000) != 0)
							smoothXList.add(relativeX + 15);
					}
					if ((y1 & 0x8080808080808080L) != 0) {
						final int y10 = (int) (y1 & 0x80808080L);
						final int y11 = (int) (y1 >> 32);
						if ((y10 & 0x80) != 0)
							smoothXList.add(relativeX);
						if ((y10 & 0x8000) != 0)
							smoothXList.add(relativeX + 1);
						if ((y10 & 0x800000) != 0)
							smoothXList.add(relativeX + 2);
						if ((y10 & 0x80000000) != 0)
							smoothXList.add(relativeX + 3);
						if ((y11 & 0x80) != 0)
							smoothXList.add(relativeX + 4);
						if ((y11 & 0x8000) != 0)
							smoothXList.add(relativeX + 5);
						if ((y11 & 0x800000) != 0)
							smoothXList.add(relativeX + 6);
						if ((y11 & 0x80000000) != 0)
							smoothXList.add(relativeX + 7);
					}
				}
			}
			if (profile)
				collectDuration += timer.capture();
		}

		// re-initialize sieve array for negative x
		this.initializeSieveArray(sieveArraySize);
		if (profile)
			initDuration += timer.capture();

		// negative x, large primes:
		for (i = primeBaseSize - 1; i >= p1Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + p - x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + p - x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
		}
		for (; i >= p2Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + p - x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + p - x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			x1Addr += p;
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr += p;
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
		}
		for (; i >= p3Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + p - x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + p - x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			x1Addr += p;
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr += p;
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			x1Addr += p;
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr += p;
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
		}
		if (profile)
			sieveDuration += timer.capture();

		// negative x, small primes:
		for (int b = 0; b < blockCount; b++) { // bottom-up order is required because in each block, the data for the
												// next block is adjusted
			// negative x: initialize block
			final int blockOffset = b * effectiveBlockSize;
			UNSAFE.copyMemory(sieveArrayAddress + blockOffset, sieveBlockAddress, effectiveBlockSize);
			if (profile)
				initDuration += timer.capture();

			// negative x: sieve block [b*B, (b+1)*B] with prime index ranges 0...r_s-1 and r_s...max
			// LOG.debug("sieve neg. block " + b + " (" + effectiveBlockSize + " bytes) with primes " + pMinIndex + "
			// ... " + r_s + " ... " + p3Index);
			sieveXBlock(powers, logPArray, xNegArray, dNegArray, effectiveBlockSize, pMinIndex, r_s, p3Index);
			if (profile)
				sieveDuration += timer.capture();

			// collect block
			long y0, y1;
			for (long x = nextBlockAddress; x > sieveBlockAddress;) {
				if ((((y0 = UNSAFE.getLong(x -= 8)) | (y1 = UNSAFE.getLong(x -= 8))) & 0x8080808080808080L) != 0) {
					// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division
					final int relativeX = (int) (blockOffset + x - sieveBlockAddress);
					if ((y0 & 0x8080808080808080L) != 0) {
						final int y00 = (int) (y0 & 0x80808080L);
						final int y01 = (int) (y0 >> 32);
						if ((y00 & 0x80) != 0)
							smoothXList.add(-(relativeX + 8));
						if ((y00 & 0x8000) != 0)
							smoothXList.add(-(relativeX + 9));
						if ((y00 & 0x800000) != 0)
							smoothXList.add(-(relativeX + 10));
						if ((y00 & 0x80000000) != 0)
							smoothXList.add(-(relativeX + 11));
						if ((y01 & 0x80) != 0)
							smoothXList.add(-(relativeX + 12));
						if ((y01 & 0x8000) != 0)
							smoothXList.add(-(relativeX + 13));
						if ((y01 & 0x800000) != 0)
							smoothXList.add(-(relativeX + 14));
						if ((y01 & 0x80000000) != 0)
							smoothXList.add(-(relativeX + 15));
					}
					if ((y1 & 0x8080808080808080L) != 0) {
						final int y10 = (int) (y1 & 0x80808080L);
						final int y11 = (int) (y1 >> 32);
						if ((y10 & 0x80) != 0)
							smoothXList.add(-relativeX);
						if ((y10 & 0x8000) != 0)
							smoothXList.add(-(relativeX + 1));
						if ((y10 & 0x800000) != 0)
							smoothXList.add(-(relativeX + 2));
						if ((y10 & 0x80000000) != 0)
							smoothXList.add(-(relativeX + 3));
						if ((y11 & 0x80) != 0)
							smoothXList.add(-(relativeX + 4));
						if ((y11 & 0x8000) != 0)
							smoothXList.add(-(relativeX + 5));
						if ((y11 & 0x800000) != 0)
							smoothXList.add(-(relativeX + 6));
						if ((y11 & 0x80000000) != 0)
							smoothXList.add(-(relativeX + 7));
					}
				}
			}
			if (profile)
				collectDuration += timer.capture();
		}
		return smoothXList;
	}

	/**
	 * Sieve a single block. This method is used to sieve inner and outer blocks, positive and negative x.
	 * 
	 * @param primesArray
	 * @param logPArray
	 * @param xArray
	 * @param d1Array
	 * @param B
	 * @param r_start
	 * @param r_medium
	 * @param r_max
	 */
	private void sieveXBlock(final int[] primesArray, final byte[] logPArray, final long[] xArray, final int[] d1Array,
			final int B, final int r_start, final int r_medium, final int r_max) {
		int r, d1;
		long x;
		// negative x, large primes
		for (r = r_max - 1; r >= r_medium; r--) {
			final byte logP = logPArray[r];
			x = xArray[r];
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			if ((d1 = d1Array[r]) != 0) { // two x-solutions
				// LOG.debug("p=" + p + ", x1=" + x + ", d1=" + d1);
				// For some reasons it is faster to do the comparison with sieveBlockAddress+B instead of with
				// a precomputed nextBlockAddress = sieveBlockAddress+B. Probably a cache-access effect.
				if (x < sieveBlockAddress + B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					final int d2 = primesArray[r] - d1;
					if (x < sieveBlockAddress + B) {
						UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
						x += d2;
						// the difference is still correct
					} else {
						d1Array[r] = d2;
					}
				}
			} else {
				// only one x-solution
				// LOG.debug("p=" + p + ", x1=" + x);
				if (x < sieveBlockAddress + B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += primesArray[r];
				}
			} // end if (x2 == x1)
			xArray[r] = x - B;
		}
		// negative x, small primes
		for (; r >= r_start; r--) {
			final byte logP = logPArray[r];
			x = xArray[r];
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			if ((d1 = d1Array[r]) != 0) { // two x-solutions
				// LOG.debug("p=" + p + ", x1=" + x + ", d1=" + d1);
				final int d2 = primesArray[r] - d1;
				final long M_d = sieveBlockAddress + B - d1;
				for (; x < M_d;) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					// d1 = (p-x2)-(p-x1) = x1-x2
					x += d1;
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					// d2 = p + (p-x1)-(p-x2) = p+x2-x1
					x += d2;
				}
				// sieve last locations
				if (x < sieveBlockAddress + B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					d1Array[r] = d2;
				} // else: the difference is still correct
			} else {
				// only one x-solution
				// LOG.debug("p=" + p + ", x1=" + x);
				final int p = primesArray[r];
				for (; x < sieveBlockAddress + B; x += p) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
				}
			} // end if (x2 == x1)
			xArray[r] = x - B;
		}
	}

	/**
	 * Initialize the sieve array(s) with the initializer value computed before.
	 * 
	 * @param sieveArraySize
	 */
	private void initializeSieveArray(int sieveArraySize) {
		// Overwrite existing arrays with initializer. We know that sieve array size is a multiple of 256.
		// XXX We could use setMemory() to initialize the whole sieve array. This is indeed a bit faster,
		// but for some reason the collect phase is slowing down much more if we do that...
		UNSAFE.setMemory(sieveArrayAddress, 256, initializer);
		int filled = 256;
		int unfilled = sieveArraySize - filled;
		while (unfilled > 0) {
			int fillNext = Math.min(unfilled, filled);
			UNSAFE.copyMemory(sieveArrayAddress, sieveArrayAddress + filled, fillNext);
			filled += fillNext;
			unfilled = sieveArraySize - filled;
		}
	}

	@Override
	public SieveReport getReport() {
		return new SieveReport(initDuration, sieveDuration, collectDuration);
	}

	@Override
	public void cleanUp() {
		solutionArrays = null;
		UnsafeUtil.freeMemory(sieveArrayAddress);
		UnsafeUtil.freeMemory(sieveBlockAddress);
		xPosArray = null;
		xNegArray = null;
		dPosArray = null;
		dNegArray = null;
	}
}
