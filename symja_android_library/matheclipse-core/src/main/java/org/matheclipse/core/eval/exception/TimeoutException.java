package org.matheclipse.core.eval.exception;

public class TimeoutException extends FlowControlException {

	private static final long serialVersionUID = -1664843691177511531L;
	
	public final static TimeoutException TIMED_OUT = new TimeoutException();
	
	public TimeoutException() {
		super();
	}

}
