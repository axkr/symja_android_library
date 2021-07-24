package org.matheclipse.core.eval.exception;

import org.matheclipse.core.basic.Config;
import org.matheclipse.parser.client.math.MathException;

public class SymjaMathException extends MathException {

  /** */
  private static final long serialVersionUID = 3520033778672500363L;

  /**
   * @param exprs
   * @return exception with message consisting of truncated string expressions of given tensors
   * @throws Exception if any of the listed tensors is null
   */
  public static SymjaMathException of(Object... exprs) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < exprs.length; i++) {
      buf.append(exprs[i].toString());
      if (i < exprs.length - 1) {
        buf.append(", ");
      }
    }
    return new SymjaMathException(buf.toString());
  }

  /**
   * Constructs a new MathException with the specified detail <code>message=null</code>, <code>
   * cause=null</code>, <code>enableSuppression=false</code>, and <code>writableStackTrace=false
   * </code> .
   */
  public SymjaMathException() {
    super(null, null, false, Config.SHOW_STACKTRACE);
  }

  public SymjaMathException(String message) {
    super(message);
  }

  public SymjaMathException(String message, Throwable cause) {
    super(message, cause);
  }

  public SymjaMathException(Throwable cause) {
    super(cause);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    if (Config.SHOW_STACKTRACE) {
      // doesn't fill the stack for FlowControlExceptions
      return super.fillInStackTrace();
    } else {
      return this;
    }
  }
}
