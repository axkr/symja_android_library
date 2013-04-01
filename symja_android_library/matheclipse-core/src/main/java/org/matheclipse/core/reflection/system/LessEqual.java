package org.matheclipse.core.reflection.system;

import org.matheclipse.core.interfaces.IExpr;

public class LessEqual extends Greater {
	public final static LessEqual CONST = new LessEqual();
	public LessEqual() {
	}

	@Override
	public COMPARE_RESULT compare(final IExpr a0, final IExpr a1) {
		// don't compare strings
		if (a0.equals(a1)) {
			return COMPARE_RESULT.TRUE;
		}
		if (a0.isSignedNumber() && a1.isSignedNumber()) {
			if (a0.isLTOrdered(a1)) {
				return COMPARE_RESULT.TRUE;
			}
			return COMPARE_RESULT.FALSE;
		}
		return COMPARE_RESULT.UNDEFINED;
	}
}
