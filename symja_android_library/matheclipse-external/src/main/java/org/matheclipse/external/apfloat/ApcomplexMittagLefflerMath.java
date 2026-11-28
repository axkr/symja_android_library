package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;

/**
 * The Mittag-Leffler function for {@link Apcomplex} arguments.<p>
 *
 * The method is written as it would appear in <code>org.apfloat.ApcomplexMath</code>:
 * static, and the precision of the result is the smallest precision of the arguments.
 *
 * @see <a href="https://dlmf.nist.gov/10.46#E3">DLMF 10.46.3</a>
 */

public class ApcomplexMittagLefflerMath
{
    private ApcomplexMittagLefflerMath()
    {
    }

    // The largest number of extra digits which the cancelling series is summed with
    private static final long MAX_EXTRA_PRECISION = 2000;

    // The sum is repeated with twice the extra digits at most this many times
    private static final int MAX_ATTEMPTS = 4;

    // The largest number of terms of the series
    private static final long MAX_TERMS = 200000;

    /**
     * Generalized Mittag-Leffler function.<p>
     *
     * <i>E<sub>&alpha;,&beta;</sub>(z)</i> = &sum;<sub>k</sub> <i>z<sup>k</sup></i> / &Gamma;(&alpha; <i>k</i> + &beta;).<p>
     *
     * The power series is summed, with as many extra digits as its largest term has more than its
     * sum may have.
     *
     * @implNote
     * This implementation is <i>slow</i>, meaning that it isn't a <i>fast algorithm</i>.
     * The number of terms and of extra digits grows like |<i>z</i>|<sup>1/Re(&alpha;)</sup>.
     *
     * @param α The first parameter. Its real part must be positive.
     * @param β The second parameter.
     * @param z The argument.
     *
     * @return <i>E<sub>&alpha;,&beta;</sub>(z)</i>
     *
     * @throws InfiniteExpansionException If all arguments have infinite precision.
     * @throws LossOfPrecisionException If the sums at increasing working precisions don't agree.
     * @throws ArithmeticException If the real part of &alpha; isn't positive, or the series cancels beyond the largest working precision.
     */

    public static Apcomplex mittagLefflerE(Apcomplex α, Apcomplex β, Apcomplex z)
        throws ArithmeticException, ApfloatRuntimeException
    {
        long precision = Math.min(α.precision(), Math.min(β.precision(), z.precision()));
        if (precision == Apfloat.INFINITE)
        {
            throw new InfiniteExpansionException("Cannot calculate Mittag-Leffler function to infinite precision", "mittagLeffler.infinitePrecision");
        }
        double alpha = α.real().doubleValue();
        if (!(alpha > 0.0))
        {
            throw new ArithmeticException("Mittag-Leffler function with a nonpositive first parameter");
        }
        int radix = z.radix();
        // The largest term is about exp(|z|^(1/alpha)); its digits above the sum are lost in the cancellation
        double peak = Math.pow(ApcomplexMath.abs(z).doubleValue(), 1.0 / alpha);
        double lostDigits = peak / Math.log(radix);
        if (lostDigits > MAX_EXTRA_PRECISION)
        {
            throw new ArithmeticException("The series of the Mittag-Leffler function cancels beyond the largest working precision");
        }
        long extraPrecision = ApfloatHelperShim.EXTRA_PRECISION + (long) Math.ceil(lostDigits);
        Apcomplex previous = null;
        for (int attempt = 0; attempt <= MAX_ATTEMPTS; attempt++)
        {
            long workingPrecision = ApfloatHelperShim.extendPrecision(precision, extraPrecision);
            Apcomplex result = series(ApfloatHelperShim.ensurePrecision(α, workingPrecision),
                                      ApfloatHelperShim.ensurePrecision(β, workingPrecision),
                                      ApfloatHelperShim.ensurePrecision(z, workingPrecision),
                                      workingPrecision, peak);
            if (previous != null && GuardDigitsHelper.agree(previous, result, precision))
            {
                return ApfloatHelperShim.limitPrecision(result, precision);
            }
            previous = result;
            extraPrecision *= 2;
        }
        throw new LossOfPrecisionException("Complete loss of accurate digits");
    }

    // The sum of z^k / Gamma(alpha k + beta) at the working precision
    private static Apcomplex series(Apcomplex α, Apcomplex β, Apcomplex z, long workingPrecision, double peak)
    {
        int radix = z.radix();
        Apcomplex sum = ApfloatHelperShim.ZEROS[radix],
                  power = ApfloatHelperShim.ONES[radix];
        long maxScale = Long.MIN_VALUE;
        int smallTerms = 0;
        for (long k = 0; k < MAX_TERMS; k++)
        {
            ApfloatHelperShim.checkInterrupted();
            Apcomplex argument = α.multiply(new Apint(k, radix)).add(β);
            if (!isNonPositiveInteger(argument))
            {
                // 1/Gamma is zero at the poles of Gamma
                Apcomplex term = power.divide(ApcomplexMath.gamma(argument));
                if (term.real().signum() != 0 || term.imag().signum() != 0)
                {
                    maxScale = Math.max(maxScale, term.scale());
                    sum = sum.add(term);
                    if (k > peak && term.scale() < maxScale - workingPrecision)
                    {
                        if (++smallTerms >= 3)
                        {
                            return sum;
                        }
                    }
                    else
                    {
                        smallTerms = 0;
                    }
                }
            }
            if (power.real().signum() == 0 && power.imag().signum() == 0)
            {
                return sum;
            }
            power = power.multiply(z);
        }
        throw new ArithmeticException("The series of the Mittag-Leffler function doesn't converge within the largest number of terms");
    }

    private static boolean isNonPositiveInteger(Apcomplex z)
    {
        return z.imag().signum() == 0 && z.real().signum() <= 0 && z.real().isInteger();
    }
}
