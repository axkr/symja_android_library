package org.matheclipse.core.eval.exception;

public final class FailedException extends FlowControlException {

  private static final long serialVersionUID = 5411356928714730288L;

  public static final FailedException FAILED = new FailedException();
}
