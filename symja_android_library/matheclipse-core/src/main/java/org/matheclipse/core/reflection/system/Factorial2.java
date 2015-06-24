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

	public static IInteger factorial2(final IInteger iArg) {
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

		return F.integer(result);
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger() && !arg1.isNegative()) {
			return factorial2((IInteger) arg1);
		}
		try {
			int n = ((IInteger) arg1).toInt();
			switch (n) {
			case -1:
				return F.C1;
			case -2:
				return F.CComplexInfinity;
			case -3:
				return F.CN1;
			case -4:
				return F.CComplexInfinity;
			case -5:
				return F.C1D3;
			case -6:
				return F.CComplexInfinity;
			case -7:
				return F.fraction(-1, 15);
			}
		} catch (ArithmeticException ae) {

		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
