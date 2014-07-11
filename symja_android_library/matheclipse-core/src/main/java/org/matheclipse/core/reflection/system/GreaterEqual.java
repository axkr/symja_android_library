package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>&gt;=</code> operator implementation.
 * 
 */
public class GreaterEqual extends Greater {
	public final static GreaterEqual CONST = new GreaterEqual();

	public GreaterEqual() {
	}

	@Override
	protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
		return simplifyCompare(a1, a2, F.GreaterEqual, F.LessEqual);
	}

	/** {@inheritDoc} */
	@Override
	public COMPARE_RESULT compare(final IExpr a0, final IExpr a1) {
		if (a0.equals(a1)) {
			return COMPARE_RESULT.TRUE;
		}
		return super.compare(a0,a1);
	}

}
