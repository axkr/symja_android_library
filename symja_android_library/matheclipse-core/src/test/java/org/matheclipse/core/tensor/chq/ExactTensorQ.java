// code by jph
package org.matheclipse.core.tensor.chq;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;

/**
 * @see InexactScalarMarker
 * @see ExactScalarQ
 */
public class ExactTensorQ {

  /**
   * @param tensor
   * @return true if all scalar entries in given tensor satisfy the predicate
   *         {@link ExactScalarQ#of(Scalar)}
   */
  public static boolean of(IAST tensor) {
    return tensor.forAllLeaves(S.List, x -> x.isExactNumber(), 1);
    // return tensor.mapLeaf(S.List, -1).map(IExpr.class::cast).allMatch(ExactScalarQ::of);
  }

  /**
   * @param tensor
   * @return given tensor
   * @throws Exception if given tensor does not have all entries in exact precision
   */
  public static IAST require(IAST tensor) {
    if (of(tensor)) {
      return tensor;
    }
    throw new IllegalArgumentException("tensor require to be exact: " + tensor.toString());
  }
}
