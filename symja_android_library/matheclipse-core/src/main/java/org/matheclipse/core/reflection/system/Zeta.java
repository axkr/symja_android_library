package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractArg12;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Zeta extends AbstractArg12 {

	public Zeta() {
	}

	@Override
	public IExpr e1ObjArg(final IExpr arg1) {
		if (arg1.equals(F.C2)) {
			return F.Divide(F.Sqr(F.Pi), F.C6);
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
