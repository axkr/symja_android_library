package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Prepend extends AbstractFunctionEvaluator {

	public Prepend() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (ast.get(1).isAST()) {
			final IAST f0 = ((IAST) ast.get(1)).clone();
			f0.add(1, ast.get(2));
			return f0;
		}

		return null;
	}

}