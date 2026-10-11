package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;

/**
 * Generalized hypergeometric functions with parameters, at which the series of the library
 * doesn't serve.
 */

public class ApcomplexHypergeometricMath
{
    private ApcomplexHypergeometricMath()
    {
    }

    /**
     * The generalized hypergeometric function
     * <sub><i>p</i></sub><i>F</i><sub><i>p</i></sub>(1, &hellip;, 1; 2, &hellip;, 2; <i>z</i>)
     * with <i>p</i> ones and <i>p</i> twos, which is the entire function
     * &sum; <i>z</i><sup><i>k</i></sup> / ((<i>k</i> + 1)<sup><i>p</i></sup> <i>k</i>!).<p>
     *
     * For <i>z</i> far to the left the terms grow to about <i>e</i><sup>|<i>z</i>|</sup>
     * before they cancel, so the sum is accumulated with |<i>z</i>| / log(radix) extra digits:
     * the time and the memory grow with |<i>z</i>|.
     *
     * @param p The number of ones, and of twos.
     * @param z The argument.
     *
     * @return The value, to the precision of the argument.
     *
     * @throws ArithmeticException If the terms don't decrease within the number of terms which the size of the argument asks for.
     */

    public static Apcomplex hypergeometricOnesTwos(int p, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = z.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate hypergeometric function to infinite precision", "hypergeometric.infinitePrecision");
        }
        int radix = z.radix();
        double absZ = ApcomplexMath.abs(z).doubleValue();
        long guardDigits = (long) (absZ / Math.log(radix)) + 10,
             workingPrecision = ApfloatHelperShim.extendPrecision(precision, guardDigits),
             // The terms grow until k is about |z| and decrease at least geometrically from 2 |z| on
             maxIterations = 2 * (long) absZ + 4 * workingPrecision + 100;
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        Apcomplex term = new Apfloat(1, workingPrecision, radix),
                  sum = term;
        Apfloat maxAbs = ApfloatHelperShim.ONES[radix],
                // Relative to the largest term, not to the result: the guard digits are for the difference
                epsilon = ApfloatMath.scale(new Apfloat(1, workingPrecision, radix), -workingPrecision);
        for (long k = 1; ; k++)
        {
            if (k > maxIterations)
            {
                throw new ArithmeticException("Hypergeometric series does not converge");
            }
            // z^k / k!
            term = term.multiply(z).divide(new Apfloat(k, workingPrecision, radix));
            Apcomplex summand = term.divide(ApfloatMath.pow(new Apfloat(k + 1, workingPrecision, radix), p));
            sum = sum.add(summand);
            Apfloat absSummand = ApcomplexMath.abs(summand);
            if (absSummand.compareTo(maxAbs) > 0)
            {
                maxAbs = absSummand;
            }
            else if (k > absZ && absSummand.compareTo(maxAbs.multiply(epsilon)) < 0)
            {
                break;
            }
        }
        return ApfloatHelperShim.limitPrecision(sum, precision);
    }

    // Most extra digits which are added for the cancellation of a series
    private static final long MAX_EXTRA_PRECISION = 1000;

    // Most terms of a series
    private static final long MAX_TERMS = 200000;

    // The series in z is summed up to this size of z, and the series in 1/z from the inverse of it
    private static final double SERIES_RADIUS = 0.99;

    /**
     * Generalized hypergeometric function
     * <sub><i>p</i></sub><i>F</i><sub><i>q</i></sub>(<i>a</i><sub>1</sub>, &hellip;, <i>a</i><sub><i>p</i></sub>;
     * <i>b</i><sub>1</sub>, &hellip;, <i>b</i><sub><i>q</i></sub>; <i>z</i>).<p>
     *
     * The series is summed with the digits which its terms cancel. For <i>p</i> = <i>q</i> + 1
     * and |<i>z</i>| &gt; 1 the function is continued with the expansion in 1 / <i>z</i>; on the
     * cut along the real axis from 1 it has the value from below. Next to the unit circle,
     * and for <i>p</i> &gt; <i>q</i> + 1 unless the series terminates, there is no result.
     *
     * @param a The upper parameters.
     * @param b The lower parameters.
     * @param z The argument.
     *
     * @return The value, to the smallest precision of the parameters and the argument.
     *
     * @throws ArithmeticException If the function is infinite, or cannot be calculated at the argument.
     *
     * @see <a href="https://dlmf.nist.gov/16.2">DLMF 16.2</a>
     * @see <a href="https://dlmf.nist.gov/16.8#E8">DLMF 16.8.8</a>, the expansion in 1 / <i>z</i>
     */

    public static Apcomplex hypergeometricPFQ(Apcomplex[] a, Apcomplex[] b, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = z.precision();
        for (Apcomplex x : a)
        {
            precision = Math.min(precision, x.precision());
        }
        for (Apcomplex x : b)
        {
            precision = Math.min(precision, x.precision());
        }
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate hypergeometric function to infinite precision", "hypergeometric.infinitePrecision");
        }
        int radix = z.radix();
        Apcomplex one = new Apfloat(1, precision, radix);
        if (z.isZero())
        {
            return one;
        }
        // Parameters which are both above and below cancel
        java.util.List<Apcomplex> upper = new java.util.ArrayList<Apcomplex>(java.util.Arrays.asList(a)),
                                  lower = new java.util.ArrayList<Apcomplex>(java.util.Arrays.asList(b));
        for (java.util.Iterator<Apcomplex> i = upper.iterator(); i.hasNext();)
        {
            if (lower.remove(i.next()))
            {
                i.remove();
            }
        }
        a = upper.toArray(new Apcomplex[0]);
        b = lower.toArray(new Apcomplex[0]);

        // A non-positive integer above ends the series, one below is a pole unless the series ends before it
        long last = -1;
        for (Apcomplex x : a)
        {
            if (x.isZero())
            {
                return one;
            }
            if (isNonPositiveInteger(x))
            {
                long n = x.real().negate().longValue();
                last = (last < 0 ? n : Math.min(last, n));
            }
        }
        for (Apcomplex x : b)
        {
            if (isNonPositiveInteger(x) && (last < 0 || x.real().negate().longValue() < last))
            {
                throw new ArithmeticException("Hypergeometric function is infinite");
            }
        }
        double size = ApcomplexMath.abs(z).doubleValue();
        if (last < 0)
        {
            if (a.length > b.length + 1)
            {
                throw new ArithmeticException("Hypergeometric series diverges");
            }
            if (a.length == b.length + 1 && size > SERIES_RADIUS)
            {
                if (size < 1.0 / SERIES_RADIUS)
                {
                    throw new ArithmeticException("Hypergeometric series next to the unit circle");
                }
                return inverseArgument(a, b, z, precision);
            }
        }
        if (last < 0 && a.length <= b.length)
        {
            // The largest term has about this many digits, which is what the terms cancel on the left
            double digits = (b.length + 1 - a.length) * Math.pow(size, 1.0 / (b.length + 1 - a.length)) / Math.log(radix);
            if (digits > (z.real().signum() < 0 ? 1 : 3) * (double) MAX_EXTRA_PRECISION)
            {
                throw new ArithmeticException("Hypergeometric series needs too many digits at this argument");
            }
        }
        long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION / 2;
        while (true)
        {
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision, extraPrecision);
            long[] loss = new long[1];
            Apcomplex result = series(a, b, z, workingPrecision, loss);
            if (loss[0] + ApfloatHelperShim.getSmallExtraPrecision(radix) <= extraPrecision)
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            extraPrecision = loss[0] + ApfloatHelperShim.EXTRA_PRECISION / 2;
            if (extraPrecision > MAX_EXTRA_PRECISION)
            {
                throw new ArithmeticException("Hypergeometric series loses " + loss[0] + " digits");
            }
        }
    }

    private static boolean isNonPositiveInteger(Apcomplex x)
    {
        return x.imag().signum() == 0 && x.real().signum() <= 0 && x.real().isInteger();
    }

    // The sum of the series, and the number of digits which the largest term has in front of the sum
    private static Apcomplex series(Apcomplex[] a, Apcomplex[] b, Apcomplex z, long workingPrecision, long[] loss)
    {
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        int radix = z.radix();
        a = ensurePrecision(a, workingPrecision);
        b = ensurePrecision(b, workingPrecision);
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        Apcomplex term = new Apfloat(1, workingPrecision, radix),
                  sum = term;
        long largest = 1;
        // From here on the terms decrease: the factors of a term are within a factor of 2 of their limits
        double decreasing = 0.0;
        for (Apcomplex x : a)
        {
            decreasing = Math.max(decreasing, 4.0 * ApcomplexMath.abs(x).doubleValue());
        }
        for (Apcomplex x : b)
        {
            decreasing = Math.max(decreasing, 4.0 * ApcomplexMath.abs(x).doubleValue());
        }
        double size = ApcomplexMath.abs(z).doubleValue();
        if (a.length <= b.length)
        {
            decreasing = Math.max(decreasing, Math.pow(4.0 * size, 1.0 / (b.length + 1 - a.length)));
        }
        for (long k = 0; ; k++)
        {
            if (k > MAX_TERMS)
            {
                throw new ArithmeticException("Hypergeometric series does not converge");
            }
            Apfloat index = new Apfloat(k, workingPrecision, radix);
            Apcomplex numerator = z,
                      denominator = new Apfloat(k + 1, workingPrecision, radix);
            for (Apcomplex x : a)
            {
                numerator = h.multiply(numerator, h.add(x, index));
            }
            for (Apcomplex x : b)
            {
                denominator = h.multiply(denominator, h.add(x, index));
            }
            term = h.divide(h.multiply(term, numerator), denominator);
            if (term.isZero())
            {
                break;
            }
            sum = h.add(sum, term);
            largest = Math.max(largest, term.scale());
            if (k > decreasing && !sum.isZero())
            {
                // The rest is at most a geometric series of the ratio |z| for p = q + 1, and smaller otherwise
                long rest = (a.length == b.length + 1 ? (long) Math.ceil(-Math.log(1.0 - Math.min(size, 0.999999)) / Math.log(radix)) : 1);
                if (term.scale() + rest < sum.scale() - workingPrecision)
                {
                    break;
                }
            }
        }
        loss[0] = (sum.isZero() ? workingPrecision : Math.max(0, largest - sum.scale()));
        return sum;
    }

    private static Apcomplex[] ensurePrecision(Apcomplex[] x, long precision)
    {
        Apcomplex[] result = new Apcomplex[x.length];
        for (int i = 0; i < x.length; i++)
        {
            result[i] = ApfloatHelperShim.ensurePrecision(x[i], precision);
        }
        return result;
    }

    // The expansion in 1 / z for p = q + 1. Upper parameters which differ by an integer are moved
    // apart: the expansion is their limit, and it is taken with as many digits more as the terms
    // cancel then.
    private static Apcomplex inverseArgument(Apcomplex[] a, Apcomplex[] b, Apcomplex z, long precision)
    {
        int radix = z.radix(),
            p = a.length,
            multiplicity = 1;
        boolean[] moved = new boolean[p];
        for (int i = 0; i < p; i++)
        {
            int same = 1;
            for (int j = 0; j < i; j++)
            {
                Apcomplex difference = a[i].subtract(a[j]);
                if (difference.imag().signum() == 0 && difference.real().isInteger())
                {
                    moved[i] = true;
                    same++;
                }
            }
            multiplicity = Math.max(multiplicity, same);
        }
        Apcomplex previous = null;
        for (long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION; extraPrecision <= MAX_EXTRA_PRECISION; extraPrecision *= 2)
        {
            // The distance h of the moved parameters gives an error of the order h, and terms of the order h^(1 - multiplicity)
            long distance = precision + extraPrecision / 2,
                 workingPrecision = precision + extraPrecision + (multiplicity - 1) * distance;
            FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
            Apcomplex[] aw = ensurePrecision(a, workingPrecision),
                        bw = ensurePrecision(b, workingPrecision);
            for (int i = 0, n = 1; i < p; i++)
            {
                if (moved[i])
                {
                    aw[i] = aw[i].add(ApfloatMath.scale(new Apfloat(n++, workingPrecision, radix), -distance));
                }
            }
            Apcomplex zw = ApfloatHelperShim.ensurePrecision(z, workingPrecision),
                      inverse = h.divide(new Apfloat(1, workingPrecision, radix), zw),
                      minusZ = zw.negate(),
                      one = new Apfloat(1, workingPrecision, radix),
                      sum = Apcomplex.ZERO;
            for (int j = 0; j < p; j++)
            {
                // Gamma(a_j) prod Gamma(a_k - a_j) / prod Gamma(b_k - a_j) (-z)^(-a_j)
                Apcomplex factor = h.gamma(aw[j]);
                boolean zero = false;
                Apcomplex[] upper = new Apcomplex[p],
                            lower = new Apcomplex[p - 1];
                upper[0] = aw[j];
                for (int k = 0, n = 0; k < p; k++)
                {
                    if (k != j)
                    {
                        Apcomplex difference = h.subtract(aw[k], aw[j]);
                        factor = h.multiply(factor, h.gamma(difference));
                        lower[n++] = h.subtract(one, difference);
                    }
                }
                for (int k = 0; k < bw.length; k++)
                {
                    Apcomplex difference = h.subtract(bw[k], aw[j]);
                    if (isNonPositiveInteger(difference))
                    {
                        zero = true;
                        break;
                    }
                    factor = h.divide(factor, h.gamma(difference));
                    upper[k + 1] = h.subtract(one, difference);
                }
                if (zero)
                {
                    continue;
                }
                factor = h.multiply(factor, h.pow(minusZ, aw[j].negate()));
                long[] loss = new long[1];
                sum = h.add(sum, h.multiply(factor, series(upper, lower, inverse, workingPrecision, loss)));
            }
            // Gamma(b) / Gamma(a)
            for (Apcomplex x : bw)
            {
                sum = h.multiply(sum, h.gamma(x));
            }
            for (Apcomplex x : aw)
            {
                sum = h.divide(sum, h.gamma(x));
            }
            if (previous != null && GuardDigitsHelper.agree(previous, sum, precision))
            {
                return ApfloatHelperShim.limitPrecision(sum, precision);
            }
            previous = sum;
        }
        throw new LossOfPrecisionException("Complete loss of accurate digits");
    }
}
