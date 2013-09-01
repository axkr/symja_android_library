package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Continue extends AbstractCoreFunctionEvaluator {

	public Continue() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() == 1) {
			throw new ContinueException();
		}
		Validate.checkSize(ast, 1);

		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
