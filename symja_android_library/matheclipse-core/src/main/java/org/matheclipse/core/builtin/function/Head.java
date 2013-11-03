package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Head extends AbstractCoreFunctionEvaluator {

	public Head() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() == 2) {
			return F.eval(ast.arg1()).head();
		}
		return F.SymbolHead;
	}

}
