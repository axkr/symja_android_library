package org.matheclipse.core.eval;

import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class PowerOp {
	/**
	 * Evaluate <code>base ^ exponent</code>.
	 * @param base
	 * @param exponent
	 * @return
	 */
	public static IExpr power(IExpr base, IExpr exponent) {
		return Arithmetic.CONST_POWER.binaryOperator(F.NIL, base, exponent).orElseGet(()->F.Power(base, exponent));
	}
}
