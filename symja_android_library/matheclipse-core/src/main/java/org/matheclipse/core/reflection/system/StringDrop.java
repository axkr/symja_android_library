package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

public class StringDrop extends AbstractFunctionEvaluator {

	public StringDrop() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1() instanceof IStringX && ast.arg2().isInteger()) {
			String s = ast.arg1().toString();
			final int n = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
			if (n >= 0) {
				return F.stringx(s.substring(n, s.length()));
			} else {
				return F.stringx(s.substring(0, s.length() + n));
			}
		}

		return null;
	}
}