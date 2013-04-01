package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryIndexFunctionDiagonal;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.generic.nested.IndexTableGenerator;

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
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		if (ast.get(1).isInteger()) {
			int indx = Validate.checkIntType(ast, 1);
			final IExpr[] valueArray = { F.C0, F.C1 };
			return diagonalMatrix(valueArray, indx);
		}
		return null;
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
		final IndexTableGenerator<IExpr, IAST> generator = new IndexTableGenerator<IExpr, IAST>(
				indexArray, resultList, new UnaryIndexFunctionDiagonal(
						valueArray), AST.COPY);
		final IAST matrix = (IAST) generator.table();
		if (matrix != null) {
			matrix.addEvalFlags(IAST.IS_MATRIX);
		}
		return matrix;
	}
}
