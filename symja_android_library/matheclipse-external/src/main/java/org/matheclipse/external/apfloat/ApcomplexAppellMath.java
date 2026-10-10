package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;

/**
 * Appell's hypergeometric function of two variables for {@link Apcomplex} arguments.<p>
 *
 * The method is written as it would appear in <code>org.apfloat.ApcomplexMath</code>:
 * static, and the precision of the result is the smallest precision of the arguments.
 */

public class ApcomplexAppellMath
{
    private ApcomplexAppellMath()
    {
    }

    // The series in both arguments is summed if both have at most this absolute value
    private static final double DOUBLE_SERIES_RADIUS = 0.99;

    // The series of hypergeometric functions is summed only in an argument of at most this absolute value
    private static final double SERIES_RADIUS = 0.9;

    // The sum is repeated with twice the extra digits at most this many times
    private static final int MAX_ATTEMPTS = 5;

    /**
     * Appell hypergeometric function <i>F<sub>1</sub></i>.<p>
     *
     * <i>F<sub>1</sub>(a; b<sub>1</sub>, b<sub>2</sub>; c; x, y)</i> = &sum;<sub>m,n</sub>
     * (<i>a</i>)<sub>m+n</sub> (<i>b<sub>1</sub></i>)<sub>m</sub> (<i>b<sub>2</sub></i>)<sub>n</sub> /
     * ((<i>c</i>)<sub>m+n</sub> <i>m</i>! <i>n</i>!) <i>x<sup>m</sup> y<sup>n</sup></i>
     * and its analytic continuation.<p>
     *
     * If <i>a</i>, <i>b<sub>1</sub></i> or <i>b<sub>2</sub></i> is a nonpositive integer, the
     * terminating series is summed, for any arguments.<p>
     *
     * Otherwise the function is written in the one of its six Euler transformations which
     * has the smallest arguments. If both are inside of the unit circle the power series is
     * summed, with the three term recurrence of its coefficients. If only one is, the function
     * is summed as a series of hypergeometric functions in the smaller one of the two
     * arguments, which is much slower. This covers the arguments for which one of the
     * transformed arguments has an absolute value less than 0.9; for all other arguments an
     * <code>ArithmeticException</code> is thrown.<p>
     *
     * The function has a branch cut for a real argument from one to infinity. There is no
     * value on the cut, unless the series terminates in that argument.
     *
     * @implNote
     * This implementation is <i>slow</i>, meaning that it isn't a <i>fast algorithm</i>.
     * It is impractically slow beyond a precision of a few hundred digits.
     *
     * @param a The first parameter.
     * @param b1 The parameter of the first argument.
     * @param b2 The parameter of the second argument.
     * @param c The denominator parameter.
     * @param x The first argument.
     * @param y The second argument.
     *
     * @return <i>F<sub>1</sub>(a; b<sub>1</sub>, b<sub>2</sub>; c; x, y)</i>
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws LossOfPrecisionException If the sums at increasing working precisions don't agree.
     * @throws ArithmeticException If an argument is on the branch cut, if the function has a pole, or if the arguments are outside of the range which is covered.
     */

    public static Apcomplex appellF1(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(Math.min(Math.min(a.precision(), b1.precision()), Math.min(b2.precision(), c.precision())),
                                  Math.min(x.precision(), y.precision()));
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Appell F1 to infinite precision", "appellF1.infinitePrecision");
        }
        // The sum is accepted when it comes out the same with more digits: this finds the digits which the
        // terms of the sum cancel as well as those which the recurrence of the coefficients loses
        long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION;
        Apcomplex previous = null;
        boolean[] tracked = new boolean[1];
        for (int attempt = 0; attempt <= MAX_ATTEMPTS; attempt++)
        {
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision, extraPrecision);
            Apcomplex result = appellF1(ApfloatHelperShim.ensurePrecision(a, workingPrecision),
                                        ApfloatHelperShim.ensurePrecision(b1, workingPrecision),
                                        ApfloatHelperShim.ensurePrecision(b2, workingPrecision),
                                        ApfloatHelperShim.ensurePrecision(c, workingPrecision),
                                        ApfloatHelperShim.ensurePrecision(x, workingPrecision),
                                        ApfloatHelperShim.ensurePrecision(y, workingPrecision),
                                        workingPrecision, tracked);
            // The series of hypergeometric functions is slow, and apfloat keeps track of the digits it loses
            if ((tracked[0] && result.precision() >= precision) ||
                (previous != null && agree(previous, result, precision)))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
            extraPrecision *= 2;
        }
        throw new LossOfPrecisionException("Complete loss of accurate digits");
    }

    // Are the two values the same to the precision, with a digit to spare?
    private static boolean agree(Apcomplex z, Apcomplex w, long precision)
    {
        if (z.precision() < precision || w.precision() < precision)
        {
            return isZero(z) && isZero(w);
        }
        Apcomplex difference = z.subtract(w);
        return isZero(difference) || difference.scale() < Math.max(z.scale(), w.scale()) - precision;
    }

    // The value at the working precision; tracked[0] is set if the precision of the value is the one which apfloat kept track of
    private static Apcomplex appellF1(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y, long workingPrecision, boolean[] tracked)
    {
        tracked[0] = false;
        if (isNonPositiveInteger(a))
        {
            // A polynomial in both arguments
            return doubleSeries(a, b1, b2, c, x, y, workingPrecision);
        }
        boolean terminates1 = isNonPositiveInteger(b1),
                terminates2 = isNonPositiveInteger(b2);
        if (terminates1 || terminates2)
        {
            if (!terminates1)
            {
                // F1(a; b1, b2; c; x, y) = F1(a; b2, b1; c; y, x)
                Apcomplex t = b1;
                b1 = b2;
                b2 = t;
                t = x;
                x = y;
                y = t;
            }
            if (onBranchCut(y) && !(terminates1 && terminates2))
            {
                throw new ArithmeticException("Appell F1 on the branch cut");
            }
            // A polynomial in x
            tracked[0] = true;
            return series(a, b1, b2, c, x, y, workingPrecision);
        }
        if (onBranchCut(x) || onBranchCut(y))
        {
            throw new ArithmeticException("Appell F1 on the branch cut");
        }

        Apint one = ApfloatHelperShim.ONES[x.radix()];
        Apcomplex oneMinusX = one.subtract(x),
                  oneMinusY = one.subtract(y),
                  cMinusA = c.subtract(a),
                  b3 = c.subtract(b1).subtract(b2),
                  xT = x.divide(oneMinusX).negate(),     // x / (x - 1)
                  yT = y.divide(oneMinusY).negate(),     // y / (y - 1)
                  yMinusX = y.subtract(x);

        // The function and its Euler transformations: factor F1(a'; b1', b2'; c; u, v) as { a', b1', b2', u, v }
        Apcomplex[][] forms = {
            { a, b1, b2, x, y },
            { cMinusA, b1, b2, xT, yT },
            { a, b3, b2, xT, yMinusX.divide(oneMinusX) },
            { a, b1, b3, yMinusX.negate().divide(oneMinusY), yT },
            { cMinusA, b3, b2, x, yMinusX.negate().divide(oneMinusY) },
            { cMinusA, b1, b3, yMinusX.divide(oneMinusX), y }
        };
        // The forms with the argument (y - x) / (1 - x) or (x - y) / (1 - y) hold on the principal sheet only
        // if that argument doesn't cross the branch cut on the way from the origin to (x, y)
        boolean crossesX = crossesBranchCut(x, y),
                crossesY = crossesBranchCut(y, x);
        boolean[] valid = { true, true, !crossesX, !crossesY, !crossesY, !crossesX };
        int best = -1;
        double bestScore = Double.MAX_VALUE;
        for (int i = 0; i < forms.length; i++)
        {
            if (!valid[i])
            {
                continue;
            }
            double u = ApcomplexMath.abs(forms[i][3]).doubleValue(),
                   v = ApcomplexMath.abs(forms[i][4]).doubleValue(),
                   max = Math.max(u, v),
                   // Both arguments inside of the unit circle are preferred; otherwise the series runs in
                   // the smaller argument and the hypergeometric function continues the larger one
                   score = (max <= DOUBLE_SERIES_RADIUS ? max : 10.0 + Math.min(u, v));
            if (score < bestScore)
            {
                bestScore = score;
                best = i;
            }
        }
        Apcomplex[] form = forms[best];
        Apcomplex series;
        if (bestScore <= DOUBLE_SERIES_RADIUS)
        {
            series = doubleSeries(form[0], form[1], form[2], c, form[3], form[4], workingPrecision);
        }
        else
        {
            Apcomplex f1 = form[1],
                      f2 = form[2],
                      u = form[3],
                      v = form[4];
            if (ApcomplexMath.abs(u).compareTo(ApcomplexMath.abs(v)) > 0)
            {
                // F1(a; b1, b2; c; x, y) = F1(a; b2, b1; c; y, x)
                Apcomplex t = f1;
                f1 = f2;
                f2 = t;
                t = u;
                u = v;
                v = t;
            }
            if (!(ApcomplexMath.abs(u).doubleValue() < SERIES_RADIUS))
            {
                throw new ArithmeticException("Appell F1 is not implemented for these arguments");
            }
            tracked[0] = true;
            series = series(form[0], f1, f2, c, u, v, workingPrecision);
        }
        Apcomplex factor;
        switch (best)
        {
            case 1:
                factor = pow(oneMinusX, b1.negate()).multiply(pow(oneMinusY, b2.negate()));
                break;
            case 2:
                factor = pow(oneMinusX, a.negate());
                break;
            case 3:
                factor = pow(oneMinusY, a.negate());
                break;
            case 4:
                factor = pow(oneMinusX, cMinusA.subtract(b1)).multiply(pow(oneMinusY, b2.negate()));
                break;
            case 5:
                factor = pow(oneMinusX, b1.negate()).multiply(pow(oneMinusY, cMinusA.subtract(b2)));
                break;
            default:
                factor = one;
        }
        return factor.multiply(series);
    }

    // The function has a branch cut for a real argument from one to infinity
    private static boolean onBranchCut(Apcomplex z)
    {
        return z.imag().signum() == 0 && z.real().compareTo(ApfloatHelperShim.ONES[z.radix()]) >= 0;
    }

    // Is t (y - x) / (1 - t x) a real number r >= 1 for a t in (0, 1]? Then y - x + r x = r / t is real, which gives r
    private static boolean crossesBranchCut(Apcomplex x, Apcomplex y)
    {
        if (x.imag().signum() == 0)
        {
            return false;
        }
        Apfloat one = ApfloatHelperShim.ONES[x.radix()],
                r = one.subtract(y.imag().divide(x.imag()));
        if (r.compareTo(one) < 0)
        {
            return false;
        }
        Apfloat rOverT = y.real().subtract(x.real()).add(r.multiply(x.real()));
        return rOverT.signum() > 0 && r.compareTo(rOverT) <= 0;
    }

    private static boolean isNonPositiveInteger(Apcomplex z)
    {
        return z.imag().signum() == 0 && z.real().signum() <= 0 && z.real().isInteger();
    }

    private static Apcomplex pow(Apcomplex z, Apcomplex w)
    {
        if (isZero(w))
        {
            return ApfloatHelperShim.ONES[z.radix()];
        }
        return ApcomplexMath.pow(z, w);
    }

    // Adds the term to the sum, and keeps the scale of the largest term in maxScale[0]
    private static Apcomplex add(Apcomplex sum, Apcomplex term, long[] maxScale)
    {
        if (!isZero(term))
        {
            maxScale[0] = Math.max(maxScale[0], term.scale());
        }
        return sum.add(term);
    }

    // A term which is below the last digit of the largest term can't change the sum any more
    private static boolean isNegligible(Apcomplex term, long[] maxScale, long workingPrecision)
    {
        return isZero(term) || term.scale() < maxScale[0] - workingPrecision;
    }

    // F1(a; b1, b2; c; x, y) = sum (a)_k / (c)_k p_k, where p_k is the coefficient of t^k in (1 - x t)^(-b1) (1 - y t)^(-b2):
    // (k + 1) p_(k+1) = ((x + y) k + b1 x + b2 y) p_k - x y (k - 1 + b1 + b2) p_(k-1)
    private static Apcomplex doubleSeries(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y, long workingPrecision)
    {
        int radix = x.radix();
        boolean terminates = isNonPositiveInteger(a);
        long maxTerms;
        if (terminates)
        {
            maxTerms = a.real().negate().longValue() + 4;
        }
        else
        {
            double radius = Math.max(ApcomplexMath.abs(x).doubleValue(), ApcomplexMath.abs(y).doubleValue()),
                   size = ApcomplexMath.abs(a).doubleValue() + ApcomplexMath.abs(b1).doubleValue() +
                          ApcomplexMath.abs(b2).doubleValue() + ApcomplexMath.abs(c).doubleValue();
            // The terms decrease like radius^k, after they have grown like a power of k which depends on the parameters
            maxTerms = 1000L + (long) Math.min(1.0e7, (2.0 * workingPrecision * Math.log(radix) + 50.0 * size) / -Math.log(Math.min(radius, DOUBLE_SERIES_RADIUS)));
        }
        Apcomplex xPlusY = x.add(y),
                  linear = b1.multiply(x).add(b2.multiply(y)),
                  xy = x.multiply(y),
                  b = b1.add(b2),
                  sum = ApfloatHelperShim.ZEROS[radix],
                  pochhammer = ApfloatHelperShim.ONES[radix],
                  previous = ApfloatHelperShim.ZEROS[radix],
                  p = ApfloatHelperShim.ONES[radix];
        long[] maxScale = { Long.MIN_VALUE };
        int smallTerms = 0;
        for (long k = 0; k < maxTerms; k++)
        {
            Apint kInt = new Apint(k, radix);
            Apcomplex term = pochhammer.multiply(p);
            sum = add(sum, term, maxScale);
            if (isZero(pochhammer))
            {
                // The series has terminated
                break;
            }
            if (!terminates && isNegligible(term, maxScale, workingPrecision))
            {
                if (++smallTerms >= 3)
                {
                    break;
                }
            }
            else
            {
                smallTerms = 0;
            }
            Apcomplex cK = c.add(kInt);
            if (isZero(cK))
            {
                throw new ArithmeticException("Appell F1 of a nonpositive integer denominator parameter");
            }
            if (k + 1 == maxTerms)
            {
                throw new ArithmeticException("Appell F1 series does not converge");
            }
            Apcomplex next = xPlusY.multiply(kInt).add(linear).multiply(p)
                                   .subtract(xy.multiply(b.add(new Apint(k - 1, radix))).multiply(previous))
                                   .divide(new Apint(k + 1, radix));
            // The subtraction above doesn't lose the digits which apfloat takes away for it, step after step
            previous = p;
            p = ApfloatHelperShim.ensurePrecision(next, workingPrecision);
            pochhammer = pochhammer.multiply(a.add(kInt)).divide(cK);
        }
        return sum;
    }

    // F1(a; b1, b2; c; x, y) = sum (a)_m (b1)_m / ((c)_m m!) x^m 2F1(a + m, b2; c + m; y)
    private static Apcomplex series(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y, long workingPrecision)
    {
        int radix = x.radix();
        Apint one = ApfloatHelperShim.ONES[radix];
        boolean terminates = isNonPositiveInteger(b1);
        long maxTerms = (terminates ? b1.real().negate().longValue() + 4 : Math.min(100000L, 200L + 25L * workingPrecision));
        Apcomplex sum = ApfloatHelperShim.ZEROS[radix],
                  coefficient = one;
        long[] maxScale = { Long.MIN_VALUE };
        int smallTerms = 0;
        for (long m = 0; m < maxTerms; m++)
        {
            if (isZero(coefficient))
            {
                // The series has terminated
                break;
            }
            Apint mInt = new Apint(m, radix);
            Apcomplex aM = a.add(mInt),
                      cM = c.add(mInt);
            if (isZero(cM))
            {
                throw new ArithmeticException("Appell F1 of a nonpositive integer denominator parameter");
            }
            Apcomplex term = coefficient.multiply(hypergeometric(aM, b2, cM, y));
            sum = add(sum, term, maxScale);
            if (!terminates && isNegligible(term, maxScale, workingPrecision))
            {
                if (++smallTerms >= 3)
                {
                    break;
                }
            }
            else
            {
                smallTerms = 0;
            }
            if (m + 1 == maxTerms)
            {
                throw new ArithmeticException("Appell F1 series does not converge");
            }
            coefficient = coefficient.multiply(aM).multiply(b1.add(mInt)).multiply(x).divide(cM.multiply(new Apint(m + 1, radix)));
        }
        return sum;
    }

    private static Apcomplex hypergeometric(Apcomplex a, Apcomplex b, Apcomplex c, Apcomplex z)
    {
        if (isZero(z) || isZero(a) || isZero(b))
        {
            return ApfloatHelperShim.ONES[z.radix()];
        }
        return ApcomplexMath.hypergeometric2F1(a, b, c, z);
    }

    private static boolean isZero(Apcomplex z)
    {
        return z.real().signum() == 0 && z.imag().signum() == 0;
    }
}
