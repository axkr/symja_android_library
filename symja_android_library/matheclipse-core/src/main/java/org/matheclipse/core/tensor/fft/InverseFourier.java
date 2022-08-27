// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.fft;

import org.matheclipse.core.interfaces.IAST;

/** consistent with Mathematica for input vectors of length of power of 2
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/InverseFourier.html">InverseFourier</a> */
public class InverseFourier {

  /** @param vector of length of power of 2
   * @return */
  public static IAST of(IAST vector) {
    return Fourier.of(vector, -1);
  }
}
