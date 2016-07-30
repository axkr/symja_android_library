package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.stat.StatUtils;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compute the variance for a list of elements
 */
public class Variance extends AbstractFunctionEvaluator {

	public Variance() {
		// empty default constructor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		int dim = arg1.isVector();
		if (dim >= 0) {
			if (arg1.isRealVector()) {
				return F.num(StatUtils.variance(arg1.toDoubleVector()));
			}
			return F.NIL;
		}
		int[] matrixDimensions = arg1.isMatrix();
		if (matrixDimensions != null) {

			if (arg1.isRealMatrix()) {
				double[][] matrix = arg1.toDoubleMatrix();
				matrix = Convert.toDoubleTransposed(matrix);
				double[] result = new double[matrixDimensions[1]];
				for (int i = 0; i < matrix.length; i++) {
					result[i] = StatUtils.variance(matrix[i]);
				}
				return new ASTRealVector(result, false);
			}
			return F.NIL;
		}
		return F.NIL;
	}

}
