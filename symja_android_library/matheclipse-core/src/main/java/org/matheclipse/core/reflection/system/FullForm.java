package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FullForm implements IFunctionEvaluator {

	public FullForm() {
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.size() != 2) {
			return null;
		}
		return F.stringx(new StringBuffer(ast.get(1).fullFormString()));
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(ISymbol symbol) {
	}
}
