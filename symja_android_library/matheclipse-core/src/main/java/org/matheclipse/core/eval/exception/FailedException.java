package org.matheclipse.core.eval.exception;

public class FailedException extends FlowControlException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5411356928714730288L;

	public final static FailedException FAILED = new FailedException();

	public FailedException() {
		super();
	}

}
