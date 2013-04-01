package org.matheclipse.core.reflection.system;

import org.apache.commons.math3.optimization.fitting.PolynomialFitter;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
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
 * See <a
 * href="http://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm"
 * >Levenberg–Marquardt algorithm</a>
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

		if (ast.get(2).isInteger() && ast.get(3).isSymbol()) {
			int rowSize = -1;
			int degree = ((IInteger) ast.get(2)).toInt();
			PolynomialFitter fitter = new PolynomialFitter(degree, new LevenbergMarquardtOptimizer());
			int[] im = ast.get(1).isMatrix();
			if (im != null && im[1] == 2) {
				IAST matrix = (IAST) ast.get(1);
				IAST row;
				for (int i = 1; i < matrix.size(); i++) {
					row = matrix.getAST(i);
					fitter.addObservedPoint(1.0, ((ISignedNumber) row.get(1)).doubleValue(), ((ISignedNumber) row.get(2)).doubleValue());
				}
			} else {
				rowSize = ast.get(1).isVector();
				if (rowSize < 0) {
					return null;
				}
				IAST vector = (IAST) ast.get(1);
				for (int i = 1; i < vector.size(); i++) {
					fitter.addObservedPoint(1.0, i, ((ISignedNumber) vector.get(i)).doubleValue());
				}
			}
			return Convert.polynomialFunction2Expr(fitter.fit(), (ISymbol) ast.get(3));
		}

		return null;
	}
}