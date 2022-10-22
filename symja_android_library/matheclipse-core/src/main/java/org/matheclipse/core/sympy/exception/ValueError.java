package org.matheclipse.core.sympy.exception;

import org.matheclipse.core.eval.exception.FlowControlException;

public class ValueError extends FlowControlException {
  public ValueError(String message) {
    super(message);
  }
}
