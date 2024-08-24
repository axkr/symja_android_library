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
import java.util.Iterator;
import java.util.List;
import io.github.mangara.diophantine.LinearSolver;
import io.github.mangara.diophantine.XYPair;
import io.github.mangara.diophantine.iterators.IntegerIterator;
import io.github.mangara.diophantine.iterators.MappingIterator;
import io.github.mangara.diophantine.iterators.MergedIterator;

/**
 * A solver for quadratic Diophantine equations a
 * <code>x^2 + b xy + c y^2 + d x + e y + f = 0</code>, where <code>D =
 * b^2 - 4ac = 0</code> and not all of <code>a, b</code>, and <code>c</code> are zero.
 */
public class ParabolicSolver {

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + d x + e y + f = 0, given that
   * D = b^2 - 4ac = 0 and not all of a, b, and c are zero.
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @param e
   * @param f
   * @return an iterator over all integer solutions (x, y)
   */
  public static Iterator<XYPair> solve(long a, long b, long c, long d, long e, long f) {
    return solve(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c),
        BigInteger.valueOf(d), BigInteger.valueOf(e), BigInteger.valueOf(f));
  }

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + d x + e y + f = 0, given that
   * D = b^2 - 4ac = 0 and not all of a, b, and c are zero.
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @param e
   * @param f
   * @return an iterator over all integer solutions (x, y)
   */
  public static Iterator<XYPair> solve(BigInteger a, BigInteger b, BigInteger c, BigInteger d,
      BigInteger e, BigInteger f) {
    if (a.signum() == 0) {
      // b^2 = 4ac = 0 implies both a and b are 0.
      // Since not all of a, b, and c are zero, c is non-zero, so we swap x and y
      Iterator<XYPair> yxSolutions = solveNonZeroA(c, b, a, e, d, f);
      return new MappingIterator<>(yxSolutions, (sol) -> new XYPair(sol.y, sol.x));
    } else {
      return solveNonZeroA(a, b, c, d, e, f);
    }
  }

  // Pre: D = 0, a != 0
  private static Iterator<XYPair> solveNonZeroA(BigInteger a, BigInteger b, BigInteger c,
      BigInteger d, BigInteger e, BigInteger f) {
    // Multiply by 4a to get
    // (2ax + by)^2 + 4adx + 4aey + 4af = 0
    // Substitute t = 2ax + by, u = 2(bd - 2ae), and v = d^2 - 4af to get
    // (t + d)^2 = uy + v

    BigInteger u = computeU(a, b, d, e);

    if (u.signum() == 0) {
      return solveSimple(a, b, c, d, e, f);
    } else {
      return solveGeneral(a, b, c, d, e, f);
    }
  }

  // Pre: D = 0, a != 0, u = 2(bd - 2ae) = 0
  private static Iterator<XYPair> solveSimple(BigInteger a, BigInteger b, BigInteger c,
      BigInteger d, BigInteger e, BigInteger f) {
    // With u = 0, we're now solving
    // (t + d)^2 = v

    BigInteger v = computeV(a, d, f);

    if (v.signum() == 0) {
      // (t + d)^2 = 0 => t + d = 0 => 2ax + by + d = 0
      return LinearSolver.solve(BigInteger.TWO.multiply(a), b, d);
    }

    if (v.signum() < 0) {
      // (t + d)^2 = v has no solutions for v < 0
      return Collections.emptyIterator();
    }

    BigInteger[] sqrtV = v.sqrtAndRemainder();
    if (sqrtV[1].signum() != 0) {
      // (t + d)^2 = v has no solutions for v not a perfect square
      return Collections.emptyIterator();
    }

    BigInteger g = sqrtV[0];

    // (t + d)^2 = g^2 => t + d = +/- g => 2ax + by + d +/- g = 0
    Iterator<XYPair> negativeG = LinearSolver.solve(BigInteger.TWO.multiply(a), b, d.subtract(g));
    Iterator<XYPair> positiveG = LinearSolver.solve(BigInteger.TWO.multiply(a), b, d.add(g));

    return MergedIterator.merge(negativeG, positiveG);
  }

  // Pre: D = 0, a != 0, u = 2(bd - 2ae) != 0
  private static Iterator<XYPair> solveGeneral(BigInteger a, BigInteger b, BigInteger c,
      BigInteger d, BigInteger e, BigInteger f) {
    // With u != 0, we're still solving
    // (t + d)^2 = uy + v
    // To do that, we first solve
    // (t + d)^2 = v (mod |u|)

    BigInteger u = computeU(a, b, d, e);
    BigInteger v = computeV(a, d, f);

    List<BigInteger> SqrtVModU =
        UnaryCongruenceSolver.solve(BigInteger.ONE, BigInteger.ZERO, v.negate(), u.abs());

    if (SqrtVModU.isEmpty()) {
      return Collections.emptyIterator();
    }

    List<Iterator<XYPair>> familyIterators = new ArrayList<>();

    for (BigInteger Ti : SqrtVModU) {
      // t + d = T_i + uk satisfies (t + d)^2 = v (mod |u|) for every integer k
      // Substituting t + d = Ti + uk into (t + d)^2 = uy + v and solving for y yields
      // y = (Ti^2 - v) / u + 2Tik + uk^2 = r + sk + uk^2
      // where r = (Ti^2 - v) / u and s = 2Ti

      BigInteger r = Ti.multiply(Ti).subtract(v).divide(u); // r = (Ti^2 - v) / u
      BigInteger s = BigInteger.TWO.multiply(Ti); // s = 2Ti

      // Substituting y = r + sk + uk^2 into 2ax + by = t = T_i - d + uk yields
      // 2ax = Ti - d - br + (u - bs)k - buk^2
      // x = (Ti - d - br) / 2a + ((u - bs) / 2a)k - (bu / 2a)k^2
      // If all three coefficients are integers, this yields an x and y for each k

      BigInteger c1 = Ti.subtract(d).subtract(b.multiply(r));
      BigInteger c2 = u.subtract(b.multiply(s));
      BigInteger c3 = b.multiply(u).negate();

      BigInteger doubleA = BigInteger.TWO.multiply(a);

      if (c1.mod(doubleA.abs()).signum() == 0 && c2.mod(doubleA.abs()).signum() == 0
          && c3.mod(doubleA.abs()).signum() == 0) {
        BigInteger c1by2a = c1.divide(doubleA);
        BigInteger c2by2a = c2.divide(doubleA);
        BigInteger c3by2a = c3.divide(doubleA);

        familyIterators.add(new MappingIterator<>(new IntegerIterator(),
            (k) -> new XYPair(c1by2a.add(c2by2a.multiply(k)).add(c3by2a.multiply(k).multiply(k)),
                r.add(s.multiply(k)).add(u.multiply(k).multiply(k)))));
      }
    }

    return MergedIterator.merge(familyIterators);
  }

  private static BigInteger computeU(BigInteger a, BigInteger b, BigInteger d, BigInteger e) {
    // u = 2(bd - 2ae)
    return BigInteger.TWO.multiply(b.multiply(d).subtract(BigInteger.TWO.multiply(a).multiply(e)));
  }

  private static BigInteger computeV(BigInteger a, BigInteger d, BigInteger f) {
    // v = d^2 - 4af
    return d.multiply(d).subtract(BigInteger.valueOf(4).multiply(a).multiply(f));
  }
}
