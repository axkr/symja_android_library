package org.matheclipse.external.apfloat;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;

/**
 * Mathematical constants which <code>org.apfloat.ApfloatMath</code> calculates slowly.<p>
 *
 * <code>ApfloatMath.catalan()</code> takes a quarter of a second for 140 digits and 14 seconds
 * for 1000 digits; the series of this class takes half a millisecond and 8 milliseconds.
 */

public class ApfloatConstantMath
{
    private ApfloatConstantMath()
    {
    }

    // The most precise value of Catalan's constant calculated so far, indexed by the radix
    private static final Apfloat[] CATALAN = new Apfloat[Character.MAX_RADIX + 1];

    // Above this precision the value is calculated each time, so that one huge value isn't kept forever
    private static final long MAX_CACHED_PRECISION = 20000;

    /**
     * Catalan's constant <i>G</i> = &sum;<sub>k&ge;0</sub> (-1)<sup>k</sup> / (2<i>k</i> + 1)<sup>2</sup>,
     * from the series<p>
     *
     * <i>G</i> = &pi;/8 log(2 + &radic;3) + 3/8 &sum;<sub>n&ge;0</sub> (<i>n</i>!)<sup>2</sup> / ((2<i>n</i>)! (2<i>n</i> + 1)<sup>2</sup>),<p>
     *
     * whose terms decrease by a factor of 4. The most precise value up to 20000 digits is kept
     * for each radix.
     *
     * @param precision The precision.
     * @param radix The radix.
     *
     * @return <i>G</i>
     *
     * @throws InfiniteExpansionException If the precision is infinite.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Catalan%27s_constant#Series">Catalan's constant, series</a>
     */

    public static Apfloat catalan(long precision, int radix)
        throws IllegalArgumentException, ApfloatRuntimeException
    {
        if (precision <= 0)
        {
            throw new IllegalArgumentException("Precision " + precision + " is not positive");
        }
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Catalan's constant to infinite precision");
        }
        synchronized (CATALAN)
        {
            Apfloat cached = CATALAN[radix];
            if (cached != null && cached.precision() >= precision)
            {
                return cached.precision(precision);
            }
        }
        Apfloat value = catalanSeries(precision, radix);
        synchronized (CATALAN)
        {
            Apfloat cached = CATALAN[radix];
            if (value.precision() <= MAX_CACHED_PRECISION && (cached == null || cached.precision() < value.precision()))
            {
                CATALAN[radix] = value;
            }
        }
        return value;
    }

    private static Apfloat catalanSeries(long precision, int radix)
        throws ApfloatRuntimeException
    {
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                term = one,
                sum = one;
        // term n is (n!)^2 / (2n)!, less than 4^-n
        long terms = (long) Math.ceil(workingPrecision * Math.log(radix) / Math.log(4)) + 2;
        for (long n = 0; n < terms; n++)
        {
            ApfloatHelperShim.checkInterrupted();
            term = term.multiply(new Apint(n + 1, radix)).divide(new Apint(4 * n + 2, radix));
            Apint odd = new Apint(2 * n + 3, radix);
            sum = sum.add(term.divide(odd.multiply(odd)));
        }
        Apfloat three = new Apfloat(3, workingPrecision, radix),
                log = ApfloatMath.log(new Apfloat(2, workingPrecision, radix).add(ApfloatMath.sqrt(three)));
        Apfloat result = ApfloatMath.pi(workingPrecision, radix).multiply(log).add(three.multiply(sum)).divide(new Apint(8, radix));
        return result.precision(precision);
    }
}
