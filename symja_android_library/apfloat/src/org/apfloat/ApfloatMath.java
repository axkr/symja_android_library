package org.apfloat;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apfloat.spi.Util;

/**
 * Various mathematical functions for arbitrary precision floating-point numbers.<p>
 *
 * Due to different types of round-off errors that can occur in the implementation,
 * no guarantees about e.g. monotonicity are given for any of the methods.
 *
 * @see ApintMath
 *
 * @version 1.8.1
 * @author Mikko Tommila
 */

public class ApfloatMath
{
    private ApfloatMath()
    {
    }

    /**
     * Integer power.
     *
     * @param x Base of the power operator.
     * @param n Exponent of the power operator.
     *
     * @return <code>x</code> to the <code>n</code>:th power, that is <code>x<sup>n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If both <code>x</code> and <code>n</code> are zero.
     */

    public static Apfloat pow(Apfloat x, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (n == 0)
        {
            if (x.signum() == 0)
            {
                throw new ArithmeticException("Zero to power zero");
            }

            return new Apfloat(1, Apfloat.INFINITE, x.radix());
        }
        else if (n < 0)
        {
            x = inverseRoot(x, 1);
            n = -n;
        }

        long precision = x.precision();
        x = ApfloatHelper.extendPrecision(x);   // Big exponents will accumulate round-off errors

        // Algorithm improvements by Bernd Kellner
        int b2pow = 0;

        while ((n & 1) == 0)
        {
            b2pow++;
            n >>>= 1;
        }

        Apfloat r = x;

        while ((n >>>= 1) > 0)
        {
            x = x.multiply(x);
            if ((n & 1) != 0)
            {
                r = r.multiply(x);
            }
        }

        while (b2pow-- > 0)
        {
            r = r.multiply(r);
        }

        return r.precision(precision);
    }

    /**
     * Square root.
     *
     * @param x The argument.
     *
     * @return Square root of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> is negative.
     */

    public static Apfloat sqrt(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return root(x, 2);
    }

    /**
     * Cube root.
     *
     * @param x The argument.
     *
     * @return Cube root of <code>x</code>.
     */

    public static Apfloat cbrt(Apfloat x)
        throws ApfloatRuntimeException
    {
        return root(x, 3);
    }

    /**
     * Positive integer root.
     *
     * @param x The argument.
     * @param n Which root to take.
     *
     * @return <code>n</code>:th root of <code>x</code>, that is <code>x<sup>1/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>n</code> is zero, or <code>x</code> is negative and <code>n</code> is even.
     */

    public static Apfloat root(Apfloat x, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (n == 0)
        {
            throw new ArithmeticException("Zeroth root");
        }
        else if (x.signum() == 0)
        {
            return Apfloat.ZERO;                // Avoid division by zero
        }
        else if (n == 1)
        {
            return x;
        }
        else if (n == 0x8000000000000000L)
        {
            return sqrt(inverseRoot(x, n / -2));
        }
        else if (n < 0)
        {
            return inverseRoot(x, -n);
        }
        else if (n == 2)
        {
            return x.multiply(inverseRoot(x, 2));
        }
        else if (n == 3)
        {
            Apfloat y = x.multiply(x);
            return x.multiply(inverseRoot(y, 3));
        }
        else
        {
            Apfloat y = inverseRoot(x, n);
            return inverseRoot(y, 1);
        }
    }

    /**
     * Inverse positive integer root.
     *
     * @param x The argument.
     * @param n Which inverse root to take.
     *
     * @return Inverse <code>n</code>:th root of <code>x</code>, that is <code>x<sup>-1/n</sup></code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> or <code>n</code> is zero, or <code>x</code> is negative and <code>n</code> is even.
     */

    public static Apfloat inverseRoot(Apfloat x, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return inverseRoot(x, n, x.precision());
    }

    /**
     * Inverse positive integer root.
     *
     * @param x The argument.
     * @param n Which inverse root to take.
     * @param targetPrecision Precision of the desired result.
     *
     * @return Inverse <code>n</code>:th root of <code>x</code>, that is <code>x<sup>-1/n</sup></code>.
     *
     * @exception java.lang.IllegalArgumentException If <code>targetPrecision <= 0</code>.
     * @exception java.lang.ArithmeticException If <code>x</code> or <code>n</code> is zero, or <code>x</code> is negative and <code>n</code> is even.
     */

    public static Apfloat inverseRoot(Apfloat x, long n, long targetPrecision)
        throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException
    {
        return inverseRoot(x, n, targetPrecision, null);
    }

    /**
     * Inverse positive integer root.
     *
     * @param x The argument.
     * @param n Which inverse root to take.
     * @param targetPrecision Precision of the desired result.
     * @param initialGuess Initial guess for the result value, or <code>null</code> if none is available.
     *
     * @return Inverse <code>n</code>:th root of <code>x</code>, that is <code>x<sup>-1/n</sup></code>.
     *
     * @exception java.lang.IllegalArgumentException If <code>targetPrecision <= 0</code>.
     * @exception java.lang.ArithmeticException If <code>x</code> or <code>n</code> is zero, or <code>x</code> is negative and <code>n</code> is even.
     */

    public static Apfloat inverseRoot(Apfloat x, long n, long targetPrecision, Apfloat initialGuess)
        throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException
    {
        return inverseRoot(x, n, targetPrecision, initialGuess, initialGuess == null ? 0 : initialGuess.precision());
    }

    /**
     * Inverse positive integer root.<p>
     *
     * This method is the basis for most of apfloat's non-elementary operations.
     * It is used e.g. in {@link Apfloat#divide(Apfloat)}, {@link #sqrt(Apfloat)}
     * and {@link #root(Apfloat,long)}.
     *
     * @param x The argument.
     * @param n Which inverse root to take.
     * @param targetPrecision Precision of the desired result.
     * @param initialGuess Initial guess for the result value, or <code>null</code> if none is available.
     * @param initialPrecision Precision of the initial guess, if available.
     *
     * @return Inverse <code>n</code>:th root of <code>x</code>, that is <code>x<sup>-1/n</sup></code>.
     *
     * @exception java.lang.IllegalArgumentException If <code>targetPrecision <= 0</code> or <code>initialPrecision <= 0</code>.
     * @exception java.lang.ArithmeticException If <code>x</code> or <code>n</code> is zero, or <code>x</code> is negative and <code>n</code> is even.
     */

    public static Apfloat inverseRoot(Apfloat x, long n, long targetPrecision, Apfloat initialGuess, long initialPrecision)
        throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            throw new ArithmeticException("Inverse root of zero");
        }
        else if (n == 0)
        {
            throw new ArithmeticException("Inverse zeroth root");
        }
        else if ((n & 1) == 0 && x.signum() < 0)
        {
            throw new ArithmeticException("Even root of negative number; result would be complex");
        }
        else if (targetPrecision <= 0)
        {
            throw new IllegalArgumentException("Target precision " + targetPrecision + " is not positive");
        }
        else if (x.equals(Apfloat.ONE))
        {
            // Trivial case
            return x.precision(targetPrecision);
        }
        else if (targetPrecision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate inverse root to infinite precision");
        }
        else if (n == 0x8000000000000000L)
        {
            Apfloat y = inverseRoot(x, n / -2);
            return inverseRoot(y, 2);
        }
        else if (n < 0)
        {
            Apfloat y = inverseRoot(x, -n);
            return inverseRoot(y, 1);
        }

        long precision,
             doublePrecision = ApfloatHelper.getDoublePrecision(x.radix());
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix()),
                divisor = new Apfloat(n, Apfloat.INFINITE, x.radix()),
                result;

        if (initialGuess == null || initialPrecision < doublePrecision)
        {
            // Calculate initial guess from x
            long scaleQuot = x.scale() / n,
                 scaleRem = x.scale() - scaleQuot * n;

            result = x.precision(doublePrecision);
            result = scale(result, -result.scale());    // Allow scales in exess of doubles'

            precision = doublePrecision;

            result = new Apfloat((double) result.signum() * Math.pow(Math.abs(result.doubleValue()), -1.0 / (double) n) * Math.pow((double) x.radix(), (double) -scaleRem / (double) n), precision, x.radix());
            result = scale(result, -scaleQuot);
        }
        else
        {
            // Take initial guess as given
            result = initialGuess;
            precision = initialPrecision;
        }

        int iterations = 0;

        // Compute total number of iterations
        for (long maxPrec = precision; maxPrec < targetPrecision; maxPrec <<= 1)
        {
            iterations++;
        }

        int precisingIteration = iterations;

        // Check where the precising iteration should be done
        for (long minPrec = precision; precisingIteration > 0; precisingIteration--, minPrec <<= 1)
        {
            if ((minPrec - Apfloat.EXTRA_PRECISION) << precisingIteration >= targetPrecision)
            {
                break;
            }
        }

        x = ApfloatHelper.extendPrecision(x);

        // Newton's iteration
        while (iterations-- > 0)
        {
            precision *= 2;
            result = result.precision(Math.min(precision, targetPrecision));

            Apfloat t = pow(result, n);
            t = lastIterationExtendPrecision(iterations, precisingIteration, t);
            t = one.subtract(x.multiply(t));
            if (iterations < precisingIteration)
            {
                t = t.precision(precision / 2);
            }

            result = lastIterationExtendPrecision(iterations, precisingIteration, result);
            result = result.add(result.multiply(t).divide(divisor));

            // Precising iteration
            if (iterations == precisingIteration)
            {
                t = pow(result, n);
                t = lastIterationExtendPrecision(iterations, -1, t);

                result = lastIterationExtendPrecision(iterations, -1, result);
                result = result.add(result.multiply(one.subtract(x.multiply(t))).divide(divisor));
            }
        }

        return result.precision(targetPrecision);
    }

    /**
     * Floor function. Returns the largest (closest to positive infinity) value
     * that is not greater than the argument and is equal to a mathematical integer.
     *
     * @param x The argument.
     *
     * @return <code>x</code> rounded towards negative infinity.
     */

    public static Apint floor(Apfloat x)
        throws ApfloatRuntimeException
    {
        return x.floor();
    }

    /**
     * Ceiling function. Returns the smallest (closest to negative infinity) value
     * that is not less than the argument and is equal to a mathematical integer.
     *
     * @param x The argument.
     *
     * @return <code>x</code> rounded towards positive infinity.
     */

    public static Apint ceil(Apfloat x)
        throws ApfloatRuntimeException
    {
        return x.ceil();
    }

    /**
     * Truncates fractional part.
     *
     * @param x The argument.
     *
     * @return <code>x</code> rounded towards zero.
     */

    public static Apint truncate(Apfloat x)
        throws ApfloatRuntimeException
    {
        return x.truncate();
    }

    /**
     * Extracts fractional part.
     *
     * @param x The argument.
     *
     * @return The fractional part of <code>x</code>.
     *
     * @since 1.7.0
     */

    public static Apfloat frac(Apfloat x)
        throws ApfloatRuntimeException
    {
        return x.frac();
    }

    /**
     * Rounds the given number to the specified precision with the specified rounding mode.
     *
     * @param x The number to round.
     * @param precision The precision to round to.
     * @param roundingMode The rounding mode to use.
     *
     * @return The rounded number.
     *
     * @exception java.lang.IllegalArgumentException If <code>precision</code> is less than zero or zero.
     * @exception java.lang.ArithmeticException If rounding is necessary (result is not exact) and rounding mode is {@link RoundingMode#UNNECESSARY}.
     *
     * @since 1.7.0
     */

    public static Apfloat round(Apfloat x, long precision, RoundingMode roundingMode)
        throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException
    {
        return RoundingHelper.round(x, precision, roundingMode);
    }

    /**
     * Returns an apfloat whose value is <code>-x</code>.
     *
     * @deprecated Use {@link Apfloat#negate()}.
     *
     * @param x The argument.
     *
     * @return <code>-x</code>.
     */

    @Deprecated
    public static Apfloat negate(Apfloat x)
        throws ApfloatRuntimeException
    {
        return x.negate();
    }

    /**
     * Absolute value.
     *
     * @param x The argument.
     *
     * @return Absolute value of <code>x</code>.
     */

    public static Apfloat abs(Apfloat x)
        throws ApfloatRuntimeException
    {
        if (x.signum() >= 0)
        {
            return x;
        }
        else
        {
            return x.negate();
        }
    }

    /**
     * Copy sign from one argument to another.
     *
     * @param x The value whose sign is to be adjusted.
     * @param y The value whose sign is to be used.
     *
     * @return <code>x</code> with its sign changed to match the sign of <code>y</code>.
     *
     * @since 1.1
     */

    public static Apfloat copySign(Apfloat x, Apfloat y)
        throws ApfloatRuntimeException
    {
        if (y.signum() == 0)
        {
            return y;
        }
        else if (x.signum() != y.signum())
        {
            return x.negate();
        }
        else
        {
            return x;
        }
    }

    /**
     * Multiply by a power of the radix.
     *
     * @param x The argument.
     * @param scale The scaling factor.
     *
     * @return <code>x * x.radix()<sup>scale</sup></code>.
     */

    public static Apfloat scale(Apfloat x, long scale)
        throws ApfloatRuntimeException
    {
        if (scale == 0 || x.signum() == 0)
        {
            return x;
        }

        Apfloat radix = new Apfloat(x.radix(), Apfloat.INFINITE, x.radix()),
                result;

        if ((Math.abs(scale) & 0xC000000000000000L) != 0)
        {
            // The exponent might overflow in the string or in intermediate calculations
            Apfloat scaler1 = pow(radix, Math.abs(scale) >>> 1),
                    scaler2 = ((scale & 1) == 0 ? scaler1 : scaler1.multiply(radix));
            result = (scale >= 0 ? x.multiply(scaler1).multiply(scaler2) : x.divide(scaler1).divide(scaler2));
        }
        else if (x.radix() <= 14)
        {
            Apfloat scaler = new Apfloat("1e" + scale, Apfloat.INFINITE, x.radix());
            result = x.multiply(scaler);
        }
        else
        {
            // "e" would be interpreted as a digit
            Apfloat scaler = pow(radix, Math.abs(scale));
            result = (scale >= 0 ? x.multiply(scaler) : x.divide(scaler));
        }

        return result;
    }

    /**
     * Split to integer and fractional parts.
     * The integer part is simply <code>i = floor(x)</code>.
     * For the fractional part <code>f</code> the following is always true:<p>
     *
     * <code>0 <= f < 1</code>
     *
     * @param x The argument.
     *
     * @return An array of two apfloats, <code>[i, f]</code>, the first being the integer part and the last being the fractional part.
     */

    public static Apfloat[] modf(Apfloat x)
        throws ApfloatRuntimeException
    {
        Apfloat[] result = new Apfloat[2];

        result[0] = x.floor();
        result[1] = (x.signum() >= 0 ? x.frac() : x.subtract(result[0]));

        return result;
    }

    /**
     * Returns x modulo y.<p>
     *
     * This function calculates the remainder <code>f</code> of <code>x / y</code>
     * such that <code>x = i * y + f</code>, where <code>i</code> is an integer,
     * <code>f</code> has the same sign as <code>x</code>,
     * and the absolute value of <code>f</code> is less than the absolute value of <code>y</code>.<p>
     *
     * If <code>y</code> is zero, then zero is returned.
     *
     * @param x The dividend.
     * @param y The divisor.
     *
     * @return The remainder when x is divided by y.
     */

    public static Apfloat fmod(Apfloat x, Apfloat y)
        throws ApfloatRuntimeException
    {
        if (y.signum() == 0)
        {
            return y;                           // By definition
        }
        else if (x.signum() == 0)
        {
            // 0 % x = 0
            return x;
        }

        long precision;
        Apfloat t, a, b, tx, ty;

        a = abs(x);
        b = abs(y);

        if (a.compareTo(b) < 0)
        {
            return x;                           // abs(x) < abs(y)
        }
        else if (x.precision() <= x.scale() - y.scale())                        // We now know that x.scale() >= y.scale()
        {
            return Apfloat.ZERO;                // Degenerate case; not enough precision to make any sense
        }
        else
        {
            precision = x.scale() - y.scale() + Apfloat.EXTRA_PRECISION;        // Some extra precision to avoid round-off errors
        }

        tx = x.precision(precision);
        ty = y.precision(precision);

        t = tx.divide(ty).truncate();           // Approximate division

        precision = Math.min(Util.ifFinite(y.precision(), y.precision() + x.scale() - y.scale()), x.precision());

        tx = x.precision(precision);
        ty = y.precision(precision);

        a = abs(tx).subtract(abs(t.multiply(ty)));
        b = abs(ty);

        if (a.compareTo(b) >= 0)                // Fix division round-off error
        {
            a = a.subtract(b);
        }
        else if (a.signum() < 0)                // Fix division round-off error
        {
            a = a.add(b);
        }

        t = copySign(a, x);

        return t;
    }

    /**
     * Fused multiply-add. Calculates <code>a * b + c * d</code>
     * so that the precision used in the multiplications is only
     * what is needed for the end result. Performance can this way
     * be better than by calculating <code>a.multiply(b).add(c.multiply(d))</code>.
     *
     * @param a First argument.
     * @param b Second argument.
     * @param c Third argument.
     * @param d Fourth argument.
     *
     * @return <code>a * b + c * d</code>.
     */

    public static Apfloat multiplyAdd(Apfloat a, Apfloat b, Apfloat c, Apfloat d)
        throws ApfloatRuntimeException
    {
        return multiplyAddOrSubtract(a, b, c, d, false);
    }

    /**
     * Fused multiply-subtract. Calculates <code>a * b - c * d</code>
     * so that the precision used in the multiplications is only
     * what is needed for the end result. Performance can this way
     * be better than by calculating <code>a.multiply(b).subtract(c.multiply(d))</code>.
     *
     * @param a First argument.
     * @param b Second argument.
     * @param c Third argument.
     * @param d Fourth argument.
     *
     * @return <code>a * b - c * d</code>.
     */

    public static Apfloat multiplySubtract(Apfloat a, Apfloat b, Apfloat c, Apfloat d)
        throws ApfloatRuntimeException
    {
        return multiplyAddOrSubtract(a, b, c, d, true);
    }

    private static Apfloat multiplyAddOrSubtract(Apfloat a, Apfloat b, Apfloat c, Apfloat d, boolean subtract)
        throws ApfloatRuntimeException
    {
        long[] precisions;
        Apfloat ab, cd;

        precisions = ApfloatHelper.getMatchingPrecisions(a, b, c, d);
        if (precisions[0] == 0)
        {
            ab = Apfloat.ZERO;
        }
        else
        {
            a = a.precision(precisions[0]);
            b = b.precision(precisions[0]);
            ab = a.multiply(b);
        }
        if (precisions[1] == 0)
        {
            cd = Apfloat.ZERO;
        }
        else
        {
            c = c.precision(precisions[1]);
            d = d.precision(precisions[1]);
            cd = c.multiply(d);
        }

        Apfloat result = (subtract ? ab.subtract(cd) : ab.add(cd));

        return (result.signum() == 0 ? result : result.precision(precisions[2]));
    }

    /**
     * Arithmetic-geometric mean.
     *
     * @param a First argument.
     * @param b Second argument.
     *
     * @return Arithmetic-geometric mean of a and b.
     */

    public static Apfloat agm(Apfloat a, Apfloat b)
        throws ApfloatRuntimeException
    {
        if (a.signum() == 0 || b.signum() == 0)         // Would not converge quadratically
        {
            return Apfloat.ZERO;
        }

        long workingPrecision = Math.min(a.precision(), b.precision()),
             targetPrecision = Math.max(a.precision(), b.precision());

        if (workingPrecision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate agm to infinite precision");
        }

        // Some extra precision is required for the algorithm to work accurately
        workingPrecision = ApfloatHelper.extendPrecision(workingPrecision);
        a = ApfloatHelper.ensurePrecision(a, workingPrecision);
        b = ApfloatHelper.ensurePrecision(b, workingPrecision);

        long precision = 0,
             halfWorkingPrecision = (workingPrecision + 1) / 2;
        final long CONVERGING = 1000;           // Arbitrarily chosen value...
        Apfloat two = new Apfloat(2, Apfloat.INFINITE, a.radix());

        // First check convergence
        while (precision < CONVERGING && precision < halfWorkingPrecision)
        {
            Apfloat t = a.add(b).divide(two);
            b = sqrt(a.multiply(b));
            a = t;

            // Conserve precision in case of accumulating round-off errors
            a = ApfloatHelper.ensurePrecision(a, workingPrecision);
            b = ApfloatHelper.ensurePrecision(b, workingPrecision);

            precision = a.equalDigits(b);
        }

        // Now we know quadratic convergence
        while (precision <= halfWorkingPrecision)
        {
            Apfloat t = a.add(b).divide(two);
            b = sqrt(a.multiply(b));
            a = t;

            // Conserve precision in case of accumulating round-off errors
            a = ApfloatHelper.ensurePrecision(a, workingPrecision);
            b = ApfloatHelper.ensurePrecision(b, workingPrecision);

            precision *= 2;
        }

        return a.add(b).divide(two).precision(targetPrecision);
    }

    /**
     * Calculates &pi;. Uses default radix.
     *
     * @param precision Number of digits of &pi; to calculate.
     *
     * @return &pi; accurate to <code>precision</code> digits, in the default radix.
     *
     * @exception java.lang.NumberFormatException If the default radix is not valid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public static Apfloat pi(long precision)
        throws IllegalArgumentException, NumberFormatException, ApfloatRuntimeException
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        int radix = ctx.getDefaultRadix();

        return pi(precision, radix);
    }

    /**
     * Calculates &pi;.
     *
     * @param precision Number of digits of &pi; to calculate.
     * @param radix The radix in which the number should be presented.
     *
     * @return &pi; accurate to <code>precision</code> digits, in base <code>radix</code>.
     *
     * @exception java.lang.NumberFormatException If the radix is not valid.
     * @exception java.lang.IllegalArgumentException In case the precision is invalid.
     */

    public static Apfloat pi(long precision, int radix)
        throws IllegalArgumentException, NumberFormatException, ApfloatRuntimeException
    {
        if (precision <= 0)
        {
            throw new IllegalArgumentException("Precision " + precision + " is not positive");
        }
        else if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate pi to infinite precision");
        }

        // Get synchronization lock - getting the lock is also synchronized
        Integer radixKey = getRadixPiKey(new Integer(radix));   // Use new Integer since we synchronize on it; Integer.valueOf() could be shared instance

        Apfloat pi;

        // Synchronize getting pre-calculated pi by radix key
        // - two threads won't try to calculate it at the same time
        // - doesn't block getting it for other radixes
        synchronized (radixKey)
        {
            pi = ApfloatMath.radixPi.get(radixKey);

            if (pi == null || pi.precision() < precision)
            {
                pi = calculatePi(precision, radixKey);
            }
            else
            {
                pi = pi.precision(precision);
            }
        }

        return pi;
    }

    // Get shared radix key for synchronizing getting and calculating the pi related constants
    private static Integer getRadixPiKey(Integer radix)
    {
        Integer radixKey = ApfloatMath.radixPiKeys.putIfAbsent(radix, radix);
        if (radixKey == null)
        {
            radixKey = radix;
        }

        return radixKey;
    }

    /**
     * Simple JavaBean to hold one apfloat. This class can
     * also be thought of as a pointer to an apfloat.
     */

    private static class ApfloatHolder
    {
        public ApfloatHolder()
        {
            this(null);
        }

        public ApfloatHolder(Apfloat apfloat)
        {
            this.apfloat = apfloat;
        }

        public Apfloat getApfloat()
        {
            return this.apfloat;
        }

        public void setApfloat(Apfloat apfloat)
        {
            this.apfloat = apfloat;
        }

        private Apfloat apfloat;
    }

    private static class PiCalculator
    {
        public PiCalculator(int radix)
            throws ApfloatRuntimeException
        {
            this.A = new Apfloat(13591409, Apfloat.INFINITE, radix);
            this.B = new Apfloat(545140134, Apfloat.INFINITE, radix);
            this.J = new Apfloat(10939058860032000L, Apfloat.INFINITE, radix);
            this.ONE = new Apfloat(1, Apfloat.INFINITE, radix);
            this.TWO = new Apfloat(2, Apfloat.INFINITE, radix);
            this.FIVE = new Apfloat(5, Apfloat.INFINITE, radix);
            this.SIX = new Apfloat(6, Apfloat.INFINITE, radix);
            this.radix = radix;
        }

        private Apfloat a(long n)
            throws ApfloatRuntimeException
        {
            Apfloat s = new Apfloat(n, Apfloat.INFINITE, this.radix),
                    v = this.A.add(this.B.multiply(s));

            v = ((n & 1) == 0 ? v : v.negate());

            return v;
        }

        private Apfloat p(long n)
            throws ApfloatRuntimeException
        {
            Apfloat v;

            if (n == 0)
            {
                v = this.ONE;
            }
            else
            {
                Apfloat f = new Apfloat(n, Apfloat.INFINITE, this.radix),
                        sixf = this.SIX.multiply(f);

                v = sixf.subtract(this.ONE).multiply(this.TWO.multiply(f).subtract(this.ONE)).multiply(sixf.subtract(this.FIVE));
            }

            return v;
        }

        private Apfloat q(long n)
            throws ApfloatRuntimeException
        {
            Apfloat v;

            if (n == 0)
            {
                v = this.ONE;
            }
            else
            {
                Apfloat f = new Apfloat(n, Apfloat.INFINITE, this.radix);

                v = this.J.multiply(f).multiply(f).multiply(f);
            }

            return v;
        }

        public void r(long n1, long n2, ApfloatHolder T, ApfloatHolder Q, ApfloatHolder P)
            throws ApfloatRuntimeException
        {
            assert (n1 != n2);
            long length = n2 - n1;

            if (length == 1)
            {
                Apfloat p0 = p(n1);

                T.setApfloat(a(n1).multiply(p0));
                Q.setApfloat(q(n1));
                P.setApfloat(p0);
            }
            else
            {
                long nMiddle = (n1 + n2) / 2;
                ApfloatHolder LT = new ApfloatHolder(),
                              LQ = new ApfloatHolder(),
                              LP = new ApfloatHolder();

                r(n1, nMiddle, LT, LQ, LP);
                r(nMiddle, n2, T, Q, P);

                T.setApfloat(Q.getApfloat().multiply(LT.getApfloat()).add(LP.getApfloat().multiply(T.getApfloat())));
                Q.setApfloat(LQ.getApfloat().multiply(Q.getApfloat()));
                P.setApfloat(LP.getApfloat().multiply(P.getApfloat()));
            }
        }

        private final Apfloat A;
        private final Apfloat B;
        private final Apfloat J;
        private final Apfloat ONE;
        private final Apfloat TWO;
        private final Apfloat FIVE;
        private final Apfloat SIX;
        private int radix;
    }

    // Perform actual calculation of pi for radix, and store the result to pre-calulation maps.
    // Uses the Chudnovskys' binary splitting algorithm.
    // Uses previously calculated terms (if such exist) to improve the precision of the calculation.
    private static Apfloat calculatePi(long precision, Integer radixKey)
        throws ApfloatRuntimeException
    {
        int radix = radixKey;
        PiCalculator piCalculator = ApfloatMath.radixPiCalculator.get(radixKey);
        if (piCalculator == null)
        {
            piCalculator = new PiCalculator(radix);
            ApfloatMath.radixPiCalculator.put(radixKey, piCalculator);
        }

        Apfloat LT,
                LQ,
                LP,
                inverseRoot;

        ApfloatHolder RT = new ApfloatHolder(),
                      RQ = new ApfloatHolder(),
                      RP = new ApfloatHolder();

        // Perform the calculation of T, Q and P to infinite precision
        // to make possible to use them later for further calculations

        long neededTerms = (long) ((double) precision * Math.log((double) radix) / 32.65445004177),
             workingPrecision = ApfloatHelper.extendPrecision(precision);   // To avoid cumulative round-off errors

        Long terms = ApfloatMath.radixPiTerms.get(radixKey);
        LT = ApfloatMath.radixPiT.get(radixKey);
        LQ = ApfloatMath.radixPiQ.get(radixKey);
        LP = ApfloatMath.radixPiP.get(radixKey);
        inverseRoot = ApfloatMath.radixPiInverseRoot.get(radixKey);

        if (terms != null && LT != null && LQ != null && LP != null && inverseRoot != null)
        {
            // Some terms have been calculated already previously and cached
            long currentTerms = terms;

            // Check if there actually are more needed terms or if the needed
            // extra precision is just a few digits achievable with current terms
            if (currentTerms != neededTerms + 1)
            {
                piCalculator.r(currentTerms, neededTerms + 1, RT, RQ, RP);

                LT = RQ.getApfloat().multiply(LT).add(LP.multiply(RT.getApfloat()));
                LQ = LQ.multiply(RQ.getApfloat());
                LP = LP.multiply(RP.getApfloat());
            }

            // Improve the inverse root value from the current precision
            inverseRoot = inverseRoot(new Apfloat(640320, workingPrecision, radix), 2, workingPrecision, inverseRoot);
        }
        else
        {
            piCalculator.r(0, neededTerms + 1, RT, RQ, RP);

            LT = RT.getApfloat();
            LQ = RQ.getApfloat();
            LP = RP.getApfloat();

            inverseRoot = inverseRoot(new Apfloat(640320, workingPrecision, radix), 2);
        }

        Apfloat pi = inverseRoot(inverseRoot.multiply(LT), 1).multiply(new Apfloat(53360, Apfloat.INFINITE, radix)).multiply(LQ);

        // Limit precisions to actual after extended working precisions
        inverseRoot = inverseRoot.precision(precision);
        pi = pi.precision(precision);

        // Put the updated values to the caches
        ApfloatMath.radixPiT.put(radixKey, LT);
        ApfloatMath.radixPiQ.put(radixKey, LQ);
        ApfloatMath.radixPiP.put(radixKey, LP);
        ApfloatMath.radixPiInverseRoot.put(radixKey, inverseRoot);
        ApfloatMath.radixPiTerms.put(radixKey, neededTerms + 1);
        ApfloatMath.radixPi.put(radixKey, pi);

        return pi;
    }

    /**
     * Natural logarithm.<p>
     *
     * The logarithm is calculated using the arithmetic-geometric mean.
     * See the Borweins' book for the formula.
     *
     * @param x The argument.
     *
     * @return Natural logarithm of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x <= 0</code>.
     */

    public static Apfloat log(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return log(x, true);
    }

    /**
     * Logarithm in arbitrary base.<p>
     *
     * The logarithm is calculated using the arithmetic-geometric mean.
     * See the Borweins' book for the formula.
     *
     * @param x The argument.
     * @param b The base.
     *
     * @return Base-<code>b</code> logarithm of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x <= 0</code> or <code>b <= 0</code>.
     *
     * @since 1.6
     */

    public static Apfloat log(Apfloat x, Apfloat b)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long targetPrecision = Math.min(x.precision(), b.precision());

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix());
        long xPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(x)); // If the log() argument is close to 1, the result is less accurate
        x = x.precision(Math.min(x.precision(), xPrecision));

        long bPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(b));
        b = b.precision(Math.min(b.precision(), bPrecision));

        return log(x, false).divide(log(b, false));
    }

    private static Apfloat log(Apfloat x, boolean multiplyByPi)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (x.signum() <= 0)
        {
            throw new ArithmeticException("Logarithm of " + (x.signum() == 0 ? "zero" : "negative number; result would be complex"));
        }
        else if (x.equals(Apfloat.ONE))
        {
            return Apfloat.ZERO;
        }

        // Calculate the log using 1 / radix <= x < 1 and the log addition formula
        // because the agm converges badly for big x.

        long targetPrecision = x.precision();
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix());
        long finalPrecision = Util.ifFinite(targetPrecision, targetPrecision - one.equalDigits(x));     // If the argument is close to 1, the result is less accurate

        long originalScale = x.scale();

        x = scale(x, -originalScale);   // Set x's scale to zero

        Apfloat radixPower;
        if (originalScale == 0)
        {
            radixPower = Apfloat.ZERO;
        }
        else
        {
            Apfloat logRadix = ApfloatHelper.extendPrecision(logRadix(targetPrecision, x.radix(), multiplyByPi));
            radixPower = new Apfloat(originalScale, Apfloat.INFINITE, x.radix()).multiply(logRadix);
        }

        return ApfloatHelper.extendPrecision(rawLog(x, multiplyByPi)).add(radixPower).precision(finalPrecision);
    }

    // Raw logarithm, regardless of x
    // Doesn't work for big x, but is faster if used alone for small numbers
    private static Apfloat rawLog(Apfloat x, boolean multiplyByPi)
        throws ApfloatRuntimeException
    {
        assert (x.signum() > 0);                                        // Must be real logarithm

        long targetPrecision = x.precision();

        if (targetPrecision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate logarithm to infinite precision");
        }

        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix());

        final int EXTRA_PRECISION = 25;

        long workingPrecision = ApfloatHelper.extendPrecision(targetPrecision),
             n = targetPrecision / 2 + EXTRA_PRECISION;                 // Very rough estimate

        x = ApfloatHelper.extendPrecision(x, EXTRA_PRECISION);

        Apfloat e = one.precision(workingPrecision);
        e = scale(e, -n);
        x = scale(x, -n);

        Apfloat agme = ApfloatHelper.extendPrecision(agm(one, e)),
                agmex = ApfloatHelper.extendPrecision(agm(one, x));

        Apfloat log = agmex.subtract(agme).precision(workingPrecision);
        if (multiplyByPi)
        {
            Apfloat pi = ApfloatHelper.extendPrecision(pi(targetPrecision, x.radix()));
            log = pi.multiply(log);
        }
        log = log.divide(new Apfloat(2, Apfloat.INFINITE, x.radix()).multiply(agme).multiply(agmex));

        return log.precision(targetPrecision);
    }

    // Get shared radix key for synchronizing getting and calculating the logarithm related constants
    private static Integer getRadixLogKey(Integer radix)
    {
        Integer radixKey = ApfloatMath.radixLogKeys.putIfAbsent(radix, radix);
        if (radixKey == null)
        {
            radixKey = radix;
        }

        return radixKey;
    }

    /**
     * Gets or calculates logarithm of a radix to required precision.
     * The calculated value is stored in a cache for later usage.
     *
     * @param precision The needed precision.
     * @param radix The radix.
     *
     * @return Natural logarithm of <code>radix</code> to the specified precision.
     *
     * @exception java.lang.NumberFormatException If the radix is invalid.
     */

    public static Apfloat logRadix(long precision, int radix)
        throws ApfloatRuntimeException
    {
        return logRadix(precision, radix, true);
    }

    private static Apfloat logRadix(long precision, int radix, boolean multiplyByPi)
        throws ApfloatRuntimeException
    {
        // Get synchronization lock - getting the lock is also synchronized
        Integer radixKey = getRadixLogKey(new Integer(radix));      // Use new Integer since we synchronize on it; Integer.valueOf() could be shared instance

        Apfloat logRadix;

        // Synchronize getting pre-calculated log by radix key
        // - two threads won't try to calculate it at the same time
        // - doesn't block getting it for other radixes
        synchronized (radixKey)
        {
            Map<Integer, Apfloat> cache = (multiplyByPi ? ApfloatMath.radixLogPi : ApfloatMath.radixLog);     // Which cache to use, the one multiplied by pi or not
            logRadix = cache.get(radixKey);

            if (logRadix == null || logRadix.precision() < precision)
            {
                if (multiplyByPi)
                {
                    // We want the multiplied-by-pi version so get first the not-multiplied-by-pi version
                    logRadix = ApfloatHelper.extendPrecision(logRadix(precision, radix, false));
                    Apfloat pi = ApfloatHelper.extendPrecision(pi(precision, radix));
                    logRadix = logRadix.multiply(pi).precision(precision);
                }
                else
                {
                    Apfloat f = new Apfloat("0.1", precision, radix);

                    logRadix = rawLog(f, multiplyByPi).negate();
                }

                cache.put(radixKey, logRadix);
            }
            else
            {
                logRadix = logRadix.precision(precision);
            }
        }

        return logRadix;
    }

    /**
     * Exponent function.
     * Calculated using Newton's iteration for the inverse of logarithm.
     *
     * @param x The argument.
     *
     * @return <code>e<sup>x</sup></code>.
     */

    public static Apfloat exp(Apfloat x)
        throws ApfloatRuntimeException
    {
        int radix = x.radix();

        if (x.signum() == 0)
        {
            return new Apfloat(1, Apfloat.INFINITE, radix);
        }

        long targetPrecision = x.precision(),
             precision,
             doublePrecision = ApfloatHelper.getDoublePrecision(radix);

        // If the argument is close to 0, the result is more accurate
        targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + Math.max(1 - x.scale(), 0));

        if (targetPrecision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate exponent to infinite precision");
        }
        else if (x.compareTo(new Apfloat((double) Long.MAX_VALUE * Math.log((double) radix), doublePrecision, radix)) >= 0)
        {
            throw new OverflowException("Overflow");
        }
        else if (x.compareTo(new Apfloat((double) Long.MIN_VALUE * Math.log((double) radix), doublePrecision, radix)) <= 0)
        {
            // Underflow

            return Apfloat.ZERO;
        }
        else if (x.scale() <= Long.MIN_VALUE / 2 + Apfloat.EXTRA_PRECISION)
        {
            // Taylor series: exp(x) = 1 + x + x^2/2 + ...

            return new Apfloat(1, Apfloat.INFINITE, radix).add(x).precision(Apfloat.INFINITE);
        }

        Apfloat result;

        if (x.scale() < -doublePrecision / 2)
        {
            // Taylor series: exp(x) = 1 + x + x^2/2 + ...

            precision = -2 * x.scale();
            result = new Apfloat(1, precision, radix).add(x);
        }
        else
        {
            // Approximate starting value for iteration

            // An overflow or underflow should not occur
            long scaledXPrecision = Math.max(0, x.scale()) + doublePrecision;
            Apfloat logRadix = log(new Apfloat((double) radix, scaledXPrecision, radix)),
                    scaledX = x.precision(scaledXPrecision).divide(logRadix),
                    integerPart = scaledX.truncate(),
                    fractionalPart = scaledX.frac();

            result = new Apfloat(Math.pow((double) radix, fractionalPart.doubleValue()), doublePrecision, radix);
            result = scale(result, integerPart.longValue());

            if (result.signum() == 0) {
                // Underflow
                return Apfloat.ZERO;
            }

            precision = doublePrecision;
        }

        int iterations = 0;

        // Compute total number of iterations
        for (long maxPrec = precision; maxPrec < targetPrecision; maxPrec <<= 1)
        {
            iterations++;
        }

        int precisingIteration = iterations;

        // Check where the precising iteration should be done
        for (long minPrec = precision; precisingIteration > 0; precisingIteration--, minPrec <<= 1)
        {
            if ((minPrec - Apfloat.EXTRA_PRECISION) << precisingIteration >= targetPrecision)
            {
                break;
            }
        }

        if (iterations > 0)
        {
            // Precalculate the needed values once to the required precision
            logRadix(targetPrecision, radix);
        }

        x = ApfloatHelper.extendPrecision(x);

        // Newton's iteration
        while (iterations-- > 0)
        {
            precision *= 2;
            result = result.precision(Math.min(precision, targetPrecision));

            Apfloat t = log(result);
            t = lastIterationExtendPrecision(iterations, precisingIteration, t);
            t = x.subtract(t);

            if (iterations < precisingIteration)
            {
                t = t.precision(precision / 2);
            }

            result = lastIterationExtendPrecision(iterations, precisingIteration, result);
            result = result.add(result.multiply(t));

            // Precising iteration
            if (iterations == precisingIteration)
            {
                t = log(result);
                t = lastIterationExtendPrecision(iterations, -1, t);

                result = lastIterationExtendPrecision(iterations, -1, result);
                result = result.add(result.multiply(x.subtract(t)));
            }
        }

        return result.precision(targetPrecision);
    }

    /**
     * Arbitrary power. Calculated using <code>log()</code> and <code>exp()</code>.<p>
     *
     * This method doesn't calculate the result properly if <code>x</code> is negative
     * and <code>y</code> is an integer. For that you should use {@link #pow(Apfloat,long)}.
     *
     * @param x The base.
     * @param y The exponent.
     *
     * @return <code>x<sup>y</sup></code>.
     *
     * @exception java.lang.ArithmeticException If both <code>x</code> and <code>y</code> are zero, or <code>x</code> is negative.
     */

    public static Apfloat pow(Apfloat x, Apfloat y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long targetPrecision = Math.min(x.precision(), y.precision());

        Apfloat result = ApfloatHelper.checkPow(x, y, targetPrecision);
        if (result != null)
        {
            return result;
        }

        // Try to precalculate the needed values just once to the required precision,
        // this may not work too efficiently if x is close to 1 or y is close to zero
        logRadix(targetPrecision, x.radix());

        // Limits precision for log() but may be sub-optimal; precision could be limited more
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix());
        targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(x)); // If the log() argument is close to 1, the result is less accurate
        x = x.precision(Math.min(x.precision(), targetPrecision));

        result = log(x);
        long intermediatePrecision = Math.min(y.precision(), result.precision());
        result = ApfloatHelper.extendPrecision(result);
        result = ApfloatHelper.extendPrecision(y).multiply(result);
        result = exp(result.precision(intermediatePrecision));

        return result;
    }

    /**
     * Inverse hyperbolic cosine. Calculated using <code>log()</code>.
     *
     * @param x The argument.
     *
     * @return Inverse hyperbolic cosine of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x < 1</code>.
     */

    public static Apfloat acosh(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix());

        return log(x.add(sqrt(x.multiply(x).subtract(one))));
    }

    /**
     * Inverse hyperbolic sine. Calculated using <code>log()</code>.
     *
     * @param x The argument.
     *
     * @return Inverse hyperbolic sine of <code>x</code>.
     */

    public static Apfloat asinh(Apfloat x)
        throws ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix());

        if (x.signum() >= 0)
        {
            return log(sqrt(x.multiply(x).add(one)).add(x));
        }
        else
        {
            return log(sqrt(x.multiply(x).add(one)).subtract(x)).negate();
        }
    }

    /**
     * Inverse hyperbolic tangent. Calculated using <code>log()</code>.
     *
     * @param x The argument.
     *
     * @return Inverse hyperbolic tangent of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>abs(x) >= 1</code>.
     */

    public static Apfloat atanh(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, x.radix());

        return log(one.add(x).divide(one.subtract(x))).divide(two);
    }

    /**
     * Hyperbolic cosine. Calculated using <code>exp()</code>.
     *
     * @param x The argument.
     *
     * @return Hyperbolic cosine of <code>x</code>.
     */

    public static Apfloat cosh(Apfloat x)
        throws ApfloatRuntimeException
    {
        Apfloat y = exp(x),
                one = new Apfloat(1, Apfloat.INFINITE, x.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, x.radix());

        return y.add(one.divide(y)).divide(two);
    }

    /**
     * Hyperbolic sine. Calculated using <code>exp()</code>.
     *
     * @param x The argument.
     *
     * @return Hyperbolic sine of <code>x</code>.
     */

    public static Apfloat sinh(Apfloat x)
        throws ApfloatRuntimeException
    {
        Apfloat y = exp(x),
                one = new Apfloat(1, Apfloat.INFINITE, x.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, x.radix());

        return y.subtract(one.divide(y)).divide(two);
    }

    /**
     * Hyperbolic tangent. Calculated using <code>exp()</code>.
     *
     * @param x The argument.
     *
     * @return Hyperbolic tangent of <code>x</code>.
     */

    public static Apfloat tanh(Apfloat x)
        throws ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, x.radix()),
                y = exp(two.multiply(abs(x)));

        y = y.subtract(one).divide(y.add(one));
        return (x.signum() < 0 ? y.negate() : y);
    }

    /**
     * Inverse cosine. Calculated using complex functions.
     *
     * @param x The argument.
     *
     * @return Inverse cosine of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>abs(x) > 1</code>.
     */

    public static Apfloat acos(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);

        return ApcomplexMath.log(x.add(i.multiply(sqrt(one.subtract(x.multiply(x)))))).imag();
    }

    /**
     * Inverse sine. Calculated using complex functions.
     *
     * @param x The argument.
     *
     * @return Inverse sine of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>abs(x) > 1</code>.
     */

    public static Apfloat asin(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);

        return ApcomplexMath.log(sqrt(one.subtract(x.multiply(x))).subtract(i.multiply(x))).imag().negate();
    }

    /**
     * Inverse tangent. Calculated using complex functions.
     *
     * @param x The argument.
     *
     * @return Inverse tangent of <code>x</code>.
     */

    public static Apfloat atan(Apfloat x)
        throws ApfloatRuntimeException
    {
        Apfloat one = new Apfloat(1, Apfloat.INFINITE, x.radix()),
                two = new Apfloat(2, Apfloat.INFINITE, x.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);

        return ApcomplexMath.log(i.subtract(x).divide(i.add(x))).imag().divide(two);
    }

    /**
     * Converts cartesian coordinates to polar coordinates. Calculated using complex functions.<p>
     *
     * Computes the phase angle by computing an arc tangent of <code>x/y</code> in the range of -&pi; < angle <= &pi;.
     *
     * @param x The argument.
     * @param y The argument.
     *
     * @return The angle of the point <code>(y, x)</code> in the plane.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> and <code>y</code> are both zero.
     */

    public static Apfloat atan2(Apfloat x, Apfloat y)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (y.signum() == 0)
        {
            if (x.signum() == 0)
            {
                throw new ArithmeticException("Angle of (0, 0)");
            }

            Apfloat pi = pi(x.precision(), x.radix()),
                    two = new Apfloat(2, Apfloat.INFINITE, x.radix());

            return new Apfloat(x.signum(), Apfloat.INFINITE, x.radix()).multiply(pi).divide(two);
        }
        else if (x.signum() == 0)
        {
            if (y.signum() > 0)
            {
                return Apfloat.ZERO;
            }

            return pi(y.precision(), y.radix());
        }
        else if (Math.min(x.precision(), y.precision()) == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate atan2 to infinite precision");
        }
        else
        {
            long maxScale = Math.max(x.scale(), y.scale());

            x = scale(x, -maxScale);    // Now neither x nor y is zero
            y = scale(y, -maxScale);

            return ApcomplexMath.log(new Apcomplex(y, x)).imag();
        }
    }

    /**
     * Cosine. Calculated using complex functions.
     *
     * @param x The argument (in radians).
     *
     * @return Cosine of <code>x</code>.
     */

    public static Apfloat cos(Apfloat x)
        throws ApfloatRuntimeException
    {
        return ApcomplexMath.exp(new Apcomplex(Apfloat.ZERO, x)).real();
    }

    /**
     * Sine. Calculated using complex functions.
     *
     * @param x The argument (in radians).
     *
     * @return Sine of <code>x</code>.
     */

    public static Apfloat sin(Apfloat x)
        throws ApfloatRuntimeException
    {
        return ApcomplexMath.exp(new Apcomplex(Apfloat.ZERO, x)).imag();
    }

    /**
     * Tangent. Calculated using complex functions.
     *
     * @param x The argument (in radians).
     *
     * @return Tangent of <code>x</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> is &pi;/2 + n &pi; where n is an integer.
     */

    public static Apfloat tan(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        Apcomplex w = ApcomplexMath.exp(new Apcomplex(Apfloat.ZERO, x));

        return w.imag().divide(w.real());
    }

    /**
     * Lambert W function. The W function gives the solution to the equation
     * <code>W e<sup>W</sup> = x</code>. Also known as the product logarithm.<p>
     *
     * This function only gives the solution to the principal branch, W<sub>0</sub>.
     * For the real-valued W<sub>-1</sub> branch, use {@link ApcomplexMath#w(Apcomplex,long)}.
     *
     * @param x The argument.
     *
     * @return <code>W<sub>0</sub>(x)</code>.
     *
     * @exception java.lang.ArithmeticException If <code>x</code> is less than -1/e.
     *
     * @since 1.8.0
     */

    public static Apfloat w(Apfloat x)
        throws ArithmeticException, ApfloatRuntimeException
    {
        return LambertWHelper.w(x);
    }

    /**
     * Converts an angle measured in radians to degrees.<p>
     *
     * @param x The angle, in radians.
     *
     * @return The angle in degrees.
     *
     * @since 1.8.0
     */

    public static Apfloat toDegrees(Apfloat x)
        throws ApfloatRuntimeException
    {
        return x.multiply(new Apfloat(180, Apfloat.INFINITE, x.radix())).divide(pi(x.precision(), x.radix()));
    }

    /**
     * Converts an angle measured in degrees to radians.<p>
     *
     * @param x The angle, in degrees.
     *
     * @return The angle in radians.
     *
     * @since 1.8.0
     */

    public static Apfloat toRadians(Apfloat x)
        throws ApfloatRuntimeException
    {
        return x.divide(new Apfloat(180, Apfloat.INFINITE, x.radix())).multiply(pi(x.precision(), x.radix()));
    }

    /**
     * Product of numbers.
     * The precision used in the multiplications is only
     * what is needed for the end result. This method may
     * perform significantly better than simply multiplying
     * the numbers sequentially.<p>
     *
     * If there are no arguments, the return value is <code>1</code>.
     *
     * @param x The argument(s).
     *
     * @return The product of the given numbers.
     *
     * @since 1.3
     */

    public static Apfloat product(Apfloat... x)
        throws ApfloatRuntimeException
    {
        if (x.length == 0)
        {
            return Apfloat.ONE;
        }

        // Determine working precision
        long maxPrec = Apfloat.INFINITE;
        for (int i = 0; i < x.length; i++)
        {
            if (x[i].signum() == 0)
            {
                return Apfloat.ZERO;
            }
            maxPrec = Math.min(maxPrec, x[i].precision());
        }

        // Do not use x.clone() as the array might be of some subclass type, resulting in ArrayStoreException later
        Apfloat[] tmp = new Apfloat[x.length];

        // Add sqrt length digits for round-off errors
        long extraPrec = (long) Math.sqrt((double) x.length),
             destPrec = ApfloatHelper.extendPrecision(maxPrec, extraPrec);
        for (int i = 0; i < x.length; i++)
        {
            tmp[i] = x[i].precision(destPrec);
        }
        x = tmp;

        // Create a heap, ordered by size
        final Queue<Apfloat> heap = new PriorityQueue<Apfloat>(x.length, new Comparator<Apfloat>()
        {
            public int compare(Apfloat x, Apfloat y)
            {
                long xSize = x.size(),
                     ySize = y.size();
                return (xSize < ySize ? -1 : (xSize > ySize ? 1 : 0));
            }
        });

        // Perform the multiplications in parallel
        ParallelHelper.ProductKernel<Apfloat> kernel = new ParallelHelper.ProductKernel<Apfloat>()
        {
            public void run(Queue<Apfloat> heap)
            {
                Apfloat a = heap.remove();
                Apfloat b = heap.remove();
                Apfloat c = a.multiply(b);
                heap.add(c);
            }
        };
        ParallelHelper.parallelProduct(x, heap, kernel);

        return heap.remove().precision(maxPrec);
    }

    /**
     * Sum of numbers.
     * The precision used in the additions is only
     * what is needed for the end result. This method may
     * perform significantly better than simply adding
     * the numbers sequentially.<p>
     *
     * If there are no arguments, the return value is <code>0</code>.
     *
     * @param x The argument(s).
     *
     * @return The sum of the given numbers.
     *
     * @since 1.3
     */

    public static Apfloat sum(Apfloat... x)
        throws ApfloatRuntimeException
    {
        if (x.length == 0)
        {
            return Apfloat.ZERO;
        }

        // Determine working precision
        long maxScale = -Apfloat.INFINITE,
             maxPrec = Apfloat.INFINITE;
        for (int i = 0; i < x.length; i++)
        {
            long oldScale = maxScale,
                 oldPrec = maxPrec,
                 newScale = x[i].scale(),
                 newPrec = x[i].precision();
            maxScale = Math.max(oldScale, newScale);
            long oldScaleDiff = (maxScale - oldScale < 0 ? Apfloat.INFINITE : maxScale - oldScale),
                 newScaleDiff = (maxScale - newScale < 0 ? Apfloat.INFINITE : maxScale - newScale);
            maxPrec = Math.min(Util.ifFinite(oldPrec, oldPrec + oldScaleDiff),
                               Util.ifFinite(newPrec, newPrec + newScaleDiff));
        }

        // Do not use x.clone() as the array might be of some subclass type, resulting in ArrayStoreException later
        Apfloat[] tmp = new Apfloat[x.length];

        for (int i = 0; i < x.length; i++)
        {
            long scale = x[i].scale(),
                 scaleDiff = (maxScale - scale < 0 ? Apfloat.INFINITE : maxScale - scale),
                 destPrec = (maxPrec - scaleDiff <= 0 ? 0 : Util.ifFinite(maxPrec, maxPrec - scaleDiff));
            if (destPrec > 0)
            {
                tmp[i] = x[i].precision(destPrec);
            }
            else
            {
                tmp[i] = Apfloat.ZERO;
            }
        }
        x = tmp;

        // Sort by scale (might be mostly equal to size)
        Comparator<Apfloat> comparator = new Comparator<Apfloat>()
        {
            public int compare(Apfloat x, Apfloat y)
            {
                long xScale = x.scale(),
                     yScale = y.scale();
                return (xScale < yScale ? -1 : (xScale > yScale ? 1 : 0));
            }
        };
        Arrays.sort(x, comparator);

        // The list of numbers to be added
        List<Apfloat> list;

        // If there are lots of numbers then use a fully parallel algorithm, for small sums the overhead is not worth it
        // Also note that memory (or worse - disk) bandwidth is likely a bottleneck, preventing efficient parallelization
        if (x.length >= 1000)
        {
            // Only add in parallel numbers, which are (probably) below the memory threshold in size
            ApfloatContext ctx = ApfloatContext.getContext();
            long maxSize = (long) (ctx.getMemoryThreshold() * 5.0 / Math.log((double) ctx.getDefaultRadix()));

            // Create a queue of small numbers where the parallel algorithm can add and remove elements
            final Queue<Apfloat> queue = new ConcurrentLinkedQueue<Apfloat>();

            // The large numbers go to the list where they will be added using a single thread algorithm
            list = new ArrayList<Apfloat>();

            // Put all the numbers to either the parallel queue or single thread list
            for (Apfloat a : x)
            {
                (a.size() <= maxSize ? queue : list).add(a);
            }

            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    // Add numbers as long as there are any left in the queue
                    Apfloat s = Apfloat.ZERO,
                            a;
                    while ((a = queue.poll()) != null)
                    {
                        s = s.add(a);
                    }
                    // Finally, put the sub-sum back in the queue
                    queue.add(s);
                }
            };

            // Run the runnable in multiple threads
            ParallelHelper.runParallel(runnable);

            // Put the remaining sub-sums to the list to be added once more
            list.addAll(queue);

            // Sort again the list as it has been now mixed up
            Collections.sort(list, comparator);
        }
        else
        {
            // Single thread case - just add all the numbers
            list = Arrays.asList(x);
        }

        // Add the remaining elements in the queue (all, for the single-thread case, and sub-sums for the parallel case)
        Apfloat s = Apfloat.ZERO;
        for (Apfloat a : list)
        {
            s = s.add(a);
        }

        return s;
    }

    // Extend the precision on last iteration
    private static Apfloat lastIterationExtendPrecision(int iterations, int precisingIteration, Apfloat x)
        throws ApfloatRuntimeException
    {
        return (iterations == 0 && precisingIteration != 0 ? ApfloatHelper.extendPrecision(x) : x);
    }

    static Apfloat factorial(long n, long precision)
        throws ArithmeticException, NumberFormatException, ApfloatRuntimeException
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        int radix = ctx.getDefaultRadix();

        return factorial(n, precision, radix);
    }

    static Apfloat factorial(long n, long precision, int radix)
        throws ArithmeticException, NumberFormatException, ApfloatRuntimeException
    {
        if (n < 0)
        {
            throw new ArithmeticException("Factorial of negative number");
        }
        else if (n < 2)
        {
            return new Apfloat(1, precision, radix);
        }

        long targetPrecision = precision;
        precision = ApfloatHelper.extendPrecision(precision);

        // Thanks to Peter Luschny for the improved algorithm.
        // The idea is to split the factorial to two parts:
        // a product of odd numbers, and a power of two.
        // This saves some operations, as squaring is more
        // efficient than multiplication, in the power of two.
        // For any n, factorial(n) = oddProduct(n) * factorial(m) * 2^m,
        // where m = n >>> 1, which gives the following algorithm.
        Apfloat oddProduct = new Apfloat(1, precision, radix),
                factorialProduct = oddProduct;
        long exponentOfTwo = 0;

        for (int i = 62 - Long.numberOfLeadingZeros(n); i >= 0; i--)
        {
            long m = n >>> i,
                 k = m >>> 1;
            exponentOfTwo += k;
            oddProduct = oddProduct.multiply(oddProduct(k + 1, m, precision, radix));
            factorialProduct = factorialProduct.multiply(oddProduct);
        }

        return factorialProduct.multiply(pow(new Apfloat(2, precision, radix), exponentOfTwo)).precision(targetPrecision);
    }

    private static Apfloat oddProduct(long n, long m, long precision, int radix)
        throws ApfloatRuntimeException
    {
        n = n | 1;       // Round n up to the next odd number
        m = (m - 1) | 1; // Round m down to the next odd number

        if (n > m)
        {
            return new Apfloat(1, precision, radix);
        }
        else if (n == m)
        {
            return new Apfloat(n, precision, radix);
        }
        else
        {
            long k = (n + m) >>> 1;
            return oddProduct(n, k, precision, radix).multiply(oddProduct(k + 1, m, precision, radix));
        }
    }

    // Clean up static maps at shutdown, to allow garbage collecting temporary files
    static void cleanUp()
    {
        ApfloatMath.radixPi = SHUTDOWN_MAP;
        ApfloatMath.radixPiT = SHUTDOWN_MAP;
        ApfloatMath.radixPiQ = SHUTDOWN_MAP;
        ApfloatMath.radixPiP = SHUTDOWN_MAP;
        ApfloatMath.radixPiInverseRoot = SHUTDOWN_MAP;
        ApfloatMath.radixLog = SHUTDOWN_MAP;
        ApfloatMath.radixLogPi = SHUTDOWN_MAP;
    }

    // Map that always throws ApfloatRuntimeException, to be used after clean-up has been initiated
    private static final Map<Integer, Apfloat> SHUTDOWN_MAP = new ShutdownMap<Integer, Apfloat>();

    // Synchronization keys for pi calculation
    private static ConcurrentMap<Integer, Integer> radixPiKeys = new ConcurrentHashMap<Integer, Integer>();

    // Shared cached values related to pi for different radixes
    private static Map<Integer, Apfloat> radixPi = new ConcurrentSoftHashMap<Integer, Apfloat>();
    private static Map<Integer, PiCalculator> radixPiCalculator = new Hashtable<Integer, PiCalculator>();
    private static Map<Integer, Apfloat> radixPiT = new ConcurrentSoftHashMap<Integer, Apfloat>();
    private static Map<Integer, Apfloat> radixPiQ = new ConcurrentSoftHashMap<Integer, Apfloat>();
    private static Map<Integer, Apfloat> radixPiP = new ConcurrentSoftHashMap<Integer, Apfloat>();
    private static Map<Integer, Apfloat> radixPiInverseRoot = new ConcurrentSoftHashMap<Integer, Apfloat>();
    private static Map<Integer, Long> radixPiTerms = new Hashtable<Integer, Long>();

    // Synchronization keys for logarithm calculation
    private static ConcurrentMap<Integer, Integer> radixLogKeys = new ConcurrentHashMap<Integer, Integer>();

    // Shared cached values related to logarithm for different radixes
    private static Map<Integer, Apfloat> radixLog = new ConcurrentHashMap<Integer, Apfloat>();
    private static Map<Integer, Apfloat> radixLogPi = new ConcurrentHashMap<Integer, Apfloat>();
}
