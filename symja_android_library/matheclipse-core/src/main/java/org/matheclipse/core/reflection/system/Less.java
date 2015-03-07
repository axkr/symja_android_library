package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>&lt;</code> operator implementation.
 * 
 */
public class Less extends Greater {
	public final static Less CONST = new Less();

	public Less() {
	}

	/** {@inheritDoc} */
	@Override
	protected IExpr checkAssumptions(IExpr arg1, IExpr arg2) {
		if (arg2.isNegative()) {
			// arg1 < "negative number"
			if (arg1.isPositiveResult()) {
				return F.False;
			}

		} else if (arg2.isZero()) {
			// arg1 < 0
			if (arg1.isNegativeResult()) {
				return F.True;
			}
			if (arg1.isPositiveResult()) {
				return F.False;
			}
		} else {
			// arg1 < "positive number"
			if (arg1.isNegativeResult()) {
				return F.True;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected IAST simplifyCompare(IExpr a1, IExpr a2) {
		return simplifyCompare(a1, a2, F.Less, F.Greater);
	}

	/** {@inheritDoc} */
	@Override
	public COMPARE_RESULT compare(final IExpr a0, final IExpr a1) {
		// swap arguments
		return super.compare(a1, a0);
	}

}
