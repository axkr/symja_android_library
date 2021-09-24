package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Exception for the <a
 * href="https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Return.md">Return</a>
 * function. This exception throws the result of a Symja {@link S#Return} function.
 */
public class ReturnException extends FlowControlException {

  private static final long serialVersionUID = 6165872840807864554L;

  public static final ReturnException RETURN_FALSE = new ReturnException(S.False);

  public static final ReturnException RETURN_TRUE = new ReturnException(S.True);

  private final IExpr value;

  /** This exception throws the result {@link S.Null} of a Symja {@link S#Return} function. */
  public ReturnException() {
    this(S.Null);
  }

  /**
   * This exception throws the result of a Symja {@link S#Return} function.
   *
   * @param value the returned value
   */
  public ReturnException(final IExpr value) {
    this.value = value;
  }

  public IExpr getValue() {
    return value;
  }
}
