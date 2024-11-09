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
}
