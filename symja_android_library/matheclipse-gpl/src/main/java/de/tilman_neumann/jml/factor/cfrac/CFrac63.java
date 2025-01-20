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
package de.tilman_neumann.jml.factor.cfrac;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.base.PrimeBaseGenerator;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollector01;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollectorReport;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollector;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest01;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;
import de.tilman_neumann.jml.factor.cfrac.tdiv.TDiv_CF63;
import de.tilman_neumann.util.Ensure;
import de.tilman_neumann.util.TimeUtil;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.ANALYZE;
import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.ANALYZE_LARGE_FACTOR_SIZES;
import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.ANALYZE_Q_SIGNS;

/**
 * 63 bit CFrac with Knuth-Schroeppel multiplier.
 * 
 * @author Tilman Neumann
 */
public class CFrac63 extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(CFrac63.class);
	private static final boolean DEBUG = false;

	// input
	private BigInteger N, kN;
	private long floor_sqrt_kN;
	/** Test all Q or only those Q_i+1 with odd i ? "All Q" boosts performance for N > 45 bits approximately */
	private boolean use_all_i; // 

	// maximum number of SquFoF iterations for a single k
	private int stopRoot;
	private float stopMult;
	private long maxI;
	
	// multiplier
	private int ks_adjust;
	private KnuthSchroeppelCFrac ks = new KnuthSchroeppelCFrac();

	// prime base
	private float C;
	private int primeBaseSize;
	private PrimeBaseGenerator primeBaseBuilder = new PrimeBaseGenerator();
	private int[] primesArray;
	/** The union of all reduced prime bases -- this is the number of variables in the equation system */
	private HashSet<Integer> combinedPrimesSet;

	// factorizer for Q
	private TDiv_CF63 auxFactorizer;
	private float smoothBoundExponent;
	
	// collects the congruences we find
	private CongruenceCollector congruenceCollector;
	
	/** The solver used for smooth congruence equation systems. */
	private MatrixSolver matrixSolver;
	
	/**
	 * Standard constructor.
	 * @param use_all_i
	 * @param stopRoot order of the root to compute the maximum number of iterations
	 * @param stopMult multiplier to compute the maximum number of iterations
	 * @param C multiplier for prime base size
	 * @param smoothBoundExponent
	 * @param auxFactorizer the algorithm to find smooth Q
	 * @param matrixSolver matrix solver for the smooth congruence equation system
	 * @param ks_adjust
	 */
	public CFrac63(boolean use_all_i, int stopRoot, float stopMult, float C, float smoothBoundExponent,
				   TDiv_CF63 auxFactorizer, MatrixSolver matrixSolver, int ks_adjust) {

		this.use_all_i = use_all_i;
		this.stopRoot = stopRoot;
		this.stopMult = stopMult;
		this.C = C;
		this.smoothBoundExponent = smoothBoundExponent;
		this.auxFactorizer = auxFactorizer;
		this.congruenceCollector = new CongruenceCollector01();
		this.matrixSolver = matrixSolver;
		this.ks_adjust = ks_adjust;
	}

	@Override
	public String getName() {
		return "CFrac63(all_i=" + use_all_i + ", ks_adjust=" + ks_adjust + ", stop=(" + stopRoot + ", " + stopMult + "), C=" + C + ", maxSuSmoothExp=" + smoothBoundExponent + ", " + auxFactorizer.getName() + ")";
	}
	
	/**
	 * Test the current N.
	 * @return factor, or null if no factor was found.
	 */
	public BigInteger findSingleFactor(BigInteger N) {
		long startTime = System.currentTimeMillis();
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
		double smoothBound = Math.pow(N_dbl, smoothBoundExponent);
		
		// initialize sub-algorithms for N
		this.auxFactorizer.initialize(N, smoothBound);
		FactorTest factorTest = new FactorTest01(N);
		matrixSolver.initialize(N, factorTest);
		congruenceCollector.initialize(N, primeBaseSize, matrixSolver, factorTest);
		this.combinedPrimesSet = new HashSet<Integer>();

		// Max iterations per multiplier:
		// * Multiplier switching seems to useful for small factor arguments only (<60 bit), but it may be worth to keep it for further experiments
		// * There is no need to account for k in the computation of maxI, because expansions of smooth kN are typically not longer than those for N.
		// * long maxI is sufficient to hold any practicable number of iteration steps (~9.22*10^18);
		// * stopRoot must be chosen appropriately such that there is no overflow of long values.
		//   With stopRoot=5, the overflow of longs starts at N~6.67 * 10^94...
		this.maxI = (long) (stopMult*Math.pow(N_dbl, 1.0/stopRoot));
		
		// compute multiplier k: though k=1 is better than Knuth-Schroeppel for N<47 bits,
		// we can ignore that here because that is far out of the optimal CFrac range
		TreeMap<Double, Integer> kMap = ks.computeMultiplier(N, ks_adjust);
		Iterator<Integer> kIter = kMap.values().iterator();
		
		while (kIter.hasNext()) {
			// get a new k, return immediately if kN is square
			int k = kIter.next();
			if (DEBUG) LOG.debug("k = " + k);
			this.kN = BigInteger.valueOf(k).multiply(N);
			floor_sqrt_kN = (long) Math.sqrt(kN.doubleValue()); // faster than BigInteger sqrt; but the type conversion may give ceil(sqrt(kN)) for some N >= 54 bit
			BigInteger sqrt_kN_big = BigInteger.valueOf(floor_sqrt_kN);
			long diff = kN.subtract(sqrt_kN_big.multiply(sqrt_kN_big)).longValue();
			if (diff==0) return N.gcd(sqrt_kN_big);
			if (diff<0) {
				// floor_sqrt_kN was too big, diff too small -> compute correction
				floor_sqrt_kN--;
				diff += (floor_sqrt_kN<<1) + 1;
			}

			// Create the reduced prime base for kN:
			primeBaseBuilder.computeReducedPrimeBase(kN, primeBaseSize, primesArray);
			
			// add new reduced prime base to the combined prime base
			for (int i=0; i<primeBaseSize; i++) combinedPrimesSet.add(primesArray[i]);
			congruenceCollector.setPrimeBaseSize(combinedPrimesSet.size());

			// initialize the Q-factorizer with new prime base
			this.auxFactorizer.initialize(kN, primeBaseSize, primesArray);

			// search square Q_i
			BigInteger factor = test(diff);
			if (factor != null) {
				if (ANALYZE) {
					long endTime = System.currentTimeMillis();
					LOG.info(getName() + ":");
					LOG.info("Found factor " + factor + " (" + factor.bitLength() + " bits) of N=" + N + " (" + N.bitLength() + " bits) in " + TimeUtil.timeStr(endTime-startTime));
					CongruenceCollectorReport ccReport = congruenceCollector.getReport();
					LOG.info("    cc: " + ccReport.getOperationDetails());
					if (ccReport.getMaxMatrixSize() > 0) LOG.info("    cc: The biggest partial solver matrix had " + ccReport.getMaxMatrixSize() + " rows"); // not all congruence collectors need a PartialSolver
					if (ANALYZE_LARGE_FACTOR_SIZES) {
						for (int i=1; i<=4; i++) LOG.info("        " + ccReport.getSmoothBigFactorPercentiles(i));
						for (int i=1; i<=4; i++) LOG.info("        " + ccReport.getSmoothQRestPercentiles(i));
						for (int i=1; i<=4; i++) LOG.info("        " + ccReport.getPartialBigFactorPercentiles(i));
						for (int i=1; i<=4; i++) LOG.info("        " + ccReport.getPartialQRestPercentiles(i));
						LOG.info("        " + ccReport.getNonIntFactorPercentages());
					}
					if (ANALYZE_Q_SIGNS) {
						LOG.info("        " + ccReport.getPartialQSignCounts());
						LOG.info("        " + ccReport.getSmoothQSignCounts());
					}
					LOG.info("    #solverRuns = " + congruenceCollector.getSolverRunCount() + ", #tested null vectors = " + congruenceCollector.getTestedNullVectorCount());
					LOG.info("    Approximate phase timings: cc=" + congruenceCollector.getCollectDuration() + "ms, solver=" + congruenceCollector.getSolverDuration() + "ms");
				}
				return factor;
			}
		}
		
		return I_1; // fail, too few Knuth-Schroeppel multipliers
	}

	protected BigInteger test(long Q_ip1) {
		// initialization for first iteration step
		long i = 0;
		BigInteger A_im2 = null;
		BigInteger A_im1 = I_1;
		BigInteger A_i = BigInteger.valueOf(floor_sqrt_kN);
		long P_im1 = 1;
		long P_i = floor_sqrt_kN;
		long Q_i = 1;
		
		// first iteration step
		long two_floor_sqrt_kN = floor_sqrt_kN<<1;
		while (true) {
			if (DEBUG) verifyCongruence(i, A_i, BigInteger.valueOf(Q_ip1));
			// [McMath 2004] points out (on SquFoF) that we have to look for square Q_i at some even i.
			// Here I test Q_i+1, so I have to look for square Q_i+1 at odd i.
			// In CFRAC, square congruences are also tested in the CongruenceCollector,
			// but doing it here before trial division is good for performance.
			boolean isSquare = false;
			if (i%2==1) {
				long Q_ip1_sqrt = (long) Math.sqrt(Q_ip1);
				if (Q_ip1_sqrt*Q_ip1_sqrt==Q_ip1) {
					// Q_i+1 is square -> test gcd
					BigInteger gcd = N.gcd(A_i.subtract(BigInteger.valueOf(Q_ip1_sqrt)));
					if (gcd.compareTo(I_1)>0 && gcd.compareTo(N)<0) return gcd;
					isSquare = true;
				}
			}
			if (isSquare==false && (use_all_i || i%2==1)) {
				// Q_i+1 is not square and the i is right, too -> check for smooth relations.
				// Here a constraint on the size of Q would mean a severe performance penalty!
				long Q_test = i%2==1 ? Q_ip1 : -Q_ip1; // make Q congruent A^2
				AQPair aqPair = auxFactorizer.test(A_i, Q_test);
				if (DEBUG) LOG.debug("N = " + N + ": Q_test = " + Q_test + " -> aqPair = " + aqPair);
				if (aqPair!=null) {
					// the Q was sufficiently smooth
					congruenceCollector.collectAndProcessAQPair(aqPair);
					BigInteger factor = congruenceCollector.getFactor();
					if (factor != null) return factor;
				}
			}
		
			// exit loop ?
			if (++i==maxI) return null;
			
			// keep values from last round
			A_im2 = A_im1;
			A_im1 = A_i;
			P_im1 = P_i;
			long Q_im1 = Q_i;
			Q_i = Q_ip1;
			// compute next values
			long b_i = (floor_sqrt_kN + P_im1)/Q_i; // floor(rational result)
			P_i = b_i*Q_i - P_im1;
			Q_ip1 = Q_im1 + b_i*(P_im1-P_i);
			// carry along A_i % N from continuant recurrence
			A_i = addModN(mulModN(b_i, A_im1), A_im2);
			
			// stop when continuant period is complete
			if (b_i==two_floor_sqrt_kN) return null;
		}
	}

	/**
	 * Verify congruence A_i^2 == (-1)^(i+1)*Q_i+1 (mod N)
	 * @param i
	 * @param A_i
	 * @param Q_ip1
	 */
	private void verifyCongruence(long i, BigInteger A_i, BigInteger Q_ip1) {
		Ensure.ensureGreaterEquals(Q_ip1.signum(), 0);
		// verify congruence A^2 == Q (mod N)
		BigInteger Q_test = i%2==1 ? Q_ip1 : Q_ip1.negate().mod(N);
		BigInteger div[] = A_i.pow(2).subtract(Q_test).divideAndRemainder(N);
		Ensure.ensureEquals(I_0, div[1]); // works
		LOG.debug("A^2-Q = " + div[0] + " * N");
		LOG.debug("A^2 % N = " + A_i.pow(2).mod(N) + ", Q = " + Q_test);
		Ensure.ensureEquals(Q_test, A_i.pow(2).mod(N)); // works
	}
	
	/**
	 * Addition modulo N, with <code>a, b < N</code>.
	 * @param a
	 * @param b
	 * @return (a+b) mod N
	 */
	private BigInteger addModN(BigInteger a, BigInteger b) {
		BigInteger sum = a.add(b);
		return sum.compareTo(N)<0 ? sum : sum.subtract(N);
	}

	/**
	 * Multiplication (m*a) modulo N, with m often small and <code>a < N</code>.
	 * @param m
	 * @param a
	 * @return (m*a) mod N
	 */
	private BigInteger mulModN(long m, BigInteger a) {
		if (m<4) { // 0, 1, 10, 11
			int m_int = (int) m;
			switch (m_int) {
			case 0: return I_0;
			case 1: return a;
			case 2: {
				BigInteger two_a = a.shiftLeft(1); // faster than 2*a or a+a
        		return two_a.compareTo(N)<0 ? two_a : two_a.subtract(N);
			}
			case 3: {
				BigInteger ma_modN = a.shiftLeft(1).add(a); // < 3*N
        		if (ma_modN.compareTo(N)<0) return ma_modN;
        		ma_modN = ma_modN.subtract(N); // < 2*N
        		return ma_modN.compareTo(N)<0 ? ma_modN : ma_modN.subtract(N);
			}
			}
			// adding case 4 does not help because then bitLength() does not fit exactly
		}
		BigInteger product = a.multiply(BigInteger.valueOf(m));
		return product.compareTo(N)<0 ? product : product.mod(N);
	}
}
