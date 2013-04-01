package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;


/**
 *
 */
public abstract class AbstractSymbolEvaluator implements ISymbolEvaluator {

	public IExpr evaluate(final ISymbol symbol) {
		return null;
	}

	public IExpr numericEval(final ISymbol symbol) {
		return evaluate(symbol);
	}

  public void setUp(final ISymbol symbol) {
    // do nothing
  }
}
