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
package de.tilman_neumann.jml.factor.siqs.poly;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.BinarySearch;

/**
 * Generator for the a-parameter (or "hypercube"), which is the leading coefficient of the quadratic polynomial
 * Q(x) = (d*a*x+b)^2 - kN used by SIQS. d is typically 1 or 2.</br></br>
 * 
 * The a-parameter in SIQS is chosen as a product of primes from the prime base: <code>a = q1 * ... * q_s</code>.
 * Its value should be roughly a ~ sqrt(2*k*N)/(d*M), where M is the sieve array size, such that |Q(x)|
 * is about the same at x=+M and x=-M, and |Q(x)| <= kN for all x.
 * 
 * One consequence of this choice is that about 70% of the Q we get are negative.
 * But trying to correct that means bigger values for positive Q and slows down the QS.
 * 
 * As suggested by [Carrier/Wagstaff], it is important to avoid q_l that divide k.
 * Otherwise for many N we would require a big number of solver runs.
 * 
 * This class does not implement the standard-procedure due to [Carrier/Wagstaff] for two reasons:
 * 1. I wanted to test which size of the q_l is actually the best. It turned out that the q_l should be rather large and qCount rather small.
 * 2. Splitting the prime base and {q_l} into three sets feels like introducing some unnecessary complexity to the code.
 * 
 * The algorithm used here is mostly random, chosing the q_l from a range of about the wanted size.
 * The last q_l is chosen deterministically such that the best a-parameter is matched as close as possible.
 * Therefore the algorithm is not stable for N < 50 bit, but faster for bigger N.
 * The expected best a-value is approximated quite accurately.
 * 
 * Since 2016-12-15, entries of qArray and qIndexArray are sorted bottom-up now.
 * Since 2018-02-13, the d-parameter was introduced.
 * 
 * A minimum value of qCount==4 gives best stability at small N, down to 53 bits.
 * 
 * @author Tilman Neumann
 */
public class AParamGenerator01 implements AParamGenerator {
//	private static final Logger LOG = Logger.getLogger(AParamGenerator01.class);
//	private static final boolean DEBUG = false;

	/** multiplier k: we must avoid q_l that divide k */
	private int k;
	/** the wanted number of factors of the a-parameter (null -> automatic choice) */
	private Integer wanted_qCount;
	// prime base
	private int primeBaseSize;
	private int[] primesArray, tArray;
	
	/** approximate size of the a-parameter for some kN */
	private double best_a;
	/** the actual number of factors of the a-parameter for given kN */
	private int qCount;
	/** centre and variance for q-index generation */
	private int indexVariance, indexCentre;
	/** indices of the prime base elements that are the factors of a */
	private int[] qtArray;
	/** and the factors themselves */
	private int[] qArray;
	/** random generator */
	private SecureRandom rng = new SecureRandom();
	/** map of a-values already used to their q-values */
	private HashMap<BigInteger, int[]> aParamHistory;

	/** The generated a-parameter */
	private BigInteger a;

	private BinarySearch binarySearch = new BinarySearch();

	/**
	 * Full constructor.
	 * @param wanted_qCount the wanted number of factors of the a-parameter; null for automatic selection
	 */
	public AParamGenerator01(Integer wanted_qCount) {
		this.wanted_qCount = wanted_qCount;
	}

	@Override
	public String getName() {
		return "apg01(" + qCount + ")";
	}

	@Override
	public void initialize(int k, BigInteger N, BigInteger kN, int d, int primeBaseSize, int[] primesArray, int[] tArray, int sieveArraySize) {
		this.k = k;
		this.primeBaseSize = primeBaseSize;
		this.primesArray = primesArray;
		this.tArray = tArray;
		
		// compute expected best a-parameter. The constant 2 has been confirmed experimentally.
		this.best_a = Math.sqrt(2*kN.doubleValue()) / (d*(double)sieveArraySize);
		// estimate best qCount if it was not given
		qCount = (wanted_qCount != null) ? wanted_qCount : estimateQCount(kN.bitLength());
		// compute average "best" q, such that best_q^qCount = best_a
		double best_q = Math.pow(best_a, 1.0/qCount);
		// compute index of average "best" q, in the sense of realizing the a-parameter with qCount prime base values
		int best_q_index = getBestIndex(best_q);
		
		// Compute centre and variance for index generator:
		// The variance multiplier plays an important role for the stability of the quadratic sieve at small N.
		// With multiplier 0.55 it works for N >= 53 bit,
		// with multiplier 0.75 it works for N >= 46 bit,
		// and with multiplier 1 it works even for N > 10 bit (with struggles, very slow then).
		// Multipliers > 1 degrade performance for big N.
		indexVariance = (int) (0.75*Math.sqrt(primeBaseSize));
		indexCentre = best_q_index;
		int minIndex = indexCentre - indexVariance; // this should be 1 at least (to avoid p[0]=2)
		if (minIndex < 1) {
			int minIndexDefect = 1-minIndex; // minIndex is that much too small
			indexCentre += minIndexDefect; // move centre up so that the defect vanishes
		}
		int maxIndex = indexCentre + indexVariance;
		if (maxIndex>=primeBaseSize) {
			int maxIndexDefect = maxIndex - (primeBaseSize-1); // maxIndex is that much too big
			indexCentre -= maxIndexDefect; // move centre down so that the defect vanishes
		}
		
		// reset aParamHistory
		aParamHistory = new HashMap<BigInteger, int[]>();
		// reset rng seed for more reliable tests
		rng.setSeed(0x1234567890abcdefL);
	}
	
	/**
	 * Estimate best number of q whose product builds the a-parameter.
	 * These are experimental values derived from tests up to 320 bit.
	 * 
	 * @param bits bitsize of kN
	 * @return estimated best q-count
	 */
	private int estimateQCount(int bits) {
		// qCount=4 is most stable for small N
		if (bits<100) return 4;
		if (bits<120) return 5;
		if (bits<140) return 6;
		if (bits<160) return 7;
		if (bits<200) return 8;
		if (bits<240) return 9;
		if (bits<290) return 10;
		if (bits<350) return 11;
		return 12;
	}
	
	/**
	 * Given qCount and best_a, compute qIndexArray, qArray and <code>a</code>.
	 * The computation should be pretty safe to avoid that QS becomes unstable for bad choices of qCount.
	 */
	private void computeAParameter() {
		TreeSet<Integer> qIndexSet = new TreeSet<Integer>();
		a = I_1;
		// find the first (qCount-1) q randomly
		for (int i=0; i<qCount-1; i++) {
			// introduce some randomness
			int randomOffset = rng.nextInt(indexVariance<<1) - indexVariance; // randomOffset = -indexVariance..+indexVariance -> centered at 0
			int wanted_qIndex = indexCentre + randomOffset;
//			if (DEBUG) LOG.debug("indexCentre=" + indexCentre + ", indexVariance=" + indexVariance + " -> randomOffset=" + randomOffset + ", wanted_qIndex=" + wanted_qIndex);
			// find the first "free" index around wanted_qIndex
			Integer qIndex = findFreeQIndex(qIndexSet, wanted_qIndex);
			// now we have found qIndex :)
			qIndexSet.add(qIndex);
			int q = primesArray[qIndex];
			BigInteger q_big = BigInteger.valueOf(q);
			a = a.multiply(q_big);
		}
		// determine the last q such that we get a good fit with best_a
		double a_rest = best_a / a.doubleValue();
		int best_q_index = getBestIndex(a_rest);
		int qIndex = findFreeQIndex(qIndexSet, best_q_index);
		qIndexSet.add(qIndex);
		int q = primesArray[qIndex];
		BigInteger q_big = BigInteger.valueOf(q);
		a = a.multiply(q_big);
		// finally, create qArray and qtArray sorted bottom-up
		qArray = new int[qCount];
		qtArray = new int[qCount];
		Iterator<Integer> qIndexIter = qIndexSet.iterator();
		for (int i=0; i<qCount; i++) {
			qIndex = qIndexIter.next().intValue();
			qArray[i] = primesArray[qIndex];
			qtArray[i] = tArray[qIndex];
		}

		//LOG.debug("kN=" + kN + " -> best_a = " + best_a + ", qCount = " + qCount + " -> qArray = " + Arrays.toString(qArray) + ", a = " + a);
	}

	private int getBestIndex(double wanted_q) {
		int q1_index = binarySearch.getInsertPosition(primesArray, primeBaseSize, (int) wanted_q);
		if (q1_index==primeBaseSize) return primeBaseSize-1; // wanted_q is bigger than pMax
		if (q1_index<2) return 1; // avoid p[0]=2
		double q1_error = Math.abs(wanted_q - primesArray[q1_index]);
		int q0_index = q1_index-1;
		double q0_error = Math.abs(wanted_q - primesArray[q0_index]);
		return (q1_error < q0_error) ? q1_index : q0_index;
	}

	private int findFreeQIndex(Set<Integer> qIndexSet, int wanted_qIndex) {
		if (!qIndexSet.contains(wanted_qIndex)) {
			if (k % primesArray[wanted_qIndex] != 0) { // avoid q_l that divide k
				return wanted_qIndex; // easy
			}
		}
		// the wanted index is not available anymore -> check the neighbors
		for (int step=1; ; step++) {
			int stepUp_qIndex = wanted_qIndex+step;
			if (stepUp_qIndex<primeBaseSize && !qIndexSet.contains(stepUp_qIndex)) {
				if (k % primesArray[stepUp_qIndex] != 0) { // avoid q_l that divide k
					return stepUp_qIndex;
				}
			}
			int stepDown_qIndex = wanted_qIndex-step;
			if (stepDown_qIndex>0 && !qIndexSet.contains(stepDown_qIndex)) { // avoid p[0]==2
				if (k % primesArray[stepDown_qIndex] != 0) { // avoid q_l that divide k
					return stepDown_qIndex;
				}
			}
		}
	}

	@Override
	public BigInteger computeNextAParameter() {
		@SuppressWarnings("unused")
		int duplicateACount = 0;
		Set<BigInteger> previousAParams = aParamHistory.keySet();
		while (true) {
			this.computeAParameter();
			if (!previousAParams.contains(a)) break; // a new "a"
			duplicateACount++;
//			if (DEBUG) LOG.warn("New a-parameter #" + aParamHistory.size() + ": a=" + a + " has already been used! #(duplicate a in a row) = " + duplicateACount);
		}
//		if (DEBUG) {
//			// Analyze disjunctness with previous a-parameters
//			int[] disjunctQCountCounts = new int[qCount]; // entry 0: #aParams with 1 disjunct q, entry 1: #aParams with 2 disjunct q, ...
//			for (int[] oldQArray : aParamHistory.values()) {
//				int sharedQCount = getSharedQCount(oldQArray, qArray);
//				int disjunctQCount = qCount - sharedQCount;
//				disjunctQCountCounts[disjunctQCount-1]++;
//			}
//			LOG.debug("Disjunct qCount counts = " + Arrays.toString(disjunctQCountCounts));
//			// Result: The q's are typically quite disjunct. At 200 bit we get something like [0, 0, 0, 0, 1, 4, 15, 78, 87].
//		}
		aParamHistory.put(a, qArray);
		return a;
	}

	private int getSharedQCount(int[] arr1, int[] arr2) {
		int i1 = 0, i2 = 0;
		int sharedCount = 0;
		while (i1<qCount && i2<qCount) {
			int cmp = arr1[i1] - arr2[i2];
			if (cmp == 0) {
				sharedCount++;
				i1++;
				i2++;
			} else if (cmp < 0) {
				// arr1[i1] is smaller
				i1++;
			} else {
				// arr2[i2] is smaller
				i2++;
			}
		}
		return sharedCount;
	}

	@Override
	public int getQCount() {
		return qCount;
	}

	@Override
	public int[] getQTArray() {
		return qtArray;
	}

	@Override
	public int[] getQArray() {
		return qArray;
	}
	
	@Override
	public void cleanUp() {
		primesArray = null;
		tArray = null;
		aParamHistory = null;
		qArray = null;
		qtArray = null;
	}
}
