package org.matheclipse.core.builtin.constant;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.ISymbolEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The symbol <code>Ans</code> returns the last result expression (&quot;answer&quot;) from the evaluation engine.
 * 
 * 
 */
public class Ans implements ISymbolEvaluator {

	public Ans() {
	}

	@Override
	public void setUp(ISymbol symbol) {

	}

	@Override
	public IExpr evaluate(ISymbol symbol) {
		return EvalEngine.get().getAnswer();
	}

	@Override
	public IExpr numericEval(ISymbol symbol) {
		return EvalEngine.get().getAnswer();
	}

}
