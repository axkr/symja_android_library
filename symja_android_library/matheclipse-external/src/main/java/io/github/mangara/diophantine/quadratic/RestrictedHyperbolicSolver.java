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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import io.github.mangara.diophantine.Utils;
import io.github.mangara.diophantine.XYPair;
import io.github.mangara.diophantine.utils.ContinuedFraction;
import io.github.mangara.diophantine.utils.Divisors;

/**
 * This implementation is based on Keith Matthews' paper "The Diophantine equation a x^2 + b xy + c
 * y^2 = N, D = b^2 - 4ac > 0" Journal de Théorie des Nombres de Bordeaux 14 (2002), 257-270
 */
public class RestrictedHyperbolicSolver {

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + f = 0, given that D = b^2 -
   * 4ac > 0 and not a perfect square.
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
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + f = 0, given that D = b^2 -
   * 4ac > 0 and not a perfect square.
   *
   * @param a
   * @param b
   * @param c
   * @param f
   * @return an iterator over all integer solutions (x, y)
   */
  public static Iterator<XYPair> solve(BigInteger a, BigInteger b, BigInteger c, BigInteger f) {
    List<XYPair> representativeSolutions = getRepresentativeSolutions(a, b, c, f);

    if (representativeSolutions.isEmpty()) {
      return Collections.emptyIterator();
    } else {
      return new RestrictedHyperbolicIterator(a, b, c, representativeSolutions);
    }
  }

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + f = 0, given that D = b^2 -
   * 4ac > 0 and not a perfect square.
   *
   * @param a
   * @param b
   * @param c
   * @param f
   * @return A solution (x, y) from each family. If gcd(a, f) = 1, this is the solution with least
   *         positive y.
   */
  public static List<XYPair> getRepresentativeSolutions(BigInteger a, BigInteger b, BigInteger c,
      BigInteger f) {
    if (Utils
        .legendreConstant(a, b, c, BigInteger.ZERO, BigInteger.ZERO, f, Utils.discriminant(a, b, c))
        .signum() == 0) {
      throw new IllegalArgumentException("k must be non-zero");
    }

    RestrictedEquation eq = new RestrictedEquation(a, b, c, f).withoutCommonDivisor();

    if (eq == RestrictedEquation.NO_SOLUTIONS) {
      return Collections.emptyList();
    }

    if (eq.a.gcd(eq.f).equals(BigInteger.ONE)) {
      return getRepresentativeSolutionsReduced(eq);
    }

    Reduction reduction = Reduction.forEquation(eq);
    List<XYPair> reducedSolutions = getRepresentativeSolutionsReduced(reduction.reduce(eq));
    return reduction.unreduce(reducedSolutions);
  }

  // Pre: D = b^2 - 4ac > 0, D not a perfect square, gcd(a, b, c) = 1, gcd(a, f) = 1
  private static List<XYPair> getRepresentativeSolutionsReduced(RestrictedEquation eq) {
    // If x and y share a factor d, then -f = a x^2 + b xy + c y^2 is divisible by d^2
    // So we find all square factors of f (including 1) and solve a(x/d)^2 + b(x/d)(y/d) + c(y/d)^2
    // = -(f/d^2) in relatively prime x/d and y/d
    List<XYPair> solutions = new ArrayList<>();
    List<Long> squareDivisors = Divisors.getSquareDivisors(eq.absF.longValueExact());

    for (Long div : squareDivisors) {
      BigInteger divisor = BigInteger.valueOf(div);
      BigInteger factor = divisor.sqrt();

      Set<XYPair> primitiveSolutions =
          getPrimitiveSolutions(eq.a, eq.b, eq.c, eq.f.divide(divisor), eq.D);

      for (XYPair sol : primitiveSolutions) {
        solutions.add(new XYPair(sol.x.multiply(factor), sol.y.multiply(factor)));
      }
    }

    return solutions;
  }

  // Pre: D = b^2 - 4ac > 0, D not a perfect square, gcd(a, b, c) = 1, gcd(a, f) = 1
  private static Set<XYPair> getPrimitiveSolutions(BigInteger a, BigInteger b, BigInteger c,
      BigInteger f, BigInteger D) {
    List<BigInteger> thetas = UnaryCongruenceSolver.solve(a, b, c, f.abs());
    Set<XYPair> primitiveSolutions = new HashSet<>();

    for (BigInteger theta : thetas) {
      getPrimitiveSolution(a, b, c, f, D, theta).ifPresent(sol -> primitiveSolutions.add(sol));
    }

    return primitiveSolutions;
  }

  private static Optional<XYPair> getPrimitiveSolution(BigInteger a, BigInteger b, BigInteger c,
      BigInteger f, BigInteger D, BigInteger theta) {
    Set<XYPair> solutions;

    if (b.mod(BigInteger.TWO).signum() == 0) {
      solutions = getPrimitiveSolutionEven(a, b, c, f, D, theta);
    } else {
      solutions = getPrimitiveSolutionOdd(a, b, c, f, D, theta);
    }

    return solutions.stream().min((sol1, sol2) -> sol1.y.compareTo(sol2.y));
  }

  private static Set<XYPair> getPrimitiveSolutionEven(BigInteger a, BigInteger b, BigInteger c,
      BigInteger f, BigInteger D, BigInteger theta) {
    Set<XYPair> solutions = new HashSet<>();

    int signN = f.negate().signum();
    BigInteger P = a.multiply(theta).add(b.divide(BigInteger.TWO)); // a * theta + b / 2
    BigInteger Q = a.multiply(f.abs()); // a * |F|
    BigInteger Delta = D.divide(BigInteger.valueOf(4));

    // Find continued fraction of w = (-P + √Delta)/Q
    ContinuedFraction w = ContinuedFraction.ofExpression(P.negate(), Delta, Q);
    int i = findSignMatchedCompleteQuotientIndex(w, P.negate(), Delta, Q, 1, signN);
    if (i > 0) {
      solutions.add(solutionFromConvergent(w.convergent(i - 1), theta, f.abs()));
    }

    // Repeat for w* = (-P - √Delta)/Q = (P + √Delta)/(-Q)
    ContinuedFraction wStar = ContinuedFraction.ofExpression(P, Delta, Q.negate());
    int j = findSignMatchedCompleteQuotientIndex(wStar, P, Delta, Q.negate(), 1, -signN);
    if (j > 0) {
      solutions.add(solutionFromConvergent(wStar.convergent(j - 1), theta, f.abs()));
    }

    // D cannot be 5 here, as both b^2 and 4ac are multiples of 4, so we don't have an exceptional
    // solution

    return solutions;
  }

  private static Set<XYPair> getPrimitiveSolutionOdd(BigInteger a, BigInteger b, BigInteger c,
      BigInteger f, BigInteger D, BigInteger theta) {
    Set<XYPair> solutions = new HashSet<>();

    int signN = f.negate().signum();
    BigInteger P = BigInteger.TWO.multiply(a).multiply(theta).add(b); // 2 * a * theta + b
    BigInteger Q = BigInteger.TWO.multiply(a).multiply(f.abs()); // 2 * a * |F|

    // Find continued fraction of w = (-(2P + 1) + √D)/2Q -- this doesn't line up with the next
    // line, not sure what's up there
    ContinuedFraction w = ContinuedFraction.ofExpression(P.negate(), D, Q);
    int i = findSignMatchedCompleteQuotientIndex(w, P.negate(), D, Q, 2, signN);
    if (i > 0) {
      solutions.add(solutionFromConvergent(w.convergent(i - 1), theta, f.abs()));
    }

    // Repeat for w* = (-(2P + 1) - √D)/2Q = (2P + 1 + √D)/(-2Q)
    ContinuedFraction wStar = ContinuedFraction.ofExpression(P, D, Q.negate());
    int j = findSignMatchedCompleteQuotientIndex(wStar, P, D, Q.negate(), 2, -signN);
    if (j > 0) {
      solutions.add(solutionFromConvergent(wStar.convergent(j - 1), theta, f.abs()));
    }

    // Add exceptional solution for D == 5
    if (D.equals(BigInteger.valueOf(5)) && a.signum() != signN) {
      int r = w.getRepetitionStart() <= 1 ? 2 : w.getRepetitionStart() - 1; // If the continued
                                                                            // fraction is purely
                                                                            // periodic, take the
                                                                            // first two convergents

      XYPair convR = w.convergent(r);
      XYPair convRs1 = w.convergent(r - 1);

      BigInteger X = convR.x.subtract(convRs1.x);
      BigInteger y = convR.y.subtract(convRs1.y);
      BigInteger x = y.multiply(theta).add(f.abs().multiply(X));

      solutions.add(new XYPair(x, y));
    }

    return solutions;
  }

  private static int findSignMatchedCompleteQuotientIndex(ContinuedFraction cf, BigInteger a,
      BigInteger b, BigInteger c, long target, int signN) {
    // Check first period or two for Q_i = (-1)^i N/|N| = (-1)^i signN
    int checkLength = cf.getPeriod() % 2 == 0 ? cf.getRepetitionStart() + cf.getPeriod()
        : cf.getRepetitionStart() + 2 * cf.getPeriod();
    List<BigInteger> completeQuotientDenominators =
        cf.getCompleteQuotientDenominators(checkLength, a, b, c);

    for (int i = 1; i < completeQuotientDenominators.size(); i++) {
      try {
        long Qi = completeQuotientDenominators.get(i).longValueExact();
        if (Math.abs(Qi) == target) {
          if (i % 2 == 0) {
            if (Qi == signN * target) {
              return i;
            }
          } else {
            if (Qi == -signN * target) {
              return i;
            }
          }
        }
      } catch (ArithmeticException ex) {
        // Qi is too large for a long, so it can't be equal to target
      }
    }

    return -1;
  }

  private static XYPair solutionFromConvergent(XYPair convergent, BigInteger theta,
      BigInteger absF) {
    BigInteger X = convergent.x;
    BigInteger y = convergent.y;

    // x = y * theta + |f| * X
    BigInteger x = y.multiply(theta).add(absF.multiply(X));

    return new XYPair(x, y);
  }

  private static class RestrictedHyperbolicIterator implements Iterator<XYPair> {

    private final BigInteger a, b, c;
    private final List<XYPair> representativeSolutions;
    private final Iterator<XYPair> pellsFour;

    private int familyIndex = -1;
    private XYPair pellsFourSolution;

    public RestrictedHyperbolicIterator(BigInteger a, BigInteger b, BigInteger c,
        List<XYPair> representativeSolutions) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.representativeSolutions = representativeSolutions;
      pellsFour = PellsSolver.solvePellsFour(Utils.discriminant(a, b, c));
      pellsFourSolution = pellsFour.next();
    }

    @Override
    public boolean hasNext() {
      return true;
    }

    @Override
    public XYPair next() {
      nextFamily();
      return new XYPair(nextX(), nextY());
    }

    private void nextFamily() {
      familyIndex++;
      if (familyIndex == representativeSolutions.size()) {
        familyIndex = 0;
        pellsFourSolution = pellsFour.next();
      }
    }

    private BigInteger nextX() {
      // x = (u - bv)/2 x' - cv y'
      BigInteger xFactor = getU().subtract(b.multiply(getV())).divide(BigInteger.TWO);
      BigInteger yFactor = c.multiply(getV());
      return xFactor.multiply(getX()).subtract(yFactor.multiply(getY()));
    }

    private BigInteger nextY() {
      // y = av x' + (u + bv)/2 y'
      BigInteger xFactor = a.multiply(getV());
      BigInteger yFactor = getU().add(b.multiply(getV())).divide(BigInteger.TWO);
      return xFactor.multiply(getX()).add(yFactor.multiply(getY()));
    }

    private BigInteger getU() {
      return pellsFourSolution.x;
    }

    private BigInteger getV() {
      return pellsFourSolution.y;
    }

    private BigInteger getX() {
      return representativeSolutions.get(familyIndex).x;
    }

    private BigInteger getY() {
      return representativeSolutions.get(familyIndex).y;
    }
  }
}
