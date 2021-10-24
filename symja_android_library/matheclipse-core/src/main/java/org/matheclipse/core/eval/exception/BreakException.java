package org.matheclipse.core.eval.exception;

/**
 * Exception for the <a
 * href="https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Break.md">Break</a>
 * function.
 */
public class BreakException extends FlowControlException {
  public static final BreakException CONST = new BreakException();

  /** */
  private static final long serialVersionUID = 3859495776051748837L;

  public BreakException() {
    super();
  }
}
