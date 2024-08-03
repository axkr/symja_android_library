package org.matheclipse.core.sympy.exception;

import org.matheclipse.core.eval.exception.SymjaMathException;

public class NotImplementedError extends SymjaMathException {

  private static final long serialVersionUID = 7157337899871788712L;

  public NotImplementedError(String message) {
    super(message);
  }
}
