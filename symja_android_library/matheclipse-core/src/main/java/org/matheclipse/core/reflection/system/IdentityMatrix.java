package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IndexFunctionDiagonal;
import org.matheclipse.core.eval.util.IndexTableGenerator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Create an identity matrix
 * 
 * See <a href="http://en.wikipedia.org/wiki/Identity_matrix">Wikipedia -
 * Identity matrix</a>
 */
public class IdentityMatrix extends AbstractFunctionEvaluator {

	public IdentityMatrix() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isInteger()) {
			int indx = Validate.checkIntType(ast, 1);
			final IExpr[] valueArray = { F.C0, F.C1 };
			return diagonalMatrix(valueArray, indx);
		}
		return F.NIL;
	}

	/**
	 * Create a diagonal matrix from <code>valueArray[0]</code> (non-diagonal
	 * elements) and <code>valueArray[1]</code> (diagonal elements).
	 * 
	 * @param valueArray
	 *            2 values for non-diagonal and diagonal elemnets of the matrix.
	 * @param dimension
	 *            of the square matrix
	 * 
	 * @return
	 */
	public static IAST diagonalMatrix(final IExpr[] valueArray, int dimension) {
		final IAST resultList = F.List();
		final int[] indexArray = new int[2];
		indexArray[0] = dimension;
		indexArray[1] = dimension;
		final IndexTableGenerator generator = new IndexTableGenerator(indexArray, resultList,
				new IndexFunctionDiagonal(valueArray));
		final IAST matrix = (IAST) generator.table();
		matrix.addEvalFlags(IAST.IS_MATRIX);
		return matrix;
	}
}
