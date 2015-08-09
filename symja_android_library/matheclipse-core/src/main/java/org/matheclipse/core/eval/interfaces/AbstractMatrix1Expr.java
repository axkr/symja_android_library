package org.matheclipse.core.eval.interfaces;

import org.apache.commons.math4.linear.RealMatrix;
import org.matheclipse.commons.math.linear.FieldMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public abstract class AbstractMatrix1Expr extends AbstractFunctionEvaluator {

	public AbstractMatrix1Expr() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		FieldMatrix matrix;
		try {

			int[] dim = ast.arg1().isMatrix();
			if (dim != null) {
				final IAST list = (IAST) ast.arg1();
				matrix = Convert.list2Matrix(list);
				if (matrix != null) {
					return matrixEval(matrix);
				}
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

		return null;
	}

	@Override
	public IExpr numericEval(final IAST ast) {
		Validate.checkSize(ast, 2);

		RealMatrix matrix;
		final IAST list = (IAST) ast.arg1();
		try {
			EvalEngine engine = EvalEngine.get();
			if (engine.isApfloat()) {
				FieldMatrix fieldMatrix = Convert.list2Matrix(list);
				if (fieldMatrix != null) {
					return matrixEval(fieldMatrix);
				}
				return null;
			}
			matrix = Convert.list2RealMatrix(list);
			return realMatrixEval(matrix);
		} catch (final WrongArgumentType e) {
			// WrongArgumentType occurs in list2RealMatrix(),
			// if the matrix elements aren't pure numerical values
			FieldMatrix fieldMatrix = Convert.list2Matrix(list);
			return matrixEval(fieldMatrix);
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public abstract IExpr matrixEval(FieldMatrix matrix);

	public abstract IExpr realMatrixEval(RealMatrix matrix);
}