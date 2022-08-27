// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.api;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.img.ColorDataGradient;
import org.matheclipse.core.tensor.itp.BSplineFunction;

/**
 * serializable function that maps a {@link Scalar} to a {@link IAST}
 * 
 * Examples: {@link ColorDataGradient}, and {@link BSplineFunction}
 */
@FunctionalInterface
public interface ScalarTensorFunction extends Function<IExpr, IAST>, Serializable {
  /** @param before non-null
   * @return scalar -> apply(before.apply(scalar))
   * @throws Exception if operator before is null */
  default ScalarTensorFunction compose(ScalarUnaryOperator before) {
    Objects.requireNonNull(before);
    return tensor -> apply(before.apply(tensor));
  }

  /** @param after non-null
   * @return scalar -> after.apply(apply(scalar))
   * @throws Exception if operator after is null */
  default ScalarTensorFunction andThen(TensorUnaryOperator after) {
    Objects.requireNonNull(after);
    return scalar -> after.apply(apply(scalar));
  }
}
