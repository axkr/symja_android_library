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
 * Get the specified history line fro the <code>EvalEngine's</code> history list. <br />
 * <b>Note</b> that the history maybe disabled in the <code>EvalEngine</code>.
 */
public class Out extends AbstractFunctionEvaluator {

	/**
	 * Get the specified history line fro the <code>EvalEngine's</code> history list. <br />
	 * <b>Note</b> that the history maybe disabled in the <code>EvalEngine</code>.
	 */
	public IExpr e1DblArg(final double iNum) {
		try {
			// int i = iNum.toInt();
			int i = NumberUtil.toInt(iNum);
			final LastCalculationsHistory list = EvalEngine.get().getOutList();
			if (list != null) {
				return list.entry(i);
			}
		} catch (final Exception ae) {

		}
		return F.NIL;
	}

	/**
	 * Get the specified history line from the <code>EvalEngine's</code> history list. <br />
	 * <b>Note</b> that the history maybe disabled in the <code>EvalEngine</code>.
	 */
	public IExpr e1IntArg(final IInteger ii) {
		try {
			int i = ii.toInt();
			final LastCalculationsHistory list = EvalEngine.get().getOutList();
			if (list != null) {
				return list.entry(i);
			}
		} catch (final Exception ae) {

		}
		return F.NIL;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST0()) {
			final LastCalculationsHistory list = EvalEngine.get().getOutList();
			if (list != null) {
				return list.entry(-1);
			}
		}

		final IExpr arg1 = ast.arg1();
		if (arg1.isReal()) {
			if (arg1.isInteger()) {
				return e1IntArg((IInteger) arg1);
			}
			return e1DblArg(((Num) arg1).doubleValue());
		}
		return F.NIL;
	}
	public int[] expectedArgSize() {
		return IOFunctions.ARGS_0_1;
	}
}