package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Returns the subfactorial of a positive integer n
 * 
 * See <a href="http://en.wikipedia.org/wiki/Derangement">Wikipedia -
 * Derangement</a>
 * 
 */
public class Subfactorial extends AbstractTrigArg1 {

	public Subfactorial() {
	}

	/**
	 * <p>
	 * Iterative subfactorial algorithm based on the recurrence:
	 * <code>Subfactorial(n) = n * Subfactorial(n-1) + (-1)^n</code>
	 * </p>
	 * See <a href="http://en.wikipedia.org/wiki/Derangement">Wikipedia -
	 * Derangement</a>
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
	private static IInteger subFactorial(final long n) {
		if (0L <= n && n <= 2L) {
			return n != 1L ? F.C1 : F.C0;
		}
		IInteger result = F.C1;
		boolean isOdd = true;
		for (long i = 3; i <= n; i++) {
			result = AbstractIntegerSym.valueOf(i).multiply(result);
			if (isOdd) {
				// result = (result - 1)
				result = result.subtract(F.C1);
				isOdd = false;
			} else {
				// result = (result + 1)
				result = result.add(F.C1);
				isOdd = true;
			}
		}
		return result;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger() && arg1.isPositive()) {
			try {
				long n = ((IInteger) arg1).toLong();
				return subFactorial(n);
			} catch (ArithmeticException ae) {
				EvalEngine.get().printMessage("Subfactorial: argument n is to big.");
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}
