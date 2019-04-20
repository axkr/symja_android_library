package org.matheclipse.core.eval.exception;

public class NoEvalException extends FlowControlException {
	
	public final static NoEvalException CONST = new NoEvalException();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3859495776051748837L;

	public NoEvalException() {
		super();
	}
}
