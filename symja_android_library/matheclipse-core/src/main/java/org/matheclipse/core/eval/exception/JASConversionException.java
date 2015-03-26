package org.matheclipse.core.eval.exception;

import org.matheclipse.parser.client.math.MathException;


public class JASConversionException extends MathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1572094432627031023L;

	public JASConversionException() {
		super("JAS conversion error");
	}
}
