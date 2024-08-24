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

/**
 * This class contains various utility functions used in multiple parts of the library.
 */
public class Utils {

  /**
   * Computes the GCD (greatest common divisor) of a and b, using the Euclidean algorithm.
   *
   * @param a
   * @param b
   * @return the greatest common divisor of a and b
   */
  public static long gcd(long a, long b) {
    long t;

    while (b != 0) {
      t = b;
      b = a % b;
      a = t;
    }

    return Math.abs(a);
  }

  /**
   * Computes the GCD (greatest common divisor) of a and b, using the Euclidean algorithm.
   *
   * @param a
   * @param b
   * @return the greatest common divisor of a and b
   */
  public static int gcd(int a, int b) {
    return (int) gcd((long) a, (long) b);
  }

  /**
   * Computes the GCD (greatest common divisor) of the given numbers, using repeated invocations of
   * the Euclidean algorithm.
   *
   * @param a
   * @param b
   * @param numbers
   * @return the greatest common divisor of all given numbers
   */
  public static long gcd(long a, long b, long... numbers) {
    long result = gcd(a, b);

    for (long l : numbers) {
      result = gcd(result, l);
    }

    return result;
  }

  /**
   * Tests whether the given number is a perfect square.
   * 
   * @param n
   * @return true iff there is an integer x such that n = x^2
   */
  public static boolean isSquare(long n) {
    return isSquare(BigInteger.valueOf(n));
  }

  /**
   * Tests whether the given number is a perfect square.
   * 
   * @param n
   * @return true iff there is an integer x such that n = x^2
   */
  public static boolean isSquare(BigInteger n) {
    if (n.signum() < 0) {
      return false;
    }

    return n.sqrtAndRemainder()[1].signum() == 0;
  }

  /**
   * Computes the discriminant b^2 - 4ac.
   * 
   * @param a
   * @param b
   * @param c
   * @return b^2 - 4ac
   * @throws ArithmeticException if the final value does not fit in a long
   */
  public static long discriminant(long a, long b, long c) {
    return discriminant(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c))
        .longValueExact();
  }

  /**
   * Computes the discriminant b^2 - 4ac.
   * 
   * @param a
   * @param b
   * @param c
   * @return b^2 - 4ac
   */
  public static BigInteger discriminant(BigInteger a, BigInteger b, BigInteger c) {
    // b * b - 4 * a * c
    return b.multiply(b).subtract(BigInteger.valueOf(4).multiply(a).multiply(c));
  }

  /**
   * Computes the Legendre k = -D(ae^2 - bed + cd^2 + fD).
   * 
   * @param a
   * @param b
   * @param c
   * @param d
   * @param e
   * @param f
   * @param D
   * @return -D(ae^2 - bed + cd^2 + fD)
   * @throws ArithmeticException if the final value does not fit in a long
   */
  public static long legendreConstant(long a, long b, long c, long d, long e, long f, long D) {
    return legendreConstant(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c),
        BigInteger.valueOf(d), BigInteger.valueOf(e), BigInteger.valueOf(f), BigInteger.valueOf(D))
            .longValueExact();
  }

  /**
   * Computes the Legendre k = -D(ae^2 - bed + cd^2 + fD).
   * 
   * @param a
   * @param b
   * @param c
   * @param d
   * @param e
   * @param f
   * @param D
   * @return -D(ae^2 - bed + cd^2 + fD)
   */
  public static BigInteger legendreConstant(BigInteger a, BigInteger b, BigInteger c, BigInteger d,
      BigInteger e, BigInteger f, BigInteger D) {
    // -D(ae^2 - bed + cd^2 + fD)
    return D.negate().multiply(a.multiply(e).multiply(e).subtract(b.multiply(e).multiply(d))
        .add(c.multiply(d).multiply(d)).add(f.multiply(D)));
  }

  /**
   * Computes the Legendre alpha = 2cd - be.
   * 
   * @param b
   * @param c
   * @param d
   * @param e
   * @return 2cd - be
   * @throws ArithmeticException if the final value does not fit in a long
   */
  public static long legendreAlpha(long b, long c, long d, long e) {
    return legendreAlpha(BigInteger.valueOf(b), BigInteger.valueOf(c), BigInteger.valueOf(d),
        BigInteger.valueOf(e)).longValueExact();
  }

  /**
   * Computes the Legendre alpha = 2cd - be.
   * 
   * @param b
   * @param c
   * @param d
   * @param e
   * @return 2cd - be
   */
  public static BigInteger legendreAlpha(BigInteger b, BigInteger c, BigInteger d, BigInteger e) {
    // 2cd - be
    return BigInteger.TWO.multiply(c).multiply(d).subtract(b.multiply(e));
  }

  /**
   * Computes the Legendre beta = 2ae - bd.
   * 
   * @param a
   * @param b
   * @param d
   * @param e
   * @return 2ae - bd
   * @throws ArithmeticException if the final value does not fit in a long
   */
  public static long legendreBeta(long a, long b, long d, long e) {
    return legendreBeta(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(d),
        BigInteger.valueOf(e)).longValueExact();
  }

  /**
   * Computes the Legendre beta = 2ae - bd.
   * 
   * @param a
   * @param b
   * @param d
   * @param e
   * @return 2ae - bd
   */
  public static BigInteger legendreBeta(BigInteger a, BigInteger b, BigInteger d, BigInteger e) {
    // 2ae - bd
    return BigInteger.TWO.multiply(a).multiply(e).subtract(b.multiply(d));
  }
}
