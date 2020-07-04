package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LastCalculationsHistory;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Get the specified history line from the <code>EvalEngine's</code> history list. <br />
 * <b>Note</b> that the history maybe disabled in the <code>EvalEngine</code>.
 */
public class Out extends AbstractFunctionEvaluator {

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST0()) {
			final LastCalculationsHistory list = engine.getOutList();
			if (list != null) {
				return list.entry(-1);
			}
			return F.NIL;
		}

		final int position = ast.arg1().toIntDefault(0);
		if (position != 0) {
			final LastCalculationsHistory list = engine.getOutList();
			if (list != null) {
				return list.entry(position);
			}
		}
		return F.NIL;
	}

	public int[] expectedArgSize(IAST ast) {
		return IOFunctions.ARGS_0_1;
	}
}