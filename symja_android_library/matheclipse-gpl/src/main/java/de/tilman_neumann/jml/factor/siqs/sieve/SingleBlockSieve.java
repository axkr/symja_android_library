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
 * Single block sieve implementation, essentially following [Wambach, Wettig 1995].
 * 
 * @author Tilman Neumann
 */
public class SingleBlockSieve implements Sieve {
	private static final Logger LOG = LogManager.getLogger(SingleBlockSieve.class);
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
	private int filteredBaseSize;
	private int pMinIndex;
	private double[] smallPrimesLogPArray;

	private SolutionArrays solutionArrays;

	private int[] qArray;
	private double[] logQArray;
	
	// sieve
	private int sieveArraySize;
	private byte[] sieveBlock;
	/** sieve block size */
	private int desiredBlockSize;
	private int effectiveBlockSize;
	/** number of complete blocks */
	private int blockCount;
	private byte[] initializedBlock;

	/** the initalizer value */
	private byte initializerValue;
	/** basic building block for fast initialization of sieve array */
	private byte[] initializerBlock;
	
	private int[] xPosArray;
	private int[] xNegArray;
	private int[] dPosArray;
	private int[] dNegArray;

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

	// timings
	private long sieveHitCount;
	private Timer timer = new Timer();
	private long initDuration, sieveDuration, collectDuration;

	/**
	 * Full constructor.
	 * @param blockSize size of a sieve segment
	 */
	public SingleBlockSieve(int blockSize) {
		this.desiredBlockSize = blockSize;
	}
	
	@Override
	public String getName() {
		return "singleBlock(" + sieveArraySize + "/" + effectiveBlockSize + ")";
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
		
		int[] primes = baseArrays.primes;
		this.smallPrimesLogPArray = new double[pMinIndex];
		for (int i=pMinIndex-1; i>=0; i--) {
			smallPrimesLogPArray[i] = Math.log(primes[i]) * sieveParams.lnPMultiplier;
		}

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
		if (DEBUG) {
			LOG.debug("sieveArraySize0=" + sieveArraySize0 + ", desiredBlockSize=" + desiredBlockSize + " -> blockCount=" + blockCount + ", sieveArraySize=" + sieveArraySize + ", effectiveBlockSize=" + effectiveBlockSize);
			Ensure.ensureEquals(sieveArraySize, blockCount*effectiveBlockSize);
		}

		// create initialized block
		initializedBlock = new byte[effectiveBlockSize];
		int filled = Math.min(256, effectiveBlockSize);
		System.arraycopy(initializerBlock, 0, initializedBlock, 0, filled);
		int unfilled = effectiveBlockSize-filled;
		while (unfilled>0) {
			int fillNext = Math.min(unfilled, filled);
			System.arraycopy(initializedBlock, 0, initializedBlock, filled, fillNext);
			filled += fillNext;
			unfilled = effectiveBlockSize-filled;
		}
		
		// allocate sieve block
		sieveBlock = new byte[effectiveBlockSize];

		// allocate "bookkeeping arrays" (slightly too big because before filtering)
		xPosArray = new int[mergedBaseSize];
		xNegArray = new int[mergedBaseSize];
		dPosArray = new int[mergedBaseSize];
		dNegArray = new int[mergedBaseSize];

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
		this.filteredBaseSize = filteredBaseSize;
		this.qArray = qArray;
		
		// compute scaled log-values for the q-parameters
		logQArray = new double[qArray.length];
		for (int i=0; i<qArray.length; i++) {
			logQArray[i] =  Math.log(qArray[i]) * ln2logPMultiplier;
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
		sieveResult.reset();

		// preprocessing
		final int[] pArray = solutionArrays.pArray;
		int r_s = binarySearch.getInsertPosition(pArray, filteredBaseSize, effectiveBlockSize);

		final int[] x1Array = solutionArrays.x1Array;
		final int[] x2Array = solutionArrays.x2Array;
		final byte[] logPArray = solutionArrays.logPArray;
		int x1, x2;
		for (int i=pMinIndex; i<filteredBaseSize; i++) {
			x1 = x1Array[i];
			x2 = x2Array[i];
			if (x1<x2) {
				xPosArray[i] = x1;
				xNegArray[i] = pArray[i] - x2;
				dNegArray[i] = dPosArray[i] = x2 - x1;
			} else {
				xPosArray[i] = x2;
				xNegArray[i] = pArray[i] - x1;
				dNegArray[i] = dPosArray[i] = x1 - x2;
			}
		}
		
		for (int b=0; b<blockCount; b++) { // bottom-up order is required because in each block, the data for the next block is adjusted
			// positive x: initialize block
			System.arraycopy(initializedBlock, 0, sieveBlock, 0, effectiveBlockSize);
			if (ANALYZE) initDuration += timer.capture();
			
			// positive x: sieve block [b*B, (b+1)*B] with prime index ranges 0...r_s-1 and r_s...max
			sievePositiveXBlock(pArray, logPArray, effectiveBlockSize, pMinIndex, r_s, filteredBaseSize);
			if (ANALYZE) sieveDuration += timer.capture();
			
			// collect block
			// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
			final int blockOffset = b*effectiveBlockSize;
			final int blockOffset1 = blockOffset+1;
			final int blockOffset2 = blockOffset+2;
			final int blockOffset3 = blockOffset+3;
			final int blockOffset4 = blockOffset+4;
			for (int x=effectiveBlockSize-1; x>=0; ) {
				// Unfortunately, in Java we can not cast byte[] to int[] or long[].
				// So we have to use 'or'. More than 4 'or's do not pay out.
				if (((sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--]) & 0x80) != 0) {
					// at least one of the tested Q(x) is sufficiently smooth to be passed to trial division!
					if (sieveBlock[x+1] < 0) addSmoothCandidate(x+blockOffset1, sieveBlock[x+1] & 0xFF);
					if (sieveBlock[x+2] < 0) addSmoothCandidate(x+blockOffset2, sieveBlock[x+2] & 0xFF);
					if (sieveBlock[x+3] < 0) addSmoothCandidate(x+blockOffset3, sieveBlock[x+3] & 0xFF);
					if (sieveBlock[x+4] < 0) addSmoothCandidate(x+blockOffset4, sieveBlock[x+4] & 0xFF);
				}
			} // end for (x)
			if (ANALYZE) collectDuration += timer.capture();
			
			// negative x: initialize block
			System.arraycopy(initializedBlock, 0, sieveBlock, 0, effectiveBlockSize);
			if (ANALYZE) initDuration += timer.capture();
			
			// sieve block [b*B, (b+1)*B] with prime index ranges 0...r_s-1 and r_s...max
			sieveNegativeXBlock(pArray, logPArray, effectiveBlockSize, pMinIndex, r_s, filteredBaseSize);
			if (ANALYZE) sieveDuration += timer.capture();
			
			// collect block
			// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
			for (int x=effectiveBlockSize-1; x>=0; ) {
				// Unfortunately, in Java we can not cast byte[] to int[] or long[].
				// So we have to use 'or'. More than 4 'or's do not pay out.
				if (((sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--] | sieveBlock[x--]) & 0x80) != 0) {
					// at least one of the tested Q(-x) is sufficiently smooth to be passed to trial division!
					if (sieveBlock[x+1] < 0) addSmoothCandidate(-(x+blockOffset1), sieveBlock[x+1] & 0xFF);
					if (sieveBlock[x+2] < 0) addSmoothCandidate(-(x+blockOffset2), sieveBlock[x+2] & 0xFF);
					if (sieveBlock[x+3] < 0) addSmoothCandidate(-(x+blockOffset3), sieveBlock[x+3] & 0xFF);
					if (sieveBlock[x+4] < 0) addSmoothCandidate(-(x+blockOffset4), sieveBlock[x+4] & 0xFF);
				}
			} // end for (x)
			if (ANALYZE) collectDuration += timer.capture();
		}
		return sieveResult;
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
		sieveBlock = null;
		initializedBlock = null;
		xPosArray = null;
		xNegArray = null;
		dPosArray = null;
		dNegArray = null;
	}
}
