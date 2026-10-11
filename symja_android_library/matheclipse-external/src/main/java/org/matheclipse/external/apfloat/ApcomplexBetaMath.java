package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
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
}
