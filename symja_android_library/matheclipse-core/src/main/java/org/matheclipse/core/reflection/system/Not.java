package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Not extends AbstractArg1 {

	public Not() {
	}

	@Override
	public IExpr e1ObjArg(final IExpr o) {
		if (o.isTrue()) {
			return F.False;
		}
		if (o.isFalse()) {
			return F.True;
		}
		if (o.isNot()) {
			return ((IAST) o).arg1();
		}
		 
		return null;
	}

}