package org.matheclipse.core.eval.exception;

/** Exception which will be thrown, if the Config.MAX_AST_SIZE limit was exceeded. */
public class BigIntegerLimitExceeded extends LimitException {

  private static final long serialVersionUID = 8925451277545397036L;

  private final long fLimit;

  public BigIntegerLimitExceeded(final long limit) {
    fLimit = limit;
  }

  /**
   * Set the exceeded limit to <code>(long)rowDimension*(long)columnDimension</code>.
   *
   * @param rowDimension
   * @param columnDimension
   */
  public BigIntegerLimitExceeded(int rowDimension, int columnDimension) {
    fLimit = rowDimension * (long) columnDimension;
  }

  @Override
  public String getMessage() {
    return "BigInteger bit length " + fLimit + " exceeded";
  }

  public static void throwIt(final long limit) {
    // HeapContext.enter();
    // try {
    throw new BigIntegerLimitExceeded(limit); // .copy());
    // } finally {
    // HeapContext.exit();
    // }
  }
}
