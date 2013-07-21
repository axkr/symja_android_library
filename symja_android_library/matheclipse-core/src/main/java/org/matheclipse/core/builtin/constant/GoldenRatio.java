package org.matheclipse.core.builtin.constant;

import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.INumericConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Golden ratio
 * 
 * See <a href="http://en.wikipedia.org/wiki/Golden_ratio">Wikipedia:Golden ratio</a>
 */
public class GoldenRatio extends AbstractSymbolEvaluator implements INumericConstant {
	final static public double GOLDEN_RATIO = 1.6180339887498948482045868343656381177203091798058;

	public GoldenRatio() {
	}

	@Override
	public IExpr evaluate(final ISymbol symbol) {
		// 1/2*(1+5^(1/2))
		return F.Times(F.C1D2, F.Plus(F.C1, F.Power(F.integer(5), F.C1D2)));
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.CONSTANT);
	}

	@Override
	public IExpr numericEval(final ISymbol symbol) {
		return F.num(GOLDEN_RATIO);
	}

	public double evalReal() {
		return GOLDEN_RATIO;
	}

}
