package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Return implements IFunctionEvaluator {

	public Return() {
	}

	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() == 1) {
			throw new ReturnException();
		}
		if (functionList.size() == 2) {
			throw new ReturnException(functionList.get(1));
		}

		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
	}

}
