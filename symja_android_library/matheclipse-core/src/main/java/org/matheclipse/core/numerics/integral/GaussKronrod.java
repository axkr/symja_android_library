package org.matheclipse.core.numerics.integral;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.numerics.utils.Constants;
import org.matheclipse.core.numerics.utils.SimpleMath;

/**
 * Integrate a real function of one variable using a globally adaptive
 * Gauss-Kronrod quadrature rule. This algorithm is a translation of subroutines
 * from the QUADPACK library and well-documented in [1].
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Piessens, Robert, et al. QUADPACK: A subroutine package for automatic
 * integration. Vol. 1. Springer Science & Business Media, 2012.</li>
 * </ul>
 * </p>
 */
public final class GaussKronrod extends Quadrature {

    private static final double EPMACH = SimpleMath.D1MACH[4 - 1];
    private static final double UFLOW = SimpleMath.D1MACH[1 - 1];
    private static final double OFLOW = SimpleMath.D1MACH[2 - 1];

    private static final double[] WG21 = { //
	    0.066671344308688137593568809893332, 0.149451349150580593145776339657697,
	    0.219086362515982043995534934228163, 0.269266719309996355091226921569469,
	    0.295524224714752870173892994651338 };
    private static final double[] XGK21 = { //
	    0.995657163025808080735527280689003, 0.973906528517171720077964012084452,
	    0.930157491355708226001207180059508, 0.865063366688984510732096688423493,
	    0.780817726586416897063717578345042, 0.679409568299024406234327365114874,
	    0.562757134668604683339000099272694, 0.433395394129247190799265943165784,
	    0.294392862701460198131126603103866, 0.148874338981631210884826001129720,
	    0.000000000000000000000000000000000 };
    private static final double[] WGK21 = { //
	    0.011694638867371874278064396062192, 0.032558162307964727478818972459390,
	    0.054755896574351996031381300244580, 0.075039674810919952767043140916190,
	    0.093125454583697605535065465083366, 0.109387158802297641899210590325805,
	    0.123491976262065851077958109831074, 0.134709217311473325928054001771707,
	    0.142775938577060080797094273138717, 0.147739104901338491374841515972068,
	    0.149445554002916905664936468389821 };

    private static final double[] WG15I = { //
	    0.0000000000000000, 0.1294849661688697, 0.0000000000000000, //
	    0.2797053914892767, 0.0000000000000000, 0.3818300505051189, //
	    0.0000000000000000, 0.4179591836734694 };
    private static final double[] XGK15I = { //
	    0.9914553711208126, 0.9491079123427585, 0.8648644233597691, //
	    0.7415311855993944, 0.5860872354676911, 0.4058451513773972, //
	    0.2077849550078985, 0.0000000000000000 };
    private static final double[] WGK15I = { //
	    0.02293532201052922, 0.06309209262997855, 0.1047900103222502, //
	    0.1406532597155259, 0.1690047266392679, 0.1903505780647854, //
	    0.2044329400752989, 0.2094821410847278 };

    private final double myRelTol;

    /**
     * Creates a new instance of the Gauss-Kronrod quadrature integrator.
     * 
     * @param relativeTolerance the smallest acceptable relative change in integral
     *                          estimates in consecutive iterations that indicates
     *                          the algorithm has converged
     * @param tolerance         the smallest acceptable absolute change in integral
     *                          estimates in consecutive iterations that indicates
     *                          the algorithm has converged
     * @param maxEvaluations    the maximum number of evaluations of each function
     *                          permitted
     */
    public GaussKronrod(final double tolerance, final double relativeTolerance, final int maxEvaluations) {
	super(tolerance, maxEvaluations);
	myRelTol = relativeTolerance;
    }

    public GaussKronrod(final double tolerance, final int maxEvaluations) {
	this(tolerance, 50.0 * Constants.EPSILON, maxEvaluations);
    }

    @Override
    protected final QuadratureResult properIntegral(final DoubleUnaryOperator f, final double a,
	    final double b) {

	// prepare variables
	final double[] result = new double[1];
	final double[] abserr = new double[1];
	final int[] neval = new int[1];
	final int[] ier = new int[1];

	// call main subroutine
	dqags(f, a, b, myTol, myRelTol, result, abserr, neval, ier, myMaxEvals);
	return new QuadratureResult(result[0], abserr[0], neval[0], ier[0] == 0);
    }

    @Override
    public final QuadratureResult integrate(DoubleUnaryOperator f, final double a, final double b) {

	// null integral
	if (a == b) {
	    return new QuadratureResult(0.0, 0.0, 0, true);
	}

	// make sure a < b
	if (a > b) {
	    return integrate(f, b, a);
	}

	// both are finite
	if (Double.isFinite(a) && Double.isFinite(b)) {
	    return properIntegral(f, a, b);
	}

	// infinite bounds case
	final double[] result = new double[1], abserr = new double[1];
	final int[] neval = new int[1], ier = new int[1];
	final int inf;
	final double bound;
	if (Double.isInfinite(a) && Double.isInfinite(b)) {
	    inf = 2;
	    bound = 0.0;
	} else if (Double.isFinite(a)) {
	    inf = 1;
	    bound = a;
	} else {
	    inf = -1;
	    bound = b;
	}

	// call main subroutine
	dqagi1(f, bound, inf, myTol, myRelTol, result, abserr, neval, ier, myMaxEvals);
	return new QuadratureResult(result[0], abserr[0], neval[0], ier[0] == 0);
    }

    @Override
    public final String getName() {
	return "Gauss-Kronrod";
    }

    // *******************************************************************************
    // FINITE BOUNDS INTEGRATION (DQAGS)
    // *******************************************************************************
    private static final void dqags(final DoubleUnaryOperator f, final double a, final double b,
	    final double epsabs, final double epsrel, final double[] result, final double[] abserr, final int[] neval,
	    final int[] ier, final int limit) {
	final int[] last = new int[1];

	// CHECK VALIDITY OF LIMIT AND LENW
	ier[0] = 6;
	neval[0] = last[0] = 0;
	result[0] = abserr[0] = 0.0;
	if (limit < 1) {
	    return;
	}

	// PREPARE CALL FOR DQAGSE
	final double[] alist = new double[limit], blist = new double[limit], rlist = new double[limit],
		elist = new double[limit];
	final int[] iwork = new int[limit];
	dqagse(f, a, b, epsabs, epsrel, limit, result, abserr, neval, ier, alist, blist, rlist, elist, iwork, last);
    }

    private static final void dqagse(final DoubleUnaryOperator f, final double a, final double b,
	    final double epsabs, final double epsrel, final int limit, final double[] result, final double[] abserr,
	    final int[] neval, final int[] ier, final double[] alist, final double[] blist, final double[] rlist,
	    final double[] elist, final int[] iord, final int[] last) {
	double area, area12, a1, a2, b1, b2, correc = 0.0, dres, epmach, erlarg = 0.0, erlast, errbnd, erro12, errsum,
		ertest = 0.0, oflow, small = 0.0, uflow;
	int id, ierro, iroff1, iroff2, iroff3, jupbnd, k, ksgn, ktmin;
	boolean extrap, noext;
	final double[] res3la = new double[3];
	final double[] rlist2 = new double[52];
	final double[] abseps = new double[1], area1 = new double[1], area2 = new double[1], defabs = new double[1],
		defab1 = new double[1], defab2 = new double[1], errmax = new double[1], error1 = new double[1],
		error2 = new double[1], resabs = new double[1], reseps = new double[1];
	final int[] maxerr = new int[1], nrmax = new int[1], numrl2 = new int[1], nres = new int[1];

	epmach = EPMACH;

	// TEST ON VALIDITY OF PARAMETERS
	ier[0] = neval[0] = last[0] = 0;
	result[0] = abserr[0] = 0.0;
	alist[1 - 1] = a;
	blist[1 - 1] = b;
	rlist[1 - 1] = elist[1 - 1] = 0.0;
	if (epsabs <= 0.0 && epsrel < Math.max(50.0 * epmach, 0.5e-28)) {
	    ier[0] = 6;
	    return;
	}

	// FIRST APPROXIMATION TO THE INTEGRAL
	uflow = UFLOW;
	oflow = OFLOW;
	ierro = 0;
	dqk21(f, a, b, result, abserr, defabs, resabs);

	// TEST ON ACCURACY
	dres = Math.abs(result[0]);
	errbnd = Math.max(epsabs, epsrel * dres);
	last[0] = 1;
	rlist[1 - 1] = result[0];
	elist[1 - 1] = abserr[0];
	iord[1 - 1] = 1;
	if (abserr[0] <= 100.0 * epmach * defabs[0] && abserr[0] > errbnd) {
	    ier[0] = 2;
	}
	if (limit == 1) {
	    ier[0] = 1;
	}
	if (ier[0] != 0 || (abserr[0] <= errbnd && abserr[0] != resabs[0]) || abserr[0] == 0.0) {
	    neval[0] = 42 * last[0] - 21;
	    return;
	}

	// INITIALIZATION
	rlist2[1 - 1] = result[0];
	errmax[0] = abserr[0];
	maxerr[0] = 1;
	area = result[0];
	errsum = abserr[0];
	abserr[0] = oflow;
	nrmax[0] = 1;
	nres[0] = 0;
	numrl2[0] = 2;
	ktmin = 0;
	extrap = noext = false;
	iroff1 = iroff2 = iroff3 = 0;
	ksgn = -1;
	if (dres >= (1.0 - 50.0 * epmach) * defabs[0]) {
	    ksgn = 1;
	}

	// MAIN DO-LOOP
	for (last[0] = 2; last[0] <= limit; ++last[0]) {

	    // BISECT THE SUBINTERVAL WITH THE NRMAX-TH LARGEST ERROR ESTIMATE
	    a1 = alist[maxerr[0] - 1];
	    b1 = 0.5 * (alist[maxerr[0] - 1] + blist[maxerr[0] - 1]);
	    a2 = b1;
	    b2 = blist[maxerr[0] - 1];
	    erlast = errmax[0];
	    dqk21(f, a1, b1, area1, error1, resabs, defab1);
	    dqk21(f, a2, b2, area2, error2, resabs, defab2);

	    // IMPROVE PREVIOUS APPROXIMATIONS TO INTEGRAL AND ERROR AND TEST FOR ACCURACY
	    area12 = area1[0] + area2[0];
	    erro12 = error1[0] + error2[0];
	    errsum += erro12 - errmax[0];
	    area += area12 - rlist[maxerr[0] - 1];
	    if (defab1[0] != error1[0] && defab2[0] != error2[0]) {
		final double reltol = 1e-5 * Math.abs(area12);
		if (Math.abs(rlist[maxerr[0] - 1] - area12) <= reltol && erro12 >= 0.99 * errmax[0]) {
		    if (extrap) {
			++iroff2;
		    } else {
			++iroff1;
		    }
		}
		if (last[0] > 10 && erro12 > errmax[0]) {
		    ++iroff3;
		}
	    }
	    rlist[maxerr[0] - 1] = area1[0];
	    rlist[last[0] - 1] = area2[0];
	    errbnd = Math.max(epsabs, epsrel * Math.abs(area));

	    // TEST FOR ROUNDOFF ERROR AND EVENTUALLY SET ERROR FLAG
	    if (iroff1 + iroff2 >= 10 || iroff3 >= 20) {
		ier[0] = 2;
	    }
	    if (iroff2 >= 5) {
		ierro = 3;
	    }

	    // SET ERROR FLAG IN THE CASE THAT THE NUMBER OF SUBINTERVALS EQUALS LIMIT
	    if (last[0] == limit) {
		ier[0] = 1;
	    }

	    // SET ERROR FLAG IN THE CASE OF BAD INTEGRAND BEHAVIOUR
	    // AT A POINT OF THE INTEGRATION RANGE
	    if (Math.max(Math.abs(a1), Math.abs(b2)) <= (1.0 + 100.0 * epmach) * (Math.abs(a2) + 1000.0 * uflow)) {
		ier[0] = 4;
	    }

	    // APPEND THE NEWLY-CREATED INTERVALS TO THE LIST
	    if (error2[0] > error1[0]) {
		alist[maxerr[0] - 1] = a2;
		alist[last[0] - 1] = a1;
		blist[last[0] - 1] = b1;
		rlist[maxerr[0] - 1] = area2[0];
		rlist[last[0] - 1] = area1[0];
		elist[maxerr[0] - 1] = error2[0];
		elist[last[0] - 1] = error1[0];
	    } else {
		alist[last[0] - 1] = a2;
		blist[maxerr[0] - 1] = b1;
		blist[last[0] - 1] = b2;
		elist[maxerr[0] - 1] = error1[0];
		elist[last[0] - 1] = error2[0];
	    }

	    // CALL SUBROUTINE DQPSRT TO MAINTAIN THE DESCENDING ORDERING
	    // IN THE LIST OF ERROR ESTIMATES AND SELECT THE SUBINTERVAL
	    // WITH NRMAX-TH LARGEST ERROR ESTIMATE (TO BE BISECTED NEXT)
	    dqpsrt(limit, last[0], maxerr, errmax, elist, iord, nrmax);
	    if (errsum <= errbnd) {

		// COMPUTE GLOBAL INTEGRAL SUM
		result[0] = 0.0;
		for (k = 1; k <= last[0]; ++k) {
		    result[0] += rlist[k - 1];
		}
		abserr[0] = errsum;
		if (ier[0] > 2) {
		    --ier[0];
		}
		neval[0] = 42 * last[0] - 21;
		return;
	    }
	    if (ier[0] != 0) {
		break;
	    }
	    if (last[0] == 2) {
		small = Math.abs(b - a) * 0.375;
		erlarg = errsum;
		ertest = errbnd;
		rlist2[2 - 1] = area;
		continue;
	    }
	    if (noext) {
		continue;
	    }
	    erlarg -= erlast;
	    if (Math.abs(b1 - a1) > small) {
		erlarg += erro12;
	    }
	    if (!extrap) {

		// TEST WHETHER THE INTERVAL TO BE BISECTED NEXT IS THE SMALLEST INTERVAL
		if (Math.abs(blist[maxerr[0] - 1] - alist[maxerr[0] - 1]) > small) {
		    continue;
		}
		extrap = true;
		nrmax[0] = 2;
	    }

	    if (ierro != 3 && erlarg > ertest) {

		// THE SMALLEST INTERVAL HAS THE LARGEST ERROR.
		// BEFORE BISECTING DECREASE THE SUM OF THE ERRORS OVER THE
		// LARGER INTERVALS (ERLARG) AND PERFORM EXTRAPOLATION
		id = nrmax[0];
		jupbnd = last[0];
		if (last[0] > 2 + (limit >> 1)) {
		    jupbnd = limit + 3 - last[0];
		}
		boolean skipto90 = false;
		for (k = id; k <= jupbnd; ++k) {
		    maxerr[0] = iord[nrmax[0] - 1];
		    errmax[0] = elist[maxerr[0] - 1];
		    if (Math.abs(blist[maxerr[0] - 1] - alist[maxerr[0] - 1]) > small) {
			skipto90 = true;
			break;
		    }
		    ++nrmax[0];
		}
		if (skipto90) {
		    continue;
		}
	    }

	    // PERFORM EXTRAPOLATION
	    ++numrl2[0];
	    rlist2[numrl2[0] - 1] = area;
	    dqelg(numrl2, rlist2, reseps, abseps, res3la, nres);
	    ++ktmin;
	    if (ktmin > 5 && abserr[0] < 1e-3 * errsum) {
		ier[0] = 5;
	    }
	    if (abseps[0] < abserr[0]) {
		ktmin = 0;
		abserr[0] = abseps[0];
		result[0] = reseps[0];
		correc = erlarg;
		ertest = Math.max(epsabs, epsrel * Math.abs(reseps[0]));
		if (abserr[0] <= ertest) {
		    break;
		}
	    }

	    // PREPARE BISECTION OF THE SMALLEST INTERVAL
	    if (numrl2[0] == 1) {
		noext = true;
	    }
	    if (ier[0] == 5) {
		break;
	    }
	    maxerr[0] = iord[1 - 1];
	    errmax[0] = elist[maxerr[0] - 1];
	    nrmax[0] = 1;
	    extrap = false;
	    small *= 0.5;
	    erlarg = errsum;
	}

	// SET FINAL RESULT AND ERROR ESTIMATE
	if (abserr[0] == oflow) {

	    // COMPUTE GLOBAL INTEGRAL SUM
	    result[0] = 0.0;
	    for (k = 1; k <= last[0]; ++k) {
		result[0] += rlist[k - 1];
	    }
	    abserr[0] = errsum;
	    if (ier[0] > 2) {
		--ier[0];
	    }
	    neval[0] = 42 * last[0] - 21;
	    return;
	}

	if (ier[0] + ierro != 0) {
	    if (ierro == 3) {
		abserr[0] += correc;
	    }
	    if (ier[0] == 0) {
		ier[0] = 3;
	    }
	    if (result[0] != 0.0 && area != 0.0) {
		if (abserr[0] / Math.abs(result[0]) > errsum / Math.abs(area)) {

		    // COMPUTE GLOBAL INTEGRAL SUM
		    result[0] = 0.0;
		    for (k = 1; k <= last[0]; ++k) {
			result[0] += rlist[k - 1];
		    }
		    abserr[0] = errsum;
		    if (ier[0] > 2) {
			--ier[0];
		    }
		    neval[0] = 42 * last[0] - 21;
		    return;
		}
	    } else {
		if (abserr[0] > errsum) {

		    // COMPUTE GLOBAL INTEGRAL SUM
		    result[0] = 0.0;
		    for (k = 1; k <= last[0]; ++k) {
			result[0] += rlist[k - 1];
		    }
		    abserr[0] = errsum;
		    if (ier[0] > 2) {
			--ier[0];
		    }
		    neval[0] = 42 * last[0] - 21;
		    return;
		}
		if (area == 0.0) {
		    if (ier[0] > 2) {
			--ier[0];
		    }
		    neval[0] = 42 * last[0] - 21;
		    return;
		}
	    }
	}

	// TEST ON DIVERGENCE
	if (ksgn == -1 && Math.max(Math.abs(result[0]), Math.abs(area)) <= defabs[0] * 0.01) {
	    if (ier[0] > 2) {
		--ier[0];
	    }
	    neval[0] = 42 * last[0] - 21;
	    return;
	}
	if (0.01 > (result[0] / area) || (result[0] / area) > 100.0 || errsum > Math.abs(area)) {
	    ier[0] = 6;
	}
	if (ier[0] > 2) {
	    --ier[0];
	}
	neval[0] = 42 * last[0] - 21;
    }

    private static final void dqelg(final int[] n, final double[] epstab, final double[] result, final double[] abserr,
	    final double[] res3la, final int[] nres) {
	double delta1, delta2, delta3, epsinf, error, err1, err2, err3, e0, e1, e1abs, e2, e3, res, ss, tol1, tol2,
		tol3;
	int i, ib, ib2, ie, indx, k1, k2, k3, limexp, newelm, num;

	++nres[0];
	abserr[0] = OFLOW;
	result[0] = epstab[n[0] - 1];
	if (n[0] < 3) {
	    abserr[0] = Math.max(abserr[0], 5.0 * EPMACH * Math.abs(result[0]));
	    return;
	}

	limexp = 50;
	epstab[n[0] + 2 - 1] = epstab[n[0] - 1];
	newelm = (n[0] - 1) >> 1;
	epstab[n[0] - 1] = OFLOW;
	num = k1 = n[0];
	for (i = 1; i <= newelm; ++i) {
	    k2 = k1 - 1;
	    k3 = k1 - 2;
	    res = epstab[k1 + 2 - 1];
	    e0 = epstab[k3 - 1];
	    e1 = epstab[k2 - 1];
	    e2 = res;
	    e1abs = Math.abs(e1);
	    delta2 = e2 - e1;
	    err2 = Math.abs(delta2);
	    tol2 = Math.max(Math.abs(e2), e1abs) * EPMACH;
	    delta3 = e1 - e0;
	    err3 = Math.abs(delta3);
	    tol3 = Math.max(e1abs, Math.abs(e0)) * EPMACH;
	    if (err2 <= tol2 && err3 <= tol3) {

		// if e0, e1 and e2 are equal to within machine accuracy, convergence is
		// assumed.
		result[0] = res;
		abserr[0] = err2 + err3;
		abserr[0] = Math.max(abserr[0], 5.0 * EPMACH * Math.abs(result[0]));
		return;
	    }

	    e3 = epstab[k1 - 1];
	    epstab[k1 - 1] = e1;
	    delta1 = e1 - e3;
	    err1 = Math.abs(delta1);
	    tol1 = Math.max(e1abs, Math.abs(e3)) * EPMACH;

	    // if two elements are very close to each other, omit a part of the table by
	    // adjusting the value of n
	    if (err1 <= tol1 || err2 <= tol2 || err3 <= tol3) {
		n[0] = i + i - 1;
		break;
	    }
	    ss = 1.0 / delta1 + 1.0 / delta2 - 1.0 / delta3;
	    epsinf = Math.abs(ss * e1);

	    // test to detect irregular behaviour in the table, and eventually omit a part
	    // of the table adjusting the value of n
	    if (epsinf <= 0.1E-3) {
		n[0] = i + i - 1;
		break;
	    }

	    // compute a new element and eventually adjust the value of result.
	    res = e1 + 1.0 / ss;
	    epstab[k1 - 1] = res;
	    k1 -= 2;
	    error = err2 + Math.abs(res - e2) + err3;
	    if (error <= abserr[0]) {
		abserr[0] = error;
		result[0] = res;
	    }
	}

	// shift the table.
	if (n[0] == limexp) {
	    n[0] = ((limexp >> 1) << 1) - 1;
	}
	ib = 1;
	if (((num >> 1) << 1) == num) {
	    ib = 2;
	}
	ie = newelm + 1;
	for (i = 1; i <= ie; ++i) {
	    ib2 = ib + 2;
	    epstab[ib - 1] = epstab[ib2 - 1];
	    ib = ib2;
	}
	if (num != n[0]) {
	    indx = num - n[0] + 1;
	    for (i = 1; i <= n[0]; ++i) {
		epstab[i - 1] = epstab[indx - 1];
		++indx;
	    }
	}

	if (nres[0] >= 4) {

	    // compute error estimate
	    abserr[0] = Math.abs(result[0] - res3la[3 - 1]) + Math.abs(result[0] - res3la[2 - 1])
		    + Math.abs(result[0] - res3la[1 - 1]);
	    res3la[1 - 1] = res3la[2 - 1];
	    res3la[2 - 1] = res3la[3 - 1];
	    res3la[3 - 1] = result[0];
	} else {
	    res3la[nres[0] - 1] = result[0];
	    abserr[0] = OFLOW;
	}
	abserr[0] = Math.max(abserr[0], 5.0 * EPMACH * Math.abs(result[0]));
    }

    private static final void dqpsrt(final int limit, final int last, final int[] maxerr, final double[] ermax,
	    final double[] elist, final int[] iord, final int[] nrmax) {
	double errmax, errmin;
	int i, ibeg, ido, isucc, j, jbnd, jupbn, k;

	if (last <= 2) {
	    iord[1 - 1] = 1;
	    iord[2 - 1] = 2;

	    // set maxerr and ermax.
	    maxerr[0] = iord[nrmax[0] - 1];
	    ermax[0] = elist[maxerr[0] - 1];
	    return;
	}

	// this part of the routine is only executed if, due to a
	// difficult integrand, subdivision increased the error
	// estimate. in the normal case the insert procedure should
	// start after the nrmax-th largest error estimate.
	errmax = elist[maxerr[0] - 1];
	if (nrmax[0] != 1) {
	    ido = nrmax[0] - 1;
	    for (i = 1; i <= ido; ++i) {
		isucc = iord[nrmax[0] - 1];
		if (errmax <= elist[isucc - 1]) {
		    break;
		}
		iord[nrmax[0] - 1] = isucc;
		--nrmax[0];
	    }
	}

	// compute the number of elements in the list to be maintained
	// in descending order. this number depends on the number of
	// subdivisions still allowed.
	jupbn = last;
	if (last > ((limit >> 1) + 2)) {
	    jupbn = limit + 3 - last;
	}
	errmin = elist[last - 1];

	// insert errmax by traversing the list top-down,
	// starting comparison from the element elist(iord(nrmax+1)).
	jbnd = jupbn - 1;
	ibeg = nrmax[0] + 1;
	if (ibeg <= jbnd) {
	    for (i = ibeg; i <= jbnd; ++i) {
		isucc = iord[i - 1];
		if (errmax >= elist[isucc - 1]) {

		    // insert errmin by traversing the list bottom-up.
		    iord[i - 1 - 1] = maxerr[0];
		    k = jbnd;
		    for (j = 1; j <= jbnd; ++j) {
			isucc = iord[k - 1];
			if (errmin < elist[isucc - 1]) {
			    iord[k + 1 - 1] = last;

			    // set maxerr and ermax.
			    maxerr[0] = iord[nrmax[0] - 1];
			    ermax[0] = elist[maxerr[0] - 1];
			    return;
			}
			iord[k + 1 - 1] = isucc;
			--k;
		    }
		    iord[i - 1] = last;

		    // set maxerr and ermax.
		    maxerr[0] = iord[nrmax[0] - 1];
		    ermax[0] = elist[maxerr[0] - 1];
		    return;
		}
		iord[i - 1 - 1] = isucc;
	    }
	}
	iord[jbnd - 1] = maxerr[0];
	iord[jupbn - 1] = last;

	// set maxerr and ermax.
	maxerr[0] = iord[nrmax[0] - 1];
	ermax[0] = elist[maxerr[0] - 1];
    }

    private static final void dqk21(final DoubleUnaryOperator f, final double a, final double b,
	    final double[] result, final double[] abserr, final double[] resabs, final double[] resasc) {
	double absc, centr, dhlgth, fc, fsum, fval1, fval2, hlgth, resg, resk, reskh;
	int j, jtw, jtwm1;
	final double[] fv1 = new double[10];
	final double[] fv2 = new double[10];

	centr = 0.5 * (a + b);
	hlgth = 0.5 * (b - a);
	dhlgth = Math.abs(hlgth);

	// compute the 21-point kronrod approximation to the integral, and estimate the
	// absolute error.
	resg = 0.0;
    fc = f.applyAsDouble(centr);
	resk = WGK21[11 - 1] * fc;
	resabs[0] = Math.abs(resk);
	for (j = 1; j <= 5; ++j) {
	    jtw = j << 1;
	    absc = hlgth * XGK21[jtw - 1];
        fval1 = f.applyAsDouble(centr - absc);
        fval2 = f.applyAsDouble(centr + absc);
	    fv1[jtw - 1] = fval1;
	    fv2[jtw - 1] = fval2;
	    fsum = fval1 + fval2;
	    resg += WG21[j - 1] * fsum;
	    resk += WGK21[jtw - 1] * fsum;
	    resabs[0] += WGK21[jtw - 1] * (Math.abs(fval1) + Math.abs(fval2));
	}
	for (j = 1; j <= 5; ++j) {
	    jtwm1 = (j << 1) - 1;
	    absc = hlgth * XGK21[jtwm1 - 1];
        fval1 = f.applyAsDouble(centr - absc);
        fval2 = f.applyAsDouble(centr + absc);
	    fv1[jtwm1 - 1] = fval1;
	    fv2[jtwm1 - 1] = fval2;
	    fsum = fval1 + fval2;
	    resk += WGK21[jtwm1 - 1] * fsum;
	    resabs[0] += WGK21[jtwm1 - 1] * (Math.abs(fval1) + Math.abs(fval2));
	}
	reskh = resk * 0.5;
	resasc[0] = WGK21[11 - 1] * Math.abs(fc - reskh);
	for (j = 1; j <= 10; ++j) {
	    resasc[0] += WGK21[j - 1] * (Math.abs(fv1[j - 1] - reskh) + Math.abs(fv2[j - 1] - reskh));
	}
	result[0] = resk * hlgth;
	resabs[0] *= dhlgth;
	resasc[0] *= dhlgth;
	abserr[0] = Math.abs((resk - resg) * hlgth);
	if (resasc[0] != 0.0 && abserr[0] != 0.0) {
	    abserr[0] = resasc[0] * Math.min(10.0, Math.pow(200.0 * abserr[0] / resasc[0], 1.5));
	}
	if (resabs[0] > UFLOW / (50.0 * EPMACH)) {
	    abserr[0] = Math.max((EPMACH * 50.0) * resabs[0], abserr[0]);
	}
    }

    // *******************************************************************************
    // INFINITE BOUNDS INTEGRATION (DQAGI)
    // *******************************************************************************
    private static void dqk15i(final DoubleUnaryOperator f, final double boun, final int inf,
	    final double a, final double b, final double[] result, final double[] abserr, final double[] resabs,
	    final double[] resasc) {

	double absc, absc1, absc2, centr, dinf, epmach, fc, fsum, fval1, fval2, hlgth, resg, resk, reskh, tabsc1,
		tabsc2, uflow;
	int j;
	final double[] fv1 = new double[8], fv2 = new double[8];

	epmach = EPMACH;
	uflow = UFLOW;
	dinf = Math.min(1.0, inf);
	centr = 0.5 * (a + b);
	hlgth = 0.5 * (b - a);
	tabsc1 = boun + dinf * (1.0 - centr) / centr;
    fval1 = f.applyAsDouble(tabsc1);
	if (inf == 2) {
      fval1 += f.applyAsDouble(-tabsc1);
	}
	fc = (fval1 / centr) / centr;

	// COMPUTE THE 15-POINT KRONROD APPROXIMATION TO THE INTEGRAL, AND ESTIMATE THE
	// ERROR
	resg = WG15I[8 - 1] * fc;
	resk = WGK15I[8 - 1] * fc;
	resabs[0] = Math.abs(resk);
	for (j = 1; j <= 7; ++j) {
	    absc = hlgth * XGK15I[j - 1];
	    absc1 = centr - absc;
	    absc2 = centr + absc;
	    tabsc1 = boun + dinf * (1.0 - absc1) / absc1;
	    tabsc2 = boun + dinf * (1.0 - absc2) / absc2;
        fval1 = f.applyAsDouble(tabsc1);
        fval2 = f.applyAsDouble(tabsc2);
	    if (inf == 2) {
          fval1 += f.applyAsDouble(-tabsc1);
          fval2 += f.applyAsDouble(-tabsc2);
	    }
	    fval1 = (fval1 / absc1) / absc1;
	    fval2 = (fval2 / absc2) / absc2;
	    fv1[j - 1] = fval1;
	    fv2[j - 1] = fval2;
	    fsum = fval1 + fval2;
	    resg += WG15I[j - 1] * fsum;
	    resk += WGK15I[j - 1] * fsum;
	    resabs[0] += WGK15I[j - 1] * (Math.abs(fval1) + Math.abs(fval2));
	}

	reskh = resk * 0.5;
	resasc[0] = WGK15I[8 - 1] * Math.abs(fc - reskh);
	for (j = 1; j <= 7; ++j) {
	    resasc[0] += WGK15I[j - 1] * (Math.abs(fv1[j - 1] - reskh) + Math.abs(fv2[j - 1] - reskh));
	}
	result[0] = resk * hlgth;
	resasc[0] *= hlgth;
	resabs[0] *= hlgth;
	abserr[0] = Math.abs((resk - resg) * hlgth);
	if (resasc[0] != 0.0 && abserr[0] != 0.0) {
	    abserr[0] = resasc[0] * Math.min(1.0, Math.pow(200.0 * abserr[0] / resasc[0], 1.5));
	}
	if (resabs[0] > uflow / (50.0 * epmach)) {
	    abserr[0] = Math.max((epmach * 50.0) * resabs[0], abserr[0]);
	}
    }

    private static void dqagie(final DoubleUnaryOperator f, final double bound, final int inf,
	    final double epsabs, final double epsrel, final int limit, final double[] result, final double[] abserr,
	    final int[] neval, final int[] ier, final double[] alist, final double[] blist, final double[] rlist,
	    final double[] elist, final int[] iord, final int[] last) {

	double area, area12, a1, a2, boun, b1, b2, correc = 0.0, dres, epmach, erlarg = 0.0, erlast, errbnd, erro12,
		errsum, ertest = 0.0, oflow, small = 0.0, uflow;
	final double[] res3la = new double[4], rlist2 = new double[53], defabs = new double[1], resabs = new double[1],
		reseps = new double[1], area1 = new double[1], area2 = new double[1], defab1 = new double[1],
		defab2 = new double[1], error1 = new double[1], error2 = new double[1], errmax = new double[1],
		abseps = new double[1];
	int id, ierro, iroff1, iroff2, iroff3, jupbnd, k, ksgn, ktmin;
	final int[] maxerr = new int[1], nrmax = new int[1], numrl2 = new int[1], nres = new int[1];
	boolean extrap, noext;

	epmach = EPMACH;

	// TEST ON VALIDITY OF PARAMETERS
	ier[0] = neval[0] = last[0] = 0;
	result[0] = abserr[0] = 0.0;
	alist[1 - 1] = 0.0;
	blist[1 - 1] = 1.0;
	rlist[1 - 1] = elist[1 - 1] = 0.0;
	iord[1 - 1] = 0;
	if (epsabs <= 0.0 && epsrel < Math.max(50.0 * epmach, 0.5e-28)) {
	    ier[0] = 6;
	}
	if (ier[0] == 6) {
	    return;
	}

	// FIRST APPROXIMATION TO THE INTEGRAL
	// -----------------------------------
	//
	// DETERMINE THE INTERVAL TO BE MAPPED ONTO (0,1).
	// IF INF = 2 THE INTEGRAL IS COMPUTED AS I = I1+I2, WHERE
	// I1 = INTEGRAL OF F OVER (-INFINITY,0),
	// I2 = INTEGRAL OF F OVER (0,+INFINITY).
	boun = bound;
	if (inf == 2) {
	    boun = 0.0;
	}
	dqk15i(f, boun, inf, 0.0, 1.0, result, abserr, defabs, resabs);

	// TEST ON ACCURACY
	last[0] = 1;
	rlist[1 - 1] = result[0];
	elist[1 - 1] = abserr[0];
	iord[1 - 1] = 1;
	dres = Math.abs(result[0]);
	errbnd = Math.max(epsabs, epsrel * dres);
	if (abserr[0] <= 100.0 * epmach * defabs[0] && abserr[0] > errbnd) {
	    ier[0] = 2;
	}
	if (limit == 1) {
	    ier[0] = 1;
	}
	if (ier[0] != 0 || (abserr[0] <= errbnd && abserr[0] != resabs[0]) || abserr[0] == 0.0) {
	    neval[0] = 30 * last[0] - 15;
	    if (inf == 2) {
		neval[0] <<= 1;
	    }
	    if (ier[0] > 2) {
		--ier[0];
	    }
	    return;
	}

	// INITIALIZATION
	uflow = UFLOW;
	oflow = OFLOW;
	rlist2[1 - 1] = result[0];
	errmax[0] = abserr[0];
	maxerr[0] = 1;
	area = result[0];
	errsum = abserr[0];
	abserr[0] = oflow;
	nrmax[0] = 1;
	nres[0] = ktmin = 0;
	numrl2[0] = 2;
	extrap = noext = false;
	ierro = iroff1 = iroff2 = iroff3 = 0;
	ksgn = -1;
	if (dres >= (1.0 - 50.0 * epmach) * defabs[0]) {
	    ksgn = 1;
	}

	// MAIN DO-LOOP
	for (last[0] = 2; last[0] <= limit; ++last[0]) {

	    // BISECT THE SUBINTERVAL WITH NRMAX-TH LARGEST ERROR ESTIMATE
	    a1 = alist[maxerr[0] - 1];
	    b1 = 0.5 * (alist[maxerr[0] - 1] + blist[maxerr[0] - 1]);
	    a2 = b1;
	    b2 = blist[maxerr[0] - 1];
	    erlast = errmax[0];
	    dqk15i(f, boun, inf, a1, b1, area1, error1, resabs, defab1);
	    dqk15i(f, boun, inf, a2, b2, area2, error2, resabs, defab2);

	    // IMPROVE PREVIOUS APPROXIMATIONS TO INTEGRAL
	    // AND ERROR AND TEST FOR ACCURACY
	    area12 = area1[0] + area2[0];
	    erro12 = error1[0] + error2[0];
	    errsum += (erro12 - errmax[0]);
	    area += (area12 - rlist[maxerr[0] - 1]);
	    if (defab1[0] != error1[0] && defab2[0] != error2[0]) {
		final double reltol = 1e-5 * Math.abs(area12);
		if (Math.abs(rlist[maxerr[0] - 1] - area12) <= reltol && erro12 >= 0.99 * errmax[0]) {
		    if (extrap) {
			++iroff2;
		    } else {
			++iroff1;
		    }
		}
		if (last[0] > 10 && erro12 > errmax[0]) {
		    ++iroff3;
		}
	    }
	    rlist[maxerr[0] - 1] = area1[0];
	    rlist[last[0] - 1] = area2[0];
	    errbnd = Math.max(epsabs, epsrel * Math.abs(area));

	    // TEST FOR ROUNDOFF ERROR AND EVENTUALLY SET ERROR FLAG
	    if (iroff1 + iroff2 >= 10 || iroff3 >= 20) {
		ier[0] = 2;
	    }
	    if (iroff2 >= 5) {
		ierro = 3;
	    }

	    // SET ERROR FLAG IN THE CASE THAT THE NUMBER OF
	    // SUBINTERVALS EQUALS LIMIT
	    if (last[0] == limit) {
		ier[0] = 1;
	    }

	    // SET ERROR FLAG IN THE CASE OF BAD INTEGRAND BEHAVIOUR
	    // AT SOME POINTS OF THE INTEGRATION RANGE
	    if (Math.max(Math.abs(a1), Math.abs(b2)) <= (1.0 + 100.0 * epmach) * (Math.abs(a2) + 1.0e3 * uflow)) {
		ier[0] = 4;
	    }

	    // APPEND THE NEWLY-CREATED INTERVALS TO THE LIST
	    if (error2[0] > error1[0]) {
		alist[maxerr[0] - 1] = a2;
		alist[last[0] - 1] = a1;
		blist[last[0] - 1] = b1;
		rlist[maxerr[0] - 1] = area2[0];
		rlist[last[0] - 1] = area1[0];
		elist[maxerr[0] - 1] = error2[0];
		elist[last[0] - 1] = error1[0];
	    } else {
		alist[last[0] - 1] = a2;
		blist[maxerr[0] - 1] = b1;
		blist[last[0] - 1] = b2;
		elist[maxerr[0] - 1] = error1[0];
		elist[last[0] - 1] = error2[0];
	    }

	    // CALL SUBROUTINE DQPSRT TO MAINTAIN THE DESCENDING ORDERING
	    // IN THE LIST OF ERROR ESTIMATES AND SELECT THE SUBINTERVAL
	    // WITH NRMAX-TH LARGEST ERROR ESTIMATE (TO BE BISECTED NEXT)
	    dqpsrt(limit, last[0], maxerr, errmax, elist, iord, nrmax);
	    if (errsum <= errbnd) {

		// COMPUTE GLOBAL INTEGRAL SUM
		result[0] = 0.0;
		for (k = 1; k <= last[0]; ++k) {
		    result[0] += rlist[k - 1];
		}
		abserr[0] = errsum;
		neval[0] = 30 * last[0] - 15;
		if (inf == 2) {
		    neval[0] <<= 1;
		}
		if (ier[0] > 2) {
		    --ier[0];
		}
		return;
	    }
	    if (ier[0] != 0) {
		break;
	    }
	    if (last[0] == 2) {
		small = 0.375;
		erlarg = errsum;
		ertest = errbnd;
		rlist2[2 - 1] = area;
		continue;
	    }
	    if (noext) {
		continue;
	    }
	    erlarg -= erlast;
	    if (Math.abs(b1 - a1) > small) {
		erlarg += erro12;
	    }
	    if (!extrap) {

		// TEST WHETHER THE INTERVAL TO BE BISECTED NEXT IS THE
		// SMALLEST INTERVAL
		if (Math.abs(blist[maxerr[0] - 1] - alist[maxerr[0] - 1]) > small) {
		    continue;
		}
		extrap = true;
		nrmax[0] = 2;
	    }

	    if (ierro != 3 && erlarg > ertest) {

		// THE SMALLEST INTERVAL HAS THE LARGEST ERROR.
		// BEFORE BISECTING DECREASE THE SUM OF THE ERRORS OVER THE
		// LARGER INTERVALS (ERLARG) AND PERFORM EXTRAPOLATION
		id = nrmax[0];
		jupbnd = last[0];
		if (last[0] > 2 + (limit >> 1)) {
		    jupbnd = limit + 3 - last[0];
		}
		boolean skipto90 = false;
		for (k = id; k <= jupbnd; ++k) {
		    maxerr[0] = iord[nrmax[0] - 1];
		    errmax[0] = elist[maxerr[0] - 1];
		    if (Math.abs(blist[maxerr[0] - 1] - alist[maxerr[0] - 1]) > small) {
			skipto90 = true;
			break;
		    }
		    ++nrmax[0];
		}
		if (skipto90) {
		    continue;
		}
	    }

	    // PERFORM EXTRAPOLATION
	    ++numrl2[0];
	    rlist2[numrl2[0] - 1] = area;
	    dqelg(numrl2, rlist2, reseps, abseps, res3la, nres);
	    ++ktmin;
	    if (ktmin > 5 && abserr[0] < 1.0e-3 * errsum) {
		ier[0] = 5;
	    }
	    if (abseps[0] < abserr[0]) {
		ktmin = 0;
		abserr[0] = abseps[0];
		result[0] = reseps[0];
		correc = erlarg;
		ertest = Math.max(epsabs, epsrel * Math.abs(reseps[0]));
		if (abserr[0] <= ertest) {
		    break;
		}
	    }

	    // PREPARE BISECTION OF THE SMALLEST INTERVAL
	    if (numrl2[0] == 1) {
		noext = true;
	    }
	    if (ier[0] == 5) {
		break;
	    }
	    maxerr[0] = iord[1 - 1];
	    errmax[0] = elist[maxerr[0] - 1];
	    nrmax[0] = 1;
	    extrap = false;
	    small *= 0.5;
	    erlarg = errsum;
	}

	// SET FINAL RESULT AND ERROR ESTIMATE
	if (abserr[0] == oflow) {

	    // COMPUTE GLOBAL INTEGRAL SUM
	    result[0] = 0.0;
	    for (k = 1; k <= last[0]; ++k) {
		result[0] += rlist[k - 1];
	    }
	    abserr[0] = errsum;
	    neval[0] = 30 * last[0] - 15;
	    if (inf == 2) {
		neval[0] <<= 1;
	    }
	    if (ier[0] > 2) {
		--ier[0];
	    }
	    return;
	}

	if (ier[0] + ierro != 0) {
	    if (ierro == 3) {
		abserr[0] += correc;
	    }
	    if (ier[0] == 0) {
		ier[0] = 3;
	    }
	    if (result[0] != 0.0 && area != 0.0) {
		if (abserr[0] / Math.abs(result[0]) > errsum / Math.abs(area)) {

		    // COMPUTE GLOBAL INTEGRAL SUM
		    result[0] = 0.0;
		    for (k = 1; k <= last[0]; ++k) {
			result[0] += rlist[k - 1];
		    }
		    abserr[0] = errsum;
		    neval[0] = 30 * last[0] - 15;
		    if (inf == 2) {
			neval[0] <<= 1;
		    }
		    if (ier[0] > 2) {
			--ier[0];
		    }
		    return;
		}
	    } else {
		if (abserr[0] > errsum) {

		    // COMPUTE GLOBAL INTEGRAL SUM
		    result[0] = 0.0;
		    for (k = 1; k <= last[0]; ++k) {
			result[0] += rlist[k - 1];
		    }
		    abserr[0] = errsum;
		    neval[0] = 30 * last[0] - 15;
		    if (inf == 2) {
			neval[0] <<= 1;
		    }
		    if (ier[0] > 2) {
			--ier[0];
		    }
		    return;
		}
		if (area == 0.0) {
		    neval[0] = 30 * last[0] - 15;
		    if (inf == 2) {
			neval[0] <<= 1;
		    }
		    if (ier[0] > 2) {
			--ier[0];
		    }
		    return;
		}
	    }
	}

	// TEST ON DIVERGENCE
	if (ksgn == -1 && Math.max(Math.abs(result[0]), Math.abs(area)) <= defabs[0] * 0.01) {
	    neval[0] = 30 * last[0] - 15;
	    if (inf == 2) {
		neval[0] <<= 1;
	    }
	    if (ier[0] > 2) {
		--ier[0];
	    }
	    return;
	}
	if (0.01 > (result[0] / area) || (result[0] / area) > 100.0 || errsum > Math.abs(area)) {
	    ier[0] = 6;
	}
	neval[0] = 30 * last[0] - 15;
	if (inf == 2) {
	    neval[0] <<= 1;
	}
	if (ier[0] > 2) {
	    --ier[0];
	}
    }

    private static void dqagi1(final DoubleUnaryOperator f, final double boun, final int inf,
	    final double epsabs, final double epsrel, final double[] result, final double[] abserr, final int[] neval,
	    final int[] ier, final int limit) {

	final int[] last = new int[1];

	// CHECK VALIDITY OF LIMIT AND LENW
	ier[0] = 6;
	neval[0] = last[0] = 0;
	result[0] = abserr[0] = 0.0;
	if (limit < 1) {
	    return;
	}

	// PREPARE CALL FOR DQAGIE
	final double[] alist = new double[limit], blist = new double[limit], rlist = new double[limit],
		elist = new double[limit];
	final int[] iwork = new int[limit];
	dqagie(f, boun, inf, epsabs, epsrel, limit, result, abserr, neval, ier, alist, blist, rlist, elist, iwork,
		last);
    }
}
