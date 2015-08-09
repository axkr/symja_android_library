package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NonMonotonicSequenceException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <b>Newton polynomial interpolation</b> , is the interpolation polynomial for a given set of data points in the Newton form. The
 * Newton polynomial is sometimes called Newton's divided differences interpolation polynomial because the coefficients of the
 * polynomial are calculated using divided differences.
 * <p>
 *  
 * See: <a href="http://en.wikipedia.org/wiki/Polynomial_interpolation">Wikipedia - Polynomial interpolation</a> and <a
 * href="http://en.wikipedia.org/wiki/Newton_polynomial">Wikipedia - Newton Polynomial</a>
 */
public class InterpolatingPolynomial implements IFunctionEvaluator {

	public InterpolatingPolynomial() {
	}

	/**
	 * Return a copy of the divided difference array.
	 * <p>
	 * The divided difference array is defined recursively by
	 * 
	 * <pre>
	 * f[x0] = f(x0)
	 * f[x0,x1,...,xk] = (f[x1,...,xk] - f[x0,...,x[k-1]]) / (xk - x0)
	 * </pre>
	 * 
	 * </p>
	 * <p>
	 * The computational complexity is O(N^2).
	 * </p>
	 * 
	 * @param x
	 *            Interpolating points array.
	 * @param y
	 *            Interpolating values array.
	 * @return a fresh copy of the divided difference array.
	 * @throws DimensionMismatchException
	 *             if the array lengths are different.
	 * @throws NumberIsTooSmallException
	 *             if the number of points is less than 2.
	 * @throws NonMonotonicSequenceException
	 *             if {@code x} is not sorted in strictly increasing order.
	 */
	protected static IExpr[] computeDividedDifference(final IExpr x[], final IExpr y[]) {
		final IExpr[] divdiff = y.clone(); // initialization

		final int n = x.length;
		final IExpr[] a = new IExpr[n];
		a[0] = divdiff[0];
		for (int i = 1; i < n; i++) {
			for (int j = 0; j < n - i; j++) {
				final IExpr denominator = F.eval(F.Subtract(x[j + i], x[j]));
				divdiff[j] = F.eval(F.Divide(F.Subtract(divdiff[j + 1], divdiff[j]), denominator));
			}
			a[i] = divdiff[0];
		}

		return a;
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isList() && ast.arg2().isSymbol()) {
			final IAST list = (IAST) ast.arg1();
			final ISymbol x = (ISymbol) ast.arg2();
			if (list.size() > 1) {
				int n = list.size() - 1;
				IExpr[] xv = new IExpr[n];
				IExpr[] yv = new IExpr[n];
				int[] dim = list.isMatrix();
				if (dim != null && dim[1] == 2) {
					if (dim[1] != 2) {
						return null;
					}
					for (int i = 0; i < n; i++) {
						IAST row = list.getAST(i + 1);
						xv[i] = row.arg1();
						yv[i] = row.arg2();
					}
				} else {
					for (int i = 0; i < n; i++) {
						xv[i] = F.integer(i + 1);
						yv[i] = list.get(i + 1);
					}
				}
				IExpr[] c = computeDividedDifference(xv, yv);

				IAST polynomial = F.Plus();
				IAST times, plus;
				IAST tempPlus = polynomial;
				polynomial.add(c[0]);// c[0]
				for (int i = 2; i < list.size(); i++) {
					times = F.Times();
					plus = F.Plus();
					times.add(plus);
					times.add(F.Subtract(x, xv[i - 2]));
					tempPlus.add(times);
					tempPlus = plus;
					tempPlus.add(c[i - 1]);
				}
				return polynomial;
			}
		}
		return null;
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
