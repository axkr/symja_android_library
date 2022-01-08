package org.matheclipse.parser.client.math;

public class MathException extends RuntimeException {

  /** */
  private static final long serialVersionUID = 3520033778672500363L;

  /**
   * @param exprs
   * @return exception with message consisting of truncated string expressions of given tensors
   * @throws Exception if any of the listed tensors is null
   */
  public static MathException of(Object... exprs) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < exprs.length; i++) {
      buf.append(exprs[i].toString());
      if (i < exprs.length - 1) {
        buf.append(", ");
      }
    }
    return new MathException(buf.toString());
  }

  /**
   * Constructs a new MathException with the specified detail <code>message=null</code>, <code>
   * cause=null</code>, <code>enableSuppression=false</code>, and <code>writableStackTrace=false
   * </code> .
   */
  public MathException() {
    super();
  }

  public MathException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public MathException(String message) {
    super(message);
  }

  public MathException(String message, Throwable cause) {
    super(message, cause);
  }

  public MathException(Throwable cause) {
    super(cause);
  }
}
