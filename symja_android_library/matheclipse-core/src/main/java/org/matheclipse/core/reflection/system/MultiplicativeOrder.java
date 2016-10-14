package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * See <a href="https://en.wikipedia.org/wiki/Multiplicative_order">Wikipedia:
 * Multiplicative order</a> and
 * <a href="https://rosettacode.org/wiki/Multiplicative_order">Rosettacode.org:
 * Multiplicative order</a>.
 *
 */
public class MultiplicativeOrder extends AbstractFunctionEvaluator {

	public MultiplicativeOrder() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isInteger() && ast.arg2().isInteger()) {
			try {
				IInteger k = ast.getInt(1);
				IInteger n = ast.getInt(2);
				if (n.isNegative()) {
					return F.NIL;
				}

				if (!k.gcd(n).isOne()) {
					return F.NIL;
				}

				IAST primeExponentList = n.factorInteger();
				IInteger res = F.C1;
				for (int i = 1; i < primeExponentList.size(); i++) {
					res = res.lcm(multiplicativeOrder(k, primeExponentList.getAST(i).getInt(1),
							primeExponentList.getAST(i).getInt(2).toLong()));
				}
				return res;
			} catch (ArithmeticException ae) {

			}

		}
		return F.NIL;
	}

	private static IInteger multiplicativeOrder(IInteger a, IInteger prime, long exponent) {
		IInteger m = prime.pow(exponent);
		IInteger t = m.div(prime).multiply(prime.subtract(F.C1));
		IAST divisors = t.divisors();
		int len = divisors.size();
		for (int i = 1; i < len; i++) {
			IInteger factor = divisors.getInt(i);
			if (a.modPow(factor, m).isOne()) {
				return factor;
			}
		}
		return F.C0;
	}
}
