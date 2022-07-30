// code by jph
package org.matheclipse.core.img;

import java.awt.Color;
import java.util.Arrays;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISignedNumber;


/** reference implementation of a {@link ColorDataIndexed} with strict indexing
 * 
 * color indices are required to be in the range 0, 1, ..., tensor.length() - 1 */
public class StrictColorDataIndexed extends BaseColorDataIndexed {
  /** Hint: tensor may be empty
   * 
   * @param tensor with dimensions N x 4 where each row encodes {R, G, B, A}
   * @return
   * @see CyclicColorDataIndexed */
  public static ColorDataIndexed of(IAST tensor) {
    return new StrictColorDataIndexed(tensor.copy());
  }

  /** @param colors
   * @return palette of given colors where index maps to colors[index] */
  @SafeVarargs
  public static ColorDataIndexed of(Color... colors) {
    return new StrictColorDataIndexed(
        F.ListAlloc(Arrays.stream(colors).map(ColorFormat::toVector)));
  }

  // ---
  /** @param tensor with dimensions N x 4 where each row encodes {R, G, B, A} */
  /* package */ StrictColorDataIndexed(IAST tensor) {
    super(tensor);
  }

  @Override // from ColorDataIndexed
  public RGBColor getRGBColor(int index) {
    return colors[index];
  }

  @Override // from ColorDataIndexed
  public ColorDataIndexed deriveWithAlpha(int alpha) {
    return new StrictColorDataIndexed(tableWithAlpha(alpha));
  }

  @Override // from BaseColorDataIndexed
  protected int toInt(ISignedNumber scalar) {
    return scalar.toIntDefault();
  }
}
