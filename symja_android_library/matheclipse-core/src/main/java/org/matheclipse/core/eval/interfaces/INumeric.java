package org.matheclipse.core.eval.interfaces;

public interface INumeric {
  /**
   * Evaluate the function to a double number
   *
   * @return
   */
  double evalReal(double[] stack, int top, int size);
}
