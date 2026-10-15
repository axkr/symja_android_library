package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;

/**
 * Parabolic cylinder function and Scorer functions.<p>
 *
 * Both are entire functions which are computed from series whose terms cancel for a large
 * argument: the working precision is raised until two results agree.
 */

public class ApcomplexParabolicCylinderMath
{
    // The largest number of extra digits which a cancelling series is summed with
    private static final long MAX_EXTRA_PRECISION = 2000;

    private ApcomplexParabolicCylinderMath()
    {
    }

    /**
     * Parabolic cylinder function <code>D<sub>&nu;</sub>(z)</code>.
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return <code>D<sub>&nu;</sub>(z)</code>
     *
     * @exception ArithmeticException If the terms cancel beyond the largest working precision.
     *
     * @see <a href="https://dlmf.nist.gov/12.7#E12">DLMF 12.7.12</a>, the even solution
     * @see <a href="https://dlmf.nist.gov/12.7#E13">DLMF 12.7.13</a>, the odd solution
     */

    public static Apcomplex parabolicCylinderD(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(ν.precision(), z.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate parabolic cylinder function to infinite precision", "parabolicCylinder.infinitePrecision");
        }
        // The two series are of the size exp(|z|^2 / 4), the function is as small as exp(-Re(z^2) / 4)
        double x = z.real().doubleValue(),
               y = z.imag().doubleValue();
        long loss = (long) Math.ceil((x * x + y * y) / 2.0 / Math.log(z.radix()));
        Apcomplex previous = null;
        for (long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION + loss; extraPrecision <= MAX_EXTRA_PRECISION + loss; extraPrecision += ApfloatHelperShim.EXTRA_PRECISION + extraPrecision / 2)
        {
            Apcomplex result = parabolicCylinderD(ν, z, precision + extraPrecision);
            if (previous != null && GuardDigitsHelper.agree(previous, result, precision))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
            if (loss > MAX_EXTRA_PRECISION)
            {
                break;
            }
        }
        throw new ArithmeticException("Parabolic cylinder function does not reach the precision");
    }

    private static Apcomplex parabolicCylinderD(Apcomplex ν, Apcomplex z, long workingPrecision)
    {
        int radix = z.radix();
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                two = new Apfloat(2, workingPrecision, radix),
                half = new Apfloat("0.5", workingPrecision, radix);
        ν = ApfloatHelperShim.ensurePrecision(ν, workingPrecision);
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        Apcomplex z2 = h.multiply(z, z),
                  argument = h.multiply(z2, half),
                  a = h.multiply(ν, half).negate(),               // -ν/2
                  b = h.multiply(h.subtract(one, ν), half),        // (1-ν)/2
                  first = ApcomplexHypercombMath.reciprocalGamma(h, b),
                  second = ApcomplexHypercombMath.reciprocalGamma(h, a),
                  sum = Apcomplex.ZERO;
        if (!first.isZero())
        {
            sum = h.multiply(first, series(h, a, half, argument, one));
        }
        if (!second.isZero() && !z.isZero())
        {
            Apcomplex term = h.multiply(h.multiply(h.multiply(h.sqrt(two), z), second), series(h, b, h.add(one, half), argument, one));
            sum = (sum.isZero() ? term.negate() : h.subtract(sum, term));
        }
        if (sum.isZero())
        {
            return Apcomplex.ZERO;
        }
        // 2^(ν/2) exp(-z^2/4) sqrt(pi)
        Apcomplex factor = h.multiply(h.multiply(h.exp(h.multiply(h.multiply(ν, half), h.log(two))), h.exp(h.multiply(z2, new Apfloat("-0.25", workingPrecision, radix)))), h.sqrt(h.pi()));
        return h.multiply(factor, sum);
    }

    // The confluent hypergeometric series 1F1(a; b; x)
    private static Apcomplex series(FixedPrecisionApfloatHelper h, Apcomplex a, Apfloat b, Apcomplex x, Apfloat one)
    {
        if (x.isZero())
        {
            return one;
        }
        long workingPrecision = h.precision();
        Apcomplex sum = one,
                  term = one;
        for (long k = 0; k < 10000000; k++)
        {
            Apfloat index = new Apfloat(k, workingPrecision, x.radix());
            Apcomplex numerator = (a.isZero() ? index : h.add(a, index));
            if (numerator.isZero())
            {
                break;
            }
            term = h.divide(h.multiply(h.multiply(term, numerator), x), h.multiply(h.add(b, index), new Apfloat(k + 1, workingPrecision, x.radix())));
            sum = h.add(sum, term);
            // behind the largest term the terms decrease
            if (term.isZero() || (decreasing(k, a, x) && term.scale() < sum.scale() - workingPrecision - 2))
            {
                break;
            }
        }
        return sum;
    }

    // Whether the terms of the series decrease from the index k on
    private static boolean decreasing(long k, Apcomplex a, Apcomplex x)
    {
        double size = Math.hypot(x.real().doubleValue(), x.imag().doubleValue()),
               shift = Math.hypot(a.real().doubleValue(), a.imag().doubleValue());
        return k > 2.0 * (size + shift);
    }

    /**
     * Scorer function <code>Gi(z)</code>.
     *
     * @param z The argument.
     *
     * @return <code>Gi(z)</code>
     *
     * @exception ArithmeticException If the terms cancel beyond the largest working precision.
     *
     * @see <a href="https://dlmf.nist.gov/9.12">DLMF 9.12</a>
     */

    public static Apcomplex scorerGi(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return scorer(z, true);
    }

    /**
     * Scorer function <code>Hi(z)</code>.
     *
     * @param z The argument.
     *
     * @return <code>Hi(z)</code>
     *
     * @exception ArithmeticException If the terms cancel beyond the largest working precision.
     *
     * @see <a href="https://dlmf.nist.gov/9.12#E17">DLMF 9.12.17</a>
     */

    public static Apcomplex scorerHi(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return scorer(z, false);
    }

    private static Apcomplex scorer(Apcomplex z, boolean gi)
    {
        long precision = z.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Scorer function to infinite precision", "scorer.infinitePrecision");
        }
        // The terms are of the size exp(2/3 |z|^(3/2))
        double size = Math.hypot(z.real().doubleValue(), z.imag().doubleValue());
        long loss = (long) Math.ceil(2.0 / 3.0 * Math.pow(size, 1.5) / Math.log(z.radix()));
        if (loss > MAX_EXTRA_PRECISION)
        {
            throw new ArithmeticException("Scorer function of a too large argument");
        }
        Apcomplex previous = null;
        for (long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION + loss; extraPrecision <= MAX_EXTRA_PRECISION + loss; extraPrecision += ApfloatHelperShim.EXTRA_PRECISION + extraPrecision / 2)
        {
            Apcomplex result = scorer(z, gi, precision + extraPrecision);
            if (previous != null && GuardDigitsHelper.agree(previous, result, precision))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
        }
        throw new ArithmeticException("Scorer function does not reach the precision");
    }

    // 3^(-2/3) / pi times the sum of c(k) gamma((k+1)/3) (3^(1/3) z)^k / k!, with c(k) = cos((2k-1) pi/3) for Gi and 1 for Hi
    private static Apcomplex scorer(Apcomplex z, boolean gi, long workingPrecision)
    {
        int radix = z.radix();
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                three = new Apfloat(3, workingPrecision, radix),
                third = h.divide(one, three),
                half = new Apfloat("0.5", workingPrecision, radix),
                root = h.exp(h.multiply(third, h.log(three)));         // 3^(1/3)
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        Apcomplex w = h.multiply(root, z),
                  z3 = h.multiply(z, h.multiply(z, z));
        // the terms of the three residue classes of k: each is the one three before times z^3 / ((k+2) (k+3))
        Apcomplex[] terms = { h.gamma(third), h.multiply(h.gamma(h.multiply(new Apfloat(2, workingPrecision, radix), third)), w), h.multiply(h.multiply(w, w), half) };
        Apfloat[] weights = (gi ? new Apfloat[] { half, half, one.negate() } : new Apfloat[] { one, one, one });
        Apcomplex sum = Apcomplex.ZERO;
        for (long k = 0; k < 30000000; k += 3)
        {
            boolean done = true;
            for (int j = 0; j < 3; j++)
            {
                Apcomplex term = terms[j];
                if (!term.isZero())
                {
                    sum = h.add(sum, h.multiply(weights[j], term));
                    if (term.scale() >= sum.scale() - workingPrecision - 2)
                    {
                        done = false;
                    }
                    terms[j] = h.divide(h.multiply(term, z3), new Apfloat((k + j + 2) * (k + j + 3), workingPrecision, radix));
                }
            }
            if (done && k > 3)
            {
                break;
            }
        }
        // 3^(-2/3) / pi
        return h.divide(sum, h.multiply(h.multiply(root, root), h.pi()));
    }
}
