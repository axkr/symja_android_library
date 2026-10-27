package org.matheclipse.core.numerics.series.dp.complex;

import org.hipparchus.complex.Complex;

/**
 * Implements a modified adaptive Aitken delta^2 process for estimating infinite series, based on
 * the method in [1].
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Osada, Naoki. Acceleration methods for slowly convergent sequences and their
 * applications. Diss. PhD thesis, Nagoya University, 1993.</li>
 * </ul>
 * </p>
 */
public final class AitkenComplex extends OsadaAdaptiveAlgorithmComplex {

  private final Complex[][] s, ds, ts, dts;

  public AitkenComplex(final double tolerance, final int maxIters, final int patience) {
    super(tolerance, maxIters, patience);
    s = new Complex[2][maxIters + 1];
    ds = new Complex[2][maxIters + 1];
    ts = new Complex[2][maxIters + 1];
    dts = new Complex[2][maxIters + 1];
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < maxIters + 1; j++) {
        s[i][j] = Complex.ZERO;
        ds[i][j] = Complex.ZERO;
        ts[i][j] = Complex.ZERO;
        dts[i][j] = Complex.ZERO;
      }
    }
  }

  @Override
  protected final Complex transform(final Complex xx, final boolean alphaTable, final int n,
      final Complex theta) {
    return alphaTable ? aitken(xx, ts, dts, n, theta) : aitken(xx, s, ds, n, theta);
  }

  // CPD-OFF double and Complex variants of the same algorithm
  private final Complex aitken(final Complex xx, final Complex[][] s, final Complex[][] ds,
      final int n, final Complex theta) {
    status = 0;
    if (n != 1) {
      final int kend = (n - 1) >> 1;
      System.arraycopy(s[1], 0, s[0], 0, kend + 1);
      if ((n & 1) == 0) {
        System.arraycopy(ds[1], 0, ds[0], 0, kend);
      } else {
        System.arraycopy(ds[1], 0, ds[0], 0, kend + 1);
      }
    }
    s[1][0] = xx;
    if (n == 1) {
      ds[1][0] = xx;
      return xx;
    }
    ds[1][0] = xx.subtract(s[0][0]);
    for (int k = 1; k <= (n >> 1); ++k) {
      final Complex w1 = ds[0][k - 1].multiply(ds[1][k - 1]);
      final Complex w2 = ds[1][k - 1].subtract(ds[0][k - 1]);
      if (w2.norm() < TINY) {
        status = 1;
        return xx;
      }
      final int twok = k << 1;
      final Complex coef =
          (new Complex(twok - 1).subtract(theta)).divide(new Complex(twok - 2).subtract(theta));
      s[1][k] = s[0][k - 1].subtract(coef.multiply(w1).divide(w2));
      if (n != twok - 1) {
        ds[1][k] = s[1][k].subtract(s[0][k]);
      }
    }
    final int kopt = n >> 1;
    return s[1][kopt];
  }
  // CPD-ON

  @Override
  public final String getName() {
    return "Modified Aitken Complex";
  }
}
