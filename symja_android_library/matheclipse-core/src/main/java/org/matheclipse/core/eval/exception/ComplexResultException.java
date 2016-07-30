package org.matheclipse.core.eval.exception;

import org.matheclipse.parser.client.math.MathException;

public class ComplexResultException extends MathException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5196477906961508553L;

	/**
	 * Constructs a dimension exception with no detail message.
	 */
	public ComplexResultException() {
		super();
	}

	/**
	 * Constructs a dimension exception with the specified message.
	 * 
	 * @param message
	 *            the error message.
	 */
	public ComplexResultException(String message) {
		super(message);
	}

}
