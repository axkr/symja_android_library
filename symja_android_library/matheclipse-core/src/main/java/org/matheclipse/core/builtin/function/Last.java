package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Last(list) - get the last element of a list.
 */
public class Last extends AbstractCoreFunctionEvaluator {

	public Last() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = engine.evaluate(ast.arg1());
		if (arg1.isAST()) {
			final IAST list = (IAST) arg1;
			if (list.size() > 1) {
				return list.last();
			}
		}
		return F.NIL;
	}
}
