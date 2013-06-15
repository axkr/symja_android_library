package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class NonCommutativeMultiply extends AbstractFunctionEvaluator {

	public NonCommutativeMultiply() {
	}

	@Override
	public IExpr evaluate(final IAST lst) {
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.ONEIDENTITY | ISymbol.FLAT
				| ISymbol.LISTABLE);
	}

}
