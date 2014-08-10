package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.math.MathException;

/**
 */
public class WrongArgumentType extends MathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -361382155486374959L;

	int fPosition;

	IExpr fArg;

	IAST fExpr;

	String fMessage;

	/**
	 * The function {@code expr} has wrong argument {@code arg} at position:
	 * {@code position}
	 * 
	 * @param expr
	 * @param arg
	 * @param position
	 */
	public WrongArgumentType(final IAST expr, final IExpr arg, final int position) {
		this(expr, arg, position, null);
	}

	public WrongArgumentType(final IExpr arg, String message) {
		this(null, arg, 0, message);
	}

	/**
	 * The function {@code expr} has wrong argument {@code arg} at position:
	 * {@code position}: {@code message}
	 * 
	 * @param expr
	 * @param arg
	 * @param position
	 */
	public WrongArgumentType(final IAST expr, final IExpr arg, final int position, String message) {
		super();
		fPosition = position;
		fArg = arg;
		fExpr = expr;
		fMessage = message;
	}

	@Override
	public String getMessage() {
		if (fExpr == null) {
			return "The expression: " + fArg.toString() + " has a wrong type:\n" + fMessage;
		}
		if (fMessage == null) {
			return "The function: " + fExpr.toString() + " has wrong argument " + fArg.toString() + " at position:"
					+ Integer.toString(fPosition);
		} else {
			if (fPosition < 0) {
				return "The function: " + fExpr.toString() + " has wrong type:\n" + fMessage;
			}
			return "The function: " + fExpr.toString() + " has wrong argument " + fArg.toString() + " at position:"
					+ Integer.toString(fPosition) + ":\n" + fMessage;
		}
	}

}
