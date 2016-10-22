package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class StringDrop extends AbstractFunctionEvaluator {

	public StringDrop() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isString()) {
			String s = ast.arg1().toString();
			final int n = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
			if (n >= 0) {
				return F.$str(s.substring(n, s.length()));
			} else {
				return F.$str(s.substring(0, s.length() + n));
			}
		}

		return F.NIL;
	}
}