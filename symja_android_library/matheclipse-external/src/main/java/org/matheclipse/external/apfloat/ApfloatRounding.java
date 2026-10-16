package org.matheclipse.external.apfloat;

import java.math.RoundingMode;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;

/**
 * Rounding of a number to its precision with the digits stored beyond the precision.<p>
 *
 * An apfloat keeps the digits an operation computed beyond its precision: the precision is a
 * mark, and {@link Apfloat#precision(long)} with a smaller precision does not remove digits. The
 * <code>FixedPrecisionApfloatHelper</code> limits every result that way, so a product of two
 * numbers with 30 digits carries a few correct digits beyond the 30th, and the next operation
 * uses them like guard digits. {@link Apfloat#toString()} and {@link Apfloat#writeTo} print the
 * digits up to the precision and drop the rest: the last printed digit is cut off, not rounded.
 * For example the product of pi and e, each rounded to 30 digits, is
 * 8.53973422267356706546355086953956..., and prints as <code>8.53973422267356706546355086953</code>.<p>
 *
 * {@link #roundToPrecision(Apfloat)} rounds a number to its precision with these stored digits,
 * for printing. The number itself is left as it is: rounding the result of every operation to its
 * precision would drop the stored digits, and an evaluation of many steps would lose accuracy.
 */

public class ApfloatRounding
{
    /**
     * The count of the digits beyond the precision used for the rounding.
     */

    public static final long GUARD_DIGITS = 3;

    private ApfloatRounding()
    {
    }

    /**
     * The number rounded half to even to its precision, with up to {@link #GUARD_DIGITS} digits
     * stored beyond the precision.
     *
     * @param x The number.
     *
     * @return <code>x</code> rounded to <code>x.precision()</code> digits; <code>x</code> itself
     *         if it is zero, if its precision is infinite, or if it has no digits beyond the
     *         precision.
     *
     * @throws ApfloatRuntimeException
     */

    public static Apfloat roundToPrecision(Apfloat x)
        throws ApfloatRuntimeException
    {
        long precision = x.precision();
        if (x.signum() == 0 || precision == Apfloat.INFINITE)
        {
            return x;
        }
        // size() counts the digits up to the precision only
        Apfloat extended = x.precision(precision + GUARD_DIGITS);
        if (extended.size() <= precision)
        {
            return x;
        }
        return ApfloatMath.roundToPrecision(extended, precision, RoundingMode.HALF_EVEN);
    }

    /**
     * The real and imaginary part of the number rounded half to even to their precisions, with
     * {@link #roundToPrecision(Apfloat)}.
     *
     * @param z The number.
     *
     * @return <code>z</code> with both parts rounded.
     *
     * @throws ApfloatRuntimeException
     */

    public static Apcomplex roundToPrecision(Apcomplex z)
        throws ApfloatRuntimeException
    {
        if (z instanceof Apfloat)
        {
            return roundToPrecision((Apfloat) z);
        }
        Apfloat real = roundToPrecision(z.real()),
                imag = roundToPrecision(z.imag());
        return (real == z.real() && imag == z.imag() ? z : new Apcomplex(real, imag));
    }
}
