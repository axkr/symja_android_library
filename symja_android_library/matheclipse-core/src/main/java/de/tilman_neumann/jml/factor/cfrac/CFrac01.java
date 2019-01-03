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
package de.tilman_neumann.jml.factor.cfrac;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import de.tilman_neumann.jml.factor.FactorAlgorithmBase;
import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.PrimeBaseGenerator;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollector;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollectorReport;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest01;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver01_Gauss;
import de.tilman_neumann.jml.factor.base.matrixSolver.SmoothSolverController;
import de.tilman_neumann.jml.factor.cfrac.tdiv.TDiv_CF;
import de.tilman_neumann.jml.factor.cfrac.tdiv.TDiv_CF02;
import de.tilman_neumann.jml.roots.SqrtExact;
import de.tilman_neumann.jml.roots.SqrtInt;
import de.tilman_neumann.util.TimeUtil;
import de.tilman_neumann.util.Timer;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * CFrac = Shanks' SQUFOF algorithm + carry along continuant recurrence + collect smooth relations + LinAlg solver.<br/>
 * <br/>
 * 
 * The original CFrac was implemented by Morrison&Brillhart intending to factor the 7.th Fermat number F7 with 39 digits
 * (~130 bits). Now this number can be factored in about three seconds.
 * 
 * Stopping criterion for a single k: after complete period or at a maximum number of iterations.
 *
 * Since 2017-09 Knuth-Schroeppel multipliers have been implemented, too.
 * 
 * @author Tilman Neumann
 */
public class CFrac01 extends FactorAlgorithmBase {
	// private static final Logger LOG = Logger.getLogger(CFrac01.class);
	// private static final boolean DEBUG = false;

	// input
	private BigInteger N, kN, floor_sqrt_kN;
	/** Test all Q or only those Q_i+1 with odd i ? "All Q" boosts performance for N > 45 bits approximately */
	private boolean use_all_i; //

	// maximum number of SquFoF iterations for a single k
	private int stopRoot;
	private float stopMult;
	private long maxI;

	// multiplier
	private int ks_adjust;
	private KnuthSchroeppel_CFrac ks = new KnuthSchroeppel_CFrac();

	// prime base
	private float C;
	private int primeBaseSize;
	private PrimeBaseGenerator primeBaseBuilder = new PrimeBaseGenerator();
	private int[] primesArray;
	private BigInteger[] primesArray_big;
	/** The union of all reduced prime bases -- this is the number of variables in the equation system */
	private HashSet<Integer> combinedPrimesSet;

	// factorizer for Q
	private TDiv_CF auxFactorizer;
	private float maxQRestExponent;

	// collects the congruences we find
	private CongruenceCollector congruenceCollector;

	// the number of congruences we need to find before we try to solve the smooth congruence equation system
	private int requiredSmoothCongruenceCount;
	// extra congruences to have a bigger chance that the equation system solves. the likelihood is >=
	// 1-2^(extraCongruences+1)
	private int extraCongruences;
	/** A controller for the solver used for smooth congruence equation systems. */
	private SmoothSolverController smoothSolverController;

	// profiling
	private boolean profile;
	private long startTime, linAlgStartTime;

	/**
	 * Standard constructor.
	 * 
	 * @param use_all_i
	 * @param stopRoot
	 *            order of the root to compute the maximum number of iterations
	 * @param stopMult
	 *            multiplier to compute the maximum number of iterations
	 * @param C
	 *            multiplier for prime base size
	 * @param maxQRestExponent
	 * @param auxFactorizer
	 *            the algorithm to find smooth Q
	 * @param extraCongruences
	 *            the number of surplus congruences we collect to have a greater chance that the equation system solves.
	 * @param matrixSolver
	 *            matrix solver for the smooth congruence equation system
	 * @param ks_adjust
	 * @param profile
	 *            if true then analyze operations & timings
	 */
	public CFrac01(boolean use_all_i, int stopRoot, float stopMult, float C, float maxQRestExponent,
			TDiv_CF auxFactorizer, int extraCongruences, MatrixSolver matrixSolver, int ks_adjust, boolean profile) {

		this.use_all_i = use_all_i;
		this.stopRoot = stopRoot;
		this.stopMult = stopMult;
		this.C = C;
		this.maxQRestExponent = maxQRestExponent;
		this.auxFactorizer = auxFactorizer;
		this.congruenceCollector = new CongruenceCollector();
		this.extraCongruences = extraCongruences;
		this.smoothSolverController = new SmoothSolverController(matrixSolver);
		this.ks_adjust = ks_adjust;
		this.profile = profile;
	}

	@Override
	public String getName() {
		return "CFrac01(all_i=" + use_all_i + ", ks_adjust=" + ks_adjust + ", stop=(" + stopRoot + ", " + stopMult
				+ "), C=" + C + ", maxSuSmoothExp=" + maxQRestExponent + ", " + auxFactorizer.getName() + ")";
	}

	/**
	 * Test the current N.
	 * 
	 * @return factor, or null if no factor was found.
	 */
	public BigInteger findSingleFactor(BigInteger N) {
		this.N = N;
		this.startTime = System.currentTimeMillis();

		// compute prime base size
		double N_dbl = N.doubleValue();
		double lnN = Math.log(N_dbl);
		double lnlnN = Math.log(lnN);
		double lnNPow = 0.666667; // heuristics for CFrac
		// we want that the exponents of lnN and lnlnN sum to 1
		this.primeBaseSize = 25 + (int) (Math.exp(Math.pow(lnN, lnNPow) * Math.pow(lnlnN, 1 - lnNPow) * C));
		// if (DEBUG) LOG.debug("N = " + N + ": primeBaseSize = " + primeBaseSize);
		this.primesArray = new int[primeBaseSize];
		this.primesArray_big = new BigInteger[primeBaseSize];

		// compute the biggest unfactored rest where some Q is still considered "sufficiently smooth".
		double maxQRest = Math.pow(N_dbl, maxQRestExponent);

		// initialize sub-algorithms for N
		this.auxFactorizer.initialize(N, maxQRest);
		FactorTest factorTest = new FactorTest01(N);
		this.congruenceCollector.initialize(N, factorTest, profile);
		this.combinedPrimesSet = new HashSet<Integer>();
		this.smoothSolverController.initialize(N, factorTest);

		// max iterations: there is no need to account for k, because expansions of smooth kN are typically not longer
		// than those for N.
		// long maxI is sufficient to hold any practicable number of iteration steps (~9.22*10^18);
		// stopRoot must be chosen appropriately such that there is no overflow of long values.
		// with stopRoot=5, the overflow of longs starts at N~6.67 * 10^94...
		this.maxI = (long) (stopMult * Math.pow(N_dbl, 1.0 / stopRoot));

		// compute multiplier k: though k=1 is better than Knuth-Schroeppel for N<47 bits,
		// we can ignore that here because that is far out of the optimal CFrac range
		TreeMap<Double, Integer> kMap = ks.computeMultiplier(N, ks_adjust);
		Iterator<Integer> kIter = kMap.values().iterator();

		while (true) {
			// get a new k, return immediately if kN is square
			this.kN = BigInteger.valueOf(kIter.next()).multiply(N);
			BigInteger[] iSqrt = SqrtInt.iSqrt(kN);
			this.floor_sqrt_kN = iSqrt[0];
			if (floor_sqrt_kN.equals(iSqrt[1]))
				return N.gcd(floor_sqrt_kN);

			// Create the reduced prime base for kN:
			primeBaseBuilder.computeReducedPrimeBase(kN, primeBaseSize, primesArray, primesArray_big);
			// add new reduced prime base to the combined prime base
			for (int i = 0; i < primeBaseSize; i++)
				combinedPrimesSet.add(primesArray[i]);
			// we want: #equations = #variables + some extra congruences
			this.requiredSmoothCongruenceCount = combinedPrimesSet.size() + extraCongruences;

			try {
				// initialize the Q-factorizer with new prime base
				this.auxFactorizer.initialize(kN, primeBaseSize, primesArray, primesArray_big); // may throw
																								// FactorException
				// search square Q_i
				test();
			} catch (FactorException fe) {
				long endTime = System.currentTimeMillis();
				if (profile) {
//					LOG.info("Found factor of N=" + N + " in " + (endTime - startTime) + "ms (LinAlgPhase took "
//							+ (endTime - linAlgStartTime) + "ms)");
//					CongruenceCollectorReport ccReport = congruenceCollector.getReport();
//					LOG.info("    cc: " + ccReport.getOperationDetails());
//					if (CongruenceCollector.ANALYZE_BIG_FACTOR_SIZES) {
//						LOG.info("        " + ccReport.getPartialBigFactorSizes());
//						LOG.info("        " + ccReport.getSmoothBigFactorSizes());
//						LOG.info("        " + ccReport.getNonIntFactorPercentages());
//					}
//					if (CongruenceCollector.ANALYZE_Q_SIGNS) {
//						LOG.info("        " + ccReport.getPartialQSignCounts());
//						LOG.info("        " + ccReport.getSmoothQSignCounts());
//					}
				}
				return fe.getFactor();
			} finally {
			}
		}
	}

	protected void test() throws FactorException {
		// initialization for first iteration step
		long i = 0;
		BigInteger A_im2 = null;
		BigInteger A_im1 = I_1;
		BigInteger A_i = floor_sqrt_kN;
		BigInteger P_im1 = I_1;
		BigInteger P_i = floor_sqrt_kN;
		BigInteger Q_i = I_1;
		BigInteger Q_ip1 = kN.subtract(P_i.multiply(P_i));

		// first iteration step
		BigInteger two_floor_sqrt_kN = floor_sqrt_kN.shiftLeft(1);
		while (true) {
//			if (DEBUG)
//				verifyCongruence(i, A_i, Q_ip1);
			
			
			// [McMath 2004] points out (on SquFoF) that we have to look for square Q_i at some even i.
			// Here I test Q_i+1, so I have to look for square Q_i+1 at odd i.
			// In CFRAC, square congruences are also tested in the CongruenceCollector,
			// but doing it here before trial division is good for performance.
			BigInteger Q_ip1_sqrt = null;
			if (i % 2 == 1) {
				Q_ip1_sqrt = SqrtExact.exactSqrt(Q_ip1);
				if (Q_ip1_sqrt != null) {
					// Q_i+1 is square -> test gcd
					BigInteger gcd = N.gcd(A_i.subtract(Q_ip1_sqrt));
					if (gcd.compareTo(I_1) > 0 && gcd.compareTo(N) < 0)
						throw new FactorException(gcd);
				}
			}
			if (Q_ip1_sqrt == null && (use_all_i || i % 2 == 1)) {
				// Q_i+1 is not square and the i is right, too -> check for smooth relations.
				// Here a constraint on the size of Q would mean a severe performance penalty!
				BigInteger Q_test = i % 2 == 1 ? Q_ip1 : Q_ip1.negate(); // make Q congruent A^2
				AQPair aqPair = auxFactorizer.test(A_i, Q_test);
//				if (DEBUG)
//					LOG.debug("N = " + N + ": Q_test = " + Q_test + " -> aqPair = " + aqPair);
				
				if (aqPair != null) {
					// the Q was sufficiently smooth
					linAlgStartTime = System.currentTimeMillis(); // just in case it really starts
					boolean addedSmooth = congruenceCollector.add(aqPair);
					if (addedSmooth) {
						int smoothCongruenceCount = congruenceCollector.getSmoothCongruenceCount();
						if (smoothCongruenceCount >= requiredSmoothCongruenceCount) {
							// try to solve equation system
//							if (DEBUG)
//								LOG.debug("#smooths = " + smoothCongruenceCount + ", #requiredSmooths = "
//										+ requiredSmoothCongruenceCount);
							smoothSolverController.solve(congruenceCollector.getSmoothCongruences()); // throws
																										// FactorException
							// no factor exception -> extend equation system and continue searching smooth congruences
							requiredSmoothCongruenceCount += extraCongruences;
						}
					}
				}
			}

			// exit loop ?
			if (++i == maxI)
				return;

			// keep values from last round
			A_im2 = A_im1;
			A_im1 = A_i;
			P_im1 = P_i;
			BigInteger Q_im1 = Q_i;
			Q_i = Q_ip1;
			// Compute next values
			// The following (exact) division "costs" 20-25% of the total runtime
			BigInteger b_i = floor_sqrt_kN.add(P_im1).divide(Q_i);
			P_i = b_i.multiply(Q_i).subtract(P_im1);
			Q_ip1 = Q_im1.add(b_i.multiply(P_im1.subtract(P_i)));
			// carry along A_i % N from continuant recurrence
			A_i = addModN(mulModN(b_i, A_im1), A_im2);

			// stop when continuant period is complete
			if (b_i.equals(two_floor_sqrt_kN))
				return;
		}
	}

	/**
	 * Verify congruence A_i^2 == (-1)^(i+1)*Q_i+1 (mod N)
	 * 
	 * @param i
	 * @param A_i
	 * @param Q_ip1
	 */
//	private void verifyCongruence(long i, BigInteger A_i, BigInteger Q_ip1) {
//		assertTrue(Q_ip1.signum() >= 0);
//		// verify congruence A^2 == Q (mod N)
//		BigInteger Q_test = i % 2 == 1 ? Q_ip1 : Q_ip1.negate().mod(N);
//		BigInteger div[] = A_i.pow(2).subtract(Q_test).divideAndRemainder(N);
//		assertEquals(I_0, div[1]); // exact division
//		LOG.debug("A^2-Q = " + div[0] + " * N");
//		LOG.debug("A^2 % N = " + A_i.pow(2).mod(N) + ", Q = " + Q_test);
//		assertEquals(Q_test, A_i.pow(2).mod(N)); // works
//	}

	/**
	 * Addition modulo N, with <code>a, b < N</code>.
	 * 
	 * @param a
	 * @param b
	 * @return (a+b) mod N
	 */
	private BigInteger addModN(BigInteger a, BigInteger b) {
		BigInteger sum = a.add(b);
		return sum.compareTo(N) < 0 ? sum : sum.subtract(N);
	}

	/**
	 * Multiplication (m*a) modulo N, with m often small and <code>a < N</code>.
	 * 
	 * @param m
	 * @param a
	 * @return (m*a) mod N
	 */
	private BigInteger mulModN(BigInteger m, BigInteger a) {
		if (m.bitLength() < 3) { // 0, 1, 10, 11
			switch (m.intValue()) {
			case 0:
				return I_0;
			case 1:
				return a;
			case 2: {
				BigInteger two_a = a.shiftLeft(1); // faster than 2*a or a+a
				return two_a.compareTo(N) < 0 ? two_a : two_a.subtract(N);
			}
			case 3: {
				BigInteger ma_modN = a.shiftLeft(1).add(a); // < 3*N
				if (ma_modN.compareTo(N) < 0)
					return ma_modN;
				ma_modN = ma_modN.subtract(N); // < 2*N
				return ma_modN.compareTo(N) < 0 ? ma_modN : ma_modN.subtract(N);
			}
			}
			// adding case 4 does not help because then bitLength() does not fit exactly
		}
		BigInteger product = m.multiply(a);
		return product.compareTo(N) < 0 ? product : product.mod(N);
	}

//	private static void testInput() {
//		CFrac01 cfrac = new CFrac01(true, 5, 1.5F, 0.152F, 0.253F, new TDiv_CF02(), 10, new MatrixSolver01_Gauss(), 5,
//				false);
//		Timer timer = new Timer();
//		while (true) {
//			try {
//				LOG.info("Please insert the number to factor:");
//				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//				String input = in.readLine().trim();
//				// LOG.debug("input = >" + input + "<");
//				BigInteger N = new BigInteger(input);
//				LOG.info("Factoring " + N + " ...");
//				timer.capture();
//				BigInteger factor = cfrac.findSingleFactor(N);
//				if (factor != null) {
//					long duration = timer.capture();
//					LOG.info("Found factor " + factor + " in " + TimeUtil.timeStr(duration) + ".");
//				} else {
//					LOG.info("No factor found.");
//				}
//			} catch (Exception ex) {
//				LOG.error("Error " + ex, ex);
//			}
//		}
//	}

	/**
	 * Standalone test.
	 * 
	 * Test numbers: F7 = 340282366920938463463374607431768211457
	 * 
	 * @param args
	 *            ignored
	 */
//	public static void main(String[] args) {
//		ConfigUtil.initProject();
//		testInput();
//	}
}
