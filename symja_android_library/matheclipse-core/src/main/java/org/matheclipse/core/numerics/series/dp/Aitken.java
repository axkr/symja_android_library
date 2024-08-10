package org.matheclipse.core.numerics.series.dp;

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
public final class Aitken extends SeriesAlgorithm {

  private int status;
  private double xx, dx, dd, xp, alpha;
  private final double[] x;
  private final double[][] s, ds, ts, dts;

  public Aitken(final double tolerance, final int maxIters, final int patience) {
    super(tolerance, maxIters, patience);
    x = new double[maxIters];
    s = new double[2][maxIters + 1];
    ds = new double[2][maxIters + 1];
    ts = new double[2][maxIters + 1];
    dts = new double[2][maxIters + 1];
  }

  @Override
  public final double next(final double e, final double term) {
    if (myIndex == 0) {
      xx = dx = dd = 0.0;
    }
    final double xxp = xx;
    x[myIndex] = xx = term;
    if (myIndex == 0) {
      dx = xx;
    } else if (myIndex == 1) {
      final double dxp = dx;
      dx = xx - xxp;
      dd = dx / (dx - dxp);
    } else {
      final double dxp = dx;
      final double ddp = dd;
      dx = xx - xxp;
      dd = dx / (dx - dxp);
      alpha = 1.0 / (dd - ddp) + 1.0;
      alpha = aitken(alpha, ts, dts, myIndex - 1, -2.0);
    }
    xp = xx;
    if (myIndex >= 2) {
      for (int m = 1; m <= myIndex + 1; ++m) {
        xp = aitken(x[m - 1], s, ds, m, alpha);
      }
    }
    ++myIndex;
    if (status == 1) {
      return Double.NaN;
    } else {
      return xp;
    }
  }

  private final double aitken(final double xx, final double[][] s, final double[][] ds, final int n,
      final double theta) {
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
    ds[1][0] = xx - s[0][0];
    for (int k = 1; k <= (n >> 1); ++k) {
      final double w1 = ds[0][k - 1] * ds[1][k - 1];
      final double w2 = ds[1][k - 1] - ds[0][k - 1];
      if (Math.abs(w2) < TINY) {
        status = 1;
        return xx;
      }
      final int twok = k << 1;
      final double coef = ((twok - 1) - theta) / (twok - 2 - theta);
      s[1][k] = s[0][k - 1] - coef * w1 / w2;
      if (n != twok - 1) {
        ds[1][k] = s[1][k] - s[0][k];
      }
    }
    final int kopt = n >> 1;
    return s[1][kopt];
  }

  @Override
  public final String getName() {
    return "Modified Aitken";
  }
}
