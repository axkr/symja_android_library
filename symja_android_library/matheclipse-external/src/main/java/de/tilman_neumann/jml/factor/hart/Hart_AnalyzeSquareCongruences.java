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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.util.ConfigUtil;

/**
 * Analyze until which s we obtain test == "some square" (mod 2^s).
 *
 * @author Tilman Neumann
 */
public class Hart_AnalyzeSquareCongruences extends FactorAlgorithm {
  private static final Logger LOG = Logger.getLogger(Hart_AnalyzeSquareCongruences.class);

  /**
   * We only test k-values that are multiples of this constant. Best values for performance are 315,
   * 45, 105, 15 and 3, in that order.
   */
  private static final int K_MULT = 3 * 3 * 5 * 7; // 315

  /** Size of arrays */
  private static final int I_MAX = 1 << 20;

  /** This constant is used for fast rounding of double values to long. */
  private static final double ROUND_UP_DOUBLE = 0.9999999665;

  private final boolean doTDivFirst;
  private final double[] sqrt;

  private static final int MOD = 1024;
  private static final int ADJUST_COUNT = 30;
  private int[][] sMin = new int[MOD][ADJUST_COUNT];

  /**
   * Full constructor.
   *
   * @param doTDivFirst If true then trial division is done before the Lehman loop. This is
   *     recommended if arguments N are known to have factors < cbrt(N) frequently.
   */
  public Hart_AnalyzeSquareCongruences(boolean doTDivFirst) {
    this.doTDivFirst = doTDivFirst;
    // Precompute sqrts for all k < I_MAX
    sqrt = new double[I_MAX];
    for (int i = 1; i < I_MAX; i++) {
      sqrt[i] = Math.sqrt(i * K_MULT);
    }
    // initialize result
    for (int rest = 0; rest < MOD; rest += 2) {
      for (int adjustExp = 0; adjustExp < ADJUST_COUNT; adjustExp++) {
        sMin[rest][adjustExp] = 64;
      }
    }
  }

  @Override
  public String getName() {
    return "Hart_AnalyzeSquareCongruences(" + doTDivFirst + ")";
  }

  @Override
  public BigInteger findSingleFactor(BigInteger N) {
    return BigInteger.valueOf(findSingleFactor(N.longValue()));
  }

  /**
   * Find a factor of long N.
   *
   * @param N
   * @return factor of N
   */
  public long findSingleFactor(long N) {
    final long fourN = N << 2;
    final double sqrt4N = Math.sqrt(fourN);
    try {
      for (int i = 1, k = K_MULT; i < Math.cbrt(N); i++, k += K_MULT) {
        final long a = (long) (sqrt4N * sqrt[i] + ROUND_UP_DOUBLE);
        final long kNp1 = k * N + 1;
        final long fourkN = k * fourN;
        final int rest = (int) (kNp1 & (MOD - 1));
        for (int adjustExp = 0; adjustExp < ADJUST_COUNT; adjustExp++) {
          int adjustMod = 1 << adjustExp;
          long a2 = a + ((kNp1 - a) & (adjustMod - 1));
          int s = computeS(a2, fourkN);
          if (s < sMin[rest][adjustExp]) {
            sMin[rest][adjustExp] = s;
          }
        }
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      LOG.error(
          getName()
              + ": Failed to factor N="
              + N
              + ". Either it has factors < cbrt(N) needing trial division, or the arrays are too small.",
          e);
      return 1;
    }
    return 0;
  }

  private int computeS(long a, long fourkN) {
    long test = a * a - fourkN;

    // analyze test==square (mod 2^s)
    int s;
    for (s = 0; s < 64; s++) {
      long testMod = test % (1L << s);
      long sqrt = (long) Math.sqrt(testMod);
      if (sqrt * sqrt != testMod) { // 2^s failed
        s--; // last success
        break;
      }
    }
    return s;
  }

  private static final int NCOUNT = 1000;
  private static final int NBITS = 50;

  /**
   * Test.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();

    SecureRandom RNG = new SecureRandom();
    Hart_AnalyzeSquareCongruences holf = new Hart_AnalyzeSquareCongruences(false);
    for (int i = 0; i < NCOUNT; i++) {
      BigInteger N = new BigInteger(NBITS, RNG);
      LOG.info("Test N_" + i + " = " + N);
      holf.findSingleFactor(N);
    }
    int[] adjusts = new int[MOD];
    for (int rest = 0; rest < MOD; rest++) {
      int bestAdjustExp = 0;
      int maxSMin = 0;
      for (int adjustExp = 0; adjustExp < ADJUST_COUNT; adjustExp++) {
        int sMin = holf.sMin[rest][adjustExp];
        if (sMin > maxSMin) {
          maxSMin = sMin;
          bestAdjustExp = adjustExp;
        }
      }
      LOG.info(
          "(kN+1)%"
              + MOD
              + "="
              + rest
              + ": adjust = [(kN+1)-a] % (2^"
              + bestAdjustExp
              + ") has sMin="
              + maxSMin);
      adjusts[rest] = (1 << bestAdjustExp) - 1; // subtract 1 for use with & instead of %
    }
    LOG.debug("best adjusts=" + Arrays.toString(adjusts));
  }
}
