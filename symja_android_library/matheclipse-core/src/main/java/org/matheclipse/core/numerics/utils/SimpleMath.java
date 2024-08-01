package org.matheclipse.core.numerics.utils;

public final class SimpleMath {

  // ==========================================================================
  // INTEGER MATH
  // ==========================================================================
  /**
   * Computes the integer average of two integers.
   */
  public static final int average(final int x, final int y) {
    // Hacker's delight 2-5 (3)
    return (x & y) + ((x ^ y) >> 1);
  }

  /**
   * Computes the long average of two long values.
   */
  public static final long average(final long x, final long y) {
    // Hacker's delight 2-5 (3)
    return (x & y) + ((x ^ y) >> 1);
  }

  /**
   * Computes the floor of the base-two logarith of the specified integer.
   */
  public static final int log2Int(final int n) {
    if (n <= 0) {
      throw new IllegalArgumentException();
    }
    return 31 - Integer.numberOfLeadingZeros(n);
  }

  // ==========================================================================
  // FLOATING POINT MATH
  // ==========================================================================
  public static final double[] D1MACH = {Double.MIN_VALUE, Double.MAX_VALUE, pow(2.0, -52),
      pow(2.0, -51), Math.log(2.0) / Math.log(10.0)};

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

    // negative power
    if (y < 0) {
      return pow(1.0 / x, -y);
    }

    // trivial cases
    switch (y) {
      case 0:
        return 1.0;
      case 1:
        return x;
      case 2:
        return x * x;
      default:
        break;
    }

    // non trivial case
    double res = 1.0;
    while (y != 0) {
      switch (y & 1) {
        case 0:
          x *= x;
          y >>>= 1;
          break;
        default:
          res *= x;
          --y;
          break;
      }
    }
    return res;
  }

  private SimpleMath() {}
}
