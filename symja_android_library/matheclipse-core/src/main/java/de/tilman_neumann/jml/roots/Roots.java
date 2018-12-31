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
package de.tilman_neumann.jml.roots;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.BigIntConverter;
//import de.tilman_neumann.util.ConfigUtil;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * i.th root of integers.
 * 
 * The current state-of-the-art is a Heron-style algorithm with a good initial guess derived from double computations.
 * For very small <code>ld(N)/i</code> a linear algorithm is applied.</br></br>
 * 
 * The Heron-style algorithm realizes the iteration formula x(k+1) = 1/n * ( (n-1) * x(k) + N/(x(k)^(n-1)) ),
 * see {@link https://en.wikipedia.org/wiki/Nth_root_algorithm}.</br></br>
 * 
 * Thanks to Graeme Willoughby to point my nose to that algorithm.
 * 
 * @author Tilman Neumann
 */
public class Roots {
//	private static final Logger LOG = Logger.getLogger(Roots.class);
	private static final SecureRandom RNG = new SecureRandom();
//	private static final boolean DEBUG = false;
	private static final boolean TEST_BITWISE = false;

	/**
	 * Computes the i.th root of N, using either a bitwise correction approach (for rather big roots <code>i</code>)
	 * or a Heron iteration procedure (for rather small roots <code>i</code>). The decision point between the two algorithms
	 * is heuristic and may not be very accurate.
	 * 
	 * @param N argument
	 * @param i root
	 * @return [lower, upper] integer bounds of i.th root of N
	 */
	public static BigInteger[] ithRoot(BigInteger N, int i) {
		// heuristic derived from performance tests:
		// use bitwise approach for very small bits/i, otherwise use Heron algorithm
		// The transition point is exact !!
		return (N.bitLength()/i <= 2) ? ithRoot_bitwise(N, i) : ithRoot_Heron2(N, i);
	}

	/**
	 * Computes integer values around (i.th root(N)), using a bitwise correction approach.
	 * This algorithm is fast for very small <code>ld(N)/i</code>.
	 * 
	 * @param N argument
	 * @param i root
	 * @return [lower, upper] int values of i.th root(N)
	 */
	private static BigInteger[] ithRoot_bitwise(BigInteger N, int i) {
		BigInteger ret = I_0;
		int maxResultBitIndex = N.bitLength()/i;
		for (int bitIdx=maxResultBitIndex; bitIdx>=0; bitIdx--) {
			BigInteger next = ret.setBit(bitIdx);
			int cmp = N.compareTo(next.pow(i));
			if (cmp >= 0) {
				ret = next;
				if (cmp == 0) {
					return new BigInteger[] {ret, ret};
				}
			}
		}
		return new BigInteger[] {ret, ret.add(I_1)};
	}
	
	/**
	 * Heron-style i.th root implementation.
	 * 
	 * This algorithm is much faster than the bitwise correction except for <code>ld(N)/i <= 2 </code>.</br></br>
	 * 
	 * Notes:</br>
	 * 1. We use a good initial guess based on double computations.</br>
	 * 2. The main loop could have convergence problems for small <code>ld(N)/i</code>; but in these cases
	 * the initial guess is very good! Testing the initial guess before the main loop solves the problem.</br>
	 * 3. Doing an additional iteration step before the main loop does not pay out.</br></br>
	 * 
	 * Version 1 is sure to give the correct result because of the final step doing a loop.
	 * 
	 * @param N
	 * @param i
	 * @return [lower, upper] int values of i.th root(N)
	 */
	public static BigInteger[] ithRoot_Heron1(BigInteger N, int i) {
		BigInteger guess = computeInitialGuess(N, i);
		
		// For small ld(N)/i the initial guess is usually very accurate.
		// checking that we can resolve bad convergence problems we'ld get otherwise later on.
		int cmp = guess.pow(i).compareTo(N);
		if (cmp==0) return new BigInteger[] {guess, guess};
		if (cmp<0) {
			BigInteger guessPlus1 = guess.add(I_1);
			if (guessPlus1.pow(i).compareTo(N)>0) return new BigInteger[] {guess, guessPlus1};
		} else {
			BigInteger guessMinus1 = guess.subtract(I_1);
			if (guessMinus1.pow(i).compareTo(N)<0) return new BigInteger[] {guessMinus1, guess};
		}
		
		// The initial guess was not that good, do main loop
		int iMinus1 = i-1;
		BigInteger i_big = BigInteger.valueOf(i);
		BigInteger iMinus1_big = BigInteger.valueOf(iMinus1);
		try {
			BigInteger lastGuess;
			do {
				lastGuess = guess;
				guess = N.divide(guess.pow(iMinus1)).add(guess.multiply(iMinus1_big)).divide(i_big);
				//LOG.debug("next guess: " + i + ".th root(" + N + ") ~ " + guess);
			} while (guess.subtract(lastGuess).abs().bitLength()>1); // while absolute difference > 1
		} catch (ArithmeticException e) {
//			LOG.debug("N=" + N + ", i=" + i + ", guess = " + guess, e);
		}
		
		// final test: linear steps
		//int linStepCount = 0;
		cmp = guess.pow(i).compareTo(N);
		if (cmp < 0) {
			// guess is smaller than the exact result
			do {
				guess = guess.add(I_1);
				cmp = guess.pow(i).compareTo(N);
				//linStepCount++;
			} while (cmp < 0);
			// now guess >= exact result
			//if (linStepCount>0) LOG.info(i + ".th root(" + N + ") required " + linStepCount + " linear steps at the end.");
			return cmp==0 ? new BigInteger[] {guess, guess} : new BigInteger[] {guess.subtract(I_1), guess};
		}
		if (cmp > 0) {
			// guess is bigger than the exact result
			do {
				guess = guess.subtract(I_1);
				cmp = guess.pow(i).compareTo(N);
				//linStepCount++;
			} while (cmp > 0);
			// now guess <= exact result
			//if (linStepCount>0) LOG.info(i + ".th root(" + N + ") required " + linStepCount + " linear steps at the end.");
			return cmp==0 ? new BigInteger[] {guess, guess} : new BigInteger[] {guess, guess.add(I_1)};
		}
		return new BigInteger[] {guess, guess}; // exact sqrt()
	}

	/**
	 * Heron-style i.th root implementation.
	 * 
	 * This algorithm is much faster than the bitwise correction except for <code>ld(N)/i <= 2 </code>.</br></br>
	 * 
	 * Notes:</br>
	 * 1. We use a good initial guess based on double computations.</br>
	 * 2. The main loop could have convergence problems for small <code>ld(N)/i</code>; but in these cases
	 * the initial guess is very good! Testing the initial guess before the main loop solves the problem.</br>
	 * 3. Doing an additional iteration step before the main loop does not pay out.</br></br>
	 * 
	 * This implementation differs from version 1 as follows:</br>
	 * 4. Following {@link https://en.wikipedia.org/wiki/Nth_root_algorithm} we do
	 *    <code>delta(k) = 1/n * [N/x(k)^(n-1) - x(k)]; x(k+1) = x(k)+delta(k)</code>.</br>
	 * 5. The above allows for a simpler convergence check.</br>
	 * 6. The final step exploits that the guess after the main loop does not differ from the correct result
	 *    by more than 1.
	 * 
	 * @param N
	 * @param i
	 * @return [lower, upper] int values of i.th root(N)
	 */
	public static BigInteger[] ithRoot_Heron2(BigInteger N, int i) {
		BigInteger guess = computeInitialGuess(N, i);
		//LOG.debug("Compute " + i + ".th root(" + N + ") with initial guess = " + guess);
		
		// For small ld(N)/i the initial guess is usually very accurate.
		// checking that we can resolve bad convergence problems we'ld get otherwise later on.
		int cmp = guess.pow(i).compareTo(N);
		if (cmp==0) return new BigInteger[] {guess, guess};
		if (cmp<0) {
			BigInteger guessPlus1 = guess.add(I_1);
			if (guessPlus1.pow(i).compareTo(N)>0) return new BigInteger[] {guess, guessPlus1};
		} else {
			BigInteger guessMinus1 = guess.subtract(I_1);
			if (guessMinus1.pow(i).compareTo(N)<0) return new BigInteger[] {guessMinus1, guess};
		}
		
		// The initial guess was not that good, do main loop
		int iMinus1 = i-1;
		BigInteger i_big = BigInteger.valueOf(i);
		try {
			BigInteger delta;
			do {
				delta = N.divide(guess.pow(iMinus1)).subtract(guess).divide(i_big);
				guess = delta.add(guess);
				//LOG.debug("next guess: " + i + ".th root(" + N + ") ~ " + guess);
			} while (delta.abs().bitLength()>1); // while absolute difference > 1
		} catch (ArithmeticException e) {
//			LOG.debug("   N=" + N + ", i=" + i + ", guess = " + guess, e);
		}
		//LOG.debug("guess = " + guess);
		
		// final test
		cmp = guess.pow(i).compareTo(N);
		//LOG.debug("cmp = " + cmp);
		if (cmp==0) return new BigInteger[] {guess, guess}; // exact sqrt()
		if (cmp<0) {
			BigInteger guessPlus1 = guess.add(I_1);
			if (guessPlus1.pow(i).compareTo(N)>0) return new BigInteger[] {guess, guessPlus1};
			// else guess+1 is exact
//			if (DEBUG) assertEquals(guessPlus1.pow(i), N);
			return new BigInteger[] {guessPlus1, guessPlus1};
		} else {
			BigInteger guessMinus1 = guess.subtract(I_1);
			if (guessMinus1.pow(i).compareTo(N)<0) return new BigInteger[] {guessMinus1, guess};
			// else guess-1 is exact
//			if (DEBUG) assertEquals(guessMinus1.pow(i), N);
			return new BigInteger[] {guessMinus1, guessMinus1};
		}
	}

	private static BigInteger computeInitialGuess(BigInteger N, int i) {
		int bits = N.bitLength();
		if (bits >= 1024) {
			// right shift: the shift result should have at least the double mantissa precision which is 52 bit.
			// additionally, the shift result should have 1022 bits maximally to have one more bit for the
			// double multiplication with 2^(the fractional part of the shift) -> we use 1022 here
			int shiftsRight = bits - 1022;
			int shiftsLeft_int = shiftsRight/i; // truncated
			double shiftsLeft_frac = ((double) shiftsRight)/i - shiftsLeft_int;
			// compute root in double precision: double precision of exponent 1/i is better than float!
			double root = Math.pow(N.shiftRight(shiftsRight).doubleValue(), 1.0D/i) * Math.pow(2, shiftsLeft_frac);
			BigInteger initialGuess = BigIntConverter.fromDoubleMulPow2(root, shiftsLeft_int);
			//LOG.debug(i + ".th root(" + N + "): initialGuess = " + initialGuess);
			return initialGuess;
		}
		// no shifts required, double precision is sufficient
		BigInteger initialGuess = BigIntConverter.fromDouble(Math.pow(N.doubleValue(), 1.0D/i));
		//LOG.debug(i + ".th root(" + N + "): initialGuess = " + initialGuess);
		return initialGuess;
	}

   	/**
   	 * create test set for performance test: random ints with random bit length < 1000
   	 * @param nCount
   	 * @return
   	 */
	private static ArrayList<BigInteger> createTestSet(int nCount, int bits) {
	   	ArrayList<BigInteger> testSet = new ArrayList<BigInteger>();
	   	for (int i=0; i<nCount;) {
	   		BigInteger testNum = new BigInteger(bits, RNG);
	   		if (testNum.bitLength()<bits) continue; // not exact size, skip
	   		testSet.add(testNum);
	   		i++;
	   	}
	   	return testSet;
	}
	
//	private static void testCorrectness(int nCount) {
//		for (int bits = 100; bits<=1000; bits+=100) {
//			ArrayList<BigInteger> testSet = createTestSet(nCount, bits);
//			// test correctness
//		   	for (BigInteger testNum : testSet) {
//		   		int root = 2 + RNG.nextInt(48);
//		   		BigInteger[] linResult = Roots.ithRoot_bitwise(testNum, root);
//		   		BigInteger[] heronResult = Roots.ithRoot_Heron1(testNum, root);
//		   		if (!linResult[0].equals(heronResult[0])) {
//		   			LOG.error("ERROR: Heron1: lower bound of " + root + ".th root(" + testNum + "): linear algorithm -> " + linResult[0] + ", Heron1 -> " + heronResult[0]);
//		   		}
//		   		if (!linResult[1].equals(heronResult[1])) {
//		   			LOG.error("ERROR: Heron1: upper bound of " + root + ".th root(" + testNum + "): linear algorithm -> " + linResult[1] + ", Heron1 -> " + heronResult[1]);
//		   		}
//
//		   		heronResult = Roots.ithRoot_Heron2(testNum, root);
//		   		if (!linResult[0].equals(heronResult[0])) {
//		   			LOG.error("ERROR: Heron2: lower bound of " + root + ".th root(" + testNum + "): linear algorithm -> " + linResult[0] + ", Heron2 -> " + heronResult[0]);
//		   		}
//		   		if (!linResult[1].equals(heronResult[1])) {
//		   			LOG.error("ERROR: Heron2: upper bound of " + root + ".th root(" + testNum + "): linear algorithm -> " + linResult[1] + ", Heron2 -> " + heronResult[1]);
//		   		}
//		   	}
//		}
//	}

//	private static void testPerformance(int nCount) {
//		for (int bits = 100; ; bits+=100) {
//			ArrayList<BigInteger> testSet = createTestSet(nCount, bits);
//			for (int root=3; ((float)bits)/root > 4; root += 1+RNG.nextInt(10)) {
//				// RNG reduces compiler optimizations ? -> makes test results more comparable ?
//				LOG.info("test " + root + ".th root of " + bits + "-bit numbers:");
//				long t0, t1;
//				if (TEST_BITWISE) {
//				   	t0 = System.currentTimeMillis();
//				   	for (BigInteger testNum : testSet) {
//				   		Roots.ithRoot_bitwise(testNum, root);
//				   	}
//				   	t1 = System.currentTimeMillis();
//					LOG.info("   Bitwise ith root test with " + nCount + " numbers took " + (t1-t0) + " ms");
//				}
//		   		
//			   	t0 = System.currentTimeMillis();
//			   	for (BigInteger testNum : testSet) {
//			   		Roots.ithRoot_Heron1(testNum, root);
//			   	}
//			   	t1 = System.currentTimeMillis();
//				LOG.info("   Heron1 ith root test with " + nCount + " numbers took " + (t1-t0) + " ms");
//		   		
//			   	t0 = System.currentTimeMillis();
//			   	for (BigInteger testNum : testSet) {
//			   		Roots.ithRoot_Heron2(testNum, root);
//			   	}
//			   	t1 = System.currentTimeMillis();
//				LOG.info("   Heron2 ith root test with " + nCount + " numbers took " + (t1-t0) + " ms");
//			}
//		}
//	}

	/**
	 * Test.
	 * @param args ignored
	 */
//	public static void main(String[] args) {
//	   	ConfigUtil.initProject();
//	   	testCorrectness(10000);
//	   	testPerformance(5000000);
//	}
}
