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
import io.github.mangara.diophantine.Utils;
import io.github.mangara.diophantine.XYPair;
import io.github.mangara.diophantine.iterators.MergedIterator;
import io.github.mangara.diophantine.utils.Divisors;

/**
 * Solves quadratic Diophantine equations in two variables, where the discriminant is a perfect
 * square.
 * <p>
 * The method is based on K. R. Matthews, "Solving the Diophantine equation \(ax^2 + bxy + cy^2 + dx
 * + ey + f = 0\)", http://www.numbertheory.org/PDFS/general_quadratic_solution.pdf
 */
public class SquareDiscriminantSolver {

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + d x + e y + f = 0, given that
   * D = b^2 - 4ac = g^2, for g > 0.
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
   * D = b^2 - 4ac = g^2, for g > 0.
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
      return solveZeroA(a, b, c, d, e, f);
    } else {
      return solveNonZeroA(a, b, c, d, e, f);
    }
  }

  // Pre: D = b^2 - 4ac = g^2, g > 0 && a == 0
  private static Iterator<XYPair> solveZeroA(BigInteger a, BigInteger b, BigInteger c, BigInteger d,
      BigInteger e, BigInteger f) {
    // Legendre's transformation with Dx = X + alpha and Dy = Y + beta gives
    // aX^2 + bXY + cY^2 = k
    // With a = 0, this reduces to
    // Y(bX + cY) = k

    BigInteger D = Utils.discriminant(a, b, c);
    BigInteger alpha = Utils.legendreAlpha(b, c, d, e);
    BigInteger beta = Utils.legendreBeta(a, b, d, e);
    BigInteger k = Utils.legendreConstant(a, b, c, d, e, f, D);
    BigInteger h = b.gcd(c);

    if (k.signum() == 0) {
      // Y(bX + cY) = 0, so
      // Y = 0 or bX + cY = 0

      // Dy - beta = 0 => Dy = beta
      Iterator<XYPair> yZero = LinearSolver.solve(BigInteger.ZERO, D, beta.negate());

      // b(Dx - alpha) + c(Dy - beta) = 0 => bDx + cDy = b alpha + c beta
      BigInteger t = b.multiply(alpha).add(c.multiply(beta));
      Iterator<XYPair> yNonZero = LinearSolver.solve(b.multiply(D), c.multiply(D), t.negate());

      return MergedIterator.merge(yZero, yNonZero);
    } else {
      // Y((b/h)X + (c/h)Y) = k/h
      // We need to examine all divisors d_i of k/h and solve the system
      // Y = d_i and (b/h)X + (c/h)Y = k/(h d_i)

      BigInteger b2 = b.divide(h);
      BigInteger c2 = c.divide(h);
      BigInteger k2 = k.divide(h);

      List<XYPair> solutions = new ArrayList<>();
      List<Long> divisors = Divisors.getPositiveAndNegativeDivisors(k2.longValueExact());

      for (Long divisor : divisors) {
        BigInteger Y = BigInteger.valueOf(divisor);
        BigInteger Xtop = k2.divide(BigInteger.valueOf(divisor)).subtract(c2.multiply(Y));

        if (Xtop.mod(b2.abs()).signum() == 0) {
          BigInteger X = Xtop.divide(b2);

          BigInteger xD = X.add(alpha);
          BigInteger yD = Y.add(beta);

          if (xD.mod(D).signum() == 0 && yD.mod(D).signum() == 0) {
            solutions.add(new XYPair(xD.divide(D), yD.divide(D)));
          }
        }
      }

      return solutions.iterator();
    }
  }

  // Pre: D = b^2 - 4ac = g^2, g > 0 && a != 0
  private static Iterator<XYPair> solveNonZeroA(BigInteger a, BigInteger b, BigInteger c,
      BigInteger d, BigInteger e, BigInteger f) {
    // Legendre's transformation with Dx = X + alpha and Dy = Y + beta gives
    // aX^2 + bXY + cY^2 = k
    // After multiplying by 4a, we can rewrite this as
    // (2aX + (b + g)Y)(2aX + (b - g)Y) = 4ak

    BigInteger D = Utils.discriminant(a, b, c);
    BigInteger g = D.sqrt();
    BigInteger k = Utils.legendreConstant(a, b, c, d, e, f, D);
    BigInteger fourAK = BigInteger.valueOf(4).multiply(a).multiply(k);

    BigInteger g1 = BigInteger.TWO.multiply(a).gcd(b.add(g));
    BigInteger g2 = BigInteger.TWO.multiply(a).gcd(b.subtract(g));
    BigInteger g1g2 = g1.multiply(g2);

    BigInteger alpha = Utils.legendreAlpha(b, c, d, e);
    BigInteger beta = Utils.legendreBeta(a, b, d, e);

    // This reduces to
    // ((2a / g1)X + ((b + g) / g1)Y)((2a / g2)X + ((b - g) / g2)Y) = 4ak / g1g2
    if (k.signum() == 0) {
      // If k = 0 we can solve both parts separately
      // 2aX + (b + g)Y = 0 or 2aX + (b - g)Y = 0
      // Subsituting X = Dx - alpha and Y = Dy - beta gives
      // 2aDx + (b + g)Dy = 2a alpha + (b + g) beta or
      // 2aDx + (b - g)Dy = 2a alpha + (b - g) beta

      BigInteger Dg1 = D.multiply(g1);
      BigInteger right1 = BigInteger.TWO.multiply(a).multiply(alpha).add(b.add(g).multiply(beta));

      Iterator<XYPair> eq1 = Collections.emptyIterator();
      if (right1.mod(Dg1.abs()).signum() == 0) {
        eq1 = LinearSolver.solve(BigInteger.TWO.multiply(a).divide(g1), b.add(g).divide(g1),
            right1.negate().divide(Dg1));
      }

      BigInteger Dg2 = D.multiply(g2);
      BigInteger right2 =
          BigInteger.TWO.multiply(a).multiply(alpha).add(b.subtract(g).multiply(beta));

      Iterator<XYPair> eq2 = Collections.emptyIterator();
      if (right2.mod(Dg2.abs()).signum() == 0) {
        eq2 = LinearSolver.solve(BigInteger.TWO.multiply(a).divide(g2), b.subtract(g).divide(g2),
            right2.negate().divide(Dg2));
      }

      return MergedIterator.merge(eq1, eq2);
    } else {
      // We have T1 T2 = 4ak / g1g2, with both T1 and T2 integer
      // so we can test each divisor d_i (positive and negative) of 4ak / g1g2
      // to see if we can solve the system T1 = d_i, T2 = 4ak / g1g2d_i

      List<XYPair> solutions = new ArrayList<>();
      List<Long> divisors =
          Divisors.getPositiveAndNegativeDivisors(fourAK.divide(g1g2).longValueExact());

      for (Long divisor : divisors) {
        // We need to solve
        // (1) (2a / g1)X + ((b + g) / g1)Y = divisor
        // (2) (2a / g2)X + ((b - g) / g2)Y = 4ak / (g1g2 divisor)
        // Adding (g2 / g1) times (2) to (1) gives
        // (2g / g1) Y = divisor - 4ak / (g1^2 divisor)
        // Y = ((g1 divisor)^2 - 4ak) / (2g g1 divisor)
        BigInteger div = BigInteger.valueOf(divisor);

        BigInteger Ytop = g1.multiply(g1).multiply(div.multiply(div)).subtract(fourAK);
        BigInteger Ybottom = BigInteger.TWO.multiply(g).multiply(g1).multiply(div);

        if (Ytop.mod(Ybottom.abs()).signum() == 0) {
          BigInteger Y = Ytop.divide(Ybottom);

          // Subsituting into (2a / g1)X + ((b + g) / g1)Y = divisor gives
          // X = (g1 divisor - (b + g) Y) / 2a
          BigInteger Xtop = g1.multiply(div).subtract(b.add(g).multiply(Y));
          BigInteger Xbottom = BigInteger.TWO.multiply(a);

          if (Xtop.mod(Xbottom.abs()).signum() == 0) {
            BigInteger X = Xtop.divide(Xbottom);

            BigInteger xD = X.add(alpha);
            BigInteger yD = Y.add(beta);

            if (xD.mod(D).signum() == 0 && yD.mod(D).signum() == 0) {
              solutions.add(new XYPair(xD.divide(D), yD.divide(D)));
            }
          }
        }
      }

      return solutions.iterator();
    }
  }

}
