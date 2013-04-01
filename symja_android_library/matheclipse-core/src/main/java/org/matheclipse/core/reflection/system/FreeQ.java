package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FreeQ implements IFunctionEvaluator {

	public FreeQ() {
	}

	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() == 3) {
			return F.bool(functionList.get(1).isFree(functionList.get(2), true));
		}
		return F.False;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
	}

}
