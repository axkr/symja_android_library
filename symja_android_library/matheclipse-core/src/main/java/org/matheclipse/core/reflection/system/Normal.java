package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTPowerSeries;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;

/**
 * Power series expansion
 */
public class Normal extends AbstractFunctionEvaluator {
	public Normal() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		if (arg1 instanceof ASTPowerSeries) {
			ASTPowerSeries seriesData = (ASTPowerSeries) arg1;

			try {
				IExpr x = seriesData.getX();
				IExpr x0 = seriesData.getX0();
				long nmin = seriesData.getNMin();
				long nmax = seriesData.getNMax();
				long den = seriesData.getDenominator();
				
				int size = seriesData.size();

				IASTAppendable result = F.PlusAlloc(size);
				seriesData.forEach((expr, i) -> {
					INumber exp = F.fraction(nmin + i - 1L, den).normalize();
					IExpr pow = x.subtract(x0).power(exp);
					result.append(F.Times(expr, pow));
				});
				return result;
			} catch (ArithmeticException ex) {
			}
		}
		return F.NIL;
	}

}
