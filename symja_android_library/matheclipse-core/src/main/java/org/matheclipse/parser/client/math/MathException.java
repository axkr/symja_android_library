package org.matheclipse.parser.client.math;

public class MathException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3520033778672500363L;

	public MathException() {
		super();
	}

	public MathException(String message, Throwable cause) {
		super(message, cause);
	}

	public MathException(String message) {
		super(message);
	}

	public MathException(Throwable cause) {
		super(cause);
	}
}