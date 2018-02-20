package org.apfloat;

import java.math.RoundingMode;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Various mathematical functions for arbitrary precision rational numbers.
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class AprationalMath
{
    private AprationalMath()
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

    public static Aprational pow(Aprational x, long n)
        throws ArithmeticException, ApfloatRuntimeException
    {
        if (n == 0)
        {
            if (x.signum() == 0)
            {
                throw new ArithmeticException("Zero to power zero");
            }

            return new Apint(1, x.radix());
        }
        else if (n < 0)
        {
            x = Aprational.ONE.divide(x);
            n = -n;
        }

        // Algorithm improvements by Bernd Kellner
        int b2pow = 0;

        while ((n & 1) == 0)
        {
            b2pow++;
            n >>>= 1;
        }

        Aprational r = x;

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

        return r;
    }

    /**
     * Returns an aprational whose value is <code>-x</code>.
     *
     * @deprecated Use {@link Aprational#negate()}.
     *
     * @param x The argument.
     *
     * @return <code>-x</code>.
     */

    @Deprecated
    public static Aprational negate(Aprational x)
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

    public static Aprational abs(Aprational x)
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

    public static Aprational copySign(Aprational x, Aprational y)
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
     * Note that this method is prone to intermediate overflow errors.
     * Also, scaling by a very large negative number won't result in an
     * underflow and a zero result, but an overflow of the denominator
     * and an exception thrown.
     *
     * @param x The argument.
     * @param scale The scaling factor.
     *
     * @return <code>x * x.radix()<sup>scale</sup></code>.
     */

    public static Aprational scale(Aprational x, long scale)
        throws ApfloatRuntimeException
    {
        if (scale >= 0)
        {
            return new Aprational(ApintMath.scale(x.numerator(), scale), x.denominator());
        }
        else if (scale == 0x8000000000000000L)
        {
            Apint scaler = ApintMath.pow(new Apint(x.radix(), x.radix()), 0x4000000000000000L);
            return new Aprational(x.numerator(), x.denominator().multiply(scaler)).divide(scaler);
        }
        else
        {
            return new Aprational(x.numerator(), ApintMath.scale(x.denominator(), -scale));
        }
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

    public static Apfloat round(Aprational x, long precision, RoundingMode roundingMode)
        throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException
    {
        return RoundingHelper.round(x, precision, roundingMode);
    }

    /**
     * Product of numbers.
     * This method may perform significantly better
     * than simply multiplying the numbers sequentially.<p>
     *
     * If there are no arguments, the return value is <code>1</code>.
     *
     * @param x The argument(s).
     *
     * @return The product of the given numbers.
     *
     * @since 1.3
     */

    public static Aprational product(Aprational... x)
        throws ApfloatRuntimeException
    {
        if (x.length == 0)
        {
            return Aprational.ONE;
        }

        Apint[] n = new Apint[x.length],
                m = new Apint[x.length];
        for (int i = 0; i < x.length; i++)
        {
            if (x[i].signum() == 0)
            {
                return Aprational.ZERO;
            }
            n[i] = x[i].numerator();
            m[i] = x[i].denominator();
        }
        return new Aprational(ApintMath.product(n), ApintMath.product(m));
    }

    /**
     * Sum of numbers.
     * This method may perform significantly better
     * than simply adding the numbers sequentially.<p>
     *
     * If there are no arguments, the return value is <code>0</code>.
     *
     * @param x The argument(s).
     *
     * @return The sum of the given numbers.
     *
     * @since 1.3
     */

    public static Aprational sum(Aprational... x)
        throws ApfloatRuntimeException
    {
        if (x.length == 0)
        {
            return Aprational.ZERO;
        }

        // Sort by size
        x = x.clone();

        Arrays.sort(x, new Comparator<Aprational>()
        {
            public int compare(Aprational x, Aprational y)
            {
                long xSize = ApfloatHelper.size(x),
                     ySize = ApfloatHelper.size(y);
                return (xSize < ySize ? -1 : (xSize > ySize ? 1 : 0));
            }
        });

        // Recursively add
        return recursiveSum(x, 0, x.length - 1);
    }

    private static Aprational recursiveSum(Aprational[] x, int n, int m)
        throws ApfloatRuntimeException
    {
        if (n == m)
        {
            return x[n];
        }
        else
        {
            int k = (n + m) >>> 1;
            return recursiveSum(x, n, k).add(recursiveSum(x, k + 1, m));
        }
    }
}
