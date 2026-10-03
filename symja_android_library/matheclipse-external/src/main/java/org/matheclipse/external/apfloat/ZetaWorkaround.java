package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.spi.Util;

/**
 * Riemann zeta function next to its pole at <i>s</i> = 1.<p>
 *
 * <code>org.apfloat.ZetaHelper.alternatingSum()</code> estimates the number of terms from
 * |1 - 2<sup>1 - <i>s</i></sup>|, which it computes with <i>s</i> rounded to the precision of a
 * <code>double</code>. For |<i>s</i> - 1| below that precision the estimate is zero, its
 * logarithm is infinite, and the method fails with an <code>OverflowException</code>, e.g. for
 * <code>new Apfloat("1.00000000000000001", 40)</code>.<p>
 *
 * {@link #alternatingSum(Apcomplex)} is the method of apfloat with that one estimate taken
 * from the difference <i>s</i> - 1 instead. When the correction is made in apfloat itself,
 * this class is deleted.
 */

public class ZetaWorkaround
{
    private ZetaWorkaround()
    {
    }

    /**
     * Riemann zeta function. The same as <code>ApcomplexMath.zeta(s)</code>, except that
     * it also works for an argument which is very close to 1.
     *
     * @param s The argument.
     *
     * @return <i>&zeta;(s)</i>
     *
     * @throws ArithmeticException If <code>s</code> is <code>1</code>.
     */

    public static Apcomplex zeta(Apcomplex s)
        throws ArithmeticException, ApfloatRuntimeException
    {
        int radix = s.radix();
        Apcomplex s1 = s.subtract(ApfloatHelperShim.ONES[radix]);
        if (s1.real().signum() == 0 && s1.imag().signum() == 0 ||
            s.precision() == Apfloat.INFINITE ||
            s1.scale() > -NEAR_POLE_SCALE)
        {
            return ApcomplexMath.zeta(s);
        }
        if (s.imag().signum() < 0)
        {
            return alternatingSum(s.conj()).conj();
        }
        return alternatingSum(s);
    }

    // org.apfloat.ZetaHelper.alternatingSum() with the corrected estimate of the number of terms
    static Apcomplex alternatingSum(Apcomplex s)
    {
        int radix = s.radix();
        long doublePrecision = getDoublePrecision(radix),
             extraPrecision = ApfloatHelperShim.getSmallExtraPrecision(radix),
             workingPrecision = ApfloatHelperShim.extendPrecision(s.precision(), extraPrecision);
        s = ApfloatHelperShim.ensurePrecision(s, workingPrecision);
        Apint one = ApfloatHelperShim.ONES[radix],
              two = new Apint(2, radix),
              four = new Apint(4, radix);
        // 1 - 2^(1 - s) is (s - 1) log(2) to the first order, and the difference s - 1 is exact
        double t = Math.abs(s.imag().doubleValue()),
               logS12 = ApfloatMath.log(ApcomplexMath.abs(s.subtract(one)).precision(doublePrecision)).doubleValue() + Math.log(Math.log(2));
        long n = (long) ((workingPrecision * Math.log(radix) + t * Math.PI / 2 + Math.log(1 + 2 * t) - logS12) / Math.log(3 + Math.sqrt(8))),
             n2 = Util.multiplyExact(n, 2);
        // In apfloat: ApfloatMath.factorial(n2, workingPrecision, radix), which is package-private
        Apfloat denominator = ApfloatMath.gamma(new Apint(n2 + 1, radix).precision(workingPrecision)),
                numerator = new Apint(n, radix).multiply(denominator).multiply(ApfloatMath.pow(four.precision(workingPrecision), n)),
                d = Apfloat.ZERO;
        Apcomplex sum = Apcomplex.ZERO;
        for (long k = n; k > 0; k--)
        {
            numerator = numerator.divide(new Apint(n + k, radix));
            d = d.add(numerator.divide(denominator));
            numerator = numerator.divide(four);
            denominator = denominator.multiply(new Apint(n - k + 1, radix)).divide(new Apint(2 * k, radix)).divide(new Apint(2 * k - 1, radix));
            sum = sum.add(((k & 1) == 0 ? d.negate() : d).multiply(ApcomplexMath.pow(new Apint(k, radix), s.negate())));
        }
        d = d.add(numerator.divide(denominator));
        Apcomplex result = one.divide(d.multiply(one.subtract(ApcomplexMath.pow(two, one.subtract(s))))).multiply(sum);
        long precision = result.precision();
        return (precision == Apfloat.INFINITE || precision <= extraPrecision ? result : ApfloatHelperShim.limitPrecision(result, precision - extraPrecision));
    }

    // The package-private ApfloatHelper.getDoublePrecision()
    private static long getDoublePrecision(int radix)
    {
        return (long) Math.ceil(Math.log(2) * 53 / Math.log(radix));
    }

    // Below this distance to the pole (as a power of the radix) the sum of this class is used
    private static final long NEAR_POLE_SCALE = 8;
}
