package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.InverseErf;
import static org.matheclipse.core.expression.F.Negate;

import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Returns the inverse erf.
 * 
 * @see org.matheclipse.core.reflection.system.Erf
 */
public class InverseErf extends AbstractTrigArg1 implements INumeric, DoubleUnaryOperator {
	public InverseErf() {
	}

	@Override
	public double applyAsDouble(double operand) {
		return org.apache.commons.math3.special.Erf.erfInv(operand);
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		try {
			return Num.valueOf(org.apache.commons.math3.special.Erf.erfInv(arg1));
		} catch (final MaxCountExceededException e) {
		}
		return F.NIL;
	}
	
	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		try {
			return org.apache.commons.math3.special.Erf.erfInv(stack[top]);
		} catch (final MaxCountExceededException e) {
		}
		throw new UnsupportedOperationException();
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
		if (negExpr.isPresent()) {
			return Negate(InverseErf(negExpr));
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}
