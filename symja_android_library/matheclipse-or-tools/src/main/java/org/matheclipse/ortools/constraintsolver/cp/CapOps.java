package org.matheclipse.ortools.constraintsolver.cp;

/**
 * Saturating 64 bit arithmetic as used by OR-Tools (<code>CapAdd</code>, <code>CapSub</code>,
 * <code>CapProd</code>): results which do not fit into a <code>long</code> are replaced by
 * <code>Long.MAX_VALUE</code> or <code>Long.MIN_VALUE</code>, which act as plus and minus
 * infinity.
 *
 * <p>
 * Part of the simplified engine behind the OR-Tools constraint solver API of this module.
 */
public final class CapOps {

  private CapOps() {}

  /** <code>a + b</code>, saturated. */
  public static long capAdd(long a, long b) {
    long r = a + b;
    if (((a ^ r) & (b ^ r)) < 0) {
      return a < 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
    }
    return r;
  }

  /** <code>a - b</code>, saturated. */
  public static long capSub(long a, long b) {
    long r = a - b;
    if (((a ^ b) & (a ^ r)) < 0) {
      return a < 0 ? Long.MIN_VALUE : Long.MAX_VALUE;
    }
    return r;
  }

  /** <code>a * b</code>, saturated. */
  public static long capProd(long a, long b) {
    if (a == 0 || b == 0) {
      return 0;
    }
    long hi = Math.multiplyHigh(a, b);
    long lo = a * b;
    if ((hi == 0 && lo >= 0) || (hi == -1 && lo < 0)) {
      return lo;
    }
    return ((a ^ b) < 0) ? Long.MIN_VALUE : Long.MAX_VALUE;
  }

  /** <code>-a</code>, saturated. */
  public static long capOpp(long a) {
    if (a == Long.MIN_VALUE) {
      return Long.MAX_VALUE;
    }
    if (a == Long.MAX_VALUE) {
      return Long.MIN_VALUE;
    }
    return -a;
  }

  /** Whether <code>v</code> is one of the two saturation values. */
  public static boolean isInfinite(long v) {
    return v == Long.MAX_VALUE || v == Long.MIN_VALUE;
  }

  /** The largest integer not greater than <code>a / b</code>, for <code>b != 0</code>. */
  public static long floorDiv(long a, long b) {
    return Math.floorDiv(a, b);
  }

  /** The smallest integer not less than <code>a / b</code>, for <code>b != 0</code>. */
  public static long ceilDiv(long a, long b) {
    return -Math.floorDiv(-a, b);
  }
}
