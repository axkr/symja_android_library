package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.INumericConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Constant Pi
 *
 */
public class Pi extends AbstractSymbolEvaluator implements INumericConstant {
  public Pi() {
  }

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.CONSTANT);
	}

	@Override
	public IExpr numericEval(final ISymbol symbol) {
		return F.num(Math.PI);
	}

	public double evalReal() {
		return Math.PI;
	}

}
