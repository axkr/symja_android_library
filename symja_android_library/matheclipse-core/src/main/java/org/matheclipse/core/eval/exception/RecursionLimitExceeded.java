package org.matheclipse.core.eval.exception;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/** Exception which will be thrown, if the recursion limit of the evaluation stack was exceeded. */
public class RecursionLimitExceeded extends LimitException {

  private static final long serialVersionUID = 3610700158103716674L;

  private final int fLimit;

  private final IExpr fExpr;

  public RecursionLimitExceeded(final int limit, final IExpr expr) {
    fLimit = limit;
    fExpr = expr;
  }

  @Override
  public String getMessage() {
    if (fExpr == null) {
      // Recursion depth of `1` exceeded during evaluation of `2`.
      return Errors.getMessage("reclim2", F.list(F.ZZ(fLimit), S.Null), EvalEngine.get());
    }
    // Recursion depth of `1` exceeded during evaluation of `2`.
    return Errors.getMessage("reclim2", F.list(F.ZZ(fLimit), fExpr), EvalEngine.get());
  }

  public static void throwIt(final int limit, final IExpr expr) {
    // HeapContext.enter();
    // try {
    throw new RecursionLimitExceeded(limit, null); // .copy());
    // } finally {
    // HeapContext.exit();
    // }
  }
}
