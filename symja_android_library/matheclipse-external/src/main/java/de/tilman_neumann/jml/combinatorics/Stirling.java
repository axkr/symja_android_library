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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.BigIntCollectionUtil;
import de.tilman_neumann.jml.base.BigIntPoly;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.Pair;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Computation of Stirling numbers.
 *
 * @author Tilman Neumann
 */
public class Stirling {
  private static final Logger LOG = Logger.getLogger(Stirling.class);

  /** Cache for fast implementation with memory */
  private static BigInteger[][] stirling1Arr = new BigInteger[][] {new BigInteger[] {I_1}};

  /**
   * Object used to synchronize access to the static Stirling numbers array. We need to call a
   * constructor, with valueof() we would block one of the standard values!
   */
  private static Object syncObject = new Object();

  /** hashtable for of 1.kind Stirling numbers indexed by (n,k) */
  private static HashMap<Pair<Integer, Integer>, BigInteger> s1Map =
      new HashMap<Pair<Integer, Integer>, BigInteger>();

  /**
   * (Signed) Stirling numbers of the first kind.
   *
   * @param n Upper parameter
   * @param k Lower parameter
   * @return s(n,k)
   */
  public static BigInteger stirling1(int n, int k) {
    if (n < 500) { // avoid spending too much memory
      return stirling1WithMemory(n, k);
    }
    // slower but less memory-expensive
    return stirling1byGF(n, k);
  }

  /**
   * Absolute Stirling numbers of the first kind.
   *
   * @param n Upper parameter
   * @param k Lower parameter
   * @return |s(n,k)|
   */
  public static BigInteger absStirling1(int n, int k) {
    return stirling1(n, k).abs();
  }

  /**
   * Slow recursive calculation of the signed Stirling numbers of 1. kind. These correspond to the
   * number of permutations of a set of n symbols with exactly k permutation cycles.
   */
  private static BigInteger stirling1Recurrent(int n, int k) {
    if (n > k && k > 0) {
      // recursion:
      return stirling1Recurrent(n - 1, k - 1)
          .subtract(stirling1Recurrent(n - 1, k).multiply(BigInteger.valueOf(n - 1)));
    }
    if (n == k) {
      return I_1;
    }
    return I_0;
  }

  /**
   * Evaluate s(n,k) by using the finite sum formula relating it to the Stirling numbers of the
   * second kind. This is faster than the recurrence, but slower than the g.f. approach.
   *
   * @param n the first argument to the Stirling function.
   * @param k the second argument to the Stirling function.
   * @return s(n,k)
   * @see <a
   *     href="http://en.wikipedia.org/wiki/Stirling_number">http://en.wikipedia.org/wiki/Stirling_number</a>
   */
  private static BigInteger stirling1byStirling2(int n, int k) {
    if (n > k && k > 0) {
      BigInteger result = I_0;
      for (int j = 0; j <= n - k; j++) {
        BigInteger elem =
            Binomial.binomial((n - 1 + j), (n - k + j))
                .multiply(Binomial.binomial((2 * n - k), (n - k - j)))
                .multiply(stirling2(n - k + j, j));
        if (j % 2 != 0) {
          elem = elem.negate();
        }
        result = result.add(elem);
      }
      if ((n - k) % 2 == 0) {
        result = result.negate();
      }
      return result;
    }
    if (n == k) {
      return I_1;
    }
    return I_0;
  }

  /**
   * Evaluate s(n,k) by building the product of the polynomials in the g.f. and extracting the
   * corresponding factor. This is faster than the recurrence formula or computation via the
   * Stirlings of the second kind, but slower than the version with memory.
   *
   * @param n the first argument to the Stirling function.
   * @param k the second argument to the Stirling function.
   * @return s(n,k)
   * @warning insufficient checks for overflow of n or m
   * @see adapted_from http://www.strw.leidenuniv.nl/~mathar/progs/FI/oeis_8java.html
   */
  private static BigInteger stirling1byGF(int n, int k) {
    if (n > k && k > 0) {
      // define the polynomial x
      BigIntPoly xprod = new BigIntPoly(n);
      BigIntPoly xn = new BigIntPoly(n);

      for (int i = 1; i < n; i++) {
        // multiply xprod by x-i; first set the absolute coefficient of xn to -i
        xn.set(0, BigInteger.valueOf(-i));
        xprod = xprod.multiply(xn);
      }
      return xprod.get(k);
    }
    if (n == k) {
      return I_1;
    }
    return I_0;
  }

  /**
   * (Signed) Stirling numbers of the first kind s(n,k). Implementation with memory is very fast,
   * but might lead to OutOfMemoryErrors for n slightly above 1000.
   *
   * @param n Upper parameter
   * @param k Lower parameter
   * @return s(n,k)
   */
  private static BigInteger stirling1WithMemory(int n, int k) {
    if (n > k && k > 0) {
      // pass by the synchronized block if the Stirling array is big enough
      if (n >= stirling1Arr.length) {
        // we need to enlarge the Stirling array, but we don't want
        // several threads to do this in parallel (and the latter undo
        // the effects of the first ones). Therefore we do this in a
        // synchronized block, and every thread checks again if the
        // array is still to small when it enters the block:
        synchronized (syncObject) {
          if (n >= stirling1Arr.length) {
            BigInteger[][] newStirlings = new BigInteger[n + 10][];
            System.arraycopy(stirling1Arr, 0, newStirlings, 0, stirling1Arr.length);
            // initialize new entries:
            for (int i = stirling1Arr.length; i < newStirlings.length; i++) {
              newStirlings[i] = new BigInteger[i + 1];
            }
            stirling1Arr = newStirlings;
          }
        }
      }

      if (stirling1Arr[n][k] == null) {
        // Here is the recursion !!
        stirling1Arr[n][k] =
            stirling1WithMemory(n - 1, k - 1)
                .subtract(stirling1WithMemory(n - 1, k).multiply(BigInteger.valueOf(n - 1)));
        // LOG.debug("|S1(" + n + ", " + k + ")| = " + stirlings1Abs[n][k]);
      }

      // Get the absolute Stirling number from the internal array:
      return stirling1Arr[n][k];
    }
    if (n == k) {
      return I_1;
    }
    // k==0, n>0:
    return I_0;
  }

  /** Fast version of the 1. kind Stirling numbers with hashed memory. OutOfMemoryError at n=849. */
  @SuppressWarnings("unused")
  private static BigInteger stirling1WithHashedMemory(int n, int k) {
    if (n > k && k > 0) {
      Pair<Integer, Integer> nkp = new Pair<Integer, Integer>(n, k);
      BigInteger retValue = s1Map.get(nkp);
      if (retValue == null) {
        // recursion:
        retValue =
            stirling1WithHashedMemory(n - 1, k - 1)
                .subtract(stirling1WithHashedMemory(n - 1, k).multiply(BigInteger.valueOf(n - 1)));
        s1Map.put(nkp, retValue);
      }
      return (retValue);
    }
    if (n == k) {
      return I_1;
    }
    return I_0;
  }

  /**
   * Stirling numbers of the second kind S(n,k).
   *
   * @see <a
   *     href="http://mathworld.wolfram.com/StirlingNumberoftheSecondKind.html">http://mathworld.wolfram.com/StirlingNumberoftheSecondKind.html</a>
   * @param n
   * @param k
   * @return BigInt
   */
  public static BigInteger stirling2(int n, int k) {
    BigInteger ret = I_0;
    if (k > 0) {
      for (int i = 0; i <= n; i++) {
        BigInteger elem = Binomial.binomial(k, i).multiply(BigInteger.valueOf(k - i).pow(n));
        if (i % 2 != 0) {
          elem = elem.negate();
        }
        ret = ret.add(elem);
      }
      return ret.divide(Factorial.factorial(k));
    }
    if (k == 0) {
      return I_0;
    }
    throw new IllegalArgumentException("Parameter k must be non-negative, but is " + k);
  }

  /**
   * Compute r-Stirling numbers of the first kind s(n,k,r)
   *
   * @param n Upper parameter
   * @param k Lower parameter
   * @param r r-value
   * @return s(n,k,r)
   */
  public static BigInteger rStirling1(int n, int k, int r) {
    if (n > r) {
      return rStirling1(n - 1, k - 1, r)
          .add(rStirling1(n - 1, k, r).multiply(BigInteger.valueOf(n - 1)));
    }
    if (n == r && k == r) {
      return I_1;
    }
    return I_0;
  }

  /**
   * Calculates the diagonal of Stirling numbers of the first kind S1(n-k+1,1), S1(n-k+2,2), ...,
   * S1(n-1,k-1), S1(n,k).
   *
   * @param n
   * @param k
   * @return an array containing the Stirling numbers of the diagonal
   */
  public static BigInteger[] stirling1Diag(int n, int k) {
    // prepare initial diagonal S1(1,1), ... S1(k,k):
    BigInteger[] diag = new BigInteger[k];
    for (int i = 1; i <= k; i++) diag[i - 1] = I_1;

    // progression until n:
    BigInteger[] nextDiag;
    for (int n1 = 1; n1 <= n - k; n1++) {
      nextDiag = nextStirling1Diag(diag, n1, k);
      diag = nextDiag;
    }

    return diag;
  }

  public static BigInteger[] nextStirling1Diag(BigInteger[] lastDiag, int n1, int k) {
    BigInteger[] nextDiag = new BigInteger[k];
    nextDiag[0] = lastDiag[0].multiply(BigInteger.valueOf(n1));

    for (int i = 2; i <= k; i++) {
      nextDiag[i - 1] = lastDiag[i - 1].multiply(BigInteger.valueOf(n1 + i - 1));
      nextDiag[i - 1] = nextDiag[i - 1].add(nextDiag[i - 2]);
    }
    return nextDiag;
  }

  @SuppressWarnings("unused")
  private static void testDiagonal(String[] args) {
    int nofArgs = args.length;
    if (nofArgs == 3) {
      // get parameters from command line
      int a1 = Integer.parseInt(args[0]);
      int a2 = Integer.parseInt(args[1]);
      int b = Integer.parseInt(args[2]);

      // Calculate my Stirling number sum [20.1.03]:
      BigInteger num, den, powerTerm, divMod[], gcd;
      double sum = 0.0, fraction;
      int n = a1, dn = a1;
      int i;

      // create initial diagonal of Stirling numbers:
      BigInteger[] diag = new BigInteger[a1];
      for (i = 1; i <= a1; i++) diag[i - 1] = I_1;

      // create initial n!
      BigInteger fac = I_1;
      if (a1 > 1) {
        for (i = 1; i <= a1; i++) {
          fac = fac.multiply(BigInteger.valueOf(i));
        }
      }

      while (true) {
        // calculate numerator:
        num = diag[a1 - 1];

        // calculate denominator:
        powerTerm = BigInteger.valueOf(n + b).pow(a2 + 1);
        den = fac.multiply(powerTerm);

        // simplify num/den:
        gcd = num.gcd(den);
        num = num.divide(gcd);
        den = den.divide(gcd);

        // update sum: this might be a pithole, because there is no
        // method that converts the fraction directly into a double !?
        divMod = num.divideAndRemainder(den);
        fraction = divMod[1].doubleValue() / den.doubleValue();
        sum = sum + divMod[0].doubleValue() + fraction;

        // report at each double number of cycles
        if (n == dn) {
          LOG.debug("Sum after " + n + " iterations: " + sum);
          dn = dn * 2;
        }

        // prepare next round
        n = n + 1;
        diag = nextStirling1Diag(diag, n - a1, a1);
        fac = fac.multiply(BigInteger.valueOf(n));
      }
    } else if (nofArgs == 4) {
      // CALCULATION OF THE (a1,a2).TH DERIVATIVE OF B(b1,b2) !! [13.2.03]
      // get commandline arguments
      int a1 = Integer.parseInt(args[0]);
      int a2 = Integer.parseInt(args[1]);
      int b1 = Integer.parseInt(args[2]);
      int b2 = Integer.parseInt(args[3]);

      // other initializations
      BigInteger num, den, gcd, divMod[];
      double sum = 0.0, isum, fraction, bi, f;
      int i1 = a1, dn = a1, i2, i;

      // create initial diagonal of Stirling numbers:
      BigInteger[] diag = new BigInteger[a1];
      for (i = 1; i <= a1; i++) diag[i - 1] = I_1;

      // create initial i1!
      BigInteger fac = Factorial.factorial(a1);

      // create the multiplicator in front of the sum:
      BigInteger mu = fac.multiply(Factorial.factorial(a2));

      // add sign:
      if ((a1 + a2) % 2 != 0) mu = mu.negate();

      // outer loop
      while (true) {
        // calculate numerator and denominator and simplify them:
        num = mu.multiply(diag[a1 - 1]);
        den = fac;
        // LOG.debug("n=" + i1 + ": numerator=" + num + ", ");
        // LOG.debug("denominator=" + den);
        gcd = num.gcd(den);
        num = num.divide(gcd);
        den = den.divide(gcd);

        // inner loop
        isum = 0.0;
        bi = 1.0;
        for (i2 = 0; i2 < b1; i2++) {
          if (i2 > 0) {
            // calculate binomial coefficient recursively
            bi = bi * (b1 - i2) / i2;
            // LOG.debug("binomial("+ (b1-1) + "," + i2 + ")=" + bi);
          }
          f = Math.pow(-1, i2) * bi / Math.pow(i1 + i2 + b2, a2 + 1);
          isum = isum + f;
        }
        // LOG.debug("inner sum for i1=" + i1 + ": " + isum);
        // update outer sum: this might be a pithole, because there is
        // no method that converts the fraction directly into a double ?
        divMod = num.divideAndRemainder(den);
        fraction = divMod[1].doubleValue() / den.doubleValue();
        sum = sum + (divMod[0].doubleValue() + fraction) * isum;

        // report at each double number of cycles
        if (i1 == dn) {
          LOG.debug("Sum after " + i1 + " iterations: " + sum);
          dn = dn * 2;
        }

        // prepare next outer loop
        i1 = i1 + 1;
        diag = nextStirling1Diag(diag, i1 - a1, a1);
        fac = fac.multiply(BigInteger.valueOf(i1));
      } // end(outer loop)
    } else { // wrong number of arguments
      LOG.error("usage: Stirling <a1> <a2> <b1> [<b2>]");
    }
  }

  @SuppressWarnings("unused")
  private static void printFirstStirlings() {
    for (int n = 0; n < 12; n++) {
      ArrayList<BigInteger> stirlings1 = new ArrayList<>();
      for (int i = 1; i <= n; i++) {
        BigInteger stirling1 = stirling1WithMemory(n, i);
        stirlings1.add(stirling1);
      }
      LOG.info("1st stirling numbers (" + n + ", i) = " + stirlings1);
      LOG.info("sum of row elements = " + BigIntCollectionUtil.sum(stirlings1));
      LOG.info("sum of absolute row elements = " + BigIntCollectionUtil.absSum(stirlings1));
    }
  }

  private static void testPerformance() {
    int n = 25;
    long start;
    // this one is just too slow...
    start = System.currentTimeMillis();
    for (int i = 0; i < n; i++) {
      for (int k = 0; k <= i; k++) {
        stirling1Recurrent(i, k);
      }
    }
    LOG.info("stirling1Recurrent took " + (System.currentTimeMillis() - start) + " ms");
    LOG.info("stirling1Recurrent(11,4)=" + stirling1Recurrent(11, 4));
    LOG.info("stirling1Recurrent(8,0)=" + stirling1Recurrent(8, 0));
    LOG.info("stirling1Recurrent(0,0)=" + stirling1Recurrent(0, 0));

    // better but not too good for n>40
    start = System.currentTimeMillis();
    for (int i = 0; i < n; i++) {
      for (int k = 0; k <= i; k++) {
        stirling1byStirling2(i, k);
      }
    }
    LOG.info("stirling1byStirling2 took " + (System.currentTimeMillis() - start) + " ms");
    LOG.info("stirling1byStirling2(11,4)=" + stirling1byStirling2(11, 4));
    LOG.info("stirling1byStirling2(8,0)=" + stirling1byStirling2(8, 0));
    LOG.info("stirling1byStirling2(0,0)=" + stirling1byStirling2(0, 0));

    // best without caching !
    start = System.currentTimeMillis();
    for (int i = 0; i < n; i++) {
      for (int k = 0; k <= i; k++) {
        stirling1byGF(i, k);
      }
    }
    LOG.info("stirling1byGF took " + (System.currentTimeMillis() - start) + " ms");
    LOG.info("stirling1byGF(11,4)=" + stirling1byGF(11, 4));
    LOG.info("stirling1byGF(8,0)=" + stirling1byGF(8, 0));
    LOG.info("stirling1byGF(0,0)=" + stirling1byGF(0, 0));

    // this is the fastest solution, but runs out of memory at 1000<n<2000
    // in a VM with 512MB heap space...
    start = System.currentTimeMillis();
    for (int i = 0; i < n; i++) {
      for (int k = 0; k <= i; k++) {
        stirling1WithMemory(i, k);
      }
    }
    LOG.info("stirling1WithMemory took " + (System.currentTimeMillis() - start) + " ms");
    LOG.info("stirling1WithMemory(11,4)=" + stirling1WithMemory(11, 4));
    LOG.info("stirling1WithMemory(8,0)=" + stirling1WithMemory(8, 0));
    LOG.info("stirling1WithMemory(0,0)=" + stirling1WithMemory(0, 0));
  }

  /**
   * Tests.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();

    // printFirstStirlings();
    // testDiagonal(args);
    testPerformance();
  }
}
