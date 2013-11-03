package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 */
public class N extends AbstractCoreFunctionEvaluator {

	public N() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		return numericEval(ast);
	}

	@Override
	public IExpr numericEval(final IAST ast) {
		Validate.checkSize(ast, 2);

		final EvalEngine engine = EvalEngine.get();
		final boolean numericMode = engine.isNumericMode();
		try {
			engine.setNumericMode(true);
			return engine.evalWithoutNumericReset(ast.arg1());
		} finally {
			engine.setNumericMode(numericMode);
		}
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
