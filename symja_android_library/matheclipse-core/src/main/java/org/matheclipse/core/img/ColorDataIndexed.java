// code by jph
package org.matheclipse.core.img;

import java.awt.Color;
import java.util.function.Function;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISignedNumber;

/** function maps {@link Scalar}s to vectors of the form {R, G, B, A} with entries in the interval [0, 255].
 * implementations are immutable.
 * 
 * <p>The tensor library provides the implementations
 * {@link CyclicColorDataIndexed}
 * {@link StrictColorDataIndexed}
 * 
 * <p>The tensor library provides the color data
 * {@link ColorDataLists}
 * 
 * <p>since ColorDataIndexed extends from ScalarTensorFunction
 * every instance also implements the serializable interface. */
public interface ColorDataIndexed extends Function<ISignedNumber, IAST> {
  /** @param index
   * @return color associated to given index */
  RGBColor getRGBColor(int index);

  /**
   * @param index
   * @return color associated to given index
   */
  default Color getColor(int index) {
    RGBColor rgbColor = getRGBColor(index);
    return new Color(rgbColor.getRGB());
  }

  /** @return number of unique colors defined by the instance */
  int length();

  /** @param alpha in the interval [0, 1, ..., 255]
   * @return new instance of ColorDataIndexed with identical RGB color values
   * but with transparency as given alpha
   * @throws Exception if alpha is not in the valid range */
  ColorDataIndexed deriveWithAlpha(int alpha);
}
