package org.matheclipse.core.interfaces;

import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;

/** An expression representing a symbol (i.e. variable- constant- or function-name) */
public interface IBuiltInSymbol extends ISymbol {

  /**
   * Get the current evaluator for this symbol
   *
   * @return the evaluator which is associated to this symbol or <code>null</code> if no evaluator
   *         is associated
   */
  public IEvaluator getEvaluator();

  /** Set the current evaluator which is associated to this symbol */
  public void setEvaluator(IEvaluator module);

  /** Define a predicate as the current evaluator which is associated to this symbol */
  public void setPredicateQ(Predicate<IExpr> predicate);

  public default ISymbol mapToGlobal(EvalEngine engine) {
    return null;
  }
}
