package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Reverse extends AbstractFunctionEvaluator {

	public Reverse() {
	}

	@Override
	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() != 2) {
			return null;
		}
		if (!functionList.arg1().isAtom()) {
			final IAST result = F.ast(functionList.arg1().head());
			((IAST) functionList.arg1()).args().reverse(result);
//			Rotating.reverse((IAST) list.arg1(), result, 1);
			return result;
		}
		return null;
	}

}
