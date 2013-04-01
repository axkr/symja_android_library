package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Harmonic number of a given integer value
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Harmonic_number">Harmonic
 * number</a>
 */
public class HarmonicNumber implements IFunctionEvaluator {

	public HarmonicNumber() {
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.size() != 2) {
			throw new WrongNumberOfArguments(ast, 1, ast.size() - 1);
		}
		IExpr arg1 = ast.get(1);
		if (arg1.isInteger()) {

			int n = Validate.checkIntType(ast, 1, Integer.MIN_VALUE);
			if (n < 0) {
				return null;
			}
			if (n == 0) {
				return F.C0;
			}
			if (n == 1) {
				return F.C1;
			}
			n--;
			BigFraction sum = BigFraction.ONE;
			BigInteger counter = BigInteger.ONE;
			for (int i = 0; i < n; i++) {
				counter = counter.add(BigInteger.ONE);
				sum = sum.add(new BigFraction(BigInteger.ONE, counter));
			}
			return F.fraction(sum);

		}

		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(ISymbol symbol) {

	}

}
