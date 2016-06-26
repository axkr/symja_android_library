package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IIndexFunction;
import org.matheclipse.core.eval.util.IndexTableGenerator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * Hilbert matrix, defined by A<sub>i,j</sub> = 1 / (i+j-1). See
 * <a> href="http://en.wikipedia.org/wiki/Hilbert_matrix">Wikipedia:Hilbert
 * matrix</a>
 */
public class HilbertMatrix extends AbstractFunctionEvaluator {

	public static class HilbertFunctionDiagonal implements IIndexFunction<IExpr> {

		public HilbertFunctionDiagonal() {
		}

		@Override
		public IRational evaluate(final int[] index) {
			return F.fraction(1, 1l + index[0] + index[1]);
		}
	}

	public HilbertMatrix() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		int rowSize = 0;
		int columnSize = 0;
		if (ast.isAST1() && ast.arg1().isInteger()) {
			rowSize = Validate.checkIntType(ast, 1);
			columnSize = rowSize;
		} else if (ast.isAST2() && ast.arg1().isInteger() && ast.arg2().isInteger()) {
			rowSize = Validate.checkIntType(ast, 1);
			columnSize = Validate.checkIntType(ast, 2);
		} else {
			return F.NIL;
		}

		final IAST resultList = F.List();
		final int[] indexArray = new int[2];
		indexArray[0] = rowSize;
		indexArray[1] = columnSize;
		final IndexTableGenerator generator = new IndexTableGenerator(indexArray, resultList,
				new HilbertFunctionDiagonal());
		final IAST matrix = (IAST) generator.table();
		matrix.addEvalFlags(IAST.IS_MATRIX);
		return matrix;
	}
}
