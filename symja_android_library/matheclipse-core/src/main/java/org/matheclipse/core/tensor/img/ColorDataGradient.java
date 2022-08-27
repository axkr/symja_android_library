// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import java.util.function.Function;
import org.matheclipse.core.interfaces.IExpr;

/**
 * ColorDataGradient maps a {@link Scalar} from the interval [0, 1] to a 4-vector {r, g, b, a} with
 * rgba entries using linear interpolation on a given table of rgba values.
 * 
 * <p>
 * Each color component in the output tensor is an integer or double value in the semi-open interval
 * [0, 256). Because {@link ColorFormat} uses Number::intValue to obtain the int color component,
 * the value 256 is not allowed and results in an Exception.
 *
 * <p>
 * In case NumberQ.of(scalar) == false then a transparent color is assigned. The result is {0, 0, 0,
 * 0}, which corresponds to a transparent color.
 */
public interface ColorDataGradient extends Function<IExpr, IExpr> {

  /**
   * @param opacity in the interval [0, 1]
   * @return new instance of ColorDataIndexed with identical RGB color values but with transparency
   *         as given opacity
   * @throws Exception if opacity is not in the valid range
   */
  ColorDataGradient deriveWithOpacity(IExpr opacity);
}
