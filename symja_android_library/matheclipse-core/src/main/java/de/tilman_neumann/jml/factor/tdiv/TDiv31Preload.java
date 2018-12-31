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
package de.tilman_neumann.jml.factor.tdiv;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.FactorAlgorithmBase;

/**
 * Trial division factor algorithm preloading all primes <= sqrt(Integer.MAX_VALUE).
 * 
 * sqrt(Integer.MAX_VALUE) = sqrt(2^31 - 1) = sqrt(2147483647) = 46340.95
 * -> we need to preload all primes < 46340.
 * -> there are 4793 such primes...
 * 
 * Performance just like TDiv31.
 * 
 * @author Tilman Neumann
 */
public class TDiv31Preload extends FactorAlgorithmBase {
	
	private static int[] primes;

	public TDiv31Preload() {
		primes = new int[NUM_PRIMES_FOR_31_BIT_TDIV];
		for (int i=0; i<NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
			primes[i] = SMALL_PRIMES.getPrime(i);
		}
	}
	
	@Override
	public String getName() {
		return "TDiv31Preload";
	}

	@Override
	// TODO will not work for N > 31 bit having smallest factor > 15 bit
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.intValue()));
	}
	
	public int findSingleFactor(int N) {
		// if N is odd and composite then the loop runs maximally up to test = floor(sqrt(N))
		for (int i=0; i<NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
			if (N%primes[i]==0) return primes[i];
		}
		// otherwise N is prime!
		throw new IllegalArgumentException("N = " + N + " is prime!");
	}
}
