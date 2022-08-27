// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.api;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Serializable function that maps a {@link IAST} to a {@link Scalar}
 * 
 * <p>
 * Examples: 1) an implicit function that defines a region as {x | f(x) &lt; 0} 2) a smooth noise
 * function that maps a vector to a value in the interval [-1, 1]
 */
@FunctionalInterface
public interface TensorScalarFunction extends Function<IAST, IExpr>, Serializable {
  /** @param before non-null
   * @return scalar -> apply(before.apply(scalar))
   * @throws Exception if operator before is null */
  default TensorScalarFunction compose(TensorUnaryOperator before) {
    Objects.requireNonNull(before);
    return tensor -> apply(before.apply(tensor));
  }

  /** @param after non-null
   * @return tensor -> after.apply(apply(tensor))
   * @throws Exception if operator after is null */
  default TensorScalarFunction andThen(ScalarUnaryOperator after) {
    Objects.requireNonNull(after);
    return tensor -> after.apply(apply(tensor));
  }
}
