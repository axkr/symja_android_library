// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.api;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import org.matheclipse.core.interfaces.IExpr;

/** interface for serializable functions that map a {@link IExpr} to another {@link Scalar} */
@FunctionalInterface
public interface ScalarUnaryOperator extends UnaryOperator<IExpr>, Serializable {
  ScalarUnaryOperator IDENTITY = s -> s;

  /** @param before non-null
   * @return scalar -> apply(before.apply(scalar))
   * @throws Exception if operator before is null */
  default ScalarUnaryOperator compose(ScalarUnaryOperator before) {
    Objects.requireNonNull(before);
    return scalar -> apply(before.apply(scalar));
  }

  /** @param after non-null
   * @return scalar -> after.apply(apply(scalar))
   * @throws Exception if operator after is null */
  default ScalarUnaryOperator andThen(ScalarUnaryOperator after) {
    Objects.requireNonNull(after);
    return scalar -> after.apply(apply(scalar));
  }

  /** Remark:
   * The empty chain ScalarUnaryOperator.chain() returns
   * the instance {@link #IDENTITY}
   * 
   * @param scalarUnaryOperators
   * @return operator that nests given operators with execution from left to right
   * @throws Exception if any given operator is null */
  @SafeVarargs
  static ScalarUnaryOperator chain(ScalarUnaryOperator... scalarUnaryOperators) {
    return List.of(scalarUnaryOperators).stream() //
        .reduce(IDENTITY, ScalarUnaryOperator::andThen);
  }
}
