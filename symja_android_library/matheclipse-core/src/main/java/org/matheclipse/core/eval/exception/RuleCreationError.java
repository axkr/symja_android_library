package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.Context;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class RuleCreationError extends ValidateException {

  private static final long serialVersionUID = 4289111239388531874L;

  private final IExpr fLHS;

  public RuleCreationError(IExpr lhs) {
    fLHS = lhs;
  }

  @Override
  public String getMessage() {
    if (fLHS == null) {
      return "Operation not allowed in server mode.";
    }
    // if (fCondition != null) {
    // return "Error in rule creation: Condition not allowed in rules containing no pattern (" +
    // fLHS
    // + " " + fRHS + " " + fCondition + ")";
    // }
    // if (fRHS != null) {
    // return "Error in rule creation: " + fLHS + " " + fRHS;
    // }
    Context context = fLHS.topHead().getContext();
    return "Not allowed left-hand-side expression: \"" + fLHS + "\" from context \"" + context
        + "\"\nPlease use names which aren't predefined by the system.";
  }

  @Override
  public String getMessage(ISymbol symbol) {
    if (fLHS == null) {
      return symbol + ": " + "Operation not allowed in server mode.";
    }
    Context context = fLHS.topHead().getContext();
    return symbol + ": " + "Not allowed left-hand-side expression: \"" + fLHS + "\" from context \""
        + context + "\"\nPlease use names which aren't predefined by the system.";
  }
}
