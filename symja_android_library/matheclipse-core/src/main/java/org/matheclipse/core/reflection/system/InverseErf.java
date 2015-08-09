package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.InverseErf;
import static org.matheclipse.core.expression.F.Negate;

import org.apache.commons.math4.exception.MaxCountExceededException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Returns the inverse erf.
 * 
 * @see org.matheclipse.core.reflection.system.Erf
 */
public class InverseErf extends AbstractTrigArg1 implements INumeric {
	public InverseErf() {
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		try {
			return Num.valueOf(org.apache.commons.math4.special.Erf.erfInv(arg1));
		} catch (final MaxCountExceededException e) {
		}
		return null;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isZero()) {
			return F.C0;
		}
		if (arg1.isOne()) {
			return F.CInfinity;
		}
		if (arg1.isMinusOne()) {
			return F.CNInfinity;
		}
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr != null) {
			return Negate(InverseErf(negExpr));
		}
		return null;
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		try {
			return org.apache.commons.math4.special.Erf.erfInv(stack[top]);
		} catch (final MaxCountExceededException e) {
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
