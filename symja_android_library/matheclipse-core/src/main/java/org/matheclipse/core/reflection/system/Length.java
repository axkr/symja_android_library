package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Length extends AbstractFunctionEvaluator {

	public Length() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		if (ast.get(1).isAST()) {
			return F.integer(((IAST) ast.get(1)).size() - 1);
		}
		return F.C0;
	}

}
