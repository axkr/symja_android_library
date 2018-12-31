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
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Partial_1Large;
import de.tilman_neumann.jml.factor.base.congruence.Smooth_Perfect;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Auxiliary factor algorithm to find smooth decompositions of Q's.
 * 
 * Version 01:
 * Uses only trial division -> this means that partials can have only 1 big factor
 * 
 * @author Tilman Neumann
 */
public class TDiv_CF01 implements TDiv_CF {
//	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(TDiv_CF01.class);
//	private static final boolean DEBUG = false;
	
	private int primeBaseSize;
	private int[] primesArray_int;
	private BigInteger[] primesArray_big;
	
	/** Q is sufficiently smooth if the unfactored Q_rest is smaller than this bound depending on N */
	private double maxQRest;

	private UnsignedBigInt Q_rest_UBI = new UnsignedBigInt(new int[50]);

	// result: two arrays that are reused, their content is _copied_ to AQ-pairs
	private SortedIntegerArray smallFactors = new SortedIntegerArray();

	@Override
	public String getName() {
		return "TDiv01";
	}

	public void initialize(BigInteger N, double maxQRest) {
		this.maxQRest = maxQRest;
	}
	
	public void initialize(BigInteger kN, int primeBaseSize, int[] primesArray, BigInteger[] primesArray_big) {
		this.primeBaseSize = primeBaseSize;
		this.primesArray_int = primesArray;
		this.primesArray_big = primesArray_big;
	}

	public AQPair test(BigInteger A, BigInteger Q) {
		smallFactors.reset();
		
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
		if (Q_rest.bitLength()>31 || Q_rest.doubleValue() > maxQRest) return null; // Q is not sufficiently smooth
		
		// Q is sufficiently smooth
		return new Partial_1Large(A, smallFactors, Q_rest.intValue());
	}
}
