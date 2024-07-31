package org.matheclipse.core.numerics.series.dp;

/**
 * Implements the Wynn Rho algorithm for convergence of infinite series
 * introduced in [1] and as described in [2].
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Wynn, Peter. "On a procrustean technique for the numerical
 * transformation of slowly convergent sequences and series." Mathematical
 * Proceedings of the Cambridge Philosophical Society. Vol. 52. No. 4. Cambridge
 * University Press, 1956.</li>
 * <li>[2] Osada, Naoki. Acceleration methods for slowly convergent sequences
 * and their applications. Diss. PhD thesis, Nagoya University, 1993.</li>
 * </ul>
 * </p>
 */
public final class WynnRho extends SeriesAlgorithm {

    private int status;
    private double xx, dx, dd, xp, alpha;
    private final double[] x;
    private final double[][] myTab, myTab2;

    public WynnRho(final double tolerance, final int maxIters, final int patience) {
	super(tolerance, maxIters, patience);
	x = new double[maxIters + 1];
	myTab = new double[2][maxIters + 1];
	myTab2 = new double[2][maxIters + 1];
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
	    alpha = wynnrho(alpha, myTab2, myIndex - 1, -2.0);
	}
	xp = xx;
	if (myIndex >= 2) {
	    for (int m = 1; m <= myIndex + 1; ++m) {
		xp = wynnrho(x[m - 1], myTab, m, alpha);
	    }
	}
	++myIndex;
	if (status == 1) {
	    return Double.NaN;
	} else {
	    return xp;
	}
    }

    private final double wynnrho(final double xx, final double[][] tab, final int n, final double theta) {
	status = 0;
	if (n != 1) {
	    System.arraycopy(tab[1], 0, tab[0], 0, n);
	}
	tab[1][0] = xx;
	if (n == 1) {
	    tab[1][1] = -theta / xx;
	    return xx;
	}
	double drho = tab[1][0] - tab[0][0];
	if (Math.abs(drho) < TINY) {
	    status = 1;
	    return xx;
	}
	tab[1][1] = -theta / drho;
	for (int k = 2; k <= n; ++k) {
	    drho = tab[1][k - 1] - tab[0][k - 1];
	    if (Math.abs(drho) < TINY) {
		if ((k & 1) == 0) {
		    status = 1;
		    return xx;
		} else {
		    return tab[1][k - 1];
		}
	    }
	    tab[1][k] = tab[0][k - 2] + (k - 1.0 - theta) / drho;
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
