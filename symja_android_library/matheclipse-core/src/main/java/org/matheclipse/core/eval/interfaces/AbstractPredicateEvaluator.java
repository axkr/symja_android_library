package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Abstract interface for built-in Symja functions. The <code>numericEval()</code> method delegates
 * to the <code>evaluate()</code>
 */
public abstract class AbstractPredicateEvaluator extends AbstractFunctionEvaluator
    implements IPredicateHooks {

  public AbstractPredicateEvaluator() {}

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    return evaluatePredicate(ast, ast.arg1(), engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }
}
