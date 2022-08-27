// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.api;

import java.io.Serializable;
import java.util.function.BinaryOperator;
import org.matheclipse.core.interfaces.IExpr;

/** interface for serializable functions that maps two {@link Scalar}s to a {@link Scalar} */
@FunctionalInterface
public interface ScalarBinaryOperator extends BinaryOperator<IExpr>, Serializable {
  // ---
}
