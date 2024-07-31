package org.matheclipse.core.numerics.series.dp;

import org.matheclipse.core.numerics.utils.Constants;
import org.matheclipse.core.numerics.utils.SimpleMath;

/**
 * This is an implementation of Algorithm 1 in [1] for evaluating infinite
 * series whose terms have alternating signs.
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Cohen, Henri, Fernando Rodriguez Villegas, and Don Zagier.
 * "Convergence acceleration of alternating series." Experimental mathematics
 * 9.1 (2000): 3-12.</li>
 * </ul>
 * </p>
 */
public final class Cohen extends SeriesAlgorithm {

    private final double[] myTab;
    private double mySign0;

    public Cohen(final double tolerance, final int maxIters, final int patience) {
	super(tolerance, maxIters, patience);
	myTab = new double[maxIters];
    }

    @Override
    public final double next(final double e, final double term) {

	// add next element
	final int n = myIndex + 1;
	myTab[myIndex] = Math.abs(e);

	// record the sign of the first term, since this method
	// requires the first term of the sequence to be positive
	if (myIndex == 0) {
	    mySign0 = Math.signum(e);
	    if (mySign0 == 0.0) {
		mySign0 = 1.0;
	    }
	}

	// initialize d value
	double d = SimpleMath.pow(3.0 + 2.0 * Constants.SQRT2, n);
	d = 0.5 * (d + 1.0 / d);

	// apply the Chebychef polynomial recursively (Algorithm 1)
	double b = -1.0;
	double c = -d;
	double s = 0.0;
	for (int k = 0; k < n; ++k) {
	    c = b - c;
	    s += c * myTab[k];
	    final double numer = (k + n) * (k - n);
	    final double denom = (k + 0.5) * (k + 1);
	    b *= numer / denom;
	}
	++myIndex;
	return mySign0 * s / d;
    }

    @Override
    public final String getName() {
	return "Cohen";
    }
}
