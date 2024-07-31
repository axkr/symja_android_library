package org.matheclipse.core.numerics.integral;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.numerics.utils.Constants;
import org.matheclipse.core.numerics.utils.SimpleMath;

/**
 * Integrate a function using a 7-point adaptive Newton-Cotes quadrature rule.
 * This algorithm is a translation of the corresponding subroutine dqnc79 from
 * the SLATEC library.
 */
public final class NewtonCotes extends Quadrature {

    private static final double W1 = 41.0 / 140.0;
    private static final double W2 = 216.0 / 140.0;
    private static final double W3 = 27.0 / 140.0;
    private static final double W4 = 272.0 / 140.0;

    public NewtonCotes(final double tolerance, final int maxEvaluations) {
	super(tolerance, maxEvaluations);
    }

    @Override
    final QuadratureResult properIntegral(final DoubleUnaryOperator f, final double a,
        final double b) {
	return dqnc79(f, a, b);
    }

    @Override
    public final String getName() {
	return "Newton-Cotes";
    }

    private final QuadratureResult dqnc79(final DoubleUnaryOperator fun, final double a,
        final double b) {
	double ae, area, bank, blocal, c, ce, ee, ef, eps, q13, q7, q7l, sq2 = Constants.SQRT2, test, tol, vr, ans;
	int i, kml = 7, kmx = 5000, l, lmn, lmx, nbits, nib, nlmn = 2, nlmx, fev = 0, k = 0;
	final double[] aa = new double[100], f = new double[14], f1 = new double[100], f2 = new double[100],
		f3 = new double[100], f4 = new double[100], f5 = new double[100], f6 = new double[100],
		f7 = new double[100], hh = new double[100], q7r = new double[100], vl = new double[100];
	final int[] lr = new int[100];

	nbits = (int) (SimpleMath.D1MACH[5 - 1] * 53 / 0.30102000);
	nlmx = Math.min(99, (nbits * 4) / 5);
	ce = 0.0;
	ans = 0.0;
	if (a == b) {
	    return new QuadratureResult(ans, ce, fev, true);
	}
	lmx = nlmx;
	lmn = nlmn;
	if (b != 0.0 && SimpleMath.sign(1.0, b) * a > 0.0) {
	    c = Math.abs(1.0 - a / b);
	    if (c <= 0.1) {
		if (c <= 0.0) {
		    return new QuadratureResult(ans, ce, fev, true);
		}
		nib = (int) (0.5 - Math.log(c) * Constants.LOG2_INV);
		lmx = Math.min(nlmx, nbits - nib - 4);
		if (lmx < 2) {
		    return new QuadratureResult(ans, ce, fev, true);
		}
		lmn = Math.min(lmn, lmx);
	    }
	}

	tol = Math.max(Math.abs(myTol), SimpleMath.pow(2.0, 5 - nbits));
	if (myTol == 0.0) {
	    tol = Math.sqrt(SimpleMath.D1MACH[4 - 1]);
	}
	eps = tol;
	hh[1 - 1] = (b - a) / 12.0;
	aa[1 - 1] = a;
	lr[1 - 1] = 1;
	for (i = 1; i <= 11; i += 2) {
      f[i - 1] = fun.applyAsDouble(a + (i - 1) * hh[1 - 1]);
	    ++fev;
	}
	blocal = b;
    f[13 - 1] = fun.applyAsDouble(blocal);
	++fev;
	k = 7;
	l = 1;
	area = q7 = 0.0;
	ef = 256.0 / 255.0;
	bank = 0.0;

	while (true) {

	    // Compute refined estimates, estimate the error, etc
	    for (i = 2; i <= 12; i += 2) {
          f[i - 1] = fun.applyAsDouble(aa[l - 1] + (i - 1) * hh[l - 1]);
		++fev;
		if (fev >= myMaxEvals) {
		    return new QuadratureResult(ans, ce, fev, false);
		}
	    }
	    k += 6;

	    // Compute left and right half estimates
	    q7l = hh[l - 1] * ((W1 * (f[1 - 1] + f[7 - 1]) + W2 * (f[2 - 1] + f[6 - 1]))
		    + (W3 * (f[3 - 1] + f[5 - 1]) + W4 * f[4 - 1]));
	    q7r[l - 1] = hh[l - 1] * ((W1 * (f[7 - 1] + f[13 - 1]) + W2 * (f[8 - 1] + f[12 - 1]))
		    + (W3 * (f[9 - 1] + f[11 - 1]) + W4 * f[10 - 1]));

	    // Update estimate of integral of absolute value
	    area += (Math.abs(q7l) + Math.abs(q7r[l - 1]) - Math.abs(q7));

	    // Do not bother to test convergence before minimum refinement level
	    if (l < lmn) {
		++l;
		eps *= 0.5;
		if (l <= 17) {
		    ef /= sq2;
		}
		hh[l - 1] = hh[l - 1 - 1] * 0.5;
		lr[l - 1] = -1;
		aa[l - 1] = aa[l - 1 - 1];
		q7 = q7l;
		f1[l - 1] = f[7 - 1];
		f2[l - 1] = f[8 - 1];
		f3[l - 1] = f[9 - 1];
		f4[l - 1] = f[10 - 1];
		f5[l - 1] = f[11 - 1];
		f6[l - 1] = f[12 - 1];
		f7[l - 1] = f[13 - 1];
		f[13 - 1] = f[7 - 1];
		f[11 - 1] = f[6 - 1];
		f[9 - 1] = f[5 - 1];
		f[7 - 1] = f[4 - 1];
		f[5 - 1] = f[3 - 1];
		f[3 - 1] = f[2 - 1];
		continue;
	    }

	    // Estimate the error in new value for whole interval, Q13
	    q13 = q7l + q7r[l - 1];
	    ee = Math.abs(q7 - q13) * ef;

	    // Compute nominal allowed error
	    ae = eps * area;

	    // Borrow from bank account, but not too much
	    test = Math.min(ae + 0.8 * bank, 10.0 * ae);

	    // Don't ask for excessive accuracy
	    test = Math.max(test, Math.max(tol * Math.abs(q13), 3.0e-5 * tol * area));

	    // Now, did this interval pass or not?
	    if (ee - test <= 0.0) {

		// On good intervals accumulate the theoretical estimate
		ce += (q7 - q13) / 255.0;
	    } else {

		// Consider the left half of next deeper level
		if (k > kmx) {
		    lmx = Math.min(kml, lmx);
		}
		if (l >= lmx) {

		    // Have hit maximum refinement level --
		    // penalize the cumulative error
		    ce += (q7 - q13);
		} else {
		    ++l;
		    eps *= 0.5;
		    if (l <= 17) {
			ef /= sq2;
		    }
		    hh[l - 1] = hh[l - 1 - 1] * 0.5;
		    lr[l - 1] = -1;
		    aa[l - 1] = aa[l - 1 - 1];
		    q7 = q7l;
		    f1[l - 1] = f[7 - 1];
		    f2[l - 1] = f[8 - 1];
		    f3[l - 1] = f[9 - 1];
		    f4[l - 1] = f[10 - 1];
		    f5[l - 1] = f[11 - 1];
		    f6[l - 1] = f[12 - 1];
		    f7[l - 1] = f[13 - 1];
		    f[13 - 1] = f[7 - 1];
		    f[11 - 1] = f[6 - 1];
		    f[9 - 1] = f[5 - 1];
		    f[7 - 1] = f[4 - 1];
		    f[5 - 1] = f[3 - 1];
		    f[3 - 1] = f[2 - 1];
		    continue;
		}
	    }

	    // Update the bank account. Don't go into debt.
	    bank += (ae - ee);
	    if (bank < 0.0) {
		bank = 0.0;
	    }

	    // Did we just finish a left half or a right half?
	    if (lr[l - 1] <= 0.0) {

		// Proceed to right half at this level
		vl[l - 1] = q13;
		q7 = q7r[l - 1 - 1];
		lr[l - 1] = 1;
		aa[l - 1] += 12.0 * hh[l - 1];
		f[1 - 1] = f1[l - 1];
		f[3 - 1] = f2[l - 1];
		f[5 - 1] = f3[l - 1];
		f[7 - 1] = f4[l - 1];
		f[9 - 1] = f5[l - 1];
		f[11 - 1] = f6[l - 1];
		f[13 - 1] = f7[l - 1];
	    } else {

		// Left and right halves are done, so go back up a level
		vr = q13;
		while (true) {
		    if (l <= 1) {
			ans = vr;
			if (Math.abs(ce) > 2.0 * tol * area) {
			    return new QuadratureResult(ans, Math.abs(ce), fev, false);
			} else {
			    return new QuadratureResult(ans, Math.abs(ce), fev, true);
			}
		    }
		    if (l <= 17) {
			ef *= sq2;
		    }
		    eps *= 2.0;
		    --l;
		    if (lr[l - 1] <= 0.0) {
			vl[l - 1] = vl[l + 1 - 1] + vr;
			q7 = q7r[l - 1 - 1];
			lr[l - 1] = 1;
			aa[l - 1] += 12.0 * hh[l - 1];
			f[1 - 1] = f1[l - 1];
			f[3 - 1] = f2[l - 1];
			f[5 - 1] = f3[l - 1];
			f[7 - 1] = f4[l - 1];
			f[9 - 1] = f5[l - 1];
			f[11 - 1] = f6[l - 1];
			f[13 - 1] = f7[l - 1];
			break;
		    } else {
			vr += vl[l + 1 - 1];
		    }
		}
	    }
	}
    }
}
