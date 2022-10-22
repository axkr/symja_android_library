package org.matheclipse.core.sympy.exception;

import org.matheclipse.core.eval.exception.FlowControlException;

public class PoleError extends FlowControlException {
  public PoleError(String message) {
    super(message);
  }
}
