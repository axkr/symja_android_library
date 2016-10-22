package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class StringTake extends AbstractFunctionEvaluator {

	public StringTake() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isString()) {
			String s = ast.arg1().toString();
			final int n = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
			if (n >= 0) {
				return F.$str(s.substring(0, n));
			} else {
				return F.$str(s.substring(s.length() + n, s.length()));
			}
		}

		return F.NIL;
	}
}