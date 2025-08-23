package org.matheclipse.core.eval.exception;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.matheclipse.core.basic.Config;
import org.matheclipse.parser.client.math.MathException;

public class SymjaMathException extends MathException {

  private static final long serialVersionUID = 3520033778672500363L;

  /**
   * @param exprs
   * @return exception with message consisting of truncated string expressions of given tensors
   * @throws Exception if any of the listed tensors is null
   */
  public static SymjaMathException of(Object... exprs) {
    String args = Arrays.stream(exprs).map(Object::toString).collect(Collectors.joining(", "));
    return new SymjaMathException(args);
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
  public final synchronized Throwable fillInStackTrace() {
    if (Config.SHOW_STACKTRACE) {
      // doesn't fill the stack for FlowControlExceptions
      return super.fillInStackTrace();
    } else {
      return this;
    }
  }
}
