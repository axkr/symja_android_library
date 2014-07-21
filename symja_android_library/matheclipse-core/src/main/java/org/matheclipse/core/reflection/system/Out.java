package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LastCalculationsHistory;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Get the specified history line fro the <code>EvalEngine's</code> history list. <br />
 * <b>Note</b> that the history maybe disabled in the <code>EvalEngine</code>.
 */
public class Out extends AbstractArg1 {

	/**
	 * Get the specified history line fro the <code>EvalEngine's</code> history list. <br />
	 * <b>Note</b> that the history maybe disabled in the <code>EvalEngine</code>.
	 */
	@Override
	public IExpr e1DblArg(final double iNum) {
		try {
			// int i = iNum.toInt();
			int i = NumberUtil.toInt(iNum);
			final LastCalculationsHistory list = EvalEngine.get().getOutList();
			return list.get(i);
		} catch (final Exception ae) {

		}
		return null;
	}

	/**
	 * Get the specified history line fro the <code>EvalEngine's</code> history list. <br />
	 * <b>Note</b> that the history maybe disabled in the <code>EvalEngine</code>.
	 */
	@Override
	public IExpr e1IntArg(final IInteger ii) {
		try {
			int i = ii.toInt();
			final LastCalculationsHistory list = EvalEngine.get().getOutList();
			return list.get(i);
		} catch (final Exception ae) {

		}
		return null;
	}

}