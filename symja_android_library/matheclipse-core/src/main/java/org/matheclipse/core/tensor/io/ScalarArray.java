// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.io;

import org.matheclipse.core.generic.Tensors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public enum ScalarArray {
  ;
  /**
   * Hint: convert back with {@link Tensors#of(IExpr...)}
   * 
   * @param vector
   * @return
   * @throws Exception if given vector is not a tensor of rank 1
   */
  public static IExpr[] ofVector(IAST vector) {
    return vector.stream().map(IExpr.class::cast).toArray(IExpr[]::new);
  }

  /**
   * Hint: convert back with {@link Tensors#matrix(IExpr[][])}
   * 
   * @param matrix not necessarily with array structure
   * @return
   * @throws Exception if given matrix is not a list of vectors
   */
  public static IExpr[][] ofMatrix(IAST matrix) {
    return matrix.stream().map(x -> ofVector((IAST) x)).toArray(IExpr[][]::new);
  }
}
