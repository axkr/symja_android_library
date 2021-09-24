package org.matheclipse.core.eval.exception;

public class AbortException extends FlowControlException {

  private static final long serialVersionUID = 7641731563890805624L;

  public static final AbortException ABORTED = new AbortException();
}
