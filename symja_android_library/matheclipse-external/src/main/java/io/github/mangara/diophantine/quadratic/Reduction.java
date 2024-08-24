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
import java.util.List;
import io.github.mangara.diophantine.XYPair;
import io.github.mangara.diophantine.utils.ChineseRemainder;
import io.github.mangara.diophantine.utils.ExtendedEuclidean;
import io.github.mangara.diophantine.utils.Primes;

/**
 * Transforms a restricted equation a x^2 + b xy + c y^2 + f = 0 in which gcd(a, f) > 1 into one
 * with gcd(a, f) = 1.
 */
public class Reduction {

  /**
   * Computes a transformation for the given restricted quadratic equation a x^2 + b xy + c y^2 + f
   * = 0 with gcd(a, f) > 1 into one gcd(a, f) = 1.
   *
   * @param eq
   * @return
   */
  public static Reduction forEquation(RestrictedEquation eq) {
    // Find a unimodular transform into a form with GCD(a, f) = 1
    // 1. Find relatively prime integers alpha and gamma such that a alpha^2 + b alpha gamma + c
    // gamma^2 = A with gcd(A, f) = 1
    XYPair alphaGamma = findAlphaGamma(eq);
    BigInteger alpha = alphaGamma.x, gamma = alphaGamma.y;

    // 2. Find integers beta and delta such that alpha delta - beta gamma = 1
    XYPair betaDelta = findBetaDelta(alpha, gamma);
    BigInteger beta = betaDelta.x, delta = betaDelta.y;

    return new Reduction(alpha, beta, gamma, delta);
  }

  private static XYPair findAlphaGamma(RestrictedEquation eq) {
    List<Long> distinctFactors = Primes.getDistinctPrimeFactors(eq.absF.longValueExact());
    List<XYPair> xEquations = new ArrayList<>();
    List<XYPair> yEquations = new ArrayList<>();

    for (Long p : distinctFactors) {
      BigInteger factor = BigInteger.valueOf(p);

      if (eq.a.mod(factor).signum() == 0) {
        if (eq.c.mod(factor).signum() == 0) {
          if (eq.b.mod(factor).signum() == 0) {
            throw new UnsupportedOperationException();
          } else {
            xEquations.add(new XYPair(BigInteger.ONE, factor));
            yEquations.add(new XYPair(BigInteger.ONE, factor));
          }
        } else {
          xEquations.add(new XYPair(BigInteger.ZERO, factor));
          yEquations.add(new XYPair(BigInteger.ONE, factor));
        }
      } else {
        xEquations.add(new XYPair(BigInteger.ONE, factor));
        yEquations.add(new XYPair(BigInteger.ZERO, factor));
      }
    }

    BigInteger alpha = ChineseRemainder.solveSystem(xEquations);
    BigInteger gamma = ChineseRemainder.solveSystem(yEquations);
    BigInteger gcdAlphaGamma = alpha.gcd(gamma);

    return new XYPair(alpha.divide(gcdAlphaGamma), gamma.divide(gcdAlphaGamma));
  }

  private static XYPair findBetaDelta(BigInteger alpha, BigInteger gamma) {
    XYPair deltaBeta = ExtendedEuclidean.gcdPair(alpha, gamma);
    return new XYPair(deltaBeta.y.negate(), deltaBeta.x);
  }

  private final BigInteger alpha, beta, gamma, delta;

  public Reduction(BigInteger alpha, BigInteger beta, BigInteger gamma, BigInteger delta) {
    this.alpha = alpha;
    this.beta = beta;
    this.gamma = gamma;
    this.delta = delta;
  }

  /**
   * Returns a restricted quadratic equation a x^2 + b xy + c y^2 + f = 0 with gcd(a, f) = 1 whose
   * solutions are in bijection with solutions to the original equation. The given equation must be
   * the same as the one used to create this reduction.
   *
   * @param eq
   * @return
   */
  public RestrictedEquation reduce(RestrictedEquation eq) {
    // Substitute x = alpha X + beta Y; y = gamma X + delta Y to obtain A X^2 + B XY + C Y^2 + f = 0

    // A = a alpha^2 + b alpha gamma + c gamma^2
    BigInteger A = eq.a.multiply(alpha).multiply(alpha).add(eq.b.multiply(alpha).multiply(gamma))
        .add(eq.c.multiply(gamma).multiply(gamma));

    // B = 2 a alpha beta + b alpha delta + b beta gamma + 2 c gamma delta
    BigInteger B = eq.a.multiply(BigInteger.TWO).multiply(alpha).multiply(beta)
        .add(eq.b.multiply(alpha).multiply(delta)).add(eq.b.multiply(beta).multiply(gamma))
        .add(eq.c.multiply(BigInteger.TWO).multiply(gamma).multiply(delta));

    // C = a beta^2 + b beta delta + c delta^2
    BigInteger C = eq.a.multiply(beta).multiply(beta).add(eq.b.multiply(beta).multiply(delta))
        .add(eq.c.multiply(delta).multiply(delta));

    return new RestrictedEquation(A, B, C, eq.f);
  }

  /**
   * Transforms the solutions to the reduced equation into solutions of the original equation.
   *
   * @param reducedSolutions
   * @return
   */
  public List<XYPair> unreduce(List<XYPair> reducedSolutions) {
    List<XYPair> originalSolutions = new ArrayList<>(reducedSolutions.size());

    for (XYPair sol : reducedSolutions) {
      // x = alpha X + beta Y
      BigInteger x = alpha.multiply(sol.x).add(beta.multiply(sol.y));
      // y = gamma X + delta Y
      BigInteger y = gamma.multiply(sol.x).add(delta.multiply(sol.y));

      originalSolutions.add(new XYPair(x, y));
    }

    return originalSolutions;
  }
}
