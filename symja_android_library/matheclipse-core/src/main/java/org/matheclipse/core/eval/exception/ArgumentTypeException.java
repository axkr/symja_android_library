package org.matheclipse.core.eval.exception;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ArgumentTypeException extends ValidateException {

  private static final long serialVersionUID = 4017342168597803850L;

  private final String fMessage;

  public ArgumentTypeException(String message) {
    fMessage = message;
  }

  public ArgumentTypeException(String messageShortcut, final IAST listOfArgs) {
    String message = Errors.getMessage(messageShortcut, listOfArgs);
    fMessage = message;
  }

  @Override
  public String getMessage() {
    return fMessage;
  }

  public static void throwNIL() {
    // unexpected NIL expression encountered.
    String str = Errors.getMessage("nil", F.CEmptyList);
    throw new ArgumentTypeException(str);
  }

  public static void throwArg(IExpr arg1, IExpr arg2) {
    // illegal arguments: \"`1`\" in `2`
    String str = Errors.getMessage("argillegal", F.list(arg1, arg2));
    throw new ArgumentTypeException(str);
  }

  @Override
  public String getMessage(ISymbol symbol) {
    return symbol + ": " + fMessage;
  }
}
