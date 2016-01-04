package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Length extends AbstractCoreFunctionEvaluator {

	public Length() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = engine.evaluate(ast.arg1());
		if (arg1.isAST()) {
			return F.integer(((IAST) arg1).size() - 1);
		}
		return F.C0;
	}

}
