package org.matheclipse.core.numerics.series.dp.complex;

import org.hipparchus.complex.Complex;

/**
 * Implements the Wynn Rho algorithm for convergence of infinite series introduced in [1] and as
 * described in [2].
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Wynn, Peter. "On a procrustean technique for the numerical transformation of slowly
 * convergent sequences and series." Mathematical Proceedings of the Cambridge Philosophical
 * Society. Vol. 52. No. 4. Cambridge University Press, 1956.</li>
 * <li>[2] Osada, Naoki. Acceleration methods for slowly convergent sequences and their
 * applications. Diss. PhD thesis, Nagoya University, 1993.</li>
 * </ul>
 * </p>
 */
public final class WynnRhoComplex extends SeriesAlgorithmComplex {

  private int status;
  private Complex xx, dx, dd, xp, alpha;
  private final Complex[] x;
  private final Complex[][] myTab, myTab2;

  public WynnRhoComplex(final double tolerance, final int maxIters, final int patience) {
    super(tolerance, maxIters, patience);
    x = new Complex[maxIters + 1];
    myTab = new Complex[2][maxIters + 1];
    myTab2 = new Complex[2][maxIters + 1];
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
      alpha = wynnrho(alpha, myTab2, myIndex - 1, new Complex(-2.0));
    }
    xp = xx;
    if (myIndex >= 2) {
      for (int m = 1; m <= myIndex + 1; ++m) {
        xp = wynnrho(x[m - 1], myTab, m, alpha);
      }
    }
    ++myIndex;
    if (status == 1) {
      return Complex.NaN;
    } else {
      return xp;
    }
  }

  private final Complex wynnrho(final Complex xx, final Complex[][] tab, final int n,
      final Complex theta) {
    status = 0;
    if (n != 1) {
      System.arraycopy(tab[1], 0, tab[0], 0, n);
    }
    tab[1][0] = xx;
    if (n == 1) {
      tab[1][1] = theta.negate().divide(xx);
      return xx;
    }
    Complex drho = tab[1][0].subtract(tab[0][0]);
    if (drho.norm() < TINY) {
      status = 1;
      return xx;
    }
    tab[1][1] = theta.negate().divide(drho);
    for (int k = 2; k <= n; ++k) {
      drho = tab[1][k - 1].subtract(tab[0][k - 1]);
      if (drho.norm() < TINY) {
        if ((k & 1) == 0) {
          status = 1;
          return xx;
        } else {
          return tab[1][k - 1];
        }
      }
      tab[1][k] = tab[0][k - 2].add((new Complex(k - 1.0).subtract(theta)).divide(drho));
    }
    if ((n & 1) == 0) {
      return tab[1][n];
    } else {
      return tab[1][n - 1];
    }
  }

  @Override
  public final String getName() {
    return "Wynn Rho";
  }
}
