package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class Log10 extends AbstractArg1 implements INumeric {
	public Log10() {
	}

	@Override
	public IExpr e1ObjArg(final IExpr o) {
		return F.Divide(F.Log(o), F.Log(F.C10));
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.log10(stack[top]);
	}
}
