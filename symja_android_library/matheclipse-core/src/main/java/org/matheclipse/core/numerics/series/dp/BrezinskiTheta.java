package org.matheclipse.core.numerics.series.dp;

/**
 * Implements Brezinski's Theta algorithm for convergence of infinite series
 * based on [1] and as described in [2].
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Brezinski, Claude. Acc�l�ration de la convergence en analyse
 * num�rique. Vol. 584. Springer, 2006.</li>
 * <li>[2] Weniger, Ernst Joachim. "Nonlinear sequence transformations for the
 * acceleration of convergence and the summation of divergent series." arXiv
 * preprint math/0306302 (2003).</li>
 * </ul>
 * </p>
 */
public final class BrezinskiTheta extends SeriesAlgorithm {

    private final double[] myTabA, myTabB;

    public BrezinskiTheta(final double tolerance, final int maxIters, final int patience) {
	super(tolerance, maxIters, patience);
	myTabA = new double[maxIters];
	myTabB = new double[maxIters];
    }

    @Override
    public final double next(final double e, final double term) {

	// first entry of table
	if (myIndex == 0) {
	    myTabA[0] = term;
	    ++myIndex;
	    return term;
	}

	// swapping the A and B arrays
	final double[] a, b;
	if ((myIndex & 1) == 0) {
	    a = myTabA;
	    b = myTabB;
	} else {
	    a = myTabB;
	    b = myTabA;
	}

	// the case n >= 1
	final int jmax = ((myIndex << 1) + 1) / 3;
	double aux1 = a[0];
	double aux2 = 0.0;
	a[0] = term;
	for (int j = 1; j <= jmax; ++j) {
	    final double aux3 = aux2;
	    aux2 = aux1;
	    if (j < jmax) {
		aux1 = a[j];
	    }
	    if ((j & 1) == 0) {
		final double denom = a[j - 1] - 2.0 * b[j - 1] + aux2;

		// correct for underflow
		if (Math.abs(denom) <= TINY) {
		    a[j] = HUGE;
		} else {
		    a[j] = aux3 + (b[j - 2] - aux3) * (a[j - 1] - b[j - 1]) / denom;
		}
	    } else {
		final double diff = a[j - 1] - b[j - 1];

		// correct for underflow
		if (Math.abs(diff) <= TINY) {
		    a[j] = HUGE;
		} else {
		    a[j] = aux3 + 1.0 / diff;
		}
	    }
	}
	++myIndex;

	// return result
	if ((jmax & 1) == 0) {
	    return a[jmax];
	} else {
	    return a[jmax - 1];
	}
    }

    @Override
    public final String getName() {
	return "Brezinski Theta";
    }
}
