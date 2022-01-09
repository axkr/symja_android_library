package org.matheclipse.core.eval.exception;

public class MemoryLimitExceeded extends LimitException {

  private static final long serialVersionUID = -8031017311519064342L;

  public MemoryLimitExceeded(String message) {
    super(message);
  }
}
