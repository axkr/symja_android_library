package org.matheclipse.core.eval.exception;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISymbol;

/** */
public class ArgumentTypeStopException extends LimitException {

  private static final long serialVersionUID = -464391877949488192L;

  private final String fMessage;

  public ArgumentTypeStopException(String message) {
    fMessage = message;
  }

  public ArgumentTypeStopException(String messageShortcut, final IAST listOfArgs) {
    String message = Errors.getMessage(messageShortcut, listOfArgs);
    fMessage = message;
  }

  @Override
  public String getMessage() {
    return fMessage;
  }

  public static void throwNIL() {
    // unexpected NIL expression encountered.
    String str = Errors.getMessage("nil", F.CEmptyList, EvalEngine.get());
    throw new ArgumentTypeStopException(str);
  }

  public String getMessage(ISymbol symbol) {
    return symbol + ": " + fMessage;
  }
}
