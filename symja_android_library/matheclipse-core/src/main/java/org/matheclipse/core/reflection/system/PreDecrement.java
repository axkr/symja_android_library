package org.matheclipse.core.reflection.system;

import org.matheclipse.core.interfaces.IExpr;

public class PreDecrement extends Decrement {

	public PreDecrement() {
		super();
	}

	@Override
	protected IExpr getResult(IExpr symbolValue, IExpr calculatedResult) {
		return calculatedResult;
	}

}