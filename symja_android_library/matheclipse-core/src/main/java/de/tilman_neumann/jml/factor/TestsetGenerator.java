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
package de.tilman_neumann.jml.factor;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.siqs.KnuthSchroeppel;
import de.tilman_neumann.jml.primes.probable.BPSWTest;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Generation of random N that are not too easy to factor.
 * 
 * @author Tilman Neumann
 */
public class TestsetGenerator {
//	private static final Logger LOG = Logger.getLogger(TestsetGenerator.class);
//	private static final boolean DEBUG = false;
	private static final boolean SELECT = false;
	
	private static final BPSWTest bpsw = new BPSWTest();
	private static final KnuthSchroeppel multiplierFinder = new KnuthSchroeppel(); // used to compute the multiplier k

	/** random generator */
	private static final SecureRandom RNG = new SecureRandom();

	/**
	 * Generate N_count random numbers of the given bit length.
	 * @param bits
	 * @param N_count
	 * @return test set
	 */
	public static ArrayList<BigInteger> generate(int bits, int N_count) {
		ArrayList<BigInteger> NSet = new ArrayList<BigInteger>();
		int minBits = (bits+2)/3; // analogue to 3rd root(N)
		int maxBits = (bits+1)/2;
		for (int i=0; i<N_count; ) {
			// generate random N with 2 prime factors
			int n1bits = uniformRandomInteger(minBits, maxBits);
			BigInteger n1 = new BigInteger(n1bits, RNG);
			if (n1bits>0) n1 = n1.setBit(n1bits-1);
			n1 = bpsw.nextProbablePrime(n1);
			int n2bits = bits-n1.bitLength();
			BigInteger n2 = new BigInteger(n2bits, RNG);
			if (n2bits>0) n2 = n2.setBit(n2bits-1);
			n2 = bpsw.nextProbablePrime(n2);
			BigInteger N = n1.multiply(n2);
		
			// Skip cases where the construction above failed to produce the correct bit length
			if (N.bitLength() != bits) continue;
			
			if (SELECT) {
				// skip N that do not match the selection criterion
				int k = multiplierFinder.computeMultiplier(N);
				int kNMod = BigInteger.valueOf(k).multiply(N).mod(I_8).intValue();
				if (kNMod == 1) continue;
			}
			
//			if (DEBUG) {
//				assertTrue(n1bits >= minBits);
//				assertTrue(n1bits <= maxBits);
//				LOG.debug("TestsetGenerator3: minBits = " + minBits + ", maxBits = " + maxBits + ", n1.bitLength() = " + n1.bitLength());
//				assertTrue(n1.bitLength() >= minBits);
//				assertTrue(n1.bitLength() <= maxBits);
//				int resultBits = N.bitLength();
//				LOG.debug("TestsetGenerator3: wanted bits = " + bits + ", result bits = " + resultBits);
//				assertTrue(resultBits >= bits-1);
//				assertTrue(resultBits <= bits+1);
//			}
			NSet.add(N);
			i++;
		}
		return NSet;
	}
	
	/**
	 * Creates a random integer from the uniform distribution U[minValue, maxValue-1].
	 * Works also for negative arguments; the only requirement is maxValue>minValue.
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	private static int uniformRandomInteger(int minValue, int maxValue) {
		int normedMaxValue = Math.max(1, maxValue - minValue);
		return RNG.nextInt(normedMaxValue) + minValue;
	}
}
