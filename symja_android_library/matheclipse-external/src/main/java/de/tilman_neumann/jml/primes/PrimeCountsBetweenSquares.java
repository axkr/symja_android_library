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
package de.tilman_neumann.jml.primes;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.primes.exact.SegmentedSieve;
import de.tilman_neumann.jml.primes.exact.SieveCallback;
import de.tilman_neumann.util.ConfigUtil;

/**
 * Find #primes between consecutive squares. Gives OEIS sequences A014085.
 *
 * @author Tilman Neumann
 */
public class PrimeCountsBetweenSquares implements SieveCallback {
  private static final Logger LOG = Logger.getLogger(PrimeCountsBetweenSquares.class);

  private SegmentedSieve sieve;
  private long limit;
  private int count = 0;

  /** base of lower square */
  private long b0, b1;
  /** upper square */
  private long s1;

  public PrimeCountsBetweenSquares(long limit) {
    sieve = new SegmentedSieve(this);
    this.limit = limit;
  }

  public void run() {
    b0 = 1;
    b1 = 2;
    s1 = 4;
    count = 0;
    sieve.sieve(limit);
  }

  @Override
  public void processPrime(long prime) {
    if (prime < s1) {
      // LOG.debug("prime = " + prime);
      count++;
    } else {
      // overflow of current range (s0, s1)
      LOG.info("#primes between " + b0 + "^2 and " + b1 + "^2 = " + count);
      b0 = b1;
      b1++;
      s1 = b1 * b1;
      // LOG.debug("prime = " + prime);
      count = 1;
    }
  }

  public static void main(String[] args) {
    ConfigUtil.initProject();
    new PrimeCountsBetweenSquares(100000000000L).run();
    // result: A014085 = 2, 2, 2, 3, 2, 4, 3, 4, 3, 5, 4, 5, 5, 4, 6, 7, 5, 6, 6, 7, 7, 7, ...
    // In that entry we also see that a stronger conjecture exists:
    // There is at least one prime in n^k...n^(k+1) with k = log(127)/log(16) ~ 1.747171172
  }
}
