package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

public class StringTake extends AbstractFunctionEvaluator {

	public StringTake() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (ast.get(1) instanceof IStringX && ast.get(2).isInteger()) {
			String s = ast.get(1).toString();
			final int n = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
			if (n >= 0) {
				return F.stringx(s.substring(0, n));
			} else {
				return F.stringx(s.substring(s.length() + n, s.length()));
			}
		}

		return null;
	}
}