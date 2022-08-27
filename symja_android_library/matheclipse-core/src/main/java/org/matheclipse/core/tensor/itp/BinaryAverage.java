// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import org.matheclipse.core.interfaces.IExpr;

/** Reference:
 * "Smoothing using Geodesic Averages", 2018
 * http://www.vixra.org/abs/1810.0283 */
@FunctionalInterface
public interface BinaryAverage {
  /** implementations are not required to be symmetric at parameter 1/2.
   * 
   * Example: For clothoids
   * <pre>
   * split(p, q, 1/2) != split(q, p, 1/2)
   * </pre>
   * 
   * @implSpec the function treats the parameters p and q as unmodifiable.
   * That means, the calling entity may passes in the parameters by reference and has
   * the guarantee that the content is not altered after the call.
   * 
   * @param p
   * @param q
   * @param scalar <em>not</em> constrained to the interval [0, 1]
   * @return point on curve/geodesic that connects p and q at parameter scalar
   * for scalar == 0 the function returns p, for scalar == 1 the function returns q */
  IExpr split(IExpr p, IExpr q, IExpr scalar);
}
