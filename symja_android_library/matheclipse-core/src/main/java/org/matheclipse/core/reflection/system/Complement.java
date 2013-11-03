package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ExprComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Complement extends AbstractFunctionEvaluator {

	public Complement() {
	}

	@Override
	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() != 3) {
			return null;
		}
		if (!functionList.arg1().isAtom() && !functionList.arg2().isAtom()) {
			final IAST result = F.List();
			((IAST) functionList.arg1()).args().complement(result,
					((IAST) functionList.arg2()).args());
			return result.args().sort(ExprComparator.CONS);
		}
		return null;
	}
}
