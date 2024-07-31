package org.matheclipse.core.numerics.series.dp;

import java.util.Iterator;

/**
 * Implements a generalized form of the Richardson extrapolation for evaluating
 * infinite series. This is implemented using a d-transform of the original
 * series and is called the W(m) algorithm [1].
 * 
 * <p>
 * The method in [1] defines an auxiliary increasing sequence of integers, R(l),
 * defined by the recursion R(l + 1) = (int)(sigma * R(l)) + s, where sigma and
 * s are two tuning parameters. The series are evaluated at each R(l) of the
 * auxiliary sequence and forms a subsequence of the original partial sums that
 * converges to the same limit. The d-transform is then applied to accelerate
 * this sequence to infinity.
 * </p>
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Ford, William & Sidi, Avram. (1987). An Algorithm for a
 * Generalization of the Richardson Extrapolation Process. SIAM Journal on
 * Numerical Analysis. 24. 10.1137/0724080.</li>
 * </ul>
 * </p>
 */
public final class Richardson extends SeriesAlgorithm {

    private final double mySigma;
    private final int myIncr;

    private int myPrevIndex;
    private final double[] myElements;

    private final int myM;
    private final double[] myG;
    private final double[][][] myPsiAI, myPsiQ, myPsiG;
    private int evals;

    /**
     * Creates an instance of the d-transform for a given auxiliary sequence.
     * 
     * @param tolerance the smallest acceptable change in series evaluation in
     *                  consecutive iterations that indicates the algorithm has
     *                  converged
     * @param maxIters  the maximum number of sequence terms to evaluate before
     *                  giving up
     * @param patience  the number of consecutive iterations required for the
     *                  tolerance criterion to be satisfied to stop the algorithm
     * @param m         the parameter in the paper; must be >= 1
     * @param sigma     the parameter sigma defining the auxiliary sequence R(l);
     *                  must be > 1
     * @param s         the parameter s defining the auxiliary sequence R(l); must
     *                  be >= 1
     */
    public Richardson(final double tolerance, final int maxIters, final int patience, final int m, final double sigma,
	    final int s) {
	super(tolerance, maxIters, patience);
	myM = m;
	mySigma = sigma;
	myIncr = s;

	myElements = new double[maxIters + 1];
	myG = new double[myM];
	myPsiAI = new double[maxIters + 1][2][2];
	myPsiQ = new double[maxIters + 1][myM][2];
	myPsiG = new double[maxIters + 1][myM][2];
    }

    /**
     * Creates an instance of the d-transform for a given auxiliary sequence with
     * sigma = 1. The case s = 1 is most suitable for alternating series or
     * quickly-converging series, while s > 1 is suitable for power series,
     * trigonometric series, and other series representing analytic functions, for
     * arguments close to the functions' singular values. [1] notes that s = 1 is OK
     * for arguments far away from the functions' singular values.
     * 
     * @param tolerance the smallest acceptable change in series evaluation in
     *                  consecutive iterations that indicates the algorithm has
     *                  converged
     * @param maxIters  the maximum number of sequence terms to evaluate before
     *                  giving up
     * @param patience  the number of consecutive iterations required for the
     *                  tolerance criterion to be satisfied to stop the algorithm
     * @param m         the parameter in the paper; must be >= 1
     * @param s         the parameter s defining the auxiliary sequence R(l); must
     *                  be >= 1
     */
    public Richardson(final double tolerance, final int maxIters, final int patience, final int m, final int s) {
	this(tolerance, maxIters, patience, m, 1.0, s);
    }

    /**
     * Creates an instance of the d-transform for a given auxiliary sequence with s
     * = 1. Most suitable for monotonic series that converge slowly.
     * 
     * @param tolerance the smallest acceptable change in series evaluation in
     *                  consecutive iterations that indicates the algorithm has
     *                  converged
     * @param maxIters  the maximum number of sequence terms to evaluate before
     *                  giving up
     * @param patience  the number of consecutive iterations required for the
     *                  tolerance criterion to be satisfied to stop the algorithm
     * @param m         the parameter in the paper; must be >= 1
     * @param sigma     the parameter sigma defining the auxiliary sequence R(l);
     *                  must be > 1
     */
    public Richardson(final double tolerance, final int maxIters, final int patience, final int m, final double sigma) {
	this(tolerance, maxIters, patience, m, sigma, 1);
    }

    /**
     * Creates an instance of the d-transform for the simplest auxiliary sequence
     * R(l) = l. Most suitable for alternating series.
     * 
     * @param tolerance the smallest acceptable change in series evaluation in
     *                  consecutive iterations that indicates the algorithm has
     *                  converged
     * @param maxIters  the maximum number of sequence terms to evaluate before
     *                  giving up
     * @param patience  the number of consecutive iterations required for the
     *                  tolerance criterion to be satisfied to stop the algorithm
     * @param m         the parameter in the paper; must be >= 1
     */
    public Richardson(final double tolerance, final int maxIters, final int patience, final int m) {
	this(tolerance, maxIters, patience, m, 1.0, 1);
    }

    /**
     * Creates an instance of the d-transform for the simplest auxiliary sequence
     * R(l) = l. Most suitable for alternating series.
     * 
     * @param tolerance the smallest acceptable change in series evaluation in
     *                  consecutive iterations that indicates the algorithm has
     *                  converged
     * @param maxIters  the maximum number of sequence terms to evaluate before
     *                  giving up
     * @param patience  the number of consecutive iterations required for the
     *                  tolerance criterion to be satisfied to stop the algorithm
     */
    public Richardson(final double tolerance, final int maxIters, final int patience) {
	this(tolerance, maxIters, patience, 2);
    }

    @Override
    public final double next(final double e, final double term) {

	// first m elements do not induce an update of g or psi
	myElements[myIndex] = e;
	++myIndex;
	if (myIndex < myM) {
	    return term;
	}

	// main updates for psi: in the iterative mode we can only use R(l) = l
	final int l = myIndex - myM;
	final int Rl = l;
	double al = 0.0;
	for (int i = 0; i <= Rl; ++i) {
	    al += myElements[i];
	}
	updateG(Rl);
	return updatePsi(al, Rl, l);
    }

    @Override
    public final SeriesSolution limit(final Iterable<Double> seq, final boolean series, final int extrapolateStart) {

	// the sequence will be consumed one element at time
	final Iterator<Double> it = seq.iterator();
	myPrevIndex = -1;
	myIndex = 0;
	evals = 0;

	// read the first elements in the partial sum
	double partialSum = 0.0;
	for (int i = 0; i < extrapolateStart; ++i) {
	    partialSum += it.next();
	    ++myIndex;
	    ++evals;
	}

	// extract the first element of the transformed series
	int Rl = computeR(0);
	double al = computePartialSum(it, Rl);
	if (Double.isNaN(al)) {
	    return new SeriesSolution(Double.NaN, Double.NaN, evals, false);
	}
	updateG(Rl);
	updatePsi(al, Rl, 0);

	// main loop
	double result = al;
	double oldResult = result;
	int convergeSteps = 0;
	for (int l = 1; l <= myMaxIters; ++l) {

	    // extract the next element of the transformed series
	    Rl = computeR(l);
	    al = computePartialSum(it, Rl);
	    if (Double.isNaN(al)) {
		break;
	    }
	    updateG(Rl);
	    result = updatePsi(al, Rl, l);

	    // check for convergence
	    if (l >= 2) {
		final double error = Math.abs(result - oldResult);
		if (error <= myTol) {
		    ++convergeSteps;
		} else {
		    convergeSteps = 0;
		}
		if (convergeSteps >= myPatience) {
		    return new SeriesSolution(result + partialSum, error, evals, true);
		}
	    }
	    oldResult = result;
	}

	// could not converge
	return new SeriesSolution(Double.NaN, Double.NaN, evals, false);
    }

    @Override
    public final String getName() {
	return "W(" + myM + ") Algorithm";
    }

    private final double computePartialSum(final Iterator<Double> it, final int R) {

	// cache the sequence elements so they only need to be consumed once
	final int newIndex = R + myM - 1;
	if (newIndex >= myMaxIters) {
	    return Double.NaN;
	}
	for (int i = 1 + myPrevIndex; i <= newIndex; ++i) {
	    myElements[i] = it.next();
	    ++myIndex;
	    ++evals;
	}
	myPrevIndex = newIndex;

	// compute the transformed series element a(l) (B.3)
	double al = 0.0;
	for (int i = 0; i <= R; ++i) {
	    al += myElements[i];
	}
	return al;
    }

    private final double updatePsi(final double term, final int R, final int l) {
	final int ic = (l + 1) & 1;
	final int ip = 1 - ic;
	myPsiAI[0][0][ip] = term / myG[0];
	myPsiAI[0][1][ip] = 1.0 / myG[0];
	myPsiQ[0][0][ip] = R + 1.0;
	for (int k = 1; k < myM; ++k) {
	    myPsiG[0][k - 1][ip] = myG[k] / myG[0];
	}
	myPsiG[0][myM - 1][ip] = 1.0 / (R + 1.0);

	double sign = -1.0;
	for (int p = 1; p <= l; ++p) {
	    double d = 0.0;
	    if (p <= myM) {
		d = myPsiG[p - 1][p - 1][ip] - myPsiG[p - 1][p - 1][ic];
		for (int i = p + 2; i <= myM + 1; ++i) {
		    myPsiG[p][i - 2][ip] = (myPsiG[p - 1][i - 2][ip] - myPsiG[p - 1][i - 2][ic]) / d;
		}
	    }
	    if (p < myM) {
		myPsiQ[p][p][ip] = sign / myPsiG[p][myM - 1][ip];
		sign = -sign;
	    }
	    for (int q = 1; q <= p - 1 && q <= myM - 1; ++q) {
		final double ps = myPsiQ[p - 2][q - 1][ic];
		final double dq = ps / myPsiQ[p - 1][q - 1][ic] - ps / myPsiQ[p - 1][q - 1][ip];
		myPsiQ[p][q][ip] = (myPsiQ[p - 1][q][ip] - myPsiQ[p - 1][q][ic]) / dq;
	    }
	    if (p > myM) {
		final double ps = myPsiQ[p - 2][myM - 1][ic];
		d = ps / myPsiQ[p - 1][myM - 1][ic] - ps / myPsiQ[p - 1][myM - 1][ip];
	    }
	    myPsiQ[p][0][ip] = (myPsiQ[p - 1][0][ip] - myPsiQ[p - 1][0][ic]) / d;
	    myPsiAI[p][0][ip] = (myPsiAI[p - 1][0][ip] - myPsiAI[p - 1][0][ic]) / d;
	    myPsiAI[p][1][ip] = (myPsiAI[p - 1][1][ip] - myPsiAI[p - 1][1][ic]) / d;
	}

	final double denom = myPsiAI[l][1][ip];
	if (Math.abs(denom) < TINY) {
	    return HUGE;
	} else {
	    return myPsiAI[l][0][ip] / denom;
	}
    }

    private final void updateG(final int R) {

	// compute the elements of the modified differences g(l) (B.4)
	for (int k = 0; k < myM; ++k) {
	    myG[k] = myElements[R + k];
	}
	for (int i = 2; i <= myM; ++i) {
	    for (int j = myM; j >= i; --j) {
		myG[j - 1] -= myG[j - 2];
	    }
	}
	double p = R + 1.0;
	for (int k = 0; k < myM; ++k) {
	    myG[k] *= p;
	    p *= (R + 1.0);
	}
    }

    private final int computeR(final int l) {
	int r = 0;
	for (int i = 0; i < l; ++i) {
	    r = (int) (mySigma * r) + myIncr;
	}
	return r;
    }
}
