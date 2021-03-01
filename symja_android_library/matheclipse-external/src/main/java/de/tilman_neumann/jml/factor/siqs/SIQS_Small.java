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
package de.tilman_neumann.jml.factor.siqs;

import static de.tilman_neumann.jml.base.BigIntConstants.*;
import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.ANALYZE_LARGE_FACTOR_SIZES;
import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.ANALYZE_Q_SIGNS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.FactorArguments;
import de.tilman_neumann.jml.factor.base.FactorResult;
import de.tilman_neumann.jml.factor.base.PrimeBaseGenerator;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollector;
import de.tilman_neumann.jml.factor.base.congruence.CongruenceCollectorReport;
import de.tilman_neumann.jml.factor.base.congruence.Smooth;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest01;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver01_Gauss;
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
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParams;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveReport;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDivReport;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS_Small;
import de.tilman_neumann.jml.powers.PurePowerTest;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.TimeUtil;
import de.tilman_neumann.util.Timer;

/**
 * Single-threaded SIQS implementation used to factor the Q(x)-rests in the trial division stage of SIQS/PSIQS.
 * 
 * So far, the main purpose of this class is to prevent excessive logging when AnalysisOptions.ANALYZE == true and SIQS
 * trial division starts to use an nested SIQS to factor large Q rests. A second purpose would be to optimize
 * this class for the factorization of small N (say, below 100 bit).
 * 
 * @author Tilman Neumann
 */
public class SIQS_Small extends FactorAlgorithm {
	private static final Logger LOG = Logger.getLogger(SIQS_Small.class);
	private static final boolean DEBUG = false;
	/**
	 * SIQS_Small's own ANALYSIS flag should typically be turned off.
	 * Turning it on may only make sense when SIQS_Small is run stand-alone, e.g. for performance tests.
	 */
	private static final boolean ANALYZE = false;

	/** if true then SIQS_Small uses a sieve exploiting sun.misc.Unsafe features. This may be ~10% faster. */
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
	private Float maxQRestExponent0;
	private float maxQRestExponent;
	private Sieve sieve;
	
	// trial division engine
	private TDiv_QS auxFactorizer;
	// collects the congruences we find
	private CongruenceCollector congruenceCollector;
	// extra congruences to have a bigger chance that the equation system solves. the likelihood is >= 1-2^(extraCongruences+1)
	private int extraCongruences;
	/** The solver used for smooth congruence equation systems. */
	private MatrixSolver matrixSolver;
	
	// statistics
	private Timer timer = new Timer();
	private long powerTestDuration, initNDuration, ccDuration, solverDuration;
	private int solverRunCount;
	
	/**
	 * Standard constructor.
	 * @param Cmult multiplier for prime base size
	 * @param Mmult multiplier for sieve array size
	 * @param wantedQCount the wanted number of q whose product gives the a-parameter
	 * @param maxQRestExponent A Q with unfactored rest QRest is considered smooth if QRest <= N^maxQRestExponent.
	 *                         Good values are 0.16..0.19; null means that it is determined automatically.
	 * @param polyGenerator
	 * @param extraCongruences the number of surplus congruences we collect to have a greater chance that the equation system solves.
	 * @param permitUnsafeUsage if true then SIQS_Small uses a sieve exploiting sun.misc.Unsafe features. This may be ~10% faster.
	 */
	public SIQS_Small(
			float Cmult, float Mmult, Integer wantedQCount, Float maxQRestExponent, SIQSPolyGenerator polyGenerator, int extraCongruences, 
			boolean permitUnsafeUsage) {
		
		super(null);
		
		this.Cmult = Cmult;
		this.Mmult = Mmult;
		this.maxQRestExponent0 = maxQRestExponent;
		this.powerFinder = new PowerOfSmallPrimesFinder();
		this.polyGenerator = polyGenerator;
		this.useUnsafe = permitUnsafeUsage;
		this.sieve = permitUnsafeUsage ? new Sieve03gU() : new Sieve03g();
		this.congruenceCollector = new CongruenceCollector();
		this.auxFactorizer = new TDiv_QS_Small();
		this.extraCongruences = extraCongruences;
		this.matrixSolver = new MatrixSolver01_Gauss();
		apg = new AParamGenerator01(wantedQCount);
	}

	@Override
	public String getName() {
		String maxQRestExponentStr = "maxQRestExponent=" + String.format("%.3f", maxQRestExponent);
		return "SIQS_Small(Cmult=" + Cmult + ", Mmult=" + Mmult + ", qCount=" + apg.getQCount() + ", " + maxQRestExponentStr + ", " + powerFinder.getName() + ", " + polyGenerator.getName() + ", " + sieve.getName() + ", " + auxFactorizer.getName() + ", " + matrixSolver.getName() + ", useUnsafe = " + useUnsafe + ")";
	}

	@Override
	public void searchFactors(FactorArguments args, FactorResult result) {
		if (ANALYZE) {
			timer.start(); // start timer
			powerTestDuration = initNDuration = ccDuration = solverDuration = 0;
			solverRunCount = 0;
		}

		BigInteger N = args.N;
		
		// the quadratic sieve does not work for pure powers
		PurePowerTest.Result purePower = powerTest.test(N);
		if (purePower!=null) {
			// N is indeed a pure power. In contrast to findSingleFactor() we can also add the exponent, so following steps get faster
			result.untestedFactors.add(purePower.base, purePower.exponent);
			return;
		} // else: no pure power, run quadratic sieve
		if (ANALYZE) powerTestDuration += timer.capture();
		
		// run quadratic sieve
		BigInteger factor1 = findSingleFactorInternal(N);
		if (factor1.compareTo(I_1) > 0 && factor1.compareTo(N) < 0) {
			// We found a factor, but here we cannot know if it is prime or composite
			result.untestedFactors.add(factor1, args.exp);
			result.untestedFactors.add(N.divide(factor1), args.exp);
			return;
		}

		return; // nothing found
	}

	/**
	 * Test the current N.
	 * @return factor, or null if no factor was found.
	 */
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		if (ANALYZE) {
			timer.start(); // start timer
			powerTestDuration = initNDuration = ccDuration = solverDuration = 0;
			solverRunCount = 0;
		}

		// the quadratic sieve does not work for pure powers; check that first:
		PurePowerTest.Result purePower = powerTest.test(N);
		if (purePower!=null) {
			// N is indeed a pure power -> return a factor that is about sqrt(N)
			return purePower.base.pow(purePower.exponent>>1);
		} // else: no pure power, run quadratic sieve
		if (ANALYZE) powerTestDuration += timer.capture();
		
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
			LOG.error("N=" + N + " (" + NBits + " bits) is too big for SIQS_Small!");
			return null;
		}
		int primeBaseSize = Math.max(30, (int) primeBaseSize_dbl); // min. size for very small N
		int[] primesArray = new int[primeBaseSize];

		// The number of congruences we need to find before we try to solve the smooth congruence equation system:
		// We want: #equations = #variables + some extra congruences
		int requiredSmoothCongruenceCount = primeBaseSize + extraCongruences;

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
		
		// compute biggest QRest admitted for a smooth relation
		if (maxQRestExponent0 != null) {
			maxQRestExponent = maxQRestExponent0;
		} else {
			maxQRestExponent = (NBits<=150) ? 0.16F : 0.16F + (NBits-150.0F)/5250;
		}
		double maxQRest = Math.pow(N_dbl, maxQRestExponent);

		// initialize sub-algorithms for new N
		apg.initialize(k, N, kN, d, primeBaseSize, primesArray, tArray, adjustedSieveArraySize); // must be done before polyGenerator initialization where qCount is required
		FactorTest factorTest = new FactorTest01(N);
		congruenceCollector.initialize(N, factorTest);
		matrixSolver.initialize(N, factorTest);

		// compute some basic parameters for N
		SieveParams sieveParams = new SieveParams(kN, primesArray, primeBaseSize, adjustedSieveArraySize, maxQRest, 127);
		// compute logP array
		byte[] logPArray = computeLogPArray(primesArray, primeBaseSize, sieveParams.lnPMultiplier);
		// compute reciprocals of primes
		double[] pinvArrayD = new double[primeBaseSize];
		long[] pinvArrayL = new long[primeBaseSize];
		for (int i=0; i<primeBaseSize; i++) {
			pinvArrayD[i] = 1.0 / primesArray[i];
			pinvArrayL[i] = (1L<<32) / primesArray[i];
		}

		// Find and add powers to the prime base
		BaseArrays baseArrays = powerFinder.addPowers(kN, primesArray, tArray, logPArray, pinvArrayD, pinvArrayL, primeBaseSize, sieveParams);

		// initialize polynomial generator and sub-engines
		polyGenerator.initializeForN(k, N, kN, d, sieveParams, baseArrays, apg, sieve, auxFactorizer);
		if (ANALYZE) initNDuration += timer.capture();

		try {
			while (true) {
				// create new polynomial Q(x)
				polyGenerator.nextPolynomial(); // sets filtered prime base in SIQS
	
				// run sieve and get the sieve locations x where Q(x) is sufficiently smooth
				List<Integer> smoothXList = sieve.sieve();
				//LOG.debug("Sieve found " + smoothXList.size() + " Q(x) smooth enough to be passed to trial division.");

				// trial division stage: produce AQ-pairs
				List<AQPair> aqPairs = this.auxFactorizer.testList(smoothXList);
				//LOG.debug("Trial division found " + aqPairs.size() + " Q(x) smooth enough for a congruence.");

				// add all congruences
				if (ANALYZE) timer.capture();
				for (AQPair aqPair : aqPairs) {
					boolean addedSmooth = congruenceCollector.add(aqPair);
					if (addedSmooth) {
						int smoothCongruenceCount = congruenceCollector.getSmoothCongruenceCount();
						if (smoothCongruenceCount >= requiredSmoothCongruenceCount) {
							// Try to solve equation system
							if (ANALYZE) {
								ccDuration += timer.capture();
								solverRunCount++;
								if (DEBUG) LOG.debug("Run " + solverRunCount + ": #smooths = " + smoothCongruenceCount + ", #requiredSmooths = " + requiredSmoothCongruenceCount);
							}
							ArrayList<Smooth> congruences = congruenceCollector.getSmoothCongruences();
							matrixSolver.solve(congruences); // throws FactorException
							
							// If we get here then there was no FactorException
							if (DEBUG) {
								// Log all congruences
								LOG.debug("Failed solver run with " + smoothCongruenceCount + " congruences:");
								for (Smooth smooth : congruences) {
									LOG.debug("    " + smooth.toString());
								}
							}
							
							if (ANALYZE) solverDuration += timer.capture();
							// Extend equation system and continue searching smooth congruences
							requiredSmoothCongruenceCount += extraCongruences;
						}
					}
				}
				if (ANALYZE) ccDuration += timer.capture();
				if (DEBUG) {
					if (ANALYZE) LOG.debug(polyGenerator.getName() + ": " + polyGenerator.getReport().getOperationDetails());
					LOG.debug("Sieve found " + smoothXList.size() + " smooth x, tDiv let " + aqPairs.size() + " pass.");
					LOG.debug("-> Now in total we have found " + congruenceCollector.getSmoothCongruenceCount() + " / " + requiredSmoothCongruenceCount + " smooth congruences and " + congruenceCollector.getPartialCongruenceCount() + " partials.");
				}
			} // end poly
		} catch (FactorException fe) {
			BigInteger factor = fe.getFactor();
			if (ANALYZE) {
				solverDuration += timer.capture();
				logResults(N, k, kN, factor, primeBaseSize, pMax, adjustedSieveArraySize);
			}
			
			// release native memory after each Q-rest factorization
			this.sieve.cleanUp();
			// done
			return factor;
		}
	}

	private byte[] computeLogPArray(int[] primesArray, int primeBaseSize, float lnPMultiplier) {
		byte[] logPArray = new byte[primeBaseSize];
		for (int i=primeBaseSize-1; i>=0; i--) {
			logPArray[i] = (byte) ((float) Math.log(primesArray[i]) * lnPMultiplier + 0.5F);
		}
		return logPArray;
	}

	private void logResults(BigInteger N, int k, BigInteger kN, BigInteger factor, int primeBaseSize, int pMax, int adjustedSieveArraySize) {
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
		int pMaxBits = 32 - Integer.numberOfLeadingZeros(pMax);
		LOG.info("    multiplier k = " + k + ", kN%8 = " + kN.mod(I_8) + ", primeBaseSize = " + primeBaseSize + ", pMax = " + pMax + " (" + pMaxBits + " bits), sieveArraySize = " + adjustedSieveArraySize);
		LOG.info("    polyGenerator: " + polyReport.getOperationDetails());
		LOG.info("    tDiv: " + tdivReport.getOperationDetails());
		if (ANALYZE_LARGE_FACTOR_SIZES) {
			String qRestSizes = tdivReport.getQRestSizes();
			if (qRestSizes != null) {
				LOG.info("        " + qRestSizes);
			}
		}
		LOG.info("    cc: " + ccReport.getOperationDetails());
		if (ANALYZE_LARGE_FACTOR_SIZES) {
			LOG.info("        " + ccReport.getPartialBigFactorSizes());
			LOG.info("        " + ccReport.getSmoothBigFactorSizes());
			LOG.info("        " + ccReport.getSmoothBigFactorPercentiles());
			LOG.info("        " + ccReport.getNonIntFactorPercentages());
		}
		if (ANALYZE_Q_SIGNS) {
			LOG.info("        " + ccReport.getPartialQSignCounts());
			LOG.info("        " + ccReport.getSmoothQSignCounts());
		}
		LOG.info("    #solverRuns = " + solverRunCount + ", #tested null vectors = " + matrixSolver.getTestedNullVectorCount());
		LOG.info("    Approximate phase timings: powerTest=" + powerTestDuration + "ms, initN=" + initNDuration + "ms, initPoly=" + initPolyDuration + "ms, sieve=" + sieveDuration + "ms, tdiv=" + tdivDuration + "ms, cc=" + ccDuration + "ms, solver=" + solverDuration + "ms");
		LOG.info("    -> initPoly sub-timings: " + polyReport.getPhaseTimings(1));
		LOG.info("    -> sieve sub-timings: " + sieveReport.getPhaseTimings(1));
		LOG.info("    -> tdiv sub-timings: " + tdivReport.getPhaseTimings(1));
		// CC and solver have no sub-timings yet
	}
	
	/**
	 * Clean up after a factorization of N.
	 */
	public void cleanUp() {
		apg.cleanUp();
		polyGenerator.cleanUp();
		// the sieve is not cleaned here but after each Q-rest factorization, just in case it is using native memory
		auxFactorizer.cleanUp();
		congruenceCollector.cleanUp();
		matrixSolver.cleanUp();
	}

	// Standalone test --------------------------------------------------------------------------------------------------

	/**
	 * Stand-alone test.
	 * @param args ignored
	 * 
	 * Some test numbers:
	 * 11111111111111111111111111
	 * 5679148659138759837165981543
	 * 11111111111111111111111111155555555555111111111111111
	 * 
	 * 2900608971182010301486951469292513060638582965350239259380273225053930627446289431038392125
	 * = 33333 * 33335 * 33337 * 33339 * 33341 * 33343 * 33345 * 33347 * 33349 * 33351 * 33353 * 33355 * 33357 * 33359 * 33361 * 33363 * 33367 * 33369 * 33369 * 33371
	 * 
	 * 15841065490425479923 = 2604221509 * 6082841047
	 */
	public static void main(String[] args) {
    	ConfigUtil.initProject();
		SIQS_Small qs = new SIQS_Small(0.32F, 0.37F, null, null, new SIQSPolyGenerator(), 10, true);
		Timer timer = new Timer();
		while(true) {
			try {
				LOG.info("Please insert the number to factor:");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				String line = in.readLine();
				String input = line !=null ? line.trim() : "";
				//LOG.debug("input = >" + input + "<");
				BigInteger N = new BigInteger(input);
				LOG.info("Factoring " + N + " (" + N.bitLength() + " bits)...");
				timer.capture();
				SortedMultiset<BigInteger> factors = qs.factor(N);
				if (factors != null) {
					long duration = timer.capture();
					LOG.info("Factored N = " + factors + " in " + TimeUtil.timeStr(duration) + ".");
			} else {
					LOG.info("No factor found...");
				}
			} catch (Exception ex) {
				LOG.error("Error " + ex, ex);
			}
		}
	}
}
