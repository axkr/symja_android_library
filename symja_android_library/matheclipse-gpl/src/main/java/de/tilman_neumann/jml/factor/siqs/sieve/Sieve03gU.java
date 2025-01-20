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
package de.tilman_neumann.jml.factor.siqs.sieve;

import static de.tilman_neumann.jml.base.BigIntConstants.I_0;
import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.*;

import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.BinarySearch;
import de.tilman_neumann.jml.factor.base.UnsafeUtil;
import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;
import de.tilman_neumann.util.Ensure;
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
	private static final Logger LOG = LogManager.getLogger(Sieve03gU.class);
	private static final boolean DEBUG = false;
	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

	private static final long LONG_MASK =   0x8080808080808080L;
	private static final long UPPER_MASK =  0x8080808000000000L;
	private static final long LOWER_MASK =          0x80808080L;

	private BigInteger daParam, bParam, cParam, kN;

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

	private SieveResult sieveResult = new SieveResult(10);

	private BinarySearch binarySearch = new BinarySearch();

	// statistics
	private long sieveHitCount;
	private Timer timer = new Timer();
	private long initDuration, sieveDuration, collectDuration;
	
	@Override
	public String getName() {
		return "sieve03gU";
	}
	
	@Override
	public void initializeForN(SieveParams sieveParams, BaseArrays baseArrays, int mergedBaseSize) {
		this.kN = sieveParams.kN;
		this.pMinIndex = sieveParams.pMinIndex;
		int pMax = sieveParams.pMax;
		this.initializer = sieveParams.initializer;

		// Allocate sieve array: Typically SIQS adjusts such that pMax/sieveArraySize = 2.5 to 5.0.
		// For large primes with 0 or 1 sieve locations we need to allocate pMax+1 entries;
		// For primes p[i], i<p1Index, we need p[i]+sieveArraySize = 2*sieveArraySize entries.
		this.sieveArraySize = sieveParams.sieveArraySize;
		int sieveAllocationSize = Math.max(pMax+1, 2*sieveArraySize);
		sieveArrayAddress = UnsafeUtil.allocateMemory(sieveAllocationSize);
		if (DEBUG) LOG.debug("pMax = " + pMax + ", sieveArraySize = " + sieveArraySize + " --> sieveAllocationSize = " + sieveAllocationSize);

		if (ANALYZE) {
			sieveHitCount = 0;
			initDuration = sieveDuration = collectDuration = 0;
		}
	}

	@Override
	public void initializeForAParameter(int d, BigInteger daParam, SolutionArrays solutionArrays, int filteredBaseSize, int[] qArray) {
		this.daParam = daParam;
		this.solutionArrays = solutionArrays;
		this.primeBaseSize = filteredBaseSize;
		
		int[] pArray = solutionArrays.pArray;
		this.p1Index = binarySearch.getInsertPosition(pArray, primeBaseSize, sieveArraySize);
		this.p2Index = binarySearch.getInsertPosition(pArray, p1Index, (sieveArraySize+1)/2);
		this.p3Index = binarySearch.getInsertPosition(pArray, p2Index, (sieveArraySize+2)/3);
		if (DEBUG) LOG.debug("primeBaseSize=" + primeBaseSize + ", p1Index=" + p1Index + ", p2Index=" + p2Index + ", p3Index=" + p3Index);
		
		// The minimum number of x-solutions in the sieve array is floor(sieveArraySize/p).
		// E.g. for p=3, sieveArraySize=8 there are solutions (0, 3, 6), (1, 4, 7), (2, 5)  <-- 8 is not in sieve array anymore
		// -> minSolutionCount = 2
		this.minSolutionCounts_m3 = new int[p3Index];
		for (int i=p3Index-1; i>=pMinIndex; i--) {
			try { // entering a try-catch-block has no time cost
				minSolutionCounts_m3[i] = sieveArraySize/pArray[i] - 3;
			} catch (Exception e) {
				LOG.error("p3Index = " + p3Index + ", pMinIndex = " + pMinIndex + ", i = " + i + ", pArray[i] = " + pArray[i]);
				throw e;
			}
			//LOG.debug("p=" + primesArray[i] + ": minSolutionCount = " + minSolutionCounts_m3[i]);
		}
	}

	@Override
	public void setBParameter(BigInteger b) {
		this.bParam = b;
		if (DEBUG) Ensure.ensureEquals(b.multiply(b).subtract(kN).mod(daParam), I_0);
		this.cParam = b.multiply(b).subtract(kN).divide(daParam);
	}

	@Override
	public Iterable<SmoothCandidate> sieve() {
		if (ANALYZE) timer.capture();
		this.initializeSieveArray(sieveArraySize);
		sieveResult.reset();
		if (ANALYZE) initDuration += timer.capture();
		
		// Sieve with positive x, large primes:
		final int[] pArray = solutionArrays.pArray;
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
			final int p = pArray[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			UNSAFE.putByte(x1Addr+p, (byte) (UNSAFE.getByte(x1Addr+p) + logP));
			UNSAFE.putByte(x2Addr+p, (byte) (UNSAFE.getByte(x2Addr+p) + logP));
		}
		for ( ; i>=p3Index; i--) {
			final int p = pArray[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			x1Addr += p;
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr += p;
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			UNSAFE.putByte(x1Addr+p, (byte) (UNSAFE.getByte(x1Addr+p) + logP));
			UNSAFE.putByte(x2Addr+p, (byte) (UNSAFE.getByte(x2Addr+p) + logP));
		}
		// Unrolling the loop with four large prime bounds looks beneficial for N>=340 bit
		
		// Positive x, small primes:
		for ( ; i>=pMinIndex; i--) {
			final int p = pArray[i];
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
		if (ANALYZE) sieveDuration += timer.capture();

		// collect results: we check 8 sieve locations in one long
		long x = sieveArrayAddress-8;
		while (x<sieveArrayAddress+sieveArraySize-8) {
			long t = UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8); 
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			if((t & LONG_MASK) == 0) continue;
			
			// back up to get the last 8 and look in more detail
			x -= 256;
			
			for(int l=0; l<32; l++) {				
				final long y = UNSAFE.getLong(x+=8);
				if((y & LONG_MASK) != 0) {
					testLongPositive(y, (int) (x-sieveArrayAddress));
				}
			}
		}
		if (ANALYZE) collectDuration += timer.capture();
		
		// re-initialize sieve array for negative x
		this.initializeSieveArray(sieveArraySize);
		if (ANALYZE) initDuration += timer.capture();

		// negative x, large primes:
		for (i=primeBaseSize-1; i>=p1Index; i--) {
			final int p = pArray[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + p - x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + p - x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
		}
		for (; i>=p2Index; i--) {
			final int p = pArray[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + p - x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + p - x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			UNSAFE.putByte(x1Addr+p, (byte) (UNSAFE.getByte(x1Addr+p) + logP));
			UNSAFE.putByte(x2Addr+p, (byte) (UNSAFE.getByte(x2Addr+p) + logP));
		}
		for (; i>=p3Index; i--) {
			final int p = pArray[i];
			final byte logP = logPArray[i];
			x1Addr = sieveArrayAddress + p - x1Array[i];
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr = sieveArrayAddress + p - x2Array[i];
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			x1Addr += p;
			UNSAFE.putByte(x1Addr, (byte) (UNSAFE.getByte(x1Addr) + logP));
			x2Addr += p;
			UNSAFE.putByte(x2Addr, (byte) (UNSAFE.getByte(x2Addr) + logP));
			UNSAFE.putByte(x1Addr+p, (byte) (UNSAFE.getByte(x1Addr+p) + logP));
			UNSAFE.putByte(x2Addr+p, (byte) (UNSAFE.getByte(x2Addr+p) + logP));
		}
		// negative x, small primes:
		for (; i>=pMinIndex; i--) {
			final int p = pArray[i];
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
		if (ANALYZE) sieveDuration += timer.capture();

		// collect results
		x = sieveArrayAddress-8;
		while (x<sieveArrayAddress+sieveArraySize-8) {
			long t = UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8); 
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			t |= UNSAFE.getLong(x+=8);
			if((t & LONG_MASK) == 0) continue;
			
			// back up to get the last 8 and look in more detail
			x -= 256;
			
			for(int l=0; l<32; l++) {
				final long y = UNSAFE.getLong(x+=8);
				if((y & LONG_MASK) != 0) {
					testLongNegative(y, (int) (x-sieveArrayAddress));
				}
			}
		}
		if (ANALYZE) collectDuration += timer.capture();
		return sieveResult;
	}
	
	/**
	 * Initialize the sieve array(s) with the initializer value computed before.
	 * @param sieveArraySize
	 */
	private void initializeSieveArray(int sieveArraySize) {
		// Overwrite existing arrays with initializer. We know that sieve array size is a multiple of 256.
		UNSAFE.setMemory(sieveArrayAddress, sieveArraySize, initializer);		
	}

	private void testLongPositive(long y, int x) {
		if ((y & LOWER_MASK) != 0) {
			final int y0 = (int) y;
			if ((y0 &       0x80) != 0) addSmoothCandidate(x  ,  y0      & 0xFF);
			if ((y0 &     0x8000) != 0) addSmoothCandidate(x+1, (y0>> 8) & 0xFF);
			if ((y0 &   0x800000) != 0) addSmoothCandidate(x+2, (y0>>16) & 0xFF);
			if ((y0 & 0x80000000) != 0) addSmoothCandidate(x+3, (y0>>24) & 0xFF);
		}
		if((y & UPPER_MASK) != 0) {
			final int y1 = (int) (y >> 32);
			if ((y1 &       0x80) != 0) addSmoothCandidate(x+4,  y1      & 0xFF);
			if ((y1 &     0x8000) != 0) addSmoothCandidate(x+5, (y1>> 8) & 0xFF);
			if ((y1 &   0x800000) != 0) addSmoothCandidate(x+6, (y1>>16) & 0xFF);
			if ((y1 & 0x80000000) != 0) addSmoothCandidate(x+7, (y1>>24) & 0xFF);
		}
	}
	
	private void testLongNegative(long y, int x) {
		if ((y & LOWER_MASK) != 0) {
			final int y0 = (int) y;
			if ((y0 &       0x80) != 0) addSmoothCandidate(- x   ,  y0      & 0xFF);
			if ((y0 &     0x8000) != 0) addSmoothCandidate(-(x+1), (y0>> 8) & 0xFF);
			if ((y0 &   0x800000) != 0) addSmoothCandidate(-(x+2), (y0>>16) & 0xFF);
			if ((y0 & 0x80000000) != 0) addSmoothCandidate(-(x+3), (y0>>24) & 0xFF);
		}
		if((y & UPPER_MASK) != 0) {
			final int y1 = (int) (y >> 32);
			if ((y1 &       0x80) != 0) addSmoothCandidate(-(x+4),  y1      & 0xFF);
			if ((y1 &     0x8000) != 0) addSmoothCandidate(-(x+5), (y1>> 8) & 0xFF);
			if ((y1 &   0x800000) != 0) addSmoothCandidate(-(x+6), (y1>>16) & 0xFF);
			if ((y1 & 0x80000000) != 0) addSmoothCandidate(-(x+7), (y1>>24) & 0xFF);
		}
	}

	private void addSmoothCandidate(int x, int score) {
		if (ANALYZE) sieveHitCount++;
		
		// Compute Q(x)/(da): If kN==1 (mod 8), then d=2 and Q(x) is divisible not just by 'a' but by 2a
		BigInteger xBig = BigInteger.valueOf(x);
		BigInteger dax = daParam.multiply(xBig);
		BigInteger A = dax.add(bParam);
		BigInteger QDivDa = dax.multiply(xBig).add(bParam.multiply(BigInteger.valueOf(x<<1))).add(cParam);
		
		SmoothCandidate smoothCandidate = sieveResult.peekNextSmoothCandidate();
		smoothCandidate.x = x;
		smoothCandidate.QRest = QDivDa;
		smoothCandidate.A = A;
		smoothCandidate.smallFactors.reset(); // this sieve does not find small factors
		sieveResult.commitNextSmoothCandidate();
	}

	@Override
	public SieveReport getReport() {
		return new SieveReport(sieveHitCount, initDuration, sieveDuration, collectDuration);
	}
	
	@Override
	public void cleanUp() {
		solutionArrays = null;
		minSolutionCounts_m3 = null;
		UnsafeUtil.freeMemory(sieveArrayAddress);
	}
}
