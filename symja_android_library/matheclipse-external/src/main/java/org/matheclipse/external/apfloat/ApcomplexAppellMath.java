package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;

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

    /**
     * Appell hypergeometric function <i>F<sub>1</sub></i>.<p>
     *
     * <i>F<sub>1</sub>(a; b<sub>1</sub>, b<sub>2</sub>; c; x, y)</i> = &sum;<sub>m,n</sub>
     * (<i>a</i>)<sub>m+n</sub> (<i>b<sub>1</sub></i>)<sub>m</sub> (<i>b<sub>2</sub></i>)<sub>n</sub> /
     * ((<i>c</i>)<sub>m+n</sub> <i>m</i>! <i>n</i>!) <i>x<sup>m</sup> y<sup>n</sup></i>
     * and its analytic continuation.<p>
     *
     * The function is written in the one of its six Euler transformations which has the
     * smallest arguments. If both are inside of the unit circle the power series is summed,
     * with the three term recurrence of its coefficients. Otherwise the function is summed
     * as a series of hypergeometric functions in the smaller one of the two arguments, which
     * is much slower. This covers the arguments for which one of the transformed arguments
     * has an absolute value less than 0.9; for all other arguments an
     * <code>ArithmeticException</code> is thrown.
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
     * @throws ArithmeticException If an argument is real and at least one (the branch cut), if the function has a pole, or if the arguments are outside of the range which is covered.
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
        if (onBranchCut(x) || onBranchCut(y))
        {
            throw new ArithmeticException("Appell F1 on the branch cut");
        }
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        a = ApfloatHelperShim.ensurePrecision(a, workingPrecision);
        b1 = ApfloatHelperShim.ensurePrecision(b1, workingPrecision);
        b2 = ApfloatHelperShim.ensurePrecision(b2, workingPrecision);
        c = ApfloatHelperShim.ensurePrecision(c, workingPrecision);
        x = ApfloatHelperShim.ensurePrecision(x, workingPrecision);
        y = ApfloatHelperShim.ensurePrecision(y, workingPrecision);

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
        int best = -1;
        double bestScore = Double.MAX_VALUE;
        for (int i = 0; i < forms.length; i++)
        {
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
        Apcomplex series = (bestScore <= DOUBLE_SERIES_RADIUS ?
                            doubleSeries(form[0], form[1], form[2], c, form[3], form[4], workingPrecision) :
                            series(form[0], form[1], form[2], c, form[3], form[4], workingPrecision));
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
        return ApfloatHelperShim.limitPrecision(factor.multiply(series), precision);
    }

    // The function has a branch cut for a real argument from one to infinity
    private static boolean onBranchCut(Apcomplex z)
    {
        return z.imag().signum() == 0 && z.real().compareTo(ApfloatHelperShim.ONES[z.radix()]) >= 0;
    }

    private static Apcomplex pow(Apcomplex z, Apcomplex w)
    {
        if (w.real().signum() == 0 && w.imag().signum() == 0)
        {
            return ApfloatHelperShim.ONES[z.radix()];
        }
        return ApcomplexMath.pow(z, w);
    }

    // F1(a; b1, b2; c; x, y) = sum (a)_k / (c)_k p_k, where p_k is the coefficient of t^k in (1 - x t)^(-b1) (1 - y t)^(-b2):
    // (k + 1) p_(k+1) = ((x + y) k + b1 x + b2 y) p_k - x y (k - 1 + b1 + b2) p_(k-1)
    private static Apcomplex doubleSeries(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y, long workingPrecision)
    {
        int radix = x.radix();
        double radius = Math.max(ApcomplexMath.abs(x).doubleValue(), ApcomplexMath.abs(y).doubleValue());
        // The terms decrease like radius^k
        long maxTerms = 1000L + (long) (2.0 * workingPrecision * Math.log(radix) / -Math.log(Math.min(radius, DOUBLE_SERIES_RADIUS)));
        Apcomplex xPlusY = x.add(y),
                  linear = b1.multiply(x).add(b2.multiply(y)),
                  xy = x.multiply(y),
                  b = b1.add(b2),
                  sum = ApfloatHelperShim.ZEROS[radix],
                  pochhammer = ApfloatHelperShim.ONES[radix],
                  previous = ApfloatHelperShim.ZEROS[radix],
                  p = ApfloatHelperShim.ONES[radix];
        int smallTerms = 0;
        for (long k = 0; k < maxTerms; k++)
        {
            Apint kInt = new Apint(k, radix);
            Apcomplex term = pochhammer.multiply(p);
            sum = sum.add(term);
            if (isZero(term) || (!isZero(sum) && term.scale() < sum.scale() - workingPrecision))
            {
                if (++smallTerms >= 3)
                {
                    return sum;
                }
            }
            else
            {
                smallTerms = 0;
            }
            Apcomplex cK = c.add(kInt);
            if (isZero(cK))
            {
                if (isZero(pochhammer))
                {
                    // The series has terminated before
                    return sum;
                }
                throw new ArithmeticException("Appell F1 of a nonpositive integer denominator parameter");
            }
            Apcomplex next = xPlusY.multiply(kInt).add(linear).multiply(p)
                                   .subtract(xy.multiply(b.add(new Apint(k - 1, radix))).multiply(previous))
                                   .divide(new Apint(k + 1, radix));
            previous = p;
            p = next;
            pochhammer = pochhammer.multiply(a.add(kInt)).divide(cK);
        }
        throw new ArithmeticException("Appell F1 series does not converge");
    }

    // F1(a; b1, b2; c; x, y) = sum (a)_m (b1)_m / ((c)_m m!) x^m 2F1(a + m, b2; c + m; y), summed in the smaller argument
    private static Apcomplex series(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y, long workingPrecision)
    {
        if (ApcomplexMath.abs(x).compareTo(ApcomplexMath.abs(y)) > 0)
        {
            // F1(a; b1, b2; c; x, y) = F1(a; b2, b1; c; y, x)
            Apcomplex t = b1;
            b1 = b2;
            b2 = t;
            t = x;
            x = y;
            y = t;
        }
        if (!(ApcomplexMath.abs(x).doubleValue() < SERIES_RADIUS))
        {
            throw new ArithmeticException("Appell F1 is not implemented for these arguments");
        }
        int radix = x.radix();
        Apint one = ApfloatHelperShim.ONES[radix];
        long maxTerms = Math.min(100000L, 200L + 25L * workingPrecision);
        Apcomplex sum = ApfloatHelperShim.ZEROS[radix],
                  coefficient = one;
        int smallTerms = 0;
        for (long m = 0; m < maxTerms; m++)
        {
            Apint mInt = new Apint(m, radix);
            Apcomplex aM = a.add(mInt),
                      cM = c.add(mInt);
            if (isZero(cM))
            {
                throw new ArithmeticException("Appell F1 of a nonpositive integer denominator parameter");
            }
            Apcomplex term = (isZero(coefficient) ? coefficient : coefficient.multiply(hypergeometric(aM, b2, cM, y)));
            sum = sum.add(term);
            if (isZero(term) || (!isZero(sum) && term.scale() < sum.scale() - workingPrecision))
            {
                if (++smallTerms >= 3)
                {
                    return sum;
                }
            }
            else
            {
                smallTerms = 0;
            }
            Apcomplex b1M = b1.add(mInt);
            if (isZero(aM) || isZero(b1M))
            {
                // The series terminates
                return sum;
            }
            coefficient = coefficient.multiply(aM).multiply(b1M).multiply(x).divide(cM.multiply(new Apint(m + 1, radix)));
        }
        throw new ArithmeticException("Appell F1 series does not converge");
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
