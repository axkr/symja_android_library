package org.matheclipse.core.builtin.constant;

import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.INumericConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Khinchin constant
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Khinchin%27s_constant">Wikipedia:Khinchin's constant</a>
 * 
 */
public class Khinchin extends AbstractSymbolEvaluator implements
		INumericConstant {
	final static public double KHINCHIN = 2.6854520010653064453097148354817956938203822939945;
	
	public Khinchin() {
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.CONSTANT);
	}

	@Override
	public IExpr numericEval(final ISymbol symbol) {
		return F.num(KHINCHIN);
	}

	public double evalReal() {
		return KHINCHIN;
	}

}
