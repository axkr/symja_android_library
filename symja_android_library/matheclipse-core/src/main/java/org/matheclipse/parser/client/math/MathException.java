package org.matheclipse.parser.client.math;

import org.matheclipse.core.basic.Config;

public class MathException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3520033778672500363L;

	public MathException() {
		super();
	}

	public MathException(String message) {
		super(message);
	}

	public MathException(String message, Throwable cause) {
		super(message, cause);
	}

	public MathException(Throwable cause) {
		super(cause);
	}
	
	@Override
	public synchronized Throwable fillInStackTrace() {
		if (Config.SHOW_STACKTRACE) {
			return super.fillInStackTrace();
		} else {
			return this;
		}
	}
}