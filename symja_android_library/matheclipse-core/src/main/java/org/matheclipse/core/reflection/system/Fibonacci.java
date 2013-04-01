package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

public class Fibonacci extends AbstractTrigArg1 {

	public Fibonacci() {
	}

	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return null;
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return null;
	}

	public static IInteger fibonacci(final IInteger iArg) {
		BigInteger a = BigInteger.ONE;
		BigInteger b = BigInteger.ZERO;
		BigInteger c = BigInteger.ONE;
		BigInteger d = BigInteger.ZERO;
		BigInteger f = BigInteger.ZERO;
		final BigInteger c2 = BigInteger.valueOf(2);
		BigInteger temp = iArg.getBigNumerator();

		while (!NumberUtil.isZero(temp)) {
			if (NumberUtil.isOdd(temp)) {
				d = f.multiply(c);
				f = a.multiply(c).add(f.multiply(b).add(d));
				a = a.multiply(b).add(d);
			}

			d = c.multiply(c);
			c = b.multiply(c).multiply(c2).add(d);
			b = b.multiply(b).add(d);
			temp = temp.shiftRight(1);
		}

		final IInteger i = F.integer(f);

		return i;

	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			return fibonacci((IInteger) arg1);
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
