package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** */
public abstract class AbstractSymbolEvaluator extends AbstractFunctionEvaluator
    implements ISymbolEvaluator {

  @Override
  public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public IExpr evaluate(final IAST symbol, EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
    return evaluate(symbol, engine);
  }

  @Override
  public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
    return numericEval(symbol, engine);
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // do nothing
  }
}
