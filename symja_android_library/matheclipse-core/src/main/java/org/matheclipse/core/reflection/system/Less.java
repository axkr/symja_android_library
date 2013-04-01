package org.matheclipse.core.reflection.system;

import org.matheclipse.core.interfaces.IExpr;

public class Less extends Greater {
	public final static Less CONST = new Less();

	public Less() {
	}

	@Override
	public COMPARE_RESULT compare(final IExpr a0, final IExpr a1) {
		// don't compare strings
		if (a0.isSignedNumber() && a1.isSignedNumber()) {
			if (a0.isLTOrdered(a1)) {
				return COMPARE_RESULT.TRUE;
			}
			return COMPARE_RESULT.FALSE;
		}
		return COMPARE_RESULT.UNDEFINED;
	}

}
