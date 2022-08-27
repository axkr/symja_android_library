// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.api;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import org.matheclipse.core.interfaces.IAST;


/** interface for serializable functions that map a {@link IAST} to another {@link IAST} */
@FunctionalInterface
public interface TensorUnaryOperator extends UnaryOperator<IAST>, Serializable {
  TensorUnaryOperator IDENTITY = t -> t;

  /** @param before non-null
   * @return tensor -> apply(before.apply(tensor))
   * @throws Exception if operator before is null */
  default TensorUnaryOperator compose(TensorUnaryOperator before) {
    Objects.requireNonNull(before);
    return tensor -> apply(before.apply(tensor));
  }

  /** @param after non-null
   * @return tensor -> after.apply(apply(tensor))
   * @throws Exception if operator after is null */
  default TensorUnaryOperator andThen(TensorUnaryOperator after) {
    Objects.requireNonNull(after);
    return tensor -> after.apply(apply(tensor));
  }

  /** Remark:
   * The empty chain TensorUnaryOperator.chain() returns
   * the instance {@link #IDENTITY}
   * 
   * @param tensorUnaryOperators
   * @return operator that nests given operators with execution from left to right
   * @throws Exception if any given operator is null */
  @SafeVarargs
  static TensorUnaryOperator chain(TensorUnaryOperator... tensorUnaryOperators) {
    return List.of(tensorUnaryOperators).stream() //
        .reduce(IDENTITY, TensorUnaryOperator::andThen);
  }
}
