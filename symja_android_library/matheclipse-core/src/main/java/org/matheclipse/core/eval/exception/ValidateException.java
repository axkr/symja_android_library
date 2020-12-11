package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.ISymbol;

/** Base exception for validating function arguments */
public abstract class ValidateException extends SymjaMathException {

  /** */
  private static final long serialVersionUID = 5266791137903109695L;

  public ValidateException() {
    super();
  }

  public abstract String getMessage(ISymbol symbol);
}
