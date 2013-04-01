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
		if (!functionList.get(1).isAtom()) {
			final IAST result = F.ast(functionList.get(1).head());
			((IAST) functionList.get(1)).args().reverse(result);
//			Rotating.reverse((IAST) list.get(1), result, 1);
			return result;
		}
		return null;
	}

}
