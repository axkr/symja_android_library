// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/* package */ class HueColorData implements ColorDataGradient {

  public static final ColorDataGradient DEFAULT = new HueColorData(1.0);
  // ---
  private final double opacity;

  private HueColorData(double opacity) {
    this.opacity = opacity;
  }

  @Override // from ScalarTensorFunction
  public IAST apply(IExpr scalar) {
    double value = scalar.evalDouble();
    return Double.isFinite(value) //
        ? ColorFormat.toVector(Hue.of(value, 1, 1, opacity))
        : Transparent.rgba();
  }

  @Override // from ColorDataGradient
  public ColorDataGradient deriveWithOpacity(IExpr opacity) {
    return new HueColorData(opacity.evalDouble());
  }
}
