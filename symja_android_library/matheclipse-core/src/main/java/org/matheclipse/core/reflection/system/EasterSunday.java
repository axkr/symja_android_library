package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class EasterSunday extends AbstractFunctionEvaluator {

	public EasterSunday() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		try {
			IExpr arg1 = ast.arg1();
			ISignedNumber signedNumber = arg1.evalReal();
			if (signedNumber != null) {
				int y = signedNumber.toInt();
				// "Anonymous Gregorian algorithm", see
				// https://en.wikipedia.org/wiki/Computus
				int a = y % 19;
				int b = y / 100;
				int c = y % 100;
				int d = b / 4;
				int e = b % 4;
				int f = (b + 8) / 25;
				int g = (b - f + 1) / 3;
				int h = (19 * a + b - d - g + 15) % 30;
				int i = c / 4;
				int k = c % 4;
				int l = (32 + 2 * e + 2 * i - h - k) % 7;
				int m = (a + 11 * h + 22 * l) / 451;
				int month = (h + l - 7 * m + 114) / 31;
				int day = ((h + l - 7 * m + 114) % 31) + 1;

				return F.List(F.integer(y), F.integer(month), F.integer(day));
			}
		} catch (ArithmeticException ae) {
			// toInt() method may throw ArithmeticException
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
