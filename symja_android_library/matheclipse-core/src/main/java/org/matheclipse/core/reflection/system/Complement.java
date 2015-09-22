package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ExprComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Complement extends AbstractFunctionEvaluator {

	public Complement() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (!ast.arg1().isAtom() && !ast.arg2().isAtom()) {
			final IAST result = F.List();
			((IAST) ast.arg1()).args().complement(result, ((IAST) ast.arg2()).args());
			return result.args().sort(ExprComparator.CONS);
		}
		return null;
	}
}
