// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.fft;

import org.matheclipse.core.builtin.WindowFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.api.TensorUnaryOperator;

public class PeriodogramArray implements TensorUnaryOperator {
  /**
   * @param vector of length of power of 2
   * @return squared magnitude of the discrete Fourier transform (power spectrum) of given vector
   */
  public static IAST of(IAST vector) {
    EvalEngine engine = EvalEngine.get();
    return Fourier.of(vector).map(z -> engine.evaluate(F.Times(z, F.Conjugate(z))));
    // return Fourier.of(vector).map(AbsSquared.FUNCTION);
  }

  /**
   * @param vector of length of power of 2
   * @param windowLength positive
   * @return averages the power spectra of non-overlapping partitions of given windowLength
   */
  public static IAST of(IAST vector, int windowLength) {
    return of(windowLength, windowLength).apply(vector);
  }

  // ---
  /**
   * @param windowLength
   * @param offset
   * @param window for instance {@link HammingWindow#FUNCTION}
   * @return
   */
  public static TensorUnaryOperator of(int windowLength, int offset,
      WindowFunctions.WindowFunction window) {
    return new PeriodogramArray(windowLength, offset, window);
  }

  /**
   * @param windowLength not smaller than offset
   * @param offset positive
   * @return
   */
  public static TensorUnaryOperator of(int windowLength, int offset) {
    return of(windowLength, offset, WindowFunctions.DIRICHLET);
  }

  // ---
  private final TensorUnaryOperator spectrogramArray;

  private PeriodogramArray(int windowLength, int offset, WindowFunctions.WindowFunction window) {
    spectrogramArray = SpectrogramArray.of(windowLength, offset, window);
  }

  @Override
  public IAST apply(IAST vector) {
    EvalEngine engine = EvalEngine.get();
    return (IAST) S.Mean.of(engine,
        spectrogramArray.apply(vector).map(z -> engine.evaluate(F.Times(z, F.Conjugate(z)))));
    // return Mean.of(spectrogramArray.apply(vector).map(AbsSquared.FUNCTION));
  }
}
