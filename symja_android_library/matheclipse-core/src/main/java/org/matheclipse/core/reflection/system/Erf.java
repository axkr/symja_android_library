package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Erf;
import static org.matheclipse.core.expression.F.Times;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ComplexNum;
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
	public IExpr numericEvalD1(final Num arg1) {
		try {
			return Num.valueOf(org.apache.commons.math3.special.Erf.erf(arg1.getRealPart()));
		} catch (final MaxCountExceededException e) {
		}
		return null;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isZero()) {
			return F.C0;
		}
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Times(CN1, Erf(Times(CN1, arg1)));
		}
		return null;
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return null;
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		try {
			return org.apache.commons.math3.special.Erf.erf(stack[top]);
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
