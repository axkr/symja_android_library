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
