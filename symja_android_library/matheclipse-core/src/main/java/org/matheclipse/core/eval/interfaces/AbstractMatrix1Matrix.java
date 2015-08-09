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

public abstract class AbstractMatrix1Matrix extends AbstractFunctionEvaluator {

	public AbstractMatrix1Matrix() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		FieldMatrix matrix;
		try {
			Validate.checkSize(ast, 2);

			final IAST list = (IAST) ast.arg1();
			matrix = Convert.list2Matrix(list);
			matrix = matrixEval(matrix);
			return Convert.matrix2List(matrix);
			// return F.eval(F.Together(Convert.matrix2List(matrix)));

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
		RealMatrix matrix;
		try {
			Validate.checkSize(ast, 2);

			EvalEngine engine = EvalEngine.get();
			if (engine.isApfloat()) {
				final IAST list = (IAST) ast.arg1();
				FieldMatrix fieldMatrix = Convert.list2Matrix(list);
				fieldMatrix = matrixEval(fieldMatrix);
				return Convert.matrix2List(fieldMatrix);
			}
			final IAST list = (IAST) ast.arg1();
			matrix = Convert.list2RealMatrix(list);
			matrix = realMatrixEval(matrix);

			return Convert.realMatrix2List(matrix);
		} catch (final WrongArgumentType e) {
			// WrongArgumentType occurs in list2RealMatrix(),
			// if the matrix elements aren't pure numerical values
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return evaluate(ast);
	}

	public abstract FieldMatrix matrixEval(FieldMatrix matrix);

	public abstract RealMatrix realMatrixEval(RealMatrix matrix);
}