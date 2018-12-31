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
 * Combination of a monolithic sieve for large primes > sieveArraySize/3,
 * and a single block sieve for p < sieveArraySize/3.
 * 
 * Sieving incomplete blocks makes no sense, because we have to run through all primes,
 * but harvest only a small x-range. Thus, we arrange blocks such that block size divides sieve array size.
 * 
 * @author Tilman Neumann
 */
public class DoubleBlockHybridSieveU implements Sieve {
//	private static final Logger LOG = Logger.getLogger(DoubleBlockHybridSieveU.class);
//	private static final boolean DEBUG = false;
	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

	// prime base
	private int solutionCount;
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
	/** sieve block sizes */
	private int B1, B2;
	/** number of blocks */
	private int k1, k2;
	private int effectiveB1, effectiveB2;
	private int geometricMeanOfB1AndB2;
	int r_l, r_m, r_s;
	
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
	 * @param B1 inner block size, e.g. 32kB; B1 should divide B2
	 * @param B2 outer block size
	 */
	public DoubleBlockHybridSieveU(int B1, int B2) {
		this.B1 = B1;
		this.B2 = B2;
		this.k1 = B2 / B1; // number of inner blocks per outer block
	}
	
	@Override
	public String getName() {
		return "doubleHybridU(" + sieveArraySize + "/" + effectiveB2 + "/" + effectiveB1 + ")";
	}
	
	@Override
	public void initializeForN(SieveParams sieveParams, int mergedBaseSize, boolean profile) {
		this.pMinIndex = sieveParams.pMinIndex;
		int pMax = sieveParams.pMax;
		this.initializer = sieveParams.initializer;

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
		
		// Allocate sieve array: Typically SIQS adjusts such that 2.75 * sieveArraySize ~ pMax.
		// For large primes with 0 or 1 sieve locations we need to allocate pMax+1 entries;
		// For primes p[i], i<p1Index, we need p[i]+sieveArraySize = 2*sieveArraySize entries.
		int sieveAllocationSize = Math.max(pMax+1, 2*sieveArraySize);
		sieveArrayAddress = UnsafeUtil.allocateMemory(sieveAllocationSize);
//		if (DEBUG) LOG.debug("pMax = " + pMax + ", sieveArraySize = " + sieveArraySize + " --> sieveAllocationSize = " + sieveAllocationSize);
		sieveBlockAddress = UnsafeUtil.allocateMemory(effectiveB2);

		// profiling
		this.profile = profile;
		initDuration = sieveDuration = collectDuration = 0;
	}

	@Override
	public void initializeForAParameter(SolutionArrays solutionArrays, int filteredBaseSize) {
		this.solutionArrays = solutionArrays;
		int[] powers = solutionArrays.powers;
		this.solutionCount = filteredBaseSize;
		
		p1Index = binarySearch.getInsertPosition(powers, solutionCount, sieveArraySize);
		p2Index = binarySearch.getInsertPosition(powers, p1Index, (sieveArraySize+1)/2);
		p3Index = binarySearch.getInsertPosition(powers, p2Index, (sieveArraySize+2)/3);
//		if (DEBUG) LOG.debug("primeBaseSize=" + solutionCount + ", p1Index=" + p1Index + ", p2Index=" + p2Index + ", p3Index=" + p3Index);

		r_l = binarySearch.getInsertPosition(powers, p3Index, effectiveB2);
		r_m = binarySearch.getInsertPosition(powers, r_l, geometricMeanOfB1AndB2);
		r_s = binarySearch.getInsertPosition(powers, r_m, effectiveB1);
//		if (DEBUG) LOG.debug("db: r_s = " + r_s + ", r_m = " + r_m + ", r_l = " + r_l);

		xPosArray = new long[p3Index];
		xNegArray = new long[p3Index];
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
				xPosArray[i] = sieveBlockAddress + x1;
				xNegArray[i] = sieveBlockAddress + powers[i] - x2;
				dNegArray[i] = dPosArray[i] = x2 - x1;
			} else {
				xPosArray[i] = sieveBlockAddress + x2;
				xNegArray[i] = sieveBlockAddress + powers[i] - x1;
				dNegArray[i] = dPosArray[i] = x1 - x2;
			}
		}
		if (profile) initDuration += timer.capture();

		// Sieve with positive x, large primes:
		List<Integer> smoothXList = new ArrayList<Integer>();
		final byte[] logPArray = solutionArrays.logPArray;
		int i;
		long x1Addr, x2Addr;
		for (i=solutionCount-1; i>=p1Index; i--) {
			// x1 == x2 happens only if p divides k -> for large primes p > k there are always 2 distinct solutions.
			// x1, x2 may exceed sieveArraySize, but we allocated the arrays somewhat bigger to save the size checks.
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
		}
		for ( ; i>=p2Index; i--) {
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
		for ( ; i>=p3Index; i--) {
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
		if (profile) sieveDuration += timer.capture();

		// Positive x, small primes:
		long nextB2Address = sieveBlockAddress + effectiveB2;
		for (int b2=0; b2<k2; b2++) { // bottom-up order is required because in each block, the data for the next block is adjusted
			// positive x: initialize block
			final int b2Offset = b2*effectiveB2;
			UNSAFE.copyMemory(sieveArrayAddress + b2Offset, sieveBlockAddress, effectiveB2);
			if (profile) initDuration += timer.capture();

			for (int b1=0; b1<k1; b1++) {
				// sieve inner block [b1*B1, (b1+1)*B1] with prime index ranges 0...r_s-1 and r_s...r_m
				//LOG.debug("db: b2 = " + b2 + ", b1 = " + b1);
				sievePositiveXBlock(powers, logPArray, effectiveB1, b1*effectiveB1, b1<k1-1 ? 0 : effectiveB2, pMinIndex, r_s, r_m);
			}
			// sieve outer block [b2*B2, (b2+1)*B2] with prime index ranges r_m...r_l-1 and r_l...max
			sievePositiveXBlock(powers, logPArray, effectiveB2, 0, effectiveB2, r_m, r_l, p3Index);
			if (profile) sieveDuration += timer.capture();

			// Collect block: We collect 16 bytes at once, thus we need 16 | effectiveBlockSize -> see initialize()
			long y0, y1;
			for (long x=nextB2Address; x>sieveBlockAddress; ) {
				if ((((y0 = UNSAFE.getLong(x-=8)) | (y1 = UNSAFE.getLong(x-=8))) & 0x8080808080808080L) != 0) {
					// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division
					final int relativeX = (int) (b2Offset+x-sieveBlockAddress);
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
			} // end for (x)
			if (profile) collectDuration += timer.capture();
		}
		
		// re-initialize sieve array for negative x
		this.initializeSieveArray(sieveArraySize);
		if (profile) initDuration += timer.capture();

		// negative x, large primes:
		for (i=solutionCount-1; i>=p1Index; i--) {
			final int p = powers[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + p - x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + p - x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
		}
		for (; i>=p2Index; i--) {
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
		for (; i>=p3Index; i--) {
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
		if (profile) sieveDuration += timer.capture();

		// negative x, small primes:
		for (int b2=0; b2<k2; b2++) { // bottom-up order is required because in each block, the data for the next block is adjusted
			final int b2Offset = b2*effectiveB2;
			UNSAFE.copyMemory(sieveArrayAddress + b2Offset, sieveBlockAddress, effectiveB2);
			if (profile) initDuration += timer.capture();
			
			for (int b1=0; b1<k1; b1++) {
				// sieve inner block [b1*B1, (b1+1)*B1] with prime index ranges 0...r_s-1 and r_s...r_m
				//LOG.debug("db: b2 = " + b2 + ", b1 = " + b1);
				sieveNegativeXBlock(powers, logPArray, effectiveB1, b1*effectiveB1, b1<k1-1 ? 0 : effectiveB2, pMinIndex, r_s, r_m);
			}
			// sieve outer block [b2*B2, (b2+1)*B2] with prime index ranges r_m...r_l-1 and r_l...max
			sieveNegativeXBlock(powers, logPArray, effectiveB2, 0, effectiveB2, r_m, r_l, p3Index);
			if (profile) sieveDuration += timer.capture();
			
			// collect block
			long y0, y1;
			for (long x=nextB2Address; x>sieveBlockAddress; ) {
				if ((((y0 = UNSAFE.getLong(x-=8)) | (y1 = UNSAFE.getLong(x-=8))) & 0x8080808080808080L) != 0) {
					// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division
					final int relativeX = (int) (b2Offset+x-sieveBlockAddress);
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
	 * @param xSubtrahend subtrahend for x-values required at the end of an outer block
	 * @param r_start index of the first prime to sieve with
	 * @param r_medium index of the first prime > B -> all smaller primes have at least 2 x-solutions in the block
	 * @param r_max index of the biggest prime to sieve with
	 */
	private void sievePositiveXBlock(final int[] primesArray, final byte[] logPArray,
			                         final int B, final int blockOffset, final int xSubtrahend,
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
				// For some reasons it is faster to do the comparison with sieveBlockAddress+blockOffset+B instead of with
				// a precomputed nextBlockAddress = sieveBlockAddress+blockOffset+B. Probably a cache-access effect.
				if (x < sieveBlockAddress+blockOffset+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					final int d2 = primesArray[r] - d1;
					if (x < sieveBlockAddress+blockOffset+B) {
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
				if (x < sieveBlockAddress+blockOffset+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += primesArray[r];
				}
			} // end if (x2 == x1)
			xPosArray[r] = x-xSubtrahend;
		}
		// positive x, small primes
		for (; r>=r_start; r--) {
			x = xPosArray[r];
			final byte logP = logPArray[r];
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			if ((d1 = dPosArray[r]) != 0) { // two x-solutions
				//LOG.debug("p=" + p + ", x1=" + x + ", d1=" + d1);
				final int d2 = primesArray[r]-d1;
				final long M_d = sieveBlockAddress+blockOffset+B - d1;
				for ( ; x<M_d; ) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d2;
				}
				// sieve last location
				if (x < sieveBlockAddress+blockOffset+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					dPosArray[r] = d2;
				} // else: the difference is still correct
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				final int p = primesArray[r];
				for ( ; x<sieveBlockAddress+blockOffset+B; x+=p) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
				}
			} // end if (x2 == x1)
			xPosArray[r] = x-xSubtrahend;
		}
	}
	
	/**
	 * sieve a single block for negative x. this method is used to sieve inner and outer blocks.
	 * 
	 * @param primesArray
	 * @param logPArray
	 * @param B the size of the block to sieve
	 * @param blockOffset offset of the current block in an outer block
	 * @param xSubtrahend subtrahend for x-values required at the end of an outer block
	 * @param r_start index of the first prime to sieve with
	 * @param r_medium index of the first prime > B -> all smaller primes have at least 2 x-solutions in the block
	 * @param r_max index of the biggest prime to sieve with
	 */
	private void sieveNegativeXBlock(final int[] primesArray, final byte[] logPArray,
			                         final int B, final int blockOffset, final int xSubtrahend,
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
				// For some reasons it is faster to do the comparison with sieveBlockAddress+blockOffset+B instead of with
				// a precomputed nextBlockAddress = sieveBlockAddress+blockOffset+B. Probably a cache-access effect.
				if (x < sieveBlockAddress+blockOffset+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					final int d2 = primesArray[r] - d1;
					if (x < sieveBlockAddress+blockOffset+B) {
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
				if (x < sieveBlockAddress+blockOffset+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += primesArray[r];
				}
			} // end if (x2 == x1)
			xNegArray[r] = x-xSubtrahend;
		}
		// negative x, small primes
		for (; r>=r_start; r--) {
			final byte logP = logPArray[r];
			x = xNegArray[r];
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			if ((d1 = dNegArray[r]) != 0) { // two x-solutions
				//LOG.debug("p=" + p + ", x1=" + x + ", d1=" + d1);
				final int d2 = primesArray[r]-d1;
				final long M_d = sieveBlockAddress+blockOffset+B - d1;
				for ( ; x<M_d; ) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					// d1 = (p-x2)-(p-x1) = x1-x2
					x += d1;
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					// d2 = p + (p-x1)-(p-x2) = p+x2-x1
					x += d2;
				}
				// sieve last locations
				if (x < sieveBlockAddress+blockOffset+B) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
					x += d1;
					dNegArray[r] = d2;
				} // else: the difference is still correct
			} else {
				// only one x-solution
				//LOG.debug("p=" + p + ", x1=" + x);
				final int p = primesArray[r];
				for ( ; x<sieveBlockAddress+blockOffset+B; x+=p) {
					UNSAFE.putByte(x, (byte) (UNSAFE.getByte(x) + logP));
				}
			} // end if (x2 == x1)
			xNegArray[r] = x-xSubtrahend;
		}
	}

	/**
	 * Initialize the sieve array(s) with the initializer value computed before.
	 * @param sieveArraySize
	 */
	private void initializeSieveArray(int sieveArraySize) {
		// Overwrite existing arrays with initializer. We know that sieve array size is a multiple of 256.
		// XXX We could use setMemory() to initialize the whole sieve array. This is indeed a bit faster,
		//     but for some reason the collect phase is slowing down much more if we do that...
		UNSAFE.setMemory(sieveArrayAddress, 256, initializer);
		int filled = 256;
		int unfilled = sieveArraySize-filled;
		while (unfilled>0) {
			int fillNext = Math.min(unfilled, filled);
			UNSAFE.copyMemory(sieveArrayAddress, sieveArrayAddress + filled, fillNext);
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
		UnsafeUtil.freeMemory(sieveArrayAddress);
		UnsafeUtil.freeMemory(sieveBlockAddress);
		xPosArray = null;
		xNegArray = null;
		dPosArray = null;
		dNegArray = null;
	}
}
