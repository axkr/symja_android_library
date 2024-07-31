package org.matheclipse.core.numerics.integral;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.numerics.utils.Constants;
import org.matheclipse.core.numerics.utils.SimpleMath;

/**
 * An adaptive numerical integrator based on the Clenshaw-Curtis quadrature
 * rule. There are two implementations:
 * <ol>
 * <li>Havie : the integral is approximated by Chebychev polynomials over each
 * subinterval, as introduced in [1]. This code is a translation of the Fortran
 * subroutine by John Burkardt.</li>
 * <li>Oliver : a doubly-adaptive Clenshaw-Curtis algorithm also using Chebychev
 * polynomials as introduced in [2].</li>
 * </ol>
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] H�vie, T. "On a modification of the Clenshaw-Curtis quadrature
 * formula." BIT Numerical Mathematics 9.4 (1969): 338-350.</li>
 * <li>[2] Oliver, J. "A doubly-adaptive Clenshaw-Curtis quadrature method." The
 * Computer Journal 15.2 (1972): 141-147.</li>
 * </ul>
 * </p>
 */
public final class ClenshawCurtis extends Quadrature {

    /**
     * The extrapolation method to use for Clenshaw-Curtis integration.
     */
    public static enum ClenshawCurtisExtrapolationMethod {
	HAVIE, OLIVER
    }

    private static final double[][] SIGMA = { //
	    { 0.455, 0.272, 0.606, 0.811, 0.908 }, { 0.550, 0.144, 0.257, 0.376, 0.511 },
	    { 0.667, 0.243, 0.366, 0.449, 0.522 }, { 0.780, 0.283, 0.468, 0.565, 0.624 },
	    { 0.855, 0.290, 0.494, 0.634, 0.714 }, { -1.00, 0.292, 0.499, 0.644, 0.745 } };

    private final ClenshawCurtisExtrapolationMethod myMethod;

    /**
     * Creates a new instance of the Clenshaw-Curtis integrator.
     * 
     * @param tolerance      the smallest acceptable absolute change in integral
     *                       estimates in consecutive iterations that indicates the
     *                       algorithm has converged
     * @param maxEvaluations the maximum number of evaluations of each function
     *                       permitted
     * @param method         the extrapolation method to use
     */
    public ClenshawCurtis(final double tolerance, final int maxEvaluations,
	    final ClenshawCurtisExtrapolationMethod method) {
	super(tolerance, maxEvaluations);
	myMethod = method;
    }

    public ClenshawCurtis(final double tolerance, final int maxEvaluations) {
	this(tolerance, maxEvaluations, ClenshawCurtisExtrapolationMethod.OLIVER);
    }

    @Override
    final QuadratureResult properIntegral(final DoubleUnaryOperator f, final double a,
        final double b) {
	switch (myMethod) {
	case HAVIE:
	    return havie(f, a, b);
	case OLIVER:
	    return oliver(f, a, b);
	default:
	    return new QuadratureResult(Double.NaN, Double.NaN, 0, false);
	}
    }

    @Override
    public final String getName() {
	return "Clenshaw-Curtis-" + myMethod.toString();
    }

    private final QuadratureResult havie(final DoubleUnaryOperator f, double a, double b) {
	final double[] result = new double[1];
	final double[] epsout = new double[1];
	final int[] fev = new int[1];
	final boolean[] success = new boolean[1];
	final double epsin = myTol;
	final int nupper = SimpleMath.log2Int(myMaxEvals) - 1;
	havie(f, a, b, nupper, epsin, epsout, result, fev, success);
	return new QuadratureResult(result[0], epsout[0], fev[0], success[0]);
    }

    private final void havie(final DoubleUnaryOperator f, double a, double b, final int nupper,
	    final double epsin, final double[] epsout, final double[] result, final int[] fev,
	    final boolean[] success) {
	double a0, a1, a2, alf, alfnj, alfno, bet, betnj, betno, bounds, cof, cofmax, const1, const2, deln, deltan,
		error, etank, gamman, hnstep;
	double r1, r2, rk, rn, rnderr, rounde, tend, tnew, triarg, umid, wmean, xmin, xplus, xsink;
	final int mem = 1 << (nupper - 1);
	final double[] acof = new double[mem + 1];
	final double[] bcof = new double[mem + 1];
	final double[] ccof = new double[(mem << 1) + 1];
	int i, index, j, k, ksign, n, ncof, nhalf, nn;

	// Set coefficients in formula for accumulated roundoff error.
	// N is the current number of function values used.
	fev[0] = 0;
	rnderr = Constants.EPSILON;
	success[0] = true;

	r1 = 1.0;
	r2 = 2.0;
	error = epsin;

	// Integration interval parameters.
	alf = 0.5 * (b - a);
	bet = 0.5 * (b + a);

	// Parameters for trigonometric recurrence relations.
	triarg = Math.atan(1.0);
	alfno = -1.0;

	// Parameters for integration stepsize and loops.
	rn = 2.0;
	n = 2;
	nhalf = 1;
	hnstep = 1.0;

	// Initial calculation for the end-point approximation.
    const1 = 0.5 * (f.applyAsDouble(a) + f.applyAsDouble(b));
	fev[0] += 2;
    const2 = f.applyAsDouble(bet);
	acof[1 - 1] = 0.5 * (const1 + const2);
	acof[2 - 1] = 0.5 * (const1 - const2);
	bcof[2 - 1] = acof[2 - 1];
	tend = 2.0 * (acof[1 - 1] - acof[2 - 1] / 3.0);

	// Start actual calculations.
	for (i = 1; i <= nupper; ++i) {

	    // Compute function values.
	    const1 = -Math.sin(triarg);
	    const2 = 0.5 * alfno / const1;
	    alfno = const1;
	    betno = const2;
	    gamman = 1.0 - 2.0 * alfno * alfno;
	    deltan = -2.0 * alfno * betno;
	    bcof[1 - 1] = 0.0;
	    for (j = 1; j <= nhalf; ++j) {
		alfnj = gamman * const1 + deltan * const2;
		betnj = gamman * const2 - deltan * const1;
		xplus = alf * alfnj + bet;
		xmin = -alf * alfnj + bet;
        ccof[j - 1] = f.applyAsDouble(xplus) + f.applyAsDouble(xmin);
		fev[0] += 2;
		bcof[1 - 1] += ccof[j - 1];
		const1 = alfnj;
		const2 = betnj;
	    }
	    bcof[1 - 1] *= 0.5 * hnstep;

	    // Calculation of first B-coefficient finished compute the higher
	    // coefficients if NHALF greater than one.
	    if (nhalf > 1) {

		const1 = 1.0;
		const2 = 0.0;
		ncof = nhalf - 1;
		ksign = -1;
		for (k = 1; k <= ncof; ++k) {

		    // Compute trigonometric sum for B-coefficient.
		    etank = gamman * const1 - deltan * const2;
		    xsink = gamman * const2 + deltan * const1;
		    cof = 2.0 * (2.0 * etank * etank - 1.0);
		    a2 = a1 = 0.0;
		    a0 = ccof[nhalf - 1];
		    for (j = 1; j <= ncof; ++j) {
			a2 = a1;
			a1 = a0;
			index = nhalf - j;
			a0 = ccof[index - 1] + cof * a1 - a2;
		    }
		    bcof[k + 1 - 1] = hnstep * (a0 - a1) * etank;
		    bcof[k + 1 - 1] *= ksign;
		    ksign = -ksign;
		    const1 = etank;
		    const2 = xsink;
		}
	    }

	    // Compute new modified mid-point approximation when the interval
	    // of integration is divided in N equal sub intervals.
	    umid = 0.0;
	    rk = rn;
	    nn = nhalf + 1;
	    for (k = 1; k <= nn; ++k) {
		index = nn + 1 - k;
		umid += bcof[index - 1] / (rk * rk - 1.0);
		rk -= 2.0;
	    }
	    umid *= -2.0;

	    // Compute new C-coefficients for end-point approximation and largest
	    // absolute value of coefficients.
	    nn = n + 2;
	    cofmax = 0.0;
	    for (j = 1; j <= nhalf; ++j) {
		index = nn - j;
		ccof[j - 1] = 0.5 * (acof[j - 1] + bcof[j - 1]);
		ccof[index - 1] = 0.5 * (acof[j - 1] - bcof[j - 1]);
		const1 = Math.abs(ccof[j - 1]);
		cofmax = Math.max(cofmax, const1);
		const1 = Math.abs(ccof[index - 1]);
		cofmax = Math.max(cofmax, const1);
	    }
	    ccof[nhalf + 1 - 1] = acof[nhalf + 1 - 1];

	    // Compute new end-point approximation when the interval of
	    // integration is divided in 2N equal sub intervals.
	    wmean = 0.5 * (tend + umid);
	    bounds = 0.5 * (tend - umid);
	    deln = 0.0;
	    rk = 2.0 * rn;
	    for (j = 1; j <= nhalf; ++j) {
		index = n + 2 - j;
		deln += ccof[index - 1] / (rk * rk - 1.0);
		rk -= 2.0;
	    }
	    deln *= -2.0;
	    tnew = wmean + deln;
	    epsout[0] = Math.abs(bounds / tnew);
	    if (cofmax >= rnderr) {
		rounde = rnderr * (r1 + r2 * rn);
		epsout[0] = Math.max(epsout[0], rounde);
		error = Math.max(error, rounde);
		if (error >= epsout[0]) {

		    // Required accuracy obtained or the maximum number of function
		    // values used without obtaining the required accuracy.
		    n = 2 * n + 1;
		    tend = alf * (tend + deln);
		    umid = alf * (umid + deln);
		    deln *= alf;
		    result[0] = alf * tnew;
		    success[0] = true;
		    return;
		}
	    }

	    // If I = NUPPER then the required accuracy is not obtained.
	    if (i == nupper) {

		// Required accuracy obtained or the maximum number of function
		// values used without obtaining the required accuracy.
		n = (n << 1) + 1;
		tend = alf * (tend + deln);
		umid = alf * (umid + deln);
		deln *= alf;
		result[0] = alf * tnew;
		success[0] = false;
		return;
	    }
	    System.arraycopy(ccof, 0, acof, 0, n + 1);
	    bcof[n + 1 - 1] = ccof[n + 1 - 1];
	    tend = tnew;
	    nhalf = n;
	    n <<= 1;
	    rn *= 2.0;
	    hnstep *= 0.5;
	    triarg *= 0.5;
	}
    }

    private final QuadratureResult oliver(final DoubleUnaryOperator f, double a, double b) {
	final double eps = myTol;
	final double acc = 0.0;
	final double eta = SimpleMath.D1MACH[1 - 1];
	final int divmax = 256;
	final double[] ans = new double[1];
	final double[] error = new double[1];
	final int[] fev = new int[1];
	final boolean[] success = new boolean[1];
	adapquad(f, a, b, eps, acc, eta, divmax, ans, error, fev, myMaxEvals, success);
	return new QuadratureResult(ans[0], error[0], fev[0], success[0]);
    }

    private static final void adapquad(final DoubleUnaryOperator f, double a, double b, double eps,
	    double acc, final double eta, final int divmax, final double[] ans, final double[] error, final int[] fev,
	    final int maxfev, final boolean[] success) {
	int i, j = 0, m, mmax, n, n2, nmax, maxrule, order = 0, div;
	double c, cprev = 0, e = 0, eprev = 0, fmax = 0, fmin = 0, h = 0, hmin, iint = 0, intprev = 0, k = 0, k1 = 0,
		re = 0, x, xa, xb, xc;
	boolean caution = false;
	final double[] ec = new double[6];
	final double[] fs = new double[divmax];
	final double[] xs = new double[divmax];
	final double[] fx = new double[129];
	final double[] w1 = new double[129];
	final double[] t = new double[65];
	final double[] w = new double[126];

	ans[0] = error[0] = 0.0;
	fev[0] = 0;
	success[0] = true;
	if (a > b) {
	    c = b;
	    b = a;
	    a = c;
	}
	hmin = (b - a) / SimpleMath.pow(2, divmax);
	if (acc < eta) {
	    acc = eta;
	}
	acc *= 16;
	x = 4;
	xa = 64;
	for (i = 1; i <= 6; ++i) {
	    ec[i - 1] = xa / ((x * x - 1) * (x * x - 9));
	    x = x + x;
	    xa = xa + xa;
	}
	n = 4;
	n2 = 2;
	nmax = 128;
	m = mmax = nmax / n;
	t[0] = 1;
	t[nmax >> 1] = 0;
	maxrule = div = 0;
	w1[0] = -1;
	maxrule = quadrule(t, w, w1, n, n2, m, maxrule);
	xa = xc = a;
	xb = b;
    fx[0] = f.applyAsDouble(b);
    fx[n] = f.applyAsDouble(a);
	fev[0] = 2;

	// next is one of the following values:
	// 0 : NEXT
	// 1 : AGAIN
	// 2 : EVAL
	// 3 : TEST
	// 4 : UPDATE
	// 5 : DOUBLE
	// 6 : SPLIT
	int next = 0;
	while (true) {

	    if (next == 0) {

		// NEXT: Integration over new subinterval
		n = 4;
		n2 = 2;
		m = mmax;
		order = 1;
		caution = xa < xc;
		h = xb - xa;
		k1 = h / (b - xa);
		if (k1 < 0.1) {
		    k1 = 0.1;
		}
		h *= 0.5;
		j = 1;
        fx[n2] = f.applyAsDouble(xa + h);
		++fev[0];
		fmin = fmax = fx[0];
		if (fmax < fx[n]) {
		    fmax = fx[n];
		} else if (fmin > fx[n]) {
		    fmin = fx[n];
		}
		if (fmax < fx[n2]) {
		    fmax = fx[n2];
		} else if (fmin > fx[n2]) {
		    fmin = fx[n2];
		}
		next = 1;
		if (fev[0] > maxfev) {
		    success[0] = false;
		    return;
		}
	    }

	    if (next == 1) {

		// AGAIN: Calculate new integrand values, Chebyshev coefficients and error
		// estimate
		for (i = 1; i <= n2 - 1; i += j) {
          fx[i] = f.applyAsDouble(xa + (1 + t[i * m]) * h);
		    ++fev[0];
		    if (fmax < fx[i]) {
			fmax = fx[i];
		    } else if (fmin > fx[i]) {
			fmin = fx[i];
		    }
            fx[n - i] = f.applyAsDouble(xa + (1 - t[i * m]) * h);
		    ++fev[0];
		    if (fmax < fx[n - i]) {
			fmax = fx[n - i];
		    } else if (fmin > fx[n - i]) {
			fmin = fx[n - i];
		    }
		    if (fev[0] > maxfev) {
			success[0] = false;
			return;
		    }
		}
		re = acc * Math.max(Math.abs(fmax), Math.abs(fmin));
		j = n == 4 ? 4 : 6;
		k = 0;
		c = Math.abs(cheb(-1, fx, n)) / n;
		for (i = 2; i <= j; i += 2) {
		    if (i <= n2) {
			x = -t[i * m];
		    } else {
			x = t[(n - i) * m];
		    }
		    cprev = c;
		    c = Math.abs(cheb(x, fx, n)) / n2;
		    if (c > re) {
			if (k < cprev / c) {
			    k = cprev / c;
			}
		    } else if (cprev > re) {
			k = 1;
		    }
		}
		next = 2;
		if (k > SIGMA[order - 1][4]) {
		    if (n == 4) {
			next = 6;
		    }
		} else {
		    if (n == 4) {
			cprev = c;
		    } else if (cprev < re) {
			cprev = k * c;
		    }
		    e = h * cprev * ec[order - 1] * k * k * k;
		    for (i = 0; k > SIGMA[order - 1][i]; ++i) {
			e *= 2;
		    }
		    re = h * re;
		}
	    }

	    if (next == 2) {

		// EVAL: Evaluate integral and select appropriate error estimate
		iint = w1[n] * (fx[0] + fx[n]) + w[n - 3] * fx[n2];
		for (i = 1; i <= n2 - 1; ++i) {
		    iint += w[n2 + i - 3] * (fx[i] + fx[n - i]);
		}
		iint *= h;
		if (n != 4) {
		    c = Math.abs(iint - intprev);
		    if (c > eprev) {
			caution = true;
			if (xc < xb) {
			    xc = xb;
			}
		    } else {
			caution = false;
		    }
		    if (k > SIGMA[order - 1][4] || caution) {
			e = c;
		    }
		    if (e > c) {
			e = c;
		    }
		}
		next = 3;
	    }

	    if (next == 3) {

		// TEST: Finish consideration of current subinterval if local error acceptable
		if (e < re || e <= k1 * eps) {
		    next = 4;
		} else if (k > SIGMA[order - 1][0]) {
		    next = 6;
		} else {
		    next = 5;
		}
	    }

	    if (next == 4) {

		// UPDATE
		if (n != 4 || !(caution || (xa == a && div == 0))) {
		    if (e < re) {
			e = re;
		    }
		    error[0] += e;
		    eps -= e;
		    if (eps < 0.1 * error[0]) {
			eps = 0.1 * error[0];
		    }
		    ans[0] += iint;
		    if (div == 0) {
			success[0] = true;
			return;
		    }
		    --div;
		    xa = xb;
		    xb = xs[div];
		    fx[4] = fx[0];
		    fx[0] = fs[div];
		    next = 0;
		} else {
		    next = 5;
		}
	    }

	    if (next == 5) {

		// DOUBLE: Double order of formula
		for (i = n; i >= 1; --i) {
		    fx[i << 1] = fx[i];
		}
		n2 = n;
		n <<= 1;
		m *= 0.5;
		++order;
		eprev = e;
		intprev = iint;
		if (eprev < re) {
		    eprev = re;
		}
		if (n > maxrule) {
		    maxrule = quadrule(t, w, w1, n, n2, m, maxrule);
		}
		j = 2;
		next = 1;
	    }

	    if (next == 6) {

		// SPLIT: Split current subinterval unless simple error estimate acceptable and
		// consider left half
		e = 2 * h * (fmax - fmin);
		if (e < re || e <= k1 * eps) {
		    iint = h * (fmax + fmin);
		    next = 4;
		} else {
		    if (h < hmin) {
			success[0] = false;
			return;
		    }
		    xs[div] = xb;
		    xb = xa + h;
		    fs[div] = fx[0];
		    fx[0] = fx[n2];
		    fx[4] = fx[n];
		    ++div;
		    next = 0;
		}
	    }
	}
    }

    private static final int quadrule(final double[] t, final double[] w, final double[] w1, final int n, final int n2,
	    final int m, final int maxrule) {
	for (int i = 1; i <= n2 - 1; i += 2) {
	    t[i * m] = Math.cos((Math.PI * i) / n);
	}
	for (int i = (maxrule >> 1) + 2; i <= n; i += 2) {
	    w1[i - 1] = 0;
	    w1[i] = 1.0 / (i * i - 1);
	}
	for (int i = 1; i <= n2; ++i) {
	    w[n2 + i - 3] = -4 * cheb(t[i * m], w1, n) / n;
	}
	return n;
    }

    private static final double cheb(final double x, final double[] a, final int n) {
	double b1 = 0.0;
	double b0 = 0.5 * a[n];
	final double twox = 2.0 * x;
	for (int r = n - 1; r >= 1; --r) {
	    final double b2 = b1;
	    b1 = b0;
	    b0 = twox * b1 - b2 + a[r];
	}
	return x * b0 - b1 + 0.5 * a[0];
    }
}
