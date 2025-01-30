/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2025 Tilman Neumann - tilman.neumann@web.de
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

import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.*;

import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.base.FactorArguments;
import de.tilman_neumann.jml.factor.base.FactorResult;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolverGauss02;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolverBlockLanczos;
import de.tilman_neumann.jml.factor.ecm.EllipticCurveMethod;
import de.tilman_neumann.jml.factor.ecm.TinyEcm64MHInlined;
import de.tilman_neumann.jml.factor.hart.HartFast2Mult;
import de.tilman_neumann.jml.factor.pollardRho.PollardRhoBrentMontgomery64MH;
import de.tilman_neumann.jml.factor.psiqs.PSIQS;
import de.tilman_neumann.jml.factor.psiqs.PSIQS_U;
import de.tilman_neumann.jml.factor.siqs.SIQS;
import de.tilman_neumann.jml.factor.siqs.poly.SIQSPolyGenerator;
import de.tilman_neumann.jml.factor.siqs.powers.NoPowerFinder;
import de.tilman_neumann.jml.factor.siqs.powers.PowerOfSmallPrimesFinder;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03g;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03gU;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03h;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03hU;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS_2LP;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS_Small;
import de.tilman_neumann.jml.factor.tdiv.TDiv;
import de.tilman_neumann.jml.factor.tdiv.TDiv31Barrett;
import de.tilman_neumann.jml.primes.probable.BPSWTest;
import de.tilman_neumann.util.Ensure;

/**
 * Final combination of factor algorithms. Integrates trial division and ECM to search small factors of large numbers.
 * On the long end, a parallel SIQS is used. Altogether it is the best algorithm for general factoring arguments in this library.
 * 
 * @author Tilman Neumann
 */
public class CombinedFactorAlgorithm extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(CombinedFactorAlgorithm.class);
	private static final boolean DEBUG = false;
	
	/** If true then search for small factors before PSIQS is run. This is standard now. */
	private static final boolean SEARCH_SMALL_FACTORS = true;

	private TDiv31Barrett tDiv31 = new TDiv31Barrett();
	private HartFast2Mult hart = new HartFast2Mult(true); // for general factor arguments, trial division is needed
	private TinyEcm64MHInlined tinyEcm = new TinyEcm64MHInlined(true); // for general factor arguments, trial division is needed
	private PollardRhoBrentMontgomery64MH pollardRhoBrentMontgomery64MH = new PollardRhoBrentMontgomery64MH();
	private TDiv tdiv = new TDiv();
	private EllipticCurveMethod ecm = new EllipticCurveMethod(0);

	// SIQS tuned for small N
	private SIQS siqsForSmallArgs;

	// The SIQS chosen for big arguments depends on constructor parameters
	private FactorAlgorithm siqsForBigArgs;

	private BPSWTest bpsw = new BPSWTest();

	// profiling
	private long t0;

	/**
	 * Simple constructor, computing the amount of trial division automatically 
	 * and using PSIQS with sun.misc.Unsafe features.
	 * @param numberOfThreads the number of parallel threads for PSIQS
	 */
	public CombinedFactorAlgorithm(int numberOfThreads) {
		this(numberOfThreads, null, true);
	}

	/**
	 * Full constructor.
	 * @param numberOfThreads the number of parallel threads for PSIQS
	 * @param tdivLimit limit of primes p for trial division; if null then the value is determined by best experimental results
	 * @param permitUnsafeUsage if true then PSIQS_U using sun.misc.Unsafe features is used. This may be ~10% faster.
	 */
	public CombinedFactorAlgorithm(int numberOfThreads, Integer tdivLimit, boolean permitUnsafeUsage) {
		super(tdivLimit);
		
		Sieve smallSieve = permitUnsafeUsage ? new Sieve03gU() : new Sieve03g();
		siqsForSmallArgs = new SIQS(0.32F, 0.37F, null, new PowerOfSmallPrimesFinder(), new SIQSPolyGenerator(), smallSieve, new TDiv_QS_Small(), 10, new MatrixSolverGauss02());

		if (numberOfThreads==1) {
			// Avoid multi-thread overhead if the requested number of threads is 1
			if (permitUnsafeUsage) {
				siqsForBigArgs = new SIQS(0.31F, 0.37F, null, new NoPowerFinder(), new SIQSPolyGenerator(), new Sieve03hU(), new TDiv_QS_2LP(permitUnsafeUsage), 10, new MatrixSolverBlockLanczos());
			} else {
				siqsForBigArgs = new SIQS(0.31F, 0.37F, null, new NoPowerFinder(), new SIQSPolyGenerator(), new Sieve03h(), new TDiv_QS_2LP(permitUnsafeUsage), 10, new MatrixSolverBlockLanczos());
			}
		} else {
			if (permitUnsafeUsage) {
				siqsForBigArgs = new PSIQS_U(0.31F, 0.37F, null, numberOfThreads, new NoPowerFinder(), new MatrixSolverBlockLanczos());
			} else {
				siqsForBigArgs = new PSIQS(0.31F, 0.37F, null, numberOfThreads, new NoPowerFinder(), new MatrixSolverBlockLanczos());
			}
		}
	}

	@Override
	public String getName() {
		return "combi(" + (tdivLimit!=null ? tdivLimit : "auto") + ")";
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		int NBits = N.bitLength();
		if (NBits<25) return tDiv31.findSingleFactor(N);
		if (NBits<46) return hart.findSingleFactor(N);
		if (NBits<63) return tinyEcm.findSingleFactor(N);
		if (NBits<64) return pollardRhoBrentMontgomery64MH.findSingleFactor(N);
		if (NBits<=150) return siqsForSmallArgs.findSingleFactor(N);
		return siqsForBigArgs.findSingleFactor(N);
	}
	
	@Override
	public void searchFactors(FactorArguments args, FactorResult result) {
		int NBits = args.NBits;
		if (NBits<32) {
			// Find all remaining factors; these are known to be prime factors.
			// The bit bound here is higher than in findSingleFactor() because here we find all factors in a single tdiv run.
			tDiv31.factor(args.N, args.exp, result.primeFactors);
		}
		else if (NBits<46) hart.searchFactors(args, result);
		else if (NBits<63) tinyEcm.searchFactors(args, result);
		else if (NBits<64) pollardRhoBrentMontgomery64MH.searchFactors(args, result);
		else {
			if (SEARCH_SMALL_FACTORS) {
				int actualTdivLimit;
				if (tdivLimit != null) {
					// use "dictated" limit
					actualTdivLimit = tdivLimit.intValue();
				} else {
					// Adjust tdivLimit=2^e by experimental results.
					final double e = 10 + (args.NBits-45)*0.07407407407; // constant 0.07.. = 10/135
					actualTdivLimit = (int) Math.min(1<<20, Math.pow(2, e)); // upper bound 2^20
				}
				if (actualTdivLimit > result.smallestPossibleFactor) {
					// there is still tdiv/EM work to do...
					BigInteger N0 = args.N;
					
					if (DEBUG) LOG.debug("result before TDiv: " + result);
					if (ANALYZE) t0 = System.currentTimeMillis();
					tdiv.setTestLimit(actualTdivLimit).searchFactors(args, result);
					if (ANALYZE) LOG.debug("TDiv up to " + actualTdivLimit + " took " + (System.currentTimeMillis()-t0) + "ms");
					if (DEBUG) LOG.debug("result after TDiv:  " + result);
	
					if (result.untestedFactors.isEmpty()) return; // N was "easy"
	
					// Otherwise we continue
					BigInteger N = result.untestedFactors.firstKey();
					int exp = result.untestedFactors.removeAll(N);
					if (DEBUG) Ensure.ensureEquals(1, exp); // looks safe, otherwise we'ld have to consider exp below
	
					if (bpsw.isProbablePrime(N)) { // TODO exploit tdiv done so far
						result.primeFactors.add(N);
						return;
					}
	
					// update factor arguments for ECM or SIQS
					args.N = N;
					args.NBits = N.bitLength();
					args.exp = exp;
					
					// Check if ECM makes sense for a number of the size of N
					int maxCurvesForN = EllipticCurveMethod.computeMaxCurvesForN(N);
					if (maxCurvesForN == 0) {
						// ECM would create too much overhead for N, SIQS is faster
						result.compositeFactors.add(N, args.exp);
					} else {
						if (DEBUG) LOG.debug("result before ECM: " + result);
						if (ANALYZE) t0 = System.currentTimeMillis();
						ecm.searchFactors(args, result); // TODO a parallel ECM implementation with numberOfThreads threads would be nice here
						if (ANALYZE) LOG.debug("ECM took " + (System.currentTimeMillis()-t0) + "ms");
						if (DEBUG) LOG.debug("result after ECM:  " + result);
					}
					
					if (!result.compositeFactors.containsKey(N0)) {
						// either tdiv or ECM found some factors -> return immediately
						return;
					}
					// Neither tdiv nor ECM found a factor. N0 has been added to compositeFactors again -> remove it and continue with SIQS
					result.compositeFactors.removeAll(N);
				}
			}

			// SIQS / PSIQS: The crossover point needs checking
			if (NBits<=150) siqsForSmallArgs.searchFactors(args, result);
			else siqsForBigArgs.searchFactors(args, result);
		}
	}
}
