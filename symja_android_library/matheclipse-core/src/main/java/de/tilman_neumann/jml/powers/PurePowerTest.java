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
package de.tilman_neumann.jml.powers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.UnsignedBigInt;
import de.tilman_neumann.jml.gcd.Gcd31;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.jml.roots.Roots;
import de.tilman_neumann.jml.roots.SqrtExact;
//import de.tilman_neumann.util.ConfigUtil;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
/**
 * Test for pure powers (with exponent >= 2).
 * 
 * Thanks to Graeme Willoughby for countless suggestions, which dramatically improved performance (something like factor 200 compared to PSIQS 01)
 * 
 * @author Tilman Neumann
 */
// TODO: A p-adic implementation like in gmp would be much faster for large numbers (with thousands of digits)
public class PurePowerTest {
//	private static final Logger LOG = Logger.getLogger(PurePowerTest.class);

	private static final double LN_2 = Math.log(2);
	private static final double LN_3 = Math.log(3);
	
	private Gcd31 gcdEngine = new Gcd31();
	private AutoExpandingPrimesArray primesArray = AutoExpandingPrimesArray.get();

	public static class Result {
		public BigInteger base;
		public int exponent;
		
		public Result(BigInteger base, int exponent) {
			this.base = base;
			this.exponent = exponent;
		}
	}
	
	/**
	 * Test if N is a pure power.</br></br>
	 * 
	 * The algorithm is based on {@link https://en.wikipedia.org/wiki/Rational_sieve#Limitations_of_the_algorithm},
	 * with several improvements pointed out by to Graeme Willoughby:</br>
	 * 
	 * 1. Write an even number as N=(2^m)k (k odd). Then its p.th power (2^m)k)^p = (2^mp)k^p has the binary representation
     *    <somenumber><pm zeros>. So there is a fast bit pattern test for even N:
     *    If the number of trailing zeros in N is not equal to zero (mod p), then N is not a p.th power</br>
	 * 2. All even powers will have been detected as squares, all powers that are multiples of three will have been detected
	 *    as cubes and so on. In short, we only need to test prime exponents up to log3 N, not all exponents.</br>
	 * 3. A square test is of course faster than an i.th power test with argument 2.</br>
	 * 
	 * @param N
	 * @return prime power representing N, or null if N is no pure power.
	 */
	public Result test_v01(BigInteger N) {
		if (N.compareTo(I_4)<0) return null; // negative or not composite
		if (N.bitCount()==1) {
			// only 1 bit set -> N is a power of 2
			return new Result(I_2, N.getLowestSetBit());
		}
		
		// square test
		BigInteger exactSqrt = SqrtExact.exactSqrt(N);
		if (exactSqrt!=null) return new Result(exactSqrt, 2);
		
		// test higher powers
		int lsb = N.getLowestSetBit();
		// ln(N) upper bound that works for arguments > Double.MAX_VALUE:
		// We have bitLength(N) >= ld(N) for all N --> ln(2) * bitLength(N) >= ln(N)
		double lnN = N.bitLength() * LN_2;
		double log3N = lnN/LN_3;
		int bIndex = 1; // skip 2
		if (lsb > 0) {
			// N is even -> we can test bit patterns before the full i.th root
			BigInteger N_reduced = N.shiftRight(lsb);
			for (int b = 3; b<log3N; b=primesArray.getPrime(++bIndex)) {
				//LOG.debug("test b = " + b);
				// if the number of trailing zeros in N is not equal to 0 (mod b), then N is not a b.th power
				if (lsb % b != 0) continue;
				// full b.th root required
				BigInteger floor_bthRoot = Roots.ithRoot(N_reduced, b)[0];
				if (floor_bthRoot.pow(b).equals(N_reduced)) {
					// found exact power!
					return new Result(floor_bthRoot.shiftLeft(lsb/b), b);
				}
			}
		} else {
			// N is odd
			for (int b = 3; b<log3N; b=primesArray.getPrime(++bIndex)) {
				//LOG.debug("test b = " + b);
				BigInteger floor_bthRoot = Roots.ithRoot(N, b)[0];
				if (floor_bthRoot.pow(b).equals(N)) {
					// found exact power!
					return new Result(floor_bthRoot, b);
				}
			}
		}
		// no pure power
		return null;
	}

	/**
	 * Much faster power test, elaborated together with Graeme Willoughby.
	 * @param N
	 * @return base and exponent, or null if N is no pure power
	 */
	public Result test/*_v02*/(BigInteger N) {
		if (N.compareTo(I_4)<0) return null; // negative or not composite
		if (N.bitCount()==1) return new Result(I_2, N.getLowestSetBit()); // N>=4 and only 1 bit set -> N is a power of 2
		
		// square test: we could skip it for odd lsb but it makes no notable difference
		BigInteger exactSqrt = SqrtExact.exactSqrt(N);
		if (exactSqrt!=null) return new Result(exactSqrt, 2);

		// test higher powers
		int lsb = N.getLowestSetBit();
		if (lsb == 0) {
			// N is odd
			// ln(N) upper bound that works for arguments > Double.MAX_VALUE:
			// We have bitLength(N) >= ld(N) for all N --> ln(2) * bitLength(N) >= ln(N)
			double lnN = N.bitLength() * LN_2;
			// In the following loop we check divisibility of N by some small primes a.
			// If some a divides N, we determine the exponent which then is the biggest possible exponent, maxExp.
			// If no a<aMax divides N, then we have already excluded exponents b>bMax with N ~ aMax^bMax <=> bMax = ln(N)/ln(aMax)
			// As such, we do not need aMax = ln(N)/ln(3); it is rather a performance trade-off.
			double aMax = lnN/2; // this limit seems to give good performance
			int maxExp = 0;
			UnsignedBigInt N_UBI = new UnsignedBigInt(N);
			int intLen = (N.bitLength()+31)/32;
			UnsignedBigInt rest = new UnsignedBigInt(new int[intLen]);
			UnsignedBigInt quotient = new UnsignedBigInt(new int[intLen]);
			UnsignedBigInt tmp;
			int a, aIndex=1;
			for (a = 3; a<aMax; a=primesArray.getPrime(++aIndex)) {
				int exp = 0;
				int rem = N_UBI.divideAndRemainder(a, quotient);
				if (rem > 0) continue;
				do {
					// exact division
					exp++;
					// assign quotient to rest
					tmp = rest;
					rest = quotient;
					quotient = tmp;
					// next division
					rem = rest.divideAndRemainder(a, quotient);
				} while (rem == 0);
				
				// N = a^exp * rest. If we had another exact division before, say N = a0^maxExp * rest0, 
				// then the biggest possible exponent is gcd(exp, maxExp). Note also that gcd(0,exp)=exp.
				maxExp = (int) gcdEngine.gcd(maxExp, exp);
				// If maxExp = 1 then N can not be a power;
				// if maxExp = 2, N can not be a square because we already did a square test.
				// The same holds for all maxExp being a power of 2.
				if ((maxExp & (maxExp-1)) == 0) return null;
				// At this point we'ld have a couple of other options:
				// * doing an exp.th root test
				// * leave the loop if maxExp==3 or if it falls below some threshold
				// But experiments showed that it is fastest to keep on searching, with the chance to find maxExp<3 above
			}
			
			int bMax = maxExp>0 ? maxExp : (int) (1 + lnN/Math.log(a)); // +1 resolves precision issues
			UnsignedBigInt N_square = new UnsignedBigInt(N.multiply(N));
			int b2 = 3, b2Index = 1; // skip 2
			int bIndex = 1; // skip 2
			for (int b = 3; b<=bMax; b=primesArray.getPrime(++bIndex)) {
				if (maxExp>0 && (maxExp%b != 0)) continue;
				// Sieve out some powers: If (2b+1) is prime, then N = x^b is only possible if N^2 == 1 (mod (2b+1)) or if (2b+1) | N
				final int b2p = (b<<1) + 1;
				while (b2 < b2p) b2 = primesArray.getPrime(b2Index++);
				//LOG.debug("test b = " + b + ", b2 = " + b2);
				if (b2 == b2p) {
					// 2*b+1 is prime
					final int mod = N_square.mod(b2p);
					//LOG.debug("N = " + N + ", N^2 % b2 = " + mod);
					if (mod > 1) continue;
				}
				// Full root required.
				BigInteger[] root = Roots.ithRoot(N, b);
				if (root[0].equals(root[1])) {
					// found exact power!
					return new Result(root[0], b);
				}
			}
			return null; // no pure power
		}
		
		// N is even
		// If lsb = 1 then N can not be a power;
		// if lsb = 2, N can not be a square because we already did a square test.
		// The same holds for all lsb being a power of 2.
		if ((lsb & (lsb-1)) == 0) return null;
		
		// Since N is even we can test bit patterns before the full i.th root
		BigInteger N_reduced = N.shiftRight(lsb);
		UnsignedBigInt N_reduced_square = new UnsignedBigInt(N_reduced.multiply(N_reduced));
		int b2 = 3, b2Index = 1; // skip 2
		int bIndex = 1; // skip 2
		for (int b = 3; b<=lsb; b=primesArray.getPrime(++bIndex)) {
			//LOG.debug("test b = " + b);
			// N can only be a b.th power if b | lsb
			if (lsb % b != 0) continue;
			// Sieve out some powers: If (2b+1) is prime, then N = x^b is only possible if N^2 == 1 (mod (2b+1)) or if (2b+1) | N
			final int b2p = (b<<1) + 1;
			while (b2 < b2p) b2 = primesArray.getPrime(b2Index++);
			//LOG.debug("test b = " + b + ", b2 = " + b2);
			if (b2 == b2p) {
				// 2*b+1 is prime
				final int mod = N_reduced_square.mod(b2p);
				//LOG.debug("N = " + N + ", N^2 % b2 = " + mod);
				if (mod > 1) continue;
			}
			// full b.th root required
			BigInteger[] root = Roots.ithRoot(N_reduced, b);
			if (root[0].equals(root[1])) {
				// found exact power!
				return new Result(root[0].shiftLeft(lsb/b), b);
			}
		}
		return null; // no pure power
	}

	private static void testCorrectness(int nCount) {
	   	PurePowerTest powTest = new PurePowerTest();

	   	// create test set for performance test
	   	SecureRandom rng = new SecureRandom();
	   	for (int bits=10; bits<=50; bits+=5) {
//	   		LOG.info("Test correctness with " + nCount + " " + bits + "-bit numbers");
		   	ArrayList<BigInteger> testSet = new ArrayList<BigInteger>();
		   	for (int i=0; i<nCount; i++) {
		   		testSet.add(new BigInteger(bits, rng));
		   	}
		   	
		   	// test correctness:
		   	// pure powers are not unique, e.g. 3^9 == 27^3, thus we can only check if the final result is correct
		   	for (BigInteger testNum : testSet) {
		   		Result r1 = powTest.test_v01(testNum);
//	   			if (r1!=null) assertEquals(testNum, r1.base.pow(r1.exponent));
	   			
		   		Result r2 = powTest.test/*_v02*/(testNum);
//		   		assertEquals(r1==null, r2==null);
//	   			if (r2!=null) assertEquals(testNum, r2.base.pow(r2.exponent));
		   	}
	   	}
//	   	LOG.info("");
	}
	
	private static void testPerformance(int nCount) {
	   	PurePowerTest powTest = new PurePowerTest();

	   	// create test set for performance test
	   	SecureRandom rng = new SecureRandom();
	   	for (int bits=50; ; bits+=50) {
		   	ArrayList<BigInteger> testSet = new ArrayList<BigInteger>();
		   	for (int i=0; i<nCount; i++) {
		   		testSet.add(new BigInteger(bits, rng));
		   	}
	
		   	// test performance
		   	long t0, t1;
		   	t0 = System.currentTimeMillis();
		   	for (BigInteger testNum : testSet) {
		   		powTest.test_v01(testNum);
		   	}
		   	t1 = System.currentTimeMillis();
//			LOG.info("v01: Testing " + nCount + " " + bits + "-bit numbers took " + (t1-t0) + " ms");

		   	t0 = System.currentTimeMillis();
		   	for (BigInteger testNum : testSet) {
		   		powTest.test/*_v02*/(testNum);
		   	}
		   	t1 = System.currentTimeMillis();
//			LOG.info("v02: Testing " + nCount + " " + bits + "-bit numbers took " + (t1-t0) + " ms");
//			LOG.info("");
	   	}
	}
	
	private static void testInputs() {
	   	PurePowerTest powTest = new PurePowerTest();
	   	while(true) {
			try {
//				LOG.info("Insert test argument N:");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				String input = in.readLine().trim();
				//LOG.debug("input = >" + input + "<");
				BigInteger N = new BigInteger(input);
				Result purePower = powTest.test(N);
//				if (purePower == null) {
//					LOG.info("N = " + N + " is not a pure power.");
//				} else {
//					LOG.info("N = " + N + " = " + purePower.base + "^" + purePower.exponent + " is a pure power!");
//				}
			} catch (Exception ex) {
//				LOG.error("Error " + ex, ex);
			}
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
//	   	testInputs();
//	}
}
