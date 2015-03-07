package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>&gt;=</code> operator implementation.
 * 
 */
public class GreaterEqual extends Greater {
	public final static GreaterEqual CONST = new GreaterEqual();

	public GreaterEqual() {
	}

	/** {@inheritDoc} */
	@Override
	protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
		if (arg2.isNegative()) {
			// arg1 >= "negative number"
			if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
				return F.True;
			}
		} else if (arg2.isZero()) {
			// arg1 >= 0
			if (arg1.isNonNegativeResult() || arg1.isPositiveResult()) {
				return F.True;
			}
			if (arg1.isNegativeResult()) {
				return F.False;
			}
		} else {
			// arg1 >= "positive number" > 0
			if (arg1.isNegativeResult()) {
				return F.False;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected IAST simplifyCompare(IExpr a1, IExpr a2) {
		return simplifyCompare(a1, a2, F.GreaterEqual, F.LessEqual);
	}

	/** {@inheritDoc} */
	@Override
	public COMPARE_RESULT compare(final IExpr a0, final IExpr a1) {
		if (a0.equals(a1)) {
			return COMPARE_RESULT.TRUE;
		}
		return super.compare(a0, a1);
	}

}
