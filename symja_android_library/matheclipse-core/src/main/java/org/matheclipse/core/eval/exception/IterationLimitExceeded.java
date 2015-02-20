package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.ToString;
import org.matheclipse.parser.client.math.MathException;

/**
 * Exception which will be thrown, if the iteration limit of the evaluation loop
 * was exceeded.
 */
public class IterationLimitExceeded extends MathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4334847679009499117L;

	/**
	 * 
	 */
	long fLimit;

	IExpr fExpr;

	public IterationLimitExceeded(final long limit, final IExpr expr) {
		fLimit = limit;
		fExpr = expr;
	}

	@Override
	public String getMessage() {
		if (fExpr == null) {
			return "Iteration limit " + fLimit + " exceeded at: null";
		}
		return "Iteration limit " + fLimit + " exceeded at: " + ToString.outputForm(fExpr);
	}

	public static void throwIt(long iterationCounter, final IExpr expr) {
		// HeapContext.enter();
		// try {
		throw new IterationLimitExceeded(iterationCounter, expr);// expr.copy());
		// } finally {
		// HeapContext.exit();
		// }
	}
}
