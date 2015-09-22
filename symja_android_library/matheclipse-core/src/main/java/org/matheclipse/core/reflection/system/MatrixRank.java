package org.matheclipse.core.reflection.system;

import org.matheclipse.commons.math.linear.FieldMatrix;
import org.matheclipse.commons.math.linear.FieldReducedRowEchelonForm;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Compute the rank of a matrix.
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Rank_%28linear_algebra%29">Wikipedia - Rank (linear algebra)</a>.
 */
public class MatrixRank extends AbstractFunctionEvaluator {

	public MatrixRank() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		FieldMatrix matrix;
		try {
			Validate.checkSize(ast, 2);

			IExpr arg1 = F.eval(ast.arg1());
			if (arg1.isMatrix() != null) {
				final IAST astMatrix = (IAST) arg1;
				matrix = Convert.list2Matrix(astMatrix);
				FieldReducedRowEchelonForm fmw = new FieldReducedRowEchelonForm(matrix);
				return F.integer(fmw.getMatrixRank());
			}

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
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}