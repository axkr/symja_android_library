package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
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
		IAST ast = F.ast(F.DirectedInfinity);
		ast.add(F.C1);
		return ast;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.CONSTANT);
	}
}
