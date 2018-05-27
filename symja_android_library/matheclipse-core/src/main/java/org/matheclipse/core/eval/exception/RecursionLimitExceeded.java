package org.matheclipse.core.eval.exception;

import org.matheclipse.core.builtin.StringFunctions;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.math.MathException;

/**
 * Exception which will be thrown, if the recursion limit of the evaluation
 * stack was exceeded.
 */
public class RecursionLimitExceeded extends MathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9179261773009842147L;

	int fLimit;

	IExpr fExpr;

	public RecursionLimitExceeded(final int limit, final IExpr expr) {
		fLimit = limit;
		fExpr = expr;
	}

	@Override
	public String getMessage() {
		if (fExpr == null) {
			return "Recursion limit " + fLimit + " exceeded at: null";
		}
		return "Recursion limit " + fLimit + " exceeded at: " + StringFunctions.inputForm(fExpr, true);
	}

	public static void throwIt(final int limit, final IExpr expr) {
		// HeapContext.enter();
		// try {
		throw new RecursionLimitExceeded(limit, expr);// .copy());
		// } finally {
		// HeapContext.exit();
		// }
	}

}
