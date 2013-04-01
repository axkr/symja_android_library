package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class Not extends AbstractArg1 {

	public Not() {
	}

	@Override
	public IExpr e1ObjArg(final IExpr o) {
		if (o.equals(F.True)) {
			return F.False;
		}

		if (o.equals(F.False)) {
			return F.True;
		}

		return null;
	}

}