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
package de.tilman_neumann.jml.squareSums;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.gaussianInteger.GaussianInteger;
import de.tilman_neumann.jml.precision.Precision;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.jml.quaternion.HurwitzQuaternion;
import de.tilman_neumann.jml.transcendental.Ln;
import de.tilman_neumann.util.Ensure;
import de.tilman_neumann.util.Timer;

/**
 * An implementation of the algorithm of Pollack and Treviño that finds some four squares representation of an odd number n
 * in O((log n)^2 * (log log n)^-1) given that ERH (the extended Riemann hypothesis) holds.
 * 
 * @author Tilman Neumann
 * 
 * @see Pollack, Treviño: "Finding the four squares in Lagrange’s theorem", Integers: 18A (2018)
 * @see http://campus.lakeforest.edu/trevino/finding4squares.pdf
 */
public class FourSquaresFinder {
	private static final Logger LOG = LogManager.getLogger(FourSquaresFinder.class);

	private static final boolean DEBUG = false;
	
	public static final boolean ANALYZE = false; // if true then collect statistics

	private static final AutoExpandingPrimesArray PRIMES = AutoExpandingPrimesArray.get().ensurePrimeCount(1000); // modest size initialization

	private static final Random RNG = new Random(43);
	
	/** The bases of the 4 squares representation that was found. */
	private BigInteger X=null, Y=null, Z=null, W=null;
	
	/** The number of random iterations that were needed. */
	private int numberOfIterations = 0;
	
	private Timer timer = new Timer();
	
	private long step1Duration, step2Duration, step3Duration;
	private long step2nPowDuration, step2kDuration, step2pDuration, step2uDuration, step2sDuration, step2sSquareDuration;
	
	/**
	 * Full constructor. One instance may be used for several four squares decompositions,
	 * the timings are accumulated.
	 */
	public FourSquaresFinder() {
		if (ANALYZE) {
			step1Duration = step2Duration = step3Duration = 0;
			step2nPowDuration = step2kDuration = step2pDuration = step2uDuration = step2sDuration = step2sSquareDuration = 0;
		}
	}
	
	/**
	 * Find a four square representation of odd n.
	 * @param n an odd integer > 20
	 */
	public void find(BigInteger n) {
		if (!n.testBit(0)) {
			throw new IllegalArgumentException("n = " + n + " is not odd");
		}
		if (n.compareTo(I_20) <= 0) {
			throw new IllegalArgumentException("4 squares finder is still missing implementation for n <= 20 but n is " + n);
		}
		if (ANALYZE) timer.capture();
		
		// (1) [Precomputation] Determine the primes not exceeding log n and compute their product M.
		BigDecimal nDecimal = new BigDecimal(n);
		Precision nPrecision = Precision.of(nDecimal);
		if (DEBUG) LOG.debug("n has " + nPrecision);
		int pmax = Ln.ln(nDecimal, nPrecision.add(5)).intValue(); // floor
		BigInteger M = I_1;
		for (int i=0; ; i++) {
			int p = PRIMES.getPrime(i); // auto-expanding
			if (p > pmax) break;
			
			M = M.multiply(BigInteger.valueOf(p));
		}
		if (DEBUG) Ensure.ensureEquals(false, M.testBit(0)); // M is even
		BigInteger Mn = M.multiply(n);
		if (ANALYZE) step1Duration += timer.capture();
		
		// (2) [Random trials]
		BigInteger p, s; // output of step 2
		BigInteger nPow5 = n.pow(5);
		int nPow5Bits = nPow5.bitLength();
		if (ANALYZE) step2nPowDuration += timer.capture();
		
		for (numberOfIterations = 1; ; numberOfIterations++) { // iteration loop
			// Choose an odd number k < n^5 at random
			BigInteger k = new BigInteger(nPow5Bits, RNG);
			if (!k.testBit(0)) k = k.add(I_1); // make k odd
			if (k.compareTo(nPow5) >= 0) continue;
			if (ANALYZE) step2kDuration += timer.capture();

			// let p = Mnk - 1
			p = Mn.multiply(k).subtract(I_1);
			// (Notice that p == 1 (mod 4), since 2 || M and n, k are odd.)
			if (DEBUG) {
				Ensure.ensureEquals(I_1, p.and(I_3)); // p == 1 (mod 4)
				LOG.debug("p has " + p.bitLength() + " bit");
			}
			if (ANALYZE) step2pDuration += timer.capture();

			// choose random u ∈ [1, p-1]
			BigInteger u;
			do {
				u = new BigInteger(p.bitLength(), RNG).add(I_1);
			} while (u.compareTo(p) >= 0);
			if (ANALYZE) step2uDuration += timer.capture();

			// compute s = u^((p-1)/4) mod p
			BigInteger pm1 = p.subtract(I_1);
			s = u.modPow(pm1.shiftRight(2), p); // this is the absolute performance bottleneck of the whole algorithm
			if (ANALYZE) step2sDuration += timer.capture();

			// Test if s^2 == -1 (mod p). If so, continue to the next step. Otherwise, restart this step.
			BigInteger sSquare = s.multiply(s);
			if (sSquare.mod(p).equals(pm1)) break;
			if (ANALYZE) step2sSquareDuration += timer.capture();
		}
		if (ANALYZE) {
			step2sSquareDuration += timer.capture();
			step2Duration += step2nPowDuration + step2kDuration + step2pDuration + step2uDuration + step2sDuration + step2sSquareDuration;
		}

		/* *****************************************************************************************************
		 * Notes on step 2:
		 * 
		 * s^2 = (u^((p-1)/4))^2 (mod p) = u^((p-1)/2) (mod p)
		 * So if p is an odd prime, then by Euler's criterion we have s^2 = Legendre(u | p) (mod p).
		 * The algorithm finds most s^2 == -1 (mod p) for p that are prime.
		 * Since the modular power is by far the most expensive computation in this algorithm, one could think
		 * that "guarding" the modular power with a probable prime test could yield a performance improvement.
		 * But experiments showed that such an approach is slower;
		 * the solutions of s^2 == -1 (mod p) found by composite p seem to be important, too.
		 * 
		 * Consequently, the only way to improve performance seems to speed up the modular power itself.
		 * *****************************************************************************************************/
		
		//(3) [Denouement] Compute A+Bi := gcd(s+i, p). Then compute gcrd(A+Bi+j, n), normalized to have integer components.
		//    Write this gcrd as X + Yi + Zj + Wk, and output the representation n = X^2 + Y^2 + Z^2 + W^2.
		GaussianInteger gcd = new GaussianInteger(s, I_1).gcd(new GaussianInteger(p, I_0));
		BigInteger A = gcd.realPart();
		BigInteger B = gcd.imaginaryPart();
		HurwitzQuaternion gcrd = new HurwitzQuaternion(A, B, I_1, I_0, true).rightGcd(new HurwitzQuaternion(n, true));
		if (ANALYZE) step3Duration += timer.capture();
		
		X = gcrd.getX().abs();
		Y = gcrd.getY().abs();
		Z = gcrd.getZ().abs();
		W = gcrd.getW().abs();
		if (DEBUG) Ensure.ensureEquals(n, X.multiply(X).add(Y.multiply(Y)).add(Z.multiply(Z)).add(W.multiply(W)));
		
		// done
	}
	
	public BigInteger[] getSquareBases() {
		return new BigInteger[] {X, Y, Z, W};
	}
	
	public int getNumberOfIterations() {
		return numberOfIterations;
	}
	
	public String getPhaseTimings() {
		return "step1=" + step1Duration + "ms, step2=" + step2Duration + "ms, step3=" + step3Duration + "ms";
	}
	
	public String getStep2Subtimings() {
		return "n^5=" + step2nPowDuration + "ms, k=" + step2kDuration + "ms, p=" + step2pDuration + "ms, u=" + step2uDuration + "ms, s=" + step2sDuration + "ms, s^2=" + step2sSquareDuration + "ms";
	}
}
