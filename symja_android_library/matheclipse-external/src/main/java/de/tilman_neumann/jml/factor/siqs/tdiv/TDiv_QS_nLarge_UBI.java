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

import static de.tilman_neumann.jml.factor.base.AnalysisOptions.*;
import static de.tilman_neumann.jml.base.BigIntConstants.I_1;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.UnsignedBigInt;
import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.jml.factor.base.SortedLongArray;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.AQPairFactory;
import de.tilman_neumann.jml.factor.base.congruence.Smooth_Perfect;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver01_Gauss;
import de.tilman_neumann.jml.factor.hart.Hart_TDiv_Race;
import de.tilman_neumann.jml.factor.pollardRho.PollardRhoBrentMontgomery64;
import de.tilman_neumann.jml.factor.pollardRho.PollardRhoBrentMontgomeryR64Mul63;
import de.tilman_neumann.jml.factor.siqs.SIQS;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;
import de.tilman_neumann.jml.factor.siqs.poly.SIQSPolyGenerator;
import de.tilman_neumann.jml.factor.siqs.powers.PowerOfSmallPrimesFinder;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03g;
import de.tilman_neumann.jml.primes.probable.PrPTest;
import de.tilman_neumann.util.Multiset;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;
import de.tilman_neumann.util.Timer;

/**
 * A trial division engine where partials can have several large factors.
 * 
 * Division is carried out in two stages:
 * Stage 1 identifies prime factors of Q, applying long-valued Barrett reduction
 * Stage 2 does the actual division using UnsignedBigInt; this way less intermediate objects are created.
 * 
 * Faster than 1Large for approximately N>=220 bit.
 * 
 * @author Tilman Neumann
 */
public class TDiv_QS_nLarge_UBI implements TDiv_QS {
	private static final Logger LOG = Logger.getLogger(TDiv_QS_nLarge_UBI.class);
	private static final boolean DEBUG = false;
	
	// factor argument and polynomial parameters
	private BigInteger kN;
	private BigInteger da; // d*a with d = 1 or 2 depending on kN % 8
	private BigInteger bParam;

	/** Q is sufficiently smooth if the unfactored Q_rest is smaller than this bound depending on N */
	private double maxQRest;

	// prime base
	private int[] primes;
	private int[] exponents;
	private int[] pArray;
	private long[] pinvArrayL;
	private int baseSize;
	private int pMax;
	private BigInteger pMaxSquare;
	private int[] unsievedBaseElements;

	/** buffers for trial division engine. */
	private UnsignedBigInt Q_rest_UBI = new UnsignedBigInt(new int[50]);
	private UnsignedBigInt quotient_UBI = new UnsignedBigInt(new int[50]);

	/** the indices of the primes found to divide Q in pass 1 */
	private int[] pass2Primes = new int[100];
	private int[] pass2Powers = new int[100];
	private int[] pass2Exponents = new int[100];

	private PrPTest prpTest = new PrPTest();
	
	private Hart_TDiv_Race hart = new Hart_TDiv_Race();
	private PollardRhoBrentMontgomeryR64Mul63 pollardRhoR64Mul63 = new PollardRhoBrentMontgomeryR64Mul63();
	private PollardRhoBrentMontgomery64 pollardRho64 = new PollardRhoBrentMontgomery64();
	// Nested SIQS is required only for approximately N>310 bit.
	// XXX For safety reasons we do not use Sieve03gU yet for the internal quadratic sieve
	private SIQS qsInternal = new SIQS(0.32F, 0.37F, null, 0.16F, new PowerOfSmallPrimesFinder(), new SIQSPolyGenerator(), new Sieve03g(), new TDiv_QS_1Large_UBI(), 10, new MatrixSolver01_Gauss(), true, false); // XXX set useLegacyFactoring to false
	                        
	// smallest solutions of Q(x) == A(x)^2 (mod p)
	private int[] x1Array, x2Array;

	// result: two arrays that are reused, their content is _copied_ to AQ-pairs
	private SortedIntegerArray smallFactors = new SortedIntegerArray();
	private SortedLongArray bigFactors = new SortedLongArray();
	private AQPairFactory aqPairFactory = new AQPairFactory();

	// statistics
	private Timer timer = new Timer();
	private long testCount, sufficientSmoothCount;
	private long aqDuration, pass1Duration, pass2Duration, primeTestDuration, factorDuration;
	private Multiset<Integer> qRestSizes;

	@Override
	public String getName() {
		return "TDiv_nL_UBI";
	}

	@Override
	public void initializeForN(double N_dbl, BigInteger kN, double maxQRest) {
		// the biggest unfactored rest where some Q is considered smooth enough for a congruence.
		this.maxQRest = maxQRest;
		if (DEBUG) LOG.debug("maxQRest = " + maxQRest + " (" + (64 - Long.numberOfLeadingZeros((long)maxQRest)) + " bits)");
		this.kN = kN;
		// statistics
		if (ANALYZE) testCount = sufficientSmoothCount = 0;
		if (ANALYZE) aqDuration = pass1Duration = pass2Duration = primeTestDuration = factorDuration = 0;
		if (ANALYZE_LARGE_FACTOR_SIZES) qRestSizes = new SortedMultiset_BottomUp<>();
	}

	@Override
	public void initializeForAParameter(BigInteger da, BigInteger b, SolutionArrays solutionArrays, int filteredBaseSize, int[] unsievedBaseElements) {
		this.da = da;
		bParam = b;
		primes = solutionArrays.primes;
		exponents = solutionArrays.exponents;
		pArray = solutionArrays.pArray;
		pinvArrayL = solutionArrays.pinvArrayL;
		baseSize = filteredBaseSize;
		x1Array = solutionArrays.x1Array;
		x2Array = solutionArrays.x2Array;
		pMax = primes[baseSize-1];
		pMaxSquare = BigInteger.valueOf(pMax * (long) pMax);
		this.unsievedBaseElements = unsievedBaseElements;
	}

	@Override
	public void setBParameter(BigInteger b) {
		this.bParam = b;
	}

	@Override
	public List<AQPair> testList(List<Integer> xList) {
		if (ANALYZE) timer.capture();

		// do trial division with sieve result
		ArrayList<AQPair> aqPairs = new ArrayList<AQPair>();
		for (int x : xList) {
			smallFactors.reset();
			bigFactors.reset();
			if (ANALYZE) testCount++;
			BigInteger A = da.multiply(BigInteger.valueOf(x)).add(bParam); // A(x) = d*a*x+b, with d = 1 or 2 depending on kN % 8
			BigInteger Q = A.multiply(A).subtract(kN); // Q(x) = A(x)^2 - kN
			if (ANALYZE) aqDuration += timer.capture();
			AQPair aqPair = test(A, Q, x);
			if (ANALYZE) factorDuration += timer.capture();
			if (aqPair != null) {
				// Q(x) was found sufficiently smooth to be considered a (partial) congruence
				aqPairs.add(aqPair);
				if (ANALYZE) sufficientSmoothCount++;
				if (DEBUG) {
					LOG.debug("Found congruence " + aqPair);
					assertEquals(A.multiply(A).mod(kN), Q.mod(kN));
					// make sure that the product of factors gives Q
					SortedMultiset<Long> allQFactors = aqPair.getAllQFactors();
					BigInteger testProduct = I_1;
					for (Map.Entry<Long, Integer> entry : allQFactors.entrySet()) {
						BigInteger prime = BigInteger.valueOf(entry.getKey());
						int exponent = entry.getValue();
						testProduct = testProduct.multiply(prime.pow(exponent));
					}
					assertEquals(Q, testProduct);
				}
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
				if (DEBUG) {
					assertTrue(0<=xModP && xModP<p);
					int xModP2 = x % p;
					if (xModP2<0) xModP2 += p;
					if (xModP != xModP2) LOG.debug("x=" + x + ", p=" + p + ": xModP=" + xModP + ", but xModP2=" + xModP2);
					assertEquals(xModP2, xModP);
				}
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
		Q_rest_UBI.set(Q_rest);
		for (int pass2Index = 0; pass2Index < pass2Count; pass2Index++) {
			int p = pass2Powers[pass2Index];
			int rem;
			while ((rem = Q_rest_UBI.divideAndRemainder(p, quotient_UBI)) == 0) {
				// the division was exact. assign quotient to Q_rest and add p to factors
				UnsignedBigInt tmp = Q_rest_UBI;
				Q_rest_UBI = quotient_UBI;
				quotient_UBI = tmp;
				smallFactors.add(pass2Primes[pass2Index], (short)pass2Exponents[pass2Index]);
				if (DEBUG) {
					BigInteger pBig = BigInteger.valueOf(p);
					BigInteger[] div = Q_rest.divideAndRemainder(pBig);
					assertEquals(div[1].intValue(), rem);
					Q_rest = div[0];
				}
			}
		}
		if (ANALYZE) pass2Duration += timer.capture();
		if (Q_rest_UBI.isOne()) return new Smooth_Perfect(A, smallFactors);
		Q_rest = Q_rest_UBI.toBigInteger();
		
		// Division by all p<=pMax was not sufficient to factor Q completely.
		// The remaining Q_rest is either a prime > pMax, or a composite > pMax^2.
		if (Q_rest.doubleValue() >= maxQRest) return null; // Q is not sufficiently smooth
		
		// now we consider Q as sufficiently smooth. then we want to know all prime factors, as long as we do not find one that is too big to be useful.
		if (DEBUG) LOG.debug("test(): pMax=" + pMax + " < Q_rest=" + Q_rest + " < maxQRest=" + maxQRest + " -> resolve all factors");
		boolean isSmooth = factor_recurrent(Q_rest);
		if (bigFactors.size()>2) LOG.debug("Found " + bigFactors.size() + " distinct big factors!");
		return isSmooth ? aqPairFactory.create(A, smallFactors, bigFactors) : null;
	}

	private boolean factor_recurrent(BigInteger Q_rest) {
		// Here we need a prime test, because factor algorithms may not return when called with a prime argument.
		boolean restIsPrime = Q_rest.compareTo(pMaxSquare)<0 || prpTest.isProbablePrime(Q_rest);
		if (ANALYZE) primeTestDuration += timer.capture();
		if (restIsPrime) {
			// Check that the simple prime test using pMaxSquare is correct
			if (DEBUG) assertTrue(prpTest.isProbablePrime(Q_rest));
			if (Q_rest.bitLength() > 31) return false;
			bigFactors.add(Q_rest.longValue());
			return true;
		} // else: Q_rest is surely not prime
		
		// Find a factor of Q_rest, where Q_rest is odd and has two+ factors, each greater than pMax.
		// This starts to happen at N >= 200 bit where we have pMax ~ 17 bit, thus Q_rest >= 34 bit
		// -> trial division is no help here.
		BigInteger factor1;
		int Q_rest_bits = Q_rest.bitLength();
		if (ANALYZE_LARGE_FACTOR_SIZES) qRestSizes.add(Q_rest_bits);
		if (Q_rest_bits<50) {
			if (DEBUG) LOG.debug("factor_recurrent(): pMax^2 = " + pMaxSquare + ", Q_rest = " + Q_rest + " (" + Q_rest_bits + " bits) not prime -> use hart");
			factor1 = hart.findSingleFactor(Q_rest);
		} else if (Q_rest_bits<57) {
			if (DEBUG) LOG.debug("factor_recurrent(): pMax^2 = " + pMaxSquare + ", Q_rest = " + Q_rest + " (" + Q_rest_bits + " bits) not prime -> use pollardRhoR64Mul63");
			factor1 = pollardRhoR64Mul63.findSingleFactor(Q_rest);
		} else if (Q_rest_bits<63) {
			if (DEBUG) LOG.debug("factor_recurrent(): pMax^2 = " + pMaxSquare + ", Q_rest = " + Q_rest + " (" + Q_rest_bits + " bits) not prime -> use pollardRho64");
			factor1 = pollardRho64.findSingleFactor(Q_rest);
		} else {
			if (DEBUG) LOG.debug("factor_recurrent(): pMax^2 = " + pMaxSquare + ", Q_rest = " + Q_rest + " (" + Q_rest_bits + " bits) not prime -> use qsInternal");
			factor1 = qsInternal.findSingleFactor(Q_rest);
		}
		if (ANALYZE) factorDuration += timer.capture();
		// Here we can not exclude factors > 31 bit because they may have 2 prime factors themselves.
		BigInteger factor2 = Q_rest.divide(factor1);
		if (DEBUG) LOG.debug("factor_recurrent(): Q_rest = " + Q_rest + " (" + Q_rest_bits + " bits) = " + factor1 + " * " + factor2);
		return factor_recurrent(factor1) && factor_recurrent(factor2);
	}

	@Override
	public TDivReport getReport() {
		return new TDivReport(testCount, sufficientSmoothCount, aqDuration, pass1Duration, pass2Duration, primeTestDuration, factorDuration, qRestSizes);
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
