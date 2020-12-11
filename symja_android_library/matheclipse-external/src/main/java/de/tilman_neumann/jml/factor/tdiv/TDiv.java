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

import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;

/**
 * Trial division for large arguments.
 *
 * @author Tilman Neumann
 */
public class TDiv {
  @SuppressWarnings("unused")
  private static final Logger LOG = Logger.getLogger(TDiv.class);

  private static AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get();

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

        if (N.bitLength() < 63) {
          // check if we are done
          // TODO do also when no division occurred? the p are increasing all the time...
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

  private void addToMap(BigInteger N, int exp, SortedMap<BigInteger, Integer> map) {
    Integer oldExp = map.get(N);
    // replaces old entry if oldExp!=null
    map.put(N, (oldExp == null) ? exp : oldExp + exp);
  }
}
