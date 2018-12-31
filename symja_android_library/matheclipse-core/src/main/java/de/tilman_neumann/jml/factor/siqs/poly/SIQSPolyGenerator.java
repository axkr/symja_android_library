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
package de.tilman_neumann.jml.factor.siqs.poly;

import java.math.BigInteger;
import java.util.Arrays;

import de.tilman_neumann.jml.base.UnsignedBigInt;
import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;
import de.tilman_neumann.jml.factor.siqs.poly.baseFilter.BaseFilter;
import de.tilman_neumann.jml.factor.siqs.poly.baseFilter.BaseFilter_q1;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParams;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS;
import de.tilman_neumann.jml.gcd.EEA31;
import de.tilman_neumann.util.Timer;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * A generator for SIQS polynomials.
 * 
 * A very important parameter is <code>qCount</code. Having it configurable via the constructor lets us test
 * which number of factors of <code>a</code> and thus which average factor size is most appropriate for 
 * given magnitudes of kN.
 * 
 * Since t^2 == N (mod p) equals t^2 == (N%p) (mod p), we can compute Tonelli-Shanks in all-integer arguments.
 * For SIQS this is no big improvement though, because Tonelli-Shanks is not time-critical anyway.
 * 
 * @author Tilman Neumann
 */
public class SIQSPolyGenerator implements PolyGenerator {
//	private static final Logger LOG = Logger.getLogger(SIQSPolyGenerator.class);
//	private static final boolean DEBUG = false;

	/** the a paramater */
	private BigInteger a;
	/** the a-term of the polynomial, i.e. 2a for kN == 1 mod 8), a else */
	private BigInteger da;
	private UnsignedBigInt da_UBI;
	/** the b-parameter */
	private BigInteger b;
	
	private int k;
	private BigInteger kN;
	/** the d-parameter of the polynomial Q2(x) = (d*a*x + b)^2 - kN */
	private int d;
	
	// prime base
	private int mergedBaseSize;
	private BaseArrays baseArrays;

	/** generator for a-parameters */
	private AParamGenerator aParamGenerator;
	/** the actual number of factors of the a-parameter for given kN */
	private int qCount;
	/** the prime base elements that are the factors of a */
	private int[] qArray;
	/** the modular sqrt's of the prime base elements q that are the factors of a */
	private int[] qtArray;
	/** the number of b-values we can have for one a */
	private int maxBIndex;
	/** the number of b-values already used */
	private int bIndex;
	/** basic Bl required to compute b */
	private BigInteger[] B2Array;
	private UnsignedBigInt[] B2Array_UBI;
	
	// solution arrays
	private int filteredBaseSize;
	private SolutionArrays solutionArrays;
	
	private EEA31 eea = new EEA31();
	private BaseFilter baseFilter;
	
	private Sieve sieveEngine;
	private TDiv_QS tDivEngine;

	// profiling
	private boolean profile;
	private Timer timer = new Timer();
	private int aParamCount, bParamCount;
	private long aDuration, firstBDuration, filterPBDuration, firstXArrayDuration, nextBDuration, nextXArrayDuration;
	
	public SIQSPolyGenerator() {
		this.baseFilter = new BaseFilter_q1();
	}
	
	@Override
	public String getName() {
		return "SIQSPoly(" + baseFilter.getName() + ")";
	}

	@Override
	public void initializeForN(
			int k, BigInteger N, BigInteger kN, int d, SieveParams sieveParams, BaseArrays baseArrays,
			AParamGenerator aParamGenerator, Sieve sieveEngine, TDiv_QS tDivEngine, boolean profile) {
		
		this.k = k;
		this.kN = kN;
		this.d = d;
		
		this.baseArrays = baseArrays;
		this.mergedBaseSize = baseArrays.primes.length;
		
		// initialize sub-engines
		this.aParamGenerator = aParamGenerator;
		sieveEngine.initializeForN(sieveParams, mergedBaseSize, profile);
		this.sieveEngine = sieveEngine;
		final double N_dbl = N.doubleValue();
		tDivEngine.initializeForN(N_dbl, kN, sieveParams.maxQRest, profile);
		this.tDivEngine = tDivEngine;

		// B2: the array needs one more element because used indices start at 1.
		// the B2 entries are non-negative, so we can use UnsignedBigInt.mod(int), which is much faster than BigInteger.mod(BigInteger).
		qCount = aParamGenerator.getQCount();
		B2Array = new BigInteger[qCount];
		B2Array_UBI = new UnsignedBigInt[qCount];
		// set bIndex=maxBIndex=0 to indicate that the first polynomial is wanted
		bIndex = maxBIndex = 0;
		
		// Allocate filtered base and solution arrays: The true size may be smaller if powers are filtered out, too.
		solutionArrays = new SolutionArrays(mergedBaseSize - qCount, qCount);
		
		// profiling
		this.profile = profile;
		aParamCount = 0;
		bParamCount = 0;
		aDuration = 0;
		firstBDuration = 0;
		filterPBDuration = 0;
		firstXArrayDuration = 0;
		nextBDuration = 0;
		nextXArrayDuration = 0;
	}
	
	@Override
	public void nextPolynomial() {
		if (bIndex==maxBIndex) {
			// Incrementing bIndex would exceed the maximum value -> we need a new a-parameter.
			// Computing a-parameters is very fast (typically 0 to 15ms) despite synchronization.
			if (profile) timer.capture();
			synchronized (aParamGenerator) {
				a = aParamGenerator.computeNextAParameter();
				qArray = aParamGenerator.getQArray();
				qtArray = aParamGenerator.getQTArray();
			}
			da = BigInteger.valueOf(d).multiply(a);
			da_UBI = new UnsignedBigInt(da);
			aParamCount++;
			maxBIndex = 1<<(qCount-1); // 2^(qCount-1)
			if (profile) aDuration += timer.capture();
			// compute the first b
			computeFirstBParameter();
			bParamCount++;
			bIndex = 1;
//			if (DEBUG) {
//				LOG.debug("first a=" + a + ", b=" + b);
//				LOG.debug("(b^2-kN)/a [" + bIndex + "] = " + b.multiply(b).subtract(kN).divide(a));
//			}
			if (profile) firstBDuration += timer.capture();
			// filter prime base
			BaseFilter.Result filterResult = baseFilter.filter(solutionArrays, baseArrays, mergedBaseSize, qArray, qCount, k);
			filteredBaseSize = filterResult.filteredBaseSize;
			if (profile) filterPBDuration += timer.capture();
			// compute ainvp[], Bainv2[][] and solution x-arrays for a and first b
			computeFirstXArrays();
			// pass data to sub-engines
			sieveEngine.initializeForAParameter(solutionArrays, filteredBaseSize);
			tDivEngine.initializeForAParameter(da, b, solutionArrays, filteredBaseSize, filterResult.filteredOutBaseElements);
			if (profile) firstXArrayDuration += timer.capture();
		} else {
			// compute the next b-parameter:
			// the gray code v with 2^v || 2*i in [Contini97] is the exponent
			// at which 2*i contains the 2. Here we have bIndex instead of Contini's "i".
			if (profile) timer.capture();
			int v = Integer.numberOfTrailingZeros(bIndex<<1);
			// bIndex/2^v is a half-integer. Therefore we have ceilTerm = ceil(bIndex/2^v) = bIndex/2^v + 1.
			// If ceilTerm is even, then (-1)^ceilTerm is positive and B_l[v] must be added.
			// Slightly faster is: if ceilTerm-1 = bIndex/2^v is odd then B_l[v] must be added.
			boolean bParameterNeedsAddition = ((bIndex>>v) & 1) == 1;
			// WARNING: In contrast to the description in [Contini p.10, 2nd paragraph],
			// WARNING: b must not be computed (mod a) !
			b = bParameterNeedsAddition ? b.add(B2Array[v-1]) : b.subtract(B2Array[v-1]);
			tDivEngine.setBParameter(b);
			bParamCount++;
//			if (DEBUG) {
//				//LOG.debug("a = " + a + ", b = " + b);
//				assertTrue(0<v && v<qCount); // exact
//				// Contini defines computations for (i+1)th polynomial in i
//				// -> the second b-parameter is computed with i=1, the third b-parameter with i=2, etc.
//				// -> do asserts involving bIndex before bIndex is incremented
//				assertTrue((2*bIndex) % Math.pow(2, v) == 0);
//				assertTrue((2*bIndex) % Math.pow(2, v+1) > 0);
//				if (d == 2) {
//					// b is odd
//					assertEquals(I_1, b.and(I_1));
//					// with Kechlibars polynomial and multiplier k with kN == 1 (mod 8) we have b^2 == kN (mod 4a)
//					assertEquals(I_0, b.multiply(b).subtract(kN).mod(a.multiply(I_4)));
//				} else {
//					assertEquals(I_0, b.multiply(b).subtract(kN).mod(a)); // we have b^2 == kN (mod a)
//				}
//			}
			bIndex++;
//			if (DEBUG) {
//				LOG.debug("a=" + a + ": " + bIndex + ".th b=" + b);
//				LOG.debug("(b^2-kN)/a [" + bIndex + "] = " + b.multiply(b).subtract(kN).divide(a));
//			}
			if (profile) nextBDuration += timer.capture();

			// Update solution arrays: 
			// Since only the array-content is modified, the x-arrays in poly are updated implicitly.
			// This approach would work in a multi-threaded SIQS implementation too, if we create a new thread for each new a-parameter.
			// Note that fix prime divisors depend only on a and k -> they do not change at a new b-parameter.
			computeNextXArrays(v, bParameterNeedsAddition);
			if (profile) nextXArrayDuration += timer.capture();
		}
	}

	/**
	 * Compute the B-array and the first b-parameter.
	 */
	private void computeFirstBParameter() {
		// compute 2*B_l[] and the first b; the notation is mostly following [Contini97]
		this.b = I_0;
		for (int l=0; l<qCount; l++) {
			int ql = qArray[l];
			int t = qtArray[l];
			BigInteger ql_big = BigInteger.valueOf(ql);
			BigInteger a_div_ql = a.divide(ql_big); // exact
			// the modular inverse is small enough to fit into int, but for the product below we need long precision
			long a_div_ql_modInv_ql = eea.modularInverse(a_div_ql.mod(ql_big).intValue(), ql);
			
			// Compute gamma according to Contini: Good for kN == 3, 5, 7 (mod 8).
			// Using the smaller choice of gamma is optional but seems to improve performance.
			int gamma = (int) ((t * a_div_ql_modInv_ql) % ql);
			if (gamma > (ql>>1)) gamma = ql - gamma;

			// Alternative: Compute gamma according to Kechlibar: Good for kN == 1 (mod 8)
			//long gamma = t * a_div_ql_modInv_ql;

			BigInteger Bl = a_div_ql.multiply(BigInteger.valueOf(gamma));
			B2Array[l] = Bl.shiftLeft(1); // store 2 * B_l in B2[0]...B2[s-1] (the last one is only required to compute b below)
			B2Array_UBI[l] = new UnsignedBigInt(B2Array[l]);
			// WARNING: In contrast to the description in [Contini p.10, 2nd paragraph],
			// WARNING: b must not be computed (mod a) !
			b = b.add(Bl);
			
//			if (DEBUG) {
//				LOG.debug("qArray = " + Arrays.toString(qArray));
//				LOG.debug("t = " + t + ", ql = " + ql + ", a_div_ql_modInv_ql = " + a_div_ql_modInv_ql + ", gamma = " + gamma + ", Bl = " + Bl);
//				assertEquals(a_div_ql.modInverse(ql_big).longValue(), a_div_ql_modInv_ql);
//				assertTrue(Bl.compareTo(I_0) >= 0);
//				assertEquals(I_0, Bl.multiply(Bl).subtract(kN).mod(ql_big));
//				//assertEquals(t % ql, Bl.mod(ql_big).intValue()); // does not hold if we choose the smaller gamma
//				assertEquals(I_0, Bl.mod(a_div_ql));
//				for (int l2=0; l2<qCount; l2++) {
//					if (l2 != l) {
//						BigInteger ql2 = BigInteger.valueOf(qArray[l2]);
//						assertEquals(I_0, Bl.mod(ql2));
//					}
//				}
//			}
		}
		
		// For d==2: If b is even then make it odd [Kechlibar 2005, p.22] 
		if (d == 2 && (b.intValue() & 1) == 0) b = b.add(a); // even/odd test needs only the lowest bit

//		if (DEBUG) {
//			LOG.debug ("a = " + a + ", b = " + b + ", b^2 = " + b.multiply(b) + ", kN = " + kN);
//			LOG.debug ("b^2 % 8 = " + b.multiply(b).mod(I_8) + ", kN % 8 = " + kN.mod(I_8));
//			// initial b are positive
//			assertTrue(b.signum() >= 0);
//			if (d == 2) {
//				// b is odd
//				assertEquals(I_1, b.and(I_1));
//				// with Kechlibars polynomial and multiplier k with kN == 1 (mod 8) we have b^2 == kN (mod 4a)
//				//assertEquals(ZERO, b.multiply(b).subtract(kN).mod(a.multiply(TWO)));
//				assertEquals(I_0, b.multiply(b).subtract(kN).mod(a.multiply(I_4)));
//			} else {
//				// we have b^2 == kN (mod a)
//				assertEquals(I_0, b.multiply(b).subtract(kN).mod(a));
//			}
//		}
	}

	/**
	 * Compute the ainvp[] required for Bainv2[][], the Bainv2[][] required to compute next b's and next x-arrays,
	 * and the first x-arrays.
	 * 
	 * The x-arrays contain the smallest non-negative solutions x_1,2 of (ax+b)^2-kN == 0 (mod p)
	 * for the first b-parameter and for all p in the prime base.
	 * 
	 * All modular inverses 1/a % p exist because the q's whose product gives the a-parameter have been filtered out before.
	 */
	private void computeFirstXArrays() { // performance-critical !
		// the first b is always positive, so we can use UnsignedBigInt here
		UnsignedBigInt b_UBI = new UnsignedBigInt(b);
		
		int[] filteredPowers = solutionArrays.powers;
		int[] filteredTArray = solutionArrays.tArray;
		int[][] Bainv2Array = solutionArrays.Bainv2Array;
		int[] x1Array = solutionArrays.x1Array;
		int[] x2Array = solutionArrays.x2Array;
		for (int pIndex=filteredBaseSize-1; pIndex>0; pIndex--) { // we do not need solutions for p[0]=2
			// 1. compute ainvp ------------------------------------------------------------------
			// All modular inverses 1/a % p exist because the q's whose product gives the a-parameter have been filtered out before.
			// Since 1/a % p = 1/(a%p) % p, we can compute the modular inverse in ints, which is much faster than with BigIntegers.
			// ainvp needs long precision in the products below.
			final int p = filteredPowers[pIndex];
			final long ainvp = eea.modularInverse(da_UBI.mod(p), p);
			
			// 2. compute Bainv2[] -----------------------------------------------------------
			for (int j=qCount-2; j>=0; j--) { // Contini's j=1...s-1. The maximum value of v is qCount-2 == s-1.
				// Bainv2 = 2 * B_j * (1/a) mod p.
				Bainv2Array[j][pIndex] = (int) ((B2Array_UBI[j].mod(p) * ainvp) % p); // much faster than BigInteger.mod(BigInteger)
			}
			
			// 3. compute first x-array entries -----------------------------------------------
			final int t = filteredTArray[pIndex];
			final int bModP = b_UBI.mod(p);
			// x1 = (1/a)* (+t - b) (mod p)
			int t_minus_b_modP = t - bModP;
			if (t_minus_b_modP < 0) t_minus_b_modP += p;
			x1Array[pIndex] = (int) ((ainvp * t_minus_b_modP) % p);
			// x2 = (1/a)* (-t - b) (mod p): For p=2 and p|k, there is no distinct second solution.
			// The number of these primes is very small, so one could think a case distinction on t makes no sense.
			// Nevertheless, without that distinction we could get p-t-bModP = p, and another case distinction would be necessary...
			if (t>0) { // there is a second solution
				int minus_t_minus_b_modP = p -t - bModP;
				if (minus_t_minus_b_modP < 0) minus_t_minus_b_modP += p;
				x2Array[pIndex] = (int) ((ainvp * minus_t_minus_b_modP) % p);
			} else { // only one solution
				x2Array[pIndex] = x1Array[pIndex];
			}
			
//			if (DEBUG) {
//				BigInteger p_big = BigInteger.valueOf(p);
//				try {
//					assertEquals(ainvp, da.modInverse(p_big).longValue());
//					assertTrue(ainvp > 0); // p that have no modular inverse (1/a) % p have been filtered out
//				} catch (ArithmeticException | AssertionError ae) {
//					LOG.debug("p = " + p + ", ainvp = " + ainvp + ", da = " + da + ": " + ae, ae);
//				}
//
//				for (int j=qCount-2; j>=0; j--) { // Contini's j=1...s-1. The maximum value of v is qCount-2 == s-1.
//					assertTrue(0<=Bainv2Array[j][pIndex] && Bainv2Array[j][pIndex]<p);
//				}
//				assertEquals(b.mod(BigInteger.valueOf(p)).intValue(), bModP);
//				assertTrue(0 <= bModP & bModP < p);
//				assertTrue(0 <= t & t < p);
//
//				assertTrue(0<=t_minus_b_modP && t_minus_b_modP<p);
//				if (t>0) {
//					int minus_t_minus_b_modP = p -t - bModP;
//					if (minus_t_minus_b_modP < 0) minus_t_minus_b_modP += p;
//					assertTrue(0<=minus_t_minus_b_modP && minus_t_minus_b_modP<p);
//				}
//				// x1,x2 were chosen such that p divides Q
//				int x1 = x1Array[pIndex];
//				if (t==0) assertEquals(x1, (int) ((ainvp * (p - bModP)) % p));
//				
//				BigInteger Q1 = da.multiply(BigInteger.valueOf(x1)).add(b).pow(2).subtract(kN);
//				assertEquals(I_0, Q1.mod(p_big));
//				int x2 = x2Array[pIndex];
//				BigInteger Q2 = da.multiply(BigInteger.valueOf(x2)).add(b).pow(2).subtract(kN);
//				assertEquals(I_0, Q2.mod(p_big));
//				if (x1<0 || x2<0) LOG.debug("p=" + p + ", ainvp=" + ainvp + ": x1 = " + x1 + ", x2 = " + x2);
//			}
		} // end_for (primes)
	}

	/**
	 * Update the entries of the solution arrays for the next b-parameter.
	 * @param v gray code, with v in [1, ..., qCount-1]
	 * @param xArraysNeedSubtraction true if ceil(bIndex/2^v) is even, (-1)^ceil(bIndex/2^v) == +1
	 */
	private void computeNextXArrays(int v, boolean xArraysNeedSubtraction) { // performance-critical !
		// update solution arrays:
		// Note that trial division needs the solutions for all primes p,
		// even if the sieve leaves out the smallest p[i] with i < pMinIndex.
		int[] filteredPowers = solutionArrays.powers;
		int[] Bainv2Row = solutionArrays.Bainv2Array[v-1];
		int[] x1Array = solutionArrays.x1Array;
		int[] x2Array = solutionArrays.x2Array;
		// WARNING: The correct case distinction depending on the sign of (-1)^ceil(bIndex/2^v)
		// WARNING: is just the opposite of [Contini, table p.14, last 2 lines]
		if (xArraysNeedSubtraction) {
			// (-1)^ceil(bIndex/2^v) == +1 -> Bainv2 must be subtracted
			for (int pIndex=filteredBaseSize-1; pIndex>0; pIndex--) {
				final int p = filteredPowers[pIndex];
				final int Bainv2 = Bainv2Row[pIndex];
				int x1 = x1Array[pIndex] - Bainv2;
				x1Array[pIndex] = x1<0 ? x1+p : x1; // faster than (mod p)
				int x2 = x2Array[pIndex] - Bainv2;
				x2Array[pIndex] = x2<0 ? x2+p : x2;
			} // end for (primes)
		} else {
			// (-1)^ceil(bIndex/2^v) == -1 -> Bainv2 must be added
			for (int pIndex=filteredBaseSize-1; pIndex>0; pIndex--) {
				final int p = filteredPowers[pIndex];
				final int Bainv2 = Bainv2Row[pIndex];
				int x1 = x1Array[pIndex] + Bainv2; // Bainv2 >= 0
				x1Array[pIndex] = x1>=p ? x1-p : x1;
				int x2 = x2Array[pIndex] + Bainv2;
				x2Array[pIndex] = x2>=p ? x2-p : x2;
			} // end for (primes)
		}
//		if (DEBUG) debugNextXArrays(v);
	}

//	private void debugNextXArrays(int v) {
//		int[] filteredPowers = solutionArrays.powers;
//		int[][] Bainv2Array = solutionArrays.Bainv2Array;
//		int[] x1Array = solutionArrays.x1Array;
//		int[] x2Array = solutionArrays.x2Array;
//		for (int pIndex=filteredBaseSize-1; pIndex>0; pIndex--) {
//			int p = filteredPowers[pIndex];
//			int Bainv2 = Bainv2Array[v-1][pIndex];
//			int x1 = x1Array[pIndex];
//			int x2 = x2Array[pIndex];
//			assertTrue(0 <= x1 && x1 < p);
//			assertTrue(0 <= x2 && x2 < p);
//			BigInteger p_big = BigInteger.valueOf(p);
//			assertEquals(kN.mod(p_big), da.multiply(BigInteger.valueOf(x1)).add(b).pow(2).mod(p_big));
//			assertEquals(kN.mod(p_big), da.multiply(BigInteger.valueOf(x2)).add(b).pow(2).mod(p_big));
////			if (x1<0 || x2<0) LOG.debug("p=" + p + ", Bainv2=" + Bainv2 + ": x1 = " + x1 + ", x2 = " + x2);
//		}
//	}

	@Override
	public PolyReport getReport() {
		return new PolyReport(aParamCount, bParamCount, aDuration, firstBDuration, filterPBDuration, firstXArrayDuration, nextBDuration, nextXArrayDuration);
	}
	
	@Override
	public void cleanUp() {
		baseArrays = null;
		solutionArrays = null;
		sieveEngine = null;
		tDivEngine = null;
		// aParamGenerator is cleaned in (P)SIQS main class
	}
}
