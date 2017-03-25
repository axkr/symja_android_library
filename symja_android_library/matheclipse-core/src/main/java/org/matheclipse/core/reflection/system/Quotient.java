package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class Quotient extends AbstractArg2 {
	public Quotient() {
	}

	@Override
	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		if (i1.isZero()) {
			EvalEngine.get().printMessage("Quotient: division by zero");
			return F.NIL;
		}
		return i0.quotient(i1);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
