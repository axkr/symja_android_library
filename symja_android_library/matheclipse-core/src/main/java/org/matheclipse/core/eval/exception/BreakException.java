package org.matheclipse.core.eval.exception;

public class BreakException extends FlowControlException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3859495776051748837L;

	public BreakException() {
		super("Break a loop or switch statement.");
	}
}
