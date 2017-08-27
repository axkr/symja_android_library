package org.matheclipse.core.eval.interfaces;

import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public abstract class AbstractMatrix1Matrix extends AbstractFunctionEvaluator {

	public AbstractMatrix1Matrix() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		FieldMatrix<IExpr> matrix;
		Validate.checkSize(ast, 2);

		boolean togetherMode = engine.isTogetherMode();
		try {
			engine.setTogetherMode(true);

			final IAST list = (IAST) ast.arg1();
			matrix = Convert.list2Matrix(list);
			if (matrix != null) {
				matrix = matrixEval(matrix);
				return Convert.matrix2List(matrix);
			}

		} catch (final ClassCastException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} finally {
			engine.setTogetherMode(togetherMode);
		}

		return F.NIL;
	}

	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		RealMatrix matrix;
		Validate.checkSize(ast, 2);

		boolean togetherMode = engine.isTogetherMode();
		try {
			engine.setTogetherMode(true);

			if (engine.isApfloat()) {
				final IAST list = (IAST) ast.arg1();
				FieldMatrix<IExpr> fieldMatrix = Convert.list2Matrix(list);
				if (fieldMatrix == null) {
					return F.NIL;
				}
				fieldMatrix = matrixEval(fieldMatrix);
				return Convert.matrix2List(fieldMatrix);
			}
			final IAST list = (IAST) ast.arg1();
			matrix = list.toRealMatrix();
			if (matrix != null) {
				matrix = realMatrixEval(matrix);
				return Convert.realMatrix2List(matrix);
			}
			return F.NIL;
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} finally {
			engine.setTogetherMode(togetherMode);
		}
		return evaluate(ast, engine);
	}

	/**
	 * Evaluate the symbolic matrix for this algorithm.
	 * 
	 * @param matrix
	 *            the matrix which contains symbolic values
	 * @return
	 */
	public abstract FieldMatrix<IExpr> matrixEval(FieldMatrix<IExpr> matrix);

	/**
	 * Evaluate the numeric matrix for this algorithm.
	 * 
	 * @param matrix
	 *            the matrix which contains numeric values
	 * @return
	 */
	public abstract RealMatrix realMatrixEval(RealMatrix matrix);
}