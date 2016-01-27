package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Return extends AbstractCoreFunctionEvaluator {

	public Return() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 1) {
			throw new ReturnException();
		}
		if (ast.size() == 2) {
			throw new ReturnException(engine.evaluate(ast.arg1()));
		}
		Validate.checkRange(ast, 1, 2);

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
