package org.matheclipse.core.generic;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.interfaces.IArrayFunction;

public class UnaryArrayFunction implements IArrayFunction<IExpr> {
	final EvalEngine fEngine;

	final IExpr fValue;

	public UnaryArrayFunction(final EvalEngine engine, final IExpr value) {
		fEngine = engine;
		fValue = value;
	}

	public IExpr evaluate(final Object[] index) {
		return fEngine.evaluate(fValue);
	}
}
