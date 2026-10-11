package org.matheclipse.external.apfloat;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;

/**
 * The reciprocal trigonometric and hyperbolic functions and their inverses for
 * {@link Apfloat} arguments, which apfloat does not have: cot, sec, csc, coth, sech, csch,
 * acot, asec, acsc, acoth, asech and acsch.<p>
 *
 * The results are real. Where an inverse function of a real argument is not real - asec and
 * acsc inside (-1, 1), acoth inside [-1, 1] and asech outside (0, 1] - the methods throw an
 * <code>ArithmeticException</code>, as <code>ApfloatMath.acos()</code> does; the value is
 * there in {@link ApcomplexTrigMath}.<p>
 *
 * An exact number has infinite precision in apfloat. The methods with a
 * <code>precision</code> argument return the result of such an argument to that precision.
 *
 * @see ApcomplexTrigMath
 */

public class ApfloatTrigMath
{
    private ApfloatTrigMath()
    {
    }

    /**
     * Cotangent, cos(<i>x</i>) / sin(<i>x</i>).
     *
     * @param x The argument.
     *
     * @return cot(<i>x</i>)
     *
     * @throws ArithmeticException If <code>x</code> is a zero of the denominator.
     */

    public static Apfloat cot(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = x.precision();
        if (precision == Apfloat.INFINITE)
        {
            return ApfloatMath.cos(x).divide(ApfloatMath.sin(x));
        }
        x = x.precision(ApfloatHelperShim.extendPrecision(precision));
        return ApfloatMath.cos(x).divide(ApfloatMath.sin(x)).precision(precision);
    }

    /**
     * Secant, 1 / cos(<i>x</i>).
     *
     * @param x The argument.
     *
     * @return sec(<i>x</i>)
     *
     * @throws ArithmeticException If <code>x</code> is a zero of the denominator.
     */

    public static Apfloat sec(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = x.precision();
        if (precision == Apfloat.INFINITE)
        {
            return one(x).divide(ApfloatMath.cos(x));
        }
        x = x.precision(ApfloatHelperShim.extendPrecision(precision));
        return one(x).divide(ApfloatMath.cos(x)).precision(precision);
    }

    /**
     * Cosecant, 1 / sin(<i>x</i>).
     *
     * @param x The argument.
     *
     * @return csc(<i>x</i>)
     *
     * @throws ArithmeticException If <code>x</code> is a zero of the denominator.
     */

    public static Apfloat csc(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = x.precision();
        if (precision == Apfloat.INFINITE)
        {
            return one(x).divide(ApfloatMath.sin(x));
        }
        x = x.precision(ApfloatHelperShim.extendPrecision(precision));
        return one(x).divide(ApfloatMath.sin(x)).precision(precision);
    }

    /**
     * Hyperbolic cotangent, cosh(<i>x</i>) / sinh(<i>x</i>).
     *
     * @param x The argument.
     *
     * @return coth(<i>x</i>)
     *
     * @throws ArithmeticException If <code>x</code> is a zero of the denominator.
     */

    public static Apfloat coth(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = x.precision();
        if (precision == Apfloat.INFINITE)
        {
            return ApfloatMath.cosh(x).divide(ApfloatMath.sinh(x));
        }
        if (ApcomplexTrigMath.isLarge(x))
        {
            // cosh and sinh overflow before their quotient does
            return ApcomplexTrigMath.coth(x).real();
        }
        x = x.precision(ApfloatHelperShim.extendPrecision(precision));
        return ApfloatMath.cosh(x).divide(ApfloatMath.sinh(x)).precision(precision);
    }

    /**
     * Hyperbolic secant, 1 / cosh(<i>x</i>).
     *
     * @param x The argument.
     *
     * @return sech(<i>x</i>)
     *
     * @throws ArithmeticException If <code>x</code> is a zero of the denominator.
     */

    public static Apfloat sech(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = x.precision();
        if (precision == Apfloat.INFINITE)
        {
            return one(x).divide(ApfloatMath.cosh(x));
        }
        if (ApcomplexTrigMath.isLarge(x))
        {
            // cosh and sinh overflow before their quotient does
            return ApcomplexTrigMath.sech(x).real();
        }
        x = x.precision(ApfloatHelperShim.extendPrecision(precision));
        return one(x).divide(ApfloatMath.cosh(x)).precision(precision);
    }

    /**
     * Hyperbolic cosecant, 1 / sinh(<i>x</i>).
     *
     * @param x The argument.
     *
     * @return csch(<i>x</i>)
     *
     * @throws ArithmeticException If <code>x</code> is a zero of the denominator.
     */

    public static Apfloat csch(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = x.precision();
        if (precision == Apfloat.INFINITE)
        {
            return one(x).divide(ApfloatMath.sinh(x));
        }
        if (ApcomplexTrigMath.isLarge(x))
        {
            // cosh and sinh overflow before their quotient does
            return ApcomplexTrigMath.csch(x).real();
        }
        x = x.precision(ApfloatHelperShim.extendPrecision(precision));
        return one(x).divide(ApfloatMath.sinh(x)).precision(precision);
    }

    /**
     * Inverse cotangent, atan(1 / <i>x</i>).
     *
     * @param x The argument.
     *
     * @return acot(<i>x</i>)
     *
     * @throws InfiniteExpansionException If <code>x</code> has infinite precision and the result has no finite expansion.
     */

    public static Apfloat acot(Apfloat x)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return acot(x, x.precision());
    }

    /**
     * Inverse cotangent, atan(1 / <i>x</i>).
     *
     * @param x The argument.
     * @param precision The precision of the result, if <code>x</code> has infinite precision.
     *
     * @return acot(<i>x</i>)
     *
     * @throws InfiniteExpansionException If <code>x</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apfloat acot(Apfloat x, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            return ApcomplexTrigMath.halfPi(precision, x.radix());
        }
        long targetPrecision = targetPrecision(x, precision);
        x = x.precision(ApfloatHelperShim.extendPrecision(targetPrecision));
        return ApfloatMath.atan(one(x).divide(x)).precision(targetPrecision);
    }

    /**
     * Inverse secant, acos(1 / <i>x</i>).
     *
     * @param x The argument.
     *
     * @return asec(<i>x</i>)
     *
     * @throws ArithmeticException If |<code>x</code>| is less than 1.
     * @throws InfiniteExpansionException If <code>x</code> has infinite precision and the result has no finite expansion.
     */

    public static Apfloat asec(Apfloat x)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return asec(x, x.precision());
    }

    /**
     * Inverse secant, acos(1 / <i>x</i>).
     *
     * @param x The argument.
     * @param precision The precision of the result, if <code>x</code> has infinite precision.
     *
     * @return asec(<i>x</i>)
     *
     * @throws ArithmeticException If |<code>x</code>| is less than 1.
     * @throws InfiniteExpansionException If <code>x</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apfloat asec(Apfloat x, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            throw new ArithmeticException("asec of zero");
        }
        if (ApfloatMath.abs(x).compareTo(one(x)) < 0)
        {
            throw new ArithmeticException("Result would be complex");
        }
        long targetPrecision = targetPrecision(x, precision);
        x = x.precision(ApfloatHelperShim.extendPrecision(targetPrecision));
        return ApfloatMath.acos(one(x).divide(x)).precision(targetPrecision);
    }

    /**
     * Inverse cosecant, asin(1 / <i>x</i>).
     *
     * @param x The argument.
     *
     * @return acsc(<i>x</i>)
     *
     * @throws ArithmeticException If |<code>x</code>| is less than 1.
     * @throws InfiniteExpansionException If <code>x</code> has infinite precision and the result has no finite expansion.
     */

    public static Apfloat acsc(Apfloat x)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return acsc(x, x.precision());
    }

    /**
     * Inverse cosecant, asin(1 / <i>x</i>).
     *
     * @param x The argument.
     * @param precision The precision of the result, if <code>x</code> has infinite precision.
     *
     * @return acsc(<i>x</i>)
     *
     * @throws ArithmeticException If |<code>x</code>| is less than 1.
     * @throws InfiniteExpansionException If <code>x</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apfloat acsc(Apfloat x, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            throw new ArithmeticException("acsc of zero");
        }
        if (ApfloatMath.abs(x).compareTo(one(x)) < 0)
        {
            throw new ArithmeticException("Result would be complex");
        }
        long targetPrecision = targetPrecision(x, precision);
        x = x.precision(ApfloatHelperShim.extendPrecision(targetPrecision));
        return ApfloatMath.asin(one(x).divide(x)).precision(targetPrecision);
    }

    /**
     * Inverse hyperbolic cotangent, atanh(1 / <i>x</i>).
     *
     * @param x The argument.
     *
     * @return acoth(<i>x</i>)
     *
     * @throws ArithmeticException If |<code>x</code>| is not greater than 1.
     * @throws InfiniteExpansionException If <code>x</code> has infinite precision and the result has no finite expansion.
     */

    public static Apfloat acoth(Apfloat x)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return acoth(x, x.precision());
    }

    /**
     * Inverse hyperbolic cotangent, atanh(1 / <i>x</i>).
     *
     * @param x The argument.
     * @param precision The precision of the result, if <code>x</code> has infinite precision.
     *
     * @return acoth(<i>x</i>)
     *
     * @throws ArithmeticException If |<code>x</code>| is not greater than 1.
     * @throws InfiniteExpansionException If <code>x</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apfloat acoth(Apfloat x, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            throw new ArithmeticException("Result would be complex");
        }
        if (ApfloatMath.abs(x).compareTo(one(x)) <= 0)
        {
            throw new ArithmeticException("Result would be complex");
        }
        long targetPrecision = targetPrecision(x, precision);
        x = x.precision(ApfloatHelperShim.extendPrecision(targetPrecision));
        return ApfloatMath.atanh(one(x).divide(x)).precision(targetPrecision);
    }

    /**
     * Inverse hyperbolic secant, acosh(1 / <i>x</i>).
     *
     * @param x The argument.
     *
     * @return asech(<i>x</i>)
     *
     * @throws ArithmeticException If <code>x</code> is not in (0, 1].
     * @throws InfiniteExpansionException If <code>x</code> has infinite precision and the result has no finite expansion.
     */

    public static Apfloat asech(Apfloat x)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return asech(x, x.precision());
    }

    /**
     * Inverse hyperbolic secant, acosh(1 / <i>x</i>).
     *
     * @param x The argument.
     * @param precision The precision of the result, if <code>x</code> has infinite precision.
     *
     * @return asech(<i>x</i>)
     *
     * @throws ArithmeticException If <code>x</code> is not in (0, 1].
     * @throws InfiniteExpansionException If <code>x</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apfloat asech(Apfloat x, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            throw new ArithmeticException("asech of zero");
        }
        if (x.signum() < 0 || x.compareTo(one(x)) > 0)
        {
            throw new ArithmeticException("Result would be complex");
        }
        long targetPrecision = targetPrecision(x, precision);
        x = x.precision(ApfloatHelperShim.extendPrecision(targetPrecision));
        return ApfloatMath.acosh(one(x).divide(x)).precision(targetPrecision);
    }

    /**
     * Inverse hyperbolic cosecant, asinh(1 / <i>x</i>).
     *
     * @param x The argument.
     *
     * @return acsch(<i>x</i>)
     *
     * @throws ArithmeticException If <code>x</code> is zero.
     * @throws InfiniteExpansionException If <code>x</code> has infinite precision and the result has no finite expansion.
     */

    public static Apfloat acsch(Apfloat x)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        return acsch(x, x.precision());
    }

    /**
     * Inverse hyperbolic cosecant, asinh(1 / <i>x</i>).
     *
     * @param x The argument.
     * @param precision The precision of the result, if <code>x</code> has infinite precision.
     *
     * @return acsch(<i>x</i>)
     *
     * @throws ArithmeticException If <code>x</code> is zero.
     * @throws InfiniteExpansionException If <code>x</code> and <code>precision</code> are infinite and the result has no finite expansion.
     */

    public static Apfloat acsch(Apfloat x, long precision)
        throws ArithmeticException, InfiniteExpansionException, ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            throw new ArithmeticException("acsch of zero");
        }
        long targetPrecision = targetPrecision(x, precision);
        x = x.precision(ApfloatHelperShim.extendPrecision(targetPrecision));
        return ApfloatMath.asinh(one(x).divide(x)).precision(targetPrecision);
    }

    private static long targetPrecision(Apfloat x, long precision)
    {
        if (x.precision() != Apfloat.INFINITE)
        {
            return x.precision();
        }
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate inverse function to infinite precision");
        }
        return precision;
    }

    private static Apint one(Apfloat x)
    {
        return ApfloatHelperShim.ONES[x.radix()];
    }
}
