package org.matheclipse.parser.client.math;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MathException extends RuntimeException {

  private static final long serialVersionUID = 3520033778672500363L;

  /**
   * @param exprs
   * @return exception with message consisting of truncated string expressions of given tensors
   * @throws Exception if any of the listed tensors is null
   */
  public static MathException of(Object... exprs) {
    String args = Arrays.stream(exprs).map(Object::toString).collect(Collectors.joining(", "));
    return new MathException(args);
  }

  /**
   * Constructs a new MathException with the specified detail <code>message=null</code>, <code>
   * cause=null</code>, <code>enableSuppression=false</code>, and <code>writableStackTrace=false
   * </code> .
   */
  public MathException() {
    super();
  }

  public MathException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
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
