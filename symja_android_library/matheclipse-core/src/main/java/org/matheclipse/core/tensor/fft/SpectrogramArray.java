// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.fft;

import org.matheclipse.core.builtin.WindowFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.api.TensorUnaryOperator;

/**
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/SpectrogramArray.html">SpectrogramArray</a>
 * 
 * @see WindowFunctions
 */
public class SpectrogramArray implements TensorUnaryOperator {

  /**
   * @param vector
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return
   */
  public static IAST of(IAST vector, WindowFunctions.WindowFunction window) {
    int windowLength = default_windowLength(vector.argSize());
    return of(windowLength, default_offset(windowLength), window).apply(vector);
  }

  /**
   * @param vector_length
   * @return power of 2
   */
  private static int default_windowLength(int vector_length) {
    // int num = Round.intValueExact(LOG2.apply(Sqrt.FUNCTION.apply(RealScalar.of(vector_length))));
    long num = Math.round(Math.log(Math.sqrt(vector_length)) / Math.log(2));
    return 1 << (num + 1L);
  }

  /**
   * Mathematica default
   * 
   * @param vector
   * @return
   * @throws Exception if input is not a vector
   */
  public static IAST of(IAST vector) {
    return of(vector, WindowFunctions.DIRICHLET);
  }

  /**
   * @param vector
   * @param window for instance {@link HannWindow#FUNCTION}
   * @return truncated and transposed spectrogram array for visualization
   * @throws Exception if input is not a vector
   */
  // public static IAST half_abs(IAST vector, ScalarUnaryOperator window) {
  // IAST tensor = of(vector, window);
  // int half = Unprotect.dimension1Hint(tensor) / 2;
  // return Tensors.vector(i -> tensor.get(IAST.ALL, half - i - 1).map(Abs.FUNCTION), half);
  // }

  // helper function
  private static int default_offset(int windowLength) {
    // return Round.intValueExact(F.QQ(windowLength, 3));
    return (int) Math.round((windowLength) / 3.0);
  }

  // ---
  /**
   * @param windowLength
   * @param offset
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return
   */
  public static TensorUnaryOperator of(int windowLength, int offset,
      WindowFunctions.WindowFunction window) {
    if (offset <= 0 || windowLength < offset) {
      throw new IllegalArgumentException("windowLength=" + windowLength + " offset=" + offset);
    }
    return new SpectrogramArray(windowLength, offset, window);
  }

  /**
   * @param windowLength
   * @param offset positive and not greater than windowLength
   * @return
   */
  public static TensorUnaryOperator of(int windowLength, int offset) {
    return of(windowLength, offset, WindowFunctions.DIRICHLET);
  }

  /**
   * @param windowDuration
   * @param samplingFrequency
   * @param offset positive
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return
   */
  public static TensorUnaryOperator of( //
      IExpr windowDuration, IExpr samplingFrequency, int offset,
      WindowFunctions.WindowFunction window) {
    return of(windowLength(windowDuration, samplingFrequency), offset, window);
  }

  /**
   * @param windowDuration
   * @param samplingFrequency
   * @param window for instance {@link DirichletWindow#FUNCTION}
   * @return spectrogram operator with default offset
   */
  public static TensorUnaryOperator of( //
      IExpr windowDuration, IExpr samplingFrequency, WindowFunctions.WindowFunction window) {
    int windowLength = windowLength(windowDuration, samplingFrequency);
    return of(windowLength, default_offset(windowLength), window);
  }

  // helper function
  private static int windowLength(IExpr windowDuration, IExpr samplingFrequency) {
    // return Round.intValueExact(windowDuration.multiply(samplingFrequency));
    return (int) Math.round(windowDuration.multiply(samplingFrequency).evalDouble());
  }

  // ---
  private final int windowLength;
  private final int offset;
  private final TensorUnaryOperator tensorUnaryOperator;
  private final IAST weights;

  private SpectrogramArray(int windowLength, int offset, WindowFunctions.WindowFunction window) {
    this.windowLength = windowLength;
    this.offset = offset;
    int highestOneBit = Integer.highestOneBit(windowLength);
    weights = StaticHelper.weights(windowLength, window);
    tensorUnaryOperator = windowLength == highestOneBit //
        ? t -> t //
        : t -> F.ConstantArray(F.C0, F.ZZ(highestOneBit * 2));// PadRight.zeros(highestOneBit * 2);
  }

  @Override // from TensorUnaryOperator
  public IAST apply(IAST vector) {
    return F.List(((IAST) S.Partition.of(EvalEngine.get(), vector, windowLength, offset)) //
        .mapThread(weights, (x, y) -> x.times(y)) //
        .map(x -> tensorUnaryOperator.apply((IAST) x)) //
        .map(x -> Fourier.of((IAST) x)));
  }

  @Override // from Object
  public String toString() {
    return "SpectrogramArray";
    // return MathematicaFormat.concise("SpectrogramArray", windowLength, offset,
    // tensorUnaryOperator);
  }
}
