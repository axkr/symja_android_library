package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class DialogReturnException extends ReturnException {

  private static final long serialVersionUID = -3723154693002116846L;

  public static final DialogReturnException DIALOG_RETURN_FALSE =
      new DialogReturnException(S.False);

  public static final DialogReturnException DIALOG_RETURN_TRUE = new DialogReturnException(S.True);

  public DialogReturnException() {
    this(S.Null);
  }

  public DialogReturnException(final IExpr val) {
    super(val);
  }
}
