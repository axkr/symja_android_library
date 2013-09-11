package org.matheclipse.core.generic;

import org.matheclipse.core.generic.interfaces.IArrayFunction;
import org.matheclipse.core.interfaces.IExpr;

public class UnaryRangeFunction implements IArrayFunction {

	public UnaryRangeFunction() {
	}

	public IExpr evaluate(final IExpr[] index) {
		return (IExpr)index[0];
	}
}
