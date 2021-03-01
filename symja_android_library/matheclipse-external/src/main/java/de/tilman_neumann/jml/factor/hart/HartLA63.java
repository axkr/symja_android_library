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
package de.tilman_neumann.jml.factor.hart;

import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.ANALYZE;
import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollector;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest01;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;
import de.tilman_neumann.jml.factor.cfrac.tdiv.TDiv_CF63;
import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;

/**
 * Experimental Hart algorithm assembling square congruences from smooth congruences.
 * 
 * For small N, parameter optimization leads to C=0 and maxQRestExponent=0, indicating that the
 * square test is the only thing that matters and such that the orginal Hart algorithm is much better.
 * 
 * For larger N, CFrac seems to be certainly faster.
 * 
 * @author Tilman Neumann
 */
public class HartLA63 extends FactorAlgorithm {
	private static final Logger LOG = Logger.getLogger(HartLA63.class);
	private static final boolean DEBUG = false;

	private final AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get();

	// input
	private BigInteger N, kN;

	// prime base
	private float C;
	private int primeBaseSize;
	private int[] primesArray;

	// factorizer for Q
	private TDiv_CF63 auxFactorizer;
	private float maxQRestExponent;
	
	// collects the congruences we find
	private CongruenceCollector congruenceCollector;
	
	// the number of congruences we need to find before we try to solve the smooth congruence equation system
	private int requiredSmoothCongruenceCount;
	// extra congruences to have a bigger chance that the equation system solves. the likelihood is >= 1-2^(extraCongruences+1)
	private int extraCongruences;
	/** The solver used for smooth congruence equation systems. */
	private MatrixSolver matrixSolver;

	private final Gcd63 gcdEngine = new Gcd63();

	// time capturing
	private long startTime, linAlgStartTime;
	
	/**
	 * We only test k-values that are multiples of this constant.
	 * Best values for performance are 315, 45, 105, 15 and 3, in that order.
	 */
	private static final int K_MULT = 3*3*5*7; // 315
	
	/** Size of arrays */
	private static final int I_MAX = 1<<20;
	
	/** This constant is used for fast rounding of double values to long. */
	private static final double ROUND_UP_DOUBLE = 0.9999999665;

	private final double[] sqrt;

	/**
	 * Standard constructor.
	 * @param C multiplier for prime base size
	 * @param maxQRestExponent
	 * @param auxFactorizer the algorithm to find smooth Q
	 * @param extraCongruences the number of surplus congruences we collect to have a greater chance that the equation system solves.
	 * @param matrixSolver matrix solver for the smooth congruence equation system
	 */
	public HartLA63(float C, float maxQRestExponent, TDiv_CF63 auxFactorizer, int extraCongruences, MatrixSolver matrixSolver) {

		this.C = C;
		this.maxQRestExponent = maxQRestExponent;
		this.auxFactorizer = auxFactorizer;
		this.congruenceCollector = new CongruenceCollector();
		this.extraCongruences = extraCongruences;
		this.matrixSolver = matrixSolver;
		
		// Precompute sqrt(i*K_MULT) for all i < I_MAX
		sqrt = new double[I_MAX];
		for (int i=1; i<I_MAX; i++) {
			sqrt[i] = Math.sqrt(i*K_MULT);
		}
	}

	@Override
	public String getName() {
		return "HartLA63(C=" + C + ", maxSuSmoothExp=" + maxQRestExponent + ", " + auxFactorizer.getName() + ")";
	}
	
	/**
	 * Test the current N.
	 * @return factor, or null if no factor was found.
	 */
	public BigInteger findSingleFactor(BigInteger N) {
		if (ANALYZE) this.startTime = System.currentTimeMillis();
		this.N = N;

		// compute prime base size
		double N_dbl = N.doubleValue();
		double lnN = Math.log(N_dbl);
		double lnlnN = Math.log(lnN);
		double lnNPow = 0.666667; // heuristics for CFrac
		// we want that the exponents of lnN and lnlnN sum to 1
		this.primeBaseSize = 25 + (int) (Math.exp(Math.pow(lnN, lnNPow) * Math.pow(lnlnN, 1-lnNPow) * C));
		if (DEBUG) LOG.debug("N = " + N + ": primeBaseSize = " + primeBaseSize);
		this.primesArray = new int[primeBaseSize];

		// compute the biggest unfactored rest where some Q is still considered "sufficiently smooth".
		double maxQRest = Math.pow(N_dbl, maxQRestExponent);
		
		// initialize sub-algorithms for N
		this.auxFactorizer.initialize(N, maxQRest);
		FactorTest factorTest = new FactorTest01(N);
		this.congruenceCollector.initialize(N, factorTest);
		this.matrixSolver.initialize(N, factorTest);
		
		// Create prime base for N: Note that a reduced prime base wpuld not help here.
		for (int i=0; i<primeBaseSize; i++) {
			int p = SMALL_PRIMES.getPrime(i);
			primesArray[i] = p;
		}
		// we want: #equations = #variables + some extra congruences
		this.requiredSmoothCongruenceCount = primeBaseSize + extraCongruences;

		try {
			// initialize the Q-factorizer with new prime base
			this.auxFactorizer.initialize(kN, primeBaseSize, primesArray); // may throw FactorException
			// search square Q_i
			test();
		} catch (FactorException fe) {
			if (ANALYZE) {
				long endTime = System.currentTimeMillis();
				LOG.info("Found factor of N=" + N + " in " + (endTime-startTime) + "ms (LinAlgPhase took " + (endTime-linAlgStartTime) + "ms)");
			}
			return fe.getFactor();
		}

		return I_1;
	}

	protected void test() throws FactorException {
		// test for exact squares
		long N_long = N.longValue();
		final double sqrtN = Math.sqrt(N_long);
		final long floorSqrtN = (long) sqrtN;
		if (floorSqrtN*floorSqrtN == N_long) throw new FactorException(BigInteger.valueOf(floorSqrtN));
		
		final long fourN = N_long<<2;
		final double sqrt4N = sqrtN*2;
		long a, b, Q_test, gcd;
		int k = K_MULT;
		try {
			for (int i=1; ; ) {
				// odd k -> adjust a mod 8, 16, 32
				a = (long) (sqrt4N * sqrt[i++] + ROUND_UP_DOUBLE);
				a = adjustAForOddK(a, k*N_long+1);
				Q_test = a*a - k * fourN;
				// finding squares is the true strength of Harts algorithm; testing them means a big performance boost
				b = (long) Math.sqrt(Q_test);
				if (b*b == Q_test) {
					if ((gcd = gcdEngine.gcd(a+b, N_long))>1 && gcd<N_long) throw new FactorException(BigInteger.valueOf(gcd));
				}
				AQPair aqPair = auxFactorizer.test(BigInteger.valueOf(a), Q_test);
				if (DEBUG) LOG.debug("N = " + N + ": Q_test = " + Q_test + " -> aqPair = " + aqPair);
				if (aqPair!=null) {
					// the Q was sufficiently smooth
					linAlgStartTime = System.currentTimeMillis(); // just in case it really starts
					boolean addedSmooth = congruenceCollector.add(aqPair);
					if (addedSmooth) {
						int smoothCongruenceCount = congruenceCollector.getSmoothCongruenceCount();
						if (smoothCongruenceCount >= requiredSmoothCongruenceCount) {
							// try to solve equation system
							//LOG.debug("#smooths = " + smoothCongruenceCount + ", #requiredSmooths = " + requiredSmoothCongruenceCount);
							matrixSolver.solve(congruenceCollector.getSmoothCongruences()); // throws FactorException
							// no factor exception -> extend equation system and continue searching smooth congruences
							requiredSmoothCongruenceCount += extraCongruences;
						}
					}
				}
				k += K_MULT;

				// even k -> a must be odd
				a = (long) (sqrt4N * sqrt[i++] + ROUND_UP_DOUBLE) | 1L;
				Q_test = a*a - k * fourN;
				b = (long) Math.sqrt(Q_test);
				if (b*b == Q_test) {
					if ((gcd = gcdEngine.gcd(a+b, N_long))>1 && gcd<N_long) throw new FactorException(BigInteger.valueOf(gcd));
				}
				aqPair = auxFactorizer.test(BigInteger.valueOf(a), Q_test);
				if (DEBUG) LOG.debug("N = " + N + ": Q_test = " + Q_test + " -> aqPair = " + aqPair);
				if (aqPair!=null) {
					// the Q was sufficiently smooth
					linAlgStartTime = System.currentTimeMillis(); // just in case it really starts
					boolean addedSmooth = congruenceCollector.add(aqPair);
					if (addedSmooth) {
						int smoothCongruenceCount = congruenceCollector.getSmoothCongruenceCount();
						if (smoothCongruenceCount >= requiredSmoothCongruenceCount) {
							// try to solve equation system
							//LOG.debug("#smooths = " + smoothCongruenceCount + ", #requiredSmooths = " + requiredSmoothCongruenceCount);
							matrixSolver.solve(congruenceCollector.getSmoothCongruences()); // throws FactorException
							// no factor exception -> extend equation system and continue searching smooth congruences
							requiredSmoothCongruenceCount += extraCongruences;
						}
					}
				}
				k += K_MULT;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.error("Hart_Fast: Failed to factor N=" + N + ". Either it has factors < cbrt(N) needing trial division, or the arrays are too small.");
		}
	}
	
	private long adjustAForOddK(long a, long kNp1) {
		if ((kNp1 & 3) == 0) {
			a += (kNp1 - a) & 7;
		} else if ((kNp1 & 7) == 6) {
			final long adjust1 = (kNp1 - a) & 31;
			final long adjust2 = (-kNp1 - a) & 31;
			a += adjust1<adjust2 ? adjust1 : adjust2;
		} else { // (kN+1) == 2 (mod 8)
			final long adjust1 = (kNp1 - a) & 15;
			final long adjust2 = (-kNp1 - a) & 15;
			a += adjust1<adjust2 ? adjust1 : adjust2;
		}
		return a;
	}
}
