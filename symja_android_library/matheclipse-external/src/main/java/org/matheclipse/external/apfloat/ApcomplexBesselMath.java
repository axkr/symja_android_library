package org.matheclipse.external.apfloat;

import java.util.function.BinaryOperator;

import org.apfloat.Apcomplex;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatInterruptedException;
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

    // Largest number of digits which are added for an order that is large against the argument: the functions of the
    // first kind take seconds with them, and those of the second kind ten times as long
    private static final double MAX_CANCELLED_DIGITS = 1000.0,
                                MAX_CANCELLED_DIGITS_SECOND_KIND = 300.0;

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
        Apcomplex[] expansion = largeArgument(ν, z);
        if (expansion != null)
        {
            return expansion[0];
        }
        return evaluate(ApcomplexMath::besselJ, ν, z, MAX_CANCELLED_DIGITS);
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
        return evaluate(ApcomplexMath::besselI, ν, z, MAX_CANCELLED_DIGITS);
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
        Apcomplex[] expansion = largeArgument(ν, z);
        if (expansion != null)
        {
            return expansion[1];
        }
        Apcomplex recurrence = upwardRecurrence(ν, z, false);
        if (recurrence != null)
        {
            return recurrence;
        }
        return evaluate(ApcomplexMath::besselY, ν, z, MAX_CANCELLED_DIGITS_SECOND_KIND);
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
        Apcomplex recurrence = upwardRecurrence(ν, z, true);
        if (recurrence != null)
        {
            return recurrence;
        }
        return evaluate(ApcomplexMath::besselK, ν, z, MAX_CANCELLED_DIGITS_SECOND_KIND);
    }

    // Order from which the functions of the second kind come from their recurrence in the order
    private static final long RECURRENCE_ORDER = 20;

    // Largest order for the recurrence, which takes one step per unit of the order
    private static final long MAX_RECURRENCE_ORDER = 100000;

    /*
     * Y or K of a large real order by the recurrence Y(ν+1) = 2ν/z Y(ν) - Y(ν-1), and
     * K(ν+1) = K(ν-1) + 2ν/z K(ν), from the orders ν0-1 and ν0 in [0, 2). Both functions are the
     * growing solution of their recurrence, so that it doesn't lose digits, while the series of
     * the library cancels about ν^2/z digits: K(1500, 1000) has more than the library adds.
     * Returns null where it doesn't apply.
     */
    private static Apcomplex upwardRecurrence(Apcomplex ν, Apcomplex z, boolean modified)
    {
        if (ν.imag().signum() != 0 || z.real().signum() <= 0)
        {
            return null;
        }
        Apfloat order = ν.real();
        if (modified && order.signum() < 0)
        {
            // K(-ν) = K(ν)
            order = order.negate();
        }
        if (order.compareTo(new Apfloat(RECURRENCE_ORDER)) < 0 || order.compareTo(new Apfloat(MAX_RECURRENCE_ORDER)) > 0)
        {
            return null;
        }
        final long steps = order.truncate().longValue() - 1;
        long precision = precision(ν, z),
             extraPrecision = ApfloatHelperShim.EXTRA_PRECISION + 10 + (long) Math.log10(steps);
        final Apfloat startOrder = order.subtract(new Apfloat(steps));
        BinaryOperator<Apcomplex> recurrence = (n, x) ->
        {
            long workingPrecision = x.precision();
            Apfloat one = new Apfloat(1, workingPrecision),
                    two = new Apfloat(2, workingPrecision);
            Apcomplex μ = ApfloatHelperShim.ensurePrecision(startOrder, workingPrecision),
                      previous = startValue(μ.subtract(one), x, modified),
                      current = startValue(μ, x, modified);
            for (long k = 0; k < steps; k++)
            {
                if ((k & 63) == 0 && Thread.currentThread().isInterrupted())
                {
                    throw new ApfloatInterruptedException("Interrupted");
                }
                Apcomplex ratio = two.multiply(μ).divide(x),
                          next = modified ? previous.add(ratio.multiply(current)) : ratio.multiply(current).subtract(previous);
                previous = current;
                current = next;
                μ = μ.add(one);
            }
            return current;
        };
        return GuardDigitsHelper.evaluate(recurrence, ν, z, precision, extraPrecision, 16 * extraPrecision);
    }

    // Y or K of an order below 2, for which Hankel's expansion is tried first: the series of the
    // library takes seconds for Y(1, 5000), the expansion milliseconds
    private static Apcomplex startValue(Apcomplex μ, Apcomplex z, boolean modified)
    {
        if (!modified)
        {
            try
            {
                return ApcomplexAsymptoticMath.besselJY(μ, z)[1];
            }
            catch (ArithmeticException ae)
            {
                // the argument isn't large enough for the precision
            }
            return besselY(μ, z);
        }
        return besselK(μ, z);
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
                  bessel = (secondKind ? besselY(order, z) : besselJ(order, z)),
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

    // J and Y of an argument from this number of digits in front of the decimal point on by the expansion for a large argument
    private static final long LARGE_ARGUMENT_SCALE = 16;

    // The two functions by Hankel's expansion, or null where it doesn't apply or doesn't reach the precision
    private static Apcomplex[] largeArgument(Apcomplex ν, Apcomplex z)
    {
        if (z.scale() < LARGE_ARGUMENT_SCALE || z.real().signum() <= 0)
        {
            return null;
        }
        try
        {
            return ApcomplexAsymptoticMath.besselJY(ν, z);
        }
        catch (ArithmeticException ae)
        {
            return null;
        }
    }

    private static Apcomplex evaluate(BinaryOperator<Apcomplex> function, Apcomplex ν, Apcomplex z, double maxCancelledDigits)
    {
        long precision = precision(ν, z),
             extraPrecision = ApfloatHelperShim.EXTRA_PRECISION;
        if (z.scale() > 0)
        {
            // The library takes twice the digits in front of the decimal point from the phase of a large argument
            extraPrecision += 2 * z.scale();
            // and its expansion for a large argument cancels about nu^2 / (2 z) nepers twice, where the order is large as well
            double order = ApcomplexMath.abs(ν).doubleValue(),
                   argument = ApcomplexMath.abs(z).doubleValue(),
                   cancelled = order * order / (argument * Math.log(z.radix()));
            if (argument > 10.0 && cancelled > 1.0)
            {
                if (!(cancelled <= maxCancelledDigits))
                {
                    throw new LossOfPrecisionException("Complete loss of accurate digits", "lossOfPrecision");
                }
                extraPrecision += (long) cancelled;
            }
        }
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
