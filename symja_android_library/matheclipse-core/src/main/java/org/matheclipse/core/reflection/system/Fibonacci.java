package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

public class Fibonacci extends AbstractTrigArg1 {

	public Fibonacci() {
	}

	public static IInteger fibonacci(final IInteger iArg) {
		IInteger a = F.C1;
		IInteger b = F.C0;
		IInteger c = F.C1;
		IInteger d = F.C0;
		IInteger result = F.C0;
		final IInteger c2 = F.C2;
		IInteger temp = iArg;

		while (!temp.isZero()) {
			if (temp.isOdd()) {
				d = result.multiply(c);
				result = a.multiply(c).add(result.multiply(b).add(d));
				a = a.multiply(b).add(d);
			}

			d = c.multiply(c);
			c = b.multiply(c).multiply(c2).add(d);
			b = b.multiply(b).add(d);
			temp = temp.shiftRight(1);
		}

		return result;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			return fibonacci((IInteger) arg1);
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
