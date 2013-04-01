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
		if (!functionList.get(1).isAtom() && !functionList.get(2).isAtom()) {
			final IAST result = F.List();
			((IAST) functionList.get(1)).args().complement(result,
					((IAST) functionList.get(2)).args());
			return result.args().sort(ExprComparator.CONS);
		}
		return null;
	}
}
