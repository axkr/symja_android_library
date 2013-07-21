package org.matheclipse.core.builtin.constant;

import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Constant <i>I</i> converted to the complex unit ( 0+1*I )
 *
 */
public class I extends AbstractSymbolEvaluator {
  public I() {
  }

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.CONSTANT);
	}

	@Override
	public IExpr numericEval(final ISymbol symbol) {
		return F.complexNum(0.0, 1.0);
	}

	@Override
	public IExpr evaluate(final ISymbol symbol) {
		return F.complex(F.C0, F.C1);
	}
}
