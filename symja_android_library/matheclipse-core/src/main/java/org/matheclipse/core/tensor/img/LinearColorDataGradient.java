// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import java.util.function.Function;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.itp.Interpolation;
import org.matheclipse.core.tensor.itp.LinearInterpolation;

/**
 * ColorDataGradient maps a {@link IExpr} from the interval [0, 1] to a 4-vector {r, g, b, a} with
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
public class LinearColorDataGradient implements ColorDataGradient {
  // private static final Clip CLIP = Clips.interval(0, 256);

  /**
   * colors are generated using {@link LinearInterpolation} of given tensor
   * 
   * @param tensor n x 4 where each row contains {r, g, b, a} with values ranging in [0, 256)
   * @return
   * @throws Exception if tensor is empty, or is not of the above form
   */
  public static ColorDataGradient of(IAST tensor) {
    if (tensor.isEmpty()) {
      throw new IllegalArgumentException();
    }
    tensor.stream().forEach(ColorFormat::toColor);
    return new LinearColorDataGradient(tensor);
    // return new LinearColorDataGradient(tensor.mapLeaf(S.List, CLIP::requireInside));
  }

  // ---
  private final IAST tensor;
  private final Interpolation interpolation;
  private final double scale;

  /* package */ LinearColorDataGradient(IAST tensor) {
    this.tensor = tensor;
    interpolation = LinearInterpolation.of(this.tensor);
    scale = tensor.argSize() - 1.0;
  }

  @Override // from ColorDataGradient
  public IExpr apply(IExpr scalar) {
    double value = scalar.multiply(scale).evalDouble();
    // if (value > 1.0 || value < 0.0) {
    // return Transparent.rgba();
    // }
    return Double.isFinite(value) //
        ? interpolation.at(F.num(value))
        : Transparent.rgba();
  }

  @Override // from ColorDataGradient
  public LinearColorDataGradient deriveWithOpacity(IExpr opacity) {
    return new LinearColorDataGradient(F.ListAlloc(tensor.stream().map(withOpacity(opacity))));
  }

  /**
   * @param opacity in the interval [0, 1]
   * @return operator that maps a vector of the form rgba to rgb, alpha*factor
   * @throws Exception if given opacity is outside the valid range
   */
  private static Function<IExpr, IAST> withOpacity(IExpr opacity) {
    // Clips.unit().requireInside(opacity);
    return rgba -> {
      IASTMutable copy = ((IAST) rgba).copy();
      copy.setApply(4, alpha -> alpha.multiply(opacity));
      return copy;
    };
  }
}
