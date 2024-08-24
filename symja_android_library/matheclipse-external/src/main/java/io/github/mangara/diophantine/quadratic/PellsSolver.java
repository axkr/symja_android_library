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
import io.github.mangara.diophantine.Utils;
import io.github.mangara.diophantine.XYPair;
import io.github.mangara.diophantine.utils.ContinuedFraction;

/**
 * Solves variations on the Pell's equation: x^2 - D y ^2 = 1.
 * 
 * Based on "Solving the generalized Pell equation x^2 - Dy^2 = N" by John P. Robertson.
 * https://web.archive.org/web/20180831180333/http://www.jpr2718.org/pell.pdf
 */
public class PellsSolver {

  /**
   * Returns an iterator over all solutions to the Diophantine equation x^2 - D y^2 = 4. D must not
   * be a perfect square.
   *
   * @param D
   * @return
   */
  public static Iterator<XYPair> solvePellsFour(BigInteger D) {
    XYPair minimalPositiveSolution = leastPositivePellsFourSolution(D);
    return new PellsFourIterator(D, minimalPositiveSolution);
  }

  /**
   * Returns the least positive solution (minimizing x) to the Diophantine equation x^2 - D * y^2 =
   * 4. D must not be a perfect square.
   *
   * @param D
   * @return
   */
  public static XYPair leastPositivePellsFourSolution(BigInteger D) {
    if (Utils.isSquare(D)) {
      throw new IllegalArgumentException("This method only supports non-squares.");
    }

    switch (D.mod(BigInteger.valueOf(4)).intValue()) {
      case 1: {
        // The solution is part of the convergents of (1 + sqrt(D))/2
        ContinuedFraction cf = ContinuedFraction.ofExpression(BigInteger.ONE, D, BigInteger.TWO);
        int period = cf.getPeriod();
        int index = period % 2 == 0 ? period - 1 : 2 * period - 1;

        XYPair convergent = cf.convergent(index);
        BigInteger x = BigInteger.TWO.multiply(convergent.x).subtract(convergent.y);
        return new XYPair(x, convergent.y);
      }
      case 0: {
        // x must be even, so substitute X = x/2 and solve X^2 - (D/4) y^2 = 1
        XYPair pellSolution = leastPositivePellsSolution(D.divide(BigInteger.valueOf(4)));
        BigInteger x = pellSolution.x.multiply(BigInteger.TWO);
        return new XYPair(x, pellSolution.y);
      }
      default: {
        // x and y must be even, so substitute X = x/2 and Y = y/2
        // and solve X^2 - D Y^2 = 1
        XYPair pellSolution = leastPositivePellsSolution(D);
        BigInteger x = pellSolution.x.multiply(BigInteger.TWO);
        BigInteger y = pellSolution.y.multiply(BigInteger.TWO);
        return new XYPair(x, y);
      }
    }
  }

  /**
   * Returns the least positive solution (minimizing x) to the Diophantine equation x^2 - D * y^2 =
   * 1. D must not be a perfect square.
   *
   * @param D
   * @return
   */
  private static XYPair leastPositivePellsSolution(BigInteger D) {
    if (Utils.isSquare(D)) {
      throw new IllegalArgumentException("This method only supports non-squares.");
    }

    ContinuedFraction cf = ContinuedFraction.ofRoot(D);
    int period = cf.getPeriod();
    int index = period % 2 == 0 ? period - 1 : 2 * period - 1;
    return cf.convergent(index);
  }

  private static class PellsFourIterator implements Iterator<XYPair> {

    // (2, 0), (-2, 0), <all four variations of minimal positive solution>, <all four variations of
    // next solution>, ...
    private final BigInteger D;
    private final BigInteger minU, minV; // Minimal positive solution
    private int startIndex = 0;
    private int progressionIndex = -1;
    private BigInteger u = BigInteger.TWO, v = BigInteger.ZERO;

    public PellsFourIterator(BigInteger D, XYPair minimalPositiveSolution) {
      this.D = D;
      this.minU = minimalPositiveSolution.x;
      this.minV = minimalPositiveSolution.y;
    }

    @Override
    public boolean hasNext() {
      return true;
    }

    @Override
    public XYPair next() {
      switch (startIndex) {
        case 0: {
          startIndex++;
          return new XYPair(BigInteger.TWO, BigInteger.ZERO);
        }
        case 1: {
          startIndex++;
          return new XYPair(BigInteger.TWO.negate(), BigInteger.ZERO);
        }
        default: {
          return nextProgressionValue();
        }
      }
    }

    private XYPair nextProgressionValue() {
      progressionIndex = (progressionIndex + 1) % 4;

      if (progressionIndex == 0) {
        BigInteger nextU = nextU();
        v = nextV();
        u = nextU;
      }

      return signVariation(progressionIndex);
    }

    private BigInteger nextU() {
      // u' = (1/2)(mU u + mV D v)
      return minU.multiply(u).add(minV.multiply(D).multiply(v)).divide(BigInteger.TWO);
    }

    private BigInteger nextV() {
      // v' = (1/2)(mU v + mV u)
      return minU.multiply(v).add(minV.multiply(u)).divide(BigInteger.TWO);
    }

    private XYPair signVariation(int index) {
      switch (index) {
        case 0: {
          return new XYPair(u, v);
        }
        case 1: {
          return new XYPair(u, v.negate());
        }
        case 2: {
          return new XYPair(u.negate(), v);
        }
        case 3: {
          return new XYPair(u.negate(), v.negate());
        }
        default:
          throw new IllegalArgumentException();
      }
    }
  }

}
