package org.matheclipse.core.eval.exception;

/**
 * Base exception for exceptions, which are used to implement &quot;control flow&quot; functions
 * like for example <code>Break()</code> (BreakException) and <code>Continue()</code>
 * (ContinueException).
 */
public abstract class FlowControlException extends SymjaMathException {

  /**
   * Constructs a new FlowControlException with the specified detail <code>message=null</code>,
   * <code>cause=null</code>, <code>enableSuppression=false</code>, and <code>
   * writableStackTrace=false</code> .
   */
  protected FlowControlException() {}

  protected FlowControlException(String message) {
    super(message);
  }
}
