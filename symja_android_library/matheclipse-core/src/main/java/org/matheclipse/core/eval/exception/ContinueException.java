package org.matheclipse.core.eval.exception;

/**
 * Exception for the <a
 * href="https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Continue.md">Continue</a>
 * function.
 */
public class ContinueException extends FlowControlException {

  public static final ContinueException CONST = new ContinueException();

  /** */
  private static final long serialVersionUID = 7698113142556488875L;

  public ContinueException() {
    super();
  }
}
