package org.matheclipse.core.eval.exception.sympy;

import org.matheclipse.core.eval.exception.FlowControlException;

public class PoleError extends FlowControlException {
  public PoleError(String message) {
    super(message);
  }
}
