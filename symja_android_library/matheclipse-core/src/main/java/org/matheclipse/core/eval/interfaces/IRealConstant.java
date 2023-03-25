package org.matheclipse.core.eval.interfaces;

/** Interface for &quot;real numeric constants&quot;. */
public interface IRealConstant {
  /**
   * Evaluate the symbol to a double number
   *
   * @return
   * @see org.matheclipse.core.eval.DoubleStackEvaluator
   */
  double evalReal();

  default boolean isNegative() {
    return false;
  }

  default boolean isPositive() {
    return true;
  }
}
