// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.fft;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;

/**
 * applications of {@link FourierMatrix} is to perform {@link Fourier} transform and inverse
 * transform of vectors or matrices of arbitrary dimensions.
 * 
 * <p>
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierMatrix.html">FourierMatrix</a>
 * 
 * @see VandermondeMatrix
 */
public class FourierMatrix {

  /**
   * @param n positive
   * @return square matrix of dimensions [n x n] with complex entries
   *         <code>(i, j) -> sqrt(1/n) exp(i * j * 2pi/n *I)</code>
   */
  public static IAST of(int n) {
    double scalar = Math.sqrt(1.0 / n);
    return F.matrix((i, j) -> //
    ComplexNum.unitOf((((double) i) * ((double) j) / n) * (Math.PI + Math.PI) * scalar), //
      n,//
      n);
  }

  /**
   * @param n
   * @return inverse of fourier matrix
   */
  public static IAST inverse(int n) {
    return (IAST) S.ConjugateTranspose.of(EvalEngine.get(), of(n));
  }
}
