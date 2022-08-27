// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class GrayscaleColorData implements ColorDataGradient {

  public static final ColorDataGradient DEFAULT = new GrayscaleColorData(255);

  public final static ColorDataGradient GRAYSCALE_DEFAULT =
      LinearColorDataGradient.of(F.List(F.List(255, 255, 255, 255), F.List(0, 0, 0, 255)));
  // ---

  private final IAST[] tensors = new IAST[256];

  private GrayscaleColorData(int alpha) {
    for (int index = 0; index < 256; ++index) {
      tensors[index] = F.List(index, index, index, alpha);
    }
  }

  @Override // from ScalarTensorFunction
  public IAST apply(IExpr scalar) {
    double value = scalar.evalDouble();
    return Double.isFinite(value) //
        ? tensors[toInt(value)].copy()
        : Transparent.rgba();
  }

  @Override // from ColorDataGradient
  public ColorDataGradient deriveWithOpacity(IExpr opacity) {
    double value = opacity.evalDouble();
    return new GrayscaleColorData(toInt(value));
  }

  private static int toInt(double value) {
    return (int) (value * 255 + 0.5);
  }
}
