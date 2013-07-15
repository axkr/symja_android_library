package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 */
public class N implements ICoreFunctionEvaluator {

	public N() {
	}

	public IExpr evaluate(final IAST ast) {
		return numericEval(ast);
	}

	public IExpr numericEval(final IAST ast) {
		Validate.checkSize(ast, 2);
		 
		final EvalEngine engine = EvalEngine.get();
		final boolean numericMode = engine.isNumericMode();
		try {
			engine.setNumericMode(true);
			return engine.evalWithoutNumericReset(ast.get(1));
		} finally {
			engine.setNumericMode(numericMode);
		} 
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
