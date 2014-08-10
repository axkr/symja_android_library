package org.matheclipse.core.builtin.constant;

import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Glaisher constant
 * 
 * See <a href="http://en.wikipedia.org/wiki/Glaisher-Kinkelin_constant">Wikipedia:Glaisher-Kinkelin constant</a>
 * 
 */
public class Glaisher extends AbstractSymbolEvaluator implements
		ISignedNumberConstant {
	final static public double GLAISHER = 1.2824271291006226368753425688697917277676889273250;
	
	public Glaisher() {
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.CONSTANT);
	}

	@Override
	public IExpr numericEval(final ISymbol symbol) {
		return F.num(GLAISHER);
	}

	public double evalReal() {
		return GLAISHER;
	}

}
