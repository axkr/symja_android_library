package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;

/**
 * Inverse cotangent, which apfloat does not have.<p>
 *
 * The function is defined as acot(<i>z</i>) = atan(1 / <i>z</i>), so it is odd, has the values
 * of the interval (-<i>&pi;</i> / 2, <i>&pi;</i> / 2] on the real axis and jumps at zero, where
 * it is <i>&pi;</i> / 2.<p>
 *
 * A zero has infinite precision in apfloat, and <i>&pi;</i> / 2 has no finite expansion. The
 * methods with a <code>precision</code> argument return the value at zero to that precision.
 */

public class ApfloatInverseTrigMath
{
    private ApfloatInverseTrigMath()
    {
    }

    /**
     * Inverse cotangent.
     *
     * @param x The argument.
     *
     * @return acot(<i>x</i>)
     *
     * @throws InfiniteExpansionException If <code>x</code> is zero.
     */

    public static Apfloat acot(Apfloat x)
        throws InfiniteExpansionException, ApfloatRuntimeException
    {
        return acot(x, x.precision());
    }

    /**
     * Inverse cotangent.
     *
     * @param x The argument.
     * @param precision The precision of the result, if <code>x</code> is zero.
     *
     * @return acot(<i>x</i>)
     *
     * @throws InfiniteExpansionException If <code>x</code> is zero and <code>precision</code> is infinite.
     */

    public static Apfloat acot(Apfloat x, long precision)
        throws InfiniteExpansionException, ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            return halfPi(precision, x.radix());
        }
        return ApfloatMath.atan(ApfloatMath.inverseRoot(x, 1));
    }

    /**
     * Inverse cotangent.
     *
     * @param z The argument.
     *
     * @return acot(<i>z</i>)
     *
     * @throws ArithmeticException If <code>z</code> is <i>i</i> or -<i>i</i>.
     * @throws InfiniteExpansionException If <code>z</code> is zero.
     */

    public static Apcomplex acot(Apcomplex z)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return acot(z, z.precision());
    }

    /**
     * Inverse cotangent.
     *
     * @param z The argument.
     * @param precision The precision of the result, if <code>z</code> is zero.
     *
     * @return acot(<i>z</i>)
     *
     * @throws ArithmeticException If <code>z</code> is <i>i</i> or -<i>i</i>.
     * @throws InfiniteExpansionException If <code>z</code> is zero and <code>precision</code> is infinite.
     */

    public static Apcomplex acot(Apcomplex z, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (z.imag().signum() == 0)
        {
            return acot(z.real(), precision);
        }
        int radix = z.radix();
        long targetPrecision = z.precision();
        // 1 - i / z and 1 + i / z cancel next to i and -i
        z = ApfloatHelperShim.ensurePrecision(z, ApfloatHelperShim.extendPrecision(targetPrecision));
        Apint one = ApfloatHelperShim.ONES[radix];
        Apcomplex i = new Apcomplex(ApfloatHelperShim.ZEROS[radix], one),
                  w = i.divide(z);
        // (i / 2) (log(1 - i / z) - log(1 + i / z))
        Apcomplex result = i.divide(new Apint(2, radix)).multiply(ApcomplexMath.log(one.subtract(w)).subtract(ApcomplexMath.log(one.add(w))));
        return ApfloatHelperShim.limitPrecision(result, targetPrecision);
    }

    private static Apfloat halfPi(long precision, int radix)
    {
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate acot of zero to infinite precision");
        }
        return ApfloatMath.pi(precision, radix).divide(new Apint(2, radix));
    }
}
