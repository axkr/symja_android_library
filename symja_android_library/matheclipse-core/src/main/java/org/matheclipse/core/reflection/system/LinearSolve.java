package org.matheclipse.core.reflection.system;

import org.matheclipse.commons.math.linear.FieldDecompositionSolver;
import org.matheclipse.commons.math.linear.FieldLUDecomposition;
import org.matheclipse.commons.math.linear.FieldMatrix;
import org.matheclipse.commons.math.linear.FieldVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
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
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		FieldMatrix aMatrix;
		FieldVector bVector;
		try {

			aMatrix = Convert.list2Matrix((IAST) ast.arg1());
			bVector = Convert.list2Vector((IAST) ast.arg2());
			final FieldLUDecomposition lu = new FieldLUDecomposition(aMatrix);

			FieldDecompositionSolver fds = lu.getSolver();
			FieldVector xVector = fds.solve(bVector);
			return F.eval(F.Together(Convert.vector2List(xVector)));

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
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

}