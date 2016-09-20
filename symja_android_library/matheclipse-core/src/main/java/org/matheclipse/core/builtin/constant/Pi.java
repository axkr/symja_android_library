package org.matheclipse.core.builtin.constant;

import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Constant Pi
 * 
 */
public class Pi extends AbstractSymbolEvaluator implements ISignedNumberConstant {
	public Pi() {
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.CONSTANT);
	}

	@Override
	public IExpr numericEval(final ISymbol symbol) {
		return F.num(Math.PI);
	}

	public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
		return F.num(ApfloatMath.pi(engine.getNumericPrecision()));
	}

	public double evalReal() {
		return Math.PI;
	}

}
