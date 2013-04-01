package org.matheclipse.core.generic;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.interfaces.IArrayFunction;

public class MultipleConstArrayFunction implements IArrayFunction<IExpr> {
	final IExpr fConstantExpr;

	public MultipleConstArrayFunction(final IExpr expr) {
		fConstantExpr = expr;
	}

	public IExpr evaluate(final Object[] index) {
		return fConstantExpr;
	}
}
