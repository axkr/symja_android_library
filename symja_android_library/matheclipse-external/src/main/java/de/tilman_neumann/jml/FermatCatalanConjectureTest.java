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
package de.tilman_neumann.jml;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.powers.PurePowerTest;
import de.tilman_neumann.jml.roots.Roots;
import de.tilman_neumann.jml.roots.SqrtExact;
import de.tilman_neumann.util.ConfigUtil;

/**
 * Search for a^m + b^n = c^k with a,b,c , m,n,k integer, a,b,c coprime, and 1/m+1/n+1/k<1.
 * 
 * The latter condition exludes (m,n,k) = (2,2,k), (2,3,3), (2,3,4), (2,3,5) with infinitely many solutions;
 * (2,3,6); and (2,4,4) and (3,3,3) not having any solutions.
 * 
 * If we find a solution with m,n,k > 2 then we have a counterexample to Beal's conjecture and win the 100k dollar prize.
 * But note that the strong Beal conjecture states that there are no more solutions than the 10 already found.
 * 
 * @see "https://de.wikipedia.org/wiki/Fermat-Catalan-Vermutung"
 * @see "http://mathworld.wolfram.com/Fermat-CatalanConjecture.html"
 * @see "http://mathworld.wolfram.com/BealsConjecture.html"
 * 
 * @author Tilman Neumann
 */
public class FermatCatalanConjectureTest {
	
	private static final Logger LOG = Logger.getLogger(FermatCatalanConjectureTest.class);
	
	private static final int b_COUNT = 10000000;
	
	private static final PurePowerTest powerTest = new PurePowerTest();
	private static final Gcd63 gcdEngine = new Gcd63();

	private static void test(int m, int n) {
		// Catalan's conjecture means that the only solution with x=1 or y=1 is 2^3 + 1^n = 3^2 -> no need to test that
		for (int a=2; ; a++) {
			LOG.info("Test a=" + a);
			BigInteger aPow = BigInteger.valueOf(a).pow(m);
			long nthRoot = Roots.ithRoot(aPow, n)[0].longValue();
			// Case 1: test a^m + b^n = c^k with a,b>0
			long bMin = Math.max(0, nthRoot - b_COUNT);
			long bMax = nthRoot + b_COUNT;
			for (long b = bMax; b>=bMin; b--) {
				if (gcdEngine.gcd(a, b) == 1) {
					// a and b are relative prime
					BigInteger bPow = BigInteger.valueOf(b).pow(n);
					BigInteger lhs = aPow.add(bPow);
					PurePowerTest.Result rhs = powerTest.test(lhs);
					if (rhs != null) {
						LOG.info("Case 1 found solution " + a + "^" + m + " + " + b + "^" + n + " = " + rhs.base + "^" + rhs.exponent);
						LOG.debug(n + ".th root(a^m) = " + nthRoot + ", b - nthRoot = " + (b-nthRoot));
					}
				}
			}
			// Case 2: test a^m - b^n = c^k with a^m > b^n -> we only want b<nthRoot
			for (long b = nthRoot; b>=bMin; b--) {
				if (gcdEngine.gcd(a, b) == 1) {
					// a and b are relative prime
					BigInteger bPow = BigInteger.valueOf(b).pow(n);
					BigInteger lhs = aPow.subtract(bPow);
					PurePowerTest.Result rhs = powerTest.test(lhs);
					if (rhs != null) {
						LOG.info("Case 2 found solution " + a + "^" + m + " = " + b + "^" + n + " + " + rhs.base + "^" + rhs.exponent);
						LOG.debug(n + ".th root(a^m) = " + nthRoot + ", b - nthRoot = " + (b-nthRoot));
					}
				}
			}
			// Case 3: test b^n - a^m = c^k with b^n > a^m -> we only want b>nthRoot
			for (long b = nthRoot+1; b<=bMax; b++) {
				if (gcdEngine.gcd(a, b) == 1) {
					// a and b are relative prime
					BigInteger bPow = BigInteger.valueOf(b).pow(n);
					BigInteger lhs = bPow.subtract(aPow);
					PurePowerTest.Result rhs = powerTest.test(lhs);
					if (rhs != null) {
						LOG.info("Case 3 found solution " + b + "^" + n + " = " + a + "^" + m + " + " + rhs.base + "^" + rhs.exponent);
						LOG.debug(n + ".th root(a^m) = " + nthRoot + ", b - nthRoot = " + (b-nthRoot));
					}
				}
			}
		}
	}

	/**
	 * If Beal's conjecture is correct, one of the powers has to be a square.
	 * This test is about 7 times faster than the general test.
	 * 
	 * @param m
	 * @param n
	 */
	@SuppressWarnings("unused")
	private static void testAssumingBealsConjecture(int m, int n) {
		// Catalan's conjecture means that the only solution with x=1 or y=1 is 2^3 + 1^n = 3^2 -> no need to test that
		for (int a=2; ; a++) {
			LOG.info("Test a=" + a);
			BigInteger aPow = BigInteger.valueOf(a).pow(m);
			long nthRoot = Roots.ithRoot(aPow, n)[0].longValue();
			// Case 1: test a^m + b^n = c^k with a,b>0
			long bMin = Math.max(0, nthRoot - b_COUNT);
			long bMax = nthRoot + b_COUNT;
			for (long b = bMax; b>=bMin; b--) {
				if (gcdEngine.gcd(a, b) == 1) {
					// a and b are relative prime
					BigInteger bPow = BigInteger.valueOf(b).pow(n);
					BigInteger lhs = aPow.add(bPow);
					BigInteger sqrt = SqrtExact.exactSqrt(lhs);
					if (sqrt != null) {
						LOG.info("Case 1 found solution " + a + "^" + m + " + " + b + "^" + n + " = " + sqrt + "^2");
						LOG.debug(n + ".th root(a^m) = " + nthRoot + ", b - nthRoot = " + (b-nthRoot));
					}
				}
			}
			// Case 2: test a^m - b^n = c^k with a^m > b^n -> we only want b<nthRoot
			for (long b = nthRoot; b>=bMin; b--) {
				if (gcdEngine.gcd(a, b) == 1) {
					// a and b are relative prime
					BigInteger bPow = BigInteger.valueOf(b).pow(n);
					BigInteger lhs = aPow.subtract(bPow);
					BigInteger sqrt = SqrtExact.exactSqrt(lhs);
					if (sqrt != null) {
						LOG.info("Case 2 found solution " + a + "^" + m + " = " + b + "^" + n + " + " + sqrt + "^2");
						LOG.debug(n + ".th root(a^m) = " + nthRoot + ", b - nthRoot = " + (b-nthRoot));
					}
				}
			}
			// Case 3: test b^n - a^m = c^k with b^n > a^m -> we only want b>nthRoot
			for (long b = nthRoot+1; b<=bMax; b++) {
				if (gcdEngine.gcd(a, b) == 1) {
					// a and b are relative prime
					BigInteger bPow = BigInteger.valueOf(b).pow(n);
					BigInteger lhs = bPow.subtract(aPow);
					BigInteger sqrt = SqrtExact.exactSqrt(lhs);
					if (sqrt != null) {
						LOG.info("Case 3 found solution " + b + "^" + n + " = " + a + "^" + m + " + " + sqrt + "^2");
						LOG.debug(n + ".th root(a^m) = " + nthRoot + ", b - nthRoot = " + (b-nthRoot));
					}
				}
			}
		}
	}

	/**
	 * Case 2 can be tested pretty fast for small m-n>0.
	 * @param m
	 * @param n
	 * @param aMax
	 */
	private static void testCase2(int m, int n, int aMax) {
		// Catalan's conjecture means that the only solution with x=1 or y=1 is 2^3 + 1^n = 3^2 -> no need to test that
		for (int a=2; a<aMax; a++) {
			//LOG.info("Test a=" + a);
			BigInteger aPow = BigInteger.valueOf(a).pow(m);
			long nthRoot = Roots.ithRoot(aPow, n)[0].longValue();
			// Case 2: test a^m - b^n = c^k with a^m > b^n -> we only want b<nthRoot
			for (long b = nthRoot; b>=2; b--) {
				if (gcdEngine.gcd(a, b) == 1) {
					// a and b are relative prime
					BigInteger bPow = BigInteger.valueOf(b).pow(n);
					BigInteger lhs = aPow.subtract(bPow);
					PurePowerTest.Result rhs = powerTest.test(lhs);
					if (rhs != null) {
						LOG.info("Case 2 found solution " + a + "^" + m + " = " + b + "^" + n + " + " + rhs.base + "^" + rhs.exponent);
						LOG.debug(n + ".th root(a^m) = " + nthRoot + ", b - nthRoot = " + (b-nthRoot));
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		ConfigUtil.initProject();
		// Easiest solution: 2^3 + 1^n = 3^2. We do not search for that.
//		test(5, 4);
		// Tested with b_COUNT=10M until a=90
		// Case 3 found solution 3^4 = 2^5 + 7^2                  nthRoot(a^m) = 2, b - nthRoot = 1
		// Case 1 found solution 3^5 + 11^4 = 122^2               nthRoot(a^m) = 3, b - nthRoot = 8
		test(7, 3);
		// Tested with b_COUNT=10M until a=230
		// Case 1 found solution 2^7 + 17^3 = 71^2                nthRoot(a^m) = 5, b - nthRoot = 12
		// Case 1 found solution 17^7 + 76271^3 = 21063928^2      nthRoot(a^m) = 743, b - nthRoot = 75528      (Beukers, Zagier)
		// Case 2 found solution 65^7 = 1414^3 + 2213459^2        nthRoot(a^m) = 16987, b - nthRoot = -15573   (Beukers, Zagier)
		// Case 2 found solution 113^7 = 9262^3 + 15312283^2      nthRoot(a^m) = 61732, b - nthRoot = -52470   (Beukers, Zagier)
//		test(8, 3);
		// Case 3 found solution 15613^3 = 33^8 + 1549034^2       nthRoot(a^m) = 11203, b - nthRoot = 4410     (Beukers, Zagier)
		// Case 1 found solution 43^8 + 96222^3 = 30042907^2      nthRoot(a^m) = 22694, b - nthRoot = 73528    (Beukers, Zagier)
		//test(9, 3);
		// Case 2 found solution 2^9 = 7^3 + 13^2                 nthRoot(a^m) = 8, b - nthRoot = -17
		
		// tests assuming Beal's conjecture
//		testAssumingBealsConjecture(5, 4); // Tested with b_COUNT=10M until a=200
//		testAssumingBealsConjecture(6, 4); // Tested with b_COUNT=10M until a=200
//		testAssumingBealsConjecture(7, 3); // Tested with b_COUNT=10M until a=840
//		testAssumingBealsConjecture(7, 4); // Tested with b_COUNT=10M until a=600
//		testAssumingBealsConjecture(8, 3); // Tested with b_COUNT=10M until a=200
//		testAssumingBealsConjecture(9, 3); // Tested with b_COUNT=10M until a=320
//		testAssumingBealsConjecture(10, 3); // Tested with b_COUNT=10M until a=269
//		testAssumingBealsConjecture(11, 3); // Tested with b_COUNT=10M until a=850
//		testAssumingBealsConjecture(12, 3); // Tested with b_COUNT=10M until a=246
//		testAssumingBealsConjecture(12, 3); // Tested with b_COUNT=10M until a=200
//		testAssumingBealsConjecture(13, 3); // Tested with b_COUNT=10M until a=200
		
		// Case 2 test, pretty fast if m/2 > m-n
		int aMax = 1000;
		for (int n=3, m=2*n-1; ; m++, n++) {
			LOG.info("Test Case2(" + m + ", " + n + ", k) for a<" + aMax);
			testCase2(m, n, aMax);
		}
	}
}
