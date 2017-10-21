package org.matheclipse.core.eval.exception;

import org.matheclipse.parser.client.math.MathException;

/**
 * Base exception for BreakException and ContinueException
 * 
 * 
 */
public class FlowControlException extends MathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7700982641897767896L;

	public FlowControlException(final String message) {
		super(message);
	}

}
