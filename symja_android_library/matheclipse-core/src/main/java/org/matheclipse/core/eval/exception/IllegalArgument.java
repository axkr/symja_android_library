package org.matheclipse.core.eval.exception;

import org.matheclipse.parser.client.math.MathException;

/**
 */
public class IllegalArgument extends MathException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7391151834506155746L;

	String fMessage;

	/**
	 * The function {@code expr} has wrong argument {@code arg} at position:
	 * {@code position}: {@code message}
	 * 
	 * @param expr
	 * @param arg
	 * @param position
	 */
	public IllegalArgument(String message) {
		super();
		fMessage = message;
	}

	@Override
	public String getMessage() {
		return fMessage;
	}

}
