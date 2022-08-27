// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.nrm;

import java.util.stream.Stream;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Euclidean norm squared
 * 
 * ||{a, b, c}||_2^2 = a^2 + b^2 + c^2
 * 
 * @see AbsSquared
 */
public class Vector2NormSquared {

  /**
   * @param vector
   * @return squared Euclidean norm of given vector, i.e. || v1 || ^ 2
   * @throws Exception if vector is empty
   */
  public static IExpr of(IAST vector) {
    return of(vector.stream().map(IExpr.class::cast));
  }

  /**
   * @param stream of scalars
   * @return sum of squares of scalars in given stream
   */
  public static IExpr of(Stream<IExpr> stream) {
    return stream.map(x -> x.multiply(x.conjugate())).reduce(IExpr::add).orElseThrow();
  }

  /**
   * @param v1 vector
   * @param v2 vector
   * @return squared Euclidean norm of vector difference, i.e. || v1 - v2 || ^ 2
   */
  public static IExpr between(IAST v1, IAST v2) {
    return of((IAST) v1.subtract(v2));
  }
}
