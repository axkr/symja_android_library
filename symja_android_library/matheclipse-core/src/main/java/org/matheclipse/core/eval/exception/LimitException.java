package org.matheclipse.core.eval.exception;

/**
 * Base exception for exceptions, which are used to implement &quot;limit control&quot; functions
 * like for example <code>ASTElementLimitExceeded</code> and <code>RecursionLimitExceeded</code> .
 */
public abstract class LimitException extends FlowControlException {

  private static final long serialVersionUID = 3573641258131547324L;

  /**
   * Constructs a new exception with the specified detail <code>message=null</code>, <code>
   * cause=null</code>, <code>enableSuppression=false</code>, and <code>writableStackTrace=false
   * </code> .
   */
  protected LimitException() {}

  protected LimitException(String message) {
    super(message);
  }
}
