package org.apfloat;

import java.math.RoundingMode;

/**
 * Helper class for rounding functions.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

class RoundingHelper
{
    private RoundingHelper()
    {
    }

    public static Apfloat round(Apfloat x, long precision, RoundingMode roundingMode)
        throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException
    {
        if (precision <= 0)
        {
            throw new IllegalArgumentException("Invalid precision: " + precision);
        }
        if (x.signum() == 0 || precision == Apfloat.INFINITE)
        {
            return x;
        }

        // Can't optimize by checking x.size() <= precision as the number might have hidden residual digits
        long scale = x.scale();
        boolean overflow = (scale - precision >= scale);
        if (overflow)
        {
            // Avoid overflow of longs, do scaling in two parts
            x = x.scale(-scale);
            x = x.scale(precision);
        }
        else
        {
            x = x.scale(precision - scale);
        }
        switch (roundingMode)
        {
            case UP:
                x = x.roundAway();
                break;
            case DOWN:
                x = x.truncate();
                break;
            case CEILING:
                x = x.ceil();
                break;
            case FLOOR:
                x = x.floor();
                break;
            case HALF_UP:
            case HALF_DOWN:
            case HALF_EVEN:
                Apint whole = x.truncate();
                Apfloat fraction = x.frac().abs();
                int comparison = fraction.compareToHalf();
                if (comparison < 0 || comparison == 0 && roundingMode.equals(RoundingMode.HALF_DOWN))
                {
                    x = x.truncate();
                }
                else if (comparison > 0 || comparison == 0 && roundingMode.equals(RoundingMode.HALF_UP))
                {
                    x = x.roundAway();
                }
                else
                {
                    x = (isEven(whole) ? x.truncate() : x.roundAway());
                }
                break;
            case UNNECESSARY:
                if (x.size() > x.scale())
                {
                    throw new ArithmeticException("Rounding necessary");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown rounding mode: " + roundingMode);
        }
        if (overflow)
        {
            // Avoid overflow of longs, do scaling in two parts
            x = ApfloatMath.scale(x, -precision);
            x = ApfloatMath.scale(x, scale);
        }
        else
        {
            x = ApfloatMath.scale(x, scale - precision);
        }
        return x.precision(precision);
    }

    public static int compareToHalf(Apfloat x)
    {
        int comparison;
        if (x.radix() % 2 == 0)
        {
            comparison = x.compareTo(new Apfloat("0." + Character.forDigit(x.radix() / 2, x.radix()), Apfloat.INFINITE, x.radix()));
        }
        else
        {
            // In an odd radix, half has an infinite digit expansion
            Apint one = new Apint(1, x.radix());
            Apint two = new Apint(2, x.radix());
            comparison = x.precision(Apfloat.INFINITE).multiply(two).compareTo(one);
        }
        return comparison;
    }

    public static int compareToHalf(Aprational x)
    {
        Aprational half = new Aprational(new Apint(1, x.radix()), new Apint(2, x.radix()));
        int comparison = x.compareTo(half);
        return comparison;
    }

    private static boolean isEven(Apint x)
    {
        // This could be further optimized if the radix is even
        // Note that any fractional part can never be exactly half when the radix is odd as a float, only as a rational
        Apint two = new Apint(2, x.radix());
        return (x.mod(two).signum() == 0);
    }
}
