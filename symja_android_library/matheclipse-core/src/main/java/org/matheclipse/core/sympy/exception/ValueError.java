package org.matheclipse.core.sympy.exception;

import org.matheclipse.core.eval.exception.SymjaMathException;

/**
 * Inappropriate argument value (of correct type).
 */
public class ValueError extends SymjaMathException {

  private static final long serialVersionUID = -7617014361454618182L;

  /**
   * Inappropriate argument value (of correct type).
   * 
   * @param message
   */
  public ValueError(String message) {
    super(message);
  }
}
