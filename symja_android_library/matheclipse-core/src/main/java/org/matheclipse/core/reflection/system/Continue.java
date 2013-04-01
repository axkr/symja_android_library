package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Continue implements IFunctionEvaluator {

	public Continue() {
	}

	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() == 1) {
			throw new ContinueException();
		}

		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
	}

}
