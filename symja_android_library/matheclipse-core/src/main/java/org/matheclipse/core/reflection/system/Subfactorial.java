package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Returns the subfactorial of a positive integer n
 * 
 * See <a href="http://en.wikipedia.org/wiki/Derangement">Wikipedia - Derangement</a>
 * 
 */
public class Subfactorial extends AbstractTrigArg1 {

	public Subfactorial() {
	}

	private static BigInteger subFactorial(final long n) {
		if (0L <= n && n <= 2L) {
			return n != 1L ? BigInteger.ONE : BigInteger.ZERO;
		}
		// subFactorial(n) = n*subFactorial(n-1) + (-1)^n.
		long stub=1L;
		if ((n & 1L) == 1L) {
			// odd n
			stub = -1L;
		}
		return BigInteger.valueOf(n).multiply(subFactorial(n - 1)).add(BigInteger.valueOf(stub));
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger() && arg1.isPositive()) {
			try {
				long n = NumberUtil.toLong(((IInteger) arg1).getBigNumerator());
				BigInteger fac = subFactorial(n);
				return F.integer(fac);
			} catch (ArithmeticException ae) {
				//
			}

		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}
