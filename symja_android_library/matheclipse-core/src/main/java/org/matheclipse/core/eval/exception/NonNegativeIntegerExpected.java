package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.parser.client.math.MathException;

/**
 */
public class NonNegativeIntegerExpected extends MathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 161506792947148754L;

	int fCurrent;

	IAST fExpr;

	public NonNegativeIntegerExpected(final IAST expr, final int current) {
		fCurrent = current;
		fExpr = expr;
	}

	@Override
	public String getMessage() {
		return "Non negative int expected at arguments position: " + fCurrent + " in expression:\n" + fExpr.toString();
	}

}
