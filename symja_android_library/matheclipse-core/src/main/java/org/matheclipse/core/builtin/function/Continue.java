package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Continue implements ICoreFunctionEvaluator {

	public Continue() {
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.size() == 1) {
			throw new ContinueException();
		}

		return null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
	}

}
