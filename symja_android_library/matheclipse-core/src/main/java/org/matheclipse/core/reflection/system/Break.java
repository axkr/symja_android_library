package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Break implements IFunctionEvaluator {

	public Break() {
	}

	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() == 1) {
			throw new BreakException();
		}

		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
	}

}
