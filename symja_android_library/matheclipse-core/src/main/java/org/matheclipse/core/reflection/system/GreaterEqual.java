package org.matheclipse.core.reflection.system;

import org.matheclipse.core.interfaces.IExpr;

public class GreaterEqual extends Greater {
	public final static GreaterEqual CONST = new GreaterEqual();
	public GreaterEqual() {
	}

	@Override
	public COMPARE_RESULT compare(final IExpr a0, final IExpr a1) {
		if (a0.equals(a1)) {
			return COMPARE_RESULT.TRUE;
		}
		// don't compare strings
		if (a0.isSignedNumber() && a1.isSignedNumber()) {
			if (a1.isLTOrdered(a0)) {
				return COMPARE_RESULT.TRUE;
			}
			return COMPARE_RESULT.FALSE;
		}
		return COMPARE_RESULT.UNDEFINED;
	}
	
}
