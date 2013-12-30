package org.matheclipse.core.reflection.system;

import org.apache.commons.math3.linear.FieldMatrix;
import org.matheclipse.commons.math.linear.FieldReducedRowEchelonForm;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ExprFieldElement;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

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
	public IExpr evaluate(final IAST function) {
		FieldMatrix<ExprFieldElement> matrix;
		try {
			Validate.checkSize(function, 2);

			final IAST list = (IAST) function.get(1);
			matrix = Convert.list2Matrix(list);
			FieldReducedRowEchelonForm<ExprFieldElement> fmw = new FieldReducedRowEchelonForm<ExprFieldElement>(matrix);
			return F.integer(fmw.getMatrixRank());

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
	public IExpr numericEval(final IAST function) {
		return evaluate(function);
	}
}