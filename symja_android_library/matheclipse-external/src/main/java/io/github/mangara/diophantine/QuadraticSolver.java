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
package io.github.mangara.diophantine;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import io.github.mangara.diophantine.quadratic.EllipticalSolver;
import io.github.mangara.diophantine.quadratic.HyperbolicSolver;
import io.github.mangara.diophantine.quadratic.ParabolicSolver;
import io.github.mangara.diophantine.quadratic.SquareDiscriminantSolver;

/**
 * Solves quadratic Diophantine equations in two variables.
 * <p>
 * The method is based on K. R. Matthews, "Solving the Diophantine equation \(ax^2 + bxy + cy^2 + dx
 * + ey + f = 0\)" http://www.numbertheory.org/PDFS/general_quadratic_solution.pdf
 */
public class QuadraticSolver {

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + d x + e y + f = 0.
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
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + d x + e y + f = 0.
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
    if (a.signum() == 0 && b.signum() == 0 && c.signum() == 0) {
      return LinearSolver.solve(d, e, f);
    }

    BigInteger D = Utils.discriminant(a, b, c);

    if (D.signum() == 0) {
      return ParabolicSolver.solve(a, b, c, d, e, f);
    } else if (Utils.isSquare(D)) {
      return SquareDiscriminantSolver.solve(a, b, c, d, e, f);
    } else if (Utils.legendreConstant(a, b, c, d, e, f, D).signum() == 0) {
      return solveTrivialCase(a, b, c, d, e, f, D);
    } else if (D.signum() < 0) {
      return EllipticalSolver.solve(a, b, c, d, e, f);
    } else {
      return HyperbolicSolver.solve(a, b, c, d, e, f);
    }
  }

  // Pre: D = b^2 - 4ac != 0 && D not a perfect square && Legendre's k = 0
  private static Iterator<XYPair> solveTrivialCase(BigInteger a, BigInteger b, BigInteger c,
      BigInteger d, BigInteger e, BigInteger f, BigInteger D) {
    BigInteger alpha = Utils.legendreAlpha(b, c, d, e);
    BigInteger beta = Utils.legendreBeta(a, b, d, e);

    if (alpha.mod(D).signum() == 0 && beta.mod(D).signum() == 0) {
      return Collections.singletonList(new XYPair(alpha.divide(D), beta.divide(D))).iterator();
    } else {
      return Collections.emptyIterator();
    }
  }
}
