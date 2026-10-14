package org.matheclipse.external.apfloat;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.InfiniteExpansionException;

/**
 * Dawson's integral <i>F</i>(<i>x</i>) = e<sup>-<i>x</i><sup>2</sup></sup> &int;<sub>0</sub><sup><i>x</i></sup> e<sup><i>t</i><sup>2</sup></sup> d<i>t</i>
 * of a real argument.<p>
 *
 * For a large argument the product of the small and the large exponential loses digits, about as
 * many as the square of the argument has before its radix point. The asymptotic series has none
 * to lose.
 *
 * @see <a href="https://dlmf.nist.gov/7.12#E1">DLMF 7.12</a>
 */

public class ApfloatDawsonMath
{
    private ApfloatDawsonMath()
    {
    }

    /**
     * Dawson's integral.
     *
     * @param x The argument.
     *
     * @return <i>F</i>(<i>x</i>)
     *
     * @exception InfiniteExpansionException If the argument has infinite precision and is not zero.
     */

    public static Apfloat dawsonF(Apfloat x)
        throws ApfloatRuntimeException
    {
        if (x.signum() == 0)
        {
            return x;
        }
        long precision = x.precision();
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Dawson's integral to infinite precision", "dawsonF.infinitePrecision");
        }
        int radix = x.radix();
        long workingPrecision = ApfloatHelperShim.extendPrecision(precision);
        // The smallest term of the asymptotic series is about exp(-x^2)
        double digits = (workingPrecision + 5) * Math.log(radix);
        boolean asymptotic = x.scale() > 4;
        if (!asymptotic)
        {
            double value = x.doubleValue();
            asymptotic = value * value > digits + 10.0;
        }
        if (!asymptotic)
        {
            long scale = Math.max(0, x.scale());
            Apfloat result = ApfloatMath.dawsonF(ApfloatHelperShim.ensurePrecision(x, ApfloatHelperShim.extendPrecision(workingPrecision, 2 * scale)));
            return ApfloatHelperShim.limitPrecision(result, precision);
        }
        x = ApfloatHelperShim.ensurePrecision(x, workingPrecision);
        // 1 / (2 x) sum (2 k - 1)!! / (2 x^2)^k
        Apfloat one = new Apfloat(1, workingPrecision, radix),
                twoSquare = x.multiply(x).multiply(new Apfloat(2, workingPrecision, radix)),
                term = one,
                sum = one;
        for (long k = 1; ; k++)
        {
            term = term.multiply(new Apfloat(2 * k - 1, workingPrecision, radix)).divide(twoSquare);
            if (term.signum() == 0 || term.scale() < -workingPrecision)
            {
                break;
            }
            sum = sum.add(term);
        }
        Apfloat result = sum.divide(x.multiply(new Apfloat(2, workingPrecision, radix)));
        return ApfloatHelperShim.limitPrecision(result, precision);
    }
}
