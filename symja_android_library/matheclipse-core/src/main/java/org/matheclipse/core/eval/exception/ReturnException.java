package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class ReturnException extends FlowControlException {
  /** */
  private static final long serialVersionUID = 6165872840807864554L;

  public static final ReturnException RETURN_FALSE = new ReturnException(S.False);

  public static final ReturnException RETURN_TRUE = new ReturnException(S.True);

  protected final IExpr value;

  public ReturnException() {
    this(S.Null);
  }

  public ReturnException(final IExpr val) {
    super();
    value = val;
  }

  public IExpr getValue() {
    return value;
  }
}
