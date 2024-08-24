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
import io.github.mangara.diophantine.Utils;

/**
 * Represents the equation a x^2 + b xy + c y^2 + f = 0.
 */
public class RestrictedEquation {

  /**
   * Represents an equation with no solutions. This is useful when transforming one restricted
   * equation into another, to capture cases where the transformation does not apply because there
   * are no solutions.
   */
  public static final RestrictedEquation NO_SOLUTIONS =
      new RestrictedEquation(BigInteger.TWO, BigInteger.TWO, BigInteger.TWO, BigInteger.ONE);

  /**
   * The coefficients of this equation.
   */
  public final BigInteger a, b, c, f;

  /**
   * The absolute value of f.
   */
  public final BigInteger absF;

  /**
   * The determinant D = b^2 - 4ac of this equation.
   */
  public final BigInteger D;

  /**
   * Create a new restricted equation a x^2 + b xy + c y^2 + f = 0.
   *
   * @param a
   * @param b
   * @param c
   * @param f
   */
  public RestrictedEquation(long a, long b, long c, long f) {
    this(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c),
        BigInteger.valueOf(f));
  }

  /**
   * Create a new restricted equation a x^2 + b xy + c y^2 + f = 0.
   *
   * @param a
   * @param b
   * @param c
   * @param f
   */
  public RestrictedEquation(BigInteger a, BigInteger b, BigInteger c, BigInteger f) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.f = f;

    this.absF = f.abs();
    this.D = Utils.discriminant(a, b, c);
  }

  /**
   * Returns an equation that represents the same set of solutions as this one, but with gcd(a, b,
   * c) = 1, or NO_SOLUTIONS if gcd(a, b, c) does not divide f.
   *
   * @return
   */
  public RestrictedEquation withoutCommonDivisor() {
    BigInteger gcd = a.gcd(b).gcd(c);

    if (gcd.equals(BigInteger.ONE)) {
      return this;
    } else if (f.remainder(gcd).signum() == 0) {
      return new RestrictedEquation(a.divide(gcd), b.divide(gcd), c.divide(gcd), f.divide(gcd));
    } else {
      return NO_SOLUTIONS;
    }
  }

  @Override
  public String toString() {
    return String.format("%d x^2 + %d xy + %d y^2 = %d, with D = %d", a, b, c, f.negate(), D);
  }
}
