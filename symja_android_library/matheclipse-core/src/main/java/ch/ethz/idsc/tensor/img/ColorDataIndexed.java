package ch.ethz.idsc.tensor.img;

import java.awt.Color;
import java.util.function.Function;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** function maps {@link IExpr}s to vectors of the form {R, G, B, A}
 * with entries in the interval [0, 255]. */
public interface ColorDataIndexed extends Function<IExpr, IAST> {
  /** @param index
   * @return color associated to given index */
  Color getColor(int index);

  /** @param value in [0, 1]
   * @return color associated to given value. for value == 0 the result is identical to getColor(0) */
  Color rescaled(double value);

  /** @param alpha in the interval [0, 1, ..., 255]
   * @return new instance of ColorDataIndexed with identical RGB color values
   * but with transparency as given alpha
   * @throws Exception if alpha is not in the valid range */
  ColorDataIndexed deriveWithAlpha(int alpha);
}
