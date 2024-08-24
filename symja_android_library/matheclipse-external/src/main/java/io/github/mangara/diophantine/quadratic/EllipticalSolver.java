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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import io.github.mangara.diophantine.Utils;
import io.github.mangara.diophantine.XYPair;

/**
 * Solves elliptical quadratic Diophantine equations in two variables.
 * <p>
 * The method is based on K. R. Matthews, "Solving the Diophantine equation \(ax^2 + bxy + cy^2 + dx
 * + ey + f = 0\)", http://www.numbertheory.org/PDFS/general_quadratic_solution.pdf
 */
public class EllipticalSolver {

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + d x + e y + f = 0, given that
   * {@literal D = b^2 - 4ac < 0} and not a perfect square.
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
   * {@literal D = b^2 - 4ac < 0} and not a perfect square.
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
    if (d.signum() == 0 && e.signum() == 0) {
      return RestrictedEllipticalSolver.solve(a, b, c, f);
    }

    // Use Legendre's transform to a X^2 + b XY + c Y^2 = k
    BigInteger D = Utils.discriminant(a, b, c);
    BigInteger alpha = Utils.legendreAlpha(b, c, d, e);
    BigInteger beta = Utils.legendreBeta(a, b, d, e);
    BigInteger k = Utils.legendreConstant(a, b, c, d, e, f, D);

    List<XYPair> transformedSolutions =
        RestrictedEllipticalSolver.getAllSolutions(a, b, c, k.negate());

    // For each solution (X, Y), if D|(X + alpha) and D|(Y + beta)
    // then ((X + alpha)/D, (Y + beta)/D) is a solution
    List<XYPair> solutions = transformedSolutions.stream()
        .filter(sol -> sol.x.add(alpha).mod(D.abs()).signum() == 0
            && sol.y.add(beta).mod(D.abs()).signum() == 0)
        .map(sol -> new XYPair(sol.x.add(alpha).divide(D), sol.y.add(beta).divide(D)))
        .collect(Collectors.toList());

    return solutions.iterator();
  }
}
