package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.Power;

public class PowerOp {
	/**
	 * Evaluate <code>base ^ exponent</code>.
	 * 
	 * @param base
	 * @param exponent
	 * @return
	 */
	public static IExpr power(IExpr base, IExpr exponent) {
		IExpr expr = Power.CONST.binaryOperator(base, exponent);
		if (expr == null) {
			return F.Power(base, exponent);
		}
		return expr;
	}
}
