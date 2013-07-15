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
		IAST arg1 = Validate.checkASTType(ast, 1);
		final IAST f0 = (IAST) arg1.clone();
		f0.add(1, ast.get(2));
		return f0;
	}

}