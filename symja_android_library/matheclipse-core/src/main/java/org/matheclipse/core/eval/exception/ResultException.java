package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/** Exception for the <code>First...</code> functions. */
public class ResultException extends SymjaMathException {

  public static final ResultException THROW_FALSE = new ResultException(S.False);

  public static final ResultException THROW_TRUE = new ResultException(S.True);

  private static final long serialVersionUID = 8962413213437314808L;

  private final IExpr value;

  public ResultException(final IExpr val) {
    this.value = val;
  }

  public IExpr getValue() {
    return value;
  }
}
