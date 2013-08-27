package org.matheclipse.core.generic;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.interfaces.IArrayFunction;

public class UnaryRangeFunction implements IArrayFunction {

	public UnaryRangeFunction() {
	}

	public IExpr evaluate(final IExpr[] index) {
		return (IExpr)index[0];
	}
}
