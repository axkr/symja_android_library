package org.matheclipse.core.numerics.series.special;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import org.matheclipse.core.numerics.integral.Quadrature;
import org.matheclipse.core.numerics.series.dp.SeriesAlgorithm;
import org.matheclipse.core.numerics.utils.Sequences;
import org.matheclipse.core.numerics.utils.SimpleMath;

/**
 * 
 */
public final class EulerMaclaurin {

    public static enum EulerMaclaurinErrorCode {
	NO_ERROR, TOO_MANY_EVALS, BIN_TOO_LARGE, DERIVATIVE_ESTIMATION_FAILED, INTEGRAL_LOWER_BOUND_SEARCH_FAILED,
	SERIES_ACCELERATION_FAILED, PIECEWISE_INTEGRATION_FAILED
    }

    private static final double STEP = 1.4;
    private static final double DECAY = 0.1;
    private static final int WORK_MEMORY_FOR_RIDDER = 100;
    private static final int MAX_RANGE = 1 << 12;
    private static final double MAX_BIN = 1e30;

    private final boolean myVerbose;
    private final double myTol;
    private final Quadrature myQuad;
    private final SeriesAlgorithm myAccelerator;
    private final Function<? super Long, Double> myBins;

    private int myFEvals, myDEvals;
    private final double[] myWorkArray = new double[2];
    private List<EulerMaclaurinErrorCode> myErrorCodes;

    /**
     * 
     * @param tolerance
     * @param integrator
     * @param accelerator
     * @param binSequence
     */
    public EulerMaclaurin(final double tolerance, final Quadrature integrator, final SeriesAlgorithm accelerator,
	    final Function<? super Long, Double> binSequence, final boolean verbose) {
	myTol = tolerance;
	myQuad = integrator;
	myAccelerator = accelerator;
	myBins = binSequence;
	myVerbose = verbose;
    }

    /**
     * 
     * @param tolerance
     * @param integrator
     * @param binSequence
     */
    public EulerMaclaurin(final double tolerance, final Quadrature integrator, final SeriesAlgorithm accelerator,
	    final Function<? super Long, Double> binSequence) {
	this(tolerance, integrator, accelerator, binSequence, false);
    }

    /**
     * 
     * @return
     */
    public final int getEvaluations() {
	return myFEvals;
    }

    /**
     * 
     * @return
     */
    public final int getDEvaluations() {
	return myDEvals;
    }

    /*
     * 
     */
    public final List<EulerMaclaurinErrorCode> getErrorStack() {
	return Collections.unmodifiableList(myErrorCodes);
    }

    // ==========================================================================
    // SERIES EVALUATION USING EULER-MACLAURIN INTEGRATION
    // ==========================================================================
    /**
     * 
     * @param f
     * @param df
     * @param start
     * @return
     */
    public final double limit(final Function<? super Double, Double> f, final Function<? super Double, Double> df,
	    final long start) {
	myFEvals = myDEvals = 0;
	myErrorCodes = new ArrayList<>();

	// find an N large enough so that the error estimate <= tol
	final long n = determineLowerBound(f, df, start);
	if (n < 0) {
	    myErrorCodes.add(EulerMaclaurinErrorCode.INTEGRAL_LOWER_BOUND_SEARCH_FAILED);
	    return Double.NaN;
	}

	// estimate sum
	final double sum = finiteSum(f, start, n);

	// compute the two lower bounds using an integral from N + 1/2 to infinity
	final double integral = improperIntegral(f, n);
	final double lower = lowerBound(f, df, n);
	final double upper = upperBound(f, df, n);
	return 0.5 * (lower + upper) + sum + integral;
    }

    /**
     * 
     * @param f
     * @param start
     * @return
     */
    public final double limit(final Function<? super Double, Double> f, final long start) {
	return limit(f, null, start);
    }

    private final double improperIntegral(final Function<? super Double, Double> f, final long n) {
	myWorkArray[0] = 0.0;
	final Function<Long, Double> seq = k -> {

	    // compute bounds of integration according to provided bins
	    final double s1 = myWorkArray[0];
	    final double s2 = s1 + myBins.apply(k);
	    myWorkArray[0] = s2;
	    if (s2 > MAX_BIN) {
		myErrorCodes.add(EulerMaclaurinErrorCode.BIN_TOO_LARGE);
		return Double.NaN;
	    }

	    // perform a change of variable to integrate on [0, 1]
      final DoubleUnaryOperator f01 = t -> {
		final double interp = (1.0 - t) * s1 + t * s2;
		return f.apply(n + 0.5 + interp);
	    };

	    // apply numerical integration to this new function
	    double integrand = myQuad.integrate(f01, 0.0, 1.0).estimate;
	    if (Double.isNaN(integrand)) {
		myErrorCodes.add(EulerMaclaurinErrorCode.PIECEWISE_INTEGRATION_FAILED);
	    }
	    integrand *= (s2 - s1);
	    return integrand;
	};

	// accelerate the series of definite integrals
	final double result = myAccelerator.limit(Sequences.toIterable(seq, 1L), true).limit;
	if (Double.isNaN(result)) {
	    myErrorCodes.add(EulerMaclaurinErrorCode.SERIES_ACCELERATION_FAILED);
	}
	return result;
    }

    private final double finiteSum(final Function<? super Double, Double> f, final long start, final long end) {
	double sum = 0.0;
	for (long k = start; k <= end; ++k) {
	    sum += f.apply((double) k);
	}
	myFEvals += end - start + 1;
	return sum;
    }

    private final long determineLowerBound(final Function<? super Double, Double> f,
	    final Function<? super Double, Double> df, final long start) {
	long a = start;
	long b = MAX_RANGE;
	long n = -1L;

	// do binary search to find a suitable bound
	while (a != b) {
	    final long mid = SimpleMath.average(a, b);
	    final double error = estimateError(f, df, mid);
	    if (Double.isNaN(error)) {
		return -1L;
	    } else if (!Double.isFinite(error)) {
		b = mid;
	    } else if (error > myTol) {
		a = mid + 1L;
	    } else {
		b = n = mid;
	    }
	}
	final double error = estimateError(f, df, a);
	if (error <= myTol) {
	    n = a;
	}
	if (myVerbose) {
	    System.out.println("Lower bound of integral = " + n + " with error = " + error + ".");
	}
	return n;
    }

    // ==========================================================================
    // ERROR ESTIMATION FOR MACLAURIN FORMULA
    // ==========================================================================
    private final double estimateError(final Function<? super Double, Double> f,
	    final Function<? super Double, Double> df, final long x) {
	return Math.abs(upperBound(f, df, x) - lowerBound(f, df, x)) / 2.0;
    }

    private final double lowerBound(final Function<? super Double, Double> f, final Function<? super Double, Double> df,
	    final long x) {
	return deriv(f, df, -0.5 + x) / 24.0;
    }

    private final double upperBound(final Function<? super Double, Double> f, final Function<? super Double, Double> df,
	    final long x) {
	return deriv(f, df, +1.5 + x) / 24.0;
    }

    // ==========================================================================
    // APPROXIMATE AND EXACT NUMERICAL DIFFERENTIATION
    // ==========================================================================
    private final double deriv(final Function<? super Double, Double> f, final Function<? super Double, Double> df,
	    final double x) {
	if (df == null) {
	    return derivRidder(f, x);
	} else {
	    ++myDEvals;
	    return df.apply(x);
	}
    }

    private final double derivRidder(final Function<? super Double, Double> f, final double x) {
	double h = 1.0;
	final double[][] work = new double[WORK_MEMORY_FOR_RIDDER][WORK_MEMORY_FOR_RIDDER];
	while (h > Double.MIN_VALUE / DECAY) {
	    updateRidder(f, x, h, WORK_MEMORY_FOR_RIDDER, work);
	    if (Double.isNaN(myWorkArray[0])) {
		break;
	    } else if (Math.abs(myWorkArray[1]) <= myTol) {
		return myWorkArray[0];
	    }
	    h *= DECAY;
	}
	myErrorCodes.add(EulerMaclaurinErrorCode.DERIVATIVE_ESTIMATION_FAILED);
	return Double.NaN;
    }

    private final void updateRidder(final Function<? super Double, Double> f, final double x, double s, final int iwork,
	    final double[][] work) {
	myWorkArray[0] = myWorkArray[1] = 0.0;
	double result = 0.0;
	double error = Double.POSITIVE_INFINITY;
	final double step2 = STEP * STEP;

	// uses Ridder's extrapolation method to numerically differentiate
	work[0][0] = (f.apply(x + s) - f.apply(x - s)) / (2.0 * s);
	myFEvals += 2;
	for (int i = 1; i < iwork; ++i) {

	    // update tableau
	    s /= STEP;
	    work[0][i] = (f.apply(x + s) - f.apply(x - s)) / (2.0 * s);
	    myFEvals += 2;
	    double fx = step2;
	    for (int j = 1; j <= i; ++j) {

		// update the tableau
		work[j][i] = (work[j - 1][i] * fx - work[j - 1][i - 1]) / (fx - 1.0);
		fx *= step2;

		// check for a lower-error estimate
		final double dif1 = work[j][i] - work[j - 1][i];
		final double dif2 = work[j][i] - work[j - 1][i - 1];
		final double new_error = Math.max(Math.abs(dif1), Math.abs(dif2));
		if (new_error <= error) {
		    error = new_error;
		    result = work[j][i];
		}
	    }

	    // check whether we can return result
	    if (Math.abs(work[i][i] - work[i - 1][i - 1]) >= 2.0 * error) {
		myWorkArray[0] = result;
		myWorkArray[1] = error;
		return;
	    }
	}
	myWorkArray[0] = Double.NaN;
	myWorkArray[1] = Double.POSITIVE_INFINITY;
    }
}
