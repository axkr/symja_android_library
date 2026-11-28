package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.IExpr;

/**
 * Thrown by a failed <code>Confirm</code>, <code>ConfirmBy</code>, <code>ConfirmMatch</code>,
 * <code>ConfirmAssert</code> or <code>ConfirmQuiet</code>, and caught by the nearest enclosing
 * <code>Enclose</code> with the same tag. It is not a <code>Throw</code>, so a <code>Catch</code>
 * doesn't intercept it.
 */
public final class ConfirmationException extends FlowControlException {

  private static final long serialVersionUID = 4384516432016858372L;

  /** The <code>Failure("ConfirmationFailed", ...)</code> object. */
  private final IExpr failure;

  /** The tag of the confirmation, or <code>F.NIL</code> for an untagged one. */
  private final IExpr tag;

  public ConfirmationException(IExpr failure, IExpr tag) {
    super();
    this.failure = failure;
    this.tag = tag;
  }

  /** @return the <code>Failure(...)</code> object */
  public IExpr getFailure() {
    return failure;
  }

  /** @return the tag, or <code>F.NIL</code> for an untagged confirmation */
  public IExpr getTag() {
    return tag;
  }
}
