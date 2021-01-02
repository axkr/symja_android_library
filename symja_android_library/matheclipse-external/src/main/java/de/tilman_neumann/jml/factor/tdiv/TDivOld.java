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

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.UnsignedBigInt;
import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.factor.base.FactorArguments;
import de.tilman_neumann.jml.factor.base.FactorResult;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;

/**
 * Trial division for large arguments.
 *
 * @author Tilman Neumann
 */
public class TDivOld extends FactorAlgorithm {
  @SuppressWarnings("unused")
  private static final Logger LOG = Logger.getLogger(TDivOld.class);

  private static AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get();

  private int pLimit = Integer.MAX_VALUE;

  @Override
  public String getName() {
    return "TDiv";
  }

  /**
   * Set the upper limit of primes to be tested.
   *
   * @param pLimit
   * @return this
   */
  public TDivOld setTestLimit(int pLimit) {
    this.pLimit = pLimit;
    return this;
  }

  /**
   * Tries to find small factors of a positive, possibly large argument N by doing trial division by
   * all primes <= limit.
   *
   * @param N
   * @param limit
   * @param primeFactors the prime factors that have been found are added to this map
   * @return the unfactored rest, N / prod{primeFactors}
   */
  public BigInteger findSmallFactors(
      BigInteger N, int limit, SortedMap<BigInteger, Integer> primeFactors) {
    // Remove multiples of 2:
    int lsb = N.getLowestSetBit();
    if (lsb > 0) {
      primeFactors.put(I_2, lsb);
      N = N.shiftRight(lsb);
    }

    return N.equals(I_1) ? I_1 : findSmallOddFactors(N, limit, primeFactors);
  }

  /**
   * Tries to find small _odd_ factors of a positive, possibly large argument N by doing trial
   * division by all primes p with 3 <= p <= limit. Typically, factor 2 should have been removed
   * before calling this method.
   *
   * @param N
   * @param limit
   * @param primeFactors the prime factors that have been found are added to this map
   * @return the unfactored rest, N / prod{primeFactors}
   */
  public BigInteger findSmallOddFactors(
      BigInteger N, int limit, SortedMap<BigInteger, Integer> primeFactors) {
    SMALL_PRIMES.ensureLimit(limit);

    int p_i;
    for (int i = 1; (p_i = SMALL_PRIMES.getPrime(i)) <= limit; i++) {
      BigInteger p_i_big = BigInteger.valueOf(p_i);
      BigInteger[] div = N.divideAndRemainder(p_i_big);
      if (div[1].equals(I_0)) {
        // p_i divides N at least once
        do {
          addToMap(p_i_big, 1, primeFactors);
          N = div[0];
          div = N.divideAndRemainder(p_i_big);
        } while (div[1].equals(I_0));

        // At least one division has occurred; check if we are done.
        // Probably the check could be improved but it wont make much difference because the
        // divisions are much more expensive.
        if (N.bitLength() < 63) {
          long p_i_square = p_i * (long) p_i;
          if (p_i_square > N.longValue()) {
            // LOG.debug("N=" + N + " < p^2=" + p_i_square);
            // the remaining N is 1 or prime
            if (N.compareTo(I_1) > 0) addToMap(N, 1, primeFactors);
            return I_1;
          }
        }
      }
    }

    return N; // unfactored rest
  }

  /**
   * Tries to find small _odd_ factors of a positive, possibly large argument N by doing trial
   * division by all primes p with 3 <= p <= limit. Typically, factor 2 should have been removed
   * before calling this method.
   *
   * @param args
   * @param limit
   * @param result a pre-initalized data structure to add results to
   * @return true if a factor has been found
   */
  public boolean findSmallOddFactors(FactorArguments args, int limit, FactorResult result) {
    SMALL_PRIMES.ensureLimit(limit);

    BigInteger N = args.N;
    int Nexp = args.exp;
    SortedMap<BigInteger, Integer> primeFactors = result.primeFactors;

    boolean found = false;
    int p_i;
    for (int i = 1; (p_i = SMALL_PRIMES.getPrime(i)) <= limit; i++) {
      BigInteger p_i_big = BigInteger.valueOf(p_i);
      BigInteger[] div = N.divideAndRemainder(p_i_big);
      if (div[1].equals(I_0)) {
        // p_i divides N at least once
        found = true;

        do {
          addToMap(p_i_big, Nexp, primeFactors);
          N = div[0];
          div = N.divideAndRemainder(p_i_big);
        } while (div[1].equals(I_0));

        // At least one division has occurred; check if we are done.
        // Probably the check could be improved but it wont make much difference because the
        // divisions are much more expensive.
        if (N.bitLength() < 63) {
          long p_i_square = p_i * (long) p_i;
          if (p_i_square > N.longValue()) {
            // LOG.debug("N=" + N + " < p^2=" + p_i_square);
            // the remaining N is 1 or prime
            if (N.compareTo(I_1) > 0) addToMap(N, Nexp, primeFactors);
            result.smallestPossibleFactorRemaining =
                p_i; // may be helpful in following factor algorithms
            return true;
          }
        }
      }
    }

    result.smallestPossibleFactorRemaining = p_i; // may be helpful in following factor algorithms
    result.untestedFactors.add(N, Nexp); // we do not know if the remaining N is prime or composite
    return found;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Very simple implementation just to complete the FactorAlgorithm interface.
   */
  @Override
  public BigInteger findSingleFactor(BigInteger N) {
    if (N.signum() < 0) N = N.negate(); // sign does not matter
    if (N.bitLength() < 3) return I_1; // N<4 is not composite
    if (!N.testBit(0)) return I_2; // N even
    UnsignedBigInt N_UBI = new UnsignedBigInt(N);

    int i = 1, p;
    while ((p = SMALL_PRIMES.getPrime(i++)) <= pLimit) { // upper bound avoids positive int overflow
      if (N_UBI.mod(p) == 0) return BigInteger.valueOf(p);
    }

    // nothing found up to pLimit
    return I_1;
  }

  private void addToMap(BigInteger N, int exp, SortedMap<BigInteger, Integer> map) {
    Integer oldExp = map.get(N);
    // replaces old entry if oldExp!=null
    map.put(N, (oldExp == null) ? exp : oldExp + exp);
  }
}
