package org.matheclipse.core.eval.interfaces;

import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.FEConfig;

public abstract class AbstractMatrix1Expr extends AbstractFunctionEvaluator {

	public AbstractMatrix1Expr() {
	}

	/**
	 * Check if <code>arg1</code> is a matrix.
	 * 
	 * @param arg1
	 * @return
	 */
	public int[] checkMatrixDimensions(IExpr arg1) {
		return arg1.isMatrix();
	}

	@Override
	public IExpr evaluate(final IAST ast, final EvalEngine engine) {
		FieldMatrix<IExpr> matrix;
		try {

			int[] dim = checkMatrixDimensions(ast.arg1());
			if (dim != null) {
				final IAST list = (IAST) ast.arg1();
				matrix = Convert.list2Matrix(list);
				if (matrix != null) {
					return matrixEval(matrix);
				}
			}

			// } catch (final ClassCastException e) {
			// if (Config.SHOW_STACKTRACE) {
			// e.printStackTrace();
			// }
			// } catch (final IndexOutOfBoundsException e) {
			// if (Config.SHOW_STACKTRACE) {
			// e.printStackTrace();
			// }
		} catch (final MathRuntimeException mre) {
			// org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 != 3
			return engine.printMessage(ast.topHead(), mre);
		} catch (final RuntimeException e) {
			if (FEConfig.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
			return engine.printMessage(ast.topHead() + ": " + e.getMessage());
		}

		return F.NIL;
	}

	@Override
	public int[] expectedArgSize() {
		return IOFunctions.ARGS_1_1;
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
	public IExpr numericEval(final IAST ast, final EvalEngine engine) {
		RealMatrix matrix;
		IExpr arg1 = ast.arg1();
		int[] dim = checkMatrixDimensions(arg1);
		if (dim != null && arg1.isList()) {
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
			} catch (final MathRuntimeException mre) {
				// org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 != 3
				return engine.printMessage(ast.topHead(), mre);
			} catch (final RuntimeException e) {
				if (FEConfig.SHOW_STACKTRACE) {
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