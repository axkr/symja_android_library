// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.lang;

import java.awt.Graphics;
import java.math.BigInteger;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.core.tensor.qty.IUnit;


public class Unicode {
  private Unicode() {
    // ---
  }

  private static final char SPACE = ' ';
  private static final String OVER = SPACE + "/" + SPACE;
  private static final char NARROW = '\u2009';

  /**
   * @param scalar
   * @return string expression of given scalar suitable for rendering in {@link Graphics}
   */
  public static String valueOf(IExpr scalar) {
    // Optional<BigInteger> optional = Scalars.optionalBigInteger(scalar);
    // if (optional.isPresent())
    // return valueOf(optional.orElseThrow());
    if (scalar instanceof IFraction) {
      IRational rationalScalar = (IRational) scalar;
      if (!rationalScalar.denominator().isOne()) {
        return valueOf(rationalScalar.numerator()) + OVER + valueOf(rationalScalar.denominator());
      }
    }
    if (scalar instanceof IQuantity) {
      IQuantity quantity = (IQuantity) scalar;
      return quantity.toString();// (quantity.value()) + SPACE + valueOf(quantity.unit());
    }
    if (scalar instanceof INum) {
      // INum doubleScalar = (INum) scalar;
      String string = scalar.toString();
      int index = string.indexOf('.');
      return 0 <= index //
          ? valueOf(new BigInteger(string.substring(0, index))) + string.substring(index)
          : string; // Infinity, NaN
    }
    return scalar.toString();
  }

  /**
   * @param bigInteger
   * @return string expression of given bigInteger suitable for rendering in {@link Graphics}
   */
  public static String valueOf(BigInteger bigInteger) {
    String string = bigInteger.abs().toString();
    int offset = string.length() % 3;
    if (offset == 0)
      offset = 3;
    StringBuilder stringBuilder = new StringBuilder();
    if (bigInteger.signum() < 0)
      stringBuilder.append('-');
    stringBuilder.append(string.substring(0, offset));
    for (int index = offset; index < string.length(); index += 3) {
      stringBuilder.append(NARROW);
      stringBuilder.append(string.substring(index, index + 3));
    }
    return stringBuilder.toString();
  }
  // ---

  /**
   * "m*s^-1" -> "m/s" use of unicode characters for degC, Ohm and micro-x use of unicode characters
   * for exponents such as ^-2 etc.
   * 
   * @param unit
   * @return string expression of given unit suitable for rendering in {@link Graphics}
   */
  public static String valueOf(IUnit unit) {
    return UnicodeUnit.toString(unit);
  }
}
