package org.matheclipse.core.eval.exception;

import org.matheclipse.parser.client.math.MathException;

public class WrappedException extends MathException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 884091932172509642L;

	Throwable fThrowable;
	
	public WrappedException(Throwable throwable) {
		fThrowable = throwable;
	}

	@Override
	public String getMessage() {
		return fThrowable.getMessage();
	}

}
