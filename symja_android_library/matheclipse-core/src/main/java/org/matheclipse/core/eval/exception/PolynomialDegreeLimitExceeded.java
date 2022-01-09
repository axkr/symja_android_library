package org.matheclipse.core.eval.exception;

/** Exception which will be thrown, if the Config.MAX_AST_SIZE limit was exceeded. */
public class PolynomialDegreeLimitExceeded extends LimitException {

  private static final long serialVersionUID = 8925451277545397036L;

  private final long fLimit;

  public PolynomialDegreeLimitExceeded(final long limit) {
    fLimit = limit;
  }

  /**
   * Set the exceeded limit to <code>(long)rowDimension*(long)columnDimension</code>.
   *
   * @param rowDimension
   * @param columnDimension
   */
  public PolynomialDegreeLimitExceeded(final int rowDimension, final int columnDimension) {
    fLimit = (long) rowDimension * (long) columnDimension;
  }

  @Override
  public String getMessage() {
    return "Polynomial degree " + fLimit + " exceeded";
  }

  public static void throwIt(final long limit) {
    // HeapContext.enter();
    // try {
    throw new PolynomialDegreeLimitExceeded(limit); // .copy());
    // } finally {
    // HeapContext.exit();
    // }
  }
}
