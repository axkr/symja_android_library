package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.INumericConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Euler gamma constant
 * 
 * See <a href="http://en.wikipedia.org/wiki/Euler–Mascheroni_constant">Euler–Mascheroni constant</a>
 */
public class EulerGamma extends AbstractSymbolEvaluator implements
		INumericConstant {
	final static public double EULER_GAMMA = 0.57721566490153286060651209008240243104215933593992;
	
	public EulerGamma() {
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.CONSTANT);
	}

	@Override
	public IExpr numericEval(final ISymbol symbol) {
		return F.num(EULER_GAMMA);
	}

	public double evalReal() {
		return EULER_GAMMA;
	}

}
