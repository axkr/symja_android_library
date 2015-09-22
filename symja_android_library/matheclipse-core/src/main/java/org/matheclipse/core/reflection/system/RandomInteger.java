package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.Random;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * 
 *
 */
public class RandomInteger extends AbstractFunctionEvaluator {
	private final static Random RANDOM = new Random();

	public RandomInteger() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isInteger()) {
			// RandomInteger[100] gives an integer between 0 and 100
			BigInteger n = ((IInteger) ast.arg1()).getBigNumerator();
			BigInteger r;
			do {
				r = new BigInteger(n.bitLength(), RANDOM);
			} while (r.compareTo(n) >= 0);
			return F.integer(r);
		}

		return null;
	}

}
