package org.matheclipse.core.reflection.system;

import org.apache.commons.math3.fraction.BigFraction;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Harmonic number of a given integer value
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Harmonic_number">Harmonic number</a>
 */
public class HarmonicNumber implements IFunctionEvaluator {

	public HarmonicNumber() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
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

			return F.fraction(harmonicNumber(n));
		}

		return null;
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	/**
	 * The Harmonic number at the index specified
	 * 
	 * @param n
	 *            the index, non-negative.
	 * @return the H_1=1 for n=1, H_2=3/2 for n=2 etc. For values of n less than 1, zero is returned.
	 */
	public BigFraction harmonicNumber(int n) {
		if (n < 1)
			return (new BigFraction(0, 1));
		else {
			/*
			 * start with 1 as the result
			 */
			BigFraction a = new BigFraction(1, 1);

			/*
			 * add 1/i for i=2..n
			 */
			for (int i = 2; i <= n; i++)
				a = a.add(new BigFraction(1, i));
			return a;
		}
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
