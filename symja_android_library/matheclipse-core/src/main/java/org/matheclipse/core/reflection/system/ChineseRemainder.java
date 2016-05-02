package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * See <a href="https://rosettacode.org/wiki/Chinese_remainder_theorem">Rosetta
 * Code: Chinese remainder theorem</a>
 *
 */
public class ChineseRemainder extends AbstractFunctionEvaluator {
	private static long chineseRemainder(long[] n, long[] a) {
		long prod = 1;
		for (int k = 0; k < n.length; k++) {
			prod = prod * n[k];
		}

		long p, sm = 0;
		for (int i = 0; i < n.length; i++) {
			p = prod / n[i];
			sm += a[i] * mulInv(p, n[i]) * p;
		}
		return sm % prod;
	}

	private static long mulInv(long a, long b) {
		long b0 = b;
		long x0 = 0;
		long x1 = 1;

		if (b == 1)
			return 1;

		while (a > 1) {
			long q = a / b;
			long amb = a % b;
			a = b;
			b = amb;
			long xqx = x1 - q * x0;
			x1 = x0;
			x0 = xqx;
		}

		if (x1 < 0)
			x1 += b0;

		return x1;
	}

	public ChineseRemainder() {
	}

	/**
	 * <p>
	 * Calculate the chinese remainder of 2 integer lists.
	 * </p>
	 * <p>
	 * See
	 * <a href="https://rosettacode.org/wiki/Chinese_remainder_theorem">Rosetta
	 * Code: Chinese remainder theorem</a>
	 * </p>
	 *
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		long[] a = Validate.checkListOfLongs(ast.arg1(), 0L);
		long[] n = Validate.checkListOfLongs(ast.arg2(), 0L);
		if (a.length != n.length) {
			return F.NIL;
		}
		try {
			return F.integer(chineseRemainder(n, a));
		} catch (ArithmeticException ae) {
			// mulInv may throw a division by zero ArithmeticException 
			return F.NIL;
		}
	}

}
