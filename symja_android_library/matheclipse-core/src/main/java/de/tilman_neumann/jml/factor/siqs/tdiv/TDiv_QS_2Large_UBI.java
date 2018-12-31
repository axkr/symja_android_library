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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.UnsignedBigInt;
import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Partial_1Large;
import de.tilman_neumann.jml.factor.base.congruence.Partial_2Large;
import de.tilman_neumann.jml.factor.base.congruence.Smooth_1LargeSquare;
import de.tilman_neumann.jml.factor.base.congruence.Smooth_Perfect;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver01_Gauss;
import de.tilman_neumann.jml.factor.lehman.Lehman_Fast;
import de.tilman_neumann.jml.factor.siqs.SIQS;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;
import de.tilman_neumann.jml.factor.siqs.poly.SIQSPolyGenerator;
import de.tilman_neumann.jml.factor.siqs.powers.PowerOfSmallPrimesFinder;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03g;
import de.tilman_neumann.jml.factor.squfof.SquFoF63;
import de.tilman_neumann.jml.primes.probable.BPSWTest;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.Timer;

import static de.tilman_neumann.jml.base.BigIntConstants.I_1;

/**
 * A trial division engine where partials can have up to 2 large factors.
 * This is absolutely adequate for the quadratic sieve, because we would hardly get 3 large factors for inputs < 400 bit.
 * 
 * Division is carried out using UnsignedBigInt; this way less intermediate objects are created.
 * 
 * Faster than 1Large for approximately N>=220 bit.
 * 
 * @author Tilman Neumann
 */
public class TDiv_QS_2Large_UBI implements TDiv_QS {
//	private static final Logger LOG = Logger.getLogger(TDiv_QS_2Large_UBI.class);
//	private static final boolean DEBUG = false;
	
	// factor argument and polynomial parameters
	private BigInteger kN;
	private BigInteger da; // d*a with d = 1 or 2 depending on kN % 8
	private BigInteger bParam;

	/** Q is sufficiently smooth if the unfactored Q_rest is smaller than this bound depending on N */
	private double maxQRest;

	// prime base
	private int[] primes;
	private int[] exponents;
	private int[] powers;
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

	private BPSWTest bpsw = new BPSWTest();
	
	private Lehman_Fast lehman = new Lehman_Fast(true); // for N <= 2^56
	private SquFoF63 squFoF63 = new SquFoF63(); // used for 2^57 <= Q <= 2^59
	private SIQS qsInternal; // Nested SIQS for Q_rest >= 2^60. Required only for approximately N>310 bit.
	                        
	// smallest solutions of Q(x) == A(x)^2 (mod p)
	private int[] x1Array, x2Array;

	// small factors found by testing some x, their content is _copied_ to AQ-pairs
	private SortedIntegerArray smallFactors = new SortedIntegerArray();
	
	// statistics
	private boolean profile;
	private Timer timer = new Timer();
	private long testCount, sufficientSmoothCount;
	private long duration;

	public TDiv_QS_2Large_UBI() {
		// XXX For safety reasons we do not use Sieve03gU yet for the internal quadratic sieve
		this.qsInternal = new SIQS(0.32F, 0.37F, null, 0.16F, new PowerOfSmallPrimesFinder(), new SIQSPolyGenerator(), new Sieve03g(), new TDiv_QS_1Large_UBI(), 10, new MatrixSolver01_Gauss(), false);
	}

	@Override
	public String getName() {
		return "TDiv_2L_UBI";
	}

	@Override
	public void initializeForN(double N_dbl, BigInteger kN, double maxQRest, boolean profile) {
		// the biggest unfactored rest where some Q is considered smooth enough for a congruence.
		this.maxQRest = maxQRest;
//		if (DEBUG) LOG.debug("maxQRest = " + maxQRest + " (" + (64 - Long.numberOfLeadingZeros((long)maxQRest)) + " bits)");
		this.kN = kN;
		// statistics
		this.profile = profile;
		this.testCount = 0;
		this.sufficientSmoothCount = 0;
		this.duration = 0;
	}

	@Override
	public void initializeForAParameter(BigInteger da, BigInteger b, SolutionArrays solutionArrays, int filteredBaseSize, int[] unsievedBaseElements) {
		this.da = da;
		bParam = b;
		primes = solutionArrays.primes;
		exponents = solutionArrays.exponents;
		powers = solutionArrays.powers;
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
		timer.capture();

		// do trial division with sieve result
		ArrayList<AQPair> aqPairs = new ArrayList<AQPair>();
		for (int x : xList) {
			smallFactors.reset();
			testCount++;
			BigInteger A = da.multiply(BigInteger.valueOf(x)).add(bParam); // A(x) = d*a*x+b, with d = 1 or 2 depending on kN % 8
			BigInteger Q = A.multiply(A).subtract(kN); // Q(x) = A(x)^2 - kN
			AQPair aqPair = test(A, Q, x);
			if (aqPair != null) {
				// Q(x) was found sufficiently smooth to be considered a (partial) congruence
				aqPairs.add(aqPair);
				sufficientSmoothCount++;
//				if (DEBUG) {
//					LOG.debug("Found congruence " + aqPair);
//					assertEquals(A.multiply(A).mod(kN), Q.mod(kN));
//					// make sure that the product of factors gives Q
//					SortedMultiset<Integer> allQFactors = aqPair.getAllQFactors();
//					BigInteger testProduct = I_1;
//					for (Map.Entry<Integer, Integer> entry : allQFactors.entrySet()) {
//						BigInteger prime = BigInteger.valueOf(entry.getKey());
//						int exponent = entry.getValue();
//						testProduct = testProduct.multiply(prime.pow(exponent));
//					}
//					assertEquals(Q, testProduct);
//				}
			}
		}
		if (profile) duration += timer.capture();
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
		
		// Pass 1: Test solution arrays ("re-sieving").
		// Starting at the biggest prime base elements is faster because then Q_rest is reduced quicker in pass 2.
		// IMPORTANT: Java gives x % p = x for |x| < p, and we have many p bigger than any sieve array entry.
		// IMPORTANT: Not computing the modulus in these cases improves performance by almost factor 2!
		int xAbs = Math.abs(x);
		for (int pIndex = baseSize-1; pIndex > 0; pIndex--) { // p[0]=2 was already tested
			int p = powers[pIndex];
			int xModP = xAbs<p ? x : x % p;
			if (xModP<0) xModP += p; // make remainder non-negative for negative x
//			if (DEBUG) {
//				if (xModP<0) LOG.debug("x=" + x + ", p=" + p + " -> x % p = " + xModP + ", x1 = " + x1Array[pIndex] + ", x2 = " + x2Array[pIndex]);
//				assertTrue(0<=xModP && xModP<p);
//			}
			if (xModP==x1Array[pIndex] || xModP==x2Array[pIndex]) {
				pass2Primes[pass2Count] = primes[pIndex];
				pass2Exponents[pass2Count] = exponents[pIndex];
				pass2Powers[pass2Count++] = p;
				// for some reasons I do not understand it is faster to divide Q by p in pass 2 only, not here
			}
		}
	
		// Pass 2: Reduce Q by the pass2Primes and collect small factors
		Q_rest_UBI.set(Q_rest);
		for (int pass2Index = 0; pass2Index < pass2Count; pass2Index++) {
			int p = pass2Powers[pass2Index];
			while (true) {
				int rem = Q_rest_UBI.divideAndRemainder(p, quotient_UBI);
				if (rem>0) break;
				// remainder == 0 -> the division was exact. assign quotient to Q_rest and add p to factors
				UnsignedBigInt tmp = Q_rest_UBI;
				Q_rest_UBI = quotient_UBI;
				quotient_UBI = tmp;
				smallFactors.add(pass2Primes[pass2Index], (short)pass2Exponents[pass2Index]);
//				if (DEBUG) {
//					BigInteger pBig = BigInteger.valueOf(p);
//					BigInteger[] div = Q_rest.divideAndRemainder(pBig);
//					assertEquals(div[1].intValue(), rem);
//					Q_rest = div[0];
//				}
			}
		}
		if (Q_rest_UBI.isOne()) return new Smooth_Perfect(A, smallFactors);
		Q_rest = Q_rest_UBI.toBigInteger();
		
		// Division by all p<=pMax was not sufficient to factor Q completely.
		// The remaining Q_rest is either a prime > pMax, or a composite > pMax^2.
		if (Q_rest.doubleValue() >= maxQRest) return null; // Q is not sufficiently smooth
		
		// now we consider Q as sufficiently smooth. then we want to know all prime factors, as long as we do not find one that is too big to be useful.
//		if (DEBUG) LOG.debug("test(): pMax=" + pMax + " < Q_rest=" + Q_rest + " < maxQRest=" + maxQRest + " -> resolve all factors");
		if (Q_rest.compareTo(pMaxSquare)<0) {
			// we divided Q_rest by all primes <= pMax and we have Q_rest < pMax^2 -> it must be prime
//			if (DEBUG) assertTrue(bpsw.isProbablePrime(Q_rest));
			if (Q_rest.bitLength() > 31) return null;
			return new Partial_1Large(A, smallFactors, Q_rest.intValue());
		}
		// now we need isProbablePrime(), because calling findSingleFactor() may not return when called with a prime argument
		if (bpsw.isProbablePrime(Q_rest)) {
			// Q_rest is a (probable) prime >= pMax^2. Such big factors do not help to find smooth congruences, so we ignore the partial.
//			if (DEBUG) LOG.debug("factor_recurrent(): Q_rest = " + Q_rest + " is probable prime > pMax^2 -> ignore");
			return null;
		} // else: Q_rest is surely not prime
		
		// Find a factor of Q_rest, where Q_rest is odd and has two+ factors, each greater than pMax.
		// At N with 200 bit we have pMax ~ 17 bit, thus Q_rest >= 34 bit -> trial division is no help here.
		BigInteger factor1;
		int Q_rest_bits = Q_rest.bitLength();
		if (Q_rest_bits<57) {
//			if (DEBUG) LOG.debug("test(): pMax^2 = " + pMaxSquare + ", Q_rest = " + Q_rest + " (" + Q_rest_bits + " bits) not prime -> use lehman");
			factor1 = lehman.findSingleFactor(Q_rest);
		} else if (Q_rest_bits<60) {
//			if (DEBUG) LOG.debug("test(): pMax^2 = " + pMaxSquare + ", Q_rest = " + Q_rest + " (" + Q_rest_bits + " bits) not prime -> use squFoF63");
			factor1 = squFoF63.findSingleFactor(Q_rest);
		} else {
			factor1 = qsInternal.findSingleFactor(Q_rest);
		}
		if (factor1.bitLength() > 31) return null;
		BigInteger factor2 = Q_rest.divide(factor1);
		if (factor2.bitLength() > 31) return null;
//		if (DEBUG) LOG.debug("test(): Q_rest = " + Q_rest + " (" + Q_rest_bits + " bits) = " + factor1 + " * " + factor2);
		if (factor1.equals(factor2)) {
			return new Smooth_1LargeSquare(A, smallFactors, factor1.intValue());
		}
		return new Partial_2Large(A, smallFactors, factor1.intValue(), factor2.intValue());
	}

	@Override
	public TDivReport getReport() {
		return new TDivReport(testCount, sufficientSmoothCount, duration);
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
