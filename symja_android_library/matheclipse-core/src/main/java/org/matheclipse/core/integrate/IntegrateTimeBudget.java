package org.matheclipse.core.integrate;

import java.util.function.Supplier;
import org.matheclipse.core.eval.TimeLimits;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Run a piece of integration work under a time budget.
 *
 * <p>
 * Some native stages call {@link org.matheclipse.core.eval.EvalEngine#evaluate} internally on
 * inputs that can grind far longer than they are worth (the Rubi rules on an integral they cannot
 * finish, or a {@code RootSum} over a solvable cubic/quartic that {@code FullSimplify} chews on for
 * tens of seconds). A budget is a limit of {@link TimeLimits}: it nests with
 * <code>TimeConstrained</code> and the other budgets, and the earliest deadline wins.
 */
public final class IntegrateTimeBudget {

  private IntegrateTimeBudget() {}

  /**
   * Run {@code work} and return its result, or {@link F#NIL} if it does not finish within
   * {@code budgetMillis} (or if the work itself returns {@code null}).
   *
   * <p>
   * Only this budget running out is answered with {@link F#NIL}; an enclosing limit which runs out
   * as well, or an abort, propagates. A {@code budgetMillis <= 0} runs {@code work} without a
   * budget.
   *
   * @param work the computation, typically a native integration stage
   * @param budgetMillis the budget in milliseconds
   */
  public static IExpr runWithin(Supplier<IExpr> work, long budgetMillis) {
    if (budgetMillis <= 0) {
      IExpr result = work.get();
      return result == null ? F.NIL : result;
    }
    return TimeLimits.call(() -> {
      IExpr result = work.get();
      return result == null ? F.NIL : result;
    }, budgetMillis, () -> F.NIL);
  }
}
