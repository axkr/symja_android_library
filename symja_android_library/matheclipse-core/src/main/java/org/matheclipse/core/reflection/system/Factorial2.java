package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

public class Factorial2 extends AbstractTrigArg1 {

	public Factorial2() {
	}

	public IInteger factorial(final IInteger iArg) {
		BigInteger result = BigInteger.ONE;
		final BigInteger biggi = iArg.getBigNumerator();
		BigInteger start;
		if (biggi.compareTo(BigInteger.ZERO) == -1) {
			result = BigInteger.valueOf(-1);
			if (NumberUtil.isOdd(biggi)) {
				start = BigInteger.valueOf(-3);
			} else {
				start = BigInteger.valueOf(-2);
			}
			for (BigInteger i = start; i.compareTo(biggi) >= 0; i = i.add(BigInteger.valueOf(-2))) {
				result = result.multiply(i);
			}
		} else {
			if (NumberUtil.isOdd(biggi)) {
				start = BigInteger.valueOf(3);
			} else {
				start = BigInteger.valueOf(2);
			}
			for (BigInteger i = start; i.compareTo(biggi) <= 0; i = i.add(BigInteger.valueOf(2))) {
				result = result.multiply(i);
			}
		}

		final IInteger i = F.integer(result);

		return i;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			return factorial((IInteger) arg1);
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
