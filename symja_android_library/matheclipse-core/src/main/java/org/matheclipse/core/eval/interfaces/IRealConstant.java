package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;

/** Interface for &quot;real numeric constants&quot;. */
public interface IRealConstant {
  /**
   * Evaluate the symbol to a double number
   *
   * @return
   * @see org.matheclipse.core.eval.DoubleStackEvaluator
   */
  double evalReal();

  default boolean isReal() {
    return true;
  }

  default boolean isNegative() {
    return false;
  }

  default boolean isPositive() {
    return true;
  }

  default boolean isNumber() {
    return true;
  }

  default COMPARE_TERNARY isIrrational() {
    return COMPARE_TERNARY.UNDECIDABLE;
  }

  default COMPARE_TERNARY isAlgebraic() {
    return COMPARE_TERNARY.UNDECIDABLE;
  }

  default COMPARE_TERNARY isTranscendental() {
    return COMPARE_TERNARY.UNDECIDABLE;
  }
}
