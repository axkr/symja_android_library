package org.matheclipse.core.eval.exception;

import org.matheclipse.core.interfaces.IAST;

/**
 * Thrown by <code>Goto(tag)</code> and caught by the <code>CompoundExpression</code> which contains
 * the matching <code>Label(tag)</code>. That compound expression continues with the element after
 * the label.
 */
public final class GotoException extends FlowControlException {

  private static final long serialVersionUID = -3150470187310628761L;

  /** The <code>CompoundExpression</code> which contains the label. */
  private final transient IAST compoundExpression;

  /** The position of the <code>Label(tag)</code> in {@link #compoundExpression}. */
  private final int position;

  public GotoException(IAST compoundExpression, int position) {
    super();
    this.compoundExpression = compoundExpression;
    this.position = position;
  }

  /** @return the <code>CompoundExpression</code> which contains the label */
  public IAST getCompoundExpression() {
    return compoundExpression;
  }

  /** @return the position of the label in the compound expression */
  public int getPosition() {
    return position;
  }
}
