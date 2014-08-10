package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.math.MathException;

public class IndeterminateException extends MathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6917088489458174642L;

	int fPosition;

	IExpr fArg;

	IAST fExpr;

	public IndeterminateException(final IAST expr, final IExpr arg, final int position) {
		fPosition = position;
		fArg = arg;
		fExpr = expr;
	}

	@Override
	public String getMessage() {
		return "Indeterminate argument: " + fExpr.toString() + " in expression " + fArg.toString() + " at position:"
				+ Integer.toString(fPosition);
	}

}
