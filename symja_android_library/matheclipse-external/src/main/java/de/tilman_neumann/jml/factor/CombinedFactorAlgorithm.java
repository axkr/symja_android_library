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
package de.tilman_neumann.jml.factor;

import static de.tilman_neumann.jml.factor.base.AnalysisOptions.*;
import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.base.FactorArguments;
import de.tilman_neumann.jml.factor.base.FactorResult;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver01_Gauss;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver02_BlockLanczos;
import de.tilman_neumann.jml.factor.ecm.EllipticCurveMethod;
import de.tilman_neumann.jml.factor.hart.Hart_TDiv_Race;
import de.tilman_neumann.jml.factor.pollardRho.PollardRhoBrentMontgomery64;
import de.tilman_neumann.jml.factor.pollardRho.PollardRhoBrentMontgomeryR64Mul63;
import de.tilman_neumann.jml.factor.psiqs.PSIQS;
import de.tilman_neumann.jml.factor.psiqs.PSIQS_U;
import de.tilman_neumann.jml.factor.siqs.SIQS;
import de.tilman_neumann.jml.factor.siqs.poly.SIQSPolyGenerator;
import de.tilman_neumann.jml.factor.siqs.powers.NoPowerFinder;
import de.tilman_neumann.jml.factor.siqs.powers.PowerOfSmallPrimesFinder;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03g;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve03gU;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS_1Large_UBI;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS_2Large_UBI;
import de.tilman_neumann.jml.factor.tdiv.TDiv;
import de.tilman_neumann.jml.factor.tdiv.TDiv31Barrett;
import de.tilman_neumann.jml.primes.probable.BPSWTest;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.TimeUtil;

/**
 * Final combination of factor algorithms. Integrates trial division and ECM to search small factors
 * of large numbers. As such it is the best algorithm for general factoring arguments.
 *
 * @author Tilman Neumann
 */
public class CombinedFactorAlgorithm extends FactorAlgorithm {
  private static final Logger LOG = Logger.getLogger(CombinedFactorAlgorithm.class);
  private static final boolean DEBUG = false;

  /** If true then search for small factors before PSIQS is run. This is standard now. */
  private static final boolean SEARCH_SMALL_FACTORS = true;

  private TDiv31Barrett tDiv31 = new TDiv31Barrett();
  private Hart_TDiv_Race hart = new Hart_TDiv_Race();
  private PollardRhoBrentMontgomeryR64Mul63 pollardRhoR64Mul63 =
      new PollardRhoBrentMontgomeryR64Mul63();
  private PollardRhoBrentMontgomery64 pollardRho64 = new PollardRhoBrentMontgomery64();
  private TDiv tdiv = new TDiv();
  private EllipticCurveMethod ecm = new EllipticCurveMethod(0);

  // SIQS tuned for small N
  private SIQS siqs_smallArgs;

  // The SIQS chosen for big arguments depends on constructor parameters
  private FactorAlgorithm siqs_bigArgs;

  private BPSWTest bpsw = new BPSWTest();

  // profiling
  private long t0;

  /**
   * Simple constructor, computing the amount of trial division automatically and using PSIQS with
   * sun.misc.Unsafe features.
   *
   * @param numberOfThreads the number of parallel threads for PSIQS
   */
  public CombinedFactorAlgorithm(int numberOfThreads) {
    this(numberOfThreads, null, true);
  }

  /**
   * Full constructor.
   *
   * @param numberOfThreads the number of parallel threads for PSIQS
   * @param tdivLimit limit of primes p for trial division; if null then the value is determined by
   *     best experimental results
   * @param permitUnsafeUsage if true then PSIQS_U using sun.misc.Unsafe features is used. This may
   *     be ~10% faster.
   */
  public CombinedFactorAlgorithm(
      int numberOfThreads, Integer tdivLimit, boolean permitUnsafeUsage) {
    super(tdivLimit);

    siqs_smallArgs =
        new SIQS(
            0.32F,
            0.37F,
            null,
            0.16F,
            new PowerOfSmallPrimesFinder(),
            new SIQSPolyGenerator(),
            new Sieve03gU(),
            new TDiv_QS_1Large_UBI(),
            10,
            new MatrixSolver01_Gauss());

    if (numberOfThreads == 1) {
      // Avoid multi-thread overhead if the requested number of threads is 1
      Sieve sieve = permitUnsafeUsage ? new Sieve03gU() : new Sieve03g();
      siqs_bigArgs =
          new SIQS(
              0.32F,
              0.37F,
              null,
              null,
              new NoPowerFinder(),
              new SIQSPolyGenerator(),
              sieve,
              new TDiv_QS_2Large_UBI(permitUnsafeUsage),
              10,
              new MatrixSolver02_BlockLanczos());
    } else {
      if (permitUnsafeUsage) {
        siqs_bigArgs =
            new PSIQS_U(
                0.32F,
                0.37F,
                null,
                null,
                numberOfThreads,
                new NoPowerFinder(),
                new MatrixSolver02_BlockLanczos());
      } else {
        siqs_bigArgs =
            new PSIQS(
                0.32F,
                0.37F,
                null,
                null,
                numberOfThreads,
                new NoPowerFinder(),
                new MatrixSolver02_BlockLanczos());
      }
    }

    // Other options that perform well: PowerOfSmallPrimesFinder, SingleBlockHybridSieve(U).
  }

  @Override
  public String getName() {
    return "combi(" + (tdivLimit != null ? tdivLimit : "auto") + ")";
  }

  @Override
  public BigInteger findSingleFactor(BigInteger N) {
    int NBits = N.bitLength();
    if (NBits < 25) return tDiv31.findSingleFactor(N);
    if (NBits < 50) return hart.findSingleFactor(N);
    if (NBits < 57) return pollardRhoR64Mul63.findSingleFactor(N);
    if (NBits < 63) return pollardRho64.findSingleFactor(N);
    if (NBits <= 150) return siqs_smallArgs.findSingleFactor(N);
    return siqs_bigArgs.findSingleFactor(N);
  }

  @Override
  public void searchFactors(FactorArguments args, FactorResult result) {
    int NBits = args.NBits;
    if (NBits < 32) {
      // Find all remaining factors; these are known to be prime factors.
      // The bit bound here is higher than in findSingleFactor() because here we find all factors in
      // a single tdiv run.
      tDiv31.factor(args.N, args.exp, result.primeFactors);
    } else if (NBits < 50) hart.searchFactors(args, result);
    else if (NBits < 57) pollardRhoR64Mul63.searchFactors(args, result);
    else if (NBits < 63) pollardRho64.searchFactors(args, result);
    else {
      if (SEARCH_SMALL_FACTORS) {
        int actualTdivLimit;
        if (tdivLimit != null) {
          // use "dictated" limit
          actualTdivLimit = tdivLimit.intValue();
        } else {
          // Adjust tdivLimit=2^e by experimental results.
          final double e = 10 + (args.NBits - 45) * 0.07407407407; // constant 0.07.. = 10/135
          actualTdivLimit = (int) Math.min(1 << 20, Math.pow(2, e)); // upper bound 2^20
        }
        if (actualTdivLimit > result.smallestPossibleFactor) {
          // there is still tdiv/EM work to do...
          BigInteger N0 = args.N;

          if (DEBUG) LOG.debug("result before TDiv: " + result);
          if (ANALYZE) t0 = System.currentTimeMillis();
          tdiv.setTestLimit(actualTdivLimit).searchFactors(args, result);
          if (ANALYZE)
            LOG.debug(
                "TDiv up to "
                    + actualTdivLimit
                    + " took "
                    + (System.currentTimeMillis() - t0)
                    + "ms");
          if (DEBUG) LOG.debug("result after TDiv:  " + result);

          if (result.untestedFactors.isEmpty()) return; // N was "easy"

          // Otherwise we continue
          BigInteger N = result.untestedFactors.firstKey();
          int exp = result.untestedFactors.removeAll(N);
          //					if (DEBUG) assertEquals(1, exp); // looks safe, otherwise we'ld have to consider
          // exp below

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
            ecm.searchFactors(
                args,
                result); // TODO a parallel ECM implementation with numberOfThreads threads would be
            // nice here
            if (ANALYZE) LOG.debug("ECM took " + (System.currentTimeMillis() - t0) + "ms");
            if (DEBUG) LOG.debug("result after ECM:  " + result);
          }

          if (!result.compositeFactors.containsKey(N0)) {
            // either tdiv or ECM found some factors -> return immediately
            return;
          }
          // Neither tdiv nor ECM found a factor. N0 has been added to compositeFactors again ->
          // remove it and continue with SIQS
          result.compositeFactors.removeAll(N);
        }
      }

      // SIQS / PSIQS: The crossover point seems to be at ~150 bit now, independently the number of
      // threads.
      // Strangely, my previous adjustment had the crossover point at 97 bit. Probably at that time
      // I wrongly compared
      // PSIQS(1 thread) with PSIQS(2 threads), but PSIQS(1 thread) also has a thread creation
      // penalty that SIQS does not have.
      if (NBits <= 150) siqs_smallArgs.searchFactors(args, result);
      else siqs_bigArgs.searchFactors(args, result);
    }
  }

  /**
   * Run with command-line arguments or console input (if no command-line arguments are given).
   * Usage for executable jar file: java -jar <jar_file> [[-t <numberOfThreads>] <numberToFactor>]
   *
   * <p>Some test numbers:
   *
   * <p>15841065490425479923 (64 bit) = 2604221509 * 6082841047 in 211ms. (done by PSIQS)
   *
   * <p>11111111111111111111111111 (84 bit) = {11=1, 53=1, 79=1, 859=1, 265371653=1, 1058313049=1}
   * in 185ms. (tdiv + PSIQS)
   *
   * <p>5679148659138759837165981543 (93 bit) = {3=3, 466932157=1, 450469808245315337=1} in 194ms.
   * (tdiv + PSIQS)
   *
   * <p>874186654300294020965320730991996026550891341278308 (170 bits) = {2=2, 3=1, 471997=1,
   * 654743=1, 2855761=1, 79833227=1, 982552477=1, 1052328969055591=1} in 685ms (tdiv + ECM + PSIQS)
   *
   * <p>11111111111111111111111111155555555555111111111111111 (173 bit) = {67=1, 157=1,
   * 1056289676880987842105819104055096069503860738769=1} in 21ms. (only tdiv needed)
   *
   * <p>1388091470446411094380555803943760956023126025054082930201628998364642 (230 bit) = {2=1,
   * 3=1, 1907=1, 1948073=1, 1239974331653=1, 50222487570895420624194712095309533522213376829=1} in
   * 304ms. (tdiv + ECM + PSIQS)
   *
   * <p>10^71-1 = 99999999999999999999999999999999999999999999999999999999999999999999999 (236 bits)
   * = {3=2, 241573142393627673576957439049=1, 45994811347886846310221728895223034301839=1} in 14s,
   * 471ms. (tdiv + PSIQS)
   *
   * <p>10^79+5923 =
   * 10000000000000000000000000000000000000000000000000000000000000000000000000005923 (263 bit) =
   * {1333322076518899001350381760807974795003=1, 7500063320115780212377802894180923803641=1} in 1m,
   * 35s, 243ms. (PSIQS)
   *
   * <p>2900608971182010301486951469292513060638582965350239259380273225053930627446289431038392125
   * (301 bit) = 33333 * 33335 * 33337 * 33339 * 33341 * 33343 * 33345 * 33347 * 33349 * 33351 *
   * 33353 * 33355 * 33357 * 33359 * 33361 * 33363 * 33367 * 33369 * 33369 * 33371 = {3=11, 5=3,
   * 7=6, 11=2, 13=2, 17=2, 19=1, 37=1, 41=1, 53=1, 59=1, 61=1, 73=1, 113=1, 151=1, 227=2, 271=1,
   * 337=1, 433=1, 457=1, 547=1, 953=1, 11113=1, 11117=1, 11119=1, 33343=1, 33347=1, 33349=1,
   * 33353=1, 33359=1} in 10ms. (only tdiv)
   *
   * @param args [-t <numberOfThreads>] <numberToFactor>
   */
  // Quite difficult 280 bit:
  // 1794577685365897117833870712928656282041295031283603412289229185967719140138841093599 takes
  // about 5:48 min
  public static void main(String[] args) {
    ConfigUtil.verbose = false;
    ConfigUtil.initProject();

    try {
      if (args.length == 0) {
        // test standard input in a loop
        testInput();
      }

      // otherwise we have commandline arguments -> parse them
      testArgs(args);
    } catch (Exception ite) {
      // when the jar is shut down with Ctrl-C, an InvocationTargetException is thrown (log4j?).
      // just suppress it and exit
      System.exit(0);
    }
  }

  private static int testInput() {
    while (true) {
      int numberOfThreads = 1;
      BigInteger N;
      String line = null;
      try {
        System.out.println("Please insert [-t <numberOfThreads>] <numberToFactor> :");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        line = in.readLine();
        String input = line.trim();
        if (input.startsWith("-t")) {
          input = input.substring(2).trim();
          StringTokenizer parser = new StringTokenizer(input);
          numberOfThreads = Integer.parseInt(parser.nextToken().trim());
          N = new BigInteger(parser.nextToken().trim());
        } else {
          N = new BigInteger(input);
        }
      } catch (IOException ioe) {
        System.err.println("IO-error occuring on input: " + ioe.getMessage());
        continue;
      } catch (NumberFormatException nfe) {
        System.err.println("Illegal input: " + line);
        continue;
      }
      test(numberOfThreads, N);
    } // next input...
  }

  private static void testArgs(String[] args) {
    int numberOfThreads = 1;
    BigInteger N = null;
    if (args.length == 1) {
      try {
        N = new BigInteger(args[0].trim());
      } catch (NumberFormatException nfe) {
        System.err.println("Invalid numberToFactor = " + args[0].trim());
        System.exit(-1);
      }
    } else if (args.length == 3) {
      if (!args[0].trim().equals("-t")) {
        System.err.println(
            "Illegal option: '"
                + args[0]
                + "'. Usage: java -jar <jar_file> [-t <numberOfThreads>] <numberToFactor>");
        System.exit(-1);
      }
      try {
        numberOfThreads = Integer.parseInt(args[1].trim());
      } catch (NumberFormatException nfe) {
        System.err.println("Invalid numberOfThreads = " + args[1].trim());
        System.exit(-1);
      }
      try {
        N = new BigInteger(args[2].trim());
      } catch (NumberFormatException nfe) {
        System.err.println("Invalid numberToFactor = " + args[2].trim());
        System.exit(-1);
      }
    } else {
      System.err.println(
          "Illegal number of arguments. Usage: java -jar <jar_file> [-t <numberOfThreads>] <numberToFactor>");
      System.exit(-1);
    }
    // run
    int exitCode = test(numberOfThreads, N);
    System.exit(exitCode);
  }

  private static int test(int numberOfThreads, BigInteger N) {
    if (numberOfThreads < 0) {
      System.err.println("numberOfThreads must be positive.");
      return -1;
    }
    if (numberOfThreads > ConfigUtil.NUMBER_OF_PROCESSORS) {
      System.err.println(
          "Too big numberOfThreads = "
              + numberOfThreads
              + ": Your machine has only "
              + ConfigUtil.NUMBER_OF_PROCESSORS
              + " processors");
      return -1;
    }

    // size check
    // LOG.debug("N = " + N);
    int N_bits = N.bitLength();
    if (N.bitLength() > 400) {
      System.err.println(
          "Too big numberToFactor: Currently only inputs <= 400 bits are supported. (Everything else would take months or years)");
      return -1;
    }
    // run
    long t0 = System.currentTimeMillis();
    CombinedFactorAlgorithm factorizer = new CombinedFactorAlgorithm(numberOfThreads, null, true);
    SortedMultiset<BigInteger> result = factorizer.factor(N);
    long duration = System.currentTimeMillis() - t0;
    String durationStr = TimeUtil.timeStr(duration);
    if (result.totalCount() == 1) {
      BigInteger singleElement = result.keySet().iterator().next();
      if (singleElement.abs().compareTo(I_1) <= 0) {
        System.out.println(N + " is trivial");
      } else {
        System.out.println(N + " is probable prime");
      }
    } else if (result.totalCount() == 2 && result.keySet().contains(I_MINUS_1)) {
      System.out.println(N + " is probable prime");
    } else {
      System.out.println(
          N
              + " ("
              + N_bits
              + " bits) = "
              + result.toString("*", "^")
              + " (factored in "
              + durationStr
              + ")");
    }
    return 0;
  }
}
