package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;

/**
 * Incomplete beta function for {@link Apcomplex} arguments, to the precision of the
 * arguments.<p>
 *
 * <code>ApcomplexMath.beta()</code> of three arguments is a hypergeometric series, whose
 * terms cancel for large parameters: at 17 digits B<sub>1/2</sub>(40, 60) comes out with no
 * correct digit. The method of this class evaluates the function with as many extra digits
 * as it takes for the result to come out the same twice.
 */

public class ApcomplexBetaMath
{
    private ApcomplexBetaMath()
    {
    }

    /**
     * Incomplete beta function B<sub><i>z</i></sub>(<i>a</i>, <i>b</i>).
     *
     * @param z The upper limit of the integral.
     * @param a The first parameter.
     * @param b The second parameter.
     *
     * @return B<sub><i>z</i></sub>(<i>a</i>, <i>b</i>)
     *
     * @throws ArithmeticException If the function is infinite.
     *
     * @see <a href="https://dlmf.nist.gov/8.17#E1">DLMF 8.17.1</a>
     */

    public static Apcomplex beta(Apcomplex z, Apcomplex a, Apcomplex b)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(z.precision(), Math.min(a.precision(), b.precision()));
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate incomplete beta function to infinite precision", "beta.infinitePrecision");
        }
        Apcomplex previous = null;
        for (long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION; extraPrecision <= 32 * ApfloatHelperShim.EXTRA_PRECISION; extraPrecision *= 2)
        {
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision, extraPrecision);
            Apcomplex result;
            try
            {
                result = ApcomplexMath.beta(ApfloatHelperShim.ensurePrecision(z, workingPrecision),
                                            ApfloatHelperShim.ensurePrecision(a, workingPrecision),
                                            ApfloatHelperShim.ensurePrecision(b, workingPrecision));
            }
            catch (LossOfPrecisionException lope)
            {
                previous = null;
                continue;
            }
            if (previous != null && GuardDigitsHelper.agree(previous, result, precision))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
        }
        throw new LossOfPrecisionException("Complete loss of accurate digits");
    }

    /**
     * Regularized incomplete beta function <i>I<sub>x</sub></i>(<i>a</i>, <i>b</i>) for
     * 0 &lt; <i>x</i> &lt; 1 and positive <i>a</i>, <i>b</i>, also for parameters of a large size.<p>
     *
     * The continued fraction of the function converges fast for an <i>x</i> below the mean
     * (<i>a</i> + 1) / (<i>a</i> + <i>b</i> + 2); above it the function is
     * 1 - <i>I</i><sub>1-<i>x</i></sub>(<i>b</i>, <i>a</i>). The factor
     * <i>x</i><sup><i>a</i></sup> (1 - <i>x</i>)<sup><i>b</i></sup> / (<i>a</i> B(<i>a</i>, <i>b</i>))
     * is the exponential of a sum of large terms which cancel; they are taken with as many more
     * digits as they have in front of the decimal point.
     *
     * @param x The upper limit of the integral, in (0, 1).
     * @param a The first parameter, which is positive.
     * @param b The second parameter, which is positive.
     * @param precision The precision of the result.
     *
     * @return <i>I<sub>x</sub></i>(<i>a</i>, <i>b</i>), or <code>null</code> if the arguments are
     *         not in their ranges, the logarithm of the prefactor can have more than 15 digits in
     *         front of the decimal point or the continued fraction does not converge.
     *
     * @see <a href="https://dlmf.nist.gov/8.17#v">DLMF 8.17(v)</a>
     */

    public static Apfloat betaRegularized(Apfloat x, Apfloat a, Apfloat b, long precision)
        throws ApfloatRuntimeException
    {
        int radix = x.radix();
        Apint one = ApfloatHelperShim.ONES[radix];
        if (x.signum() <= 0 || x.compareTo(one) >= 0 || a.signum() <= 0 || b.signum() <= 0)
        {
            return null;
        }
        // the digits in front of the decimal point of the logarithms in the prefactor, from the
        // scales, which stay in range when x is next to 0 or 1 or the parameters beyond a double
        long sum = a.add(b).add(new Apint(2, radix)).scale();
        long magnitude = Math.max(logScale(x) + a.scale(), logScale(one.subtract(x)) + b.scale());
        magnitude = Math.max(magnitude, sum + logDigits(sum));
        if (magnitude > MAX_LOG_DIGITS)
        {
            // the prefactor is beyond the range of the exponent
            return null;
        }
        long working = precision + Math.max(magnitude, 0) + 20;
        FixedPrecisionApfloatHelper w = new FixedPrecisionApfloatHelper(working);
        FixedPrecisionApfloatHelper estimate = new FixedPrecisionApfloatHelper(20);
        boolean complement = estimate.multiply(x, estimate.add(estimate.add(a, b), new Apint(2, radix))).compareTo(estimate.add(a, one)) > 0;
        Apfloat y = (complement ? w.subtract(one, x) : x),
                p = (complement ? b : a),
                q = (complement ? a : b);
        Apfloat fraction = betaContinuedFraction(w, y, p, q, working, Math.max(a.scale(), b.scale()));
        if (fraction == null)
        {
            return null;
        }
        Apfloat logBeta = w.subtract(w.add(w.logGamma(p), w.logGamma(q)), w.logGamma(w.add(p, q)));
        Apfloat logFactor = w.subtract(w.add(w.multiply(p, w.log(y)), w.multiply(q, w.log(w.subtract(one, y)))), logBeta);
        Apfloat value = w.divide(w.multiply(w.exp(logFactor), fraction), p);
        if (complement)
        {
            value = w.subtract(one, value);
        }
        return value.precision(Math.min(precision, value.precision()));
    }

    // Beyond this many digits in front of the decimal point of its logarithm a value is out of range
    private static final long MAX_LOG_DIGITS = 15;

    // An upper bound of the digits in front of the decimal point of |log(x)| for 0 < x < 1
    private static long logScale(Apfloat x)
    {
        // 10^(scale - 1) <= x < 10^scale with scale <= 0, so |log(x)| <= (1 - scale) log(10)
        return logDigits(1 - Math.min(x.scale(), 0));
    }

    // The digits in front of the decimal point of n * log(10), for n >= 1
    private static long logDigits(long n)
    {
        return (long) Math.ceil(Math.log10(Math.max(n, 1) * 2.3026)) + 1;
    }

    // The continued fraction of I_x(a, b) / (x^a (1 - x)^b / (a B(a, b))), modified Lentz algorithm
    private static Apfloat betaContinuedFraction(FixedPrecisionApfloatHelper w, Apfloat x, Apfloat a, Apfloat b, long working, long largestScale)
    {
        int radix = x.radix();
        Apint one = ApfloatHelperShim.ONES[radix];
        Apfloat tolerance = ApfloatMath.scale(one, -working),
                tiny = ApfloatMath.scale(one, -2 * working);
        Apfloat sum = w.add(a, b),
                c = one,
                d = w.subtract(one, w.divide(w.multiply(sum, x), w.add(a, one)));
        d = w.divide(one, (d.signum() == 0 ? tiny : d));
        Apfloat f = d;
        // near the mean the number of terms grows with the square root of the parameters
        long maxTerms = 10 * working + 20 * (long) Math.pow(10.0, Math.min(Math.max(largestScale, 0), 24) / 2.0) + 1000;
        for (long m = 1; m <= maxTerms; m++)
        {
            if (Thread.currentThread().isInterrupted())
            {
                return null;
            }
            Apint mm = new Apint(m, radix),
                  m2 = new Apint(2 * m, radix);
            Apfloat am2 = w.add(a, m2);
            // the even step m (b - m) x / ((a + 2m - 1) (a + 2m))
            Apfloat term = w.divide(w.multiply(w.multiply(mm, w.subtract(b, mm)), x), w.multiply(w.subtract(am2, one), am2));
            d = w.add(one, w.multiply(term, d));
            c = w.add(one, w.divide(term, c));
            d = w.divide(one, (d.signum() == 0 ? tiny : d));
            c = (c.signum() == 0 ? tiny : c);
            f = w.multiply(f, w.multiply(d, c));
            // the odd step -(a + m) (a + b + m) x / ((a + 2m) (a + 2m + 1))
            term = w.divide(w.multiply(w.multiply(w.add(a, mm), w.add(sum, mm)), x), w.multiply(am2, w.add(am2, one))).negate();
            d = w.add(one, w.multiply(term, d));
            c = w.add(one, w.divide(term, c));
            d = w.divide(one, (d.signum() == 0 ? tiny : d));
            c = (c.signum() == 0 ? tiny : c);
            Apfloat delta = w.multiply(d, c);
            f = w.multiply(f, delta);
            if (ApfloatMath.abs(w.subtract(delta, one)).compareTo(tolerance) <= 0)
            {
                return f;
            }
        }
        return null;
    }

    /**
     * Pochhammer symbol (<i>z</i>)<sub><i>n</i></sub> = &Gamma;(<i>z</i> + <i>n</i>) / &Gamma;(<i>z</i>),
     * also for a <i>z</i> of a large size.<p>
     *
     * <code>ApcomplexMath.pochhammer()</code> divides the two gamma functions. Their precision is
     * lower than the one of <i>z</i> by the digits of <i>z</i> in front of the decimal point -
     * (10<sup>15</sup>)<sub>-3</sub> has 4 digits with 17 - and beyond 10<sup>17</sup> they leave the
     * range of the exponent. For a large <i>z</i> with a positive real part this method takes the
     * difference of the logarithms of the gamma functions with that many more digits.
     *
     * @param z The first argument.
     * @param n The second argument.
     *
     * @return (<i>z</i>)<sub><i>n</i></sub>, to the smaller precision of the arguments.
     *
     * @throws ArithmeticException If the result is infinite.
     */

    public static Apcomplex pochhammer(Apcomplex z, Apcomplex n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(z.precision(), n.precision());
        if (precision == Apfloat.INFINITE || z.scale() <= 1 || z.real().signum() <= 0)
        {
            return ApcomplexMath.pochhammer(z, n);
        }
        if (n.isZero())
        {
            return new Apfloat(1, precision, z.radix());
        }
        // The logarithm of the gamma function has as many digits in front of the decimal point as its argument, and some more
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision, ApfloatHelperShim.EXTRA_PRECISION + z.scale() + 5);
        Apcomplex zw = ApfloatHelperShim.ensurePrecision(z, workingPrecision),
                  sum = zw.add(ApfloatHelperShim.ensurePrecision(n, workingPrecision));
        if (sum.real().signum() <= 0)
        {
            return ApcomplexMath.pochhammer(z, n);
        }
        Apcomplex result = ApcomplexMath.exp(ApcomplexMath.logGamma(sum).subtract(ApcomplexMath.logGamma(zw)));
        return ApfloatHelperShim.limitPrecision(result, precision);
    }
}
