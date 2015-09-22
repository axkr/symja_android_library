package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.function.AtomQ;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Join extends AbstractFunctionEvaluator {

	public Join() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);
		if (ast.args().any(AtomQ.CONST)) {
			return null;
		}
		final IAST result = F.List();
		for (int i = 1; i < ast.size(); i++) {
			result.addAll((IAST) ast.get(i));
		}
		return result;
	}
}
