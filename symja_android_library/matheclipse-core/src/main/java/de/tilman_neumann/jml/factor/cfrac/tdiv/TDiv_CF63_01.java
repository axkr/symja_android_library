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

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Partial_1Large;
import de.tilman_neumann.jml.factor.base.congruence.Smooth_Perfect;

/**
 * Auxiliary factor algorithm to find smooth decompositions of Q's.
 * 
 * Version 01:
 * Uses only trial division -> this means that partials can have only 1 big factor
 * 
 * @author Tilman Neumann
 */
public class TDiv_CF63_01 implements TDiv_CF63 {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(TDiv_CF63_01.class);
	private static final boolean DEBUG = false;
	
	private int primeBaseSize;
	private int[] primesArray;
	
	/** Q is sufficiently smooth if the unfactored Q_rest is smaller than this bound depending on N */
	private double maxQRest;

	// result: two arrays that are reused, their content is _copied_ to AQ-pairs
	private SortedIntegerArray smallFactors = new SortedIntegerArray();

	@Override
	public String getName() {
		return "TDiv63-01";
	}

	public void initialize(BigInteger N, double maxQRest) {
		this.maxQRest = maxQRest;
	}
	
	public void initialize(BigInteger kN, int primeBaseSize, int[] primesArray) {
		this.primeBaseSize = primeBaseSize;
		this.primesArray = primesArray;
	}

	public AQPair test(BigInteger A, long Q) {
		smallFactors.reset();
		
		// sign
		long Q_rest = Q;
		if (Q < 0) {
			smallFactors.add(-1);
			Q_rest = -Q;
		}
		// Remove multiples of 2
		int lsb = Long.numberOfTrailingZeros(Q_rest);
		if (lsb > 0) {
			smallFactors.add(2, (short)lsb);
			Q_rest = Q_rest>>lsb;
		}

		// Trial division chain:
		// -> first do it in long, then in int.
		// -> (small or probabilistic) prime tests during trial division just slow it down.
		// -> running indices bottom-up is faster because small dividends are more likely to reduce the size of Q_rest.
		int trialDivIndex = 1; // p[0]=2 has already been tested
		int Q_rest_bits = 64 - Long.numberOfLeadingZeros(Q_rest);
		if (Q_rest_bits>31) {
			// do trial division in long
			while (trialDivIndex < primeBaseSize) {
				int p = primesArray[trialDivIndex];
				if (Q_rest % p == 0) {
					// no remainder -> exact division -> small factor
					smallFactors.add(p);
					Q_rest /= p;
					// After division by a prime base element (typically < 20 bit), Q_rest is 12..61 bits.
					Q_rest_bits = 64 - Long.numberOfLeadingZeros(Q_rest);
					if (Q_rest_bits<32) break; // continue with int
					// trialDivIndex must remain as it is to find the same p more than once
				} else {
					trialDivIndex++;
				}
			} // end while (trialDivIndex < primeBaseSize)
		}
//		if (DEBUG) assertTrue(Q_rest>1);
		if (Q_rest_bits<32) {
			int Q_rest_int = (int) Q_rest;
			while (trialDivIndex < primeBaseSize) {
				// continue trial division in int
				int p = primesArray[trialDivIndex];
				while (Q_rest_int % p == 0) { // in the last loop, a while pays out!
					// no remainder -> exact division -> small factor
					smallFactors.add(p);
					Q_rest_int /= p;
				}
				trialDivIndex++;
			} // end while (trialDivIndex < primeBaseSize)
			if (Q_rest_int==1) return new Smooth_Perfect(A, smallFactors);
			Q_rest = (long) Q_rest_int; // keep Q_rest up-to-date
		}
		
		// trial division was not sufficient to factor Q completely.
		// the remaining Q is either a prime > pMax, or a composite > pMax^2.
		if (bitLength(Q_rest) > 31 || Q_rest > maxQRest) return null; // Q is not sufficiently smooth
	
		// Q is sufficiently smooth
		return new Partial_1Large(A, smallFactors, (int)Q_rest);
	}
	
	private int bitLength(long n) {
		return 64 - Long.numberOfLeadingZeros(n);
	}
}
