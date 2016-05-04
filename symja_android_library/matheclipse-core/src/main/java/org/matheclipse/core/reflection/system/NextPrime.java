package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Get the next prime number. See: <a
 * href="http://en.wikipedia.org/wiki/Prime_number">Wikipedia:Prime number</a>
 * 
 * @see org.matheclipse.core.builtin.function.PrimeQ
 */
public class NextPrime extends AbstractFunctionEvaluator {

	public NextPrime() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		if (ast.size() == 2 && ast.arg1().isInteger()) {

			BigInteger primeBase = ((IInteger) ast.arg1()).getBigNumerator();
			return F.integer(primeBase.nextProbablePrime());

		} else if (ast.size() == 3 && ast.arg1().isInteger() && ast.arg2().isInteger()) {

			BigInteger primeBase = ((IInteger) ast.arg1()).getBigNumerator();
			final int n = Validate.checkIntType(ast, 2, 1);
			BigInteger temp = primeBase;
			for (int i = 0; i < n; i++) {
				temp = temp.nextProbablePrime();
			}
			return F.integer(temp);

		}
		return F.NIL;
	}

}
