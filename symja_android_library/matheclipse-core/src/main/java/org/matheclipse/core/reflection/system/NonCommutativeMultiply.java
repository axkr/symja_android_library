package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class NonCommutativeMultiply extends AbstractFunctionEvaluator {

	public NonCommutativeMultiply() {
	}

	@Override
	public IExpr evaluate(final IAST lst, EvalEngine engine) {
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		// don't set OneIdentity attribute!
		newSymbol.setAttributes(ISymbol.FLAT | ISymbol.LISTABLE);
	}

}
