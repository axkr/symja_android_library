package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Transpose a matrix.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Transpose">Transpose</a>
 */
public class Transpose extends AbstractEvaluator {

	public Transpose() {

	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		// TODO generalize transpose for all levels
		Validate.checkRange(ast, 2);

		final int[] dim = ast.arg1().isMatrix();
		if (dim != null) {
			final IAST originalMatrix = (IAST) ast.arg1();
			return transpose(originalMatrix, dim[0], dim[1]);
		}
		return F.NIL;
	}

	/**
	 * Transpose the given matrix.
	 * 
	 * @param matrix
	 *            the matrix which should be transposed
	 * @param rows
	 *            number of rows of the matrix
	 * @param cols
	 *            number of columns of the matrix
	 * @return
	 */
	public IAST transpose(final IAST matrix, int rows, int cols) {
		final IAST transposedMatrix = F.ast(F.List, cols, true);
		for (int i = 1; i <= cols; i++) {
			transposedMatrix.set(i, F.ast(F.List, rows, true));
		}

		IAST originalRow;
		IAST transposedResultRow;
		for (int i = 1; i <= rows; i++) {
			originalRow = (IAST) matrix.get(i);
			for (int j = 1; j <= cols; j++) {
				transposedResultRow = (IAST) transposedMatrix.get(j);
				transposedResultRow.set(i, transform(originalRow.get(j)));
			}
		}
		transposedMatrix.addEvalFlags(IAST.IS_MATRIX);
		return transposedMatrix;
	}

	protected IExpr transform(final IExpr expr) {
		return expr;
	}

}
