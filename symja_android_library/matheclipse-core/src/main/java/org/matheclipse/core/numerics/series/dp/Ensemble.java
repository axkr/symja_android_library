package org.matheclipse.core.numerics.series.dp;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.numerics.series.dp.Levin.RemainderSequence;
import org.matheclipse.core.numerics.series.dp.WynnEpsilon.ShanksMethod;

/**
 * An ensemble algorithm for evaluating the limits of sequences and series of
 * real values. This algorithms works by initializing a number of convergence
 * acceleration algorithms with different properties, and running them in
 * parallel. The estimates of the sequence's or corresponding series limit are
 * extracted from the instance that has converged first to a stable limit to the
 * desired tolerance.
 */
public final class Ensemble extends SeriesAlgorithm {

    private static final int PRINT_DIGITS = 18;

    private final int myPrint;
    private final List<SeriesAlgorithm> myMethods;
    private final List<Boolean> myForAlternating;
    private final List<Boolean> myForSequence;

    private int myMethodCount, myBestMethod;
    private boolean myIsAlternating, myCheckForSequenceOnly;
    private double myOldSignum;
    private boolean[] myExcept;
    private double[] myPrevEst, myEst;

    /**
     * Creates a new instance of an ensemble convergence acceleration algorithm.
     * 
     * @param tolerance     the smallest acceptable change in series evaluation in
     *                      consecutive iterations that indicates the algorithm has
     *                      converged
     * @param maxIters      the maximum number of sequence terms to evaluate before
     *                      giving up
     * @param patience      the number of iterations used to assess whether or not a
     *                      particular instance of convergence acceleration
     *                      algorithm used by this algorithm has converged
     * @param printProgress how often to print the progress of the current
     *                      algorithm, e.g. the estimate of a sequence's or series's
     *                      limit at each iteration according to each algorithm
     *                      instance
     */
    public Ensemble(final double tolerance, final int maxIters, final int patience, final int printProgress) {
	super(tolerance, maxIters, patience);
	myPrint = printProgress;
	myMethods = new ArrayList<>();
	myForAlternating = new ArrayList<>();
	myForSequence = new ArrayList<>();

	// only for alternating series
	myMethods.add(new Cohen(myTol, myMaxIters, 1));
	myForAlternating.add(true);
	myForSequence.add(false);
	myMethods.add(new Levin(myTol, myMaxIters, 1, RemainderSequence.D));
	myForAlternating.add(true);
	myForSequence.add(false);

	// for alternating and linear series
	myMethods.add(new Aitken(myTol, myMaxIters, 1));
	myForAlternating.add(false);
	myForSequence.add(true);
	myMethods.add(new WynnEpsilon(myTol, myMaxIters, 1, ShanksMethod.WYNN));
	myForAlternating.add(false);
	myForSequence.add(true);
	myMethods.add(new Levin(myTol, myMaxIters, 1, RemainderSequence.T));
	myForAlternating.add(false);
	myForSequence.add(false);

	// for logarithmic series
	myMethods.add(new Levin(myTol, myMaxIters, 1, RemainderSequence.U));
	myForAlternating.add(false);
	myForSequence.add(false);
	myMethods.add(new Levin(myTol, myMaxIters, 1, RemainderSequence.V));
	myForAlternating.add(false);
	myForSequence.add(false);
	myMethods.add(new Richardson(myTol, myMaxIters, 1));
	myForAlternating.add(false);
	myForSequence.add(false);
	myMethods.add(new BrezinskiTheta(myTol, myMaxIters, 1));
	myForAlternating.add(false);
	myForSequence.add(true);
	myMethods.add(new IteratedTheta(myTol, myMaxIters, 1));
	myForAlternating.add(false);
	myForSequence.add(true);
	myMethods.add(new WynnRho(myTol, myMaxIters, 1));
	myForAlternating.add(false);
	myForSequence.add(true);
	myMethods.add(new WynnEpsilon(myTol, myMaxIters, 1, ShanksMethod.ALTERNATING));
	myForAlternating.add(false);
	myForSequence.add(true);
    }

    public Ensemble(final double tolerance, final int maxIters, final int patience) {
	this(tolerance, maxIters, patience, 0);
    }

    @Override
    public final double next(final double e, final double term) {

	// ********************************************************************
	// INITIALIZATION
	// ********************************************************************
	if (myIndex == 0) {

	    // initialize variables
	    myIsAlternating = true;
	    myMethodCount = myMethods.size();
	    myOldSignum = Math.signum(e);
	    myPrevEst = new double[myMethodCount];
	    myEst = new double[myMethodCount];
	    myExcept = new boolean[myMethodCount];

	    // disable methods for series only
	    if (myCheckForSequenceOnly) {
		for (int m = 0; m < myMethodCount; ++m) {
		    if (!myForSequence.get(m)) {
			myExcept[m] = true;
			if (myPrint > 0) {
			    System.out.println("Disabling method " + myMethods.get(m).getName()
				    + " that is invalid for sequences");
			}
		    }
		}
		myCheckForSequenceOnly = false;
	    }

	    // initialize each method
	    for (int m = 0; m < myMethodCount; ++m) {
		if (!myExcept[m]) {
		    final SeriesAlgorithm method = myMethods.get(m);
		    method.myIndex = 0;
		    myEst[m] = method.next(e, term);
		}
	    }

	    // print header
	    if (myPrint > 0) {
		String line = "";
		line += pad("Index", PRINT_DIGITS + 5);
		for (final SeriesAlgorithm method : myMethods) {
		    line += "\t" + pad(method.getName(), PRINT_DIGITS + 5);
		}
		System.out.println(line);
	    }

	    // print values
	    if (myPrint > 0) {
		printRow();
	    }

	    ++myIndex;
	    return term;
	}

	// ********************************************************************
	// SEQUENCE INSPECTION
	// ********************************************************************
	// test for invalid value of series or sequence
	if (!Double.isFinite(term) || !Double.isFinite(e)) {
	    if (myPrint > 0) {
		System.out.println("Aborting due to NaN at iteration " + myIndex);
	    }
	    ++myIndex;
	    return Double.NaN;
	}

	// track the sign of the sequence
	final double signum = Math.signum(e);
	if (myIndex > 0 && myIsAlternating && signum * myOldSignum >= 0) {
	    myIsAlternating = false;
	}
	myOldSignum = signum;

	// exclude alternating series methods if the series does not alternate
	if (!myIsAlternating) {
	    for (int m = 0; m < myMethodCount; ++m) {
		if (!myExcept[m] && myForAlternating.get(m)) {
		    myExcept[m] = true;
		    if (myPrint > 0) {
			System.out.println("Disabling method " + myMethods.get(m).getName()
				+ " because series is not alternating at iteration " + myIndex);
		    }
		}
	    }
	}

	// ********************************************************************
	// MAIN UPDATE
	// ********************************************************************
	myBestMethod = -1;
	double bestError = Double.POSITIVE_INFINITY;
	for (int m = 0; m < myMethodCount; ++m) {
	    if (myExcept[m]) {
		continue;
	    }

	    // estimate the next terms
	    final SeriesAlgorithm method = myMethods.get(m);
	    myPrevEst[m] = myEst[m];
	    myEst[m] = method.next(e, term);

	    // if a method produces an invalid estimate it is excluded
	    if (!Double.isFinite(myEst[m])) {
		if (myPrint > 0) {
		    System.out.println(
			    "Disabling method " + method.getName() + " due to instability at iteration " + myIndex);
		}
		myExcept[m] = true;
		continue;
	    }

	    // estimate error and best method
	    final double error = Math.abs(myPrevEst[m] - myEst[m]);
	    if (error < bestError && myEst[m] != HUGE) {
		myBestMethod = m;
		bestError = error;
	    }
	}

	// ********************************************************************
	// FINALIZE THIS ITERATION
	// ********************************************************************
	if (myPrint > 0 && myIndex % myPrint == 0) {
	    printRow();
	}

	// return the method with the best convergence so far
	++myIndex;
	if (myBestMethod >= 0) {
	    return myEst[myBestMethod];
	} else {
	    return Double.NaN;
	}
    }

    @Override
    public final SeriesSolution limit(final Iterable<Double> seq, final boolean series, final int extrapolateStart) {
	myCheckForSequenceOnly = !series;
	final SeriesSolution result = super.limit(seq, series, extrapolateStart);
	if (myPrint > 0 && myBestMethod >= 0 && result.converged) {
	    System.out.println(
		    "Converged at iteration " + myIndex + " with method " + myMethods.get(myBestMethod).getName());
	}
	return result;
    }

    @Override
    public final String getName() {
	return "Ensemble";
    }

    private final void printRow() {
	String line = pad(myIndex + "", PRINT_DIGITS);
	for (int m = 0; m < myMethodCount; ++m) {
	    final String str;
	    if (myExcept[m]) {
		str = "-";
	    } else {
		str = Double.toString(myEst[m]);
	    }
	    line += "\t" + pad(str, PRINT_DIGITS);
	}
	System.out.println(line);
    }

    private static final String pad(final String str, final int len) {
	if (str.length() >= len) {
	    return str;
	}
	String result = str;
	for (int i = str.length() + 1; i <= len; ++i) {
	    result += " ";
	}
	return result;
    }
}
