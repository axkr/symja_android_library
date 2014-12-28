package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.EvalEngine;
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

	/**
	 * <p>
	 * Iterative subfactorial algorithm based on the recurrence: <code>Subfactorial(n) = n * Subfactorial(n-1) + (-1)^n</code>
	 * </p>
	 * See <a href="http://en.wikipedia.org/wiki/Derangement">Wikipedia - Derangement</a>
	 * 
	 * <pre>
	 * result = 1;
	 * for (long i = 3; i &lt;= n; i++) {
	 *   result = (result * i);
	 *   if (i is ODD) {
	 *     result = (result - 1);
	 *   } else {
	 *     result = (result + 1);
	 *   }
	 * }
	 * </pre>
	 * 
	 * @param n
	 * @return
	 */
	private static BigInteger subFactorial(final long n) {
		if (0L <= n && n <= 2L) {
			return n != 1L ? BigInteger.ONE : BigInteger.ZERO;
		}
		BigInteger result = BigInteger.ONE;
		boolean isOdd = true;
		for (long i = 3; i <= n; i++) {
			result = BigInteger.valueOf(i).multiply(result);
			if (isOdd) {
				// result = (result - 1)
				result = result.subtract(BigInteger.ONE);
				isOdd = false;
			} else {
				// result = (result + 1)
				result = result.add(BigInteger.ONE);
				isOdd = true;
			}
		}
		return result;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger() && arg1.isPositive()) {
			try {
				long n = NumberUtil.toLong(((IInteger) arg1).getBigNumerator());
				BigInteger fac = subFactorial(n);
				return F.integer(fac);
			} catch (ArithmeticException ae) {
				EvalEngine.get().printMessage("Subfactorial: argument n is to big.");
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
