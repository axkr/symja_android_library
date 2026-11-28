package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;

/**
 * Appell's hypergeometric functions F1, F2, F3 and F4 of two variables for {@link Apcomplex} arguments.<p>
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
            // An exact zero from a sum whose terms cancel completely at this working precision says nothing about the value (it has infinite precision, and two of them agree):
            // it is accepted only if it stays zero up to the largest number of extra digits
            boolean zero = (result.real().signum() == 0 && result.imag().signum() == 0);
            if (zero)
            {
                if (attempt == MAX_ATTEMPTS)
                {
                    return result;
                }
            }
            // The series of hypergeometric functions is slow, and apfloat keeps track of the digits it loses
            else if ((tracked[0] && result.precision() >= precision) ||
                     (previous != null && GuardDigitsHelper.agree(previous, result, precision)))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
            extraPrecision *= 2;
        }
        throw new LossOfPrecisionException("Complete loss of accurate digits");
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

    /**
     * Appell hypergeometric function <i>F<sub>1</sub></i> from its Euler integral<p>
     *
     * <i>F<sub>1</sub>(a; b<sub>1</sub>, b<sub>2</sub>; c; x, y)</i> = &Gamma;(<i>c</i>) / (&Gamma;(<i>a</i>) &Gamma;(<i>c</i> - <i>a</i>))
     * &int;<sub>0</sub><sup>1</sup> <i>t<sup>a-1</sup></i> (1 - <i>t</i>)<sup><i>c-a-1</i></sup> (1 - <i>x t</i>)<sup>-<i>b<sub>1</sub></i></sup> (1 - <i>y t</i>)<sup>-<i>b<sub>2</sub></i></sup> d<i>t</i>,<p>
     *
     * which holds for Re(<i>c</i>) &gt; Re(<i>a</i>) &gt; 0 and covers the arguments which
     * {@link #appellF1(Apcomplex, Apcomplex, Apcomplex, Apcomplex, Apcomplex, Apcomplex)}
     * doesn't reach, among them the branch cuts: for a real argument greater than one the
     * integral is the limit from below the cut, the same value as
     * <code>ApcomplexMath.hypergeometric2F1()</code> takes on its cut.<p>
     *
     * The integral is computed by double exponential quadrature over the pieces between the
     * singular points of the integrand, see {@link ApfloatDoubleExponential}.
     *
     * @param a The first parameter.
     * @param b1 The parameter of the first argument.
     * @param b2 The parameter of the second argument.
     * @param c The last parameter.
     * @param x The first argument.
     * @param y The second argument.
     * @param precision The precision of the result.
     * @param machine If <code>true</code> the integral is first summed in <code>double</code>, and in apfloat only if that doesn't converge to machine precision.
     *
     * @return <i>F<sub>1</sub>(a; b<sub>1</sub>, b<sub>2</sub>; c; x, y)</i> with <code>precision</code> digits.
     *
     * @throws ArithmeticException If Re(<i>c</i>) &gt; Re(<i>a</i>) &gt; 0 doesn't hold, the integral doesn't exist for these parameters or the quadrature doesn't converge.
     * @throws ApfloatRuntimeException If apfloat can't evaluate the parameters, for instance to infinite precision, or the computation is interrupted.
     */

    public static Apcomplex appellF1EulerIntegral(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y, long precision, boolean machine)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return AppellF1EulerIntegralHelper.appellF1(a, b1, b2, c, x, y, precision, machine);
    }

    // The kinds of the double series of appellF2, appellF3 and appellF4
    private static final int F2 = 2,
                             F3 = 3,
                             F4 = 4;

    /**
     * Appell hypergeometric function <i>F<sub>2</sub></i>.<p>
     *
     * <i>F<sub>2</sub>(a; b<sub>1</sub>, b<sub>2</sub>; c<sub>1</sub>, c<sub>2</sub>; x, y)</i> = &sum;<sub>m,n</sub>
     * (<i>a</i>)<sub>m+n</sub> (<i>b<sub>1</sub></i>)<sub>m</sub> (<i>b<sub>2</sub></i>)<sub>n</sub> /
     * ((<i>c<sub>1</sub></i>)<sub>m</sub> (<i>c<sub>2</sub></i>)<sub>n</sub> <i>m</i>! <i>n</i>!) <i>x<sup>m</sup> y<sup>n</sup></i>.<p>
     *
     * The double series is summed for |<i>x</i>| + |<i>y</i>| &lt; 1, and for any arguments in which
     * it terminates.
     *
     * @param a The first parameter.
     * @param b1 The parameter of the first argument.
     * @param b2 The parameter of the second argument.
     * @param c1 The denominator parameter of the first argument.
     * @param c2 The denominator parameter of the second argument.
     * @param x The first argument.
     * @param y The second argument.
     *
     * @return <i>F<sub>2</sub>(a; b<sub>1</sub>, b<sub>2</sub>; c<sub>1</sub>, c<sub>2</sub>; x, y)</i>
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws LossOfPrecisionException If the sums at increasing working precisions don't agree.
     * @throws ArithmeticException If the arguments are outside of the region in which the series converges, or the function has a pole.
     */

    public static Apcomplex appellF2(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c1, Apcomplex c2, Apcomplex x, Apcomplex y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        boolean terminatesM = isNonPositiveInteger(b1),
                terminatesN = isNonPositiveInteger(b2);
        if (!isNonPositiveInteger(a) && !(terminatesM && terminatesN))
        {
            double ax = ApcomplexMath.abs(x).doubleValue(),
                   ay = ApcomplexMath.abs(y).doubleValue();
            double radius = (terminatesM ? ay : terminatesN ? ax : ax + ay);
            checkRadius(radius);
        }
        return doubleSeries(F2, new Apcomplex[] { a, b1, b2, c1, c2 }, x, y);
    }

    /**
     * Appell hypergeometric function <i>F<sub>3</sub></i>.<p>
     *
     * <i>F<sub>3</sub>(a<sub>1</sub>, a<sub>2</sub>; b<sub>1</sub>, b<sub>2</sub>; c; x, y)</i> = &sum;<sub>m,n</sub>
     * (<i>a<sub>1</sub></i>)<sub>m</sub> (<i>a<sub>2</sub></i>)<sub>n</sub> (<i>b<sub>1</sub></i>)<sub>m</sub> (<i>b<sub>2</sub></i>)<sub>n</sub> /
     * ((<i>c</i>)<sub>m+n</sub> <i>m</i>! <i>n</i>!) <i>x<sup>m</sup> y<sup>n</sup></i>.<p>
     *
     * The double series is summed for |<i>x</i>| &lt; 1 and |<i>y</i>| &lt; 1, and for any arguments
     * in which it terminates.
     *
     * @param a1 The first parameter of the first argument.
     * @param a2 The first parameter of the second argument.
     * @param b1 The second parameter of the first argument.
     * @param b2 The second parameter of the second argument.
     * @param c The denominator parameter.
     * @param x The first argument.
     * @param y The second argument.
     *
     * @return <i>F<sub>3</sub>(a<sub>1</sub>, a<sub>2</sub>; b<sub>1</sub>, b<sub>2</sub>; c; x, y)</i>
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws LossOfPrecisionException If the sums at increasing working precisions don't agree.
     * @throws ArithmeticException If the arguments are outside of the region in which the series converges, or the function has a pole.
     */

    public static Apcomplex appellF3(Apcomplex a1, Apcomplex a2, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        boolean terminatesM = isNonPositiveInteger(a1) || isNonPositiveInteger(b1),
                terminatesN = isNonPositiveInteger(a2) || isNonPositiveInteger(b2);
        double ax = (terminatesM ? 0.0 : ApcomplexMath.abs(x).doubleValue()),
               ay = (terminatesN ? 0.0 : ApcomplexMath.abs(y).doubleValue());
        checkRadius(Math.max(ax, ay));
        return doubleSeries(F3, new Apcomplex[] { a1, a2, b1, b2, c }, x, y);
    }

    /**
     * Appell hypergeometric function <i>F<sub>4</sub></i>.<p>
     *
     * <i>F<sub>4</sub>(a, b; c<sub>1</sub>, c<sub>2</sub>; x, y)</i> = &sum;<sub>m,n</sub>
     * (<i>a</i>)<sub>m+n</sub> (<i>b</i>)<sub>m+n</sub> /
     * ((<i>c<sub>1</sub></i>)<sub>m</sub> (<i>c<sub>2</sub></i>)<sub>n</sub> <i>m</i>! <i>n</i>!) <i>x<sup>m</sup> y<sup>n</sup></i>.<p>
     *
     * The double series is summed for &radic;|<i>x</i>| + &radic;|<i>y</i>| &lt; 1, and for any
     * arguments in which it terminates.
     *
     * @param a The first parameter.
     * @param b The second parameter.
     * @param c1 The denominator parameter of the first argument.
     * @param c2 The denominator parameter of the second argument.
     * @param x The first argument.
     * @param y The second argument.
     *
     * @return <i>F<sub>4</sub>(a, b; c<sub>1</sub>, c<sub>2</sub>; x, y)</i>
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws LossOfPrecisionException If the sums at increasing working precisions don't agree.
     * @throws ArithmeticException If the arguments are outside of the region in which the series converges, or the function has a pole.
     */

    public static Apcomplex appellF4(Apcomplex a, Apcomplex b, Apcomplex c1, Apcomplex c2, Apcomplex x, Apcomplex y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (!isNonPositiveInteger(a) && !isNonPositiveInteger(b))
        {
            double root = Math.sqrt(ApcomplexMath.abs(x).doubleValue()) + Math.sqrt(ApcomplexMath.abs(y).doubleValue());
            checkRadius(root * root);
        }
        return doubleSeries(F4, new Apcomplex[] { a, b, c1, c2 }, x, y);
    }

    // The series of F2, F3 and F4 converges geometrically with this ratio; it is summed only inside of the region
    private static void checkRadius(double radius)
        throws ArithmeticException
    {
        if (!(radius < DOUBLE_SERIES_RADIUS))
        {
            throw new ArithmeticException("The arguments are outside of the region in which the double series is summed");
        }
    }

    // The double series of F2, F3 or F4, summed with more digits until two sums agree
    private static Apcomplex doubleSeries(int kind, Apcomplex[] parameters, Apcomplex x, Apcomplex y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(x.precision(), y.precision());
        for (Apcomplex p : parameters)
        {
            precision = Math.min(precision, p.precision());
        }
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Appell function to infinite precision", "appell.infinitePrecision");
        }
        long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION;
        Apcomplex previous = null;
        for (int attempt = 0; attempt <= MAX_ATTEMPTS; attempt++)
        {
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision, extraPrecision);
            Apcomplex[] p = new Apcomplex[parameters.length];
            for (int i = 0; i < p.length; i++)
            {
                p[i] = ApfloatHelperShim.ensurePrecision(parameters[i], workingPrecision);
            }
            Apcomplex result = doubleSeries(kind, p, ApfloatHelperShim.ensurePrecision(x, workingPrecision),
                                            ApfloatHelperShim.ensurePrecision(y, workingPrecision), workingPrecision);
            if (previous != null && GuardDigitsHelper.agree(previous, result, precision))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
            extraPrecision *= 2;
        }
        throw new LossOfPrecisionException("Complete loss of accurate digits");
    }

    // The sum over the rows m of the sums over n; a term is the previous one in its row times the ratio of n, the first term of a row
    // the first term of the previous row times the ratio of m
    private static Apcomplex doubleSeries(int kind, Apcomplex[] p, Apcomplex x, Apcomplex y, long workingPrecision)
    {
        int radix = x.radix();
        double size = 0.0;
        for (Apcomplex q : p)
        {
            size += ApcomplexMath.abs(q).doubleValue();
        }
        double ax = ApcomplexMath.abs(x).doubleValue(),
               ay = ApcomplexMath.abs(y).doubleValue();
        double radius = (kind == F2 ? ax + ay : kind == F3 ? Math.max(ax, ay) : Math.pow(Math.sqrt(ax) + Math.sqrt(ay), 2.0));
        long maxTerms = 1000L + (long) Math.min(1.0e6, (2.0 * workingPrecision * Math.log(radix) + 50.0 * size) / -Math.log(Math.min(Math.max(radius, 1.0e-6), DOUBLE_SERIES_RADIUS)));
        Apcomplex sum = ApfloatHelperShim.ZEROS[radix],
                  rowStart = ApfloatHelperShim.ONES[radix];
        long[] maxScale = { Long.MIN_VALUE };
        int smallRows = 0;
        for (long m = 0; m < maxTerms; m++)
        {
            ApfloatHelperShim.checkInterrupted();
            Apint mInt = new Apint(m, radix);
            Apcomplex term = rowStart,
                      rowSum = ApfloatHelperShim.ZEROS[radix];
            int smallTerms = 0;
            for (long n = 0; n < maxTerms; n++)
            {
                rowSum = add(rowSum, term, maxScale);
                if (isZero(term))
                {
                    break;
                }
                if (isNegligible(term, maxScale, workingPrecision))
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
                term = term.multiply(ratioN(kind, p, mInt, new Apint(n, radix), y));
            }
            sum = sum.add(rowSum);
            if (isZero(rowStart))
            {
                break;
            }
            if (isNegligible(rowSum, maxScale, workingPrecision) && isNegligible(rowStart, maxScale, workingPrecision))
            {
                if (++smallRows >= 3)
                {
                    break;
                }
            }
            else
            {
                smallRows = 0;
            }
            rowStart = rowStart.multiply(ratioM(kind, p, mInt, x));
        }
        return sum;
    }

    // The ratio of the first terms of the rows m + 1 and m
    private static Apcomplex ratioM(int kind, Apcomplex[] p, Apint m, Apcomplex x)
    {
        Apcomplex numerator, denominator;
        switch (kind)
        {
            case F2:
                // (a + m) (b1 + m) / ((c1 + m) (m + 1))
                numerator = p[0].add(m).multiply(p[1].add(m));
                denominator = p[3].add(m);
                break;
            case F3:
                // (a1 + m) (b1 + m) / ((c + m) (m + 1))
                numerator = p[0].add(m).multiply(p[2].add(m));
                denominator = p[4].add(m);
                break;
            default:
                // (a + m) (b + m) / ((c1 + m) (m + 1))
                numerator = p[0].add(m).multiply(p[1].add(m));
                denominator = p[2].add(m);
                break;
        }
        return numerator.multiply(x).divide(denominator.multiply(m.add(ApfloatHelperShim.ONES[m.radix()])));
    }

    // The ratio of the terms n + 1 and n of the row m
    private static Apcomplex ratioN(int kind, Apcomplex[] p, Apint m, Apint n, Apcomplex y)
    {
        Apint mn = m.add(n);
        Apcomplex numerator, denominator;
        switch (kind)
        {
            case F2:
                // (a + m + n) (b2 + n) / ((c2 + n) (n + 1))
                numerator = p[0].add(mn).multiply(p[2].add(n));
                denominator = p[4].add(n);
                break;
            case F3:
                // (a2 + n) (b2 + n) / ((c + m + n) (n + 1))
                numerator = p[1].add(n).multiply(p[3].add(n));
                denominator = p[4].add(mn);
                break;
            default:
                // (a + m + n) (b + m + n) / ((c2 + n) (n + 1))
                numerator = p[0].add(mn).multiply(p[1].add(mn));
                denominator = p[3].add(n);
                break;
        }
        return numerator.multiply(y).divide(denominator.multiply(n.add(ApfloatHelperShim.ONES[n.radix()])));
    }
}
