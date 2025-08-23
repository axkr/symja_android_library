package org.matheclipse.core.interfaces;

import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;

/** An expression representing a symbol (i.e. variable- constant- or function-name) */
public interface IBuiltInSymbol extends ISymbol {

  /**
   * Get the current evaluator for this symbol
   *
   * @return the evaluator which is associated to this symbol or <code>null</code> if no evaluator
   *         is associated
   */
  public IFunctionEvaluator getEvaluator();

  public default IExpr evaluate(IAST ast, EvalEngine engine) {
    return this.getEvaluator().evaluate(ast, engine);
  }

  /** Set the current evaluator which is associated to this symbol */
  public void setEvaluator(IFunctionEvaluator module);

  /** Define a predicate as the current evaluator which is associated to this symbol */
  public void setPredicateQ(Predicate<IExpr> predicate);

  public default ISymbol mapToGlobal(EvalEngine engine) {
    return null;
  }
}
