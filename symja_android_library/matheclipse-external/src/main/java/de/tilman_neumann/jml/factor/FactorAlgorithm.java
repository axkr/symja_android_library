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

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.tdiv.TDiv;
import de.tilman_neumann.jml.primes.probable.BPSWTest;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * Abstraction of integer factorization algorithms. This class provides a framework to find the
 * complete prime factorization of N, requiring only to implement the method
 * findSingleFactor(BigInteger).
 *
 * @author Tilman Neumann
 */
public abstract class FactorAlgorithm {
  private static final Logger LOG = Logger.getLogger(FactorAlgorithm.class);

  private static final boolean DEBUG = false;

  /**
   * The best available single-threaded factor algorithm. (multi-threading may not always be wanted)
   */
  public static FactorAlgorithm DEFAULT = new CombinedFactorAlgorithm(1);

  /** the number of primes needed to factor any int <= 2^31 - 1 using trial division */
  protected static final int NUM_PRIMES_FOR_31_BIT_TDIV = 4793;

  private BPSWTest bpsw = new BPSWTest();
  private TDiv tdiv = new TDiv();

  protected Integer tdivLimit;

  public FactorAlgorithm() {
    tdivLimit = null; // automatic determination based on experimental results
  }

  public FactorAlgorithm(Integer tdivLimit) {
    this.tdivLimit = tdivLimit;
  }

  /** @return The name of the algorithm, possibly including important parameters. */
  public abstract String getName();

  /**
   * Decomposes the argument N into prime factors. The result is a multiset of BigIntegers, sorted
   * bottom-up.
   *
   * @param N Number to factor.
   * @return The prime factorization of N
   */
  public SortedMultiset<BigInteger> factor(BigInteger N) {
    SortedMultiset<BigInteger> primeFactors = new SortedMultiset_BottomUp<BigInteger>();
    // first get rid of case |N|<=1:
    if (N.abs().compareTo(I_1) <= 0) {
      // https://oeis.org/wiki/Empty_product#Prime_factorization_of_1:
      // "the set of prime factors of 1 is the empty set"
      if (!N.equals(I_1)) {
        primeFactors.add(N);
      }
      return primeFactors;
    }
    // make N positive:
    if (N.signum() < 0) {
      primeFactors.add(I_MINUS_1);
      N = N.abs();
    }
    // Remove multiples of 2:
    int lsb = N.getLowestSetBit();
    if (lsb > 0) {
      primeFactors.add(I_2, lsb);
      N = N.shiftRight(lsb);
    }
    if (N.equals(I_1)) {
      // N was a power of 2
      return primeFactors;
    }

    int Nbits = N.bitLength();
    if (Nbits > 62) {
      // "Small" algorithms like trial division, Lehman or Pollard-Rho are very good themselves
      // at finding small factors, but for larger N we do some trial division.
      // This will help "big" algorithms to factor smooth numbers much faster.
      int actualTdivLimit;
      if (tdivLimit != null) {
        // use "dictated" limit
        actualTdivLimit = tdivLimit.intValue();
      } else {
        // adjust tdivLimit=2^e by experimental results
        final double e = 10 + (Nbits - 45) * 0.07407407407; // constant 0.07.. = 10/135
        actualTdivLimit = (int) Math.min(1 << 20, Math.pow(2, e)); // upper bound 2^20
      }

      N = tdiv.findSmallOddFactors(N, actualTdivLimit, primeFactors);
      // TODO add tdiv duration to final report

      if (N.equals(I_1)) {
        // N was "easy"
        return primeFactors;
      }
    }

    // N contains larger factors...
    ArrayList<BigInteger> untestedFactors =
        new ArrayList<BigInteger>(); // faster than SortedMultiset
    untestedFactors.add(N);
    while (untestedFactors.size() > 0) {
      N = untestedFactors.remove(untestedFactors.size() - 1);
      if (bpsw.isProbablePrime(N)) { // TODO exploit tdiv done so far
        // N is probable prime. In exceptional cases this prediction may be wrong and N composite
        // -> then we would falsely predict N to be prime. BPSW is known to be exact for N <= 64
        // bit.
        // LOG.debug(N + " is probable prime.");
        primeFactors.add(N);
        continue;
      }
      BigInteger factor1 = findSingleFactor(N);
      if (factor1.compareTo(I_1) > 0 && factor1.compareTo(N) < 0) {
        // found factor
        untestedFactors.add(factor1);
        untestedFactors.add(N.divide(factor1));
      } else {
        // findSingleFactor() failed to find a factor of the composite N
        if (DEBUG)
          LOG.error("Factor algorithm " + getName() + " failed to find a factor of composite " + N);
        primeFactors.add(N);
      }
    }
    // LOG.debug(this.factorAlg + ": => all factors = " + primeFactors);
    return primeFactors;
  }

  /**
   * Find a single factor of the given N, which is composite and odd.
   *
   * @param N
   * @return factor
   */
  public abstract BigInteger findSingleFactor(BigInteger N);
}
