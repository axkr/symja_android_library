package org.matheclipse.core.reflection.system;

import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math3.exception.MaxCountExceededException;
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
public class InverseErfc extends AbstractTrigArg1 implements INumeric {
	public InverseErfc() {
	}

//	public double applyAsDouble(double operand) {
//		return org.apache.commons.math3.special.Erf.erfcInv(operand);
//	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		if (arg1 >= 0. && arg1 <= 2.0) {
			try {
				return Num.valueOf(org.apache.commons.math3.special.Erf.erfcInv(arg1));
			} catch (final MaxCountExceededException e) {
			}
		}
		return F.NIL;
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		try {
			double arg1 = stack[top];
			if (arg1 >= 0. && arg1 <= 2.0) {
				return org.apache.commons.math3.special.Erf.erfcInv(arg1);
			}
		} catch (final MaxCountExceededException e) {
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isSignedNumber()) {
			if (arg1.isZero()) {
				return F.CInfinity;
			}
			if (arg1.isOne()) {
				return F.C0;
			}
			if (arg1.equals(F.C2)) {
				return F.CNInfinity;
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}
