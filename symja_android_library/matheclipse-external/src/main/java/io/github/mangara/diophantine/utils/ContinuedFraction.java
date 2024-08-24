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
package io.github.mangara.diophantine.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.mangara.diophantine.XYPair;

public class ContinuedFraction {

  public static final int NO_REPETITION = -1;

  private final List<Long> coefficients;
  private final int repetitionStart;

  /**
   * Returns the continued fraction of a/c.
   *
   * @param a
   * @param c
   * @return
   */
  public static ContinuedFraction ofFraction(long a, long c) {
    return ofFraction(BigInteger.valueOf(a), BigInteger.valueOf(c));
  }

  /**
   * Returns the continued fraction of a/c.
   *
   * @param a
   * @param c
   * @return
   */
  public static ContinuedFraction ofFraction(BigInteger a, BigInteger c) {
    return ofExpression(a, BigInteger.ZERO, c);
  }

  /**
   * Returns the continued fraction of sqrt(b).
   *
   * @param b
   * @return
   */
  public static ContinuedFraction ofRoot(long b) {
    return ofRoot(BigInteger.valueOf(b));
  }

  /**
   * Returns the continued fraction of sqrt(b).
   *
   * @param b
   * @return
   */
  public static ContinuedFraction ofRoot(BigInteger b) {
    return ofExpression(BigInteger.ZERO, b, BigInteger.ONE);
  }

  /**
   * Returns the continued fraction of (a + sqrt(b))/c.
   *
   * @param a
   * @param b
   * @param c
   * @return
   */
  public static ContinuedFraction ofExpression(long a, long b, long c) {
    return ofExpression(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c));
  }

  /**
   * Returns the continued fraction of (a + sqrt(b))/c.
   *
   * @param a
   * @param b
   * @param c
   * @return
   */
  public static ContinuedFraction ofExpression(BigInteger a, BigInteger b, BigInteger c) {
    if (b.subtract(a.multiply(a)).remainder(c) != BigInteger.ZERO) { // b - a^2 is not divisible by
                                                                     // c, multiply by c first so
                                                                     // that all divisions are exact
      a = a.multiply(c);
      b = b.multiply(c).multiply(c);
      c = c.multiply(c);
    }

    List<Long> coefficients = new ArrayList<>();
    Map<XYPair, Integer> pairs = new HashMap<>();

    BigInteger[] sqrtBAndRemainder = b.sqrtAndRemainder();
    BigInteger sqrtB = sqrtBAndRemainder[0];
    boolean bIsPerfectSquare = sqrtBAndRemainder[1].signum() == 0;

    long coefficient = coefficient(a, sqrtB, c, bIsPerfectSquare);
    XYPair pair = new XYPair(a, c);
    int i = 0;

    do {
      coefficients.add(coefficient);
      pairs.put(pair, i);
      i++;

      a = BigInteger.valueOf(coefficient).multiply(c).subtract(a); // coefficient * C - A;
      c = b.subtract(a.multiply(a)).divide(c); // (B - A * A) / C;

      if (c.signum() == 0) {
        return new ContinuedFraction(coefficients, NO_REPETITION);
      }

      coefficient = coefficient(a, sqrtB, c, bIsPerfectSquare);

      pair = new XYPair(a, c);
    } while (!pairs.containsKey(pair));

    return new ContinuedFraction(coefficients, pairs.get(pair));
  }

  // floor((a + sqrt(b))/c)
  private static long coefficient(BigInteger A, BigInteger sqrtB, BigInteger C,
      boolean bIsPerfectSquare) {
    BigInteger top = A.add(sqrtB);
    long coefficient = top.divide(C).longValueExact();

    if (top.remainder(C) == BigInteger.ZERO && C.signum() == -1 && !bIsPerfectSquare) {
      coefficient--;
    } else if (top.signum() * C.signum() == -1) {
      coefficient--;
    }

    return coefficient;
  }

  /**
   * Creates a new continued fraction.
   *
   * Given coefficients [a0, a1, ..., an] and repetitionStart k, the continued fraction is either
   *
   * [a0; a1, ..., an] if k == NO_REPETITION, or [a0; a1, ..., (ak, ..., an)] if
   * {@literal 0 <= k < n}
   *
   * @param coefficients
   * @param repetitionStart
   */
  public ContinuedFraction(List<Long> coefficients, int repetitionStart) {
    if (coefficients.isEmpty()) {
      throw new IllegalArgumentException("coefficients may not be empty");
    }
    if (repetitionStart >= coefficients.size()) {
      throw new IllegalArgumentException(
          "repetitionStart must be a 0-based index of a coefficient, received " + repetitionStart
              + " with only " + coefficients.size() + " coefficients.");
    }

    this.coefficients = coefficients;
    this.repetitionStart = repetitionStart;
  }

  /**
   * Creates a new finite continued fraction with the given coefficients.
   *
   * @param coefficients
   */
  public ContinuedFraction(List<Long> coefficients) {
    this(coefficients, NO_REPETITION);
  }

  /**
   * Returns a list of all coefficients.
   *
   * @return
   */
  public List<Long> getCoefficients() {
    return coefficients;
  }

  /**
   * Returns the 0-based index of the first coefficient that repeats if this continued fraction is
   * infinite, or NO_REPETITION otherwise.
   *
   * @return
   */
  public int getRepetitionStart() {
    return repetitionStart;
  }

  /**
   * Returns the list of coefficients that repeat if this continued fraction is infinite, or an
   * empty list otherwise.
   *
   * @return
   */
  public List<Long> getRepeatingCoefficients() {
    if (repetitionStart == NO_REPETITION) {
      return Collections.emptyList();
    } else {
      return coefficients.subList(repetitionStart, coefficients.size());
    }
  }

  /**
   * Returns the number of repeating coefficients if this continued fraction is infinite, or 0
   * otherwise.
   *
   * @return
   */
  public int getPeriod() {
    if (repetitionStart == NO_REPETITION) {
      return 0;
    } else {
      return coefficients.size() - repetitionStart;
    }
  }

  /**
   * Returns the first length coefficients of this continued fraction.
   *
   * If length is greater than the number of coefficients, the repeating coefficients are repeated
   * as often as necessary if this continued fraction is infinite. Otherwise, all coefficients are
   * returned.
   *
   * @param length
   * @return
   */
  public List<Long> getCoefficients(int length) {
    List<Long> repeating = getRepeatingCoefficients();
    List<Long> result = new ArrayList<>(coefficients);

    if (!repeating.isEmpty()) {
      while (result.size() < length) {
        result.addAll(repeating);
      }
    }

    return result.subList(0, Math.min(length, result.size()));
  }

  /**
   * Returns all convergents of this finite continued fraction.
   *
   * @return a list of convergents x/y
   */
  public List<XYPair> getConvergents() {
    if (repetitionStart != NO_REPETITION) {
      throw new IllegalArgumentException("Must specify a length for infinite continued fractions.");
    }

    return getConvergents(coefficients.size());
  }

  /**
   * Returns the first length convergents of this continued fraction.
   *
   * Algorithm adapted from Moore (1964) An Introduction to Continuous Fractions.
   *
   * @param length
   * @return a list of convergents x/y
   */
  public List<XYPair> getConvergents(int length) {
    if (length <= 0) {
      return Collections.emptyList();
    }

    List<Long> cfs = getCoefficients(length);
    List<BigInteger> num = new ArrayList<>(length + 1);
    List<BigInteger> den = new ArrayList<>(length + 1);
    List<XYPair> result = new ArrayList<>(length);

    num.add(BigInteger.ONE); // r_0 = 1
    num.add(BigInteger.valueOf(cfs.get(0))); // r_1 = a_1

    den.add(BigInteger.ZERO); // s_0 = 0
    den.add(BigInteger.ONE); // s_1 = 1

    result.add(new XYPair(num.get(1), den.get(1))); // C_1 = r_1 / s_1 = a_1 / 1

    for (int i = 1; i < length; i++) {
      BigInteger coeff = BigInteger.valueOf(cfs.get(i));

      // r_n = a_n r_(n-1) + r_(n-2)
      BigInteger newNum = coeff.multiply(num.get(i)).add(num.get(i - 1));
      num.add(newNum);

      // s_n = a_n s_(n-1) + s_(n-2)
      BigInteger newDen = coeff.multiply(den.get(i)).add(den.get(i - 1));
      den.add(newDen);

      // C_n = r_n / s_n
      result.add(new XYPair(newNum, newDen));
    }

    return result;
  }

  /**
   * Returns the i-th convergent of this continued fraction. The 0-th convergent is the first
   * coefficient.
   *
   * @param i
   * @return
   */
  public XYPair convergent(int i) {
    List<Long> cfs = getCoefficients(i + 1);
    BigInteger numerator = BigInteger.valueOf(cfs.get(i));
    BigInteger denominator = BigInteger.ONE;

    for (int j = i - 1; j >= 0; j--) {
      BigInteger n = BigInteger.valueOf(cfs.get(j));

      // Compute n + 1 / fraction = n + denominator / numerator = (n * numerator + denominator) /
      // numerator
      BigInteger oldNumerator = numerator;
      numerator = n.multiply(numerator).add(denominator);
      denominator = oldNumerator;
    }

    return new XYPair(numerator, denominator);
  }

  /**
   * If this is the continued fraction of (a + sqrt(b))/c, this method returns the first length
   * denominators of the complete quotients.
   *
   * @param length
   * @param a
   * @param b
   * @param c
   * @return
   */
  public List<BigInteger> getCompleteQuotientDenominators(int length, BigInteger a, BigInteger b,
      BigInteger c) {
    List<Long> cfs = getCoefficients(length);
    List<BigInteger> denominators = new ArrayList<>(length);

    if (b.subtract(a.multiply(a)).remainder(c) != BigInteger.ZERO) { // b - a^2 is not divisible by
                                                                     // c, multiply by c first so
                                                                     // that all divisions are exact
      a = a.multiply(c);
      b = b.multiply(c).multiply(c);
      c = c.multiply(c);
    }

    for (Long coefficient : cfs) {
      denominators.add(c);
      a = BigInteger.valueOf(coefficient).multiply(c).subtract(a);
      c = b.subtract(a.multiply(a)).divide(c);
    }

    return denominators;
  }

  @Override
  public String toString() {
    if (coefficients.isEmpty()) {
      return "[]";
    } else if (coefficients.size() == 1) {
      return "[" + Long.toString(coefficients.get(0)) + "]";
    }

    StringBuilder sb = new StringBuilder("[");

    sb.append(coefficients.get(0));
    sb.append(';');

    for (int i = 1; i < coefficients.size(); i++) {
      if (i == repetitionStart) {
        sb.append('(');
      }

      sb.append(coefficients.get(i));
      sb.append(',');
    }

    sb.deleteCharAt(sb.length() - 1); // Remove last comma
    if (repetitionStart != NO_REPETITION) {
      sb.append(')');
    }
    sb.append(']');

    return sb.toString();
  }
}
