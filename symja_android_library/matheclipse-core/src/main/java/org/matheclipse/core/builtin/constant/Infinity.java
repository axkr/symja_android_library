package org.matheclipse.core.builtin.constant;

import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * See <a href="http://en.wikipedia.org/wiki/Infinity">Infinity</a>
 */
public class Infinity extends AbstractSymbolEvaluator {
	public Infinity() {
	}

	@Override
	public IExpr evaluate(final ISymbol symbol) {
		return F.CInfinity;//unaryAST1(F.DirectedInfinity, F.C1);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		// don't set CONSTANT attribute !
	}
}
