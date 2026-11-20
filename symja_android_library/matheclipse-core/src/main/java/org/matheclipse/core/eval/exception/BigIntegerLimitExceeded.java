package org.matheclipse.core.eval.exception;

import org.matheclipse.core.basic.Config;

/** Exception which will be thrown, if the {@link Config#MAX_BIT_LENGTH} was exceeded. */
public class BigIntegerLimitExceeded extends LimitException {

  private static final long serialVersionUID = 8925451277545397036L;

  private final long fLimit;

  public BigIntegerLimitExceeded(final long limit) {
    fLimit = limit;
  }

  @Override
  public String getMessage() {
    return "BigInteger bit length " + fLimit + " exceeded";
  }

  /**
   * Throws a new <code>BigIntegerLimitExceeded</code> exception, if the
   * {@link Config#MAX_BIT_LENGTH} limit was exceeded.
   * 
   * <p>
   * Usage:
   *
   * <pre>
   * if (((IInteger) number).bitLength() > Config.MAX_BIT_LENGTH / 100) {
   *   BigIntegerLimitExceeded.throwIt(Config.MAX_BIT_LENGTH / 100);
   * }
   * </pre>
   * 
   * @param limit
   */
  public static void throwIt(final long limit) {
    throw new BigIntegerLimitExceeded(limit);
  }

  /**
   * Throws a new <code>BigIntegerLimitExceeded</code> exception, if the <code>n</code>-th power of
   * a number of <code>bits</code> bits exceeds {@link Config#MAX_BIT_LENGTH}. The squarings of a
   * huge power can't be interrupted, so the check has to come before they start;
   * <code>(bits-1)*|n|</code> is a lower bound of the bit length of the power.
   *
   * @param bits the bit length of the base, or of the larger of its numerator and denominator
   * @param n the exponent
   */
  public static void checkPower(final long bits, final long n) {
    if (bits > 1L) {
      final long absN = n == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(n);
      if (absN > Config.MAX_BIT_LENGTH / (bits - 1L)) {
        throwIt(absN > Long.MAX_VALUE / (bits - 1L) ? Long.MAX_VALUE : absN * (bits - 1L));
      }
    }
  }
}
