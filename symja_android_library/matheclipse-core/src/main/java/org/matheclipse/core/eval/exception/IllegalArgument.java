package org.matheclipse.core.eval.exception;

import org.matheclipse.parser.client.math.MathException;

/**
 */
public class IllegalArgument extends MathException {
 
	private static final long serialVersionUID = -7391151834506155746L;

	final String fMessage;

	/**
	 * The function has a wrong argument.
	 * 
	 * @param message
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
