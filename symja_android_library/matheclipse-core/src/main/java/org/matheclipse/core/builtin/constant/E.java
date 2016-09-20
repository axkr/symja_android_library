package org.matheclipse.core.builtin.constant;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Euler's constant E
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Exponential_function">Wikipedia - Exponential function</a>
 */
public class E extends AbstractSymbolEvaluator implements ISignedNumberConstant {
	public E() {
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.CONSTANT);
	}

	@Override
	public IExpr numericEval(final ISymbol symbol) {
		return F.num(Math.E);
	}

	public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
		return F.num(ApfloatMath.exp(new Apfloat(1, engine.getNumericPrecision())));
	}

	public double evalReal() {
		return Math.E;
	}

}
