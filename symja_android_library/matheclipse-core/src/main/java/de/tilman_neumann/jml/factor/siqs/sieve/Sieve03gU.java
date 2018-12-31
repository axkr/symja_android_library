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
 * Derivative of Sieve03g holding the sieve array in native memory.
 * 
 * Both the sieve core and the collect phase are notably faster than in Sieve03g !
 * 
 * @author Tilman Neumann
 */
public class Sieve03gU implements Sieve {
//	private static final Logger LOG = Logger.getLogger(Sieve03gU.class);
//	private static final boolean DEBUG = false;
	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

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
	/** the value to initializate the sieve array with */
	private byte initializer;
	/** base address of the sieve array holding logP sums for all x */
	private long sieveArrayAddress;

	private BinarySearch binarySearch = new BinarySearch();

	// timings
	private boolean profile;
	private Timer timer = new Timer();
	private long initDuration, sieveDuration, collectDuration;
	
	@Override
	public String getName() {
		return "sieve03gU";
	}
	
	@Override
	public void initializeForN(SieveParams sieveParams, int mergedBaseSize, boolean profile) {
		this.pMinIndex = sieveParams.pMinIndex;
		int pMax = sieveParams.pMax;
		this.initializer = sieveParams.initializer;

		// Allocate sieve array: Typically SIQS adjusts such that 2.75 * sieveArraySize ~ pMax.
		// For large primes with 0 or 1 sieve locations we need to allocate pMax+1 entries;
		// For primes p[i], i<p1Index, we need p[i]+sieveArraySize = 2*sieveArraySize entries.
		this.sieveArraySize = sieveParams.sieveArraySize;
		int sieveAllocationSize = Math.max(pMax+1, 2*sieveArraySize);
		sieveArrayAddress = UnsafeUtil.allocateMemory(sieveAllocationSize);
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
			try { // entering a try-catch-block has no time cost
				minSolutionCounts_m3[i] = sieveArraySize/powers[i] - 3;
			} catch (Exception e) {
//				LOG.error("p3Index = " + p3Index + ", pMinIndex = " + pMinIndex + ", i = " + i + ", primesArray[i] = " + powers[i]);
				throw e;
			}
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
		int i, j;
		long x1Addr, x2Addr;
		for (i=primeBaseSize-1; i>=p1Index; i--) {
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
		// Positive x, small primes:
		for ( ; i>=pMinIndex; i--) {
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
			for (j=minSolutionCounts_m3[i]; j>=0; j--) {
				x1Addr += p;
				UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
				x2Addr += p;
				UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			}
		} // end for (p)
		if (profile) sieveDuration += timer.capture();

		// collect results: we check 8 sieve locations in one long
		List<Integer> smoothXList = new ArrayList<Integer>();
		long y0, y1;
		for (long x=sieveArrayAddress+sieveArraySize; x>sieveArrayAddress; ) {
			if ((((y0 = UNSAFE.getLong(x-=8)) | (y1 = UNSAFE.getLong(x-=8))) & 0x8080808080808080L) != 0) {
				// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division
				final int relativeX = (int) (x-sieveArrayAddress);
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
		
		// re-initialize sieve array for negative x
		this.initializeSieveArray(sieveArraySize);
		if (profile) initDuration += timer.capture();

		// negative x, large primes:
		for (i=primeBaseSize-1; i>=p1Index; i--) {
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
		// negative x, small primes:
		for (; i>=pMinIndex; i--) {
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
			for (j=minSolutionCounts_m3[i]; j>=0; j--) {
				x1Addr += p;
				UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
				x2Addr += p;
				UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			}
		} // end for (p)
		if (profile) sieveDuration += timer.capture();

		// collect results
		for (long x=sieveArrayAddress+sieveArraySize; x>sieveArrayAddress; ) {
			if ((((y0 = UNSAFE.getLong(x-=8)) | (y1 = UNSAFE.getLong(x-=8))) & 0x8080808080808080L) != 0) {
				// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division
				final int relativeX = (int) (x-sieveArrayAddress);
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
		return smoothXList;
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
		minSolutionCounts_m3 = null;
		UnsafeUtil.freeMemory(sieveArrayAddress);
	}
}
