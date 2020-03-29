package org.matheclipse.core.eval.exception;

import org.matheclipse.core.builtin.StringFunctions;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.math.MathException;

/**
 * Exception which will be thrown, if the Config.MAX_AST_SIZE limit was exceeded.
 */
public class ASTElementLimitExceeded extends LimitException {

	private static final long serialVersionUID = 8925451277545397036L;

	long fLimit;

	public ASTElementLimitExceeded(final long limit) {
		fLimit = limit;
	}

	@Override
	public String getMessage() {
		return "Maximum AST size " + fLimit + " exceeded";
	}

	public static void throwIt(final int limit) {
		// HeapContext.enter();
		// try {
		throw new ASTElementLimitExceeded(limit);// .copy());
		// } finally {
		// HeapContext.exit();
		// }
	}

}
