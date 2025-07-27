package org.matheclipse.core.eval.exception;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/** Exception which will be thrown, if the iteration limit of the evaluation loop was exceeded. */
public final class IterationLimitExceeded extends LimitException {

  private static final long serialVersionUID = -5953619629034039117L;

  private final long fLimit;

  private final IExpr fExpr;

  public IterationLimitExceeded(final long limit, IExpr expr) {
    fLimit = limit;
    fExpr = expr;
  }

  @Override
  public String getMessage() {
    // Iteration limit of `1` exceeded.
    return fExpr.topHead().toString() + ": "
        + Errors.getMessage("itlim", F.list(F.ZZ(fLimit), fExpr), EvalEngine.get());
  }

  public static void throwIt(long iterationCounter, final IExpr expr) {
    // HeapContext.enter();
    // try {
    throw new IterationLimitExceeded(iterationCounter, expr); // expr.copy());
    // } finally {
    // HeapContext.exit();
    // }
  }
}
