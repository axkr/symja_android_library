package org.matheclipse.core.reflection.system;

import org.hipparchus.linear.BlockFieldMatrix;
import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.NonNegativeIntegerExpected;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ExprField;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class MatrixPower extends AbstractFunctionEvaluator {

	public MatrixPower() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		FieldMatrix<IExpr> matrix;
		FieldMatrix<IExpr> resultMatrix;
		try {
			matrix = Convert.list2Matrix((IAST) ast.arg1());
			final int p = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
			if (p < 0) {
				return F.NIL;
			}
			if (p == 1) {
				((IAST) ast.arg1()).addEvalFlags(IAST.IS_MATRIX);
				return ast.arg1();
			}
			if (p == 0) {
				resultMatrix = new BlockFieldMatrix<IExpr>(ExprField.CONST, matrix.getRowDimension(), matrix.getColumnDimension());
				int min = matrix.getRowDimension();
				if (min > matrix.getColumnDimension()) {
					min = matrix.getColumnDimension();
				}
				for (int i = 0; i < min; i++) {
					resultMatrix.setEntry(i, i, F.C1);
				}

				return Convert.matrix2List(resultMatrix);
			}
			resultMatrix = matrix;
			for (int i = 1; i < p; i++) {
				resultMatrix = resultMatrix.multiply(matrix);
			}
			return Convert.matrix2List(resultMatrix);

		} catch (final ClassCastException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final ArithmeticException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
			throw new NonNegativeIntegerExpected(ast, 2);
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return F.NIL;
	}
}