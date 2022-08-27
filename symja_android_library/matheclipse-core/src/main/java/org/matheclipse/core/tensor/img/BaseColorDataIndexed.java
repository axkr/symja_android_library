// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import java.util.function.UnaryOperator;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;

/* package */ abstract class BaseColorDataIndexed implements ColorDataIndexed {

  private final IAST tensor;
  protected final RGBColor[] colors;

  public BaseColorDataIndexed(IAST tensor) {
    this.tensor = tensor;
    colors = tensor.stream() //
        .map(ColorFormat::toColor) //
        .toArray(RGBColor[]::new);
  }

  @Override // from ScalarTensorFunction
  public final IAST apply(ISignedNumber scalar) {
    // if (scalar instanceof MultiplexScalar)
    // throw TensorRuntimeException.of(scalar);
    return (IAST) tensor.get(scalar.toIntDefault() + 1);
  }

  @Override // from ColorDataIndexed
  public final int length() {
    return colors.length;
  }

  /**
   * @param scalar
   * @return
   */
  protected abstract int toInt(ISignedNumber scalar);

  /**
   * @param alpha in the range [0, 1, ..., 255]
   * @return
   */
  protected final IAST tableWithAlpha(int alpha) {
    return F.ListAlloc(tensor.stream().map(withAlpha(alpha)));
  }

  /**
   * @param alpha in the range [0, 1, ..., 255]
   * @return operator that maps a vector of the form {r, g, b, any} to {r, g, b, alpha}
   */
  private static UnaryOperator<IExpr> withAlpha(int alpha) {
    IInteger scalar = F.ZZ(alpha);
    return rgba -> {
      IASTAppendable t = ((IAST) rgba).copyFrom(1, 4);
      t.append(scalar);
      return t;
    };
  }
}
