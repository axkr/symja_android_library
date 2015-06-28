package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Transpose a matrix.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Transpose">Transpose</a>
 */
public class Transpose implements IFunctionEvaluator {

	public Transpose() {

	}

	@Override
	public IExpr evaluate(final IAST ast) {
		// TODO generalize transpose for all levels
		Validate.checkRange(ast, 2);

		final int[] dim = ast.arg1().isMatrix();
		if (dim != null) {
			final IAST originalMatrix = (IAST) ast.arg1();
			final IAST transposedMatrix = F.ast(F.List, dim[1], true);
			for (int i = 1; i <= dim[1]; i++) {
				transposedMatrix.set(i, F.ast(F.List, dim[0], true));
			}

			IAST originalRow;
			IAST transposedResultRow;
			for (int i = 1; i <= dim[0]; i++) {
				originalRow = (IAST) originalMatrix.get(i);
				for (int j = 1; j <= dim[1]; j++) {
					transposedResultRow = (IAST) transposedMatrix.get(j);
					transposedResultRow.set(i, transform(originalRow.get(j)));
				}
			}
			transposedMatrix.addEvalFlags(IAST.IS_MATRIX);
			return transposedMatrix;
		}
		return null;
	}

	protected IExpr transform(final IExpr expr) {
		return expr;
	}

	@Override
	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
