package org.matheclipse.core.reflection.system;

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
		return F.ast(F.DirectedInfinity);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.CONSTANT);
	}
}
