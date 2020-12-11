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
package de.tilman_neumann.jml.combinatorics;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.BigIntGrid;
import de.tilman_neumann.util.ConfigUtil;

/**
 * Implementations of the falling factorial (n)_k = (n-k+1)*...*n.
 *
 * <p>In combinatorics this can be interpreted as the number of variations of (n-k) and k
 * indistinguishable objects of two different kinds.
 *
 * <p>Note that the coefficients of the expanded polynomial are (signed) Stirling numbers of the
 * first kind. E.g. (n)_5 = (n-4)*(n-3)*(n-2)*(n-1)*n = 24n - 50n^2 + 35n^3 - 10n^4 + n^5 = s(5,1)n
 * + s(5,2)n^2 + s(5,3)n^3 + s(5,4)n^4 + s(5,5)n^5
 *
 * @author Tilman Neumann
 */
public class FallingFactorial {

  private static final Logger LOG = Logger.getLogger(FallingFactorial.class);

  /**
   * Computes the falling factorial.
   *
   * @param n
   * @param k
   * @return falling factorial (n)_k
   * @throws IllegalArgumentException if k<0
   */
  public static BigInteger fallingFactorial(int n, int k) throws IllegalArgumentException {
    // for k<0 see https://math.stackexchange.com/questions/612631/negative-falling-factorial/612637
    if (k < 0)
      throw new IllegalArgumentException(
          "FallingFactorial("
              + n
              + ", k): Negative k are not supported yet, the result would be rational.");

    if (k == 0) return I_1; // n!/n! = 1
    if (n == 0) return I_0;
    if (n > 0) {
      // (n-k+1) * ... * n = 0 if n==0 or k>n
      if (k > n) return I_0;
      // n>0 and k<=n
      return (k << 1 < n)
          ? simpleProduct(n, k)
          : byFactorials(n, k); // XXX the turning point estimate is very crude
    }

    // treat n<0, k>0: see
    // https://math.stackexchange.com/questions/111463/is-there-a-reasonable-generalization-of-the-falling-factorial-for-real-exponents
    return simpleProduct(n, k);
  }

  /**
   * Computes the falling factorial as a fraction of factorials of non-negative arguments. Strong
   * for small k because the factorial implements Luschny's algorithm.
   *
   * @param n
   * @param k
   * @return falling factorial (n)_k
   * @throws IllegalArgumentException if n<0 or (n-k)<0
   */
  private static BigInteger byFactorials(int n, int k) throws IllegalArgumentException {
    return Factorial.factorial(n).divide(Factorial.factorial(n - k));
  }

  /**
   * Computes the falling factorial as a simple product. Works for n<0, k>=0, too.
   *
   * @param n
   * @param k
   * @return falling factorial (n)_k
   */
  private static BigInteger simpleProduct(int n, int k) {
    BigInteger element = BigInteger.valueOf(n - k + 1);
    BigInteger result = I_1;
    for (int i = 0; i < k; i++) {
      result = result.multiply(element);
      element = element.add(I_1);
    }
    return result;
  }

  /**
   * Test
   *
   * @param args ignored
   */
  private static void testSmall() {
    int max = 10;
    BigIntGrid grid =
        new BigIntGrid("n", -max, "k", 0); // negative k not supported, results would be rational
    for (int n = -max; n <= max; n++) {
      ArrayList<BigInteger> row = new ArrayList<>();
      for (int k = 0; k <= max; k++) {
        row.add(fallingFactorial(n, k));
      }
      grid.add(row);
    }
    LOG.info("fallingFactorials:\n" + grid);
  }

  private static void testPerformance() {
    int limit = 1000;

    long t0, t1;
    t0 = System.currentTimeMillis();
    for (int n = 0; n < limit; n++) {
      for (int k = 0; k <= n; k++) {
        fallingFactorial(n, k);
      }
    }
    t1 = System.currentTimeMillis();
    LOG.info("fallingFactorial() took " + (t1 - t0) + "ms");

    t0 = System.currentTimeMillis();
    for (int n = 0; n < limit; n++) {
      for (int k = 0; k <= n; k++) {
        byFactorials(n, k);
      }
    }
    t1 = System.currentTimeMillis();
    LOG.info("byFactorials() took " + (t1 - t0) + "ms");

    t0 = System.currentTimeMillis();
    for (int n = 0; n < limit; n++) {
      for (int k = 0; k <= n; k++) {
        simpleProduct(n, k);
      }
    }
    t1 = System.currentTimeMillis();
    LOG.info("simpleProduct() took " + (t1 - t0) + "ms");
  }

  /**
   * Test
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();
    testSmall();
    testPerformance();
  }
}
