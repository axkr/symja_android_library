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
package de.tilman_neumann.jml.factor.cfrac.tdiv;

import java.math.BigInteger;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.UnsignedBigInt;
import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.jml.factor.base.SortedLongArray;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.AQPairFactory;
import de.tilman_neumann.jml.factor.base.congruence.Smooth_Perfect;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver01_Gauss;
import de.tilman_neumann.jml.factor.cfrac.CFrac63_01;
import de.tilman_neumann.jml.factor.lehman.Lehman_Fast;
import de.tilman_neumann.jml.factor.squfof.SquFoF63;
import de.tilman_neumann.jml.factor.tdiv.TDiv31Inverse;
import de.tilman_neumann.jml.primes.probable.BPSWTest;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Auxiliary factor algorithm to find smooth decompositions of Q's.
 * 
 * Version 02:
 * Uses trial division first, complete factorization if Q is considered sufficiently smooth.
 * 
 * @author Tilman Neumann
 */
public class TDiv_CF02 implements TDiv_CF {
//	private static final Logger LOG = Logger.getLogger(TDiv_CF02.class);
//	private static final boolean DEBUG = false;

	private int primeBaseSize;
	private int[] primesArray_int;
	private BigInteger[] primesArray_big;
	private int pMax;
	private BigInteger pMaxSquare;

	/** Q is sufficiently smooth if the unfactored Q_rest is smaller than this bound depending on N */
	private double maxQRest;

	private TDiv31Inverse tDiv31; // used for Q <= 2^28
	private Lehman_Fast lehman; // used for 2^29 <= Q <= 2^56
	private SquFoF63 squFoF63; // used for 2^57 <= Q <= 2^66
	private CFrac63_01 cf_internal = new CFrac63_01(true, 5, 1.5F, 0.152F, 0.25F, new TDiv_CF63_01(), 10, new MatrixSolver01_Gauss(), 12);

	private BPSWTest bpsw;

	private UnsignedBigInt Q_rest_UBI = new UnsignedBigInt(new int[50]);

	// result: two arrays that are reused, their content is _copied_ to AQ-pairs
	private SortedIntegerArray smallFactors = new SortedIntegerArray();
	private SortedLongArray bigFactors = new SortedLongArray();
	private AQPairFactory aqPairFactory = new AQPairFactory();
	
	public TDiv_CF02() {
		this.tDiv31 = new TDiv31Inverse();
		this.lehman = new Lehman_Fast(true);
		this.squFoF63 = new SquFoF63();
		this.bpsw = new BPSWTest(1<<20);
	}

	@Override
	public String getName() {
		return "TDiv02";
	}

	public void initialize(BigInteger N, double maxQRest) {
		this.maxQRest = maxQRest;
	}

	public void initialize(BigInteger kN, int primeBaseSize, int[] primesArray, BigInteger[] primesArray_big) {
		this.primeBaseSize = primeBaseSize;
		this.primesArray_int = primesArray;
		this.primesArray_big = primesArray_big;
		this.pMax = primesArray[primeBaseSize-1];
//		if (DEBUG) {
//			int pMaxBits = 32 - Integer.numberOfLeadingZeros(pMax);
//			LOG.debug("pMax = " + pMax + " (" + pMaxBits + " bits)");
//		}
		this.pMaxSquare = BigInteger.valueOf(pMax * (long) pMax);
	}

	public AQPair test(BigInteger A, BigInteger Q) {
		smallFactors.reset();
		bigFactors.reset();
		
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

		// Trial division chain:
		// -> first do it in BigInteger, then in long, then in int.
		// -> (small or probabilistic) prime tests during trial division just slow it down.
		// -> running indices bottom-up is faster because small dividends are more likely to reduce the size of Q_rest.
		int trialDivIndex = 1; // p[0]=2 has already been tested
		int Q_rest_bits = Q_rest.bitLength();
		if (Q_rest_bits>63) {
			// trial division in BigInteger required
			int p;
			Q_rest_UBI.set(Q_rest);
			while (trialDivIndex < primeBaseSize) {
				p = primesArray_int[trialDivIndex];
				if (Q_rest_UBI.mod(p)==0) { // very fast!
					// no remainder -> exact division -> small factor
					smallFactors.add(p);
					// BigInteger.divide() is slightly faster than Q_rest_UBI.divideAndRemainder()
					Q_rest = Q_rest.divide(primesArray_big[trialDivIndex]);
					// After division by a prime base element (typically < 20 bit), Q_rest is >= 44 bits.
					Q_rest_bits = Q_rest.bitLength();
					if (Q_rest_bits<64) break; // continue in long version
					Q_rest_UBI.set(Q_rest);
					// trialDivIndex must remain as it is to find the same p more than once
				} else {
					trialDivIndex++;
				}
			} // end while (trialDivIndex < primeBaseSize)
		}
		if (Q_rest_bits>31 && Q_rest_bits<64 && trialDivIndex<primeBaseSize) {
			// continue trial division in long
			long Q_rest_long = Q_rest.longValue();
			while (trialDivIndex<primeBaseSize) {
				int p = primesArray_int[trialDivIndex];
				if (Q_rest_long % p == 0) {
					// no remainder -> exact division -> small factor
					smallFactors.add(p);
					Q_rest_long /= p;
					// After division by a prime base element (typically < 20 bit), Q_rest is 12..61 bits.
					Q_rest_bits = 64 - Long.numberOfLeadingZeros(Q_rest_long);
					if (Q_rest_bits<32) break; // continue with int
					// trialDivIndex must remain as it is to find the same p more than once
				} else {
					trialDivIndex++;
				}
			} // end while (trialDivIndex < primeBaseSize)
			Q_rest = BigInteger.valueOf(Q_rest_long); // keep Q_rest up-to-date
		}
//		if (DEBUG) assertTrue(Q_rest.compareTo(I_1)>0);
		if (Q_rest_bits<32) {
			int Q_rest_int = Q_rest.intValue();
			while (trialDivIndex < primeBaseSize) {
				// continue trial division in int
				int p = primesArray_int[trialDivIndex];
				while (Q_rest_int % p == 0) { // in the last loop, a while pays out!
					// no remainder -> exact division -> small factor
					smallFactors.add(p);
					Q_rest_int /= p;
				}
				trialDivIndex++;
			} // end while (trialDivIndex < primeBaseSize)
			if (Q_rest_int==1) return new Smooth_Perfect(A, smallFactors);
			Q_rest = BigInteger.valueOf(Q_rest_int); // keep Q_rest up-to-date
		}

		// trial division was not sufficient to factor Q completely.
		// the remaining Q is either a prime > pMax, or a composite > pMax^2.
		if (Q_rest.doubleValue() > maxQRest) return null; // Q is not sufficiently smooth
		
		// now we consider Q as sufficiently smooth. then we want to know all prime factors, as long as we do not find one that is too big to be useful.
		//LOG.debug("before factor_recurrent()");
		boolean isSmooth = factor_recurrent(Q_rest);
//		if (DEBUG) if (bigFactors.size()>2) LOG.debug("Found " + bigFactors.size() + " distinct big factors: " + bigFactors);
		return isSmooth ? aqPairFactory.create(A, smallFactors, bigFactors) : null;
	}

	private boolean factor_recurrent(BigInteger Q_rest) {
		if (Q_rest.compareTo(pMaxSquare)<0) {
			// we divided Q_rest by all primes <= pMax and the rest is < pMax^2 -> it must be prime
//			if (DEBUG) assertTrue(bpsw.isProbablePrime(Q_rest));
			if (Q_rest.bitLength() > 31) return false;
			bigFactors.add(Q_rest.intValue());
			return true;
		}
		// here we can not do without isProbablePrime(), because calling findSingleFactor() may not return when called with a prime argument
		if (bpsw.isProbablePrime(Q_rest)) {
			// Q_rest is (probable) prime -> end of recurrence
			if (Q_rest.bitLength() > 31) return false;
			bigFactors.add(Q_rest.intValue());
			return true;
		} // else: Q_rest is surely not prime

		// Find a factor of Q_rest, where Q_rest is pMax < Q_rest <= maxQRest, composite and odd.
		BigInteger factor1;
		int Q_rest_bits = Q_rest.bitLength();
		if (Q_rest_bits <= 27) {
			factor1 = tDiv31.findSingleFactor(Q_rest);
		} else if (Q_rest_bits <= 56) {
			factor1 = lehman.findSingleFactor(Q_rest);
		} else if (Q_rest_bits <= 66) {
			factor1 = squFoF63.findSingleFactor(Q_rest);
		} else {
			factor1 = cf_internal.findSingleFactor(Q_rest);
		}
		// Recurrence: Is it possible to further decompose the parts?
		// Here we can not exclude factors > 31 bit because they may have 2 prime factors themselves.
		return factor_recurrent(factor1) && factor_recurrent(Q_rest.divide(factor1));
	}
}
