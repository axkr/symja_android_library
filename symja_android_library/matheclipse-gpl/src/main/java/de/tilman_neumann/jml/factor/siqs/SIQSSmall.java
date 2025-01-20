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
package de.tilman_neumann.jml.factor.siqs;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.base.FactorArguments;
import de.tilman_neumann.jml.factor.base.FactorResult;
import de.tilman_neumann.jml.factor.base.PrimeBaseGenerator;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollector;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollectorReport;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollectorSmall;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest01;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolverGauss02;
import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.poly.AParamGenerator;
import de.tilman_neumann.jml.factor.siqs.poly.AParamGenerator01;
import de.tilman_neumann.jml.factor.siqs.poly.PolyReport;
import de.tilman_neumann.jml.factor.siqs.poly.SIQSPolyGenerator;
import de.tilman_neumann.jml.factor.siqs.powers.PowerFinder;
import de.tilman_neumann.jml.factor.siqs.powers.PowerOfSmallPrimesFinder;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03g;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03gU;
import de.tilman_neumann.jml.factor.siqs.sieve.SmoothCandidate;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParams;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParamsFactory01;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveReport;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDivReport;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS_Small;
import de.tilman_neumann.jml.powers.PurePowerTest;
import de.tilman_neumann.util.TimeUtil;
import de.tilman_neumann.util.Timer;

/**
 * Single-threaded SIQS implementation used to factor the Q(x)-rests in the trial division stage of SIQS/PSIQS.
 * 
 * So far, the main purpose of this class is to prevent excessive logging when GlobalFactoringOptions.ANALYZE == true and SIQS
 * trial division starts to use an nested SIQS to factor large Q rests. A second purpose would be to optimize
 * this class for the factorization of small N (say, below 100 bit), but little effort has been dedicated to that so far.
 * 
 * On 30.12.2024 I noticed that the optimization towards small numbers has gone that far that
 * 11111111111111111111111111155555555555111111111111111 (173 bit) cannot get factored reliably anymore ;-)
 * 
 * @author Tilman Neumann
 */
public class SIQSSmall extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(SIQSSmall.class);
	private static final boolean DEBUG = false;
	/**
	 * SIQSSmall's own ANALYSIS flag should typically be turned off.
	 * Turning it on may only make sense when SIQSSmall is run stand-alone, e.g. for performance tests.
	 */
	private static final boolean ANALYZE = false;

	/** if true then SIQSSmall uses a sieve exploiting sun.misc.Unsafe features. This may be ~10% faster. */
	private boolean useUnsafe;
	
	private PurePowerTest powerTest = new PurePowerTest();
	private KnuthSchroeppel multiplierFinder = new KnuthSchroeppel(); // used to compute the multiplier k
	private float Cmult; // multiplier to compute prime base size
	private PrimeBaseGenerator primeBaseBuilder = new PrimeBaseGenerator();
	private ModularSqrtsEngine modularSqrtsEngine = new ModularSqrtsEngine(); // computes tArray
	private AParamGenerator apg;
	private SIQSPolyGenerator polyGenerator;
	private PowerFinder powerFinder;

	// sieve
	private float Mmult;
	private Sieve sieve;
	
	// trial division engine
	private TDiv_QS auxFactorizer;
	// collects the congruences we find
	private CongruenceCollector congruenceCollector;
	/** The solver used for smooth congruence equation systems. */
	private MatrixSolver matrixSolver;
	
	// statistics
	private Timer timer = new Timer();
	private long powerTestDuration, initNDuration;
	
	/**
	 * Standard constructor.
	 * @param Cmult multiplier for prime base size
	 * @param Mmult multiplier for sieve array size
	 * @param wantedQCount the wanted number of q whose product gives the a-parameter
	 * @param polyGenerator
	 * @param extraCongruences the number of surplus congruences we collect to have a greater chance that the equation system solves.
	 * @param permitUnsafeUsage if true then SIQSSmall uses a sieve exploiting sun.misc.Unsafe features. This may be ~10% faster.
	 */
	public SIQSSmall(float Cmult, float Mmult, Integer wantedQCount, SIQSPolyGenerator polyGenerator, int extraCongruences, boolean permitUnsafeUsage) {
		
		super(null);
		
		this.Cmult = Cmult;
		this.Mmult = Mmult;
		this.powerFinder = new PowerOfSmallPrimesFinder();
		this.polyGenerator = polyGenerator;
		this.useUnsafe = permitUnsafeUsage;
		this.sieve = permitUnsafeUsage ? new Sieve03gU() : new Sieve03g();
		this.congruenceCollector = new CongruenceCollectorSmall(10);
		this.auxFactorizer = new TDiv_QS_Small();
		this.matrixSolver = new MatrixSolverGauss02();
		apg = new AParamGenerator01(wantedQCount);
	}

	@Override
	public String getName() {
		return "SIQSSmall(Cmult=" + Cmult + ", Mmult=" + Mmult + ", qCount=" + apg.getQCount() + ", " + powerFinder.getName() + ", " + polyGenerator.getName() + ", " + sieve.getName() + ", " + auxFactorizer.getName() + ", " + matrixSolver.getName() + ", useUnsafe = " + useUnsafe + ")";
	}

	@Override
	public void searchFactors(FactorArguments args, FactorResult result) {
		if (ANALYZE) {
			timer.start(); // start timer
			powerTestDuration = initNDuration = 0;
		}

		BigInteger N = args.N;
		
		// the quadratic sieve does not work for pure powers
		PurePowerTest.Result purePower = powerTest.test(N);
		if (ANALYZE) powerTestDuration += timer.capture();
		if (purePower!=null) {
			// N is indeed a pure power. In contrast to findSingleFactor() we can also add the exponent, so following steps get faster
			result.untestedFactors.add(purePower.base, purePower.exponent);
			return;
		} // else: no pure power, run quadratic sieve
		
		// run quadratic sieve
		BigInteger factor1 = findSingleFactorInternal(N);
		if (factor1.compareTo(I_1) > 0 && factor1.compareTo(N) < 0) {
			// We found a factor, but here we cannot know if it is prime or composite
			result.untestedFactors.add(factor1, args.exp);
			result.untestedFactors.add(N.divide(factor1), args.exp);
			return;
		}

		// nothing found
	}

	/**
	 * Test the current N.
	 * @return factor, or null if no factor was found.
	 */
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		if (ANALYZE) {
			timer.start(); // start timer
			powerTestDuration = initNDuration = 0;
		}

		// the quadratic sieve does not work for pure powers; check that first:
		PurePowerTest.Result purePower = powerTest.test(N);
		if (ANALYZE) powerTestDuration += timer.capture();
		if (purePower!=null) {
			// N is indeed a pure power -> return a factor that is about sqrt(N)
			return purePower.base.pow(purePower.exponent>>1);
		} // else: no pure power, run quadratic sieve
		
		// run quadratic sieve
		return findSingleFactorInternal(N);
	}

	/**
	 * Runs the quadratic sieve.
	 * @return factor, or null if no factor was found.
	 */
	private BigInteger findSingleFactorInternal(BigInteger N) {
		// Compute prime base size:
		// http://www.mersenneforum.org/showthread.php?s=3087aa210d8d7f1852c690a45f22d2e5&t=11116&page=2:
		// "A rough estimate of the largest prime in the factor base is exp(0.5 * sqrt(ln(N) * ln(ln(N))))".
		// Here we estimate the number of entries instead of the largest element in the prime base,
		// with Cmult instead of the constant 0.5. A good value is Cmult = 0.32.
		int NBits = N.bitLength();
		double N_dbl = N.doubleValue();
		double lnN = Math.log(N_dbl);
		double lnTerm = Math.sqrt(lnN * Math.log(lnN)); // (lnN)^0.5 * (lnlnN)^(1-0.5)
		double primeBaseSize_dbl = Math.exp(Cmult * lnTerm);
		if (primeBaseSize_dbl > Integer.MAX_VALUE) {
			// For Cmult=0.32 this condition takes effect at 996 bits; but long before we will get memory issues
			LOG.error("N=" + N + " (" + NBits + " bits) is too big for SIQSSmall!");
			return null;
		}
		int primeBaseSize = Math.max(30, (int) primeBaseSize_dbl); // min. size for very small N
		int[] primesArray = new int[primeBaseSize];

		// compute Knuth-Schroppel multiplier
		int k = multiplierFinder.computeMultiplier(N);
		BigInteger kN = N; // iff k==1
		if (k>1) {
			BigInteger kBig = BigInteger.valueOf(k);
			// avoid square kN without square N; that would lead to an infinite loop in trial division
			if (N.mod(kBig).equals(I_0)) return kBig;
			kN = kBig.multiply(N);
		}
		
		// Compute the d-parameter from kN: We choose d=2 if kN % 8 == 1, otherwise d=1. For the modulus we only need the lowest three bits.
		int d = ((kN.intValue() & 7) == 1) ? 2 : 1;
		if (DEBUG) LOG.debug("d = " + d);

		// Create the reduced prime base for kN:
		primeBaseBuilder.computeReducedPrimeBase(kN, primeBaseSize, primesArray);
		
		// Compute the t with t^2 == kN (mod p) for all p: Throws a FactorException if some p divides N
		int[] tArray = modularSqrtsEngine.computeTArray(primesArray, primeBaseSize, kN);
		
		// compute sieve array size, a multiple of 256
		int pMax = primesArray[primeBaseSize-1];
		long proposedSieveArraySize = 6144 + (long) Math.exp(Mmult * lnTerm); // 6144 = best experimental result for small N
		if (proposedSieveArraySize+pMax > Integer.MAX_VALUE) { // this might happen at N with ~ 650 bit or later
			LOG.error("N=" + N + " (" + NBits + " bits) is too big for SIQS!");
			return null;
		}
		int adjustedSieveArraySize = (int) (proposedSieveArraySize & 0x7FFFFF00);
		if (DEBUG) LOG.debug("N=" + N + ", k=" + k + ": pMax=" + pMax + ", sieve array size was adjusted from " + proposedSieveArraySize + " to " + adjustedSieveArraySize);

		// initialize sub-algorithms for new N
		apg.initialize(k, N, kN, d, primeBaseSize, primesArray, tArray, adjustedSieveArraySize); // must be done before polyGenerator initialization where qCount is required
		FactorTest factorTest = new FactorTest01(N);
		matrixSolver.initialize(N, factorTest);
		congruenceCollector.initialize(N, primeBaseSize, matrixSolver, factorTest);

		// compute some basic parameters for N
		SieveParams sieveParams = SieveParamsFactory01.create(N_dbl, NBits, kN, d, primesArray, primeBaseSize, adjustedSieveArraySize);

		// compute logP array
		byte[] logPArray = computeLogPArray(primesArray, primeBaseSize, sieveParams.lnPMultiplier);
		// compute reciprocals of primes
		long[] pinvArrayL = new long[primeBaseSize];
		for (int i=primeBaseSize-1; i>=0; i--) {
			pinvArrayL[i] = (1L<<32) / primesArray[i];
		}

		// Find and add powers to the prime base
		BaseArrays baseArrays = powerFinder.addPowers(kN, primesArray, tArray, logPArray, pinvArrayL, primeBaseSize, sieveParams);

		// initialize polynomial generator and sub-engines
		polyGenerator.initializeForN(k, N, kN, d, sieveParams, baseArrays, apg, sieve, auxFactorizer);
		if (ANALYZE) initNDuration += timer.capture();

		while (true) {
			// create new polynomial Q(x)
			polyGenerator.nextPolynomial(); // sets filtered prime base in SIQS

			// run sieve and get the sieve locations x where Q(x) is sufficiently smooth
			Iterable<SmoothCandidate> smoothCandidates = sieve.sieve();
			//LOG.debug("Sieve found " + smoothXList.size() + " Q(x) smooth enough to be passed to trial division.");

			// trial division stage: produce AQ-pairs
			List<AQPair> aqPairs = this.auxFactorizer.testList(smoothCandidates);
			//LOG.debug("Trial division found " + aqPairs.size() + " Q(x) smooth enough for a congruence.");

			// add all congruences
			congruenceCollector.collectAndProcessAQPairs(aqPairs);
			BigInteger factor = congruenceCollector.getFactor();
			if (factor != null) {
				if (ANALYZE) logResults(N, k, kN, factor, primeBaseSize, sieveParams);
				
				// ATTENTION: SIQSSmall is only used to factor auxiliary Q-numbers for N with 320 bit or more.
				// ATTENTION: After a Q-factorization we only want to clean up the sieve, in case it allocated native memory!
				// ATTENTION: This behavior is different from SIQS or PSIQS.
				this.sieve.cleanUp();
				// done
				return factor;
			}
		}
	}

	private byte[] computeLogPArray(int[] primesArray, int primeBaseSize, float lnPMultiplier) {
		byte[] logPArray = new byte[primeBaseSize];
		for (int i=primeBaseSize-1; i>=0; i--) {
			logPArray[i] = (byte) ((float) Math.log(primesArray[i]) * lnPMultiplier + 0.5F);
		}
		return logPArray;
	}

	private void logResults(BigInteger N, int k, BigInteger kN, BigInteger factor, int primeBaseSize, SieveParams sieveParams) {
		// get all reports
		PolyReport polyReport = polyGenerator.getReport();
		SieveReport sieveReport = sieve.getReport();
		TDivReport tdivReport = auxFactorizer.getReport();
		CongruenceCollectorReport ccReport = congruenceCollector.getReport();
		// solverReport is not urgently needed
		
		long initPolyDuration = polyReport.getTotalDuration(1);
		long sieveDuration = sieveReport.getTotalDuration(1);
		long tdivDuration = tdivReport.getTotalDuration(1);
		
		// report results
		LOG.info(getName() + ":");
		LOG.info("Found factor " + factor + " (" + factor.bitLength() + " bits) of N=" + N + " (" + N.bitLength() + " bits) in " + TimeUtil.timeStr(timer.totalRuntime()));
		int pMinBits = 32 - Integer.numberOfLeadingZeros(sieveParams.pMin);
		int pMaxBits = 32 - Integer.numberOfLeadingZeros(sieveParams.pMax);
		LOG.info("    multiplier k = " + k + ", kN%8 = " + kN.mod(I_8) + ", primeBaseSize = " + primeBaseSize + ", pMin = " + sieveParams.pMin + " (" + pMinBits + " bits), pMax = " + sieveParams.pMax + " (" + pMaxBits + " bits), sieveArraySize = " + sieveParams.sieveArraySize);
		LOG.info("    polyGenerator: " + polyReport.getOperationDetails());
		LOG.info("    sieve: Found " + sieveReport.getOperationDetails());
		LOG.info("    tDiv: " + tdivReport.getOperationDetails());
		LOG.info("    cc: " + ccReport.getOperationDetails());
		if (ccReport.getMaxRelatedPartialsCount() > 0) LOG.info("    cc: maxRelatedPartialsCount = " + ccReport.getMaxRelatedPartialsCount() + ", maxPartialMatrixSize = " + ccReport.getMaxMatrixSize() + " rows");
		// large factor sizes or Q-signs are not analyzed by CongruenceCollectorSmall
		LOG.info("    #solverRuns = " + congruenceCollector.getSolverRunCount() + ", #tested null vectors = " + congruenceCollector.getTestedNullVectorCount());
		LOG.info("    Approximate phase timings: powerTest=" + powerTestDuration + "ms, initN=" + initNDuration + "ms, initPoly=" + initPolyDuration + "ms, sieve=" + sieveDuration + "ms, tdiv=" + tdivDuration + "ms, cc=" + congruenceCollector.getCollectDuration() + "ms, solver=" + congruenceCollector.getSolverDuration() + "ms");
		LOG.info("    -> initPoly sub-timings: " + polyReport.getPhaseTimings(1));
		LOG.info("    -> sieve sub-timings: " + sieveReport.getPhaseTimings(1));
		LOG.info("    -> tdiv sub-timings: " + tdivReport.getPhaseTimings(1));
		
		// CC and solver have no sub-timings yet
	}
	
	public void cleanUp() {
		apg.cleanUp();
		polyGenerator.cleanUp();
		// ATTENTION: This is the cleanup after a complete N was factorized.
		// ATTENTION: But SIQSSmall is only used to factor auxiliary Q-numbers for N >= 320 bit.
		// ATTENTION: In case it uses native memory, we clean the sieve of SIQS-Small after each Q-factorization, not here.
		auxFactorizer.cleanUp();
		congruenceCollector.cleanUp();
		matrixSolver.cleanUp();
	}
}
