package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.parser.client.math.MathException;

/**
 */
public class WrongNumberOfArguments extends MathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 754625729654866796L;

	int fAllowed;

	int fCurrent;

	IAST fExpr;

	/**
	 * Expected number of arguments: {@code allowed} but got {@code current}
	 * arguments:<br/> {@code expr}.
	 * 
	 * @param expr
	 * @param allowed
	 * @param current
	 */
	public WrongNumberOfArguments(final IAST expr, final int allowed, final int current) {
		fAllowed = allowed;
		fCurrent = current;
		fExpr = expr;
	}

	@Override
	public String getMessage() {
		return "Expected number of arguments: " + fAllowed + " but got " + fCurrent + " arguments:\n" + fExpr.toString();
	}

}
