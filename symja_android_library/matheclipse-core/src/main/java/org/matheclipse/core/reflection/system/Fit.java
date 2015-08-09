package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.fitting.AbstractCurveFitter;
import org.apache.commons.math4.fitting.PolynomialCurveFitter;
import org.apache.commons.math4.fitting.WeightedObservedPoints;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.Expr2Object;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Polynomial fitting of a given data point set.
 * 
 * <p>
 * Syntax: <code>Fit[ data, degree, variable ] </code>
 * </p>
 * 
 * 
 * Examples:<br/>
 * <code>Fit[{{1,1},{2,4},{3,9},{4,16}},2,x]  gives  x^2.0</code><br/>
 * <code>Fit[{1,4,9,16},2,x]  gives  x^2.0</code>
 * 
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm" >Levenberg–Marquardt algorithm</a>
 * </p>
 */
public class Fit extends AbstractFunctionEvaluator {

	public Fit() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		// switch to numeric calculation
		return numericEval(ast);
	}

	@Override
	public IExpr numericEval(final IAST ast) {
		Validate.checkSize(ast, 4);

		if (ast.arg2().isSignedNumber() && ast.arg3().isSymbol()) {
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
				for (int i = 0; i < elements.length; i++) {
					obs.add(1.0, elements[i][0], elements[i][1]);
				}
			} else {
				int rowSize = ast.arg1().isVector();
				if (rowSize < 0) {
					return null;
				}
				final double[] elements = Expr2Object.toDoubleVector((IAST) ast.arg1());
				for (int i = 0; i < elements.length; i++) {
					obs.add(1.0, i + 1, elements[i]);
				}
			}
			return Convert.polynomialFunction2Expr(fitter.fit(obs.toList()), (ISymbol) ast.arg3());
		}

		return null;
	}
}