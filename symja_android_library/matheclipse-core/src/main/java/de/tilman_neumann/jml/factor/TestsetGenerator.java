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

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.primes.probable.BPSWTest;
import de.tilman_neumann.util.ConfigUtil;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Generation of random N that are not too easy to factor.
 * The standard case are semiprimes N where the smaller factor of N is >= cbrt(N).
 * 
 * @author Tilman Neumann
 */
public class TestsetGenerator {
	private static final Logger LOG = Logger.getLogger(TestsetGenerator.class);
	private static final boolean DEBUG = false;
	private static final boolean DUMP_DATA_TO_FILE = false;
	
	private static final BPSWTest bpsw = new BPSWTest();
	private static final SecureRandom RNG = new SecureRandom();
	
	/**
	 * Generate N_count random numbers of the given size and nature.
	 * @param N_count number of test numbers to generate
	 * @param bits size of test numbers to generate
	 * @param mode the nature of test numbers to generate
	 * @return test set
	 */
	public static BigInteger[] generate(int N_count, int bits, TestNumberNature mode) {
		BigInteger[] NArray = new BigInteger[N_count];
		PrintWriter writer = null;
		try {
			if (DUMP_DATA_TO_FILE) {
				writer = new PrintWriter("testdata_" + bits + "bit", "UTF-8");
			}
			
			switch (mode) {
				case RANDOM_COMPOSITES: {
					if (bits<3) throw new IllegalArgumentException("There are no composites with " + bits + " bits.");
					for (int i=0; i<N_count; ) {
						BigInteger N = new BigInteger(bits, RNG);
						if(N.bitLength()==bits && !bpsw.isProbablePrime(N)) {
							NArray[i++] = N;
							// TODO write to data file
						}
					}
					break;
				}
				case RANDOM_ODD_COMPOSITES: {
					if (bits<4) throw new IllegalArgumentException("There are no odd composites with " + bits + " bits.");
					for (int i=0; i<N_count; ) {
						BigInteger N = new BigInteger(bits, RNG).or(I_1); // odd random number
						if(N.bitLength()==bits && !bpsw.isProbablePrime(N)) {
							NArray[i++] = N;
							// TODO write to data file
						}
					}
					break;
				}
				case MODERATE_SEMIPRIMES: {
					if (bits<4) throw new IllegalArgumentException("There are no odd semiprimes with " + bits + " bits.");
					int minBits = (bits+2)/3;
					int maxBits = (bits+1)/2;
					for (int i=0; i<N_count; ) {
						// Generate random N with 2 prime factors > cbrt(N). This implementation achieves a high degree
						// of randomness while still being reasonably fast for large bit sizes.
						int n1bits = uniformRandomInteger(minBits, maxBits);
						BigInteger n1 = new BigInteger(n1bits, RNG);
						n1 = bpsw.nextProbablePrime(n1);
						if (n1.bitLength()<minBits) continue;
						
						BigInteger N = new BigInteger(bits, RNG);
						BigInteger n2 = bpsw.nextProbablePrime(N.divide(n1));
						N = n1.multiply(n2);
						if (N.bitLength() != bits) continue;
						if (n1.pow(3).compareTo(N) < 0) continue;
		
						NArray[i++] = N;
						if (writer!=null) writer.println(N + ", " + n1 + ", "+ n2);
					}
					break;
				}
				case QUITE_HARD_SEMIPRIMES: {
					if (bits<4) throw new IllegalArgumentException("There are no odd semiprimes with " + bits + " bits.");
					int minBits = (bits-1)/2;
					for (int i=0; i<N_count; ) {
						// generate random N with 2 prime factors
						BigInteger n1 = new BigInteger(minBits, RNG);
						n1 = n1.setBit(minBits-1);
						n1 = bpsw.nextProbablePrime(n1);
						int n2bits = bits-n1.bitLength();
						BigInteger n2 = new BigInteger(n2bits, RNG);
						n2 = n2.setBit(n2bits-1);
						n2 = bpsw.nextProbablePrime(n2);
						BigInteger N = n1.multiply(n2);
						if (DEBUG) LOG.debug("bits=" + bits + ", N1Bits=" + n1.bitLength() + ", N2Bits=" + n2.bitLength());
						
						// Skip cases where the construction above failed to produce the correct bit length
						if (N.bitLength() != bits) continue;
						NArray[i++] = N;
						if (writer!=null) writer.println(N + ", " + n1 + ", "+ n2);
					}
					break;
				}
				default: throw new IllegalArgumentException("TestsetGeneratorMode " + mode);
			}
		} catch (IOException e) {
			LOG.error("IOException writing test data to file: " + e, e);
		} finally {
			if (writer!=null) writer.close();
		}
		return NArray;
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
		return RNG.nextInt() % (normedMaxValue+1) + minValue;
		// the above has more entropy than RNG.nextInt(normedMaxValue) + minValue
	}
	
	/**
	 * A simple main function to generate hard semi-primes.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		ConfigUtil.initProject();

		for (int bits = 330; bits<=450; bits+=10) {
			BigInteger num = generate(1, bits, TestNumberNature.QUITE_HARD_SEMIPRIMES)[0];
			LOG.info("// " + bits + " bits:");
			LOG.info(num);
		}
	}
}
