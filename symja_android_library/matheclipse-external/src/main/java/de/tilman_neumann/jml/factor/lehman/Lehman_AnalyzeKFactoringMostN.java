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
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.jml.factor.TestsetGenerator;
import de.tilman_neumann.jml.factor.tdiv.TDiv63Inverse;
import de.tilman_neumann.jml.factor.TestNumberNature;

/**
 * Try to find the best k-sequence.
 *
 * @author Tilman Neumann
 */
public class Lehman_AnalyzeKFactoringMostN {
  private static final Logger LOG = Logger.getLogger(Lehman_AnalyzeKFactoringMostN.class);

  /** Use congruences a==kN mod 2^s if true, congruences a==(k+N) mod 2^s if false */
  private static final boolean USE_kN_CONGRUENCES = true;

  /** number of test numbers */
  private static final int N_COUNT = 1000000;

  /** the bit size of N */
  private static final int BITS = 40;

  /** This is a constant that is below 1 for rounding up double values to long. */
  private static final double ROUND_UP_DOUBLE = 0.9999999665;

  private long fourN;
  private double sqrt4N;
  private final Gcd63 gcdEngine = new Gcd63();
  private final TDiv63Inverse tdiv = new TDiv63Inverse(1 << 21);

  TreeMap<Integer, HashSet<Long>> k2FactoredN;

  public long findSingleFactor(long N) {
    final int cbrt = (int) Math.cbrt(N);
    fourN = N << 2;
    sqrt4N = Math.sqrt(fourN);

    final int kLimit = cbrt;
    final double sixthRootTerm =
        0.25 * Math.pow(N, 1 / 6.0); // double precision is required for stability
    for (int k = 1; k <= kLimit; k++) {
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
                4; // stepping over both adjusts with step width 16 would be more exact but is not
            // faster
          }
        }
      }

      // processing the a-loop top-down is faster than bottom-up
      final long fourkN = k * fourN;
      for (long a = aLimit; a >= aStart; a -= aStep) {
        final long test = a * a - fourkN;
        // Here test<0 is possible because of double to long cast errors in the 'a'-computation.
        // But then b = Math.sqrt(test) gives 0 (sic!) => 0*0 != test => no errors.
        final long b = (long) Math.sqrt(test);
        if (b * b == test) {
          long gcd = gcdEngine.gcd(a + b, N);
          if (gcd > 1 && gcd < N) {
            HashSet<Long> NfactoredByK = k2FactoredN.get(k);
            if (NfactoredByK == null) NfactoredByK = new HashSet<>();
            NfactoredByK.add(N);
            k2FactoredN.put(k, NfactoredByK);
          }
        }
      }
    }

    return 1; // fail
  }

  private void testRange(int bits) {
    k2FactoredN = new TreeMap<>();
    BigInteger[] testNumbers =
        TestsetGenerator.generate(N_COUNT, bits, TestNumberNature.MODERATE_SEMIPRIMES);
    LOG.info("Test N having " + bits + " bit");

    for (BigInteger N : testNumbers) {
      this.findSingleFactor(N.longValue());
    }

    int ncount = N_COUNT;
    HashSet<Long> previousBestNSet = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
      int bestK = 0;
      int bestNumberOfFactors = 0;
      HashSet<Long> bestNSet = null;
      for (Map.Entry<Integer, HashSet<Long>> entry : k2FactoredN.entrySet()) {
        int k = entry.getKey();
        HashSet<Long> NSet = entry.getValue();
        NSet.removeAll(previousBestNSet);
        if (NSet.size() > bestNumberOfFactors) {
          bestNumberOfFactors = NSet.size();
          bestK = k;
          bestNSet = NSet;
        }
      }
      LOG.info(
          "k_"
              + i
              + " = "
              + bestK
              + " = "
              + tdiv.factor(BigInteger.valueOf(bestK))
              + " factors "
              + bestNSet.size()
              + " of the remaining "
              + ncount
              + " N");
      k2FactoredN.remove(bestK);
      ncount -= bestNumberOfFactors;
      previousBestNSet = bestNSet;
    }
  }

  public static void main(String[] args) {
    ConfigUtil.initProject();
    // test N with BITS bits
    Lehman_AnalyzeKFactoringMostN testEngine = new Lehman_AnalyzeKFactoringMostN();
    testEngine.testRange(BITS);
  }
}
