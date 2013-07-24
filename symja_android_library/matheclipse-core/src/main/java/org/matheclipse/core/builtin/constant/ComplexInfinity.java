package org.matheclipse.core.builtin.constant;

import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 */
public class ComplexInfinity extends AbstractSymbolEvaluator {
	public ComplexInfinity() {
	}

	@Override
	public IExpr evaluate(final ISymbol symbol) {
		return F.CComplexInfinity;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		// don't set CONSTANT attribute !
	}
}
