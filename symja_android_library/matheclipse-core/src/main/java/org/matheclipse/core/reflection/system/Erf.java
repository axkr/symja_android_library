package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.CNInfinity;
import static org.matheclipse.core.expression.F.Erf;
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
 * Returns the error function.
 * 
 * @see org.matheclipse.core.reflection.system.InverseErf
 */
public class Erf extends AbstractTrigArg1 implements INumeric {
	public Erf() {
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		try {
			return Num.valueOf(org.apache.commons.math4.special.Erf.erf(arg1));
		} catch (final MaxCountExceededException e) {
		}
		return null;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isZero()) {
			return F.C0;
		}
		if (arg1.equals(CInfinity)) {
			return F.C1;
		}
		if (arg1.equals(CNInfinity)) {
			return F.CN1;
		}
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr != null) {
			return Negate(Erf(negExpr));
		}
		return null;
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		try {
			return org.apache.commons.math4.special.Erf.erf(stack[top]);
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
