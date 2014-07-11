package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>&lt;=</code> operator implementation.
 * 
 */
public class LessEqual extends Greater {
	public final static LessEqual CONST = new LessEqual();

	public LessEqual() {
	}

	@Override
	protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
		return simplifyCompare(a1, a2, F.LessEqual, F.GreaterEqual);
	}

	/** {@inheritDoc} */
	@Override
	public COMPARE_RESULT compare(final IExpr a0, final IExpr a1) {
		// don't compare strings
		if (a0.equals(a1)) {
			return COMPARE_RESULT.TRUE;
		}
		// swap arguments
		return super.compare(a1, a0);
	}
}
