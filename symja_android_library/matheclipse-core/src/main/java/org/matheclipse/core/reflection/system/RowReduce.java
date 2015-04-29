package org.matheclipse.core.reflection.system;

import org.matheclipse.commons.math.linear.FieldMatrix;
import org.matheclipse.commons.math.linear.FieldReducedRowEchelonForm;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Reduce the matrix to row form.
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row echelon form</a>.
 */
public class RowReduce extends AbstractFunctionEvaluator {

	public RowReduce() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		FieldMatrix matrix;
		try {
			Validate.checkSize(ast, 2);

			final IAST list = (IAST) ast.arg1();
			matrix = Convert.list2Matrix(list);
			FieldReducedRowEchelonForm fmw = new FieldReducedRowEchelonForm(matrix);
			return Convert.matrix2List(fmw.getRowReducedMatrix());

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