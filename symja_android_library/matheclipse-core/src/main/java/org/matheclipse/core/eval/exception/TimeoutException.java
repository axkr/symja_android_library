package org.matheclipse.core.eval.exception;

public final class TimeoutException extends FlowControlException {

  private static final long serialVersionUID = -1664843691177511531L;

  public static final TimeoutException TIMED_OUT = new TimeoutException();

  private TimeoutException() {}
}
