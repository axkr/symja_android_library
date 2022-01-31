package org.matheclipse.core.eval.exception;

/** Exception which will be thrown, if the Config.MAX_AST_SIZE limit was exceeded. */
public class ASTElementLimitExceeded extends LimitException {

  private static final long serialVersionUID = 8925451277545397036L;

  private final long fRequestedCapacity;

  public ASTElementLimitExceeded(final long requestedCapacity) {
    fRequestedCapacity = requestedCapacity;
  }

  /**
   * Set the exceeded limit to <code>(long)rowDimension*(long)columnDimension</code>.
   *
   * @param rowDimension
   * @param columnDimension
   */
  public ASTElementLimitExceeded(int rowDimension, int columnDimension) {
    fRequestedCapacity = rowDimension * (long) columnDimension;
  }

  @Override
  public String getMessage() {
    return "Maximum AST dimension " + fRequestedCapacity + " exceeded";
  }

  /**
   * Throws exception which will be thrown, if the Config.MAX_AST_SIZE limit was exceeded.
   *
   * <p>
   * Usage:
   *
   * <pre>
   * if (Config.MAX_AST_SIZE < requestedCapacity) {
   *   ASTElementLimitExceeded.throwIt(requestedCapacity);
   * }
   * </pre>
   *
   * @param requestedCapacity
   */
  public static void throwIt(final long requestedCapacity) {
    throw new ASTElementLimitExceeded(requestedCapacity);
  }
}
