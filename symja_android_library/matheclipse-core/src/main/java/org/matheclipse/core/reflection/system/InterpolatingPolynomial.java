package org.matheclipse.core.reflection.system;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * InterpolatingPolynomial(data - list, symbol)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * get the polynomial representation for the given <code>data-list</code>.
 * </p>
 * </blockquote>
 * <p>
 * Newton polynomial interpolation, is the interpolation polynomial for a given set of data points in the Newton form.
 * The Newton polynomial is sometimes called Newton's divided differences interpolation polynomial because the
 * coefficients of the polynomial are calculated using divided differences.
 * </p>
 * <p>
 * See:<br />
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Newton_polynomial">Wikipedia - Newton Polynomial</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; InterpolatingPolynomial({{1,7},{3,11},{5,27}},x)
 * (3/2*x-5/2)*(x-1)+7
 * </pre>
 */
public class InterpolatingPolynomial extends AbstractEvaluator {

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
	 * @throws MathIllegalArgumentException
	 *             if the array lengths are different.
	 * @throws MathIllegalArgumentException
	 *             if the number of points is less than 2.
	 * @throws MathIllegalArgumentException
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
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isList()) {
			final IAST list = (IAST) ast.arg1();
			IExpr arg2 =  ast.arg2();
			int size = list.size();
			if (size > 1) {
				int n = size - 1;
				IExpr[] xv = new IExpr[n];
				IExpr[] yv = new IExpr[n];
				int[] dim = list.isMatrix();
				if (dim != null && dim[1] == 2) {
					if (dim[1] != 2) {
						return F.NIL;
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

				IASTAppendable polynomial = F.PlusAlloc(16);
				IASTAppendable[] tempPlus = new IASTAppendable[1];
				tempPlus[0] = polynomial;
				polynomial.append(c[0]); 
				list.forEach(2, size, (x, i) -> {
					IASTAppendable times = F.TimesAlloc(2);
					IASTAppendable plus = F.PlusAlloc(8);
					times.append(plus);
					times.append(F.Subtract(arg2, xv[i - 2]));
					tempPlus[0].append(times);
					tempPlus[0] = plus;
					tempPlus[0].append(c[i - 1]);
				});
				return polynomial;
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
