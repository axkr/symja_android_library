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
package de.tilman_neumann.jml.primes.probable;

import static de.tilman_neumann.jml.base.BigIntConstants.I_1;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Miller-Rabin probable prime test.
 * @author Tilman Neumann
 */
public class MillerRabinTest {
	
	private Random rng = ThreadLocalRandom.current();

	/**
	 * Perform up to numberOfRounds Miller-Rabin tests with random bases.
	 * @param N
	 * @param numberOfRounds
	 * @return true if N passes all tests, false if N is composite
	 */
	public boolean isProbablePrime(BigInteger N, int numberOfRounds) {
		// init
		BigInteger N_m1 = N.subtract(I_1);
        int lsb = N_m1.getLowestSetBit();
        BigInteger N_m1_without2s = N_m1.shiftRight(lsb);
        
		// test rounds
		int N_bits = N.bitLength();
        for (int round = 0; round < numberOfRounds; round++) {
        	// get random base x with 1 < x < N
            BigInteger x;
            do {
                x = new BigInteger(N_bits, rng);
            } while (x.compareTo(I_1) <= 0 || x.compareTo(N) >= 0);
            
            // do Miller-Rabin test to base x
            int l = 0;
            BigInteger test = x.modPow(N_m1_without2s, N);
            if ((!test.equals(I_1)) && !test.equals(N_m1)) {
                if (++l == lsb) return false;
                test = test.multiply(test).mod(N);
                while (!test.equals(N_m1)) {
                    if (test.equals(I_1) || ++l == lsb) return false;
                    test = test.multiply(test).mod(N);
                }
            }
        }

        return true;
	}
	
	/**
	 * Perform a single Miller-Rabin test of N to base x.
	 * @param N
	 * @param x
	 * @return true if N passes the test to base x, false if N is composite
	 */
	public boolean testSingleBase(BigInteger N, BigInteger x) {
		// init
		BigInteger N_m1 = N.subtract(I_1);
        int lsb = N_m1.getLowestSetBit();
        BigInteger N_m1_without2s = N_m1.shiftRight(lsb);
        
		// test base x
        int l = 0;
        BigInteger test = x.modPow(N_m1_without2s, N);
        if ((!test.equals(I_1)) && !test.equals(N_m1)) {
            if (++l == lsb) return false;
            test = test.multiply(test).mod(N);
            while (!test.equals(N_m1)) {
                if (test.equals(I_1) || ++l == lsb) return false;
                test = test.multiply(test).mod(N);
            }
        }
        return true;
	}
}
