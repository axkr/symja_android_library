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

public abstract class AbstractMatrix1Expr extends AbstractFunctionEvaluator {

	public AbstractMatrix1Expr() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		FieldMatrix<IExpr> matrix;
		try {

			int[] dim = ast.arg1().isMatrix();
			if (dim != null) {
				final IAST list = (IAST) ast.arg1();
				matrix = Convert.list2Matrix(list);
				if (matrix != null) {
					return matrixEval(matrix);
				}
			}

//		} catch (final ClassCastException e) {
//			if (Config.SHOW_STACKTRACE) {
//				e.printStackTrace();
//			}
//		} catch (final IndexOutOfBoundsException e) {
//			if (Config.SHOW_STACKTRACE) {
//				e.printStackTrace();
//			}
		} catch (final RuntimeException e) {
			engine.printMessage(ast.topHead()+": "+e.getMessage());
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return F.NIL;
	}

	/**
	 * Evaluate the symbolic matrix for this algorithm.
	 * 
	 * @param matrix
	 *            the matrix which contains symbolic values
	 * @return <code>F.NIL</code> if the evaluation isn't possible
	 */
	public abstract IExpr matrixEval(FieldMatrix<IExpr> matrix);

	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		RealMatrix matrix;
		IExpr arg1 = ast.arg1();
		if (arg1.isList()) {
			final IAST list = (IAST) arg1;
			try {
				if (engine.isApfloat()) {
					FieldMatrix<IExpr> fieldMatrix = Convert.list2Matrix(list);
					if (fieldMatrix != null) {
						return matrixEval(fieldMatrix);
					}
					return F.NIL;
				}
				matrix = list.toRealMatrix();
				if (matrix != null) {
					return realMatrixEval(matrix);
				} else {
					FieldMatrix<IExpr> fieldMatrix = Convert.list2Matrix(list);
					if (fieldMatrix != null) {
						return matrixEval(fieldMatrix);
					}
				}
			} catch (final IndexOutOfBoundsException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
		}
		return F.NIL;
	}

	/**
	 * Evaluate the numeric matrix for this algorithm.
	 * 
	 * @param matrix
	 *            the matrix which contains numeric values
	 * @return <code>F.NIL</code> if the evaluation isn't possible
	 */
	public abstract IExpr realMatrixEval(RealMatrix matrix);
}