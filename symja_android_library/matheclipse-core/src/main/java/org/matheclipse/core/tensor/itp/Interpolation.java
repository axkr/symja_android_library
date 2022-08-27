// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Multi-dimensional interpolation
 * 
 */
public interface Interpolation {
  /**
   * if index.length() is less than the rank r of the tensor object that is being interpolated, then
   * the function get(...) returns a tensor of rank r - index.length()
   * 
   * @param index must not be {@link IExpr}
   * @return expression similar to IAST::get(index)
   * @throws Exception if index is outside dimensions of tensor, or index is a {@link IExpr}
   */
  IExpr get(IAST index);

  /** @return {@link #get(IAST)} cast as {@link IExpr} */
  IExpr Get(IAST index);

  /**
   * optimized function for interpolation along the first dimension
   * 
   * @param index
   * @return result that is identical to get(Tensors.of(index))
   * @throws Exception if index is not in the valid range
   */
  IExpr at(IExpr index);

  /** @return {@link #at(IExpr)} cast as {@link IExpr} */
  IExpr At(IExpr index);
}
