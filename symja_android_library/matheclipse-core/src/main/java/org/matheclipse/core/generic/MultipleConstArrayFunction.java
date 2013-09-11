package org.matheclipse.core.generic;

import org.matheclipse.core.generic.interfaces.IArrayFunction;
import org.matheclipse.core.interfaces.IExpr;

public class MultipleConstArrayFunction implements IArrayFunction {
	final IExpr fConstantExpr;

	public MultipleConstArrayFunction(final IExpr expr) {
		fConstantExpr = expr;
	}

	public IExpr evaluate(final IExpr[] index) {
		return fConstantExpr;
	}
}
