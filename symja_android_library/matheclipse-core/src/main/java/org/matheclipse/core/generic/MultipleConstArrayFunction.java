package org.matheclipse.core.generic;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.interfaces.IArrayFunction;

public class MultipleConstArrayFunction implements IArrayFunction {
	final IExpr fConstantExpr;

	public MultipleConstArrayFunction(final IExpr expr) {
		fConstantExpr = expr;
	}

	public IExpr evaluate(final IExpr[] index) {
		return fConstantExpr;
	}
}
