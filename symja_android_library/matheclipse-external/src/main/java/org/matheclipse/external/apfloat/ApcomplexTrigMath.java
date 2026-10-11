package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;

/**
 * The reciprocal trigonometric and hyperbolic functions and their inverses for
 * {@link Apcomplex} arguments, which apfloat does not have: cot, sec, csc, coth, sech, csch,
 * acot, asec, acsc, acoth, asech and acsch.<p>
 *
 * An inverse function is the principal value of the inverse of the reciprocal function at
 * 1 / <i>z</i>: acot(<i>z</i>) = atan(1 / <i>z</i>), asec(<i>z</i>) = acos(1 / <i>z</i>),
 * acsc(<i>z</i>) = asin(1 / <i>z</i>), acoth(<i>z</i>) = atanh(1 / <i>z</i>),
 * asech(<i>z</i>) = acosh(1 / <i>z</i>) and acsch(<i>z</i>) = asinh(1 / <i>z</i>). On a
 * branch cut the value is the one which apfloat gives the inner function there.<p>
 *
 * An exact number has infinite precision in apfloat. The methods with a
 * <code>precision</code> argument return the result of such an argument to that precision;
 * the ones without it throw an <code>InfiniteExpansionException</code> where the result has
 * no finite expansion.
 *
 * @see ApfloatTrigMath
 * @see <a href="https://dlmf.nist.gov/4.14">DLMF 4.14</a>
 * @see <a href="https://dlmf.nist.gov/4.23">DLMF 4.23</a>
 * @see <a href="https://dlmf.nist.gov/4.28">DLMF 4.28</a>
 * @see <a href="https://dlmf.nist.gov/4.37">DLMF 4.37</a>
 */

public class ApcomplexTrigMath
{
    private ApcomplexTrigMath()
    {
    }

    /**
     * Cotangent, cos(<i>z</i>) / sin(<i>z</i>).
     *
     * @param z The argument.
     *
     * @return cot(<i>z</i>)
     *
     * @throws ArithmeticException If <code>z</code> is a pole of the function.
     */

    public static Apcomplex cot(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = z.precision();
        if (precision == Apfloat.INFINITE)
        {
            return ApcomplexMath.cos(z).divide(ApcomplexMath.sin(z));
        }
        z = ApfloatHelperShim.ensurePrecision(z, ApfloatHelperShim.extendPrecision(precision));
        return ApfloatHelperShim.limitPrecision(ApcomplexMath.cos(z).divide(ApcomplexMath.sin(z)), precision);
    }

    /**
     * Secant, 1 / cos(<i>z</i>).
     *
     * @param z The argument.
     *
     * @return sec(<i>z</i>)
     *
     * @throws ArithmeticException If <code>z</code> is a zero of the denominator.
     */

    public static Apcomplex sec(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = z.precision();
        if (precision == Apfloat.INFINITE)
        {
            return one(z).divide(ApcomplexMath.cos(z));
        }
        z = ApfloatHelperShim.ensurePrecision(z, ApfloatHelperShim.extendPrecision(precision));
        return ApfloatHelperShim.limitPrecision(one(z).divide(ApcomplexMath.cos(z)), precision);
    }

    /**
     * Cosecant, 1 / sin(<i>z</i>).
     *
     * @param z The argument.
     *
     * @return csc(<i>z</i>)
     *
     * @throws ArithmeticException If <code>z</code> is a pole of the function.
     */

    public static Apcomplex csc(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = z.precision();
        if (precision == Apfloat.INFINITE)
        {
            return one(z).divide(ApcomplexMath.sin(z));
        }
        z = ApfloatHelperShim.ensurePrecision(z, ApfloatHelperShim.extendPrecision(precision));
        return ApfloatHelperShim.limitPrecision(one(z).divide(ApcomplexMath.sin(z)), precision);
    }

    /**
     * Hyperbolic cotangent, cosh(<i>z</i>) / sinh(<i>z</i>).
     *
     * @param z The argument.
     *
     * @return coth(<i>z</i>)
     *
     * @throws ArithmeticException If <code>z</code> is a pole of the function.
     */

    public static Apcomplex coth(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = z.precision();
        if (precision == Apfloat.INFINITE)
        {
            return ApcomplexMath.cosh(z).divide(ApcomplexMath.sinh(z));
        }
        z = ApfloatHelperShim.ensurePrecision(z, ApfloatHelperShim.extendPrecision(precision));
        if (isLarge(z.real()))
        {
            return ApfloatHelperShim.limitPrecision(hyperbolic(Hyperbolic.COTH, z), precision);
        }
        return ApfloatHelperShim.limitPrecision(ApcomplexMath.cosh(z).divide(ApcomplexMath.sinh(z)), precision);
    }

    /**
     * Hyperbolic secant, 1 / cosh(<i>z</i>).
     *
     * @param z The argument.
     *
     * @return sech(<i>z</i>)
     *
     * @throws ArithmeticException If <code>z</code> is a zero of the denominator.
     */

    public static Apcomplex sech(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = z.precision();
        if (precision == Apfloat.INFINITE)
        {
            return one(z).divide(ApcomplexMath.cosh(z));
        }
        z = ApfloatHelperShim.ensurePrecision(z, ApfloatHelperShim.extendPrecision(precision));
        if (isLarge(z.real()))
        {
            return ApfloatHelperShim.limitPrecision(hyperbolic(Hyperbolic.SECH, z), precision);
        }
        return ApfloatHelperShim.limitPrecision(one(z).divide(ApcomplexMath.cosh(z)), precision);
    }

    /**
     * Hyperbolic cosecant, 1 / sinh(<i>z</i>).
     *
     * @param z The argument.
     *
     * @return csch(<i>z</i>)
     *
     * @throws ArithmeticException If <code>z</code> is a pole of the function.
     */

    public static Apcomplex csch(Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = z.precision();
        if (precision == Apfloat.INFINITE)
        {
            return one(z).divide(ApcomplexMath.sinh(z));
        }
        z = ApfloatHelperShim.ensurePrecision(z, ApfloatHelperShim.extendPrecision(precision));
        if (isLarge(z.real()))
        {
            return ApfloatHelperShim.limitPrecision(hyperbolic(Hyperbolic.CSCH, z), precision);
        }
        return ApfloatHelperShim.limitPrecision(one(z).divide(ApcomplexMath.sinh(z)), precision);
    }

    /**
     * Inverse cotangent, atan(1 / <i>z</i>).
     *
     * @param z The argument.
     *
     * @return acot(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> has infinite precision and the result has no finite expansion.
     */

    public static Apcomplex acot(Apcomplex z)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return acot(z, z.precision());
    }

    /**
     * Inverse cotangent, atan(1 / <i>z</i>), which is &pi; / 2 at zero.
     *
     * @param z The argument.
     * @param precision The precision of the result, if <code>z</code> has infinite precision.
     *
     * @return acot(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apcomplex acot(Apcomplex z, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (z.isZero())
        {
            return halfPi(precision, z.radix());
        }
        return ofInverse(Function.ATAN, z, precision);
    }

    /**
     * Inverse secant, acos(1 / <i>z</i>).
     *
     * @param z The argument.
     *
     * @return asec(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> has infinite precision and the result has no finite expansion.
     */

    public static Apcomplex asec(Apcomplex z)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return asec(z, z.precision());
    }

    /**
     * Inverse secant, acos(1 / <i>z</i>), which is infinite at zero.
     *
     * @param z The argument.
     * @param precision The precision of the result, if <code>z</code> has infinite precision.
     *
     * @return asec(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apcomplex asec(Apcomplex z, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (z.isZero())
        {
            throw new ArithmeticException("asec of zero");
        }
        return ofInverse(Function.ACOS, z, precision);
    }

    /**
     * Inverse cosecant, asin(1 / <i>z</i>).
     *
     * @param z The argument.
     *
     * @return acsc(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> has infinite precision and the result has no finite expansion.
     */

    public static Apcomplex acsc(Apcomplex z)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return acsc(z, z.precision());
    }

    /**
     * Inverse cosecant, asin(1 / <i>z</i>), which is infinite at zero.
     *
     * @param z The argument.
     * @param precision The precision of the result, if <code>z</code> has infinite precision.
     *
     * @return acsc(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apcomplex acsc(Apcomplex z, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (z.isZero())
        {
            throw new ArithmeticException("acsc of zero");
        }
        return ofInverse(Function.ASIN, z, precision);
    }

    /**
     * Inverse hyperbolic cotangent, atanh(1 / <i>z</i>).
     *
     * @param z The argument.
     *
     * @return acoth(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> has infinite precision and the result has no finite expansion.
     */

    public static Apcomplex acoth(Apcomplex z)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return acoth(z, z.precision());
    }

    /**
     * Inverse hyperbolic cotangent, atanh(1 / <i>z</i>), which is <i>i</i> &pi; / 2 at zero.
     *
     * @param z The argument.
     * @param precision The precision of the result, if <code>z</code> has infinite precision.
     *
     * @return acoth(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apcomplex acoth(Apcomplex z, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (z.isZero())
        {
            return new Apcomplex(ApfloatHelperShim.ZEROS[z.radix()], halfPi(precision, z.radix()));
        }
        return ofInverse(Function.ATANH, z, precision);
    }

    /**
     * Inverse hyperbolic secant, acosh(1 / <i>z</i>).
     *
     * @param z The argument.
     *
     * @return asech(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> has infinite precision and the result has no finite expansion.
     */

    public static Apcomplex asech(Apcomplex z)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return asech(z, z.precision());
    }

    /**
     * Inverse hyperbolic secant, acosh(1 / <i>z</i>), which is infinite at zero.
     *
     * @param z The argument.
     * @param precision The precision of the result, if <code>z</code> has infinite precision.
     *
     * @return asech(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apcomplex asech(Apcomplex z, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (z.isZero())
        {
            throw new ArithmeticException("asech of zero");
        }
        return ofInverse(Function.ACOSH, z, precision);
    }

    /**
     * Inverse hyperbolic cosecant, asinh(1 / <i>z</i>).
     *
     * @param z The argument.
     *
     * @return acsch(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> has infinite precision and the result has no finite expansion.
     */

    public static Apcomplex acsch(Apcomplex z)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return acsch(z, z.precision());
    }

    /**
     * Inverse hyperbolic cosecant, asinh(1 / <i>z</i>), which is infinite at zero.
     *
     * @param z The argument.
     * @param precision The precision of the result, if <code>z</code> has infinite precision.
     *
     * @return acsch(<i>z</i>)
     *
     * @throws ArithmeticException If the function is infinite at <code>z</code>.
     * @throws InfiniteExpansionException If <code>z</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apcomplex acsch(Apcomplex z, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (z.isZero())
        {
            throw new ArithmeticException("acsch of zero");
        }
        return ofInverse(Function.ASINH, z, precision);
    }

    private enum Hyperbolic
    {
        COTH, SECH, CSCH
    }

    // Away from the imaginary axis cosh and sinh are of the same size, and overflow before their quotient does
    static boolean isLarge(Apfloat x)
    {
        return x.scale() > 0;
    }

    // With w = e^(-z) right of the imaginary axis: coth = (1 + w^2) / (1 - w^2), sech = 2 w / (1 + w^2), csch = 2 w / (1 - w^2)
    private static Apcomplex hyperbolic(Hyperbolic function, Apcomplex z)
    {
        boolean left = (z.real().signum() < 0);
        Apint one = one(z);
        Apcomplex w = ApcomplexMath.exp(left ? z : z.negate()),
                  w2 = w.multiply(w),
                  result;
        switch (function)
        {
            case COTH:
                result = one.add(w2).divide(one.subtract(w2));
                break;
            case SECH:
                // An even function
                return w.add(w).divide(one.add(w2));
            default:
                result = w.add(w).divide(one.subtract(w2));
        }
        return (left ? result.negate() : result);
    }

    private enum Function
    {
        ATAN, ACOS, ASIN, ATANH, ACOSH, ASINH
    }

    // The inverse function at 1 / z, with the digits which the function loses next to its branch points
    private static Apcomplex ofInverse(Function function, Apcomplex z, long precision)
    {
        long targetPrecision = Math.min(z.precision(), precision);
        if (z.precision() == Apfloat.INFINITE)
        {
            if (precision == Apfloat.INFINITE)
            {
                throw new InfiniteExpansionException("Cannot calculate inverse function to infinite precision");
            }
            targetPrecision = precision;
        }
        long workingPrecision = ApfloatHelperShim.extendPrecision(targetPrecision);
        // A part which is zero stays exact
        z = new Apcomplex(z.real().signum() == 0 ? z.real() : z.real().precision(workingPrecision),
                          z.imag().signum() == 0 ? z.imag() : z.imag().precision(workingPrecision));
        Apcomplex w = one(z).divide(z);
        if (w.imag().signum() == 0)
        {
            // On the real axis, where the cuts are: the convention of the library for a real argument
            w = w.real();
        }
        Apcomplex result;
        switch (function)
        {
            case ATAN:
                result = ApcomplexMath.atan(w);
                break;
            case ACOS:
                result = ApcomplexMath.acos(w);
                break;
            case ASIN:
                result = ApcomplexMath.asin(w);
                break;
            case ATANH:
                result = ApcomplexMath.atanh(w);
                break;
            case ACOSH:
                if (w.imag().signum() == 0 && ApfloatMath.abs(w.real()).compareTo(one(z)) < 0)
                {
                    // Between -1 and 1 the principal value is i acos(w); the library has the other sign of it left of zero
                    result = new Apcomplex(ApfloatHelperShim.ZEROS[z.radix()], ApfloatMath.acos(w.real()));
                }
                else
                {
                    result = ApcomplexMath.acosh(w);
                }
                break;
            default:
                result = ApcomplexMath.asinh(w);
        }
        return ApfloatHelperShim.limitPrecision(result, targetPrecision);
    }

    private static Apint one(Apcomplex z)
    {
        return ApfloatHelperShim.ONES[z.radix()];
    }

    static Apfloat halfPi(long precision, int radix)
    {
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate pi / 2 to infinite precision");
        }
        return ApfloatMath.pi(precision, radix).divide(new Apint(2, radix));
    }
}
