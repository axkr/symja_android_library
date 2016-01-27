package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.linear.FieldMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Determine <code>x</code> for Matrix <code>A</code> in the equation <code>A.x==b</code>
 * 
 */
public class LinearSolve extends AbstractFunctionEvaluator {

	public LinearSolve() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		try {
			FieldMatrix<IExpr> augmentedMatrix = Convert.list2Matrix((IAST) ast.arg1(), (IAST) ast.arg2());
			return RowReduce.rowReduced2List(augmentedMatrix, engine);
		} catch (final ClassCastException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return F.NIL;
	}

}