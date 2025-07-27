package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/** This exception throws the result value {@link F#NIL} of a Symja {@link S#Condition} function. */
public final class ConditionException extends FlowControlException {

  public static final ConditionException CONDITION_NIL = new ConditionException();

  private static final long serialVersionUID = -1175359074220162860L;

  public IExpr getValue() {
    return F.NIL;
  }

  @Override
  public String getMessage() {
    return "Condition[] exception";
  }
}
