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
package de.tilman_neumann.jml.factor.lehman;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.jml.factor.TestsetGenerator;
import de.tilman_neumann.jml.factor.TestNumberNature;

/**
 * Analyze the frequency with which different arithmetic progressions (k = start + step*m) find a
 * factor.
 *
 * @author Tilman Neumann
 */
public class Lehman_AnalyzeKProgressions {
  private static final Logger LOG = Logger.getLogger(Lehman_AnalyzeKProgressions.class);

  private static class Progression {
    // parameters
    public int start;
    public int step;
    // results
    public int testCount;
    public int factoredN;

    public Progression(int start, int step) {
      this.start = start;
      this.step = step;
      this.testCount = 0;
      this.factoredN = 0;
    }

    @Override
    public String toString() {
      return step + "*m + " + start;
    }
  }

  /** Use congruences a==kN mod 2^s if true, congruences a==(k+N) mod 2^s if false */
  private static final boolean USE_kN_CONGRUENCES = true;

  private static final int BITS = 35;

  /** number of test numbers */
  private static final int N_COUNT = 100000;

  /** This is a constant that is below 1 for rounding up double values to long. */
  private static final double ROUND_UP_DOUBLE = 0.9999999665;

  private long fourN;
  private double sqrt4N;
  private final Gcd63 gcdEngine = new Gcd63();

  public void findSingleFactor(long N, Progression progression) {
    final int cbrt = (int) Math.cbrt(N);
    fourN = N << 2;
    sqrt4N = Math.sqrt(fourN);

    final int kLimit = cbrt;
    final double sixthRootTerm =
        0.25 * Math.pow(N, 1 / 6.0); // double precision is required for stability
    for (int k = progression.start; k <= kLimit; k += progression.step) {
      progression.testCount++; // XXX
      double sqrtK = Math.sqrt(k);
      final double sqrt4kN = sqrt4N * sqrtK;
      // only use long values
      final long aStart = (long) (sqrt4kN + ROUND_UP_DOUBLE); // much faster than ceil()
      long aLimit = (long) (sqrt4kN + sixthRootTerm / sqrtK);
      long aStep;
      if (USE_kN_CONGRUENCES) {
        long kN = k * N;
        if ((k & 1) == 0) {
          // k even -> make sure aLimit is odd
          aLimit |= 1L;
          aStep = 2;
        } else {
          final long kNp1 = kN + 1;
          if ((kNp1 & 3) == 0) {
            aLimit += (kNp1 - aLimit) & 7;
            aStep = 8;
          } else if ((kNp1 & 7) == 6) {
            final long adjust1 = (kNp1 - aLimit) & 31;
            final long adjust2 = (-kNp1 - aLimit) & 31;
            aLimit += adjust1 < adjust2 ? adjust1 : adjust2;
            aStep = 4;
          } else { // (kN+1) == 2 (mod 8)
            final long adjust1 = (kNp1 - aLimit) & 15;
            final long adjust2 = (-kNp1 - aLimit) & 15;
            aLimit += adjust1 < adjust2 ? adjust1 : adjust2;
            aStep = 4;
          }
        }
      } else {
        if ((k & 1) == 0) {
          // k even -> make sure aLimit is odd
          aLimit |= 1L;
          aStep = 2;
        } else {
          final long kPlusN = k + N;
          if ((kPlusN & 3) == 0) {
            aLimit += ((kPlusN - aLimit) & 7);
            aStep = 8;
          } else {
            final long adjust1 = (kPlusN - aLimit) & 15;
            final long adjust2 = (-kPlusN - aLimit) & 15;
            aLimit += adjust1 < adjust2 ? adjust1 : adjust2;
            aStep =
                4; // XXX true step widths may be bigger, like 4, 28, 4, 28, ... But computations
            // are too costly.
          }
        }
      }

      // processing the a-loop top-down is faster than bottom-up
      final long fourkN = k * fourN;
      for (long a = aLimit; a >= aStart; a -= aStep) {
        //				progression.testCount++;
        // XXX odd k progressions need more tests than even k progressions.
        // That's probably because for odd k we add up to 31 to aLimit, but aStep is still 4.
        final long test = a * a - fourkN;
        final long b = (long) Math.sqrt(test);
        if (b * b == test) {
          long gcd = gcdEngine.gcd(a + b, N);
          if (gcd > 1 && gcd < N) {
            progression.factoredN++;
            return;
          }
        }
      }
    }

    return; // fail
  }

  private void test() {
    LOG.info("Test N having " + BITS + " bit");
    BigInteger[] testNumbers =
        TestsetGenerator.generate(N_COUNT, BITS, TestNumberNature.MODERATE_SEMIPRIMES);
    List<Progression> initialProgressions = new ArrayList<Progression>();
    for (int j = 1; j < 1000; j++) {
      initialProgressions.add(new Progression(j, 2 * j)); // odd k
      initialProgressions.add(new Progression(2 * j, 2 * j)); // even k
    }

    for (Progression progression : initialProgressions) {
      LOG.info("Test initial progression " + progression);
      for (BigInteger N : testNumbers) {
        this.findSingleFactor(N.longValue(), progression);
      }
    }

    TreeMap<Double, List<Progression>> successRate2Progressions =
        new TreeMap<Double, List<Progression>>(Collections.reverseOrder());
    for (Progression progression : initialProgressions) {
      double avgSuccessCount = progression.factoredN / (double) progression.testCount;
      List<Progression> progressionsList = successRate2Progressions.get(avgSuccessCount);
      if (progressionsList == null) progressionsList = new ArrayList<Progression>();
      progressionsList.add(progression);
      successRate2Progressions.put(avgSuccessCount, progressionsList);
    }

    int j = 0;
    for (Map.Entry<Double, List<Progression>> entry : successRate2Progressions.entrySet()) {
      double successRate = entry.getKey();
      List<Progression> progressions = entry.getValue();
      for (Progression progression : progressions) {
        LOG.info(
            "    #"
                + j
                + ": Progression ("
                + progression
                + ") factored "
                + progression.factoredN
                + " test numbers with "
                + progression.testCount
                + " tests -> successRate = "
                + successRate);
        j++;
      }
    }
  }

  public static void main(String[] args) {
    ConfigUtil.initProject();
    // test N with BITS bits
    Lehman_AnalyzeKProgressions testEngine = new Lehman_AnalyzeKProgressions();
    testEngine.test();
  }
}
