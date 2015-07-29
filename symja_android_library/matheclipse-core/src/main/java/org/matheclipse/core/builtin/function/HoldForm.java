package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class HoldForm implements IFunctionEvaluator {

	public HoldForm() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		return null;
	}
 
	@Override
	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}
 
	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
