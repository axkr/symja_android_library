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

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.BigIntConverter;
//import de.tilman_neumann.util.ConfigUtil;

/**
 * Fast sqrt() computation with integer solutions using Herons (or "Babylonian") method
 * and the built-in Math.sqrt() as initial guess.
 * 
 * @author Tilman Neumann
 */
public class SqrtInt {
//	private static final Logger LOG = Logger.getLogger(SqrtInt.class);
	private static final SecureRandom RNG = new SecureRandom();

	/**
	 * sqrt() computation with integer solutions using Herons (or "Babylonian") method
	 * and the built-in Math.sqrt() as initial guess, for any argument size and avoiding BigDecimals.
	 * 
	 * @param N
	 * @return [lower, upper] int values of sqrt(N)
	 */
	public static BigInteger[] iSqrt/*_v01*/(BigInteger N) {
		// Make sure N is a positive number
		int sign = N.signum();
		if (sign>0) {
			int bits = N.bitLength();
			if (bits >= 1024) {
				// N has >= 1024 bits -> we can not compute sqrt(N) in doubles.
				// Thus we create a number n = N>>shiftsRight with an even number of right shifts,
				// and n having 1022 or 1023 bits. we compute the sqrt(n) and shift the result back by half
				// of the right shifts applied before. an even number of right shifts means that we have
				// an integer number of left shifts. The result is a great initial guess for Herons method.
				int shiftsRight = bits - 1023;
				if ((shiftsRight & 1) == 1) shiftsRight++; // make even -> n looses 1 bit of precision
				double sqrt = Math.sqrt(N.shiftRight(shiftsRight).doubleValue());
				BigInteger initialGuess = BigIntConverter.fromDoubleMulPow2(sqrt, shiftsRight>>1);
				//LOG.debug(i + ".th root(" + N + "): initialGuess = " + initialGuess);
				return iSqrt(N, initialGuess);
			} else if (bits >= 127) {
				// N has 127..1023 bits -> the argument fits into double, no shifts required.
				// the result has 64..511 bits which means some Heron steps are required.
				BigInteger initialGuess = BigIntConverter.fromDouble(Math.sqrt(N.doubleValue()));
				//LOG.debug(i + ".th root(" + N + "): initialGuess = " + initialGuess);
				return iSqrt(N, initialGuess);
			} else if (bits >= 107) {
				// N has 107..126 bits -> too big to get around without Heron steps, but small enough to let
				// the resulting sqrt fit into long -> BigInteger construction is faster.
				// N with 127 bits should fit here too, but showed very bad performance.
				BigInteger initialGuess = BigInteger.valueOf( (long) Math.sqrt(N.doubleValue()));
				//LOG.debug(i + ".th root(" + N + "): initialGuess = " + initialGuess);
				return iSqrt(N, initialGuess);
			} else if (bits >= 64) {
				// N has 64...106 bits -> the resulting sqrt has a size of 32...53 bits and 52 bits (double) precision.
				// so the absolute result error is <= 1 and can be corrected with the final comparison, without Heron steps
				long sqrt = (long) Math.sqrt(N.doubleValue()); // floor
				BigInteger sqrt_big = BigInteger.valueOf(sqrt);
				int cmp = sqrt_big.multiply(sqrt_big).compareTo(N);
				if (cmp==0) return new BigInteger[] {sqrt_big, sqrt_big};
				if (cmp<0) return new BigInteger[] {sqrt_big, BigInteger.valueOf(sqrt+1)};
				return new BigInteger[] {BigInteger.valueOf(sqrt-1), sqrt_big};
			} else {
				// N has <= 63 bits -> all computations fit into long, the resulting sqrt has a size of <= 31 bit
				// and a precision of 52 bit -> the absolute error is <= 1, no Heron steps needed
				long N_long = N.longValue();
				long sqrt = (long) Math.sqrt(N_long); // floor
				BigInteger sqrt_big = BigInteger.valueOf(sqrt);
				long cmp = sqrt*sqrt - N_long; // int is not sufficient for cmp!
				if (cmp==0) return new BigInteger[] {sqrt_big, sqrt_big};
				if (cmp<0) return new BigInteger[] {sqrt_big, BigInteger.valueOf(sqrt+1)};
				return new BigInteger[] {BigInteger.valueOf(sqrt-1), sqrt_big};
			}
		}
		if (sign==0) return new BigInteger[] {I_0, I_0};
		throw new IllegalArgumentException("n = " + N + ", but sqrt(n) is defined for n>=0 only!");
	}

	/**
	 * Simplest Heron-type sqrt() implementation.
	 * Here we use guess - n/guess as convergence criterion, which is simple and correct.
	 * 
	 * @param n
	 * @return [lower, upper] int values of sqrt(n)
	 */
	public static BigInteger[] iSqrt(BigInteger n, BigInteger guess) {
		// do one approximation step before first convergence check
		guess = n.divide(guess).add(guess).shiftRight(1);
		//LOG.debug("initial guess: sqrt(" + n + ") ~ " + guess);
		
		BigInteger lastGuess;
		do {
			lastGuess = guess;
			guess = n.divide(guess).add(guess).shiftRight(1);
			//LOG.debug("next guess: sqrt(" + n + ") ~ " + guess);
		} while (guess.subtract(lastGuess).abs().bitLength()>1); // while absolute difference > 1
		
		int cmp = guess.multiply(guess).compareTo(n);
		if (cmp < 0) return new BigInteger[] {guess, guess.add(I_1)};
		if (cmp > 0) return new BigInteger[] {guess.subtract(I_1), guess};
		return new BigInteger[] {guess, guess}; // exact sqrt()
	}
	
	// test -----------------------------------------------------------
	
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
//		for (int bits = 100; bits<=130; bits+=1) {
//			LOG.info("test correctness of sqrt() implementations for " + bits + "-bit numbers...");
//			ArrayList<BigInteger> testSet = createTestSet(nCount, bits);
//		   	for (BigInteger testNum : testSet) {
//		   		testCorrectness(testNum, bits, iSqrt/*_v01*/(testNum), "v01");
//		   	}
//		}
//	}

//	private static void testCorrectness(BigInteger testNum, int bits, BigInteger[] result, String algStr) {
//   		BigInteger lower = result[0];
//   		BigInteger lowerSquare = lower.multiply(lower);
//   		if (lowerSquare.compareTo(testNum) > 0) LOG.error(algStr + ": ERROR at " + bits + " bits: lower bound of sqrt(" + testNum + ") = " + lower + " is too big");
//   		BigInteger upper = result[1];
//   		BigInteger upperSquare = upper.multiply(upper);
//   		if (upperSquare.compareTo(testNum) < 0) LOG.error(algStr + ": ERROR at " + bits + " bits: upper bound of sqrt(" + testNum + ") = " + upper + " is too small");
//   		
//   		if (lowerSquare.equals(testNum) || upperSquare.equals(testNum)) {
//   			if (!lower.equals(upper)) LOG.error(algStr + ": ERROR at " + bits + " bits: sqrt(" + testNum + ") is exact, but the computed bounds = [" + lower + ", " + upper + "] are different!");
//   		} else {
//   			if (upper.subtract(lower).compareTo(I_1)>0) LOG.error(algStr + ": ERROR at " + bits + " bits: lower and upper bound of sqrt(" + testNum + ") = [" + lower + ", " + upper + "] differ by more than 1");
//   		}
//	}

	private static void testPerformance(int nCount) {
		for (int bits = 10; ; bits += 10 /*RNG.nextInt(50)*/) {
			ArrayList<BigInteger> testSet = createTestSet(nCount, bits);
//			LOG.info("test sqrt of " + bits + "-bit numbers:");
			long t0, t1;
		   	t0 = System.currentTimeMillis();
		   	for (BigInteger testNum : testSet) {
		   		iSqrt/*_v01*/(testNum);
		   	}
		   	t1 = System.currentTimeMillis();
//			LOG.info("   v01 sqrt with " + nCount + " numbers took " + (t1-t0) + " ms");
		}
	}

	/**
	 * Test.
	 * @param args ignored
	 */
//	public static void main(String[] args) {
//	   	ConfigUtil.initProject();
//	   	testCorrectness(100000);
//	   	testPerformance(1000000);
//	}
}
