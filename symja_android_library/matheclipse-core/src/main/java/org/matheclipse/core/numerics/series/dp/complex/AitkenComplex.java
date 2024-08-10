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
public final class AitkenComplex extends SeriesAlgorithmComplex {

  private int status;
  private Complex xx, dx, dd, xp, alpha;
  private final Complex[] x;
  private final Complex[][] s, ds, ts, dts;

  public AitkenComplex(final double tolerance, final int maxIters, final int patience) {
    super(tolerance, maxIters, patience);
    x = new Complex[maxIters];
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
  public final Complex next(final Complex e, final Complex term) {
    if (myIndex == 0) {
      xx = dx = dd = Complex.ZERO;
    }
    final Complex xxp = xx;
    x[myIndex] = xx = term;
    if (myIndex == 0) {
      dx = xx;
    } else if (myIndex == 1) {
      final Complex dxp = dx;
      dx = xx.subtract(xxp);
      dd = dx.divide(dx.subtract(dxp));
    } else {
      final Complex dxp = dx;
      final Complex ddp = dd;
      dx = xx.subtract(xxp);
      dd = dx.divide(dx.subtract(dxp));
      alpha = dd.subtract(ddp).reciprocal().add(1.0);
      alpha = aitken(alpha, ts, dts, myIndex - 1, new Complex(-2.0));
    }
    xp = xx;
    if (myIndex >= 2) {
      for (int m = 1; m <= myIndex + 1; ++m) {
        xp = aitken(x[m - 1], s, ds, m, alpha);
      }
    }
    ++myIndex;
    if (status == 1) {
      return Complex.NaN;
    } else {
      return xp;
    }
  }

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

  @Override
  public final String getName() {
    return "Modified Aitken Complex";
  }
}
