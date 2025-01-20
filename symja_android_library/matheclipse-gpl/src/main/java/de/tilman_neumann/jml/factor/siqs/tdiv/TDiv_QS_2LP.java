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
package de.tilman_neumann.jml.factor.siqs.tdiv;

import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.*;
import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.BinarySearch;
import de.tilman_neumann.jml.base.UnsignedBigInt;
import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Partial1Large;
import de.tilman_neumann.jml.factor.base.congruence.Partial2Large;
import de.tilman_neumann.jml.factor.base.congruence.Smooth1LargeSquare;
import de.tilman_neumann.jml.factor.base.congruence.SmoothPerfect;
import de.tilman_neumann.jml.factor.ecm.TinyEcm64MHInlined;
import de.tilman_neumann.jml.factor.hart.HartFast2Mult;
import de.tilman_neumann.jml.factor.pollardRho.PollardRhoBrentMontgomery64MH;
import de.tilman_neumann.jml.factor.siqs.SIQSSmall;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;
import de.tilman_neumann.jml.factor.siqs.poly.SIQSPolyGenerator;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParams;
import de.tilman_neumann.jml.factor.siqs.sieve.SmoothCandidate;
import de.tilman_neumann.jml.primes.probable.PrPTest;
import de.tilman_neumann.util.Ensure;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.Timer;

/**
 * A trial division engine where partials can have up to 2 large factors.
 * 
 * This variant is not testing the small primes, so it needs to be run together with a sieve doing that (like Sieve_03hU).
 * 
 * @author Tilman Neumann
 */
public class TDiv_QS_2LP implements TDiv_QS {
	private static final Logger LOG = LogManager.getLogger(TDiv_QS_2LP.class);
	private static final boolean DEBUG = false;

	// factor argument and polynomial parameters
	private BigInteger kN;
	private BigInteger da; // d*a with d = 1 or 2 depending on kN % 8
	private int d; // the d-value;

	// sieve array
	private int sieveArraySize;
	
	/** Q is sufficiently smooth if the unfactored QRest is smaller than this bound depending on N */
	private double smoothBound;

	// prime base
	private int[] primes;
	private int[] exponents;
	private int[] pArray;
	private long[] pinvArrayL;
	private int baseSize;
	private int pMax;
	private long pMaxSquare;
	private int[] unsievedBaseElements;
	private int pMinIndex;
	/** p_i with i>p1Index have at most 1 solution in the sieve array for each of x1, x2 */
	private int p1Index;

	/** buffers for trial division engine. */
	private UnsignedBigInt QRest_UBI = new UnsignedBigInt(new int[50]);
	private UnsignedBigInt quotient_UBI = new UnsignedBigInt(new int[50]);

	/** the indices of the primes found to divide Q in pass 1 */
	private int[] pass2Primes = new int[100];
	private int[] pass2Powers = new int[100];
	private int[] pass2Exponents = new int[100];

	private PrPTest prpTest = new PrPTest();
	
	private HartFast2Mult hart = new HartFast2Mult(false);
	private TinyEcm64MHInlined tinyEcm = new TinyEcm64MHInlined();
	private PollardRhoBrentMontgomery64MH pollardRhoBrentMontgomery64MH = new PollardRhoBrentMontgomery64MH();
	// Nested SIQS is required for quite large N only, > 350 bit ?
	private SIQSSmall qsInternal;
	
	// smallest solutions of Q(x) == A(x)^2 (mod p)
	private int[] x1Array, x2Array;

	// small factors found by testing some x, their content is _copied_ to AQ-pairs
	private SortedIntegerArray smallFactors;
	private BigInteger smallFactorsProd; // only for debugging
	
	private BinarySearch binarySearch = new BinarySearch();

	// statistics
	private Timer timer = new Timer();
	private long testCount, sufficientSmoothCount;
	private long aqDuration, pass1Duration, pass2Duration, primeTestDuration, factorDuration;

	/**
	 * Full constructor.
	 * @param permitUnsafeUsage if true then SIQSSmall (which is used for N > 310 bit to factor Q-rests) uses a sieve exploiting sun.misc.Unsafe features.
	 */
	public TDiv_QS_2LP(boolean permitUnsafeUsage) {
		qsInternal = new SIQSSmall(0.305F, 0.37F, null, new SIQSPolyGenerator(), 10, permitUnsafeUsage);
	}

	@Override
	public String getName() {
		return "TDiv_2L_UBI_3h";
	}

	@Override
	public void initializeForN(double N_dbl, SieveParams sieveParams) {
		// the biggest unfactored rest where some Q is considered smooth enough for a congruence.
		this.smoothBound = sieveParams.smoothBound;
		if (DEBUG) LOG.debug("smoothBound = " + smoothBound + " (" + BigDecimal.valueOf(smoothBound).toBigInteger().bitLength() + " bits)");
		this.pMinIndex = sieveParams.pMinIndex;
		this.kN = sieveParams.kN;
		this.sieveArraySize = sieveParams.sieveArraySize;
		
		// statistics
		if (ANALYZE) testCount = sufficientSmoothCount = 0;
		if (ANALYZE) aqDuration = pass1Duration = pass2Duration = primeTestDuration = factorDuration = 0;
	}

	@Override
	public void initializeForAParameter(BigInteger da, int d, BigInteger b, SolutionArrays solutionArrays, int filteredBaseSize, int[] unsievedBaseElements) {
		this.da = da;
		this.d = d;
		primes = solutionArrays.primes;
		exponents = solutionArrays.exponents;
		pArray = solutionArrays.pArray;
		pinvArrayL = solutionArrays.pinvArrayL;
		baseSize = filteredBaseSize;
		x1Array = solutionArrays.x1Array;
		x2Array = solutionArrays.x2Array;
		pMax = primes[baseSize-1];
		pMaxSquare = pMax * (long) pMax;
		this.p1Index = binarySearch.getInsertPosition(pArray, baseSize, sieveArraySize);
		this.unsievedBaseElements = unsievedBaseElements;
	}

	@Override
	public List<AQPair> testList(Iterable<SmoothCandidate> smoothCandidates) {
		if (ANALYZE) timer.capture();

		// do trial division with sieve result
		ArrayList<AQPair> aqPairs = new ArrayList<AQPair>();
		for (SmoothCandidate smoothCandidate : smoothCandidates) {
			int x = smoothCandidate.x;
			BigInteger A = smoothCandidate.A;
			BigInteger QRest = smoothCandidate.QRest;
			smallFactors = smoothCandidate.smallFactors;
			if (ANALYZE) {
				testCount++;
				aqDuration += timer.capture();
			}
			if (DEBUG) {
				// compute product of initial smallFactors before we add more of them
				smallFactorsProd = I_1;
				int smallFactorsCount = smallFactors.size();
				for (int i=0; i<smallFactorsCount; i++) {
					BigInteger elem = BigInteger.valueOf(smallFactors.get(i)).pow(smallFactors.getExponent(i));
					smallFactorsProd = smallFactorsProd.multiply(elem);
				}
			}
			
			// Find factorization of Q(x) = A(x)^2 - kN. But the complete Q(x) is not required here,
			// using the smaller Q(x)/da = da*x^2 + 2bx + c instead speeds up tdiv pass 2. 
			// Note that test finds all factors of Q(x) nonetheless.
			// Note also that unlike in MPQS, in SIQS we cannot continue working with Q(x)/da in later stages, because da is not a square
			// and thus we could not combine relations from different a-parameters.
			AQPair aqPair = test(A, QRest, x);
			if (ANALYZE) factorDuration += timer.capture();
			
			if (aqPair != null) {
				// Q(x) was found sufficiently smooth to be considered a (partial) congruence
				aqPairs.add(aqPair);
				if (ANALYZE) sufficientSmoothCount++;
				if (DEBUG) {
					LOG.debug("Found congruence " + aqPair);
					BigInteger Q = A.multiply(A).subtract(kN); // Q(x) = A(x)^2 - kN
					LOG.debug("A = " + A);
					LOG.debug("Q = " + Q);
					BigInteger QDivDa = QRest.multiply(smallFactorsProd);
					LOG.debug("Q/(da) = " + QDivDa);

					Ensure.ensureEquals(Q, QDivDa.multiply(da));
					Ensure.ensureEquals(A.multiply(A).mod(kN), Q.mod(kN));
					// make sure that the product of factors gives Q
					SortedMultiset<Long> allQFactors = aqPair.getAllQFactors();
					BigInteger testProduct = I_1;
					for (Map.Entry<Long, Integer> entry : allQFactors.entrySet()) {
						BigInteger prime = BigInteger.valueOf(entry.getKey());
						int exponent = entry.getValue();
						testProduct = testProduct.multiply(prime.pow(exponent));
					}
					Ensure.ensureEquals(Q, testProduct);
				}
			}
		}
		if (ANALYZE) aqDuration += timer.capture();
		return aqPairs;
	}
	
	private AQPair test(BigInteger A, BigInteger QRest0, int x) {
		BigInteger QRest = QRest0; // keep initial QRest0 for logging below

		// Pass 1: Test solution arrays.
		// If |x| < p, then no modulus computation is required.
		// Otherwise we compute x%p using long-valued Barrett reduction, see https://en.wikipedia.org/wiki/Barrett_reduction.
		// We can use the long-variant here because x*m will never overflow positive long values.
		// For some reasons I do not understand, it is faster to divide Q by p in pass 2 only, not here.
		// IMPORTANT: Java gives x % p = x for |x| < p, and we have many p bigger than any sieve array entry.
		// IMPORTANT: Not computing the modulus in these cases improves performance by almost factor 2!
		int pass2Count = 0;
		int pIndex = baseSize-1;
		if (x < 0) {
			for ( ; pIndex >= p1Index; pIndex--) {
				// for pIndex >= p1Index, we know that |x| < sieveArraySize < p
				int xModP = x+pArray[pIndex];
				if (xModP==x1Array[pIndex] || xModP==x2Array[pIndex]) {
					pass2Primes[pass2Count] = primes[pIndex];
					pass2Exponents[pass2Count] = exponents[pIndex];
					pass2Powers[pass2Count++] = pArray[pIndex];
				}
			}
			
			final int xAbs = -x;
			for ( ; pIndex >= pMinIndex; pIndex--) { // small primes have already been tested
				int p = pArray[pIndex];
				int xModP;
				if (xAbs<p) {
					xModP = x+p;
				} else {
					final long m = pinvArrayL[pIndex];
					final long q = ((x*m)>>>32);
					xModP = (int) (x - q * p);
					if (DEBUG) Ensure.ensureSmaller(xModP, p);
					if (xModP<0) xModP += p;
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
					pass2Powers[pass2Count++] = p;
				}
			}
		} else {
			// x >= 0
			for ( ; pIndex >= p1Index; pIndex--) {
				// for pIndex > p1Index, we know that |x| < sieveArraySize < p
				if (x==x1Array[pIndex] || x==x2Array[pIndex]) {
					pass2Primes[pass2Count] = primes[pIndex];
					pass2Exponents[pass2Count] = exponents[pIndex];
					pass2Powers[pass2Count++] = pArray[pIndex];
				}
			}
			
			for ( ; pIndex >= pMinIndex; pIndex--) { // small primes have already been tested
				int p = pArray[pIndex];
				int xModP;
				if (x<p) {
					xModP = x;
				} else {
					final long m = pinvArrayL[pIndex];
					final long q = ((x*m)>>>32);
					xModP = (int) (x - q * p);
					if (DEBUG) Ensure.ensureGreaterEquals(xModP, 0);
					if (xModP>=p) xModP -= p;
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
					pass2Powers[pass2Count++] = p;
				}
			}
		}
		if (ANALYZE) pass1Duration += timer.capture();

		// Pass 2: Reduce Q by the pass2Primes and collect small factors
		QRest_UBI.set(QRest);
		for (int pass2Index = 0; pass2Index < pass2Count; pass2Index++) {
			int p = pass2Powers[pass2Index];
			while (QRest_UBI.divideAndRemainder(p, quotient_UBI) == 0) {
				// the division was exact. assign quotient to QRest and add p to factors
				UnsignedBigInt tmp = QRest_UBI;
				QRest_UBI = quotient_UBI;
				quotient_UBI = tmp;
				smallFactors.add(pass2Primes[pass2Index], (short)pass2Exponents[pass2Index]);
				if (DEBUG) {
					BigInteger pBig = BigInteger.valueOf(p);
					BigInteger[] div = QRest.divideAndRemainder(pBig);
					Ensure.ensureEquals(div[1].intValue(), 0);
					QRest = div[0];
				}
			}
		}
		if (ANALYZE) pass2Duration += timer.capture();
		if (QRest_UBI.isOne()) {
			addCommonFactorsToSmallFactors();
			return new SmoothPerfect(A, smallFactors);
		}
		QRest = QRest_UBI.toBigInteger();
		if (DEBUG) LOG.debug("true QRest after tdiv = " + QRest.bitLength() + " bit");
		
		// Division by all p<=pMax was not sufficient to factor Q completely.
		// The remaining QRest is either a prime > pMax, or a composite > pMax^2.
		double QRestDbl = QRest.doubleValue();
		if (QRestDbl >= smoothBound) return null; // Q is not sufficiently smooth
		
		if (DEBUG) LOG.debug("test(): pMax=" + pMax + " < QRest=" + QRest + " < smoothBound=" + smoothBound + " -> resolve all factors");
		// Now we consider Q as sufficiently smooth to want to find all prime factors, as long as we do not find one that is too big to be useful.
		// First we need a prime test, because factor algorithms may not return when called with a prime argument.
		boolean restIsPrime = QRestDbl < pMaxSquare || prpTest.isProbablePrime(QRest);
		if (ANALYZE) primeTestDuration += timer.capture();
		if (restIsPrime) {
			// Check that the simple prime test using pMaxSquare is correct
			if (DEBUG) Ensure.ensureTrue(prpTest.isProbablePrime(QRest));
			if (!ANALYZE_LARGE_FACTOR_SIZES) if (QRest.bitLength() > 31) return null;
			addCommonFactorsToSmallFactors();
			return new Partial1Large(A, smallFactors, QRest.longValue());
		} // else: QRest is surely not prime
		
		// Find a factor of QRest, where QRest is odd and has two+ factors, each greater than pMax.
		// This starts to happen at N >= 200 bit where we have pMax ~ 17 bit, thus QRest >= 34 bit
		// -> trial division is no help here.
		BigInteger factor1;
		int QRestBits = QRest.bitLength();
		if (QRestBits<46) {
			if (DEBUG) LOG.debug("test(): pMax^2 = " + pMaxSquare + ", QRest = " + QRest + " (" + QRestBits + " bits) not prime -> use hart");
			factor1 = hart.findSingleFactor(QRest);
		} else if (QRestBits<63) {
			if (DEBUG) LOG.debug("test(): pMax^2 = " + pMaxSquare + ", QRest = " + QRest + " (" + QRestBits + " bits) not prime -> use tinyEcm");
			factor1 = tinyEcm.findSingleFactor(QRest);
		} else if (QRestBits<64) {
			if (DEBUG) LOG.debug("test(): pMax^2 = " + pMaxSquare + ", QRest = " + QRest + " (" + QRestBits + " bits) not prime -> use pollardRhoBrentMontgomery64MH");
			factor1 = pollardRhoBrentMontgomery64MH.findSingleFactor(QRest);
		} else {
			if (DEBUG) LOG.debug("test(): pMax^2 = " + pMaxSquare + ", QRest = " + QRest + " (" + QRestBits + " bits) not prime -> use qsInternal");
			factor1 = qsInternal.findSingleFactor(QRest);
		}
		if (!ANALYZE_LARGE_FACTOR_SIZES) if (factor1.bitLength() > 31) return null;
		BigInteger factor2 = QRest.divide(factor1);
		if (!ANALYZE_LARGE_FACTOR_SIZES) if (factor2.bitLength() > 31) return null;
		
		if (DEBUG) {
			LOG.debug("test(): QRest = " + QRest + " (" + QRestBits + " bits) = " + factor1 + " * " + factor2);
			if (factor1.intValue() < pMax) {
				LOG.error("kN=" + kN + ", QRest0=" + QRest0 + ": factor1 = " + factor1 + ", but we have done tdiv until " + pMax + "?");
			}
			if (factor2.intValue() < pMax) {
				LOG.error("kN=" + kN + ", QRest0=" + QRest0 + ": factor2 = " + factor2 + ", but we have done tdiv until " + pMax + "?");
			}
		}
		
		if (factor1.equals(factor2)) {
			addCommonFactorsToSmallFactors();
			return new Smooth1LargeSquare(A, smallFactors, factor1.longValue());
		}
		addCommonFactorsToSmallFactors();
		return new Partial2Large(A, smallFactors, factor1.longValue(), factor2.longValue());
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
		return new TDivReport(testCount, sufficientSmoothCount, aqDuration, pass1Duration, pass2Duration, primeTestDuration, factorDuration);
	}
	
	@Override
	public void cleanUp() {
		primes = null;
		unsievedBaseElements = null;
		x1Array = null;
		x2Array = null;
		qsInternal.cleanUp();
	}
}
