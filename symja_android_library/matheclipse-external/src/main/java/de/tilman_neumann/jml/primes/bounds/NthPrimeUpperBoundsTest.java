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
package de.tilman_neumann.jml.primes.bounds;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.primes.exact.SegmentedSieve;
import de.tilman_neumann.jml.primes.exact.SieveCallback;
import de.tilman_neumann.util.ConfigUtil;

/**
 * Test of upper bound estimates for the n.th prime.
 *
 * @author Tilman Neumann
 */
public class NthPrimeUpperBoundsTest implements SieveCallback {
  private static final Logger LOG = Logger.getLogger(NthPrimeUpperBoundsTest.class);

  private long n;

  /**
   * Run the sieve.
   *
   * @param limit maximum value to be checked for being prime.
   */
  private void run(long limit) {
    n = 0;
    SegmentedSieve segmentedSieve = new SegmentedSieve(this);
    segmentedSieve.sieve(limit);
  }

  /**
   * Fallback method: Receives new primes from the sieve and checks the upper bound estimates for
   * the n.th prime p(n).
   *
   * <p>Changing the mod we can regulate the resolution of the data to look at. Searching in the
   * results for something like "rs01=-" lets us investigate in which ranges the particular
   * algorithms work.
   *
   * @param p the exact n.th prime
   */
  @Override
  public void processPrime(long p) {
    n++; // update count (prime index)
    if (n % 10000000 == 0) {
      String boundStr = "p_" + n + " = " + p + ": ";

      long rs01 = NthPrimeUpperBounds.RosserSchoenfeld01(n);
      boundStr += "rs01=" + (rs01 - p);
      long rs02 = NthPrimeUpperBounds.RosserSchoenfeld02(n);
      boundStr += ", rs02=" + (rs02 - p);
      long roj = NthPrimeUpperBounds.RobinJacobsen(n);
      boundStr += ", roj=" + (roj - p);
      long rob = NthPrimeUpperBounds.Robin1983(n);
      boundStr += ", rob=" + (rob - p);
      long du99 = NthPrimeUpperBounds.Dusart1999(n);
      boundStr += ", du99=" + (du99 - p);
      long du10p7 = NthPrimeUpperBounds.Dusart2010p7(n);
      boundStr += ", du10p7=" + (du10p7 - p);
      long du10p8 = NthPrimeUpperBounds.Dusart2010p8(n);
      boundStr += ", du10p8=" + (du10p8 - p);
      long ax13 = NthPrimeUpperBounds.Axler2013(n);
      boundStr += ", ax13=" + (ax13 - p);

      LOG.info(boundStr);
    }

    // Verify individual estimates for all p
    long combi = NthPrimeUpperBounds.combinedUpperBound(p);
    if (combi - n < 0) {
      LOG.error("combi failed at p_" + n + " = " + p + ": diff = " + (combi - n));
    }
  }

  public static void main(String[] args) {
    ConfigUtil.initProject();
    NthPrimeUpperBoundsTest test = new NthPrimeUpperBoundsTest();
    test.run(100000000000L);
  }
}
