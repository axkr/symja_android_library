package org.matheclipse.external.apfloat;

import java.util.function.BinaryOperator;

import org.apfloat.Apcomplex;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.LossOfPrecisionException;

/**
 * Evaluates a function with as many extra digits as it takes for the result to come out the
 * same twice.<p>
 *
 * A series whose terms cancel, or a recurrence which loses digits, gives a result with fewer
 * correct digits than the precision it was summed at. The number of digits which are lost is
 * rarely known in advance, but it shows: the result changes when the working precision does.
 */

class GuardDigitsHelper
{
    private GuardDigitsHelper()
    {
    }

    // Are the two values the same to the precision, with a digit to spare?
    static boolean agree(Apcomplex z, Apcomplex w, long precision)
    {
        boolean zeroZ = (z.real().signum() == 0 && z.imag().signum() == 0),
                zeroW = (w.real().signum() == 0 && w.imag().signum() == 0);
        if (z.precision() < precision || w.precision() < precision)
        {
            return zeroZ && zeroW;
        }
        Apcomplex difference = z.subtract(w);
        return (difference.real().signum() == 0 && difference.imag().signum() == 0) ||
               difference.scale() < Math.max(z.scale(), w.scale()) - precision;
    }

    /**
     * The function value to the precision.
     *
     * @param function The function, which is called with its arguments padded to the working precision.
     * @param x The first argument.
     * @param y The second argument.
     * @param precision The precision of the result.
     * @param extraPrecision The extra digits of the first attempt, which are doubled with every attempt.
     * @param maxExtraPrecision The largest number of extra digits which is tried.
     *
     * @return The function value, when two successive working precisions give the same one.
     *
     * @throws LossOfPrecisionException If the results don't agree up to the largest number of extra digits.
     */

    static Apcomplex evaluate(BinaryOperator<Apcomplex> function, Apcomplex x, Apcomplex y, long precision, long extraPrecision, long maxExtraPrecision)
        throws ApfloatRuntimeException
    {
        Apcomplex previous = null;
        for (; extraPrecision <= maxExtraPrecision; extraPrecision *= 2)
        {
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision, extraPrecision);
            Apcomplex result;
            try
            {
                result = function.apply(ApfloatHelperShim.ensurePrecision(x, workingPrecision),
                                        ApfloatHelperShim.ensurePrecision(y, workingPrecision));
            }
            catch (LossOfPrecisionException lope)
            {
                // Not enough digits yet
                previous = null;
                continue;
            }
            if (previous != null && agree(previous, result, precision))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
        }
        throw new LossOfPrecisionException("Complete loss of accurate digits");
    }
}
