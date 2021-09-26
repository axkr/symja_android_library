package org.matheclipse.core.eval.exception;

/** Exception used for flow control. */
public class NoEvalException extends FlowControlException {

  public static final NoEvalException CONST = new NoEvalException();

  /** */
  private static final long serialVersionUID = 3859495776051748837L;

  public NoEvalException() {
    super();
  }
}
