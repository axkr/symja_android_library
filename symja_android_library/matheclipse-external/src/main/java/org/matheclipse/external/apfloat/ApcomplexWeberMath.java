package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.InfiniteExpansionException;

/**
 * Weber function for {@link Apcomplex} arguments, to the precision of the arguments.<p>
 *
 * <code>ApcomplexMath.weberE()</code> is the difference of two hypergeometric series in
 * -<i>z</i><sup>2</sup>/4, which grow like <i>e</i><sup>|<i>z</i>|</sup> while the function
 * decays: the result is short of a digit or two for a small argument and of five and more
 * for an argument in the hundreds. The method of this class evaluates the function with as
 * many extra digits as it takes for the result to come out the same twice.
 */

public class ApcomplexWeberMath
{
    private ApcomplexWeberMath()
    {
    }

    /**
     * Weber function <b>E</b><sub>&nu;</sub>(<i>z</i>).
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return <b>E</b><sub>&nu;</sub>(<i>z</i>)
     *
     * @see <a href="https://dlmf.nist.gov/11.10#E2">DLMF 11.10.2</a>
     * @see <a href="https://dlmf.nist.gov/11.10#iii">DLMF 11.10(iii)</a>
     */

    public static Apcomplex weberE(Apcomplex ν, Apcomplex z)
        throws ApfloatRuntimeException
    {
        long precision = Math.min(ν.precision(), z.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Weber function to infinite precision", "weber.infinitePrecision");
        }
        return GuardDigitsHelper.evaluate(ApcomplexMath::weberE, ν, z, precision,
                                          ApfloatHelperShim.EXTRA_PRECISION, 16 * ApfloatHelperShim.EXTRA_PRECISION);
    }
}
