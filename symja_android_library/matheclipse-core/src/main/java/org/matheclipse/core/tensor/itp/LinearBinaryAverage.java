// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;

/**
 * implements binary average as
 * 
 * <pre>
 * p * (1 - lambda) + q * lambda == p + lambda * (q - p)
 * </pre>
 * 
 * Remark: implementation is useful when only few interpolations between p and q are needed.
 * Otherwise, the difference q - p should be pre-computed.
 * 
 * @see LinearInterpolation
 */
public enum LinearBinaryAverage implements BinaryAverage {
  INSTANCE;

  @Override // from BinaryAverage
  public IExpr split(IExpr p, IExpr q, IExpr scalar) {
    return EvalEngine.get().evaluate(q.subtract(p).times(scalar).add(p));
  }
}
