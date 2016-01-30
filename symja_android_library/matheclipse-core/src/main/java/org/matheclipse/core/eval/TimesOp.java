package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.Times;

/**
 * 
 */
public class TimesOp {

	/**
	 * Evaluate <code>Times(a1, a2,...)</code>.
	 * 
	 * @param a0
	 * @param a1
	 * @return
	 */
	public static IExpr times(IAST timesAST) {
		IAST temp = EvalEngine.get().evalFlatOrderlessAttributesRecursive(timesAST);
		if (temp == null) {
			temp = timesAST;
		}
		IExpr expr = Times.CONST.evaluate(temp, null);
		if (!expr.isPresent()) {
			return timesAST.getOneIdentity(F.C0);
		}
		return expr;
	}

	/**
	 * Evaluate <code>a0 * a2</code>.
	 * 
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static IExpr times(IExpr a1, IExpr a2) {
		IExpr expr = timesNull(a1, a2);
		if (expr == null) {
			return F.Times(a1, a2);
		}
		return expr;
	}

	public static IExpr timesNull(IExpr a1, IExpr a2) {
		IAST times = F.Times(a1, a2);
		IExpr temp = Times.CONST.evaluate(times, null);
		if (temp.isPresent()) {
			return temp;
		}
		return null;
	}
}
