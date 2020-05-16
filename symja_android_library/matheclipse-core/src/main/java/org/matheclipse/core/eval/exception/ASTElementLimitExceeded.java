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
	
	/**
	 * Set the exceeded limit to <code>(long)rowDimension*(long)columnDimension</code>.
	 * 
	 * @param rowDimension
	 * @param columnDimension
	 */
	public ASTElementLimitExceeded(final int rowDimension, final int columnDimension) {
		fLimit = (long)rowDimension*(long)columnDimension;
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
