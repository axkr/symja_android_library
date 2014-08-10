package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.IExpr;

/**
 * Exception for the <code>Throw[]</code> function.
 *
 */
public class ThrowException extends FlowControlException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8468658696375569705L;

	
	final private IExpr value;

	public ThrowException() {
		this(null);
	}

	public ThrowException(final IExpr val) {
		super("Throw an exception for Catch[].");
		value = val;
	}

	public IExpr getValue() {
		return value;
	}
}
