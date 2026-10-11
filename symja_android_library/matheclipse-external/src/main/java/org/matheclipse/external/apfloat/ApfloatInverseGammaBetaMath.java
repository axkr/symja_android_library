package org.matheclipse.external.apfloat;

import java.math.RoundingMode;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.LossOfPrecisionException;

/**
 * Inverses of the regularized incomplete gamma and beta functions for {@link Apfloat}
 * arguments.<p>
 *
 * Each inverse is a root of <i>F</i>(<i>x</i>) - <i>p</i> for a function <i>F</i> which
 * increases from 0 to 1. The root is found by Newton's method, kept inside of the interval which
 * the signs of the residual leave, in the tail of the function which is the smaller one. The
 * incomplete functions themselves cancel heavily for large parameters, so the working precision
 * is raised until the function reproduces <i>p</i> at the root.
 */

public class ApfloatInverseGammaBetaMath
{
    private ApfloatInverseGammaBetaMath()
    {
    }

    // Extra digits of the working precision which are tried one after the other
    private static final long[] EXTRA_PRECISIONS = { 20, 100, 700, 3000 };

    private static final int MAX_STEPS = 400;

    // An increasing function on an interval, with its derivative
    private interface Distribution
    {
        // F(x) - target, times the constant which the derivative is taken of
        Apfloat residual(Apfloat x, Apfloat target, FixedPrecisionApfloatHelper helper);

        // The derivative of the residual, which is positive
        Apfloat density(Apfloat x, FixedPrecisionApfloatHelper helper);

        // F(x)
        Apfloat value(Apfloat x, FixedPrecisionApfloatHelper helper);

        // The upper end of the interval, or null if it is infinite
        Apfloat upperEnd(int radix);

        // A first approximation of the root, from the leading term of the function in its small tail
        Apfloat seed(Apfloat target, FixedPrecisionApfloatHelper helper);
    }

    /**
     * Inverse of the regularized incomplete beta function: the <i>x</i> with
     * <i>I<sub>x</sub></i>(<i>a</i>, <i>b</i>) = <i>p</i>.
     *
     * @param p The value of the function, in [0, 1].
     * @param a The first parameter, which is positive.
     * @param b The second parameter, which is positive.
     * @param precision The precision of the result.
     * @param seed An approximation of the result, or <code>NaN</code> if there is none.
     *
     * @return The inverse.
     *
     * @throws ArithmeticException If the arguments are not in their ranges.
     * @throws LossOfPrecisionException If the incomplete beta function loses more digits than are tried.
     *
     * @see <a href="https://dlmf.nist.gov/8.17">DLMF 8.17</a>
     */

    public static Apfloat inverseBetaRegularized(Apfloat p, Apfloat a, Apfloat b, long precision, double seed)
        throws ArithmeticException, ApfloatRuntimeException
    {
        int radix = p.radix();
        Apint one = ApfloatHelperShim.ONES[radix];
        if (a.signum() <= 0 || b.signum() <= 0 || p.signum() < 0 || p.compareTo(one) > 0)
        {
            throw new ArithmeticException("Inverse beta of arguments which are not in their ranges");
        }
        if (p.signum() == 0)
        {
            return p;
        }
        Apfloat complement = one.subtract(p.precision(Math.max(p.precision(), ApfloatHelperShim.extendPrecision(precision))));
        if (complement.signum() == 0)
        {
            return new Apfloat(1, precision, radix);
        }
        if (complement.compareTo(p) < 0)
        {
            // I_x(a, b) = 1 - I_(1-x)(b, a): the smaller tail keeps its digits
            Apfloat y = inverse(new Beta(b, a), complement, precision, 1.0 - seed);
            return ApfloatMath.roundToPrecision(one.subtract(y), precision, RoundingMode.HALF_EVEN);
        }
        return inverse(new Beta(a, b), p, precision, seed);
    }

    /**
     * Inverse of the regularized incomplete gamma function: the <i>x</i> with
     * <i>Q</i>(<i>a</i>, <i>x</i>) = <i>q</i> for the upper function, or with
     * <i>P</i>(<i>a</i>, <i>x</i>) = <i>q</i> for the lower one.
     *
     * @param a The parameter, which is positive.
     * @param q The value of the function, in (0, 1) for the upper function and in [0, 1) for the lower one.
     * @param upper Whether the upper function <i>Q</i> is inverted.
     * @param precision The precision of the result.
     * @param seed An approximation of the result, or <code>NaN</code> if there is none.
     *
     * @return The inverse.
     *
     * @throws ArithmeticException If the arguments are not in their ranges, or if the result is infinite.
     * @throws LossOfPrecisionException If the incomplete gamma function loses more digits than are tried.
     *
     * @see <a href="https://dlmf.nist.gov/8.2">DLMF 8.2</a>
     */

    public static Apfloat inverseGammaRegularized(Apfloat a, Apfloat q, boolean upper, long precision, double seed)
        throws ArithmeticException, ApfloatRuntimeException
    {
        int radix = q.radix();
        Apint one = ApfloatHelperShim.ONES[radix];
        if (a.signum() <= 0 || q.signum() < 0 || q.compareTo(one) > 0)
        {
            throw new ArithmeticException("Inverse gamma of arguments which are not in their ranges");
        }
        Apfloat complement = one.subtract(q.precision(Math.max(q.precision(), ApfloatHelperShim.extendPrecision(precision))));
        if ((upper ? complement : q).signum() == 0)
        {
            return ApfloatHelperShim.ZEROS[radix];
        }
        if ((upper ? q : complement).signum() == 0)
        {
            throw new ArithmeticException("Inverse gamma is infinite");
        }
        // The lower function is inverted for a small P, and the upper one for a small Q
        boolean smallUpper = (upper ? q.compareTo(complement) <= 0 : complement.compareTo(q) < 0);
        Apfloat target = (smallUpper == upper ? q : complement);
        return inverse(new Gamma(a, smallUpper), target, precision, seed);
    }

    /**
     * Inverse of the generalized regularized incomplete beta function: the <i>x</i> with
     * <i>I<sub>x</sub></i>(<i>a</i>, <i>b</i>) = <i>I<sub>z1</sub></i>(<i>a</i>, <i>b</i>) + <i>z2</i>.
     *
     * @param z1 The lower limit of the integral.
     * @param z2 The value of the generalized function.
     * @param a The first parameter, which is positive.
     * @param b The second parameter, which is positive.
     * @param precision The precision of the result.
     *
     * @return The inverse.
     */

    public static Apfloat inverseBetaRegularized(Apfloat z1, Apfloat z2, Apfloat a, Apfloat b, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (z2.signum() == 0)
        {
            return z1;
        }
        if (z1.signum() == 0)
        {
            return inverseBetaRegularized(z2, a, b, precision, Double.NaN);
        }
        int radix = z1.radix();
        Apint one = ApfloatHelperShim.ONES[radix];
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(workingPrecision);
        if (z1.compareTo(one) == 0)
        {
            // I_z1 is 1 and the target 1 + z2 with a negative z2: by I_x(a, b) = 1 - I_(1-x)(b, a) the small -z2 keeps its digits
            Apfloat reflected = inverseBetaRegularized(z2.negate(), b, a, workingPrecision, Double.NaN);
            return ApfloatMath.roundToPrecision(one.subtract(reflected), precision, RoundingMode.HALF_EVEN);
        }
        Apfloat lower = helper.divide(helper.beta(z1, a, b), helper.beta(a, b));
        return inverseBetaRegularized(helper.add(lower, z2), a, b, precision, Double.NaN);
    }

    /**
     * Inverse of the generalized regularized incomplete gamma function: the <i>x</i> with
     * <i>Q</i>(<i>a</i>, <i>z1</i>) - <i>Q</i>(<i>a</i>, <i>x</i>) = <i>z2</i>.
     *
     * @param a The parameter, which is positive.
     * @param z1 The lower limit of the integral.
     * @param z2 The value of the generalized function.
     * @param precision The precision of the result.
     *
     * @return The inverse.
     */

    public static Apfloat inverseGammaRegularized(Apfloat a, Apfloat z1, Apfloat z2, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (z1.signum() == 0)
        {
            // Q(a, 0) is 1, so this is the inverse of the lower function, which keeps the digits of a small z2
            return inverseGammaRegularized(a, z2, false, precision, Double.NaN);
        }
        FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(ApfloatHelperShim.extendPrecision(precision));
        Apfloat q = helper.subtract(helper.divide(helper.gamma(a, z1), helper.gamma(a)), z2);
        return inverseGammaRegularized(a, q, true, precision, Double.NaN);
    }

    // The root of F(x) = target for the lower, or 1 - F(x) = target for the upper function
    private static Apfloat inverse(Distribution distribution, Apfloat target, long precision, double seed)
    {
        ApfloatRuntimeException failure = null;
        for (long extraPrecision : EXTRA_PRECISIONS)
        {
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision, extraPrecision);
            FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(workingPrecision);
            try
            {
                Apfloat x = solve(distribution, target, precision, seed, helper);
                if (isVerified(distribution, x, target, precision, helper))
                {
                    return ApfloatMath.roundToPrecision(x, precision, RoundingMode.HALF_EVEN);
                }
            }
            catch (LossOfPrecisionException lope)
            {
                // The function is not computable at this working precision
                failure = lope;
            }
        }
        throw (failure != null ? failure : new LossOfPrecisionException("Complete loss of accurate digits"));
    }

    // Newton's method, with a bisection step whenever it would leave the interval which the signs of the residual leave
    private static Apfloat solve(Distribution distribution, Apfloat target, long precision, double seed, FixedPrecisionApfloatHelper helper)
    {
        int radix = target.radix();
        Apint two = new Apint(2, radix);
        Apfloat lower = ApfloatHelperShim.ZEROS[radix],
                upper = distribution.upperEnd(radix),
                x = (seed > 0.0 && !Double.isInfinite(seed) && !Double.isNaN(seed) ? new Apfloat(seed, 17, radix) : null);
        if (x != null && upper != null && x.compareTo(upper) >= 0)
        {
            x = null;
        }
        if (x == null)
        {
            x = distribution.seed(target, helper);
        }
        for (int step = 0; step < MAX_STEPS; step++)
        {
            Apfloat residual = distribution.residual(x, target, helper);
            if (residual.signum() == 0)
            {
                return x;
            }
            if (residual.signum() > 0)
            {
                upper = x;
            }
            else
            {
                lower = x;
            }
            Apfloat density = distribution.density(x, helper),
                    next = (density.signum() == 0 ? null : helper.subtract(x, helper.divide(residual, density)));
            if (upper == null)
            {
                // No upper end yet: at most twice as far, a step from the flat part of the function goes anywhere
                Apfloat twice = helper.multiply(x, two);
                if (next == null || next.compareTo(lower) <= 0 || next.compareTo(twice) > 0)
                {
                    next = twice;
                }
            }
            else if (next == null || next.compareTo(lower) <= 0 || next.compareTo(upper) >= 0)
            {
                // Outside of the interval: its middle, in the geometric sense if the ends are far apart
                next = (lower.signum() > 0 && upper.compareTo(helper.multiply(lower, new Apint(4, radix))) > 0 ?
                        helper.sqrt(helper.multiply(lower, upper)) :
                        helper.divide(helper.add(lower, upper), two));
            }
            Apfloat change = helper.subtract(next, x);
            x = next;
            if (change.signum() == 0 || change.scale() < x.scale() - precision - 2)
            {
                return x;
            }
        }
        throw new LossOfPrecisionException("Inverse doesn't converge");
    }

    // Does the function reproduce the target at the root?
    private static boolean isVerified(Distribution distribution, Apfloat x, Apfloat target, long precision, FixedPrecisionApfloatHelper helper)
    {
        Apfloat difference = helper.subtract(distribution.value(x, helper), target);
        return difference.signum() == 0 || difference.scale() <= target.scale() - precision;
    }

    // I_x(a, b) on (0, 1)
    private static class Beta
        implements Distribution
    {
        Beta(Apfloat a, Apfloat b)
        {
            this.a = a;
            this.b = b;
        }

        public Apfloat residual(Apfloat x, Apfloat target, FixedPrecisionApfloatHelper helper)
        {
            return helper.subtract(helper.beta(x, this.a, this.b), helper.multiply(target, helper.beta(this.a, this.b)));
        }

        public Apfloat density(Apfloat x, FixedPrecisionApfloatHelper helper)
        {
            Apint one = ApfloatHelperShim.ONES[x.radix()];
            return helper.multiply(helper.pow(x, helper.subtract(this.a, one)), helper.pow(helper.subtract(one, x), helper.subtract(this.b, one)));
        }

        public Apfloat value(Apfloat x, FixedPrecisionApfloatHelper helper)
        {
            return helper.divide(helper.beta(x, this.a, this.b), helper.beta(this.a, this.b));
        }

        public Apfloat upperEnd(int radix)
        {
            return ApfloatHelperShim.ONES[radix];
        }

        public Apfloat seed(Apfloat target, FixedPrecisionApfloatHelper helper)
        {
            // I_x(a, b) ~ x^a / (a B(a, b))
            int radix = target.radix();
            Apfloat half = helper.divide(ApfloatHelperShim.ONES[radix], new Apint(2, radix)),
                    x = helper.exp(helper.divide(helper.log(helper.multiply(helper.multiply(target, this.a), helper.beta(this.a, this.b))), this.a));
            return (x.compareTo(half) < 0 ? x : half);
        }

        private Apfloat a,
                        b;
    }

    // P(a, x) on (0, infinity), or 1 - Q(a, x) with the residual of Q
    private static class Gamma
        implements Distribution
    {
        Gamma(Apfloat a, boolean upper)
        {
            this.a = a;
            this.upper = upper;
        }

        public Apfloat residual(Apfloat x, Apfloat target, FixedPrecisionApfloatHelper helper)
        {
            Apfloat scaled = helper.multiply(target, helper.gamma(this.a));
            // The upper function decreases: its residual is taken with the other sign
            return (this.upper ? helper.subtract(scaled, helper.gamma(this.a, x)) :
                                 helper.subtract(helper.gamma(this.a, ApfloatHelperShim.ZEROS[x.radix()], x), scaled));
        }

        public Apfloat density(Apfloat x, FixedPrecisionApfloatHelper helper)
        {
            return helper.multiply(helper.pow(x, helper.subtract(this.a, ApfloatHelperShim.ONES[x.radix()])), helper.exp(x.negate()));
        }

        public Apfloat value(Apfloat x, FixedPrecisionApfloatHelper helper)
        {
            Apfloat incomplete = (this.upper ? helper.gamma(this.a, x) : helper.gamma(this.a, ApfloatHelperShim.ZEROS[x.radix()], x));
            return helper.divide(incomplete, helper.gamma(this.a));
        }

        public Apfloat upperEnd(int radix)
        {
            return null;
        }

        public Apfloat seed(Apfloat target, FixedPrecisionApfloatHelper helper)
        {
            Apint one = ApfloatHelperShim.ONES[target.radix()];
            if (this.upper)
            {
                // Q(a, x) ~ x^(a-1) e^(-x) / Gamma(a)
                Apfloat x = helper.log(helper.multiply(target, helper.gamma(this.a))).negate();
                return (x.compareTo(one) > 0 ? x : one);
            }
            // P(a, x) ~ x^a / Gamma(a + 1)
            return helper.exp(helper.divide(helper.log(helper.multiply(target, helper.gamma(helper.add(this.a, one)))), this.a));
        }

        private Apfloat a;
        private boolean upper;
    }
}
