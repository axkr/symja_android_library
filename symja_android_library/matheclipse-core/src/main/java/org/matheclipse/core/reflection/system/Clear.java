package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Clear implements IFunctionEvaluator {

	public Clear() {
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.size() == 2) {
			ISymbol symbol = (ISymbol) ast.get(1);
			symbol.clear(EvalEngine.get());
			return F.Null;
		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
