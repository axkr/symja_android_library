package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatRuntimeException;

/**
 * Incomplete elliptic integrals and Carlson's symmetric elliptic integrals for
 * {@link Apfloat} arguments.<p>
 *
 * The methods are written as they would appear in <code>org.apfloat.ApfloatMath</code>.
 * Like <code>ApfloatMath.ellipticK(x)</code> they throw an <code>ArithmeticException</code>
 * where the result is not real; the complex result is available from
 * {@link ApcomplexEllipticMath}.
 *
 * @see ApcomplexEllipticMath
 */

public class ApfloatEllipticMath
{
    private ApfloatEllipticMath()
    {
    }

    /**
     * Incomplete elliptic integral of the first kind.
     *
     * @param φ The amplitude.
     * @param m The parameter.
     *
     * @return <i>F(&phi;|m)</i>
     *
     * @throws InfiniteExpansionException If both arguments have infinite precision and the result is not exact.
     * @throws ArithmeticException If the integral is infinite or the result would be complex.
     *
     * @see ApcomplexEllipticMath#ellipticF(Apcomplex,Apcomplex)
     */

    public static Apfloat ellipticF(Apfloat φ, Apfloat m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return real(ApcomplexEllipticMath.ellipticF(φ, m));
    }

    /**
     * Incomplete elliptic integral of the second kind.
     *
     * @param φ The amplitude.
     * @param m The parameter.
     *
     * @return <i>E(&phi;|m)</i>
     *
     * @throws InfiniteExpansionException If both arguments have infinite precision and the result is not exact.
     * @throws ArithmeticException If the result would be complex.
     *
     * @see ApcomplexEllipticMath#ellipticE(Apcomplex,Apcomplex)
     */

    public static Apfloat ellipticE(Apfloat φ, Apfloat m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return real(ApcomplexEllipticMath.ellipticE(φ, m));
    }

    /**
     * Incomplete elliptic integral of the third kind.
     *
     * @param n The characteristic.
     * @param φ The amplitude.
     * @param m The parameter.
     *
     * @return <i>&Pi;(n; &phi;|m)</i>
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision and the result is not exact.
     * @throws ArithmeticException If the integral is infinite or the result would be complex.
     *
     * @see ApcomplexEllipticMath#ellipticPi(Apcomplex,Apcomplex,Apcomplex)
     */

    public static Apfloat ellipticPi(Apfloat n, Apfloat φ, Apfloat m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return real(ApcomplexEllipticMath.ellipticPi(n, φ, m));
    }

    /**
     * Complete elliptic integral of the third kind.
     *
     * @param n The characteristic.
     * @param m The parameter.
     *
     * @return <i>&Pi;(n|m)</i>
     *
     * @throws InfiniteExpansionException If both arguments have infinite precision.
     * @throws ArithmeticException If the integral is infinite or the result would be complex.
     *
     * @see ApcomplexEllipticMath#ellipticPi(Apcomplex,Apcomplex)
     */

    public static Apfloat ellipticPi(Apfloat n, Apfloat m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return real(ApcomplexEllipticMath.ellipticPi(n, m));
    }

    /**
     * Carlson's symmetric elliptic integral of the first kind.
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     *
     * @return R<sub>F</sub>(x, y, z)
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws ArithmeticException If more than one of the arguments is zero or the result would be complex.
     *
     * @see ApcomplexEllipticMath#carlsonRF(Apcomplex,Apcomplex,Apcomplex)
     */

    public static Apfloat carlsonRF(Apfloat x, Apfloat y, Apfloat z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return real(ApcomplexEllipticMath.carlsonRF(x, y, z));
    }

    /**
     * Carlson's degenerate elliptic integral.
     *
     * @param x The first argument.
     * @param y The second argument.
     *
     * @return R<sub>C</sub>(x, y)
     *
     * @throws InfiniteExpansionException If both arguments have infinite precision.
     * @throws ArithmeticException If <code>y</code> is zero or the result would be complex.
     *
     * @see ApcomplexEllipticMath#carlsonRC(Apcomplex,Apcomplex)
     */

    public static Apfloat carlsonRC(Apfloat x, Apfloat y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return real(ApcomplexEllipticMath.carlsonRC(x, y));
    }

    /**
     * Carlson's symmetric elliptic integral of the second kind.
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     *
     * @return R<sub>D</sub>(x, y, z)
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws ArithmeticException If the integral is infinite or the result would be complex.
     *
     * @see ApcomplexEllipticMath#carlsonRD(Apcomplex,Apcomplex,Apcomplex)
     */

    public static Apfloat carlsonRD(Apfloat x, Apfloat y, Apfloat z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return real(ApcomplexEllipticMath.carlsonRD(x, y, z));
    }

    /**
     * Carlson's symmetric elliptic integral of the third kind.
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     * @param p The fourth argument.
     *
     * @return R<sub>J</sub>(x, y, z, p)
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws ArithmeticException If the integral is infinite or the result would be complex.
     *
     * @see ApcomplexEllipticMath#carlsonRJ(Apcomplex,Apcomplex,Apcomplex,Apcomplex)
     */

    public static Apfloat carlsonRJ(Apfloat x, Apfloat y, Apfloat z, Apfloat p)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return real(ApcomplexEllipticMath.carlsonRJ(x, y, z, p));
    }

    /**
     * Carlson's completely symmetric elliptic integral of the second kind.
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     *
     * @return R<sub>G</sub>(x, y, z)
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws ArithmeticException If the result would be complex.
     *
     * @see ApcomplexEllipticMath#carlsonRG(Apcomplex,Apcomplex,Apcomplex)
     */

    public static Apfloat carlsonRG(Apfloat x, Apfloat y, Apfloat z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return real(ApcomplexEllipticMath.carlsonRG(x, y, z));
    }

    private static Apfloat real(Apcomplex z)
        throws ArithmeticException
    {
        if (z.imag().signum() != 0)
        {
            throw new ApfloatArithmeticException("Result would be complex", "complex");
        }
        return z.real();
    }
}
