package org.matheclipse.ortools.graph.internal;

/**
 * Saturated <code>long</code> arithmetic: results that do not fit are capped at
 * <code>Long.MAX_VALUE</code> or <code>Long.MIN_VALUE</code> (same contract as
 * <code>CapAdd</code>, <code>CapSub</code> and <code>CapProd</code> of
 * <code>ortools/util/saturated_arithmetic.h</code>).
 */
public final class SaturatedArithmetic {
  private SaturatedArithmetic() {}

  /** <code>x + y</code>, saturated. */
  public static long capAdd(long x, long y) {
    final long result = x + y;
    // Overflow iff both operands have the sign opposite to the result.
    if (((x ^ result) & (y ^ result)) < 0) {
      return x < 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
    }
    return result;
  }

  /** <code>x - y</code>, saturated. */
  public static long capSub(long x, long y) {
    final long result = x - y;
    // Overflow iff the operands have different signs and the result has the sign of y.
    if (((x ^ y) & (x ^ result)) < 0) {
      return x < 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
    }
    return result;
  }

  /** <code>x * y</code>, saturated. */
  public static long capProd(long x, long y) {
    final long high = Math.multiplyHigh(x, y);
    final long low = x * y;
    if ((high == 0 && low >= 0) || (high == -1 && low < 0)) {
      return low;
    }
    return ((x < 0) != (y < 0)) ? Long.MIN_VALUE : Long.MAX_VALUE;
  }
}
