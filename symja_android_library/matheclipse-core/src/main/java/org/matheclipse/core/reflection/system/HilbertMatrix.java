package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IIndexFunction;
import org.matheclipse.core.eval.util.IndexTableGenerator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Hilbert matrix, defined by A<sub>i,j</sub> = 1 / (i+j-1). See <a>
 * href="http://en.wikipedia.org/wiki/Hilbert_matrix">Wikipedia:Hilbert matrix</a>
 */
public class HilbertMatrix extends AbstractFunctionEvaluator {

	public class HilbertFunctionDiagonal implements IIndexFunction<IExpr> {

		public HilbertFunctionDiagonal() {
		}

		public IExpr evaluate(final int[] index) {
			int res = index[0] + index[1] + 1;

			return F.Power(F.integer(res), F.CN1);
		}
	}

	public HilbertMatrix() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		int rowSize = 0;
		int columnSize = 0;
		if (ast.size() == 2 && ast.get(1).isInteger()) {
			rowSize = Validate.checkIntType(ast, 1);
			columnSize = rowSize;
		} else if (ast.size() == 3 && ast.get(1).isInteger() && ast.get(2).isInteger()) {
			rowSize = Validate.checkIntType(ast, 1);
			columnSize = Validate.checkIntType(ast, 2);
		} else {
			return null;
		}

		final IAST resultList = F.List();
		final int[] indexArray = new int[2];
		indexArray[0] = rowSize;
		indexArray[1] = columnSize;
		final IndexTableGenerator generator = new IndexTableGenerator(indexArray, resultList, new HilbertFunctionDiagonal());
		final IAST matrix = (IAST) generator.table();
		if (matrix != null) {
			matrix.addEvalFlags(IAST.IS_MATRIX);
		}
		return matrix;
	}
}
