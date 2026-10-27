package org.matheclipse.core.generic;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.interfaces.INum;

/**
 * Conversions of the result of {@link UnaryCompiled} and {@link UnaryNumerical} to the numeric
 * type a caller asked for.
 */
final class UnaryFunctions {

  private UnaryFunctions() {}

  /**
   * @throws ArithmeticException if <code>value</code> is no machine number
   */
  static ComplexNum toComplexNum(Object value) {
    if (value instanceof ComplexNum) {
      return (ComplexNum) value;
    }
    if (value instanceof INum) {
      return ComplexNum.valueOf((INum) value);
    }
    throw new ArithmeticException("Expected numerical complex value object!");
  }

  /**
   * @throws ArithmeticException if <code>value</code> is no machine number
   */
  static Complex toComplex(Object value) {
    if (value instanceof ComplexNum) {
      return ((ComplexNum) value).complexValue();
    }
    if (value instanceof INum) {
      return Complex.valueOf(((INum) value).doubleValue());
    }
    throw new ArithmeticException("Expected numerical complex value object!");
  }

  /**
   * @throws ArithmeticException if <code>value</code> is no real machine number
   */
  static INum toINum(Object value) {
    if (value instanceof INum) {
      return (INum) value;
    }
    throw new ArithmeticException("Expected numerical double value object!");
  }
}
