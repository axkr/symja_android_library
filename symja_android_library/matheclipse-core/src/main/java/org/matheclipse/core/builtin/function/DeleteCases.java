package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * TODO see Cases
 * 
 */
public class DeleteCases extends AbstractCoreFunctionEvaluator {

	public DeleteCases() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
