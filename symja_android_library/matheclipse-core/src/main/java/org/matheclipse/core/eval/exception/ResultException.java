package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/** Exception for the <code>First...</code> functions. */
public final class ResultException extends FlowControlException {

  private static final long serialVersionUID = 6451923502427489113L;

  public static final ResultException THROW_FALSE = new ResultException(S.False);

  public static final ResultException THROW_TRUE = new ResultException(S.True);

  private final IExpr value;

  public ResultException(final IExpr val) {
    this.value = val;
  }

  public IExpr getValue() {
    return value;
  }
}
