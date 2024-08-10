package org.matheclipse.core.numerics.series.dp.complex;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.numerics.utils.Constants;
import org.matheclipse.core.numerics.utils.SimpleMath;

/**
 * This is an implementation of Algorithm 1 in [1] for evaluating infinite series whose terms have
 * alternating signs.
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Cohen, Henri, Fernando Rodriguez Villegas, and Don Zagier. "Convergence acceleration of
 * alternating series." Experimental mathematics 9.1 (2000): 3-12.</li>
 * </ul>
 * </p>
 */
public final class CohenComplex extends SeriesAlgorithmComplex {

  private final Complex[] myTab;
  private Complex mySign0;

  public CohenComplex(final double tolerance, final int maxIters, final int patience) {
    super(tolerance, maxIters, patience);
    myTab = new Complex[maxIters];
  }

  @Override
  public final Complex next(final Complex e, final Complex term) {

    // add next element
    final int n = myIndex + 1;
    myTab[myIndex] = e.abs();

    // record the sign of the first term, since this method
    // requires the first term of the sequence to be positive
    if (myIndex == 0) {
      mySign0 = e.sign();
      if (mySign0.isZero()) {
        mySign0 = Complex.ONE;
      }
    }

    // initialize d value
    double d = SimpleMath.pow(3.0 + 2.0 * Constants.SQRT2, n);
    d = 0.5 * (d + 1.0 / d);

    // apply the Chebychef polynomial recursively (Algorithm 1)
    double b = -1.0;
    double c = -d;
    Complex s = Complex.ZERO;
    for (int k = 0; k < n; ++k) {
      c = b - c;
      s = s.add(myTab[k].multiply(c));
      final double numer = (k + n) * (k - n);
      final double denom = (k + 0.5) * (k + 1);
      b *= numer / denom;
    }
    ++myIndex;
    return mySign0.multiply(s).divide(d);
  }

  @Override
  public final String getName() {
    return "Cohen";
  }
}
