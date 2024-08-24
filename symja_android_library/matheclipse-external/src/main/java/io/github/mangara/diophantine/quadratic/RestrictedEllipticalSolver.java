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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import io.github.mangara.diophantine.Utils;
import io.github.mangara.diophantine.XYPair;
import io.github.mangara.diophantine.utils.ContinuedFraction;
import io.github.mangara.diophantine.utils.Divisors;

/**
 * Solves some elliptical quadratic Diophantine equations in two variables.
 * <p>
 * The method is based on K. R. Matthews, "Lagrange's algorithm revisited: solving \(at^2 + btu +
 * cu^2 = n\) in the case of negative discriminant", Journal of Integer Sequences, Vol. 17 (2014),
 * Article 14.11.1, https://cs.uwaterloo.ca/journals/JIS/VOL17/Matthews/matt10.html
 */
public class RestrictedEllipticalSolver {

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + f = 0, given that
   * {@literal D = b^2 - 4ac < 0} and not a perfect square.
   *
   * @param a
   * @param b
   * @param c
   * @param f
   * @return an iterator over all integer solutions (x, y)
   */
  public static Iterator<XYPair> solve(long a, long b, long c, long f) {
    return solve(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c),
        BigInteger.valueOf(f));
  }

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + f = 0, given that
   * {@literal D = b^2 - 4ac < 0} and not a perfect square.
   *
   * @param a
   * @param b
   * @param c
   * @param f
   * @return an iterator over all integer solutions (x, y)
   */
  public static Iterator<XYPair> solve(BigInteger a, BigInteger b, BigInteger c, BigInteger f) {
    return getAllSolutions(a, b, c, f).iterator();
  }

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + f = 0, given that
   * {@literal D = b^2 - 4ac < 0} and not a perfect square.
   *
   * @param a
   * @param b
   * @param c
   * @param f
   * @return a list of all integer solutions (x, y)
   */
  public static List<XYPair> getAllSolutions(BigInteger a, BigInteger b, BigInteger c,
      BigInteger f) {
    if (f.signum() == 0) {
      // We can't get here from QuadraticSolver, as it ends up
      // in the trivial case, but I included it for completeness.

      // Solving for x with the quadratic formula gives
      // D' = Dy^2 - 4af = Dy^2
      // If y != 0, Dy^2 < 0, so there are no solutions.
      // y = 0 gives ax^2 = 0 => x = 0
      return Collections.singletonList(new XYPair(0, 0));
    }

    // We know: b^2 - 4ac < 0, so ac > 0, which means
    // a != 0, c != 0 and they have the same sign

    if (a.signum() < 0) {
      // Negate everything
      a = a.negate();
      b = b.negate();
      c = c.negate();
      f = f.negate();
    }
    // Now a > 0

    if (f.signum() > 0) {
      // Solving for x with the quadratic formula gives
      // D' = Dy^2 - 4af
      // As D < 0 and y^2 >= 0, Dy^2 <= 0.
      // Thus there are no solutions if 4af > 0.
      return Collections.emptyList();
    }

    return solveSignCorrected(a, b, c, f);
  }

  // Pre: a > 0, f < 0, D = b^2 - 4ac < 0 and not a perfect square
  private static List<XYPair> solveSignCorrected(BigInteger a, BigInteger b, BigInteger c,
      BigInteger f) {
    RestrictedEquation eq = new RestrictedEquation(a, b, c, f).withoutCommonDivisor();

    if (eq == RestrictedEquation.NO_SOLUTIONS) {
      return Collections.emptyList();
    }

    if (eq.a.gcd(eq.f).equals(BigInteger.ONE)) {
      return solveReduced(eq);
    }

    Reduction reduction = Reduction.forEquation(eq);
    List<XYPair> reducedSolutions = solveReduced(reduction.reduce(eq));
    return reduction.unreduce(reducedSolutions);
  }

  // Pre: a > 0, n > 0, gcd(a, n) = 1, D = b^2 - 4ac < 0 and not a perfect square
  private static List<XYPair> solveReduced(RestrictedEquation eq) {
    long n = eq.f.negate().longValueExact();
    // If a x^2 + b xy + c y^2 = n with gcd(x, y) = h, n must be divisible by h^2.
    // So to find all such (x, y), we can solve a X^2 + b XY + c Y^2 + n/h^2 = 0 for relatively
    // prime (X, Y).
    // We then obtain (x, y) = (hX, hY).
    List<XYPair> solutions = new ArrayList<>();

    for (Long divisor : Divisors.getSquareDivisors(n)) {
      List<XYPair> primitive =
          getPrimtiveSolutions(eq.a, eq.b, eq.c, BigInteger.valueOf(n / divisor));

      BigInteger factor = BigInteger.valueOf(divisor).sqrt();
      for (XYPair sol : primitive) {
        solutions.add(new XYPair(sol.x.multiply(factor), sol.y.multiply(factor)));
      }
    }

    return solutions;
  }

  private static List<XYPair> getPrimtiveSolutions(BigInteger a, BigInteger b, BigInteger c,
      BigInteger n) {
    List<XYPair> solutions = new ArrayList<>();

    // Solve at^2 + bt + c = 0 (mod n) for -n/2 < t <= n/2
    List<BigInteger> congruenceSolutions = UnaryCongruenceSolver.solve(a, b, c, n);
    List<BigInteger> thetas = congruenceSolutions.stream()
        .map(t -> t.compareTo(n.divide(BigInteger.TWO)) > 0 ? t.subtract(n) : t)
        .collect(Collectors.toList());

    BigInteger D = Utils.discriminant(a, b, c);
    BigInteger minusThree = BigInteger.valueOf(-3);
    BigInteger minusFour = BigInteger.valueOf(-4);

    for (BigInteger t : thetas) {
      // By substituting x = tu - ny, we obtain
      // Pu^2 + Quy + Ry^2 = 1, with

      // P = (at^2 + bt + c) / n
      BigInteger P = a.multiply(t).multiply(t).add(b.multiply(t)).add(c).divide(n);

      // Q = -(2at + b)
      BigInteger Q = BigInteger.TWO.multiply(a).multiply(t).add(b).negate();
      // R = na

      if (D.compareTo(minusFour) < 0 && P.equals(BigInteger.ONE)) { // D < -4 && P == 1
        solutions.addAll(solutions(BigInteger.ONE, BigInteger.ZERO, t, n)); // +/- (1, 0)
      } else if (D.equals(minusFour) && P.equals(BigInteger.ONE)) { // D == -4 && P == 1
        BigInteger N = Q.divide(BigInteger.TWO); // N = Q / 2
        solutions.addAll(solutions(BigInteger.ONE, BigInteger.ZERO, t, n)); // +/- (1, 0)
        solutions.addAll(solutions(N, BigInteger.ONE.negate(), t, n)); // +/- (N, -1)
      } else if (D.equals(minusFour) && P.equals(BigInteger.TWO)) { // D == -4 && P == 2
        BigInteger N = Q.divide(BigInteger.TWO); // N = Q / 2
        solutions.addAll(solutions(N.subtract(BigInteger.ONE).divide(BigInteger.TWO),
            BigInteger.ONE.negate(), t, n)); // +/- ((N - 1)/2, -1)
        solutions.addAll(
            solutions(N.add(BigInteger.ONE).divide(BigInteger.TWO), BigInteger.ONE.negate(), t, n)); // +/-
                                                                                                     // ((N
                                                                                                     // +
                                                                                                     // 1)/2,
                                                                                                     // -1)
      } else if (D.equals(minusThree) && P.equals(BigInteger.ONE)) { // D == -3 && P == 1
        BigInteger N = Q.subtract(BigInteger.ONE).divide(BigInteger.TWO); // N = (Q - 1) / 2
        solutions.addAll(solutions(BigInteger.ONE, BigInteger.ZERO, t, n)); // +/- (1, 0)
        solutions.addAll(solutions(N, BigInteger.ONE.negate(), t, n)); // +/- (N, -1)
        solutions.addAll(solutions(N.add(BigInteger.ONE), BigInteger.ONE.negate(), t, n)); // +/- (N
                                                                                           // + 1,
                                                                                           // -1)
      } else if (D.equals(minusThree) && P.equals(BigInteger.valueOf(3))) { // D == -3 && P == 3
        BigInteger N = Q.subtract(BigInteger.ONE).divide(BigInteger.TWO); // N = (Q - 1) / 2
        solutions
            .addAll(solutions(N.subtract(BigInteger.ONE).divide(minusThree), BigInteger.ONE, t, n)); // +/-
                                                                                                     // ((N
                                                                                                     // -
                                                                                                     // 1)/3,
                                                                                                     // -1)
        solutions
            .addAll(solutions(N.multiply(BigInteger.TWO).add(BigInteger.ONE).divide(minusThree),
                BigInteger.TWO, t, n)); // +/- ((2N + 1)/3, -2)
        solutions.addAll(solutions(N.add(BigInteger.TWO).divide(minusThree), BigInteger.ONE, t, n)); // +/-
                                                                                                     // ((N
                                                                                                     // +
                                                                                                     // 2)/3,
                                                                                                     // -1)
      } else {
        BigInteger bound = P.multiply(minusFour).divide(D).sqrt(); // sqrt(4P/-D)

        // convergents of -Q / 2P
        List<XYPair> convergents = ContinuedFraction
            .ofFraction(Q.negate().longValueExact(), P.multiply(BigInteger.TWO).longValueExact())
            .getConvergents();

        for (int i = 0; i < convergents.size(); i++) {
          XYPair convergent = convergents.get(i);

          if (convergent.y.compareTo(bound) > 0) {
            break;
          }

          BigInteger Ai = convergent.x;
          BigInteger Bi = convergent.y;

          // Test if (Ai, Bi) is a solution to the reduced equation
          BigInteger result = Ai.multiply(Ai).multiply(P).add(Ai.multiply(Bi).multiply(Q))
              .add(Bi.multiply(Bi).multiply(n.multiply(a)));

          if (result.equals(BigInteger.ONE)) {
            solutions.addAll(solutions(Ai, Bi, t, n));

            if (D.equals(minusThree) || D.equals(minusFour)) {
              XYPair convergent2 = convergents.get(i + 1);
              solutions.addAll(solutions(convergent2.x, convergent2.y, t, n));
            }

            if (D.equals(minusThree)) {
              XYPair convergent3 = convergents.get(i + 2);
              solutions.addAll(solutions(convergent3.x, convergent3.y, t, n));
            }

            break;
          }
        }
      }
    }

    return solutions;
  }

  private static List<XYPair> solutions(BigInteger u, BigInteger y, BigInteger theta,
      BigInteger n) {
    BigInteger x = theta.multiply(u).subtract(n.multiply(y));

    // (tu - ny, u)
    return Arrays.asList(new XYPair(x, u), new XYPair(x.negate(), u.negate()));
  }
}
