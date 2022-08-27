// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.fft;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.ext.Integers;
import org.matheclipse.core.tensor.io.ScalarArray;

/**
 * Discrete Fourier transform of a vector. Functionality works also for vectors with entries of type
 * {@link Quantity}.
 *
 * <p>
 * In the tensor library, the Fourier transform is restricted to vectors with length of power of 2.
 * 
 * <p>
 * Consistent with Mathematica: Mathematica::Fourier[{}] throws an Exception
 * 
 */
public class Fourier {

  /**
   * @param vector of length of power of 2
   * @return discrete Fourier transform of given vector
   */
  public static IAST of(IAST vector) {
    return of(vector, 1);
  }

  /**
   * Hint: uses decimation-in-time or Cooley-Tukey FFT
   * 
   * @param vector of length of power of 2
   * @param b is +1 for forward, and -1 for inverse transform
   * @return discrete Fourier transform of given vector
   */
  public static IAST of(IAST vector, int b) {
    int n = vector.argSize();
    if (!Integers.isPowerOf2(n)) {
      throw new IllegalArgumentException("vector length is not a power of two");
      // throw new Throw(vector); // vector length is not a power of two
    }
    IExpr[] array = ScalarArray.ofVector(vector);
    for (int j = 0, i = 0; i < n; ++i) {
      if (j > i) {
        IExpr val = array[i];
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
    for (int mmax = 1; mmax < n; mmax <<= 1) {
      int istep = mmax << 1;
      double thalf = b * Math.PI / istep;
      double wtemp = Math.sin(thalf);
      IExpr wp = F.complexNum(1.0 - 2.0 * wtemp * wtemp, Math.sin(thalf + thalf));
      IExpr w = wp.one();
      for (int m = 0; m < mmax; ++m) {
        for (int i = m; i < n; i += istep) {
          int j = i + mmax;
          IExpr temp = array[j].multiply(w);
          array[j] = array[i].subtract(temp);
          array[i] = array[i].add(temp);
        }
        w = w.multiply(wp);
      }
    }
    return F.List(array).map(x -> x.divide(n));
  }
}
