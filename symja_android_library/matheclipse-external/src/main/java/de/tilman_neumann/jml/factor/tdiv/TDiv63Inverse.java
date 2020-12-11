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
package de.tilman_neumann.jml.factor.tdiv;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.squfof.SquFoF63;
import de.tilman_neumann.jml.primes.bounds.PrimeCountUpperBounds;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * Trial division factor algorithm replacing division by multiplications.
 *
 * <p>Instead of dividing N by consecutive primes, we store the reciprocals of those primes, too,
 * and multiply N by those reciprocals. Only if such a result is near to an integer we need to do a
 * division.
 *
 * <p>Assuming that we want to identify "near integers" with a precision of 2^-d. Then the approach
 * works for primes p if bitLength(p) >= bitLength(N) - 53 + d.
 *
 * <p>For some unknown reason, storing and reusing the quotient q = (long) (N*r + DISCRIMINATOR)
 * only helps in TDiv31Inverse but not in TDiv63Inverse.
 *
 * @authors Thilo Harich + Tilman Neumann
 */
public class TDiv63Inverse extends FactorAlgorithm {
  private static final Logger LOG = Logger.getLogger(TDiv63Inverse.class);

  private static AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get();

  private static final int DISCRIMINATOR_BITS = 10; // experimental result
  private static final double DISCRIMINATOR = 1.0 / (1 << DISCRIMINATOR_BITS);

  private int[] primes;
  private double[] reciprocals;
  private int factorLimit, pLimit, primeCountBound;

  /**
   * Create a trial division algorithm that is capable of finding factors up to factorLimit.
   *
   * @param factorLimit
   */
  public TDiv63Inverse(int factorLimit) {
    this.factorLimit = factorLimit;
    pLimit = factorLimit; // default if not set explicitly
    primeCountBound = (int) PrimeCountUpperBounds.combinedUpperBound(factorLimit);
    primes = new int[primeCountBound];
    reciprocals = new double[primeCountBound];
    for (int i = 0; i < primeCountBound; i++) {
      int p = SMALL_PRIMES.getPrime(i);
      primes[i] = p;
      reciprocals[i] = 1.0 / p;
    }
  }

  @Override
  public String getName() {
    return "TDiv63Inverse";
  }

  /**
   * Set the upper limit of primes to be tested in the next findSingleFactor() run. pLimit must be
   * smaller than the factorLimit parameter passed to the constructor.
   *
   * @param pLimit
   * @throws IllegalStateException if pLimit > factorLimit
   */
  public void setTestLimit(int pLimit) {
    if (pLimit > factorLimit) {
      throw new IllegalStateException(
          "Requested pLimit="
              + pLimit
              + " exceeds the factorLimit="
              + factorLimit
              + " passed to the constructor!");
    }
    this.pLimit = pLimit;
  }

  @Override
  public SortedMultiset<BigInteger> factor(BigInteger Nbig) {
    int NBits = Nbig.bitLength();
    if (NBits > 63) {
      throw new IllegalArgumentException(
          "Argument N=" + Nbig + " (" + NBits + " bit) is too large for algorithm " + getName());
    }

    SortedMultiset<BigInteger> primeFactors = new SortedMultiset_BottomUp<>();
    long N = Nbig.longValue();

    int i = 0;
    int pMinBits = NBits - 53 + DISCRIMINATOR_BITS;
    if (pMinBits > 0) {
      // for the smallest primes we must do standard trial division
      int pMin = 1 << pMinBits, p;
      for (; (p = primes[i]) < pMin; i++) {
        int exp = 0;
        while (N % p == 0) {
          exp++;
          N /= p;
        }
        if (exp > 0) {
          primeFactors.add(BigInteger.valueOf(p), exp);
        }
      }
    }

    int p, exp;
    for (; (p = primes[i]) <= pLimit; i++) {
      exp = 0;
      double r = reciprocals[i];
      while ((long) (N * r + DISCRIMINATOR) * p == N) {
        exp++;
        N /= p;
      }
      if (exp > 0) {
        primeFactors.add(BigInteger.valueOf(p), exp);
      }
      if (p * (long) p > N) {
        break; // the remaining N is prime
      }
    }

    if (N > 1) {
      // either N is prime, or we could not find all factors with p<=pLimit -> add the rest to the
      // result
      primeFactors.add(BigInteger.valueOf(N));
    }
    return primeFactors;
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation will return 1 if the smallest factor of N is greater than pLimit.
   */
  @Override
  public BigInteger findSingleFactor(BigInteger N) {
    if (N.bitLength() > 63)
      throw new IllegalArgumentException(
          "TDiv63Inverse.findSingleFactor() does not work for N>63 bit, but N=" + N);
    return BigInteger.valueOf(findSingleFactor(N.longValue()));
  }

  public int findSingleFactor(long N) {
    if (N < 0) N = -N; // sign does not matter
    if (N < 4) return 1; // prime
    if ((N & 1) == 0) return 2; // N even

    int i = 1;
    // LOG.debug("N=" + N);
    int Nbits = 64 - Long.numberOfLeadingZeros(N);
    int pMinBits = Nbits - 53 + DISCRIMINATOR_BITS;
    try {
      if (pMinBits > 0) {
        // for the smallest primes we must do standard trial division
        int pMin = 1 << pMinBits;
        for (; primes[i] < pMin; i++) {
          if (N % primes[i] == 0) {
            return primes[i];
          }
        }
      }

      // Now the primes are big enough to apply trial division by inverses; unroll the loop.
      // We stop when pLimit is reached, which may have been set before via setTestLimit().
      for (; primes[i] <= pLimit; i++) {
        // LOG.debug("N=" + N + ": Test p=" + primes[i]);
        if (((long) (N * reciprocals[i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
        if (((long) (N * reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
        if (((long) (N * reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
        if (((long) (N * reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
        if (((long) (N * reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
        if (((long) (N * reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
        if (((long) (N * reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
        if (((long) (N * reciprocals[++i] + DISCRIMINATOR)) * primes[i] == N) return primes[i];
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      int pMaxIndex = primeCountBound - 1;
      int pMax = primes[pMaxIndex];
      LOG.error(
          "TDiv63Inverse has been set up to find factors until p["
              + pMaxIndex
              + "] = "
              + pMax
              + ", but now you are trying to access p["
              + i
              + "] !");
    }

    // nothing found up to pLimit
    return 1;
  }

  /**
   * Test.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();

    // test special cases
    TDiv63Inverse tdivInv = new TDiv63Inverse(1 << 21);
    SquFoF63 testFactorizer = new SquFoF63();
    long[] specialN =
        new long[] {
          // first charge
          621887327L,
          676762483L,
          2947524803L,
          5616540799L,
          35936505149L,
          145682871839L,
          317756737253L,
          3294635112749L,
          // second charge
          13293477682249L,
          24596491225651L, // = 3311299 * 7428049; can not be found with factorLimit=2^21
          44579405690563L,
          72795445155721L,
          155209074377713L,
          293851765137859L, // = 11736397 * 25037647; can not be found with factorLimit=2^21
          67915439339311L // = 2061599 * 32943089, ok
        };

    for (long N : specialN) {
      long tdivFactor = tdivInv.findSingleFactor(N);
      long testFactor = testFactorizer.findSingleFactor(N);
      if (tdivFactor <= 1 || tdivFactor == N) {
        LOG.debug(
            "TDiv63Inverse failed to factor N="
                + N
                + " = "
                + testFactor
                + " * "
                + (N / testFactor));
      }
    }

    // test random N
    SecureRandom RNG = new SecureRandom();
    int count = 100000;
    for (int bits = 10; bits < 64; bits++) {
      LOG.info("Testing " + count + " random numbers with " + bits + " bits...");
      tdivInv.setTestLimit(1 << Math.min(21, (bits + 1) / 2));
      int failCount = 0;
      for (int i = 0; i < count; i++) {
        long N = 0;
        while (true) {
          BigInteger N_big = new BigInteger(bits, RNG);
          N = N_big.longValue();
          if (N > 2 && !N_big.isProbablePrime(20)) break;
        }
        long tdivFactor = tdivInv.findSingleFactor(N);
        if (tdivFactor < 2) {
          long testFactor = testFactorizer.findSingleFactor(N);
          if (testFactor > 1 && testFactor < N) {
            // LOG.debug("N=" + N + ": TDiv failed to find factor " + testFactor);
            failCount++;
          } else {
            LOG.error("The test factorizer failed to factor N=" + N + " !");
          }
        } else {
          if (N % tdivFactor != 0) {
            failCount++;
          }
        }
      }
      LOG.info("    #fails = " + failCount);
    }
  }
}
