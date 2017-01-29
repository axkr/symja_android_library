package org.matheclipse.core.eval.exception;

public class ContinueException extends FlowControlException {

	public final static ContinueException CONST = new ContinueException();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7698113142556488875L;

	public ContinueException() {
		super("Continue a loop or switch statement.");
	}
}
