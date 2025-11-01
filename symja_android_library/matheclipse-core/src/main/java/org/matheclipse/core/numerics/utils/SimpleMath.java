package org.matheclipse.core.numerics.utils;

public final class SimpleMath {

  // ==========================================================================
  // INTEGER MATH
  // ==========================================================================
  /**
   * Computes the integer average of two integers.
   */
  private static final int average(final int x, final int y) {
    // return IntMath.mean(x, y);
    // Hacker's delight 2-5 (3)
    return (x & y) + ((x ^ y) >> 1);
  }

  /**
   * Computes the long average of two long values.
   */
  private static final long average(final long x, final long y) {
    // return LongMath.mean(x, y);
    // Hacker's delight 2-5 (3)
    return (x & y) + ((x ^ y) >> 1);
  }

  /**
   * Computes the floor of the base-two logarithm of the specified integer.
   */
  private static final int log2Int(final int n) {
    // return IntMath.log2(n, RoundingMode.FLOOR);
    if (n <= 0) {
      throw new IllegalArgumentException();
    }
    return 31 - Integer.numberOfLeadingZeros(n);
  }

  // ==========================================================================
  // FLOATING POINT MATH
  // ==========================================================================
  public static final double[] D1MACH =
      {Double.MIN_VALUE, Double.MAX_VALUE, Math.ulp(1.0), Math.ulp(1.0) * 2.0, Math.log10(2.0)};

  /**
   * Applies the sign of b to a.
   */
  public static final double sign(final double a, final double b) {
    return b >= 0.0 ? Math.abs(a) : -Math.abs(a);
  }

  /**
   * Computes the exponent of x raised to y, using exponentiation by squaring.
   */
  public static final double pow(double x, int y) {
    return Math.pow(x, y);
    // // negative power
    // if (y < 0) {
    // return pow(1.0 / x, -y);
    // }
    //
    // // trivial cases
    // switch (y) {
    // case 0:
    // return 1.0;
    // case 1:
    // return x;
    // case 2:
    // return x * x;
    // default:
    // break;
    // }
    //
    // // non trivial case
    // double res = 1.0;
    // while (y != 0) {
    // switch (y & 1) {
    // case 0:
    // x *= x;
    // y >>>= 1;
    // break;
    // default:
    // res *= x;
    // --y;
    // break;
    // }
    // }
    // return res;
  }

  private SimpleMath() {}
}
