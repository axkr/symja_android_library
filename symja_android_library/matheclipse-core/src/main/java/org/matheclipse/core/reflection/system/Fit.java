package org.matheclipse.core.reflection.system;

import org.hipparchus.fitting.AbstractCurveFitter;
import org.hipparchus.fitting.PolynomialCurveFitter;
import org.hipparchus.fitting.WeightedObservedPoints;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.Expr2Object;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * Fit(list - of - points, degree, variable)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * solve a least squares problem using the Levenberg-Marquardt algorithm.
 * </p>
 * </blockquote>
 * <p>
 * See:<br />
 * </p>
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm">Wikipedia - Levenberg–Marquardt
 * algorithm</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Fit({{1,1},{2,4},{3,9},{4,16}},2,x)
 * x^2.0
 * </pre>
 */
public class Fit extends AbstractFunctionEvaluator {

	public Fit() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		// switch to numeric calculation
		return numericEval(ast, engine);
	}

	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);

		if (ast.arg2().isReal() && ast.arg3().isSymbol()) {
			int degree = ((ISignedNumber) ast.arg2()).toInt();
			double[] initialGuess = new double[degree];
			for (int i = 0; i < degree; i++) {
				initialGuess[i] = 1.0;
			}
			AbstractCurveFitter fitter = PolynomialCurveFitter.create(degree);
			int[] isMatrix = ast.arg1().isMatrix();
			WeightedObservedPoints obs = new WeightedObservedPoints();

			if (isMatrix != null && isMatrix[1] == 2) {
				final double[][] elements = Expr2Object.toDoubleMatrix((IAST) ast.arg1());
				if (elements == null) {
					return F.NIL;
				}
				for (int i = 0; i < elements.length; i++) {
					obs.add(1.0, elements[i][0], elements[i][1]);
				}
			} else {
				int rowSize = ast.arg1().isVector();
				if (rowSize < 0) {
					return F.NIL;
				}
				final double[] elements = Expr2Object.toDoubleVector((IAST) ast.arg1());
				for (int i = 0; i < elements.length; i++) {
					obs.add(1.0, i + 1, elements[i]);
				}
			}
			return Convert.polynomialFunction2Expr(fitter.fit(obs.toList()), (ISymbol) ast.arg3());
		}

		return F.NIL;
	}
}