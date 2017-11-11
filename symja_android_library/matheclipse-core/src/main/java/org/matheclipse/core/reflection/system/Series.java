package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;

/**
 * Power series expansion
 */
public class Series extends AbstractFunctionEvaluator {
	public Series() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (!ToggleFeature.SERIES) {
			return F.NIL;
		}
		if (ast.isAST2() && (ast.arg2().isVector() == 3)) {

			IExpr function = ast.arg1();

			IAST list = (IAST) ast.arg2();
			IExpr x = list.arg1();
			IExpr x0 = list.arg2();

			try {
				final int lowerLimit = ((ISignedNumber) x0).toInt();
				if (lowerLimit != 0) {
					// TODO support other cases than 0
					return F.NIL;
				}
				x0 = F.integer(lowerLimit);
			} catch (ClassCastException cce) {
			} catch (ArithmeticException ae) {
			}

			final int n = Validate.checkIntType(list, 3, Integer.MIN_VALUE);
			if (n < 0) {
				return F.NIL;
			}
			if (function.isNumber()) {
				return function;
			}
			IExpr step = F.C1;
			return createSeriesData(function, x, x0, n, step);

		}
		return F.NIL;
	}

	/**
	 * 
	 * @param function
	 *            the function which should be generated as a power series
	 * @param x
	 *            the variable
	 * @param x0
	 *            the point to do the power expansion for
	 * @param n
	 *            the order of the expansion
	 * @param step
	 * @return
	 */
	private IExpr createSeriesData(final IExpr function, IExpr x, IExpr x0, final int n, IExpr step) {
		IInteger nExpr = F.integer(n);
		IASTAppendable result = F.ListAlloc(n);
		IAST seriesData = F.SeriesData(x, x0, result, F.C0, F.Plus(nExpr, step), step);
		IExpr derivedFunction = function;
		for (int i = 0; i <= n; i++) {
			result.append(
					F.Times(F.Power(F.Factorial(F.integer(i)), F.CN1), F.ReplaceAll(derivedFunction, F.Rule(x, x0))));
			derivedFunction = F.D(derivedFunction, x);
		}
		return seriesData;
	}
}
