// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.fft;

import org.matheclipse.core.builtin.WindowFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.sca.Clips;

/* package */ enum StaticHelper {
  ;
  /** @param length
   * @param window
   * @return symmetric vector of given length of weights that sum up to length */
  public static IAST weights(int length, WindowFunctions.WindowFunction window) {
    IAST samples = 1 == length //
        ? F.List(0)
        : samples(length);
    IAST weights = samples.map(window);
    return (IAST) weights.multiply(F.num(length).divide(S.Total.of(EvalEngine.get(), weights)));
  }

  /** @param length
   * @return vector of given length */
  public static IAST samples(int length) {
    return (IAST) S.Subdivide.of(EvalEngine.get(), //
        Clips.absolute(F.C1D2.add(F.QQ(-1, 2 * length))), //
        length - 1);
  }
}
