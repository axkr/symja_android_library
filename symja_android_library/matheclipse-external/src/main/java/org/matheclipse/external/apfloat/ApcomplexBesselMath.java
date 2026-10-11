package org.matheclipse.external.apfloat;

import java.util.function.BinaryOperator;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.InfiniteExpansionException;

/**
 * Bessel functions of the second kind for {@link Apcomplex} arguments, to the precision of the
 * arguments.<p>
 *
 * <code>ApcomplexMath.besselY()</code> and <code>ApcomplexMath.besselK()</code> are a difference
 * of two Bessel functions of the first kind, divided by sin(&nu;&pi;). The difference cancels
 * for a small argument - <i>Y</i><sub>-5/2</sub>(1/100) comes out with 27 of 40 digits - and
 * at an integer order with a large argument the library divides by zero. The methods of this
 * class evaluate the functions with as many extra digits as it takes for the result to come
 * out the same twice, and with as many digits as the size of the argument where the library
 * fails.
 */

public class ApcomplexBesselMath
{
    // Largest size of an argument, for which that many digits are added
    private static final long MAX_ARGUMENT = 10000;

    private ApcomplexBesselMath()
    {
    }

    /**
     * Bessel function of the first kind <i>J</i><sub>&nu;</sub>(<i>z</i>).<p>
     *
     * The series of the library cancels where the order and the argument are large and of
     * the same size: at 30 digits <i>J</i><sub>201/2</sub>(100) comes out with 16 of them.
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return <i>J</i><sub>&nu;</sub>(<i>z</i>)
     *
     * @see <a href="https://dlmf.nist.gov/10.2#E2">DLMF 10.2.2</a>
     */

    public static Apcomplex besselJ(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return evaluate(ApcomplexMath::besselJ, ν, z);
    }

    /**
     * Modified Bessel function of the first kind <i>I</i><sub>&nu;</sub>(<i>z</i>).<p>
     *
     * At 30 digits the library gives -4 10<sup>-25</sup> for <i>I</i><sub>201/2</sub>(100),
     * which is 3 10<sup>21</sup>.
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return <i>I</i><sub>&nu;</sub>(<i>z</i>)
     *
     * @see <a href="https://dlmf.nist.gov/10.25#E2">DLMF 10.25.2</a>
     */

    public static Apcomplex besselI(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return evaluate(ApcomplexMath::besselI, ν, z);
    }

    /**
     * Bessel function of the second kind <i>Y</i><sub>&nu;</sub>(<i>z</i>).
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return <i>Y</i><sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If <i>z</i> is zero.
     *
     * @see <a href="https://dlmf.nist.gov/10.2#E3">DLMF 10.2.3</a>
     */

    public static Apcomplex besselY(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return evaluate(ApcomplexMath::besselY, ν, z);
    }

    /**
     * Modified Bessel function of the second kind <i>K</i><sub>&nu;</sub>(<i>z</i>).
     *
     * @param ν The order.
     * @param z The argument.
     *
     * @return <i>K</i><sub>&nu;</sub>(<i>z</i>)
     *
     * @throws ArithmeticException If <i>z</i> is zero.
     *
     * @see <a href="https://dlmf.nist.gov/10.27#E4">DLMF 10.27.4</a>
     */

    public static Apcomplex besselK(Apcomplex ν, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return evaluate(ApcomplexMath::besselK, ν, z);
    }

    /**
     * Spherical Bessel function of the first kind
     * <i>j</i><sub><i>n</i></sub>(<i>z</i>) = &radic;(&pi; / 2) <i>J</i><sub><i>n</i>+1/2</sub>(<i>z</i>) / &radic;<i>z</i>.
     *
     * @param n The order.
     * @param z The argument.
     *
     * @return <i>j</i><sub><i>n</i></sub>(<i>z</i>)
     *
     * @throws ArithmeticException If <i>z</i> is zero.
     *
     * @see <a href="https://dlmf.nist.gov/10.47#E3">DLMF 10.47.3</a>
     */

    public static Apcomplex sphericalBesselJ(Apcomplex n, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return spherical(n, z, false);
    }

    /**
     * Spherical Bessel function of the second kind
     * <i>y</i><sub><i>n</i></sub>(<i>z</i>) = &radic;(&pi; / 2) <i>Y</i><sub><i>n</i>+1/2</sub>(<i>z</i>) / &radic;<i>z</i>.
     *
     * @param n The order.
     * @param z The argument.
     *
     * @return <i>y</i><sub><i>n</i></sub>(<i>z</i>)
     *
     * @throws ArithmeticException If <i>z</i> is zero.
     *
     * @see <a href="https://dlmf.nist.gov/10.47#E4">DLMF 10.47.4</a>
     */

    public static Apcomplex sphericalBesselY(Apcomplex n, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return spherical(n, z, true);
    }

    private static Apcomplex spherical(Apcomplex n, Apcomplex z, boolean secondKind)
    {
        long precision = precision(n, z),
             workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        int radix = z.radix();
        n = ApfloatHelperShim.ensurePrecision(n, workingPrecision);
        z = ApfloatHelperShim.ensurePrecision(z, workingPrecision);
        Apfloat two = new Apfloat(2, workingPrecision, radix);
        Apcomplex order = n.add(new Apfloat(1, workingPrecision, radix).divide(two)),
                  bessel = (secondKind ? evaluate(ApcomplexMath::besselY, order, z) : evaluate(ApcomplexMath::besselJ, order, z)),
                  // Sqrt(Pi/2) / Sqrt(z), which is not Sqrt(Pi / (2 z)) on the negative real axis
                  factor = ApfloatMath.sqrt(ApfloatMath.pi(workingPrecision, radix).divide(two)).divide(ApcomplexMath.sqrt(z));
        return ApfloatHelperShim.limitPrecision(factor.multiply(bessel), precision);
    }

    /**
     * Refines an approximation of a zero of the Bessel function <i>J</i><sub>&nu;</sub>(<i>x</i>),
     * or of <i>Y</i><sub>&nu;</sub>(<i>x</i>), with Newton's method.
     *
     * @param ν The order.
     * @param approximation The approximation of the zero, which must be good enough for Newton's method to stay at it: a machine precision value is.
     * @param secondKind Whether the zero is one of <i>Y</i>, and not of <i>J</i>.
     * @param precision The precision of the result.
     *
     * @return The zero.
     *
     * @throws ArithmeticException If the iteration does not converge.
     *
     * @see <a href="https://dlmf.nist.gov/10.21">DLMF 10.21</a>
     */

    public static Apfloat besselZero(Apfloat ν, Apfloat approximation, boolean secondKind, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        int radix = approximation.radix();
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        Apfloat order = ApfloatHelperShim.ensurePrecision(ν, workingPrecision),
                one = new Apfloat(1, workingPrecision, radix),
                x = approximation.precision(workingPrecision),
                tolerance = ApfloatMath.scale(one, -precision - 2);
        for (int i = 0; i < 100; i++)
        {
            // The derivative is the function of the order below, less nu / x times the function
            Apcomplex f = (secondKind ? besselY(order, x) : ApcomplexMath.besselJ(order, x)),
                      below = (secondKind ? besselY(order.subtract(one), x) : ApcomplexMath.besselJ(order.subtract(one), x));
            Apfloat value = f.real(),
                    derivative = below.real().subtract(order.multiply(value).divide(x));
            if (derivative.signum() == 0)
            {
                break;
            }
            Apfloat step = value.divide(derivative);
            x = x.subtract(step).precision(workingPrecision);
            if (step.signum() == 0 || ApfloatMath.abs(step).compareTo(ApfloatMath.abs(x).multiply(tolerance)) <= 0)
            {
                return x.precision(precision);
            }
        }
        throw new ArithmeticException("Newton's method does not converge to the zero of the Bessel function");
    }

    private static Apcomplex evaluate(BinaryOperator<Apcomplex> function, Apcomplex ν, Apcomplex z)
    {
        long precision = precision(ν, z),
             extraPrecision = ApfloatHelperShim.EXTRA_PRECISION;
        try
        {
            return GuardDigitsHelper.evaluate(function, ν, z, precision, extraPrecision, 16 * extraPrecision);
        }
        catch (ArithmeticException ae)
        {
            if (z.real().signum() == 0 && z.imag().signum() == 0)
            {
                throw ae;
            }
            // The library divides by zero at an integer order, when the series of the argument
            // has more increasing terms than there are digits
            extraPrecision += Math.min(ApcomplexMath.abs(z).longValue(), MAX_ARGUMENT);
            return GuardDigitsHelper.evaluate(function, ν, z, precision, extraPrecision, 4 * extraPrecision);
        }
    }

    private static long precision(Apcomplex ν, Apcomplex z)
    {
        long precision = Math.min(ν.precision(), z.precision());
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Bessel function to infinite precision", "bessel.infinitePrecision");
        }
        return precision;
    }
}
