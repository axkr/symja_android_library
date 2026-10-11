package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;

/**
 * Struve functions for {@link Apcomplex} arguments, to the precision of the arguments.<p>
 *
 * <code>ApcomplexMath.struveH()</code> and <code>ApcomplexMath.struveL()</code> sum a
 * hypergeometric series in <i>z</i><sup>2</sup>, whose terms grow like <i>e</i><sup>|z|</sup>
 * before they cancel. The methods of this class give them the extra digits which that takes,
 * and use the asymptotic expansions of {@link ApcomplexAsymptoticMath} where those reach the
 * precision.
 */

public class ApcomplexStruveMath
{
    private ApcomplexStruveMath()
    {
    }

    // The series is not summed for an argument above this: the extra digits it needs grow with the argument
    private static final double MAX_SERIES_ARGUMENT = 3000.0;

    /**
     * Struve function <b>H</b><sub>&nu;</sub>(<i>z</i>).
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return <b>H</b><sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If the argument is too large for the series and the asymptotic expansion doesn't reach the precision.
     *
     * @see <a href="https://dlmf.nist.gov/11.2#E1">DLMF 11.2.1</a>
     */

    public static Apcomplex struveH(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        try
        {
            return ApcomplexAsymptoticMath.struveH(ν, z);
        }
        catch (ArithmeticException ae)
        {
            // The expansion doesn't apply
        }
        return series(ν, z, false);
    }

    /**
     * Modified Struve function <b>L</b><sub>&nu;</sub>(<i>z</i>).
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return <b>L</b><sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If the argument is too large for the series and the asymptotic expansion doesn't reach the precision.
     *
     * @see <a href="https://dlmf.nist.gov/11.2#E2">DLMF 11.2.2</a>
     */

    public static Apcomplex struveL(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        try
        {
            return ApcomplexAsymptoticMath.struveL(ν, z);
        }
        catch (ArithmeticException ae)
        {
            // The expansion doesn't apply
        }
        return series(ν, z, true);
    }

    private static Apcomplex series(Apcomplex ν, Apcomplex z, boolean modified)
    {
        long precision = Math.min(ν.precision(), z.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new org.apfloat.InfiniteExpansionException("Cannot calculate Struve function to infinite precision", "struve.infinitePrecision");
        }
        double size = ApcomplexMath.abs(z).doubleValue();
        if (!(size <= MAX_SERIES_ARGUMENT))
        {
            throw new ArithmeticException("Struve function series for a too large argument");
        }
        // The terms grow to e^|z| before they cancel
        long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION + (long) (size / Math.log(z.radix()));
        return GuardDigitsHelper.evaluate((order, argument) -> modified ? ApcomplexMath.struveL(order, argument) : ApcomplexMath.struveH(order, argument),
                                          ν, z, precision, extraPrecision, 8 * extraPrecision);
    }
}
