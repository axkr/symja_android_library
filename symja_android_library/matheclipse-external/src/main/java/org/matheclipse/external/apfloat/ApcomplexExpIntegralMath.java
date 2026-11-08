package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.LossOfPrecisionException;

/**
 * The power series of the sine, cosine and exponential integrals for {@link Apcomplex} arguments
 * of moderate size, to a given precision.<p>
 *
 * The general algorithms of <code>ApcomplexMath.sinIntegral()</code> and its relatives take
 * seconds at a few hundred digits even for a small argument. For |<i>z</i>| up to
 * 40 the series are summed instead, with the extra digits which the
 * cancellation of their terms costs: the terms grow to <i>e</i><sup>|z|</sup> before they fall,
 * and a value next to a zero of the function loses digits to the cancellation of the sum against
 * its logarithmic part. The digits lost show in the result, so the sum is repeated with them.
 */

public class ApcomplexExpIntegralMath
{
    private ApcomplexExpIntegralMath()
    {
    }

    // The largest |z| for which the series are used
    private static final double SERIES_MAX_ABS = 40.0;

    // The largest number of digits, beyond those of the growth of the terms, which a value next to a zero may lose
    private static final long MAX_LOST_DIGITS = 2000;

    /**
     * Whether the series of this class are used for <code>z</code>: not at zero, where the
     * logarithmic ones have their pole, and not for a large |<i>z</i>|, where the terms grow to
     * <i>e</i><sup>|z|</sup> before they fall.
     *
     * @param z The argument.
     *
     * @return If the series of this class apply to <code>z</code>.
     */

    public static boolean isSeriesArgument(Apcomplex z)
    {
        if (z.real().signum() == 0 && z.imag().signum() == 0)
        {
            return false;
        }
        return abs(z) <= SERIES_MAX_ABS;
    }

    // |z| as a double: the full precision absolute value is a square root at the precision of z, and only the size is needed
    private static double abs(Apcomplex z)
    {
        return Math.hypot(z.real().doubleValue(), z.imag().doubleValue());
    }

    /**
     * Sine integral Si(<i>z</i>) = &sum;<sub>k&ge;0</sub> (-1)<sup>k</sup> <i>z</i><sup>2k+1</sup> / ((2k+1) (2k+1)!).
     *
     * @param z The argument, with {@link #isSeriesArgument(Apcomplex)}, or zero.
     * @param precision The precision of the result.
     *
     * @return Si(<i>z</i>)
     */

    public static Apcomplex sinIntegralSeries(Apcomplex z, long precision)
        throws ApfloatRuntimeException
    {
        return withLogarithm(z, precision, 1, 2, true, false, 0);
    }

    /**
     * Hyperbolic sine integral Shi(<i>z</i>) = &sum;<sub>k&ge;0</sub> <i>z</i><sup>2k+1</sup> / ((2k+1) (2k+1)!).
     *
     * @param z The argument, with {@link #isSeriesArgument(Apcomplex)}, or zero.
     * @param precision The precision of the result.
     *
     * @return Shi(<i>z</i>)
     */

    public static Apcomplex sinhIntegralSeries(Apcomplex z, long precision)
        throws ApfloatRuntimeException
    {
        return withLogarithm(z, precision, 1, 2, false, false, 0);
    }

    /**
     * Cosine integral Ci(<i>z</i>) = &gamma; + log(<i>z</i>) + &sum;<sub>k&ge;1</sub> (-1)<sup>k</sup> <i>z</i><sup>2k</sup> / (2k (2k)!).
     *
     * @param z The argument, with {@link #isSeriesArgument(Apcomplex)}.
     * @param precision The precision of the result.
     *
     * @return Ci(<i>z</i>)
     */

    public static Apcomplex cosIntegralSeries(Apcomplex z, long precision)
        throws ApfloatRuntimeException
    {
        // The first term, k = 1, is negative
        return withLogarithm(z, precision, 2, 2, true, true, 1);
    }

    /**
     * Hyperbolic cosine integral Chi(<i>z</i>) = &gamma; + log(<i>z</i>) + &sum;<sub>k&ge;1</sub> <i>z</i><sup>2k</sup> / (2k (2k)!).
     *
     * @param z The argument, with {@link #isSeriesArgument(Apcomplex)}.
     * @param precision The precision of the result.
     *
     * @return Chi(<i>z</i>)
     */

    public static Apcomplex coshIntegralSeries(Apcomplex z, long precision)
        throws ApfloatRuntimeException
    {
        return withLogarithm(z, precision, 2, 2, false, false, 1);
    }

    /**
     * Exponential integral Ei(<i>z</i>) = &gamma; + (log(<i>z</i>) - log(1/<i>z</i>)) / 2 + &sum;<sub>k&ge;1</sub> <i>z</i><sup>k</sup> / (k k!).<p>
     *
     * The mean of the logarithms is log(<i>z</i>) off the negative real axis and log(-<i>z</i>)
     * on it, where the value is real.
     *
     * @param z The argument, with {@link #isSeriesArgument(Apcomplex)}.
     * @param precision The precision of the result.
     *
     * @return Ei(<i>z</i>)
     */

    public static Apcomplex expIntegralEiSeries(Apcomplex z, long precision)
        throws ApfloatRuntimeException
    {
        return withLogarithm(z, precision, 1, 1, false, false, 2);
    }

    // EulerGamma + logarithm + Sum(+-z^n/(n*n!)) over n = first, first+step, ...
    // The digits lost are those of the largest part (the largest term of the sum, or the logarithm) in front of the value: the terms grow
    // to about e^|z| before they cancel, and a value next to a zero is small against all its parts. The value is accepted when the guard
    // digits cover the loss, otherwise it is computed again with the digits which were lost.
    // negative: whether the first term of the sum is negative; logarithm: 0 for none, 1 for Log(z), 2 for (Log(z)-Log(1/z))/2
    private static Apcomplex withLogarithm(Apcomplex z, long precision, int first, int step, boolean alternating, boolean negative, int logarithm)
    {
        if (z.real().signum() == 0 && z.imag().signum() == 0)
        {
            if (logarithm != 0)
            {
                // Ci, Chi and Ei have a logarithmic singularity at zero
                throw new ArithmeticException("Logarithmic singularity at zero");
            }
            return z;
        }
        double absZ = abs(z);
        long guard = (long) Math.ceil(absZ / Math.log(10.0)) + 10L,
             maxGuard = guard + MAX_LOST_DIGITS;
        double[] largest = new double[1];
        while (true)
        {
            ApfloatHelperShim.checkInterrupted();
            long working = precision + guard;
            Apcomplex w = z.precision(working),
                      sum = powerSeries(w, working, first, step, alternating, largest);
            if (negative)
            {
                sum = sum.negate();
            }
            double scale = largest[0];
            if (logarithm != 0)
            {
                Apcomplex log = ApcomplexMath.log(w);
                if (logarithm == 2)
                {
                    log = log.subtract(ApcomplexMath.log(Apcomplex.ONE.precision(working).divide(w))).divide(new Apfloat(2, working));
                }
                Apfloat euler = ApfloatMath.euler(working);
                scale = Math.max(scale, Math.max(abs(log), euler.doubleValue()));
                sum = sum.add(log).add(euler);
            }
            double magnitude = abs(sum);
            if (magnitude > 0.0)
            {
                long lost = (long) Math.ceil(Math.log10(scale / magnitude));
                if (lost + 5L <= guard)
                {
                    return sum.precision(precision);
                }
                guard = Math.max(guard + 10L, lost + 10L);
            }
            else
            {
                // All digits cancelled: the value is below the working precision
                guard = 2L * guard + precision;
            }
            if (guard > maxGuard)
            {
                throw new LossOfPrecisionException("Complete loss of accurate digits next to a zero", "lossOfPrecision");
            }
        }
    }

    // Sum(+-z^n/(n*n!)) over n = first, first+step, ...; largest[0] receives the absolute value of the largest term
    private static Apcomplex powerSeries(Apcomplex w, long working, int first, int step, boolean alternating, double[] largest)
    {
        // p is z^n/n!
        Apcomplex p = w;
        for (int n = 2; n <= first; n++)
        {
            p = p.multiply(w).divide(new Apfloat(n, working));
        }
        Apcomplex factor = (step == 1 ? w : w.multiply(w));
        if (alternating)
        {
            factor = factor.negate();
        }
        Apcomplex sum = p.divide(new Apfloat(first, working));
        largest[0] = abs(sum);
        double absZ = abs(w);
        for (long n = first; ; n += step)
        {
            long next = n + step;
            Apfloat denominator = (step == 1 ? new Apfloat(next, working) : new Apfloat((n + 1) * next, working));
            p = p.multiply(factor).divide(denominator);
            Apcomplex term = p.divide(new Apfloat(next, working));
            sum = sum.add(term);
            largest[0] = Math.max(largest[0], abs(term));
            // The term is below 10^-working of the sum (or of 1, next to a zero): compared by the scales, which cost nothing
            if (next > absZ && term.scale() <= Math.max(sum.scale(), 1) - working - 1)
            {
                return sum;
            }
        }
    }
}
