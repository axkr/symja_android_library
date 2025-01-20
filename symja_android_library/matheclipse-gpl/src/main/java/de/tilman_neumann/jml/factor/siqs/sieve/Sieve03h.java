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
import de.tilman_neumann.jml.base.UnsignedBigInt;
import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;
import de.tilman_neumann.util.Ensure;
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
 * Version 03h:
 * -> unroll largest primes with the same logP value
 * 
 * @author Tilman Neumann
 */
public class Sieve03h implements Sieve {
	private static final Logger LOG = LogManager.getLogger(Sieve03h.class);
	private static final boolean DEBUG = false;

	private static final double LN2 = Math.log(2.0);

	private BigInteger daParam, bParam, cParam, kN;
	private int d;

	/** multiplier to convert natural logarithms to the scaled log that yields a sieve hit if the sieve array entry x >= 128 */
	private double ln2logPMultiplier;
	/** multiplier to convert dual logarithms (e.g. bit length) to the scaled log that yields a sieve hit if the sieve array entry x >= 128 */
	private double ld2logPMultiplier;
	
	private int tdivTestMinLogPSum;
	private int logQdivDaEstimate;
	
	// prime base
	private int primeBaseSize;
	/** we do not sieve with primes p_i, i<pMinIndex */
	private int pMinIndex;
	/** p_i with i>p1Index have at most 1 solution in the sieve array for each of x1, x2 */
	private int p1Index;
	private int p2Index;
	private int p3Index;
	private int[] minSolutionCounts_m3;
	private double[] smallPrimesLogPArray;
	private int[] logPBounds;
	private int logPBoundCount;
	private byte maxLogP;
	
	private SolutionArrays solutionArrays;

	private int[] qArray;
	private double[] logQArray;
	
	// sieve
	private int sieveArraySize;
	/** the initalizer value */
	private byte initializerValue;
	/** basic building block for fast initialization of sieve array */
	private byte[] initializerBlock;
	/** the array holding logP sums for all x */
	private byte[] sieveArray;

	/** buffers for trial division engine. */
	private UnsignedBigInt Q_rest_UBI = new UnsignedBigInt(new int[50]);
	private UnsignedBigInt quotient_UBI = new UnsignedBigInt(new int[50]);
	private SieveResult sieveResult = new SieveResult(10);

	/** the primes found to divide Q in pass 1 */
	private int[] pass2Primes = new int[100];
	private int[] pass2Powers = new int[100];
	private int[] pass2Exponents = new int[100];
	private double[] pass2LogPArray = new double[100];
	
	private BinarySearch binarySearch = new BinarySearch();

	// statistics
	private long sieveHitCount;
	private Timer timer = new Timer();
	private long initDuration, sieveDuration, collectDuration;
	
	@Override
	public String getName() {
		return "sieve03h";
	}
	
	@Override
	public void initializeForN(SieveParams sieveParams, BaseArrays baseArrays, int mergedBaseSize) {
		this.kN = sieveParams.kN;
		this.pMinIndex = sieveParams.pMinIndex;
		this.ln2logPMultiplier = sieveParams.lnPMultiplier;
		this.ld2logPMultiplier = sieveParams.lnPMultiplier * LN2;
		this.tdivTestMinLogPSum = sieveParams.tdivTestMinLogPSum;
		this.logQdivDaEstimate = sieveParams.logQdivDaEstimate;
		initializerValue = sieveParams.initializer;
		initializerBlock = sieveParams.getInitializerBlock();
		this.maxLogP = baseArrays.logPArray[mergedBaseSize-1];
		
		int[] primes = baseArrays.primes;
		this.smallPrimesLogPArray = new double[pMinIndex];
		for (int i=pMinIndex-1; i>=0; i--) {
			smallPrimesLogPArray[i] = Math.log(primes[i]) * sieveParams.lnPMultiplier;
		}
		
		// Allocate sieve array: Typically SIQS adjusts such that pMax/sieveArraySize = 2.5 to 5.0.
		// For large primes with 0 or 1 sieve locations we need to allocate pMax+1 entries;
		// For primes p[i], i<p1Index, we need p[i]+sieveArraySize = 2*sieveArraySize entries.
		this.sieveArraySize = sieveParams.sieveArraySize;
		int pMax = sieveParams.pMax;
		int sieveAllocationSize = Math.max(pMax+1, 2*sieveArraySize);
		sieveArray = new byte[sieveAllocationSize];
		if (DEBUG) LOG.debug("pMax = " + pMax + ", sieveArraySize = " + sieveArraySize + " --> sieveAllocationSize = " + sieveAllocationSize);

		if (ANALYZE) {
			sieveHitCount = 0;
			initDuration = sieveDuration = collectDuration = 0;
		}
	}

	@Override
	public void initializeForAParameter(int d, BigInteger daParam, SolutionArrays solutionArrays, int filteredBaseSize, int[] qArray) {
		this.d = d;
		this.daParam = daParam;
		this.solutionArrays = solutionArrays;
		this.primeBaseSize = filteredBaseSize;
		this.qArray = qArray;
		
		// compute scaled log-values for the q-parameters
		logQArray = new double[qArray.length];
		for (int i=0; i<qArray.length; i++) {
			logQArray[i] =  Math.log(qArray[i]) * ln2logPMultiplier;
		}
		
		int[] pArray = solutionArrays.pArray;
		this.p1Index = binarySearch.getInsertPosition(pArray, primeBaseSize, sieveArraySize);
		this.p2Index = binarySearch.getInsertPosition(pArray, p1Index, (sieveArraySize+1)/2);
		this.p3Index = binarySearch.getInsertPosition(pArray, p2Index, (sieveArraySize+2)/3);
		if (DEBUG) LOG.debug("primeBaseSize=" + primeBaseSize + ", p1Index=" + p1Index + ", p2Index=" + p2Index + ", p3Index=" + p3Index);
		
		// compute indices i where logPArray[i] == 1 + logPArray[i-1]; exception being logPBounds[0] which must be p1Index
		byte[] logPArray = solutionArrays.logPArray;
		int logPMax = logPArray[filteredBaseSize-1] & 0xFF;
		int logPAtP1 = logPArray[p1Index-1] & 0xFF;
		if (DEBUG) LOG.debug("logPMax = " + logPMax + ", logPAtP1 = " + logPAtP1);
		logPBoundCount = logPMax - logPAtP1 + 1;
		logPBounds = new int[logPBoundCount];
		logPBounds[logPBoundCount-1] = filteredBaseSize;
		if (DEBUG) LOG.debug("filteredBaseSize = " + filteredBaseSize);
		int logP = logPMax;
		int lastBound = filteredBaseSize;
		for (int i=logPBoundCount-1; i>0; i--) {
			lastBound = logPBounds[i] = binarySearch.getInsertPosition(logPArray, lastBound, --logP);
			if (DEBUG) LOG.debug("logPBound[" + i + "] = " + logPBounds[i] + ", logP[" + logPBounds[i] + "] = " + logPArray[logPBounds[i]] + ", logP[" + (logPBounds[i]-1) + "] = " + logPArray[logPBounds[i]-1]);
		}
		logPBounds[0] = p1Index;
		if (DEBUG) LOG.debug("logPBound[0] = " + p1Index);

		// The minimum number of x-solutions in the sieve array is floor(sieveArraySize/p).
		// E.g. for p=3, sieveArraySize=8 there are solutions (0, 3, 6), (1, 4, 7), (2, 5)  <-- 8 is not in sieve array anymore
		// -> minSolutionCount = 2
		this.minSolutionCounts_m3 = new int[p3Index];
		for (int i=p3Index-1; i>=pMinIndex; i--) {
			minSolutionCounts_m3[i] = sieveArraySize/pArray[i] - 3;
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
		int x1, x2, j;
		
		// for large primes we don't need to access the logPArray at all
		byte bigLogP = maxLogP;
		
		int i = primeBaseSize-1;
		for (int lpbc=logPBoundCount-1; lpbc>=0; lpbc--, bigLogP--) {
			int logPBound = logPBounds[lpbc];
			for (; i>=logPBound; i--) {
				// x1 == x2 happens only if p divides k -> for large primes p > k there are always 2 distinct solutions.
				// x1, x2 may exceed sieveArraySize, but we allocated the arrays somewhat bigger to save the size checks.
				sieveArray[x1Array[i]] += bigLogP;
				sieveArray[x2Array[i]] += bigLogP;
			}
		}
		for ( ; i>=p2Index; i--) {
			final int p = pArray[i];
			final byte logP = logPArray[i];
			x1 = x1Array[i];
			x2 = x2Array[i];
			sieveArray[x1] += logP;
			sieveArray[x2] += logP;
			sieveArray[x1+p] += logP;
			sieveArray[x2+p] += logP;
		}
		for ( ; i>=p3Index; i--) {
			final int p = pArray[i];
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
			final int p = pArray[i];
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
		if (ANALYZE) sieveDuration += timer.capture();

		// collect results
		// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
		for (int x=sieveArraySize-1; x>=0; ) {
			// Unfortunately, in Java we can not cast byte[] to int[] or long[].
			// So we have to use 'or'. More than 4 'or's do not pay out.
			if (((sieveArray[x--] | sieveArray[x--] | sieveArray[x--] | sieveArray[x--]) & 0x80) != 0) {
				// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division!
				if (sieveArray[x+1] < 0) addSmoothCandidate(x+1, sieveArray[x+1] & 0xFF);
				if (sieveArray[x+2] < 0) addSmoothCandidate(x+2, sieveArray[x+2] & 0xFF);
				if (sieveArray[x+3] < 0) addSmoothCandidate(x+3, sieveArray[x+3] & 0xFF);
				if (sieveArray[x+4] < 0) addSmoothCandidate(x+4, sieveArray[x+4] & 0xFF);
			}
		}
		if (ANALYZE) collectDuration += timer.capture();
		
		// re-initialize sieve array for negative x
		this.initializeSieveArray(sieveArraySize);
		if (ANALYZE) initDuration += timer.capture();

		// negative x, large primes:
		bigLogP = maxLogP;
		i = primeBaseSize-1;
		for (int lpbc=logPBoundCount-1; lpbc>=0; lpbc--, bigLogP--) {
			int logPBound = logPBounds[lpbc];
			for (; i>=logPBound; i--) {
				final int p = pArray[i];
				sieveArray[p-x1Array[i]] += bigLogP;
				sieveArray[p-x2Array[i]] += bigLogP;
			}
		}
		for (; i>=p2Index; i--) {
			final int p = pArray[i];
			final byte logP = logPArray[i];
			x1 = p-x1Array[i];
			x2 = p-x2Array[i];
			sieveArray[x1] += logP;
			sieveArray[x2] += logP;
			sieveArray[x1+p] += logP;
			sieveArray[x2+p] += logP;
		}
		for (; i>=p3Index; i--) {
			final int p = pArray[i];
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
			final int p = pArray[i];
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
		if (ANALYZE) sieveDuration += timer.capture();

		// collect results
		// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
		for (int x=sieveArraySize-1; x>=0; ) {
			// Unfortunately, in Java we can not cast byte[] to int[] or long[].
			// So we have to use 'or'. More than 4 'or's do not pay out.
			if (((sieveArray[x--] | sieveArray[x--] | sieveArray[x--] | sieveArray[x--]) & 0x80) != 0) {
				// at least one of the tested Q(-x) is sufficiently smooth to be passed to trial division!
				if (sieveArray[x+1] < 0) addSmoothCandidate(-(x+1), sieveArray[x+1] & 0xFF);
				if (sieveArray[x+2] < 0) addSmoothCandidate(-(x+2), sieveArray[x+2] & 0xFF);
				if (sieveArray[x+3] < 0) addSmoothCandidate(-(x+3), sieveArray[x+3] & 0xFF);
				if (sieveArray[x+4] < 0) addSmoothCandidate(-(x+4), sieveArray[x+4] & 0xFF);
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
		// overwrite existing arrays with initializer. we know that sieve array size is a multiple of 256
		System.arraycopy(initializerBlock, 0, sieveArray, 0, 256);
		int filled = 256;
		int unfilled = sieveArraySize-filled;
		while (unfilled>0) {
			int fillNext = Math.min(unfilled, filled);
			System.arraycopy(sieveArray, 0, sieveArray, filled, fillNext);
			filled += fillNext;
			unfilled = sieveArraySize-filled;
		}
	}

	private void addSmoothCandidate(int x, int score) {
		if (ANALYZE) sieveHitCount++;
		
		// Compute Q(x)/(da): If kN==1 (mod 8), then d=2 and Q(x) is divisible not just by 'a' but by 2a
		BigInteger xBig = BigInteger.valueOf(x);
		BigInteger dax = daParam.multiply(xBig);
		BigInteger A = dax.add(bParam);
		BigInteger QDivDa = dax.multiply(xBig).add(bParam.multiply(BigInteger.valueOf(x<<1))).add(cParam);
		if (DEBUG) {
			BigInteger Q = A.multiply(A).subtract(kN); // Q(x) = A(x)^2 - kN
			Ensure.ensureEquals(Q, QDivDa.multiply(daParam));
			LOG.debug("A = " + A);
			LOG.debug("Q = " + Q);
			LOG.debug("Q/(da) = " + QDivDa);
		}
		
		// Replace estimates of unsieved prime base element (small primes, q-parameters) contributions to logPSum
		// by the true ones: The score has to rise if the true contribution is greater than expected.
		// XXX Could we do Bernsteinisms here?
		SmoothCandidate smoothCandidate = tdivUnsievedPrimeBaseElements(A, QDivDa, x);
		int logSmallPSum = (int) smoothCandidate.logPSum;
		int adjustedScore = score - ((int)initializerValue) + logSmallPSum;
		if (DEBUG) LOG.debug("adjust initializer: original score = " + score + ", initializer = " + (int)initializerValue + ", logSmallPSum = " + logSmallPSum + " -> adjustedScore1 = " + adjustedScore);
		
		// Replace estimated QDivDa size by the true one.
		// The score has to rise if the true QDivDa size is smaller than expected, because then we have less to factor.
		// We would always expect that trueLogQDivDaSize <= logQdivDaEstimate, because the latter is supposed to be an upper bound.
		// But actually we can get much bigger trueLogQDivDaSize values than expected, like trueLogQDivDaSize - logQdivDaEstimate > 18 (and in bits this is a considerably bigger number like 45)
		// This only happens for Q(x)<0. The a-parameters should not be the cause, they use to be close to the optimum.
		// One cause may be that for d=2 we need to make b-parameters odd and as a consequence they can get bigger than a.
		// But this is only part of the story; maybe the estimate of Contini, Pomerance etc. is not a true upper bound.
		// Whatever, test showed that making logQdivDaEstimate a true upper bound does not improve performance.
		int trueLogQDivDaSize = (int) (QDivDa.bitLength() * ld2logPMultiplier);
		if (DEBUG) {
			if (trueLogQDivDaSize > logQdivDaEstimate + 2) { // +2 -> don't log too much :-/
				LOG.error("d=" + d + ": logQdivDaEstimate = " + logQdivDaEstimate + ", but trueLogQDivDaSize = " + trueLogQDivDaSize);
			}
			//Ensure.ensureSmallerEquals(trueLogQDivDaSize, logQdivDaEstimate + 2); // fails sometimes
		}
		
		int adjustedScore2 = (int) (adjustedScore + this.logQdivDaEstimate - trueLogQDivDaSize);
		if (DEBUG) LOG.debug("adjust Q/a size: adjustedScore1 = " + adjustedScore + ", logQdivDaEstimate = " + logQdivDaEstimate + ", truelogQDivDaSize = " + trueLogQDivDaSize + " -> adjustedScore2 = " + adjustedScore2);

		// If we always had trueLogQDivDaSize <= logQdivDaEstimate, then this check would be useless, because the adjusted score could only rise
		if (adjustedScore2 > tdivTestMinLogPSum) {
			if (DEBUG) {
				LOG.debug("adjustedScore2 = " + adjustedScore2 + " is greater than tdivTestMinLogPSum = " + tdivTestMinLogPSum + " -> pass Q to tdiv");
				int maxAllowedQRestBits = QDivDa.bitLength() - (int) (tdivTestMinLogPSum / ld2logPMultiplier);
				int expectedQRestBits = QDivDa.bitLength() - (int) (adjustedScore2 / ld2logPMultiplier);
				LOG.debug("QDivDa = " + QDivDa.bitLength() + " bit, max allowed QRest = " + maxAllowedQRestBits + " bit, expected QRest before tdiv = " + expectedQRestBits + " bit");
			}
			smoothCandidate.x = x;
			smoothCandidate.A = A;
			sieveResult.commitNextSmoothCandidate();
		}
	}
	
	private SmoothCandidate tdivUnsievedPrimeBaseElements(BigInteger A, BigInteger QDivDa, int x) {
		SmoothCandidate smoothCandidate = sieveResult.peekNextSmoothCandidate();
		SortedIntegerArray smallFactors = smoothCandidate.smallFactors;
		smallFactors.reset();
		// For more precision, here we compute the logPSum in doubles instead of using solutionArrays.logPArray
		double logPSum = 0;
		
		// sign
		BigInteger Q_rest = QDivDa;
		if (QDivDa.signum() < 0) {
			smallFactors.add(-1);
			Q_rest = QDivDa.negate();
		}
		
		// Remove multiples of 2
		int lsb = Q_rest.getLowestSetBit();
		if (lsb > 0) {
			smallFactors.add(2, (short)lsb);
			logPSum += smallPrimesLogPArray[0] * lsb;
			Q_rest = Q_rest.shiftRight(lsb);
		}
		
		// Pass 1: Test solution arrays.
		// IMPORTANT: Java gives x % p = x for |x| < p, and we have many p bigger than any sieve array entry.
		// IMPORTANT: Not computing the modulus in these cases improves performance by almost factor 2!
		int pass2Count = 0;
		int[] pArray = solutionArrays.pArray;
		int[] primes = solutionArrays.primes;
		int[] exponents = solutionArrays.exponents;
		long[] pinvArrayL = solutionArrays.pinvArrayL;
		int[] x1Array = solutionArrays.x1Array, x2Array = solutionArrays.x2Array;
		
		final int xAbs = x<0 ? -x : x;
		for (int pIndex = pMinIndex-1; pIndex > 0; pIndex--) { // p[0]=2 was already tested
			int p = pArray[pIndex];
			int xModP;
			if (xAbs<p) {
				xModP = x<0 ? x+p : x;
			} else {
				// Compute x%p using long-valued Barrett reduction, see https://en.wikipedia.org/wiki/Barrett_reduction.
				// We can use the long-variant here because x*m will never overflow positive long values.
				final long m = pinvArrayL[pIndex];
				final long q = ((x*m)>>>32);
				xModP = (int) (x - q * p);
				if (xModP<0) xModP += p;
				else if (xModP>=p) xModP -= p;
				if (DEBUG) {
					// 0 <= xModP < p
					Ensure.ensureSmallerEquals(0, xModP);
					Ensure.ensureSmaller(xModP, p);
					
					int xModP2 = x % p;
					if (xModP2<0) xModP2 += p;
					if (xModP != xModP2) LOG.debug("x=" + x + ", p=" + p + ": xModP=" + xModP + ", but xModP2=" + xModP2);
					Ensure.ensureEquals(xModP2, xModP);
				}
			}
			if (xModP==x1Array[pIndex] || xModP==x2Array[pIndex]) {
				pass2Primes[pass2Count] = primes[pIndex];
				pass2Exponents[pass2Count] = exponents[pIndex];
				pass2LogPArray[pass2Count] = smallPrimesLogPArray[pIndex];
				pass2Powers[pass2Count++] = p;
				// for some reasons I do not understand it is faster to divide Q by p in pass 2 only, not here
			}
		}

		// Pass 2: Reduce Q by the pass2Primes and collect small factors
		Q_rest_UBI.set(Q_rest);
		for (int pass2Index = 0; pass2Index < pass2Count; pass2Index++) {
			int p = pass2Powers[pass2Index];
			while (Q_rest_UBI.divideAndRemainder(p, quotient_UBI) == 0) {
				// the division was exact. assign quotient to Q_rest and add p to factors
				UnsignedBigInt tmp = Q_rest_UBI;
				Q_rest_UBI = quotient_UBI;
				quotient_UBI = tmp;
				smallFactors.add(pass2Primes[pass2Index], (short)pass2Exponents[pass2Index]);
				logPSum += pass2LogPArray[pass2Index] * pass2Exponents[pass2Index];
				if (DEBUG) {
					BigInteger pBig = BigInteger.valueOf(p);
					BigInteger[] div = Q_rest.divideAndRemainder(pBig);
					Ensure.ensureEquals(div[1].intValue(), 0);
					Q_rest = div[0];
				}
			}
		}

		// Finally reduce Q by q-parameters
		for (int i=0; i<qArray.length; i++) {
			int p = qArray[i];
			while (Q_rest_UBI.divideAndRemainder(p, quotient_UBI) == 0) {
				// the division was exact. assign quotient to Q_rest and add p to factors
				UnsignedBigInt tmp = Q_rest_UBI;
				Q_rest_UBI = quotient_UBI;
				quotient_UBI = tmp;
				smallFactors.add(p);
				logPSum += logQArray[i];
				if (DEBUG) {
					BigInteger pBig = BigInteger.valueOf(p);
					BigInteger[] div = Q_rest.divideAndRemainder(pBig);
					Ensure.ensureEquals(div[1].intValue(), 0);
					Q_rest = div[0];
				}
			}
		}

		smoothCandidate.logPSum = logPSum;
		smoothCandidate.QRest = Q_rest_UBI.toBigInteger();
		return smoothCandidate;
	}
	
	@Override
	public SieveReport getReport() {
		return new SieveReport(sieveHitCount, initDuration, sieveDuration, collectDuration);
	}
	
	@Override
	public void cleanUp() {
		solutionArrays = null;
		minSolutionCounts_m3 = null;
		sieveArray = null;
	}
}
