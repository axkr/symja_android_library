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
package de.tilman_neumann.jml.factor.pollardRho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.Uint128;
import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.SortedMultiset;

/**
 * Brents's improvement of Pollard's Rho algorithm using Montgomery multiplication.
 *
 * <p>This version combines the Montgomery reducer R=2^64 with mul63(). It is significantly faster
 * than PollardRhoBrentMontgomery64 for N < 58 bits, because until that size the performance
 * advantage of mul63() vs. mul64() predominates the performance loss caused by errors in Montgomery
 * multiplication.
 *
 * <p>Another small performance gain stems from the choice of polynomials: x_(n+1) = x_n*(x_n + 1)
 * is slightly faster than x_(n+1) = (x_n)^2 - c because it does not need another reduction (mod N)
 * after subtracting c.
 *
 * @see [Richard P. Brent: An improved Monte Carlo Factorization Algorithm, 1980]
 * @see [http://projecteuler.chat/viewtopic.php?t=3776]
 * @see [http://coliru.stacked-crooked.com/a/f57f11426d06acd8]
 * @author Tilman Neumann
 */
public class PollardRhoBrentMontgomeryR64Mul63 extends FactorAlgorithm {
  private static final Logger LOG = Logger.getLogger(PollardRhoBrentMontgomeryR64Mul63.class);
  private static final boolean DEBUG = false;

  private static final SecureRandom RNG = new SecureRandom();

  // The reducer R is 2^64, but the only constant still required is the half of it.
  private static final long R_HALF = 1L << 63;

  private long N;

  private long minusNInvModR; // (-1/N) mod R, required for Montgomery multiplication

  private Gcd63 gcd = new Gcd63();

  @Override
  public String getName() {
    return "PollardRhoBrentMontgomeryR64Mul63";
  }

  @Override
  public BigInteger findSingleFactor(BigInteger N) {
    return BigInteger.valueOf(findSingleFactor(N.longValue()));
  }

  public long findSingleFactor(long N) {
    // N==9 would require to check if the gcd is 1 < gcd < N before returning it as a factor
    if (N == 9) return 3;

    this.N = N;
    long G, x, ys;

    setUpMontgomeryMult();

    // number of iterations before gcd tests.
    // Brent: "The probability of the algorithm failing because q_i=0 increases, so it is best not
    // to choose m too large"
    final int Nbits = 64 - Long.numberOfLeadingZeros(N);
    final int m = 2 * Nbits;

    do {
      // start with random y from [0, N)
      long y = Math.abs(RNG.nextLong()) % N;

      int r = 1;
      long q = 1;
      do {
        x = y;
        for (int i = r; i > 0; i--) {
          y = montMul63(y, y + 1, N, minusNInvModR);
        }
        int k = 0;
        do {
          ys = y;
          final int iMax = Math.min(m, r - k);
          for (int i = iMax; i > 0; i--) {
            y = montMul63(y, y + 1, N, minusNInvModR);
            final long diff = x < y ? y - x : x - y;
            q = montMul63(diff, q, N, minusNInvModR);
          }
          G = gcd.gcd(q, N);
          // if q==0 then G==N -> the loop will be left and restarted with new y
          k += m;
          // LOG.info("r = " + r + ", k = " + k);
        } while (k < r && G == 1);
        r <<= 1;
        // LOG.info("r = " + r + ", G = " + G);
      } while (G == 1);
      if (G == N) {
        do {
          ys = montMul63(ys, ys + 1, N, minusNInvModR);
          final long diff = x < ys ? ys - x : x - ys;
          G = gcd.gcd(diff, N);
        } while (G == 1);
        // LOG.info("G = " + G);
      }
    } while (G == N);
    // LOG.debug("Found factor " + G + " of N=" + N);
    return G;
  }

  /**
   * Finds (1/R) mod N and (-1/N) mod R for odd N and R=2^64.
   *
   * <p>As before, EEA63 would not work for R=2^64, but with a minor modification the algorithm from
   * http://coliru.stacked-crooked.com/a/f57f11426d06acd8 still works for R=2^64.
   */
  private void setUpMontgomeryMult() {
    // initialization
    long a = R_HALF;
    long u = 1;
    long v = 0;

    while (a != 0) { // modification
      a >>>= 1;
      if ((u & 1) == 0) {
        u >>>= 1;
        v >>>= 1;
      } else {
        u = ((u ^ N) >>> 1) + (u & N);
        v = (v >>> 1) + R_HALF;
      }
    }

    // u = (1/R) mod N and v = (-1/N) mod R. We only need the latter.
    minusNInvModR = v;
  }

  /**
   * Montgomery multiplication of a*b mod n with regard to R=2^63. ("mulredc63x" in Yafu)
   *
   * @param a
   * @param b
   * @param N
   * @param Nhat complement of N mod 2^63
   * @return Montgomery multiplication of a*b mod n
   */
  public static long montMul63(long a, long b, long N, long Nhat) {
    // Step 1: Compute a*b
    Uint128 ab = Uint128.mul63(a, b);
    // Step 2: Compute t = ab * (-1/N) mod R
    // Since R=2^64, "x mod R" just means to get the low part of x.
    // That would give t = Uint128.mul64(ab.getLow(), minusNInvModR).getLow();
    // but even better, the long product just gives the low part -> we can get rid of one expensive
    // mul64().
    long t = ab.getLow() * Nhat;
    // Step 3: Compute r = (a*b + t*N) / R
    // Since R=2^64, "x / R" just means to get the high part of x.
    long r = ab.add_getHigh(Uint128.mul63(t, N));
    // If the correct result is c, then now r==c or r==c+N.
    // This is fine for this factoring algorithm, because r will
    // * either be subjected to another Montgomery multiplication mod N,
    // * or to a gcd(r, N), where it doesn't matter if we test gcd(c, N) or gcd(c+N, N).

    //		if (DEBUG) {
    //			//LOG.debug(a + " * " + b + " = " + r);
    //			assertTrue(a >= 0 && a<N);
    //			assertTrue(b >= 0 && b<N);
    //
    //			// In a general Montgomery multiplication we would still have to check
    //			r = r<N ? r : r-N;
    //			// to satisfy
    //			assertTrue(r >= 0 && r < N);
    //		}

    return r;
  }

  /**
   * Test. Test numbers: 3225275494496681 (52 bits) = 56791489 * 56791529 322527333642009919 (59
   * bits) = 567914891 * 567914909 3225273260887418687 (62 bits) = 567914891 * 5679148957
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();

    while (true) {
      BigInteger n;
      try {
        LOG.info("Please insert the integer to factor:");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = in.readLine();
        String input = line.trim();
        n = new BigInteger(input);
        LOG.debug("factoring " + input + " (" + n.bitLength() + " bits) ...");
      } catch (IOException ioe) {
        LOG.error("io-error occuring on input: " + ioe.getMessage());
        continue;
      }

      long start = System.currentTimeMillis();
      SortedMultiset<BigInteger> result = new PollardRhoBrentMontgomeryR64Mul63().factor(n);
      LOG.info(
          "Factored "
              + n
              + " = "
              + result.toString()
              + " in "
              + (System.currentTimeMillis() - start)
              + " ms");
    } // next input...
  }
}
