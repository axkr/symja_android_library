package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/** Exception for the <code>First...</code> functions. */
public class ResultException extends SymjaMathException {

  public static final ResultException THROW_FALSE = new ResultException(S.False);

  public static final ResultException THROW_TRUE = new ResultException(S.True);
  /** */
  private static final long serialVersionUID = -8468658696375569705L;

  private final IExpr value;
  private final IExpr tag;

  public ResultException() {
    this(null);
  }

  public ResultException(final IExpr val) {
    this(val, S.None);
  }

  public ResultException(final IExpr val, final IExpr tag) {
    super();
    this.value = val;
    this.tag = tag;
  }

  public IExpr getValue() {
    return value;
  }

  public IExpr getTag() {
    return tag;
  }
}
