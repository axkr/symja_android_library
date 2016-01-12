package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.linear.FieldMatrix;
import org.matheclipse.commons.math.linear.FieldReducedRowEchelonForm;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compute the null space of a matrix.
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Kernel_%28linear_algebra%29">Wikipedia - Kernel (linear algebra)</a>. <a
 * href="http://en.wikibooks.org/wiki/Linear_Algebra/Null_Spaces">Wikibooks - Null Spaces</a>
 */
public class NullSpace extends AbstractFunctionEvaluator {

	public NullSpace() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		FieldMatrix<IExpr> matrix;
		try {
			Validate.checkSize(ast, 2);

			final IAST list = (IAST) ast.arg1();
			matrix = Convert.list2Matrix(list);
			FieldReducedRowEchelonForm fmw = new FieldReducedRowEchelonForm(matrix);
			FieldMatrix<IExpr> nullspace = fmw.getNullSpace(F.CN1);
			if (nullspace == null) {
				return F.List();
			}
			return Convert.matrix2List(nullspace);

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

}