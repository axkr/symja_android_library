package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class Log2 extends AbstractArg1 implements INumeric {
	public Log2() {
	}

	@Override
	public IExpr e1ObjArg(final IExpr o) {
		return F.Log(F.C2,o); 
	}

	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.log(stack[top] / 2.0d);
	}
}
