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
package de.tilman_neumann.jml.factor.siqs.tdiv;

import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.*;
import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matheclipse.core.numbertheory.SortedMultiset;

import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Partial_1Large;
import de.tilman_neumann.jml.factor.base.congruence.Smooth_Perfect;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;
import de.tilman_neumann.util.Timer;

/**
 * A trial division engine where partials can only have 1 large factor.
 * 
 * Division is carried out in two stages:
 * Stage 1 identifies prime factors of Q, applying long-valued Barrett reduction
 * Stage 2 does the actual division using BigInteger
 * 
 * @author Tilman Neumann
 */
public class TDiv_QS_1Large implements TDiv_QS {
	private static final Logger LOG = Logger.getLogger(TDiv_QS_1Large.class);
	private static final boolean DEBUG = false;

	// factor argument and polynomial parameters
	private BigInteger kN;
	private BigInteger da; // d*a with d = 1 or 2 depending on kN % 8
	private int d; // the d-value;
	private BigInteger bParam;
	private BigInteger cParam; // c = (b^2-kN)/(da), division is exact
	
	/** Q is sufficiently smooth if the unfactored Q_rest is smaller than this bound depending on N */
	private double maxQRest;

	// prime base
	private int[] primes;
	private int[] exponents;
	private int[] pArray;
	private long[] pinvArrayL;
	private int baseSize;
	private int[] unsievedBaseElements;
	/** the indices of the primes found to divide Q in pass 1 */
	private int[] pass2Primes = new int[100];
	private int[] pass2Powers = new int[100];
	private int[] pass2Exponents = new int[100];

	// smallest solutions of Q(x) == A(x)^2 (mod p)
	private int[] x1Array, x2Array;
	
	// small factors found by testing some x, their content is _copied_ to AQ-pairs
	private SortedIntegerArray smallFactors = new SortedIntegerArray();
	
	// statistics
	private Timer timer = new Timer();
	private long testCount, sufficientSmoothCount;
	private long aqDuration, pass1Duration, pass2Duration, factorDuration;

	@Override
	public String getName() {
		return "TDiv_1L";
	}

	@Override
	public void initializeForN(double N_dbl, BigInteger kN, double maxQRest) {
		// the biggest unfactored rest where some Q is considered smooth enough for a congruence.
		this.maxQRest = maxQRest;
		if (DEBUG) LOG.debug("maxQRest = " + maxQRest + " (" + (64 - Long.numberOfLeadingZeros((long)maxQRest)) + " bits)");
		this.kN = kN;
		// statistics
		if (ANALYZE) testCount = sufficientSmoothCount = 0;
		if (ANALYZE) aqDuration = pass1Duration = pass2Duration = factorDuration = 0;
	}

	@Override
	public void initializeForAParameter(BigInteger da, int d, BigInteger b, SolutionArrays solutionArrays, int filteredBaseSize, int[] unsievedBaseElements) {
		this.da = da;
		this.d = d;
		setBParameter(b);
		primes = solutionArrays.primes;
		exponents = solutionArrays.exponents;
		pArray = solutionArrays.pArray;
		pinvArrayL = solutionArrays.pinvArrayL;
		baseSize = filteredBaseSize;
		x1Array = solutionArrays.x1Array;
		x2Array = solutionArrays.x2Array;
		this.unsievedBaseElements = unsievedBaseElements;
	}

	@Override
	public void setBParameter(BigInteger b) {
		this.bParam = b;
//		if (DEBUG) assertTrue(b.multiply(b).subtract(kN).mod(da).equals(I_0));
		this.cParam = b.multiply(b).subtract(kN).divide(da);
	}

	@Override
	public List<AQPair> testList(List<Integer> xList) {
		if (ANALYZE) timer.capture();

		// do trial division with sieve result
		ArrayList<AQPair> aqPairs = new ArrayList<AQPair>();
		for (int x : xList) {
			smallFactors.reset();
			if (ANALYZE) testCount++;
			
			// Compute A(x) = d*a*x+b, required in the final sqrt computation; d is 1 or 2 depending on kN % 8
			BigInteger xBig = BigInteger.valueOf(x);
			BigInteger dax = da.multiply(xBig);
			BigInteger A = dax.add(bParam);
			if (ANALYZE) aqDuration += timer.capture();
			
			// Find factorization of Q(x) = A(x)^2 - kN. But the complete Q(x) is not required here,
			// using the smaller Q(x)/da = da*x^2 + 2bx + c instead speeds up tdiv pass 2. 
			// Note that test finds all factors of Q(x) nonetheless.
			// Note also that unlike in MPQS, in SIQS we cannot continue working with Q(x)/da in later stages, because da is not a square
			// and thus we could not combine relations from different a-parameters.
			BigInteger Qdiva = dax.multiply(xBig).add(bParam.multiply(BigInteger.valueOf(x<<1))).add(cParam);
			AQPair aqPair = test(A, Qdiva, x);
			if (ANALYZE) factorDuration += timer.capture();
			
			if (aqPair != null) {
				// Q(x) was found sufficiently smooth to be considered a (partial) congruence
				aqPairs.add(aqPair);
				if (ANALYZE) sufficientSmoothCount++;
//				if (DEBUG) {
//					LOG.debug("Found congruence " + aqPair);
//					BigInteger Q = A.multiply(A).subtract(kN); // Q(x) = A(x)^2 - kN
//					assertEquals(Q, Qdiva.multiply(da));
//					assertEquals(A.multiply(A).mod(kN), Q.mod(kN));
//					// make sure that the product of factors gives Q
//					SortedMultiset<Long> allQFactors = aqPair.getAllQFactors();
//					BigInteger testProduct = I_1;
//					for (Map.Entry<Long, Integer> entry : allQFactors.entrySet()) {
//						BigInteger prime = BigInteger.valueOf(entry.getKey());
//						int exponent = entry.getValue();
//						testProduct = testProduct.multiply(prime.pow(exponent));
//					}
//					assertEquals(Q, testProduct);
//				}
			}
		}
		if (ANALYZE) aqDuration += timer.capture();
		return aqPairs;
	}
	
	private AQPair test(BigInteger A, BigInteger Q, int x) {
		// sign
		BigInteger Q_rest = Q;
		if (Q.signum() < 0) {
			smallFactors.add(-1);
			Q_rest = Q.negate();
		}
		
		// Remove multiples of 2
		int lsb = Q_rest.getLowestSetBit();
		if (lsb > 0) {
			smallFactors.add(2, (short)lsb);
			Q_rest = Q_rest.shiftRight(lsb);
		}

		// Unsieved prime base elements are added directly to pass 2.
		int pass2Count = 0;
		for (; pass2Count<unsievedBaseElements.length; pass2Count++) {
			pass2Primes[pass2Count] = unsievedBaseElements[pass2Count];
			pass2Powers[pass2Count] = unsievedBaseElements[pass2Count];
			pass2Exponents[pass2Count] = 1;
		}
		
		// Pass 1: Test solution arrays.
		// IMPORTANT: Java gives x % p = x for |x| < p, and we have many p bigger than any sieve array entry.
		// IMPORTANT: Not computing the modulus in these cases improves performance by almost factor 2!
		final int xAbs = x<0 ? -x : x;
		for (int pIndex = baseSize-1; pIndex > 0; pIndex--) { // p[0]=2 was already tested
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
//				if (DEBUG) {
//					assertTrue(0<=xModP && xModP<p);
//					int xModP2 = x % p;
//					if (xModP2<0) xModP2 += p;
//					if (xModP != xModP2) LOG.debug("x=" + x + ", p=" + p + ": xModP=" + xModP + ", but xModP2=" + xModP2);
//					assertEquals(xModP2, xModP);
//				}
			}
			if (xModP==x1Array[pIndex] || xModP==x2Array[pIndex]) {
				pass2Primes[pass2Count] = primes[pIndex];
				pass2Exponents[pass2Count] = exponents[pIndex];
				pass2Powers[pass2Count++] = p;
				// for some reasons I do not understand it is faster to divide Q by p in pass 2 only, not here
			}
		}
		if (ANALYZE) pass1Duration += timer.capture();

		// Pass 2: Reduce Q by the pass2Primes and collect small factors
		BigInteger div[];
		for (int pass2Index = 0; pass2Index < pass2Count; pass2Index++) {
			BigInteger pBig = BigInteger.valueOf(pass2Powers[pass2Index]);
			while (true) {
				div = Q_rest.divideAndRemainder(pBig);
				if (div[1].compareTo(I_0)>0) break;
				smallFactors.add(pass2Primes[pass2Index], (short)pass2Exponents[pass2Index]);
				Q_rest = div[0];
			}
		}
		if (ANALYZE) pass2Duration += timer.capture();
		if (Q_rest.equals(I_1)) {
			addCommonFactorsToSmallFactors();
			return new Smooth_Perfect(A, smallFactors);
		}
		
		// Division by all p<=pMax was not sufficient to factor Q completely.
		// The remaining Q_rest is either a prime > pMax, or a composite > pMax^2.
		if (Q_rest.bitLength()>31 || Q_rest.doubleValue() >= maxQRest) return null; // Q is not sufficiently smooth
		// Note: We could as well use pMax^c with c~1.75 as threshold. Larger factors do not help to find smooth congruences.
	
		// Q is sufficiently smooth
		if (DEBUG) LOG.debug("Sufficient smooth big factor = " + Q_rest);
		addCommonFactorsToSmallFactors();
		return new Partial_1Large(A, smallFactors, Q_rest.longValue());
	}
	
	/**
	 * Add factors that all Q(x) for the same a-parameter have in common.
	 * These are the q-values whose product gives the a-parameter and 2 if d==2.
	 */
	private void addCommonFactorsToSmallFactors() {
		if (d==2) {
			smallFactors.add(2);
		}
		for (int i=0; i<unsievedBaseElements.length; i++) {
			smallFactors.add(unsievedBaseElements[i]);
		}
	}

	@Override
	public TDivReport getReport() {
		return new TDivReport(testCount, sufficientSmoothCount, aqDuration, pass1Duration, pass2Duration, 0, factorDuration, null);
	}
	
	@Override
	public void cleanUp() {
		primes = null;
		unsievedBaseElements = null;
		x1Array = null;
		x2Array = null;
	}
}
