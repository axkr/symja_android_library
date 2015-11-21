package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class Quotient extends AbstractArg2 {
	public Quotient() {
	}

	@Override
	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		return ((AbstractIntegerSym)i0).quotient((AbstractIntegerSym)i1);
	}

}
