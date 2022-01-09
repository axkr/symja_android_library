package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Exception for the <a
 * href="https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Throw.md">Throw</a>
 * function.
 */
public class ThrowException extends FlowControlException {

  private static final long serialVersionUID = -8468658696375569705L;

  public static final ThrowException THROW_FALSE = new ThrowException(S.False);

  public static final ThrowException THROW_TRUE = new ThrowException(S.True);

  private final IExpr value;
  private final IExpr tag;

  public ThrowException(final IExpr val) {
    this(val, S.None);
  }

  public ThrowException(final IExpr val, final IExpr tag) {
    super();
    this.value = val;
    this.tag = tag;
  }

  /** @return the value of this exception */
  public IExpr getValue() {
    return value;
  }

  /** @return the tag of this exception */
  public IExpr getTag() {
    return tag;
  }
}
