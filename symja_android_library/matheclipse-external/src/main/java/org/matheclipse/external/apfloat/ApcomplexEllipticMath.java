package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApfloatRuntimeException;

/**
 * Incomplete elliptic integrals and Carlson's symmetric elliptic integrals for
 * {@link Apcomplex} arguments.<p>
 *
 * The methods are written as they would appear in <code>org.apfloat.ApcomplexMath</code>:
 * static, the precision of the result is the smallest precision of the arguments, and an
 * infinite result is reported with an <code>ArithmeticException</code>.
 *
 * @see ApfloatEllipticMath
 */

public class ApcomplexEllipticMath
{
    private ApcomplexEllipticMath()
    {
    }

    /**
     * Incomplete elliptic integral of the first kind.<p>
     *
     * Note that this function uses the definition
     * <i>F(&phi;|m)</i> = &int;<sub>0</sub><sup>&phi;</sup> (1 - <i>m</i> sin<sup>2</sup> <i>t</i>)<sup>-1/2</sup> d<i>t</i>,
     * with the parameter <i>m</i> = <i>k</i><sup>2</sup>, like <code>ApcomplexMath.ellipticK(z)</code>.
     *
     * @implNote
     * This implementation is <i>slow</i>, meaning that it isn't a <i>fast algorithm</i>.
     * It is impractically slow beyond a precision of a few thousand digits.
     *
     * @param φ The amplitude.
     * @param m The parameter.
     *
     * @return <i>F(&phi;|m)</i>
     *
     * @throws InfiniteExpansionException If both arguments have infinite precision and the result is not exact.
     * @throws ArithmeticException If the integral is infinite.
     */

    public static Apcomplex ellipticF(Apcomplex φ, Apcomplex m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return EllipticHelper.ellipticF(φ, m, Math.min(φ.precision(), m.precision()));
    }

    /**
     * Incomplete elliptic integral of the second kind.<p>
     *
     * Note that this function uses the definition
     * <i>E(&phi;|m)</i> = &int;<sub>0</sub><sup>&phi;</sup> (1 - <i>m</i> sin<sup>2</sup> <i>t</i>)<sup>1/2</sup> d<i>t</i>,
     * with the parameter <i>m</i> = <i>k</i><sup>2</sup>, like <code>ApcomplexMath.ellipticE(z)</code>.
     *
     * @implNote
     * This implementation is <i>slow</i>, meaning that it isn't a <i>fast algorithm</i>.
     * It is impractically slow beyond a precision of a few thousand digits.
     *
     * @param φ The amplitude.
     * @param m The parameter.
     *
     * @return <i>E(&phi;|m)</i>
     *
     * @throws InfiniteExpansionException If both arguments have infinite precision and the result is not exact.
     */

    public static Apcomplex ellipticE(Apcomplex φ, Apcomplex m)
        throws ApfloatRuntimeException
    {
        return EllipticHelper.ellipticE(φ, m, Math.min(φ.precision(), m.precision()));
    }

    /**
     * Incomplete elliptic integral of the third kind.<p>
     *
     * Note that this function uses the definition
     * <i>&Pi;(n; &phi;|m)</i> = &int;<sub>0</sub><sup>&phi;</sup> (1 - <i>n</i> sin<sup>2</sup> <i>t</i>)<sup>-1</sup> (1 - <i>m</i> sin<sup>2</sup> <i>t</i>)<sup>-1/2</sup> d<i>t</i>,
     * with the parameter <i>m</i> = <i>k</i><sup>2</sup>. For a real characteristic
     * <i>n</i> &gt; 1 the path of integration passes a pole of the integrand, and the result
     * is the value which Carlson's integral R<sub>J</sub> continues to, which is complex.
     *
     * @implNote
     * This implementation is <i>slow</i>, meaning that it isn't a <i>fast algorithm</i>.
     * It is impractically slow beyond a precision of a few thousand digits.
     *
     * @param n The characteristic.
     * @param φ The amplitude.
     * @param m The parameter.
     *
     * @return <i>&Pi;(n; &phi;|m)</i>
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision and the result is not exact.
     * @throws ArithmeticException If the integral is infinite.
     */

    public static Apcomplex ellipticPi(Apcomplex n, Apcomplex φ, Apcomplex m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return EllipticHelper.ellipticPi(n, φ, m, Math.min(n.precision(), Math.min(φ.precision(), m.precision())));
    }

    /**
     * Complete elliptic integral of the third kind, <i>&Pi;(n|m)</i> = <i>&Pi;(n; &pi;/2|m)</i>.
     *
     * @implNote
     * This implementation is <i>slow</i>, meaning that it isn't a <i>fast algorithm</i>.
     * It is impractically slow beyond a precision of a few thousand digits.
     *
     * @param n The characteristic.
     * @param m The parameter.
     *
     * @return <i>&Pi;(n|m)</i>
     *
     * @throws InfiniteExpansionException If both arguments have infinite precision.
     * @throws ArithmeticException If <code>n</code> or <code>m</code> is one.
     */

    public static Apcomplex ellipticPi(Apcomplex n, Apcomplex m)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return EllipticHelper.ellipticPi(n, m, Math.min(n.precision(), m.precision()));
    }

    /**
     * Carlson's symmetric elliptic integral of the first kind,
     * R<sub>F</sub>(x, y, z) = &frac12; &int;<sub>0</sub><sup>&infin;</sup> ((<i>t</i> + <i>x</i>) (<i>t</i> + <i>y</i>) (<i>t</i> + <i>z</i>))<sup>-1/2</sup> d<i>t</i>.
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     *
     * @return R<sub>F</sub>(x, y, z)
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws ArithmeticException If more than one of the arguments is zero.
     */

    public static Apcomplex carlsonRF(Apcomplex x, Apcomplex y, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return CarlsonHelper.rf(x, y, z, Math.min(x.precision(), Math.min(y.precision(), z.precision())));
    }

    /**
     * Carlson's degenerate elliptic integral R<sub>C</sub>(x, y) = R<sub>F</sub>(x, y, y).
     * For a negative real <code>y</code> the Cauchy principal value is returned.
     *
     * @param x The first argument.
     * @param y The second argument.
     *
     * @return R<sub>C</sub>(x, y)
     *
     * @throws InfiniteExpansionException If both arguments have infinite precision.
     * @throws ArithmeticException If <code>y</code> is zero.
     */

    public static Apcomplex carlsonRC(Apcomplex x, Apcomplex y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return CarlsonHelper.rc(x, y, Math.min(x.precision(), y.precision()));
    }

    /**
     * Carlson's symmetric elliptic integral of the second kind,
     * R<sub>D</sub>(x, y, z) = R<sub>J</sub>(x, y, z, z).
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     *
     * @return R<sub>D</sub>(x, y, z)
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws ArithmeticException If <code>z</code> is zero, or both <code>x</code> and <code>y</code> are zero.
     */

    public static Apcomplex carlsonRD(Apcomplex x, Apcomplex y, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return CarlsonHelper.rd(x, y, z, Math.min(x.precision(), Math.min(y.precision(), z.precision())));
    }

    /**
     * Carlson's symmetric elliptic integral of the third kind,
     * R<sub>J</sub>(x, y, z, p) = &frac32; &int;<sub>0</sub><sup>&infin;</sup> (<i>t</i> + <i>p</i>)<sup>-1</sup> ((<i>t</i> + <i>x</i>) (<i>t</i> + <i>y</i>) (<i>t</i> + <i>z</i>))<sup>-1/2</sup> d<i>t</i>.
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     * @param p The fourth argument.
     *
     * @return R<sub>J</sub>(x, y, z, p)
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws ArithmeticException If <code>p</code> is zero, or more than one of <code>x</code>, <code>y</code> and <code>z</code> is zero.
     */

    public static Apcomplex carlsonRJ(Apcomplex x, Apcomplex y, Apcomplex z, Apcomplex p)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return CarlsonHelper.rj(x, y, z, p, Math.min(Math.min(x.precision(), y.precision()), Math.min(z.precision(), p.precision())));
    }

    /**
     * Carlson's completely symmetric elliptic integral of the second kind, R<sub>G</sub>(x, y, z).
     *
     * @param x The first argument.
     * @param y The second argument.
     * @param z The third argument.
     *
     * @return R<sub>G</sub>(x, y, z)
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     */

    public static Apcomplex carlsonRG(Apcomplex x, Apcomplex y, Apcomplex z)
        throws ApfloatRuntimeException
    {
        return CarlsonHelper.rg(x, y, z, Math.min(x.precision(), Math.min(y.precision(), z.precision())));
    }
}
