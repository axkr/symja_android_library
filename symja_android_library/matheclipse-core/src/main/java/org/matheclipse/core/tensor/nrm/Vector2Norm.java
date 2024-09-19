// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.nrm;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Euclidean norm
 * 
 * ||{a, b, c}||_2 = Sqrt[a^2 + b^2 + c^2]
 */
public class Vector2Norm {

  // public static final UnaryOperator<IExpr> NORMALIZE = S.Normalize.with(Vector2Norm::of);

  /**
   * @param vector
   * @return 2-norm of given vector
   */
  public static IExpr of(IAST vector) {
    try {
      // Hypot prevents the incorrect evaluation: Norm_2[ {1e-300, 1e-300} ] == 0
      return Hypot.ofVector(vector);
    } catch (Exception exception) {
      Errors.rethrowsInterruptException(exception);
      // <- when vector is a scalar
      // <- when vector is empty, or contains NaN
    }
    return F.eval(F.Sqrt(Vector2NormSquared.of(vector)));
  }

  /**
   * @param v1 vector
   * @param v2 vector
   * @return 2-norm of vector difference || v1 - v2 ||
   */
  public static IExpr between(IAST v1, IAST v2) {
    return of((IAST) v1.subtract(v2));
  }
}
