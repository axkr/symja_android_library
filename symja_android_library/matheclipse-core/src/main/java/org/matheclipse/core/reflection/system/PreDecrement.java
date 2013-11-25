package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class PreDecrement extends Decrement {

	public PreDecrement() {
		super();
	}

	@Override
	protected IExpr getResult(IExpr symbolValue, IExpr calculatedResult) {
		return calculatedResult;
	}

	protected ISymbol getFunctionSymbol() {
		return F.PreDecrement;
	}
}