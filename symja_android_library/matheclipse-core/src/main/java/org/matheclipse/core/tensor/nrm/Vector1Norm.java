// code by jph
package org.matheclipse.core.tensor.nrm;

import java.util.stream.Stream;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** vector 1-norm
 * 
 * ||{a, b, c}||_1 = |a| + |b| + |c| */
public enum Vector1Norm {
  ;
  // public static final UnaryOperator<IExpr> NORMALIZE = Normalize.with(Vector1Norm::of);

  /** @param vector
   * @return 1-norm of given vector, i.e. |a_1| + ... + |a_n| also known as ManhattanDistance */
  public static IExpr of(IAST vector) {
    return of(vector.stream().map(IExpr.class::cast));
  }

  /** @param stream of scalars
   * @return sum of absolute values of scalars in given stream
   * @throws Exception if stream is empty */
  public static IExpr of(Stream<IExpr> stream) {
    return stream.map(x -> S.Abs.of(x)).reduce(IExpr::add).orElseThrow();
  }

  /** inspired by
   * <a href="https://reference.wolfram.com/language/ref/ManhattanDistance.html">ManhattanDistance</a>
   * 
   * @param v1 vector
   * @param v2 vector
   * @return 1-norm of vector difference || v1 - v2 || */
  public static IExpr between(IAST v1, IAST v2) {
    return of((IAST) v1.subtract(v2));
  }
}
