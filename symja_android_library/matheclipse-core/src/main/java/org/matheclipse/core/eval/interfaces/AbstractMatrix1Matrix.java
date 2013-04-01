package org.matheclipse.core.eval.interfaces;

import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.ExprFieldElement;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public abstract class AbstractMatrix1Matrix extends AbstractFunctionEvaluator {

	public AbstractMatrix1Matrix() {
	}

	@Override
	public IExpr evaluate(final IAST function) {
		FieldMatrix<ExprFieldElement> matrix;
		try {
			Validate.checkSize(function, 2);

			final IAST list = (IAST) function.get(1);
			matrix = Convert.list2Matrix(list);
			matrix = matrixEval(matrix);
			return Convert.matrix2List(matrix);
//			return F.eval(F.Together(Convert.matrix2List(matrix)));

		} catch (final ClassCastException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public IExpr numericEval(final IAST function) {
		RealMatrix matrix;
		try {
			if (function.size() == 2) {
				final IAST list = (IAST) function.get(1);
				matrix = Convert.list2RealMatrix(list);
				matrix = realMatrixEval(matrix);

				return Convert.realMatrix2List(matrix);
			}
		} catch (final ClassCastException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return evaluate(function);
	}

	public abstract FieldMatrix<ExprFieldElement> matrixEval(FieldMatrix<ExprFieldElement> matrix);

	public abstract RealMatrix realMatrixEval(RealMatrix matrix);
}