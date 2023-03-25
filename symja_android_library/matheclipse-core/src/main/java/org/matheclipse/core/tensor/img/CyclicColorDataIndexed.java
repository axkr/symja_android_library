// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import java.awt.Color;
import java.util.Arrays;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IReal;

/** reference implementation of a {@link ColorDataIndexed} with cyclic indexing */
public class CyclicColorDataIndexed extends BaseColorDataIndexed {
  /**
   * @param tensor with dimensions N x 4 where each row encodes {R, G, B, A}
   * @return
   * @throws Exception if tensor is empty
   * @see StrictColorDataIndexed
   */
  public static ColorDataIndexed of(IAST tensor) {
    return new CyclicColorDataIndexed(tensor.copy());
  }

  /**
   * @param colors
   * @return palette of given colors where index maps to colors[index]
   */
  @SafeVarargs
  public static ColorDataIndexed of(Color... colors) {
    return new CyclicColorDataIndexed(
        F.ListAlloc(Arrays.stream(colors).map(ColorFormat::toVector)));
  }

  // ---
  private final java.util.function.IntUnaryOperator mod;

  /** @param tensor with dimensions N x 4 where each row encodes {R, G, B, A} */
  /* package */ CyclicColorDataIndexed(IAST tensor) {
    super(tensor);
    mod = i -> i % tensor.argSize();
  }

  @Override // from ColorDataIndexed
  public RGBColor getRGBColor(int index) {
    return colors[Math.floorMod(index, colors.length)];
  }

  @Override // from ColorDataIndexed
  public ColorDataIndexed deriveWithAlpha(int alpha) {
    return new CyclicColorDataIndexed(tableWithAlpha(alpha));
  }

  @Override // from BaseColorDataIndexed
  protected int toInt(IReal scalar) {
    return mod.applyAsInt(scalar.toIntDefault());
  }
}
