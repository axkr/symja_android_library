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
package de.tilman_neumann.jml.primes.probable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;

/**
 * A deterministic prime test for N < 32 bit using fast trial division.
 * 
 * @author Tilman Neumann
 */
public class TDivPrimeTest {
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(TDivPrimeTest.class);

	private static final int NUM_PRIMES_FOR_31_BIT_TDIV = 4793;

	private int[] primes;
	private long[] pinv;
	
	// lazy-initialized singleton
	private static TDivPrimeTest the_instance = null;
	
	/**
	 * @return the only TDivPrimeTest instance (singleton)
	 */
	public static synchronized final TDivPrimeTest getInstance() {
		if (the_instance == null) {
			the_instance = new TDivPrimeTest();
		}
		return the_instance;
	}

	private TDivPrimeTest() {
		AutoExpandingPrimesArray smallPrimesProvider = AutoExpandingPrimesArray.get();
		primes = new int[NUM_PRIMES_FOR_31_BIT_TDIV];
		pinv = new long[NUM_PRIMES_FOR_31_BIT_TDIV];
		for (int i=0; i<NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
			int p = smallPrimesProvider.getPrime(i);
			primes[i] = p;
			pinv[i] = (1L<<32)/p;
		}
	}
	
	public boolean isPrime(int N) {
		if (N==1) return false;
		if ((N&1)==0) return N==2;
		
		// if N is odd and composite then the loop runs maximally up to prime = floor(sqrt(N)); unroll the loop.
		int i=1;
		int unrolledLimit = NUM_PRIMES_FOR_31_BIT_TDIV-8;
		for ( ; i<unrolledLimit; i++) {
			if ((1 + (int) ((N*pinv[i])>>32)) * primes[i] == N) return primes[i]==N;
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i]==N;
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i]==N;
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i]==N;
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i]==N;
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i]==N;
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i]==N;
			if ((1 + (int) ((N*pinv[++i])>>32)) * primes[i] == N) return primes[i]==N;
		}
		for ( ; i<NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
			if ((1 + (int) ((N*pinv[i])>>32)) * primes[i] == N) return primes[i]==N;
		}
		// N is prime
		return true;
	}
}
