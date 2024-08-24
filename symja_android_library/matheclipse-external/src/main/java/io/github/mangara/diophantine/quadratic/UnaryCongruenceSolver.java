/*
 * Copyright 2022 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.mangara.diophantine.quadratic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A solver for unary quadratic congruences a x^2 + b x + c = 0 (mod n) , where n > 1.
 */
public class UnaryCongruenceSolver {

  /**
   * Solves the quadratic congruence a x^2 + b x + c = 0 (mod n) given that n > 1.
   * 
   * @param a
   * @param b
   * @param c
   * @param n
   * @return all integer solutions {@literal 0 <= x < n} to a x^2 + b x + c = 0 (mod n)
   */
  public static List<BigInteger> solve(long a, long b, long c, long n) {
    return solve(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c),
        BigInteger.valueOf(n));
  }

  /**
   * Solves the quadratic congruence a x^2 + b x + c = 0 (mod n) given that n > 1.
   * 
   * @param a
   * @param b
   * @param c
   * @param n
   * @return all integer solutions {@literal 0 <= x < n} to a x^2 + b x + c = 0 (mod n)
   */
  public static List<BigInteger> solve(BigInteger a, BigInteger b, BigInteger c, BigInteger n) {
    if (n.signum() <= 0) {
      throw new IllegalArgumentException("n too small");
    }
    if (n.equals(BigInteger.ONE)) {
      return Collections.singletonList(BigInteger.ZERO);
    }

    if (a.signum() == 0) {
      return solveLinear(b, c, n);
    }
    if (a.signum() < 0) {
      a = a.negate();
      b = b.negate();
      c = c.negate();
    }

    if (b.signum() == 0) {
      return solveReduced(a, c, n);
    } else {
      // TODO: speed up
      return bruteForce(a, b, c, n);
    }
  }

  // Solves b x + c = 0 (mod n)
  // Pre: n > 1
  private static List<BigInteger> solveLinear(BigInteger b, BigInteger c, BigInteger n) {
    if (b.signum() == 0) {
      if (c.signum() == 0) {
        List<BigInteger> solutions = new ArrayList<>();

        for (BigInteger i = BigInteger.ZERO; i.compareTo(n) < 0; i = i.add(BigInteger.ONE)) {
          solutions.add(i);
        }

        return solutions;
      } else {
        return Collections.emptyList();
      }
    }

    // TODO: speed up
    return bruteForce(BigInteger.ZERO, b, c, n);
  }

  // Solves a x^2 + c = 0 (mod n)
  // Pre: a > 0, n > 1
  private static List<BigInteger> solveReduced(BigInteger a, BigInteger c, BigInteger n) {
    // TODO: speed up
    return bruteForce(a, BigInteger.ZERO, c, n);
  }

  private static List<BigInteger> bruteForce(BigInteger a, BigInteger b, BigInteger c,
      BigInteger n) {
    List<BigInteger> result = new ArrayList<>();

    for (BigInteger x = BigInteger.ZERO; x.compareTo(n) < 0; x = x.add(BigInteger.ONE)) {
      BigInteger calc = a.multiply(x).multiply(x).add(b.multiply(x)).add(c).mod(n);

      if (calc.signum() == 0) {
        result.add(x);
      }
    }

    return result;
  }
}
