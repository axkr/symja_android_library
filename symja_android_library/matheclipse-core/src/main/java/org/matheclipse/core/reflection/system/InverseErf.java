package org.matheclipse.core.reflection.system;


import org.hipparchus.exception.MathIllegalStateException;
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
public class InverseErf extends AbstractTrigArg1 implements INumeric {
	public InverseErf() {
	}
 
	@Override
	public IExpr e1DblArg(final double arg1) {
		try {
			if (arg1 >= -1.0 && arg1 <= 1.0) {
				return Num.valueOf(org.hipparchus.special.Erf.erfInv(arg1));
			}
		} catch (final MathIllegalStateException e) {
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
			if (arg1 >= -1.0 && arg1 <= 1.0) {
				return org.hipparchus.special.Erf.erfInv(arg1);
			}
		} catch (final MathIllegalStateException e) {
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isSignedNumber()) {
			if (arg1.isZero()) {
				return F.C0;
			}
			if (arg1.isOne()) {
				return F.CInfinity;
			}
			if (arg1.isMinusOne()) {
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
