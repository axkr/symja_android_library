// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import java.util.Arrays;
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
    if (scalar.isOne()) {
      return q.copy();
    }
    return EvalEngine.get().evaluate(q.subtract(p).times(scalar).plus(p));
  }

  @Override // from BinaryAverage
  public double[] split(final double[] p, final double[] q, final double scalar) {
    if (scalar == 1.0) {
      return Arrays.copyOf(q, q.length);
    }
    double[] result = new double[p.length];
    for (int i = 0; i < p.length; i++) {
      result[i] = (q[i] - p[i]) * scalar + p[i];
    }
    return result;
  }

}
