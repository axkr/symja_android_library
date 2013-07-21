package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Return implements ICoreFunctionEvaluator {

	public Return() {
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.size() == 1) {
			throw new ReturnException();
		}
		if (ast.size() == 2) {
			throw new ReturnException(F.eval(ast.get(1)));
		}

		return null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
	}

}
