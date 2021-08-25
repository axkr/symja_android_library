package org.matheclipse.core.reflection.system;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Fourier extends AbstractFunctionEvaluator {

  public Fourier() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr expr = ast.arg1();
    try {
      if (expr.isVector() >= 0) {
        final int n = ((IAST) expr).argSize();
        if (n == 0 || 0 != (n & (n - 1))) {
          return IOFunctions.printMessage(S.Fourier, "vpow2", F.List(expr), engine);
        }
        IAST result = fourier((IAST) expr, 1);
        if (result.isPresent()) {
          return result;
        }
      }
    } catch (RuntimeException rex) {
      //
    }
    // Argument `1` is not a non-empty list or rectangular array of numeric quantities.
    return IOFunctions.printMessage(S.Fourier, "fftl", F.List(expr), engine);
  }

  /**
   * Uses decimation-in-time or Cooley-Tukey FFT
   *
   * @param vector of length of power of 2
   * @param b is +1 for forward, and -1 for inverse transform
   * @return discrete Fourier transform of given vector
   */
  private static IAST fourier(IAST vector, int b) {
    final int n = vector.argSize();
    Complex[] array = Convert.list2Complex(vector);
    if (array == null) {
      return F.NIL;
    }
    //    FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.UNITARY);
    //    org.hipparchus.complex.Complex[] result = fft.transform(array, TransformType.FORWARD);
    //    return Object2Expr.convertComplex(true, result);

    int j = 0;
    for (int i = 0; i < n; ++i) {
      if (j > i) {
        Complex val = array[i];
        array[i] = array[j];
        array[j] = val;
      }
      int m = n >> 1;
      while (m > 0 && j >= m) {
        j -= m;
        m >>= 1;
      }
      j += m;
    }

    int mmax = 1;
    while (n > mmax) {
      int istep = mmax << 1;
      final double thalf = b * Math.PI / istep;
      final double wtemp = Math.sin(thalf);
      Complex wp = new Complex(-2 * wtemp * wtemp, Math.sin(thalf + thalf));
      Complex w = Complex.ONE;
      for (int m = 0; m < mmax; ++m) {
        for (int i = m; i < n; i += istep) {
          j = i + mmax;
          Complex temp = array[j].multiply(w);
          array[j] = array[i].subtract(temp);
          array[i] = array[i].add(temp);
        }
        w = w.add(w.multiply(wp));
      }
      mmax = istep;
    }
    return F.Divide(Convert.toVector(array), F.Sqrt(n));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
