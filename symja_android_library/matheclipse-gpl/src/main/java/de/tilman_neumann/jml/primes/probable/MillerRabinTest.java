/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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

import static de.tilman_neumann.jml.base.BigIntConstants.I_1;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Miller-Rabin probable prime test.
 * @author Tilman Neumann
 */
// TODO use Montgomery multiplication/exponentiation
public class MillerRabinTest {
	
	private Random rng = ThreadLocalRandom.current();

	private BigInteger N, Nm1, D;
	private int lsb;
	
	/**
	 * Perform up to numberOfRounds Miller-Rabin tests with random bases.
	 * @param N
	 * @param numberOfRounds
	 * @return true if N passes all tests, false if N is composite
	 */
	public boolean isProbablePrime(BigInteger N, int numberOfRounds) {
		this.setInput(N);
        
		// test rounds
		int N_bits = N.bitLength();
        for (int round = 0; round < numberOfRounds; round++) {
        	// get random base x with 1 < x < N
            BigInteger x;
            do {
                x = new BigInteger(N_bits, rng);
            } while (x.compareTo(I_1) <= 0 || x.compareTo(N) >= 0);
            
            // do Miller-Rabin test to base x
            if (testSingleBase(x) == false) return false; // surely composite
        }

        return true; // probable prime
	}
	
	/**
	 * Perform Miller-Rabin test of N to several bases.
	 * @param N
	 * @param bases
	 * @return true if N is probable prime, false if N is composite
	 */
	public boolean testBases(BigInteger N, BigInteger[] bases) {
		this.setInput(N);
        for (BigInteger x : bases) {
            if (testSingleBase(x) == false) return false; // surely composite
        }
        return true; // probable prime
	}

	/**
	 * Perform a single Miller-Rabin test of N to base x.
	 * @param N
	 * @param x the base
	 * @return true if N is probable prime, false if N is composite
	 */
	public boolean testSingleBase(BigInteger N, BigInteger x) {
		this.setInput(N);
		return testSingleBase(x);
	}

	/**
	 * Set the argument N for a bunch of base tests.
	 * @param N
	 */
	private void setInput(BigInteger N) {
		this.N = N;
		Nm1 = N.subtract(I_1);
		// N-1 = 2^lsb * D, D odd
        lsb = Nm1.getLowestSetBit();
        D = Nm1.shiftRight(lsb);
	}

	/**
	 * Perform a single Miller-Rabin test of N to base x.
	 * setInput(N) must have been called before.
	 * @param x the base
	 * @return true if N passes the test to base x, false if N is composite
	 */
	private boolean testSingleBase(BigInteger x) {
        //if (x.compareTo(N) >= 0) x = x.mod(N); // not required
        BigInteger test = x.modPow(D, N);
        if ((test.equals(I_1)) || test.equals(Nm1)) return true;

		for (int i=1; i<lsb; i++) {
	        test = test.multiply(test).mod(N);
            if (test.equals(I_1)) return false;
            if (test.equals(Nm1)) return true;
		}

		return false;
	}
}
