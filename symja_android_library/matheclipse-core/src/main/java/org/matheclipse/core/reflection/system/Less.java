package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class Less extends Greater {
	public final static Less CONST = new Less();

	public Less() {
	}

	@Override
	protected IExpr simplifyCompare(IExpr a1, IExpr a2) {
		return simplifyCompare(a1, a2, F.Less, F.Greater);
	}

	@Override
	public COMPARE_RESULT compareGreater(final IExpr a0, final IExpr a1) {
		// swap arguments
		return super.compareGreater(a1, a0);
	}

}
