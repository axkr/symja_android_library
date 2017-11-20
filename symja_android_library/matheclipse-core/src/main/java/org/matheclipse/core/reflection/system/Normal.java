package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
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
		if (arg1.isAST()) {
			IAST seriesData = (IAST) arg1;
			if (seriesData.isAST(F.SeriesData) && (seriesData.size() == 7)) {
				if (seriesData.arg3().isList()) {
					try {
						IExpr x = seriesData.arg1();
						IExpr x0 = seriesData.arg2();
						IAST list = (IAST) seriesData.arg3();
						long nmin = ((IInteger) seriesData.arg4()).toLong();
						long nmax = ((IInteger) seriesData.arg5()).toLong();
						long den = ((IInteger) seriesData.get(6)).toLong();
						int size = list.size();

						IASTAppendable result = F.PlusAlloc(size);
						list.forEach((expr, i) -> {
							INumber exp = F.fraction(nmin + i - 1L, den).normalize();
							IExpr pow = x.subtract(x0).power(exp);
							result.append(F.Times(expr, pow));
						}); 
						return result;
					} catch (ArithmeticException ex) {
					}
				}
			}
		}
		return F.NIL;
	}

}
